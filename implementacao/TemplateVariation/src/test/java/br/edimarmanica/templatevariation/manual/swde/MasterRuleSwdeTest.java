/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.templatevariation.manual.swde;

import br.edimarmanica.dataset.Attribute;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.metrics.SiteWithoutThisAttribute;
import br.edimarmanica.metrics.swde.ResultsSWDE;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import static junit.framework.Assert.assertEquals;
import junit.framework.TestCase;

/**
 *
 * @author edimar
 */
public class MasterRuleSwdeTest extends TestCase {

    public MasterRuleSwdeTest(String testName) {
        super(testName);
    }

    /**
     * Test of getMasterRule method, of class MasterRuleSwde.
     */
    public void testGetMasterRule() throws Exception {
        System.out.println("getMasterRule");

        String expResult = "rule_227.csv";


        Site site = br.edimarmanica.dataset.swde.book.Site.BOOKDEPOSITORY;
        Attribute attribute = br.edimarmanica.dataset.swde.book.Attribute.TITLE;
        ResultsSWDE results = new ResultsSWDE(site);
        Map<String, Map<Integer, String>> allRules = results.loadAllRules();

        MasterRuleSwde master = new MasterRuleSwde(site, attribute, allRules);
        try {
            String result = master.getMasterRule();
            assertEquals(expResult, result);
        } catch (SiteWithoutThisAttribute ex) {
            Logger.getLogger(MasterRuleSwde.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
