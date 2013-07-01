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
	private final raceCarCodes codes = new raceCarCodes();
	// Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;
    private static Context context;
    private static Handler handler = new Handler();
    private Intent data;
    private int steer = 0;
    private int steer_right = 0;
    private int speed = 0;
    private int speed_front = 0;
    private int lightsCount = 0;
	
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
		
		Button horn = (Button)findViewById(R.id.horn);
		horn.setOnTouchListener(new View.OnTouchListener() {
			@Override			
		    public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					// Check that we're actually connected before trying anything
					if (mBtSS.getState() != BluetoothSerialService.STATE_CONNECTED) {
						Toast.makeText(context, R.string.not_connected, Toast.LENGTH_SHORT).show();
					}
					else {
						byte[] send = ByteBuffer.allocate(4).putInt(codes.HORN_ON).array();
						mBtSS.write(send);
					}
				}
				if(event.getAction() == MotionEvent.ACTION_UP){
					byte[] send = ByteBuffer.allocate(4).putInt(codes.HORN_OFF).array();
					mBtSS.write(send);
	            }
	            return true;
		    }
		});
		
		Button lights = (Button)findViewById(R.id.lights);
		lights.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	byte[] send = ByteBuffer.allocate(4).putInt(codes.LIGHTS_OFF).array();
            	if(lightsCount == 0) {
            		send = ByteBuffer.allocate(4).putInt(codes.LIGHTS_SOFT).array();
            		lightsCount++;
            	}
            	else if(lightsCount == 1) {
            		send = ByteBuffer.allocate(4).putInt(codes.LIGHTS).array();
            		lightsCount++;
            	}
            	else {
            		send = ByteBuffer.allocate(4).putInt(codes.LIGHTS_OFF).array();
            		lightsCount = 0;
            	}
				mBtSS.write(send);
            }
        });
		
//		Button steerLeftPlus = (Button)findViewById(R.id.steer_left_plus);
//		steerLeftPlus.setOnTouchListener(new View.OnTouchListener() {
//			@Override			
//		    public boolean onTouch(View v, MotionEvent event) {
//				if(event.getAction() == MotionEvent.ACTION_DOWN) {
//					byte[] send = ByteBuffer.allocate(4).putInt(codes.STEER_LEFT[steer]).array();
//					mBtSS.write(send);
//					if(steer < codes.STEER_LEFT.length - 9) {
//						steer += 8;
//					}
//				}
//				return true;
//			}
//		});
//		
//		Button steerLeftMinus = (Button)findViewById(R.id.steer_left_minus);
//		steerLeftMinus.setOnTouchListener(new View.OnTouchListener() {
//			@Override			
//		    public boolean onTouch(View v, MotionEvent event) {
//				if(event.getAction() == MotionEvent.ACTION_DOWN) {
//					byte[] send = ByteBuffer.allocate(4).putInt(codes.STEER_LEFT[steer]).array();
//					mBtSS.write(send);
//					if(steer > 8) {
//						steer -= 8;
//					}
//				}
//				return true;
//			}
//		});
//		
//		Button steerRightPlus = (Button)findViewById(R.id.steer_right_plus);
//		steerRightPlus.setOnTouchListener(new View.OnTouchListener() {
//			@Override			
//		    public boolean onTouch(View v, MotionEvent event) {
//				if(event.getAction() == MotionEvent.ACTION_DOWN) {
//					byte[] send = ByteBuffer.allocate(4).putInt(codes.STEER_RIGHT[steer_right]).array();
//					mBtSS.write(send);
//					if(steer_right < codes.STEER_RIGHT.length - 9) {
//						steer_right += 8;
//					}
//				}
//				return true;
//			}
//		});
//		
//		Button steerRightMinus = (Button)findViewById(R.id.steer_right_minus);
//		steerRightMinus.setOnTouchListener(new View.OnTouchListener() {
//			@Override			
//		    public boolean onTouch(View v, MotionEvent event) {
//				if(event.getAction() == MotionEvent.ACTION_DOWN) {
//					byte[] send = ByteBuffer.allocate(4).putInt(codes.STEER_RIGHT[steer_right]).array();
//					mBtSS.write(send);
//					if(steer_right > 8) {
//						steer_right -= 8;
//					}
//				}
//				return true;
//			}
//		});
//		
//		Button noSteer = (Button)findViewById(R.id.no_steer);
//		noSteer.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//            	byte[] send = ByteBuffer.allocate(4).putInt(codes.STEER_LEFT[0]).array();
//            	mBtSS.write(send);
//            	steer = 0;
//            	steer_right = 0;
//            }
//        });
//		
//		Button speedPlus = (Button)findViewById(R.id.speed_plus);
//		speedPlus.setOnTouchListener(new View.OnTouchListener() {
//			@Override			
//		    public boolean onTouch(View v, MotionEvent event) {
//				if(event.getAction() == MotionEvent.ACTION_DOWN) {
//					byte[] send = ByteBuffer.allocate(4).putInt(codes.SPEED_BACK[speed]).array();
//					mBtSS.write(send);
//					if(speed < codes.SPEED_BACK.length - 9) {
//						speed += 8;
//					}
//				}
//				return true;
//			}
//		});
//		
//		Button speedFront = (Button)findViewById(R.id.speed_front);
//		speedFront.setOnTouchListener(new View.OnTouchListener() {
//			@Override			
//		    public boolean onTouch(View v, MotionEvent event) {
//				if(event.getAction() == MotionEvent.ACTION_DOWN) {
//					byte[] send = ByteBuffer.allocate(4).putInt(codes.SPEED_FRONT[speed_front]).array();
//					mBtSS.write(send);
//					if(speed_front < codes.SPEED_FRONT.length - 9) {
//						speed_front += 8;
//					}
//				}
//				return true;
//			}
//		});
		
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
