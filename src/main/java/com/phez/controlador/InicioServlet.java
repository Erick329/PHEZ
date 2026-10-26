package com.phez.controlador;

import com.phez.repositorio.CategoriaRepositorio;
import com.phez.repositorio.ProductoRepositorio;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(urlPatterns = {"", "/inicio"})
public class InicioServlet extends BaseServlet {

    private final ProductoRepositorio productoRepositorio = new ProductoRepositorio();
    private final CategoriaRepositorio categoriaRepositorio = new CategoriaRepositorio();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            java.util.List<com.phez.modelo.Producto> productos = new java.util.ArrayList<>(productoRepositorio.listar(0, null, true));
            productos.removeIf(p -> p.getStock() <= 0);
            java.util.Collections.shuffle(productos);
            req.setAttribute("destacados", productos.subList(0, Math.min(12, productos.size())));
            req.setAttribute("categorias", categoriaRepositorio.listar(true));
            vista(req, resp, "general/inicio", "Inicio");
        } catch (SQLException e) {
            errorBD(req, resp, e);
        }
    }
}
