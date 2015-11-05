/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.intrasite;

import java.io.File;

/**
 *
 * @author edimar
 */
public class Teste {
    public static void main(String[] args) {
        String st = "1/01/2015";
        System.out.println(st.matches("\\d{1,2}/\\d{1,2}/\\d{4}"));
    }
}
