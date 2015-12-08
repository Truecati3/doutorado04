/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.weir.util;

/**
 *
 * @author edimar
 */
public class ValueNormalizer {
    public static String normalize(String value){
        //o replace abaixo foi adicionado pq os filtros eliminam se os valores são iguais para diferentes regras do mesmo site, mas as vezes os valores de uma regra tinham um símbolo como >>
        return value.replaceAll("[^((a-zA-Z)|\\s|\\d||\\.||,||\\$||€)]+", "").trim();
    }
}
