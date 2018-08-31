package info.beraki.blogprojectapi;

import com.google.gson.Gson;
import com.mysql.cj.api.log.Log;
import info.beraki.blogprojectapi.Model.Post;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static info.beraki.blogprojectapi.Util.mysql_real_escape_string;

public class PostHandling {
    static List<Post> getListPosts(Connection connection, int limit) throws SQLException, NullPointerException {

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
    static List<Post> getPost(Connection connection, int post) throws SQLException, NullPointerException {

        List<Post> postList= new ArrayList<>();

        Statement statement =connection.createStatement();

        String sql="SELECT * FROM posts";
        sql+=" ORDER BY post_id DESC";
        if(post != 0){
            sql+="'+user_id+'"+post;
        }

        ResultSet resultSet= statement.executeQuery(sql);
            Post postObject = new Post();

            if(resultSet.next()) {

                Integer postId = resultSet.getInt("post_id");
                String postTitle = resultSet.getString("post_title");
                String postText = resultSet.getString("post_text");
                Integer postCategory = resultSet.getInt("post_category");
                Integer postUserId = resultSet.getInt("user_id");
                Integer dateCreated = resultSet.getInt("date_created");
                Integer lastModified = resultSet.getInt("last_modified");
                Integer status = resultSet.getInt("status");

                postObject.setPostId(postId);
                postObject.setPostTitle(postTitle);
                postObject.setPostText(postText);
                postObject.setPostCategory(postCategory);
                postObject.setPostUserId(postUserId);
                postObject.setDateCreated(dateCreated);
                postObject.setLastModified(lastModified);
                postObject.setStatus(status);

            }

            postList.add(postObject);

            return postList;
        }



    private static String addPost(Connection connection, Integer postUserId, Integer postCategory, String postTitle, String postText) throws SQLException, Exception{

        Statement statement =connection.createStatement();

        String sql="INSERT INTO " +
                "posts(user_id, post_category,post_title, post_text) " +
                "VALUES " +
                "('"+ postUserId +"','"+ postCategory +"','"+ postTitle +"','"+ postText+"')";

        int resultSet= statement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);

        ResultSet rs= statement.getGeneratedKeys();

        int lastId=0;
        if(rs.next())
            lastId= rs.getInt(1);


        if(resultSet != 0)
            return new Gson().toJson("{\"error\":0,\"message\":\"Success\",\"last_id\":\""+lastId+"\"}");
        else
            return new CustomException(1,"Error adding post").toString();

    }


//    private static String addUser(Connection connection, Integer userId, String firebaseId, String fullname, String postText) throws SQLException, Exception{
//
//        Statement statement =connection.createStatement();
//
//        String sql="INSERT INTO " +
//                "posts(user_id, post_category,post_title, post_text) " +
//                "VALUES " +
//                "('"+ postUserId +"','"+ postCategory +"','"+ postTitle +"','"+ postText+"')";
//
//        int resultSet= statement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
//
//        ResultSet rs= statement.getGeneratedKeys();
//
//        int lastId=0;
//        if(rs.next())
//            lastId= rs.getInt(1);
//
//
//        if(resultSet != 0)
//            return new Gson().toJson("{\"error\":0,\"message\":\"Success\",\"last_id\":\""+lastId+"\"}");
//        else
//            return new CustomException(1,"Error adding post").toString();
//
//    }

    public static String handlePost(Connection connection, String reqBody) throws Exception {

        String toReturn=null;

        JSONObject reqJsonObject=new JSONObject(reqBody);
        Integer postUserId=reqJsonObject.getInt("postUserId");
        Integer postCategory=reqJsonObject.getInt("postCategory");
        String postTitle= mysql_real_escape_string(reqJsonObject.getString("postTitle"));
        String postText=mysql_real_escape_string(reqJsonObject.getString("postText"));

        toReturn = addPost(connection, postUserId, postCategory, postTitle, postText);

        return toReturn;
    }
}
