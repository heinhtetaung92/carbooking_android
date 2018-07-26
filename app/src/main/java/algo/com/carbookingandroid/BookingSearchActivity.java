package algo.com.carbookingandroid;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.android.clustering.ClusterManager;

import java.util.List;

import algo.com.carbookingandroid.googlemap.MapContract;
import algo.com.carbookingandroid.googlemap.MapPresenter;
import algo.com.carbookingandroid.model.APIResponse;
import algo.com.carbookingandroid.model.ParkingLocation;
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

    private FusedLocationProviderClient mFusedLocationClient;

    private ClusterManager<ParkingLocation> mClusterManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mapView = new MapView(this);
        mapView.onCreate(savedInstanceState);
        mapPresenter = new MapPresenter(this);
        LinearLayout layout = new LinearLayout(this);
        setupMapView();

        setContentView(mapView);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mapPresenter.GoogleMapIsReady();

    }

    private void setupMapView() {
        mapView.getMapAsync(this);
        mapView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    public void setUpClusterer() {

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        mClusterManager = new ClusterManager<>(this, mMap);

        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setOnInfoWindowClickListener(mClusterManager);
        ClusterRenderer clusterRenderer = new ClusterRenderer(this, mMap, mClusterManager);


        mClusterManager.setOnClusterItemInfoWindowClickListener(new ClusterManager.OnClusterItemInfoWindowClickListener<ParkingLocation>() {
            @Override
            public void onClusterItemInfoWindowClick(ParkingLocation parkingLocation) {
                Log.i(TAG, "Parking Location clicked : " + parkingLocation.get_id());
                mapPresenter.onPossibleStartLocationSelected(parkingLocation.get_id());
            }
        });

    }

    @Override
    public void addClusterItems(List<ParkingLocation> items) {
        mClusterManager.addItems(items);
//        mClusterManager.cluster();
    }

    @Override
    public void addClusterItem(ParkingLocation item) {
        mClusterManager.addItem(item);
    }

    @Override
    public void showBackButton() {
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception ex) {//actionbar is empty
        }
    }

    @Override
    public void dismissBackButton() {
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        } catch (Exception ex) {//actionbar is empty
        }
    }

    @Override
    public void cluster() {
        mClusterManager.cluster();
    }

    @Override
    public void addMarker(LatLng latLng, String title) {
        mMap.addMarker(new MarkerOptions().position(latLng)
                .title(title));
    }

    @Override
    public void addStartLocationMarker(LatLng latLng, String title) {
        mMap.addMarker(new MarkerOptions().position(latLng)
                .title(title)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
    }

    @Override
    public void moveCamera(LatLng latLng, float zoom) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    @SuppressLint("MissingPermission")
    @Override
    public void enableMyLocationOnMap() {
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        if (hasLocationPermission()) {
            mMap.setMyLocationEnabled(true);
        } else {
            requestLocationPermission();
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void getUserLastLocation(){
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {

                            mapPresenter.lastLocationReceived(location);
                        }
                    }
                });
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
    public void showSnackbar(String msg) {
        Snackbar snackbar = Snackbar
                .make(mapView, msg, Snackbar.LENGTH_LONG);

        snackbar.show();
    }

    @Override
    public void clearMarkers() {
        mClusterManager.clearItems();
    }

    private boolean hasLocationPermission() {
        return (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    //request permission to access user location
    public void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, LOCATION_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case LOCATION_REQUEST: {
                if (grantResults.length > 0
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(mapPresenter.onBackPressed()){
            super.onBackPressed();
        }
    }

}
