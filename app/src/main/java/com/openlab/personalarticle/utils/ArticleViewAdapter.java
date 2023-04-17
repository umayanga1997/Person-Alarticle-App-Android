package com.openlab.personalarticle.utils;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.openlab.personalarticle.Models.ArticleModel;
import com.openlab.personalarticle.R;
import com.openlab.personalarticle.screen.ArticleViewActivity;
import java.util.ArrayList;

public class ArticleViewAdapter extends RecyclerView.Adapter<ArticleViewAdapter.ArticleViewHolder> {

    Context context;
    ArrayList<ArticleModel> articleList;

    public ArticleViewAdapter(Context context, ArrayList<ArticleModel> articleList) {
        this.context = context;
        this.articleList = articleList;
    }

    @NonNull
    @Override
    public ArticleViewAdapter.ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                   int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.article_card, parent,
                false);
        return new ArticleViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewAdapter.ArticleViewHolder holder, int position)
    {
        ArticleModel articleModel = articleList.get(position);
        holder.titleTxt.setText(articleModel.getTitle());
        holder.writerTxt.setText(articleModel.getWriter());
        Glide.with(context).load(articleModel.getThumbnailUrl()).centerCrop().into(holder
                .thumbnailImg);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(), ArticleViewActivity.class);
                intent.putExtra("ARTICLE_DATA",  articleModel);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    public static class ArticleViewHolder extends RecyclerView.ViewHolder{

        TextView titleTxt;
        TextView writerTxt;
        ImageView thumbnailImg;
        ImageView editBtn;

        public ArticleViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.title_card_txt);
            writerTxt = itemView.findViewById(R.id.writer_card_txt);
            thumbnailImg = itemView.findViewById(R.id.thumbnail_card_img);
            editBtn = itemView.findViewById(R.id.article_card_edit_btn);

            editBtn.setVisibility(View.GONE);
        }
    }
}
