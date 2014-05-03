package com.example.btmouse;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MouseFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.mouse_fragment, container, false);
		
		MouseScreen screen = (MouseScreen)v.findViewById(R.id.mouseScreen);
		screen.setMouseScreenEventListener((MainActivity)getActivity());
		
		return v;
	}

	
}
