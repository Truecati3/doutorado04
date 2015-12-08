/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.weir.load;

import br.edimarmanica.configuration.General;
import br.edimarmanica.configuration.Paths;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.weir.bean.Rule;
import br.edimarmanica.weir.bean.Value;
import br.edimarmanica.weir.util.ValueNormalizer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.HashSet;
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
public class LoadRules {

    private Site site;
    private Map<Integer, String> labels = new HashMap<>(); //<ruleID,label>
    private Map<Integer, String> rulesCypher = new HashMap<>(); //<ruleID,ruleCypher>
    private Map<String, String> ids = new HashMap<>(); //<URL,EntityID>

    public LoadRules(Site site) {
        this.site = site;
    }

    public Set<Rule> getRules() {
        Set<Rule> rules = new HashSet<>();

        ids = LoadEntityID.loadEntityID(site);
        loadRulesInfo();

        File dir = new File(Paths.PATH_INTRASITE + "/" + site.getPath() + "/extracted_values");
        for (File rule : dir.listFiles()) {//para cada rule
            if (!rule.getName().endsWith(".csv")){
                continue;
            }
            
            Map<String, String> idsCopy = new HashMap<>(ids);
            
            int ruleID = Integer.valueOf(rule.getName().replaceAll("rule_", "").replaceAll("\\.csv", ""));
            Rule newRule = new Rule(ruleID, site, labels.get(ruleID), rulesCypher.get(ruleID));
            
            try (Reader in = new FileReader(rule.getAbsolutePath())) {
                try (CSVParser parser = new CSVParser(in, CSVFormat.EXCEL.withHeader())) {
                    for (CSVRecord record : parser) { //para cada value
                        String url = record.get("URL").replaceAll(".*/bases/"+site.getDomain().getDataset().getFolderName()+"/", "");
                        //o replace abaixo foi adicionado pq os filtros eliminam se os valores são iguais para diferentes regras do mesmo site, mas as vezes os valores de uma regra tinham um símbolo como >>
                        newRule.addValue(ValueNormalizer.normalize(record.get("EXTRACTED VALUE")), url, idsCopy.get(url));
                        idsCopy.remove(url);
                    }
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(LoadRules.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(LoadRules.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            /**
             * As páginas que a regra não extrai valor tem que ficar com valor nulo pq na função de distância isso aumentará a distância e ajudará a eliminar regras fracas
             */
            
            for(String url: idsCopy.keySet()){
                newRule.addValue(new Value(null, url, idsCopy.get(url)));
            }
            
            rules.add(newRule);
        }
        
        return rules;
    }

    private void loadRulesInfo() {
        try (Reader in = new FileReader(Paths.PATH_INTRASITE + "/" + site.getPath() + "/rule_info.csv")) {
            try (CSVParser parser = new CSVParser(in, CSVFormat.EXCEL.withHeader())) {
                for (CSVRecord record : parser) {
                    labels.put(Integer.valueOf(record.get("ID")), record.get("LABEL"));
                    rulesCypher.put(Integer.valueOf(record.get("ID")), record.get("RULE"));
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LoadRules.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LoadRules.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void main(String[] args) {
        Site site = br.edimarmanica.dataset.weir.book.Site.BLACKWELL;
        
        LoadRules load = new LoadRules(site);
        Set<Rule> rules = load.getRules();
        for(Rule r: rules){
            System.out.println("R: "+r.getRuleID());
            for(Value v: r.getValues()){
                System.out.println("\t"+v.getEntityID()+"-"+v.getPageID()+"-["+v.getValue()+"]");
            }
        }
    }
}
