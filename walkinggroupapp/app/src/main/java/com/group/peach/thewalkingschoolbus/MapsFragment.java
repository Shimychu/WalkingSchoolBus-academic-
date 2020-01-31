package com.group.peach.thewalkingschoolbus;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.group.peach.thewalkingschoolbus.model.GPSLocationModel;
import com.group.peach.thewalkingschoolbus.model.Group;
import com.group.peach.thewalkingschoolbus.model.Leader;
import com.group.peach.thewalkingschoolbus.model.ObjectIDModel;
import com.group.peach.thewalkingschoolbus.model.User;
import com.group.peach.thewalkingschoolbus.proxy.ProxyBuilder;
import com.group.peach.thewalkingschoolbus.proxy.WGServerProxy;

import java.lang.reflect.Proxy;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import retrofit2.Call;


public class MapsFragment extends Fragment {

    //TabLayout variable
    public static boolean hasFocus;

    //Class Variables
    private static final String TAG = "MAP_FRAGMENT";
    private static final int TIMER = 30000;
    private static final int TIME_OUT = 600000;
    private GoogleMap googleMap;
    private LocationManager locationManager;
    private LatLng mylocation;
    private Double previousLat;
    private Double previousLong;
    private com.getbase.floatingactionbutton.FloatingActionButton confirmCreateGroupFab;
    private FloatingActionsMenu floatingActionsMenu;
    private FloatingActionButton fabEmergency;
    private FloatingActionButton fabStartGps;
    private MapView mMapView;
    private View rootView;

    //Markers
    private Marker myMarker;
    private LatLng startLocation;
    private Marker selectedMarker;
    private Marker endMarker;
    private User user;
    private List<Marker> childrenMarkers = new ArrayList<>();
    private HashSet<Long> leaderIdSet = new HashSet<Long>();
    private List<Marker> leaderMarkers = new ArrayList<>();

    private WGServerProxy proxy;

    //Walk features
    private volatile boolean toggleGps;
    private ScheduledExecutorService executor;
    //private Runnable sendGpsTask;
    private ScheduledFuture<?> scheduledFuture;
    private Handler handler;

