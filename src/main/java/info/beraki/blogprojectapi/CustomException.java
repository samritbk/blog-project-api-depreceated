package info.beraki.blogprojectapi;

public class CustomException extends Exception {

    int error;
    String error_message;

    CustomException(int error, String error_message){
        this.error = error;
        this.error_message = error_message;
    }

    @Override
    public String toString() {
        return "{\"error\":1,\""+error+"\":\""+error_message+"\"}";
    }
}
