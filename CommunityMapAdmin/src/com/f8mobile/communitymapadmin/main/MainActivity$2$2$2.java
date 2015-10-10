// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.community_app_admin.mobile;

import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import com.community_app_admin.mobile.asynctask.CommunityAppAsyncTaskHttpCall;
import com.community_app_admin.mobile.asynctask.CommunityAppGeocodingAsyncTask;
import com.community_app_admin.mobile.custom_views.PlaceHolder;
import com.community_app_admin.mobile.custom_views.PopupDialog;
import com.community_app_admin.mobile.model.UserModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// Referenced classes of package com.community_app_admin.mobile:
//            MainActivity

class val.searchPrompt
    implements android.view.r
{

    final licationContext this$2;
    private final AutoCompleteTextView val$searchAutoComplete;
    private final PopupDialog val$searchPopupDialog;
    private final View val$searchPrompt;

    public void onClick(View view)
    {
        view = val$searchAutoComplete.getText().toString();
        if (view.trim().isEmpty()) goto _L2; else goto _L1
_L1:
        val$searchAutoComplete.setText("");
        MainActivity.access$3(_fld0).clear();
        view = (LatLng)(new CommunityAppGeocodingAsyncTask(_fld0, MainActivity.access$7(_fld0))).execute(new String[] {
            view
        }).get();
        if (view == null) goto _L4; else goto _L3
_L3:
        MainActivity.access$3(_fld0).moveCamera(CameraUpdateFactory.newLatLngZoom(view, 15F));
        MainActivity.access$5(_fld0, true);
        MainActivity.access$6(_fld0, false);
_L2:
        val$searchPopupDialog.dismiss();
        val$searchPrompt.setVisibility(8);
        return;
_L4:
        Toast.makeText(getApplicationContext(), "No results found", 5000).show();
          goto _L2
        view;
_L5:
        view.printStackTrace();
        Toast.makeText(getApplicationContext(), "An error occurred..", 5000).show();
          goto _L2
        view;
          goto _L5
    }

    ialog()
    {
        this$2 = final_ialog;
        val$searchAutoComplete = autocompletetextview;
        val$searchPopupDialog = popupdialog;
        val$searchPrompt = View.this;
        super();
    }

    // Unreferenced inner class com/community_app_admin/mobile/MainActivity$2

/* anonymous class */
    class MainActivity._cls2
        implements android.view.View.OnClickListener
    {

        final MainActivity this$0;

        public void onClick(final View floatingMenuPrompt)
        {
            floatingMenuPrompt = getWindow().getLayoutInflater().inflate(0x7f030003, null);
            final PopupWindow floatingMenuPopupWindow = new PopupWindow(floatingMenuPrompt, -2, -2);
            ((Button)floatingMenuPrompt.findViewById(0x7f05000d)).setOnClickListener(new MainActivity._cls2._cls1());
            ((Button)floatingMenuPrompt.findViewById(0x7f05000e)).setOnClickListener(floatingMenuPrompt. new MainActivity._cls2._cls2());
            ((Button)floatingMenuPrompt.findViewById(0x7f05000f)).setOnClickListener(new MainActivity._cls2._cls3());
            ((Button)floatingMenuPrompt.findViewById(0x7f050010)).setOnClickListener(new MainActivity._cls2._cls4());
            ((Button)floatingMenuPrompt.findViewById(0x7f050011)).setOnClickListener(new MainActivity._cls2._cls5());
            floatingMenuPopupWindow.setBackgroundDrawable(new BitmapDrawable());
            floatingMenuPopupWindow.setOutsideTouchable(true);
            floatingMenuPopupWindow.setFocusable(true);
            floatingMenuPopupWindow.showAsDropDown(MainActivity.access$12(MainActivity.this), 85, -60);
            floatingMenuPopupWindow.setOnDismissListener(new MainActivity._cls2._cls6());
        }


            
            {
                this$0 = MainActivity.this;
                super();
            }

        // Unreferenced inner class com/community_app_admin/mobile/MainActivity$2$1

/* anonymous class */
        class MainActivity._cls2._cls1
            implements android.view.View.OnClickListener
        {

            final MainActivity._cls2 this$1;
            private final PopupWindow val$floatingMenuPopupWindow;
            private final View val$floatingMenuPrompt;

            public void onClick(View view)
            {
                floatingMenuPopupWindow.dismiss();
                floatingMenuPrompt.setVisibility(8);
                MainActivity.access$5(this$0, false);
                MainActivity.access$6(this$0, false);
                view = getWindow().getLayoutInflater().inflate(0x7f030006, null);
                saveLocation(1, view, MainActivity.access$4(this$0));
            }

                    
                    {
                        this$1 = MainActivity._cls2.this;
                        floatingMenuPopupWindow = popupwindow;
                        floatingMenuPrompt = view;
                        super();
                    }
        }


        // Unreferenced inner class com/community_app_admin/mobile/MainActivity$2$3

/* anonymous class */
        class MainActivity._cls2._cls3
            implements android.view.View.OnClickListener
        {

            final MainActivity._cls2 this$1;
            private final PopupWindow val$floatingMenuPopupWindow;
            private final View val$floatingMenuPrompt;

            public void onClick(View view)
            {
                MainActivity.access$3(this$0).clear();
                view = (new StringBuilder("method=3&user_id=")).append(MainActivity.access$8(this$0)).append("&latitude=").append(MainActivity.access$4(this$0).latitude).append("&longitude=").append(MainActivity.access$4(this$0).longitude).toString();
                view = (String)(new CommunityAppAsyncTaskHttpCall(MainActivity.access$7(this$0))).execute(new String[] {
                    view, "GET"
                }).get();
                if (!view.equalsIgnoreCase("null") && !view.trim().isEmpty()) goto _L2; else goto _L1
_L1:
                Toast.makeText(getApplicationContext(), "No nearby members found", 5000).show();
_L7:
                floatingMenuPopupWindow.dismiss();
                floatingMenuPrompt.setVisibility(8);
                return;
_L2:
                view = new JSONArray(view);
                if (view == null) goto _L4; else goto _L3
_L3:
                if (view.length() <= 0) goto _L4; else goto _L5
_L5:
                MainActivity.access$6(this$0, true);
                MainActivity.access$5(this$0, false);
                MainActivity.access$3(this$0).animateCamera(CameraUpdateFactory.newLatLngZoom(MainActivity.access$4(this$0), 14F));
                int i = 0;
_L8:
                if (i >= view.length()) goto _L7; else goto _L6
_L6:
                JSONObject jsonobject;
                Object obj;
                String s;
                jsonobject = view.getJSONObject(i);
                obj = jsonobject.getString("latitude");
                s = jsonobject.getString("longitude");
                break MISSING_BLOCK_LABEL_282;
_L4:
                Toast.makeText(getApplicationContext(), "No nearby members found", 5000).show();
                  goto _L7
                if (((String) (obj)).equalsIgnoreCase("null") || s.equalsIgnoreCase("null") || obj == null || s == null)
                {
                    break MISSING_BLOCK_LABEL_449;
                }
                try
                {
                    if (!((String) (obj)).trim().isEmpty() && !s.trim().isEmpty())
                    {
                        obj = new LatLng(Double.parseDouble(((String) (obj))), Double.parseDouble(s));
                        MainActivity.access$3(this$0).addMarker((new MarkerOptions()).title("nearbyMembers").position(((LatLng) (obj))).snippet(jsonobject.toString()));
                    }
                    break MISSING_BLOCK_LABEL_449;
                }
                // Misplaced declaration of an exception variable
                catch (View view) { }
                // Misplaced declaration of an exception variable
                catch (View view) { }
                // Misplaced declaration of an exception variable
                catch (View view) { }
                view.printStackTrace();
                Toast.makeText(getApplicationContext(), "An error occurred..", 5000).show();
                  goto _L7
                i++;
                  goto _L8
            }

                    
                    {
                        this$1 = MainActivity._cls2.this;
                        floatingMenuPopupWindow = popupwindow;
                        floatingMenuPrompt = view;
                        super();
                    }
        }


        // Unreferenced inner class com/community_app_admin/mobile/MainActivity$2$4

/* anonymous class */
        class MainActivity._cls2._cls4
            implements android.view.View.OnClickListener
        {

            final MainActivity._cls2 this$1;
            private final PopupWindow val$floatingMenuPopupWindow;
            private final View val$floatingMenuPrompt;

            public void onClick(View view)
            {
                MainActivity.access$3(this$0).clear();
                view = (new StringBuilder("method=2&user_id=")).append(MainActivity.access$8(this$0)).append("&latitude=").append(MainActivity.access$4(this$0).latitude).append("&longitude=").append(MainActivity.access$4(this$0).longitude).toString();
                view = (String)(new CommunityAppAsyncTaskHttpCall(MainActivity.access$7(this$0))).execute(new String[] {
                    view, "GET"
                }).get();
                if (!view.equalsIgnoreCase("null")) goto _L2; else goto _L1
_L1:
                Toast.makeText(getApplicationContext(), "No nearby members' establishment found", 5000).show();
_L7:
                floatingMenuPopupWindow.dismiss();
                floatingMenuPrompt.setVisibility(8);
                return;
_L2:
                view = new JSONArray(view);
                if (view == null) goto _L4; else goto _L3
_L3:
                if (view.length() <= 0) goto _L4; else goto _L5
_L5:
                MainActivity.access$6(this$0, true);
                MainActivity.access$5(this$0, false);
                MainActivity.access$3(this$0).animateCamera(CameraUpdateFactory.newLatLngZoom(MainActivity.access$4(this$0), 14F));
                int i = 0;
_L8:
                if (i >= view.length()) goto _L7; else goto _L6
_L6:
                JSONObject jsonobject;
                Object obj;
                String s;
                jsonobject = view.getJSONObject(i);
                obj = jsonobject.getString("latitude");
                s = jsonobject.getString("longitude");
                break MISSING_BLOCK_LABEL_272;
_L4:
                Toast.makeText(getApplicationContext(), "No nearby members' establishment found", 5000).show();
                  goto _L7
                if (((String) (obj)).equalsIgnoreCase("null") || s.equalsIgnoreCase("null") || obj == null || s == null)
                {
                    break MISSING_BLOCK_LABEL_439;
                }
                try
                {
                    if (!((String) (obj)).trim().isEmpty() && !s.trim().isEmpty())
                    {
                        obj = new LatLng(Double.parseDouble(((String) (obj))), Double.parseDouble(s));
                        MainActivity.access$3(this$0).addMarker((new MarkerOptions()).title("nearbyEstablishments").position(((LatLng) (obj))).snippet(jsonobject.toString()));
                    }
                    break MISSING_BLOCK_LABEL_439;
                }
                // Misplaced declaration of an exception variable
                catch (View view) { }
                // Misplaced declaration of an exception variable
                catch (View view) { }
                // Misplaced declaration of an exception variable
                catch (View view) { }
                view.printStackTrace();
                Toast.makeText(getApplicationContext(), "An error occurred..", 5000).show();
                  goto _L7
                i++;
                  goto _L8
            }

                    
                    {
                        this$1 = MainActivity._cls2.this;
                        floatingMenuPopupWindow = popupwindow;
                        floatingMenuPrompt = view;
                        super();
                    }
        }


        // Unreferenced inner class com/community_app_admin/mobile/MainActivity$2$5

/* anonymous class */
        class MainActivity._cls2._cls5
            implements android.view.View.OnClickListener
        {

            final MainActivity._cls2 this$1;
            private final PopupWindow val$floatingMenuPopupWindow;

            public void onClick(View view)
            {
                ArrayList arraylist;
                MainActivity.access$3(this$0).clear();
                MainActivity.access$9(this$0, new ArrayList());
                arraylist = new ArrayList();
                view = (String)(new CommunityAppAsyncTaskHttpCall(MainActivity.access$7(this$0))).execute(new String[] {
                    "method=8", "GET"
                }).get();
                if (view.equalsIgnoreCase("null") || view.trim().isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "No members found", 5000).show();
                    floatingMenuPopupWindow.dismiss();
                    MainActivity.access$10(this$0).setText("     0");
                    return;
                }
                JSONArray jsonarray = new JSONArray(view);
                if (jsonarray == null) goto _L2; else goto _L1
_L1:
                if (jsonarray.length() <= 0) goto _L2; else goto _L3
_L3:
                MainActivity.access$6(this$0, true);
                MainActivity.access$5(this$0, false);
                int i = 0;
_L14:
                if (i >= jsonarray.length())
                {
                    if (MainActivity.access$11(this$0).size() > 0)
                    {
                        MainActivity.access$3(this$0).animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(4D, 121D), 5F));
                    }
                    MainActivity.access$10(this$0).setText((new StringBuilder("     ")).append(MainActivity.access$11(this$0).size()).toString());
                    floatingMenuPopupWindow.dismiss();
                    return;
                }
                  goto _L4
_L12:
                view.printStackTrace();
                floatingMenuPopupWindow.dismiss();
                Toast.makeText(getApplicationContext(), "An error occurred..", 5000).show();
                return;
_L4:
                String s1;
                JSONObject jsonobject;
                jsonobject = jsonarray.getJSONObject(i);
                view = jsonobject.getString("latitude");
                s1 = jsonobject.getString("longitude");
                if (view.equalsIgnoreCase("null") || s1.equalsIgnoreCase("null") || view == null || s1 == null)
                {
                    break; /* Loop/switch isn't completed */
                }
                if (view.trim().isEmpty() || s1.trim().isEmpty())
                {
                    break; /* Loop/switch isn't completed */
                }
                String s;
                Object obj;
                obj = view;
                s = s1;
                if (arraylist.size() <= 0) goto _L6; else goto _L5
_L5:
                obj = arraylist.iterator();
                s = s1;
_L10:
                if (((Iterator) (obj)).hasNext()) goto _L8; else goto _L7
_L7:
                obj = view;
_L6:
                view = new LatLng(Double.parseDouble(((String) (obj))), Double.parseDouble(s));
                MainActivity.access$3(this$0).addMarker((new MarkerOptions()).title("nearbyMembers").position(view).snippet(jsonobject.toString()));
                view = new UserModel();
                view.setUserId(jsonobject.getString("id"));
                view.setLatitude(((String) (obj)));
                view.setLongitude(s);
                MainActivity.access$11(this$0).add(view);
                arraylist.add((new StringBuilder(String.valueOf(obj))).append(",").append(s).toString());
                Log.e("", (new StringBuilder("user: ")).append(view.getUserId()).append(", coordinates: ").append(view.getLatitude()).append(" , ").append(view.getLongitude()).toString());
                break; /* Loop/switch isn't completed */
_L8:
                if (((String)((Iterator) (obj)).next()).equalsIgnoreCase((new StringBuilder(String.valueOf(view))).append(",").append(s).toString()))
                {
                    view = Double.toString(Double.parseDouble(view) + Double.parseDouble(generateRandom()));
                    s = Double.toString(Double.parseDouble(s) + Double.parseDouble(generateRandom()));
                }
                if (true) goto _L10; else goto _L9
_L9:
                break; /* Loop/switch isn't completed */
_L2:
                try
                {
                    Toast.makeText(getApplicationContext(), "No members found", 5000).show();
                    floatingMenuPopupWindow.dismiss();
                    MainActivity.access$10(this$0).setText("     0");
                    return;
                }
                // Misplaced declaration of an exception variable
                catch (View view) { }
                // Misplaced declaration of an exception variable
                catch (View view) { }
                // Misplaced declaration of an exception variable
                catch (View view) { }
                if (true) goto _L12; else goto _L11
_L11:
                i++;
                if (true) goto _L14; else goto _L13
_L13:
            }

                    
                    {
                        this$1 = MainActivity._cls2.this;
                        floatingMenuPopupWindow = popupwindow;
                        super();
                    }
        }


        // Unreferenced inner class com/community_app_admin/mobile/MainActivity$2$6

