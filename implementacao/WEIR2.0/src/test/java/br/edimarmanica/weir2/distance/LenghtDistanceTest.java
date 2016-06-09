/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.weir2.distance;

import br.edimarmanica.configuration.InterSite;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;

/**
 *
 * @author edimar
 */
public class LenghtDistanceTest extends TestCase{
    
    
    public LenghtDistanceTest() {
        InterSite.MIN_SHARED_ENTITIES = 1;
    }

    /**
     * Test of normalize method, of class LenghtDistance.
     */
    public void testNormalize() throws NoiseException {
        System.out.println("normalize");
        String numericValue = "15m";
        LenghtDistance instance = new LenghtDistance();
        Double expResult = new Double("15");
        Double result = instance.normalize(numericValue);
        assertEquals(expResult, result);
        
        numericValue = "200 cm";
        instance = new LenghtDistance();
        expResult = new Double(2);
        result = instance.normalize(numericValue);
        assertEquals(expResult, result);
        
        numericValue = "2 km";
        instance = new LenghtDistance();
        expResult = new Double(2000);
        result = instance.normalize(numericValue);
        assertEquals(expResult, result);
        
    }
    
    public void testDistanceSpecific() throws InsufficientOverlapException {
        System.out.println("distanceSpecific");
         Map<String, String> r1S1 = new HashMap<>();
        r1S1.put("e1", "3 m"); // 1 pq tem valor nulo em r1S2
        r1S1.put("e2", "4 m"); // 0 pq são iguais
        r1S1.put("e3", "5 m"); // nada pq não tem em r1S2
        r1S1.put("e4", "6 m"); // 0 pq são iguais
        r1S1.put("e5", "7 m"); // 0 pq são iguais  

         Map<String, String> r1S2 = new HashMap<>();
        r1S2.put("e1", null); // -- valor nulo
        r1S2.put("e2", "4 m");  // ---
        r1S2.put("e4", "6 m");  // ---
        r1S2.put("e5", "7 m");        // ---
        r1S2.put("e6", "8 m");      //nada pq não tem em r1S1
        r1S2.put("e7", "9 m");      // nada pq não tem em r1S1

        LenghtDistance instance = new LenghtDistance();
        double expResult = 1.0 / 4;
        double result = instance.distance(r1S1, r1S2);
        assertEquals(expResult, result, 0.0);

    }
}