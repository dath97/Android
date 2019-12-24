package com.ce411.project.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.ce411.project.R;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {
    private Context context;
    private int pos;
    private LayoutInflater inflater;
    private ImageView imageView;
    ArrayList<Uri> mArrayUri;
    public ImageAdapter(Context ctx,ArrayList<Uri> mArrayUri){
        this.context = ctx;
        this.mArrayUri = mArrayUri;
    }
    @Override
    public int getCount(){
        return mArrayUri.size();
    }
    @Override
    public Uri getItem(int pos){
        return mArrayUri.get(pos);
    }
    @Override
    public long getItemId(int pos){
        return 0;
    }
    public ArrayList<Uri> getAllItem(){
        return mArrayUri;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        pos = position;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.item_image,parent,false);
        imageView = (ImageView)itemView.findViewById(R.id.ivGallery);
        imageView.setImageURI(mArrayUri.get(position));
        return itemView;
    }
}
