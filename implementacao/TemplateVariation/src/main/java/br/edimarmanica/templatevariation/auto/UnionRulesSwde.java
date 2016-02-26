/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.templatevariation.auto;

import br.edimarmanica.configuration.General;
import br.edimarmanica.configuration.Paths;
import br.edimarmanica.dataset.Attribute;
import br.edimarmanica.dataset.Domain;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.metrics.Labels;
import br.edimarmanica.metrics.SiteWithoutThisAttribute;
import br.edimarmanica.metrics.ValueFormatter;
import br.edimarmanica.metrics.swde.GroundTruthSwde;
import br.edimarmanica.metrics.swde.PrinterSwde;
import br.edimarmanica.metrics.swde.ResultsSWDE;
import br.edimarmanica.metrics.swde.RuleMetricsSwde;
import br.edimarmanica.metrics.swde.ValueFormatterSwde;
import br.edimarmanica.templatevariation.auto.bean.Rule;
import br.edimarmanica.templatevariation.auto.load.LoadRule;
import br.edimarmanica.templatevariation.manual.swde.MasterRuleSwde;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author edimar
 */
public class UnionRulesSwde {

    private Site site;
    private Map<Integer, Rule> allRules;
    private int nrPages;
    private PrinterSwde printer;

    public UnionRulesSwde(Site site) {
        this.site = site;

        allRules = LoadRule.loadAllRules(site);
    }

    public void execute() {
        printer = new PrinterSwde(site, Paths.PATH_TEMPLATE_VARIATION_AUTO);
        nrPages = getNrPages();

        for (Attribute attribute : site.getDomain().getAttributes()) {
            execute(attribute);
        }
    }

    public static Integer getMasterRuleManual(Site site, Attribute attribute) throws SiteWithoutThisAttribute {
        ResultsSWDE results = new ResultsSWDE(site);
        Map<String, Map<Integer, String>> allRules = results.loadAllRules();
        MasterRuleSwde master = new MasterRuleSwde(site, attribute, allRules);
        return Integer.parseInt(master.getMasterRule().replaceAll("rule_", "").replaceAll(".csv", ""));
    }

    public static Set<Rule> getMasterRulesInOtherSitesManual(Site site, Attribute attribute) {
        Set<Rule> rules = new HashSet<>();
        System.out.println("MasterRulesInOtherSites");
        for (Site otherSite : site.getDomain().getSites()) {
            if (otherSite != site) {
                LoadRule lr;
                try {
                    lr = new LoadRule(otherSite, getMasterRuleManual(otherSite, attribute));
                    Rule r = lr.loadRule();

                    System.out.println("\tSite: " + otherSite + " - rule: " + r.getRuleID());
                    rules.add(r);
                } catch (SiteWithoutThisAttribute ex) {
                 //   Logger.getLogger(UnionRulesSwde.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return rules;
    }

    public void execute(Attribute attribute) {
        System.out.println("\tAttribute: " + attribute);

        try {

            Integer masterRuleID = getMasterRuleManual(site, attribute);
            if (masterRuleID == null) {
                print(attribute, null, null);
                return;
            }

            Set<Rule> masterRulesInOtherSites = getMasterRulesInOtherSitesManual(site, attribute);

            List<Integer> masterRulesInSite = new ArrayList<>();
            masterRulesInSite.add(masterRuleID);

            Map<String, String> urlsAlreadyExtracted = allRules.get(masterRuleID).getUrlValues();

            while (urlsAlreadyExtracted.size() != nrPages) {
                ComplementaryRule rum = new ComplementaryRule(masterRulesInSite, allRules, masterRulesInOtherSites, nrPages);

                Rule complementaryRuleID = rum.getComplementaryRule();
                if (complementaryRuleID == null) {
                    break; //não tem mais complementares
                }

                masterRulesInSite.add(complementaryRuleID.getRuleID());
                urlsAlreadyExtracted.putAll(complementaryRuleID.getUrlValues());
            }

            print(attribute, masterRulesInSite, urlsAlreadyExtracted);

        } catch (SiteWithoutThisAttribute ex) {
        }
    }

    private void print(Attribute attribute, List<Integer> masterRuleIDs, Map<String, String> masterRuleValues) throws SiteWithoutThisAttribute {
        GroundTruthSwde groundTruth = new GroundTruthSwde(site, attribute);
        groundTruth.load();

        if (groundTruth.getGroundTruth().isEmpty()) {
            return;//não tem esse atributo no gabarito
        }

        if (masterRuleIDs == null) {
            printer.print(attribute, "Attribute not found", "", groundTruth.getGroundTruth(), new HashMap<Integer, String>(), new HashSet<Integer>(), 0, 0, 0);
            return;
        }

        Labels labels = new Labels(site);
        labels.load();

        ValueFormatter formatter = new ValueFormatterSwde();
        Map<Integer, String> pairUrlValue = new HashMap<>();
        for (String url : masterRuleValues.keySet()) {
            pairUrlValue.put(Integer.parseInt(url), formatter.format(masterRuleValues.get(url))); //esse formater vai preparar os valores para a avaliação deixando de acordo com a formatação do groundtruth
        }

        RuleMetricsSwde metrics = new RuleMetricsSwde(pairUrlValue, groundTruth.getGroundTruth());
        metrics.computeMetrics();

        String masterRuleIDSst = "";
        String labelsSt = "";
        for (Integer ruleID : masterRuleIDs) {
            masterRuleIDSst += ruleID + General.SEPARADOR;
            labelsSt += labels.getLabels().get("rule_" + ruleID + ".csv") + General.SEPARADOR;
        }

        printer.print(attribute, masterRuleIDSst, labelsSt, groundTruth.getGroundTruth(), pairUrlValue, metrics.getIntersection(), metrics.getRecall(), metrics.getPrecision(), metrics.getRelevantRetrieved());
    }

    private int getNrPages() {
        File dir = new File(Paths.PATH_BASE + site.getPath());
        return dir.list().length;
    }

    public static void main(String[] args) {
        General.DEBUG = true;
        Domain domain = br.edimarmanica.dataset.swde.Domain.CAMERA;
        for (Site site : domain.getSites()) {
            System.out.println("Site: " + site);
            UnionRulesSwde urw = new UnionRulesSwde(site);
            urw.execute();
        }
    }
}
