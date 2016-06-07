/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.weir.algorithms.distance;

import br.edimarmanica.configuration.InterSite;
import br.edimarmanica.dataset.weir.book.Site;
import junit.framework.TestCase;
import br.edimarmanica.weir.bean.Rule;
import br.edimarmanica.weir.bean.Value;

/**
 *
 * @author edimar
 */
public class PhoneDistanceTest extends TestCase {

    Site s1 = Site.AMAZON;
    Site s2 = Site.BARNESANDNOBLE;
    
    public PhoneDistanceTest() {
        InterSite.MIN_SHARED_ENTITIES = 1;
    }

    /**
     * Test of distanceSpecific method, of class PhoneDistance.
     */
    public void testDistanceSpecific() throws InsufficientOverlapException {
        System.out.println("distanceSpecific");
        Rule r1S1 = new Rule(1, s1);
        r1S1.addValue(new Value("3381-1988", "1", "e1")); // 1 pq tem valor nulo em r1S2
        r1S1.addValue(new Value("3381-1964", "2", "e2")); // 0 pq são iguais
        r1S1.addValue(new Value("3381-1985", "3", "e3")); // nada pq não tem em r1S2
        r1S1.addValue(new Value("3381-1952", "4", "e4")); // 0 pq são iguais
        r1S1.addValue(new Value("3381-1937", "5", "e5")); // 0 pq são iguais  

        Rule r1S2 = new Rule(1, s2);
        r1S2.addValue(new Value(null, "1", "e1")); // -- valor nulo
        r1S2.addValue(new Value("(54) 3381-2001", "1", "e6"));      //nada pq não tem em r1S1
        r1S2.addValue(new Value("0xx54 3381 1964", "2", "e2"));  // ---
        r1S2.addValue(new Value("+55543381-1952", "3", "e4"));  // ---
        r1S2.addValue(new Value("3381 1937", "4", "e5"));        // ---
        r1S2.addValue(new Value("5433812001", "5", "e7"));      // nada pq não tem em r1S1

        PhoneDistance instance = new PhoneDistance();
        double expResult = 1.0 / 4;
        double result = instance.distance(r1S1, r1S2);
        assertEquals(expResult, result, 0.0);

    }
}