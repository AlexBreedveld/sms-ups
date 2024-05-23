//
// Source code recreated from a .class file by Vineflower
//

package br.com.alerta24h.comunicacao;

import br.com.alerta24h.comunicacao.bean.ComandoNobreak;
import br.com.alerta24h.comunicacao.util.StringUtil;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

public class InformacoesTest {
    private static final Logger LOGGER = Logger.getLogger(InformacoesTest.class);

    public InformacoesTest() {
    }

    @Test
    public void test() {
        LOGGER.debug("Incio da comunicao.");
        System.out.println(System.getProperty("java.library.path"));
        PortaComunicacao driver = (PortaComunicacao)FactoryPortaSerials.getListaPortas().get(0);

        try {
            driver.open();
        } catch (Exception var5) {
            Assert.fail(var5.getMessage());
            LOGGER.error("Erro: [" + var5.getMessage() + "]", var5);
        }

        ComandoNobreak cmd = new ComandoNobreak();
        cmd.setComando(73);

        try {
            String resposta = driver.executaComando(cmd);
            resposta = resposta.replaceAll(" ", "");
            LOGGER.debug("Resposta do nobreak: [" + StringUtil.hex2Ascii(resposta.substring(0, resposta.length() - 4)) + "]");
            this.checaByteErro(resposta);
        } catch (Exception var4) {
            LOGGER.error(var4.getMessage(), var4);
            Assert.fail(var4.getMessage());
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
