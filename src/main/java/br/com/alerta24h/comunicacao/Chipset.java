//
// Source code recreated from a .class file by Vineflower
//

package br.com.alerta24h.comunicacao;

public enum Chipset {
    INVALIDO(-1, -1),
    DAKER(65535, 0),
    VOLTRONIC(1637, 20833),
    SMS(1204, 21760);

    private int vendorID;
    private int productID;

    private Chipset(int vendor, int product) {
        this.vendorID = vendor;
        this.productID = product;
    }

    public int getVendorID() {
        return this.vendorID;
    }

    public int getProductID() {
        return this.productID;
    }
}
