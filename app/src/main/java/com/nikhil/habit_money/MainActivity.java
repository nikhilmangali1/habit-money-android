package com.nikhil.habit_money;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);

        navController.addOnDestinationChangedListener((controller, destination, args) -> {
            boolean isAuthScreen = destination.getId() == R.id.login_fragment
                    || destination.getId() == R.id.register_fragment;
            bottomNav.setVisibility(isAuthScreen ? View.GONE : View.VISIBLE);
        });

        NavigationUI.setupWithNavController(bottomNav, navController);
    }
}