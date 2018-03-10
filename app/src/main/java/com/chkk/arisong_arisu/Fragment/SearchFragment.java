package com.chkk.arisong_arisu.Fragment;


import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chkk.arisong_arisu.LocationData;
import com.chkk.arisong_arisu.MainActivity;
import com.chkk.arisong_arisu.R;
import com.chkk.arisong_arisu.locationDTO;
import com.chkk.arisong_arisu.saveData;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */

public class SearchFragment extends Fragment implements OnMapReadyCallback {

    ArrayList<String> titles = new ArrayList<String>();
    ArrayList<String> details = new ArrayList<String>();
    ArrayList<Double> Lat = new ArrayList<Double>();
    ArrayList<Double> Lon = new ArrayList<Double>();
    private LinearLayout linearlayout;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    public double lat = 37,lon = 127;
    String UMarkerId;
    int id;

    private GoogleMap mMap;
    private MapView mapView = null;
    HomeFragment hf;
    SettingFragment sf;
    MainActivity mia;
    LocationData ldata;
    Typeface regular;
   // private double Latitude;
    private double Hardness;
    MarkerOptions nowLocation;
    Marker marker;
    private double Latitude = 0;
    private LatLng Position,Location;
    private saveData data;
    private int textSize;
    final String imgPath = "data/data/com.chkk.arisong_arisu/files/";
    private TextView gu,smallName,distance;
    private ImageView picture;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        data = new saveData(getActivity());
        data.open();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_search, container, false);

        Cursor all_cursor = data.AllRows();
        all_cursor.moveToFirst();

        linearlayout =layout.findViewById(R.id.getmore);

        mapView = layout.findViewById(R.id.googlemap);
        mapView.getMapAsync(this);
        gu = layout.findViewById(R.id.gu);
        smallName = layout.findViewById(R.id.smallname);
        distance = layout.findViewById(R.id.distance);
        picture = layout.findViewById(R.id.picture);

        if (data.ChkDB()) {
            textSize = all_cursor.getInt(all_cursor.getColumnIndex("TEXTSIZE"));
            gu.setTextSize(textSize + 19);
            smallName.setTextSize(textSize + 10);
        }else{
            sf.dbset();
        }
        regular = Typeface.createFromAsset(getActivity().getAssets(), "NanumBarunGothicBold.ttf");
        try{
            Field staticField = Typeface.class.getDeclaredField("MONOSPACE");
            staticField.setAccessible(true);
            staticField.set(null,regular);
            Log.i("font","Successful");
        }catch (Exception e){
            Log.i("font","Error : "+e.toString());
        }
        linearlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearlayout.setVisibility(View.GONE);
            }
        });

        return layout;
    }
    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onLowMemory();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //액티비티가 처음 생성될 때 실행되는 함수

        if(mapView != null)
        {
            mapView.onCreate(savedInstanceState);
            mia  = new MainActivity();
//            mia.ProgaressDialogoff();
        }
    }

    public void setPosition() {
        delmark();
        setLatLon();
        Position = new LatLng(lat, lon);
        Log.i(Position+"","sdfsdsdsdsdddsdsdsddddddddddd");
        //    Log.i("SDSD", "DSDS12");

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Position, 15));
        nowLocation.position(Position);
        nowLocation.icon(BitmapDescriptorFactory.fromResource(R.drawable.usermaker));
        marker = mMap.addMarker(nowLocation);
        UMarkerId = marker.getId();
    }
    public void delmark(){
        if (marker != null)
        marker.remove();
        nowLocation = new MarkerOptions();
    }
    public void setLatLon() {
        this.lat = mia.getLat();
        this.lon = mia.getLon();
    }

    @Override
    public  void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setPosition();
        if(Location == null) {
            getDatas();
        }
    }

    private void getDatas(){
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        hf = new HomeFragment();

        databaseReference.child("LocationDB").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override

            public void onDataChange(DataSnapshot dataSnapshot) {
                int a = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    locationDTO locationDTO = snapshot.getValue(locationDTO.class);
                    Log.d("asd", "값받아옴");
                    Latitude = locationDTO.getWedo();
                    Log.d("asd", "값받아옴2");
                    Hardness = locationDTO.getGyoundo();
                    titles.add(locationDTO.getLcatesmallName());
                    details.add(locationDTO.getLocationName());

                    //lat,lon :현재위치 blat,blon :아리수위치
                    Lat.add(Latitude);
                    Lon.add(Hardness);

                    Location = new LatLng(Latitude, Hardness);
                    Log.d("asd", "경도 설정함" + Latitude + " / " + Hardness + "\t " + a + "번째임");

                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(Location);
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
                    markerOptions.title(details.get(a));
                    markerOptions.snippet(titles.get(a));
                    mMap.addMarker(markerOptions);

                    //   mMap
                    mMap.setOnInfoWindowClickListener(infoWindowClickListener);
                    mMap.setOnMarkerClickListener(markerClickListener);
                    a++;
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("SDSDSDSSDSDSDSDS", "SDSDSDSDSDSDSDSDSDS");
            }}

        );
    }

    //정보창 클릭 리스너
    GoogleMap.OnInfoWindowClickListener infoWindowClickListener = new GoogleMap.OnInfoWindowClickListener() {
        @Override
        public void onInfoWindowClick(Marker marker) {
        }
    };


    //마커 클릭 리스너
    GoogleMap.OnMarkerClickListener markerClickListener = new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            String markerId =  String.valueOf(marker.getId()); //String.valueOf(Integer.parseInt(marker.getId()));
            //선택한 타겟위치
            if(markerId.equals("m0")||markerId.equals(UMarkerId)){
                if (markerId.equals(UMarkerId)){
                    Toast.makeText(getActivity().getApplicationContext(),
                            "현재위치"
                                    +"\n"+
                                    "Lat : "+String.format("%.5f",lat)
                                    +"\n"+
                                    "lon : "+String.format("%.5f",lon)
                            , Toast.LENGTH_LONG).show();
                }
                return  false;
            }
            linearlayout.setVisibility(View.VISIBLE);
            id = Integer.parseInt(markerId.substring(1));
            setImage(id);
            Log.i("getid",id+"");
            smallName.setText(titles.get(id-1));
            gu.setText(details.get(id-1));
            if (hf.GetDistance(lat,lon,Lat.get(id-1),Lon.get(id-1)) > 0.1){
                distance.setText(hf.GetDistance(lat,lon,Lat.get(id-1),Lon.get(id-1))+"km");
            }else{
                distance.setText("근처에있음");
            }

            return false;
        }
    };

    public void setImage(int id){
        String picurl = "m"+(id-1);
        try {
            File files = new File(imgPath + picurl + ".png");
            picture.setImageURI(Uri.fromFile(files));
        }catch (Exception e){
            Log.e("error", "imageNotFound");
        }
    }

}


