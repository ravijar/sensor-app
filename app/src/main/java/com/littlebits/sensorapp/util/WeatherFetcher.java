package com.littlebits.sensorapp.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONObject;

public class WeatherFetcher {

    private static final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather";
    private static final String TAG = "WeatherFetcher";

    private final Context context;
    private final FusedLocationProviderClient fusedLocationClient;
    private final RequestQueue requestQueue;
    private final WeatherListener weatherListener;

    private static String API_KEY;

    static {
        API_KEY = "";
    }

    public interface WeatherListener {
        void onWeatherFetched(String temperature, String location, String weatherIcon);
    }

    public WeatherFetcher(Context context, WeatherListener listener) {
        this.context = context;
        this.weatherListener = listener;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        this.requestQueue = Volley.newRequestQueue(context);

        if (API_KEY.isEmpty()) {
            API_KEY = getApiKey(context);
        }
    }

    private static String getApiKey(Context context) {
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            return appInfo.metaData.getString("com.littlebits.sensorapp.WEATHER_API_KEY");
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Could not fetch API key");
            return "";
        }
    }

    public void fetchWeather() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Location permission not granted");
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    fetchWeatherFromAPI(latitude, longitude);
                } else {
                    Log.e(TAG, "Location is null");
                }
            }
        });
    }

    private void fetchWeatherFromAPI(double lat, double lon) {
        String url = WEATHER_URL + "?lat=" + lat + "&lon=" + lon + "&appid=" + API_KEY + "&units=metric";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Get weather temperature, location name, and weather code
                            String temperature = String.format("%.1f°", response.getJSONObject("main").getDouble("temp"));
                            String location = response.getString("name");
                            String weatherIcon = response.getJSONArray("weather").getJSONObject(0).getString("icon");

                            // Notify the listener
                            weatherListener.onWeatherFetched(temperature, location, weatherIcon);
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing weather data", e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Weather API request failed", error);
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }
}
