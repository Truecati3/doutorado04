/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.contentsimilarity;

import java.util.HashSet;
import java.util.Set;
import junit.framework.TestCase;

/**
 *
 * @author edimar
 */
public class StringSimilarityTest extends TestCase {

    Set<String> r2 = new HashSet<>();

    public StringSimilarityTest(String testName) {
        super(testName);

        r2.add("Partido dos Trabalhadores");
        r2.add("Partido Progressista");
        r2.add("Partido da Social Democracia");
    }

    /**
     * Test of similarity method, of class StringSimilarity.
     */
    public void testSimilarity() {
        System.out.println("similarity");
        Set<String> r1 = new HashSet<>();
        r1.add("Partido dos Trabalhadores");
        r1.add("Partido Verde");

        StringSimilarity instance = new StringSimilarity();
        double expResult = 0.75;
        double result = instance.similarity(r1, r2);
        assertEquals(expResult, result, 0.0);
    }

  
}
