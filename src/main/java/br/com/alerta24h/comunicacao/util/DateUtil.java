//
// Source code recreated from a .class file by Vineflower
//

package br.com.alerta24h.comunicacao.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    public DateUtil() {
    }

    public static String formatarData(String dataI) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("ddMMyyhhmm");
            Date data = format.parse(dataI);
            SimpleDateFormat newFormat = new SimpleDateFormat("dd/MM/yy-hh:mm");
            return newFormat.format(data);
        } catch (Exception var4) {
            return dataI;
        }
    }
}
