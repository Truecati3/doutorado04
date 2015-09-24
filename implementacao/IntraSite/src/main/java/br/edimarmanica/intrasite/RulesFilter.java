/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.intrasite;

import br.edimarmanica.dataset.Site;
import br.edimarmanica.extractionrules.neo4j.Neo4jHandler;
import br.edimarmanica.extractionrules.neo4j.Neo4jHandlerType;
import java.util.Set;

/**
 *
 * @author edimar
 */
public abstract class RulesFilter {

    Neo4jHandler neo4j;
    private Site site;
    private Neo4jHandlerType type;

    public RulesFilter(Site site, Neo4jHandlerType type) {
        this.site = site;
        this.type = type;
    }
    
    

    public Set<String> filter(Set<String> rules) {
        neo4j = Neo4jHandler.getInstance(type, site);
        Set<String> rulesFiltered = execute(rules);
        neo4j.shutdown();
        return rulesFiltered;
    }

    public abstract Set<String> execute(Set<String> rules);
}
