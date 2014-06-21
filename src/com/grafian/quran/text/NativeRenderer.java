package com.grafian.quran.text;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;

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

	public static void loadFont(String path) {
		try {
			FileInputStream is = new FileInputStream(path);
			loadFont(is);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	static {
		System.loadLibrary("render");
	}
}
