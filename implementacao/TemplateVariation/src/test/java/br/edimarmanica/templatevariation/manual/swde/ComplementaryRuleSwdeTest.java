/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.templatevariation.manual.swde;

import br.edimarmanica.dataset.Attribute;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.metrics.SiteWithoutThisAttribute;
import br.edimarmanica.metrics.swde.ResultsSWDE;
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
public class ComplementaryRuleSwdeTest extends TestCase {

    public ComplementaryRuleSwdeTest(String testName) {
        super(testName);
    }

    /**
     * Test of getComplementaryRule method, of class ComplementaryRuleSwde.
     */
    public void testGetComplementaryRule() throws Exception {
        System.out.println("getComplementaryRule");

        String expResult = "rule_1580.csv";

        Site site = br.edimarmanica.dataset.swde.camera.Site.AMAZON;
        Attribute attribute = br.edimarmanica.dataset.swde.camera.Attribute.MANUFECTURER;
        ResultsSWDE results = new ResultsSWDE(site);
        Map<String, Map<Integer, String>> allRules = results.loadAllRules();

        List<String> masterRuleIDs = new ArrayList<>();
        masterRuleIDs.add("rule_1100.csv");
        Map<Integer, String> masterRuleValues = allRules.get("rule_1100.csv");

        ComplementaryRuleSwde rum = new ComplementaryRuleSwde(site, attribute, allRules, masterRuleIDs, masterRuleValues);
        try {
            String result = rum.getComplementaryRule();
            assertEquals(expResult, result);
        } catch (SiteWithoutThisAttribute ex) {
            Logger.getLogger(ComplementaryRuleSwde.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
