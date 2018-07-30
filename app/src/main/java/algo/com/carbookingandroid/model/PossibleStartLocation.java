package algo.com.carbookingandroid.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;
import com.google.maps.android.clustering.ClusterItem;

import java.io.Serializable;
import java.util.List;

import algo.com.carbookingandroid.App;
import algo.com.carbookingandroid.R;

/**
 * Created by heinhtetaung on 25/7/18.
 */

public class PossibleStartLocation implements Serializable{
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
    List<ParkingLocation> dropOffLocations;

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

    public List<ParkingLocation> getDropOffLocations() {
        return dropOffLocations;
    }

    public void setDropOffLocations(List<ParkingLocation> dropOffLocations) {
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

    public LatLng getPosition() {
        if(location.size() < 2){
            //location data is not perfect
            return null;
        }else{
            return new LatLng(location.get(0), location.get(1));
        }
    }

    public String getTitle() {
        if(availableCars <= 0){
            return App.getContext().getString(R.string.no_cars_txt);
        }
        return App.getContext().getString(R.string.available_cars_txt, availableCars);
    }

    public boolean hasAvailableCars(){
        return availableCars > 0;
    }

}
