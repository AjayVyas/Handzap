package com.assignment.handzap.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.assignment.handzap.R;
import com.assignment.handzap.model.PostAttachmentModel;
import com.assignment.handzap.util.ImageFilePath;

import java.util.ArrayList;

public class PostAttachmentListAdapter extends RecyclerView.Adapter<PostAttachmentListAdapter.CategoryListViewHolder> {

    private ArrayList<PostAttachmentModel> alPostCategoryModels;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context context;

    public PostAttachmentListAdapter(Context context, ArrayList<PostAttachmentModel> alPostCategoryModels) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.alPostCategoryModels = alPostCategoryModels;
    }

    @Override
    @NonNull
    public CategoryListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_attachment, parent, false);
        return new CategoryListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryListViewHolder holder, int position) {

        if (alPostCategoryModels.get(position).isVideo()) {
            String selectedPathVideo = "";
            selectedPathVideo = ImageFilePath.getPath(context, alPostCategoryModels.get(position).getImageUri());

            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(selectedPathVideo, MediaStore.Video.Thumbnails.MINI_KIND);
            holder.ivAttachment.setImageBitmap(thumb);
            holder.ivVideo.setVisibility(View.VISIBLE);
        } else {
            holder.ivVideo.setVisibility(View.GONE);
            holder.ivAttachment.setImageURI(alPostCategoryModels.get(position).getImageUri());
        }
    }

    @Override
    public int getItemCount() {
        return alPostCategoryModels.size();
    }

    public class CategoryListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private AppCompatImageView ivAttachment, ivVideo;

        CategoryListViewHolder(View itemView) {
            super(itemView);
            ivVideo = itemView.findViewById(R.id.ivVideo);
            ivAttachment = itemView.findViewById(R.id.ivAttachment);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            PostAttachmentModel postAttachmentModel = alPostCategoryModels.get(getAdapterPosition());


            if (mClickListener != null)
                mClickListener.onItemClick(postAttachmentModel, getAdapterPosition());
        }
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(PostAttachmentModel postAttachmentModel, int position);
    }
}
