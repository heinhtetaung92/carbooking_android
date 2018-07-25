package algo.com.carbookingandroid.googlemap;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import algo.com.carbookingandroid.model.APIErrorResponse;
import algo.com.carbookingandroid.model.APIResponse;
import algo.com.carbookingandroid.model.DropOffLocation;
import algo.com.carbookingandroid.model.LocationSector;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by heinhtetaung on 25/7/18.
 */

public class MapPresenter implements MapContract.Presenter {

    MapContract.View mView;
    private List<LocationSector> mSectorList;

    public MapPresenter(MapContract.View view){
        this.mView = view;
    }

    @Override
    public void GoogleMapIsReady() {
        addSingaporeLocationMarker();
        mView.enableMyLocationOnMap();
        searchBookings();
    }

    @Override
    public void locationPermissionGranted() {
        mView.enableMyLocationOnMap();
    }

    private void addSingaporeLocationMarker(){
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        LatLng singapore = new LatLng(	1.290270, 103.851959);

        mView.addMarker(singapore, "Marker in Singapore");
        mView.moveCamera(singapore, 12.0f);
    }

    private void searchBookings(){
        mView.searchBookingsAvailability(234523456, 23452345,
                new Callback<APIResponse>() {
                    @Override
                    public void onResponse(Call<APIResponse> call, Response<APIResponse> response) {
                        if(response.isSuccessful()){
                            saveSectors(response.body().getSectorList());
                            showFirstSector();
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
                    }
                });
    }

    private void showSimpleError(){
        mView.showToast("Something went wrong!!!");
    }

    private void parseErrorResponse(String errStr){
        Gson gson = new Gson();
        APIErrorResponse errResponse = gson.fromJson(errStr, APIErrorResponse.class);
        mView.showToast(errResponse.getMessage());
    }

    private void saveSectors(List<LocationSector> sectors){
        if(sectors == null){
            this.mSectorList = new ArrayList<>();
        }else{
            this.mSectorList = sectors;
        }
    }

    private void showFirstSector(){
        if(mSectorList.size() > 0){
            LocationSector sector =  mSectorList.get(0);
            mView.clearMarkers();
            for(int i=0;i<sector.getDropOffLocations().size() ; i++){
                showMarker(sector.getDropOffLocations().get(i));
            }
        }
    }

    private void showMarker(DropOffLocation location){
        mView.addMarker(location.getLatLong(), "");
    }

}
