//
// Source code recreated from a .class file by Vineflower
//

package br.com.alerta24h.comunicacao.bean;

import java.util.ArrayList;
import java.util.List;

public class ComandoNobreakSR extends Bean {
    private static final long serialVersionUID = 1L;
    private int comando;
    private List<Integer> parametros = new ArrayList<>();
    private int tamanhoResposta;
    private String caracterInicial;

    public ComandoNobreakSR() {
    }

    public int getComando() {
        return this.comando;
    }

    public void setComando(int aComando) {
        this.comando = aComando;
    }

    public List<Integer> getParametros() {
        return this.parametros;
    }

    public void setParametros(List<Integer> parametros) {
        this.parametros = parametros;
    }

    public void setParametro(int parametro) {
        this.parametros.add(parametro);
    }

    public String getCaracterInicial() {
        return this.caracterInicial;
    }

    public void setCaracterInicial(String caracterInicial) {
        this.caracterInicial = caracterInicial;
    }

    public int getTamanhoResposta() {
        return this.tamanhoResposta;
    }

    public void setTamanhoResposta(int tamanhoResposta) {
        this.tamanhoResposta = tamanhoResposta;
    }

    public Bean copy() {
        return this;
    }
}
