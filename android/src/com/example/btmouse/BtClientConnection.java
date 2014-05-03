package com.example.btmouse;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class BtClientConnection extends Thread {
	private final BluetoothSocket mSocket;
	private final BluetoothDevice mDevice;
	
	private final OutputStream mOutput;
	
	private enum State{CONNECT, CONNECTED, DISCONNECT};
	private State mState;
	
	public BtClientConnection(BluetoothDevice device) {
		mDevice = device;
		BluetoothSocket socket = null;
		OutputStream out = null;
		
		try {
			socket = mDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
			out = socket.getOutputStream();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		mSocket = socket;
		mOutput = out;
		
		mState = State.CONNECT;
	}
	
	public void run() {
		Log.d("LOG", "Client start");
		if (mSocket == null) return;
		
		while (true) {
			switch (mState) {
			case CONNECT:
				try {
					mSocket.connect();
				} catch (IOException e) {
					e.printStackTrace();
					mState = State.DISCONNECT;
					return;
				}
				mState = State.CONNECTED;
				Log.d("LOG", "Connected!");
				break;
			case CONNECTED:
				
				break;
			case DISCONNECT:
				if (mSocket != null) {
					try {
						mOutput.write('E');
						TimeUnit.MILLISECONDS.sleep(100);
						mSocket.close();
						Log.d("LOG", "Disconnected");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				return;
			}
		}
	}
	
	public void send(byte sendData[]) {
		if (!mState.equals(State.CONNECTED)) return;
		try {
			mOutput.write(sendData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void disconnect() {
		mState  = State.DISCONNECT;
	}

}
