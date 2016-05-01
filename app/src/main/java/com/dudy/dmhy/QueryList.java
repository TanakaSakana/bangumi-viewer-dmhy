package com.dudy.dmhy;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class QueryList extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    TextView resultTable;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_list);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.main_swipe);
        swipeRefreshLayout.setOnRefreshListener(this);
        resultTable = (TextView) findViewById(R.id.main_result);
    }

    @Override
    public void onRefresh() {
        new Query().execute();
    }

    class Query extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            return grabBySelector();
        }

        @Override
        protected void onPostExecute(String str) {
            super.onPostExecute(str);
            resultTable.setText(str);
            swipeRefreshLayout.setRefreshing(false);
        }

        public String grabBySelector() {
            final String html = "https://share.dmhy.org/";
            String result = "";
            Document doc;
            try {
                doc = Jsoup.connect(html).get();
                Elements table = doc.getElementsByClass("title");

                for (int i = 9; i < table.size(); i++) {
                    Element e = table.get(i);
                    Elements se = e.select("a[href]");
                    e = se.last();
                    result += String.format("[%d] : %s\n", i-8, e.text());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }
    }
}
