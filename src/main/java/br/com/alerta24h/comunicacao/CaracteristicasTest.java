//
// Source code recreated from a .class file by Vineflower
//

package br.com.alerta24h.comunicacao;

import br.com.alerta24h.comunicacao.bean.ComandoNobreak;
import br.com.alerta24h.comunicacao.util.StringUtil;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

public class CaracteristicasTest {
    private static final Logger LOGGER = Logger.getLogger(CaracteristicasTest.class);

    public CaracteristicasTest() {
    }

    @Test
    public void testHexConvert() {
        int[] bytes = new int[]{58, 65, 82, 50, 50, 48, 48, 32, 83, 32, 32, 32, 32, 49, 46, 48, -17, -65, -67, 13};
        String msg = "";

        for (int b : bytes) {
            msg = msg + " " + b + ">" + Integer.toHexString(b);
        }

        System.out.println(msg);
    }

    @Test
    public void test() {
        LOGGER.debug("Incio da comunicao.");
        PortaUSBHID driver = PortaUSBHID.getInstance();

        try {
            driver.open();
        } catch (Exception var5) {
            Assert.fail(var5.getMessage());
            LOGGER.error("Erro: [" + var5.getMessage() + "]", var5);
        }

        ComandoNobreak cmd = new ComandoNobreak();
        cmd.setComando(70);

        try {
            String resposta = driver.executaComando(cmd);
            resposta = resposta.replaceAll(" ", "");
            LOGGER.debug("Resposta do nobreak: [" + StringUtil.hex2Ascii(resposta.substring(0, resposta.length() - 4)) + "]");
            this.checaByteErro(resposta);
        } catch (Exception var4) {
            Assert.fail(var4.getMessage());
            LOGGER.error(var4.getMessage(), var4);
        }

        Assert.assertTrue(true);
    }

    private void checaByteErro(String aBase) throws Exception {
        if (aBase != null) {
            int leituraSoma = 0;

            for (int i = 0; i <= aBase.length() - 6; i += 2) {
                leituraSoma += (byte)Integer.parseInt(aBase.substring(i, i + 2), 16);
            }

            String check = Integer.toHexString(256 - leituraSoma);
            if (check.length() == 1) {
                check = "0" + check;
            } else if (check.length() > 2) {
                check = check.substring(check.length() - 2, check.length());
            }

            String checkLeitura = aBase.substring(aBase.length() - 4, aBase.length() - 2);
            if (!checkLeitura.equalsIgnoreCase(check)) {
                throw new Exception("Erro de check");
            }
        }
    }
}
