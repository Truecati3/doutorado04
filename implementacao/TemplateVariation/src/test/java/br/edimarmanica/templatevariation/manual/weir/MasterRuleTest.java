/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.templatevariation.manual.weir;

import br.edimarmanica.dataset.Attribute;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.metrics.SiteWithoutThisAttribute;
import br.edimarmanica.templatevariation.manual.MasterRule;
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
    public void testGetMasterRuleWeir() throws Exception {
        System.out.println("getMasterRuleWeir");

        String expResult = "rule_263.csv";

        Site site = br.edimarmanica.dataset.weir.book.Site.BLACKWELL;
        Attribute attribute = br.edimarmanica.dataset.weir.book.Attribute.TITLE;
        
        try {
            String result = MasterRule.getMasterRule(site, attribute);
            assertEquals(expResult, result);
        } catch (SiteWithoutThisAttribute ex) {
            Logger.getLogger(MasterRule.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
     /**
     * Test of getMasterRule method, of class MasterRuleWeir.
     */
    public void testGetMasterRuleSWDE() throws Exception {
        System.out.println("getMasterRuleSWDE");

        String expResult = "rule_12420.csv";

        Site site = br.edimarmanica.dataset.swde.book.Site.CHRISTIANBOOK;
        Attribute attribute = br.edimarmanica.dataset.swde.book.Attribute.TITLE;
        
        try {
            String result = MasterRule.getMasterRule(site, attribute);
            assertEquals(expResult, result);
        } catch (SiteWithoutThisAttribute ex) {
            Logger.getLogger(MasterRule.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
