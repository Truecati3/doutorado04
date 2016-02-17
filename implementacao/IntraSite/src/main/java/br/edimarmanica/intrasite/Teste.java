/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.intrasite;



/**
 *
 * @author edimar
 */
public class Teste {

    public static void main(String[] args) {

        String st = "AvisioTech Corp.";
        String st2 = "Avisio 27.15 Tech - & Corp 15.​";
        System.out.println("["+st2.replaceAll("[^(a-zA-Z)|\\d|\\.]", "").trim()+"]");
    }
}
