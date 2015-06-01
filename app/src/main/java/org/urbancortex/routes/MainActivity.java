package org.urbancortex.routes;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.io.File;

import static android.os.SystemClock.elapsedRealtime;

import static org.urbancortex.routes.Routes.*;


public class MainActivity extends Activity {

    public final static String EXTRA_MESSAGE = "org.urbancortex.routes.MESSAGE";
    private File fileWriteDirectory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.out.println("main.oncreate");

        new locations(this, locations.ProviderType.GPS).start();

        Button b = (Button)findViewById(R.id.cont);
        Button s = (Button)findViewById(R.id.stop);

        if(isRecording){

            b.setVisibility(View.VISIBLE);
            s.setVisibility(View.VISIBLE);
        } else if (!isRecording){
            b.setVisibility(View.INVISIBLE);
            s.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void onResume(){
        super.onResume();

        System.out.println("main.onresume "+ isRecording);


        Button b = (Button)findViewById(R.id.cont);
        Button s = (Button)findViewById(R.id.stop);

        if(isRecording){

            b.setVisibility(View.VISIBLE);
            s.setVisibility(View.VISIBLE);
        } else if (!isRecording){
            b.setVisibility(View.INVISIBLE);
            s.setVisibility(View.INVISIBLE);
        }
    }

    public void onStart(){
        super.onStart();

        System.out.println("main.onstart");

        Button b = (Button)findViewById(R.id.cont);
        Button s = (Button)findViewById(R.id.stop);

        if(isRecording){

            b.setVisibility(View.VISIBLE);
            s.setVisibility(View.VISIBLE);
        } else if (!isRecording){
            b.setVisibility(View.INVISIBLE);
            s.setVisibility(View.INVISIBLE);
        }
    }

    /** Called when the user clicks the Start new button */
    public void newExperiment(View view) {

        // Do something in response to button
        EditText editText = (EditText) findViewById(R.id.edit_message);
        final String participantID = editText.getText().toString();


        if(readWriteSettings.folderSettings() && checkPassword()){

            // prepare logger settings
            final Intent loggerIntent = new Intent(this, csv_logger.class);
            loggerIntent.putExtra("fileDir", readWriteSettings.fileWriteDirectory.toString());
            loggerIntent.putExtra("participantID", participantID);

            if(!participantID.isEmpty()){
                System.out.println(participantID);

                // start GPS logging
                if(isRecording){

                    // check if the want to start new recording
                    final Intent intent = new Intent(this, MapTestingActivity.class);
                    intent.putExtra(EXTRA_MESSAGE, participantID);

                    new AlertDialog.Builder(this)
                            .setTitle("New Recording")
                            .setMessage("Are you sure you want to start a new recording session?")
                            .setIcon(0)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    // if yes, start the logger
                                    startNewRecording(loggerIntent);

                                    // start the second activity (screen)
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })
                            .show();


                } else if (!isRecording) {

                    // if it is not recording already .
                    // just start a new recording
                    startNewRecording(loggerIntent);

                    // start second activity
                    Intent intent = new Intent(this, MapTestingActivity.class);
                    intent.putExtra(EXTRA_MESSAGE, participantID);
                    startActivity(intent);

                }

            } else {
                Toast.makeText(this, "Hey, did you forget to enter your participant ID?", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Oops, I couldn't find the folders I need in the system!", Toast.LENGTH_LONG).show();
        }
    }

    /** Called when the user clicks the Continue button */
    public void continueExperiment(View view) {

        // Do something in response to button
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String participantID = editText.getText().toString();

        if(readWriteSettings.folderSettings() ){

            if(checkPassword()){
                if(!participantID.isEmpty()){

                    // start second activity
                    Intent intent = new Intent(this, MapTestingActivity.class);
                    intent.putExtra(EXTRA_MESSAGE, participantID);

                    startActivity(intent);

                } else {
                    Toast.makeText(this, "Hey, did you forget to enter your participant ID?", Toast.LENGTH_LONG).show();
                }
            }

        } else {
            Toast.makeText(this, "Oops, I couldn't find the folders I need in the system!", Toast.LENGTH_LONG).show();
        }
    }



    /** Called when the user clicks the STOP button */
    public void stopExperiment(View view) {


        // get ready to stop
        final Intent stopCSVIntent = new Intent(this, csv_logger.class);
        final Intent stopMapIntent = new Intent(this, MapTestingActivity.class);
        stopMapIntent.putExtra("keep", false);

        // Do something in response to button
        if (isRecording && checkPassword()) {

            // check if the want to stop  recording
            new AlertDialog.Builder(this)
                    .setTitle("Stop Recording")
                    .setMessage("Are you sure you want to stop the current recording session?")
                    .setIcon(0)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            // if yes, stop the logger
                            isRecording = false;
                            stopService(stopCSVIntent);
                            stopService(stopMapIntent);

                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .show();
        }
    }



    public boolean checkPassword(){

        // get the present password
        EditText passwordText = (EditText) findViewById(R.id.edit_password);

        if(passwordText!=null){
            // get the present password
            String password = passwordText.getText().toString();
            System.out.println(password);


            if (password.equals("4321")) {
                // clean the password field
                passwordText.setText("");
                return true;
            } else {
                return false;
            }

        } else {
            Toast.makeText(this, "Enter research password first.", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public void startNewRecording(Intent intent){
        isRecording = true;
        eventsCounter = 0;
        startMillis = elapsedRealtime();
        startTime = System.currentTimeMillis();
        startService(intent);

        System.out.println("starting logger service");
    }
}