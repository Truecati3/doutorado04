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
public class DateSimilarityTest extends TestCase {

    public DateSimilarityTest(String testName) {
        super(testName);

        br.edimarmanica.configuration.TypeAwareSimilarity.MIN_SHARED_ENTITIES = 3;
    }

    /**
     * Test of distanceSpecific method, of class DateDistance.
     */
    public void testDistanceSpecific() throws InsufficientOverlapException {

        System.out.println("distanceSpecific");
        Map<String, String> r1 = new HashMap<>();
        r1.put("1", "11/01/1988");
        r1.put("2", "11/04/1964");
        r1.put("3", "20/02/1985");
        r1.put("4", "29/05/1952");
        r1.put("5", "08/07/1937");
        r1.put("8", "08/07/1937");

        Map<String, String> r2 = new HashMap<>();
        r2.put("2", "1964-04-11"); // igual
        r2.put("4", "29/05/1952"); //igual
        r2.put("5", "1937-07-07"); //diff
        r2.put("6", "11/07/2001"); // -- ignorado pq não tem em r1
        r2.put("7", "11/07/2001"); // -- ignorado pq não tem em r1
        r2.put("8", "ERROR");       // diff

        DateSimilarity instance = new DateSimilarity();
        double expResult = 2.0 / 4; //2 corretas de 3
        double result = instance.similarity(r1, r2);
        assertEquals(expResult, result, 0.0);

    }

    public void testDifferentFormats() throws InsufficientOverlapException {

        Map<String, String> r1 = new HashMap<>();
        r1.put("1", "03/01/1988");
        r1.put("2", "1989-04-1");
        r1.put("3", "05/1990");
        r1.put("4", "1991-6");
        r1.put("5", "1995-07");
        r1.put("6", "1997");

        Map<String, String> r2 = new HashMap<>();
        r2.put("1", "1988-01");
        r2.put("2", "1989");
        r2.put("3", "05 May 1990");
        r2.put("4", "June 05, 1991");
        r2.put("5", "July 1995");
        r2.put("6", "03 February 1997");

        DateSimilarity instance = new DateSimilarity();
        double expResult = 1;
        double result = instance.similarity(r1, r2);
        assertEquals(expResult, result, 0.0);

    }
}
