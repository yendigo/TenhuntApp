package com.example.tenhunt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder> {
    private android.content.Context mContext;
    private List<Upload> uploads;
    private OnItemClickListener mListener;
    private Picasso picasso;
    private OkHttpClient okHttpClient;

    public RecyclerAdapter(Context context, List<Upload> posts){
        mContext = context;
        uploads = posts;
    }


    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.post_rv_item, parent, false);
        return new RecyclerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        Upload currentUpload = uploads.get(position);
        holder.titleTextView.setText(currentUpload.getPostTitle());
        holder.descTextView.setText(currentUpload.getPostDesc());
        holder.priceTextView.setText(currentUpload.getPostPrice());
        holder.contactTextView.setText(currentUpload.getPostContact());
        holder.emailTextView.setText(currentUpload.getPostEmail());
        Picasso.with(mContext).setLoggingEnabled(true);
        Picasso.with(mContext)
                .load(currentUpload.getmImageUrl()).skipMemoryCache()
                .placeholder(R.drawable.dorm_hunt)
                .fit()
                .centerCrop()
                .into(holder.uploadImageView);

    }

    @Override
    public int getItemCount() {
        return uploads.size();
    }




    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView titleTextView, descTextView, priceTextView, contactTextView, emailTextView;
        public ImageView uploadImageView;

        public RecyclerViewHolder(View itemView){
            super(itemView);
            titleTextView = itemView.findViewById(R.id.idTVPostTitle);
            descTextView = itemView.findViewById(R.id.idTVPostDesc);
            priceTextView = itemView.findViewById(R.id.idTVPrice);
            contactTextView = itemView.findViewById(R.id.idTVPostContact);
            emailTextView = itemView.findViewById(R.id.idTVPostEmail);
            uploadImageView = itemView.findViewById(R.id.image_view_upload);

            itemView.setOnClickListener(this);

        }


        @Override
        public void onClick(View view) {
            if (mListener != null){
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION){
                    mListener.onItemClick(position);
                    }
                }
            }
        }

    //This is where we'll work on
    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public void setOnItemCLickListener(OnItemClickListener listener) {
        mListener = listener;
    }

}
