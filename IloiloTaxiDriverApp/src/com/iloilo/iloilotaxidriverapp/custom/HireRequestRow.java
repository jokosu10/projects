package com.iloilo.iloilotaxidriverapp.custom;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.iloilo.iloilotaxidriverapp.R;
import com.iloilo.iloilotaxidriverapp.asynctask.IloiloAsyncTaskHttpCall;
import com.iloilo.iloilotaxidriverapp.model.HireRequestModel;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class HireRequestRow implements HireRequestInterface {
	
    private HireRequestModel hireRequestModel;
    private LayoutInflater layoutInflater;
    private Context contxt;
    
    public HireRequestRow(HireRequestModel h, LayoutInflater l, Context c) {
        this.hireRequestModel = h;
        this.layoutInflater = l;
        this.contxt = c;
    }
    
    @SuppressLint("InflateParams")
	@Override
    public View getView(View convertView, int pos) {
    	
    	View view = null;
    	ViewHolder holder = null;
        ViewGroup viewGroup = (ViewGroup)layoutInflater.inflate(R.layout.hire_request_row, null, false);
        ImageView iconImgView = (ImageView) viewGroup.findViewById(R.id.iconTaxi);
        TextView addrr = (TextView) viewGroup.findViewById(R.id.textViewHireAddress);
        holder = new ViewHolder(iconImgView, addrr);
        viewGroup.setTag(holder);
        view = viewGroup;

        holder.getImgView().setBackground(contxt.getResources().getDrawable(R.drawable.caricon));
        holder.getAddressTv().setText(hireRequestModel.getAddress());
        
        view.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
            	AlertDialog.Builder builder = new AlertDialog.Builder(contxt);
                builder.setMessage("Accept this hire request?")
                .setCancelable(false)
                .setTitle("Accept Request Dialog")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                   
                    public void onClick(DialogInterface dialog, int id) {
                        
                    	String[] params = new String[2];
                        params[0] = "method=5";
                        params[1] = "POST";
                        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                        nameValuePairs.add(new BasicNameValuePair("registration_id", hireRequestModel.getGcmRegId()));
                        new IloiloAsyncTaskHttpCall(nameValuePairs).execute(params);
                        
                        Log.e("", "-----" + hireRequestModel.getGcmRegId());
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                   
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        
                       /* String[] params = new String[2];
                        params[0] = "method=8";
                        params[1] = "POST";
                        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                        nameValuePairs.add(new BasicNameValuePair("request_id", hireRequestModel.getRequestId()));
                        new IloiloAsyncTaskHttpCall(nameValuePairs).execute(params); */
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
			}
		});
        
        
        return view;
    }
    
    @Override
    public int getViewType() {
        return 0;
    }
    
    class ViewHolder {
    	
    	private ImageView imgView;
    	private TextView addressTv;
    	
    	public ViewHolder(ImageView i, TextView t) {
    		setImgView(i);
    		setAddressTv(t);
    	}

		public TextView getAddressTv() {
			return addressTv;
		}

		public void setAddressTv(TextView addressTv) {
			this.addressTv = addressTv;
		}

		public ImageView getImgView() {
			return imgView;
		}

		public void setImgView(ImageView imgView) {
			this.imgView = imgView;
		}
    	
    	
    }
    
}
