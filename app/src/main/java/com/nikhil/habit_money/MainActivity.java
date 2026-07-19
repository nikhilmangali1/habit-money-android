package com.nikhil.habit_money;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.nikhil.habit_money.core.util.TokenManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        NavGraph navGraph = navController.getNavInflater().inflate(R.navigation.nav_graph);

        TokenManager tokenManager = new TokenManager(this);
        boolean shouldAutoLogin = tokenManager.isLoggedIn() && tokenManager.isSameBuild();
        if (!shouldAutoLogin) {
            tokenManager.clearAll();
        }
        navGraph.setStartDestination(shouldAutoLogin
                ? R.id.dashboard_fragment
                : R.id.login_fragment);
        navController.setGraph(navGraph);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);

        navController.addOnDestinationChangedListener((controller, destination, args) -> {
            boolean isAuthScreen = destination.getId() == R.id.login_fragment
                    || destination.getId() == R.id.register_fragment;
            bottomNav.setVisibility(isAuthScreen ? View.GONE : View.VISIBLE);
        });

        NavigationUI.setupWithNavController(bottomNav, navController);
    }
}