/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.weir2.check;

import br.edimarmanica.configuration.Paths;
import br.edimarmanica.dataset.Attribute;
import br.edimarmanica.dataset.Domain;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.metrics.MergeResults;
import br.edimarmanica.metrics.SiteWithoutThisAttribute;
import br.edimarmanica.weir2.distance.TypeAwareDistance;
import br.edimarmanica.weir2.rule.Loader;
import br.edimarmanica.weir2.rule.type.DataType;
import br.edimarmanica.weir2.rule.type.RuleDataType;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 *
 * @author edimar
 */
public class CheckDistanceExpectedMapping {

    private Domain domain;

    public CheckDistanceExpectedMapping(Domain domain) {
        this.domain = domain;
    }

    public void execute() {
        System.out.println("dataset;domain;attribute;site1;rule1;site2;rule2;distance");
        for (Attribute attr : domain.getAttributes()) {
            List<String> masterRule = new ArrayList<>();
            for (Site site : domain.getSites()) {
                masterRule.add(getMasterRule(site, attr)); //coloca mesmo qdo retorna null para manter alinhado com o vetor de sites
            }

            for (int i = 0; i < masterRule.size() - 1; i++) {
                if (masterRule.get(i) == null) { //pula
                    continue;
                }
                for (int j = 0; j < masterRule.size(); j++) {
                    if (masterRule.get(j) == null) { //pula
                        continue;
                    }
                    System.out.println(domain.getDataset()+";"+domain+";"+attr+";"+domain.getSites()[i]+";"+masterRule.get(i)+";"+domain.getSites()[j]+";"+masterRule.get(j)+ ";" + getDistance(domain.getSites()[i], masterRule.get(i), domain.getSites()[j], masterRule.get(j)));
                }
            }

        }
    }

    //obter as masterRules de cada site para cada atributo
    private String getMasterRule(Site site, Attribute attribute) {
        try (Reader in = new FileReader(Paths.PATH_INTRASITE + "/" + site.getPath() + "/result.csv")) {
            try (CSVParser parser = new CSVParser(in, CSVFormat.EXCEL.withHeader())) {

                for (CSVRecord record : parser) {
                    if (record.get("ATTRIBUTE").equals(attribute.getAttributeID())) {
                        if (record.get("RULE").equals("Attribute not found")) {
                            return null;
                        }
                        return record.get("RULE");
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CheckDistanceExpectedMapping.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CheckDistanceExpectedMapping.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    private double getDistance(Site site1, String rule1, Site site2, String rule2) {
        File fileR1 = new File(Paths.PATH_INTRASITE + "/" + site1.getPath() + "/extracted_values/" + rule1);
        DataType typeR1 = RuleDataType.getMostFrequentType(fileR1);
        Map<String, String> entityValuesR1 = Loader.loadEntityValues(fileR1, Loader.loadEntityID(site1));

        File fileR2 = new File(Paths.PATH_INTRASITE + "/" + site2.getPath() + "/extracted_values/" + rule2);
        DataType typeR2 = RuleDataType.getMostFrequentType(fileR2);
        Map<String, String> entityValuesR2 = Loader.loadEntityValues(fileR2, Loader.loadEntityID(site2));

        return TypeAwareDistance.typeDistance(entityValuesR1, typeR1, entityValuesR2, typeR2);
    }

    public static void main(String[] args) {
        Domain domain = br.edimarmanica.dataset.weir.Domain.BOOK;
        CheckDistanceExpectedMapping check = new CheckDistanceExpectedMapping(domain);
        check.execute();
    }
}
