package algo.com.carbookingandroid.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by heinhtetaung on 25/7/18.
 */

public class LocationSector {
    /***
     * "dropoff_locations":[]
     * "available_cars": 4,
     "id": 31,
     "location": [
     1.3149,
     103.7643
     ]
     */

    @SerializedName("id")
    int _id;

    @SerializedName("dropoff_locations")
    List<DropOffLocation> dropOffLocations;

    @SerializedName("available_cars")
    int availableCars;

    @SerializedName("location")
    List<Float> location;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public List<DropOffLocation> getDropOffLocations() {
        return dropOffLocations;
    }

    public void setDropOffLocations(List<DropOffLocation> dropOffLocations) {
        this.dropOffLocations = dropOffLocations;
    }

    public int getAvailableCars() {
        return availableCars;
    }

    public void setAvailableCars(int availableCars) {
        this.availableCars = availableCars;
    }

    public List<Float> getLocation() {
        return location;
    }

    public void setLocation(List<Float> location) {
        this.location = location;
    }
}
