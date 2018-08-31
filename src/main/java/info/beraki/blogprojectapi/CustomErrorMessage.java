package info.beraki.blogprojectapi;

import com.google.gson.Gson;

public class CustomErrorMessage {

    int error;
    String message;

    CustomErrorMessage(int error, String error_message){
        this.error = error;
        this.message = error_message;
    }

    @Override
    public String toString() {

        return new Gson().toJson(this);
    }
}
