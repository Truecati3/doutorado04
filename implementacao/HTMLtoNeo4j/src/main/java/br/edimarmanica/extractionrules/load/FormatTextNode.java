/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.extractionrules.load;

import br.edimarmanica.configuration.Html2Neo4j;


/**
 *
 * @author edimar
 */
public class FormatTextNode {
    
    public static String format(String value) throws InvalidTextNode{
        
        
        //remove extra spaces
        String ret = value.replace((char)160, ' ').replaceAll("\n", " ").replaceAll("\\s\\s+", " ").trim();  //esse 160 é um espaço que surge a partir do "&nbsp;"
        
        check(ret);
        
        return ret;
    }
    
    public static void check(String value) throws InvalidTextNode{
        
        
        //nada
        if (value.isEmpty()){
            throw new InvalidTextNode(value);
        }
        
        //muito grande
        if (value.length() > Html2Neo4j.TEXT_NODE_MAX_LENGHT){
            throw new InvalidTextNode(value);
        }
        
        //sem dígito ou letra
        if (!value.matches(".*(\\d|[a-zA-Z]).*")){
            throw new InvalidTextNode(value);
        }
    }
}
