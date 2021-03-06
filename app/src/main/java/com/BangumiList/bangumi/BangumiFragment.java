package com.BangumiList.bangumi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.BangumiList.GloVar.BangumiData;
import com.BangumiList.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BangumiFragment extends Fragment implements OnRefreshListener, BangumiAdapter.ClickListener {

    public static BangumiList bitmapList;
    List<Bangumi> WeeklyList = new ArrayList<>();
    SwipeRefreshLayout swipe;
    BangumiAdapter adapter;
    RecyclerView recycler;
    View row;

    private static Bundle mBundleRecyclerViewState;
    private final String KEY_RECYCLER_STATE = "recycler_state";

    static boolean LOADED = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        row = inflater.inflate(R.layout.activity_bangumi, null);
        if (!LOADED) new grabBangumi().execute();
        return row;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        StaggeredGridLayoutManager gaggeredGridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);

        adapter = new BangumiAdapter(getContext(), WeeklyList);

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
            List<Bangumi> WeeklyList = new ArrayList<Bangumi>();
            final String html = "https://share.dmhy.org/cms/page/name/programme.html/";
            Document doc = null;
            try {
                doc = Jsoup.connect(html).get();
                String url = "^.*http://share.dmhy.org/images/weekly/.*[\\.jpg|\\.gif|\\.png].*$";
                Pattern pattern = Pattern.compile(url);
                Matcher matcher;

                String[] rows = doc.toString().split("\n");

                mSize = rows.length;
                mDialog.setMax(mSize);

                for (String row : rows) {
                    publishProgress(++mCount);

                    matcher = pattern.matcher(row);
                    if (matcher.find()) {
                        String[] rawAttrs = row.split(",");
                        for (int i = 0; i < rawAttrs.length; i++) {
                            rawAttrs[i] = rawAttrs[i].replaceAll("'", "");
                        }
                        String name = rawAttrs[1];
                        String image = rawAttrs[0].replaceAll("^.*push\\(\\[", "");
                        String link = rawAttrs[4].replaceAll("]\\);", "");
                        Bangumi item = new Bangumi(name, image, link);
                        WeeklyList.add(item);
                    }
                }
                WeeklyList.remove(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return WeeklyList;
        }

        @Override
        protected void onPostExecute(List<Bangumi> bangumis) {
            super.onPostExecute(bangumis);
            if (bangumis != null && !bangumis.isEmpty()) {
                WeeklyList.clear();
                WeeklyList.addAll(bangumis);
            }
            swipe.setRefreshing(false);
            mDialog.dismiss();
            adapter.notifyDataSetChanged();
            BangumiData.banListA = new BangumiList(WeeklyList);
            BangumiData.banListA.renderImage(getContext());

            LOADED = true;
        }
    }
}
