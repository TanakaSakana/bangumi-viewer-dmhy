package com.dudy.dmhy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class BangumiAdapter extends RecyclerView.Adapter<BangumiAdapter.BangumiHolder> {
    LayoutInflater inflater;
    List<Bangumi> data = Collections.emptyList();

    public BangumiAdapter(Context mContext, List<Bangumi> data) {
        inflater = LayoutInflater.from(mContext);
        this.data = data;
    }

    @Override
    public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        super.registerAdapterDataObserver(observer);
    }

    @Override
    public BangumiHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = inflater.inflate(R.layout.row_bangumi, parent, false);
        Log.e("onCreateViewHolder","OK");
        BangumiHolder holder = new BangumiHolder(row);
        return holder;
    }

    @Override
    public void onBindViewHolder(BangumiHolder holder, int position) {
        Bangumi bangumi = data.get(position);
        holder.name.setText(bangumi.getName());
        holder.image.setImageBitmap(getImg(bangumi.getImage()));
    }

    private Bitmap getImg(final String path) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        Future<Bitmap> future = executorService.submit(new Callable<Bitmap>() {
            @Override
            public Bitmap call() throws Exception {
                Bitmap bitmap = null;
                HttpURLConnection connection;
                try {
                    URL url = new URL(path);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.connect();
                    bitmap = BitmapFactory.decodeStream(connection.getInputStream());
                    connection.getInputStream().close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return bitmap;
            }
        });
        executorService.shutdown();
        Bitmap img = null;
        try {
            img = future.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return img;
    }

    @Override
    public int getItemCount() {
        return  data == null ? 0 : data.size();
    }

    class BangumiHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView image;

        public BangumiHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.bangumi_name);
            image = (ImageView) itemView.findViewById(R.id.bangumi_image);
        }

    }
}
