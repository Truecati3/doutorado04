/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.entitysimilarity;

import com.google.common.collect.HashBiMap;
import java.util.HashMap;
import java.util.Map;
import static junit.framework.Assert.assertEquals;
import junit.framework.TestCase;

/**
 *
 * @author edimar
 */
public class StringSimilarityTest extends TestCase {
    
    public StringSimilarityTest(String testName) {
        super(testName);
        
        br.edimarmanica.configuration.TypeAwareSimilarity.MIN_SHARED_ENTITIES=3; 
    }

     public void testDistance() throws InsufficientOverlapException {
        System.out.println("distance");

        Map<String, String> r1S1 = new HashMap<>();
        r1S1.put("1","Alexandre Isquierdo"); 
        r1S1.put("2","Cristiane Brasil");    
        r1S1.put("3","Carlos Bolsonaro");    
        r1S1.put("4","General Magalhaes"); 
        r1S1.put("5","Cesar Maia");        

        Map<String, String> r1S2 = new HashMap<>();
        r1S2.put("1","Jorge Manaia");      // 0 - diferente
        r1S2.put("2","Cristiane Brasil");  // 1 - igual
        r1S2.put("4","Magalhaes Silva");  // 0,5 - similar
        r1S2.put("5","Cesar Maia");        // 1  - diferente
        r1S2.put("6","Dr. Jairinho");      // -- não conta pq não é compartilhado

        StringSimilarity instance = new StringSimilarity();
        double expResult = 2.50 / 4; //4 is the number of shared entities
        double result = instance.similarity(r1S1, r1S2);
        System.out.println("Result: " + result);
        assertEquals(expResult, result, 0.001);

    }
}
