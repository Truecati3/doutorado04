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
public class TemplateNodesFilter extends RulesFilter {

    public TemplateNodesFilter(Site site, Neo4jHandlerType type) {
        super(site, type);
    }

    
    /**
     * We discard rules whose extracted values include template nodes
     *
     * @param rules
     * @return
     */
    @Override
    public Set<String> execute(Set<String> rules) {
        Set<String> rulesFiltered = new HashSet<String>();

        List<TemplateRuleAnalyzer> analyzers = new ArrayList<TemplateRuleAnalyzer>();
        List<Thread> threads = new ArrayList<Thread>();

        /**
         * * Disparando consultas ao neo4j em paralelo **
         */
        int i = 0;
        for (String rule : rules) {
            analyzers.add(new TemplateRuleAnalyzer(super.neo4j, rule));
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
            if (!analyzers.get(j).hasTemplate()) {
                rulesFiltered.add(analyzers.get(j).getRule());
            }
        }
        return rulesFiltered;
    }

    public static void main(String[] args) {
        Set<String> rules = new HashSet<String>();
        String query01 = "MATCH (n:CandValue) RETURN 'Template' in LABELS(n) as template LIMIT ";
        String query02 = "MATCH (n:Template) RETURN 'Template' in LABELS(n) as template LIMIT ";
        for (int i = 1; i < 10; i++) {
            rules.add(query01 + i);
        }
        for (int i = 1; i < 10; i++) {
            rules.add(query02 + i);
        }


        TemplateNodesFilter filter = new TemplateNodesFilter(br.edimarmanica.dataset.weir.book.Site.AMAZON, Neo4jHandlerType.LOCAL);
        Set<String> rulesFiltered = filter.filter(rules);
        for (String rule : rulesFiltered) {
            System.out.println(rule);
            System.out.println("");
        }
    }
}
class TemplateRuleAnalyzer implements Runnable {

    private volatile boolean containsTemplate;
    private Neo4jHandler neo4j;
    private String rule;

    public TemplateRuleAnalyzer(Neo4jHandler neo4j, String rule) {
        this.neo4j = neo4j;
        this.rule = rule;
    }

    public void run() {
        containsTemplate = containsTemplate();
    }

    public boolean hasTemplate() {
        return containsTemplate;
    }

    public String getRule() {
        return rule;
    }

    private boolean containsTemplate() {
        List<Object> values = neo4j.querySingleColumn(rule, "template");
        for (Object value : values) {
            if (((Boolean) value)) {
                return true;
            }
        }
        return false;
    }
}
