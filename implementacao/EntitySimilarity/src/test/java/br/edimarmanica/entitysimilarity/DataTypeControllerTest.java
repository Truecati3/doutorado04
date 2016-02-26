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
public class DataTypeControllerTest extends TestCase {

    public DataTypeControllerTest(String testName) {
        super(testName);
    }

    /**
     * Test of getDataType method, of class DataTypeController.
     */
    public void testGetDataType() {
        System.out.println("getDataType");



        String[] values = {"31/07/1972", "2000-01", "1998-05-05", "23 Oct 1995", "October 23, 1995", "January 1996",
            "23 October 1995", "October 23, 1995", "January 2000", "1/1/1999"};

        DataType expResult = DataType.DATE;
        for (String value : values) {

            DataType result = DataTypeController.getDataType(value);
            assertEquals(value, expResult, result);
        }

        String value = "9780201398298";
        expResult = DataType.ISBN;
        DataType result = DataTypeController.getDataType(value);
        assertEquals(expResult, result);

        value = "3814-2406";
        expResult = DataType.PHONE;
        result = DataTypeController.getDataType(value);
        assertEquals(expResult, result);

        value = "R$ 30.000,00";
        expResult = DataType.CURRENCY;
        result = DataTypeController.getDataType(value);
        assertEquals(expResult, result);

        value = "1.66 m";
        expResult = DataType.LENGHT;
        result = DataTypeController.getDataType(value);
        assertEquals(expResult, result);

        value = "59,25 kg";
        expResult = DataType.WEIGHT;
        result = DataTypeController.getDataType(value);
        assertEquals(expResult, result);

        value = "Rio de Janeiro";
        expResult = DataType.STRING;
        result = DataTypeController.getDataType(value);
        assertEquals(expResult, result);
    }

    /**
     * Test of getMostSpecificType method, of class DataTypeController.
     */
    public void testGetMostSpecificType_Rule() {
        System.out.println("getMostSpecificType");
        Map<String, String> r = new HashMap<>();
        r.put("1", "3814-2406");
        r.put("2", "3814-2416");
        r.put("3", "3815-2416");
        r.put("4", "ERROR");

        DataType expResult = DataType.PHONE;
        DataType result = DataTypeController.getMostFrequentType(r);
        assertEquals(expResult, result);

    }

    /**
     * Test of getMostSpecificType method, of class DataTypeController.
     */
    public void testGetMostSpecificType_Rule_Rule() {
        System.out.println("getMostSpecificType");
        Map<String, String> r1 = new HashMap<>();
        r1.put("1", "3814-2406");
        r1.put("2", "3814-2416");
        r1.put("3", "3815-2416");
        r1.put("4", "ERROR");

        Map<String, String> r2 = new HashMap<>();
        r2.put("1", "1,55 m");
        r2.put("2", "1,75 m");
        r2.put("3", "1,78 m");
        r2.put("4", "ERROR");

        DataType expResult = DataType.STRING;
        DataType result = DataTypeController.getMostSpecificType(r1, r2);
        assertEquals(expResult, result);

    }
}
