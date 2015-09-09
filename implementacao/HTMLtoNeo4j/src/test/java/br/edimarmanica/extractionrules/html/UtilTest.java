/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.extractionrules.html;

import br.edimarmanica.extractionrules.load.Util;
import static junit.framework.Assert.assertEquals;
import junit.framework.TestCase;

/**
 *
 * @author edimar
 */
public class UtilTest extends TestCase {

    public UtilTest(String testName) {
        super(testName);
    }

    /**
     * Test of getNodePosition method, of class Util.
     */
    public void testGetNodePosition() {
        System.out.println("getNodePosition");

        /**
         * ********** Teste 01 ************
         */
        String formattedUniquePath = "/HTML[1]/BODY[1]/FONT[1]/TABLE[2]/TR[2]/TD[2]/TABLE[2]/TR[1]/TD[1]/TABLE[1]/TR[1]/TD[1]/TABLE[1]/TR[15]";
        int expResult = 15;
        int result = Util.getNodePosition(formattedUniquePath);
        assertEquals(expResult, result);

        /**
         * ********** Teste 02 ************
         */
        formattedUniquePath = "/HTML[1]/BODY[1]/FONT[1]/TABLE[2]/TR[2]/TD[2]/TABLE[2]/TR[1]/TD[1]/TABLE[1]/TR[1]/TD[1]/TABLE[1]/TR[1]/TD[3]/A[1]/text()[1]";
        expResult = 1;
        result = Util.getNodePosition(formattedUniquePath);
        assertEquals(expResult, result);
    }


    /**
     * Test of getCypherRelativePath method, of class Util.
     */
    public void testGetCypherRelativePath() {
        System.out.println("getCypherRelativePath");
        /**
         * ***************** Teste 01 ***************
         */
        String formattedUniquePathValue = "/HTML[1]/BODY[1]/FONT[1]/TABLE[2]/TR[2]/TD[2]/TABLE[2]/TR[1]/TD[1]/TABLE[1]/TR[1]/TD[1]/TABLE[1]/TR[1]/TD[3]/A[1]/text()[1]";
        String formattedUniquePathTemplate = "/HTML[1]/BODY[1]/FONT[1]/TABLE[2]/TR[2]/TD[2]/TABLE[2]/TR[1]/TD[1]/TABLE[1]/TR[1]/TD[1]/TABLE[1]/TR[1]/TD[1]/p[2]/text()[1]";
        String templateText = "Author:";
        String templatePath = "/HTML/BODY/FONT/TABLE/TR/TD/TABLE/TR/TD/TABLE/TR/TD/TABLE/TR/TD/text()";

        String expResult = "MATCH (a2)<-[*]-(a1)<-[*]-(a0)<-[*]-(b)-[*]->(c0)-[*]->(c1)-[*]->(c2) "
                + "\nWHERE a2.VALUE='Author:' AND a2.PATH='/HTML/BODY/FONT/TABLE/TR/TD/TABLE/TR/TD/TABLE/TR/TD/TABLE/TR/TD/text()' "
                + "\nAND a1.VALUE='p' AND a1.POSITION='2' "
                + "\nAND a0.VALUE='TD' AND a0.POSITION='1' "
                + "\nAND b.VALUE='TR' AND b.POSITION='1' "
                + "\nAND c0.VALUE='TD' AND c0.POSITION='3' "
                + "\nAND c1.VALUE='A' AND c1.POSITION='1' "
                + "\nAND c2.NODE_TYPE='3' "
                + "\nRETURN c2.VALUE";
        String result = Util.getCypherRelativePath(formattedUniquePathValue, formattedUniquePathTemplate, templateText, templatePath);
        assertEquals("Teste 01", expResult.replaceAll("\\s+", ""), result.replaceAll("\\s+", ""));

        /**
         * ***************** Teste 02 ***************
         */
        formattedUniquePathValue = "/HTML[1]/BODY[1]/FONT[1]/TABLE[2]/TR[2]/TD[2]/TABLE[2]/TR[1]/TD[1]/TABLE[1]/TR[1]/TD[1]/TABLE[1]/TR[1]/TD[3]/A[1]/text()[1]";
        formattedUniquePathTemplate = "/HTML[1]/BODY[1]/FONT[1]/TABLE[2]/TR[2]/TD[2]/TABLE[2]/TR[1]/TD[1]/TABLE[1]/TR[1]/TD[1]/TABLE[1]/TR[1]/TD[1]/text()[1]";

        expResult = "MATCH (a1)<-[*]-(a0)<-[*]-(b)-[*]->(c0)-[*]->(c1)-[*]->(c2) "
                + "\nWHERE a1.VALUE='Author:' AND a1.PATH='/HTML/BODY/FONT/TABLE/TR/TD/TABLE/TR/TD/TABLE/TR/TD/TABLE/TR/TD/text()'  "
                + "\nAND a0.VALUE='TD' AND a0.POSITION='1'  "
                + "\nAND b.VALUE='TR' AND b.POSITION='1'  "
                + "\nAND c0.VALUE='TD' AND c0.POSITION='3'  "
                + "\nAND c1.VALUE='A' AND c1.POSITION='1'  "
                + "\nAND c2.NODE_TYPE='3' "
                + "\nRETURN c2.VALUE";

        result = Util.getCypherRelativePath(formattedUniquePathValue, formattedUniquePathTemplate, templateText, templatePath);
        assertEquals("Teste 02", expResult.replaceAll("\\s+", ""), result.replaceAll("\\s+", ""));

        /**
         * ***************** Teste 03 ***************
         */
        formattedUniquePathValue = "/HTML[1]/BODY[1]/FONT[1]/TABLE[2]/TR[2]/TD[2]/TABLE[2]/TR[1]/TD[1]/TABLE[1]/TR[1]/TD[1]/TABLE[1]/TR[1]/TD[1]/A[1]/text()[1]";
        formattedUniquePathTemplate = "/HTML[1]/BODY[1]/FONT[1]/TABLE[2]/TR[2]/TD[2]/TABLE[2]/TR[1]/TD[1]/TABLE[1]/TR[1]/TD[1]/TABLE[1]/TR[1]/TD[1]/text()[1]";
        expResult = "MATCH (a0)<-[*]-(b)-[*]->(c0)-[*]->(c1) "
                + "\nWHERE a0.VALUE='Author:' AND a0.PATH='/HTML/BODY/FONT/TABLE/TR/TD/TABLE/TR/TD/TABLE/TR/TD/TABLE/TR/TD/text()'  "
                + "\nAND b.VALUE='TD' AND b.POSITION='1'  "
                + "\nAND c0.VALUE='A' AND c0.POSITION='1'  "
                + "\nAND c1.NODE_TYPE='3' "
                + "\nRETURN c1.VALUE";

        result = Util.getCypherRelativePath(formattedUniquePathValue, formattedUniquePathTemplate, templateText, templatePath);
        assertEquals("Teste 03", expResult.replaceAll("\\s+", ""), result.replaceAll("\\s+", ""));
    }
}
