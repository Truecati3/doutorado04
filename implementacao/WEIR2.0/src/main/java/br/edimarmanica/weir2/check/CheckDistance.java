/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.weir2.check;

import br.edimarmanica.configuration.Paths;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.weir2.distance.TypeAwareDistance;
import br.edimarmanica.weir2.rule.Loader;
import br.edimarmanica.weir2.rule.type.DataType;
import br.edimarmanica.weir2.rule.type.RuleDataType;
import java.io.File;
import java.util.Map;

/**
 *
 * @author edimar
 */
public class CheckDistance {

    public static void main(String[] args) {
        Site site1 = br.edimarmanica.dataset.weir.book.Site.BOOKMOOCH;
        File rule1 = new File(Paths.PATH_INTRASITE + "/" + site1.getPath() + "/extracted_values/rule_110.csv");
        DataType typeRule1 = RuleDataType.getMostFrequentType(rule1);
        Map<String, String> entityValuesR1 = Loader.loadEntityValues(rule1, Loader.loadEntityID(site1));

        Site site2 = br.edimarmanica.dataset.weir.book.Site.BOOKMOOCH;
        File rule2 = new File(Paths.PATH_INTRASITE + "/" + site2.getPath() + "/extracted_values/rule_379.csv");
        DataType typeRule2 = RuleDataType.getMostFrequentType(rule2);
        Map<String, String> entityValuesR2 = Loader.loadEntityValues(rule2, Loader.loadEntityID(site2));

        double distance = TypeAwareDistance.typeDistance(entityValuesR1, typeRule1, entityValuesR2, typeRule2);
        System.out.println("Type R1: "+typeRule1);
        System.out.println("Type R2: "+typeRule2);
        System.out.println("Distance: "+distance);
    }
}