    private static int testN = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_maps_fragment, container, false);

        user = User.getInstance();
        // Build the server proxy
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), user.getUserToken());

        //Grab views
        floatingActionsMenu = rootView.findViewById(R.id.floating_menu);
        confirmCreateGroupFab = rootView.findViewById(R.id.confirm_create_group);
        fabEmergency = rootView.findViewById(R.id.btn_emergency);
        fabStartGps = rootView.findViewById(R.id.startGps);

        //Create a handler
        handler = new Handler();

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Google maps
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                // For showing a move to my location button
                if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                googleMap.setMyLocationEnabled(true);
                grabGpsLocation();
                grabGroups();


                // Add a marker in Sydney and move the camera
                mMap.moveCamera(CameraUpdateFactory.newLatLng(mylocation));

                //Setting max and min zoom
                mMap.setMinZoomPreference(15.0f);
                mMap.setMaxZoomPreference(20.0f);


                //move marker accordingly to where the user clicks
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        if (floatingActionsMenu.isExpanded()) {
                            if (myMarker != null) {
                                myMarker.setPosition(latLng);
                                mylocation = latLng;
                            }
                        }
                        selectedMarker = null;
                        if(endMarker != null){
                            endMarker.remove();
                        }
                    }
                });

                //Find which marker was selected
                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        selectedMarker = marker;
                        Call<Group> groupCall = proxy.getGroupDetail((Long) marker.getTag());
                        ProxyBuilder.callProxy(getActivity(),groupCall,returnGroup->endGroupResponse(returnGroup));

                        return false;
                    }
                });

                //Enable GPS tracking when clicked
                googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                    @Override
                    public boolean onMyLocationButtonClick() {

                        return true;
                    }
                });
            }
        });
        setUpFloatingButton();
        handler.post(groupGpsTracking);
        return rootView;
    }

    /*
    Remove handler for gps tracking when application is no longer active
     */
    @Override
    public void onPause() {
        handler.removeCallbacks(groupGpsTracking);
        super.onPause();
    }
    /*
    Resumes handler for gps tracking when application is active again.
     */

    @Override
    public void onResume()
    {
        handler.post(groupGpsTracking);
        super.onResume();
    }

    private Runnable groupGpsTracking = new Runnable() {
        @Override
        public void run() {
            grabChildrenLocation();
            handler.postDelayed(groupGpsTracking,TIMER);
        }
    };


    /*
    Floating button to create a group
    as well as display markers
     */
    private void setUpFloatingButton(){
        grabGpsLocation();
        fabEmergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //inflate the dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                View dialogView = getLayoutInflater().inflate(R.layout.new_message_dialog, null);
                builder.setView(dialogView);
                AlertDialog dialog = builder.create();
                dialog.show();

                //setup the views in the dialog
                EditText txtMsgContent = dialog.findViewById(R.id.txt_messageContent);
                Button btnOK = dialogView.findViewById(R.id.msg_dialog_confirm);
                Button btnCancel = dialogView.findViewById(R.id.msg_dialog_cancel);

                btnOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //get the message body
                        String input = txtMsgContent.getText().toString();
                        System.out.println(input);

                        //create payload that send to server
                        Map<String, Object> payload;
                        payload = new HashMap<>();
                        payload.put("text", input);
                        payload.put("emergency", true);

                        //make server call
                        Call<Void> parentCaller = proxy.sendMsgToParent(User.getInstance().getId(), payload);
                        ProxyBuilder.callProxy(getContext(), parentCaller, nothing-> emptyResponse(nothing));

                        //finally dismiss the dialog
                        dialog.dismiss();
                    }
                });


                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });            }
        });
        floatingActionsMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {

            @Override
            public void onMenuExpanded() {
                grabGpsLocation();
                myMarker = googleMap.addMarker(new MarkerOptions()
                        .position(mylocation)
                        .title("Create group here."));
            }

            @Override
            public void onMenuCollapsed() {
                startLocation = null;
                myMarker.remove();
            }
        });

        // I hope I get marks just for commenting on this function
        // 2 markers for start destination and end destination
        confirmCreateGroupFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(startLocation == null){
                    startLocation = mylocation;
                    Toast.makeText(getActivity(),"Select destination",Toast.LENGTH_LONG).show();
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Group Description");

                EditText input = new EditText(getActivity().getApplicationContext());

                Context dialogContext = builder.getContext();
                EditText url = new EditText(dialogContext);

                builder.setView(url);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Group g = new Group();
                        g.setGroupDescription(url.getText().toString());

                        g.addRouteLatLng(startLocation.latitude,startLocation.longitude);
                        g.addRouteLatLng(mylocation.latitude, mylocation.longitude);
                        Leader leader = new Leader();
                        leader.setId(user.getId());
                        g.setLeader(leader);
                        //TODO send permission to user's parents

                        Call<Group> caller = proxy.createNewGroup(g);
                        ProxyBuilder.callProxy(getActivity().getApplicationContext(), caller, returnGroups -> response(returnGroups));
                        floatingActionsMenu.collapse();
                        googleMap.clear();
                        grabGroups();
                    }
                });


                builder.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startLocation = null;
                                dialog.dismiss();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();

            }
        });

        fabStartGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedMarker == null){
                    Toast.makeText(getActivity(),"Please select a group before enabling gps tracking",Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    if(toggleGps) {
                        System.out.println("toggle = true setting to false");
                        toggleGps = false;
                    }
                    else {
                        System.out.println("toggle = false setting to true");
                        toggleGps = true;
                    }
                    updateUserLocation();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void emptyResponse(Void nothing) {}

    /*
    Get user location
     */

    private void updateUserLocation() throws InterruptedException {
        if(toggleGps) {
            handler.post(sendGpsTask);
            handler.postDelayed(timeOutGpsTask,TIME_OUT);
            Toast.makeText(getActivity(),"GPS tracking has started",Toast.LENGTH_LONG).show();
            System.out.println("STARTED GPS");

        } else{
            handler.removeCallbacks(sendGpsTask);
            handler.removeCallbacks(timeOutGpsTask);
            System.out.println("CANCELLED GPS");
            Toast.makeText(getActivity(),"GPS tracking has been cancelled",Toast.LENGTH_LONG).show();
        }
    }

    /*
    Runnable task to update map
     */

    private Runnable sendGpsTask =new  Runnable(){
        @Override
        public void run() {
            grabGpsLocation();
            GPSLocationModel newGPS = new GPSLocationModel();
            newGPS.setLat(mylocation.latitude);
            newGPS.setLng(mylocation.longitude);
            newGPS.setTimestamp(new Date());
            if(previousLong == null || previousLat == null){
                previousLong = mylocation.longitude;
                previousLat = mylocation.latitude;
            }
            //If user moves while gps is active then points will be accumulated.
            if(previousLat == mylocation.latitude || previousLong == mylocation.longitude){
                updateUserPoints();
            }
            Call<GPSLocationModel> caller = proxy.setUserGPS(user.getId(),newGPS);
            ProxyBuilder.callProxy(getActivity(),caller,returnGPS->responseSetChildrenGPS(returnGPS));
            handler.postDelayed(sendGpsTask,TIMER);
        }
    };

    private void updateUserPoints() {
        Call<User> caller=proxy.getUserByEmail(user.getEmail());
        ProxyBuilder.callProxy(getActivity(),caller,returnedUser->responseUserPoint(returnedUser));
    }

    private void responseUserPoint(User returnedUser) {
        Long points = 0L;
        if(returnedUser.getTotalPointsEarned() == null){
            returnedUser.setTotalPointsEarned(points);
        } else{
            points = returnedUser.getTotalPointsEarned();
            points = points + 1L;
            returnedUser.setTotalPointsEarned(points);
        }
        Call<User> caller = proxy.editUser(user.getId(),returnedUser);
        ProxyBuilder.callProxy(getActivity(),caller,pointUser->responseEditUser(pointUser));
    }

    private void responseEditUser(User pointUser) {
        user.setTotalPointsEarned(pointUser.getTotalPointsEarned());
        System.out.println("New user points" + pointUser.getTotalPointsEarned());
    }

    /*
    Runnable task to time out
     */

    private Runnable timeOutGpsTask = new Runnable() {
        @Override
        public void run() {
            handler.removeCallbacks(sendGpsTask);
            System.out.println("TIMED OUT");
            toggleGps = false;
            Toast.makeText(getActivity(),"GPS tracking has been timed out",Toast.LENGTH_LONG).show();
        }
    };
    /*
    Do nothing with setting response for children GPS
     */

    private void responseSetChildrenGPS(GPSLocationModel returnGPS) {
    }

    private void response(Group groups) {
        Log.w(TAG, "Server replied with user: " + groups.toString());

    }


    /*
    Grab the GPS location
     */
    private void grabGpsLocation() {
        locationManager = (LocationManager)
                getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location != null){
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            mylocation = new LatLng(latitude,longitude);
        } else{
            mylocation = new LatLng(0,0);
        }
    }

    /*
    Grab the group
     */
    private void grabGroups(){
        Call<List<Group>> groupCall = proxy.getGroups();
        ProxyBuilder.callProxy(getActivity(),groupCall,returnGroup->response(returnGroup));
    }

    private void response(List<Group> returnGroup){
        Log.w(TAG, "All Users:");
        for (Group g : returnGroup) {
            Log.w(TAG, "    User: " + returnGroup.toString());
            if(g.hasLocation()){
                if(g.getLeader() != null){
                    Call<User> caller = proxy.getUserById(g.getLeader().getId());
                    ProxyBuilder.callProxy(getActivity(),caller,returnLeader->returnCustomLeaderTier(returnLeader,g));
                } else {
                    Marker marker = googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(g.getRouteLatArray().get(0),g.getRouteLngArray().get(0)))
                            .title(g.getGroupDescription()));
                    marker.setTag(g.getId());
                }

            }
        }
        //When info window is click open up groupActivity passing in the group ID of the group that was clicked.
        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Long markerId = (Long) marker.getTag();
                if(markerId == 0L) {

                } else {
                    Intent intent = new Intent(getContext(), GroupActivity.class);
                    intent.putExtra("groupID", markerId);
                    startActivity(intent);
                }
            }
        });
    }

    private void returnCustomLeaderTier(User returnLeader,Group g) {
        Marker marker = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(g.getRouteLatArray().get(0),g.getRouteLngArray().get(0)))
                .title(g.getGroupDescription()));
        marker.setTag(g.getId());
        if(returnLeader.getRewards() != null){
            if(returnLeader.getRewards().getTier() == 1){
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.t1_48));
            } else if(returnLeader.getRewards().getTier() == 2){
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.t2_48));
            } else if(returnLeader.getRewards().getTier() == 3){
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.t3_48));
            } else{

            }
        }
    }

    private void endGroupResponse(Group returnGroup) {
        if(endMarker != null){
            endMarker.remove();
        }
        Double endLat = returnGroup.getRouteLatArray().get(returnGroup.getRouteLatArray().size()-1);
        Double endLng = returnGroup.getRouteLngArray().get(returnGroup.getRouteLngArray().size()-1);
        if(returnGroup.getRouteLatArray().size() != 0){
            endMarker = googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(endLat,endLng))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
            endMarker.setTag(0L);
        }
    }


    /*
    Grab information about children as well as populate their last known location on the map.
     */

    private void grabChildrenLocation(){
        for(Marker marker: childrenMarkers){
            marker.remove();
        }
        childrenMarkers = new ArrayList<>();
        for(ObjectIDModel objectIDModel : user.getMonitorsUsers()){
            Call<User> childrenCall = proxy.getUserById(objectIDModel.getId());
            ProxyBuilder.callProxy(getActivity(),childrenCall,returnedUser-> returnedUser(returnedUser));
        }
    }

    private void returnedUser(User returnedUser) {
        Call<GPSLocationModel> childrenCall = proxy.getUserGps(returnedUser.getId());
        ProxyBuilder.callProxy(getActivity(), childrenCall, returnedGPS -> responseGetChildrenGPS(returnedUser.getName(), returnedGPS));
        System.out.println(returnedUser.getMemberOfGroups().size() + "X # GROUPS IM IN");
        //With the given children user, find which groups they're apart of and get the group from group id
        for (ObjectIDModel model : returnedUser.getMemberOfGroups()) {
            Call<Group> groupCall = proxy.getGroupDetail(model.getId());
            ProxyBuilder.callProxy(getActivity(), groupCall, returnedGroup -> responseReturnedGroup(returnedGroup));

        }
    }


    private void responseGetChildrenGPS(String name,GPSLocationModel returnGPS) {
        if(returnGPS.getLat() == null || returnGPS.getLng() == null ||returnGPS.getTimestamp() == null ){
            return;
        } else{
            DateFormat df = new SimpleDateFormat("MM/dd HH:mm");
            String dateFormat = df.format(returnGPS.getTimestamp());
            String title = user.getName() + ": " + dateFormat;
            Marker marker = googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(returnGPS.getLat(),returnGPS.getLng()))
                    .title(title)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            marker.setTag(0L);
            childrenMarkers.add(marker);
        }
    }


    private void responseReturnedGroup(Group returnedGroup) {
        leaderIdSet.add(returnedGroup.getLeader().getId());
        for(Marker marker: leaderMarkers){
            marker.remove();
        }
        for(Long id : leaderIdSet){
            Call<User> leaderCall = proxy.getUserById(id);
            ProxyBuilder.callProxy(getActivity(),leaderCall,returnedUser-> returnedLeaderUser(returnedUser));
        }
    }

    private void returnedLeaderUser(User returnedUser) {
        Call<GPSLocationModel> childrenCall = proxy.getUserGps(returnedUser.getId());
        ProxyBuilder.callProxy(getActivity(),childrenCall,returnGPS-> responseGetLeaderGPS(returnedUser.getName(),returnGPS));
    }

    private void responseGetLeaderGPS(String name, GPSLocationModel returnGPS) {
        if(returnGPS.getLat() == null || returnGPS.getLng() == null ||returnGPS.getTimestamp() == null ){
            return;
        } else{
            DateFormat df = new SimpleDateFormat("MM/dd HH:mm");
            String dateFormat = df.format(returnGPS.getTimestamp());
            String title = user.getName() + ": " + dateFormat;
            Marker marker = googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(returnGPS.getLat(),returnGPS.getLng()))
                    .title(title)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            marker.setTag(0L);
            leaderMarkers.add(marker);
        }

    }




}
