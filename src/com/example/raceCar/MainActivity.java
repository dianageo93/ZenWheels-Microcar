package com.example.raceCar;

import java.nio.ByteBuffer;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.myfirstbluetooth.BluetoothSerialService;
import com.example.myfirstbluetooth.DeviceListActivity;
import com.example.myfirstbluetooth.R;

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
    private int speed = 0;
    private int lightsCount = 0;
    private int blinkLeftFlag = 0;
    private int blinkRightFlag = 0;
    private int faultFlag = 0;
	
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
            	if (mBtSS.getState() != BluetoothSerialService.STATE_CONNECTED) {
					Toast.makeText(context, R.string.not_connected, Toast.LENGTH_SHORT).show();
				}
            	else {
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
            }
        });
		
		speed = codes.SPEED.length/2;
		Button steerFrButton = (Button)findViewById(R.id.steer_front);
		steerFrButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	if (mBtSS.getState() != BluetoothSerialService.STATE_CONNECTED) {
					Toast.makeText(context, R.string.not_connected, Toast.LENGTH_SHORT).show();
				}
            	else {
            		byte[] send = ByteBuffer.allocate(4).putInt(codes.SPEED[speed]).array();
					mBtSS.write(send);
					if(speed < codes.SPEED.length - 9) {
						speed += 8;
					}
            	}
            }
        });
		
		Button steerBackButton = (Button)findViewById(R.id.steer_back);
		steerBackButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	if (mBtSS.getState() != BluetoothSerialService.STATE_CONNECTED) {
					Toast.makeText(context, R.string.not_connected, Toast.LENGTH_SHORT).show();
				}
            	else {
            		byte[] send = ByteBuffer.allocate(4).putInt(codes.SPEED[speed]).array();
					mBtSS.write(send);
					if(speed > 8) {
						speed -= 8;
					}
            	}
            }
        });
		
		steer = codes.SPEED.length/2;
		Button steerLeftButton = (Button)findViewById(R.id.steer_left);
		steerLeftButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	if (mBtSS.getState() != BluetoothSerialService.STATE_CONNECTED) {
					Toast.makeText(context, R.string.not_connected, Toast.LENGTH_SHORT).show();
				}
            	else {
            		byte[] send = ByteBuffer.allocate(4).putInt(codes.STEER[steer]).array();
					mBtSS.write(send);
					if(steer < codes.STEER.length - 9) {
						steer += 8;
					}
            	}
            }
        });
		
		Button steerRightButton = (Button)findViewById(R.id.steer_right);
		steerRightButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	if (mBtSS.getState() != BluetoothSerialService.STATE_CONNECTED) {
					Toast.makeText(context, R.string.not_connected, Toast.LENGTH_SHORT).show();
				}
            	else {
            		byte[] send = ByteBuffer.allocate(4).putInt(codes.STEER[steer]).array();
					mBtSS.write(send);
					if(steer > 8) {
						steer -= 8;
					}
            	}
            }
        });
		
		Button onoffBtn = (Button)findViewById(R.id.onoff);
		onoffBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	if (mBtSS.getState() != BluetoothSerialService.STATE_CONNECTED) {
					Toast.makeText(context, R.string.not_connected, Toast.LENGTH_SHORT).show();
				}
            	else {
            		byte[] send = ByteBuffer.allocate(4).putInt(codes.OFF).array();
					mBtSS.write(send);
            	}
            }
        });
		
		Runnable blinkRunnable = new Runnable() {
			@Override
			public void run() {
				byte[] send = null;
				while(true) {
					if(blinkLeftFlag == 1) {
						//back
						send = ByteBuffer.allocate(4).putInt(codes.BLINK_LEFT[0]).array();
						//front
						mBtSS.write(send);
						send = ByteBuffer.allocate(4).putInt(codes.BLINK_LEFT[1]).array();
						mBtSS.write(send);
						try {
								Thread.sleep(400);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						send = ByteBuffer.allocate(4).putInt(codes.BLINK_LEFT_OFF).array();
						mBtSS.write(send);
						try {
							Thread.sleep(400);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					else if(blinkRightFlag == 1) {
						//back
						send = ByteBuffer.allocate(4).putInt(codes.BLINK_RIGHT[0]).array();
						//front
						mBtSS.write(send);
						send = ByteBuffer.allocate(4).putInt(codes.BLINK_RIGHT[1]).array();
						mBtSS.write(send);
						try {
								Thread.sleep(400);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						send = ByteBuffer.allocate(4).putInt(codes.BLINK_RIGHT_OFF).array();
						mBtSS.write(send);
						try {
							Thread.sleep(400);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					else if(faultFlag == 1) {
						for(int signal : codes.FAULT) {
							send = ByteBuffer.allocate(4).putInt(signal).array();
							mBtSS.write(send);
						}
						try {
								Thread.sleep(400);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						for(int signal : codes.FAULT_OFF) {
							send = ByteBuffer.allocate(4).putInt(signal).array();
							mBtSS.write(send);
						}
						try {
							Thread.sleep(400);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					else {
						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
				}
			}
		};
		
		final Thread blinkRunnableThread = new Thread(blinkRunnable);
		blinkRunnableThread.start();
		
		Button blinkLeft = (Button)findViewById(R.id.blink_left);
		blinkLeft.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	if (mBtSS.getState() != BluetoothSerialService.STATE_CONNECTED) {
					Toast.makeText(context, R.string.not_connected, Toast.LENGTH_SHORT).show();
				}
            	else {
            		if(blinkLeftFlag == 0) {
            			blinkLeftFlag = 1;
            		}
            		else if(blinkLeftFlag == 1) {
            			blinkLeftFlag = 0;
            		}
            	}
            }
        });
		
		Button blinkRight = (Button)findViewById(R.id.blink_right);
		blinkRight.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	if (mBtSS.getState() != BluetoothSerialService.STATE_CONNECTED) {
					Toast.makeText(context, R.string.not_connected, Toast.LENGTH_SHORT).show();
				}
            	else {
            		if(blinkRightFlag == 0) {
            			blinkRightFlag = 1;
            		}
            		else if(blinkRightFlag == 1) {
            			blinkRightFlag = 0;
            		}
            	}
            }
        });
		
		Button fault = (Button)findViewById(R.id.fault);
		fault.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	if (mBtSS.getState() != BluetoothSerialService.STATE_CONNECTED) {
					Toast.makeText(context, R.string.not_connected, Toast.LENGTH_SHORT).show();
				}
            	else {
            		if(faultFlag == 0) {
            			faultFlag = 1;
            		}
            		else if(faultFlag == 1) {
            			faultFlag = 0;
            		}
            	}
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
