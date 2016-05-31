package Modules;

import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Hai on 23/04/2016.
 */
public class Place {
    public String name;
    public LatLng latlng;

    public Place(String name, LatLng latlng)
    {
        this.name = name;
        this.latlng = latlng;
    }
}
