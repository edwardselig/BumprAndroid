package com.bumpr.bumpr;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Hashtable;
import java.util.Map;

public class profileClick extends AppCompatActivity implements View.OnClickListener  {

    private Button buttonChoose;
    private Button buttonUpload;

    private ImageView imageView;

    private EditText editTextName;

    private Bitmap bitmap;

    private int PICK_IMAGE_REQUEST = 1;

    private String UPLOAD_URL ="http://socialgainz.com/Bumpr/androidupload.php";

    private String KEY_IMAGE = "image";
    private String KEY_NAME = "name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_click);

        buttonChoose = (Button) findViewById(R.id.buttonChoose);
        buttonUpload = (Button) findViewById(R.id.buttonUpload);

        editTextName = (EditText) findViewById(R.id.editText);

        imageView  = (ImageView) findViewById(R.id.imageView);

        buttonChoose.setOnClickListener(this);
        buttonUpload.setOnClickListener(this);
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void uploadImage(){
        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(this,"Uploading...","Please wait...",false,false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        //Showing toast message of the response
                        //Toast.makeText(profileClick.this, s , Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();

                        //Showing toast
                        //Toast.makeText(profileClick.this, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                String image = getStringImage(bitmap);
                Log.d("myTag",image);
                String type = "multipart/form-data; boundary=---------------------------14737809831466499882746641449";
                try{
                    URL u = new URL(UPLOAD_URL);
                    HttpURLConnection conn = (HttpURLConnection) u.openConnection();
                    conn.setDoOutput(true);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty( "Content-Type", type );
                }catch(Exception e){

                }

                //Getting Image Name
                String name = editTextName.getText().toString().trim();

                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();

                //Adding parameters
                params.put(KEY_IMAGE, image);
                params.put(KEY_NAME, name);

                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final Uri uri = data.getData();
        final Intent data1 = data;
        //Log.d("myTag","POOPY" + getRealPathFromURI(uri));
        new Thread(new Runnable() {
            public void run() {
                try{
                    Log.d("myTag", "hello");
                    //if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                    String realPath;
                    // SDK < API11
                    if (Build.VERSION.SDK_INT < 11)
                        realPath = getRealPathFromURI_BelowAPI11(profileClick.this, data1.getData());

                        // SDK >= 11 && SDK < 19
                    else if (Build.VERSION.SDK_INT < 19)
                        realPath = getRealPathFromURI_API11to18(profileClick.this, data1.getData());

                        // SDK > 19 (Android 4.4)
                    else
                        realPath = getRealPathFromURI_API19(profileClick.this, data1.getData());

                        //Uri filePath = data.getData();
                        Log.d("myTag", "hello1");
                        HttpURLConnection connection = null;
                        DataOutputStream outputStream = null;
                        DataInputStream inputStream = null;
                        String pathToOurFile = realPath;
                        Log.d("myTag", "path" + pathToOurFile);
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        String id = preferences.getString("id","");
                        String urlServer = UPLOAD_URL + "?id=" + id;
                        Log.d("myTag", urlServer);
                        String lineEnd = "\r\n";
                        String twoHyphens = "--";
                        String boundary =  "*****";

                        int bytesRead, bytesAvailable, bufferSize;
                        byte[] buffer;
                        int maxBufferSize = 15*1024*1024;

                        try
                        {
                            Log.d("myTag", "hello2");
                            FileInputStream fileInputStream = new FileInputStream(new File(pathToOurFile) );

                            URL url = new URL(urlServer);
                            connection = (HttpURLConnection) url.openConnection();

                            // Allow Inputs &amp; Outputs.
                            connection.setDoInput(true);
                            connection.setDoOutput(true);
                            connection.setUseCaches(false);

                            // Set HTTP method to POST.
                            connection.setRequestMethod("POST");

                            connection.setRequestProperty("Connection", "Keep-Alive");
                            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);

                            outputStream = new DataOutputStream( connection.getOutputStream() );
                            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                            outputStream.writeBytes("Content-Disposition: form-data; name=\"Bumpr\";filename=\"" + pathToOurFile +"\"" + lineEnd);
                            outputStream.writeBytes(lineEnd);

                            bytesAvailable = fileInputStream.available();
                            bufferSize = Math.min(bytesAvailable, maxBufferSize);
                            buffer = new byte[bufferSize];

                            // Read file
                            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                            while (bytesRead > 0)
                            {
                                outputStream.write(buffer, 0, bufferSize);
                                bytesAvailable = fileInputStream.available();
                                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                            }

                            outputStream.writeBytes(lineEnd);
                            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                            // Responses from the server (code and message)
                            int serverResponseCode = connection.getResponseCode();
                            String serverResponseMessage = connection.getResponseMessage();
                            Log.d("myTag", serverResponseMessage);

                            fileInputStream.close();
                            outputStream.flush();
                            outputStream.close();
                            Log.d("myTag", "hello3");
                        }
                        catch (Exception ex)
                        {
                            ex.printStackTrace();
                        }
                    //}
                }catch(Exception e){
                    e.printStackTrace();
                }

            }
        }).start();

    }

    @Override
    public void onClick(View v) {

        if(v == buttonChoose){
            showFileChooser();
        }

        if(v == buttonUpload){
            uploadImage();
        }
    }
    @SuppressLint("NewApi")
    public static String getRealPathFromURI_API19(Context context, Uri uri){
        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(uri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = { MediaStore.Images.Media.DATA };

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{ id }, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }


    @SuppressLint("NewApi")
    public static String getRealPathFromURI_API11to18(Context context, Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        String result = null;

        CursorLoader cursorLoader = new CursorLoader(
                context,
                contentUri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        if(cursor != null){
            int column_index =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            result = cursor.getString(column_index);
        }
        return result;
    }

    public static String getRealPathFromURI_BelowAPI11(Context context, Uri contentUri){
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        int column_index
                = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}
