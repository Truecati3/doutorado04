/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.expressiveness;

import br.edimarmanica.expressiveness.generate.CypherNotation;
import static junit.framework.Assert.assertEquals;
import junit.framework.TestCase;

/**
 *
 * @author edimar
 */
public class CypherNotationTest extends TestCase {

    public CypherNotationTest(String testName) {
        super(testName);
    }

    /**
     * Test of getNotation method, of class CypherNotation.
     */
    public void testGetNotation() {
        System.out.println("getNotation");

        /**
         * ***************** Teste 01 ***************
         */
        String uniquePathValue = "/HTML[1]/BODY[1]/FONT[1]/TABLE[2]/TR[2]/TD[2]/TABLE[2]/TR[1]/TD[1]/TABLE[1]/TR[1]/TD[1]/TABLE[1]/TR[1]/TD[3]/A[1]/text()[1]";
        String uniquePathLabel = "/HTML[1]/BODY[1]/FONT[1]/TABLE[2]/TR[2]/TD[2]/TABLE[2]/TR[1]/TD[1]/TABLE[1]/TR[1]/TD[1]/TABLE[1]/TR[1]/TD[1]/p[2]/text()[1]";
        String label = "Author:";

        String expResult = "MATCH (a2)<--(a1)<--(a0)<--(b)-->(c0)-->(c1)-->(c2) "
                + "\nWHERE a2.VALUE='Author:' AND a2.PATH='/HTML/BODY/FONT/TABLE/TR/TD/TABLE/TR/TD/TABLE/TR/TD/TABLE/TR/TD/p/text()' AND a2.POSITION='1' "
                + "\nAND a1.VALUE='p' AND a1.POSITION='2' "
                + "\nAND a0.VALUE='TD' AND a0.POSITION='1' "
                + "\nAND b.VALUE='TR' "
                + "\nAND c0.VALUE='TD' AND c0.POSITION='3' "
                + "\nAND c1.VALUE='A' AND c1.POSITION='1' "
                + "\nAND c2.NODE_TYPE='3' AND c2.POSITION='1' "
                + "\nRETURN c2.VALUE AS VALUE, c2.URL AS URL";
        String result = CypherNotation.getNotation(label, uniquePathLabel, uniquePathValue);
        assertEquals("Teste 01", expResult.replaceAll("\\s+", ""), result.replaceAll("\\s+", ""));

        /**
         * ***************** Teste 02 ***************
         */
        uniquePathValue = "/HTML[1]/BODY[1]/FONT[1]/TABLE[2]/TR[2]/TD[2]/TABLE[2]/TR[1]/TD[1]/TABLE[1]/TR[1]/TD[1]/TABLE[1]/TR[1]/TD[3]/A[1]/text()[15]";
        uniquePathLabel = "/HTML[1]/BODY[1]/FONT[1]/TABLE[2]/TR[2]/TD[2]/TABLE[2]/TR[1]/TD[1]/TABLE[1]/TR[1]/TD[1]/TABLE[1]/TR[1]/TD[1]/text()[13]";

        expResult = "MATCH (a1)<--(a0)<--(b)-->(c0)-->(c1)-->(c2) "
                + "\nWHERE a1.VALUE='Author:' AND a1.PATH='/HTML/BODY/FONT/TABLE/TR/TD/TABLE/TR/TD/TABLE/TR/TD/TABLE/TR/TD/text()' AND a1.POSITION='13'  "
                + "\nAND a0.VALUE='TD' AND a0.POSITION='1'  "
                + "\nAND b.VALUE='TR' "
                + "\nAND c0.VALUE='TD' AND c0.POSITION='3'  "
                + "\nAND c1.VALUE='A' AND c1.POSITION='1'  "
                + "\nAND c2.NODE_TYPE='3' AND c2.POSITION='15' "
                + "\nRETURN c2.VALUE AS VALUE, c2.URL AS URL";

        result = CypherNotation.getNotation(label, uniquePathLabel, uniquePathValue);
        assertEquals("Teste 02", expResult.replaceAll("\\s+", ""), result.replaceAll("\\s+", ""));

        /**
         * ***************** Teste 03 ***************
         */
        uniquePathValue = "/HTML[1]/BODY[1]/FONT[1]/TABLE[2]/TR[2]/TD[2]/TABLE[2]/TR[1]/TD[1]/TABLE[1]/TR[1]/TD[1]/TABLE[1]/TR[1]/TD[1]/A[1]/text()[1]";
        uniquePathLabel = "/HTML[1]/BODY[1]/FONT[1]/TABLE[2]/TR[2]/TD[2]/TABLE[2]/TR[1]/TD[1]/TABLE[1]/TR[1]/TD[1]/TABLE[1]/TR[1]/TD[1]/text()[1]";
        expResult = "MATCH (a0)<--(b)-->(c0)-->(c1) "
                + "\nWHERE a0.VALUE='Author:' AND a0.PATH='/HTML/BODY/FONT/TABLE/TR/TD/TABLE/TR/TD/TABLE/TR/TD/TABLE/TR/TD/text()' AND a0.POSITION='1' "
                + "\nAND b.VALUE='TD' "
                + "\nAND c0.VALUE='A' AND c0.POSITION='1'  "
                + "\nAND c1.NODE_TYPE='3' AND c1.POSITION='1' "
                + "\nRETURN c1.VALUE AS VALUE, c1.URL AS URL";

        result = CypherNotation.getNotation(label, uniquePathLabel, uniquePathValue);
        System.out.println(result);
        assertEquals("Teste 03", expResult.replaceAll("\\s+", ""), result.replaceAll("\\s+", ""));
    }
}
