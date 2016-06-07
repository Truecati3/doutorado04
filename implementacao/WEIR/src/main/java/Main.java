
import br.edimarmanica.configuration.General;
import br.edimarmanica.dataset.Domain;
import br.edimarmanica.dataset.Site;
import br.edimarmanica.weir.algorithms.filter.ExpectedValuesFilter;
import br.edimarmanica.weir.algorithms.filter.NullValuesFilter;
import br.edimarmanica.weir.algorithms.filter.RulesFilter;
import br.edimarmanica.weir.algorithms.filter.SameValuesFilter;
import br.edimarmanica.weir.algorithms.filter.SubSetFilter;
import br.edimarmanica.weir.algorithms.integration.ScoredPairs;
import br.edimarmanica.weir.algorithms.integration.Weir;
import static br.edimarmanica.weir.algorithms.weakremoval.WeakRemoval.weakRemoval;
import br.edimarmanica.weir.bean.Mapping;
import br.edimarmanica.weir.bean.Rule;
import br.edimarmanica.weir.load.LoadRules;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author edimar
 */
public class Main {
    public static void main(String[] args) {
        General.DEBUG = true;
        Domain domain = br.edimarmanica.dataset.swde.Domain.BOOK;
        List<Rule> rules = new ArrayList<>();
        
        for (Site site : domain.getSites()) {
            System.out.println("Site: "+site);
            
            LoadRules lr = new LoadRules(site);
            /** adicionando filtros **/
            RulesFilter filter01 = new NullValuesFilter(site);
            RulesFilter filter02 = new SameValuesFilter(site);
            RulesFilter filter03 = new SubSetFilter(site);
            rules.addAll(filter03.filter(filter02.filter(filter01.filter(lr.getRules())))); //só add as regras que satisfazem os filtros
        }
        
        //aqui tem que ter um passo que calcula o score e armazena pq hj ela calcula duas vezes: uma para o WeakRemoval e outra para o Weir
        System.out.println("ScoredPairs");
        ScoredPairs scores = new ScoredPairs(domain, rules);
        scores.compute();
        scores.persists();
        
        //rules = weakRemoval(domain, rules); -- não estou usando
        
        System.out.println("Integration");
        Weir weir = new Weir(domain, rules);
        weir.execute();
    }
}
