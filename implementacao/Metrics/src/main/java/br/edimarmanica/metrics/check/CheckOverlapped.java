/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.metrics.check;

import br.edimarmanica.configuration.Paths;
import br.edimarmanica.dataset.Attribute;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.metrics.swde.ResultsSWDE;
import java.io.File;
import java.util.Map;

/**
 *
 * @author edimar
 */
public class CheckOverlapped {

    private Site site;
    private Attribute attribute;
    private String ruleID1;
    private String ruleID2;

    public CheckOverlapped(Site site, Attribute attribute, String ruleID1, String ruleID2) {
        this.site = site;
        this.attribute = attribute;
        this.ruleID1 = ruleID1;
        this.ruleID2 = ruleID2;
    }

    public void printOverlapped() {
        ResultsSWDE results = new ResultsSWDE(site);
        Map<Integer, String> r1Values = results.loadRule(new File(Paths.PATH_INTRASITE + site.getPath() + "/extracted_values/" + ruleID1));

        Map<Integer, String> r2Values = results.loadRule(new File(Paths.PATH_INTRASITE + site.getPath() + "/extracted_values/" + ruleID2));

        for (Integer u1: r1Values.keySet()) {
            if (r2Values.containsKey(u1)){
                System.out.println("URL: ["+u1+"] - Value R1: ["+r1Values.get(u1)+"] - Value R2: ["+r2Values.get(u1)+"]");
            }
        }
    }
    
    public static void main(String[] args) {
        Site site = br.edimarmanica.dataset.swde.camera.Site.NEWEGG;
        Attribute attribute = br.edimarmanica.dataset.swde.camera.Attribute.PRICE;
        String ruleID1 = "rule_286.csv";
        String ruleID2 = "rule_353.csv";
        
        CheckOverlapped check = new CheckOverlapped(site, attribute, ruleID1, ruleID2);
        check.printOverlapped();
        
    }
}
