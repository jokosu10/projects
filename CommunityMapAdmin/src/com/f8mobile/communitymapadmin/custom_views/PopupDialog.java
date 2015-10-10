package com.f8mobile.communitymapadmin.custom_views;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;

public class PopupDialog extends Dialog {

    private final Context context;

    public PopupDialog(Context context1)
    {
        super(context1);
        context = context1;
        requestWindowFeature(1);
    }

    public void showAsDropDown(View view)
    {
        float f = context.getResources().getDisplayMetrics().density;
        android.view.WindowManager.LayoutParams layoutparams = getWindow().getAttributes();
        int ai[] = new int[2];
        view.getLocationInWindow(ai);
        layoutparams.gravity = 51;
        layoutparams.x = ai[0] + (int)((float)view.getWidth() / f);
        layoutparams.y = ai[1] + (int)((float)view.getHeight() / f);
        show();
    }

    public void showAtLocation(int i, int j)
    {
        android.view.WindowManager.LayoutParams layoutparams = getWindow().getAttributes();
        layoutparams.gravity = 53;
        layoutparams.x = i;
        layoutparams.y = j;
        show();
    }
}
