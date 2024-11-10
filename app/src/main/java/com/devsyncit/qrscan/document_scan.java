package com.devsyncit.qrscan;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;


public class document_scan extends AppCompatActivity {

    ListView image_list;
    public static int page_count = 1;
    public static Uri image_uri = null;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_scan);


        image_list = findViewById(R.id.image_list);

        ListAdapter listAdapter = new ListAdapter();
        image_list.setAdapter(listAdapter);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);




    }

    public class ListAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return page_count;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            convertView = inflater.inflate(R.layout.page_layout, null);

            ImageView image = convertView.findViewById(R.id.image);

            image.setImageURI(image_uri);

            return convertView;
        }
    }
}