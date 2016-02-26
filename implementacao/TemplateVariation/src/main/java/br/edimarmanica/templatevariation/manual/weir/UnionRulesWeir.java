/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.templatevariation.manual.weir;

import br.edimarmanica.configuration.General;
import br.edimarmanica.configuration.Paths;
import br.edimarmanica.dataset.Attribute;
import br.edimarmanica.dataset.Domain;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.metrics.Labels;
import br.edimarmanica.metrics.RuleMetrics;
import br.edimarmanica.metrics.SiteWithoutThisAttribute;
import br.edimarmanica.metrics.weir.GroundTruthWeir;
import br.edimarmanica.metrics.weir.PrinterWeir;
import br.edimarmanica.metrics.weir.ResultsWeir;
import br.edimarmanica.metrics.weir.RuleMetricsWeir;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author edimar
 */
public class UnionRulesWeir {

    private Site site;
    private Map<String, Set<String>> allRules;
    private int nrPages;
    private PrinterWeir printer;

    public UnionRulesWeir(Site site) {
        this.site = site;

        ResultsWeir results = new ResultsWeir(site);
        allRules = results.loadAllRules();
    }

    public void execute() {
        printer = new PrinterWeir(site, Paths.PATH_TEMPLATE_VARIATION_MANUAL);
        nrPages = getNrPages();

        for (Attribute attribute : site.getDomain().getAttributes()) {
            execute(attribute);
        }
    }

    public void execute(Attribute attribute) {
        System.out.println("\tAttribute: " + attribute);
        MasterRuleWeir master = new MasterRuleWeir(site, attribute, allRules);
        try {
            String masterRuleID = master.getMasterRule();
            
            if (masterRuleID == null){
                print(attribute, null, null);
                return;
            }

            List<String> masterRuleIDs = new ArrayList<>();
            masterRuleIDs.add(masterRuleID);

            Set<String> masterRuleValues = allRules.get(masterRuleID);

            while (masterRuleValues.size() != nrPages) {
                ComplementaryRuleWeir rum = new ComplementaryRuleWeir(site, attribute, allRules, masterRuleIDs, masterRuleValues);

                String complementaryRuleID = rum.getComplementaryRule();
                if (complementaryRuleID == null) {
                    break; //não tem mais complementares
                }

                masterRuleIDs.add(complementaryRuleID);
                masterRuleValues.addAll(allRules.get(complementaryRuleID));
            }

            print(attribute, masterRuleIDs, masterRuleValues);

        } catch (SiteWithoutThisAttribute ex) {
        }
    }

    private void print(Attribute attribute, List<String> masterRuleIDs, Set<String> masterRuleValues) throws SiteWithoutThisAttribute {
        GroundTruthWeir groundTruth = new GroundTruthWeir(site, attribute);
        groundTruth.load();
        
        if (masterRuleIDs == null){
            printer.print(attribute, "Attribute not found", "", groundTruth.getGroundTruth(), new HashSet<String>(), 0, 0, 0);
            return;
        }

        Labels labels = new Labels(site);
        labels.load();

        RuleMetrics metrics = new RuleMetricsWeir(masterRuleValues, groundTruth.getGroundTruth());
        metrics.computeMetrics();

        String masterRuleIDSst = "";
        String labelsSt = "";
        for (String ruleID : masterRuleIDs) {
            masterRuleIDSst += ruleID + ":";
            labelsSt += labels.getLabels().get(ruleID) + General.SEPARADOR;
        }

        printer.print(attribute, masterRuleIDSst, labelsSt, groundTruth.getGroundTruth(), masterRuleValues, metrics.getRecall(), metrics.getPrecision(), metrics.getRelevantRetrieved());
    }

    private int getNrPages() {
        File dir = new File(Paths.PATH_BASE + site.getPath());
        return dir.list().length;
    }

    public static void main(String[] args) {
        Domain domain = br.edimarmanica.dataset.weir.Domain.SOCCER;
        for (Site site : domain.getSites()) {
            System.out.println("Site: "+site);
            UnionRulesWeir urw = new UnionRulesWeir(site);
            urw.execute();
        }
    }
}
