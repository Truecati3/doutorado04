/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.weir.algorithms.integration;

/**
 *
 * @author edimar
 */
public class Teste {
    public static void main(String[] args) {
        String st = "» The New Naturalist, Ireland - 15";
        System.out.println(st.replaceAll("[^((a-zA-Z)|\\s|\\d)]+", ""));
        //st = "abelha";
        //System.out.println(st.matches("\\d+((,|.)\\d+)*"));
    }
}
