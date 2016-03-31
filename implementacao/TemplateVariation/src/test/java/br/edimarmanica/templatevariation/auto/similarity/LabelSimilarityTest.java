/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.templatevariation.auto.similarity;

import br.edimarmanica.templatevariation.auto.bean.Rule;
import junit.framework.TestCase;

/**
 *
 * @author edimar
 */
public class LabelSimilarityTest extends TestCase {
    
    public LabelSimilarityTest(String testName) {
        super(testName);
    }

    /**
     * Test of score method, of class LabelSimilarity.
     */
    public void testSimilarity() {
        System.out.println("similarity");
        Rule masterRule = new Rule();
        masterRule.setLabel("Height");

        Rule complementaryRule = new Rule();
        complementaryRule.setLabel("Heig.");
        //complementaryRule.setLabel("Height:");
        
        LabelSimilarity instance = new LabelSimilarity(masterRule, complementaryRule);
        double expResult = 0.893;
        double result = instance.score();
        assertEquals(expResult, result, 0.001);
        
    }
}
