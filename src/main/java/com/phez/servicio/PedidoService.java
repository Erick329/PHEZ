package com.phez.servicio;

import com.phez.repositorio.ConexionBD;
import com.phez.repositorio.LoteRepositorio;
import com.phez.repositorio.PedidoRepositorio;
import com.phez.repositorio.ProductoRepositorio;
import com.phez.modelo.Carrito;
import com.phez.modelo.DetallePedido;
import com.phez.modelo.ItemCarrito;
import com.phez.modelo.Lote;
import com.phez.modelo.MovimientoInventario;
import com.phez.modelo.Pedido;
import com.phez.modelo.Producto;
import com.phez.modelo.Usuario;
import com.phez.util.ReglasDeNegocio; 
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class PedidoService {
    
    private final ProductoRepositorio ProductoRepositorio = new ProductoRepositorio ();
    private final LoteRepositorio LoteRepositorio = new LoteRepositorio();
    private final PedidoRepositorio PedidoRepositorio = new PedidoRepositorio ();
    private final PagoService PagoService = new PagoService ();
    
    public Pedido crearPedido(Usuario cliente, Carrito carrito, String direccion, String metodoPago,
                              String tarjeta, String vencimiento, String cvv, int idUsuarioRegistra)
            throws ReglasDeNegocio, SQLException {

        if (carrito == null || carrito.isVacio()) throw new ReglasDeNegocio("El carrito está vacío.");

        try (Connection cn = ConexionBD.getConnection()) {
            cn.setAutoCommit(false);                                          
            try {
                Pedido pedido = new Pedido();
                pedido.setIdUsuario(cliente.getIdUsuario());
                pedido.setDireccionEnvio(direccion);
                BigDecimal subtotal = BigDecimal.ZERO;
                List<List<Lote>> lotesPorItem = new ArrayList<>();

                
                for (ItemCarrito item : carrito.getItems()) {
                    Producto p = ProductoRepositorio.bloquear(cn, item.getProducto().getIdProducto());
                    if (p == null || !p.isActivo()) {
                        throw new ReglasDeNegocio("El producto \"" + item.getProducto().getNombre() + "\" ya no está disponible.");
                    }
                    List<Lote> lotes = LoteRepositorio.disponiblesParaVenta(cn, p.getIdProducto());
                    int disponible = 0;
                    for (Lote l : lotes) disponible += l.getCantidad();
                    if (disponible < item.getCantidad()) {
                        throw new ReglasDeNegocio("Existencias insuficientes de \"" + p.getNombre()
                                + "\". Disponibles (sin contar lotes vencidos): " + disponible + ".");
                    }
                    lotesPorItem.add(lotes);
                    DetallePedido d = new DetallePedido();
                    d.setIdProducto(p.getIdProducto());
                    d.setProductoNombre(p.getNombre());
                    d.setCantidad(item.getCantidad());
                    d.setPrecioUnitario(p.getPrecio());                       
                    d.setSubtotal(p.getPrecio().multiply(BigDecimal.valueOf(item.getCantidad())));
                    pedido.getDetalles().add(d);
                    subtotal = subtotal.add(d.getSubtotal());
                }

                
                subtotal = subtotal.setScale(2, RoundingMode.HALF_UP);
                BigDecimal envio = subtotal.compareTo(Carrito.COSTO_GRATIS_DESDE) >= 0 ? BigDecimal.ZERO : Carrito.COSTO_ENVIO;
                BigDecimal iva = subtotal.subtract(subtotal.divide(Carrito.TASA_IVA, 2, RoundingMode.HALF_UP));
                pedido.setSubtotal(subtotal);
                pedido.setIva(iva);
                pedido.setCostoEnvio(envio);  
                pedido.setTotal(subtotal.add(envio));

                
                PagoService.ResultadoPago pago = PagoService.procesar(metodoPago, pedido.getTotal(), tarjeta, vencimiento, cvv);
                if (!pago.isAprobado()) throw new ReglasDeNegocio(pago.getMensaje());
                pedido.setEstado("APROBADO".equals(pago.getEstado()) ? "PAGADO" : "PENDIENTE");

                
                int idPedido = PedidoRepositorio.insertar(cn, pedido);
                pedido.setIdPedido(idPedido);
                PedidoRepositorio.insertarPago(cn, idPedido, metodoPago, pedido.getTotal(), pago.getEstado(), pago.getReferencia());

                
                for (int i = 0; i < pedido.getDetalles().size(); i++) {
                    DetallePedido d = pedido.getDetalles().get(i);
                    int pendiente = d.getCantidad();
                    for (Lote l : lotesPorItem.get(i)) {
                        if (pendiente == 0) break;
                        int tomar = Math.min(pendiente, l.getCantidad());
                        LoteRepositorio.sumarCantidad(cn, l.getIdLote(), -tomar);
                        ProductoRepositorio.registrarMovimiento(cn, "SALIDA", tomar, "Venta pedido #" + idPedido,
                                d.getIdProducto(), idUsuarioRegistra, l.getIdLote());
                        pendiente -= tomar;
                    }
                    ProductoRepositorio.recalcularStock(cn, d.getIdProducto());
                }

                cn.commit();                                                  
                pedido.setMetodoPago(metodoPago);
                pedido.setEstadoPago(pago.getEstado());
                pedido.setReferenciaPago(pago.getReferencia());
                return pedido;
            } catch (ReglasDeNegocio | SQLException e) {
                cn.rollback();                                                
                throw e;
            }
        }
    }

   
    public static List<String> siguientesEstados(String actual) {
        if (actual == null) return Arrays.asList();
        switch (actual) {
            case "PENDIENTE": return Arrays.asList("PAGADO", "CANCELADO");
            case "PAGADO": return Arrays.asList("EN_PREPARACION", "CANCELADO");
            case "EN_PREPARACION": return Arrays.asList("ENVIADO", "CANCELADO");
            case "ENVIADO": return Arrays.asList("ENTREGADO");
            default: return Arrays.asList();
        }
    }

   
    public void cambiarEstado(int idPedido, String nuevoEstado, int idUsuario) throws ReglasDeNegocio, SQLException {
        Pedido pedido = PedidoRepositorio.buscar(idPedido);
        if (pedido == null) throw new ReglasDeNegocio("El pedido no existe.");
        if (!siguientesEstados(pedido.getEstado()).contains(nuevoEstado)) {
            throw new ReglasDeNegocio("No se puede pasar de " + pedido.getEstadoTexto() + " a " + nuevoEstado.replace('_', ' ') + ".");
        }
        try (Connection cn = ConexionBD.getConnection()) {
            cn.setAutoCommit(false);
            try {
                PedidoRepositorio.actualizarEstado(cn, idPedido, nuevoEstado);
                if ("CANCELADO".equals(nuevoEstado)) {
                    for (MovimientoInventario m : ProductoRepositorio.salidasDeVenta(cn, idPedido)) {
                        if (m.getIdLote() != null) LoteRepositorio.sumarCantidad(cn, m.getIdLote(), m.getCantidad());
                        ProductoRepositorio.registrarMovimiento(cn, "ENTRADA", m.getCantidad(), "Cancelación pedido #" + idPedido,
                                m.getIdProducto(), idUsuario, m.getIdLote());
                        ProductoRepositorio.recalcularStock(cn, m.getIdProducto());
                    }
                    PedidoRepositorio.actualizarEstadoPago(cn, idPedido, "RECHAZADO");
                } else if ("PAGADO".equals(nuevoEstado) || "ENTREGADO".equals(nuevoEstado)) {
                    PedidoRepositorio.actualizarEstadoPago(cn, idPedido, "APROBADO");
                }
                cn.commit();
            } catch (SQLException e) {
                cn.rollback();
                throw e;
            }
        }
    }

    
    public int registrarEntrada(int idProducto, String numeroLote, int cantidad, Date vence, String motivo, int idUsuario)
            throws ReglasDeNegocio, SQLException {
        if (cantidad <= 0) throw new ReglasDeNegocio("La cantidad debe ser mayor que cero.");
        if (numeroLote == null || numeroLote.isBlank()) throw new ReglasDeNegocio("Ingrese el número de lote.");
        if (vence == null) throw new ReglasDeNegocio("Ingrese la fecha de vencimiento del lote.");
        try (Connection cn = ConexionBD.getConnection()) {
            cn.setAutoCommit(false);
            try {
                if (ProductoRepositorio.bloquear(cn, idProducto) == null) throw new ReglasDeNegocio("El producto no existe.");
                Lote l = new Lote();
                l.setIdProducto(idProducto);
                l.setNumeroLote(numeroLote.trim());
                l.setCantidad(cantidad);
                l.setFechaVencimiento(vence);
                int idLote = LoteRepositorio.insertar(cn, l);
                ProductoRepositorio.registrarMovimiento(cn, "ENTRADA", cantidad, motivo, idProducto, idUsuario, idLote);
                ProductoRepositorio.recalcularStock(cn, idProducto);
                cn.commit();
                return idLote;
            } catch (ReglasDeNegocio | SQLException e) {
                cn.rollback();
                throw e;
            }
        }
    }

    
    public void registrarSalidaOAjuste(int idLote, String tipo, int cantidad, String motivo, int idUsuario)
            throws ReglasDeNegocio, SQLException {
        if (cantidad < 0 || (cantidad == 0 && "SALIDA".equals(tipo))) throw new ReglasDeNegocio("La cantidad no es válida.");
        try (Connection cn = ConexionBD.getConnection()) {
            cn.setAutoCommit(false);
            try {
                Lote l = LoteRepositorio.bloquear(cn, idLote);
                if (l == null) throw new ReglasDeNegocio("Seleccione un lote.");
                int cambio;
                if ("SALIDA".equals(tipo)) {
                    if (cantidad > l.getCantidad()) throw new ReglasDeNegocio("El lote " + l.getNumeroLote() + " solo tiene " + l.getCantidad() + " unidades.");
                    cambio = -cantidad;
                } else if ("AJUSTE".equals(tipo)) {
                    cambio = cantidad - l.getCantidad();
                    if (cambio == 0) throw new ReglasDeNegocio("El conteo coincide con el sistema; no hay nada que ajustar.");
                } else {
                    throw new ReglasDeNegocio("Tipo de movimiento no válido.");
                }
                LoteRepositorio.sumarCantidad(cn, idLote, cambio);
                ProductoRepositorio.registrarMovimiento(cn, tipo, "SALIDA".equals(tipo) ? cantidad : cambio, motivo,
                        l.getIdProducto(), idUsuario, idLote);
                ProductoRepositorio.recalcularStock(cn, l.getIdProducto());
                cn.commit();
            } catch (ReglasDeNegocio | SQLException e) {
                cn.rollback();
                throw e;
            }
        }
    }
}

   

