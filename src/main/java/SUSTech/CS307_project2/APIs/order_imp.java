package SUSTech.CS307_project2.APIs;

import SUSTech.CS307_project2.APIs.javaBean.*;

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
    private static List<ContractInfo> Q7;
    private static List<Staff> staff;
    private static List<Model> model;
    private static List<Center> center;
    private String Path;
    private BufferedReader bf;
    private File file;
    private String[] parts = {};

    Map<String, String> number_center = new HashMap<>();

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
        center = JDBCUtils.query("select * from center", Center.class);
    }

    public static List<ContractInfo> getOrderList() {
        return Q7;
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
                //CSE0000327    ImageAlarm30	11112116	0	2022-04-01	2022-04-01
                if (line.contains("quantity") || line.contains("contract")) {
                    lines = readline(regex);
                } else {
                    lines = line.split(regex);
                }
                for (int i = 0; i < lines.length; i++) {
                    if (i == 2) {
                        lines[2] = lines[2].replace("/", "-");
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
                inventories.add(new Inventory(parts));
            }
            parts = readline(",");
        }
        number_center = staff.stream().collect(Collectors.toMap(Staff::getNumber, Staff::getSupply_center));
        Map<String, Model> models = model.stream().collect(Collectors.toMap(Model::getModel, m -> m));
        Map<String, String> number_type = staff.stream().collect(Collectors.toMap(Staff::getNumber, Staff::getType));
        Map<Integer, String> _center = center.stream().collect(Collectors.toMap(Center::getId, Center::getCity));
        //1. 供应中⼼与⼈员所在的供应中⼼对应不上 -> filter1
        //2. ⼈员的类型不是“supply_staff" -> filter2
        //3. 供应中⼼不存在 -> filter3 !!首先要在center的表里
        //4. 产品不存在 -> filter4 比较model
        //5. ⼈员不存在 -> 同2
        //171,Europe,LaserPrinterC5,11111111,2008/12/11,538,699
        inventories = inventories.stream()
                .filter(e -> number_center.get(e.getSupply_staff()) != null
                        && number_center.get(e.getSupply_staff()).equals(e.getSupply_center()))
                .filter(e -> number_type.get(e.getSupply_staff()).equals("Supply Staff"))
                .filter(e -> _center.containsValue(e.getSupply_center()) && number_center.containsValue(e.getSupply_center()))
                .filter(e -> models.containsKey(e.getProduct_model()))
                .collect(Collectors.toList());
//        List<Inventory> list = new ArrayList<>();
//        for (Inventory inventory : inventories) {
//            list.add(inventory);
//        }
//        inventories.clear();
//        for (Inventory inv : list) {
//            for (Staff staff1 : staff) {
//                if (staff1.getType().equals("Supply Staff")) {
//                    //center
//                    if (staff1.getSupply_center().equals(inv.getSupply_center())) { //number
//                        if (staff1.getNumber().equals(inv.getSupply_staff())) {
//                            for (Model model1 : model) {
//                                if (model1.getModel().equals(inv.getProduct_model())) {
//                                    inventories.add(inv);
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        System.out.println("inv " + inventories.size());
        try {
            PreparedStatement stmt = JDBCUtils.getConn().prepareStatement("insert into inventories " + "values(?,?,?,?,?,?,?)");
            //         int id;
            //        String supply_center, product_model, supply_staff;
            //        Date date;
            //        int purchase_price, quantity;
            for (Inventory inventory : inventories) {
                stmt.setInt(1, inventory.getId());
                stmt.setString(2, inventory.getSupply_center());
                stmt.setString(3, inventory.getProduct_model());
                stmt.setString(4, inventory.getSupply_staff());
                stmt.setDate(5, new Date(inventory.getDate().getTime()));
                stmt.setInt(6, inventory.getPurchase_price());
                stmt.setInt(7, inventory.getQuantity());
//                System.out.println(stmt);
                stmt.execute();
            }
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void placeOrder(String path) {
        setPath(path, 1);
        while (parts != null) {
            if (parts.length != 0) {
                orderList.add(new ContractInfo(parts));
            }
            parts = readline("\t");
        }

        Map<String, Long> model_quantity_order = orderList.stream().collect(Collectors.groupingBy(ContractInfo::getProduct_model, Collectors.summingLong(ContractInfo::getQuantity)));
        Map<String, Long> model_quantity_inv = inventories.stream().collect(Collectors.groupingBy(Inventory::getProduct_model, Collectors.summingLong(Inventory::getQuantity)));
        Map<String, String> number_type = staff.stream().collect(Collectors.toMap(Staff::getNumber, Staff::getType));

        //1. 订单中的商品数量⼤于库存数量(库存不存在)。（每个商品型号的库存在不同供应中⼼是不同的）
        //2. ⼈员类型不是“Salesman"
        Q7 = orderList;
        List<ContractInfo> list = new ArrayList<>();
        List<Inventory> inv_tmp = new ArrayList<>();
        for (Inventory inventory : inventories) {
            inv_tmp.add(inventory);
        }
        for (int i = 0; i < orderList.size(); i++) {
            list.add(orderList.get(i));
        }
       // System.out.println(list.size());
        orderList.clear();
        for (ContractInfo con : list) {
            for (Inventory inv : inv_tmp) {
                if (number_type.get(con.getSalesman_num()).equals("Salesman")) {
                    if (con.getProduct_model().equals(inv.getProduct_model())) {

                        if (number_center.get(con.getSalesman_num()) != null
                                && number_center.get(con.getSalesman_num()).equals(inv.getSupply_center())) {
                            if (con.getQuantity() <= inv.getQuantity()) {
                                inv.setQuantity(inv.getQuantity() - con.getQuantity());
                                orderList.add(con);
                            }
                        }
                    }
                }
            }
        }
//        orderList = orderList.stream().filter(e -> model_quantity_inv.get(e.getProduct_model()) != null
//                        && model_quantity_order.get(e.getProduct_model()) < model_quantity_inv.get(e.getProduct_model()))
//                .filter(e -> number_type.get(e.getSalesman_num()).equals("Salesman"))
//                .collect(Collectors.toList());
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
//                System.out.println(stmt);
            }
            stmt.close();

            for (ContractInfo con : orderList) {
                for (Inventory inv : inventories) {
                    if (inv.getProduct_model().equals(con.getProduct_model())
                            && inv.getSupply_center().equals(number_center.get(con.getSalesman_num()))) {
                        //库存list保留原始数据
                        //inv.setQuantity(inv.getQuantity() - con.getQuantity());
                        update_quantity(inv.getProduct_model(), inv.getSupply_center(), inv.getQuantity() - con.getQuantity());
                        break;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void update_quantity(String model, String center, int quantity) {
        try {
            String sql = " update inventories set quantity = " + quantity +
                    " where product_model = '" + model + "' and " + " supply_center = '" + center + "'";
            Connection connection = JDBCUtils.getConn();
            PreparedStatement stmt = connection.prepareStatement(sql);
            //System.out.println(stmt);
            stmt.execute();
            stmt.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void updateOrder(String path) {
        setPath(path, 3);
        List<order_up> ups = new ArrayList<>();
        try {
            while (parts != null) {
                parts = readline("\t");
                if (parts != null)
                    ups.add(new order_up(parts));
            }
            Map<String, List<ContractInfo>> id_contract = orderList.stream().collect(Collectors.groupingBy(ContractInfo::getSalesman_num));
            //1. 销售员只能更新自己的订单。
            //2. 更新订单数量的同时，库存数量也要随之改变
            //3. 如果⼀个订单更新后的数量是0， 那么这个订单要在合同中移除
            //4. 更新后，如果⼀个合同中没有订单，不要删除合同
            //更新订单时inventories不变,orderList修改,但是不删除数量为0的订单
            ups = ups.stream().filter(e -> {
                        if (id_contract.containsKey(e.getSalesman())) {
                            for (ContractInfo con : id_contract.get(e.getSalesman())) {
                                if (con.getContract_num().equals(e.getContract())) {
                                    return true;
                                }
                            }
                        }
                        return false;
                    }
            ).collect(Collectors.toList());

            for (order_up up : ups) {
                for (ContractInfo con : orderList) {
                    if (up.getSalesman().equals(con.getSalesman_num()) &&
                            up.getContract().equals(con.getContract_num())) {
                        con.setQuantity(up.getQuantity());
                    }
                }
                for (Inventory inv : inventories) {
                    if (inv.getProduct_model().equals(up.getProduct_model())
                            && inv.getSupply_center().equals(number_center.get(up.getSalesman()))) {

                        update_quantity(inv.getProduct_model(), inv.getSupply_center(),
                                inv.getQuantity() - up.getQuantity());
                        break;
                    }
                }
            }

            JDBCUtils.getConn()
                    .prepareStatement("delete from order_list cascade where quantity <=0").execute();
            System.out.println(orderList.size());
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void deleteOrder(String path) {
        setPath(path, 2);
        List<order_de> des = new ArrayList<>();
        while (parts != null) {
            parts = readline(",");
            if (parts != null)
                des.add(new order_de(parts));
        }
        for (order_de de : des) {
            List<ContractInfo> order_salesman = new ArrayList<>();
            for (ContractInfo con : orderList) {
                if (con.getQuantity() == 0) {
                    continue;
                }
                if (con.getContract_num().equals(de.getContract())
                        && con.getSalesman_num().equals(de.getSalesman())) {
                    order_salesman.add(con);
                }
            }
            /*new Comparator<ContractInfo>() {
                @Override
                public int compare(ContractInfo o1, ContractInfo o2) {
                    int result=o1.getEstimated_delivery_date().compareTo(o2.getEstimated_delivery_date());
                    if (result==0){
                        result=o1.getProduct_model().compareTo(o2.getProduct_model());
                    }
                    return result;
                }
            });*/
            Collections.sort(order_salesman, Comparator.comparing(ContractInfo::getEstimated_delivery_date).thenComparing(ContractInfo::getProduct_model));
            if (de.getSeq() <= order_salesman.size()) {
                ContractInfo con = order_salesman.get(de.getSeq() - 1);
                String sql = "delete from order_list where product_model='" + con.getProduct_model() +
                        "' and contract_num='" + con.getContract_num() + "'"+" and enterprise = '"+ con.getEnterprise()+"'"+ " and salesman_num='"+con.getSalesman_num()+"'";
                try {
                    Connection connection=JDBCUtils.getConn();
                    PreparedStatement stmt = connection.prepareStatement(sql);
                    stmt.execute();
                    System.out.println(stmt);
                    stmt.close();
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
