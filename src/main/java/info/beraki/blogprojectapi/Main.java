package info.beraki.blogprojectapi;

import com.google.gson.Gson;
import info.beraki.blogprojectapi.Model.Post;
import spark.Spark;
import spark.utils.SparkUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import static spark.Spark.before;
import static spark.Spark.options;

public class Main extends PostHandling {

  private static final String JDBC_DRIVER = Config.JDBC_DRIVER;
  private static final String DB_USER = Config.DB_USER;
  private static final String DB_PASS = Config.DB_PASS;
  private static final String DB_URL = Config.DB_URL;
  private static Logger LOGGER = Logger.getLogger("Logging");

  private static Connection connection;

  public static void main(String[] args) {

      if(args.length != 0){
        int port=Util.tryParse(args[0],8888);
        Spark.port(port);
            if(port == 8888)
                LOGGER.info(port+"");
      }


      Spark.get("/posts/limit/:limit", (req, res) -> { // post/LIMIT
          String toReturn=null;
          LOGGER.info("1");
          if(connectUsingJDBC(JDBC_DRIVER, DB_URL, DB_USER, DB_PASS)) {
              String limitSplat = req.params(":limit");
              try {
                  int limit=0;
                  limit= Util.tryParse(limitSplat,0); // PROBLEM

                  if (limit != 0) {
                      List<Post> listPosts;
                      listPosts = getListPosts(connection, limit);
                      toReturn = new Gson().toJson(listPosts);
                  } else {
                      LOGGER.info(limitSplat);
                      toReturn = new CustomException(1, "Limit Problem").toString();
                  }
              }catch(NumberFormatException e){
                  System.out.println(e.getMessage() + e.getCause());
                  LOGGER.info(e.getCause().toString());
                  toReturn = new CustomException(1, "Invalid Limit").toString();
              }catch (SQLException | NullPointerException e) {
                  System.out.println(e.getMessage() + e.getCause());
                  LOGGER.info(e.getCause().toString());
                  toReturn = new CustomException(1, "SQL or Null pointer execption").toString();
              }
          }else{
             toReturn = new CustomException(1, "Database Error").toString();
          }
          connection.close();
          res.type("application/json");
          return toReturn;
        });

        Spark.get("/posts", (req, res) -> { // post/LIMIT

            String toReturn=null;
            if(connectUsingJDBC(JDBC_DRIVER, DB_URL, DB_USER, DB_PASS)) {
                List<Post> listPosts = null;
                if (connection != null) {
                    try {
                        listPosts = getListPosts(connection, 0);
                        if (listPosts == null) throw new NullPointerException("list is null");
                    } catch (SQLException | NullPointerException e) {
                        System.out.println(e.getMessage() + e.getCause());
                        LOGGER.info(e.getCause().toString());
                    }
                }
                toReturn=new Gson().toJson(listPosts);
            }else{
                toReturn=new CustomException(1, "Database Error").toString();;
            }

            connection.close();
            res.type("application/json");
          return toReturn;
      });

      Spark.get("/signup/newuser", (req, res) -> { // post/LIMIT

          String toReturn=null;
          if(connectUsingJDBC(JDBC_DRIVER, DB_URL, DB_USER, DB_PASS)) {
              List<Post> listPosts = null;
              if (connection != null) {
                  try {
                      listPosts = getListPosts(connection, 0);
                      if (listPosts == null) throw new NullPointerException("list is null");
                  } catch (SQLException | NullPointerException e) {
                      System.out.println(e.getMessage() + e.getCause());
                      LOGGER.info(e.getCause().toString());
                  }
              }
              toReturn=new Gson().toJson(listPosts);
          }else{
              toReturn=new CustomException(1, "Database Error").toString();;
          }

          connection.close();
          res.type("application/json");
          return toReturn;
      });

      Spark.get("/posts/id/:id", (req, res) -> { // post/LIMIT
          String toReturn=null;
          if(connectUsingJDBC(JDBC_DRIVER, DB_URL, DB_USER, DB_PASS)) {
              List<Post> listPosts = null;
              if (connection != null) {
                  try {
                      String idString =req.params(":id");
                      int id= Util.tryParse(idString,0);
                      listPosts = getPost(connection, id);
                      if (listPosts == null) throw new NullPointerException("list is null");
                  } catch (SQLException | NullPointerException e) {
                      System.out.println(e.getMessage() + e.getCause());
                      LOGGER.info(e.getCause().toString());
                  }
              }
              toReturn=new Gson().toJson(listPosts);
          }else{
              toReturn=new CustomException(1, "Database error").toString();;
          }

          connection.close();
          res.type("application/json");
          return toReturn;
      });


        Spark.get("/bye", (req, res) -> {
          UUID randomUUID= UUID.randomUUID();
          res.type("text/html");

          return randomUUID;
        });

        Spark.post("/post/add", (req, res) -> { // post/LIMIT
            String toReturn;

            try{
                connectUsingJDBC(JDBC_DRIVER, DB_URL, DB_USER, DB_PASS);
                toReturn=handlePost(connection, req.body());
            }catch (SQLException | ClassNotFoundException e){
                toReturn = new CustomException(1, "SQL error").toString();
            }
            connection.close();
            res.type("application/json");
            return toReturn;
        });


        Spark.before((req, res) -> {
          String path = req.pathInfo();
          if (path.endsWith("/") && path.length() != 1)
            res.redirect(path.substring(0, path.length() - 1));

        });

        options("/*", (request, response) -> {

              String accessControlRequestHeaders = request
                      .headers("Access-Control-Request-Headers");
              if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers",
                        accessControlRequestHeaders);
              }

              String accessControlRequestMethod = request
                      .headers("Access-Control-Request-Method");
              if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods",
                        accessControlRequestMethod);
              }

              return "OK";
        });

        before((request, response) ->
                response.header("Access-Control-Allow-Origin", "*"));
  }


  public static boolean connectUsingJDBC(String JDBC_DRIVER, String DB_URL, String DB_USER, String DB_PASS) throws ClassNotFoundException, SQLException {
    boolean toReturn=false;

    Class.forName(JDBC_DRIVER);

    connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    if(connection.isValid(0))
        toReturn=true;

    return toReturn;
  }

}
