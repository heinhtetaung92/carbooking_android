package algo.com.carbookingandroid.googlemap;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import algo.com.carbookingandroid.model.APIResponse;
import algo.com.carbookingandroid.model.ParkingLocation;
import algo.com.carbookingandroid.model.PossibleStartLocation;
import retrofit2.Callback;

/**
 * Created by heinhtetaung on 25/7/18.
 */

public class MapContract {
    public interface  View{
        void addMarker(LatLng latLng, String title);

        //with different color for Possible Start Location Marker
        void addStartLocationMarker(LatLng latLng, String title);

        void moveCamera(LatLng latLng, float zoom);

        void enableMyLocationOnMap();

        void searchBookingsAvailability(long startTime, long endTime, Callback<APIResponse> responseCallback);

        void showToast(String msg);

        void showToast(int msgId);

        void showSnackbar(String msg);

        void clearMarkers();

        void setUpClusterer();

        void addClusterItems(List<ParkingLocation> items);

        void addClusterItem(ParkingLocation item);

        void showBackButton();

        void dismissBackButton();

        void cluster();

        void getUserLastLocation();

        void hideDateSelector();

        void showDateSelector();

        void showLoading(boolean willShow);

        //to update title for drop off locations, locId is the starting point location id
        void changeTitle(int locId);

        void setDefaultTitle();

    }

    public interface Presenter {

        void GoogleMapIsReady();

        void locationPermissionGranted();

        void onPossibleStartLocationSelected(int locId);

        boolean onBackPressed();

        void lastLocationReceived(Location location);

        void searchAvailableBookings(long startTime, long endTime);

        HashMap<Integer, PossibleStartLocation> getLocations();

        void setLocations(HashMap<Integer, PossibleStartLocation> locations);

    }

}
