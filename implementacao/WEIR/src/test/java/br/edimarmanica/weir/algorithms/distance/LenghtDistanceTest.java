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
public class LenghtDistanceTest extends TestCase{
    
    Site s1 = Site.AMAZON;
    Site s2 = Site.BARNESANDNOBLE;
    
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
        Rule r1S1 = new Rule(1, s1);
        r1S1.addValue(new Value("3 m", "1", "e1")); // 1 pq tem valor nulo em r1S2
        r1S1.addValue(new Value("4 m", "2", "e2")); // 0 pq são iguais
        r1S1.addValue(new Value("5 m", "3", "e3")); // nada pq não tem em r1S2
        r1S1.addValue(new Value("6 m", "4", "e4")); // 0 pq são iguais
        r1S1.addValue(new Value("7 m", "5", "e5")); // 0 pq são iguais  

        Rule r1S2 = new Rule(1, s2);
        r1S2.addValue(new Value(null, "1", "e1")); // -- valor nulo
        r1S2.addValue(new Value("8 m", "1", "e6"));      //nada pq não tem em r1S1
        r1S2.addValue(new Value("4 m", "2", "e2"));  // ---
        r1S2.addValue(new Value("6 m", "3", "e4"));  // ---
        r1S2.addValue(new Value("7 m", "4", "e5"));        // ---
        r1S2.addValue(new Value("9 m", "5", "e7"));      // nada pq não tem em r1S1

        LenghtDistance instance = new LenghtDistance();
        double expResult = 1.0 / 4;
        double result = instance.distance(r1S1, r1S2);
        assertEquals(expResult, result, 0.0);

    }
}