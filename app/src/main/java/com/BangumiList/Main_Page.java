package com.BangumiList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class Main_Page extends AppCompatActivity {
    private long mBackPressed;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }

    public void Redirect(View view) {
        Intent intent = new Intent(this, Pager_Page.class);
        Log.e("Click",(String) view.getTag());
        switch ((String) view.getTag()) {
            case "tab1":
                intent.setAction("Bangumi");
                startActivity(intent);
                break;
            case "tab2":
                intent.setAction("DMHY");
                startActivity(intent);
                break;
            case "tab3":
                intent.setAction("soruly");
                startActivity(intent);
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
