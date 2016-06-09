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
public class PhoneDistanceTest extends TestCase {

    public PhoneDistanceTest() {
        InterSite.MIN_SHARED_ENTITIES = 1;
    }

    /**
     * Test of distanceSpecific method, of class PhoneDistance.
     */
    public void testDistanceSpecific() throws InsufficientOverlapException {
        System.out.println("distanceSpecific");
        Map<String, String> r1S1 = new HashMap<>();
        r1S1.put("e1", "3381-1988"); // 1 pq tem valor nulo em r1S2
        r1S1.put("e2", "3381-1964"); // 0 pq são iguais
        r1S1.put("e3", "3381-1985"); // nada pq não tem em r1S2
        r1S1.put("e4", "3381-1952"); // 0 pq são iguais
        r1S1.put("e5", "3381-1937"); // 0 pq são iguais  

        Map<String, String> r1S2 = new HashMap<>();
        r1S2.put("e1", null); // -- valor nulo

        r1S2.put("e2", "0xx54 3381 1964");  // ---
        r1S2.put("e4", "+55543381-1952");  // ---
        r1S2.put("e5", "3381 1937");        // ---
        r1S2.put("e6", "(54) 3381-2001");      //nada pq não tem em r1S1
        r1S2.put("e7", "5433812001");      // nada pq não tem em r1S1

        PhoneDistance instance = new PhoneDistance();
        double expResult = 1.0 / 4;
        double result = instance.distance(r1S1, r1S2);
        assertEquals(expResult, result, 0.0);

    }
}
