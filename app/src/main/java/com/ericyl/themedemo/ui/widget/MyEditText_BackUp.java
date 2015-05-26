package com.ericyl.themedemo.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.ericyl.themedemo.R;

/**
 * Created by liangyu on 15/5/25.
 *
 * onUpClickListener和onDownClickListener未完成协调
 *
 * setError与右图同时使用有冲突
 *
 */
public class MyEditText_BackUp extends EditText implements TextWatcher,
        View.OnFocusChangeListener {

    private Drawable left = null;
    private Drawable right = null;
    private boolean hasFocus = false;

    private int unfocusedLeftBtnDrawableId = R.drawable.ic_username;
    private int focusedLeftBtnDrawableId = R.drawable.ic_username_focused;
    private int unfocusedRightBtnDrawableId = R.drawable.ic_action_cancel;
    private int focusedRightBtnDrawableId = R.drawable.ic_action_cancel_focused;

    private OnRightBtnClickListener onRightBtnClickListener = null;

    public void setOnRightBtnClickListener(OnRightBtnClickListener onRightBtnClickListener) {
        this.onRightBtnClickListener = onRightBtnClickListener;
    }

    public MyEditText_BackUp(Context context) {
        super(context);
        init();
    }

    public MyEditText_BackUp(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.MyEdittext);
        focusedLeftBtnDrawableId = typedArray.getResourceId(
                R.styleable.MyEdittext_focusLeftBtnDrawable, R.drawable.ic_username_focused);
        unfocusedLeftBtnDrawableId = typedArray.getResourceId(
                R.styleable.MyEdittext_unFocusLeftBtnDrawable, R.drawable.ic_username);
        focusedRightBtnDrawableId = typedArray.getResourceId(
                R.styleable.MyEdittext_focusRightBtnDrawable, R.drawable.ic_action_cancel_focused);
        unfocusedRightBtnDrawableId = typedArray.getResourceId(
                R.styleable.MyEdittext_unFocusRightBtnDrawable, R.drawable.ic_action_cancel);
        typedArray.recycle();
        init();
    }

    public MyEditText_BackUp(Context context, AttributeSet attrs, int defStryle) {
        super(context, attrs, defStryle);
        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.MyEdittext, defStryle, 0);
        focusedLeftBtnDrawableId = typedArray.getResourceId(
                R.styleable.MyEdittext_focusLeftBtnDrawable, R.drawable.ic_username_focused);
        unfocusedLeftBtnDrawableId = typedArray.getResourceId(
                R.styleable.MyEdittext_unFocusLeftBtnDrawable, R.drawable.ic_username);
        focusedRightBtnDrawableId = typedArray.getResourceId(
                R.styleable.MyEdittext_focusRightBtnDrawable, R.drawable.ic_action_cancel_focused);
        unfocusedRightBtnDrawableId = typedArray.getResourceId(
                R.styleable.MyEdittext_unFocusRightBtnDrawable, R.drawable.ic_action_cancel);
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
            if (TextUtils.isEmpty(getText().toString()))
                right = null;
            else
                right = getResources().getDrawable(unfocusedRightBtnDrawableId);
        } else {
            left = getResources().getDrawable(unfocusedLeftBtnDrawableId);
            right = null;
        }
    }

    private void setStatus() {
        setCompoundDrawablesWithIntrinsicBounds(left, null, right, null);
        postInvalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (!TextUtils.isEmpty(getText().toString())) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    if (isRightClick(event)) {
                        if (onRightBtnClickListener == null)
                            setText(null);
                        else
                            onRightBtnClickListener.onUpClickListener(this);
                    } else {
                        getDrawable();
                        setStatus();
                    }
                    break;
                case MotionEvent.ACTION_DOWN:
                    if (isRightClick(event)) {
                        right = getResources().getDrawable(focusedRightBtnDrawableId);
                        setStatus();
                        if (onRightBtnClickListener != null)
                            onRightBtnClickListener.onDownClickListener(this);
                    }
                    break;
            }
        }
        return super.onTouchEvent(event);
    }

    private boolean isRightClick(MotionEvent event){
        return (event.getX() > (getWidth() - getTotalPaddingRight())
                && (event.getX() < (getWidth() - getPaddingRight())) && (event.getY() <= (this.getHeight() - this.getPaddingBottom())));
    }

}
