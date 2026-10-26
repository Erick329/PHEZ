/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.phez.controlador.admin;

import com.phez.controlador.BaseServlet;
import com.phez.repositorio.UsuarioRepositorio;
import com.phez.util.Validador;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import com.phez.util.Seguridad;

/**
 *
 * @author erick
 */
@WebServlet("/admin/clientes")
public class ClientesServlet extends BaseServlet {

    private final UsuarioRepositorio usuarioRepositoiro = new UsuarioRepositorio();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            req.setAttribute("usuarios", usuarioRepositoiro.listar(null));
            vista(req, resp, "admin/clientes", "Clientes y usuarios");
        } catch (SQLException e) {
            errorBD(req, resp, e);
        }
    }
    
    private String generarTemporal() {
    String letras = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz";
    String numeros = "23456789";
    java.security.SecureRandom r = new java.security.SecureRandom();
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 6; i++) sb.append(letras.charAt(r.nextInt(letras.length())));
    for (int i = 0; i < 3; i++) sb.append(numeros.charAt(r.nextInt(numeros.length())));
    return sb.toString();   // ej. "kPqRmT482": cumple 8+ caracteres con letras y números
}

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id = Validador.entero(req.getParameter("id"), 0);
        String accion = Validador.limpiar(req.getParameter("accion"));
        try {
            if (id == idUsuario(req)) {
                error(req, "No se puede modificar su propia cuenta.");
            } else if (accion.equals("desbloquear")) {
                usuarioRepositoiro.reiniciarIntentos(id);
                exito(req, "Cuenta desbloqueada.");
            } else if (accion.equals("activar") || accion.equals("desactivar")) {
                usuarioRepositoiro.cambiarStatus(id, accion.equals("activar") ? "ACTIVO" : "INACTIVO");
                exito(req, accion.equals("activar") ? "Cuenta activada." : "Cuenta desactivada.");
            } else if (accion.equals("rol")) {
                int idRol = Validador.entero(req.getParameter("idRol"), 0);
                if (usuario(req) == null || !usuario(req).isAdmin()) {
                    resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return;
                } else if (idRol < 1 || idRol > 3) {
                    error(req, "Rol no válido.");
                } else {
                    usuarioRepositoiro.cambiarRol(id, idRol);
                    exito(req, "Rol actualizado. Se aplica cuando el usuario vuelva a iniciar sesión.");
                }
            } else if (accion.equals("restablecer")) {
                String temporal = generarTemporal();
                usuarioRepositoiro.actualizarPassword(id, Seguridad.hashPassword(temporal));
                exito(req, "Contraseña temporal: " + temporal + "  (entréguela al cliente; debe cambiarla al entrar)");
            } else {
                error(req, "Acción no válida.");
            }
            redirigir(req, resp, "/admin/clientes");
        } catch (SQLException e) {
            errorBD(req, resp, e);
        }
    }
}
