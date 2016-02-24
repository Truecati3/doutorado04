/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.templatevariation.auto.similarity;

import br.edimarmanica.templatevariation.auto.bean.Rule;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import junit.framework.TestCase;

/**
 *
 * @author edimar
 */
public class EntitySimilarityTest extends TestCase {

    public EntitySimilarityTest(String testName) {
        super(testName);
    }

    /**
     * Test of score method, of class EntitySimilarity.
     */
    public void testSimilarity() {
        System.out.println("similarity");

        /**
         * ** Site 01 **
         */
        Map<String, String> entityValuesCompS1 = new HashMap<>();
        entityValuesCompS1.put("01", "Internacional"); // 1 
        entityValuesCompS1.put("02", "Grêmio"); // 1
        entityValuesCompS1.put("03", "Plameiras"); // 0 - escrito errado
        entityValuesCompS1.put("04", "Fluminense"); // 1
        entityValuesCompS1.put("05", "Flamengo"); // 1
        entityValuesCompS1.put("06", "Sport"); // --- ignora pq não compartilha entidade

        Rule complementaryRuleS1 = new Rule();
        complementaryRuleS1.setEntityValues(entityValuesCompS1);

        /**
         * ** Site 02 **
         */
        Map<String, String> entityValuesMasterS2 = new HashMap<>();
        entityValuesMasterS2.put("01", "Internacional");
        entityValuesMasterS2.put("02", "Grêmio");
        entityValuesMasterS2.put("03", "Palmeiras");
        entityValuesMasterS2.put("04", "Fluminense");
        entityValuesMasterS2.put("05", "Falmengo");

        Rule masterRuleS2 = new Rule();
        masterRuleS2.setEntityValues(entityValuesMasterS2);

        /**
         * ** Site 03 **
         */
        Map<String, String> entityValuesMasterS3 = new HashMap<>();
        entityValuesMasterS3.put("01", "Internacional");
        entityValuesMasterS3.put("02", "Grêmio");
        entityValuesMasterS3.put("03", "Palmeiras");
        entityValuesMasterS3.put("04", "Fluminense");
        entityValuesMasterS3.put("05", "Flamengo");

        Rule masterRuleS3 = new Rule();
        masterRuleS3.setEntityValues(entityValuesMasterS3);


        /**
         * * adicionando no conjunto *
         */
        Set<Rule> masterRuleInOtherSites = new HashSet<>();
        masterRuleInOtherSites.add(masterRuleS2);
        masterRuleInOtherSites.add(masterRuleS3);



        EntitySimilarity instance = new EntitySimilarity(masterRuleInOtherSites, null, complementaryRuleS1);
        double expResult = 4.0 / 5; // 4 corretos de 5 entidades compartilhadas -- Vai bater com a masterRuleS3 que tem maior score
        double result = instance.score();
        assertEquals(expResult, result, 0.0);

    }
}
