package br.com.alerta24h.comunicacao;

import br.com.alerta24h.comunicacao.bean.ComandoNobreak;
import br.com.alerta24h.comunicacao.bean.DumpLogBean;
import br.com.alerta24h.comunicacao.excecoes.OtherChipsetFoundException;
import br.com.alerta24h.comunicacao.util.EscritaNobreak;
import br.com.alerta24h.comunicacao.util.StringUtil;
import com.codeminders.hidapi.HIDDevice;
import com.codeminders.hidapi.HIDDeviceInfo;
import com.codeminders.hidapi.HIDDeviceNotFoundException;
import com.codeminders.hidapi.HIDManager;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.log4j.Logger;

public class PortaUSBHID extends PortaComunicacao {
    private enum Arch {
        X86, AMD64;

        private static Arch getNative() {
            String osArch = System.getProperty("os.arch");
            if (osArch.equals("i386") || osArch.equals("i686") || osArch.equals("x86"))
                return X86;
            if (osArch.equals("amd64") || osArch.equals("universal") || osArch.equals("x86_64"))
                return AMD64;
            throw new UnsupportedOperationException(String.format("Unsupported os.arch: %s", new Object[] { osArch }));
        }
    }

    public enum Platform {
        LINUX_X64("libs/64/libhidapi-jni.so"),
        LINUX_X86("libs/32/libhidapi-jni.so"),
        WINDOWS_X64("libs\\hidapi-jni-64.dll"),
        WINDOWS_X86("libs\\hidapi-jni.dll"),
        OSX_X64("libs/64/libhidapi-jni.jnilib"),
        OSX_X86("libs/32/libhidapi-jni.jnilib");

        private final String libPath;

        Platform(String libPath) {
            this.libPath = libPath;
        }

        public static Platform getNative() {
            PortaUSBHID.Arch arch = PortaUSBHID.Arch.getNative();
            String osName = System.getProperty("os.name");
            if (osName.equals("Linux")) {
                switch (arch) {
                    case null:
                        return LINUX_X64;
                    case X86:
                        return LINUX_X86;
                    case AMD64:
                        break;
                }
            } else if (osName.startsWith("Win")) {
                switch (arch) {
                    case null:
                        return WINDOWS_X64;
                    case X86:
                        return WINDOWS_X86;
                    case AMD64:
                        break;
                }
            } else if (osName.equals("Mac OS X") || osName.equals("Darwin")) {
                switch (arch) {
                    case null:
                        return OSX_X64;
                    case X86:
                        return OSX_X86;
                    case AMD64:
                        break;
                }
            }
            throw new UnsupportedOperationException(String.format("Unsupported os.name %s (os.arch: %s)", new Object[] { osName, System.getProperty("os.arch") }));
        }

        private static void init() {
            Platform platform = getNative();
            try {
                String libPath = String.valueOf(System.getProperty("user.dir")) + File.separator + platform.libPath;
                System.load(libPath);
                PortaUSBHID.LOGGER.debug("Lib carregada: [" + libPath + "]");
            } catch (UnsatisfiedLinkError e) {
                PortaUSBHID.LOGGER.error("LoadLibrary unsatisfiedLinkError: [" + e.getMessage() + "]");
            } catch (Exception e) {
                PortaUSBHID.LOGGER.error("LoadLibrary exception: [" + e.getMessage() + "] Lib: [" + platform.libPath + "]");
            }
        }
    }

    private static final Logger LOGGER = Logger.getLogger(PortaUSBHID.class);

    private static final long READ_UPDATE_DELAY_MS = 50L;

    private static final long BUFFER_TIMEOUT_TRIES = 50L;

    private HIDDevice device;

    static {
        Platform.init();
    }

    private Chipset nobreakIdentificado = Chipset.INVALIDO;

    private static PortaUSBHID instance;

    public static PortaUSBHID getInstance() {
        if (instance == null)
            instance = new PortaUSBHID();
        return instance;
    }

    public Boolean isClosed() {
        return (!Chipset.INVALIDO.equals(this.nobreakIdentificado) && this.device != null) ? Boolean.valueOf(false) : Boolean.valueOf(true);
    }

