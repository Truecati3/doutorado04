/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.templatevariation.manual.swde;

import br.edimarmanica.configuration.General;
import br.edimarmanica.configuration.Paths;
import br.edimarmanica.dataset.Attribute;
import br.edimarmanica.dataset.Domain;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.metrics.Labels;
import br.edimarmanica.metrics.SiteWithoutThisAttribute;
import br.edimarmanica.metrics.swde.GroundTruthSwde;
import br.edimarmanica.metrics.swde.PrinterSwde;
import br.edimarmanica.metrics.swde.ResultsSWDE;
import br.edimarmanica.metrics.swde.RuleMetricsSwde;
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
public class UnionRulesSwde {

    private Site site;
    private Map<String, Map<Integer, String>> allRules;
    private int nrPages;
    private PrinterSwde printer;

    public UnionRulesSwde(Site site) {
        this.site = site;

        ResultsSWDE results = new ResultsSWDE(site);
        allRules = results.loadAllRules();
    }

    public void execute() {
        printer = new PrinterSwde(site, Paths.PATH_TEMPLATE_VARIATION);
        nrPages = getNrPages();

        for (Attribute attribute : site.getDomain().getAttributes()) {
            execute(attribute);
        }
    }

    public void execute(Attribute attribute) {
        System.out.println("\tAttribute: " + attribute);
        MasterRuleSwde master = new MasterRuleSwde(site, attribute, allRules);
        try {
            String masterRuleID = master.getMasterRule();
            
            if (masterRuleID == null){
                print(attribute, null, null);
                return;
            }

            List<String> masterRuleIDs = new ArrayList<>();
            masterRuleIDs.add(masterRuleID);

            Map<Integer, String> masterRuleValues = allRules.get(masterRuleID);

            while (masterRuleValues.size() != nrPages) {
                ComplementaryRuleSwde rum = new ComplementaryRuleSwde(site, attribute, allRules, masterRuleIDs, masterRuleValues);

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

    private void print(Attribute attribute, List<String> masterRuleIDs, Map<Integer, String> masterRuleValues) throws SiteWithoutThisAttribute {
        GroundTruthSwde groundTruth = new GroundTruthSwde(site, attribute);
        groundTruth.load();
        
        if (masterRuleIDs == null){
            printer.print(attribute, "Attribute not found", "", groundTruth.getGroundTruth(), new HashMap<Integer,String>(), new HashSet<Integer>(), 0, 0, 0);
            return;
        }

        Labels labels = new Labels(site);
        labels.load();

        RuleMetricsSwde metrics = new RuleMetricsSwde(masterRuleValues, groundTruth.getGroundTruth());
        metrics.computeMetrics();

        String masterRuleIDSst = "";
        String labelsSt = "";
        for (String ruleID : masterRuleIDs) {
            masterRuleIDSst += ruleID + General.SEPARADOR;
            labelsSt += labels.getLabels().get(ruleID) + General.SEPARADOR;
        }

        printer.print(attribute, masterRuleIDSst, labelsSt, groundTruth.getGroundTruth(), masterRuleValues, metrics.getIntersection(), metrics.getRecall(), metrics.getPrecision(), metrics.getRelevantRetrieved());
    }

    private int getNrPages() {
        File dir = new File(Paths.PATH_BASE + site.getPath());
        return dir.list().length;
    }

    public static void main(String[] args) {
        Domain domain = br.edimarmanica.dataset.swde.Domain.CAMERA;
        for (Site site : domain.getSites()) {
            System.out.println("Site: "+site);
            UnionRulesSwde urw = new UnionRulesSwde(site);
            urw.execute();
        }
    }
}
