package com.racky.ifilemanager;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.FileUtils;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


public class internal_storage extends Fragment {

    ListView listview;
    TextView path_name;
    ImageView backBtn;
    LinearLayout toolBar;
    private ImageView refreshBtn;

    private boolean isFileManagerInitialized;
    private File dir;
    private File[] files;
    private int filesFountCount;
    private String currentPath;
    private List<String> filesList;

    private boolean[] selection;
    private boolean isLongClick;

    private int selectItemIndex;

    private String copyPath;



    public internal_storage() {
        // Required empty public constructor
    }

    public class MyAdapter extends BaseAdapter {

        private List<String> data = new ArrayList<>();

        private boolean[] selection;

        public void setData(List<String> data){
            if(data!=null){
                this.data.clear();
                if(data.size()>0){
                    this.data.addAll(data);
                }
                notifyDataSetChanged();
            }
        }

        void setSelection(boolean[] selection){
            this.selection = new boolean[selection.length];
            if(selection!=null){
                for(int i=0;i<selection.length;i++){
                    this.selection[i] = selection[i];
                }
                notifyDataSetChanged();
            }
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public String getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }



        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView==null){
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items, parent, false);
                convertView.setTag(new ViewHolder((TextView) convertView.findViewById(R.id.textItem)));
            }
            ImageView icon = (ImageView) convertView.findViewById(R.id.file_image);
            ViewHolder holder = (ViewHolder) convertView.getTag();
            final String item = getItem(position);
            holder.info.setText(item);

            if(selection!=null ){
                if( selection[position]){
                    holder.info.setBackgroundColor(Color.LTGRAY);

                }
                else{
                    holder.info.setBackgroundColor(Color.WHITE);
                }

            }
            return convertView;
        }

        class ViewHolder{
            TextView info;

            ViewHolder(TextView info){
                this.info = info;
            }
        }
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_internal_storage, container, false);



        if(!isFileManagerInitialized) {
            String rootPath = String.valueOf(Environment.getExternalStorageDirectory());
            currentPath = rootPath;



            toolBar = v.findViewById(R.id.toolBar);

            TextView pathOutput = v.findViewById(R.id.path_name);


            listview = (ListView) v.findViewById(R.id.listView);

            MyAdapter adapter = new MyAdapter();
            listview.setAdapter(adapter);

            filesList = new ArrayList<>();

             refreshBtn = v.findViewById(R.id.refreshBtn);

            refreshBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    

                    pathOutput.setText(currentPath);

                    dir = new File(currentPath);

                    files = dir.listFiles();
                    filesFountCount = files.length;
                    selection = new boolean[filesFountCount];
                    adapter.setSelection(selection);

                    filesList.clear();
                    for (int i = 0; i < filesFountCount; i++) {
                        filesList.add(String.valueOf(files[i].getName()));
                    }

                    adapter.setData(filesList);

                adapter.notifyDataSetChanged();

                }
            });

            refreshBtn.callOnClick();


            ImageView backBtn = v.findViewById(R.id.backBtn);


            backBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(currentPath.equals(rootPath)){
                        return;
                    }
                    currentPath = currentPath.substring(0, currentPath.lastIndexOf('/'));


                    refreshBtn.callOnClick();
                }
            });

            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(!isLongClick){



                                if(files[position].isDirectory()) {
                                    currentPath = files[position].getAbsolutePath();

                                    refreshBtn.callOnClick();
                                }
                                else{



                                    Intent target = new Intent(Intent.ACTION_VIEW);
                                    String fname = files[position].getName();
                                    int lastIndexOf = fname.lastIndexOf(".");
                                    String ftype = fname.substring(lastIndexOf+1);
                                    if(ftype.contains("pdf") || ftype.contains("txt") || ftype.contains("docs")) {
                                        final Intent intent1 = target.setDataAndType(Uri.fromFile(files[position]), "application/"+ftype);
                                    }
                                    else if(ftype.contains("jpg") || ftype.contains("png") || ftype.contains("jpeg") || ftype.contains("gif") ){
                                        final Intent intent1 = target.setDataAndType(Uri.fromFile(files[position]), "image/"+ftype);
                                    }
                                    else if(ftype.contains("mp4") ){
                                        final Intent intent1 = target.setDataAndType(Uri.fromFile(files[position]), "video/*");
                                    }
									else if(ftype.contains("mp3") ){
                                        final Intent intent1 = target.setDataAndType(Uri.fromFile(files[position]), "audio/*");
                                    }
                                    else{
                                        final Intent intent1 = target.setDataAndType(Uri.fromFile(files[position]), "*/*");
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
                        }
                    },50);

                }
            });


            selection = new boolean[files.length];

            listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    isLongClick = true;
                    selection[position]=!selection[position];
                    adapter.setSelection(selection);
                    int selectionCount = 0;
                    for (boolean aSelection : selection){
                        if(aSelection){
                            selectionCount++;
                        }
                    }
                    if(selectionCount>0) {
                        if (selectionCount == 1) {
                            selectItemIndex = position;
                            v.findViewById(R.id.rename).setVisibility(View.VISIBLE);
                            if(!files[selectItemIndex].isDirectory()){
                                v.findViewById(R.id.copyBtn).setVisibility(View.VISIBLE);
                            }
                        } else {
                            v.findViewById(R.id.copyBtn).setVisibility(View.GONE);
                            v.findViewById(R.id.rename).setVisibility(View.GONE);

                        }

                        v.findViewById(R.id.toolBar).setVisibility(View.VISIBLE);
                    }
                    else{
                        v.findViewById(R.id.toolBar).setVisibility(View.GONE);
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isLongClick = false;
                        }
                    },1000);
                    return false;
                }
            });


            ImageView renameBtn = v.findViewById(R.id.rename);
            renameBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final AlertDialog.Builder renameDialog =
                            new AlertDialog.Builder(getActivity());

                    renameDialog.setTitle("Rename :");
                    final EditText input = new EditText(getActivity());
                    final String renamePath = files[selectItemIndex].getAbsolutePath();
                    input.setText(renamePath.substring(renamePath.lastIndexOf('/')));
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    renameDialog.setView(input);
                    renameDialog.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String s = new File(renamePath).getParent() + "/" + input.getText();
                            File newFile = new File(s);
                            new File(renamePath).renameTo(newFile);
                            refreshBtn.callOnClick();
                            selection = new boolean[files.length];
                            adapter.setSelection(selection);
                        }
                    });
                    renameDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            refreshBtn.callOnClick();
                        }
                    });
                    renameDialog.show();
                }
            });

            ImageView addFolder = v.findViewById(R.id.addFolder);
            addFolder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder newFolderAlert = new AlertDialog.Builder(getActivity());
                    newFolderAlert.setTitle("Add New Folder");
                    final EditText input = new EditText(getActivity());
                    input.setInputType((InputType.TYPE_CLASS_TEXT));
                    newFolderAlert.setView(input);
                    newFolderAlert.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final File newFolder = new File(currentPath+"/"+input.getText());
                            if(!newFolder.exists()){
                                newFolder.mkdir();
                                refreshBtn.callOnClick();
                            }

                        }
                    });
                    newFolderAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    newFolderAlert.show();
                }
            });

            ImageView deleteBtn = v.findViewById(R.id.deleteBtn);

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder deleteAlert = new AlertDialog.Builder(getActivity());
                    deleteAlert.setTitle("Delete");
                    deleteAlert.setMessage("Are you sure?");
                    deleteAlert.setPositiveButton("YES?", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            for (int i = 0; i < files.length; i++) {
                                if (selection[i]) {
                                    deleteFileOrFolder(files[i]);
                                    selection[i] = false;
                                }

                            }
                            refreshBtn.callOnClick();

                        }
                    });
                    deleteAlert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            refreshBtn.callOnClick();
                        }
                    });

                    deleteAlert.show();
                }
            });

            ImageView copyBtn = v.findViewById(R.id.copyBtn);
            ImageView pasteBtn = v.findViewById(R.id.pasteBtn);

            copyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    copyPath = files[selectItemIndex].getAbsolutePath();

                    selection = new boolean[files.length];
                    adapter.setSelection(selection);
                   pasteBtn.setVisibility(View.VISIBLE);
                }
            });


            pasteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pasteBtn.setVisibility(View.GONE);
                    String destPath = currentPath + copyPath.substring(copyPath.lastIndexOf('/'));
                    Log.d("testing", destPath);

                    copy(new File(copyPath), new File(destPath));
                    refreshBtn.callOnClick();
                }
            });

            isFileManagerInitialized = true;
        }
        else{
            refreshBtn.callOnClick();
        }



        return v;
    }



    private void copy(File src, File dst){
        try  {
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dst);
            byte[] buf = new byte[1024];
            int len;
            while((len= in.read(buf))>0){
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        }
        catch (FileNotFoundException e){
            e.printStackTrace();;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void deleteFileOrFolder(File fileOrFolder){
        if(fileOrFolder.isDirectory()){
            if(fileOrFolder.list().length==0){
                fileOrFolder.delete();
            } else{
                String files[] = fileOrFolder.list();
                for(String temp:files){
                    File fileToDelete = new File(fileOrFolder, temp);
                    deleteFileOrFolder(fileToDelete);

                }
            }
//            if(fileOrFolder.list().length==0){
//                fileOrFolder.delete();
//            }

        }
        else{
            fileOrFolder.delete();
        }
    }






}


