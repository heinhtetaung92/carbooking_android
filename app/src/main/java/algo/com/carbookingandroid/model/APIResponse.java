package algo.com.carbookingandroid.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by heinhtetaung on 25/7/18.
 */

public class APIResponse {

    @SerializedName("data")
    List<LocationSector> sectorList;

    public List<LocationSector> getSectorList() {
        return sectorList;
    }

    public void setSectorList(List<LocationSector> sectorList) {
        this.sectorList = sectorList;
    }
}
