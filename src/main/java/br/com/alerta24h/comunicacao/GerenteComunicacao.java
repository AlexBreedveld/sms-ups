//
// Source code recreated from a .class file by Vineflower
//

package br.com.alerta24h.comunicacao;

import br.com.alerta24h.comunicacao.bean.ComandoNobreak;
import br.com.alerta24h.comunicacao.bean.ComandoNobreakSR;
import br.com.alerta24h.comunicacao.bean.DumpLogBean;
import br.com.alerta24h.comunicacao.serial.UtilSerial;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;

public class GerenteComunicacao {
    private static final Logger LOGGER = Logger.getLogger(GerenteComunicacao.class);
    protected PortaComunicacao portaComm = null;

    public GerenteComunicacao() {
    }

    private boolean validarRespostaUpsilon(int tamanhoResposta, String caracterInicial, String resposta) {
        if (resposta == null || resposta.isEmpty()) {
            LOGGER.debug("LENTH DA RESPOSTA: <a resposta veio em branco>");
            return false;
        } else if (tamanhoResposta != 0 && caracterInicial != null) {
            LOGGER.debug(
                    "LENTH DA RESPOSTA: ["
                            + resposta.length()
                            + "] ESPERADO: ["
                            + tamanhoResposta
                            + "], iniciando com ["
                            + caracterInicial
                            + "] RESPOSTA: ["
                            + resposta
                            + "]"
            );
            return resposta.startsWith(caracterInicial) && resposta.length() == tamanhoResposta;
        } else {
            LOGGER.debug("<parâmetros de checagem vazios> tamanhoResposta: [" + tamanhoResposta + "], caracterInicial: [" + caracterInicial + "]");
            return true;
        }
    }

    protected String getResposta(PortaComunicacao porta, ComandoNobreak beanComunicacao) throws Exception {
        String respComunicacao = porta.executaComando(beanComunicacao);
        LOGGER.debug("Resposta: [" + respComunicacao + "]");
        return respComunicacao;
    }

    protected List<PortaComunicacao> getListaPortas() {
        List<PortaComunicacao> ports = FactoryPortaSerials.getListaPortas();
        ports.add(PortaUSBHID.getInstance());
        return ports;
    }

    public Chipset getChipset() {
        for (PortaComunicacao pcomm : this.getListaPortas()) {
            if (pcomm instanceof PortaUSBHID) {
                return pcomm.getChipset();
            }
        }

        return Chipset.INVALIDO;
    }

    private String configuraPorta(ComandoNobreak beanComunicacao) {
        for (PortaComunicacao pcomm : this.getListaPortas()) {
            LOGGER.debug("Configurar porta: [" + pcomm.getNome() + "]");

            try {
                pcomm.open();
                String resposta = this.getResposta(pcomm, beanComunicacao);
                LOGGER.debug("Resposta: [" + resposta + "]");
                if (resposta != null) {
                    this.portaComm = pcomm;
                    return resposta;
                }

                LOGGER.debug("Resposta foi NULL para porta [" + pcomm.getNome() + "], tenta próxima porta");
                this.fecharPortaComunicacao(pcomm);
            } catch (Exception var6) {
                LOGGER.error("Erro: [" + var6.getMessage() + "]", var6);
                this.fecharPortaComunicacao(pcomm);
            }
        }

        return null;
    }

    private String configuraPortaUpsilon(String caracterInicial, int tamanhoResposta, int comando, int... parametros) {
        for (PortaComunicacao pcomm : this.getListaPortas()) {
            LOGGER.debug("Configurando porta Upsilon: [" + pcomm.getNome() + "].");

            try {
                LOGGER.debug("Tentando abrir porta ");
                pcomm.open();
                String resposta = this.getRespostaUpsilon(pcomm, comando, parametros);
                resposta = UtilSerial.limpaInicioResposta(resposta);
                if (this.validarRespostaUpsilon(tamanhoResposta, caracterInicial, resposta)) {
                    LOGGER.debug("resposta: [" + resposta + "]");
                    this.portaComm = pcomm;
                    return resposta;
                }

                this.fecharPortaComunicacao(pcomm);
            } catch (Exception var9) {
                LOGGER.error("Porta:[" + pcomm.getNome() + "] Error: [" + var9.getMessage() + "]", var9);
                this.fecharPortaComunicacao(pcomm);
            }
        }

        return null;
    }

    public synchronized void executaDisparo(ComandoNobreak beanComunicacao) throws Exception {
        this.portaComm.executaDisparo(beanComunicacao);
    }

    public synchronized void executaDisparoUpsilon(ComandoNobreak beanComunicacao) throws Exception {
        LOGGER.debug(beanComunicacao.toString());
        this.executaDisparoUpsilon(
                beanComunicacao.getComando(), beanComunicacao.getParam1(), beanComunicacao.getParam2(), beanComunicacao.getParam3(), beanComunicacao.getParam4()
        );
    }

    public synchronized void executaDisparoUpsilon(ComandoNobreakSR beanComunicacao) throws Exception {
        LOGGER.debug(beanComunicacao.toString());
        List<Integer> params = beanComunicacao.getParametros();
        this.executaDisparoUpsilon(beanComunicacao.getComando(), ArrayUtils.toPrimitive(params.toArray(new Integer[params.size()])));
    }

