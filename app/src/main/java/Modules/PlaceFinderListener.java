package Modules;

import java.util.List;

/**
 * Created by Hai on 23/04/2016.
 */
public interface PlaceFinderListener {
    void onPlaceFinderStart();
    void onPlaceFinderSuccess(List<Place> places);
}
