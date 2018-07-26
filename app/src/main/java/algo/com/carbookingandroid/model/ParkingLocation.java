package algo.com.carbookingandroid.model;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;
import com.google.maps.android.clustering.ClusterItem;

import java.util.List;

/**
 * Created by heinhtetaung on 25/7/18.
 */

public class ParkingLocation implements ClusterItem{

    /***
     * "id": 64,
     "location": [123,123]
     */

    @SerializedName("id")
    int _id;

    @SerializedName("location")
    List<Float> location;

    String title, snippet;
    private BitmapDescriptor icon;

    public ParkingLocation(int id, List<Float> loc, String title, String snippet, BitmapDescriptor icon){
        this._id = id;
        this.location = loc;
        this.title = title;
        this.snippet = snippet;
        this.icon = icon;
    }

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

    public BitmapDescriptor getIcon() {
        if(icon == null){
            return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN);
        }
        return icon;
    }

    public void setIcon(BitmapDescriptor icon) {
        this.icon = icon;
    }

    @Override
    public LatLng getPosition() {
        if(location.size() < 2){
            //location data is not perfect
            return null;
        }else{
            return new LatLng(location.get(0), location.get(1));
        }
    }

    @Override
    public String getTitle() {
        if(title == null) {
            return String.valueOf(_id);
        }else{
            return title;
        }
    }

    @Override
    public String getSnippet() {
        return snippet;
    }
}
