package com.f8mobile.community_app.mobile.custom;

import java.util.ArrayList;
import java.util.List;

import com.f8mobile.community_app.mobile.model.ChatMessage;
import com.f8mobile.f8mobile.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ChatArrayAdapter extends ArrayAdapter<ChatMessage> {

	private TextView chatText;
	private TextView chatTimestamp;
	private List<ChatMessage> chatMessageList = new ArrayList<ChatMessage>();
	private LinearLayout singleMessageContainer;

	@Override
	public void add(ChatMessage object) {
		chatMessageList.add(object);
		super.add(object);
	}

	public ChatArrayAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}

	public int getCount() {
		return this.chatMessageList.size();
	}

	public ChatMessage getItem(int index) {
		return this.chatMessageList.get(index);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.activity_chat_singlemessage, parent, false);
		}
		singleMessageContainer = (LinearLayout) row.findViewById(R.id.singleMessageContainer);
		ChatMessage chatMessageObj = getItem(position);
		chatText = (TextView) row.findViewById(R.id.singleMessage);
		chatTimestamp = (TextView) row.findViewById(R.id.messageTimestamp);
		chatText.setText(chatMessageObj.getMessage());
		chatText.setBackgroundResource(chatMessageObj.isLeft() ? R.drawable.bubble_b : R.drawable.bubble_a);
		chatTimestamp.setText(chatMessageObj.getTimestamp());
		singleMessageContainer.setGravity(chatMessageObj.isLeft() ? Gravity.LEFT : Gravity.RIGHT);
		return row;
	}

	public Bitmap decodeToBitmap(byte[] decodedByte) {
		return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
	}

}