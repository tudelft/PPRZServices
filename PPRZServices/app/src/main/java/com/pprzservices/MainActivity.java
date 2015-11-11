package com.pprzservices;

import com.pprzservices.service.MavLinkService;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class MainActivity extends Activity {

	Handler mHandler = null;
    Thread mThread = null;
	private static final String TAG = MainActivity.class.getSimpleName();
	
	Intent intent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		this.mHandler = new Handler();
		intent = new Intent(this, MavLinkService.class);
        startService(intent);

		Log.d(TAG, "The service is started");
		Button btnEnter = (Button) findViewById(R.id.bebopStartButton);
		btnEnter.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				bebopStart();
			}
		});

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
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override 
    public void onDestroy() {
		super.onDestroy();
		stopService(intent);
    }


	public void bebopStart() {
        if(mThread != null)
        {
            mThread.stop();
            mThread = null;
        }

        mThread = new Thread(new TelnetClient());
        mThread.start();
	}

    public void showToast(final String toast)
    {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(MainActivity.this, toast, Toast.LENGTH_SHORT).show();
            }
        });
    }


    public class TelnetClient implements Runnable
    {
        Handler mHandler = null;
        Socket mSocket = null;

        public void send(String strCommand)
        {
            try
            {
                OutputStream streamOutput = mSocket.getOutputStream();

                try
                {
                    byte[] arrayOutput = strCommand.getBytes("ASCII");
                    int nLen = arrayOutput.length;
                    streamOutput.write(arrayOutput, 0, nLen);
                }
                catch (Exception e0)
                {}
            }
            catch (IOException e1)
            {
                e1.printStackTrace();
            }
        }
/*
        public void read()
        {
            try
            {
                InputStream streamInput = mSocket.getInputStream();

                byte[] arrayOfByte = new byte[10000];
                int j = 0;
                try
                {
                    int i = arrayOfByte.length;
                    j = streamInput.read(arrayOfByte, 0, i);
                    if (j == -1)
                    {
                        throw new Exception("Error while reading socket.");
                    }
                }
                catch (Exception e0)
                {
                    try
                    {
                        mSocket.close();
                    }
                    catch (IOException e1)
                    {
                        e1.printStackTrace();
                    }
                    mSocket = null;
                }
            }
            catch (IOException e1)
            {
                e1.printStackTrace();
            }
        }
*/


        public void run()
        {
            try
            {
                Socket socket = new Socket("192.168.42.1", 23);
                mSocket = socket;

                SystemClock.sleep(100);
                showToast("Connected");

                this.send("root\n");

                SystemClock.sleep(200);
                this.send("\n");

                SystemClock.sleep(500);
                this.send("killall -9 dragon-prog.elf\n");
                this.send("killall -9 program.elf\n");

                SystemClock.sleep(1000);
                this.send("cd /data/ftp/ap/\n");

                SystemClock.sleep(500);
                this.send("./ap.ef > /dev/null 2>&1 &\n");

                socket.close();
                mSocket = null;
            }
            catch (Exception e0)
            {
                String strException = e0.getMessage();

                if(strException == null)
                    strException = "Connection closed";
                else
                    strException = "Cannot connect to the server:\r\n" + strException;

                showToast(strException);
      }
        }
    }


}