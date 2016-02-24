/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.entitysimilarity;

import junit.framework.TestCase;

/**
 *
 * @author edimar
 */
public class ISBNSimilarityTest extends TestCase {
    
    public ISBNSimilarityTest(String testName) {
        super(testName);
    }

    /**
     * Test of similaritySpecific method, of class ISBNSimilarity.
     */
    public void testSimilaritySpecific() {
        System.out.println("similaritySpecific");
        String valueR1 = "9780807071243";
        String valueR2 = "9780807071243";
        ISBNSimilarity instance = new ISBNSimilarity();
        double expResult = 1.0;
        double result = instance.similaritySpecific(valueR1, valueR2);
        assertEquals(expResult, result, 0.0);
        
    }
}
