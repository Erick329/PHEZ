/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phez.modelo;
import java.util.Date;
/**
 *
 * @author erick
 */
public class Usuario {
    
    public static final int MAX_INTENTOS = 5;
    
    private int idUsuario;
    private String nombre;
    private String apellido;
    private String email;
    private String nit;
    private String password;
    private String telefono;
    private String direccion;
    private String status = "ACTIVO";
    private Date fechaRegistro;
    private int intentosFallidos;
    private int idRol;
    private String rol;
    private int totalpedidos;

    public String getNombre() { return nombre + " " + apellido;}
    public boolean isActivo() {return "ACTIVO".equals(status);}
    public boolean isBloqueado() {return intentosFallidos >= MAX_INTENTOS;}
    public boolean isAdmin() { return "VENDEDOR".equals(rol);}
    public boolean isVendedor() { return "VENDEDOR".equals(rol); }
    public boolean isPersonal() {return isAdmin() || isVendedor();}
    
    
    
    
}
