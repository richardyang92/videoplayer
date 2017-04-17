package com.example.fx_jsj686.videoplayer;

import android.os.Environment;

import java.io.File;

/**
 * Created by fx-jsj686 on 17-4-17.
 */

public class FileUtil {
    public static final String PATH = Environment.getExternalStorageDirectory().getPath();

    public static File[] searchFiles(String specDir) {
        String filePath = PATH + specDir;
        return new File(filePath).listFiles();
    }
}
