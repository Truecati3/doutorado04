/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.intrasite.filter;

import br.edimarmanica.dataset.Site;
import br.edimarmanica.expressiveness.generate.beans.CypherRule;
import br.edimarmanica.htmltocsvtoneo4j.neo4j.Neo4jHandler;
import java.util.Set;

/**
 *
 * @author edimar
 */
public abstract class RulesFilter {

    Neo4jHandler neo4j;
    private Site site;

    public RulesFilter(Site site) {
        this.site = site;
    }
    
    

    public Set<CypherRule> filter(Set<CypherRule> rules) {
        neo4j = new Neo4jHandler(site);
        Set<CypherRule> rulesFiltered = execute(rules);
        neo4j.shutdown();
        return rulesFiltered;
    }

    public abstract Set<CypherRule> execute(Set<CypherRule> rules);
}
