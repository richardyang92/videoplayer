package com.example.fx_jsj686.videoplayer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by fx-jsj686 on 17-4-17.
 */

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ListView mFileListView;
    private List<FileInfo> mListFileInfo;
    {
        mListFileInfo = new ArrayList<>();
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        mFileListView = (ListView) findViewById(R.id.fileList);

        setFileInfo();

        mFileListView.setAdapter(new ListViewAdaptor(mListFileInfo));

        mFileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FileInfo fileInfo = mListFileInfo.get(position);
                Log.i(TAG, "onItemClick: filename is " + fileInfo.getName());
                String moviePath = FileUtil.PATH + "/" + fileInfo.getName();
                if (isMovieFile(fileInfo.getName())) {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString(Consts.VIDEO_NAME, moviePath);
                    intent.putExtras(bundle);
                    intent.setClass(MainActivity.this, VideoActivity.class);
                    startActivity(intent);
                }
                else {
                    Log.e(TAG, "onItemClick: file is not in a movie format");
                }

            }
        });
    }

    private boolean isMovieFile(String filePath) {
        Pattern pattern = Pattern.compile(".*\\.(?i)mp4");
        Matcher matcher = pattern.matcher(filePath);
        return matcher.matches();
    }

    private void setFileInfo() {
        mListFileInfo.clear();

        File[] files = FileUtil.searchFiles("/");

        for (File file : files) {
            FileInfo fileInfo = new FileInfo();
            fileInfo.setName(file.getName());
            mListFileInfo.add(fileInfo);
        }
    }

    public class ListViewAdaptor extends BaseAdapter {
        View[] itemViews;

        public ListViewAdaptor(List<FileInfo> fileInfoList) {
            itemViews = new View[fileInfoList.size()];
            for (int i = 0; i < fileInfoList.size(); i++) {
                FileInfo fileInfo = fileInfoList.get(i);
                itemViews[i] = makeItemView(fileInfo.getName());
            }
        }

        private View makeItemView(String name) {
            LayoutInflater inflater =
                    (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View itemView = inflater.inflate(R.layout.layout_item, null);
            TextView fileName = (TextView) itemView.findViewById(R.id.filename);
            fileName.setText(name);
            return itemView;
        }

        @Override
        public int getCount() {
            return itemViews.length;
        }

        @Override
        public Object getItem(int position) {
            return itemViews[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                return itemViews[position];
            }
            return convertView;
        }
    }
}
