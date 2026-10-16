/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phez.util;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 *
 * @author erick
 */
public class Validador {

    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");
    private static final Pattern TELEFONO = Pattern.compile("^[0-9 +-]{8,20}$");

    private Validador() {

    }

    public static boolean vacio(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static String limpiar(String s) {
        return s == null ? "" : s.trim();
    }

    public static boolean email(String s) {
        return s != null && s.length() <= 120 && EMAIL.matcher(s).matches();
    }

    public static boolean telefono(String s) {
        return vacio(s) || TELEFONO.matcher(s).matches();
    }

    public static boolean nit(String s) {
        return s != null && s.matches("CF|[0-9]{1,12}-?[0-9K]");
    }

    public static boolean passwordSegura(String s) {
        return s != null && s.length() >= 8 && s.matches(".*[A-Za-z].*") && s.matches(".*\\d.*");
    }

    public static int entero(String s, int porDefecto) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return porDefecto;
        }
    }

    public static BigDecimal decimal(String s) {
        try {
            return new BigDecimal(s.trim());
        } catch (Exception e) {
            return null;
        }
    }

}
