package com.phez.controlador;

import com.phez.modelo.Carrito;
import com.phez.modelo.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

public class BaseServlet extends HttpServlet {

    protected void vista(HttpServletRequest req, HttpServletResponse resp, String vista, String titulo)
            throws ServletException, IOException {
        req.setAttribute("titulo", titulo);
        req.getRequestDispatcher("/WEB-INF/vistas/" + vista + ".jsp").forward(req, resp);
    }
    
    protected void redirigir(HttpServletRequest req, HttpServletResponse resp, String ruta) throws IOException {
        resp.sendRedirect(req.getContextPath() + ruta);
    }
    
    protected Usuario usuario(HttpServletRequest req) {
        return (Usuario) req.getSession().getAttribute("Usuario");
    }
    
    protected Carrito carrito(HttpServletRequest req) {
        HttpSession s = req.getSession();
        Carrito c = (Carrito) s.getAttribute("carrito");
        if (c== null) {
            c = new Carrito();
            s.setAttribute ("carrito", c);
        }
        return c;
    }
    
protected void exito(HttpServletRequest req, String msg) { req.getSession().setAttribute("flashOk", msg); }
protected void error(HttpServletRequest req, String msg) { req.getSession().setAttribute("flashError", msg); }

protected String rutaSegura(String ruta, String porDefecto) {
    if (ruta == null || !ruta.startsWith("/") || ruta.startsWith("//") || ruta.contains("\\")) return porDefecto;
    return ruta;
}

protected String ip(HttpServletRequest req) { return req.getRemoteAddr(); }

protected Integer idUsuario(HttpServletRequest req) {
    Usuario u = usuario(req);
    return u == null ? null : u.getIdUsuario();
}

protected void errorBD(HttpServletRequest req, HttpServletResponse resp, SQLException e) throws IOException {
    log("Error de base de datos", e);
    resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
            "No es posible conectarse a la base de datos. Verificar que MYSQL está iniciado y la configuración en db.properties.");
}
}
   
