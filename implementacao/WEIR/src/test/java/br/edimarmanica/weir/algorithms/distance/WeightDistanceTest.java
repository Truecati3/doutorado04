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
public class WeightDistanceTest extends TestCase{
    
    Site s1 = Site.AMAZON;
    Site s2 = Site.BARNESANDNOBLE;
    
    public WeightDistanceTest() {
        InterSite.MIN_SHARED_ENTITIES = 1;
    }
    
    

    /**
     * Test of normalize method, of class WeightDistance.
     */
    public void testNormalize() throws NoiseException {
        System.out.println("normalize");
        String numericValue = "15 g";
        WeightDistance instance = new WeightDistance();
        Double expResult = new Double("15");
        Double result = instance.normalize(numericValue);
        assertEquals(expResult, result);
        
        
        numericValue = "2 kg";
        expResult = new Double(2000);
        result = instance.normalize(numericValue);
        assertEquals(expResult, result);
    }
    
    public void testDistanceSpecific() throws InsufficientOverlapException {
        System.out.println("distanceSpecific");
        Rule r1S1 = new Rule(1, s1);
        r1S1.addValue(new Value("3 kg", "1", "e1")); // 1 pq tem valor nulo em r1S2
        r1S1.addValue(new Value("4 kg", "2", "e2")); // 0 pq são iguais
        r1S1.addValue(new Value("5 kg", "3", "e3")); // nada pq não tem em r1S2
        r1S1.addValue(new Value("6 kg", "4", "e4")); // 0 pq são iguais
        r1S1.addValue(new Value("7 kg", "5", "e5")); // 0 pq são iguais  

        Rule r1S2 = new Rule(1, s2);
        r1S2.addValue(new Value(null, "1", "e1")); // -- valor nulo
        r1S2.addValue(new Value("8 kg", "1", "e6"));      //nada pq não tem em r1S1
        r1S2.addValue(new Value("4000 g", "2", "e2"));  // ---
        r1S2.addValue(new Value("6 kg", "3", "e4"));  // ---
        r1S2.addValue(new Value("7 kg", "4", "e5"));        // ---
        r1S2.addValue(new Value("9 kg", "5", "e7"));      // nada pq não tem em r1S1

        WeightDistance instance = new WeightDistance();
        double expResult = 1.0 / 4;
        double result = instance.distance(r1S1, r1S2);
        assertEquals(expResult, result, 0.0);

    }
}