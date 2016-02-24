/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.entitysimilarity;

import static junit.framework.Assert.assertEquals;
import junit.framework.TestCase;

/**
 *
 * @author edimar
 */
public class CurrencySimilarityTest extends TestCase {
    
    public CurrencySimilarityTest(String testName) {
        super(testName);
    }

    /**
     * Test of normalize method, of class CurrencySimilarity.
     */
    public void testNormalize() {
        System.out.println("normalize");
        String numericValue = "R$ 15.325,23";//tem que ter o espaço depois do cifrão
        CurrencySimilarity instance = new CurrencySimilarity();
        Double expResult = new Double(15325.23);
        Double result = instance.normalize(numericValue);
        assertEquals(expResult, result);
        
        
        numericValue = "$43,217.45";
        expResult = new Double(43217.45);
        result = instance.normalize(numericValue);
        assertEquals(expResult, result);
        
        numericValue = "€ 2123,89";
        expResult = new Double(2123.89);
        result = instance.normalize(numericValue);
        assertEquals(expResult, result);
        
        numericValue = "8,759.59 €";
        System.out.println(numericValue.matches(".*\\.\\d\\d €"));
        expResult = new Double(8759.59);
        result = instance.normalize(numericValue);
        assertEquals(expResult, result);
    }
}
