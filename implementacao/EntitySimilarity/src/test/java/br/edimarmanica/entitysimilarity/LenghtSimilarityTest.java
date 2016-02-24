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
public class LenghtSimilarityTest extends TestCase {
    
    public LenghtSimilarityTest(String testName) {
        super(testName);
        br.edimarmanica.configuration.TypeAwareSimilarity.MIN_SHARED_ENTITIES=3;
    }

     /**
     * Test of normalize method, of class LenghtDistance.
     */
    public void testNormalize() {
        System.out.println("normalize");
        String numericValue = "15m";
        LenghtSimilarity instance = new LenghtSimilarity();
        Double expResult = new Double("15");
        Double result = instance.normalize(numericValue);
        assertEquals(expResult, result);
        
        numericValue = "200 cm";
        instance = new LenghtSimilarity();
        expResult = new Double(2);
        result = instance.normalize(numericValue);
        assertEquals(expResult, result);
        
        numericValue = "2 km";
        instance = new LenghtSimilarity();
        expResult = new Double(2000);
        result = instance.normalize(numericValue);
        assertEquals(expResult, result);
        
    }
    
    public void testDistanceSpecific() throws InsufficientOverlap {
        System.out.println("distanceSpecific");
        Map<String,String> r1 = new HashMap<>();
        r1.put("1", "3 m"); // 1 pq tem valor nulo em r1S2
        r1.put("2", "4 m"); // 0 pq são iguais
        r1.put("3", "5 m"); // nada pq não tem em r1S2
        r1.put("4", "6 m"); // 0 pq são iguais
        r1.put("5", "7 m"); // 0 pq são iguais  

        Map<String,String> r2 = new HashMap<>();
        r2.put("2", "4 m");  // ---
        r2.put("4", "6 m");  // ---
        r2.put("5", "7 m");        // ---
        r2.put("6", "8 m");      //nada pq não tem em r1S1
        r2.put("7", "9 m");      // nada pq não tem em r1S1

        LenghtSimilarity instance = new LenghtSimilarity();
        double expResult = 1.0;
        double result = instance.similarity(r1, r2);
        assertEquals(expResult, result, 0.0);

    }
}
