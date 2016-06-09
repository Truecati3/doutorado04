/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.weir2.distance;

import br.edimarmanica.dataset.weir.book.Site;
import br.edimarmanica.weir2.rule.type.DataType;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;

/**
 *
 * @author edimar
 */
public class TypeAwareDistanceTest extends TestCase {

    Map<String, String> r1, r2, r3, r4, r5, r6;

    Site s1 = Site.AMAZON;

    public TypeAwareDistanceTest() {
        r1 = new HashMap<>();
        r1.put("e1", "X");
        r1.put("e2", "Y");

        r2 = new HashMap<>();
        r2.put("e1", "16.13");
        r2.put("e2", "15.06");

        r3 = new HashMap<>();
        r3.put("e1", "Dan");

        r4 = new HashMap<>();
        r4.put("e1", "Dan");
        r4.put("e2", "46 m");

        r5 = new HashMap<>();
        r5.put("e1", "38 m");
        r5.put("e2", "46 m");

        r6 = new HashMap<>();
        r6.put("e1", "38 m");
    }

    /**
     * Test of typeDistance method, of class TypeAwareDistance.
     */
    public void testTypeDistance() {
        System.out.println("typeDistance");

        double expResult = 1.0;
        double result = TypeAwareDistance.typeDistance(r2, DataType.NUMBER, r5, DataType.LENGHT);
        assertEquals(expResult, result, 0.0);

    }
}
