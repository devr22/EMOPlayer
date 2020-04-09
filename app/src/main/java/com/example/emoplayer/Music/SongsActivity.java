package com.example.emoplayer.Music;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.emoplayer.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SongsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs);

        callHomeFragment();

        BottomNavigationView bottomNavigationView = findViewById(R.id.songs_bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(selectedListener);

    }

    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    switch (menuItem.getItemId()) {

                        case R.id.nav_home:
                            callHomeFragment();
                            return true;

                        case R.id.nav_favourites:
                            callFavouritesFragment();
                            return true;

                        case R.id.nav_profile:
                            callProfileFragment();
                            return true;
                    }

                    return false;
                }
            };

    private void callHomeFragment() {

        HomeFragment homeFragment = new HomeFragment();
        FragmentTransaction home_fragmentTransaction = getSupportFragmentManager().beginTransaction();
        home_fragmentTransaction.replace(R.id.songs_fragmentContainer, homeFragment, "");
        home_fragmentTransaction.commit();
    }

    private void callFavouritesFragment() {

        FavouritesFragment favouritesFragment = new FavouritesFragment();
        FragmentTransaction favourites_fragmentTransaction = getSupportFragmentManager().beginTransaction();
        favourites_fragmentTransaction.replace(R.id.songs_fragmentContainer, favouritesFragment, "");
        favourites_fragmentTransaction.commit();
    }

    private void callProfileFragment() {

        ProfileFragment profileFragment = new ProfileFragment();
        FragmentTransaction profile_fragmentTransaction = getSupportFragmentManager().beginTransaction();
        profile_fragmentTransaction.replace(R.id.songs_fragmentContainer, profileFragment, "");
        profile_fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
