package com.phez.controlador;

import com.phez.repositorio.ProductoRepositorio;
import com.phez.modelo.Producto;
import com.phez.util.Validador;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/producto")
public class ProductoServlet extends BaseServlet {

    private final ProductoRepositorio productoRepositorio = new ProductoRepositorio();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Producto p = productoRepositorio.buscar(Validador.entero(req.getParameter("id"), 0));
            if (p == null || !p.isActivo()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "El producto no existe.");
                return;
            }
            req.setAttribute("producto", p);
            req.setAttribute("relacionados", productoRepositorio.listar(p.getIdCategoria(), null, true)
                    .stream().filter(x -> x.getIdProducto() != p.getIdProducto()).limit(4).toList());
            req.setAttribute("enCarrito", carrito(req).cantidadDe(p.getIdProducto()));
            vista(req, resp, "publico/producto", p.getNombre());
        } catch (SQLException e) {
            errorBD(req, resp, e);
        }
    }
}
