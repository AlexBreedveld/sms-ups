//
// Source code recreated from a .class file by Vineflower
//

package br.com.alerta24h.comunicacao;

import br.com.alerta24h.comunicacao.serial.PortaSerial;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import jssc.SerialPortList;
import org.apache.log4j.Logger;

public class FactoryPortaSerials {
    private static final Logger LOGGER = Logger.getLogger(FactoryPortaSerials.class);
    private static final List<PortaComunicacao> listaPortas = new ArrayList<>();

    public FactoryPortaSerials() {
    }

    public static List<PortaComunicacao> getListaPortas() {
        LOGGER.debug("Criou Lista!");
        listaPortas.clear();
        String[] portNames;
        if (isMac()) {
            portNames = SerialPortList.getPortNames(Pattern.compile("tty.(serial|usbserial|usbmodem|Plser|PL2303).*"));
        } else {
            portNames = SerialPortList.getPortNames();
        }

        LOGGER.debug("Portas seriais encontradas:");

        for (String port : portNames) {
            LOGGER.debug(port);
            if (isWindows()) {
                listaPortas.add(new PortaSerial(port, 2400, 8, 1, 0, true, false));
            } else if (isMac()) {
                listaPortas.add(new PortaSerial(port));
            } else {
                listaPortas.add(new PortaSerial(port, 2400, 8, 1, 0, true, false));
            }
        }

        LOGGER.debug("Fim de portas seriais encontradas:");
        return listaPortas;
    }

    public static boolean isWindows() {
        return System.getProperty("os.name").indexOf("indow") != -1;
    }

    public static boolean isMac() {
        return System.getProperty("os.name").indexOf("Mac") != -1;
    }
}
