package com.example.emoplayer.Music;

import android.graphics.Paint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.emoplayer.R;

public class HomeFragment extends Fragment {

    public HomeFragment() {
    }

    TextView emotionTv;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_home, container, false);

        emotionTv = view.findViewById(R.id.home_emotionTv);

        return view;
    }
}
