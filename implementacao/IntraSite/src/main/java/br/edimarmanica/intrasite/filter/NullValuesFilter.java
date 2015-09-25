/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.intrasite.filter;

import br.edimarmanica.dataset.Site;
import br.edimarmanica.expressiveness.generate.beans.CypherRule;
import br.edimarmanica.extractionrules.neo4j.Neo4jHandlerType;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 *
 * @author edimar
 */
public class NullValuesFilter extends RulesFilter {

    private static final double PR_NULL_VALUES = 80; //percentual máximo de null values admitido para uma regra
    private boolean DEBUG = false;

    public NullValuesFilter(Site site, Neo4jHandlerType type) {
        super(site, type);
    }

    private long getNrPages() {
        String cypher = "MATCH n RETURN COUNT(DISTINCT n.URL) AS qtde";
        return (Long) (neo4j.querySingleColumn(cypher, "qtde").get(0));
    }

    /**
     * We discard rules whose a large majority (more than 80%) of null values.
     *
     * @param rules
     * @return
     */
    @Override
    public Set<CypherRule> execute(Set<CypherRule> rules) {
        long nrPages = getNrPages();

        Set<CypherRule> rulesFiltered = new HashSet<>();

        int i = 0;
        for (CypherRule rule : rules) {
            
            if (DEBUG) {
                System.out.println("Filtrando regra[" + i + "]: " + rule.getQueryWithoutParameters());
            }

            if (((nrPages - findNrNotNullPages(rule)) * 100 / nrPages) <= PR_NULL_VALUES) {
                rulesFiltered.add(rule);
            }
            i++;
        }
        return rulesFiltered;
    }

    private int findNrNotNullPages(CypherRule rule) {
        Set<String> urls = new HashSet<>();
        List<Object> values = neo4j.querySingleColumn(rule.getQuery(), rule.getParams(), "URL");
        for (Object value : values) {
            urls.add(value.toString());
        }
        return urls.size();
    }
}
