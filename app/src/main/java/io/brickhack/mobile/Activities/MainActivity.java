package io.brickhack.mobile.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import io.brickhack.mobile.Fragments.AboutFragment;
import io.brickhack.mobile.Fragments.MapFragments;
import io.brickhack.mobile.Fragments.ScheduleFragments;
import io.brickhack.mobile.LiveInfoActivity;
import io.brickhack.mobile.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    private Fragment fragment = null;


    @Override
    protected void onStart() {
        super.onStart();
        loadFragment(new ScheduleFragments());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupBottomNav();


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setupBottomNav(){
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.btn_schedule:
                    fragment = new ScheduleFragments();
                    Toast.makeText(MainActivity.this, "Schedule", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.btn_stuff:
                    fragment = new AboutFragment();
                    Toast.makeText(MainActivity.this, "Stuff", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.btn_map:
                    fragment = new MapFragments();
                    Toast.makeText(MainActivity.this, "map", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.btn_about:
                    fragment = new AboutFragment();
                    Toast.makeText(MainActivity.this, "About", Toast.LENGTH_SHORT).show();
                    break;
            }
            return loadFragment(fragment);
        });
    }

    private boolean loadFragment(Fragment fragment){
        if (fragment != null){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_framelayout, fragment)
                    .commit();

            return true;
        }

        return false;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()){
            case R.id.nav_home:
                Toast.makeText(this, "Nav home", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_gallery:
                Toast.makeText(this, "Gallery", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_slideshow:
                Toast.makeText(this, "Slideshow", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_tools:
                Toast.makeText(this, "Tools", Toast.LENGTH_SHORT).show();
                break;
            case R.id.info_center:
                startActivity(new Intent(this, LiveInfoActivity.class));
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
