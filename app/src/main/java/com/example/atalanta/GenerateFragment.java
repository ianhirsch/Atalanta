package com.example.atalanta;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class GenerateFragment extends Fragment {
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.generate_fragment, container, false);
        BottomNavigationView bottomNavigationView = view.findViewById(R.id.navBar);
        bottomNavigationView.setSelectedItemId(R.id.navGenerate);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
             @Override
             public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                 switch (item.getItemId()){
                     case R.id.navHealth:
//                         selectedFragment = ItemoneFragment.newInstance();
//                         FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                         transaction.replace(R.id.content,selectedFragment);
//                         transaction.addToBackStack(null);
//                         transaction.commit();
                         return true;

                     case R.id.navGenerate:
//                         selectedFragment = ItemtwoFragment.newInstance();
//                         transaction = getSupportFragmentManager().beginTransaction();
//                         transaction.replace(R.id.content,selectedFragment);
//                         transaction.addToBackStack(null);
//                         transaction.commit();
                         return true;
                     case R.id.navProfile:
                         Intent intent = new Intent(getActivity(), LoginActivity.class);
                         startActivity(intent);
                         return true;
                 }
//                 FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                 transaction.replace(R.id.content, selectedFragment);
//                 transaction.commit();
                 return true;
             }
         });
        return view;
    }
}