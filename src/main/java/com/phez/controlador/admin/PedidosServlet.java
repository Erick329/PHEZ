package com.phez.controlador.admin;

import com.phez.controlador.BaseServlet;
import com.phez.modelo.Pedido;
import com.phez.servicio.PedidoService;
import com.phez.util.ReglasDeNegocio;
import com.phez.util.Validador;
import com.phez.repositorio.PedidoRepositorio;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;

@WebServlet("/admin/pedidos")
public class PedidosServlet extends BaseServlet {

    private final PedidoRepositorio pedido = new PedidoRepositorio();
    private final PedidoService pedidoService = new PedidoService();
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            int id = Validador.entero(req.getParameter("id"), 0);
            if (id > 0) {
                Pedido p = pedido.buscar(id);
                if (p == null) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "El pedido no existe.");
                    return;
                }
                req.setAttribute("pedido", p);
                req.setAttribute("siguientes", PedidoService.siguientesEstados(p.getEstado()));
                vista(req, resp, "admin/pedido", "Pedido #" + id);
                return;
            }
            String estado = Validador.limpiar(req.getParameter("estado"));
            if (!Arrays.asList(Pedido.ESTADOS).contains(estado)) estado = null;
            req.setAttribute("pedidos", pedido.listar(estado, 200));
            req.setAttribute("estadoSel", estado);
            req.setAttribute("estados", Pedido.ESTADOS);
            vista(req, resp, "admin/pedidos", "Pedidos");
        } catch (SQLException e) {
            errorBD(req, resp, e);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id = Validador.entero(req.getParameter("id"), 0);
        String estado = Validador.limpiar(req.getParameter("estado"));
        Integer idUsr = idUsuario(req);
        if (id <= 0 || estado.isEmpty() || idUsr == null) {
            error(req, "Solicitud no válida.");
            redirigir(req, resp, "/admin/pedidos");
            return;
        }
        try {
            pedidoService.cambiarEstado(id, estado, idUsr);
            exito(req, "El pedido #" + id + " cambió a " + estado.replace('_', ' ') + ".");
        } catch (ReglasDeNegocio ex) {
            error(req, ex.getMessage());
        } catch (SQLException ex) {
            log("Error al cambiar el estado del pedido #" + id, ex);
            error(req, "No se pudo cambiar el estado del pedido. Intente de nuevo.");
        }
        redirigir(req, resp, "/admin/pedidos?id=" + id);
    }
}
