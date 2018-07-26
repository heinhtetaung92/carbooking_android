package algo.com.carbookingandroid.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by heinhtetaung on 25/7/18.
 */

public class APIResponse {

    @SerializedName("data")
    List<PossibleStartLocation> startLocationList;

    public List<PossibleStartLocation> getStartLocationList() {
        return startLocationList;
    }

    public void setStartLocationList(List<PossibleStartLocation> startLocationList) {
        this.startLocationList = startLocationList;
    }
}
