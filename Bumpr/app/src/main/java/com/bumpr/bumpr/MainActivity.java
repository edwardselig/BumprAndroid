package com.bumpr.bumpr;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    CallbackManager callbackManager;
    //RequestQueue queue = Volley.newRequestQueue(this);
    @Override
    public void onCreate(Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile","email","user_friends"));
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        Bundle parameters = new Bundle();
                        parameters.putString("fields","id,name,email,picture,gender,first_name,last_name,age_range"); new GraphRequest(
                                AccessToken.getCurrentAccessToken(),
                                "/me",
                                parameters,
                                HttpMethod.GET,
                                new GraphRequest.Callback() {
                                    public void onCompleted(GraphResponse response) {

                                        try{
                                            JSONObject obj = response.getJSONObject();
                                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                            SharedPreferences.Editor editor = preferences.edit();
                                            int distance = 100;
                                            editor.putInt("distance", distance);
                                            editor.commit();
                                            int timer = 300;
                                            editor.putInt("timer", timer);
                                            editor.commit();
                                            String desiredGender = "male";
                                            editor.putString("gender", desiredGender);
                                            editor.commit();
                                            String id = obj.getString("id");
                                            editor.putString("id", id);
                                            editor.commit();
                                            Map<String,?> keys = preferences.getAll();
                                            String first = obj.getString("first_name");
                                            String email = obj.getString("email");
                                            String last = obj.getString("last_name");
                                            String gender = obj.getString("gender");;
                                            String urlString ="http://www.socialgainz.com/Bumpr/facebooklogin.php?id=" + id + "&first=" + first + "&last=" + last + "&email=" + email + "&gender=" + gender + "&desiredgender=" + desiredGender + "&distance=" + Integer.toString(distance);

                                            //try {
                                                //String urlString1 = URLEncoder.encode(urlString, "UTF-8");
                                            final String urlString1 = urlString.replaceAll(" ", "%20");
                                            //}catch(UnsupportedEncodingException e) {
                                              //  e.printStackTrace();
                                            //}
                                            new Thread(new Runnable() {
                                                public void run() {
                                                    try{
                                                        URL url = new URL(urlString1);
                                                        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                                                        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                                                        String resp = readStream(in);
                                                    }catch(Exception e){
                                                        e.printStackTrace();
                                                    }

                                                }
                                            }).start();
                                            final Button button = (Button) findViewById(R.id.button);
                                            button.setOnClickListener(new View.OnClickListener() {
                                                public void onClick(View v) {
                                                    startActivity(new Intent(MainActivity.this, MapsActivity.class));
                                                }
                                            });
                                            final Button button2 = (Button) findViewById(R.id.button2);
                                            button2.setOnClickListener(new View.OnClickListener() {
                                                public void onClick(View v) {
                                                    startActivity(new Intent(MainActivity.this, FragmentPreferences.class));
                                                }
                                            });
                                            final Button button3 = (Button) findViewById(R.id.button3);
                                            button3.setOnClickListener(new View.OnClickListener() {
                                                public void onClick(View v) {
                                                    startActivity(new Intent(MainActivity.this, FragmentPreferences.class));
                                                }
                                            });
                                            //startActivity(new Intent(MainActivity.this, MapsActivity.class));

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
            /* handle the result */
                                    }
                                }
                        ).executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        Log.d("myTag", "This is my message");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });
    }
    private String readStream(InputStream is) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int i = is.read();
            while(i != -1) {
                bo.write(i);
                i = is.read();
            }
            return bo.toString();
        } catch (IOException e) {
            return "";
        }
    }
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}

