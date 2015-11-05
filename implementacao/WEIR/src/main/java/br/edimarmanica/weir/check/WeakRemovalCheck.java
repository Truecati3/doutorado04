/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.weir.check;

import br.edimarmanica.configuration.General;
import br.edimarmanica.configuration.Paths;
import br.edimarmanica.dataset.Domain;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.weir.algorithms.filter.NullValuesFilter;
import br.edimarmanica.weir.algorithms.filter.RulesFilter;
import br.edimarmanica.weir.algorithms.integration.ScoredPairs;
import static br.edimarmanica.weir.algorithms.weakremoval.WeakRemoval.weakRemoval;
import br.edimarmanica.weir.bean.Rule;
import br.edimarmanica.weir.load.LoadRules;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
public class WeakRemovalCheck {

    private Domain domain;
    private Set<String> expectedRules = new HashSet<>();
    private static final String LOCAL_SEPARATOR = "!_!";

    public WeakRemovalCheck(Domain domain) {
        this.domain = domain;
    }

    private void loadExpectedRules() {
        try (Reader in = new FileReader(Paths.PATH_INTRASITE + "/" + domain.getPath() + "/result.csv")) {
            try (CSVParser parser = new CSVParser(in, CSVFormat.EXCEL.withHeader())) {
                for (CSVRecord record : parser) {
                    expectedRules.add(record.get("SITE") + LOCAL_SEPARATOR + record.get("RULE").replace("rule_", "").replace(".csv", ""));
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(WeakRemovalCheck.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WeakRemovalCheck.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void check(List<Rule> rules) {
        for (String expectedRule : expectedRules) {
            boolean flag = false;
            for (Rule rule : rules) {
                if (expectedRule.equals(rule.getSite().getFolderName() + LOCAL_SEPARATOR + rule.getRuleID())) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                System.out.println("Eliminada: " + expectedRule);
            }
        }
    }

    public static void main(String[] args) {
        General.DEBUG = true;
        Domain domain = br.edimarmanica.dataset.weir.Domain.BOOK;
        List<Rule> rules = new ArrayList<>();

        for (Site site : domain.getSites()) {
            LoadRules lr = new LoadRules(site);
            /**
             * adicionando filtros *
             */
            RulesFilter filter = new NullValuesFilter(site);
            rules.addAll(filter.filter(lr.getRules())); //só add as regras que satisfazem o filtro
        }

        //aqui tem que ter um passo que calcula o score e armazena pq hj ela calcula duas vezes: uma para o WeakRemovalCheck e outra para o Weir
    //    ScoredPairs scores = new ScoredPairs(domain, rules);
   //     scores.compute();
   //     scores.persists();

        rules = weakRemoval(domain, rules);

        WeakRemovalCheck wr = new WeakRemovalCheck(domain);
        wr.loadExpectedRules();
        wr.check(rules);
    }
}
