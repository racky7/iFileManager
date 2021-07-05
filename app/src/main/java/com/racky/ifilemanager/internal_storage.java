package com.racky.ifilemanager;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class internal_storage extends Fragment {

    ListView listview;
    TextView path_name;
    ImageView backBtn;
    Button refreshBtn;

    private boolean isFileManagerInitialized = false;
    private File dir;
    private File[] files;
    private int filesFountCount;
    private String currentPath;

    public internal_storage() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_internal_storage, container, false);

        if(!isFileManagerInitialized) {
            String rootPath = String.valueOf(Environment.getExternalStorageDirectory());
            currentPath = rootPath;
            dir = new File(currentPath);

            files = dir.listFiles();
            filesFountCount = files.length;

            TextView pathOutput = v.findViewById(R.id.path_name);
            pathOutput.setText(currentPath);

            listview = (ListView) v.findViewById(R.id.listView);


            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getBaseContext(), android.R.layout.simple_spinner_dropdown_item);
            listview.setAdapter(adapter);

            List<String> filesList = new ArrayList<>();

            for (int i = 0; i < filesFountCount; i++) {
                filesList.add(String.valueOf(files[i].getName()));
            }

            adapter.addAll(filesList);
//        adapter.notifyDataSetChanged();

            Button refreshBtn = v.findViewById(R.id.refreshBtn);

            refreshBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    files = dir.listFiles();
                    filesFountCount = files.length;
                    filesList.clear();
                    for (int i = 0; i < filesFountCount; i++) {
                        filesList.add(String.valueOf(files[i].getName()));
                    }

                    adapter.clear();
                    adapter.addAll(filesList);
//                adapter.notifyDataSetChanged();

                }
            });

            ImageView backBtn = v.findViewById(R.id.backBtn);

            backBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(currentPath.equals("/storage/emulated/0")){
                        return;
                    }
                    currentPath = currentPath.substring(0, currentPath.lastIndexOf('/'));
                    dir = new File(currentPath);
                    pathOutput.setText(currentPath);
                    refreshBtn.callOnClick();
                }
            });

            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    currentPath = files[position].getAbsolutePath();
                    dir = new File(currentPath);
                    if(dir.isDirectory()) {
                        pathOutput.setText(currentPath);
                        refreshBtn.callOnClick();
                    }
                    else{

                        Intent target = new Intent(Intent.ACTION_VIEW);
                        String fname = dir.getName();
                        int lastIndexOf = fname.lastIndexOf(".");
                        String ftype = fname.substring(lastIndexOf+1);
                        if(ftype.contains("pdf") || ftype.contains("txt") || ftype.contains("docs")) {
                            final Intent intent1 = target.setDataAndType(Uri.fromFile(dir), "application/"+ftype);
                        }
                        else if(ftype.contains("jpg") || ftype.contains("png") || ftype.contains("jpeg") || ftype.contains("gif") ){
                            final Intent intent1 = target.setDataAndType(Uri.fromFile(dir), "image/"+ftype);
                        }
                        else if(ftype.contains("mp4") ){
                            final Intent intent1 = target.setDataAndType(Uri.fromFile(dir), "video/*");
                        }
                        else{
                            final Intent intent1 = target.setDataAndType(Uri.fromFile(dir), "*/*");
                        }
                        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);




                        Intent intent = Intent.createChooser(target, "Open File");
                        try {
                            startActivity(intent);
                        } catch (ActivityNotFoundException e) {
//                            Toast.makeText(internal_storage.this, "there is no supported app for this file", Toast.LENGTH_SHORT).show();

                        }
                    }
                }
            });


            isFileManagerInitialized = true;
        }



        return v;
    }
}