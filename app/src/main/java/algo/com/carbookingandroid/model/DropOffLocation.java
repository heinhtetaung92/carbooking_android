package algo.com.carbookingandroid.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by heinhtetaung on 25/7/18.
 */

public class DropOffLocation {

    /***
     * "id": 64,
     "location": [123,123]
     */

    @SerializedName("id")
    int _id;

    @SerializedName("location")
    List<Float> location;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public List<Float> getLocation() {
        return location;
    }

    public void setLocation(List<Float> location) {
        this.location = location;
    }
}
