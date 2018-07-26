package algo.com.carbookingandroid;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import algo.com.carbookingandroid.model.ParkingLocation;

public class ClusterRenderer extends DefaultClusterRenderer<ParkingLocation> {

    public ClusterRenderer(Context context, GoogleMap map, ClusterManager<ParkingLocation> clusterManager) {
        super(context, map, clusterManager);
        clusterManager.setRenderer(this);
    }


    @Override
    protected void onBeforeClusterItemRendered(ParkingLocation markerItem, MarkerOptions markerOptions) {
        if (markerItem.getIcon() != null) {
            markerOptions.icon(markerItem.getIcon()); //Here you retrieve BitmapDescriptor from ClusterItem and set it as marker icon
        }
        markerOptions.visible(true);
    }
}