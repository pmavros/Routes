package org.urbancortex.routes;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static java.lang.System.out;

/**
 * Created by Panos on 21/04/2015.
 */
public class readWriteSettings {

    static File fileDirectory = null;
    static File fileWriteDirectory = null;

    public static boolean folderSettings() {

        if (isExternalStorageWritable()) {
            out.println("external storage is fine");

            String strSDCardPath = System.getenv("SECONDARY_STORAGE");

            if ((strSDCardPath == null) || (strSDCardPath.length() == 0)) {
                strSDCardPath = System.getenv("EXTERNAL_SDCARD_STORAGE");
            }

            //If may get a full path that is not the right one, even if we don't have the SD Card there.
            //We just need the "/mnt/extSdCard/" i.e and check if it's writable
            if(strSDCardPath != null) {
                if (strSDCardPath.contains(":")) {
                    strSDCardPath = strSDCardPath.substring(0, strSDCardPath.indexOf(":"));
                }
                File externalFilePath = new File(strSDCardPath);

                if (externalFilePath.exists() && externalFilePath.canWrite()){
                    //do what you need here
                    System.out.println(externalFilePath);
                }
            }

            // sets the files in the directory
            fileDirectory = new File(Environment.getExternalStorageDirectory()+ "/Routes-io");
            fileWriteDirectory = new File(Environment.getExternalStorageDirectory()+ "/Routes-io/data");
            // check if directory exists
            if (fileDirectory.exists()) {
                // do something here
                out.println("folder routes-io exists in sd storage" +fileDirectory);

            } else {
                fileDirectory.mkdirs();
                System.out.println("Had to make Routes-io folder");
            }

            // check if data folder exists
            if (!fileWriteDirectory.isDirectory()) {
                // do something here
                fileWriteDirectory.mkdirs();
            }

            return true;

        } else {
            out.println("external not writable");
            return false;
        }
    }



    /* Renames the buttons dynamically, based on app settings */
    public static String[] getButtonSettings() {

        String[] events = new String[0];
       if(folderSettings()){
            // lists all the files into an array
            File[] dirFiles = fileDirectory.listFiles();


            if (dirFiles.length != 0) {
                // loops through the array of files, outputing the name to console
                for (int ii = 0; ii < dirFiles.length; ii++) {

                    String fileInput = dirFiles[ii].getName();
//                    System.out.println("1 file is "+fileInput);

                    if (fileInput.toString().equals("fieldworker_events.txt")) {
                        out.println("2 file is "+fileInput);

                        String data = getStringFromFile(dirFiles[ii]);
                        events = getEventNames(data);

                        out.println(events.length);





                    }
                }
            }

        }
        return events;
    }

    protected static String[] getEventNames(String s){
//        out.println(s);
        String[] events = s.split("\n");
        String[] eventNames = new String[events.length];
        for (int i = 0; i < events.length; i++) {
            try {
                eventNames[i] = events[i];
            } catch (NumberFormatException e) {
                e.printStackTrace();
                //TODO
            }
        }
        return eventNames;
    }


    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    private static String getStringFromFile(File filename) {
        out.println(filename);

        //Get the text file
        File file = new File(String.valueOf(filename));
        StringBuilder text = new StringBuilder();

        try {

            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
        }

        out.println(text);
        //Make sure you close all streams.

        return text.toString();
    }


}
