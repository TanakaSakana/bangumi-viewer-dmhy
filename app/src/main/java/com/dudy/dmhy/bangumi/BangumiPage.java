package com.dudy.dmhy.bangumi;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dudy.dmhy.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BangumiPage extends Fragment implements OnRefreshListener, BangumiAdapter.ClickListener {

    public static BangumiList bitmapList;
    final String filename = "bangumi_cache";
    List<Bangumi> WeeklyList = new ArrayList<>();
    SwipeRefreshLayout swipe;
    BangumiAdapter adapter;
    RecyclerView recycler;
    View row;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        row = inflater.inflate(R.layout.activity_bangumi, null);
        new grabBangumi().execute();
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
    public void onRefresh() {
        new grabBangumi().execute();
    }

    class grabBangumi extends AsyncTask<Void, Void, List<Bangumi>> {
        @Override
        protected List<Bangumi> doInBackground(Void... params) {
            try {
                return grabBangumi();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Bangumi> bangumis) {
            super.onPostExecute(bangumis);
            if (bangumis != null && !bangumis.isEmpty()) {
                WeeklyList.clear();
                WeeklyList.addAll(bangumis);
            }

            swipe.setRefreshing(false);
            adapter.notifyDataSetChanged();

            bitmapList = new BangumiList(WeeklyList);
            bitmapList.renderImage(getActivity());
        }

        public List<Bangumi> grabBangumi() throws Exception {
            List<Bangumi> WeeklyList = new ArrayList<Bangumi>();
            final String html = "https://share.dmhy.org/cms/page/name/programme.html/";
            Document doc = Jsoup.connect(html).get();

            String url = "^.*http://share.dmhy.org/images/weekly/.*[\\.jpg|\\.gif|\\.png].*$";
            Pattern pattern = Pattern.compile(url);
            Matcher matcher;

            String[] rows = doc.toString().split("\n");

            for (String row : rows) {
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
            return WeeklyList;
        }
    }
}
