package SUSTech.CS307_project2.APIs.javaBean;


public class Center {
        int id;
        String city;

    public int getId() {
        return id;
    }

    public String getCity() {
        return city;
    }

    public Center() {
    }

    public Center(String[] row) {
            this.id = Integer.parseInt(row[0]);
            this.city = row[1];

        }

    }