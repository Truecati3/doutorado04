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
public class NumberDistanceTest extends TestCase {

    Site s1 = Site.AMAZON;
    Site s2 = Site.BARNESANDNOBLE;
    
    public NumberDistanceTest() {
        InterSite.MIN_SHARED_ENTITIES = 1;
    }


    /**
     * Test of distance method, of class NumberDistance.
     */
    public void testDistance() throws InsufficientOverlapException {
        System.out.println("distance");
        Rule r1S1 = new Rule(1, s1);
        r1S1.addValue(new Value("11.157,15", "1", "e1")); //1 pq tem valor nulo em r1S2
        r1S1.addValue(new Value("10.000,00", "2", "e2"));    //0 pq são iguais (na verdade eles são diferentes mas estão dentro do p que nesse caso é 227.91)
        r1S1.addValue(new Value("12.563,45", "3", "e3"));    //nada pq não tem em r1S2
        r1S1.addValue(new Value("14.523,55", "4", "e4")); // 0 pq são iguais
        r1S1.addValue(new Value("10.205,00", "5", "e5"));         //0 pq são iguais  

        Rule r1S2 = new Rule(1, s2);
        r1S2.addValue(new Value(null, "1", "e1")); // --  
        r1S2.addValue(new Value("9.002,00", "1", "e6"));      //nada pq não tem em r1S1
        r1S2.addValue(new Value("10.227,01", "2", "e2"));  // ---
        r1S2.addValue(new Value("14.523,55", "3", "e4"));  // ---
        r1S2.addValue(new Value("10.205,00", "4", "e5"));        // ---
        r1S2.addValue(new Value("13.021,00", "5", "e7"));      // nada pq não tem em r1S1

        NumberDistance instance = new NumberDistance();
        double expResult = 1.0/4; //4 é o número de instâncias compartilhadas
        double result = instance.distance(r1S1, r1S2);
        assertEquals(expResult, result, 0.0);
        
    }
}