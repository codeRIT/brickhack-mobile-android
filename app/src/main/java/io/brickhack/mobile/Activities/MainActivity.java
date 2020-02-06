package io.brickhack.mobile.Activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import io.brickhack.mobile.Adapters.ViewPagerAdapter;
import io.brickhack.mobile.Fragments.HomePage;
import io.brickhack.mobile.Fragments.ProfileFragment;
import io.brickhack.mobile.R;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPagerAdapter adapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tablayout);
        viewPager = findViewById(R.id.view_pager);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());

//        adapter.addFragments(new FavoriteFragment(), "Favorites");
        adapter.addFragments(new HomePage(), "");
        adapter.addFragments(new ProfileFragment(), "");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        //Icons
//        tabLayout.getTabAt(0).setIcon(R.drawable.favorite);
        tabLayout.getTabAt(0).setIcon(R.drawable.home);
        tabLayout.getTabAt(1).setIcon(R.drawable.profile);
    }
}
