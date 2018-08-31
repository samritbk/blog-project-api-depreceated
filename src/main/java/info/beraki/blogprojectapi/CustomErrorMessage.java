package info.beraki.blogprojectapi;

import com.google.gson.Gson;

public class CustomException extends Exception {

    int error;
    String error_message;

    CustomException(int error, String error_message){
        this.error = error;
        this.error_message = error_message;
    }

    @Override
    public String toString() {

        return new Gson().toJson("{\"error\":1,\"message\":\""+error_message+"\"}");
    }
}
