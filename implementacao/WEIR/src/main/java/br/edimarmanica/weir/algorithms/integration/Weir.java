/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.weir.algorithms.integration;

import br.edimarmanica.configuration.General;
import br.edimarmanica.configuration.Paths;
import br.edimarmanica.dataset.Domain;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.weir.bean.Mapping;
import br.edimarmanica.weir.bean.Rule;
import br.edimarmanica.weir.bean.ScoredPair;
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
public class Weir {
    private Domain domain;
    private List<Rule> rules;
    private Set<Mapping> mappings;

    public Weir(Domain domain, List<Rule> rules) {
        this.domain = domain;
        this.rules = rules;
    }
    
    public void execute(){
        integration();
        persistResults();
    }
    
    private void integration() {

        /**
         * starts with singleton mappings
         */
        mappings = new HashSet<>();
        for (Rule r : rules) {
            mappings.add(new Mapping(r));
        }

        //for
        List<ScoredPair> scoredPairs = ScoredPairs.getScoredPairs(domain, rules);
        int i = 0;
        for (ScoredPair sp : scoredPairs) {
            if (General.DEBUG) {
                System.out.println("Evaluating the pair rule " + sp.getR1().getRuleID() + " of Site " + sp.getR1().getSite().getFolderName() + " with rule " + sp.getS1().getRuleID() + " of Site " + sp.getS1().getSite().getFolderName());
                System.out.println("\tScore: " + sp.getDistance());
            }

            if (sp.getDistance() == 1) {
                break; //Os próximos todos são 1 pq está ordenado. Não vai unir mapeamentos com distância de 1 (totalmente diferentes)
            }

            //se as duas regras já estão no mesmo mapping ignora
            if (inSameMappgin(sp.getR1(), sp.getS1(), mappings)) {
                continue;
            }

            //Minha tentativa: não está no artigo: se as duas regras são do mesmo site e ainda pertencem a mapeamentos únicos, então ignora, pq possivelmente uma delas é uma regra fraca. Ver caso no Goodreadings as regras 79 e 467 não entram nos filtros pq tem alguns valores um pouco diferente para título, mas extraem a mesma coisa
            if (sp.getR1().getSite() == sp.getS1().getSite()){
                if (getMapping(sp.getR1(), mappings).getRules().size() == 1 && getMapping(sp.getS1(), mappings).getRules().size() == 1){
                    continue;
                }
            }

            if (isComplete(sp.getR1(), mappings) || isComplete(sp.getS1(), mappings) || isLC(sp.getR1(), sp.getS1(), mappings)) {
                getMapping(sp.getR1(), mappings).setComplete(true);
                getMapping(sp.getS1(), mappings).setComplete(true);
                if (General.DEBUG) {
                    System.out.println("\t Mapping complete");
                    System.out.print("\t M_R:");
                    for (Rule r : getMapping(sp.getR1(), mappings).getRules()) {
                        System.out.print(r.getSite() + "->" + r.getRuleID() + ",");
                    }
                    System.out.println("");
                    System.out.print("\t M_S:");
                    for (Rule r : getMapping(sp.getS1(), mappings).getRules()) {
                        System.out.print(r.getSite() + "->" + r.getRuleID() + ",");
                    }
                    System.out.println("");
                }
            } else {
                if (General.DEBUG) {
                    System.out.println("\t Unindo...");
                }
                Mapping mR1 = getMapping(sp.getR1(), mappings);
                Mapping mS1 = getMapping(sp.getS1(), mappings);
                Mapping mUnion = new Mapping();
                mUnion.addRules(mR1.getRules());
                mUnion.addRules(mS1.getRules());

                if (General.DEBUG) {
                    System.out.print("\t ");
                    for (Rule r : mUnion.getRules()) {
                        System.out.print(r.getSite() + "->" + r.getRuleID() + ",");
                    }
                    System.out.println("");
                }

                mappings.remove(mR1);
                mappings.remove(mS1);
                mappings.add(mUnion);
            }
        }
    }

    /**
     *
     * @param r
     * @return the Mapping containing the rule r
     */
    private static Mapping getMapping(Rule r, Set<Mapping> mappings) {
        for (Mapping m : mappings) {
            if (m.getRules().contains(r)) {
                return m;
            }
        }
        System.out.println("Não deve entrar aqui! - WEIR.java - getMapping");
        System.exit(-1);
        return null;
    }

    /**
     *
     * @param r
     * @param s
     * @return true if the rules are already in the same mapping
     */
    private static boolean inSameMappgin(Rule r, Rule s, Set<Mapping> mappings) {
        boolean flag = false;
        for (Mapping m : mappings) {

            if (m.getRules().contains(r) && m.getRules().contains(s)) {
                flag = true;
                break;
            }
        }

        return flag;
    }

    /**
     *
     * @param r
     * @param wrappers
     * @return true if the mapping that contains the rule r is complete
     */
    private static boolean isComplete(Rule r, Set<Mapping> mappings) {
        return getMapping(r, mappings).isComplete();
    }

    /**
     *
     * @param r1
     * @param s1
     * @param mappings true se a intersecção entre (os sites do mapping que
     * contém o r1) e (os sites do mapping que contém o s1) não é vazia -- passo
     * 6 do algoritmo
     * @return
     */
    private static boolean isLC(Rule r1, Rule s1, Set<Mapping> mappings) {
        Mapping mR1 = getMapping(r1, mappings);
        Mapping mS1 = getMapping(s1, mappings);

        Set<Site> sitesR1 = new HashSet<>();
        for (Rule r : mR1.getRules()) {
            sitesR1.add(r.getSite());
        }

        for (Rule s : mS1.getRules()) {
            if (sitesR1.contains(s.getSite())) {
                return true;
            }
        }

        return false;
    }

    private void persistResults() {
        String[] header = {"MAP_ID", "SITE", "RULE"};
        File file = new File(Paths.PATH_WEIR + "/mappings/" + domain.getPath() );
        file.mkdirs();
        
        try (Writer out = new FileWriter(file.getAbsolutePath()+ "/mappings.csv")) {
            try (CSVPrinter csvFilePrinter = new CSVPrinter(out, CSVFormat.EXCEL.withHeader(header))) {
                int i=0;
                for (Mapping map : mappings) {
                    if (map.getRules().size() < 2){
                        continue;
                    }
                    
                    for (Rule rule : map.getRules()) {
                        List<String> dataRecord = new ArrayList<>();
                        dataRecord.add(i+"");
                        dataRecord.add(rule.getSite().getFolderName());
                        dataRecord.add(rule.getRuleID()+"");
                        csvFilePrinter.printRecord(dataRecord);
                    }
                    i++;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Weir.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
