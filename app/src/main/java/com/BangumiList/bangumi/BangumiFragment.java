package com.BangumiList.bangumi;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.BangumiList.GloVar.BangumiData;
import com.BangumiList.R;
import com.BangumiList.Util.BaseAsyncBangumi;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BangumiFragment extends Fragment implements OnRefreshListener, BangumiAdapter.ClickListener {

    public static BangumiList bitmapList;
    static boolean LOADED = false;
    private static Bundle mBundleRecyclerViewState;
    private final String KEY_RECYCLER_STATE = "recycler_state";
    List<Bangumi> WeeklyList = new ArrayList<>();
    SwipeRefreshLayout swipe;
    BangumiAdapter adapter;
    RecyclerView recycler;
    View row;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        row = inflater.inflate(R.layout.activity_bangumi, null);

        adapter = new BangumiAdapter(getContext(), WeeklyList);
        swipe = (SwipeRefreshLayout) row.findViewById(R.id.swipe_bangumi);
        if (!LOADED)
            new grabBangumi(getContext(), adapter, swipe).execute();
        return row;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        StaggeredGridLayoutManager gaggeredGridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);

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
            List<Bangumi> WeeklyList = new ArrayList<Bangumi>();
            final String html = "https://share.dmhy.org/cms/page/name/programme.html/";
            Document doc;
            try{
                publishProgress(0, 2);

                doc = Jsoup.connect(html).get();
                String url = "^.*http://share.dmhy.org/images/weekly/.*[\\.jpg|\\.gif|\\.png].*$";
                Pattern pattern = Pattern.compile(url);
                Matcher matcher;

                String[] rows = doc.toString().split("\n");

                mSize = rows.length;
                mDialog.setMax(mSize);
                publishProgress(0, 3);

                for (String row : rows) {
                    publishProgress(++mCount, 3);
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
                        // Progress ++
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
            BangumiData.banListA = new BangumiList(WeeklyList);
            BangumiData.banListA.ImageArchiving(getContext(),mDialog);
            LOADED = true;
        }
    }
}
