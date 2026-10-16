/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.phez.util;

import java.security.SecureRandom;
import java.util.Base64;
import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author erick
 */
public class Seguridad {

    private static final SecureRandom SECURE = new SecureRandom();

    private Seguridad() {
    }

    public static String hashPassword(String txtPlano) {
        return BCrypt.hashpw(txtPlano, BCrypt.gensalt(12));
    }

    public static boolean verificarPassword(String txtPlano, String txtHash) {
        if (txtPlano == null || txtHash == null || !txtHash.startsWith("$2")) {
            return false;
        }
        try {
            return BCrypt.checkpw(txtPlano, txtHash);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    
    public static String token(int bytes) {
        byte[] b = new byte[bytes];
        SECURE.nextBytes(b);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(b);
    }
    
    public static void main(String[] args) {
        String pass = args.length > 0 ? args[0] : "Admin123";
        System.out.println(pass + " -> " + hashPassword(pass));
    }

}
