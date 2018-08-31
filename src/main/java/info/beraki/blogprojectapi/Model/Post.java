package info.beraki.blogprojectapi.Model;

public class Post {
    private Integer postId;
    private String postTitle;
    private String postText;
    private Integer postCategory;
    private Integer postUserId;
    private Integer dateCreated;
    private Integer lastModified;
    private Integer status;

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

    public Integer getPostUserId() {
        return postUserId;
    }

    public Integer getDateCreated() {
        return dateCreated;
    }

    public Integer getLastModified() {
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

    public void setPostUserId(Integer postUserId) {
        this.postUserId = postUserId;
    }

    public void setDateCreated(Integer dateCreated) {
        this.dateCreated = dateCreated;
    }

    public void setLastModified(Integer lastModified) {
        this.lastModified = lastModified;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
