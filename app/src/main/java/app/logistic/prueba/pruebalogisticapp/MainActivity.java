package app.logistic.prueba.pruebalogisticapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener{


    private TextView txtFecha, txtCiudad, txtGrados, txtWindSpeed, txtHumidity, txtDirection;
    private ImageView imgTemperature, imgFlag, imgCompass, imgUmbrella, imgDisponibilidad;
    private String url = "https://api.darksky.net/forecast/249e9795287eda8977453977f89cbd4b/";
    private Button btnRefresh;
    private ProgressBar progressCarga;

    private String CLEARDAY = "clear-day";
    private String CLEARNIGHT = "clear-night";
    private String RAIN = "rain";
    private String SNOW = "snow";
    private String SLEET = "sleet";
    private String WIND = "wind";
    private String FOG = "fog";
    private String CLOUDY = "cloudy";
    private String CLOUDYDAYICON = "partly-cloudy-day";
    private String CLOUDYNIGHTICON = "partly-cloudy-night";
    private String HAIL = "hail";
    private String THUNDERSTORM = "thunderstorm";
    private String TORNADO = "tornado";
    private Toolbar toolbarTop;
    private TextView mTitle;
    private RequestQueue queue;
    private AlertDialog alert;
    private AdapterDays adapter;
    private RecyclerView recyclerView;
    private ArrayList<Days> listDays = new ArrayList<Days>();
    private SwipeRefreshLayout swipeUpdate;
    private double longitudactual, latitudeactual;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    private LocationControl locationControlTask;



    @SuppressLint("ObjectAnimatorBinding")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alert=null;
        queue = Volley.newRequestQueue(this);
        adapter = new AdapterDays(listDays);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_days);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));

        txtFecha = findViewById(R.id.txtFecha);
        txtCiudad = findViewById(R.id.txtCiudad);
        txtGrados = findViewById(R.id.txtGrados);
        txtWindSpeed = findViewById(R.id.txtWindSpeed);
        txtHumidity = findViewById(R.id.txtHumedad);
        txtDirection = findViewById(R.id.txtDirection);
        imgTemperature = findViewById(R.id.iconTemperatureToday);
        swipeUpdate = findViewById(R.id.swipeUpdate);
        imgDisponibilidad = findViewById(R.id.disponibilidad);

        imgCompass = findViewById(R.id.imgCompass);
        imgUmbrella = findViewById(R.id.imgUmbrella);
        imgFlag = findViewById(R.id.imgFlag);

        toolbarTop = (Toolbar) findViewById(R.id.toolbar_top);
        mTitle = (TextView) toolbarTop.findViewById(R.id.toolbar_title);
        btnRefresh = findViewById(R.id.buttonRefresh);
        progressCarga = findViewById(R.id.progressCarga);

        Calendar cal = Calendar.getInstance();
        int dayofweek = cal.get(Calendar.DAY_OF_WEEK);

        final String[] days = getResources().getStringArray(R.array.week_days);
        String day = days[dayofweek - 1];

        SimpleDateFormat sdf = new SimpleDateFormat("MMM d");
        String currentDateandTime = sdf.format(new Date());



        txtFecha.setText(day.toUpperCase() + ", " + currentDateandTime.toUpperCase());

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);


        locationControlTask = new LocationControl();
        locationControlTask.execute(this);
        //GetLocation();

        swipeUpdate.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

               alert=null;

                if (isOnline(MainActivity.this)) {

                    btnRefresh.setVisibility(View.GONE);
                    progressCarga.setVisibility(View.VISIBLE);
                    listDays.clear();
                    GetLocation();
                    swipeUpdate.setRefreshing(false);

                }else{
                    LimpiarCampos();
                    listDays.clear();
                    btnRefresh.setVisibility(View.VISIBLE);
                    progressCarga.setVisibility(View.INVISIBLE);
                    imgDisponibilidad.setImageResource(R.drawable.no_disponible);
                    Toast.makeText(getApplicationContext(), R.string.internet_problems, Toast.LENGTH_SHORT).show();
                    swipeUpdate.setRefreshing(false);
                }

            }
        });


        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               alert=null;

                if (isOnline(MainActivity.this)) {

                    btnRefresh.setVisibility(View.INVISIBLE);
                    progressCarga.setVisibility(View.VISIBLE);
                    listDays.clear();
                    GetLocation();
                    swipeUpdate.setRefreshing(false);

                }else{
                    LimpiarCampos();
                    listDays.clear();
                    btnRefresh.setVisibility(View.VISIBLE);
                    progressCarga.setVisibility(View.INVISIBLE);
                    imgDisponibilidad.setImageResource(R.drawable.no_disponible);
                    Toast.makeText(getApplicationContext(), R.string.internet_problems, Toast.LENGTH_SHORT).show();
                    swipeUpdate.setRefreshing(false);

                }

            }
        });

    }

    public static boolean isOnline(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            latitudeactual=mLastLocation.getLatitude();
            longitudactual=mLastLocation.getLongitude();
           // GetLocation();
        }else{

        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private void LimpiarCampos(){
        txtGrados.setText("");
        imgTemperature.setImageResource(0);
        txtWindSpeed.setText("");
        txtDirection.setText("");
        txtHumidity.setText("");
        imgFlag.setImageResource(0);
        imgUmbrella.setImageResource(0);
        imgCompass.setImageResource(0);

    }

    private void AlertaNoGPS() {
        btnRefresh.setVisibility(View.VISIBLE);
        progressCarga.setVisibility(View.INVISIBLE);
        imgDisponibilidad.setImageResource(R.drawable.no_disponible);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.aviso_ubicacion)
                .setCancelable(false)
                .setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));


                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });


        if(alert!=null) {

        }else {
        alert = builder.create();
        alert.show();

       }

    }

    protected void onResume(){
        super.onResume();
        locationControlTask = new LocationControl();
        locationControlTask.execute(this);
    }

    @Override
    public void onLocationChanged(Location location) {


    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }


    private class LocationControl extends AsyncTask<Context, Void, Void>
    {

        protected void onPreExecute()
        {



        }

        protected Void doInBackground(Context... params)
        {

            while (mLastLocation==null) {

                try {

                    Thread.sleep(1000);

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Log.i("Getting", "Location");
                            GetLocation();
                        }
                    });


                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            };
            return null;
        }

        protected void onPostExecute(final Void unused)
        {
           if (mLastLocation != null)
            {
                double latitud = mLastLocation.getLatitude();
                double longitud = mLastLocation.getLongitude();
                Log.i("Coordenadas", latitud+" "+longitud);
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        CargarDatos();
                    }
                });

            }
            else
            {
               Toast.makeText(getApplicationContext(), R.string.error_ubicacion, Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void GetLocation(){

        btnRefresh.setVisibility(View.INVISIBLE);
        progressCarga.setVisibility(View.VISIBLE);
        imgDisponibilidad.setImageResource(R.drawable.no_disponible);

        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            LimpiarCampos();

            return ;

        }else{

            if(manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                        mGoogleApiClient);

                if (mLastLocation != null) {
                    latitudeactual = mLastLocation.getLatitude();
                    longitudactual = mLastLocation.getLongitude();

                    if(isOnline(MainActivity.this)){
                        CargarDatos();
                    }else{

                        final Toast toast = Toast.makeText(getApplicationContext(), R.string.internet_problems, Toast.LENGTH_SHORT);
                        toast.show();

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                toast.cancel();
                            }
                        }, 500);
                    }

                } else {

                    btnRefresh.setVisibility(View.VISIBLE);
                    progressCarga.setVisibility(View.INVISIBLE);
                    imgDisponibilidad.setImageResource(R.drawable.no_disponible);

                }

            }else{

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        btnRefresh.setVisibility(View.VISIBLE);
                        progressCarga.setVisibility(View.INVISIBLE);
                        imgDisponibilidad.setImageResource(R.drawable.no_disponible);
                        AlertaNoGPS();
                        if(alert!=null) {

                        }else{

                        }
                        Log.i("Solicitud de ", "activar ubicacion");
                        LimpiarCampos();

                    }
                });



            }
        }





    }

    public void CargarDatos(){

        double lat = latitudeactual;
        double lon = longitudactual;

        btnRefresh.setVisibility(View.INVISIBLE);
        progressCarga.setVisibility(View.VISIBLE);
        imgDisponibilidad.setImageResource(R.drawable.no_disponible);

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(lat, lon, 1);

        } catch (IOException e) {
            e.printStackTrace();
        }

        if(addresses!=null){
            String[] parts = addresses.get(0).getAddressLine(0).split(",");
            String direccion = parts[0];
            txtCiudad.setText(direccion);

            if(addresses.get(0).getLocality()!=null && addresses.get(0).getAdminArea()!=null){
                mTitle.setText(addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea());
            }else if(addresses.get(0).getLocality()!=null){
                mTitle.setText(addresses.get(0).getLocality());
            }else if(addresses.get(0).getAdminArea()!=null){
                mTitle.setText(addresses.get(0).getAdminArea());
            }else{

            }
        }

        String urlfinal = url + lat + "," + lon;

        if (isOnline(this)) {

            StringRequest stringRequest = new StringRequest(Request.Method.GET, urlfinal, new Response.Listener<String>() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject objetoJSON = new JSONObject(response);
                        JSONObject primero = objetoJSON.getJSONObject("currently");

                        double tempe = primero.getDouble("temperature");
                        double speed = primero.getDouble("windSpeed");
                        double humidity = primero.getDouble("humidity");
                        double direccion = primero.getDouble("windBearing");
                        String icon = primero.getString("icon");
                        double humitidytotal = humidity * 100;
                        txtGrados.setText(String.valueOf((int) tempe) + "°");
                        txtWindSpeed.setText(String.valueOf(speed + " MPH"));
                        txtHumidity.setText(String.valueOf(((int) humitidytotal) + " %"));


                        imgFlag.setImageResource(R.drawable.flag);
                        imgUmbrella.setImageResource(R.drawable.umbrella);
                        imgCompass.setImageResource(R.drawable.compass);

                        btnRefresh.setVisibility(View.VISIBLE);
                        progressCarga.setVisibility(View.INVISIBLE);

                        if (direccion == 0) {
                            txtDirection.setText(R.string.N);
                        } else if (direccion > 0 || direccion < 90) {
                            txtDirection.setText(R.string.NE);
                        } else if (direccion == 90) {
                            txtDirection.setText(R.string.E);
                        } else if (direccion > 90 || direccion < 180) {
                            txtDirection.setText(R.string.SE);
                        } else if (direccion == 180) {
                            txtDirection.setText(R.string.S);
                        } else if (direccion > 180 || direccion < 270) {
                            txtDirection.setText(R.string.SW);
                        } else if (direccion == 270) {
                            txtDirection.setText(R.string.W);
                        } else if (direccion > 270 || direccion < 260) {
                            txtDirection.setText(R.string.NW);
                        } else if (direccion == 360) {
                            txtDirection.setText(R.string.N);
                        }

                        if (icon.equals(CLEARDAY)) {
                            imgTemperature.setImageResource(R.drawable.clear_day_white);
                        } else if (icon.equals(CLEARNIGHT)) {
                            imgTemperature.setImageResource(R.drawable.clear_night_white);
                        } else if (icon.equals(RAIN)) {
                            imgTemperature.setImageResource(R.drawable.rain_white);
                        } else if (icon.equals(SNOW)) {
                            imgTemperature.setImageResource(R.drawable.snow_white);
                        } else if (icon.equals(SLEET)) {
                            imgTemperature.setImageResource(R.drawable.sleet_white);
                        } else if (icon.equals(WIND)) {
                            imgTemperature.setImageResource(R.drawable.wind_white);
                        } else if (icon.equals(FOG)) {
                            imgTemperature.setImageResource(R.drawable.fog_white);
                        } else if (icon.equals(CLOUDY)) {
                            imgTemperature.setImageResource(R.drawable.cloudy_white);
                        } else if (icon.equals(CLOUDYDAYICON)) {
                            imgTemperature.setImageResource(R.drawable.claudy_day_white);
                        } else if (icon.equals(CLOUDYNIGHTICON)) {
                            imgTemperature.setImageResource(R.drawable.cloudly_night_white);
                        } else if (icon.equals(HAIL)) {
                            imgTemperature.setImageResource(R.drawable.hail_white);
                        } else if (icon.equals(THUNDERSTORM)) {
                            imgTemperature.setImageResource(R.drawable.thunderstorm_white);
                        } else if (icon.equals(TORNADO)) {
                            imgTemperature.setImageResource(R.drawable.tornado_white);
                        }

                        JSONObject diario = objetoJSON.getJSONObject("daily");
                        JSONArray data = diario.getJSONArray("data");

                        listDays.clear();
                        for (int i = 0; i <= 4; i++) {

                            JSONObject objeto = data.getJSONObject(i);

                            String icono = objeto.getString("icon");
                            int temperatura = objeto.getInt("temperatureMax");

                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE");
                            Calendar calendar = new GregorianCalendar();
                            calendar.add(Calendar.DATE, i + 1);
                            String dayy = simpleDateFormat.format(calendar.getTime());
                            String dia = dayy.toUpperCase();

                            imgDisponibilidad.setImageResource(R.drawable.disponible);

                            Days day = new Days(icono, temperatura, dia);
                            listDays.add(day);
                        }

                        adapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
            queue.add(stringRequest);


        } else {
            imgDisponibilidad.setImageResource(R.drawable.no_disponible);
            btnRefresh.setVisibility(View.VISIBLE);
            progressCarga.setVisibility(View.INVISIBLE);
            final Toast toast = Toast.makeText(getApplicationContext(), R.string.internet_problems, Toast.LENGTH_SHORT);
            toast.show();

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    toast.cancel();
                }
            }, 500);
        }

    }

}


