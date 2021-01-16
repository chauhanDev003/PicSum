package com.abhishek.picsum.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abhishek.picsum.Models.RecyclerViewModel;
import com.abhishek.picsum.R;
import com.abhishek.picsum.Utils.ImageLoader;
import com.abhishek.picsum.Utils.Url;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewVH> {

    ImageLoader imageLoader;
    private List<RecyclerViewModel> recyclerViewModelList;
    private Context context;

    public RecyclerViewAdapter(List<RecyclerViewModel> recyclerViewModelList, Context context) {
        this.recyclerViewModelList = recyclerViewModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerViewVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_item, viewGroup, false);
        return new RecyclerViewVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewVH holder, int i) {
        RecyclerViewModel recyclerViewModel = recyclerViewModelList.get(i);
        holder.authorTV.setText(recyclerViewModel.getAuthor());

        ImageView image = holder.bookImgView;

        //DisplayImage function from ImageLoader Class
        imageLoader = new ImageLoader(context);
        holder.urlImg = Url.imgURL + recyclerViewModel.getId();
        imageLoader.DisplayImage(holder.urlImg, image);
    }

    @Override
    public int getItemCount() {
        return recyclerViewModelList.size();
    }

    class RecyclerViewVH extends RecyclerView.ViewHolder {

        TextView authorTV;
        ImageView bookImgView;
        String urlImg;

        public RecyclerViewVH(@NonNull View itemView) {
            super(itemView);
            authorTV = itemView.findViewById(R.id.authorName);
            bookImgView = itemView.findViewById(R.id.image);
        }
    }
}
