/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phez.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author erick
 */
public class Producto implements Serializable {

    private int idProducto;
    private String nombre;
    private String descripcion;
    private String codigo;
    private BigDecimal precio = BigDecimal.ZERO;
    private int stock;
    private int stockMinimo;
    private String status = "ACTIVO";
    private int idCategoria;
    private String categoriaNombre;
    private Date proximoVencimiento;

    public boolean isActivo() {
        return "ACTIVO".equals(status);
    }

    public String getIcono() {
        String cat = categoriaNombre == null ? "" : categoriaNombre.toLowerCase();
        if (cat.contains("grano")) {
            return "Granos";
        }
        if (cat.contains("lacteo") || cat.contains("lacteo")) {
            return " Lacteo";
        }
        if (cat.contains("bebida")) {
            return "Bebida";
        }
        if (cat.contains("snack")) {
            return "Snack";
        }
        if (cat.contains("fruta") || cat.contains("verdura")) {
            return "Verdura";
        }
        return "Carrito";
    }

    public int getIdProducto() {
        return idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getCodigo() {
        return codigo;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public int getStock() {
        return stock;
    }

    public int getStockMinimo() {
        return stockMinimo;
    }

    public String getStatus() {
        return status;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public String getCategoriaNombre() {
        return categoriaNombre;
    }

    public Date getProximoVencimiento() {
        return proximoVencimiento;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void setStockMinimo(int stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public void setCategoriaNombre(String categoriaNombre) {
        this.categoriaNombre = categoriaNombre;
    }

    public void setProximoVencimiento(Date proximoVencimiento) {
        this.proximoVencimiento = proximoVencimiento;
    }

}
