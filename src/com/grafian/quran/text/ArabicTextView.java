package com.grafian.quran.text;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.Gravity;
import android.widget.TextView;

public class ArabicTextView extends TextView {

	private static class Line {
		int index;
		int start;
		int end;
		int width;
		int height;
		int ascend;
	}

	final private ArrayList<Line> mLayout = new ArrayList<Line>();
	final private Paint mPaint = new Paint();
	final private StringBuffer mBuffer = new StringBuffer();
	final private SparseArray<WeakReference<Bitmap>> mBitmaps = new SparseArray<WeakReference<Bitmap>>();

	private String[] mWords = null;
	private int mTopOverflow;
	private int mBottomOverflow;
	private int mFontHeight;
	private int mFontAscend;
	private int mTotalWidth;
	private int mTotalHeight;

	public ArabicTextView(Context context) {
		super(context);
		setGravity(Gravity.RIGHT);
	}

	public ArabicTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setGravity(Gravity.RIGHT);
	}

	private String joinWords(StringBuffer sb, int start, int end) {
		sb.setLength(0);
		for (int i = start; i <= end; i++) {
			if (i > start) {
				sb.append(" ");
			}
			sb.append(mWords[i]);
		}
		return sb.toString();
	}

	private int consumeLine(int start, int width) {
		int realStart = start;
		int end = mWords.length - 1;

		// Scan full words first
		String text = joinWords(mBuffer, start, end);
		int goodExt[] = NativeRenderer.getTextExtent(text, (int) getTextSize());
		if (width < goodExt[0] && start < end) {
			int badWidth = goodExt[0];
			int goodWidth = 0;
			end = end - 1;
			while (end > start) {
				// Guess mid using ratio
				int mid = (end - start) * (width - goodWidth) / (badWidth - goodWidth);
				mid += start + 1;

				text = joinWords(mBuffer, realStart, mid);
				int[] ext = NativeRenderer.getTextExtent(text, (int) getTextSize());
				if (width < ext[0]) {
					end = mid - 1;
					badWidth = ext[0];
				} else {
					start = mid;
					goodExt = ext;
					goodWidth = ext[0];
				}
			}
		}

		Line line = new Line();
		line.index = mLayout.size();
		line.start = realStart;
		line.end = end;
		line.width = goodExt[0];
		line.height = goodExt[1];
		line.ascend = mFontAscend - goodExt[2];
		mLayout.add(line);
		return end + 1;
	}

	private void createLayout(int width) {
		mLayout.clear();
		for (int start = 0; start < mWords.length;) {
			start = consumeLine(start, width);
		}

		// Calculate overflow
		mTopOverflow = 0;
		mBottomOverflow = 0;
		if (mLayout.size() > 0) {
			Line top = mLayout.get(0);
			if (top.ascend < 0) {
				mTopOverflow = -top.ascend;
			}
			Line bottom = mLayout.get(mLayout.size() - 1);
			if (bottom.ascend + bottom.height > mFontHeight) {
				mBottomOverflow = (bottom.ascend + bottom.height) - mFontHeight;
			}
		}
		mTotalHeight = mTopOverflow + mBottomOverflow + mFontHeight * mLayout.size();
	}

	@Override
	public void setTextSize(int unit, float size) {
		super.setTextSize(unit, size);
		requestLayout();
	}

	@Override
	public void setText(CharSequence text, BufferType type) {
		super.setText(text, type);
		mWords = text.toString().trim().split(" +");
		requestLayout();
	}

	@Override
	public int getLineHeight() {
		return mFontHeight;
	}

	@Override
	public int getLineCount() {
		return mLayout.size();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		int width;
		int height;

		String text = joinWords(mBuffer, 0, mWords.length - 1);
		int[] ext = NativeRenderer.getTextExtent(text, (int) getTextSize());
		mTotalWidth = ext[0];
		mFontHeight = ext[3];
		mFontAscend = ext[4];

		if (widthMode == MeasureSpec.EXACTLY) {
			width = widthSize;
		} else {
			width = mTotalWidth + getCompoundPaddingLeft() + getCompoundPaddingRight();
			width = Math.max(width, getSuggestedMinimumWidth());
			if (widthMode == MeasureSpec.AT_MOST) {
				width = Math.min(width, widthSize);
			}
		}

		int usableWidth = width - getCompoundPaddingLeft() - getCompoundPaddingRight();
		createLayout(usableWidth);

		if (heightMode == MeasureSpec.EXACTLY) {
			height = heightSize;
		} else {
			height = mTotalHeight + getCompoundPaddingTop() + getCompoundPaddingBottom();
			height = Math.max(height, getSuggestedMinimumHeight());
			if (heightMode == MeasureSpec.AT_MOST) {
				height = Math.min(height, heightSize);
			}
		}

		setMeasuredDimension(width, height);
	}

	private Bitmap getBitmapCache(int index, int width, int height) {
		WeakReference<Bitmap> ref = mBitmaps.get(index);
		Bitmap bitmap = ref != null ? ref.get() : null;
		if (bitmap == null || width > bitmap.getWidth() || height > bitmap.getHeight()) {
			bitmap = Bitmap.createBitmap(width, height, Config.ALPHA_8);
			mBitmaps.put(index, new WeakReference<Bitmap>(bitmap));
		}
		return bitmap;
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		int usableWidth = getWidth() - getCompoundPaddingLeft() - getCompoundPaddingRight();
		int usableHeight = getHeight() - getCompoundPaddingTop() - getCompoundPaddingBottom();
		int color = getCurrentTextColor();
		float[] matrix = {
				0, 0, 0, 0, Color.red(color),
				0, 0, 0, 0, Color.green(color),
				0, 0, 0, 0, Color.blue(color),
				0, 0, 0, 1, 0
		};
		mPaint.setColorFilter(new ColorMatrixColorFilter(matrix));
		Rect clip = canvas.getClipBounds();
		for (Line line : mLayout) {
			int x = getCompoundPaddingLeft();
			int gravity = getGravity() & Gravity.HORIZONTAL_GRAVITY_MASK;
			if (gravity == Gravity.RIGHT) {
				x += usableWidth - line.width;
			} else if (gravity == Gravity.CENTER_HORIZONTAL) {
				x += (usableWidth - line.width) / 2;
			}

			int y = getCompoundPaddingTop() + mTopOverflow + line.index * mFontHeight + line.ascend;
			gravity = getGravity() & Gravity.VERTICAL_GRAVITY_MASK;
			if (gravity == Gravity.BOTTOM) {
				y += usableHeight - mTotalHeight;
			} else if (gravity == Gravity.CENTER_VERTICAL) {
				y += (usableHeight - mTotalHeight) / 2;
			}

			if (!clip.intersects(x, y, x + line.width, y + line.height)) {
				continue;
			}

			String text = joinWords(mBuffer, line.start, line.end);
			Bitmap bitmap = getBitmapCache(line.index, line.width, line.height);
			NativeRenderer.renderText(text, (int) getTextSize(), bitmap);

			Rect src = new Rect(0, 0, line.width, line.height);
			Rect dst = new Rect(x, y, x + line.width, y + line.height);
			canvas.drawBitmap(bitmap, src, dst, mPaint);
		}
	}
}
