//
// Source code recreated from a .class file by Vineflower
//

package br.com.alerta24h.comunicacao.bean;

import java.io.ByteArrayOutputStream;

public class ComandoNobreak extends Bean {
    private static final long serialVersionUID = 1L;
    private int comando;
    private int param1 = -1;
    private int param2 = -1;
    private int param3 = -1;
    private int param4 = -1;
    private int check;
    private String caracterInicial;
    private int tamanhoResposta;

    public ComandoNobreak() {
    }

    public String toString() {
        return "Comando: ("
                + this.comando
                + " / "
                + (char)this.comando
                + ") parametros: ("
                + this.param1
                + ","
                + this.param2
                + ","
                + this.param3
                + ","
                + this.param4
                + ") check: ("
                + this.check
                + ")";
    }

    public int getComando() {
        return this.comando;
    }

    public int getParam1() {
        return this.param1;
    }

    public int getParam2() {
        return this.param2;
    }

    public int getParam3() {
        return this.param3;
    }

    public int getParam4() {
        return this.param4;
    }

    public int getCheck() {
        return this.check;
    }

    public void setComando(int aComando) {
        this.comando = aComando;
    }

    public void setParam1(int aParam1) {
        this.param1 = aParam1;
    }

    public void setParam2(int aParam2) {
        this.param2 = aParam2;
    }

    public void setParam3(int aParam3) {
        this.param3 = aParam3;
    }

    public void setParam4(int aParam4) {
        this.param4 = aParam4;
    }

    public void setCheck(int aCheck) {
        this.check = aCheck;
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

    public ComandoNobreak copy() {
        return this;
    }

    public byte[] toByte(boolean reportId) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        if (reportId) {
            bos.write(0);
            bos.write(23);
        }

        bos.write(this.getComando());
        bos.write(this.getParam1());
        bos.write(this.getParam2());
        bos.write(this.getParam3());
        bos.write(this.getParam4());
        bos.write(this.getCheck());
        bos.write(13);
        return bos.toByteArray();
    }
}
