package SUSTech.CS307_project2.APIs;

import com.sun.javafx.geom.transform.GeneralTransform3D;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class OriginData {
    //原始数据的对象
    static class Center {
        int id;
        String center;
        List columns = new ArrayList();

        public Center(String[] row) {
            this.id = Integer.parseInt(row[0]);
            this.center = row[1];

        }

    }

    static class Enterprise {
        int id;
        String name, country, city, supply_center, industry;

        public Enterprise(String[] row) {
            this.id = Integer.parseInt(row[0]);
            this.name = row[1];
            this.country = row[2];
            this.city = row[3];
            this.supply_center = row[4];
            this.industry = row[5];

        }
    }

    static class Model {
        int id;
        String number, model, name;
        int unit_price;

        public Model(String[] row) {
            this.id = Integer.parseInt(row[0]);
            this.number = row[1];
            this.model = row[2];
            this.name = row[3];
            this.unit_price = Integer.parseInt(row[4]);
        }
    }

    static class Staff {
        int id;
        String name, age, gender;
        String number;
        String supply_center, mobile_number, type;

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
    }

    static Map<Class, List> data = new HashMap();
    static Map<Class, String> sqlString = new HashMap<>();
    //原始数据路径
    static String[] paths;
    //原始数据类名
    static String[] ClassName;
    static String[] sqlStrings;

    static {
        paths = new String[]{"src/main/resources/release-to-students/data/center.csv",
                "src/main/resources/release-to-students/data/enterprise.csv",
                "src/main/resources/release-to-students/data/model.csv",
                "src/main/resources/release-to-students/data/staff.csv"
        };
        ClassName = new String[]{
                "SUSTech.CS307_project2.APIs.OriginData$Center",
                "SUSTech.CS307_project2.APIs.OriginData$Enterprise",
                "SUSTech.CS307_project2.APIs.OriginData$Model",
                "SUSTech.CS307_project2.APIs.OriginData$Staff"
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
        for (int i = 0; i < paths.length; i++) {
            importOriginData(paths[i], ClassName[i]);
        }
//        List dataList;
//        for (String className : ClassName) {
//            try {
//                String tableName=className.split("\\$")[1];
//                Class clazz=Class.forName(className);
//                dataList=data.get(clazz);
//                Connection conn=JDBCUtils.getConn();
//                PreparedStatement stmt= conn.prepareStatement(sqlString.get(clazz));
//                if (tableName.equals("Center")){
//                    List<Center>list=dataList;
//                    for (Object column : list.get(0).columns) {
//
//                    }
//                }
//
//            } catch (ClassNotFoundException e) {
//                e.printStackTrace();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }


    }

    public static void main(String[] args) throws ClassNotFoundException {
        System.out.println(data.get(Class.forName(ClassName[0])).size());
    }

    //导入数据
    private static void importOriginData(String path, String className) {
        File file = new File(path);
        try (BufferedReader bf = new BufferedReader(new FileReader(file))) {
            Connection conn = JDBCUtils.getConn();
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
                    parts[i]=parts[i].replace("@", ", ");
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

    private void updateCsv() {

    }

    private void add_update_delete(String sql) {
        JDBCUtils.update(sql);
    }

    private List<Object> query(String sql) {
        return JDBCUtils.query(sql, Object.class);
    }

}
