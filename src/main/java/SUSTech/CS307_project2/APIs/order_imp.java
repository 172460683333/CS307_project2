package SUSTech.CS307_project2.APIs;

import SUSTech.CS307_project2.APIs.javaBean.ContractInfo;
import SUSTech.CS307_project2.APIs.javaBean.Model;
import SUSTech.CS307_project2.APIs.javaBean.Staff;
import SUSTech.CS307_project2.APIs.javaBean.Inventory;

import java.io.*;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class order_imp implements order {

    private static List<String> paths_order;
    private static List<Inventory> inventories;
    private static List<ContractInfo> orderList;
    private static List<Staff> staff;
    private static List<Model> model;
    private String Path;
    private BufferedReader bf;
    private File file;
    private String[] parts = {};

    static {
        paths_order = new ArrayList<>();
        inventories = new ArrayList<>();
        orderList = new ArrayList<>();
        paths_order.add("src/main/resources/release-to-students/release-testcase1/task1_in_stoke_test_data_publish.csv");
        paths_order.add("src/main/resources/release-to-students/release-testcase1/task2_test_data_publish.csv");
        paths_order.add("src/main/resources/release-to-students/release-testcase1/task34_delete_test_data_publish.tsv");
        paths_order.add("src/main/resources/release-to-students/release-testcase1/task34_update_test_data_publish.tsv");
        staff = JDBCUtils.query("select * from staff", Staff.class);
        model = JDBCUtils.query("select * from model", Model.class);
    }

    private void setPath(String path, int index) {
        if (path == null) {
            Path = paths_order.get(index);
        } else {
            Path = path;
        }
        file = new File(Path);
        try {
            bf = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        parts = new String[]{};
    }

    private String[] readline(String regex) {
        try {
            String line = bf.readLine();
            String[] lines = null;
            if (line != null) {
                line = line.replace(", ", "$");
                line = line.replace("-", "/");
                if (line.contains("quantity") || line.contains("contract")) {
                    lines = readline(regex);
                } else {
                    lines = line.split(regex);
                }
                for (int i = 0; i < lines.length; i++) {
                    if (i==2){
                        lines[2]=lines[2].replace("/","-");
                    }
                    lines[i] = lines[i].replace("$", ", ");
                }
                return lines;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void stockIn(String path) {
        OriginData.importOriData();
        setPath(path, 0);
        while (parts != null) {
            if (parts.length != 0) {
//                for (String part : parts) {
//                    System.out.print(part + " | ");
//                }
//                System.out.println();
                inventories.add(new Inventory(parts));
            }
            parts = readline(",");
        }
        Map<String, String> number_center = staff.stream().collect(Collectors.toMap(Staff::getNumber, Staff::getSupply_center));
        Map<String, Model> models = model.stream().collect(Collectors.toMap(Model::getModel, m -> m));
        Map<String, String> number_type = staff.stream().collect(Collectors.toMap(Staff::getNumber, Staff::getType));
        //1. 供应中⼼与⼈员所在的供应中⼼对应不上 -> filter1
        //2. ⼈员的类型不是“supply_staff" -> filter2
        //3. 供应中⼼不存在 -> filter3
        //4. 产品不存在 -> filter4 比较model
        //5. ⼈员不存在 -> 同2
        //171,Europe,LaserPrinterC5,11111111,2008/12/11,538,699
        inventories = inventories.stream()
                .filter(e -> number_center.get(e.getSupply_staff()) != null)
                .filter(e -> number_type.get(e.getSupply_staff()).equals("Supply Staff"))
                .filter(e -> number_center.containsValue(e.getSupply_center()))
                .filter(e -> models.containsKey(e.getProduct_model()))
                .collect(Collectors.toList());
        try {
            PreparedStatement stmt = JDBCUtils.getConn().prepareStatement("insert into inventories " + "values(?,?,?,?,?,?,?)");
            //         int id;
            //        String supply_center, product_model, supply_staff;
            //        Date date;
            //        int purchase_price, quantity;
            System.out.println(inventories.size());
            for (Inventory inventory : inventories) {
                stmt.setInt(1, inventory.getId());
                stmt.setString(2, inventory.getSupply_center());
                stmt.setString(3, inventory.getProduct_model());
                stmt.setString(4, inventory.getSupply_staff());
                stmt.setDate(5, new Date(inventory.getDate().getTime()));
                stmt.setInt(6, inventory.getPurchase_price());
                stmt.setInt(7, inventory.getQuantity());
                System.out.println(stmt);
                stmt.execute();
            }
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void placeOrder(String path) {
        try {
            Connection conn = JDBCUtils.getConn();
            PreparedStatement stmt;
            stmt=conn.prepareStatement("drop table if exists order_list cascade ;create table if not exists order_list(\n" +
                    "      contract_num varchar(88) ,\n" +
                    "enterprise varchar(88),product_model varchar(88) ,\n" +
                    "    quantity int,contract_manager varchar(88),\n" +
                    "  contract_date Date ,estimated_delivery_date Date , lodgement_date Date ,\n" +
                    "    salesman_num varchar(88),contract_type varchar(88)\n" +
                    ");\n");
            stmt.execute();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        setPath(path, 1);
        while (parts != null) {
            if (parts.length != 0) {
                orderList.add(new ContractInfo(parts));
            }
            parts = readline(",");
        }
        Map<String, Long> model_quantity_order = orderList.stream().collect(Collectors.groupingBy(ContractInfo::getProduct_model, Collectors.summingLong(ContractInfo::getQuantity)));
        Map<String, Long> model_quantity_inv = inventories.stream().collect(Collectors.groupingBy(Inventory::getProduct_model, Collectors.summingLong(Inventory::getQuantity)));
        Map<String, String> number_type = staff.stream().collect(Collectors.toMap(Staff::getNumber, Staff::getType));

        //1. 订单中的商品数量⼤于库存数量(库存不存在)。（每个商品型号的库存在不同供应中⼼是不同的）
        //2. ⼈员类型不是“Salesman"

        orderList = orderList.stream().filter(e -> model_quantity_inv.get(e.getProduct_model())!=null
                        &&model_quantity_order.get(e.getProduct_model()) < model_quantity_inv.get(e.getProduct_model()))
                .filter(e -> number_type.get(e.getSalesman_num()).equals("Salesman"))
                .collect(Collectors.toList());
        PreparedStatement stmt = null;
        try {
            stmt = JDBCUtils.getConn().prepareStatement("insert into order_list " + "values(?,?,?,?,?,?,?,?,?,?)");
            // int contract_num;
            //   String enterprise,product_model;
            //   int quantity,contract_manager;
            //   Date contract_date,estimated_delivery_date, lodgement_date;
            //   String salesman_num,contract_type;
            for (ContractInfo contractInfo : orderList) {
                stmt.setString(1, contractInfo.getContract_num());
                stmt.setString(2, contractInfo.getEnterprise());
                stmt.setString(3, contractInfo.getProduct_model());
                stmt.setInt(4, contractInfo.getQuantity());
                stmt.setString(5, contractInfo.getContract_manager());
                stmt.setDate(6, new Date(contractInfo.getContract_date().getTime()));
                stmt.setDate(7, new Date(contractInfo.getEstimated_delivery_date().getTime()));
                stmt.setDate(8, new Date(contractInfo.getLodgement_date().getTime()));
                stmt.setString(9, contractInfo.getSalesman_num());
                stmt.setString(10, contractInfo.getContract_type());
                stmt.execute();
                System.out.println(stmt);
            }

            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateOrder(String path) {
        setPath(path, 2);
        while(parts!=null){
            parts = readline("\t");
            for (String part : parts) {
                System.out.print(part+" ");
            }
            System.out.println("");
        }

        try{
            Connection conn = JDBCUtils.getConn();
            PreparedStatement stmt = conn.prepareStatement("");
            stmt.execute();
            stmt.close();
        }catch (SQLException e){e.printStackTrace();}

    }

    @Override
    public void deleteOrder(String path) {
        setPath(path, 3);
    }

    public static void main(String[] args) {
        order_imp oi = new order_imp();
//        oi.stockIn(null);
//        oi.placeOrder(null);
        oi.updateOrder(null);
    }
}
