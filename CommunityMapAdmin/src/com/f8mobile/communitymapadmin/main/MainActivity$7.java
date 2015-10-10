// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.community_app_admin.mobile;

import android.view.View;
import android.widget.PopupWindow;

// Referenced classes of package com.community_app_admin.mobile:
//            MainActivity

class val.savePrompt
    implements android.view.tener
{

    final MainActivity this$0;
    private final PopupWindow val$savePopupWindow;
    private final View val$savePrompt;

    public void onClick(View view)
    {
        val$savePopupWindow.dismiss();
        val$savePrompt.setVisibility(8);
    }

    ()
    {
        this$0 = final_mainactivity;
        val$savePopupWindow = popupwindow;
        val$savePrompt = View.this;
        super();
    }
}
