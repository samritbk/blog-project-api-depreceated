package info.beraki.blogprojectapi.Model;

public class Post {
    private Integer postId;
    private String postTitle;
    private String postText;
    private Integer postCategory;
    private String postUserId;
    private long dateCreated;
    private long lastModified;
    private Integer status;
    private User user;

    public Integer getPostId() {
        return postId;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public String getPostText() {
        return postText;
    }

    public Integer getPostCategory() {
        return postCategory;
    }

    public String getPostUserId() {
        return postUserId;
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public long getLastModified() {
        return lastModified;
    }

    public Integer getStatus() {
        return status;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }

    public void setPostCategory(Integer postCategory) {
        this.postCategory = postCategory;
    }

    public void setPostUserId(String postUserId) {
        this.postUserId = postUserId;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
