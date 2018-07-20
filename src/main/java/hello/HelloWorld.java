package hello;
import com.google.gson.Gson;
import hello.Interface.PostInterface;
import hello.Model.Post;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static spark.Spark.*;

public class HelloWorld implements PostInterface {



  static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";

  static final String DB_USER = "root";
  static final String DB_PASS = "";
  static final String DB_NAME = "projectblog";

  static final String DB_URL = "jdbc:mysql://localhost/"+DB_NAME;

  private static Logger LOGGER = Logger.getLogger("InfoLogging");

  static Connection connection;

  public static void main(String[] args) {



    connectUsingJDBC(JDBC_DRIVER, DB_URL, DB_USER, DB_PASS);

    get("/hello", (req, res) -> {

      res.type("application/json");


      List<Post> listPosts=null;
      if(connection != null){
        try {
          listPosts = getListPosts(connection);
        }catch (SQLException | NullPointerException e){
          System.out.println(e.getMessage() + e.getCause());
          LOGGER.info(e.getCause().toString());
        }
      }

      String listPostsJson = new Gson().toJson(listPosts);

      return listPostsJson;
    });
    get("/bye", (req, res) -> "Goodbye Cruel World!");

  }

  private static List<Post> getListPosts(Connection connection) throws SQLException, NullPointerException {

    List<Post> postList= new ArrayList<>();

    Statement statement =connection.createStatement();
    String sql="SELECT * FROM posts";
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
}
