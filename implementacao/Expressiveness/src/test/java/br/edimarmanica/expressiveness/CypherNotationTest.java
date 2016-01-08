/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.expressiveness;

import br.edimarmanica.expressiveness.generate.CypherNotation;
import br.edimarmanica.expressiveness.generate.beans.CypherRule;
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

        String expResult = "MATCH (a2:Template)<--(a1)<--(a0)<--(b)-->(c0)-->(c1)-->(c2) "
                + "\nWHERE a2.VALUE={value0} AND a2.PATH={value1} AND a2.POSITION={value2} "
                + "\nAND a1.VALUE={value3} AND a1.POSITION={value4} "
                + "\nAND a0.VALUE={value5} AND a0.POSITION={value6} "
                + "\nAND b.VALUE={value7} "
                + "\nAND c0.VALUE={value8} AND c0.POSITION={value9} "
                + "\nAND c1.VALUE={value10} AND c1.POSITION={value11} "
                + "\nAND c2.NODE_TYPE={value12} AND c2.POSITION={value13} "
                + "\nRETURN c2.VALUE AS VALUE, c2.URL AS URL, 'Template' in LABELS(c2) as template";
        CypherNotation cypherNotation = new CypherNotation(label, uniquePathLabel, uniquePathValue);
        CypherRule result = cypherNotation.getNotation();
        assertEquals("Teste 01", expResult.replaceAll("\\s+", ""), result.getQuery().replaceAll("\\s+", ""));

        /**
         * ***************** Teste 02 ***************
         */
        uniquePathValue = "/HTML[1]/BODY[1]/FONT[1]/TABLE[2]/TR[2]/TD[2]/TABLE[2]/TR[1]/TD[1]/TABLE[1]/TR[1]/TD[1]/TABLE[1]/TR[1]/TD[3]/A[1]/text()[15]";
        uniquePathLabel = "/HTML[1]/BODY[1]/FONT[1]/TABLE[2]/TR[2]/TD[2]/TABLE[2]/TR[1]/TD[1]/TABLE[1]/TR[1]/TD[1]/TABLE[1]/TR[1]/TD[1]/text()[13]";

        expResult = "MATCH (a1:Template)<--(a0)<--(b)-->(c0)-->(c1)-->(c2) "
                + "\nWHERE a1.VALUE={value0} AND a1.PATH={value1} AND a1.POSITION={value2}  "
                + "\nAND a0.VALUE={value3} AND a0.POSITION={value4}  "
                + "\nAND b.VALUE={value5} "
                + "\nAND c0.VALUE={value6} AND c0.POSITION={value7}  "
                + "\nAND c1.VALUE={value8} AND c1.POSITION={value9}  "
                + "\nAND c2.NODE_TYPE={value10} AND c2.POSITION={value11} "
                + "\nRETURN c2.VALUE AS VALUE, c2.URL AS URL, 'Template' in LABELS(c2) as template";

        cypherNotation = new CypherNotation(label, uniquePathLabel, uniquePathValue);
        result = cypherNotation.getNotation();
        assertEquals("Teste 02", expResult.replaceAll("\\s+", ""), result.getQuery().replaceAll("\\s+", ""));

        /**
         * ***************** Teste 03 ***************
         */
        uniquePathValue = "/HTML[1]/BODY[1]/FONT[1]/TABLE[2]/TR[2]/TD[2]/TABLE[2]/TR[1]/TD[1]/TABLE[1]/TR[1]/TD[1]/TABLE[1]/TR[1]/TD[1]/A[1]/text()[1]";
        uniquePathLabel = "/HTML[1]/BODY[1]/FONT[1]/TABLE[2]/TR[2]/TD[2]/TABLE[2]/TR[1]/TD[1]/TABLE[1]/TR[1]/TD[1]/TABLE[1]/TR[1]/TD[1]/text()[1]";
        expResult = "MATCH (a0:Template)<--(b)-->(c0)-->(c1) "
                + "\nWHERE a0.VALUE={value0} AND a0.PATH={value1} AND a0.POSITION={value2} "
                + "\nAND b.VALUE={value3} "
                + "\nAND c0.VALUE={value4} AND c0.POSITION={value5}  "
                + "\nAND c1.NODE_TYPE={value6} AND c1.POSITION={value7} "
                + "\nRETURN c1.VALUE AS VALUE, c1.URL AS URL, 'Template' in LABELS(c1) as template";

        cypherNotation = new CypherNotation(label, uniquePathLabel, uniquePathValue);
        result = cypherNotation.getNotation();
        System.out.println(result);
        assertEquals("Teste 03", expResult.replaceAll("\\s+", ""), result.getQuery().replaceAll("\\s+", ""));
    }
}
