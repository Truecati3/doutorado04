/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.trinity.intrasitemapping.auto;

import br.edimarmanica.trinity.extract.Extract;

/**
 *
 * @author edimar
 */
public class Preprocessing {

    public static String filter(String st) throws InvalidValue {
        String aux = st.replaceAll("<[^>]*>", ""); //removing tags
        aux = aux.replaceAll("\\&[^;]*;", "");//removing html entities
        aux = aux.replaceAll("\"", "").replaceAll("\\\\", ""); //removing some unsupported characteres
        aux = aux.replace((char) 160, ' ').replaceAll("\n", " ").replaceAll("\\s\\s+", " ");//removing extra white space
        
        check(aux);
        
        return aux;
    }
    
    public static void check(String value) throws InvalidValue {

        //nada
        if (value.isEmpty()) {
            throw new InvalidValue(value);
        }

        //muito grande
        if (value.length() > Extract.VALUE_MAX_LENGHT) {
            throw new InvalidValue(value);
        }

        //sem dígito ou letra
        if (!value.matches(".*(\\d|[a-zA-Z]).*")) {
            throw new InvalidValue(value);
        }
    }
}
