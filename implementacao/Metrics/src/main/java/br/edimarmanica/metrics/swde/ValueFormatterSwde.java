/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.metrics.swde;

import br.edimarmanica.metrics.ValueFormatter;
import org.apache.commons.lang.StringEscapeUtils;

/**
 *
 * @author edimar
 */
public class ValueFormatterSwde implements ValueFormatter{

    @Override
    public String format(String value) {
       /*return value.replaceAll("&#039;", "'")
         .replaceAll("&amp;", "&")
         .replaceAll("&quot;", "\"")
         .replaceAll("&nbsp;", " ")
         .replaceAll("&#46;", ".")
         .replaceAll("&#45;", "-")
         .replaceAll("&#40;", "(")
         .replaceAll("&#41;", ")")
         .replaceAll("&reg;", "®")
         .replaceAll("&#34;", "\"")
         .replaceAll("&#47;", "/")
         .replaceAll("&#43;", "+")
         .trim();*/

        return StringEscapeUtils.unescapeHtml(value)
                .replaceAll(" ", " ")
                .replaceAll("\\\\", "")
                .replaceAll("\"", "")
                .replaceAll("\\s+", " ")
                .replaceAll("[^(a-zA-Z)|\\d|\\.]", ""); //só deixa números, letras e o ponto
    }
    
}
