package com.BangumiList;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.BangumiList.GloVar.BangumiData;
import com.BangumiList.bangumi.Bangumi;
import com.BangumiList.bangumi.BangumiAdapter;
import com.BangumiList.bangumi.BangumiList;
import com.dmhyparser.MainParser;
import com.dmhyparser.info.BANGUMI;
import com.dmhyparser.info.BangumiInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class History_Page extends Fragment implements SwipeRefreshLayout.OnRefreshListener, BangumiAdapter.ClickListener {

    List<Bangumi> MainBangumiList = new ArrayList<>();
    SwipeRefreshLayout swipe;
    BangumiAdapter adapter;
    RecyclerView recycler;
    View row;

    private static Bundle mBundleRecyclerViewState;
    private final String KEY_RECYCLER_STATE = "recycler_state";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        row = inflater.inflate(R.layout.activity_bangumi, null);
        return row;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        StaggeredGridLayoutManager gaggeredGridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);

        adapter = new BangumiAdapter(getContext(), MainBangumiList);

        recycler = (RecyclerView) row.findViewById(R.id.recycler_bangumi);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(gaggeredGridLayoutManager);

        swipe = (SwipeRefreshLayout) row.findViewById(R.id.swipe_bangumi);
        swipe.setOnRefreshListener(this);
        adapter.setClickListener(this);
    }

    @Override
    public void itemClicked(View view, String link) {
        Uri uri = Uri.parse(link);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    @Override
    public void onPause() {
        super.onPause();
        // save RecyclerView state
        mBundleRecyclerViewState = new Bundle();
        Parcelable listState = recycler.getLayoutManager().onSaveInstanceState();
        mBundleRecyclerViewState.putParcelable(KEY_RECYCLER_STATE, listState);
    }

    @Override
    public void onResume() {
        super.onResume();
        // restore RecyclerView state
        if (mBundleRecyclerViewState != null) {
            Parcelable listState = mBundleRecyclerViewState.getParcelable(KEY_RECYCLER_STATE);
            recycler.getLayoutManager().onRestoreInstanceState(listState);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        new grabBangumi().execute();
    }

    class grabBangumi extends AsyncTask<Void, Integer, List<Bangumi>> {

        ProgressDialog mDialog;
        int mCount = 0;
        int mSize = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = new ProgressDialog(getContext());
            mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mDialog.setTitle(String.format("Loading Information"));
            mDialog.setIndeterminate(false);
            mDialog.setCancelable(false);
            mDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            mDialog.setProgress(mCount);
            adapter.notifyDataSetChanged();
            super.onProgressUpdate(values);
        }

        @Override
        protected List<Bangumi> doInBackground(Void... params) {
            List<Bangumi> SeasonList = new ArrayList<>();
            BANGUMI.BangumiInfoList = Collections.emptyList();

            try {
                MainParser.update();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("MainParser", "Error");
            }
            while (BANGUMI.getBangumiInfoList().size() <=500 && BANGUMI.BangumiInfoList.isEmpty()) {
                Log.e("List size" , String.valueOf(BANGUMI.getBangumiInfoList().size()));
            }
            List<BangumiInfo> banList = BANGUMI.getBangumiInfoList();
            mSize = banList.size();
            mDialog.setMax(mSize);

            for (BangumiInfo row : banList) {
                publishProgress(++mCount);
                Bangumi item = new Bangumi(row);
                SeasonList.add(item);
            }
            return SeasonList;
        }

        @Override
        protected void onPostExecute(List<Bangumi> bangumis) {
            super.onPostExecute(bangumis);
            Log.e("Postex", "Work");
            if (bangumis != null && !bangumis.isEmpty()) {
                MainBangumiList.clear();
                MainBangumiList.addAll(bangumis);
            }
            swipe.setRefreshing(false);
            mDialog.dismiss();
            adapter.notifyDataSetChanged();
            BangumiData.banListB = new BangumiList(MainBangumiList);
            BangumiData.banListB.renderImage(getContext());
        }
    }
}
