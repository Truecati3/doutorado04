/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.weir.algorithms.distance;

import br.edimarmanica.configuration.InterSite;
import br.edimarmanica.dataset.weir.book.Site;
import junit.framework.TestCase;
import br.edimarmanica.weir.bean.Rule;

/**
 *
 * @author edimar
 */
public class DataTypeControllerTest02 extends TestCase {

    Site s1 = Site.AMAZON;

    public DataTypeControllerTest02() {
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
        Rule r = new Rule(1, s1);
        r.addValue("3814-2406", "1");
        r.addValue("3814-2416", "2");
        r.addValue("3815-2416", "3");
        r.addValue("ERROR", "4");

        DataType expResult = DataType.PHONE;
        DataType result = DataTypeController.getMostFrequentType(r);
        assertEquals(expResult, result);

    }

    /**
     * Test of getMostSpecificType method, of class DataTypeController.
     */
    public void testGetMostSpecificType_Rule_Rule() {
        System.out.println("getMostSpecificType");
        Rule r1 = new Rule(1, s1);
        r1.addValue("3814-2406", "1");
        r1.addValue("3814-2416", "2");
        r1.addValue("3815-2416", "3");
        r1.addValue("ERROR", "4");

        Rule r2 = new Rule(2, s1);
        r2.addValue("1,55 m", "1");
        r2.addValue("1,75 m", "2");
        r2.addValue("1,78 m", "3");
        r2.addValue("ERROR", "4");

        DataType expResult = DataType.STRING;
        DataType result = DataTypeController.getMostSpecificType(r1, r2);
        assertEquals(expResult, result);

    }
}