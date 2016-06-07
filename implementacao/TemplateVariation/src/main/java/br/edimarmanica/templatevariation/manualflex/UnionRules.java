/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.templatevariation.manualflex;

import br.edimarmanica.templatevariation.manual.MasterRule;
import br.edimarmanica.configuration.General;
import br.edimarmanica.configuration.Paths;
import br.edimarmanica.dataset.Attribute;
import br.edimarmanica.dataset.Dataset;
import br.edimarmanica.dataset.Domain;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.metrics.GroundTruth;
import br.edimarmanica.metrics.Labels;
import br.edimarmanica.metrics.Printer;
import br.edimarmanica.metrics.Results;
import br.edimarmanica.metrics.RuleMetrics;
import br.edimarmanica.metrics.SiteWithoutThisAttribute;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 *
 * @author edimar
 */
public class UnionRules {

    private Site site;
    private Map<String, Map<String, String>> allRules;
    private int nrPages;
    private Printer printer;

    public UnionRules(Site site) {
        this.site = site;

        Results results = new Results(site);
        allRules = results.loadAllRules(Paths.PATH_INTRASITE);
    }

    public void execute() {
        printer = new Printer(site, Paths.PATH_TEMPLATE_VARIATION_MANUAL_FLEX);
        nrPages = getNrPages(site);

        for (Attribute attribute : site.getDomain().getAttributes()) {
            execute(attribute);
        }
    }

    public void execute(Attribute attribute) {
        System.out.println("\tAttribute: " + attribute);
        
        try {
            String masterRuleID = MasterRule.getMasterRule(site, attribute);

            if (masterRuleID == null) {
                print(attribute, null, null);
                return;
            }

            List<String> masterRuleIDs = new ArrayList<>();
            masterRuleIDs.add(masterRuleID);

            Map<String, String> masterRuleValues = allRules.get(masterRuleID);

            while (masterRuleValues.size() != nrPages) {
                ComplementaryRule rum = new ComplementaryRule(site, attribute, allRules, masterRuleIDs, masterRuleValues);

                String complementaryRuleID = rum.getComplementaryRule();
                if (complementaryRuleID == null) {
                    break; //não tem mais complementares
                }

                masterRuleIDs.add(complementaryRuleID);
                masterRuleValues.putAll(allRules.get(complementaryRuleID));
            }

            print(attribute, masterRuleIDs, masterRuleValues);

        } catch (SiteWithoutThisAttribute ex) {
        }
    }

    private void print(Attribute attribute, List<String> masterRuleIDs, Map<String, String> masterRuleValues) throws SiteWithoutThisAttribute {
        GroundTruth groundTruth = GroundTruth.getInstance(site, attribute);
        groundTruth.load();

        if (groundTruth.getGroundTruth().isEmpty()) {
            return;//não tem esse atributo no gabarito
        }

        if (masterRuleIDs == null) {
            printer.print(attribute, "Attribute not found", "", groundTruth.getGroundTruth(), new HashMap<String, String>(), new HashSet<String>(), 0, 0, 0);
            return;
        }

        Labels labels = new Labels(site);
        labels.load();

        RuleMetrics metrics = RuleMetrics.getInstance(site, masterRuleValues, groundTruth.getGroundTruth());
        metrics.computeMetrics();

        String masterRuleIDSst = "";
        String labelsSt = "";
        for (String ruleID : masterRuleIDs) {
            masterRuleIDSst += ruleID + General.SEPARADOR;
            labelsSt += labels.getLabels().get(ruleID) + General.SEPARADOR;
        }

        printer.print(attribute, masterRuleIDSst, labelsSt, groundTruth.getGroundTruth(), masterRuleValues, metrics.getIntersection(), metrics.getRecall(), metrics.getPrecision(), metrics.getRelevantRetrieved());
    }

    public static int getNrPages(Site site) {
        File dir = new File(Paths.PATH_BASE + site.getPath());
        return dir.list().length;
    }

    public static void main(String[] args) {

        for (Dataset dataset : Dataset.values()) {
            System.out.println("Dataset: " + dataset);
            for (Domain domain : dataset.getDomains()) {
                System.out.println("\tDomain: " + domain);
                for (Site site : domain.getSites()) {
                    if (site != br.edimarmanica.dataset.weir.soccer.Site.CNN) {
                        continue;
                    }

                    System.out.println("\t\tSite: " + site);
                    UnionRules urw = new UnionRules(site);
                    urw.execute();
                }
            }
        }
    }
}
