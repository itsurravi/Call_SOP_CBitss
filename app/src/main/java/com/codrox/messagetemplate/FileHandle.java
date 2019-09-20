package com.codrox.messagetemplate;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import com.codrox.messagetemplate.Modals.FileInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FileHandle {

    Context c;

    public FileHandle(Context c) {
        this.c = c;
    }

    public static String getPath(final Context context, final Uri uri) {

        // ExternalStorageProvider
        if (isExternalStorageDocument(uri)) {
            final String docId = DocumentsContract.getDocumentId(uri);
            final String[] split = docId.split(":");
            final String type = split[0];

            if ("primary".equalsIgnoreCase(type)) {
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            }

            // TODO handle non-primary volumes
        }
        // DownloadsProvider
        else if (isDownloadsDocument(uri)) {

            final String id = DocumentsContract.getDocumentId(uri);
            final Uri contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

            return getDataColumn(context, contentUri, null, null);
        }
        // MediaProvider
        else if (isMediaDocument(uri)) {
            final String docId = DocumentsContract.getDocumentId(uri);
            final String[] split = docId.split(":");
            final String type = split[0];

            Uri contentUri = null;
            if ("image".equals(type)) {
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            } else if ("video".equals(type)) {
                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            } else if ("audio".equals(type)) {
                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            }

            final String selection = "_id=?";
            final String[] selectionArgs = new String[]{
                    split[1]
            };

            return getDataColumn(context, contentUri, selection, selectionArgs);
        }
        return null;
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private List<File> getListFiles(File parentDir) {
        ArrayList<File> inFiles = new ArrayList<File>();
        File[] files = parentDir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                inFiles.addAll(getListFiles(file));
            } else {
                if (file.getName().endsWith(".mp3")) {
                    inFiles.add(file);
                }
            }
        }
        return inFiles;
    }

    /*
    public List<FileInfo> getFiles(File parentDir) {
        Log.d("Test", String.valueOf(parentDir));
        List<File> files = getListFiles(parentDir);
        Log.d("Test", String.valueOf(files.size()));
        List<FileInfo> filesinfo = new ArrayList<>();

        for(File file : files)
        {
            String path = file.getAbsolutePath();
//            Log.d("Test", path);
            Cursor returnCursor = c.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Audio.Media.DATA + "='"+path+"'", null, null);
//            Cursor returnCursor = c.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Audio.Media.DATA + "=? ", new String[]{path}, null);
            Log.d("Test2", String.valueOf(MediaStore.Audio.Media.DATA + "='"+path+"'"));
            if (returnCursor != null && returnCursor.getCount() > 0) {
                returnCursor.moveToFirst();

                String date = returnCursor.getString(returnCursor.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED));
                String title = returnCursor.getString(returnCursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String filepath = returnCursor.getString(returnCursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                FileInfo fi = new FileInfo(title, date, filepath);

                filesinfo.add(fi);

                Log.d("Test2", filepath);
            }
        }

        Log.d("Test", ""+filesinfo.size());

        return filesinfo;
    }*/

    public File getFiles(File parentDir) {
        Log.d("Test", String.valueOf(parentDir));
        List<File> files = getListFiles(parentDir);

        Collections.sort(files, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                String a = String.valueOf(o1.lastModified());
                String b = String.valueOf(o2.lastModified());
                return b.compareTo(a);
            }
        });

        if(files.size()>0)
        return files.get(0);

        else
            return null;
    }

    /*
    * Log.d("Test", String.valueOf(files.size()));
        List<FileInfo> filesinfo = new ArrayList<>();

        for(File file : files)
        {
            String path = file.getAbsolutePath();
            Cursor returnCursor = c.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Audio.Media.DATA + "='"+path+"'", null, null);
            if (returnCursor != null && returnCursor.getCount() > 0) {
                returnCursor.moveToFirst();

                String date = returnCursor.getString(returnCursor.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED));
                String title = returnCursor.getString(returnCursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String filepath = returnCursor.getString(returnCursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                FileInfo fi = new FileInfo(title, date, filepath);

                filesinfo.add(fi);
            }
            returnCursor.close();
        }

        Log.d("Test", ""+filesinfo.size());

        return filesinfo;
    * */
}
