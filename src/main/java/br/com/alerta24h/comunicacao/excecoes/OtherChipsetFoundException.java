//
// Source code recreated from a .class file by Vineflower
//

package br.com.alerta24h.comunicacao.excecoes;

import br.com.alerta24h.comunicacao.Chipset;

public class OtherChipsetFoundException extends Exception {
    private static final long serialVersionUID = -7176691189216842885L;
    private Chipset chipset = Chipset.INVALIDO;

    public OtherChipsetFoundException() {
    }

    public OtherChipsetFoundException(Chipset c) {
        this.chipset = c;
    }

    public OtherChipsetFoundException(String message) {
        super(message);
    }

    public OtherChipsetFoundException(String message, Chipset c) {
        super(message);
        this.chipset = c;
    }

    public OtherChipsetFoundException(Throwable cause) {
        super(cause);
    }

    public OtherChipsetFoundException(Throwable cause, Chipset c) {
        super(cause);
        this.chipset = c;
    }

    public OtherChipsetFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public OtherChipsetFoundException(String message, Throwable cause, Chipset c) {
        super(message, cause);
        this.chipset = c;
    }

    public OtherChipsetFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public OtherChipsetFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Chipset c) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.chipset = c;
    }

    public Chipset getChipset() {
        return this.chipset;
    }
}