    private void executaDisparoUpsilon(int comando, int... parametros) throws Exception {
        parametros = this.getArrayValidoUpsilon(parametros);
        this.portaComm.enviaUpsilon(comando, parametros);
    }

    public synchronized String executaComando(ComandoNobreak beanComunicacao) throws Exception {
        try {
            return this.executaCmd(beanComunicacao);
        } catch (Throwable var3) {
            return null;
        }
    }

    private String executaCmd(ComandoNobreak beanComunicacao) throws Throwable {
        if (this.portaComm != null && !this.portaComm.isClosed()) {
            try {
                LOGGER.debug("Porta nao era nula! utilizando porta existente. Chipset: [" + this.portaComm.getChipset().name() + "]");
                String resposta = this.getResposta(this.portaComm, beanComunicacao);
                LOGGER.debug("resposta: [" + resposta + "]");
                if (resposta == null) {
                    throw new Exception("Resposta nula ao comunicar com porta: [" + this.portaComm + "]");
                } else {
                    return resposta;
                }
            } catch (Exception var3) {
                LOGGER.error("erro: " + var3.getMessage(), var3);
                this.fecharPortaComunicacao(this.portaComm);
                throw new Exception("Falha ao comunicar com porta: [" + this.portaComm + "]");
            }
        } else {
            LOGGER.debug("Iniciando porta!");
            String resposta = this.configuraPorta(beanComunicacao);
            LOGGER.debug("resposta: [" + resposta + "]");
            return resposta;
        }
    }

    public synchronized void fecharPortaComunicacao() {
        this.fecharPortaComunicacao(this.portaComm);
    }

    private void fecharPortaComunicacao(PortaComunicacao porta) {
        try {
            if (porta != null) {
                LOGGER.debug("Fechando porta de comunicacao: [" + porta.getNome() + "]");
                porta.close();
            }
        } catch (Exception var6) {
            LOGGER.error("Erro ao fechar porta de comunicacao: [" + porta.getNome() + "]");
        } finally {
            PortaComunicacao var8 = null;
        }
    }

    public synchronized String executaComandoUpsilon(ComandoNobreak beanComunicacao) {
        LOGGER.debug(beanComunicacao.toString());
        return this.executaComandoUpsilon(
                beanComunicacao.getCaracterInicial(),
                beanComunicacao.getTamanhoResposta(),
                beanComunicacao.getComando(),
                beanComunicacao.getParam1(),
                beanComunicacao.getParam2(),
                beanComunicacao.getParam3(),
                beanComunicacao.getParam4()
        );
    }

    private int[] getArrayValidoUpsilon(int... parametros) {
        LOGGER.debug(Arrays.toString(parametros));
        int tamanho = 0;

        for (int i : parametros) {
            if (i == -1) {
                return Arrays.copyOf(parametros, tamanho);
            }

            tamanho++;
        }

        int[] resposta = Arrays.copyOf(parametros, tamanho);
        LOGGER.debug(Arrays.toString(resposta));
        return resposta;
    }

    protected String getRespostaUpsilon(PortaComunicacao porta, int comando, int... parametros) throws Exception {
        return porta.enviaUpsilon(comando, parametros);
    }

    private String executaComandoUpsilon(String caracterInicial, int tamanhoResposta, int comando, int... parametros) {
        parametros = this.getArrayValidoUpsilon(parametros);
        if (this.portaComm != null && !this.portaComm.isClosed()) {
            try {
                LOGGER.debug("porta: [" + this.portaComm + "]");
                String resposta = this.getRespostaUpsilon(this.portaComm, comando, parametros);
                LOGGER.debug("resposta: [" + resposta + "]");
                if (resposta == null) {
                    throw new Exception("Resposta nula ao comunicar com porta: [" + this.portaComm + "]");
                } else {
                    return resposta;
                }
            } catch (Exception var6) {
                LOGGER.error("erro! fechando portas: [" + var6.getMessage() + "]", var6);
                this.fecharPortaComunicacao(this.portaComm);
                return null;
            }
        } else {
            String resposta = this.configuraPortaUpsilon(caracterInicial, tamanhoResposta, comando, parametros);
            LOGGER.debug("resposta: [" + resposta + "]");
            return resposta;
        }
    }

    public synchronized String executaComandoUpsilon(ComandoNobreakSR beanComunicacao) {
        List<Integer> params = beanComunicacao.getParametros();
        return this.executaComandoUpsilon(
                beanComunicacao.getCaracterInicial(),
                beanComunicacao.getTamanhoResposta(),
                beanComunicacao.getComando(),
                ArrayUtils.toPrimitive(params.toArray(new Integer[params.size()]))
        );
    }

    public synchronized DumpLogBean executaComandoDumpLog(ComandoNobreak beanComunicacao) throws Exception {
        try {
            return this.portaComm.executaComandoDumLog(beanComunicacao);
        } catch (Exception var3) {
            LOGGER.error("erro! fechando portas: [" + var3.getMessage() + "]", var3);
            this.fecharPortaComunicacao(this.portaComm);
            return null;
        }
    }
}
