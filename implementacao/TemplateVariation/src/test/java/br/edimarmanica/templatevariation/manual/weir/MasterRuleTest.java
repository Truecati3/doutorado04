/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.templatevariation.manual.weir;

import br.edimarmanica.dataset.Attribute;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.metrics.SiteWithoutThisAttribute;
import br.edimarmanica.metrics.weir.ResultsWeir;
import br.edimarmanica.templatevariation.manual.MasterRule;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import static junit.framework.Assert.assertEquals;
import junit.framework.TestCase;

/**
 *
 * @author edimar
 */
public class MasterRuleTest extends TestCase {

    public MasterRuleTest(String testName) {
        super(testName);
    }

    /**
     * Test of getMasterRule method, of class MasterRuleWeir.
     */
    public void testGetMasterRule() throws Exception {
        System.out.println("getMasterRule");

        String expResult = "rule_263.csv";

        Site site = br.edimarmanica.dataset.weir.book.Site.BLACKWELL;
        Attribute attribute = br.edimarmanica.dataset.weir.book.Attribute.TITLE;
        ResultsWeir results = new ResultsWeir(site);
        Map<String, Map<String, String>> allRules = results.loadAllRules();

        MasterRule master = new MasterRule(site, attribute, allRules);
        try {
            String result = master.getMasterRule();
            assertEquals(expResult, result);
        } catch (SiteWithoutThisAttribute ex) {
            Logger.getLogger(MasterRule.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
