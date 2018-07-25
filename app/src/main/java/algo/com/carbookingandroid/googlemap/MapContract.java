package algo.com.carbookingandroid.googlemap;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by heinhtetaung on 25/7/18.
 */

public class MapContract {
    public interface  View{
        void addMarker(LatLng latLng, String title);

        void moveCamera(LatLng latLng, float zoom);

        void enableMyLocationOnMap();

    }

    public interface Presenter {

        void setupGoogleMap();

        void locationPermissionGranted();

    }

}
