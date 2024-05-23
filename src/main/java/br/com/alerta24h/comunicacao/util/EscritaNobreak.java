//
// Source code recreated from a .class file by Vineflower
//

package br.com.alerta24h.comunicacao.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import org.apache.log4j.Logger;

public class EscritaNobreak {
    private static final Logger LOGGER = Logger.getLogger(EscritaNobreak.class);
    private final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd--HH-mm-ss");
    private final String BARRABARRA_ = "// ";
    private final String REQUISICAO = ">";
    private final String RESPOSTA = "<";
    private EscritaArquivo escritaArquivo = null;
    private static EscritaNobreak instancia = null;

    public static EscritaNobreak getInstancia() {
        if (instancia == null) {
            instancia = new EscritaNobreak();
        }

        return instancia;
    }

    private EscritaNobreak() {
        String caminho = System.getProperty("user.dir") + File.separator + "logs" + File.separator + "sistema";
        this.escritaArquivo = new EscritaArquivo(caminho + File.separator + "RR_" + this.SDF.format(new Date()) + ".log");
    }

    public void escreverRespostaTradicional(byte[] arr) {
        this.formatarERegistrarTradicional(arr, "<");
    }

    public void escreverRespostaTradicional(int[] arr) {
        this.formatarERegistrarTradicional(this.getBytes(arr), "<");
    }

    public void escreverRequisicaoTradicional(byte[] arr) {
        this.formatarERegistrarTradicional(arr, ">");
    }

    public void escreverRequisicaoTradicional(int[] arr) {
        this.formatarERegistrarTradicional(this.getBytes(arr), ">");
    }

    private byte[] getBytes(int[] arr) {
        byte[] b = new byte[arr.length];
        int i = 0;

        for (int c : arr) {
            b[i] = (byte)c;
            i++;
        }

        return b;
    }

    private void formatarERegistrarTradicional(byte[] arr, String simbolo) {
        String msg = simbolo + Arrays.toString(arr) + "\n";
        msg = msg + "// ";

        for (byte b : arr) {
            msg = msg + (char)b;
        }

        msg = msg + "\n";
        msg = msg + "// ";
        int byteHexa = 0;
        StringBuffer bufferLeitura = new StringBuffer();

        for (byte element : arr) {
            bufferLeitura.append(Integer.toHexString(element));
        }

        msg = msg + bufferLeitura.toString() + "\r";
        msg = msg + "\n";
        this.escritaArquivo.escreve("// " + this.SDF.format(new Date()));
        this.escritaArquivo.escreve("\n");
        LOGGER.debug(msg);
        this.escritaArquivo.escreve(msg);
        this.escritaArquivo.escreve("\n");
    }

    public void escreverRequisicaoUpsilon(byte[] arr) {
        this.formatarERegistrarUpsilonRequisicao(arr, ">");
    }

    public void escreverRespostaUpsilon(byte[] arr) {
        this.formatarERegistrarUpsilonResposta(arr, "<");
    }

    private void formatarERegistrarUpsilonResposta(byte[] arr, String simbolo) {
        this.escritaArquivo.escreve("// " + this.SDF.format(new Date()));
        this.escritaArquivo.escreve("\n");
        String msg = simbolo + Arrays.toString(arr) + "\n";
        msg = msg + "// ";

        for (byte b : arr) {
            msg = msg + Integer.toHexString(b);
        }

        msg = msg + "\n";
        msg = msg + "// ";

        for (byte b : arr) {
            msg = msg + (char)b;
        }

        msg = msg + "\n";
        LOGGER.debug(msg);
        this.escritaArquivo.escreve(msg);
    }

    private void formatarERegistrarUpsilonRequisicao(byte[] arr, String simbolo) {
        this.escritaArquivo.escreve("// " + this.SDF.format(new Date()));
        this.escritaArquivo.escreve("\n");
        String msg = simbolo + Arrays.toString(arr) + "\n";
        msg = msg + "// ";

        for (byte b : arr) {
            msg = msg + (char)b;
        }

        msg = msg + "\n";
        msg = msg + "// ";
        int i = 0;

        for (byte b : arr) {
            if (i > 0) {
                msg = msg + " ";
            }

            msg = msg + String.format("%02x", b & 255);
            i++;
        }

        msg = msg + "\n";
        LOGGER.debug(msg);
        this.escritaArquivo.escreve(msg);
    }

    public void escreveComentario(String sinal, String comentario) {
        this.escritaArquivo.escreve(sinal + " " + comentario + "\n");
    }

    public void escreveComentario(String comentario) {
        this.escreveComentario("// ", comentario);
    }
}
