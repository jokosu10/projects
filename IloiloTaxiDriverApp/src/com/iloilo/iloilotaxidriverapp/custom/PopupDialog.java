package com.iloilo.iloilotaxidriverapp.custom;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class PopupDialog extends Dialog {
    private final Context context;
    
    public PopupDialog(Context context) {
        super(context);
        this.context = context;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }
    
    public void showAtLocation(int x, int y) {
        WindowManager.LayoutParams wmlp = getWindow().getAttributes();
        wmlp.gravity = Gravity.CENTER;
        wmlp.x = x;
        wmlp.y = y;
        show();
    }
    
    public void showAsDropDown(View view) {
        float density = context.getResources().getDisplayMetrics().density;
        WindowManager.LayoutParams wmlp = getWindow().getAttributes();
        int[] location = new int[2];
        view.getLocationInWindow(location);
        wmlp.gravity = Gravity.CENTER;
        wmlp.x = (location[0] + (int)((float)view.getWidth() / density));
        wmlp.y = (location[1] + (int)((float)view.getHeight() / density));
        show();
    }
}
