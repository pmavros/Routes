package org.urbancortex.routes;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.commons.net.ntp.TimeInfo;

import static android.os.SystemClock.elapsedRealtime;


public class TimeOffsetActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeoffset);


        // must give permission in manifest to access internet

        //        For use in ad-hoc, use static IP address.
        //        InetAddress.getByName("192.168.1.232");


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        System.out.println(elapsedRealtime());

    }

    public void getTimeOffset (View view){

        EditText input = (EditText) findViewById(R.id.edit_message);
        String newIp = input.getText().toString();

        TextView offsetText = (TextView) findViewById(R.id.offset);
        TextView offsetAverageText = (TextView) findViewById(R.id.offsetAverage);

        int[] a = new int[100];
        int sum = 0;
        TimeInfo info = null;


        for (int i =0; i < a.length; i++) {

            offsetAverageText.setText("Running test "+i);


            if (newIp != null) {
                info = NTPClient.main(new String[]{newIp}); //"time-a.nist.gov"
            }

            if (info != null) {
                info.computeDetails(); // compute offset/delay if not already done
                Long offsetValue = info.getOffset();
                Long delayValue = info.getDelay();
                String delay = (delayValue == null) ? "N/A" : delayValue.toString();
                String offset = (offsetValue == null) ? "N/A" : offsetValue.toString();

                offsetText.setText(offset);
                a[i]  =   Integer.parseInt( offset );

            }

            System.out.println(i);
        }

        for (int i =0; i < a.length; i++) {
            sum += a[i];
        }
        int average = sum / a.length;
        Routes.timeOffset = average;
        System.out.println("average is "+average);
        offsetAverageText.setText("Offset average is "+average);



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
}
