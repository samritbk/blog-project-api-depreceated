package info.beraki.blogprojectapi.Model;

public class User {
    int userId;
    String firebaseId;
    String fullName;
    String email;
    long dataCreated;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getDataCreated() {
        return dataCreated;
    }

    public void setDataCreated(long dataCreated) {
        this.dataCreated = dataCreated;
    }
}