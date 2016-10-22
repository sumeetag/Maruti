package com.dsource.ekalpa.maruti;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.dsource.ekalpa.maruti.app.AppConfig;
import com.dsource.ekalpa.maruti.app.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    MultiAutoCompleteTextView vendorname;
    //String[] languages={"Android ","java","IOS","jasewe","JDBC","jaja"};
    Button start, end;

    //GPSTracker gps;
    //Geocoder geocoder;
    //String cityName, stateName, countryName;

    int a=0, flag = 0;
    String b,c;

    SharedPreferences sharedpreferences;
    public static final String entry = "entry";
    public static final String vendor = "vendor";
    public static final String start_time = "time";

    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        start = (Button) findViewById(R.id.btStart);
        end = (Button) findViewById(R.id.btEnd);
        vendorname = (MultiAutoCompleteTextView)findViewById(R.id.multiAutoCompleteTextView1);
        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,LoginActivity.vendor);

        start.setEnabled(false);
        vendorname.setAdapter(adapter);
        vendorname.setTokenizer(new SpaceTokenizer());
        end.setVisibility(View.INVISIBLE);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        vendorname.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                start.setEnabled(true);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(vendor, parent.getItemAtPosition(position).toString());
                editor.commit();
                b = parent.getItemAtPosition(position).toString();
            }
        });

        vendorname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag == 1) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    flag = 0;
                }else {
                    flag = 1;
                }
            }
        });


        sharedpreferences = getSharedPreferences(entry,
                Context.MODE_PRIVATE);


        if (sharedpreferences.contains(entry)) {
            a = sharedpreferences.getInt(entry, 0);
            b = sharedpreferences.getString(vendor, "");
            c = sharedpreferences.getString(start_time, "");

        }

        if (a == 0){
            start.setVisibility(View.VISIBLE);
            end.setVisibility(View.INVISIBLE);
        } else if (a == 1){
            end.setVisibility(View.VISIBLE);
            start.setVisibility(View.INVISIBLE);
            vendorname.setText(b);
            vendorname.setEnabled(false);
        }


        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                end.setVisibility(View.VISIBLE);
                start.setVisibility(View.INVISIBLE);
                vendorname.setEnabled(false);
                Calendar ca = Calendar.getInstance();
                System.out.println("Current time =&gt; "+ca.getTime());

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String formattedDate = df.format(ca.getTime());
                //Toast.makeText(getApplicationContext(), formattedDate, Toast.LENGTH_SHORT).show();
                /*gps = new GPSTracker(MainActivity.this);

                // check if GPS enabled
                if(gps.canGetLocation()){

                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();

                    geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                        cityName = addresses.get(0).getAddressLine(0);
                        stateName = addresses.get(0).getAddressLine(1);
                        countryName = addresses.get(0).getAddressLine(2);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                    *//*Toast.makeText(getApplicationContext(), cityName , Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(), stateName , Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(), countryName, Toast.LENGTH_LONG).show();*//*

                    *//*Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();*//*
                }else{
                    // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
                    gps.showSettingsAlert();
                }*/

                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putInt(entry, 1);
                editor.putString(start_time, formattedDate);
                editor.commit();
                //Toast.makeText(getApplicationContext(), countryName, Toast.LENGTH_LONG).show();
                c = formattedDate;
                checkStart(LoginActivity.emp_id, b, formattedDate);
            }
        });

        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start.setVisibility(View.VISIBLE);
                end.setVisibility(View.INVISIBLE);
                Calendar ca = Calendar.getInstance();
                System.out.println("Current time =&gt; "+ca.getTime());

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String formattedDate = df.format(ca.getTime());
                //Toast.makeText(getApplicationContext(), formattedDate, Toast.LENGTH_SHORT).show();
                vendorname.setEnabled(true);

                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putInt(entry, 0);
                editor.commit();

                checkEnd(LoginActivity.emp_id, b, c, formattedDate);
                SharedPreferences.Editor editor1 = sharedpreferences.edit();
                editor1.clear().commit();

                vendorname.setText("");

            }
        });
    }

    public class SpaceTokenizer implements MultiAutoCompleteTextView.Tokenizer {

        public int findTokenStart(CharSequence text, int cursor) {
            int i = cursor;

            while (i > 0 && text.charAt(i - 1) != ' ') {
                i--;
            }
            while (i < cursor && text.charAt(i) == ' ') {
                i++;
            }

            return i;
        }

        public int findTokenEnd(CharSequence text, int cursor) {
            int i = cursor;
            int len = text.length();

            while (i < len) {
                if (text.charAt(i) == ' ') {
                    return i;
                } else {
                    i++;
                }
            }

            return len;
        }

        public CharSequence terminateToken(CharSequence text) {
            int i = text.length();

            while (i > 0 && text.charAt(i - 1) == ' ') {
                i--;
            }

            if (i > 0 && text.charAt(i - 1) == ' ') {
                return text;
            } else {
                if (text instanceof Spanned) {
                    SpannableString sp = new SpannableString(text + "");
                    TextUtils.copySpansFrom((Spanned) text, 0, text.length(),
                            Object.class, sp, 0);
                    return sp;
                } else {
                    return text + " ";
                }
            }
        }
    }

    private void checkStart(final String emp_id, final String vendor_name, final String time) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Starting ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //Log.d(TAG, "Login Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        // user successfully logged in
                        // Create login session
                        //session.setLogin(true);
                        Toast.makeText(getApplicationContext(), "Successfully Saved ", Toast.LENGTH_LONG).show();
                        // Launch main activity
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Successfully Saved ", Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("emp_id", emp_id);
                params.put("vendor_name", vendor_name);
                params.put("time", time);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    private void checkEnd(final String emp_id, final String vendor_name, final String start_time, final String time) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Ending ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //Log.d(TAG, "Login Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        // user successfully logged in
                        // Create login session
                        //session.setLogin(true);
                        Toast.makeText(getApplicationContext(), "Successfully Saved ", Toast.LENGTH_LONG).show();
                        // Launch main activity
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(), "Successfully Saved ", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Successfully Saved ", Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("emp_id", emp_id);
                params.put("vendor_name", vendor_name);
                params.put("start_time", start_time);
                params.put("time_end", time);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
