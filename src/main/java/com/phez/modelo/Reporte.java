/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phez.modelo;
import java.math.BigDecimal;
/**
 *
 * @author erick
 */
public class Reporte {
    private String etiqueta;
    private String detalle;
    private BigDecimal monto = BigDecimal.ZERO;
    private long cantidad;
    
    public Reporte() { }
    
    public Reporte(String etiqueta, BigDecimal monto, long cantidad) {
        this.etiqueta = etiqueta;
        this.monto = monto == null ? BigDecimal.ZERO : monto;
        this.cantidad = cantidad;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public String getDetalle() {
        return detalle;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public long getCantidad() {
        return cantidad;
    }

    public void setEtiqueta(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public void setCantidad(long cantidad) {
        this.cantidad = cantidad;
    }
    
    
    
}
