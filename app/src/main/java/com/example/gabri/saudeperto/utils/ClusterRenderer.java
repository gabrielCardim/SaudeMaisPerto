package com.example.gabri.saudeperto.utils;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;


public class ClusterRenderer extends DefaultClusterRenderer<Cluster> {

    public ClusterRenderer(Context context, GoogleMap map, ClusterManager<Cluster> clusterManager) {
        super(context, map, clusterManager);
    }

    protected void onBeforeClusterItemRendered(Cluster item, MarkerOptions markerOptions) {
        markerOptions.title(item.getTitle());
        markerOptions.snippet(item.getSnippet());
        super.onBeforeClusterItemRendered(item, markerOptions);
    }

}
