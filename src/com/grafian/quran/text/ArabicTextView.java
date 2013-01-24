package com.grafian.quran.text;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

public class ArabicTextView extends TextView {

	public ArabicTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setGravity(Gravity.RIGHT);
	}

	private boolean mNativeRenderer = true;
	final private Paint mPaint = new Paint();

	public boolean isNativeRenderer() {
		return mNativeRenderer;
	}

	public void setNativeRenderer(boolean nativeRenderer) {
		if (mNativeRenderer != nativeRenderer) {
			mNativeRenderer = nativeRenderer;
			requestLayout();
			invalidate();
		}
	}

	private int getFontSize() {
		float scaledDensity = getContext().getResources().getDisplayMetrics().scaledDensity;
		return (int) (getTextSize() * scaledDensity);
	}

	@Override
	public int getLineHeight() {
		if (!mNativeRenderer) {
			return super.getLineHeight();
		}

		int[] ext = NativeRenderer.getTextExtent("M", getFontSize());
		return ext[2];
	}

	@Override
	public int getLineCount() {
		if (!mNativeRenderer) {
			return super.getLineHeight();
		}

		int usableWidth = getWidth() - getCompoundPaddingLeft() - getCompoundPaddingRight();
		int ext[] = measureText(usableWidth);
		return ext[2];
	}

	private int[] measureText(int width) {
		int w = 0;
		int h = 0;
		int lines = 0;

		String text = getText().toString().trim();
		String[] words = text.split(" +");
		int[] lastExt = null;
		int start = 0;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < words.length; i++) {
			if (i == start) {
				sb.setLength(0);
			} else {
				sb.append(" ");
			}
			sb.append(words[i]);
			if (width == 0 && i != words.length - 1) {
				continue;
			}
			boolean full = false;
			int[] ext = NativeRenderer.getTextExtent(sb.toString(), getFontSize());
			if (width < ext[0]) {
				full = true;
				if (i == start) {
					start = i + 1;
				} else {
					start = i;
					ext = lastExt;
					i--;
				}
			} else if (i == words.length - 1) {
				full = true;
			}
			if (full) {
				w = Math.max(w, ext[0]);
				h += ext[3];
				lines++;
			}
			lastExt = ext;
		}

		int[] result = new int[3];
		result[0] = w;
		result[1] = h;
		result[2] = lines;
		return result;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (!mNativeRenderer) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			return;
		}

		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		int width;
		int height;

		if (widthMode == MeasureSpec.EXACTLY) {
			width = widthSize;
		} else {
			int[] ext = NativeRenderer.getTextExtent(getText().toString(), getFontSize());
			width = ext[0];
			width += getCompoundPaddingLeft() + getCompoundPaddingRight();
			width = Math.max(width, getSuggestedMinimumWidth());
			if (widthMode == MeasureSpec.AT_MOST) {
				width = Math.min(widthSize, width);
			}
		}

		if (heightMode == MeasureSpec.EXACTLY) {
			height = heightSize;
		} else {
			int usableWidth = width -= getCompoundPaddingLeft() + getCompoundPaddingRight();
			int[] ext = measureText(usableWidth);
			height = ext[1];
			height += getCompoundPaddingTop() + getCompoundPaddingBottom();
			if (heightMode == MeasureSpec.AT_MOST) {
				height = Math.min(height, heightSize);
			}
		}

		setMeasuredDimension(width, height);
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		if (!mNativeRenderer) {
			super.onDraw(canvas);
			return;
		}

		int usableWidth = getWidth() - getCompoundPaddingLeft() - getCompoundPaddingRight();
		int usableHeight = getHeight() - getCompoundPaddingTop() - getCompoundPaddingBottom();
		int[] ext = measureText(usableWidth);
		int totalHeight = ext[1];

		String[] words = getText().toString().split(" +");
		int line = 0;
		int[] lastExt = null;
		int start = 0;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < words.length; i++) {
			if (i == start) {
				sb.setLength(0);
			} else {
				sb.append(" ");
			}
			sb.append(words[i]);
			boolean full = false;
			ext = NativeRenderer.getTextExtent(sb.toString(), getFontSize());
			if (usableWidth < ext[0]) {
				full = true;
				if (i == start) {
					start = i + 1;
				} else {
					start = i;
					ext = lastExt;
					sb.setLength(sb.length() - words[i].length() - 1);
					i--;
				}
			} else if (i == words.length - 1) {
				full = true;
			}
			if (full) {
				Bitmap bitmap = Bitmap.createBitmap(ext[0], ext[1], Config.ALPHA_8);
				NativeRenderer.renderText(sb.toString(), getFontSize(), bitmap);

				int x = getCompoundPaddingLeft();
				if ((getGravity() & Gravity.RIGHT) == Gravity.RIGHT) {
					x += usableWidth - ext[0];
				} else if ((getGravity() & Gravity.CENTER_HORIZONTAL) == Gravity.CENTER_HORIZONTAL) {
					x += (usableWidth - ext[0]) / 2;
				}

				int y = getCompoundPaddingTop() + line * ext[3];
				if ((getGravity() & Gravity.BOTTOM) == Gravity.BOTTOM) {
					y += usableHeight - totalHeight;
				} else if ((getGravity() & Gravity.CENTER_VERTICAL) == Gravity.CENTER_VERTICAL) {
					y += (usableHeight - totalHeight) / 2;
				}
				y += ext[4] - ext[2];

				mPaint.setColor(getCurrentTextColor());
				canvas.drawBitmap(bitmap, x, y, mPaint);

				line++;
			}
			lastExt = ext;
		}
	}
}
