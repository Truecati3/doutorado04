/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.intrasite;

import br.edimarmanica.dataset.Site;
import br.edimarmanica.extractionrules.neo4j.Neo4jHandler;
import br.edimarmanica.extractionrules.neo4j.Neo4jHandlerType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author edimar
 */
public class NullValuesFilter extends RulesFilter {

    private static final double PR_NULL_VALUES = 80; //percentual máximo de null values admitido para uma regra

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
    public Set<String> execute(Set<String> rules) {
        long nrPages = getNrPages();

        Set<String> rulesFiltered = new HashSet<String>();

        List<NullValuesRuleAnalyzer> analyzers = new ArrayList<NullValuesRuleAnalyzer>();
        List<Thread> threads = new ArrayList<Thread>();

        /**
         * * Disparando consultas ao neo4j em paralelo **
         */
        int i = 0;
        for (String rule : rules) {
            analyzers.add(new NullValuesRuleAnalyzer(super.neo4j, rule));
            threads.add(new Thread(analyzers.get(i)));
            threads.get(i).start();
            i++;
        }

        /**
         * * fork ***
         */
        for (int j = 0; j < threads.size(); j++) {
            try {
                threads.get(j).join();
            } catch (InterruptedException ex) {
                Logger.getLogger(TemplateNodesFilter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        /**
         * ** Obtendo as respostas ***
         */
        for (int j = 0; j < analyzers.size(); j++) {
            if (((analyzers.get(j).getNrNotNullPages() - nrPages) * 100 / nrPages) <= PR_NULL_VALUES) {
                rulesFiltered.add(analyzers.get(j).getRule());
            }
        }
        return rulesFiltered;

    }
}
class NullValuesRuleAnalyzer implements Runnable {

    private volatile double nrNotNullPages;
    private Neo4jHandler neo4j;
    private String rule;

    public NullValuesRuleAnalyzer(Neo4jHandler neo4j, String rule) {
        this.neo4j = neo4j;
        this.rule = rule;
    }

    public void run() {
        nrNotNullPages = findNrNotNullPages();
    }

    public double getNrNotNullPages() {
        return nrNotNullPages;
    }

    public String getRule() {
        return rule;
    }

    private int findNrNotNullPages() {
        Set<String> urls = new HashSet<String>();
        List<Object> values = neo4j.querySingleColumn(rule, "URL");
        for (Object value : values) {
            urls.add(value.toString());
        }
        return urls.size();
    }
}
