package com.chkk.arisong_arisu;

import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by GingGingI on 2017-10-28.
 */


public class RecyclerAdapter extends RecyclerView.Adapter<ViewHolder>{

    ArrayList<String> titles = new ArrayList<>();
    ArrayList<String> address = new ArrayList<>();
    ArrayList<Double> distance = new ArrayList<>();
    ArrayList<String> condition = new ArrayList<>();
    ArrayList<String> arisuPicture = new ArrayList<>();

    String imgPath = "data/data/com.chkk.arisong_arisu/files/";

    public RecyclerAdapter() {}

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardlayout, parent, false);
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.titles.setText(address.get(position));
        holder.distance.setText(distance.get(position) + "Km");
        holder.address.setText(titles.get(position));
        holder.Condition.setText("수질검사: " + condition.get(position));
        try {
            File files = new File(imgPath + arisuPicture.get(position) + ".png");
            holder.picture.setImageURI(Uri.fromFile(files));
        }catch (Exception e){
            Log.e("error", "imageNotFound");
        }
    }

    @Override
    public int getItemCount() {
        return (titles != null) ? titles.size() : 0;
    }

    public void addItem (String titles, String address, double distance, String condition, String bitmap){

        this.titles.add(titles);
        this.address.add(address);
        this.distance.add(distance);
        this.condition.add(condition);
        this.arisuPicture.add(bitmap);
    }
}

class ViewHolder extends RecyclerView.ViewHolder{

    Typeface regular;

    public TextView titles;
    public TextView distance;
    public TextView address;
    public TextView Condition;
    public ImageView picture;

    public ViewHolder(View itemView) {
        super(itemView);
        titles = (TextView) itemView.findViewById(R.id.Arisu_Title);
        distance = (TextView) itemView.findViewById(R.id.Arisu_distance);
        address = (TextView) itemView.findViewById(R.id.Arisu_Address);
        Condition = (TextView) itemView.findViewById(R.id.Arisu_WaterCondition);
        picture = (ImageView) itemView.findViewById(R.id.Arisu_picture);

        regular = Typeface.createFromAsset(itemView.getContext().getAssets(), "NanumBarunGothicBold.ttf");
        GetAsset();
        SetFont();
    }

    private void GetAsset(){
        try{
            Field staticField = Typeface.class.getDeclaredField("MONOSPACE");
            staticField.setAccessible(true);
            staticField.set(null,regular);
            Log.i("font","Successful");
        }catch (Exception e){
            Log.i("font","Error : "+e.toString());
        }
    }
    private void SetFont(){
        titles.setTypeface(regular);
        distance.setTypeface(regular);
        address.setTypeface(regular);
        Condition.setTypeface(regular);
    }
}