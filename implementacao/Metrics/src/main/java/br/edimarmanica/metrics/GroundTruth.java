/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.metrics;

import br.edimarmanica.dataset.Attribute;
import static br.edimarmanica.dataset.Dataset.SWDE;
import static br.edimarmanica.dataset.Dataset.WEIR;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.metrics.swde.GroundTruthSwde;
import br.edimarmanica.metrics.weir.GroundTruthWeir;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author edimar
 */
public abstract class GroundTruth {

    protected Site site;
    protected Attribute attribute;
    protected Map<String, String> groundTruth = new HashMap<>(); 

    public GroundTruth(Site site, Attribute attribute) {
        this.site = site;
        this.attribute = attribute;
    }
    
      public static GroundTruth getInstance(Site site, Attribute attribute) {
        switch (site.getDomain().getDataset()) {
            case SWDE:
                return new GroundTruthSwde(site, attribute);
            case WEIR:
                return new GroundTruthWeir(site, attribute);
            default:
                return null;
        }
    }

    public abstract void load() throws SiteWithoutThisAttribute;

    public Map<String, String> getGroundTruth() {
        return groundTruth;
    }
}
