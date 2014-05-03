package com.example.btmouse;

import java.util.EventListener;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;

public class MouseScreen extends View implements OnGestureListener, OnScaleGestureListener  {
	private final int MAX_DISP_WIDTH = 65535;
	private final int MAX_DISP_HEIGHT = 65535;
	
	private GestureDetector mGesDetector;
	private ScaleGestureDetector mSGesDetector;
	private MouseScreenEventListener mListener;
	
	private boolean isDrag;
	
	public interface MouseScreenEventListener extends EventListener {
		public void onMove(int x, int y);
		public void onLeftClick();
		public void onRightClick();
		public void onWheel(int num);
		public void onLeftDrag();
		public void onRightDrag();
		public void onDrop();
	}
	
	public MouseScreen(Context context, AttributeSet attrs) {
		super(context, attrs);
		mGesDetector = new GestureDetector(context, this);
		mSGesDetector = new ScaleGestureDetector(context, this);
	}
	
	public void setMouseScreenEventListener(MouseScreenEventListener listener) {
		mListener = listener;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.d("LOG", "onTouch");
		mSGesDetector.onTouchEvent(event);
		
		if (event.getPointerCount() == 1) {
			mGesDetector.onTouchEvent(event);
		}
		return true;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return true;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		Log.d("LOG", "velo:" + velocityY);
		int wheel = 0;
		float velocity = Math.abs(velocityY);
		if (velocity < 100) {
			wheel = 1;
		} else if (velocity < 1000){
			wheel = 3;
		} else {
			wheel = 5;
		}
		if (mListener != null) {
			int m = velocityY < 0 ? 1:-1;
			mListener.onWheel(m*wheel);
		}
		return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		Log.d("LOG", "onLongPress");
		if (mListener != null) {
			isDrag = true;
			mListener.onLeftDrag();
		}
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		//Windows‚ÆÀ•W‚ª‹t‚É‚È‚é‚©‚ç*-1
		moveMouse(-1*distanceX, -1*distanceY);
		return true;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		Log.d("LOG", "onShowPress");
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		Log.d("LOG", "onSingleTapUp");
		if (mListener != null) {
			if (isDrag) {
				isDrag = false;
				mListener.onDrop();				
			} else {
				mListener.onLeftClick();
			}
		}
		return true;
	}

	public void moveMouse(float x, float y) {
		int dstX = (int)(MAX_DISP_WIDTH * (x/this.getWidth()));
		int dstY = (int)(MAX_DISP_HEIGHT* (y/this.getHeight()));
		Log.d("LOG", "send:" + dstX + "/" + dstY);

		//‚Õ‚é‚Õ‚é–hŽ~
		if (Math.abs(dstX) < 15) dstX = 0;
		if (Math.abs(dstY) < 15) dstY = 0;
		
		if (mListener != null) mListener.onMove(dstX, dstY);
	}

	@Override
	public boolean onScale(ScaleGestureDetector arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onScaleBegin(ScaleGestureDetector arg0) {
		if (mListener != null) mListener.onRightClick();
		return true;
	}

	@Override
	public void onScaleEnd(ScaleGestureDetector arg0) {
		// TODO Auto-generated method stub
		
	}

}
