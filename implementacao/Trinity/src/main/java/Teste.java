
import org.apache.commons.lang.StringEscapeUtils;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author edimar
 */
public class Teste {
    public static void main(String[] args) {
        String st = "aki/teste/0012.csv";
        
        System.out.println(st.replaceAll("\\..*", ""));
        
        
        
    }
}
