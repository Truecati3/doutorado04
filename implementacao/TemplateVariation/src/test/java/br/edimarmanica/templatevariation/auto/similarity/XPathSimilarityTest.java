/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.templatevariation.auto.similarity;

import br.edimarmanica.templatevariation.auto.similarity.XPathSimilarity;
import br.edimarmanica.templatevariation.auto.bean.Rule;
import static junit.framework.Assert.assertEquals;
import junit.framework.TestCase;

/**
 *
 * @author edimar
 */
public class XPathSimilarityTest extends TestCase {

    public XPathSimilarityTest(String testName) {
        super(testName);
    }

    /**
     * Test of score method, of class XPathSimilarity.
     */
    public void testSimilarity() {
        System.out.println("similarity");

        /**
         * ** teste 01 **
         */
        Rule masterRule = new Rule();
        masterRule.setXPath("DIV[1]/LI[2]/LI[1]/TEXT()");

        Rule complementaryRule = new Rule();
        complementaryRule.setXPath("DIV[1]/LI[2]/LI[1]/B[1]/TEXT()");

        XPathSimilarity instance = new XPathSimilarity(masterRule, complementaryRule);
        double expResult = 0.966;
        double result = instance.score();
        assertEquals("Teste 01", expResult, result, 0.001);

        /**
         * ** teste 02 **
         */
        complementaryRule.setXPath("DIV[1]/LI[2]/SPAM[1]/TEXT()");
        
        expResult = 0.954;
        result = instance.score();
        assertEquals("Teste 02", expResult, result, 0.001);
    }
}
