/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.weir.check;

import br.edimarmanica.configuration.Paths;
import br.edimarmanica.dataset.Domain;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.weir.algorithms.distance.DataTypeController;
import br.edimarmanica.weir.algorithms.filter.NullValuesFilter;
import br.edimarmanica.weir.algorithms.filter.RulesFilter;
import br.edimarmanica.weir.bean.Rule;
import br.edimarmanica.weir.load.LoadRules;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

/**
 *
 * @author edimar
 */
public class DataTypeCheck {
    public static void main(String[] args) {
        Domain domain = br.edimarmanica.dataset.weir.Domain.FINANCE;
        Set<Rule> rules = new HashSet<>();
        
        
         for (Site site : domain.getSites()) {
            LoadRules lr = new LoadRules(site);
            /** adicionando filtros **/
            RulesFilter filter = new NullValuesFilter(site);
            rules.addAll(filter.filter(lr.getRules())); //só add as regras que satisfazem o filtro
        }
        
         File file = new File(Paths.PATH_WEIR + "/dataTypes/"+domain.getDataset().getFolderName());
         file.mkdirs();
         
         try (Writer out = new FileWriter(file.getAbsolutePath()+"/" + domain.getFolderName() + ".csv")) {
            String[] header = {"DATASET", "DOMAIN", "SITE", "RULE_ID", "RULE_LABEL", "DATA_TYPE"};
            try (CSVPrinter csvFilePrinter = new CSVPrinter(out, CSVFormat.EXCEL.withHeader(header))) {
                for (Rule rule: rules) {
                    List<String> dataRecord = new ArrayList<>();
                    dataRecord.add(domain.getDataset().getFolderName());
                    dataRecord.add(domain.getFolderName());
                    dataRecord.add(rule.getSite().getFolderName());
                    dataRecord.add(rule.getRuleID()+"");
                    dataRecord.add(rule.getLabel());
                    dataRecord.add(DataTypeController.getMostFrequentType(rule).name());
                    csvFilePrinter.printRecord(dataRecord);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(DataTypeCheck.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
