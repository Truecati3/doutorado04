/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.extractionrules;

/**
 *
 * @author edimar
 */
public class Config {
    public static final int TEXT_NODE_MAX_LENGHT = 150; //Tem títulos no bookmooch (WEIR) com mais de 100 caracteres
    public static final int TEMPLATES_MIN_PR_PAGES = 40; //percentual mínimo de páginas que um valor deve ocorrer no mesmo XPath sem índice para ser considerado um template
    public static final short MAX_DISTANCE_NEAR_NODES = 6; 
    public static final String SEPARADOR = "_@_"; 
}
