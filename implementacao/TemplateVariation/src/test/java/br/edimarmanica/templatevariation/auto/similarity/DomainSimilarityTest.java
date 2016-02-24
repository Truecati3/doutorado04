/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.templatevariation.auto.similarity;

import br.edimarmanica.templatevariation.auto.bean.Rule;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;

/**
 *
 * @author edimar
 */
public class DomainSimilarityTest extends TestCase {
    
    public DomainSimilarityTest(String testName) {
        super(testName);
    }

    /**
     * Test of score method, of class DomainSimilarity.
     */
    public void testSimilarity() {
        System.out.println("similarity");
        
        Map<String,String> urlValuesMaster = new HashMap<>();
        urlValuesMaster.put("01", "Internacional");
        urlValuesMaster.put("02", "Grêmio");
        urlValuesMaster.put("03", "Palmeiras");
        urlValuesMaster.put("04", "Fluminense");
        
        Rule masterRule = new Rule();
        masterRule.setUrlValues(urlValuesMaster);

        
        Map<String,String> urlValuesComp = new HashMap<>();
        
        urlValuesComp.put("01", "Plameiras"); // 0 - l invertido
        urlValuesComp.put("05", "Fluminense"); // 1
        urlValuesComp.put("07", "Internacional"); // 1
        urlValuesComp.put("15", "Santos"); // 0
        
        Rule complementaryRule = new Rule();
        complementaryRule.setUrlValues(urlValuesComp);
        
        
        DomainSimilarity instance = new DomainSimilarity(masterRule, complementaryRule);
        double expResult = 2.0/4;
        double result = instance.score();
        assertEquals(expResult, result, 0.0);
        
    }
}
