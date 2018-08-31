package info.beraki.blogprojectapi;

import com.google.gson.Gson;
import info.beraki.blogprojectapi.Model.Post;
import org.json.JSONObject;
import spark.Spark;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import static info.beraki.blogprojectapi.UserHandling.addUser;
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
                  int limit;
                  limit= Util.tryParse(limitSplat,0); // PROBLEM

                  if (limit != 0) {
                      List<Post> listPosts;
                      listPosts = getListPosts(connection, limit);
                      toReturn = new Gson().toJson(listPosts);
                  } else {
                      LOGGER.info(limitSplat);
                      toReturn = new CustomErrorMessage(1, "Limit Problem").toString();
                  }
              }catch(NumberFormatException e){
                  System.out.println(e.getMessage() + e.getCause());
                  LOGGER.info(e.getCause().toString());
                  toReturn = new CustomErrorMessage(1, "Invalid Limit").toString();
              }catch (SQLException | NullPointerException e) {
                  e.printStackTrace();
                  System.out.println(e.getMessage() + e.getCause());
                  LOGGER.info(e.getCause().toString());
                  toReturn = new CustomErrorMessage(1, "SQL or Null pointer exception").toString();
              }
          }else{
             toReturn = new CustomErrorMessage(1, "Database ErrorModel").toString();
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
                toReturn=new CustomErrorMessage(1, "Database ErrorModel").toString();;
            }

            connection.close();
            res.type("application/json");
          return toReturn;
      });

      Spark.put("/signup/newuser", (req, res) -> { // /signup/newuser
          LOGGER.info("start");
          String toReturn=null;
          if(connectUsingJDBC(JDBC_DRIVER, DB_URL, DB_USER, DB_PASS)) {
              LOGGER.info("start");
              if (connection != null) {
                  toReturn=handleUser(connection, req.body());
                  LOGGER.info("ok");
              }else{
                  toReturn=new CustomErrorMessage(1, "No Connection to database").toString();
              }
          }else{
              toReturn=new CustomErrorMessage(1, "Database ErrorModel").toString();
          }

          LOGGER.info("Last");
          connection.close();
          res.type("application/json");
          return toReturn;
      });

      Spark.get("/posts/id/:id", (req, res) -> { // post/LIMIT
          String toReturn=null;
          if(connectUsingJDBC(JDBC_DRIVER, DB_URL, DB_USER, DB_PASS)) {
              Post post = null;
              if (connection != null) {
                  try {
                      String idString =req.params(":id");
                      int id= Util.tryParse(idString,0);
                      post = getPost(connection, id);
                  } catch (SQLException | NullPointerException e) {
                      System.out.println(e.getMessage() + e.getCause());
                      LOGGER.info(e.getCause().toString());
                  }
              }
              if (post == null)
                  toReturn =  new CustomErrorMessage(1,"Post doesn't exist").toString();
              else
                  toReturn=new Gson().toJson(post);
          }else{
              toReturn=new CustomErrorMessage(1, "Database error").toString();;
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
                if(connectUsingJDBC(JDBC_DRIVER, DB_URL, DB_USER, DB_PASS)) {
                    toReturn = handlePost(connection, req.body());
                }else{
                    toReturn = new CustomErrorMessage(1, "Database error").toString();
                }
            }catch (SQLException | ClassNotFoundException e){
                toReturn = new CustomErrorMessage(1, "SQL error").toString();
                e.printStackTrace();
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

    private static String handleUser(Connection connection, String body) throws Exception {
      String firebaseId,fullName, email;

        JSONObject requestJsonObject= new JSONObject(body);

        firebaseId= requestJsonObject.getString("firebase_id");
        fullName= requestJsonObject.getString("full_name");
        email= requestJsonObject.getString("email");
        LOGGER.info("deligating");

        String returna=addUser(connection, firebaseId, fullName, email);


        return returna;
    }


    public static boolean connectUsingJDBC(String JDBC_DRIVER, String DB_URL, String DB_USER, String DB_PASS){
        boolean toReturn=false;

        try {
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            if(connection.isValid(0))
                toReturn=true;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        return toReturn;
  }

}
