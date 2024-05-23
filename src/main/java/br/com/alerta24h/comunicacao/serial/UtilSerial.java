//
// Source code recreated from a .class file by Vineflower
//

package br.com.alerta24h.comunicacao.serial;

import br.com.alerta24h.comunicacao.bean.ComandoNobreak;
import br.com.alerta24h.comunicacao.util.EscritaNobreak;
import java.util.Arrays;
import jssc.SerialPort;
import org.apache.log4j.Logger;

public class UtilSerial {
    private static final int TIMEOUT_READ_SERIAL_MILISECONDS = 600;
    private static final Logger LOGGER = Logger.getLogger(UtilSerial.class);
    static final String CODE_ERROR_5A5A5A = "5a 5a 5a";
    private static final int ESPERA_TOTAL = 1000;

    public UtilSerial() {
    }

    public static int getCheckSum(ComandoNobreak comando) {
        int com = comando.getComando();
        int param1 = comando.getParam1();
        int param2 = comando.getParam2();
        int param3 = comando.getParam3();
        int param4 = comando.getParam4();
        int total = com + param1 + param2 + param3 + param4;
        LOGGER.debug("Check for command " + (char)comando.getComando() + " = " + (256 - total));
        return 256 - total;
    }

    public static String limpaInicioResposta(String resposta) {
        String BYTE_INICIAL_INVALIDO = new String(new char[1]);
        if (resposta == null || !resposta.startsWith("00") && !resposta.startsWith(BYTE_INICIAL_INVALIDO + BYTE_INICIAL_INVALIDO)) {
            if (resposta != null && resposta.startsWith(BYTE_INICIAL_INVALIDO)) {
                resposta = resposta.substring(1);
            }
        } else {
            resposta = resposta.substring(2);
        }

        return resposta;
    }

    public static void sendUpsilon(SerialPort port, int comando, int... parametros) throws Exception {
        LOGGER.debug("Parametros: [" + Arrays.toString(parametros) + "]");
        int LENGTH = parametros.length;
        byte[] buffer = new byte[2 + LENGTH];
        LOGGER.debug("Setando comando");
        buffer[0] = (byte)comando;
        LOGGER.debug("Setando parametros");

        for (int i = 0; i < LENGTH; i++) {
            buffer[1 + i] = (byte)parametros[i];
        }

        LOGGER.debug("Fechando comando com barra r");
        buffer[1 + LENGTH] = "\r".getBytes()[0];
        String strBuffet = new String(buffer);
        LOGGER.debug("Escrevendo requisicao: [" + strBuffet + "]");
        EscritaNobreak.getInstancia().escreverRequisicaoUpsilon(buffer);
        port.writeBytes(buffer);
    }

    public static void send(SerialPort port, ComandoNobreak comando) throws Exception {
        byte[] buffer = new byte[]{
                (byte)comando.getComando(),
                (byte)comando.getParam1(),
                (byte)comando.getParam2(),
                (byte)comando.getParam3(),
                (byte)comando.getParam4(),
                (byte)comando.getCheck(),
                "\r".getBytes()[0]
        };
        LOGGER.debug(
                "char: ("
                        + (char)buffer[0]
                        + ","
                        + (char)buffer[1]
                        + ","
                        + (char)buffer[2]
                        + ","
                        + (char)buffer[3]
                        + ","
                        + (char)buffer[4]
                        + ","
                        + (char)buffer[5]
                        + ","
                        + (char)buffer[6]
                        + ")"
        );
        LOGGER.debug("bytes: (" + buffer[0] + "," + buffer[1] + "," + buffer[2] + "," + buffer[3] + "," + buffer[4] + "," + buffer[5] + "," + buffer[6] + ")");
        EscritaNobreak.getInstancia().escreverRequisicaoTradicional(buffer);
        port.writeBytes(buffer);
    }

    public static void sendComandDumpLog(SerialPort port, int comando, int param1, int param2, int param3, int param4) throws Exception {
        port.writeByte((byte)comando);
        port.writeByte((byte)param1);
        port.writeByte((byte)param2);
        port.writeByte((byte)param3);
        port.writeByte((byte)param4);
        int com = (byte)comando;
        int total = com + param1 + param2 + param3 + param4;
        int numeroDecimal2 = -total;
        port.writeByte((byte)numeroDecimal2);
        port.writeBytes("\r".getBytes());
    }

    public static String receive(SerialPort port, int size) throws Exception {
        String hexChar = "";

        try {
            byte[] data;
            if (size > 0) {
                LOGGER.info("Leitura do serial disponivel: [" + port.getInputBufferBytesCount() + "]");
                data = port.readBytes(size, 1000);
                StringBuilder sb = new StringBuilder();

                for (byte b : data) {
                    sb.append(String.format("%02X", b & 255));
                }

                hexChar = sb.toString();
            } else {
                do {
                    data = port.readBytes(1, 600);
                    hexChar = hexChar + String.format("%02X", data[0] & 255);
                } while (data[0] != 13);
            }
        } catch (Exception var9) {
            LOGGER.error("Falha na Leitura do serial. Erro: [" + var9.getMessage() + "]", var9);
        }

        EscritaNobreak.getInstancia().escreverRespostaTradicional(hexChar.getBytes());
        if (size > 0 && hexChar.length() != size * 2) {
            LOGGER.debug("Dados inválidos: tamanho da resposta fora da faixa: [" + hexChar.length() + "]");
            return null;
        } else if (hexChar.contains("5a 5a 5a")) {
            LOGGER.debug("Resposta em formato invalido (5a) [comando nao reconhecido]: [" + hexChar + "]");
            return null;
        } else {
            LOGGER.debug("Retornando: [" + hexChar + "]");
            return hexChar;
        }
    }
}
