//
// Source code recreated from a .class file by Vineflower
//

package br.com.alerta24h.comunicacao.util;

public class StringUtil {
    private static final byte BYTE_DELIMITER = 13;

    public StringUtil() {
    }

    public static String substringEndChar(byte[] dadosRecebido) {
        return substring(dadosRecebido, (byte)13);
    }

    public static String substring(byte[] dadosRecebido, byte delimiter) {
        StringBuilder bufferLeitura = new StringBuilder();

        for (byte element : dadosRecebido) {
            char caracter = (char)element;
            bufferLeitura.append(caracter);
            if (element == delimiter) {
                break;
            }
        }

        return bufferLeitura.toString();
    }

    public static String hex2Ascii(String hex) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < hex.length() - 1; i += 2) {
            String output = hex.substring(i, i + 2);
            int decimal = Integer.parseInt(output, 16);
            sb.append((char)decimal);
        }

        return sb.toString();
    }
}
