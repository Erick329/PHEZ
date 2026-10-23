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
            req.setAttribute("destacados", productoRepositorio.destacados(8));
            req.setAttribute("categorias", categoriaRepositorio.listar(true));
            vista(req, resp, "publico/inicio", "Inicio");
        } catch (SQLException e) {
            errorBD(req, resp, e);
        }
    }
}
