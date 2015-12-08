/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.weir.algorithms.distance;

import br.edimarmanica.dataset.weir.book.Site;
import junit.framework.TestCase;
import br.edimarmanica.weir.bean.Rule;
import br.edimarmanica.weir.bean.Value;
import static junit.framework.Assert.assertEquals;

/**
 *
 * @author edimar
 */
public class DataTypeControllerTest extends TestCase {

    Rule r1, r2, r3, r4, r5, r6;
    Site s1 = Site.AMAZON;

    public DataTypeControllerTest() {
        r1 = new Rule(1, s1);
        r1.addValue(new Value("X", "1", "e1"));
        r1.addValue(new Value("Y", "2", "e2"));

        r2 = new Rule(2, s1);
        r2.addValue(new Value("16.13", "1", "e1"));
        r2.addValue(new Value("15.06", "2", "e2"));

        r3 = new Rule(3, s1);
        r3.addValue(new Value("Dan", "1", "e1"));

        r4 = new Rule(4, s1);
        r4.addValue(new Value("Dan", "1", "e1"));
        r4.addValue(new Value("46 m", "2", "e2"));
        r4.addValue(new Value("Pan", "3", "e3"));

        r5 = new Rule(5, s1);
        r5.addValue(new Value("38 m", "1", "e1"));
        r5.addValue(new Value("46 m", "2", "e2"));

        r6 = new Rule(6, s1);
        r6.addValue(new Value("38 m", "1", "e1"));
    }

    /**
     * Test of getDataType method, of class DataTypeController.
     */
    public void testGetDataType() {
        System.out.println("getDataType");
        String value = "R$ 23,35";
        DataType expResult = DataType.CURRENCY;
        DataType result = DataTypeController.getDataType(value);
        assertEquals(expResult, result);
        
        value = "€2,89";
        expResult = DataType.CURRENCY;
        result = DataTypeController.getDataType(value);
        assertEquals(expResult, result);
    }

    /**
     * Test of getMostSpecificType method, of class DataTypeController.
     */
    public void testGetMostSpecificType_Rule() {
        System.out.println("getMostSpecificType");

        DataType expResult = DataType.STRING;
        DataType result = DataTypeController.getMostFrequentType(r4);
        assertEquals("ex01: ", expResult, result);

        expResult = DataType.LENGHT;
        result = DataTypeController.getMostFrequentType(r5);
        assertEquals("ex02: ", expResult, result);

    }

    /**
     * Test of getMostSpecificType method, of class DataTypeController.
     */
    public void testGetMostSpecificType_Rule_Rule() {
        System.out.println("getMostSpecificType");

        DataType expResult = DataType.NUMBER;
        DataType result = DataTypeController.getMostSpecificType(r2, r5);
        assertEquals(expResult, result);
    }
}