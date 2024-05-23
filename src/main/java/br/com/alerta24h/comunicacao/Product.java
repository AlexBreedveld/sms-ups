//
// Source code recreated from a .class file by Vineflower
//

package br.com.alerta24h.comunicacao;

public class Product {
    private int id;
    private int vendor;
    private String path;

    public Product() {
        this.id = -1;
        this.vendor = -1;
        this.path = "";
    }

    public Product(int id, int vendor, String path) {
        this.id = id;
        this.vendor = vendor;
        this.path = path;
    }

    @Override
    public String toString() {
        return "(" + this.getId() + "/" + this.getVendor() + ") path: " + this.path;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVendor() {
        return this.vendor;
    }

    public void setVendor(int vendor) {
        this.vendor = vendor;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + this.id;
        result = 31 * result + this.vendor;
        return 31 * result + (this.path == null ? "" : this.path).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (this.getClass() != obj.getClass()) {
            return false;
        } else {
            Product other = (Product)obj;
            return this.id == other.getId() && this.vendor == other.getVendor() && this.path == other.getPath();
        }
    }
}
