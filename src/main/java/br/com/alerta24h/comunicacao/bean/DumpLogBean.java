//
// Source code recreated from a .class file by Vineflower
//

package br.com.alerta24h.comunicacao.bean;

public class DumpLogBean extends Bean {
    private static final long serialVersionUID = 1L;
    private String data;
    private String numeroEvento;
    private String descricao;

    public DumpLogBean() {
    }

    public String getData() {
        return this.data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDescricao() {
        return this.descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getNumeroEvento() {
        return this.numeroEvento;
    }

    public void setNumeroEvento(String numeroEvento) {
        this.numeroEvento = numeroEvento;
    }

    public String toString() {
        return "Data   : " + this.data + "\nNumero : " + this.numeroEvento + "\nEvento : " + this.descricao;
    }

    public Bean copy() {
        return this;
    }
}
