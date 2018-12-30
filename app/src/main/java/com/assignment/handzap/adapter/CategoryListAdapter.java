package com.assignment.handzap.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.assignment.handzap.R;
import com.assignment.handzap.model.PostCategoryModel;

import java.util.ArrayList;

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.CategoryListViewHolder> {

    private ArrayList<PostCategoryModel> alPostCategoryModels;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context context;

    public CategoryListAdapter(Context context, ArrayList<PostCategoryModel> alPostCategoryModels) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.alPostCategoryModels = alPostCategoryModels;
    }

    @Override
    @NonNull
    public CategoryListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_category_list, parent, false);
        return new CategoryListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryListViewHolder holder, int position) {
        holder.tvCategoryName.setText(alPostCategoryModels.get(position).getCategoryName());
        holder.tvBackCatName.setText(alPostCategoryModels.get(position).getCategoryName());

        holder.ivCategory.setImageResource(alPostCategoryModels.get(position).getCategoryImage());
    }

    @Override
    public int getItemCount() {
        return alPostCategoryModels.size();
    }

    public class CategoryListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private AppCompatTextView tvCategoryName, tvBackCatName;
        private AppCompatImageView ivCategory;
        private RelativeLayout rlBack, rlFront;

        CategoryListViewHolder(View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            tvBackCatName = itemView.findViewById(R.id.tvBackCatName);
            rlBack = itemView.findViewById(R.id.rlBack);
            rlFront = itemView.findViewById(R.id.rlFront);
            ivCategory = itemView.findViewById(R.id.ivCategory);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (getCount() >= 3) {
                Toast.makeText(context, "Maximum 3 categories allowed", Toast.LENGTH_SHORT).show();
                return;
            }
            PostCategoryModel postCategoryModel = alPostCategoryModels.get(getAdapterPosition());
            if (!postCategoryModel.isFlipped()) {
                rlBack.setVisibility(View.VISIBLE);
                final ObjectAnimator oa1 = ObjectAnimator.ofFloat(rlBack, "scaleX", 1f, 0f);
                final ObjectAnimator oa2 = ObjectAnimator.ofFloat(rlBack, "scaleX", 0f, 1f);
                oa1.setInterpolator(new DecelerateInterpolator());
                oa2.setInterpolator(new AccelerateDecelerateInterpolator());
                oa1.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        rlFront.setVisibility(View.GONE);
                        oa2.start();
                    }
                });
                postCategoryModel.setFlipped(true);
                oa1.start();
            } else {
                rlFront.setVisibility(View.VISIBLE);
                final ObjectAnimator oa1 = ObjectAnimator.ofFloat(rlFront, "scaleX", 1f, 0f);
                final ObjectAnimator oa2 = ObjectAnimator.ofFloat(rlFront, "scaleX", 0f, 1f);
                oa1.setInterpolator(new DecelerateInterpolator());
                oa2.setInterpolator(new AccelerateDecelerateInterpolator());
                oa1.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        rlBack.setVisibility(View.GONE);
                        oa1.start();
                    }
                });
                postCategoryModel.setFlipped(false);
                oa2.start();
            }

            if (mClickListener != null)
                mClickListener.onItemClick(postCategoryModel, getAdapterPosition());
        }
    }

    public int getCount() {
        int count = 0;

        for (PostCategoryModel post : alPostCategoryModels) {
            if (post.isFlipped()) {
                count++;
            }
        }
        return count;
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(PostCategoryModel postCategoryModel, int position);
    }
}
