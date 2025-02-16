package com.smsgt.mobile.application.supertsuper_v3.util_helpers;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;

public class ParseHttpContent {

	public static String getASCIIContentFromEntity(HttpEntity entity) throws IllegalStateException, IOException {

		InputStream in = entity.getContent();
		StringBuffer out = new StringBuffer();
		int n = 1;
		while (n>0) {
		byte[] b = new byte[4096];
		n =  in.read(b);
		if (n>0) out.append(new String(b, 0, n));
		}
		return out.toString();
	}	
}
