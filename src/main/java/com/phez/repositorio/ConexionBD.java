/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phez.repositorio;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 *
 * @author erick
 */
public class ConexionBD {

    private static final Properties CONFIGURACION = new Properties();

    static {
        try (InputStream in = ConexionBD.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in == null) {
                throw new IllegalStateException("No se encontró db.properties");
            }
            CONFIGURACION.load(in);
        } catch (Exception e) {
            throw new ExceptionInInitializerError("Error al leer la configuracion de la Basse de Datos: " + e.getMessage());
        }
        String driver = CONFIGURACION.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            System.err.println("No se encontro el driver " + driver);
        }
    }

    static Connection getConexion() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private ConexionBD() {
    }


        public static Connection getConnection() throws SQLException {
            return DriverManager.getConnection(
                    CONFIGURACION.getProperty("db.url"),
                    CONFIGURACION.getProperty("db.user"),
                    CONFIGURACION.getProperty("db.password", ""));
        }

        public static void main(String[] args) {
            try (Connection conection = getConnection()) {
                System.out.println("Conexion exitosa a: " + conection.getMetaData().getURL());
            } catch (SQLException e) {
                System.out.println("Error de conexion: " + e.getMessage());
            }
    }

}
