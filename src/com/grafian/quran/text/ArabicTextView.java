package com.grafian.quran.text;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

public class ArabicTextView extends TextView {

	private static class Line {
		int start;
		int end;
		int width;
		int height;
		int ascend;
	}

	final private ArrayList<Line> mLayout = new ArrayList<Line>();
	final private Paint mPaint = new Paint();
	final private StringBuffer mBuffer = new StringBuffer();

	private String[] mWords = null;
	private int mTotalWidth;
	private int mLineHeight;

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
		line.start = realStart;
		line.end = end;
		line.width = goodExt[0];
		line.height = goodExt[1];
		line.ascend = (mLayout.size() * mLineHeight) + (goodExt[4] - goodExt[2]);
		mLayout.add(line);
		return end + 1;
	}

	private void createLayout(int width) {
		mLayout.clear();
		for (int start = 0; start < mWords.length;) {
			start = consumeLine(start, width);
		}
	}

	@Override
	public void setTextSize(float size) {
		super.setTextSize(size);
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
		return mLineHeight;
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
		mLineHeight = ext[3];

		if (widthMode == MeasureSpec.EXACTLY) {
			width = widthSize;
		} else {
			width = mTotalWidth + getCompoundPaddingLeft() + getCompoundPaddingRight();
			width = Math.max(width, getSuggestedMinimumWidth());
			if (widthMode == MeasureSpec.AT_MOST) {
				width = Math.min(width, widthSize);
			}
		}

		int usableWidth = width - getCompoundPaddingLeft() + getCompoundPaddingRight();
		createLayout(usableWidth);

		if (heightMode == MeasureSpec.EXACTLY) {
			height = heightSize;
		} else {
			height = mLayout.size() * mLineHeight + getCompoundPaddingTop() + getCompoundPaddingBottom();
			height = Math.max(height, getSuggestedMinimumHeight());
			if (heightMode == MeasureSpec.AT_MOST) {
				height = Math.min(height, heightSize);
			}
		}

		setMeasuredDimension(width, height);
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		int usableWidth = getWidth() - getCompoundPaddingLeft() - getCompoundPaddingRight();
		int usableHeight = getHeight() - getCompoundPaddingTop() - getCompoundPaddingBottom();
		int totalHeight = mLineHeight * mLayout.size();
		Rect clip = canvas.getClipBounds();
		for (Line line : mLayout) {
			int x = getCompoundPaddingLeft();
			int gravity = getGravity() & Gravity.HORIZONTAL_GRAVITY_MASK;
			if (gravity == Gravity.RIGHT) {
				x += usableWidth - line.width;
			} else if (gravity == Gravity.CENTER_HORIZONTAL) {
				x += (usableWidth - line.width) / 2;
			}

			int y = getCompoundPaddingTop() + line.ascend;
			gravity = getGravity() & Gravity.VERTICAL_GRAVITY_MASK;
			if (gravity == Gravity.BOTTOM) {
				y += usableHeight - totalHeight;
			} else if (gravity == Gravity.CENTER_VERTICAL) {
				y += (usableHeight - totalHeight) / 2;
			}

			if (!clip.intersects(x, y, x + line.width, y + line.height)) {
				continue;
			}

			String text = joinWords(mBuffer, line.start, line.end);
			Bitmap bitmap = Bitmap.createBitmap(line.width, line.height, Config.ALPHA_8);
			NativeRenderer.renderText(text, (int) getTextSize(), bitmap);

			mPaint.setColor(getCurrentTextColor());
			canvas.drawBitmap(bitmap, x, y, mPaint);
		}
	}
}
