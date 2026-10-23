/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.phez.controlador.cliente;

import com.phez.controlador.BaseServlet;
import com.phez.repositorio.UsuarioRepositorio;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.phez.util.Validador;
import com.phez.modelo.Usuario;
import java.sql.SQLException;

/**
 *
 * @author erick
 */
@WebServlet("/cliente/perfil")
public class PerfilServlet extends BaseServlet {

    private final UsuarioRepositorio usuarioRepositorio = new UsuarioRepositorio();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        vista(req, resp, "cliente/perfil", "Mi cuenta");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Usuario u = usuario(req);
        String nombre = Validador.limpiar(req.getParameter("nombre"));
        String apellido = Validador.limpiar(req.getParameter("apellido"));
        String telefono = Validador.limpiar(req.getParameter("telefono"));
        String nit = Validador.limpiar(req.getParameter("nit")).toUpperCase();
        if (nit.isEmpty()) {
            nit = "CF";
        }
        String direccion = Validador.limpiar(req.getParameter("direccion"));
        if (Validador.vacio(nombre) || Validador.vacio(apellido) || !Validador.telefono(telefono)
                || Validador.vacio(direccion) || !Validador.nit(nit)) {
            error(req, "Revise los datos: nombre, apellido y dirección son obligatorios; el teléfono solo admite números y el NIT debe ser CF o números con guion.");
            redirigir(req, resp, "/cliente/perfil");
            return;
        }
        u.setNombre(nombre);
        u.setApellido(apellido);
        u.setTelefono(telefono);
        u.setNit(nit);
        u.setDireccion(direccion);
        try {
            usuarioRepositorio.actualizarPerfil(u);
            exito(req, "Sus datos se actualizaron correctamente.");
            redirigir(req, resp, "/cliente/perfil");
        } catch (SQLException e) {
            errorBD(req, resp, e);
        }
    }
}
