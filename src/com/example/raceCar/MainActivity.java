package com.example.raceCar;

import java.nio.ByteBuffer;

import com.example.myfirstbluetooth.BluetoothSerialService;
import com.example.myfirstbluetooth.DeviceListActivity;
import com.example.myfirstbluetooth.R;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

	private BluetoothAdapter mBluetoothAdapter = null;
	BluetoothSerialService mBtSS = null;
	public final int HORN_OFF = 0x8600;
	public final int HORN_ON = 0x8601;
	public final String horn = "8601";
	// Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;
    private static Context context;
    private static Handler handler = new Handler();
    private Intent data;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		context = this;
		
		// Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button pairedDevicesBtn = (Button)findViewById(R.id.bluetooth_connect);
		pairedDevicesBtn.setOnClickListener(new Button.OnClickListener() {
		    public void onClick(View v) {
		        data = new Intent(context,DeviceListActivity.class) ;
		        startActivityForResult(data,REQUEST_CONNECT_DEVICE_SECURE);
		    }
		});
		
		mBtSS = new BluetoothSerialService(context, handler);
		
		Button goForthBtn = (Button)findViewById(R.id.go_forth);
		goForthBtn.setOnTouchListener(new View.OnTouchListener() {
			@Override			
		    public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					// Check that we're actually connected before trying anything
					if (mBtSS.getState() != BluetoothSerialService.STATE_CONNECTED) {
						Toast.makeText(context, R.string.not_connected, Toast.LENGTH_SHORT).show();
					}
					else {
						byte[] send = ByteBuffer.allocate(4).putInt(HORN_ON).array();
						mBtSS.write(send);
					}
				}
				if(event.getAction() == MotionEvent.ACTION_UP){
					byte[] send = ByteBuffer.allocate(4).putInt(HORN_OFF).array();
					mBtSS.write(send);
	            }
	            return true;
		    }
		});
	}
	
	@Override
    public void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else {
            if (mBtSS == null) 
            	mBtSS = new BluetoothSerialService(context, handler);
        }
    }
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
	    // See which child activity is calling us back.
	    switch (requestCode) {
	        case REQUEST_CONNECT_DEVICE_SECURE:
	            if (resultCode == Activity.RESULT_OK){
	                connectDevice(data, true);
	            } 
	        case REQUEST_ENABLE_BT:
	            break;
	    }
	}
	
	private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        if(mBluetoothAdapter == null) {
        	return;
        }
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mBtSS.connect(device, secure);
    }

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
