package trunghai95_1312165.miniproject1.nearestbusstops;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import Modules.Place;
import Modules.PlaceFinder;
import Modules.PlaceFinderListener;

public class MainActivity extends AppCompatActivity implements PlaceFinderListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private LinearLayout _linearLayout;
    private ProgressDialog _progressDialog;
    private List<View> _views = new ArrayList<>();
    private List<Place> _places;
    private LatLng _myLatLng;
    private GoogleApiClient _googleApiClient;
    private LocationManager _locationManager;
    private MainActivity _this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _this = this;
        _linearLayout = (LinearLayout) findViewById(R.id.linearParent);

        _locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(this, "Location access permission denied!", Toast.LENGTH_LONG).show();
            return;
        }
        _locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10, this);

        _googleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API)
                .addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();

        _progressDialog = ProgressDialog.show(this, "Please wait!", "Finding your position...", true);
        _googleApiClient.connect();

        ((Button) findViewById(R.id.btnRefresh)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                find();
            }
        });
    }

    @Override
    public void onPlaceFinderStart() {
        _progressDialog = ProgressDialog.show(this, "Please wait!", "Finding nearest bus stops...", true);

        for (View v : _views)
            ((ViewGroup) v.getParent()).removeView(v);
        _views.clear();
    }

    @Override
    public void onPlaceFinderSuccess(List<Place> places) {
        _progressDialog.dismiss();

        _places = places;

        for (final Place place : _places) {
            LinearLayout ll = new LinearLayout(_this);
            ll.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            ll.setOrientation(LinearLayout.HORIZONTAL);
            ll.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);

            ImageView iv = new ImageView(_this);
            iv.setImageResource(R.mipmap.ic_launcher);
            TextView tv = new TextView(_this);
            tv.setText(place.name);
            tv.setTextColor(Color.BLACK);
            tv.setTextSize(15f);
            ll.setClickable(true);

            ll.addView(iv);
            ll.addView(tv);

            ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ActivityCompat.checkSelfPermission(_this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(_this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    Location loc = LocationServices.FusedLocationApi.getLastLocation(_googleApiClient);
                    _myLatLng = new LatLng(loc.getLatitude(), loc.getLongitude());
                    Intent i = new Intent(getApplicationContext(), DetailActivity.class);
                    i.putExtra("myLatLng", _myLatLng);
                    i.putExtra("destName", place.name);
                    i.putExtra("destLatLng", place.latlng);
                    startActivity(i);
//                    find();
                }
            });

            _linearLayout.addView(ll);
            _views.add(ll);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        _progressDialog.dismiss();
        find();
    }

    private void find()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(this, "Location access permission denied!", Toast.LENGTH_LONG).show();
            return;
        }
        Location loc = LocationServices.FusedLocationApi.getLastLocation(_googleApiClient);
        _myLatLng = new LatLng(loc.getLatitude(), loc.getLongitude());
        new PlaceFinder(this, _myLatLng, "bus_station", getString(R.string.api_key)).execute();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Connection suspended!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection failed!", Toast.LENGTH_LONG).show();
    }
}
