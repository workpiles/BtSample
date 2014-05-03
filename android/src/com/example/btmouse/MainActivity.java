package com.example.btmouse;

import java.util.Set;

import com.example.btmouse.GamePadFragment.GamePadEventListener;
import com.example.btmouse.GamePadFragment.KeyState;
import com.example.btmouse.MouseScreen.MouseScreenEventListener;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

public class MainActivity extends Activity implements MouseScreenEventListener, TabListener, GamePadEventListener {
	private final int REQUEST_ENABLE_BT = 1;
	
	private BluetoothAdapter mBluetoothAdapter;
	private BtClientConnection mConnection;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Bluetoothをサポートしていません", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}
		
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivity(enableIntent);
		} else {
			connectServer();
		}

		final ActionBar ab = this.getActionBar();
		ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		ab.addTab(ab.newTab()
				.setText("Mouse")
				.setTabListener(this));
		ab.addTab(ab.newTab()
				.setText("Keyboard")
				.setTabListener(this));
		ab.addTab(ab.newTab()
				.setText("GamePad")
				.setTabListener(this));
	}
		
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case REQUEST_ENABLE_BT :
				if (resultCode == Activity.RESULT_OK) {
					connectServer();
				} else {
					Toast.makeText(this, "Bluetoothが有効になりませんでした", Toast.LENGTH_SHORT).show();
					finish();
				}
				break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void connectServer() {
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
		BluetoothDevice selected = null;
		
		if (pairedDevices.size() > 0) {
			for (BluetoothDevice device : pairedDevices) {
				Log.d("LOG", "Devices:" + device.getName() + "/" + device.getAddress() + "/" + device.getBondState());
				//TODO:ペアリング済み＆デバイスは１つ以外は対応しない
				selected = device;
			}
			
			mConnection = new BtClientConnection(selected);
			mConnection.start();
			
		} else {
			Toast.makeText(this, "端末がありません", Toast.LENGTH_SHORT).show();
		}		
	}
	
	@Override
	protected void onPause() {
		mConnection.disconnect();
		super.onPause();
	}
	
	@Override
	public void onMove(int x, int y) {
		byte[] data = new byte[5];
		data[0] = 'M';
		data[1] = (byte) (0x000000FF&x);
		data[2] = (byte) (0x000000FF&(x>>>8));
		data[3] = (byte) (0x000000FF&y);
		data[4] = (byte) (0x000000FF&(y>>>8));

		mConnection.send(data);
	}

	@Override
	public void onLeftClick() {
		byte[] data = new byte[]{'L'};
		mConnection.send(data);
	}

	@Override
	public void onRightClick() {
		byte[] data = new byte[]{'R'};
		mConnection.send(data);
		
	}

	@Override
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		if (tab.getPosition() == 0) {
			ft.replace(R.id.main_container, new MouseFragment());
		} else if (tab.getPosition() == 1) {
			ft.replace(R.id.main_container, new KeyboardFragment());
		} else {
			GamePadFragment gpf = new GamePadFragment();
			gpf.setGamePadEventListener(this);
			ft.replace(R.id.main_container, gpf);
		}
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onWheel(int num) {
		byte[] data = new byte[2];
		
		data[0] = 'O';
		data[1] = (byte) (0x000000FF&num);
		mConnection.send(data);
		
	}

	@Override
	public void onLeftDrag() {
		byte[] data = new byte[]{'U'};
		mConnection.send(data);

	}

	@Override
	public void onRightDrag() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDrop() {
		byte[] data = new byte[]{'J'};
		mConnection.send(data);
		
	}

	@Override
	public void updateKeyState(KeyState state) {
		byte[] data = new byte[3];
		
		data[0] = 'G';
		data[1] = state.State[1];
		data[2] = state.State[0];
		mConnection.send(data);
		Log.d("LOG", "updateKeyState:" + state.State[0] + "/" + state.State[1]);
	}

}
