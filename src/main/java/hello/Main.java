package hello;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import hello.Interface.PostInterface;
import hello.Model.Post;
import spark.Spark;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import static spark.Spark.*;

public class Main implements PostInterface {



  static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";

  static final String DB_USER = "root";
  static final String DB_PASS = "";
  static final String DB_NAME = "projectblog";

  static final String DB_URL = "jdbc:mysql://localhost/"+DB_NAME;

  private static Logger LOGGER = Logger.getLogger("InfoLogging");

  static Connection connection;

  static String personallogger;

  public static void main(String[] args) {



        connectUsingJDBC(JDBC_DRIVER, DB_URL, DB_USER, DB_PASS);

        get("/posts/:limit", (req, res) -> { // post/LIMIT

          Integer limit= Integer.parseInt(req.params(":limit"));

          res.type("application/json");

          List<Post> listPosts=null;
          if(connection != null ){
            try {

              listPosts = getListPosts(connection, limit);

              if(listPosts == null) throw new NullPointerException("list is null");

            }catch (SQLException | NullPointerException e){
              System.out.println(e.getMessage() + e.getCause());
              LOGGER.info(e.getCause().toString());
            }
          }

          return new Gson().toJson(listPosts);
        });

        get("/posts", (req, res) -> { // post/LIMIT

          res.type("application/json");

          List<Post> listPosts=null;
          if(connection != null ){
            try {

              listPosts = getListPosts(connection, null);

              if(listPosts == null) throw new NullPointerException("list is null");

            }catch (SQLException | NullPointerException e){
              System.out.println(e.getMessage() + e.getCause());
              LOGGER.info(e.getCause().toString());
            }
          }

          return new Gson().toJson(listPosts);
        });


        get("/bye", (req, res) -> "Goodbye Cruel World!");


        Spark.get("*", (req, res) -> {


          if(!req.pathInfo().startsWith("/static")){
            res.type("application/json");
            res.status(404);
          }

          JsonObject jsonObject=new JsonObject();
          jsonObject.addProperty("status", 400);
          jsonObject.addProperty("message", "Not Found");

          return jsonObject;

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

        before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));
  }


  public static void connectUsingJDBC(String JDBC_DRIVER, String DB_URL, String DB_USER, String DB_PASS){
    //Connection connection=null;
    try {
      Class.forName(JDBC_DRIVER);
      connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);

    } catch (SQLException | ClassNotFoundException e) {
      e.printStackTrace();
      LOGGER.info(e.getCause().toString());

    }
  }

  @Override
  public void addPost(Integer postUserId, Integer postCategory, String postTitle, String postText) {
    // JDBC of jdbc connection here

  }
  private static List<Post> getListPosts(Connection connection, Integer limit) throws SQLException, NullPointerException {

    List<Post> postList= new ArrayList<>();

    Statement statement =connection.createStatement();

    String sql="SELECT * FROM posts";
    if(limit != null){
      sql=sql+" LIMIT "+limit;
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
}
