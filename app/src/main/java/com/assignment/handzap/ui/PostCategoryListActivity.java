
package com.assignment.handzap.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.assignment.handzap.R;
import com.assignment.handzap.adapter.CategoryListAdapter;
import com.assignment.handzap.databinding.ActivityPostCategoryBinding;
import com.assignment.handzap.model.PostCategoryModel;
import com.assignment.handzap.util.AppConstants;

import java.util.ArrayList;

public class PostCategoryListActivity extends AppCompatActivity implements CategoryListAdapter.ItemClickListener {

    private ActivityPostCategoryBinding binding;
    private ArrayList<PostCategoryModel> alPostCategoryModels;
    private CategoryListAdapter categoryListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addDummyData();

        binding = DataBindingUtil.setContentView(this, R.layout.activity_post_category);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.lbl_post_categories);

        categoryListAdapter = new CategoryListAdapter(this, alPostCategoryModels);
        categoryListAdapter.setClickListener(this);
        binding.rvCategories.setLayoutManager(new GridLayoutManager(this, 3));
        binding.rvCategories.setAdapter(categoryListAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.category_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menuSelect:
                setResult();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addDummyData() {
        alPostCategoryModels = new ArrayList<>();
        alPostCategoryModels.add(new PostCategoryModel(1, "C 001", R.drawable.ic_cat_1, false));
        alPostCategoryModels.add(new PostCategoryModel(2, "C 002", R.drawable.ic_cat_2, false));
        alPostCategoryModels.add(new PostCategoryModel(3, "C 003", R.drawable.ic_cat_3, false));
        alPostCategoryModels.add(new PostCategoryModel(4, "C 004", R.drawable.ic_cat_4, false));
        alPostCategoryModels.add(new PostCategoryModel(5, "C 005", R.drawable.ic_cat_1, false));
        alPostCategoryModels.add(new PostCategoryModel(6, "C 006", R.drawable.ic_cat_2, false));
        alPostCategoryModels.add(new PostCategoryModel(7, "C 007", R.drawable.ic_cat_3, false));
        alPostCategoryModels.add(new PostCategoryModel(8, "C 008", R.drawable.ic_cat_4, false));
        alPostCategoryModels.add(new PostCategoryModel(9, "C 009", R.drawable.ic_cat_1, false));
        alPostCategoryModels.add(new PostCategoryModel(10, "C 010", R.drawable.ic_cat_2, false));
        alPostCategoryModels.add(new PostCategoryModel(11, "C 011", R.drawable.ic_cat_3, false));
        alPostCategoryModels.add(new PostCategoryModel(12, "C 012", R.drawable.ic_cat_4, false));
        alPostCategoryModels.add(new PostCategoryModel(13, "C 013", R.drawable.ic_cat_1, false));
        alPostCategoryModels.add(new PostCategoryModel(14, "C 014", R.drawable.ic_cat_2, false));
        alPostCategoryModels.add(new PostCategoryModel(15, "C 015", R.drawable.ic_cat_3, false));
        alPostCategoryModels.add(new PostCategoryModel(16, "C 016", R.drawable.ic_cat_4, false));
    }

    @Override
    public void onItemClick(PostCategoryModel post, int position) {

        for (int i = 0; i < alPostCategoryModels.size(); i++) {
            if (alPostCategoryModels.get(i).getCategoryId() == post.getCategoryId()) {
                alPostCategoryModels.get(i).setFlipped(alPostCategoryModels.get(i).isFlipped());
            }
        }
        updateCount();
    }

    private int updateCount() {
        int count = categoryListAdapter.getCount();

        if (count == 0) {
            binding.tvSelected.setVisibility(View.INVISIBLE);
            binding.tvHintSelected.setText(R.string.lbl_upto_cat);
        } else {
            binding.tvSelected.setText(count + " selected");
            binding.tvHintSelected.setText(getString(R.string.lbl_max_categories));
            binding.tvSelected.setVisibility(View.VISIBLE);
        }
        return count;
    }

    private void setResult() {
        Intent data = new Intent();
        data.putExtra(AppConstants.INTENT_COUNT, updateCount());
        setResult(RESULT_OK, data);
        finish();
    }
}
