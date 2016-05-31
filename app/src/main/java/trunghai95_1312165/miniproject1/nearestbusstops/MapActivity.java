package trunghai95_1312165.miniproject1.nearestbusstops;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private LatLng _myLatLng;
    private LatLng _destLatLng;
    private String _destName;
    private PolylineOptions _polylineOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent i = getIntent();
        _myLatLng = i.getParcelableExtra("myLatLng");
        _destLatLng = i.getParcelableExtra("destLatLng");
        _destName = i.getStringExtra("destName");
        _polylineOptions = i.getParcelableExtra("polylineOptions");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(_myLatLng, 16));

        googleMap.addMarker(new MarkerOptions()
                .position(_myLatLng)
                .title("You are here!")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pedes_marker)));
        googleMap.addMarker(new MarkerOptions()
                .position(_destLatLng)
                .title(_destName)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.busstop_marker)));
        googleMap.addPolyline(_polylineOptions);
    }
}
