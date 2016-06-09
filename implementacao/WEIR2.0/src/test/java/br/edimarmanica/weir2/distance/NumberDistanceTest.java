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
public class NumberDistanceTest extends TestCase {

    public NumberDistanceTest() {
        InterSite.MIN_SHARED_ENTITIES = 1;
    }

    /**
     * Test of distance method, of class NumberDistance.
     */
    public void testDistance() throws InsufficientOverlapException {
        System.out.println("distance");
        Map<String, String> r1S1 = new HashMap<>();
        r1S1.put("e1", "11.157,15"); //1 pq tem valor nulo em r1S2
        r1S1.put("e2", "10.000,00");    //0 pq são iguais (na verdade eles são diferentes mas estão dentro do p que nesse caso é 227.91)
        r1S1.put("e3", "12.563,45");    //nada pq não tem em r1S2
        r1S1.put("e4", "14.523,55"); // 0 pq são iguais
        r1S1.put("e5", "10.205,00");         //0 pq são iguais  

        Map<String, String> r1S2 = new HashMap<>();
        r1S2.put("e1", null); // --  
        r1S2.put("e6", "9.002,00");      //nada pq não tem em r1S1
        r1S2.put("e2", "10.227,01");  // ---
        r1S2.put("e4", "14.523,55");  // ---
        r1S2.put("e5", "10.205,00");        // ---
        r1S2.put("e7", "13.021,00");      // nada pq não tem em r1S1

        NumberDistance instance = new NumberDistance();
        double expResult = 1.0 / 4; //4 é o número de instâncias compartilhadas
        double result = instance.distance(r1S1, r1S2);
        assertEquals(expResult, result, 0.0);

    }
}
