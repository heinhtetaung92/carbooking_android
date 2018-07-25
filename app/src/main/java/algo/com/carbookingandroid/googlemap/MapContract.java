package algo.com.carbookingandroid.googlemap;

import com.google.android.gms.maps.model.LatLng;

import algo.com.carbookingandroid.model.APIResponse;
import retrofit2.Callback;

/**
 * Created by heinhtetaung on 25/7/18.
 */

public class MapContract {
    public interface  View{
        void addMarker(LatLng latLng, String title);

        void moveCamera(LatLng latLng, float zoom);

        void enableMyLocationOnMap();

        void searchBookingsAvailability(long startTime, long endTime, Callback<APIResponse> responseCallback);

        void showToast(String msg);

    }

    public interface Presenter {

        void GoogleMapIsReady();

        void locationPermissionGranted();

    }

}
