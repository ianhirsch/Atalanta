package com.example.atalanta;
/**
 * This activity is based on example code from Google Map's DirectionsJSONParser.java
 * https://github.com/firebase/quickstart-android/tree/master/auth
 *
 * author: Ting-Hung Lin
 */


import com.google.android.gms.maps.model.LatLng;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;


public class DirectionsJSONParser {

    /** Receives a JSONObject and returns a list of lists containing LatLng */
    public List<List<List<LatLng>>> parse(JSONObject jObject){
        List wrapper = new ArrayList();

        List<Double> dist = new ArrayList<>();
        List<List<LatLng>> routes = new ArrayList<>();

        List helper;
        try {
            helper = helperFunc(jObject);
            dist.add((Double)helper.get(0));
            routes.add((List<LatLng>)helper.get(1)); // First Route is the rootRoute

            JSONArray alterRoutes = jObject.getJSONObject("route").getJSONArray("alternateRoutes");
            for(int i=0;i<alterRoutes.length();i++) {
                JSONObject alterRoute = alterRoutes.getJSONObject(i);
                helper = helperFunc(alterRoute);
                dist.add((Double)helper.get(0));
                routes.add((List<LatLng>)helper.get(1)); // grow the list with alternative routes
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
        }
        wrapper.add(dist);
        wrapper.add(routes);
        return wrapper;
    }

    /**
     *
     * @param jObject
     * @return
     * first object in list is distance
     * second object in list is the List<LatLng>
     */
    private List helperFunc(JSONObject jObject)
    {
        List wrapper = new ArrayList();
        List<LatLng> path = new ArrayList<>(); // first two element for bounding box; path list doesn't include destination latlng
        try {
            JSONObject rootRoute = jObject.getJSONObject("route");
            Double distance = rootRoute.getDouble("distance");
            wrapper.add(distance);
            JSONObject boundingBox = rootRoute.getJSONObject("boundingBox");
            Double SELng = boundingBox.getJSONObject("lr").getDouble("lng");
            Double SELat = boundingBox.getJSONObject("lr").getDouble("lat");
            Double NWLng = boundingBox.getJSONObject("ul").getDouble("lng");
            Double NWLat = boundingBox.getJSONObject("ul").getDouble("lat");
            LatLng SWBound = new LatLng(SELat, NWLng);
            path.add(SWBound);
            LatLng NEBound = new LatLng(NWLat, SELng);
            path.add(NEBound);
            JSONArray legs = rootRoute.getJSONArray("legs");
            for (int i = 0; i < legs.length(); i++) {
                JSONObject leg = legs.getJSONObject(i);
                Double legDistance = leg.getDouble("distance");
                JSONArray maneuvers = leg.getJSONArray("maneuvers");
                for (int j = 0; j < maneuvers.length(); j++) {
                    JSONObject maneuver = maneuvers.getJSONObject(j);
                    String narrative = maneuver.getString("narrative");
                    int index = maneuver.getInt("index");
                    JSONObject startPoint = maneuver.getJSONObject("startPoint");
                    LatLng latLng = new LatLng(startPoint.getDouble("lat"), startPoint.getDouble("lng"));
                    path.add(latLng);
                }
            }
            wrapper.add(path);
        }catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
        }
        return wrapper;
    }
}