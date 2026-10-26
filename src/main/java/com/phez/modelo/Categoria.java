package com.phez.modelo;

import java.io.Serializable;

public class Categoria implements Serializable {
    
    private int idCategoria;
    private String nombre;
    private String descripcion;
    private String status = "ACTIVO";
    private int totalProductos;
    
    public boolean isActivo(){ return "ACTIVO".equals(status); }

    public String getIcono() {
        String n = nombre == null ? "" : java.text.Normalizer.normalize(nombre.toLowerCase(), java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        if (n.contains("grano")) return "🌾";
        if (n.contains("lacteo")) return "🥛";
        if (n.contains("bebida")) return "🧃";
        if (n.contains("snack")) return "🍪";
        if (n.contains("fruta") || n.contains("verdura")) return "🥕";
        if (n.contains("carne")) return "🥩";
        if (n.contains("limpieza")) return "🧴";
        return "🛒";
    }
    
    public int getIdCategoria(){ return idCategoria; } 
    public void setIdCategoria( int idCategoria) { this.idCategoria = idCategoria; }
    public String getNombre(){ return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; } 
    public void setDescripcion (String descripcion) { this.descripcion = descripcion; }
    public String getStatus () {return status; }
    public void setStatus(String status) { this.status=status; } 
    public int getTotalProductos() { return totalProductos; }
    public void setTotalProductos(int totalProductos) { this.totalProductos = totalProductos; }
    
}