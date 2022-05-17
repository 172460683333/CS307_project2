package SUSTech.CS307_project2.APIs.javaBean;

public class Enterprise {
        int id;
        String name, country, city, supply_center, industry;

    public Enterprise() {
    }

    public Enterprise(String[] row) {
            this.id = Integer.parseInt(row[0]);
            this.name = row[1];
            this.country = row[2];
            this.city = row[3];
            this.supply_center = row[4];
            this.industry = row[5];

        }
    }