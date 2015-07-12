package com.lovespectre.lwin.openemr;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.zip.Inflater;

/**
 * Created by lwin on 7/7/15.
 */
public class Message extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.message, container, false);
        return v;
    }


    public static Message newInstance() {

        Message fragment = new Message();
        return fragment;
    }
}
