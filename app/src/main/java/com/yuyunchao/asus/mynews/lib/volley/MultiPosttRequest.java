package com.yuyunchao.asus.mynews.lib.volley;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.Response.ErrorListener;
import com.android.volley.toolbox.HttpHeaderParser;

public class MultiPosttRequest extends Request<String> {

	private MultipartEntity entity = new MultipartEntity();
	private final Listener<String> mListener;

	public MultiPosttRequest(String url, Listener<String> listener,
			ErrorListener errorListener) {
		super(Method.POST, url, errorListener);
		// TODO Auto-generated constructor stub
		mListener = listener;
	}

	/**
	 * �ϴ��ļ�
	 * 
	 * @param key
	 *            :����
	 * @param mFilePart
	 *            :�ļ�
	 */
	public void buildMultipartEntity(String key, File mFilePart) {
		entity.addFilePart(key, mFilePart);
	}
	

	
	/**
	 * �ϴ�String
	 * 
	 * @param key
	 *            :����
	 *            :�ļ�
	 */
	public void buildMultipartEntity(String key, String value) {
		entity.addStringPart(key, value);
	}

	@Override
	public String getBodyContentType() {
		return entity.getContentType().getValue();
	}

	@Override
	public byte[] getBody() throws AuthFailureError {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			entity.writeTo(bos);
		} catch (IOException e) {
			e.getStackTrace();
		}
		return bos.toByteArray();
	}

	@Override
	protected Response<String> parseNetworkResponse(NetworkResponse response) {
		
//		return Response.success("Uploaded", getCacheEntry());
	
		try {
			String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
			return Response.success(json, HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		}
	}

	@Override
	protected void deliverResponse(String response) {
		mListener.onResponse(response);
	}

}
