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

        br.edimarmanica.configuration.TypeAwareSimilarity.MIN_SHARED_ENTITIES = 3;
    }

    /**
     * Test of distance method, of class NumberDistance.
     */
    public void testDistance() throws InsufficientOverlapException {
        System.out.println("distance");
        Map<String, String> r1 = new HashMap<>();
        r1.put("1", "11.157,15");
        r1.put("2", "10.000,00");
        r1.put("3", "12.563,45");
        r1.put("4", "14.523,55");
        r1.put("5", "10.205,00");
        r1.put("8", "10.205,00");

        Map<String, String> r2 = new HashMap<>();
        r2.put("2", "10.227,01"); // igual (dentro do intervalo)
        r2.put("4", "14.523,55");  // igual (dentro do intervalo)
        r2.put("5", "15.205,00");// diff
        r2.put("6", "9.002,00");  // -- ignorado pq não tem no r1
        r2.put("7", "13.021,00"); // -- ignorado pq não tem no r1
        r2.put("8", "ERROR");    // diff

        NumberSimilarity instance = new NumberSimilarity();
        double expResult = 2.0 / 4; //2 valores com diferença aceitável de 4 entidades sobrepostas
        double result = instance.similarity(r1, r2);
        assertEquals(expResult, result, 0.0);

    }
}
