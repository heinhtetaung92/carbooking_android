package algo.com.carbookingandroid;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

import java.time.Year;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import algo.com.carbookingandroid.UIComponents.DatePickerFragment;
import algo.com.carbookingandroid.UIComponents.TimePickerFragment;
import algo.com.carbookingandroid.googlemap.MapContract;
import algo.com.carbookingandroid.googlemap.MapPresenter;
import algo.com.carbookingandroid.model.APIResponse;
import algo.com.carbookingandroid.model.ParkingLocation;
import algo.com.carbookingandroid.model.PossibleStartLocation;
import algo.com.carbookingandroid.restfullapi.APIManager;
import retrofit2.Callback;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class BookingSearchActivity extends AppCompatActivity implements OnMapReadyCallback, MapContract.View, View.OnClickListener {

    private static final String TAG = BookingSearchActivity.class.getCanonicalName();
    private static final String LOCATIONS_KEY = "locations";
    private static final String FROM_DATE_KEY = "fromdate";
    private static final String TO_DATE_KEY = "todate";
    private static final String FROM_TIME_KEY = "fromtime";
    private static final String TO_TIME_KEY = "totime";

    private final int LOCATION_REQUEST = 0x01;

    GoogleMap mMap;
    MapView mapView;
    MapContract.Presenter mapPresenter;

    private FusedLocationProviderClient mFusedLocationClient;

    private ClusterManager<ParkingLocation> mClusterManager;
    EditText fromDateTimeSelector, toDateTimeSelector;
    LinearLayout dateSelectorLayout;
    AlertDialog loadingDialog;

    long fromDate, fromTime, toDate, toTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(createViews());

        mapView.onCreate(savedInstanceState);
        mapPresenter = new MapPresenter(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(LOCATIONS_KEY, mapPresenter.getLocations());
        outState.putLong(FROM_DATE_KEY, fromDate);
        outState.putLong(TO_DATE_KEY, toDate);
        outState.putLong(FROM_TIME_KEY, fromTime);
        outState.putLong(TO_TIME_KEY, toTime);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        fromDate = savedInstanceState.getLong(FROM_DATE_KEY);
        toDate = savedInstanceState.getLong(TO_DATE_KEY);
        fromTime = savedInstanceState.getLong(FROM_TIME_KEY);
        toTime = savedInstanceState.getLong(TO_TIME_KEY);

        showSelectedDateAndTime(R.id.from_date_selector);
        showSelectedDateAndTime(R.id.to_date_selector);

        HashMap<Integer, PossibleStartLocation> locations = (HashMap<Integer, PossibleStartLocation>) savedInstanceState.getSerializable(LOCATIONS_KEY);
        if(locations != null){
            mapPresenter.setLocations(locations);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mapPresenter.GoogleMapIsReady();

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
        //this one to add custom icon for items

        ClusterRenderer clusterRenderer = new ClusterRenderer(this, mMap, mClusterManager);

        mClusterManager.setOnClusterItemInfoWindowClickListener(new ClusterManager.OnClusterItemInfoWindowClickListener<ParkingLocation>() {
            @Override
            public void onClusterItemInfoWindowClick(ParkingLocation parkingLocation) {
                mapPresenter.onPossibleStartLocationSelected(parkingLocation.get_id());
            }
        });

    }

    @Override
    public void addClusterItems(List<ParkingLocation> items) {
        mClusterManager.addItems(items);
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
            Log.i(TAG, "has Permission");
            mMap.setMyLocationEnabled(true);
        } else {
            Log.i(TAG, "request Permission");
            requestLocationPermission();
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void getUserLastLocation() {
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
    public void hideDateSelector() {
        dateSelectorLayout.setVisibility(View.GONE);
    }

    @Override
    public void showDateSelector() {
        dateSelectorLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void showLoading(boolean willShow) {
        if(willShow){
            if(loadingDialog == null){
                loadingDialog = Utils.getLoadingDialog(this);
            }
            loadingDialog.show();
        }else{
            if(loadingDialog != null){
                loadingDialog.dismiss();
            }
        }

    }

    @Override
    public void changeTitle(int locId) {

        try {
            getSupportActionBar().setTitle(getString(R.string.dropoff_loc_title, locId));
        }catch (Exception ex){}
    }

    @Override
    public void setDefaultTitle() {
        try {
            getSupportActionBar().setTitle(getString(R.string.carbooking_title));
        }catch (Exception ex){}
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
    public void showToast(int msgId) {
        Toast.makeText(this, getString(msgId), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showSnackbar(String msg) {
        Snackbar snackbar = Snackbar
                .make(mapView, msg, Snackbar.LENGTH_LONG);

        snackbar.show();
    }

    @Override
    public void clearMarkers() {
        if(mClusterManager != null) {
            mClusterManager.clearItems();
        }
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

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mapPresenter.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.from_date_selector:
                callDateSelector(v.getId());
                break;

            case R.id.to_date_selector:
                callDateSelector(v.getId());
                break;

            case R.id.search_btn:
                checkTimeValidatiyAndRequest();
                break;

        }
    }

    private void checkTimeValidatiyAndRequest() {
        if(fromTime == 0 || toTime == 0){
            showToast(getString(R.string.invalid_time_txt));
        }else{
            mapPresenter.searchAvailableBookings(fromTime, toTime);
        }
    }

    public void callDateSelector(final int viewId) {
        DatePickerFragment mDatePicker = new DatePickerFragment();
        mDatePicker.disablePreviousDate(true);
        mDatePicker.init(getSelectedDate(viewId));
        mDatePicker.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                fromDateTimeSelector.setText("Selected date: " + String.valueOf(year) + " - " + String.valueOf(month) + " - " + String.valueOf(dayOfMonth));
                callTimeSelector(viewId);

                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth, 0, 0);
                saveSelectedDate(viewId, calendar.getTimeInMillis());
            }
        });
        mDatePicker.show(getSupportFragmentManager(), getString(R.string.select_date_title));
    }

    public void callTimeSelector(final int viewId) {
        TimePickerFragment mTimePicker = new TimePickerFragment();
        mTimePicker.init(getSelectedTime(viewId));
        mTimePicker.setOnTimeSetListener(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(android.widget.TimePicker view, int hourOfDay, int minute) {

                Calendar datetime = Calendar.getInstance();
                datetime.setTimeInMillis(getSelectedDate(viewId));
                datetime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                datetime.set(Calendar.MINUTE, minute);

                Log.i(TAG, "date : " + String.valueOf(getSelectedDate(viewId)));
                Log.i(TAG, "datetime : " + String.valueOf(datetime.getTimeInMillis()));

                if (isTimeValid(viewId, datetime.getTimeInMillis())) {
                    //it's after current
                    int hour = hourOfDay % 12;

                    saveSelectedTime(viewId, datetime.getTimeInMillis());

                    showSelectedDateAndTime(viewId);
                } else {
                    //it's before current'
                    showToast(getString(R.string.invalid_time_txt));
                }
            }
        });
        mTimePicker.show(getSupportFragmentManager(), getString(R.string.select_time_title));
    }

    private boolean isTimeValid(int viewId, long selectedTime){

        Calendar c = Calendar.getInstance();

        if(selectedTime < c.getTimeInMillis()){
            return false;
        }else if(viewId == R.id.to_date_selector){
            if(fromTime > selectedTime){
                return false;
            }

        }

        return true;
    }

    private void showSelectedDateAndTime(int viewId) {
        Calendar c = Calendar.getInstance();
        int minute, hour;
        switch (viewId) {
            case R.id.from_date_selector:

                c.setTimeInMillis(fromTime);//I added date value to this parameter earlier

                minute = c.get(Calendar.MINUTE);
                hour = c.get(Calendar.HOUR_OF_DAY);

                StringBuilder fromText = new StringBuilder();
                fromText.append(c.get(Calendar.YEAR)).append("/")
                        .append(c.get(Calendar.MONTH)).append("/")
                        .append(c.get(Calendar.DAY_OF_MONTH)).append(" ")
                        .append(hour < 10 ? "0" + hour : hour).append(":")
                        .append(minute < 10 ? "0" + minute : minute);

                fromDateTimeSelector.setText(fromText.toString());
                break;

            case R.id.to_date_selector:
                c.setTimeInMillis(toTime);//I added date value to this parameter earlier

                minute = c.get(Calendar.MINUTE);
                hour = c.get(Calendar.HOUR_OF_DAY);

                StringBuilder toText = new StringBuilder();
                toText.append(c.get(Calendar.YEAR)).append("/")
                        .append(c.get(Calendar.MONTH)).append("/")
                        .append(c.get(Calendar.DAY_OF_MONTH)).append(" ")
                        .append(hour < 10 ? "0" + hour : hour).append(":")
                        .append(minute < 10 ? "0" + minute : minute);

                toDateTimeSelector.setText(toText.toString());
                break;
        }

    }

    private void saveSelectedTime(int viewId, long selectedTime) {
        switch (viewId) {
            case R.id.from_date_selector:
                fromTime = selectedTime;
                break;

            case R.id.to_date_selector:
                toTime = selectedTime;
                break;
        }
    }

    private void saveSelectedDate(int viewId, long selectedDate) {
        switch (viewId) {
            case R.id.from_date_selector:
                fromDate = selectedDate;
                break;

            case R.id.to_date_selector:
                toDate = selectedDate;
                break;
        }
    }

    private long getSelectedDate(int viewId) {
        switch (viewId) {
            case R.id.from_date_selector:
                return fromDate;

            case R.id.to_date_selector:
                return toDate;
        }
        return 0;
    }


    private long getSelectedTime(int viewId) {
        switch (viewId) {
            case R.id.from_date_selector:
                return fromTime;

            case R.id.to_date_selector:
                return toTime;
        }
        return 0;
    }

    //create views on runtime
    public View createViews() {
        RelativeLayout container = new RelativeLayout(this);
        container.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        container.addView(getDateRangeSelectionView());
        container.addView(getMapView());

        return container;

    }

    public View getDateRangeSelectionView() {
        dateSelectorLayout = new LinearLayout(this);
        dateSelectorLayout.setId(R.id.date_range_selection_layout);
        dateSelectorLayout.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
        dateSelectorLayout.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        dateSelectorLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        dateSelectorLayout.setOrientation(LinearLayout.HORIZONTAL);

        fromDateTimeSelector = getEditText(R.id.from_date_selector, getString(R.string.from_txt));
        dateSelectorLayout.addView(fromDateTimeSelector);

        toDateTimeSelector = getEditText(R.id.to_date_selector, getString(R.string.to_txt));
        dateSelectorLayout.addView(toDateTimeSelector);

        dateSelectorLayout.addView(getSearchButton());

        return dateSelectorLayout;

    }

    public EditText getEditText(int id, String hint) {
        EditText edittext = new EditText(this);
        edittext.setLayoutParams(new LinearLayout.LayoutParams(Utils.dpToPx(150), ViewGroup.LayoutParams.WRAP_CONTENT));
        edittext.setHint(hint);
        edittext.setFocusable(false);
        edittext.setId(id);
        edittext.setOnClickListener(this);

        return edittext;
    }

    public View getSearchButton() {
        Button button = new Button(this, null, android.R.attr.borderlessButtonStyle);
        button.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        button.setText(R.string.search_txt);
        button.setId(R.id.search_btn);
        button.setOnClickListener(this);

        return button;
    }

    public View getMapView() {
        mapView = new MapView(this);
        mapView.getMapAsync(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.BELOW, R.id.date_range_selection_layout);
        mapView.setLayoutParams(params);
        return mapView;
    }

}
