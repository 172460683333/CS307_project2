package SUSTech.CS307_project2.APIs.javaBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ContractInfo {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    String contract_num;
    String enterprise, product_model;
    int quantity;
    String contract_manager;
    Date contract_date, estimated_delivery_date, lodgement_date;
    String salesman_num, contract_type;

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "ContractInfo{" +
                "contract_num='" + contract_num + '\'' +
                ", enterprise='" + enterprise + '\'' +
                ", product_model='" + product_model + '\'' +
                ", quantity=" + quantity +
                ", salesman_num='" + salesman_num + '\'' +
                '}';
    }

    public String getContract_num() {
        return contract_num;
    }

    public String getEnterprise() {
        return enterprise;
    }

    public String getProduct_model() {
        return product_model;
    }

    public ContractInfo() {
    }

    public int getQuantity() {
        return quantity;
    }

    public String getContract_manager() {
        return contract_manager;
    }

    public Date getContract_date() {
        return contract_date;
    }

    public Date getEstimated_delivery_date() {
        return estimated_delivery_date;
    }

    public Date getLodgement_date() {
        return lodgement_date;
    }

    public String getSalesman_num() {
        return salesman_num;
    }

    public String getContract_type() {
        return contract_type;
    }

    public ContractInfo(String[] part) {
        this.contract_num = part[0];
        this.enterprise = part[1];
        this.product_model = part[2];
        this.quantity = Integer.parseInt(part[3]);
        this.contract_manager = part[4];
        try {
            this.contract_date = sdf.parse(part[5]);
            this.estimated_delivery_date = sdf.parse(part[6]);
            this.lodgement_date = sdf.parse(part[7]);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.salesman_num = part[8];
        this.contract_type = part[9];
    }
}
