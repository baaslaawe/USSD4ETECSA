package dev.mad.ussd4etecsa;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by Daymel on 7/12/2017.
 */

public class Util {

    public static List<String> convertirCadena(String text) {
        List<String> valores = new ArrayList<>();
        StringTokenizer tProcesar = new StringTokenizer(text);
        while (tProcesar.hasMoreTokens()) {
            valores.add(tProcesar.nextToken());
        }
        return valores;
    }
}
