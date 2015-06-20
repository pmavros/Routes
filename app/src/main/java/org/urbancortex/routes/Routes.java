package org.urbancortex.routes;

import android.app.Application;

/**
 * Created by Panos on 29/05/2015.
 */
public class Routes extends Application {
    // setup global variables

    public static boolean isRecording = false;
    public static int eventsCounter = 0;
    public static long startMillis = 0;
    public static long startTime = System.currentTimeMillis();
    public static boolean isRunning = false;
    public static boolean isWalking = false;
    public static boolean isMapDisplayed = false;
    public static String currentRoute = "none";
    public static long timeOffset = 0;

}