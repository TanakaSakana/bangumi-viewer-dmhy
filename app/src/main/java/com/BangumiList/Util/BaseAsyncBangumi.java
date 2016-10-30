package com.BangumiList.Util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;

import com.BangumiList.GloVar.BangumiData;
import com.BangumiList.bangumi.Bangumi;
import com.BangumiList.bangumi.BangumiAdapter;

import java.util.List;

public abstract class BaseAsyncBangumi extends AsyncTask<Void, Integer, List<Bangumi>> {

    public ProgressDialog mDialog;
    public int mSize = 0;
    public int mCount = 0;


    private Context mContext;
    private BangumiAdapter mAdapter;
    private SwipeRefreshLayout mSwipe;
    /*private AsyncCallback mAsyncCallback;
    private BaseResponse mResult;
    private BaseAsyncTask mInstanceRef;*/

    public BaseAsyncBangumi(Context context, BangumiAdapter adapter, SwipeRefreshLayout swipe) {
        if (!(context instanceof Activity) && !(context instanceof Service))
            throw new IllegalArgumentException("The AsyncTask context must be able to cast into Activity or Service");
        mContext = context;
        mAdapter = adapter;
        mSwipe = swipe;
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mDialog = new ProgressDialog(mContext);
        mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mDialog.setTitle("Initializing");
        mDialog.setIndeterminate(false);
        mDialog.setCancelable(false);
        mDialog.show();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        String MSG;
        switch (values[1]) {
            case 2:
                MSG = BangumiData.PROGRESS_CONNECTIING_INTERNET;
                break;
            case 3:
                MSG = BangumiData.PROGRESS_LOADING;
                break;
            default:
                MSG = "";
        }
        mDialog.setProgress(values[0]);
        mDialog.setTitle(MSG);
    }

    @Override
    protected void onPostExecute(List<Bangumi> bangumis) {
        super.onPostExecute(bangumis);
        mSwipe.setRefreshing(false);
        mDialog.dismiss();
        mAdapter.notifyDataSetChanged();
    }
}