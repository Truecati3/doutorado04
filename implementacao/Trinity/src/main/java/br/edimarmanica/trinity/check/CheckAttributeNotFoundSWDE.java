/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.trinity.check;

import br.edimarmanica.configuration.General;
import br.edimarmanica.dataset.Attribute;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.metrics.SiteWithoutThisAttribute;
import java.util.Map;
import org.apache.commons.lang.StringEscapeUtils;


/**
 *
 * @author edimar
 */
public class CheckAttributeNotFoundSWDE extends CheckAttributeNotFound {

    public CheckAttributeNotFoundSWDE(Site site, Attribute attribute) {
        super(site, attribute);
    }

    @Override
    protected String formatGroundTruthURL(String url) {
        return url.replaceAll(".htm", "");
    }

    @Override
    protected String formatValue(String value) {
        return StringEscapeUtils.unescapeHtml(value)
                .replaceAll(" ", " ")
                .replaceAll("\\\\", "")
                .replaceAll("\"", "")
                .replaceAll("\\s+", " ")
                .replaceAll("[^(a-zA-Z)|\\d|\\.]", ""); //só deixa números, letras e o ponto
    }

    /**
     *
     * @param group a group of a offset (equivalente a uma regra)
     */
    @Override
    protected double getGroupScore(Map<String, String> group) {
        double totalSim = 0;
        for (String url : group.keySet()) {
            String realValue = group.get(url);

            String aux = groundTruth.get(formatGroundTruthURL(url));
            if (aux == null) {
                if (realValue.trim().isEmpty()) {
                    totalSim += 1;
                } else {
                    totalSim += 0;
                }
                continue;
            }
            String partes[] = groundTruth.get(formatGroundTruthURL(url)).split(General.SEPARADOR);
            int nrValues = Integer.parseInt(partes[0]);

            //multivalued attribute
            double maxSim = sim.score(realValue, partes[1]);
            System.out.print(realValue + " X " + partes[1]);
            for (int i = 1; i < nrValues; i++) {
                double curSim = sim.score(realValue, partes[i + 1]);

                if (curSim > maxSim) {
                    maxSim = curSim;
                }
            }
            System.out.println(" -> " + maxSim);
            totalSim += maxSim; //pega o máximo de similaridade
        }
        return totalSim / group.size();
    }

    public static void main(String[] args) throws SiteWithoutThisAttribute {
        Site site = br.edimarmanica.dataset.swde.auto.Site.CARS;
        Attribute attribute = br.edimarmanica.dataset.swde.auto.Attribute.MODEL;

        CheckAttributeNotFound anf = new CheckAttributeNotFoundSWDE(site, attribute);
        anf.execute();

    }

}
