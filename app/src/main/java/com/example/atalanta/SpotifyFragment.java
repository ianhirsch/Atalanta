package com.example.atalanta;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class SpotifyFragment extends AppCompatActivity {

    private static final String CLIENT_ID = "94dcadb5863349829ae406bae1fff241";
    private static final String REDIRECT_URI = "com.example.atalanta://callback";
    private static  final int REQUEST_CODE = 1337;
    private SpotifyAppRemote mSpotifyAppRemote;
    private String accessToken = "";
    private String responseString = "";
    private String[] slowSongs = new String[]{};
    private String[] fastSongs = new String[]{};


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health);
        getPlaylistInfo(new ResponseListener() {
            @Override
            public void onSuccess(String result, String speed) {
                responseString = result;
                System.out.print(responseString);
                try {
                //String sci = result.substring(0,18);
                    String sci = result.replace("\n","").replaceAll("\\s+","");
                System.out.print(sci);
                String temp = sci.replace("{\"track\":{\"id\":\"", "");

                String temp2 = temp.replace("\"}}", "");
                String[] temp3 = temp2.substring(10, temp2.length()-2).split(",");

                    if (speed == "slow") {
                        slowSongs = temp3;
                    } else if (speed == "fast") {
                        fastSongs = temp3;
                    }

                    System.out.println(fastSongs);
                    System.out.println(slowSongs);

                }catch (IndexOutOfBoundsException e){
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

//        ConnectionParams connectionParams =
//                new ConnectionParams.Builder(CLIENT_ID)
//                        .setRedirectUri(REDIRECT_URI)
//                        .showAuthView(true)
//                        .build();
//
//        SpotifyAppRemote.connect(this, connectionParams,
//                new Connector.ConnectionListener() {
//
//                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
//                        mSpotifyAppRemote = spotifyAppRemote;
//                        Log.d("MainActivity", "Connected! Yay!");
//
//                        // Now you can start interacting with App Remote
//                        connected();
//
//                    }
//
//                    public void onFailure(Throwable throwable) {
//                        Log.e("MyActivity", throwable.getMessage(), throwable);
//
//                        // Something went wrong when attempting to connect! Handle errors here
//                    }
//                });
    }


    @Override
    protected void onStop() {
        super.onStop();
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }

    private void connected() {

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);

            accessToken = response.getAccessToken();

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    // Handle successful response
                    //tokenExchange();
                    break;

                // Auth flow returned an error
                case ERROR:
                    // Handle error response
                    break;

                // Most likely auth flow was cancelled
                default:
                    // Handle other cases
            }
        }
    }

    private void getPlaylistInfo(final ResponseListener responseListener){


        Thread t = new Thread() {
            @Override
            @TargetApi(Build.VERSION_CODES.M)
            public void run() {
                final RequestQueue requestQueue = Volley.newRequestQueue(SpotifyFragment.this);
                String server_urlpost = "https://accounts.spotify.com/api/token";

                StringRequest stringRequestpost = new StringRequest(Request.Method.POST, server_urlpost,

                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {   //Server Response Handler
                                //PostResponse.setText(response);
                                try {
                                    JSONObject jsonObj = new JSONObject(response);
                                    String accessToken = jsonObj.getString("access_token");
                                    startSong(responseListener, accessToken);
                                    requestQueue.stop();
                                }catch (JSONException e){

                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {    //On Error Response Handler
                        //PostResponse.setText("Something went wrong...");
                        error.printStackTrace();
                        requestQueue.stop();
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> params = new HashMap<String, String>();
                        params.put("grant_type","client_credentials");

                        //Log.i(TAG, params.toString());
                        return params;
                    }
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String,String> headers=new HashMap<String,String>();
                        headers.put("Authorization", "Basic OTRkY2FkYjU4NjMzNDk4MjlhZTQwNmJhZTFmZmYyNDE6MDgxMDI0NmFiOGZhNDM0MTk4ZTBkNmMwYzg2MzAyZDc=");
                        return headers;
                    }
                };

                //Starts Request
                requestQueue.add(stringRequestpost);
            }
        };
        t.start();
    }

    private void startSong(final ResponseListener responseListener, String code){
        Thread t = new Thread() {
            @Override
            @TargetApi(Build.VERSION_CODES.M)
            public void run() {
                final RequestQueue requestQueue = Volley.newRequestQueue(SpotifyFragment.this);
                String playlist_idSlow = "37i9dQZF1DXc7KgLAqOCoC";
                String server_urlpostSlow = "https://api.spotify.com/v1/playlists/37i9dQZF1DXc7KgLAqOCoC/tracks/?fields=items(track(id))";

                StringRequest stringRequestpostSlow = new StringRequest(Request.Method.GET, server_urlpostSlow,

                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {   //Server Response Handler
                                //PostResponse.setText(response);
                                responseListener.onSuccess(response, "slow");
                                requestQueue.stop();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {    //On Error Response Handler
                        //PostResponse.setText("Something went wrong...");
                        error.printStackTrace();
                        requestQueue.stop();
                    }
                }){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String,String> headers=new HashMap<String,String>();
                        Log.d("token = ", accessToken);
                        headers.put("Authorization", "Bearer " + code);
                        return headers;
                    }
                };

                String playlist_idFast = "spotify:playlist:37i9dQZF1DX8jnAPF7Iiqp";
                String server_urlpostFast = "https://api.spotify.com/v1/playlists/37i9dQZF1DX8jnAPF7Iiqp/tracks/?fields=items(track(id))";

                StringRequest stringRequestpostFast = new StringRequest(Request.Method.GET, server_urlpostFast,

                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {   //Server Response Handler
                                //PostResponse.setText(response);
                                responseListener.onSuccess(response, "fast");
                                requestQueue.stop();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {    //On Error Response Handler
                        //PostResponse.setText("Something went wrong...");
                        error.printStackTrace();
                        requestQueue.stop();
                    }
                }){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String,String> headers=new HashMap<String,String>();
                        Log.d("token = ", accessToken);
                        headers.put("Authorization", "Bearer " + code);
                        return headers;
                    }
                };

                //Starts Request
                requestQueue.add(stringRequestpostSlow);
                requestQueue.add(stringRequestpostFast);

            }
        };
        t.start();
    }
}