package hello;

import java.util.logging.Logger;

public class Config {
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_USER = "root";
    static final String DB_PASS = "";
    static final String DB_NAME = "projectblog";
    static final String JDBC_URL = "jdbc:mysql://localhost/";
    static final String DB_URL = JDBC_URL+DB_NAME;
    private static Logger LOGGER = Logger.getLogger("Logging");
}
