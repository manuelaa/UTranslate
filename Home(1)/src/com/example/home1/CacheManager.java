package com.example.home1;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import android.content.Context;
import android.os.Environment;

//brine se oko cache memorije
//sve funkcije bi se trebale odvijati u background threadu
public class CacheManager {
	private static final long MAX_SIZE = 5242880L; //5MB

	public static String EXTERNAL_STORAGE_PATH = null; 
	
	//stvara folder za external cache (prave fileove)
	//pozvati na pocetku aplikacije
	public static void createExternalDataDir() {
		File storageDir = Environment.getExternalStoragePublicDirectory("/UTranslate/");
		storageDir.mkdir();
		EXTERNAL_STORAGE_PATH = storageDir.getAbsolutePath() + "/";		
	}
	
	//sprema podatke u cache
    public static void cacheData(Context context, byte[] data, String name) throws IOException {            	
    	File cacheDir = context.getCacheDir();
        long size = getDirSize(cacheDir);
        long newSize = data.length + size;
        
        //ocisti cache
        if (newSize > MAX_SIZE)
            cleanDir(cacheDir, newSize - MAX_SIZE);

        File file = new File(cacheDir, name);
        FileOutputStream fos = new FileOutputStream(file);
        
        try {
            fos.write(data);
        }
        finally {
            fos.flush();
            fos.close();
        }
    }
    
    //vraca podatke iz cachea
    public static byte[] getData(Context context, String name) throws IOException {    	
    	File cacheDir = context.getCacheDir();
        File file = new File(cacheDir, name);

        if (!file.exists()) return null;        

        byte[] data = new byte[(int)file.length()];
        FileInputStream fis = new FileInputStream(file);
        try {
            fis.read(data);
        }
        finally {
            fis.close();
        }

        return data;
    }

    //cisti cache direktorij
    private static void cleanDir(File dir, long bytes) {
        long bytesDeleted = 0;
        File[] files = dir.listFiles();

        for (File file : files) {
            bytesDeleted += file.length();
            file.delete();

            if (bytesDeleted >= bytes) break;
        }
    }

    //velicina direktorija
    private static long getDirSize(File dir) {
        long size = 0;
        File[] files = dir.listFiles();

        for (File file : files) {
            if (file.isFile()) {
                size += file.length();
            }
        }

        return size;
    }
}
