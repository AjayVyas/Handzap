package com.assignment.handzap.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

import com.assignment.handzap.R;

public class CharCountTextView extends AppCompatTextView {
    private int maxLength = 150; //Emulate Twitter by Default
    private CharCountChangedListener listener;

    public interface CharCountChangedListener {
        void onCountChanged(int countRemaining, boolean hasExceededLimit);
    }

    public CharCountTextView(Context context) {
        super(context);
    }

    public CharCountTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        handleAttrs(context, attrs);
    }

    public CharCountTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        handleAttrs(context, attrs);
    }

    public void setCharCountChangedListener(CharCountChangedListener listener) {
        this.listener = listener;
    }

    public void setEditText(final EditText targetEt) {
        targetEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                setText(String.valueOf(maxLength - charSequence.length()));
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String enteredText = editable.toString();
                int countRemaining = maxLength - enteredText.length();
                boolean hasExceededLimit = enteredText.length() > maxLength;
                setText(String.valueOf(countRemaining));

                if (listener != null) {
                    listener.onCountChanged(countRemaining, hasExceededLimit);
                } else {
                    throw new NullPointerException("A TextCountDownListener has not been set!");
                }
            }
        });

        targetEt.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    setVisibility(INVISIBLE);
                } else {
                    setVisibility(VISIBLE);
                }
            }
        });
    }

    public void setMaxCharacters(Context context, int maxCharacters) {
        this.maxLength = maxCharacters;
    }

    private void handleAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CharCountTextView);
        this.maxLength = a.getInt(R.styleable.CharCountTextView_maxCharacters, 150);
        a.recycle();
    }

}
