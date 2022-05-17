package SUSTech.CS307_project2.APIs.javaBean;

public class Model {
    int id;
    String number, model_name, name;
    String unit_price;

    public Model(String[] row) {
        this.id = Integer.parseInt(row[0]);
        this.number = row[1];
        this.model_name = row[2];
        this.name = row[3];
        this.unit_price = row[4];
    }

    public Model() {
    }

    public int getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public String getModel() {
        return model_name;
    }

    public String getName() {
        return name;
    }

    public String getUnit_price() {
        return unit_price;
    }
}