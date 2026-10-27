package com.phez.controlador.admin;

import com.phez.controlador.BaseServlet;
import com.phez.modelo.Pedido;
import com.phez.modelo.Producto;
import com.phez.modelo.Reporte;
import com.phez.modelo.Lote;
import java.math.BigDecimal;
import com.phez.repositorio.PedidoRepositorio;
import com.phez.repositorio.ProductoRepositorio;
import com.phez.repositorio.ReporteRepositorio;
import com.phez.repositorio.LoteRepositorio;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@WebServlet("/admin/reportes")
public class ReportesServlet extends BaseServlet {

private final ReporteRepositorio reporte =
        new ReporteRepositorio();

private final ProductoRepositorio producto =
        new ProductoRepositorio();

private final PedidoRepositorio pedido =
        new PedidoRepositorio();

    private final LoteRepositorio lote =
        new LoteRepositorio();
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LocalDate hasta = fecha(req.getParameter("hasta"), LocalDate.now());
        LocalDate desde = fecha(req.getParameter("desde"), hasta.minusDays(89));
        if (desde.isAfter(hasta)) {
            LocalDate t = desde;
            desde = hasta;
            hasta = t;
        }
        try {
            String exportar = req.getParameter("exportar");
            if (exportar != null) {
                exportarCsv(resp, exportar, desde, hasta);
                return;
            }
          List<Reporte> ventas = reporte.ventasPorPeriodo(desde, hasta);
          List<Reporte> top = reporte.topProductos(desde, hasta);
          List<Reporte> categorias = reporte.ventasPorCategoria(desde, hasta);
          List<Reporte> estados = reporte.pedidosPorEstado(desde, hasta);

            req.setAttribute("desde", desde);
            req.setAttribute("hasta", hasta);
            req.setAttribute("resumen", reporte.resumen(desde, hasta));
            req.setAttribute("ventas", ventas);
            req.setAttribute("top", top);
            req.setAttribute("categorias", categorias);
            req.setAttribute("estados", estados);
            req.setAttribute("clientes", reporte.clientesFrecuentes(desde, hasta));
            req.setAttribute("bajaRotacion", reporte.bajaRotacion(desde, hasta));
            req.setAttribute("bajoStock", producto.bajoStock());
            
            List<Pedido> pedidos = pedido.listarPorFechas(desde, hasta);
            BigDecimal totalVendido = BigDecimal.ZERO;
            for (Pedido p : pedidos) if (!"CANCELADO".equals(p.getEstado())) totalVendido = totalVendido.add(p.getTotal());
            req.setAttribute("pedidos", pedidos);
            req.setAttribute("totalVendido", totalVendido);
            
            List<Producto> inventario = (List<Producto>) producto.listar(0, null, true);
            int unidades = 0;
            BigDecimal valor = BigDecimal.ZERO;
            for (Producto p : inventario) {
                unidades += p.getStock();
                valor = valor.add(p.getPrecio().multiply(BigDecimal.valueOf(p.getStock())));
            }
            req.setAttribute("inventario", inventario);
            req.setAttribute("totalUnidades", unidades);
            req.setAttribute("valorInventario", valor);
            
            req.setAttribute("lotesPorVencer", lote.proximosAVencer(30));

            req.setAttribute("jsonGraficas", "{\"ventas\":" + json(ventas, true) + ",\"top\":" + json(top, false)
                    + ",\"categorias\":" + json(categorias, true) + ",\"estados\":" + json(estados, false) + "}");
            vista(req, resp, "admin/reportes", "Reportes");
        } catch (SQLException e) {
            errorBD(req, resp, e);
        }
    }
    
    private void exportarCsv(HttpServletResponse resp, String tipo, LocalDate desde, LocalDate hasta)
            throws SQLException, IOException {
        String titulo;
        String[] encabezado;
        List<Reporte> filas;
        switch (tipo) {
            case "top":
                titulo = "productos_mas_vendidos"; encabezado = new String[]{"Producto", "Ingresos (Q)", "Unidades"};
                filas = reporte.topProductos(desde, hasta); break;
            case "categorias":
                titulo = "ventas_por_categoria"; encabezado = new String[]{"Categoría", "Ventas (Q)", "Unidades"};
                filas = reporte.ventasPorCategoria(desde, hasta); break;
            case "clientes":
                titulo = "clientes_frecuentes"; encabezado = new String[]{"Cliente", "Total comprado (Q)", "Pedidos"};
                filas = reporte.clientesFrecuentes(desde, hasta); break;
            case "estados":
                titulo = "pedidos_por_estado"; encabezado = new String[]{"Estado", "Monto (Q)", "Pedidos"};
                filas = reporte.pedidosPorEstado(desde, hasta); break;
            case "pedidos": {
                resp.setContentType("text/csv; charset=UTF-8");
                resp.setHeader("Content-Disposition", "attachment; filename=\"detalle_pedidos_" + desde + "_" + hasta + ".csv\"");
                PrintWriter out = resp.getWriter();
                out.print('\uFEFF');
                out.println("Pedido,Fecha,Cliente,NIT,Método de pago,Estado,Total (Q)");
                BigDecimal total = BigDecimal.ZERO;
                for (Pedido p : pedido.listarPorFechas(desde, hasta)) {
                    out.println(p.getIdPedido() + "," + p.getFechaPedido() + "," + csv(p.getClienteNombre()) + "," + csv(p.getClienteNit()) + ","
                            + csv(p.getMetodoPago()) + "," + p.getEstado() + "," + p.getTotal());
                    if (!"CANCELADO".equals(p.getEstado())) total = total.add(p.getTotal());
                }
                out.println(",,,,,TOTAL VENDIDO," + total);
                return;
            }
            case "stock": {
                resp.setContentType("text/csv; charset=UTF-8");
                resp.setHeader("Content-Disposition", "attachment; filename=\"inventario_actual.csv\"");
                PrintWriter out = resp.getWriter();
                out.print('\uFEFF');
                out.println("Código,Producto,Categoría,Stock,Precio (Q),Valor (Q)");
                int unidades = 0;
                BigDecimal valor = BigDecimal.ZERO;
                for (Producto p : producto.listar(0, null, true)) {
                    BigDecimal v = p.getPrecio().multiply(BigDecimal.valueOf(p.getStock()));
                    out.println(csv(p.getCodigo()) + "," + csv(p.getNombre()) + "," + csv(p.getCategoriaNombre()) + ","
                            + p.getStock() + "," + p.getPrecio() + "," + v);
                    unidades += p.getStock();
                    valor = valor.add(v);
                }
                out.println(",,TOTAL EN STOCK," + unidades + ",," + valor);
                return;
            }
            case "lotes": {
                resp.setContentType("text/csv; charset=UTF-8");
                resp.setHeader("Content-Disposition", "attachment; filename=\"lotes_por_vencer.csv\"");
                PrintWriter out = resp.getWriter();
                out.print('\uFEFF');
                out.println("Producto,Lote,Cantidad,Vence,Estado");
                for (Lote l : lote.proximosAVencer(30)) {
                    out.println(csv(l.getProductoNombre()) + "," + csv(l.getNumeroLote()) + "," + l.getCantidad() + ","
                            + l.getFechaVencimiento() + "," + csv(l.getEstadoVencimiento()));
                }
                return;
            }
            case "inventario": {
                resp.setContentType("text/csv; charset=UTF-8");
                resp.setHeader("Content-Disposition", "attachment; filename=\"inventario_bajo_minimo.csv\"");
                PrintWriter out = resp.getWriter();
                out.print('﻿');
                out.println("Código,Producto,Categoría,Stock,Stock mínimo");
                for (Producto p : producto.bajoStock()) {
                    out.println(csv(p.getCodigo()) + "," + csv(p.getNombre()) + "," + csv(p.getCategoriaNombre()) + ","
                            + p.getStock() + "," + p.getStockMinimo());
                }
                return;
            }
            default:
                titulo = "ventas_por_periodo"; encabezado = new String[]{"Período", "Ventas (Q)", "Pedidos"};
                filas = reporte.ventasPorPeriodo(desde, hasta);
        }
        resp.setContentType("text/csv; charset=UTF-8");
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + titulo + "_" + desde + "_" + hasta + ".csv\"");
        PrintWriter out = resp.getWriter();
        out.print('﻿');  
        out.println(String.join(",", encabezado));
        for (Reporte f : filas) out.println(csv(f.getEtiqueta()) + "," + f.getMonto() + "," + f.getCantidad());
    }
    
    private static String csv(String s) {
        if (s == null) return "";
        String v = s.replace("\"", "\"\"");
        if (v.matches("^[=+\\-@].*")) v = "'" + v;   
        return "\"" + v + "\"";
    }
    
    private static String json(List<Reporte> filas, boolean usarMonto) {
        StringBuilder e = new StringBuilder("[");
        StringBuilder v = new StringBuilder("[");
        for (int i = 0; i < filas.size(); i++) {
            if (i > 0) { e.append(','); v.append(','); }
            e.append('"').append(escaparJson(filas.get(i).getEtiqueta())).append('"');
            v.append(usarMonto ? filas.get(i).getMonto().toPlainString() : String.valueOf(filas.get(i).getCantidad()));
        }
        return "{\"etiquetas\":" + e.append(']') + ",\"valores\":" + v.append(']') + "}";
    }
    
    private static String escaparJson(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (c == '"' || c == '\\') sb.append('\\').append(c);
            else if (c == '<') sb.append("\\u003c");   // evita cerrar la etiqueta <script>
            else if (c == '>') sb.append("\\u003e");
            else if (c == '&') sb.append("\\u0026");
            else if (c < 0x20) sb.append(String.format("\\u%04x", (int) c));
            else sb.append(c);
        }
        return sb.toString();
    }

    private static LocalDate fecha(String s, LocalDate porDefecto) {
        try {
            return s == null || s.isBlank() ? porDefecto : LocalDate.parse(s);
        } catch (DateTimeParseException e) {
            return porDefecto;
        }
    }
}
    