// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.community_app_admin.mobile;

import android.net.Uri;
import android.os.AsyncTask;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;
import com.community_app_admin.mobile.asynctask.CommunityAppAsyncTaskHttpCall;
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

class pDialog
    implements android.view.r
{

    final is._cls0 this$1;
    private final AutoCompleteTextView val$searchCountryAutoComplete;
    private final PopupDialog val$searchPopupDialog;

    public boolean onKey(View view, int i, KeyEvent keyevent)
    {
        if (keyevent.getAction() != 0 || i != 66 || val$searchCountryAutoComplete.getText().toString().trim().isEmpty()) goto _L2; else goto _L1
_L1:
        ArrayList arraylist;
        view = (new StringBuilder("method=7&country=")).append(Uri.encode(val$searchCountryAutoComplete.getText().toString())).toString();
        MainActivity.access$3(_fld0).clear();
        MainActivity.access$9(_fld0, new ArrayList());
        arraylist = new ArrayList();
        view = (String)(new CommunityAppAsyncTaskHttpCall(MainActivity.access$7(_fld0))).execute(new String[] {
            view, "GET"
        }).get();
        if (!view.equalsIgnoreCase("null") && !view.trim().isEmpty()) goto _L4; else goto _L3
_L3:
        Toast.makeText(getApplicationContext(), "No members found", 5000).show();
        val$searchCountryAutoComplete.setText("");
        val$searchPopupDialog.dismiss();
        MainActivity.access$10(_fld0).setText("     0");
          goto _L5
_L4:
        JSONArray jsonarray = new JSONArray(view);
        if (jsonarray == null) goto _L7; else goto _L6
_L6:
        if (jsonarray.length() <= 0) goto _L7; else goto _L8
_L8:
        MainActivity.access$6(_fld0, true);
        MainActivity.access$5(_fld0, false);
        i = 0;
_L22:
        if (i < jsonarray.length()) goto _L10; else goto _L9
_L9:
        if (MainActivity.access$11(_fld0).size() > 0)
        {
            double d = Double.parseDouble(((UserModel)MainActivity.access$11(_fld0).get(MainActivity.access$11(_fld0).size() - 1)).getLatitude());
            double d1 = Double.parseDouble(((UserModel)MainActivity.access$11(_fld0).get(MainActivity.access$11(_fld0).size() - 1)).getLongitude());
            MainActivity.access$3(_fld0).animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(d, d1), 5F));
        }
        MainActivity.access$10(_fld0).setText((new StringBuilder("     ")).append(MainActivity.access$11(_fld0).size()).toString());
        val$searchCountryAutoComplete.setText("");
        val$searchPopupDialog.dismiss();
          goto _L5
        view;
_L20:
        view.printStackTrace();
        val$searchCountryAutoComplete.setText("");
        val$searchPopupDialog.dismiss();
        Toast.makeText(getApplicationContext(), "An error occurred..", 5000).show();
          goto _L5
_L10:
        String s;
        JSONObject jsonobject;
        jsonobject = jsonarray.getJSONObject(i);
        view = jsonobject.getString("latitude");
        s = jsonobject.getString("longitude");
        if (view.equalsIgnoreCase("null") || s.equalsIgnoreCase("null") || view == null || s == null) goto _L12; else goto _L11
_L11:
        if (view.trim().isEmpty() || s.trim().isEmpty()) goto _L12; else goto _L13
_L13:
        Object obj;
        obj = view;
        keyevent = s;
        if (arraylist.size() <= 0) goto _L15; else goto _L14
_L14:
        obj = arraylist.iterator();
        keyevent = s;
_L19:
        if (((Iterator) (obj)).hasNext()) goto _L17; else goto _L16
_L16:
        obj = view;
_L15:
        view = new LatLng(Double.parseDouble(((String) (obj))), Double.parseDouble(keyevent));
        MainActivity.access$3(_fld0).addMarker((new MarkerOptions()).title("nearbyMembers").position(view).snippet(jsonobject.toString()));
        view = new UserModel();
        view.setUserId(jsonobject.getString("id"));
        view.setLatitude(((String) (obj)));
        view.setLongitude(keyevent);
        MainActivity.access$11(_fld0).add(view);
        arraylist.add((new StringBuilder(String.valueOf(obj))).append(",").append(keyevent).toString());
        Log.e("", (new StringBuilder("user: ")).append(view.getUserId()).append(", coordinates: ").append(view.getLatitude()).append(" , ").append(view.getLongitude()).toString());
          goto _L12
