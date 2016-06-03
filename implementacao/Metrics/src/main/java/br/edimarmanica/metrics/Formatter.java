/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.metrics;

import org.apache.commons.lang.StringEscapeUtils;

/**
 *
 * @author edimar
 */
public class Formatter {
 
    public static String formatValue(String value){
        return StringEscapeUtils.unescapeHtml(value)
                .replaceAll(" ", " ")
                .replaceAll("\\\\", "")
                .replaceAll("\"", "")
                .replaceAll("\\s+", " ")
                .replaceAll("<[^>]*>", "") //removing tags
                .replaceAll("\\&[^;]*;", "")//removing html entities
                .replaceAll("[^(a-zA-Z)|\\d|\\.]", ""); //só deixa números, letras e o ponto
    }
    
    public static String formatURL(String url){
        return url.replaceAll(".*/", "").replaceAll("\\..*", "");
    }
}
