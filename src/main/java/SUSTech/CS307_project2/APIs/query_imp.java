package SUSTech.CS307_project2.APIs;

import java.sql.*;

public class query_imp implements query{

    @Override//6
    public String getAllStaffCount() {
        String ans = "Q6\n";
        try{
            Connection conn = JDBCUtils.getConn();
            String sql = "select type, count(type) from staff group by type";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while(rs.next()){
                String type = rs.getString("type");
                int count = rs.getInt("count");
                ans = ans + String.format("%-30s",type) + count + "\n";
            }

            rs.close();
            stmt.close();
            conn.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return ans;
    }

    @Override//7
    public String getContractCount() {
        String ans = "Q7 ";
        try{
            Connection conn = JDBCUtils.getConn();
            String sql = "select count(distinct contract_num) from order_list";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            rs.next();
            int count = rs.getInt("count");
            ans = ans + count;

            rs.close();
            stmt.close();
            conn.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return ans;
    }

    @Override//8
    public String getOrderCount() {
        String ans = "Q8 ";
        try{
            Connection conn = JDBCUtils.getConn();
            String sql = "select count(*) from order_list";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            rs.next();
            int count = rs.getInt("count");
            ans = ans + count;

            rs.close();
            stmt.close();
            conn.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return ans;
    }

    @Override//9
    public String getNeverSoldProductCount() {
        String ans = "Q9 ";
        try{
            Connection conn = JDBCUtils.getConn();
            String sql = "select count(*) from inventories where quantity > 0\n" +
                    "and product_model not in (select distinct order_list.product_model from order_list);";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            rs.next();
            int count = rs.getInt("count");
            ans = ans + count;

            rs.close();
            stmt.close();
            conn.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return ans;
    }

    @Override//10
    public String getFavoriteProductModel() {
        String ans = "Q10\n";
        try{
            Connection conn = JDBCUtils.getConn();
            String sql = "select product_model, quantity from order_list\n" +
                    "where quantity = (select max(quantity) from order_list)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while(rs.next()){
                String name = rs.getString("product_model");
                int quantity = rs.getInt("quantity");
                ans = ans + String.format("%-30s",name) + quantity + "\n";
            }

            rs.close();
            stmt.close();
            conn.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return ans;
    }

    @Override//11
    public String getAvgStockByCenter() {
        String ans = "Q11\n";
        try{
            Connection conn = JDBCUtils.getConn();
            String sql = "select supply_center, round(avg(quantity),1) from inventories group by supply_center order by supply_center";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while(rs.next()){
                String supply_center = rs.getString("supply_center");
                double average = rs.getInt("round");
                ans = ans + String.format("%-50s",supply_center) + average + "\n";
            }

            rs.close();
            stmt.close();
            conn.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return ans;
    }

    @Override//12
    public String getProductByNumber(String product_number) {
        String ans = "Q12\n";
        try{
            Connection conn = JDBCUtils.getConn();
            String sql = "select supply_center, product_model, purchase_price, quantity from inventories\n" +
                    "join (select model_name from model where number = '" + product_number + "') x\n" +
                    "on inventories.product_model = x.model_name;";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while(rs.next()){
                String supply_center = rs.getString("supply_center");
                String product_model = rs.getString("product_model");
                int purchase_price = rs.getInt("purchase_price");
                int quantity = rs.getInt("quantity");
                ans = ans + String.format("%-30s",supply_center) + String.format("%-30s",product_model)
                        + String.format("%-30d",purchase_price) + quantity + "\n";
            }

            rs.close();
            stmt.close();
            conn.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return ans;
    }

    @Override//13
    public String getContractInfo(String contract_number) {
        String ans = "Q13\n";
        try{
            Connection conn = JDBCUtils.getConn();
            String sql = "select * from\n" +
                    "(select * from order_list join staff on order_list.salesman_num = staff.number) x\n" +
                    "join model on x.product_model = model.model_name where contract_num = '"+contract_number+"'";

            //number + manager
            PreparedStatement stmt0 = conn.prepareStatement("select distinct name from order_list\n" +
                    "join staff on order_list.contract_manager = staff.number\n" +
                    "where contract_num = '"+contract_number+"'");
            ResultSet rs0 = stmt0.executeQuery();
            rs0.next();
            ans = ans + "number: " + contract_number + "\n"
                      +"manager:" + rs0.getString("name") + "\n";
            rs0.close();
            stmt0.close();

            //enterprise
            PreparedStatement stmt1 = conn.prepareStatement("select enterprise from order_list where contract_num = '"+contract_number+"'");
            ResultSet rs1 = stmt1.executeQuery();
            ans = ans + "enterprise: ";
            rs1.next();
            ans = ans + rs1.getString("enterprise")+"\n";
            rs1.close();

            //supply_center
            PreparedStatement stmt2 = conn.prepareStatement("select distinct supply_center from order_list\n" +
                    "join staff on order_list.salesman_num = staff.number\n" +
                    "where contract_num ='"+contract_number+"';");
            ResultSet rs2 = stmt2.executeQuery();
            rs2.next();
            ans = ans + "supply_center: "+ rs2.getString("supply_center")+ "\n";
            rs2.close();

            //information
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                String product_model = rs.getString("product_model");
                String salesman = rs.getString("name");
                int quantity = rs.getInt("quantity");
                int unit_price = rs.getInt("unit_price");
                String estimate_delivery_date = rs.getDate("estimated_delivery_date").toString();
                String lodgement_date = rs.getDate("lodgement_date").toString();
                ans = ans + String.format("%-30s",product_model) + String.format("%-30s",salesman)
                        + String.format("%-30d",quantity) + String.format("%-30d",unit_price)
                        + estimate_delivery_date + "           " + lodgement_date + "\n";
            }
            rs.close();
            stmt.close();
            conn.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return ans;
    }

    public static void main(String[] args) {
        query_imp qi = new query_imp();
//        System.out.println(qi.getAllStaffCount());//6
//        System.out.println(qi.getContractCount());//7
//        System.out.println(qi.getOrderCount());//8
//        System.out.println(qi.getNeverSoldProductCount());//9
//        System.out.println(qi.getFavoriteProductModel());//10
//        System.out.println(qi.getAvgStockByCenter());//11
//        System.out.println(qi.getProductByNumber("E7R6098"));//12
        System.out.println(qi.getContractInfo("CSE0000219"));//13
    }
}