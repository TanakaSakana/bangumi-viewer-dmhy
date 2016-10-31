package com.BangumiList.bangumi;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.BangumiList.R;

import java.util.Collections;
import java.util.List;

public class BangumiAdapterSimple extends RecyclerView.Adapter<BangumiAdapterSimple.SimpleBangumiHolder> {
    LayoutInflater inflater;
    List<Bangumi> data = Collections.emptyList();
    ClickListener clickListener;
    Context mContext;

    public BangumiAdapterSimple(Context mContext, List<Bangumi> data) {
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
        this.data = data;
    }

    @Override
    public SimpleBangumiHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = inflater.inflate(R.layout.simplerow_bangumi, parent, false);
        SimpleBangumiHolder holder = new SimpleBangumiHolder(row);
        return holder;
    }

    @Override
    public void onBindViewHolder(SimpleBangumiHolder holder, int position) {
        Bangumi bangumi = data.get(position);
        holder.name.setText(bangumi.getName());
        holder.description.setText(bangumi.getDescription());
    }


    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ClickListener {
        void itemClicked(View view, String link);
    }

    class SimpleBangumiHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name;
        TextView description;

        public SimpleBangumiHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            name = (TextView) itemView.findViewById(R.id.bangumi_name);
            description = (TextView) itemView.findViewById(R.id.bangumi_description);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                clickListener.itemClicked(v, data.get(getLayoutPosition()).getLink());
            }
        }
    }
}
