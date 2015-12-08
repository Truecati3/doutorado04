/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.intrasite.filter;

import br.edimarmanica.dataset.Site;
import br.edimarmanica.expressiveness.generate.beans.CypherRule;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author edimar
 */
public class TemplateNodesFilter extends RulesFilter {

    public TemplateNodesFilter(Site site) {
        super(site);
    }

    /**
     * We discard rules whose extracted values include template nodes
     *
     * @param rules
     * @return
     */
    @Override
    public Set<CypherRule> execute(Set<CypherRule> rules) {
        Set<CypherRule> rulesFiltered = new HashSet<>();

        int i = 0;
        for (CypherRule rule : rules) {
           
            if (!containsTemplate(rule)) {
                rulesFiltered.add(rule);
            }
            i++;
        }
        return rulesFiltered;
    }

    private boolean containsTemplate(CypherRule rule) {
        List<Object> values = neo4j.querySingleColumn(rule.getQuery(), rule.getParams(), "template");
        for (Object value : values) {
            if (((Boolean) value)) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        Set<CypherRule> rules = new HashSet<>();
        String query01 = "MATCH (n:CandValue) RETURN 'Template' in LABELS(n) as template LIMIT ";
        String query02 = "MATCH (n:Template) RETURN 'Template' in LABELS(n) as template LIMIT ";
        for (int i = 1; i < 10; i++) {
            rules.add(new CypherRule(query01 + i, null, "nada"));
        }
        for (int i = 1; i < 10; i++) {
            rules.add(new CypherRule(query02 + i, null, "nada"));
        }


        TemplateNodesFilter filter = new TemplateNodesFilter(br.edimarmanica.dataset.weir.book.Site.AMAZON);
        Set<CypherRule> rulesFiltered = filter.filter(rules);
        for (CypherRule rule : rulesFiltered) {
            System.out.println(rule);
            System.out.println("");
        }
    }
}
