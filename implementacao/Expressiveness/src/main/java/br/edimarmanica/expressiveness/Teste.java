/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.expressiveness;

import br.edimarmanica.expressiveness.extract.QueryNeo4J;



/**
 *
 * @author edimar
 */
public class Teste {

    public static void main(String[] args) {
        String st = "-0.98\n%";
        
        System.out.println(st.matches(".*(\\d|[a-zA-Z]).*"));
        System.out.println(st.matches(".*\\d.*"));

    }
}
