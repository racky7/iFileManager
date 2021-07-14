package com.racky.ifilemanager;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class home extends Fragment {


    public home() {
        // Required empty public constructor
    }


    ImageView imageBtn, docBtn, videoBtn, musicBtn;
    ListView listView;
//    private String ftype;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

        imageBtn = v.findViewById(R.id.imageBtn);
        docBtn = v.findViewById(R.id.docBtn);
        musicBtn = v.findViewById(R.id.musicBtn);
        videoBtn = v.findViewById(R.id.videoBtn);


        listView = (ListView) v.findViewById(R.id.listView);

        File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath());


        musicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fetchSongs(root);


            }
        });

        docBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fetchDoc(root);


            }
        });

        videoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchVideo(root);

            }
        });

        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fetchImg(root);
            }
        });


        return v;
    }


    public ArrayList<File> fetchSongs(File file) {
        ArrayList arrayList = new ArrayList();


        File[] songs = file.listFiles();
        arrayList.clear();


        if (songs != null) {
            for (File myFile : songs) {
                if (!myFile.isHidden() && myFile.isDirectory()) {
                    arrayList.addAll(fetchSongs(myFile));


                } else {
                    if (myFile.getName().endsWith(".mp3") && !myFile.getName().startsWith(".")) {
                        arrayList.add(myFile);


                    }
                }
            }

        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getBaseContext(), android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                File myFile2 = new File(arrayList.get(position).toString());
                Intent target = new Intent(Intent.ACTION_VIEW);

                final Intent intent1 = target.setDataAndType(FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() + ".provider", myFile2), "audio/*");

                target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);


                Intent intent = Intent.createChooser(target, "Open File");
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    // Instruct the user to install a PDF reader here, or something
                }

            }
        });


        return arrayList;
    }

    public ArrayList<File> fetchImg(File file) {
        ArrayList arrayList = new ArrayList();


        File[] songs = file.listFiles();
        arrayList.clear();


        if (songs != null) {
            for (File myFile : songs) {
                if (!myFile.isHidden() && myFile.isDirectory()) {
                    arrayList.addAll(fetchImg(myFile));


                } else {
                    if (myFile.getName().endsWith(".png") || myFile.getName().endsWith(".jpg") || myFile.getName().endsWith(".jpeg") || myFile.getName().endsWith(".gif")   && !myFile.getName().startsWith(".")) {
                        arrayList.add(myFile);


                    }
                }
            }

        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getBaseContext(), android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                File myFile2 = new File(arrayList.get(position).toString());
                Intent target = new Intent(Intent.ACTION_VIEW);


                final Intent intent1 = target.setDataAndType(FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() + ".provider", myFile2), "image/*");

                target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                Intent intent = Intent.createChooser(target, "Open File");
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    // Instruct the user to install a PDF reader here, or something
                }

            }
        });


        return arrayList;
    }

    public ArrayList<File> fetchVideo(File file) {
        ArrayList arrayList = new ArrayList();


        File[] songs = file.listFiles();
        arrayList.clear();


        if (songs != null) {
            for (File myFile : songs) {
                if (!myFile.isHidden() && myFile.isDirectory()) {
                    arrayList.addAll(fetchVideo(myFile));


                } else {
                    if (myFile.getName().endsWith(".mp4") || myFile.getName().endsWith(".wav")  && !myFile.getName().startsWith(".")) {
                        arrayList.add(myFile);


                    }
                }
            }

        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getBaseContext(), android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                File myFile2 = new File(arrayList.get(position).toString());
                Intent target = new Intent(Intent.ACTION_VIEW);

                final Intent intent1 = target.setDataAndType(FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() + ".provider", myFile2), "video/*");
                target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);


                Intent intent = Intent.createChooser(target, "Open File");
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    // Instruct the user to install a PDF reader here, or something
                }

            }
        });


        return arrayList;
    }

    public ArrayList<File> fetchDoc(File file) {
        ArrayList arrayList = new ArrayList();


        File[] songs = file.listFiles();
        arrayList.clear();


        if (songs != null) {
            for (File myFile : songs) {
                if (!myFile.isHidden() && myFile.isDirectory()) {
                    arrayList.addAll(fetchDoc(myFile));


                } else {
                    if (myFile.getName().endsWith(".pdf") && !myFile.getName().startsWith(".")) {
                        arrayList.add(myFile);


                    }
                }
            }

        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getBaseContext(), android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                File myFile2 = new File(arrayList.get(position).toString());
                Intent target = new Intent(Intent.ACTION_VIEW);

                final Intent intent1 = target.setDataAndType(FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() + ".provider", myFile2), "application/pdf");
                target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                Intent intent = Intent.createChooser(target, "Open File");
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    // Instruct the user to install a PDF reader here, or something
                }

            }
        });


        return arrayList;
    }


}



