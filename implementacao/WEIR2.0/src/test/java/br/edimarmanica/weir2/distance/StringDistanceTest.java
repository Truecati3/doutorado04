/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.weir2.distance;

import br.edimarmanica.configuration.InterSite;
import junit.framework.TestCase;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author edimar
 */
public class StringDistanceTest extends TestCase {

    public StringDistanceTest() {
        InterSite.MIN_SHARED_ENTITIES = 1;
    }

    /**
     * Test of distance method, of class StringDistance.
     */
    public void testDistance() throws InsufficientOverlapException {
        System.out.println("distance");

        Map<String, String> r1S1 = new HashMap<>();
        r1S1.put("e1", "Alexandre Isquierdo"); //1 pq tem valor nulo em r1S2
        r1S1.put("e2", "Cristiane Brasil");    //0 pq são iguais
        r1S1.put("e3", "Carlos Bolsonaro");    //nada pq não tem em r1S2
        r1S1.put("e4", "General Magalhaes"); // valor quebrado pq são similares
        r1S1.put("e5", "Cesar Maia");         //0 pq são iguais  

        Map<String, String> r1S2 = new HashMap<>();
        r1S2.put("e1", null);                //--- testando valor nulo
        r1S2.put("e2", "Cristiane Brasil");  // ---
        r1S2.put("e4", "Magalhaes Silva");  // ---
        r1S2.put("e5", "Cesar Maia");        // ---
        r1S2.put("e6", "Jorge Manaia");      // nada pq não tem em r1S1
        r1S2.put("e7", "Dr. Jairinho");      // nada pq não tem em r1S1

        StringDistance instance = new StringDistance();
        double expResult = 1.50 / 4; //4 is the number of shared entities
        double result = instance.distance(r1S1, r1S2);
        System.out.println("Result: " + result);
        assertEquals(expResult, result, 0.001);

    }
}
