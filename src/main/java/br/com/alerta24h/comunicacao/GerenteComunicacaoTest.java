//
// Source code recreated from a .class file by Vineflower
//

package br.com.alerta24h.comunicacao;

import br.com.alerta24h.comunicacao.bean.Bean;
import br.com.alerta24h.comunicacao.bean.ComandoNobreak;
import java.lang.reflect.Method;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Test;

public class GerenteComunicacaoTest {
    public GerenteComunicacaoTest() {
    }

    @After
    public void tearDown() throws Exception {
    }

    private String getTempoHexa(int tempo) {
        String sTempo = Integer.toHexString(tempo);
        int sTempoLength = sTempo.length();

        for (int i = 0; i < 4 - sTempoLength; i++) {
            sTempo = "0" + sTempo;
        }

        return sTempo;
    }

    public int hexToInt(String base) {
        return Integer.parseInt(base, 16);
    }

    @Test
    public void testExecutaComando() throws SecurityException, NoSuchMethodException {
        GerenteComunicacao ger = new GerenteComunicacao();
        Method metodo = GerenteComunicacao.class.getDeclaredMethod("executaComando", Bean.class);
        ComandoNobreak b = new ComandoNobreak();
        b.setComando(84);
        int n = 1;
        int r = 0;
        String tempoN = this.getTempoHexa(1);
        String tempoR = this.getTempoHexa(0);
        String sParam1 = tempoN.substring(0, 2);
        String sParam2 = tempoN.substring(2, 4);
        String sParam3 = tempoR.substring(0, 2);
        String sParam4 = tempoR.substring(2, 4);
        int param1 = this.hexToInt(sParam1);
        int param2 = this.hexToInt(sParam2);
        int param3 = this.hexToInt(sParam3);
        int param4 = this.hexToInt(sParam4);
        b.setParam1((byte)param1);
        b.setParam2((byte)param2);
        b.setParam3((byte)param3);
        b.setParam4((byte)param4);

        try {
            metodo.invoke(ger, b);
        } catch (Exception var17) {
            var17.printStackTrace();
        }
    }

    @Test
    public void testExecutaComandoSMS() {
        try {
            GerenteComunicacao gerente = new GerenteComunicacao();
            ComandoNobreak command = new ComandoNobreak();
            command.setComando(73);
            command.setParam1(0);
            command.setParam2(0);
            command.setParam3(0);
            command.setParam4(0);
            System.out.println(gerente.executaComando(command));
            Thread.sleep(1000L);
            gerente.fecharPortaComunicacao();
        } catch (Throwable var3) {
            Assert.fail(var3.getMessage());
        }
    }

    @Test
    public void testExecutaComandoVoltronic() {
        try {
            GerenteComunicacao gerente = new GerenteComunicacao();
            ComandoNobreak command = new ComandoNobreak();
            command.setComando(81);
            command.setParam1(71);
            command.setParam2(83);
            command.setParam3(13);
            command.setCaracterInicial("(");
            command.setTamanhoResposta(76);
            String RESPV = gerente.executaComandoUpsilon(command);
            System.out.println("TAMANHHO: " + RESPV.length() + " [" + RESPV + "]");
            Thread.sleep(1000L);
            gerente.fecharPortaComunicacao();
        } catch (Throwable var4) {
            Assert.fail(var4.getMessage());
        }
    }

    @Test
    public void testExecutaComandoDaker() {
        try {
            GerenteComunicacao gerente = new GerenteComunicacao();
            ComandoNobreak command = new ComandoNobreak();
            command.setComando(81);
            command.setParam1(49);
            command.setCaracterInicial("(");
            command.setTamanhoResposta(47);
            System.out.println(gerente.executaComandoUpsilon(command));
            Thread.sleep(1000L);
            gerente.fecharPortaComunicacao();
        } catch (Throwable var3) {
            Assert.fail(var3.getMessage());
        }
    }

    @Test
    public void testExecutaComandoDakerUSB() {
        try {
            GerenteComunicacao gerente = new GerenteComunicacao();
            ComandoNobreak command = new ComandoNobreak();
            command.setComando(81);
            command.setParam1(49);
            command.setCaracterInicial("(");
            command.setTamanhoResposta(47);
            System.out.println(gerente.executaComandoUpsilon(command));
            Thread.sleep(1000L);
            gerente.fecharPortaComunicacao();
        } catch (Throwable var3) {
            Assert.fail(var3.getMessage());
        }
    }

    @Test
    public void testExecutaComandoVoltronicUSB() {
        try {
            GerenteComunicacao gerente = new GerenteComunicacao();
            ComandoNobreak command = new ComandoNobreak();
            command.setComando(81);
            command.setParam1(71);
            command.setParam2(83);
            command.setParam3(13);
            command.setCaracterInicial("(");
            command.setTamanhoResposta(76);
            String RESPV = gerente.executaComandoUpsilon(command);
            System.out.println("TAMANHHO: " + RESPV.length() + " [" + RESPV + "]");
            Thread.sleep(1000L);
            gerente.fecharPortaComunicacao();
        } catch (Throwable var4) {
            Assert.fail(var4.getMessage());
        }
    }

    @Test
    public void testExecutaComandoSMSUSB() {
        try {
            GerenteComunicacao gerente = new GerenteComunicacao();
            ComandoNobreak command = new ComandoNobreak();
            command.setComando(73);
            System.out.println(gerente.executaComando(command));
            Thread.sleep(1000L);
            gerente.fecharPortaComunicacao();
        } catch (Throwable var3) {
            Assert.fail(var3.getMessage());
        }
    }
}
