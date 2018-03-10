package com.chkk.arisong_arisu.Fragment;


import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.chkk.arisong_arisu.MainActivity;
import com.chkk.arisong_arisu.R;
import com.chkk.arisong_arisu.RecyclerAdapter;
import com.chkk.arisong_arisu.locationDTO;
import com.chkk.arisong_arisu.saveData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    ArrayList<String> titles = new ArrayList<String>();
    ArrayList<String> details = new ArrayList<String>();
    ArrayList<Double> distance = new ArrayList<Double>();
    ArrayList<String> condition = new ArrayList<String>();
    ArrayList<String> pictureurl = new ArrayList<String>();

    TextView itemCnt;
    RecyclerView recyclerView;
    private saveData data;

    double lat,lon;

    private FirebaseDatabase fdb;
    private DatabaseReference  mRef;
    private StorageReference sRef;

    private RecyclerAdapter adapter;
    private RecyclerView.LayoutManager lm;

    ArrayAdapter<CharSequence> SpinAdapter, SecSpinAdpater;

    String choice_gu="";
    String choice_dong="";

    Typeface regular;
    MainActivity Ma;

    /*data/data/패키지이름/files/추가폴더이름(있어도되고 없어도댐)/파일이름.png*/
    String imgPath = "data/data/com.chkk.arisong_arisu/files/";

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        data = new saveData(getActivity());
        Ma = new MainActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_home, container, false);

        try {
            data.open();
            Cursor all_cursor = data.AllRows();
            all_cursor.moveToFirst();
        }catch (DatabaseException e){
            Toast.makeText(getActivity().getApplicationContext(), "Data가 존재하지않음", Toast.LENGTH_SHORT).show();
        }

        setLatLon();

        final Spinner spin_gu = (Spinner) layout.findViewById(R.id.Spinner_gu);
        final Spinner spin_dong = (Spinner) layout.findViewById(R.id.Spinner_dong);

        itemCnt = (TextView) layout.findViewById(R.id.itemcount);

        //폰트타입설정
        regular = Typeface.createFromAsset(getActivity().getAssets(), "NanumBarunGothicBold.ttf");
        try{
            Field staticField = Typeface.class.getDeclaredField("MONOSPACE");
            staticField.setAccessible(true);
            staticField.set(null,regular);
            Log.i("font","Successful");
        }catch (Exception e){
            Log.i("font","Error : "+e.toString());
        }

        SpinAdapter = ArrayAdapter.createFromResource(getActivity().getApplicationContext(),R.array.gu,
                R.layout.spinner_item);
        spin_gu.setAdapter(SpinAdapter);
        SpinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_gu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            //판도라의상자
            //열면 닫기귀찮음
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (SpinAdapter.getItem(i).equals("강남구")){
                    choice_gu = SpinAdapter.getItem(i).toString();
                    spin_gu.setSelection(i);
                    SecSpinAdpater = ArrayAdapter.createFromResource(getActivity().getApplicationContext(),
                            R.array.강남구,
                            R.layout.spinner_item);
                    SecSpinAdpater.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spin_dong.setAdapter(SecSpinAdpater);
                    spin_dong.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            choice_dong = SecSpinAdpater.getItem(i).toString();
                            spin_dong.setSelection(i);
                            bigyo(choice_gu+" "+choice_dong);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
                        }
                    });
                }else if (SpinAdapter.getItem(i).equals("강동구")){
                    choice_gu = SpinAdapter.getItem(i).toString();
                    spin_gu.setSelection(i);
                    SecSpinAdpater = ArrayAdapter.createFromResource(getActivity().getApplicationContext(),
                            R.array.강동구,
                            R.layout.spinner_item);
                    SecSpinAdpater.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spin_dong.setAdapter(SecSpinAdpater);
                    spin_dong.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            choice_dong = SecSpinAdpater.getItem(i).toString();
                            spin_dong.setSelection(i);
                            bigyo(choice_gu+" "+choice_dong);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }else if (SpinAdapter.getItem(i).equals("강북구")) {
                    choice_gu = SpinAdapter.getItem(i).toString();
                    spin_gu.setSelection(i);
                    SecSpinAdpater = ArrayAdapter.createFromResource(getActivity().getApplicationContext(),
                            R.array.강북구,
                            R.layout.spinner_item);
                    SecSpinAdpater.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spin_dong.setAdapter(SecSpinAdpater);
                    spin_dong.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            choice_dong = SecSpinAdpater.getItem(i).toString();
                            spin_dong.setSelection(i);
                            bigyo(choice_gu+" "+choice_dong);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                }else if (SpinAdapter.getItem(i).equals("강서구")) {
                    choice_gu = SpinAdapter.getItem(i).toString();
                    spin_gu.setSelection(i);
                    SecSpinAdpater = ArrayAdapter.createFromResource(getActivity().getApplicationContext(),
                            R.array.강서구,
                            R.layout.spinner_item);
                    SecSpinAdpater.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spin_dong.setAdapter(SecSpinAdpater);
                    spin_dong.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            choice_dong = SecSpinAdpater.getItem(i).toString();
                            spin_dong.setSelection(i);
                            bigyo(choice_gu+" "+choice_dong);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                }else if (SpinAdapter.getItem(i).equals("관악구")) {
                    choice_gu = SpinAdapter.getItem(i).toString();
                    spin_gu.setSelection(i);
                    SecSpinAdpater = ArrayAdapter.createFromResource(getActivity().getApplicationContext(),
                            R.array.관악구,
                            R.layout.spinner_item);
                    SecSpinAdpater.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spin_dong.setAdapter(SecSpinAdpater);
                    spin_dong.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            choice_dong = SecSpinAdpater.getItem(i).toString();
                            spin_dong.setSelection(i);
                            bigyo(choice_gu+" "+choice_dong);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                }else if (SpinAdapter.getItem(i).equals("광진구")) {
                    choice_gu = SpinAdapter.getItem(i).toString();
                    spin_gu.setSelection(i);
                    SecSpinAdpater = ArrayAdapter.createFromResource(getActivity().getApplicationContext(),
                            R.array.광진구,
                            R.layout.spinner_item);
                    SecSpinAdpater.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spin_dong.setAdapter(SecSpinAdpater);
                    spin_dong.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            choice_dong = SecSpinAdpater.getItem(i).toString();
                            spin_dong.setSelection(i);
                            bigyo(choice_gu+" "+choice_dong);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                }else if (SpinAdapter.getItem(i).equals("구로구")) {
                    choice_gu = SpinAdapter.getItem(i).toString();
                    spin_gu.setSelection(i);
                    SecSpinAdpater = ArrayAdapter.createFromResource(getActivity().getApplicationContext(),
                            R.array.구로구,
                            R.layout.spinner_item);
                    SecSpinAdpater.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spin_dong.setAdapter(SecSpinAdpater);
                    spin_dong.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            choice_dong = SecSpinAdpater.getItem(i).toString();
                            spin_dong.setSelection(i);
                            bigyo(choice_gu+" "+choice_dong);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                }else if (SpinAdapter.getItem(i).equals("금천구")) {
                    choice_gu = SpinAdapter.getItem(i).toString();
                    spin_gu.setSelection(i);
                    SecSpinAdpater = ArrayAdapter.createFromResource(getActivity().getApplicationContext(),
                            R.array.금천구,
                            R.layout.spinner_item);
                    SecSpinAdpater.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spin_dong.setAdapter(SecSpinAdpater);
                    spin_dong.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            choice_dong = SecSpinAdpater.getItem(i).toString();
                            spin_dong.setSelection(i);
                            bigyo(choice_gu+" "+choice_dong);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                }else if (SpinAdapter.getItem(i).equals("노원구")) {
                    choice_gu = SpinAdapter.getItem(i).toString();
                    spin_gu.setSelection(i);
                    SecSpinAdpater = ArrayAdapter.createFromResource(getActivity().getApplicationContext(),
                            R.array.노원구,
                            R.layout.spinner_item);
                    SecSpinAdpater.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spin_dong.setAdapter(SecSpinAdpater);
                    spin_dong.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            choice_dong = SecSpinAdpater.getItem(i).toString();
                            spin_dong.setSelection(i);
                            bigyo(choice_gu+" "+choice_dong);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                }else if (SpinAdapter.getItem(i).equals("도봉구")) {
                    choice_gu = SpinAdapter.getItem(i).toString();
                    spin_gu.setSelection(i);
                    SecSpinAdpater = ArrayAdapter.createFromResource(getActivity().getApplicationContext(),
                            R.array.도봉구,
                            R.layout.spinner_item);
                    SecSpinAdpater.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spin_dong.setAdapter(SecSpinAdpater);
                    spin_dong.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            choice_dong = SecSpinAdpater.getItem(i).toString();
                            spin_dong.setSelection(i);
                            bigyo(choice_gu+" "+choice_dong);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                }else if (SpinAdapter.getItem(i).equals("동대문구")) {
                    choice_gu = SpinAdapter.getItem(i).toString();
                    spin_gu.setSelection(i);
                    SecSpinAdpater = ArrayAdapter.createFromResource(getActivity().getApplicationContext(),
                            R.array.동대문구,
                            R.layout.spinner_item);
                    SecSpinAdpater.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spin_dong.setAdapter(SecSpinAdpater);
                    spin_dong.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            choice_dong = SecSpinAdpater.getItem(i).toString();
                            spin_dong.setSelection(i);
                            bigyo(choice_gu+" "+choice_dong);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                }else if (SpinAdapter.getItem(i).equals("동작구")) {
                    choice_gu = SpinAdapter.getItem(i).toString();
                    spin_gu.setSelection(i);
                    SecSpinAdpater = ArrayAdapter.createFromResource(getActivity().getApplicationContext(),
                            R.array.동작구,
                            R.layout.spinner_item);
                    SecSpinAdpater.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spin_dong.setAdapter(SecSpinAdpater);
                    spin_dong.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            choice_dong = SecSpinAdpater.getItem(i).toString();
                            spin_dong.setSelection(i);
                            bigyo(choice_gu+" "+choice_dong);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                }else if (SpinAdapter.getItem(i).equals("마포구")) {
                    choice_gu = SpinAdapter.getItem(i).toString();
                    spin_gu.setSelection(i);
                    SecSpinAdpater = ArrayAdapter.createFromResource(getActivity().getApplicationContext(),
                            R.array.마포구,
                            R.layout.spinner_item);
                    SecSpinAdpater.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spin_dong.setAdapter(SecSpinAdpater);
                    spin_dong.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            choice_dong = SecSpinAdpater.getItem(i).toString();
                            spin_dong.setSelection(i);
                            bigyo(choice_gu+" "+choice_dong);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                }else if (SpinAdapter.getItem(i).equals("서대문구")) {
                    choice_gu = SpinAdapter.getItem(i).toString();
                    spin_gu.setSelection(i);
                    SecSpinAdpater = ArrayAdapter.createFromResource(getActivity().getApplicationContext(),
                            R.array.서대문구,
                            R.layout.spinner_item);
                    SecSpinAdpater.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spin_dong.setAdapter(SecSpinAdpater);
                    spin_dong.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            choice_dong = SecSpinAdpater.getItem(i).toString();
                            spin_dong.setSelection(i);
                            bigyo(choice_gu+" "+choice_dong);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                }else if (SpinAdapter.getItem(i).equals("서초구")) {
                    choice_gu = SpinAdapter.getItem(i).toString();
                    spin_gu.setSelection(i);
                    SecSpinAdpater = ArrayAdapter.createFromResource(getActivity().getApplicationContext(),
                            R.array.서초구,
                            R.layout.spinner_item);
                    SecSpinAdpater.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spin_dong.setAdapter(SecSpinAdpater);
                    spin_dong.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            choice_dong = SecSpinAdpater.getItem(i).toString();
                            spin_dong.setSelection(i);
                            bigyo(choice_gu+" "+choice_dong);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                }else if (SpinAdapter.getItem(i).equals("성동구")) {
                    choice_gu = SpinAdapter.getItem(i).toString();
                    spin_gu.setSelection(i);
                    SecSpinAdpater = ArrayAdapter.createFromResource(getActivity().getApplicationContext(),
                            R.array.성동구,
                            R.layout.spinner_item);
                    SecSpinAdpater.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spin_dong.setAdapter(SecSpinAdpater);
                    spin_dong.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            choice_dong = SecSpinAdpater.getItem(i).toString();
                            spin_dong.setSelection(i);
                            bigyo(choice_gu+" "+choice_dong);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                }else if (SpinAdapter.getItem(i).equals("성북구")) {
                    choice_gu = SpinAdapter.getItem(i).toString();
                    spin_gu.setSelection(i);
                    SecSpinAdpater = ArrayAdapter.createFromResource(getActivity().getApplicationContext(),
                            R.array.성북구,
                            R.layout.spinner_item);
                    SecSpinAdpater.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spin_dong.setAdapter(SecSpinAdpater);
                    spin_dong.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            choice_dong = SecSpinAdpater.getItem(i).toString();
                            spin_dong.setSelection(i);
                            bigyo(choice_gu+" "+choice_dong);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                }else if (SpinAdapter.getItem(i).equals("송파구")) {
                    choice_gu = SpinAdapter.getItem(i).toString();
                    spin_gu.setSelection(i);
                    SecSpinAdpater = ArrayAdapter.createFromResource(getActivity().getApplicationContext(),
                            R.array.송파구,
                            R.layout.spinner_item);
                    SecSpinAdpater.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spin_dong.setAdapter(SecSpinAdpater);
                    spin_dong.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            choice_dong = SecSpinAdpater.getItem(i).toString();
                            spin_dong.setSelection(i);
                            bigyo(choice_gu+" "+choice_dong);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                }else if (SpinAdapter.getItem(i).equals("양천구")) {
                    choice_gu = SpinAdapter.getItem(i).toString();
                    spin_gu.setSelection(i);
                    SecSpinAdpater = ArrayAdapter.createFromResource(getActivity().getApplicationContext(),
                            R.array.양천구,
                            R.layout.spinner_item);
                    SecSpinAdpater.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spin_dong.setAdapter(SecSpinAdpater);
                    spin_dong.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            choice_dong = SecSpinAdpater.getItem(i).toString();
                            spin_dong.setSelection(i);
                            bigyo(choice_gu+" "+choice_dong);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                }else if (SpinAdapter.getItem(i).equals("영등포구")) {
                    choice_gu = SpinAdapter.getItem(i).toString();
                    spin_gu.setSelection(i);
                    SecSpinAdpater = ArrayAdapter.createFromResource(getActivity().getApplicationContext(),
                            R.array.영등포구,
                            R.layout.spinner_item);
                    SecSpinAdpater.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spin_dong.setAdapter(SecSpinAdpater);
                    spin_dong.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            choice_dong = SecSpinAdpater.getItem(i).toString();
                            spin_dong.setSelection(i);
                            bigyo(choice_gu+" "+choice_dong);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                }else if (SpinAdapter.getItem(i).equals("용산구")) {
                    choice_gu = SpinAdapter.getItem(i).toString();
                    spin_gu.setSelection(i);
                    SecSpinAdpater = ArrayAdapter.createFromResource(getActivity().getApplicationContext(),
                            R.array.용산구,
                            R.layout.spinner_item);
                    SecSpinAdpater.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spin_dong.setAdapter(SecSpinAdpater);
                    spin_dong.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            choice_dong = SecSpinAdpater.getItem(i).toString();
                            spin_dong.setSelection(i);
                            bigyo(choice_gu+" "+choice_dong);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                }else if (SpinAdapter.getItem(i).equals("은평구")) {
                    choice_gu = SpinAdapter.getItem(i).toString();
                    spin_gu.setSelection(i);
                    SecSpinAdpater = ArrayAdapter.createFromResource(getActivity().getApplicationContext(),
                            R.array.은평구,
                            R.layout.spinner_item);
                    SecSpinAdpater.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spin_dong.setAdapter(SecSpinAdpater);
                    spin_dong.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            choice_dong = SecSpinAdpater.getItem(i).toString();
                            spin_dong.setSelection(i);
                            bigyo(choice_gu+" "+choice_dong);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                }else if (SpinAdapter.getItem(i).equals("종로구")) {
                    choice_gu = SpinAdapter.getItem(i).toString();
                    spin_gu.setSelection(i);
                    SecSpinAdpater = ArrayAdapter.createFromResource(getActivity().getApplicationContext(),
                            R.array.종로구,
                            R.layout.spinner_item);
                    SecSpinAdpater.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spin_dong.setAdapter(SecSpinAdpater);
                    spin_dong.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            choice_dong = SecSpinAdpater.getItem(i).toString();
                            spin_dong.setSelection(i);
                            bigyo(choice_gu+" "+choice_dong);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                }else if (SpinAdapter.getItem(i).equals("중구")) {
                    choice_gu = SpinAdapter.getItem(i).toString();
                    spin_gu.setSelection(i);
                    SecSpinAdpater = ArrayAdapter.createFromResource(getActivity().getApplicationContext(),
                            R.array.중구,
                            R.layout.spinner_item);
                    SecSpinAdpater.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spin_dong.setAdapter(SecSpinAdpater);
                    spin_dong.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            choice_dong = SecSpinAdpater.getItem(i).toString();
                            spin_dong.setSelection(i);
                            bigyo(choice_gu+" "+choice_dong);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                }else if (SpinAdapter.getItem(i).equals("중랑구")) {
                    choice_gu = SpinAdapter.getItem(i).toString();
                    spin_gu.setSelection(i);
                    SecSpinAdpater = ArrayAdapter.createFromResource(getActivity().getApplicationContext(),
                            R.array.중랑구,
                            R.layout.spinner_item);
                    SecSpinAdpater.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spin_dong.setAdapter(SecSpinAdpater);
                    spin_dong.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            choice_dong = SecSpinAdpater.getItem(i).toString();
                            spin_dong.setSelection(i);
                            bigyo(choice_gu+" "+choice_dong);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        sRef = FirebaseStorage.getInstance().getReference();
        fdb = FirebaseDatabase.getInstance();
        mRef = fdb.getReference().child("LocationDB");

        recyclerView = (RecyclerView) layout.findViewById(R.id.recycler_view);
        //recyclerView.setAdapter(new RecyclerAdapter());
        recyclerView.setHasFixedSize(true);

        setRecycleView(mRef, layout);

        return layout;
    }

    private void savePic(String picurl) {

        try {
            Log.i("pic", picurl);

            File f = new File(imgPath);

            final File localfile = new File(f, picurl + ".png");
            StorageReference imageRef = sRef.child(picurl + ".png");
            imageRef.getFile(localfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Log.e("firebase", "localfile created" + localfile.toString());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("firebase", "localfile not created" + e.toString());
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void bigyo(String str){
        int cnt = 0;
        Log.i("strget",str);
//        Toast.makeText(getActivity().getApplicationContext(), str, Toast.LENGTH_SHORT).show();
        for (int i = 0; i < titles.size(); i++){
            Log.i("strget2",titles.get(i)+"");
            if (str.equalsIgnoreCase(titles.get(i))){
                cnt++;//setVisibility(View.VISIBLE);
            }
        }
        itemCnt.setText(String.valueOf(cnt)+" 개");
    }

    public void setLatLon(){
        this.lat = Ma.getLat();
        this.lon = Ma.getLon();
    }

    //거리구하기코드
    public double GetDistance(double lat, double lon, double blat, double blon){
        double theta = lon - blon;
        double dist = Math.sin(deg2rad(lat)) * Math.sin(deg2rad(blat)) + Math.cos(deg2rad(lat)) * Math.cos(deg2rad(blat)) * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = (dist * 60 * 1.1515)*1.609344;

        return (Double.parseDouble(String.format("%.1f",dist)));
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

    public void setRecycleView(DatabaseReference mRef, View layout) {

        mRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                setLatLon();

                int i = 0;
                adapter = new RecyclerAdapter();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    locationDTO lDTO = snapshot.getValue(locationDTO.class);

                    String picurl = "m"+(Integer.parseInt(snapshot.getKey())-1);

                    titles.add(lDTO.getLcatesmallName());
                    //Log.i("get","받음");
                    details.add(lDTO.getLocationName());
                    //lat,lon :현재위치 blat,blon :아리수위치
                    distance.add(GetDistance(lat,lon,lDTO.getWedo(),lDTO.getGyoundo()));
                    //Log.i("get",String.valueOf(GetDistance(37.504198, 127.047967 , lDTO.getGyoundo() ,  lDTO.getWedo())));
                    condition.add("적합");
                    pictureurl.add(picurl);

                    File f = new File(imgPath + picurl + ".png");

                    if (!f.exists()){
                        savePic(picurl);
                    }

/*
                    값이 받아와지는지 확인하는 Log들.
                    Log.i("gap1",titles.get(i)+" ");
                    Log.i("gap1",details.get(i));
                    Log.i("gap1",String.valueOf(distance.get(i)));
                    Log.i("gap1",String.valueOf(condition.get(i)));
                    Log.i("gap2",lDTO.getLcatesmallName()+"");
                    Log.i("gap2",lDTO.getLocationName()+"");
                    Log.i("gap2",lDTO.getGyoundo()+"");
                    Log.i("gap2",lDTO.getWedo()+"");
                    Log.i("gap2",lDTO.getPictureUrl()+"");
                    Log.i("gap2",picurl);
                    Log.i("gap2",pictureurl.get(i));
*/

                    adapter.addItem(titles.get(i),details.get(i),distance.get(i),condition.get(i),pictureurl.get(i));
                    if (adapter != null) {
                        recyclerView.setAdapter(adapter);
                        Log.i("test","adapter");
                    }
                    i++;
                }
                lm = new LinearLayoutManager(getActivity().getApplication());
                recyclerView.setLayoutManager(lm);
                recyclerView.setHasFixedSize(true);
                recyclerView.getLayoutManager().setMeasurementCacheEnabled(false);
                itemCnt.setText(String.valueOf(adapter.getItemCount())+" 개");
                bigyo("송파구 가락동");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("HomeFragment", "Error");
            }

        });
    }
}
