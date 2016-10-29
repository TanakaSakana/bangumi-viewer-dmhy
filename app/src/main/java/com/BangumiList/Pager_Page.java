package com.BangumiList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.BangumiList.bangumi.BangumiFragment;

public class Pager_Page extends AppCompatActivity {
    private ViewPager mViewPager;
    private long mBackPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(new FragmentStatePagerAdapter((FragmentManager) getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return new DMHYQueryList();
                    case 1:
                        return new BangumiFragment();
                    case 2:
                        return new History_Page();
                    default:
                        return null;
                }
            }
        });
        FragmentRedirect();
    }

    public void FragmentRedirect() {
        switch (getIntent().getAction()) {
            case "Bangumi":
                mViewPager.setCurrentItem(0);
                break;
            case "DMHY":
                mViewPager.setCurrentItem(1);
                break;
            case "History":
                mViewPager.setCurrentItem(2);
                break;
        }
    }
    @Override
    public void onBackPressed() {
        if (mBackPressed + 2000 > System.currentTimeMillis()) {
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
            return;
        } else {
            Toast.makeText(getBaseContext(), "再按退出", Toast.LENGTH_SHORT).show();
        }
        mBackPressed = System.currentTimeMillis();
    }
}
