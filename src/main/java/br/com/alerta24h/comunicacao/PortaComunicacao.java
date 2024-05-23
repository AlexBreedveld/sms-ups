//
// Source code recreated from a .class file by Vineflower
//

package br.com.alerta24h.comunicacao;

import br.com.alerta24h.comunicacao.bean.ComandoNobreak;
import br.com.alerta24h.comunicacao.bean.DumpLogBean;

public abstract class PortaComunicacao {
    public PortaComunicacao() {
    }

    public abstract void open() throws Exception;

    public abstract void open(int var1, int var2) throws Exception;

    public abstract void close();

    public abstract void executaDisparo(ComandoNobreak var1) throws Exception;

    public abstract void executaDisparoUpsilon(int var1, int... var2) throws Exception;

    public abstract String executaComando(ComandoNobreak var1) throws Exception;

    public abstract DumpLogBean executaComandoDumLog(ComandoNobreak var1) throws Exception;

    public abstract String enviaUpsilon(int var1, int... var2) throws Exception;

    public abstract String getNome();

    public Chipset getChipset() {
        return Chipset.INVALIDO;
    }

    public abstract Boolean isClosed();
}
