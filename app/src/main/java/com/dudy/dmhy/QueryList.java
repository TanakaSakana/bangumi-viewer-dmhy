package com.dudy.dmhy;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class QueryList extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    TextView resultTable;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View row = inflater.inflate(R.layout.activity_query_list, null);
        swipeRefreshLayout = (SwipeRefreshLayout) row.findViewById(R.id.main_swipe);
        swipeRefreshLayout.setOnRefreshListener(this);
        resultTable = (TextView) row.findViewById(R.id.main_result);
        return row;
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
                Elements table = doc.getElementsByTag("tbody");
                Elements rows = table.select("tr");

                for (Element row : rows) {
                    Elements items = row.select("td");
                    result += String.format("Data\t : %s\n", items.eq(0).text());
                    result += String.format("Type\t : %s\n", items.eq(1).text());
                    result += String.format("Title\t : %s\n", items.eq(2).text());
                    result += String.format("Size\t : %s\n", items.eq(4).text());
                    result += String.format("Seeds\t : %s\n", items.eq(5).text());
                    result += String.format("Peers\t : %s\n", items.eq(6).text());
                    result += String.format("Download : %s\n", items.eq(7).text());
                    result += String.format("Host\t : %s\n", items.eq(8).text());
                    result += "\n";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }
    }
}