_L17:
        if (!((String)((Iterator) (obj)).next()).equalsIgnoreCase((new StringBuilder(String.valueOf(view))).append(",").append(keyevent).toString())) goto _L19; else goto _L18
_L18:
        view = Double.toString(Double.parseDouble(view) + Double.parseDouble(generateRandom()));
        keyevent = Double.toString(Double.parseDouble(keyevent) + Double.parseDouble(generateRandom()));
          goto _L19
_L7:
        Toast.makeText(getApplicationContext(), "No members found", 5000).show();
        val$searchCountryAutoComplete.setText("");
        val$searchPopupDialog.dismiss();
        MainActivity.access$10(_fld0).setText("     0");
          goto _L5
        view;
          goto _L20
_L2:
        return false;
        view;
          goto _L20
_L5:
        return true;
_L12:
        i++;
        if (true) goto _L22; else goto _L21
_L21:
    }

    pDialog()
    {
        this$1 = final_pdialog;
        val$searchCountryAutoComplete = autocompletetextview;
        val$searchPopupDialog = PopupDialog.this;
        super();
    }

    // Unreferenced inner class com/community_app_admin/mobile/MainActivity$3

/* anonymous class */
    class MainActivity._cls3
        implements android.view.View.OnClickListener
    {

        final MainActivity this$0;

        public void onClick(View view)
        {
            view = getWindow().getLayoutInflater().inflate(0x7f030007, null);
            final PopupDialog searchPopupDialog = new PopupDialog(MainActivity.this);
            searchPopupDialog.setContentView(view);
            final AutoCompleteTextView searchCountryAutoComplete = (AutoCompleteTextView)searchPopupDialog.findViewById(0x7f05001f);
            searchCountryAutoComplete.setAdapter(new PlaceHolder(view.getContext(), 0x7f030004, 2, MainActivity.access$13(MainActivity.this)));
            searchCountryAutoComplete.setOnItemClickListener(new MainActivity._cls3._cls1());
            searchCountryAutoComplete.setOnKeyListener(searchPopupDialog. new MainActivity._cls3._cls2());
            searchPopupDialog.showAtLocation(5, 100);
        }


            
            {
                this$0 = MainActivity.this;
                super();
            }

        // Unreferenced inner class com/community_app_admin/mobile/MainActivity$3$1

/* anonymous class */
        class MainActivity._cls3._cls1
            implements android.widget.AdapterView.OnItemClickListener
        {

            final MainActivity._cls3 this$1;
            private final AutoCompleteTextView val$searchCountryAutoComplete;
            private final PopupDialog val$searchPopupDialog;

            public void onItemClick(AdapterView adapterview, View view, int i, long l)
            {
                ArrayList arraylist;
                searchCountryAutoComplete.setText((String)adapterview.getItemAtPosition(i));
                adapterview = (new StringBuilder("method=7&country=")).append(Uri.encode((String)adapterview.getItemAtPosition(i))).toString();
                MainActivity.access$3(this$0).clear();
                MainActivity.access$9(this$0, new ArrayList());
                arraylist = new ArrayList();
                adapterview = (String)(new CommunityAppAsyncTaskHttpCall(MainActivity.access$7(this$0))).execute(new String[] {
                    adapterview, "GET"
                }).get();
                if (adapterview.equalsIgnoreCase("null") || adapterview.trim().isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "No members found", 5000).show();
                    searchCountryAutoComplete.setText("");
                    searchPopupDialog.dismiss();
                    MainActivity.access$10(this$0).setText("     0");
                    return;
                }
                JSONArray jsonarray = new JSONArray(adapterview);
                if (jsonarray == null) goto _L2; else goto _L1
_L1:
                if (jsonarray.length() <= 0) goto _L2; else goto _L3
_L3:
                MainActivity.access$6(this$0, true);
                MainActivity.access$5(this$0, false);
                i = 0;
_L14:
                if (i >= jsonarray.length())
                {
                    if (MainActivity.access$11(this$0).size() > 0)
                    {
                        double d = Double.parseDouble(((UserModel)MainActivity.access$11(this$0).get(MainActivity.access$11(this$0).size() - 1)).getLatitude());
                        double d1 = Double.parseDouble(((UserModel)MainActivity.access$11(this$0).get(MainActivity.access$11(this$0).size() - 1)).getLongitude());
                        MainActivity.access$3(this$0).animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(d, d1), 5F));
                    }
                    MainActivity.access$10(this$0).setText((new StringBuilder("     ")).append(MainActivity.access$11(this$0).size()).toString());
                    searchCountryAutoComplete.setText("");
                    searchPopupDialog.dismiss();
                    return;
                }
                  goto _L4
