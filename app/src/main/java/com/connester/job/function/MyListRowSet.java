package com.connester.job.function;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import java.util.List;

public abstract class MyListRowSet {
    LinearLayout linearLayout;
    List<?> objects;
    Context context;

    public MyListRowSet(LinearLayout linearLayout, List<?> objects, Context context) {
        this.linearLayout = linearLayout;
        this.objects = objects;
        this.context = context;
    }

    public Object getObject(int position) {
        return objects.get(position);
    }

    public abstract View setView(int position);

    public void createView() {
        int i = 0;
        while (i < objects.size()) {
            linearLayout.addView(setView(i));
            i++;
        }
    }
}
