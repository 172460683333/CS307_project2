package SUSTech.CS307_project2.Spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SQLContext;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class spark_util {
    private static String master = "local[*]";
    private static String host = "localhost";
    private static String userName = "checker";
    private static String password = "123456";
    private static String database = "cs307";

    private SparkConf sparkConf;
    private JavaSparkContext jsc;
    private SQLContext sqlContext;

    public spark_util() {
        sparkConf = new SparkConf().setAppName("data_process").setMaster(master);
        jsc = new JavaSparkContext(sparkConf);
        sqlContext = new SQLContext(jsc);
    }


    //read data from sql
    public JavaRDD ReadSQL(String tableName) {
        JavaRDD rdd = null;

        //jdbc.url=jdbc:postgresql://localhost:3306/database
        String url = "jdbc:postgresql://" + host + "/" + database;
        Properties props = new Properties();
        props.setProperty("user", userName);
        props.setProperty("password", password);
        try {
            DriverManager.getConnection(url, props);
            System.out.println("Successfully connected to the database "
                    + database + " as " + userName);
            rdd = sqlContext.read().jdbc(url, tableName, props).select("*").toJavaRDD();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (rdd==null||rdd.count()==0){
            System.out.println("rdd is null or empty");
        }
        return rdd;
    }


}
