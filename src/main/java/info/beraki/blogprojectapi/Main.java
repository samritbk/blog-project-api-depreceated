package hello;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import hello.Interface.PostInterface;
import hello.Model.Post;
import org.json.JSONException;
import org.json.JSONObject;
import spark.Spark;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import static spark.Spark.before;
import static spark.Spark.options;

public class Main implements PostInterface {

  static final String JDBC_DRIVER = Config.JDBC_DRIVER;
  static final String DB_USER = Config.DB_USER;
  static final String DB_PASS = Config.DB_PASS;
  static final String DB_URL = Config.DB_URL;
  private static Logger LOGGER = Logger.getLogger("Logging");

  static Connection connection;

  public static void main(String[] args) {


      Spark.get("/posts/limit/:limit", (req, res) -> { // post/LIMIT
            String toReturn=null;
          LOGGER.info("1");
          if(connectUsingJDBC(JDBC_DRIVER, DB_URL, DB_USER, DB_PASS)) {

              try {
                  String limitSplat = req.params(":limit");

                  int limit=0;
                      limit=tryParse(limitSplat); // PROBLEM

                      res.type("application/json");

                      if (limit != 0) {

                          List<Post> listPosts;

                          listPosts = getListPosts(connection, limit);

                          toReturn = new Gson().toJson(listPosts);

                      } else {
                          LOGGER.info(limitSplat);
                          toReturn = "{\"error\":1,\"message\":\"Limit problem\"}";
                      }
              }catch(NumberFormatException e){
                  System.out.println(e.getMessage() + e.getCause());
                  LOGGER.info(e.getCause().toString());
                  toReturn="{\"error\":1,\"message\":\"Invalid limit\"}";
              }catch (SQLException | NullPointerException e) {
                  System.out.println(e.getMessage() + e.getCause());
                  LOGGER.info(e.getCause().toString());
                  toReturn="{\"error\":1,\"message\":\"SQL or Null pointer\"}";
              }
          }else{
             toReturn="{\"error\":1,\"message\":\"Database error\"}";
          }

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
                toReturn="{\"error\":1,\"message\":\"Database error\"}";
            }
            res.type("application/json");

          return toReturn;
        });


        Spark.get("/bye", (req, res) -> {
          UUID randomUUID= UUID.randomUUID();
          res.type("text/html");
          return randomUUID;
        });




        Spark.post("/post/add", (req, res) -> { // post/LIMIT
            connectUsingJDBC(JDBC_DRIVER, DB_URL, DB_USER, DB_PASS);
            res.type("application/json");
            String a=handlePost(req.body());

            LOGGER.info(a);

            return a;
        });



        Spark.before((req, res) -> {
          String path = req.pathInfo();
          if (path.endsWith("/") && path.length() != 1)
            res.redirect(path.substring(0, path.length() - 1));

        });



        options("/*",
            (request, response) -> {

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


  public static boolean connectUsingJDBC(String JDBC_DRIVER, String DB_URL, String DB_USER, String DB_PASS){
    boolean toReturn=false;
    try {
      Class.forName(JDBC_DRIVER);
      connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
        toReturn=true;
    } catch (SQLException | ClassNotFoundException e) {
      e.printStackTrace();
      LOGGER.info(e.getCause().toString());

    }
    return toReturn;
  }

  private static List<Post> getListPosts(Connection connection, int limit) throws SQLException, NullPointerException {

    List<Post> postList= new ArrayList<>();

    Statement statement =connection.createStatement();

    String sql="SELECT * FROM posts";
            sql+=" ORDER BY post_id DESC";
                if(limit != 0){
                  sql+=" LIMIT "+limit;
                }

    ResultSet resultSet= statement.executeQuery(sql);


    while(resultSet.next()){
      Post post=new Post();

      Integer postId= resultSet.getInt("post_id");
      String postTitle=resultSet.getString("post_title");
      String postText=resultSet.getString("post_text");
      Integer postCategory=resultSet.getInt("post_category");
      Integer postUserId=resultSet.getInt("user_id");
      Integer dateCreated=resultSet.getInt("date_created");
      Integer lastModified=resultSet.getInt("last_modified");
      Integer status=resultSet.getInt("status");

      post.setPostId(postId);
      post.setPostTitle(postTitle);
      post.setPostText(postText);
      post.setPostCategory(postCategory);
      post.setPostUserId(postUserId);
      post.setDateCreated(dateCreated);
      post.setLastModified(lastModified);
      post.setStatus(status);


      postList.add(post);
    }

    return postList;
  }

  public static String handlePost(String reqBody){

      String toReturn=null;

      try {
          JSONObject reqJsonObject=new JSONObject(reqBody);
            Integer postUserId=reqJsonObject.getInt("postUserId");
            Integer postCategory=reqJsonObject.getInt("postCategory");
            String postTitle=mysql_real_escape_string(reqJsonObject.getString("postTitle"));
            String postText=mysql_real_escape_string(reqJsonObject.getString("postText"));

            toReturn = addPost(postUserId, postCategory, postTitle, postText);
          LOGGER.info(toReturn);
      } catch (Exception e) {
          e.printStackTrace();
          LOGGER.info(e.getMessage());
      }

      return toReturn;
  }

  public static String addPost(Integer postUserId, Integer postCategory, String postTitle, String postText) throws SQLException, Exception{



      Statement statement =connection.createStatement();

      String sql="INSERT INTO posts(post_category,post_title, post_text) VALUES ('"+ postCategory +"','"+ postTitle +"','"+ postText+"')";
      LOGGER.info(sql);

      int resultSet= statement.executeUpdate(sql);




      if(resultSet != 0)

          return new Gson().toJson("{\"error\":0,\"message\":\"Success\"}");
      else
          LOGGER.info("{'error':1,'message':'Error adding a post'}");
          return new Gson().toJson("{\"error\":0,\"message\":\"Error adding post\"}");


  }


  public static String mysql_real_escape_string(String str){
      if (str == null) {
          return null;
      }

      if (str.replaceAll("[a-zA-Z0-9_!@#$%^&*()-=+~.;:,\\Q[\\E\\Q]\\E<>{}\\/? ]","").length() < 1) {
          return str;
      }

      String clean_string = str;
      clean_string = clean_string.replaceAll("\\\\", "\\\\\\\\");
      clean_string = clean_string.replaceAll("\\n","\\\\n");
      clean_string = clean_string.replaceAll("\\r", "\\\\r");
      clean_string = clean_string.replaceAll("\\t", "\\\\t");
      clean_string = clean_string.replaceAll("\\00", "\\\\0");
      clean_string = clean_string.replaceAll("'", "\\\\'");
      clean_string = clean_string.replaceAll("\\\"", "\\\\\"");

      if (clean_string.replaceAll("[a-zA-Z0-9_!@#$%^&*()-=+~.;:,\\Q[\\E\\Q]\\E<>{}\\/?\\\\\"' ]"
              ,"").length() < 1){

      }
      
      return clean_string;
  }
    public static Integer tryParse(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
