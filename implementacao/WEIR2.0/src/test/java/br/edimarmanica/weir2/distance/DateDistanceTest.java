/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.weir2.distance;

import br.edimarmanica.configuration.InterSite;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertEquals;

/**
 *
 * @author edimar
 */
public class DateDistanceTest extends TestCase {

    
    public DateDistanceTest() {
        InterSite.MIN_SHARED_ENTITIES = 1;
    }

    /**
     * Test of distanceSpecific method, of class DateDistance.
     */
    public void testDistanceSpecific() throws InsufficientOverlapException {
        System.out.println("distanceSpecific");
        Map<String, String> r1S1 = new HashMap<>();
        r1S1.put("e1", "11/01/1988"); //1 pq tem valor nulo em r1S2
        r1S1.put("e2", "11/04/1964");    //0 pq são iguais
        r1S1.put("e3", "20/02/1985");    //nada pq não tem em r1S2
        r1S1.put("e4", "29/05/1952"); // 0 pq são iguais
        r1S1.put("e5", "07/07/1937");         //0 pq são iguais  

        Map<String, String> r1S2 = new HashMap<>();
        r1S2.put("e1", null);
        r1S2.put("e2", "1964-04-11");  // ---
        r1S2.put("e4", "29/05/1952");  // ---
        r1S2.put("e5", "1937-07-07");        // ---
        r1S2.put("e6", "11/07/2001");      //nada pq não tem em r1S1
        r1S2.put("e7", "11/07/2001");      // nada pq não tem em r1S1

        DateDistance instance = new DateDistance();
        double expResult = 1.0 / 4; //4 instâncias compartilhadas
        double result = instance.distance(r1S1, r1S2);
        assertEquals(expResult, result, 0.0);

    }

    public void testDifferentFormats() throws InsufficientOverlapException {

        Map<String, String> r1S1 = new HashMap<>();
        r1S1.put("e1", "03/01/1988");
        r1S1.put("e2", "1989-04-1");
        r1S1.put("e3", "05/1990");
        r1S1.put("e4", "1991-6");
        r1S1.put("e5", "1995-07");
        r1S1.put("e6", "1997");

        Map<String, String> r1S2 = new HashMap<>();
        r1S2.put("e1", "1988-01");
        r1S2.put("e2", "1989");
        r1S2.put("e3", "05 May 1990");
        r1S2.put("e4", "June 05, 1991");
        r1S2.put("e6", "July 1995");
        r1S2.put("e6", "03 February 1997");

        DateDistance instance = new DateDistance();
        double expResult = 0;
        double result = instance.distance(r1S1, r1S2);
        assertEquals(expResult, result, 0.0);

    }
}