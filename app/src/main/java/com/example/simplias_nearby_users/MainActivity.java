package com.example.simplias_nearby_users;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;


import com.example.simplias_nearby_users.entities.User;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    int radius = 1500;
    GeoPoint reference , newGeoPoint ;
    MapView map = null;
    User[][] grid = null ;
    ArrayList<User> users ;
    int factor,max;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //handle permissions first, before map is created. not depicted here

        //load/initialize the osmdroid configuration, this can be done
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        //setting this before the layout is inflated is a good idea
        //it 'should' ensure that the map has a writable location for the map cache, even without permissions
        //if no tiles are displayed, you can try overriding the cache path using Configuration.getInstance().setCachePath
        //see also StorageUtils
        //note, the load method also sets the HTTP User Agent to your application's package name, abusing osm's tile servers will get you banned based on this string

        //inflate and create the map
        setContentView(R.layout.activity_main);

        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);


        newGeoPoint = new GeoPoint(51.183331,10.077777);
        reference = new GeoPoint(51.160835,10.085000);


        GeoPoint GP0 = new GeoPoint(51.133481, 10.018343);
        //This User Position
        GeoPoint GP1 = new GeoPoint(51.143479, 10.087772);
        GeoPoint GP2 = new GeoPoint(51.127547, 10.080420);
        GeoPoint GP3 = new GeoPoint(51.120008, 10.004954);
        GeoPoint GP4 = new GeoPoint(51.133331, 10.086667);

        User user0 = new User(1,"John",null,GP0);
        //This current user
        User user1 = new User(2,"Cedric",null,GP1);
        User user2 = new User(3,"Ahmed",null,GP2);
        User user3 = new User(4,"Jean",null,GP3);
        User user4 = new User(5,"Bilel",null,GP4);

        //Add users into an array
        users = new ArrayList<>();
        users.add(user0);
        users.add(user1);
        users.add(user2);
        users.add(user3);
        users.add(user4);

        IMapController mapController = map.getController();
        mapController.setCenter(GP0);
        mapController.setZoom(12);

        //Markers Settings
        Marker Marker0 = new Marker(map);
        Marker Marker1 = new Marker(map);
        Marker Marker2 = new Marker(map);
        Marker Marker3 = new Marker(map);
        Marker Marker4 = new Marker(map);
        Marker Ref = new Marker(map);

        Marker0.setPosition(user0.getNew_position());
        Marker1.setPosition(user1.getNew_position());
        Marker2.setPosition(user2.getNew_position());
        Marker3.setPosition(user3.getNew_position());
        Marker4.setPosition(user4.getNew_position());
        Ref.setPosition(reference);

        Marker0.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        Marker0.setIcon(getDrawable(R.drawable.ic_location_on_white_50dp));

        Marker1.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        Marker1.setIcon(getDrawable(R.drawable.ic_location_on_white_50dp));

        Marker2.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        Marker2.setIcon(getDrawable(R.drawable.ic_location_on_white_50dp));

        Marker3.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        Marker3.setIcon(getDrawable(R.drawable.ic_location_on_white_50dp));

        Marker4.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        Marker4.setIcon(getDrawable(R.drawable.ic_location_on_white_50dp));

        Ref.setIcon(getDrawable(R.drawable.ic_location_on_red_50dp));
        Ref.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

        map.getOverlays().add(Marker0);
        map.getOverlays().add(Marker1);
        map.getOverlays().add(Marker2);
        map.getOverlays().add(Marker3);
        map.getOverlays().add(Marker4);
        map.getOverlays().add(Ref);



        map.getOverlays().forEach(m ->{
            ((Marker)m).setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker, MapView mapView) {
                    Log.e("eeeee",""+((Marker)m).getPosition().getLatitude());
                    Log.e("eeeee",""+((Marker)m).getPosition().getLongitude());
                    return false;
                }
            });
        });
        Log.e("distance",""+GP0.distanceToAsDouble(user1.getNew_position()));



        initiateGrid();
        Log.e("Radius Chosen",""+radius);
        search(user1,radius);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                updatePosition(user4,newGeoPoint);
                initiateGrid();
                search(user1,radius);
            }
        }, 5000);   //5 seconds


    }

    public void onResume(){
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    public void onPause(){
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }


    //Search for nearby users
    public void search(User user,int radius){

        int distance =  (int) reference.distanceToAsDouble(user.getNew_position());
        int row = distance/factor;
        Log.e("search","row "+row);
        int nbrOfgrps=radius/factor;
        Log.e("search","nbrofgrps "+nbrOfgrps);

        for (int i =row-nbrOfgrps-1;i<row+nbrOfgrps+1;i++){
            Log.e("search","i  "+i);

            if(i>=0 && i<users.size()){
                for (int j =0;j<users.size();j++){
                    if(grid[i][j]==null)
                        break;
                    int usersdistance =  (int) grid[i][j].getNew_position().distanceToAsDouble(user.getNew_position());
                    Log.e("search","usersdistance "+usersdistance);

                    if(usersdistance<=radius && usersdistance>0){
                        ((Marker) map.getOverlays().get(users.indexOf(grid[i][j])))
                                .setIcon(getDrawable(R.drawable.ic_location_on_blue_50dp));
                    }else if(usersdistance>radius && usersdistance>0){
                        ((Marker) map.getOverlays().get(users.indexOf(grid[i][j])))
                                .setIcon(getDrawable(R.drawable.ic_location_on_white_50dp));
                    }
                    else if(usersdistance==0){
                        ((Marker) map.getOverlays().get(users.indexOf(grid[i][j])))
                                .setIcon(getDrawable(R.drawable.ic_location_on_green_50dp));
                    }



                }
            }

        }

    }

    //Initiade the grid and sort users by groups
    public void initiateGrid(){
        int distance;
        int row ;
        grid = new User[users.size()][users.size()];
        for (User user: users){
            distance =  (int) reference.distanceToAsDouble(user.getNew_position());
            if(distance>max)
                max=distance;
        }
        factor = max/users.size();
        Log.e("vvvvv",""+factor);
        for (User user: users) {
            distance =  (int) reference.distanceToAsDouble(user.getNew_position());
            Log.e("initiate grid","distance "+distance);

            row = distance/factor;
            if(distance==max)
                row--;

            for (int i=0 ;i<users.size();i++){
                Log.e("initiate grid","row "+row);
                Log.e("initiate grid","i "+i);

                if(grid[row][i]==null) {
                    grid[row][i]=user;
                    break;
                }
            }

        }
        for (int i =0;i<users.size();i++){
            Log.e("grid",i+"============");
            for (int j =0;j<users.size();j++){
                if(grid[i][j]!=null)
                    Log.e("grid",grid[i][j].toString());

            }
        }


    }

    //Update A user position
    public void updatePosition(User user,GeoPoint new_gp) {

        int distance =  (int) reference.distanceToAsDouble(user.getNew_position());
        int row = distance/factor;
        if(row==users.size()) row--;
        for (int i=0 ;i<users.size();i++){
            if(grid[row][i].getId()!=user.getId()) {
                grid[row]=removeTheElement(grid[row],i);
                break;
            }
        }
        user.setOld_position(user.getNew_position());
        user.setNew_position(new_gp);
        distance =  (int) reference.distanceToAsDouble(user.getNew_position());
        if(distance>max){
            max=distance;
            factor = max/users.size();
            for (User u: users) {
                distance =  (int) reference.distanceToAsDouble(u.getNew_position());
                Log.e("update position","distance "+distance);
                Log.e("update position","factor "+factor);

                row = distance/factor;
                if(row==users.size())
                    row--;

                for (int i=0 ;i<users.size();i++){


                    if(grid[row][i]==null) {
                        grid[row][i]=u;
                        break;
                    }
                }

            }
        }else {
            row = distance/factor;
            if(distance==max)
                row--;
            for (int i=0 ;i<users.size();i++){
                if(grid[row][i]==null) {
                    grid[row][i]=user;
                    break;
                }
            }
        }
        for (int i=0;i<map.getOverlays().size();i++){
            if(((Marker)map.getOverlays().get(i)).getPosition().equals(user.getOld_position()))
            {
                ((Marker)map.getOverlays().get(i)).setPosition(user.getNew_position());
            }
        }

    }

    //Remove User from his old position in the grid
    public static User[] removeTheElement(User[] arr,
                                          int index)
    {

        if (arr == null
                || index < 0
                || index >= arr.length) {

            return arr;
        }

        User[] anotherArray = new User[arr.length - 1];

        for (int i = 0, k = 0; i < arr.length; i++) {


            if (i == index) {
                continue;
            }


            anotherArray[k++] = arr[i];
        }


        return anotherArray;
    }
}