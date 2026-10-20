package com.phez.controlador;

import com.phez.dao.UsuarioRepositorio;
import com.phez.modelo.Usuario;
import com.phez.util.Seguridad;
import com.phez.util.Validador;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;



@WebServlet("/Login")
public class LoginServlet extends BaseServlet {
    
     private final UsuarioRespositorio usuarioRepositorio = new UsuarioRepositorio();
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (usuario(req) !=null) {
            redirigir(req, resp, "/inicio");
            return;
        }
        req.setAttribute("next", rutaSegura(req.getParameter("next"), ""));
        vista(req, resp, "publico/login", "Iniciar sesión");
        return;
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = Validador.limpiar(req.getParameter("email")).toLowerCase();
        String password = req.getParameter("password");
        String next = rutaSegura(req.getParameter("next"), "");
        try {
            Usuario u = usuarioRespositorio.buscarPorEmail(email);
            String mensaje = null;
            if (u == null) {
                mensaje = "Correo o contraseña incorrectos. ";
            } else if (!u.isActivo()) {
                mensaje = "Su cuenta está desactivada. Contactar a la empresa. ";
            }else if (u.isBloqueado()) {
                mensaje = "Su cuenta se bloqueó por demsiados intentos fallidos. Contactar al administrador. ";                
            } else if (!Seguridad.verificarPassword(password, u.getPassword())) {
                usuarioRepositorio.registrarIntentoFallido(u.getIdUsuario());
                int restantes = Usuario.MAX_INTENTOS - (u.getIntentosFallidos() + 1);
                mensaje = restantes > 0
                        ? "Correo o contraseña incorrectos. Intentos restantes: " + restantes + "."
                        : "Su cuenta fue bloqueada por demasiados intentos fallidos.";
            }
            
            if (mensaje !=null) {
                req.setAttribute("error", mensaje);
                req.setAttribute("email", email);
                req.setAttribute("next", next);
                vista (req, resp, "publico/login", "Iniciar sesión");
                return;
            }
            
            usuarioRepositorio.reiniciarIntentos(u.getIdUsuario());
            u.setPassword(null);
            req.getSession().setAttribute("usuario", u);
            
            if (!next.isEmpty()) redirigir(req, resp, next);
            else if (u.isPersonal()) redirigir(req, resp, next);
            else redirigir(req, resp, "/inicio");
        } catch (SQLException e) {
            erroBD(req,resp, e);
        }
    }
}