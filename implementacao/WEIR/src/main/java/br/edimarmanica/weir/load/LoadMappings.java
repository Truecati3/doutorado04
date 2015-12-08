/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.weir.load;

import br.edimarmanica.configuration.Paths;
import br.edimarmanica.dataset.Domain;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.weir.bean.Mapping;
import br.edimarmanica.weir.bean.Rule;
import br.edimarmanica.weir.bean.Value;
import br.edimarmanica.weir.util.ValueNormalizer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 *
 * @author edimar
 */
public class LoadMappings {

    private Domain domain;

    public LoadMappings(Domain domain) {
        this.domain = domain;
    }

    public List<Mapping> getMappings() {
        List<Mapping> mappings = new ArrayList<>();
        try (Reader in = new FileReader(Paths.PATH_WEIR + "/mappings/" + domain.getPath() + "/mappings.csv")) {
            try (CSVParser parser = new CSVParser(in, CSVFormat.EXCEL.withHeader())) {
                int lastMapID = -1;
                Mapping newMap = null;
                for (CSVRecord record : parser) {
                    if (Integer.valueOf(record.get("MAP_ID")) == lastMapID) {
                        Rule newRule = new Rule(Integer.valueOf(record.get("RULE")), getSite(record.get("SITE")));
                        newRule.setValues(getValues(newRule.getSite(), newRule.getRuleID()));
                        newMap.addRule(newRule);
                    } else {
                        if (lastMapID != -1) {
                            mappings.add(newMap);
                        }

                        lastMapID = Integer.valueOf(record.get("MAP_ID"));
                        newMap = new Mapping();
                        Rule newRule = new Rule(Integer.valueOf(record.get("RULE")), getSite(record.get("SITE")));
                        newRule.setValues(getValues(newRule.getSite(), newRule.getRuleID()));
                        newMap.addRule(newRule);
                    }
                }
                if (lastMapID != -1) {
                    mappings.add(newMap);
                }

            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LoadRules.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LoadRules.class.getName()).log(Level.SEVERE, null, ex);
        }

        return mappings;
    }

    private Site getSite(String folderName) {
        for (Site s : domain.getSites()) {
            if (s.getFolderName().equals(folderName)) {
                return s;
            }
        }
        return null;
    }

    private Set<Value> getValues(Site site, int ruleID) {
        Set<Value> values = new HashSet<>();

        try (Reader in = new FileReader(Paths.PATH_INTRASITE + "/" + site.getPath() + "/extracted_values/rule_" + ruleID + ".csv")) {
            try (CSVParser parser = new CSVParser(in, CSVFormat.EXCEL.withHeader())) {
                for (CSVRecord record : parser) { //para cada value
                    String url = record.get("URL").replaceAll(".*/bases/" + site.getDomain().getDataset().getFolderName() + "/", "");
                    //o replace abaixo foi adicionado pq os filtros eliminam se os valores são iguais para diferentes regras do mesmo site, mas as vezes os valores de uma regra tinham um símbolo como >>
                    values.add(new Value(ValueNormalizer.normalize(record.get("EXTRACTED VALUE")), url));
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LoadRules.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LoadRules.class.getName()).log(Level.SEVERE, null, ex);
        }
        return values;
    }

    public static void main(String[] args) {
        LoadMappings lm = new LoadMappings(br.edimarmanica.dataset.weir.Domain.BOOK);
        List<Mapping> maps = lm.getMappings();
        int i = 0;
        for (Mapping ma : maps) {
            System.out.println("Map: " + i);
            for (Rule r : ma.getRules()) {
                System.out.println(r.getRuleID() + " - " + r.getSite());
            }
            i++;
        }
    }
}
