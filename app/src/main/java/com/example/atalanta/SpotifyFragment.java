package com.example.atalanta;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.util.JsonReader;
import android.util.Log;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Track;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


import javax.net.ssl.HttpsURLConnection;

public class SpotifyFragment extends AppCompatActivity {

    private static final String CLIENT_ID = "94dcadb5863349829ae406bae1fff241";
    private static final String REDIRECT_URI = "com.example.atalanta://callback";
    private SpotifyAppRemote mSpotifyAppRemote;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health);

//        AuthenticationRequest.Builder builder =
//                new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
//
//        builder.setScopes(new String[]{"streaming", "user-read-private"});
//        AuthenticationRequest request = builder.build();
//
//        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    @Override
    protected void onStart() {
        super.onStart();

        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.connect(this, connectionParams,
                new Connector.ConnectionListener() {

                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        Log.d("MainActivity", "Connected! Yay!");

                        // Now you can start interacting with App Remote
                        connected();

                    }

                    public void onFailure(Throwable throwable) {
                        Log.e("MyActivity", throwable.getMessage(), throwable);

                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });
    }


    @Override
    protected void onStop() {
        super.onStop();
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }

    private void connected() {

//        /*  */
        String html = "<iframe src=\"https://open.spotify.com/embed/playlist/37i9dQZF1DX8jnAPF7Iiqp\" width=\"300\" height=\"80\" frameborder=\"0\" allowtransparency=\"true\" allow=\"encrypted-media\"></iframe>";
        WebView wv = findViewById(R.id.webViewFast);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.loadData(html, "text/html", null);

        html = "<iframe src=\"https://open.spotify.com/embed/playlist/37i9dQZF1DXadOVCgGhS7j\" width=\"300\" height=\"80\" frameborder=\"0\" allowtransparency=\"true\" allow=\"encrypted-media\"></iframe>";
        wv = findViewById(R.id.webViewSlow);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.loadData(html, "text/html", null);


        // Play a playlist
        //mSpotifyAppRemote.getPlayerApi().play("spotify:playlist:6bjKFnBgg2qLmRSwJbEKTC");

        mSpotifyAppRemote.getPlayerApi().queue("spotify:playlist:37i9dQZF1DX8jnAPF7Iiqp");

        // Subscribe to PlayerState
        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(playerState -> {
                    final Track track = playerState.track;
                    if (track != null) {
                        Log.d("MainActivity", track.name + " by " + track.artist.name);
                    }
                });

        /* Start Step 1 of Authentication*/

//        Thread t = new Thread() {
//            @Override
//            @TargetApi(Build.VERSION_CODES.M)
//            public void run() {
//               try {
//                String q =  "client_id=" + CLIENT_ID +"&response_type=code" + "&redirect_uri="
//                        + REDIRECT_URI + "&scope="+
//                        //scopes here
//                        "user-read-private";
//                    String url = ("https://accounts.spotify.com/authorize?"
//                            + q);
//                    URL u= new URL(url);
//                    HttpsURLConnection connect = (HttpsURLConnection) u.openConnection();
//                    int val = connect.getResponseCode();
//                    if (connect.getResponseCode() == 200) {
//
//                    }
//                } catch (MalformedURLException e) {
//                    Log.println(Log.ERROR, "Spotify Error", "BAD URL");
//                    Log.println(Log.ERROR, "fdsa", "e");
//                } catch (IOException e) {
//                    Log.println(Log.ERROR, "ERRORROROROROR", e.toString());
//                } catch ( Error e) {
//                    System.out.println(e);
//                }
//
//            }
//        };
//        t.start();




        /*Code to send to Spotify App*/

//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setData(Uri.parse("spotify:album:0sNOF9WDwhWunNAHPD3Baj"));
//        intent.putExtra(Intent.EXTRA_REFERRER,
//                Uri.parse("android-app://" + getApplicationContext().getPackageName()));
//        startActivity(intent);
    }

//    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
//        super.onActivityResult(requestCode, resultCode, intent);
//
//        // Check if result comes from the correct activity
//        if (requestCode == REQUEST_CODE) {
//            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
//
//            accessToken = response.getAccessToken();
//
//            switch (response.getType()) {
//                // Response was successful and contains auth token
//                case TOKEN:
//                    // Handle successful response
//                    //tokenExchange(accessToken);
//                    break;
//
//                // Auth flow returned an error
//                case ERROR:
//                    // Handle error response
//                    break;
//
//                // Most likely auth flow was cancelled
//                default:
//                    // Handle other cases
//            }
//        }
//    }

//    private void tokenExchange(String accessToken){

    /* Start tep 2 of authentication */

//        Thread t = new Thread() {
//            @Override
//            @TargetApi(Build.VERSION_CODES.M)
//            public void run() {
//                final RequestQueue requestQueue = Volley.newRequestQueue(SpotifyFragment.this);
//                String server_urlpost = "https://accounts.spotify.com/api/token";
//
//                StringRequest stringRequestpost = new StringRequest(Request.Method.POST, server_urlpost,
//
//                        new Response.Listener<String>() {
//                            @Override
//                            public void onResponse(String response) {   //Server Response Handler
//                                PostResponse.setText(response);
//                                requestQueue.stop();
//                            }
//                        }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {    //On Error Response Handler
//                        PostResponse.setText("Something went wrong...");
//                        error.printStackTrace();
//                        requestQueue.stop();
//                    }
//                }){
//                    @Override
//                    protected Map<String, String> getParams() throws AuthFailureError {
//                        Map<String,String> params = new HashMap<String, String>();
//                        params.put("grant_type","authorization_code");
//                        params.put("code", accessToken);
//                        params.put("redirect_uri", REDIRECT_URI);
//
//                        //Log.i(TAG, params.toString());
//                        return params;
//                    }
//                    @Override
//                    public Map<String, String> getHeaders() throws AuthFailureError {
//                        Map<String,String> headers=new HashMap<String,String>();
//                        headers.put("Authorization:", " Basic " + );
//
//                        return headers;
//                    }
//                };
//
//                //Starts Request
//                requestQueue.add(stringRequestpost);
//
//            }
//        };
//        t.start();
//    }
}