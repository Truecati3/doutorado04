/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.configuration;

/**
 * Configurations used to load HTML pages to Neo4j
 * 
 * @author edimar
 */
public class Html2Neo4j {

    /**
     * TEXT_NODE_MAX_LENGHT: Ignore textual nodes with size larger than TEXT_NODE_MAX_LENGHT
     */
    public static final int TEXT_NODE_MAX_LENGHT = 150; //Tem títulos no bookmooch (WEIR) com mais de 100 caracteres
    /**
     * TEMPLATES_MIN_PR_PAGES: percentual mínimo de páginas que um valor deve ocorrer no mesmo XPath sem índice para ser considerado um template
     */
    public static final int TEMPLATES_MIN_PR_PAGES = 40; //according to WEIR
    /**
     * BLACK_TAGS: ignore this tag and its children
     */
    public static final String[] BLACK_TAGS = {"script", "head"}; //lowercase
}
