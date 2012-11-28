/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kemikal.commons;

/**
 *
 * @author marsan
 */
public class generaMD5 {
        //funcion que codifica en md5
        public static String MD5(String md5) {
           try {
                java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
                byte[] array = md.digest(md5.getBytes());
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < array.length; ++i) {
                  sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
               }
                return sb.toString();
            } catch (java.security.NoSuchAlgorithmException e) {
            }
            return null;
        }
}
