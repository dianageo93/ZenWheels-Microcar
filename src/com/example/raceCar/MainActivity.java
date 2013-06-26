package com.example.raceCar;

import com.example.myfirstbluetooth.DeviceListActivity;
import com.example.myfirstbluetooth.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button pairedDevicesBtn = (Button)findViewById(R.id.bluetooth_connect);
		final Context context = this;
		pairedDevicesBtn.setOnClickListener(new Button.OnClickListener() {
		    public void onClick(View v) {
		        Intent intent = new Intent(context,DeviceListActivity.class) ;
		        startActivity(intent);
		    }
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
