/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.templatevariation.manual.weir;

import br.edimarmanica.dataset.Attribute;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.metrics.SiteWithoutThisAttribute;
import br.edimarmanica.metrics.weir.ResultsWeir;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import static junit.framework.Assert.assertEquals;
import junit.framework.TestCase;

/**
 *
 * @author edimar
 */
public class ComplementaryRuleWeirTest extends TestCase {

    public ComplementaryRuleWeirTest(String testName) {
        super(testName);
    }

    /**
     * Test of getComplementaryRule method, of class ComplementaryRuleWeir.
     */
    public void testGetComplementaryRule() throws Exception {
        System.out.println("getComplementaryRule");

        String expResult = "rule_48.csv";

        Site site = br.edimarmanica.dataset.weir.book.Site.BLACKWELL;
        Attribute attribute = br.edimarmanica.dataset.weir.book.Attribute.TITLE;
        ResultsWeir results = new ResultsWeir(site);
        Map<String, Set<String>> allRules = results.loadAllRules();

        List<String> masterRuleIDs = new ArrayList<>();
        masterRuleIDs.add("rule_120.csv");
        Set<String> masterRuleValues = allRules.get("rule_120.csv");

        ComplementaryRuleWeir rum = new ComplementaryRuleWeir(site, attribute, allRules, masterRuleIDs, masterRuleValues);
        try {
            String result = rum.getComplementaryRule();
            assertEquals(expResult, result);
        } catch (SiteWithoutThisAttribute ex) {
            Logger.getLogger(ComplementaryRuleWeir.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
