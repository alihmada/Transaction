package com.ali.transaction.Classes;

import android.animation.ObjectAnimator;
import android.view.View;

public class Animation {
    public static void startAnimation(View view) {
        ObjectAnimator animation = ObjectAnimator.ofFloat(view, "translationX", -100f, 0f);
        animation.setDuration(250);
        animation.start();
    }
}
