package SUSTech.CS307_project2.APIs;


import SUSTech.CS307_project2.APIs.javaBean.Center;
import SUSTech.CS307_project2.APIs.javaBean.Enterprise;
import SUSTech.CS307_project2.APIs.javaBean.Model;
import SUSTech.CS307_project2.APIs.javaBean.Staff;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public abstract class OriginData {
    //原始数据的对象
    static Map<Class, List> data = new HashMap();

    static Map<Class, String> sqlString = new HashMap<>();
    //原始数据路径
    static String[] paths;
    //原始数据类名
    static String[] ClassName;

    public static String[] getClassName() {
        return ClassName;
    }

    static String[] sqlStrings;

    static {
        paths = new String[]{"src/main/resources/release-to-students/data/center.csv",
                "src/main/resources/release-to-students/data/enterprise.csv",
                "src/main/resources/release-to-students/data/model.csv",
                "src/main/resources/release-to-students/data/staff.csv"
        };
        ClassName = new String[]{
                "SUSTech.CS307_project2.APIs.javaBean.Center",
                "SUSTech.CS307_project2.APIs.javaBean.Enterprise",
                "SUSTech.CS307_project2.APIs.javaBean.Model",
                "SUSTech.CS307_project2.APIs.javaBean.Staff"
        };
        sqlStrings = new String[]{
                "insert into center values(?,?)",
                "insert into enterprise values(?,?,?,?,?,?)",
                "insert into model values(?,?,?,?,?)",
                "insert into staff values(?,?,?,?,?,?,?,?)"
        };
        data.put(Center.class, new ArrayList<Center>());
        data.put(Enterprise.class, new ArrayList<Enterprise>());
        data.put(Model.class, new ArrayList<Model>());
        data.put(Staff.class, new ArrayList<Staff>());
        sqlString.put(Center.class, sqlStrings[0]);
        sqlString.put(Enterprise.class, sqlStrings[1]);
        sqlString.put(Model.class, sqlStrings[2]);
        sqlString.put(Staff.class, sqlStrings[3]);
    }


    public static void importOriData() {
        try {
            Connection conn = JDBCUtils.getConn();
            PreparedStatement stmt;
            stmt = conn.prepareStatement("drop table if exists center cascade ;\n" +
                    "drop table if exists enterprise cascade ;\n" +
                    "drop table if exists model cascade ;\n" +
                    "drop table if exists staff cascade ;\n" +
                    "drop table if exists inventories cascade ;\n" +
                    "drop table if exists order_list cascade ;\n" +
                    "\n" +
                    "create table center(id int primary key ,city varchar(88));\n" +
                    "\n" +
                    "create table enterprise(id int primary key ,name varchar(88),\n" +
                    "            country varchar(88),city varchar(88),supply_center\n" +
                    "                       varchar(88),industry varchar(88));\n" +
                    "\n" +
                    "create table model(id int primary key ,number varchar(88)\n" +
                    "                  ,model_name varchar(88),name varchar(88),\n" +
                    "                  unit_price varchar(88));\n" +
                    "\n" +
                    "create table staff(id int primary key ,name varchar(88)\n" +
                    "                  ,age varchar(88),gender varchar(88),\n" +
                    "                  number varchar(88) ,supply_center  varchar(88)\n" +
                    "                  ,mobile_number varchar(88),type varchar(88));\n" +
                    "\n" +
                    "create table if not exists inventories(id int primary key,\n" +
                    "         supply_center varchar(88), product_model varchar(88), supply_staff varchar(88),\n" +
                    "        Date date,\n" +
                    "         purchase_price int, quantity int\n" +
                    ");\n" +
                    "\n" +
                    "create table if not exists order_list(\n" +
                    "      contract_num varchar(88) ,\n" +
                    "enterprise varchar(88),product_model varchar(88) ,\n" +
                    "    quantity int,contract_manager varchar(88),\n" +
                    "  contract_date Date ,estimated_delivery_date Date , lodgement_date Date ,\n" +
                    "    salesman_num varchar(88),contract_type varchar(88)\n" +
                    ");");
            stmt.execute();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < paths.length; i++) {
            createTable(paths[i], ClassName[i]);
        }
    }

    //导入数据
    private static void createTable(String path, String className) {
        File file = new File(path);
        String print = null;
        try (BufferedReader bf = new BufferedReader(new FileReader(file))) {
            Connection conn = JDBCUtils.getConn();
            print = sqlString.get(Class.forName(className));
            PreparedStatement stmt = conn.prepareStatement(sqlString.get(Class.forName(className)));
            String line = "";
            String[] parts = null;
            //Center,Enterprise,Model,Staff的Class对象,用于创建实例
            Class row_class = Class.forName(className);
            Constructor constructor = row_class.getConstructor(String[].class);
            while ((line = bf.readLine()) != null) {
                line = line.replace(", ", "@");
                parts = line.split(",");
                if (parts[0].equals("id")) {
                    continue;
                }
                data.get(row_class).add(constructor.newInstance((Object) parts));
                stmt.setInt(1, Integer.parseInt(parts[0]));

                for (int i = 1; i < parts.length; i++) {
                    parts[i] = parts[i].replace("@", ", ");
                    stmt.setString(i + 1, parts[i]);
                }
                stmt.execute();
            }
            stmt.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    private static void updateCsv() {

    }

    private static void add_update_delete(String sql) {
        JDBCUtils.update(sql);
    }

    private static List<Object> query(String sql) {
        return JDBCUtils.query(sql, Object.class);
    }

}
