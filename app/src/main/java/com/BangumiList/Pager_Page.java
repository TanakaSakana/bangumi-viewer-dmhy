package com.BangumiList;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.BangumiList.bangumi.BangumiFragment;

public class Pager_Page extends AppCompatActivity {
    private ViewPager mViewPager;
    private long mBackPressed;
private static final String  TAG= "Pager Activity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(new FragmentStatePagerAdapter((FragmentManager) getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return new Glimpse_Page();
                    case 1:
                        return new BangumiFragment();
                    case 2 :
                        return new  History_Page();
                    default:
                        return null;
                }
            }
        });
        /*new AlertDialog.Builder(this)
                .setTitle("預覽版本通知")
                .setMessage("此程式只完成30%，為預覽版本\n功能完善仍然進行中...")
                .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();*/
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
