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
public class PhoneSimilarityTest extends TestCase {

    public PhoneSimilarityTest(String testName) {
        super(testName);

        br.edimarmanica.configuration.TypeAwareSimilarity.MIN_SHARED_ENTITIES = 3;
    }

    /**
     * Test of distanceSpecific method, of class PhoneDistance.
     */
    public void testDistanceSpecific() throws InsufficientOverlapException {
        System.out.println("distanceSpecific");
        Map<String, String> r1 = new HashMap<>();
        r1.put("1", "3381-1988"); 
        r1.put("2", "3381-1964"); 
        r1.put("3", "3381-1985"); 
        r1.put("4", "3381-1952"); 
        r1.put("5", "3381-1937"); 
        r1.put("6", "3381-1938"); 

        Map<String, String> r2 = new HashMap<>();
        r2.put("1", "3381-4001"); // diff
        r2.put("2", "3381 1964"); // igual
        r2.put("3", "3381-1985"); // igual
        r2.put("4", "33812001");  // diff
        r2.put("5", "3381 1936");  // diff
        r2.put("6", "ERROR");      // diff

        PhoneSimilarity instance = new PhoneSimilarity();
        double expResult = 2.0 / 6;
        double result = instance.similarity(r1, r2);
        assertEquals(expResult, result, 0.0);

    }
}
