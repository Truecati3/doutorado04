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
import static junit.framework.Assert.assertEquals;

/**
 *
 * @author edimar
 */
public class DateDistanceTest extends TestCase {

    Site s1 = Site.AMAZON;
    Site s2 = Site.BARNESANDNOBLE;

    public DateDistanceTest() {
        InterSite.MIN_SHARED_ENTITIES = 1;
    }

    /**
     * Test of distanceSpecific method, of class DateDistance.
     */
    public void testDistanceSpecific() throws InsufficientOverlapException {
        System.out.println("distanceSpecific");
        Rule r1S1 = new Rule(1, s1);
        r1S1.addValue(new Value("11/01/1988", "1", "e1")); //1 pq tem valor nulo em r1S2
        r1S1.addValue(new Value("11/04/1964", "2", "e2"));    //0 pq são iguais
        r1S1.addValue(new Value("20/02/1985", "3", "e3"));    //nada pq não tem em r1S2
        r1S1.addValue(new Value("29/05/1952", "4", "e4")); // 0 pq são iguais
        r1S1.addValue(new Value("07/07/1937", "5", "e5"));         //0 pq são iguais  

        Rule r1S2 = new Rule(1, s2);
        r1S2.addValue(new Value(null, "1", "e1"));
        r1S2.addValue(new Value("11/07/2001", "1", "e6"));      //nada pq não tem em r1S1
        r1S2.addValue(new Value("1964-04-11", "2", "e2"));  // ---
        r1S2.addValue(new Value("29/05/1952", "3", "e4"));  // ---
        r1S2.addValue(new Value("1937-07-07", "4", "e5"));        // ---
        r1S2.addValue(new Value("11/07/2001", "5", "e7"));      // nada pq não tem em r1S1

        DateDistance instance = new DateDistance();
        double expResult = 1.0 / 4; //4 instâncias compartilhadas
        double result = instance.distance(r1S1, r1S2);
        assertEquals(expResult, result, 0.0);

    }

    public void testDifferentFormats() throws InsufficientOverlapException {

        Rule r1S1 = new Rule(1, s1);
        r1S1.addValue(new Value("03/01/1988", "1", "e1"));
        r1S1.addValue(new Value("1989-04-1", "2", "e2"));
        r1S1.addValue(new Value("05/1990", "3", "e3"));
        r1S1.addValue(new Value("1991-6", "4", "e4"));
        r1S1.addValue(new Value("1995-07", "5", "e5"));
        r1S1.addValue(new Value("1997", "6", "e6"));

        Rule r1S2 = new Rule(1, s2);
        r1S2.addValue(new Value("1988-01", "1", "e1"));
        r1S2.addValue(new Value("1989", "2", "e2"));
        r1S2.addValue(new Value("05 May 1990", "3", "e3"));
        r1S2.addValue(new Value("June 05, 1991", "4", "e4"));
        r1S2.addValue(new Value("July 1995", "5", "e5"));
        r1S2.addValue(new Value("03 February 1997", "6", "e6"));

        DateDistance instance = new DateDistance();
        double expResult = 0;
        double result = instance.distance(r1S1, r1S2);
        assertEquals(expResult, result, 0.0);

    }
}