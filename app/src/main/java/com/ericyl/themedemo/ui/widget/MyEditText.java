package com.ericyl.themedemo.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

import com.ericyl.themedemo.R;

/**
 * Created by liangyu on 15/5/25.
 *
 */
public class MyEditText extends EditText implements TextWatcher,
        View.OnFocusChangeListener {

    private Drawable left = null;
    private boolean hasFocus = false;

    private int unfocusedLeftBtnDrawableId = R.drawable.ic_username;
    private int focusedLeftBtnDrawableId = R.drawable.ic_username_focused;

    public MyEditText(Context context) {
        super(context);
        init();
    }

    public MyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.MyEdittext);
        focusedLeftBtnDrawableId = typedArray.getResourceId(
                R.styleable.MyEdittext_focusLeftBtnDrawable, R.drawable.ic_username_focused);
        unfocusedLeftBtnDrawableId = typedArray.getResourceId(
                R.styleable.MyEdittext_unFocusLeftBtnDrawable, R.drawable.ic_username);
        typedArray.recycle();
        init();
    }

    public MyEditText(Context context, AttributeSet attrs, int defStryle) {
        super(context, attrs, defStryle);
        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.MyEdittext, defStryle, 0);
        focusedLeftBtnDrawableId = typedArray.getResourceId(
                R.styleable.MyEdittext_focusLeftBtnDrawable, R.drawable.ic_username_focused);
        unfocusedLeftBtnDrawableId = typedArray.getResourceId(
                R.styleable.MyEdittext_unFocusLeftBtnDrawable, R.drawable.ic_username);
        typedArray.recycle();
        init();
    }

    private void init() {
        setOnFocusChangeListener(this);
        addTextChangedListener(this);
        getDrawable();
        setStatus();
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
    }

    @Override
    public void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        this.hasFocus = focused;
        getDrawable();
        setStatus();
    }

    @Override
    public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        getDrawable();
        setStatus();
    }

    private void getDrawable() {
        if (hasFocus) {
            left = getResources().getDrawable(focusedLeftBtnDrawableId);
        } else {
            left = getResources().getDrawable(unfocusedLeftBtnDrawableId);
        }
    }

    private void setStatus() {
        setCompoundDrawablesWithIntrinsicBounds(left, null, null, null);
        postInvalidate();
    }

}
