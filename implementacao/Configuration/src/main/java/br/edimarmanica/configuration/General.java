/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.configuration;

import br.edimarmanica.extractionrules.neo4j.Neo4jHandlerType;

/**
 *
 * @author edimar
 */
public class General {

    public static boolean DEBUG = false;
    
    /**
     * Used to concat and/or split two strings
     */
    public static final String SEPARADOR = "_@_";
    
    public static final Neo4jHandlerType NEO4J_TYPE = Neo4jHandlerType.LOCAL;
}
