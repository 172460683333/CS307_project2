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
