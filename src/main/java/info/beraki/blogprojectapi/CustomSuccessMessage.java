package info.beraki.blogprojectapi;

import com.google.gson.Gson;

public class CustomSuccessMessage {
    int error;
    String message;
    int lastId;

    CustomSuccessMessage(int error, String error_message, int lastId){
        this.error = error;
        this.message = error_message;
        this.lastId= lastId;
    }

    @Override
    public String toString() {

        return new Gson().toJson(this);
    }
}
