package com.example.btmouse;

import java.util.EventListener;
import java.util.concurrent.TimeUnit;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class GamePadFragment extends Fragment implements Runnable {

	private Button mUp;
	private Button mUpLeft;
	private Button mUpRight;
	private Button mDown;
	private Button mDownLeft;
	private Button mDownRight;
	private Button mLeft;
	private Button mRight;
	private Button mP1;
	private Button mP2;
	private Button mP3;
	private Button mK1;
	private Button mK2;
	private Button mK3;
	private Button mStart;
	private Button mEsc;
	private Button mC1;
	private Button mC2;
	
	private Thread mLooper;
	private GamePadEventListener mListener;
	
	private KeyState mKeyState = new KeyState();
	
	public class KeyState {
		public byte[] State = new byte[2];
		public void setUp(boolean on) { if (on) {State[0]|=0x08;}else{State[0]&=0xF7;};}
		public void setDown(boolean on) { if (on) {State[0]|=0x04;}else{State[0]&=0xFB;};}
		public void setLeft(boolean on) { if (on) {State[0]|=0x02;}else{State[0]&=0xFD;};}
		public void setRight(boolean on) { if (on) {State[0]|=0x01;}else{State[0]&=0xFE;};}
		public void setP1(boolean on) { if (on) {State[1]|=0x40;}else{State[1]&=0xBF;};}
		public void setP2(boolean on) { if (on) {State[1]|=0x20;}else{State[1]&=0xDF;};}
		public void setP3(boolean on) { if (on) {State[1]|=0x10;}else{State[1]&=0xEF;};}
		public void setK1(boolean on) { if (on) {State[1]|=0x04;}else{State[1]&=0xFB;};}
		public void setK2(boolean on) { if (on) {State[1]|=0x02;}else{State[1]&=0xFD;};}
		public void setK3(boolean on) { if (on) {State[1]|=0x01;}else{State[1]&=0xFE;};}
		public void setC1(boolean on) { if (on) {State[1]|=0x80;}else{State[1]&=0x7F;};}
		public void setC2(boolean on) { if (on) {State[1]|=0x08;}else{State[1]&=0xF7;};}
		public void setStart(boolean on) { if (on) {State[0]|=0x20;}else{State[0]&=0xDF;};}
		public void setEsc(boolean on) { if (on) {State[0]|=0x10;}else{State[0]&=0xEF;};}
	}
	
	public interface GamePadEventListener extends EventListener {
		public void updateKeyState(KeyState state);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.gamepad_fragment, container, false);
		
		mUp = (Button)v.findViewById(R.id.button_up);
		mUpLeft = (Button)v.findViewById(R.id.button_left_up);
		mUpRight = (Button)v.findViewById(R.id.button_right_up);
		mDown = (Button)v.findViewById(R.id.button_down);
		mDownLeft = (Button)v.findViewById(R.id.button_left_down);
		mDownRight = (Button)v.findViewById(R.id.button_right_down);
		mLeft = (Button)v.findViewById(R.id.button_left);
		mRight = (Button)v.findViewById(R.id.button_right);
		mP1 = (Button)v.findViewById(R.id.button_p1);
		mP2 = (Button)v.findViewById(R.id.button_p2);
		mP3 = (Button)v.findViewById(R.id.button_p3);
		mK1 = (Button)v.findViewById(R.id.button_k1);
		mK2 = (Button)v.findViewById(R.id.button_k2);
		mK3 = (Button)v.findViewById(R.id.button_k3);
		mC1 = (Button)v.findViewById(R.id.button_c1);
		mC2 = (Button)v.findViewById(R.id.button_c2);
		mStart = (Button)v.findViewById(R.id.button_start);
		mEsc = (Button)v.findViewById(R.id.button_esc);

		start();
		return v;
	}

	
	@Override
	public void onDestroyView() {
		stop();
		super.onDestroyView();
	}


	@Override
	public void run() {
		final long frequency = (long)(Math.floor((double)TimeUnit.SECONDS.toNanos(1L)/30.0f)); //60fpsŽüŠú

		long last = System.nanoTime();
		while (mLooper != null) {
			long current = System.nanoTime();
			long elapsedTime = current - last;

			if (elapsedTime > frequency) {
				last = current;
				sendKeyState();
			} else {
				long interval = frequency - elapsedTime;
				try {
					TimeUnit.NANOSECONDS.sleep(interval);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void sendKeyState() {
		mKeyState.setUp(mUp.isPressed());
		mKeyState.setDown(mDown.isPressed());
		mKeyState.setLeft(mLeft.isPressed());
		mKeyState.setRight(mRight.isPressed());
		if (mUpLeft.isPressed()) {
			mKeyState.setUp(true);
			mKeyState.setLeft(true);
		}
		if (mUpRight.isPressed()) {
			mKeyState.setUp(true);
			mKeyState.setRight(true);
		}
		if (mDownLeft.isPressed()) {
			mKeyState.setDown(true);
			mKeyState.setLeft(true);
		}
		if (mDownRight.isPressed()) {
			mKeyState.setDown(true);
			mKeyState.setRight(true);
		}
		mKeyState.setP1(mP1.isPressed());
		mKeyState.setP2(mP2.isPressed());
		mKeyState.setP3(mP3.isPressed());
		mKeyState.setK1(mK1.isPressed());
		mKeyState.setK2(mK2.isPressed());
		mKeyState.setK3(mK3.isPressed());
		
		mKeyState.setC1(mC1.isPressed());
		mKeyState.setC2(mC2.isPressed());
		
		mKeyState.setStart(mStart.isPressed());
		mKeyState.setEsc(mEsc.isPressed());
		
		if (mListener != null) mListener.updateKeyState(mKeyState);
	}
	
	public void setGamePadEventListener(GamePadEventListener listener) {
		mListener = listener;
	}
	
	public void start() {
		if (mLooper == null) {
			mLooper = new Thread(this);
			mLooper.start();
		}
	}
	
	public void stop() {
		mLooper = null;
	}
}
