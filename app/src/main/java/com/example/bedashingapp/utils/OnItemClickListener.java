package com.example.bedashingapp.utils;

import android.view.View;

public abstract class OnItemClickListener<T> implements View.OnClickListener {
    private int position = 0;
    private OnClickCallback onClickCallback;
    private String type = "";
    private T data = null;

    public OnItemClickListener() {
        this.position = position;
        this.onClickCallback = onClickCallback;
        this.type = type;
        this.data = data;
    }

    @Override
    public void onClick(View view) {
        onClickCallback.onClicked(view, position, type,data);
    }

    public abstract void onClicked(View view, int position, String type,T data);

    public interface OnClickCallback {
       <T> void onClicked(View view, int position, String type,T data);
    }
}