package com.grafian.quran;

import org.amr.arabic.ArabicUtilities;

public class ArabicShaper {
	public static String shape(String s) {
		return ArabicUtilities.reshapeSentence(s);
	}
}
