package com.dudy.dmhy;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BangumiPage extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    List<Bangumi> WeeklyList = new ArrayList<>();
    SwipeRefreshLayout swipe;
    BangumiAdapter adapter;
    RecyclerView recycler;
    View row;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        row = inflater.inflate(R.layout.activity_bangumi, null);

        return row;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        adapter = new BangumiAdapter(getContext(), WeeklyList);

        recycler = (RecyclerView) row.findViewById(R.id.recycler_bangumi);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(layoutManager);

        swipe = (SwipeRefreshLayout) row.findViewById(R.id.swipe_bangumi);
        swipe.setOnRefreshListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        new grabBangumi().execute();
    }

    @Override
    public void onRefresh() {
        new grabBangumi().execute();
        adapter.notifyDataSetChanged();
    }

    class grabBangumi extends AsyncTask<Void, Void, List<Bangumi>> {
        @Override
        protected List<Bangumi> doInBackground(Void... params) {
            try {
                Log.e("doInBackground", "OK");
                return grabBangumi();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("printStackTrace", "OK");
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Bangumi> bangumis) {
            super.onPostExecute(bangumis);
            WeeklyList.addAll(bangumis);
            swipe.setRefreshing(false);
            adapter.notifyDataSetChanged();
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
