/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.weir.algorithms.integration;

import br.edimarmanica.configuration.General;
import br.edimarmanica.configuration.InterSite;
import br.edimarmanica.configuration.Paths;
import br.edimarmanica.dataset.Domain;
import br.edimarmanica.weir.algorithms.distance.DataTypeController;
import br.edimarmanica.weir.algorithms.distance.TypeAwareDistance;
import br.edimarmanica.weir.bean.Rule;
import br.edimarmanica.weir.bean.ScoredPair;
import br.edimarmanica.weir.check.DataTypeCheck;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

/**
 *
 * @author edimar
 */
public class ScoredPairs {

    private Domain domain;
    private List<Rule> rules;
    private List<ScoredPair> scores = new ArrayList<>();

    public ScoredPairs(Domain domain, List<Rule> rules) {
        this.domain = domain;
        this.rules = rules;
    }

    /**
     * r1 != s1 (r1,s1) = (s1,r1) ordered by scored não decrescente (é
     * crescente, só diz não decrescente, pois pode ter valores iguais)
     *
     * @param rules
     */
    public void compute() {

        for (int r = 0; r < rules.size(); r++) {
            if (rules.get(r).getValues().size() < InterSite.MIN_SHARED_ENTITIES) {
                //System.out.println("Número insuficiente de instâncias compartilhadas.");
                continue;
            }

            for (int s = r + 1; s < rules.size(); s++) { //j=i+1 pois é unordered pairs ou combinação simples ou seja o par (r1,s1) =  (s1, r1)
                if (r == s) {
                    continue; //r != s
                }

                if (rules.get(s).getValues().size() < InterSite.MIN_SHARED_ENTITIES) {
                    //   System.out.println("Número insuficiente de instâncias compartilhadas.");
                    continue;
                }

                double score = TypeAwareDistance.typeDistance(rules.get(r), rules.get(s));
                if (score == -1) {
                    //System.out.println("Número insuficiente de instâncias compartilhadas.");
                    continue;
                }
                scores.add(new ScoredPair(rules.get(r), rules.get(s), score));
            }
        }

        Collections.sort(scores); //non-decresing order
    }

    public void persists() {
        File file = new File(Paths.PATH_WEIR + "/scores/" + domain.getDataset().getFolderName());
        file.mkdirs();

        try (Writer out = new FileWriter(file.getAbsolutePath() + "/" + domain.getFolderName() + ".csv")) {
            String[] header = {"SITE01", "RULE01_ID", "RULE01_LABEL", "SITE02", "RULE02_ID", "RULE02_LABEL", "SCORE"};
            try (CSVPrinter csvFilePrinter = new CSVPrinter(out, CSVFormat.EXCEL.withHeader(header))) {
                for (ScoredPair pair : scores) {
                    List<String> dataRecord = new ArrayList<>();
                    dataRecord.add(pair.getR1().getSite().getFolderName());
                    dataRecord.add(pair.getR1().getRuleID() + "");
                    dataRecord.add(pair.getR1().getLabel() + "");
                    dataRecord.add(pair.getS1().getSite().getFolderName());
                    dataRecord.add(pair.getS1().getRuleID() + "");
                    dataRecord.add(pair.getS1().getLabel() + "");
                    dataRecord.add(pair.getDistance() + "");
                    csvFilePrinter.printRecord(dataRecord);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ScoredPairs.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Obtem o score para os pares de regras. Esse score já deve ter sido
     * calculado e persistido
     *
     * @param domain
     * @param rules
     * @return
     */
    public static List<ScoredPair> getScoredPairs(Domain domain, List<Rule> rules) {

        /**
         * obtendo valores persistidos *
         */
        String LOCAL_SEPARATOR = "!_!";

        Map<String, Double> loadedScores = new HashMap<>(); //Site1!_!rule01ID_Site2!_!rule02id, score
        try (Reader in = new FileReader(Paths.PATH_WEIR + "/scores/" + domain.getDataset().getFolderName() + "/" + domain.getFolderName() + ".csv")) {
            try (CSVParser parser = new CSVParser(in, CSVFormat.EXCEL.withHeader())) {
                for (CSVRecord record : parser) {
                    String key = record.get("SITE01") + LOCAL_SEPARATOR + record.get("RULE01_ID") + LOCAL_SEPARATOR + record.get("SITE02") + LOCAL_SEPARATOR + record.get("RULE02_ID");
                    loadedScores.put(key, Double.valueOf(record.get("SCORE")));
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ScoredPairs.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ScoredPairs.class.getName()).log(Level.SEVERE, null, ex);
        }


        /**
         * pegando só para as regras atuais *
         */
        List<ScoredPair> scores = new ArrayList<>();
        for (int r = 0; r < rules.size(); r++) {

            for (int s = r + 1; s < rules.size(); s++) { //j=i+1 pois é unordered pairs ou combinação simples ou seja o par (r1,s1) =  (s1, r1)
                if (r == s) {
                    continue; //r != s
                }

                Double score = loadedScores.get(rules.get(r).getSite().getFolderName() + LOCAL_SEPARATOR + rules.get(r).getRuleID() + LOCAL_SEPARATOR + rules.get(s).getSite().getFolderName() + LOCAL_SEPARATOR + rules.get(s).getRuleID());
                if (score == null) {
                    score = loadedScores.get(rules.get(s).getSite().getFolderName() + LOCAL_SEPARATOR + rules.get(s).getRuleID() + LOCAL_SEPARATOR + rules.get(r).getSite().getFolderName() + LOCAL_SEPARATOR + rules.get(r).getRuleID());
                    if (score == null) {
                        continue;
                    }
                }
                if (score == -1) {
                    continue;
                }

                scores.add(new ScoredPair(rules.get(r), rules.get(s), score));
            }
        }

        Collections.sort(scores); //non-decresing order
        return scores;
    }
}
