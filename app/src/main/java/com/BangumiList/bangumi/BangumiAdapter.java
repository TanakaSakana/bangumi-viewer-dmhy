package com.BangumiList.bangumi;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.BangumiList.GloVar.BangumiData;
import com.BangumiList.R;

import java.util.Collections;
import java.util.List;

public class BangumiAdapter extends RecyclerView.Adapter<BangumiAdapter.BangumiHolder> {
    LayoutInflater inflater;
    List<Bangumi> data = Collections.emptyList();
    ClickListener clickListener;
    Context mContext;

    public BangumiAdapter(Context mContext, List<Bangumi> data) {
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
        this.data = data;
    }

    @Override
    public BangumiHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = inflater.inflate(R.layout.row_bangumi, parent, false);
        BangumiHolder holder = new BangumiHolder(row);
        return holder;
    }

    @Override
    public void onBindViewHolder(BangumiHolder holder, int position) {
        Bangumi bangumi = data.get(position);
        Bitmap BitmapTemp;

        holder.name.setText(bangumi.getName());
        if ((BitmapTemp =BangumiList.getBangumiBitmap(bangumi.getName(), mContext)) != null)
            holder.image.setImageBitmap(BitmapTemp);
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

    class BangumiHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name;
        ImageView image;

        public BangumiHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            name = (TextView) itemView.findViewById(R.id.bangumi_name);
            image = (ImageView) itemView.findViewById(R.id.bangumi_image);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                clickListener.itemClicked(v, data.get(getLayoutPosition()).getLink());
            }
        }
    }
}
