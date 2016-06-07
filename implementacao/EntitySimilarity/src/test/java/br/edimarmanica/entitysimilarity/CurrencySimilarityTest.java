/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.entitysimilarity;

import java.util.HashMap;
import java.util.Map;
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
    public void testNormalize() throws NoiseException {
        System.out.println("normalize");
        String numericValue = "R$ 15.325,23";//tem que ter o espaço depois do cifrão
        CurrencySimilarity instance = new CurrencySimilarity();
        Double expResult = new Double(15325.23);
        Double result = instance.normalize(numericValue);
        assertEquals(expResult, result);
        
        
        numericValue = "$ 43,217.45";
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
    
    public void testDistanceSpecific() throws InsufficientOverlapException {
        System.out.println("distanceSpecific");
        Map<String, String> r1S1 = new HashMap<>();
        r1S1.put("1", "R$ 3,00"); 
        r1S1.put("2", "R$ 4,00"); 
        r1S1.put("3", "R$ 5,00"); 
        r1S1.put("4", "R$ 6,00"); 
        r1S1.put("5", "R$ 7,00"); 
        r1S1.put("6", "R$ 7,00"); 

        Map<String, String> r1S2 = new HashMap<>();
        r1S2.put("1", "R$ 8,00");   // diferente
        r1S2.put("2", "R$ 4,00");  // igual
        r1S2.put("3", "R$ 9,00");    // diferente
        r1S2.put("4", "R$ 6,00");  // igual
        r1S2.put("5", "R$ 5,00");  // diferente
        r1S2.put("6", "ERROR");  // diferente


        CurrencySimilarity instance = new CurrencySimilarity();
        double expResult = 2.0 / 6;
        double result = instance.similarity(r1S1, r1S2);
        assertEquals(expResult, result, 0.0);

    }
}