/* anonymous class */
        class MainActivity._cls2._cls6
            implements android.widget.PopupWindow.OnDismissListener
        {

            final MainActivity._cls2 this$1;
            private final PopupWindow val$floatingMenuPopupWindow;
            private final View val$floatingMenuPrompt;

            public void onDismiss()
            {
                floatingMenuPopupWindow.dismiss();
                floatingMenuPrompt.setVisibility(8);
            }

                    
                    {
                        this$1 = MainActivity._cls2.this;
                        floatingMenuPopupWindow = popupwindow;
                        floatingMenuPrompt = view;
                        super();
                    }
        }

    }


    // Unreferenced inner class com/community_app_admin/mobile/MainActivity$2$2

/* anonymous class */
    class MainActivity._cls2._cls2
        implements android.view.View.OnClickListener
    {

        final MainActivity._cls2 this$1;
        private final PopupWindow val$floatingMenuPopupWindow;
        private final View val$floatingMenuPrompt;

        public void onClick(View view)
        {
            floatingMenuPopupWindow.dismiss();
            floatingMenuPrompt.setVisibility(8);
            view = getWindow().getLayoutInflater().inflate(0x7f030008, null);
            final PopupDialog searchPopupDialog = new PopupDialog(this$0);
            searchPopupDialog.setContentView(view);
            final AutoCompleteTextView searchAutoComplete = (AutoCompleteTextView)view.findViewById(0x7f050021);
            searchAutoComplete.setAdapter(new PlaceHolder(view.getContext(), 0x7f030004, 1));
            searchAutoComplete.setOnItemClickListener(new MainActivity._cls2._cls2._cls1());
            ((Button)view.findViewById(0x7f050022)).setOnClickListener(view. new MainActivity._cls2._cls2._cls2());
            ((Button)view.findViewById(0x7f050023)).setOnClickListener(new MainActivity._cls2._cls2._cls3());
            searchPopupDialog.show();
        }


            
            {
                this$1 = final__pcls2;
                floatingMenuPopupWindow = popupwindow;
                floatingMenuPrompt = View.this;
                super();
            }

        // Unreferenced inner class com/community_app_admin/mobile/MainActivity$2$2$1

/* anonymous class */
        class MainActivity._cls2._cls2._cls1
            implements android.widget.AdapterView.OnItemClickListener
        {

            final MainActivity._cls2._cls2 this$2;
            private final AutoCompleteTextView val$searchAutoComplete;

            public void onItemClick(AdapterView adapterview, View view, int i, long l)
            {
                searchAutoComplete.setText((String)adapterview.getItemAtPosition(i));
            }

                    
                    {
                        this$2 = MainActivity._cls2._cls2.this;
                        searchAutoComplete = autocompletetextview;
                        super();
                    }
        }


        // Unreferenced inner class com/community_app_admin/mobile/MainActivity$2$2$3

/* anonymous class */
        class MainActivity._cls2._cls2._cls3
            implements android.view.View.OnClickListener
        {

            final MainActivity._cls2._cls2 this$2;
            private final AutoCompleteTextView val$searchAutoComplete;

            public void onClick(View view)
            {
                searchAutoComplete.setText("");
            }

                    
                    {
                        this$2 = MainActivity._cls2._cls2.this;
                        searchAutoComplete = autocompletetextview;
                        super();
                    }
        }

    }

}
