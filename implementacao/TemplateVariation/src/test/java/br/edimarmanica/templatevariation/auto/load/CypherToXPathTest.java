/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.templatevariation.auto.load;

import br.edimarmanica.templatevariation.auto.similarity.XPathSimilarity;
import static junit.framework.Assert.assertEquals;
import junit.framework.TestCase;

/**
 *
 * @author edimar
 */
public class CypherToXPathTest extends TestCase {
    
    public CypherToXPathTest(String testName) {
        super(testName);
    }

    /**
     * Test of cypher2xpath method, of class CypherToXPath.
     */
    public void testCypher2xpath() {
        System.out.println("cypher2xpath");
        
        String cypher = "MATCH (a2:Template)<--(a1)<--(a0)<--(b)-->(c0)-->(c1)-->(c2)\n"
                + "WHERE a2.VALUE='Yes' AND a2.PATH='/HTML/BODY/DIV/DIV/FORM/DIV/DIV/UL/LI/DIV/text()' AND a2.POSITION='1' \n"
                + "AND a1.VALUE='DIV' AND a1.POSITION='2' \n"
                + "AND a0.VALUE='LI' AND a0.POSITION='3' \n"
                + "AND b.VALUE='UL' \n"
                + "AND c0.VALUE='LI' AND c0.POSITION='5' \n"
                + "AND c1.VALUE='DIV' AND c1.POSITION='1' \n"
                + "AND c2.NODE_TYPE='3' AND c2.POSITION='1' \n"
                + " RETURN c2.VALUE AS VALUE, c2.URL AS URL, 'Template' in LABELS(c2) as template";

        String expResult = "/HTML/BODY/DIV/DIV/FORM/DIV/DIV/UL/LI/DIV/text()<DIV[2]<LI[3]<UL[]>LI[5]>DIV[1]>text()[1]";
        System.out.println(expResult);
        
        String result = CypherToXPath.cypher2xpath(cypher);
        System.out.println(result);
        assertEquals(expResult, result);
    }
}
