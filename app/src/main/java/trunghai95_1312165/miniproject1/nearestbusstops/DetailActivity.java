package trunghai95_1312165.miniproject1.nearestbusstops;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.UnsupportedEncodingException;
import java.util.List;

import Modules.DirectionFinder;
import Modules.DirectionFinderListener;
import Modules.Route;

public class DetailActivity extends AppCompatActivity implements DirectionFinderListener {

    private LatLng _myLatLng;
    private LatLng _destLatLng;
    private String _destName;
    private PolylineOptions _polylineOptions;
    private ProgressDialog _progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent i = getIntent();
        _myLatLng = i.getParcelableExtra("myLatLng");
        _destLatLng = i.getParcelableExtra("destLatLng");
        _destName = i.getStringExtra("destName");

        ((TextView) findViewById(R.id.tvTitle)).setText(_destName);
        ((ImageView) findViewById(R.id.imageView)).setImageResource(R.mipmap.ic_launcher);

        findViewById(R.id.btnNavigate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MapActivity.class);
                i.putExtra("myLatLng", _myLatLng);
                i.putExtra("destName", _destName);
                i.putExtra("destLatLng", _destLatLng);
                i.putExtra("polylineOptions", _polylineOptions);

                startActivity(i);
            }
        });

        try {
            new DirectionFinder(this,
                    String.valueOf(_myLatLng.latitude) + "," + String.valueOf(_myLatLng.longitude),
                    String.valueOf(_destLatLng.latitude) + "," + String.valueOf(_destLatLng.longitude),
                    "walking").execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDirectionFinderStart() {
        _progressDialog = ProgressDialog.show(this, "Please wait!", "Processing...", true);
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        _progressDialog.dismiss();
        if (routes.isEmpty())
        {
            Toast.makeText(this, "No routes found!", Toast.LENGTH_LONG).show();
            return;
        }

        Route route = routes.get(0);

        ((TextView) findViewById(R.id.tvDistance)).setText("Distance: " + route.distance.text);
        ((TextView) findViewById(R.id.tvDuration)).setText("Walking duration: " + route.duration.text);

        _polylineOptions = new PolylineOptions().
                geodesic(true).
                color(Color.RED).
                width(8);

        for (int i = 0; i < route.points.size(); i++)
            _polylineOptions.add(route.points.get(i));
    }
}
