/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.entitysimilarity;

import java.util.HashMap;
import java.util.Map;
import static junit.framework.Assert.assertEquals;
import junit.framework.TestCase;

/**
 *
 * @author edimar
 */
public class NumberSimilarityTest extends TestCase {
    
    public NumberSimilarityTest(String testName) {
        super(testName);
        
        br.edimarmanica.configuration.TypeAwareSimilarity.MIN_SHARED_ENTITIES=3;
    }

    /**
     * Test of distance method, of class NumberDistance.
     */
    public void testDistance() throws InsufficientOverlap {
        System.out.println("distance");
        Map<String,String> r1 = new HashMap<>();
        r1.put("1","11.157,15"); //1 pq tem valor nulo em r1S2
        r1.put("2","10.000,00");    //0 pq são iguais (na verdade eles são diferentes mas estão dentro do p que nesse caso é 227.91)
        r1.put("3","12.563,45");    //nada pq não tem em r1S2
        r1.put("4","14.523,55"); // 0 pq são iguais
        r1.put("5","10.205,00");         //0 pq são iguais  

        Map<String,String> r2 = new HashMap<>();
        r2.put("2","10.227,01");  // ---
        r2.put("4","14.523,55");  // ---
        r2.put("5","15.205,00");        // ---
        r2.put("6","9.002,00");      //nada pq não tem em r1S1
        r2.put("7","13.021,00");      // nada pq não tem em r1S1

        NumberSimilarity instance = new NumberSimilarity();
        double expResult = 2.0/3; //2 valores com diferença aceitável de 3 entidades sobrepostas
        double result = instance.similarity(r1, r2);
        assertEquals(expResult, result, 0.0);
        
    }
}
