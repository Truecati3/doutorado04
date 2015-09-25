/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.intrasite.filter;

import br.edimarmanica.dataset.Site;
import br.edimarmanica.expressiveness.generate.beans.CypherRule;
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
    
    

    public Set<CypherRule> filter(Set<CypherRule> rules) {
        neo4j = Neo4jHandler.getInstance(type, site);
        Set<CypherRule> rulesFiltered = execute(rules);
        neo4j.shutdown();
        return rulesFiltered;
    }

    public abstract Set<CypherRule> execute(Set<CypherRule> rules);
}