    public Chipset getChipset() {
        if (isClosed().booleanValue())
            try {
                open();
            } catch (Throwable e) {
                LOGGER.error("Erro: [" + e.getMessage() + "]", e);
            }
        return this.nobreakIdentificado;
    }

    synchronized void closeDevice() {
        try {
            if (this.device != null) {
                LOGGER.debug("Fechando porta USB. ProductString: [" + this.device.getProductString() + "]");
                this.device.close();
            } else {
                LOGGER.debug("Fechando porta USB. [device era nulo]");
            }
            LOGGER.debug("Porta USB fechada sem erros");
        } catch (Throwable e) {
            LOGGER.error("Erro ao tentar fechar porta USB. A instancia de [device] sera decartada." + e.getMessage());
        } finally {
            setDevice(null);
        }
        this.nobreakIdentificado = Chipset.INVALIDO;
    }

    public synchronized void close() {
        closeDevice();
        try {
            HIDManager.getInstance().release();
        } catch (Throwable ex) {
            LOGGER.warn("Falha ao liberar o gerente HID. Error: [" + ex.getMessage() + "]", ex);
        }
        System.gc();
    }

    public String enviaUpsilon(int comando, int... parametros) throws Exception {
        byte[] retorno;
        String result;
        byte byte0 = 0;
        Chipset chipset = getChipset();
        if (Chipset.DAKER.equals(chipset)) {
            byte0 = 5;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bos.write(byte0);
            bos.write(preparaComando(comando, parametros));
            EscritaNobreak.getInstancia().escreverRequisicaoTradicional(bos.toByteArray());
            int bytesEscritos = this.device.sendFeatureReport(bos.toByteArray());
            LOGGER.debug("Bytes escritos: [" + bytesEscritos + "] bytes lidos: [" + bos.size() + "].");
            Thread.sleep(50L);
            byte[] arrayOfByte = lerBytesDaker();
            EscritaNobreak.getInstancia().escreverRespostaTradicional(arrayOfByte);
            return StringUtil.substringEndChar(arrayOfByte);
        }
        ByteArrayInputStream bis = new ByteArrayInputStream(preparaComando(comando, parametros));
        int off = 0, len = 8, bytesLidos = -1;
        int size = bis.available();
        if (len > size - off)
            len = size - off;
        byte[] requisicao = new byte[len];
        bytesLidos = bis.read(requisicao, 0, len);
        while (bytesLidos != -1) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bos.write(byte0);
            bos.write(requisicao);
            EscritaNobreak.getInstancia().escreverRequisicaoTradicional(bos.toByteArray());
            int bytesEscritos = this.device.write(bos.toByteArray());
            LOGGER.debug("Bytes escritos: [" + bytesEscritos + "] bytes lidos: [" + bytesLidos + "]");
            off += bytesLidos;
            if (len > size - off)
                len = size - off;
            requisicao = new byte[len];
            bytesLidos = bis.read(requisicao, 0, len);
        }
        if (Chipset.SMS.equals(chipset)) {
            retorno = lerBytes(18);
            result = StringUtil.substringEndChar(retorno);
        } else {
            retorno = lerBytesVoltronic();
            result = StringUtil.substringEndChar(retorno).replaceAll("\000", "");
        }
        EscritaNobreak.getInstancia().escreverRespostaTradicional(retorno);
        return result;
    }

    private byte[] lerBytesDaker() throws IOException {
        byte[] retorno = new byte[47];
        retorno[0] = 5;
        this.device.getFeatureReport(retorno);
        return StringUtil.substringEndChar(retorno).getBytes();
    }

    public String executaComando(ComandoNobreak beanComunicacao) throws Exception {
        if (!Chipset.SMS.equals(getChipset()) && !Chipset.INVALIDO.equals(getChipset()))
            throw new OtherChipsetFoundException("Tipo de nobreak incompativel: " + this.nobreakIdentificado, this.nobreakIdentificado);
        return executaComandoNobreak(beanComunicacao, true);
    }

    public void executaDisparo(ComandoNobreak beanComunicacao) throws Exception {
        if (!Chipset.SMS.equals(getChipset()) && !Chipset.INVALIDO.equals(getChipset()))
            throw new OtherChipsetFoundException("Tipo de nobreak incompativel: " + this.nobreakIdentificado, this.nobreakIdentificado);
        executaComandoNobreak(beanComunicacao, false);
    }

    public void executaDisparoUpsilon(int comando, int... parametros) throws Exception {
        if (!Chipset.VOLTRONIC.equals(getChipset()) && !Chipset.DAKER.equals(getChipset()) && !Chipset.INVALIDO.equals(getChipset()))
            throw new OtherChipsetFoundException("Tipo de nobreak incompativel: " + this.nobreakIdentificado, this.nobreakIdentificado);
        enviaUpsilon(comando, parametros);
    }

    public String getNome() {
        return "Porta USB HID";
    }

    public void open() throws Exception {
        try {
            closeDevice();
        } catch (Throwable ex) {
            LOGGER.error("Erro ao fechar porta antes de abrir: " + ex.getMessage(), ex);
        }
        List<Chipset> chipsets = new ArrayList<>(Arrays.asList(Chipset.values()));
        chipsets.remove(Chipset.INVALIDO);
        for (Chipset chipset : chipsets) {
            try {
                LOGGER.info("Procura por nobreak: [" + chipset.name() + "]");
                open(chipset.getVendorID(), chipset.getProductID());
                LOGGER.debug("Nobreak [" + chipset.name() + "] encontrado!!!");
                this.nobreakIdentificado = chipset;
                return;
            } catch (HIDDeviceNotFoundException hIDDeviceNotFoundException) {

            } catch (UnsatisfiedLinkError e) {
                LOGGER.error("open unsatisfiedLinkError: [" + e.getMessage() + "]");
            } catch (Exception e) {
                LOGGER.error("open exception: [" + e.getMessage() + "]");
            }
        }
        LOGGER.error("Nenhum Nobreak identificado.");
        try {
            closeDevice();
        } catch (Throwable throwable) {}
        throw new Exception("Nenhum dispositivo encontrado! Verifique se o nobreak esta plugado.");
    }

    public void open(int vendorId, int productId) throws Exception {
        LOGGER.debug("Realizando busca por dispositivo. Vendor: [" + vendorId + "] productId: [" + productId + "]");
        byte b;
        int i;
        HIDDeviceInfo[] arrayOfHIDDeviceInfo;
        for (i = (arrayOfHIDDeviceInfo = HIDManager.getInstance().listDevices()).length, b = 0; b < i; ) {
            HIDDeviceInfo hidDevice = arrayOfHIDDeviceInfo[b];
            LOGGER.debug("[prod: " + hidDevice.getProduct_id() + "\tvend: " + hidDevice.getVendor_id() + "\tprodStr: " + hidDevice.getProduct_string() + "\t]");
            if (hidDevice.getProduct_id() != productId || hidDevice.getVendor_id() != vendorId) {
                LOGGER.debug("Disp ignorado.");
            } else {
                closeDevice();
                try {
                    setDevice(HIDManager.getInstance().openById(vendorId, productId, null));
                    if (this.device == null) {
                        LOGGER.debug("Nao foi possivel habilitar a comunicacao com o nobreak. [device == null]");
                    } else {
                        this.device.disableBlocking();
                        LOGGER.debug("Porta aberta. Product: [" + this.device.getProductString() + "]");
                    }
                } catch (NullPointerException e) {
                    LOGGER.error("NullPointerException: Nao foi possivel habilitar a comunicacao com o nobreak.", e);
                    setDevice(null);
                } catch (Exception e) {
                    LOGGER.error("Exception: [" + e.getMessage() + "]", e);
                    setDevice(null);
                }
            }
            b++;
        }
        if (this.device == null)
            throw new Exception("Nao foi possivel habilitar a comunicacao com o nobreak.");
    }

    private String executaComandoNobreak(ComandoNobreak cmd, boolean pegarResposta) throws Exception {
        if (!Chipset.SMS.equals(getChipset()) && !Chipset.INVALIDO.equals(getChipset()))
            throw new OtherChipsetFoundException("Tipo de nobreak incompativel: " + this.nobreakIdentificado, this.nobreakIdentificado);
        byte[] requisicao = cmd.toByte(true);
        EscritaNobreak.getInstancia().escreverRequisicaoTradicional(requisicao);
        LOGGER.debug("Enviando comando preparar");
        this.device.sendFeatureReport(getComandoPreparar());
        Thread.sleep(2000L);
        int qtd = this.device.write(requisicao);
        LOGGER.debug("Bytes enviados: [" + qtd + "]");
        if (pegarResposta) {
            Thread.sleep(50L);
            return buildResponse();
        }
        EscritaNobreak.getInstancia().escreverRequisicaoTradicional(new int[0]);
        return "";
    }

    private String buildResponse() throws Exception {
        StringBuffer bufferLeitura = new StringBuffer();
        byte[] bytesResponse = lerBytes(18);
        LOGGER.debug("Bytes Lidos: [" + bytesResponse.length + "]");
        byte b;
        int i;
        byte[] arrayOfByte1;
        for (i = (arrayOfByte1 = bytesResponse).length, b = 0; b < i; ) {
            byte element = arrayOfByte1[b];
            try {
                bufferLeitura.append(String.valueOf(Integer.toString((element & 0xFF) + 256, 16).substring(1)) + " ");
            } catch (Exception ex) {
                LOGGER.error("Erro: " + ex.getMessage(), ex);
            }
            b++;
        }
        LOGGER.debug("Resposta Bruta - Hex: " + bufferLeitura.toString());
        return bufferLeitura.toString();
    }

    private byte[] lerBytes(int quantidade) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int contador = 0;
        int readSum = 0;
        StringBuilder sb = new StringBuilder();
        do {
            byte[] buffer = new byte[10];
            this.device.readTimeout(buffer, 100);
            byte sizebuf = (byte)(buffer[0] & 0xF);
            readSum += sizebuf;
            if (sizebuf > 0) {
                bos.write(buffer, 1, sizebuf);
                for (int i = 1; i <= sizebuf; i++) {
                    sb.append((char)buffer[i]);
                    sb.append(" ");
                }
                contador = 0;
            } else {
                contador++;
            }
        } while (readSum < quantidade && contador < 1000);
        LOGGER.debug("Retornando [" + readSum + "] bytes lidos.");
        LOGGER.info("Bytes em CHAR [" + sb.toString() + "]");
        return bos.toByteArray();
    }

    private byte[] lerBytesVoltronic() throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int contador = 0;
        int readSum = 0;
        boolean fimLeitura = false;
        do {
            byte[] buffer = new byte[8];
            int read = this.device.read(buffer);
            readSum += read;
            if (read > 0) {
                for (int index = 0; index < read && !fimLeitura; index++) {
                    fimLeitura = (buffer[index] == 13);
                    bos.write(buffer[index]);
                }
                contador = 0;
            } else if (++contador < 50L) {
                try {
                    Thread.sleep(50L);
                } catch (InterruptedException interruptedException) {}
            }
        } while (!fimLeitura && contador < 50L);
        LOGGER.debug("Retornando [" + readSum + "] bytes lidos");
        return bos.toByteArray();
    }

    private static byte[] getComandoPreparar() throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bos.write(0);
        bos.write(96);
        bos.write(9);
        bos.write(0);
        bos.write(0);
        bos.write(3);
        return bos.toByteArray();
    }

    private byte[] preparaComando(int comando, int... parametros) {
        byte[] requisicao = new byte[2 + parametros.length];
        requisicao[0] = (byte)comando;
        int param = 0;
        boolean contemFim = false;
        for (; param < parametros.length; param++) {
            requisicao[param + 1] = (byte)parametros[param];
            if (parametros[param] == 13)
                contemFim = true;
        }
        if (!contemFim) {
            param++;
            requisicao[param] = 13;
        }
        if (Chipset.DAKER.equals(getChipset())) {
            byte[] req = new byte[47];
            req[0] = 0;
            req[1] = (byte)(param + 1);
            int x = 0;
            for (; x < requisicao.length; x++)
                req[x + 2] = requisicao[x];
            return req;
        }
        return requisicao;
    }

    public DumpLogBean executaComandoDumLog(ComandoNobreak beanComunicacao) throws Exception {
        return null;
    }

    private void setDevice(HIDDevice device) {
        LOGGER.debug("device [" + ((this.device != null) ? this.device.toString() : "null") +
                "] => [" + ((device != null) ? device.toString() : "null") + "]");
        this.device = device;
    }
}
