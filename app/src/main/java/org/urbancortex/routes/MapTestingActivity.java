package org.urbancortex.routes;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.os.SystemClock.elapsedRealtime;
import static org.urbancortex.routes.Routes.*;

public class MapTestingActivity extends ActionBarActivity implements OnMapReadyCallback {

    MapFragment mapFragment;
    boolean isPressed;
    Button startBtn;
    Button stopBtn;
    SimpleDateFormat formatterDate = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat formatterTime = new SimpleDateFormat("HH:mm:ss.SSS");
    private String date;
    private String currentTime;

    csv_logger mService;
    boolean mBound = false;


    Polyline RouteA;
    Polyline RouteB;
    Polyline RouteC;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_maptesting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public static FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maptesting);

        mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        fm = getFragmentManager();
        fm.beginTransaction().hide(mapFragment).commit();

        updateButtonState();

    }

    protected ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            mBound = false;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            csv_logger.LocalBinder mLocalBinder = (csv_logger.LocalBinder) service;
            mService = mLocalBinder.getService();
            mBound = true;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();

        // Bind to LocalService
        Intent intent = new Intent(this, csv_logger.class);
        bindService(intent, mConnection, 0);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onMapReady(GoogleMap map) {

        map.getUiSettings().setAllGesturesEnabled(false);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setScrollGesturesEnabled(true);

        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

            private float currentZoom = -1;
            private double currentLat = 0;
            private double currentLon = 0;

            @Override
            public void onCameraChange(CameraPosition pos) {

                System.out.println(pos.toString());
                if (pos.zoom != currentZoom) {
                    currentZoom = pos.zoom;
                    // do you action here
                    System.out.println("new zoom event");
                    recordEvents("zoomTo", String.valueOf(currentZoom), elapsedRealtime());


                } else {
                    if (pos.target.latitude != currentLat || pos.target.longitude != currentLon) {
                        System.out.println("new map scroll event");

                        //TO DO
                        recordEvents("scrollTo", pos.target.latitude+" "+pos.target.longitude, elapsedRealtime());
                    }
                }
            }
        });

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(51.5198, -0.1324), 15));

        // Polylines are useful for marking paths and routes on the map.
        RouteA = map.addPolyline(new PolylineOptions().geodesic(true).add(
                new LatLng(51.52582,-0.1332736),
                new LatLng(51.52619,-0.1336277),
                new LatLng(51.52549,-0.1358485),
                new LatLng(51.52242,-0.1325655),
                new LatLng(51.52144,-0.1351941),
                new LatLng(51.51698,-0.1307389),
                new LatLng(51.51646,-0.1303983),
                new LatLng(51.51622,-0.1329088),
                new LatLng(51.51531,-0.1322007)
        ));

        RouteB = map.addPolyline(new PolylineOptions().geodesic(true).add(
                new LatLng(51.52582,-0.1332736),
                new LatLng(51.52346,-0.1307416),
                new LatLng(51.52332,-0.1310205),
                new LatLng(51.52287,-0.1304626),
                new LatLng(51.52170,-0.1291752),
                new LatLng(51.52132,-0.1301193),
                new LatLng(51.52009,-0.1287031),
                new LatLng(51.51854,-0.1322758),
                new LatLng(51.51874,-0.1324582),
                new LatLng(51.51794,-0.1344109),
                new LatLng(51.51531,-0.1322007)
        ));

        RouteC = map.addPolyline(new PolylineOptions().geodesic(true).add(
                new LatLng(51.52216,-0.1359558),
                new LatLng(51.52224,-0.1357841),
                new LatLng(51.52236,-0.1359773),
                new LatLng(51.52292,-0.1346684),
                new LatLng(51.52426,-0.1360095),
                new LatLng(51.52464,-0.1350331),
                new LatLng(51.52509,-0.1355374),
                new LatLng(51.52587,-0.1332736)
        ));

        RouteA.setVisible(false);
        RouteB.setVisible(false);
        RouteC.setVisible(false);

        map.addMarker(new MarkerOptions().position(new LatLng(51.52587, -0.1332736)).title("Start"));
        map.addMarker(new MarkerOptions().position(new LatLng(51.515297, -0.132230)).title("Stop"));

    }




    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    long startShowingMap = 0;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        int action = event.getAction();

        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (action == KeyEvent.ACTION_DOWN && !isPressed && isWalking) {

                    recordEvents("showMap", null, elapsedRealtime());
                    isMapDisplayed = true;
                    isPressed = true;
                    startShowingMap  = elapsedRealtime();

                    updateMap();
                    showMap();

                } else if (action == KeyEvent.ACTION_UP && isMapDisplayed) {
                    long mapReadingDuration = elapsedRealtime() - startShowingMap;
                    startShowingMap=0;

                    recordEvents("hideMap", String.valueOf(mapReadingDuration), elapsedRealtime());
                    isPressed = false;
                    isMapDisplayed = false;
                    updateMap();
                    showMap();
                }
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }

    public boolean currentRoute() {

        EditText numberCode = (EditText) findViewById(R.id.numberPassword);
        String routeCode = numberCode.getText().toString();

        switch (routeCode){
            case "123":
                currentRoute = "a";
                return true;
            case "321":
                currentRoute = "b";
                return true;
            case "111":
                currentRoute = "c";
                return true;
            default:
                return false;
        }
    }

    public boolean updateMap(){
        if(currentRoute != null){

            switch (currentRoute){
                case "a":
                    RouteA.setVisible(true);
                    RouteB.setVisible(false);
                    RouteC.setVisible(false);
                    break;
                case "b":
                    RouteA.setVisible(false);
                    RouteB.setVisible(true);
                    RouteC.setVisible(false);
                    break;
                case "c":
                    RouteA.setVisible(false);
                    RouteB.setVisible(false);
                    RouteC.setVisible(true);
                    break;
                default:
                    RouteA.setVisible(false);
                    RouteB.setVisible(false);
                    RouteC.setVisible(false);

            }

            return true;

        } else {
            return false;
        }
    }

    public void showMap(){

        if(isPressed){
            fm.beginTransaction().show(mapFragment).commit();
        } else {
            fm.beginTransaction().hide(mapFragment).commit();
        }
    }

    public void onTestingClick(View view){

        final EditText numberCode = (EditText) findViewById(R.id.numberPassword);

        switch(view.getId())
        {
            case R.id.button1:
                // handle button A click;

                if(currentRoute()){
                    isWalking = true;
                    updateButtonState();

                    recordEvents("startRoute",null, elapsedRealtime());
                    numberCode.setEnabled(false);
                }

                break;
            case R.id.button2:
                // handle button B click;

                // Confirm this is not accidental
                new AlertDialog.Builder(this)
                        .setTitle("Arrived to Destination")
                        .setMessage("Press to confirm you have arrived at the destination")
                        .setIcon(0)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with
                                isWalking = false;
                                updateButtonState();

                                recordEvents("arrived", null, elapsedRealtime());
                                numberCode.setEnabled(true);
                                numberCode.setText("");
                                currentRoute = null;
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();


                break;

            default:
                throw new RuntimeException("Unknown button ID");
        }

    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     *l.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
//    private void setUpMapIfNeeded() {
//        // Do a null check to confirm that we have not already instantiated the map.
//        if (mMap == null) {
//            // Try to obtain the map from the SupportMapFragment.
//            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
//                    .getMap();
//            // Check if we were successful in obtaining the map.
//            if (mMap != null) {
//                setUpMap();
//            }
//        }
//    }


    public void recordEvents(String newEvent, String details, long eventEpoch) {

        long millisElapsed = eventEpoch - Routes.startMillis;

        long timestamp = Routes.startTime + millisElapsed;

        date = formatterDate.format(new Date(timestamp));
        Object currentTime = formatterTime.format(new Date(timestamp));

        String eventInfo = newEvent + ", " +
                details + ", " +
                currentRoute + ", " +
                date + ", " +
                currentTime + ", " +
                timestamp + ", " +
                locations.lat + ", " +
                locations.lon + ", " +
                locations.speed + ", " +
                locations.bearing + ", " +
                locations.elevation + ", " +
                locations.accuracy;

        try {
            mService.writeStringToFile(eventInfo);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void updateButtonState(){

        startBtn = (Button) findViewById(R.id.button1);
        stopBtn = (Button) findViewById(R.id.button2);

        if(!isWalking){
            startBtn.setVisibility(View.VISIBLE);
            stopBtn.setVisibility(View.INVISIBLE);
        } else {
            startBtn.setVisibility(View.INVISIBLE);
            stopBtn.setVisibility(View.VISIBLE);
        }

    }
}