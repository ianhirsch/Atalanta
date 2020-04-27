package com.example.atalanta;
/**
 * This activity is based on example code from Google Map's DirectionsJSONParser.java
 * https://github.com/firebase/quickstart-android/tree/master/auth
 *
 * author: Ting-Hung Lin
 */

import android.util.Log;
import android.util.Pair;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;


public class DirectionsJSONParser {

    /** Receives a JSONObject and returns a list of lists containing LatLng */
    public ArrayList<List<LatLng>> parse(JSONObject jObject){
        ArrayList<List<LatLng>> routes = new ArrayList<>();
        try {
            routes.add(helperFunc(jObject)); // First Route is the rootRoute

            JSONArray alterRoutes = jObject.getJSONObject("route").getJSONArray("alternateRoutes");

            for(int i=0;i<alterRoutes.length();i++) {
                JSONObject alterRoute = alterRoutes.getJSONObject(i);
                routes.add(helperFunc(alterRoute)); // grow the list of routes
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
        }
        return routes;
    }
    private List<LatLng> helperFunc(JSONObject jObject)
    {
        List<LatLng> path = new ArrayList<>(); // first two element for bounding box; path list doesn't include destination latlng
        try {
            JSONObject rootRoute = jObject.getJSONObject("route");
            Double distance = rootRoute.getDouble("distance");
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
        }catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
        }
        return path;
    }
}