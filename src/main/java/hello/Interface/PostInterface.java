package hello.Interface;

import hello.Model.Post;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface PostInterface {
    void addPost(Integer postUserId, Integer postCategory, String postTitle, String postText);

    static List<Post> getListPosts(Connection connection, Integer limit) throws SQLException;
}