_L12:
                adapterview.printStackTrace();
                searchCountryAutoComplete.setText("");
                searchPopupDialog.dismiss();
                Toast.makeText(getApplicationContext(), "An error occurred..", 5000).show();
                return;
_L4:
                String s;
                JSONObject jsonobject;
                jsonobject = jsonarray.getJSONObject(i);
                adapterview = jsonobject.getString("latitude");
                s = jsonobject.getString("longitude");
                if (adapterview.equalsIgnoreCase("null") || s.equalsIgnoreCase("null") || adapterview == null || s == null)
                {
                    break; /* Loop/switch isn't completed */
                }
                if (adapterview.trim().isEmpty() || s.trim().isEmpty())
                {
                    break; /* Loop/switch isn't completed */
                }
                Object obj;
                obj = adapterview;
                view = s;
                if (arraylist.size() <= 0) goto _L6; else goto _L5
_L5:
                obj = arraylist.iterator();
                view = s;
_L10:
                if (((Iterator) (obj)).hasNext()) goto _L8; else goto _L7
_L7:
                obj = adapterview;
_L6:
                adapterview = new LatLng(Double.parseDouble(((String) (obj))), Double.parseDouble(view));
                MainActivity.access$3(this$0).addMarker((new MarkerOptions()).title("nearbyMembers").position(adapterview).snippet(jsonobject.toString()));
                adapterview = new UserModel();
                adapterview.setUserId(jsonobject.getString("id"));
                adapterview.setLatitude(((String) (obj)));
                adapterview.setLongitude(view);
                MainActivity.access$11(this$0).add(adapterview);
                arraylist.add((new StringBuilder(String.valueOf(obj))).append(",").append(view).toString());
                Log.e("", (new StringBuilder("user: ")).append(adapterview.getUserId()).append(", coordinates: ").append(adapterview.getLatitude()).append(" , ").append(adapterview.getLongitude()).toString());
                break; /* Loop/switch isn't completed */
_L8:
                if (((String)((Iterator) (obj)).next()).equalsIgnoreCase((new StringBuilder(String.valueOf(adapterview))).append(",").append(view).toString()))
                {
                    adapterview = Double.toString(Double.parseDouble(adapterview) + Double.parseDouble(generateRandom()));
                    view = Double.toString(Double.parseDouble(view) + Double.parseDouble(generateRandom()));
                }
                if (true) goto _L10; else goto _L9
_L9:
                break; /* Loop/switch isn't completed */
_L2:
                try
                {
                    Toast.makeText(getApplicationContext(), "No members found", 5000).show();
                    searchCountryAutoComplete.setText("");
                    searchPopupDialog.dismiss();
                    MainActivity.access$10(this$0).setText("     0");
                    return;
                }
                // Misplaced declaration of an exception variable
                catch (AdapterView adapterview) { }
                // Misplaced declaration of an exception variable
                catch (AdapterView adapterview) { }
                // Misplaced declaration of an exception variable
                catch (AdapterView adapterview) { }
                if (true) goto _L12; else goto _L11
_L11:
                i++;
                if (true) goto _L14; else goto _L13
_L13:
            }

                    
                    {
                        this$1 = MainActivity._cls3.this;
                        searchCountryAutoComplete = autocompletetextview;
                        searchPopupDialog = popupdialog;
                        super();
                    }
        }

    }

}
