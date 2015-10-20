/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.runner;

import br.edimarmanica.dataset.Site;
import br.edimarmanica.extractionrules.load.DirectoryToNeo4j;
import br.edimarmanica.extractionrules.neo4j.Neo4jHandlerType;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author edimar
 */
public class HtmlToNeo4j {
    
    public void execute(Site site){
        DirectoryToNeo4j load = new DirectoryToNeo4j(site, true);
        try {
            load.loadPages();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DirectoryToNeo4j.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
