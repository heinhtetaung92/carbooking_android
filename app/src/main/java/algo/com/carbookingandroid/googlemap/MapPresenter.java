package algo.com.carbookingandroid.googlemap;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import algo.com.carbookingandroid.R;
import algo.com.carbookingandroid.model.APIErrorResponse;
import algo.com.carbookingandroid.model.APIResponse;
import algo.com.carbookingandroid.model.ParkingLocation;
import algo.com.carbookingandroid.model.PossibleStartLocation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by heinhtetaung on 25/7/18.
 */

public class MapPresenter implements MapContract.Presenter {

    public static final int STARTING_POINTS = 0x01;
    public static final int DROPOFF_POINTS = 0x02;

    //mode to control showing between possible starting locations and possible drop off locations
    private int mMode = STARTING_POINTS;

    private static final String TAG = MapPresenter.class.getCanonicalName();
    MapContract.View mView;
    private HashMap<Integer, PossibleStartLocation> mStartLocationMap = new HashMap<>();

    public MapPresenter(MapContract.View view){
        this.mView = view;
    }

    @Override
    public void GoogleMapIsReady() {
        mView.setUpClusterer();
        mView.enableMyLocationOnMap();
        addSingaporeLocationMarker();
        showPossibleStartLocations();
    }

    @Override
    public void locationPermissionGranted() {
        mView.enableMyLocationOnMap();
        mView.getUserLastLocation();
    }

    @Override
    public void onPossibleStartLocationSelected(int locationId) {
        if(mStartLocationMap.containsKey(locationId)){
            Log.i(TAG, "location id contain");
            //show possible drop off locations of selected possible starting point
            List<ParkingLocation> locations = mStartLocationMap.get(locationId).getDropOffLocations();

            if(locations.size() > 0 && mMode != DROPOFF_POINTS){
                mView.clearMarkers();
                mView.addClusterItems(locations);
                mView.cluster();
                switchMode(DROPOFF_POINTS, locationId);
            }

        }
        else{
            Log.i(TAG, "location id not contain");
        }
    }

    @Override
    public boolean onBackPressed() {
        if(mMode == DROPOFF_POINTS){

            switchMode(STARTING_POINTS, 0);
            showPossibleStartLocations();

            return false;
        }else{
            return true;
        }
    }

    @Override
    public void lastLocationReceived(Location location) {
        if(location != null)
            mView.moveCamera(new LatLng(location.getLatitude(), location.getLongitude()), 12.0f);
    }

    private void switchMode(int mode, int locationId){
        if(mode == DROPOFF_POINTS){
            mMode = DROPOFF_POINTS;
            mView.showBackButton();
            mView.hideDateSelector();
            mView.changeTitle(locationId);
        }else if(mode == STARTING_POINTS){
            mMode = STARTING_POINTS;
            mView.dismissBackButton();
            mView.showDateSelector();
            mView.setDefaultTitle();
        }
    }

    private void addSingaporeLocationMarker(){
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        LatLng singapore = new LatLng(	1.290270, 103.851959);

//        mView.addMarker(singapore, "Marker in Singapore");
        mView.moveCamera(singapore, 12.0f);
    }

    @Override
    public void searchAvailableBookings(long startTime, long endTime){

        mView.showLoading(true);

        //divided by 1000 is to get TimeInSec since it was in MilliSec
        mView.searchBookingsAvailability(startTime/1000, endTime/1000,
                new Callback<APIResponse>() {
                    @Override
                    public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {
                        mView.showLoading(false);

                        if(response.isSuccessful()){
                            saveStartLocationSets(response.body().getStartLocationList());
                            showPossibleStartLocations();

                        }else{
                            //send error message
                            try {
                                parseErrorResponse(response.errorBody().string());
                            } catch (IOException e) {
//                                e.printStackTrace();
                                showSimpleError();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<APIResponse> call, Throwable t) {
                        //show error
                        showSimpleError();
                        mView.showLoading(false);
                    }
                });
    }

    @Override
    public HashMap<Integer, PossibleStartLocation> getLocations() {
        return mStartLocationMap;
    }

    @Override
    public void setLocations(HashMap<Integer, PossibleStartLocation> locations) {
        mStartLocationMap = locations;
//        showPossibleStartLocations();
    }

    private void showSimpleError(){
        mView.showToast(R.string.simple_err_msg);
    }

    private void parseErrorResponse(String errStr){
        Gson gson = new Gson();
        APIErrorResponse errResponse = gson.fromJson(errStr, APIErrorResponse.class);
        mView.showToast(errResponse.getMessage());
    }

    private void saveStartLocationSets(List<PossibleStartLocation> startLocationSets){
        this.mStartLocationMap = new HashMap<>();
        if(startLocationSets != null){
            for(PossibleStartLocation loc : startLocationSets){
                mStartLocationMap.put(loc.get_id(), loc);
            }
        }
    }

    private void showPossibleStartLocations(){
        if(mStartLocationMap.size() > 0){

            mView.clearMarkers();

            BitmapDescriptor icon;
            for (Map.Entry<Integer, PossibleStartLocation> entry : mStartLocationMap.entrySet()) {
                PossibleStartLocation location = entry.getValue();

                if(location.hasAvailableCars()){
                    //default color for possible starting location with available cars
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                }else{
                    //if there's no available cars in this location
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
                }
                mView.addClusterItem(new ParkingLocation(location.get_id(), location.getLocation(), location.getTitle(), null,
                        icon));
            }

            mView.cluster();

        }
    }

}
