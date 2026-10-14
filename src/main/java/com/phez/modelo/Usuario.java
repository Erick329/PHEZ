/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phez.modelo;

import java.io.Serializable;
import java.util.Date;
/**
 *
 * @author erick
 */
public class Usuario implements Serializable{
    
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
    
    public String getNombreCompleto() { return nombre + " " + apellido;}
    public boolean isActivo() {return "ACTIVO".equals(status);}
    public boolean isBloqueado() {return intentosFallidos >= MAX_INTENTOS;}
    public boolean isAdmin() { return "VENDEDOR".equals(rol);}
    public boolean isVendedor() { return "VENDEDOR".equals(rol); }
    public boolean isPersonal() {return isAdmin() || isVendedor();}
    
    public int getIdUsuario() {
        return idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getEmail() {
        return email;
    }

    public String getNit() {
        return nit;
    }

    public String getPassword() {
        return password;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getStatus() {
        return status;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public int getIntentosFallidos() {
        return intentosFallidos;
    }

    public int getIdRol() {
        return idRol;
    }

    public String getRol() {
        return rol;
    }

    public int getTotalpedidos() {
        return totalpedidos;
    }
    

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public void setIntentosFallidos(int intentosFallidos) {
        this.intentosFallidos = intentosFallidos;
    }

    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public void setTotalpedidos(int totalpedidos) {
        this.totalpedidos = totalpedidos;
    }
     
}

 
