package SUSTech.CS307_project2.APIs.javaBean;

public class order_up {
    String contract, product_model, salesman;
    int quantity;
    String estimate_delivery_date, lodgement_date;

    public order_up(String[] parts) {
        this.contract = parts[0];
        this.product_model = parts[1];
        this.salesman = parts[2];
        this.quantity = Integer.parseInt(parts[3]);
        this.estimate_delivery_date = parts[4];
        this.lodgement_date = parts[5];
    }

    public String getContract() {
        return contract;
    }

    public String getProduct_model() {
        return product_model;
    }

    public String getSalesman() {
        return salesman;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getEstimate_delivery_date() {
        return estimate_delivery_date;
    }

    public String getLodgement_date() {
        return lodgement_date;
    }
}
