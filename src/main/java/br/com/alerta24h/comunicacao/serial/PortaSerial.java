//
// Source code recreated from a .class file by Vineflower
//

package br.com.alerta24h.comunicacao.serial;

import br.com.alerta24h.comunicacao.PortaComunicacao;
import br.com.alerta24h.comunicacao.bean.ComandoNobreak;
import br.com.alerta24h.comunicacao.bean.DumpLogBean;
import br.com.alerta24h.comunicacao.util.DateUtil;
import java.nio.charset.StandardCharsets;
import jssc.SerialPort;
import jssc.SerialPortException;
import org.apache.log4j.Logger;

public class PortaSerial extends PortaComunicacao {
    private static final Logger LOGGER = Logger.getLogger(PortaSerial.class);
    private final String porta;
    private SerialPort serialPort = null;
    private final int baudRate;
    private final int dataBits;
    private final int stopBits;
    private final int parity;
    private final boolean rts;
    private final boolean dtr;
    private int readSize = -1;

    public PortaSerial(String _porta) {
        this(_porta, 2400, 8, 1, 0, true, false);
    }

    public PortaSerial(String _porta, int baudRate, int dataBits, int stopBits, int parity, boolean rts, boolean dtr) {
        this.porta = _porta;
        this.baudRate = baudRate;
        this.dataBits = dataBits;
        this.stopBits = stopBits;
        this.parity = parity;
        this.rts = rts;
        this.dtr = dtr;
    }

    public synchronized void close() {
        if (this.serialPort != null) {
            try {
                this.serialPort.closePort();
            } catch (SerialPortException var2) {
                LOGGER.error("Falha fechando a porta serial: [" + this.porta + "]. Error: [" + var2.getMessage() + "]", var2);
            }

            this.serialPort = null;
        }
    }

    public Boolean isClosed() {
        return this.serialPort == null ? true : false;
    }

    public String executaComando(ComandoNobreak beanComunicacao) throws Exception {
        this.readSize = 18;
        UtilSerial.send(this.serialPort, beanComunicacao);
        return this.returnBufferRead();
    }

    private synchronized String returnBufferRead() throws Exception {
        return UtilSerial.receive(this.serialPort, this.readSize);
    }

    public void executaDisparo(ComandoNobreak beanComunicacao) throws Exception {
        this.readSize = -1;
        UtilSerial.send(this.serialPort, beanComunicacao);
    }

    public void open() throws Exception {
        this.close();
        LOGGER.debug("Abrindo porta serial: [" + this.porta + "]");
        this.serialPort = new SerialPort(this.porta);

        try {
            this.serialPort.openPort();
            this.serialPort.setParams(this.baudRate, this.dataBits, this.stopBits, this.parity);
            this.serialPort.setRTS(this.rts);
            this.serialPort.setDTR(this.dtr);
        } catch (SerialPortException var2) {
            LOGGER.error("Erro: [" + var2.getMessage() + "]", var2);
        }
    }

    public DumpLogBean executaComandoDumLog(ComandoNobreak bean) throws Exception {
        this.readSize = 36;
        UtilSerial.sendComandDumpLog(this.serialPort, bean.getComando(), bean.getParam1(), bean.getParam2(), bean.getParam3(), bean.getParam4());

        try {
            return getBeanDumpLog(this.returnBufferRead());
        } catch (Exception var3) {
            LOGGER.error("Erro: [" + var3.getMessage() + "]", var3);
            return null;
        }
    }

    public String enviaUpsilon(int c, int... p) throws Exception {
        this.readSize = -1;

        try {
            UtilSerial.sendUpsilon(this.serialPort, c, p);
            String txt = new String(Hex.fromHex(this.returnBufferRead()), StandardCharsets.US_ASCII);
            LOGGER.debug("Informações de resposta concatenadas [" + txt + "]");
            return txt;
        } catch (Exception var4) {
            LOGGER.error("Erro: [" + var4.getMessage() + "]", var4);
            this.close();
            throw new Exception("Ocorreu um erro na porta");
        }
    }

    public void executaDisparoUpsilon(int c, int... p) throws Exception {
        this.readSize = -1;

        try {
            UtilSerial.sendUpsilon(this.serialPort, c, p);
        } catch (Exception var4) {
            LOGGER.error("Erro: [" + var4.getMessage() + "]", var4);
            this.close();
            throw new Exception("Ocorreu um erro na porta");
        }
    }

    public String getNome() {
        return this.porta;
    }

    public void open(int vendorId, int productId) throws Exception {
    }

    private static DumpLogBean getBeanDumpLog(String buffer) throws Exception {
        String evento = "";
        String dataEvento = "";
        String numero = "";

        for (int index = 0; index < buffer.length(); index++) {
            char charValue = buffer.charAt(index);
            if (charValue >= 0 && charValue != '\r') {
                if (index <= 9) {
                    dataEvento = dataEvento + charValue;
                } else if (index > 9 && index <= 12) {
                    numero = numero + charValue;
                } else if (index > 12 && index <= 15) {
                    evento = evento + charValue;
                } else if (index > 17 && index <= 33) {
                    evento = evento + charValue;
                }
            }
        }

        dataEvento = DateUtil.formatarData(dataEvento);
        StringBuffer log = new StringBuffer();
        log.append("--------------------------------------\n");
        log.append("RESPOSTA : \n");
        log.append("Data   : " + dataEvento + "\n");
        log.append("Numero : " + numero + "\n");
        log.append("Evento : " + evento + "\n");
        log.append("--------------------------------------\n");
        LOGGER.debug(log.toString());
        DumpLogBean dump = new DumpLogBean();
        dump.setData(dataEvento);
        dump.setNumeroEvento(numero);
        dump.setDescricao(evento);
        return dump;
    }
}
