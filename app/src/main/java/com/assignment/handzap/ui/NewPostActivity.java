package com.assignment.handzap.ui;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.assignment.handzap.R;
import com.assignment.handzap.adapter.CountrySpinnerAdapter;
import com.assignment.handzap.adapter.PostAttachmentListAdapter;
import com.assignment.handzap.databinding.ActivityNewPostBinding;
import com.assignment.handzap.model.PostAttachmentModel;
import com.assignment.handzap.util.AppConstants;
import com.assignment.handzap.util.CharCountTextView;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class NewPostActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {

    SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE dd MMM");

    private ActivityNewPostBinding binding;
    private AlertDialog alertDialog;
    private ArrayList<PostAttachmentModel> alPostAttachmentModels = new ArrayList<>();


    CharSequence[] arrRates = {" No Preference ", " Fixed Budget ", " Hourly Rate "};
    CharSequence[] arrPaymentMethods = {" No Preference ", " E-Payment ", " Cash "};
    CharSequence[] arrJobTerms = {" Recurring Job ", " Same Day Job ", " Multi Days Job "};

    String[] textArray = {"INR", "EUR", "UZS", "CAD"};
    Integer[] imageArray = {R.drawable.ic_india, R.drawable.ic_germany,
            R.drawable.ic_uzbekistan, R.drawable.ic_canada};
    private PostAttachmentListAdapter postAttachmentListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.lbl_new_post);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_post);
        binding.tvPostTitleCounter.setEditText(binding.etPostTitle);
        binding.tvPostTitleCounter.setMaxCharacters(this, 50);
        binding.tvPostTitleCounter.setCharCountChangedListener(new CharCountTextView.CharCountChangedListener() {
            @Override
            public void onCountChanged(int countRemaining, boolean hasExceededLimit) {
                binding.tvPostTitleCounter.setText(getString(R.string.lbl_character_left, countRemaining));
            }
        });

        binding.tvDescribeCounter.setEditText(binding.etDescribePost);
        binding.tvDescribeCounter.setMaxCharacters(this, 400);
        binding.tvDescribeCounter.setCharCountChangedListener(new CharCountTextView.CharCountChangedListener() {
            @Override
            public void onCountChanged(int countRemaining, boolean hasExceededLimit) {
                binding.tvDescribeCounter.setText(getString(R.string.lbl_character_left, countRemaining));
            }
        });

        binding.etPostTitle.setOnFocusChangeListener(this);
        binding.etDescribePost.setOnFocusChangeListener(this);
        binding.etPostCategories.setOnFocusChangeListener(this);

        binding.etPostCategories.setOnClickListener(this);

        binding.etPostCategories.setClickable(true);
        binding.etPostCategories.setFocusable(false);
        binding.etPostCategories.setInputType(InputType.TYPE_NULL);

        binding.etRate.setOnClickListener(this);
        binding.etRate.setClickable(true);
        binding.etRate.setFocusable(false);
        binding.etRate.setInputType(InputType.TYPE_NULL);

        binding.etPaymentMethod.setOnClickListener(this);
        binding.etPaymentMethod.setClickable(true);
        binding.etPaymentMethod.setFocusable(false);
        binding.etPaymentMethod.setInputType(InputType.TYPE_NULL);

        binding.etLocation.setOnClickListener(this);
        binding.etLocation.setClickable(true);
        binding.etLocation.setFocusable(false);
        binding.etLocation.setInputType(InputType.TYPE_NULL);

        binding.etDate.setOnClickListener(this);
        binding.etDate.setClickable(true);
        binding.etDate.setFocusable(false);
        binding.etDate.setInputType(InputType.TYPE_NULL);

        binding.etJobTerm.setOnClickListener(this);
        binding.etJobTerm.setClickable(true);
        binding.etJobTerm.setFocusable(false);
        binding.etJobTerm.setInputType(InputType.TYPE_NULL);

        binding.ivAdaAttachment.setOnClickListener(this);

        setupUI(binding.parent);

        CountrySpinnerAdapter adapter = new CountrySpinnerAdapter(this, R.layout.item_country_spinner, textArray, imageArray);

        binding.spinCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                binding.tvCountryName.setText(textArray[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });
        binding.spinCountry.setAdapter(adapter);

        postAttachmentListAdapter = new PostAttachmentListAdapter(this, alPostAttachmentModels);
        binding.rvImages.setAdapter(postAttachmentListAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.new_post, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menuPost:
                    submitData();
//                setResult();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void submitData(){
        final ProgressDialog pd = ProgressDialog.show(NewPostActivity.this, "", "Processing Media...",
                true, false);
        pd.show();
        new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int progress = (int) ((10000 - millisUntilFinished) / 1000);
                pd.setProgress(progress);
            }

            @Override
            public void onFinish() {
                pd.dismiss();
            }

        }.start();
    }

    public void showBottomSheetDialog() {
        View view = getLayoutInflater().inflate(R.layout.item_bottom_sheet, null);

        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        LinearLayout llCamera = view.findViewById(R.id.llCamera);
        LinearLayout llVideo = view.findViewById(R.id.llVideo);
        LinearLayout llPhoto = view.findViewById(R.id.llPhoto);
        LinearLayout llDocument = view.findViewById(R.id.llDocument);

        llCamera.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA , Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            AppConstants.CAMERA_PERMISSION_CODE);
                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, AppConstants.REQUEST_CAMERA);
                }
                dialog.dismiss();
            }
        });

        llVideo.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            AppConstants.VIDEO_PERMISSION_CODE);
                } else {
                    Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30);
                    takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Environment.getExternalStorageDirectory().getPath() + "videocapture_example.mp4");

                    startActivityForResult(takeVideoIntent, AppConstants.REQUEST_VIDEO);
                }
                dialog.dismiss();
            }
        });

        llPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), AppConstants.REQUEST_GALLERY);

                dialog.dismiss();
            }
        });

        llDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                try {
                    startActivityForResult(
                            Intent.createChooser(intent, "Select a File to Upload"),
                            AppConstants.REQUEST_DOCUMENT);
                } catch (android.content.ActivityNotFoundException ex) {
                    // Potentially direct the user to the Market with a Dialog
                    Toast.makeText(NewPostActivity.this, "Please install a File Manager.",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

        dialog.setContentView(view);
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == AppConstants.CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, AppConstants.REQUEST_CAMERA);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }

            if (requestCode == AppConstants.VIDEO_PERMISSION_CODE) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30);
                    takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Environment.getExternalStorageDirectory().getPath() + "videocapture_example.mp4");

                    startActivityForResult(takeVideoIntent, AppConstants.REQUEST_VIDEO);
                } else {
                    Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
                }
            }


        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.etPostCategories:
                startActivityForResult(new Intent(getApplicationContext(), PostCategoryListActivity.class), AppConstants.REQUEST_CODE_CATEGORIES);
                break;
            case R.id.etRate:
                showRateSpinner();
                break;
            case R.id.etPaymentMethod:
                showPaymentMethodSpinner();
                break;
            case R.id.etLocation:
                showPaymentMethodSpinner();
                break;
            case R.id.etDate:
                showDatePicker();
                break;
            case R.id.etJobTerm:
                showJobTermSpinner();
                break;
            case R.id.ivAdaAttachment:
                showBottomSheetDialog();
                break;

        }
    }

    private void showDatePicker() {
        final Calendar newDate = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                newDate.set(year, monthOfYear, dayOfMonth);
                binding.etDate.setText(dateFormat.format(newDate.getTime()));
            }

        }, newDate.get(Calendar.YEAR), newDate.get(Calendar.MONTH), newDate.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AppConstants.REQUEST_CODE_CATEGORIES) {
            if (resultCode == RESULT_OK) {
                int count = data.getIntExtra(AppConstants.INTENT_COUNT, 0);
                binding.etPostCategories.setText(count + " categories selected");

                if (count > 0) {
                    binding.tilPostCategories.setHint(getString(R.string.lbl_post_categories));
                } else {
                    binding.tilPostCategories.setHint(getString(R.string.lbl_select_post_categories));
                }
            }
        }
        if (requestCode == AppConstants.REQUEST_CAMERA && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = getImageUri((Bitmap)data.getExtras().get("data"));

            alPostAttachmentModels.add(new PostAttachmentModel(selectedImage, false));
        }
        if (requestCode == AppConstants.REQUEST_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();

            alPostAttachmentModels.add(new PostAttachmentModel(selectedImage, false));
        }
        if (requestCode == AppConstants.REQUEST_DOCUMENT && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();

            alPostAttachmentModels.add(new PostAttachmentModel(selectedImage, false));
        }
        if (requestCode == AppConstants.REQUEST_VIDEO && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            alPostAttachmentModels.add(new PostAttachmentModel(selectedImage, true));
        }
        postAttachmentListAdapter.notifyDataSetChanged();
    }

    public Uri getImageUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(this.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }
    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(NewPostActivity.this);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.etPostTitle:
                if (hasFocus) {
                    binding.tilEnterPost.setHint(getString(R.string.lbl_post_title));
                } else {
                    binding.tilEnterPost.setHint(getString(R.string.lbl_enter_post_title));
                }
                break;
            case R.id.etDescribePost:
                if (hasFocus) {
                    binding.tilDescribePost.setHint(getString(R.string.lbl_post_desc));
                } else {
                    binding.tilDescribePost.setHint(getString(R.string.lbl_describe));
                }
                break;
        }
    }

    public void showRateSpinner() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getString(R.string.lbl_rate));

        builder.setSingleChoiceItems(arrRates, getCheckedId(String.valueOf(binding.etRate.getText()), arrRates), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

            }
        });
        builder.setPositiveButton(getString(R.string.lbl_select), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ListView lw = ((AlertDialog) dialog).getListView();
                Object checkedItem = lw.getAdapter().getItem(lw.getCheckedItemPosition());
                binding.etRate.setText(String.valueOf(checkedItem));
                alertDialog.dismiss();
            }
        });
        builder.setNegativeButton(getString(R.string.lbl_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog = builder.create();
        alertDialog.show();

    }

    public void showJobTermSpinner() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getString(R.string.lbl_job_term));

        builder.setSingleChoiceItems(arrJobTerms, getCheckedId(String.valueOf(binding.etJobTerm.getText()), arrJobTerms), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

            }
        });
        builder.setPositiveButton(getString(R.string.lbl_select), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ListView lw = ((AlertDialog) dialog).getListView();
                Object checkedItem = lw.getAdapter().getItem(lw.getCheckedItemPosition());
                binding.etJobTerm.setText(String.valueOf(checkedItem));
                alertDialog.dismiss();
            }
        });
        builder.setNegativeButton(getString(R.string.lbl_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog = builder.create();
        alertDialog.show();
    }

    public void showPaymentMethodSpinner() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getString(R.string.lbl_payment_method));

        builder.setSingleChoiceItems(arrPaymentMethods, getCheckedId(String.valueOf(binding.etPaymentMethod.getText()), arrPaymentMethods), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

            }
        });
        builder.setPositiveButton(getString(R.string.lbl_select), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ListView lw = ((AlertDialog) dialog).getListView();
                Object checkedItem = lw.getAdapter().getItem(lw.getCheckedItemPosition());
                binding.etPaymentMethod.setText(String.valueOf(checkedItem));
                alertDialog.dismiss();
            }
        });
        builder.setNegativeButton(getString(R.string.lbl_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog = builder.create();
        alertDialog.show();
    }

    private int getCheckedId(String text, CharSequence[] array) {
        for (int i = 0; i < array.length; i++) {
            if (text.equals(array[i])) {
                return i;
            }
        }
        return 0;
    }

}
