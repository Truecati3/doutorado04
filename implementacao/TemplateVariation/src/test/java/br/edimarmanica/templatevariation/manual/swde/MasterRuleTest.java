/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.templatevariation.manual.swde;

import br.edimarmanica.templatevariation.manual.MasterRule;
import br.edimarmanica.dataset.Attribute;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.metrics.SiteWithoutThisAttribute;
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
     * Test of getMasterRule method, of class MasterRule.
     */
    public void testGetMasterRule() throws Exception {
        System.out.println("getMasterRule");

        String expResult = "rule_227.csv";


        Site site = br.edimarmanica.dataset.swde.book.Site.BOOKDEPOSITORY;
        Attribute attribute = br.edimarmanica.dataset.swde.book.Attribute.TITLE;

        try {
            String result = MasterRule.getMasterRule(site, attribute);
            assertEquals(expResult, result);
        } catch (SiteWithoutThisAttribute ex) {
            Logger.getLogger(MasterRule.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
