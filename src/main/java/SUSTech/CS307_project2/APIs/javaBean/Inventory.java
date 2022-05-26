package SUSTech.CS307_project2.APIs.javaBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Inventory {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    int id;
    String supply_center;
    String product_model;
    String supply_staff;
    Date date;
    int purchase_price, quantity;

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public Inventory(String[] part) {
        this.id = Integer.parseInt(part[0]);
        this.supply_center = part[1];
        this.product_model = part[2];
        this.supply_staff = part[3];
        try {
            this.date = sdf.parse(part[4]);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.purchase_price = Integer.parseInt(part[5]);
        this.quantity = Integer.parseInt(part[6]);
    }
    public Inventory() {
    }

    public void setProduct_model(String product_model) {
        this.product_model = product_model;
    }

    public int getId() {
        return id;
    }

    public String getSupply_center() {
        return supply_center;
    }

    public String getProduct_model() {
        return product_model;
    }

    public String getSupply_staff() {
        return supply_staff;
    }

    public Date getDate() {
        return date;
    }

    public int getPurchase_price() {
        return purchase_price;
    }

    public int getQuantity() {
        return quantity;
    }

}