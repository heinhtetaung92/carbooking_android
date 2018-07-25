package algo.com.carbookingandroid.googlemap;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by heinhtetaung on 25/7/18.
 */

public class MapPresenter implements MapContract.Presenter {

    MapContract.View mView;

    public MapPresenter(MapContract.View view){
        this.mView = view;
    }

    @Override
    public void setupGoogleMap() {
        addSingaporeLocationMarker();
        mView.enableMyLocationOnMap();
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



}
