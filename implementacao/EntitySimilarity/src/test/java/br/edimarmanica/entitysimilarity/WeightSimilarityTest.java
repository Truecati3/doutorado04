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
public class WeightSimilarityTest extends TestCase {

    public WeightSimilarityTest(String testName) {
        super(testName);
    }

    /**
     * Test of normalize method, of class WeightDistance.
     */
    public void testNormalize() throws NoiseException {
        System.out.println("normalize");
        String numericValue = "15 g";
        WeightSimilarity instance = new WeightSimilarity();
        Double expResult = new Double("15");
        Double result = instance.normalize(numericValue);
        assertEquals(expResult, result);


        numericValue = "2 kg";
        expResult = new Double(2000);
        result = instance.normalize(numericValue);
        assertEquals(expResult, result);
    }

    public void testDistanceSpecific() throws InsufficientOverlapException {
        System.out.println("distanceSpecific");
        Map<String, String> r1S1 = new HashMap<>();
        r1S1.put("1", "3 kg"); 
        r1S1.put("2", "4 kg"); 
        r1S1.put("3", "5 kg"); 
        r1S1.put("4", "6 kg"); 
        r1S1.put("5", "7 kg"); 
        r1S1.put("6", "7 kg"); 

        Map<String, String> r1S2 = new HashMap<>();
        r1S2.put("1", "8 kg");   // diferente
        r1S2.put("2", "4000 g");  // igual
        r1S2.put("3", "9 kg");    // diferente
        r1S2.put("4", "6 kg");  // igual
        r1S2.put("5", "5 kg");  // diferente
        r1S2.put("6", "ERROR");  // diferente


        WeightSimilarity instance = new WeightSimilarity();
        double expResult = 2.0 / 6;
        double result = instance.similarity(r1S1, r1S2);
        assertEquals(expResult, result, 0.0);

    }
}
