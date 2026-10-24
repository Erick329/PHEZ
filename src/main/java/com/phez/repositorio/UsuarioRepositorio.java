/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phez.repositorio;

import com.phez.modelo.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author erick
 */
public class UsuarioRepositorio {

    private static final String SELECT_BASE
            = "SELECT u.*, r.nombre AS rol FROM Usuarios u JOIN Roles r ON r.idRol = u.idRol ";

    public Usuario buscarPorEmail(String email) throws SQLException {
        return uno(SELECT_BASE + "WHERE u.email = ?", email);
    }

    public Usuario buscarPorId(int id) throws SQLException {
        return uno(SELECT_BASE + "WHERE u.idUsuario = ?", id);
    }

    public boolean existeEmail(String email) throws SQLException {
        try (Connection con = ConexionBD.getConnection(); PreparedStatement ps = con.prepareStatement("SELECT 1 FROM Usuarios WHERE email = ?")) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public int registrarCliente(Usuario user) throws SQLException {
        String sql = "INSERT INTO Usuarios (nombre, apellido, email, nit, password, telefono, direccion, idRol)"
                + "Values (?, ?, ?, ?, ?, ?, ?, (SELECT idRol FROM Roles WHERE nombre = 'CLIENTE'))";
        try (Connection con = ConexionBD.getConnection(); PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getNombre());
            ps.setString(2, user.getApellido());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getNit());
            ps.setString(5, user.getPassword());
            ps.setString(6, user.getTelefono());
            ps.setString(7, user.getDireccion());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : 0;
            }

        }
    }

    public void actualizarPerfil(Usuario u) throws SQLException {
        String sql = "UPDATE Usuarios SET nombre = ?, apellido = ?, nit = ?, telefono = ?, direccion = ? WHERE idUsuario = ?";
        try (Connection cn = ConexionBD.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getApellido());
            ps.setString(3, u.getNit());
            ps.setString(4, u.getTelefono());
            ps.setString(5, u.getDireccion());
            ps.setInt(6, u.getIdUsuario());
            ps.executeUpdate();
        }
    }
    
        public void registrarIntentoFallido(int idUsuario) throws SQLException {
        ejecutar("UPDATE Usuarios SET intentosFallidos = intentosFallidos + 1 WHERE idUsuario = ?", idUsuario);
    }

    public void reiniciarIntentos(int idUsuario) throws SQLException {
        ejecutar("UPDATE Usuarios SET intentosFallidos = 0 WHERE idUsuario = ?", idUsuario);
    }

    public void cambiarStatus(int idUsuario, String status) throws SQLException {
        try (Connection cn = ConexionBD.getConnection();
             PreparedStatement ps = cn.prepareStatement("UPDATE Usuarios SET status = ? WHERE idUsuario = ?")) {
            ps.setString(1, status);
            ps.setInt(2, idUsuario);
            ps.executeUpdate();
        }
    }

    public List<Usuario> listar(String rol) throws SQLException {
        String sql = "SELECT u.*, r.nombre AS rol, "
                   + "(SELECT COUNT(*) FROM Pedidos p WHERE p.idUsuario = u.idUsuario) AS totalPedidos "
                   + "FROM Usuarios u JOIN Roles r ON r.idRol = u.idRol "
                   + (rol == null ? "" : "WHERE r.nombre = ? ")
                   + "ORDER BY u.fechaRegistro DESC";
        List<Usuario> lista = new ArrayList<>();
        try (Connection cn = ConexionBD.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            if (rol != null) ps.setString(1, rol);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Usuario u = mapear(rs);
                    u.setTotalPedidos(rs.getInt("totalPedidos"));
                    lista.add(u);
                }
            }
        }
        return lista;
    }

    private Usuario uno(String sql, Object param) throws SQLException {
        try (Connection cn = ConexionBD.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setObject(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    private void ejecutar(String sql, int id) throws SQLException {
        try (Connection cn = ConexionBD.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setIdUsuario(rs.getInt("idUsuario"));
        u.setNombre(rs.getString("nombre"));
        u.setApellido(rs.getString("apellido"));
        u.setEmail(rs.getString("email"));
        u.setNit(rs.getString("nit"));
        u.setPassword(rs.getString("password"));
        u.setTelefono(rs.getString("telefono"));
        u.setDireccion(rs.getString("direccion"));
        u.setStatus(rs.getString("status"));
        u.setFechaRegistro(rs.getTimestamp("fechaRegistro"));
        u.setIntentosFallidos(rs.getInt("intentosFallidos"));
        u.setIdRol(rs.getInt("idRol"));
        u.setRol(rs.getString("rol"));
        return u;
    }
    
    public void cambiarRol(int idUsuario, String rol) throws SQLException {
    String sql = "UPDATE Usuarios SET idRol = (SELECT idRol FROM Roles WHERE nombre = ?) WHERE idUsuario = ?";
    try (Connection cn = ConexionBD.getConnection();
         PreparedStatement ps = cn.prepareStatement(sql)) {
        ps.setString(1, rol);
        ps.setInt(2, idUsuario);
        ps.executeUpdate();
    }
}

}
