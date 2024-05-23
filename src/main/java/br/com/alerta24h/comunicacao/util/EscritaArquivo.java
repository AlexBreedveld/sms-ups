//
// Source code recreated from a .class file by Vineflower
//

package br.com.alerta24h.comunicacao.util;

import br.com.alerta24h.comunicacao.common.SistemaProperties;
import br.com.alerta24h.comunicacao.common.SistemaProperties.Propriedade;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.log4j.Logger;

class EscritaArquivo {
    private static final Logger LOGGER = Logger.getLogger(EscritaArquivo.class);
    private boolean enableLog = false;
    private File file = null;
    private BufferedWriter saida = null;

    EscritaArquivo(String arquivo) {
        try {
            this.enableLog = SistemaProperties.getProperty(Propriedade.LOG_COMUNICACAO).equals("1");
            if (this.enableLog) {
                this.file = new File(arquivo);
                this.saida = new BufferedWriter(new FileWriter(this.file));
                if (!this.file.exists()) {
                    this.file.createNewFile();
                }
            }
        } catch (Exception var3) {
            this.enableLog = false;
            LOGGER.error("Erro: " + var3.getMessage(), var3);
        }
    }

    void escreve(String mensagem) {
        if (this.enableLog) {
            try {
                System.out.println(mensagem);
                this.saida.write(mensagem);
                this.saida.flush();
            } catch (IOException var3) {
                LOGGER.error("Erro: " + var3.getMessage(), var3);
            }
        }
    }
}
