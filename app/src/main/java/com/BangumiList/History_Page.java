package com.BangumiList;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.BangumiList.Util.BaseAsyncBangumi;
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

    private static Bundle mBundleRecyclerViewState;
    private final String KEY_RECYCLER_STATE = "recycler_state";
    List<Bangumi> MainBangumiList = new ArrayList<>();
    SwipeRefreshLayout swipe;
    BangumiAdapter adapter;
    RecyclerView recycler;
    View row;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        row = inflater.inflate(R.layout.activity_bangumi, null);
        return row;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        StaggeredGridLayoutManager gaggeredGridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);

        swipe = (SwipeRefreshLayout) row.findViewById(R.id.swipe_bangumi);
        adapter = new BangumiAdapter(getContext(), MainBangumiList);

        recycler = (RecyclerView) row.findViewById(R.id.recycler_bangumi);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(gaggeredGridLayoutManager);

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
        new grabBangumi(getContext(), adapter, swipe).execute();
    }

    public class grabBangumi extends BaseAsyncBangumi {

        public grabBangumi(Context context, BangumiAdapter adapter, SwipeRefreshLayout swipe) {
            super(context, adapter, swipe);
        }

        @Override
        protected List<Bangumi> doInBackground(Void... params) {
            List<Bangumi> SeasonList = new ArrayList<>();
            BANGUMI.BangumiInfoList = Collections.emptyList();

            publishProgress(0, 2);
            try{
                MainParser.update();
            } catch (Exception e) {
            }
            List<BangumiInfo> banList = BANGUMI.getBangumiInfoList();
            mSize = banList.size();
            mDialog.setMax(mSize);
            for (BangumiInfo row : banList) {
                publishProgress(++mCount, 3);
                Bangumi item = new Bangumi(row);
                SeasonList.add(item);
            }
            return SeasonList;
        }

        @Override
        protected void onPostExecute(List<Bangumi> bangumis) {
            if (bangumis != null && !bangumis.isEmpty()) {
                MainBangumiList.clear();
                MainBangumiList.addAll(bangumis);
            }
            BangumiData.banListB = new BangumiList(MainBangumiList);
            BangumiData.banListB.ImageArchiving(getContext(), mDialog);
            super.onPostExecute(bangumis);
        }
    }
}
