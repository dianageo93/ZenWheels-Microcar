package com.example.myfirstbluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import com.example.raceCar.MainActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class BluetoothSerialService {
	// Debugging
    private static final String TAG = "BluetoothSERIALService";
    private static final boolean D = true;
    
    // Unique UUID for this application
    private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    
    // Constants that indicate the current connection state
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_CONNECTION_FAILED = 2;
    public static final int MESSAGE_DEVICE_DISCONNECTED = 3;
    public static final int MESSAGE_DEVICE_CONNECTED = 4;
    public static final int MESSAGE_END = 4;
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device
    
    protected final Handler mHandler;
    private int mState;
    private final BluetoothAdapter mAdapter;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
	
    //initialize a new Bluetooth session
	public BluetoothSerialService(Context context, Handler handler) {
		mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        mHandler = handler;
	}
	
	private synchronized void setState(int state) {
        if (D) Log.d(TAG, "setState() " + mState + " -> " + state);
        mState = state;

        // Give the new state to the Handler so the UI Activity can update
        mHandler.obtainMessage(MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }
	
	public int getState() {
		return mState;
	}
	
	 private void connectionFailed() {
	    setState(MESSAGE_STATE_CHANGE);
	    this.mHandler.sendEmptyMessage(MESSAGE_CONNECTION_FAILED);
	  }
	 
	 private void connectionLost() {
	    setState(MESSAGE_STATE_CHANGE);
	    this.mHandler.sendEmptyMessage(MESSAGE_DEVICE_DISCONNECTED);
	  }
	 
	 public synchronized void start() {
	        if (D) Log.d(TAG, "start");

//	        // Cancel any thread attempting to make a connection
//	        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
//
//	        // Cancel any thread currently running a connection
//	        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

	        setState(STATE_LISTEN);
	    }
	 
	 /**
	  * Stop all threads
	  */
	 
	 public synchronized void stop() {
	        if (D) Log.d(TAG, "stop");

	        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}

	        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}
	        
	        setState(STATE_NONE);
	    }
	 
	 /**
	     * Write to the ConnectedThread in an unsynchronized manner
	     * @param out The bytes to write
	     * @see ConnectedThread#write(byte[])
	     */
	    public void write(byte[] out) {
	        // Create temporary object
	        ConnectedThread r;
	        // Synchronize a copy of the ConnectedThread
	        synchronized (this) {
	            if (mState != STATE_CONNECTED) return;
	            r = mConnectedThread;
	        }
	        // Perform the write unsynchronized
	        r.write(out);
	    }
	 
	 /**
	     * Start the ConnectThread to initiate a connection to a remote device.
	     * @param device  The BluetoothDevice to connect
	     * @param secure Socket Security type - Secure (true) , Insecure (false)
	     */
	    public synchronized void connect(BluetoothDevice device, boolean secure) {
	        if (D) Log.d(TAG, "connect to: " + device);

	        // Cancel any thread attempting to make a connection
	        if (mState == STATE_CONNECTING) {
	            if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
	        }

	        // Cancel any thread currently running a connection
	        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

	        // Start the thread to connect with the given device
	        mConnectThread = new ConnectThread(device);
	        mConnectThread.run();
	        setState(STATE_CONNECTED);
	        return;
	    }
	    
	    /**
	     * Start the ConnectedThread to begin managing a Bluetooth connection
	     * @param socket  The BluetoothSocket on which the connection was made
	     * @param device  The BluetoothDevice that has been connected
	     */
	    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
	        if (D) Log.d(TAG, "connected");

	        // Cancel the thread that completed the connection
	        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}

	        // Cancel any thread currently running a connection
	        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

	        // Send the name of the connected device back to the UI Activity
	        this.mConnectedThread = new ConnectedThread(socket);
	        //this.mConnectedThread.start();
	        Message localMessage = this.mHandler.obtainMessage(MESSAGE_DEVICE_CONNECTED);
	        localMessage.obj = socket;
	        this.mHandler.sendMessage(localMessage);
	        setState(STATE_CONNECTED);
	    }
	    
	    /**
	     * This thread runs while attempting to make an outgoing connection
	     * with a device. It runs straight through; the connection either
	     * succeeds or fails.
	     */

	    public class ConnectThread extends Thread {
	    	
	    	private BluetoothSocket mSocket;
	        private final BluetoothDevice mDevice;

	    	public ConnectThread(BluetoothDevice device) {
	    		// TODO Auto-generated constructor stub
	    		this.mDevice = device;
	    		BluetoothSocket tmp = null;
	            // Get a BluetoothSocket for a connection with the
	            // given BluetoothDevice
	            try {
	                
	                    tmp = device.createRfcommSocketToServiceRecord(SPP_UUID);
	                
	            } catch (IOException e) {
	                Log.e(TAG, "create() failed", e);
	            }
	            mSocket = tmp;
	    	}

	    	public void run() {
	            Log.i(TAG, "BEGIN mConnectThread SocketType:");
	            setName("ConnectThread");

	            // Make a connection to the BluetoothSocket
	            try {
	                // This is a blocking call and will only return on a
	                // successful connection or an exception
	                mSocket.connect();
	            } catch (IOException e) {
	                // Close the socket
	                try {
	                    mSocket.close();
	                } catch (IOException e2) {
	                    Log.e(TAG, "unable to close() "+" socket during connection failure", e2);
	                }
	                connectionFailed();
	                return;
	            }

	            // Reset the ConnectThread because we're done
	            synchronized (BluetoothSerialService.this) {
	                mConnectThread = null;
	            }

	            // Start the connected thread
	            connected(mSocket, mDevice);
	        }

	        public void cancel() {
	            try {
	                mSocket.close();
	            } catch (IOException e) {
	                Log.e(TAG, "close() of connect " + " socket failed", e);
	            }
	        }
	    }

	    /**
	     * This thread runs during a connection with a remote device.
	     * It handles all incoming and outgoing transmissions.
	     */
	    
	    private class ConnectedThread extends Thread {
	        private final BluetoothSocket mmSocket;
	        private final InputStream mmInStream;
	        private final OutputStream mmOutStream;

	        public ConnectedThread(BluetoothSocket socket) {
	            Log.d(TAG, "create ConnectedThread");
	            mmSocket = socket;
	            InputStream tmpIn = null;
	            OutputStream tmpOut = null;

	            // Get the BluetoothSocket input and output streams
	            try {
	                tmpIn = socket.getInputStream();
	                tmpOut = socket.getOutputStream();
	            } catch (IOException e) {
	                Log.e(TAG, "temp sockets not created", e);
	            }

	            mmInStream = tmpIn;
	            mmOutStream = tmpOut;
	        }

	        public void run() {
	            Log.i(TAG, "BEGIN mConnectedThread");
	            byte[] buffer = new byte[1024];
	            int bytes;

	            // Keep listening to the InputStream while connected
	            while (true) {
	                try {
	                    // Read from the InputStream
	                    bytes = mmInStream.read(buffer);

	                    // Send the obtained bytes to the UI Activity
	                    BluetoothSerialService.this.processBytes(buffer,bytes);
	                } catch (IOException e) {
	                    Log.e(TAG, "disconnected", e);
	                    connectionLost();
	                    break;
	                }
	            }
	        }

	        /**
	         * Write to the connected OutStream.
	         * @param buffer  The bytes to write
	         */
	        public void write(byte[] buffer) {
	            try {
	                mmOutStream.write(buffer);
	               // mHandler.obtainMessage(MainActivity.MESSAGE_WRITE, -1, -1, buffer).sendToTarget();
	                return;
	            } catch (IOException e) {
	                Log.e(TAG, "Exception during write", e);
	            }
	        }

	        public void cancel() {
	            try {
	                mmSocket.close();
	                return ;
	            } catch (IOException e) {
	                Log.e(TAG, "close() of connect socket failed", e);
	            }
	        }
	    }
	    
	    protected void processBytes(byte[] paramArrayOfByte, int paramInt) {
		}
}
