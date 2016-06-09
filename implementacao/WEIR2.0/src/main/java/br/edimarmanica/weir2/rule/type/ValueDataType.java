/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edimarmanica.weir2.rule.type;

/**
 *
 * @author edimar
 */
public class ValueDataType {

    private static String getPattern(DataType type) {
        /**
         * Manter a ordem uma vez que é uma hierarquia
         */
        switch (type) {
            case DATE:
                return "(\\d+(-|/)\\d+(-|/)\\d+)|(.*Jan .*|.*Feb .*|.*Mar .*|.*Apr .*|.*Aug .*|.*Sept .*|.*Oct .*|.*Nov .*|.*Dec .*|.*January.*|.*February.*|.*March.*|.*April.*|.*May.*|.*June.*|.*July.*|.*August.*|.*September.*|.*October.*|.*November.*|.*December.*)|(\\d{4}-\\d{2})";
            case ISBN:
                return "(\\d{3}(-)?)?\\d{10}";
            case PHONE:
                return "(\\()?0?x?x?(\\d{2})?\\s?\\d{4}-\\d{4}"; //(0xx11) 3396-4525
            case CURRENCY:
                return "(\\$|€|EUR|USD|R\\$)(\\s)?\\d+.((,|.)\\d+)*";
            case LENGHT:
                return "\\d+((,|\\.)\\d+)*\\s*(m|cm|km|ft|'|yd|in|'')";
            case WEIGHT:
                return "\\d+((,|\\.)\\d+)*\\s*(kg|g)";
            case NUMBER:
                return "\\d+((,|\\.)\\d+)*";
            case STRING:
                return ".*";
            default:
                return null;         //String
        }
    }

    public static DataType getDataType(String value) {
        String aux = normalize(value);
        for (DataType type : DataType.values()) {
            if (value.matches(getPattern(type))) {
                return type;
            }
        }

        return DataType.STRING;
    }

    private static String normalize(String value) {
        String aux = value.toLowerCase();
        aux = aux.trim();
        aux = aux.replaceAll("\\s+", " ");
        return aux;
    }
}
