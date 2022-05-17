package SUSTech.CS307_project2.APIs.javaBean;

public class Staff {
        int id;
        String name, age, gender;
        String number;
        String supply_center, mobile_number, type;

        public Staff(){};

        public Staff(String[] row) {
            this.id = Integer.parseInt(row[0]);
            this.name = row[1];
            this.age = row[2];
            this.gender = row[3];
            this.number = row[4];
            this.supply_center = row[5];
            this.mobile_number = row[6];
            this.type = row[7];
        }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public String getNumber() {
        return number;
    }

    public String getSupply_center() {
        return supply_center;
    }

    public String getMobile_number() {
        return mobile_number;
    }

    public String getType() {
        return type;
    }
}