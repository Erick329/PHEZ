package com.phez.controlador.admin;

import com.phez.controlador.BaseServlet;
import com.phez.repositorio.LoteRepositorio;
import com.phez.repositorio.ProductoRepositorio;
import com.phez.servicio.PedidoService;
import com.phez.util.ReglasDeNegocio;
import com.phez.util.Validador;
import com.phez.repositorio.ProductoRepositorio;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/admin/inventario")
public class InventarioServlet extends BaseServlet {

    private final ProductoRepositorio producto =
        new ProductoRepositorio();

private final LoteRepositorio lote =
        new LoteRepositorio();
    private final PedidoService pedidoService = new PedidoService();
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            int productoSel = Validador.entero(req.getParameter("producto"), 0);
            req.setAttribute("productos", producto.listar(0, null, false));
            req.setAttribute("productoSel", productoSel);
            if (productoSel > 0) {
                req.setAttribute("producto", producto.buscar(productoSel));
                req.setAttribute("lotes", lote.listarPorProducto(productoSel));
            }
            req.setAttribute("porVencer", lote.proximosAVencer(30));
            req.setAttribute("bajoStock", producto.bajoStock());
            req.setAttribute("movimientos", producto.ultimosMovimientos(25));
            vista(req, resp, "admin/inventario", "Inventario por lotes");
        } catch (SQLException e) {
            errorBD(req, resp, e);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String tipo = Validador.limpiar(req.getParameter("tipo"));
        int idProducto = Validador.entero(req.getParameter("idProducto"), 0);
        int cantidad = Validador.entero(req.getParameter("cantidad"), -1);
        String motivo = Validador.limpiar(req.getParameter("motivo"));
        if (motivo.isEmpty()) motivo = tipo.equals("ENTRADA") ? "Compra a proveedor" : tipo.equals("SALIDA") ? "Merma" : "Conteo físico";
        if (motivo.length() > 150) motivo = motivo.substring(0, 150);
        try {
            if (tipo.equals("ENTRADA")) {
                String numeroLote = Validador.limpiar(req.getParameter("numeroLote")).toUpperCase();
                java.sql.Date vence;
                try {
                    vence = java.sql.Date.valueOf(Validador.limpiar(req.getParameter("fechaVencimiento")));
                } catch (IllegalArgumentException e) {
                    vence = null;
                }
                if (numeroLote.length() > 50) throw new ReglasDeNegocio("El número de lote admite máximo 50 caracteres.");
                if (vence != null && !vence.toLocalDate().isAfter(java.time.LocalDate.now()))
                    throw new ReglasDeNegocio("La fecha de vencimiento de un lote nuevo debe ser posterior a hoy.");
                pedidoService.registrarEntrada(idProducto, numeroLote, cantidad, vence, motivo, idUsuario(req));
                exito(req, "Lote " + numeroLote + " registrado (+" + cantidad + " unidades).");
            } else {
                int idLote = Validador.entero(req.getParameter("idLote"), 0);
                pedidoService.registrarSalidaOAjuste(idLote, tipo, cantidad, motivo, idUsuario(req));
                exito(req, (tipo.equals("SALIDA") ? "Salida" : "Ajuste") + " registrado en el lote.");
            }
        } catch (ReglasDeNegocio e) {
            error(req, e.getMessage());
        } catch (SQLException e) {
            errorBD(req, resp, e);
            return;
        }
        redirigir(req, resp, "/admin/inventario" + (idProducto > 0 ? "?producto=" + idProducto : ""));
    }
}

