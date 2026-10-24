/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phez.modelo;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 *
 * @author erick
 */
public class Lote {

    private int idLote;
    private String numeroLote;
    private int cantidad;
    private Date fechaVencimiento;
    private Date fechaIngreso;
    private String status = "ACTIVO";
    private int idProducto;
    private String productoNombre;
    private String productoCodigo;

    public long getDiasParaVencer() {
        if (fechaVencimiento == null) {
            return Long.MAX_VALUE;
        }
        LocalDate v = fechaVencimiento instanceof java.sql.Date
                ? ((java.sql.Date) fechaVencimiento).toLocalDate()
                : fechaVencimiento.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return ChronoUnit.DAYS.between(LocalDate.now(), v);
    }

    public boolean isVencido() {
        return getDiasParaVencer() < 0;
    }

    public String getEstadoVencimiento() {
        long dias = getDiasParaVencer();
        if (dias < 0) {
            return "VENCIDO";
        }
        if (dias == 0) {
            return "Vence hoy";
        }
        if (dias <= 30) {
            return "Vence en " + dias + " día(s)";
        }
        return "Vigente";
    }

    public int getIdLote() {
        return idLote;
    }

    public void setIdLote(int idLote) {
        this.idLote = idLote;
    }

    public String getNumeroLote() {
        return numeroLote;
    }

    public void setNumeroLote(String numeroLote) {
        this.numeroLote = numeroLote;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public Date getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(Date fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public Date getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(Date fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getProductoNombre() {
        return productoNombre;
    }

    public void setProductoNombre(String productoNombre) {
        this.productoNombre = productoNombre;
    }

    public String getProductoCodigo() {
        return productoCodigo;
    }

    public void setProductoCodigo(String productoCodigo) {
        this.productoCodigo = productoCodigo;
    }

    public Object proximosAVencer(int i) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
