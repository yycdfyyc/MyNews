package com.yuyunchao.asus.mynews.lib.volley;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.message.BasicHeader;

import android.text.TextUtils;

public class MultipartEntity implements HttpEntity {
	private final static char[] MULTIPART_CHARS = "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
			.toCharArray();
	/**
	 * 锟斤拷锟叫凤拷
	 */
	private final String NEW_LINE_STR = "\r\n";
	private final String CONTENT_TYPE = "Content-Type: ";
	private final String CONTENT_DISPOSITION = "Content-Disposition: ";
	/**
	 * 锟侥憋拷锟斤拷锟斤拷锟斤拷锟街凤拷锟斤拷
	 */
	private final String TYPE_TEXT_CHARSET = "text/plain; charset=UTF-8";

	/**
	 * 锟街斤拷锟斤拷锟斤拷锟斤拷
	 */
	private final String TYPE_OCTET_STREAM = "application/octet-stream";
	/**
	 * 锟斤拷锟斤拷锟狡诧拷锟斤拷
	 */
	private final byte[] BINARY_ENCODING = "Content-Transfer-Encoding: binary\r\n\r\n"
			.getBytes();
	/**
	 * 锟侥憋拷锟斤拷锟斤拷
	 */
	private final byte[] BIT_ENCODING = "Content-Transfer-Encoding: 8bit\r\n\r\n"
			.getBytes();

	/**
	 * 锟街革拷锟斤拷
	 */
	private String mBoundary = null;
	/**
	 * 锟斤拷锟斤拷锟?	 */
	ByteArrayOutputStream mOutputStream = new ByteArrayOutputStream();

	public MultipartEntity() {
		this.mBoundary = generateBoundary();
	}

	 /**
     * 锟斤拷锟缴分革拷锟斤拷
     * 
     * @return
     */
    private final String generateBoundary() {
        final StringBuffer buf = new StringBuffer();
        final Random rand = new Random();
        for (int i = 0; i < 30; i++) {
            buf.append(MULTIPART_CHARS[rand.nextInt(MULTIPART_CHARS.length)]);
        }
        return buf.toString();
    }
 
    /**
     * 锟斤拷锟斤拷锟斤拷头锟侥分革拷锟斤拷
     * 
     * @throws IOException
     */
    private void writeFirstBoundary() throws IOException {
        mOutputStream.write(("--" + mBoundary + "\r\n").getBytes());
    }
 
    /**
     * 锟斤拷锟斤拷谋锟斤拷锟斤拷锟?     * 
     * @param value
     */
    public void addStringPart(final String paramName, final String value) {
        writeToOutputStream(paramName, value.getBytes(), TYPE_TEXT_CHARSET, BIT_ENCODING, "");
    }
 
    /**
     * 锟斤拷锟斤拷锟斤拷写锟诫到锟斤拷锟斤拷锟斤拷锟?     * 
     * @param rawData
     * @param type
     * @param encodingBytes
     * @param fileName
     */
    private void writeToOutputStream(String paramName, byte[] rawData, String type,
            byte[] encodingBytes,
            String fileName) {
        try {
            writeFirstBoundary();
            mOutputStream.write((CONTENT_TYPE + type + NEW_LINE_STR).getBytes());
            mOutputStream
                    .write(getContentDispositionBytes(paramName, fileName));
            mOutputStream.write(encodingBytes);
            mOutputStream.write(rawData);
            mOutputStream.write(NEW_LINE_STR.getBytes());
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
 
    /**
     * 锟斤拷佣锟斤拷锟斤拷撇锟斤拷锟? 锟斤拷锟斤拷Bitmap锟斤拷锟街斤拷锟斤拷锟斤拷锟斤拷
     * 
     * @param rawData
     */
    public void addBinaryPart(String paramName, final byte[] rawData) {
        writeToOutputStream(paramName, rawData, TYPE_OCTET_STREAM, BINARY_ENCODING, "no-file");
    }
 
    /**
     * 锟斤拷锟斤拷募锟斤拷锟斤拷锟?锟斤拷锟斤拷实锟斤拷锟侥硷拷锟较达拷锟斤拷锟斤拷
     * 
     * @param key
     * @param file
     */
    public void addFilePart(final String key, final File file) {
        InputStream fin = null;
        try {
            fin = new FileInputStream(file);
            writeFirstBoundary();
            final String type = CONTENT_TYPE + TYPE_OCTET_STREAM + NEW_LINE_STR;
            mOutputStream.write(getContentDispositionBytes(key, file.getName()));
            mOutputStream.write(type.getBytes());
            mOutputStream.write(BINARY_ENCODING);
 
            final byte[] tmp = new byte[4096];
            int len = 0;
            while ((len = fin.read(tmp)) != -1) {
                mOutputStream.write(tmp, 0, len);
            }
            mOutputStream.flush();
        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            closeSilently(fin);
        }
    }
    
    
 
    private void closeSilently(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
 
    private byte[] getContentDispositionBytes(String paramName, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(CONTENT_DISPOSITION + "form-data; name=\"" + paramName + "\"");
        // 锟侥憋拷锟斤拷锟斤拷没锟斤拷filename锟斤拷锟斤拷,锟斤拷锟斤拷为锟秸硷拷锟斤拷
        if (!TextUtils.isEmpty(fileName)) {
            stringBuilder.append("; filename=\""
                    + fileName + "\"");
        }
 
        return stringBuilder.append(NEW_LINE_STR).toString().getBytes();
    }
 
    @Override
    public long getContentLength() {
        return mOutputStream.toByteArray().length;
    }
 
    @Override
    public Header getContentType() {
        return new BasicHeader("Content-Type", "multipart/form-data; boundary=" + mBoundary);
    }
 
    @Override
    public boolean isChunked() {
        return false;
    }
 
    @Override
    public boolean isRepeatable() {
        return false;
    }
 
    @Override
    public boolean isStreaming() {
        return false;
    }
 
    @Override
    public void writeTo(final OutputStream outstream) throws IOException {
    	
    	final String endString = "\r\n--" + mBoundary + "--";    
    	mOutputStream.write(endString.getBytes());
        outstream.write(mOutputStream.toByteArray());
    }
 
    @Override
    public Header getContentEncoding() {
        return null;
    }
 
    @Override
    public void consumeContent() throws IOException,
            UnsupportedOperationException {
        if (isStreaming()) {
            throw new UnsupportedOperationException(
                    "Streaming entity does not implement #consumeContent()");
        }
    }
 
    @Override
    public InputStream getContent() {
        return new ByteArrayInputStream(mOutputStream.toByteArray());
    }

}
