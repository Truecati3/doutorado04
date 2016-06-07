/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.templatevariation.manual.weir;

import br.edimarmanica.configuration.Paths;
import br.edimarmanica.dataset.Attribute;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.metrics.Results;
import br.edimarmanica.metrics.SiteWithoutThisAttribute;
import br.edimarmanica.templatevariation.manual.ComplementaryRule;
import br.edimarmanica.templatevariation.manual.UnionRules;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import static junit.framework.Assert.assertEquals;
import junit.framework.TestCase;

/**
 *
 * @author edimar
 */
public class ComplementaryRuleTest extends TestCase {

    public ComplementaryRuleTest(String testName) {
        super(testName);
    }

    /**
     * Test of getComplementaryRule method, of class ComplementaryRuleWeir.
     */
    public void testGetComplementaryRule() throws Exception {
        System.out.println("getComplementaryRule");

        String expResult = "rule_313.csv";

        Site site = br.edimarmanica.dataset.weir.book.Site.BLACKWELL;
        Attribute attribute = br.edimarmanica.dataset.weir.book.Attribute.TITLE;
        Results results = new Results(site);
        Map<String, Map<String, String>> allRules = results.loadAllRules(Paths.PATH_INTRASITE);

        List<String> masterRuleIDs = new ArrayList<>();
        masterRuleIDs.add("rule_263.csv");
        Map<String, String> masterRuleValues = allRules.get(masterRuleIDs.get(0));

        ComplementaryRule rum = new ComplementaryRule(site, attribute, allRules, masterRuleIDs, masterRuleValues, UnionRules.getNrPages(site));
        try {
            String result = rum.getComplementaryRule();
            assertEquals(expResult, result);
        } catch (SiteWithoutThisAttribute ex) {
            Logger.getLogger(ComplementaryRuleTest.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
