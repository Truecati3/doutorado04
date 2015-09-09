/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.extractionrules.html;

import br.edimarmanica.extractionrules.load.FormatUniquePath;
import static junit.framework.Assert.assertEquals;
import junit.framework.TestCase;
import org.w3c.dom.Node;

/**
 *
 * @author edimar
 */
public class FormatUniquePathTest extends TestCase {

    public FormatUniquePathTest(String testName) {
        super(testName);
    }

    /**
     * Test of format method, of class FormatUniquePath.
     */
    public void testFormat() {
        System.out.println("format");
        FormatUniquePath formatter = new FormatUniquePath("page0");

        /**
         * ********** Teste 01 ************
         */
        String uniquePath = "/HTML/BODY/FONT/TABLE[2]/TR[2]/TD[2]/TABLE[2]/TR/TD[1]/TABLE/TR/TD[1]/TABLE/TR[15]";
        String expResult = "/HTML[1]/BODY[1]/FONT[1]/TABLE[2]/TR[2]/TD[2]/TABLE[2]/TR[1]/TD[1]/TABLE[1]/TR[1]/TD[1]/TABLE[1]/TR[15]";
        String result = formatter.format(uniquePath, Node.ELEMENT_NODE);
        assertEquals("Teste 01", expResult, result);

        /**
         * ********** Teste 02 ************
         */
        uniquePath = "/HTML/BODY/FONT/TABLE[2]/TR[2]/TD[2]/TABLE[2]/TR/TD[1]/TABLE/TR/TD[1]/TABLE/TR[1]/TD[3]/A/text()";
        expResult = "/HTML[1]/BODY[1]/FONT[1]/TABLE[2]/TR[2]/TD[2]/TABLE[2]/TR[1]/TD[1]/TABLE[1]/TR[1]/TD[1]/TABLE[1]/TR[1]/TD[3]/A[1]/text()[1]";
        result = formatter.format(uniquePath, Node.TEXT_NODE);
        assertEquals("Teste 02", expResult, result);
        
        /**
         * ********** Teste 02 ************
         */
        uniquePath = "/HTML/BODY/FONT/TABLE[2]/TR[2]/TD[2]/TABLE[2]/TR/TD[1]/TABLE/TR/TD[1]/TABLE/TR[1]/TD[3]/A/text()";
        expResult = "/HTML[1]/BODY[1]/FONT[1]/TABLE[2]/TR[2]/TD[2]/TABLE[2]/TR[1]/TD[1]/TABLE[1]/TR[1]/TD[1]/TABLE[1]/TR[1]/TD[3]/A[1]/text()[2]"; //mesmo uniquePath na mesma página
        result = formatter.format(uniquePath, Node.TEXT_NODE);
        assertEquals("Teste 03", expResult, result);
    }
}
