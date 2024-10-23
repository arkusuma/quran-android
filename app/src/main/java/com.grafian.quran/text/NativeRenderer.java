package com.grafian.quran.text;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class NativeRenderer {

	public static native void loadFont(byte[] blob);

	public static native int[] getTextExtent(String text, int fontSize);

	public static native Bitmap renderText(String text, int fontSize);

	public static void loadFont(InputStream is) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			int cc;
			while ((cc = is.read()) != -1) {
				os.write(cc);
			}
			loadFont(os.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static {
		System.loadLibrary("render");
	}
}
