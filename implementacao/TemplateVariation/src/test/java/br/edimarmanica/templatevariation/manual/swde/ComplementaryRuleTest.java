/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.templatevariation.manual.swde;

import br.edimarmanica.configuration.Paths;
import br.edimarmanica.templatevariation.manual.ComplementaryRule;
import br.edimarmanica.dataset.Attribute;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.metrics.Results;
import br.edimarmanica.metrics.SiteWithoutThisAttribute;
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
     * Test of getComplementaryRule method, of class ComplementaryRule.
     */
    public void testGetComplementaryRule() throws Exception {
        System.out.println("getComplementaryRule");

        String expResult = "rule_1580.csv";

        Site site = br.edimarmanica.dataset.swde.camera.Site.AMAZON;
        Attribute attribute = br.edimarmanica.dataset.swde.camera.Attribute.MANUFECTURER;
        Results results = new Results(site);
        Map<String, Map<String, String>> allRules = results.loadAllRules(Paths.PATH_INTRASITE);

        List<String> masterRuleIDs = new ArrayList<>();
        masterRuleIDs.add("rule_1100.csv");
        Map<String, String> masterRuleValues = allRules.get("rule_1100.csv");

        ComplementaryRule rum = new ComplementaryRule(site, attribute, allRules, masterRuleIDs, masterRuleValues, UnionRules.getNrPages(site));
        try {
            String result = rum.getComplementaryRule();
            assertEquals(expResult, result);
        } catch (SiteWithoutThisAttribute ex) {
            Logger.getLogger(ComplementaryRule.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void testGetComplementaryRule02() throws Exception {
        String expResult = "rule_3365.csv";

        Site site = br.edimarmanica.dataset.swde.book.Site.AMAZON;
        Attribute attribute = br.edimarmanica.dataset.swde.book.Attribute.AUTHOR;
        Results results = new Results(site);
        Map<String, Map<String, String>> allRules = results.loadAllRules(Paths.PATH_INTRASITE);

        List<String> masterRuleIDs = new ArrayList<>();
        masterRuleIDs.add("rule_10636.csv");
        masterRuleIDs.add("rule_7588.csv");
        Map<String, String> masterRuleValues = allRules.get(masterRuleIDs.get(0));
        masterRuleValues.putAll(allRules.get(masterRuleIDs.get(1)));

        ComplementaryRule rum = new ComplementaryRule(site, attribute, allRules, masterRuleIDs, masterRuleValues, UnionRules.getNrPages(site));
        try {
            String result = rum.getComplementaryRule();
            assertEquals(expResult, result);
        } catch (SiteWithoutThisAttribute ex) {
            Logger.getLogger(ComplementaryRule.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
