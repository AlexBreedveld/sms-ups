//
// Source code recreated from a .class file by Vineflower
//

package br.com.alerta24h.comunicacao.serial;

class Hex {
    private Hex() {
    }

    public static byte[] fromHex(String hex, String delimiter) throws IllegalArgumentException {
        if (delimiter == null) {
            delimiter = "";
        }

        if ((hex.length() + delimiter.length()) % (2 + delimiter.length()) != 0) {
            throw new IllegalArgumentException("Invalid length");
        } else {
            int numBytes = (hex.length() + delimiter.length()) / (2 + delimiter.length());
            byte[] bytes = new byte[numBytes];
            int i = 0;

            for (int j = 0; i < bytes.length; i++) {
                if (i > 0) {
                    String mDelimiter = hex.substring(j, j + delimiter.length());
                    if (!mDelimiter.equals(delimiter)) {
                        throw new IllegalArgumentException(String.format("Invalid delimiter (expected: \"%s\", read: \"%s\")", delimiter, mDelimiter));
                    }

                    j += delimiter.length();
                }

                try {
                    String tmpString = hex.substring(j, j + 2);
                    int uInt = Integer.parseInt(tmpString, 16);
                    bytes[i] = (byte)uInt;
                    j += 2;
                } catch (NumberFormatException var8) {
                    throw new IllegalArgumentException(var8);
                }
            }

            return bytes;
        }
    }

    public static byte[] fromHex(String hex) throws IllegalArgumentException {
        return fromHex(hex, "");
    }

    public static String toHex(byte[] bytes, int off, int len, String delimiter) {
        StringBuilder builder = new StringBuilder();
        int i = off;

        for (int c = 0; c < len; c++) {
            if (c > 0) {
                builder.append(delimiter == null ? "" : delimiter);
            }

            builder.append(String.format("%02x", bytes[i]));
            i++;
        }

        return builder.toString();
    }

    public static String toHex(byte[] bytes, String delimiter) {
        return toHex(bytes, 0, bytes.length, delimiter);
    }

    public static String toHex(byte[] bytes) {
        return toHex(bytes, "");
    }
}
