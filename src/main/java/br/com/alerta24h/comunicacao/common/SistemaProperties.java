package br.com.alerta24h.comunicacao.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.log4j.Logger;

public class SistemaProperties {
    private static File file;

    private static Properties props;

    private static final Logger LOGGER = Logger.getLogger(SistemaProperties.class);

    static {
        String prop = String.valueOf(System.getProperty("user.dir")) + File.separator + "xml" + File.separator + "configuracoes.properties";
        file = new File(prop);
        props = new Properties();
        try {
            FileInputStream in = new FileInputStream(file);
            props.load(in);
            in.close();
        } catch (IOException ex) {
            LOGGER.error("Erro: [" + ex.getMessage() + "]", ex);
        }
    }

    public static String getProperty(Propriedade chave) {
        return props.getProperty(chave.toString());
    }

    public static void setProperty(Propriedade chave, String valor) {
        props.setProperty(chave.toString(), valor);
        try {
            FileOutputStream out = new FileOutputStream(file);
            props.store(out, "Configurado arquivo sistema.properties");
            out.close();
            out = null;
        } catch (IOException e) {
            LOGGER.error("Erro: [" + e.getMessage() + "]", e);
        }
    }

    public enum Propriedade {
        LOG_COMUNICACAO, PORTA_FIXA;
    }
}
