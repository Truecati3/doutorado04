/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.trinity.intrasitemapping.auto;

/**
 * based on InvalidTextNode do Módulo HtmltoCSVtoNeo4j
 * @author edimar
 */
public class InvalidValue extends Exception {

    public InvalidValue(String value) {
        super("Invalid Text Node -- Value:["+value+"]");
    }
    
}
