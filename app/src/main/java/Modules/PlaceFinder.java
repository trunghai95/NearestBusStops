package Modules;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hai on 23/04/2016.
 */
public class PlaceFinder {
    private static final String _PLACES_API_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?sensor=true&rankby=distance&mode=walking";
    private String _apiKey;
    private PlaceFinderListener _listener;
    private String _placeType;
    private LatLng _origin;

    public PlaceFinder(PlaceFinderListener listener, LatLng origin, String placeType, String apiKey)
    {
        _listener = listener;
        _origin = origin;
        _placeType = placeType;
        _apiKey = apiKey;
    }

    public void execute()
    {
        _listener.onPlaceFinderStart();
        new DownloadRawData().execute(_PLACES_API_URL
                + "&location=" + _origin.latitude + "," + _origin.longitude
                + "&key=" + _apiKey
                + "&types=" + _placeType);
    }

    private class DownloadRawData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String link = params[0];
            try {
                URL url = new URL(link);
                InputStream is = url.openConnection().getInputStream();
                StringBuffer buffer = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                return buffer.toString();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String res) {
            try {
                parseJSon(res);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void parseJSon(String data) throws JSONException
    {
        if (data == null)
            return;

        List<Place> places = new ArrayList<>();
        JSONObject jsonData = new JSONObject(data);
        JSONArray jsonResults = jsonData.getJSONArray("results");

        for (int i = 0; i < jsonResults.length(); ++i)
        {
            JSONObject result = jsonResults.getJSONObject(i);
            String name = result.getString("name");
            JSONObject jsonLocation = result.getJSONObject("geometry").getJSONObject("location");
            LatLng latlng = new LatLng(jsonLocation.getDouble("lat"), jsonLocation.getDouble("lng"));
            places.add(new Place(name, latlng));
        }

        _listener.onPlaceFinderSuccess(places);
    }
}
