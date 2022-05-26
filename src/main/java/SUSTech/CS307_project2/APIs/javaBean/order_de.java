package SUSTech.CS307_project2.APIs.javaBean;

public class order_de {
    String contract;
    String salesman;
    int seq;

    public String getContract() {
        return contract;
    }

    public String getSalesman() {
        return salesman;
    }

    public int getSeq() {
        return seq;
    }

    public order_de(String[]parts) {
        this.contract = parts[0];
        this.salesman = parts[1];
        this.seq = Integer.parseInt(parts[2]);
    }
}
