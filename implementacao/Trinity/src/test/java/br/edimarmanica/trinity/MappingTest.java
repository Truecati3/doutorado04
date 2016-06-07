/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.trinity;

import br.edimarmanica.trinity.intrasitemapping.auto.Mapping;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author edimar
 */
public class MappingTest {

    private List<List<String>> offset0 = new ArrayList<>();
    private List<List<String>> offsetX = new ArrayList<>();
    private List<Integer> expResult;

    public MappingTest() {
        String[][] off0 = {
            {"A", "B", "C", "D", "E"},
            {"1", "2", "3", "4", "5"},
            {"", "", "", "", ""},
            {"*", "*", "#", "$", "&"}
        };

        for (String[] aux : off0) {
            offset0.add(Arrays.asList(aux));
        }

        String[][] offx = {
            {"A", "B", "C", "D", "E"},
            {"*", "*", "$", "#", "&"},
            {"*", "*", "$", "#", "&"},
            {"*", "*", "$", "#", "&"},
            {"*", "*", "#", "$", "&"}
        };

        for (String[] aux : offx) {
            offsetX.add(Arrays.asList(aux));
        }
        
        Integer[] exp = {0, -1, -1, 4};
        expResult = Arrays.asList(exp);

    }

    @AfterClass
    public static void tearDownClass() {
    }

    /**
     * Test of mappings method, of class Mapping.
     */
    @Test
    public void testMappings() {
        System.out.println("mappings");
        Mapping instance = new Mapping(offset0, offsetX);
        List<Integer> result = instance.mappings();
        assertEquals(expResult, result);
    }

}
