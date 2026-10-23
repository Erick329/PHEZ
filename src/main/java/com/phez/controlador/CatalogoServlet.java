package com.phez.controlador;

import com.phez.repositorio.CategoriaRepositorio;
import com.phez.repositorio.ProductoRepositorio;
import com.phez.util.Validador;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/catalogo")
public class CatalogoServlet extends BaseServlet {

    private final ProductoRepositorio productoRepositorio = new ProductoRepositorio();
    private final CategoriaRepositorio categoriaRepositorio = new CategoriaRepositorio();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int categoria = Validador.entero(req.getParameter("categoria"), 0);
        String q = Validador.limpiar(req.getParameter("q"));
        if (q.length() > 60) q = q.substring(0, 60);
        try {
            req.setAttribute("productos", productoRepositorio.listar(categoria, q, true));
            req.setAttribute("categorias", categoriaRepositorio.listar(true));
            req.setAttribute("categoriaSel", categoria);
            req.setAttribute("q", q);
            vista(req, resp, "publico/catalogo", "Catálogo");
        } catch (SQLException e) {
            errorBD(req, resp, e);
        }
    }
}
