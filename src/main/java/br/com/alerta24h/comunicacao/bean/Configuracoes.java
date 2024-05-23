//
// Source code recreated from a .class file by Vineflower
//

package br.com.alerta24h.comunicacao.bean;

public class Configuracoes extends Bean {
    public static final int BITS_PER_SECOND = 2400;
    public static final int DATA_BITS = 8;
    public static final int STOP_BITS = 1;
    public static final int PARITY = 0;
    public static final int SERIAL_PORT_WAIT_TIME = 600;

    public Configuracoes() {
    }

    public Bean copy() {
        return this;
    }
}
