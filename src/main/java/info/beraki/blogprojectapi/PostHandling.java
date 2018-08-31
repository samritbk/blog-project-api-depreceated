package info.beraki.blogprojectapi;

import com.google.gson.Gson;
import info.beraki.blogprojectapi.Model.Post;
import info.beraki.blogprojectapi.Model.User;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static info.beraki.blogprojectapi.UserHandling.getUser;
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
            String postUserId=resultSet.getString("user_id");
            long dateCreated=resultSet.getLong("date_created");
            long lastModified=resultSet.getLong("last_modified");
            Integer status=resultSet.getInt("status");
            User user= getUser(connection, postUserId);

            post.setPostId(postId);
            post.setPostTitle(postTitle);
            post.setPostText(postText);
            post.setPostCategory(postCategory);
            post.setPostUserId(postUserId);
            post.setDateCreated(dateCreated);
            post.setLastModified(lastModified);
            post.setStatus(status);
            post.setUser(user);


            postList.add(post);
        }

        return postList;
    }
    static Post getPost(Connection connection, int post_id) throws SQLException, NullPointerException {

        Post postObject = null;
        Statement statement = connection.createStatement();

        String sql="SELECT * FROM posts";
        sql+=" WHERE post_id="+post_id;

        ResultSet resultSet= statement.executeQuery(sql);

            if(resultSet.next()) {
                postObject = new Post();
                    Integer postId = resultSet.getInt("post_id");
                    String postTitle = resultSet.getString("post_title");
                    String postText = resultSet.getString("post_text");
                    Integer postCategory = resultSet.getInt("post_category");
                    String postUserId = resultSet.getString("user_id");
                    long dateCreated = resultSet.getLong("date_created");
                    long lastModified = resultSet.getLong("last_modified");
                    Integer status = resultSet.getInt("status");
                    User user= getUser(connection, postUserId);

                    postObject.setPostId(postId);
                    postObject.setPostTitle(postTitle);
                    postObject.setPostText(postText);
                    postObject.setPostCategory(postCategory);
                    postObject.setPostUserId(postUserId);
                    postObject.setDateCreated(dateCreated);
                    postObject.setLastModified(lastModified);
                    postObject.setStatus(status);
                    postObject.setUser(user);
            }

            return postObject;
    }



    private static String addPost(Connection connection, String postUserId, Integer postCategory, String postTitle, String postText) throws SQLException, Exception{

        long timestamp= System.currentTimeMillis();

        Statement statement =connection.createStatement();

        String sql="INSERT INTO " +
                "posts(user_id, post_category,post_title, post_text, date_created, last_modified) " +
                "VALUES " +
                "('"+ postUserId +"','"+ postCategory +"','"+ postTitle +"','"+ postText+"','"+ timestamp +"','"+ timestamp +"')";

        int resultSet= statement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);

        ResultSet rs= statement.getGeneratedKeys();

        int lastId=0;
        if(rs.next())
            lastId= rs.getInt(1);


        if(resultSet != 0)
            return new CustomSuccessMessage(0, "Success",lastId).toString();
        else
            return new CustomErrorMessage(1,"ErrorModel adding post").toString();
    }




    public static String handlePost(Connection connection, String reqBody) throws Exception {

        String toReturn=null;

        JSONObject reqJsonObject=new JSONObject(reqBody);
        String postUserId=reqJsonObject.getString("postUserId");
        Integer postCategory=reqJsonObject.getInt("postCategory");
        String postTitle= mysql_real_escape_string(reqJsonObject.getString("postTitle"));
        String postText=mysql_real_escape_string(reqJsonObject.getString("postText"));

        toReturn = addPost(connection, postUserId, postCategory, postTitle, postText);

        return toReturn;
    }
}
