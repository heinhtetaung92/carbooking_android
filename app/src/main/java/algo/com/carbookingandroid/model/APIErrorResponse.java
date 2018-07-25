package algo.com.carbookingandroid.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by heinhtetaung on 25/7/18.
 */

public class APIErrorResponse {

    @SerializedName("message")
    String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
