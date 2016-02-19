/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.templatevariation.manual.weir;

import br.edimarmanica.dataset.Attribute;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.metrics.SiteWithoutThisAttribute;
import br.edimarmanica.metrics.weir.ResultsWeir;
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
public class MasterRuleWeirTest extends TestCase {

    public MasterRuleWeirTest(String testName) {
        super(testName);
    }

    /**
     * Test of getMasterRule method, of class MasterRuleWeir.
     */
    public void testGetMasterRule() throws Exception {
        System.out.println("getMasterRule");

        String expResult = "rule_120.csv";

        Site site = br.edimarmanica.dataset.weir.book.Site.BLACKWELL;
        Attribute attribute = br.edimarmanica.dataset.weir.book.Attribute.TITLE;
        ResultsWeir results = new ResultsWeir(site);
        Map<String, Set<String>> allRules = results.loadAllRules();

        MasterRuleWeir master = new MasterRuleWeir(site, attribute, allRules);
        try {
            String result = master.getMasterRule();
            assertEquals(expResult, result);
        } catch (SiteWithoutThisAttribute ex) {
            Logger.getLogger(MasterRuleWeir.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
