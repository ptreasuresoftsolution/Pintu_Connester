package com.connester.job.function;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

public abstract class MyGridSet {
    LinearLayout linearLayout;
    ArrayList<?> objects;
    Context context;
    int grid = 2;

    public MyGridSet(LinearLayout linearLayout, ArrayList<?> objects, Context context) {
        this.linearLayout = linearLayout;
        this.objects = objects;
        this.context = context;
    }

    public MyGridSet(LinearLayout linearLayout, ArrayList<?> objects, Context context, int grid) {
        this.linearLayout = linearLayout;
        this.objects = objects;
        this.context = context;
        this.grid = grid;
    }

    public abstract View setView(int position);

    public void createView() {
        Log.d(LogTag.CHECK_DEBUG, "Grid set Object ArrayList size " + objects.size());
        int i = 0;
        while (i < objects.size()) {
            LinearLayout linearLayoutH = new LinearLayout(context);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            linearLayoutH.setLayoutParams(layoutParams);
            linearLayoutH.setOrientation(LinearLayout.HORIZONTAL);
            //1 box
            linearLayoutH.addView(setView(i));
            i++;
            //2 box
            if (i < objects.size()) {
                linearLayoutH.addView(setView(i));
            } else {
                //add as blank layout
                LinearLayout linearLayout2 = new LinearLayout(context);
                LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
                linearLayout2.setLayoutParams(layoutParams2);
                linearLayoutH.addView(linearLayout2);
            }
            i++;
            //3 box
            if (grid > 2) {
                if (i < objects.size()) {
                    linearLayoutH.addView(setView(i));
                } else {
                    //add as blank layout
                    LinearLayout linearLayout2 = new LinearLayout(context);
                    LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
                    linearLayout2.setLayoutParams(layoutParams2);
                    linearLayoutH.addView(linearLayout2);
                }
                i++;
            }
            //4 box
            if (grid > 3) {
                if (i < objects.size()) {
                    linearLayoutH.addView(setView(i));
                } else {
                    //add as blank layout
                    LinearLayout linearLayout2 = new LinearLayout(context);
                    LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
                    linearLayout2.setLayoutParams(layoutParams2);
                    linearLayoutH.addView(linearLayout2);
                }
                i++;
            }
            //5 box
            if (grid > 4) {
                if (i < objects.size()) {
                    linearLayoutH.addView(setView(i));
                } else {
                    //add as blank layout
                    LinearLayout linearLayout2 = new LinearLayout(context);
                    LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
                    linearLayout2.setLayoutParams(layoutParams2);
                    linearLayoutH.addView(linearLayout2);
                }
                i++;
            }
            linearLayout.addView(linearLayoutH);
        }
    }
}
