// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.community_app_admin.mobile;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

// Referenced classes of package com.community_app_admin.mobile:
//            MainActivity

class this._cls0
    implements com.google.android.gms.maps.ClickListener
{

    final MainActivity this$0;

    public void onMapClick(LatLng latlng)
    {
        if (latlng.latitude != MainActivity.access$4(MainActivity.this).latitude && latlng.longitude != MainActivity.access$4(MainActivity.this).longitude && MainActivity.access$14(MainActivity.this))
        {
            MainActivity.access$3(MainActivity.this).addMarker((new MarkerOptions()).position(latlng));
        }
    }

    s()
    {
        this$0 = MainActivity.this;
        super();
    }
}
