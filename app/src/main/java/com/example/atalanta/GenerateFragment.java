package com.example.atalanta;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class GenerateFragment extends Fragment {
    View view;
    Button startButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.generate_fragment, container, false);
        BottomNavigationView v = view.findViewById(R.id.navBar);
        v.setSelectedItemId(R.id.navGenerate);
// get the reference of Button
//        startButton = (Button) view.findViewById(R.id.start_button);
// perform setOnClickListener on first Button
//        startButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//// display a message by using a Toast
//                Toast.makeText(getActivity(), "First Fragment", Toast.LENGTH_LONG).show();
//            }
//        });
        return view;
    }
}