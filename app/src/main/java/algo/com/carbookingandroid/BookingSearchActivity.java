package algo.com.carbookingandroid;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import algo.com.carbookingandroid.googlemap.MapContract;
import algo.com.carbookingandroid.googlemap.MapPresenter;
import algo.com.carbookingandroid.model.APIResponse;
import algo.com.carbookingandroid.restfullapi.APIManager;
import retrofit2.Callback;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class BookingSearchActivity extends AppCompatActivity implements OnMapReadyCallback, MapContract.View {

    private static final String TAG = BookingSearchActivity.class.getCanonicalName();
    private final int LOCATION_REQUEST = 0x01;

    GoogleMap mMap;
    MapView mapView;
    MapContract.Presenter mapPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mapView = new MapView(this);
        mapView.onCreate(savedInstanceState);
        mapPresenter = new MapPresenter(this);
        setupMapView();

        setContentView(mapView);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mapPresenter.GoogleMapIsReady();

    }

    private void setupMapView(){
        mapView.getMapAsync(this);
        mapView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    public void addMarker(LatLng latLng, String title) {
        mMap.addMarker(new MarkerOptions().position(latLng)
            .title(title));
    }

    @Override
    public void moveCamera(LatLng latLng, float zoom) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    @SuppressLint("MissingPermission")
    @Override
    public void enableMyLocationOnMap() {
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        if(hasLocationPermission()) {
            mMap.setMyLocationEnabled(true);
        }else{
            requestLocationPermission();
        }
    }

    @Override
    public void searchBookingsAvailability(long startTime, long endTime, Callback<APIResponse> responseCallback) {
        APIManager.getInstance().API_Service().searchBookingValidity(startTime, endTime)
                .enqueue(responseCallback);
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void clearMarkers() {
        mMap.clear();
    }

    private boolean hasLocationPermission(){
        return (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    //request permission to access user location
    public void requestLocationPermission(){
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, LOCATION_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case LOCATION_REQUEST: {
                if(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mapPresenter.locationPermissionGranted();
                }
            }
            break;
        }

    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

}
