package com.phez.controlador.cliente;

import com.phez.controlador.BaseServlet;
import com.phez.repositorio.PedidoRepositorio;
import com.phez.modelo.Pedido;
import com.phez.modelo.Usuario;
import com.phez.util.Validador;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(urlPatterns = {"/cliente/pedidos", "/cliente/pedido"})
public class MisPedidosServlet extends BaseServlet {
    private final PedidoRepositorio pedidoRepositorio = new PedidoRepositorio();
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Usuario u = usuario(req);
        try {
            if (req.getServletPath().equals("/cliente/pedido")) {
                Pedido p = pedidoRepositorio.buscar(Validador.entero(req.getParameter("id"), 0));
                
                if (p == null || (p.getIdUsuario() != u.getIdUsuario() && !u.isPersonal())) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "El pedido no existe.");
                    return;
                }
                req.setAttribute("pedido", p);
                req.setAttribute("nuevo", "1".equals(req.getParameter("nuevo")));
                vista(req, resp, "cliente/pedido", "Pedido #" + p.getIdPedido());
            } else {
                req.setAttribute("pedidos", pedidoRepositorio.listarPorUsuario(u.getIdUsuario()));
                vista(req, resp, "cliente/pedidos", "Mis pedidos");
            }
        } catch (SQLException e) {
            errorBD(req, resp, e);
        }
    }
}
