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
    if (codigo != null) {
        switch (codigo) {
            case "GB-001": return "🍚";   // Arroz
            case "GB-002": return "🫘";   // Frijol
            case "GB-003": return "🌽";   // Harina de maíz
            case "GB-004": return "🥣";   // Avena
            case "LA-001": return "🥛";   // Leche
            case "LA-002": return "🧀";   // Queso
            case "LA-003": return "🍦";   // Yogur
            case "LA-004": return "🧈";   // Crema
            case "BE-001": return "🍊";   // Jugo de naranja
            case "BE-002": return "💧";   // Agua pura
            case "BE-003": return "☕";   // Café
            case "BE-004": return "🌺";   // Rosa de Jamaica
            case "SN-001": return "🍪";   // Galletas
            case "SN-002": return "🥜";   // Nueces
            case "SN-003": return "🍫";   // Chocolate
            case "FV-001": return "🥑";   // Aguacate
            case "FV-002": return "🍌";   // Banano
            case "FV-003": return "🍅";   // Tomate
            case "FV-004": return "🥬";   // Güisquil
            case "FV-005": return "🥔";   // Papa
        }
    }
    return getIconoCategoria();
}

    public String getIconoCategoria() {
        String cat = categoriaNombre == null ? "" : categoriaNombre.toLowerCase();
        if (cat.contains("grano")) {
            return "🌾";
        }
        if (cat.contains("lácteo") || cat.contains("lacteo")) {
            return " 🥛";
        }
        if (cat.contains("bebida")) {
            return "🧃";
        }
        if (cat.contains("snack")) {
            return "🍪";
        }
        if (cat.contains("fruta") || cat.contains("verdura")) {
            return "🥑";
        }
        return "🛒";
    }
    
    public boolean isBajoStock() { return stock <= stockMinimo; }
    public boolean isAgotado() { return stock <= 0; }

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
