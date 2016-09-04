package com.BangumiList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.BangumiList.Util.JsoupUtil;

public class DMHYQueryList extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
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
            return JsoupUtil.grabDmhyRows();
        }

        @Override
        protected void onPostExecute(String str) {
            super.onPostExecute(str);
            resultTable.setText(str);
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}
