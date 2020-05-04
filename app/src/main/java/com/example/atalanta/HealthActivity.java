package com.example.atalanta;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.client.Result;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.Empty;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

public class HealthActivity extends AppCompatActivity {

    private static final String CLIENT_ID = "94dcadb5863349829ae406bae1fff241";
    private static final String REDIRECT_URI = "com.example.atalanta://callback";
    private SpotifyAppRemote mSpotifyAppRemote;
    private static final int REQUEST_OAUTH_REQUEST_CODE = 0x1001;
    private String[] slowSongs = new String[]{};
    private String[] fastSongs = new String[]{};
    private Boolean isPaused = false;
    private PlayerState playerState = null;
    private Boolean replayIgnore = false;
    private String oldTrackInfo = "";
    private Boolean signedIntoSpotify = false;
    private Boolean signedIntoGoogleFit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health);

        requestPlaylistInfo(new ResponseListener() {
                @Override
                public void onSuccess(String result, String speed) {
                    try {
                        //String sci = result.substring(0,18);
                        String sci = result.replace("\n", "").replaceAll("\\s+", "");
                        System.out.print(sci);
                        String temp = sci.replace("{\"track\":{\"id\":\"", "");

                        String temp2 = temp.replace("\"}}", "");
                        String[] temp3 = temp2.substring(10, temp2.length() - 2).split(",");

                        if (speed == "slow" && result != "") {
                            slowSongs = temp3.clone();
                        } else if (speed == "fast" && result != "") {
                            fastSongs = temp3.clone();
                        }

                    } catch (IndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                }
            }, true);

        requestPlaylistInfo(new ResponseListener() {
            @Override
            public void onSuccess(String result, String speed) {
                try {
                    //String sci = result.substring(0,18);
                    String sci = result.replace("\n", "").replaceAll("\\s+", "");
                    System.out.print(sci);
                    String temp = sci.replace("{\"track\":{\"id\":\"", "");

                    String temp2 = temp.replace("\"}}", "");
                    String[] temp3 = temp2.substring(10, temp2.length() - 2).split(",");

                    if (speed == "fast" && result != "") {
                        fastSongs = temp3.clone();
                    }

                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }
        }, false);

        signInGoogleFit();

        }


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume(){
        super.onResume();
        if (SpotifyAppRemote.isSpotifyInstalled(this)){
             signInSpotify();
        } else {
            Toast.makeText(this, "Must have Spotify installed to listen to music.", Toast.LENGTH_SHORT).show();
            TextView tv = findViewById(R.id.songText);
            signedIntoSpotify = false;
        }
    }

    public void signInGoogleFit(){
        FitnessOptions fitnessOptions =
                FitnessOptions.builder()
                        .addDataType(DataType.TYPE_DISTANCE_CUMULATIVE)
                        .addDataType(DataType.TYPE_DISTANCE_DELTA)
                        .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                        .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
                        .build();
        if (!signedIntoGoogleFit) {
            GoogleSignIn.requestPermissions(
                    this,
                    REQUEST_OAUTH_REQUEST_CODE,
                    GoogleSignIn.getLastSignedInAccount(this),
                    fitnessOptions);
            if(GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), fitnessOptions)){
               signedIntoGoogleFit = true;
                subscribe();
                readData();
            }
        } else {
            signedIntoGoogleFit = true;
            subscribe();
            readData();
        }
    }

    public void signInSpotify(){
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.connect(this, connectionParams,
                new Connector.ConnectionListener() {

                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        android.util.Log.d("HealthAcitivty", "Connected! Yay!");
                        signedIntoSpotify = true;
                        connected();
                        Button b = findViewById(R.id.nextSong);
                        b.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_keyboard_arrow_right_black_24dp, 0, 0, 0);
                        b = findViewById(R.id.backSong);
                        b.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_keyboard_arrow_left_black_24dp, 0, 0, 0);
                        b = findViewById(R.id.playSong);
                        b.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_play_arrow_black_24dp, 0, 0, 0);
                        // Now you can start interacting with App Remote

                    }

                    public void onFailure(Throwable throwable) {
                        android.util.Log.e("MyActivity", throwable.getMessage(), throwable);

                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });
    }

    private void connected() {
        // Subscribe to PlayerState
        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(playerState -> {
                    final Track track = playerState.track;
                    if (track != null) {
                        TextView tv = findViewById(R.id.songText);
                        tv.setText(track.name + "\n by \n" + track.artist.name);
                        tv.setTextSize(20);
                    }
                    this.playerState = playerState;

                    String currentTrackInfo = track.name + track.artist.name;

                    TextView tv = findViewById(R.id.heartRateTextView);
                    int currentHeartRate = Integer.valueOf(tv.getText().toString());
                    EditText et = findViewById(R.id.targetHeartRateEditView);
                    int targetHeartRate = Integer.valueOf(et.getText().toString());

                    if(!replayIgnore && currentTrackInfo == oldTrackInfo) {
                        if (currentHeartRate >= targetHeartRate) {
                            oldTrackInfo = currentTrackInfo;
                            mSpotifyAppRemote.getPlayerApi().play("spotify:track:" + fastSongs[(int) (Math.random() * ((fastSongs.length) + 1))]);
                            Button b = findViewById(R.id.playSong);
                            b.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pause_black_24dp, 0, 0, 0);
                            replayIgnore = false;

                        } else {
                            oldTrackInfo = currentTrackInfo;
                            mSpotifyAppRemote.getPlayerApi().play("spotify:track:" + slowSongs[(int) (Math.random() * ((slowSongs.length) + 1))]);
                            Button b = findViewById(R.id.playSong);
                            b.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pause_black_24dp, 0, 0, 0);
                            replayIgnore = false;

                        }
                    }

                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_OAUTH_REQUEST_CODE) {
                subscribe();
            }
        }
    }

    /**
     * Records step data by requesting a subscription to background step data.
     */
    public void subscribe() {
        // To create a subscription, invoke the Recording API. As soon as the subscription is
        // active, fitness data will start recording.
        Fitness.getRecordingClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .subscribe(DataType.TYPE_DISTANCE_CUMULATIVE)
                .addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.i("Google Fit Sign in", "Successfully subscribed!");
                                } else {
                                    Log.w("Google Fit Sign in", "There was a problem subscribing Distance.", task.getException());
                                }
                            }
                        });
        Fitness.getRecordingClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .subscribe(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.i("Google Fit Sign in", "Successfully subscribed!");
                                } else {
                                    Log.w("Google Fit Sign in", "There was a problem subscribing Step Count.", task.getException());
                                }
                            }
                        });
    }

    /**
     * Reads the current daily step total, computed from midnight of the current day on the device's
     * current timezone.
     */
    private void readData() {
        TextView tvDistance = findViewById(R.id.distanceTextView);
        TextView tvStep = findViewById(R.id.stepsTextView);
        Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .readDailyTotal(DataType.TYPE_DISTANCE_DELTA)
                .addOnSuccessListener(
                        new OnSuccessListener<DataSet>() {
                            @Override
                            public void onSuccess(DataSet dataSet) {
                                float total =
                                        dataSet.isEmpty()
                                                ? 0
                                                : dataSet.getDataPoints().get(0).getValue(Field.FIELD_DISTANCE).asFloat() / 1609;
                                BigDecimal bd = new BigDecimal(total).setScale(2, RoundingMode.HALF_UP);
                                tvDistance.setText("Distance:\n\n" + String.valueOf(bd.doubleValue()) + " miles");
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                tvDistance.setText("There was a problem getting the distance traveled.");
                            }
                        });

        Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
                .addOnSuccessListener(
                        new OnSuccessListener<DataSet>() {
                            @Override
                            public void onSuccess(DataSet dataSet) {
                                long total =
                                        dataSet.isEmpty()
                                                ? 0
                                                : dataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
                                tvStep.setText("Steps:\n\n" + String.valueOf(total));
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                tvStep.setText("There was a problem getting the step count.");
                            }
                        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the main; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.healthmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.refreshData){
            readData();
            return true;
        }
        if (id == R.id.signOut){
            signOut();
            return true;
        }
        if(id == R.id.signInSpotify){
            if (!signedIntoSpotify){
                signInSpotify();
            }
            return true;
        }
        if(id == R.id.signInSpotify){
            if (!signedIntoSpotify){
                signInSpotify();
            }
            return true;
        }
        if(id == R.id.signInGoogleFit){
            if (!signedIntoGoogleFit){
                signInGoogleFit();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onBackToMainButtonClick(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }

    //TODO: Move to profile page
    private void signOut() {
        Fitness.getConfigClient(this, GoogleSignIn.getLastSignedInAccount(this)).disableFit();
        signedIntoGoogleFit = false;
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
        signedIntoSpotify = false;
        TextView tv = findViewById(R.id.songText);
        tv.setText("");
        Toast.makeText(this, "Signed Out of Google Fit and Spotify", Toast.LENGTH_SHORT).show();
    }

    private void requestPlaylistInfo(final ResponseListener responseListener, Boolean isSlow){
        Thread t = new Thread() {
            @Override
            @TargetApi(Build.VERSION_CODES.M)
            public void run() {
                final RequestQueue requestQueue = Volley.newRequestQueue(HealthActivity.this);
                String server_urlpost = "https://accounts.spotify.com/api/token";

                StringRequest stringRequestpost = new StringRequest(Request.Method.POST, server_urlpost,

                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {   //Server Response Handler
                                //PostResponse.setText(response);
                                try {
                                    JSONObject jsonObj = new JSONObject(response);
                                    String accessToken = jsonObj.getString("access_token");
                                    if(isSlow){
                                        getSlowPlaylistInfo(responseListener, accessToken);
                                    } else {
                                        getFastPlaylistInfo(responseListener, accessToken);
                                    }
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
                        headers.put("Authorization", "Basic " + "OTRkY2FkYjU4NjMzNDk4MjlhZTQwNmJhZTFmZmYyNDE6MDgxMDI0NmFiOGZhNDM0MTk4ZTBkNmMwYzg2MzAyZDc=" );
                        return headers;
                    }
                };

                //Starts Request
                requestQueue.add(stringRequestpost);
            }
        };
        t.start();
    }

    private void getSlowPlaylistInfo(final ResponseListener responseListener, String code){
        Thread t = new Thread() {
            @Override
            @TargetApi(Build.VERSION_CODES.M)
            public void run() {
                final RequestQueue requestQueue = Volley.newRequestQueue(HealthActivity.this);
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
                        android.util.Log.d("token = ", code);
                        headers.put("Authorization", "Bearer " + code);
                        return headers;
                    }
                };

                //Starts Request
                requestQueue.add(stringRequestpostSlow);

            }
        };
        t.start();
    }

    private void getFastPlaylistInfo(final ResponseListener responseListener, String code){
        Thread t = new Thread() {
            @Override
            @TargetApi(Build.VERSION_CODES.M)
            public void run() {
                final RequestQueue requestQueue = Volley.newRequestQueue(HealthActivity.this);
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
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<String, String>();
                        android.util.Log.d("token = ", code);
                        headers.put("Authorization", "Bearer " + code);
                        return headers;
                    }
                };
                //Starts Request
                requestQueue.add(stringRequestpostFast);
            }
        };
            t.start();
        }



        public void backSong(View view){
        if (!signedIntoSpotify){
            Toast.makeText(this, "Must Sign in to Spotify to use.", Toast.LENGTH_SHORT).show();
            return;
        } else {
            mSpotifyAppRemote.getPlayerApi().skipPrevious();
            Button b = findViewById(R.id.playSong);
            b.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pause_black_24dp, 0, 0, 0);
        }
    }

    public void playSong(View view){
        if (!signedIntoSpotify){
            Toast.makeText(this, "Must Sign in to Spotify to use.", Toast.LENGTH_SHORT).show();
            return;
        } else {
            Button b = findViewById(R.id.playSong);

            if (playerState.isPaused) {
                mSpotifyAppRemote.getPlayerApi().resume();
                b.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pause_black_24dp, 0, 0, 0);
                replayIgnore = true;
            } else {
                mSpotifyAppRemote.getPlayerApi().pause();
                b.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_play_arrow_black_24dp, 0, 0, 0);
                replayIgnore = true;
            }
        }
    }

    public void nextSong(View view){
        if (!signedIntoSpotify){
            Toast.makeText(this, "Must Sign in to Spotify to use.", Toast.LENGTH_SHORT).show();
            return;
        } else {
            TextView tv = findViewById(R.id.heartRateValueTextView);
            int currentHeartRate = Integer.valueOf(tv.getText().toString());
            EditText et = findViewById(R.id.targetHeartRateEditView);
            int targetHeartRate = Integer.valueOf(et.getText().toString());

            String currentTrackInfo = playerState.track.name + playerState.track.artist.name;

            if (currentHeartRate >= targetHeartRate) {
                oldTrackInfo = currentTrackInfo;
                mSpotifyAppRemote.getPlayerApi().play("spotify:track:" + fastSongs[(int) (Math.random() * ((fastSongs.length) + 1))]);
                Button b = findViewById(R.id.playSong);
                b.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pause_black_24dp, 0, 0, 0);

            } else {
                oldTrackInfo = currentTrackInfo;
                mSpotifyAppRemote.getPlayerApi().play("spotify:track:" + slowSongs[(int) (Math.random() * ((slowSongs.length) + 1))]);
                Button b = findViewById(R.id.playSong);
                b.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pause_black_24dp, 0, 0, 0);

            }
        }
        }



}