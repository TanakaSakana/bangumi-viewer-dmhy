package com.dudy.dmhy;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

public class MainPage extends AppCompatActivity {
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(new FragmentStatePagerAdapter((FragmentManager) getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return new QueryList();
                    case 1:
                        return new BangumiPage();
                    default:
                        return new QueryList();
                }
            }
        });
    }
}
