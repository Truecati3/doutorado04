/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.contentsimilarity;

import java.util.HashSet;
import java.util.Set;
import static junit.framework.Assert.assertEquals;
import junit.framework.TestCase;

/**
 *
 * @author edimar
 */
public class NumberSimilarityTest extends TestCase {
    Set<String> valuesR2 = new HashSet<>();
    
    
    public NumberSimilarityTest(String testName) {
        super(testName);
        
        valuesR2.add("150");
        valuesR2.add("155");
        valuesR2.add("152");
        valuesR2.add("215");
        valuesR2.add("225");
    }

    /**
     * Test of similaritySpecific method, of class NumberSimilarity.
     */
    public void testSimilaritySpecific() {
        System.out.println("similaritySpecific -- valor igual a média");
        String valueR1 = "179.4";
        
        NumberSimilarity instance = new NumberSimilarity();
        instance.train(valuesR2);
        double expResult = 1.0;
        double result = instance.similaritySpecific(valueR1, valuesR2);
        assertEquals(expResult, result, 0.0);

    }

    /**
     * Test of similaritySpecific method, of class NumberSimilarity.
     */
    public void testSimilaritySpecific02() {
        System.out.println("similaritySpecific -- valor longe da média para baixo");
        String valueR1 = "35";
        
        NumberSimilarity instance = new NumberSimilarity();
        instance.train(valuesR2);
        double expResult = 0.0005507287;
        double result = instance.similaritySpecific(valueR1, valuesR2);
        assertEquals(expResult, result, 0.000001);

    }
    
    /**
     * Test of similaritySpecific method, of class NumberSimilarity.
     */
    public void testSimilaritySpecific03() {
        System.out.println("similaritySpecific -- valor longe da média para cima");
        String valueR1 = "300";
        
        NumberSimilarity instance = new NumberSimilarity();
        instance.train(valuesR2);
        double expResult = 0.0053299138;
        double result = instance.similaritySpecific(valueR1, valuesR2);
        assertEquals(expResult, result, 0.000001);

    }
    
    /**
     * Test of similaritySpecific method, of class NumberSimilarity.
     */
    public void testSimilaritySpecific04() {
        System.out.println("similaritySpecific -- valor perto da média para baixo");
        String valueR1 = "100";
        
        NumberSimilarity instance = new NumberSimilarity();
        instance.train(valuesR2);
        double expResult = 0.1034260922;
        double result = instance.similaritySpecific(valueR1, valuesR2);
        assertEquals(expResult, result, 0.000001);

    }
    
    /**
     * Test of similaritySpecific method, of class NumberSimilarity.
     */
    public void testSimilaritySpecific05() {
        System.out.println("similaritySpecific -- valor perto da média para cima");
        String valueR1 = "230";
        
        NumberSimilarity instance = new NumberSimilarity();
        instance.train(valuesR2);
        double expResult = 0.3979388818;
        double result = instance.similaritySpecific(valueR1, valuesR2);
        assertEquals(expResult, result, 0.000001);

    }
    
    /**
     * Test of similarity method, of class NumberSimilarity.
     */
    public void testSimilarity() {
        System.out.println("similarity");
        Set<String> valuesR1 = new HashSet<>();
        valuesR1.add("179.4");
        valuesR1.add("300");
        
        NumberSimilarity instance = new NumberSimilarity();
        double expResult = (1+0.0053299138)/2;
        double result = instance.similarity(valuesR1, valuesR2);
        assertEquals(expResult, result, 0.000001);
      
    }

    /**
     * Test of normalize method, of class NumberSimilarity.
     */
    public void testNormalize() throws Exception {
        System.out.println("normalize");
        String numericValue = "150 cm";
        NumberSimilarity instance = new NumberSimilarity();
        Double expResult = new Double(150);
        Double result = instance.normalize(numericValue);
        assertEquals(expResult, result);
        
    }
}
