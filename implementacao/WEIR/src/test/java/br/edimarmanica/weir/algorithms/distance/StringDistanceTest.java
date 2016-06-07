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
public class StringDistanceTest extends TestCase{

    Site s1 = Site.AMAZON;
    Site s2 = Site.BARNESANDNOBLE;
    
    public StringDistanceTest() {
        InterSite.MIN_SHARED_ENTITIES = 1;
    }

    
    /**
     * Test of distance method, of class StringDistance.
     */
   
    public void testDistance() throws InsufficientOverlapException {
        System.out.println("distance");

        Rule r1S1 = new Rule(1, s1);
        r1S1.addValue(new Value("Alexandre Isquierdo", "1", "e1")); //1 pq tem valor nulo em r1S2
        r1S1.addValue(new Value("Cristiane Brasil", "2", "e2"));    //0 pq são iguais
        r1S1.addValue(new Value("Carlos Bolsonaro", "3", "e3"));    //nada pq não tem em r1S2
        r1S1.addValue(new Value("General Magalhaes", "4", "e4")); // valor quebrado pq são similares
        r1S1.addValue(new Value("Cesar Maia", "5", "e5"));         //0 pq são iguais  

        Rule r1S2 = new Rule(1, s2);
        r1S2.addValue(new Value(null, "0", "e1"));                //--- testando valor nulo
        r1S2.addValue(new Value("Jorge Manaia", "1", "e6"));      // nada pq não tem em r1S1
        r1S2.addValue(new Value("Cristiane Brasil", "2", "e2"));  // ---
        r1S2.addValue(new Value("Magalhaes Silva", "3", "e4"));  // ---
        r1S2.addValue(new Value("Cesar Maia", "4", "e5"));        // ---
        r1S2.addValue(new Value("Dr. Jairinho", "5", "e7"));      // nada pq não tem em r1S1


        StringDistance instance = new StringDistance();
        double expResult = 1.50 / 4; //4 is the number of shared entities
        double result = instance.distance(r1S1, r1S2);
        System.out.println("Result: " + result);
        assertEquals(expResult, result, 0.001);

    }
}