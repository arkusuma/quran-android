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
		int yOffset;
	}

	final private ArrayList<Line> mLayout = new ArrayList<Line>();
	final private Paint mPaint = new Paint();
	private String[] mWords = null;
	private int mLineHeight;

	public ArabicTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setGravity(Gravity.RIGHT);
	}

	private void joinWords(StringBuffer sb, int start, int end) {
		sb.setLength(0);
		for (int i = start; i <= end; i++) {
			if (i > start) {
				sb.append(" ");
			}
			sb.append(mWords[i]);
		}
	}

	private int consumeLine(int start, int width) {
		Line line = new Line();
		line.start = start;

		StringBuffer sb = new StringBuffer();
		int end = mWords.length - 1;

		// Scan full words first
		joinWords(sb, start, end);
		int ext[] = NativeRenderer.getTextExtent(sb.toString(), (int) getTextSize());
		mLineHeight = ext[3];
		if (width < ext[0] && start < end) {
			int validExt[] = null;
			end = end - 1;
			while (end > start) {
				int mid = (start + end + 1) / 2;
				joinWords(sb, line.start, mid);
				ext = NativeRenderer.getTextExtent(sb.toString(), (int) getTextSize());
				if (width < ext[0]) {
					end = mid - 1;
				} else {
					start = mid;
					validExt = ext;
				}
			}
			if (validExt != null) {
				ext = validExt;
			}
		}

		line.end = end;
		line.width = ext[0];
		line.height = ext[1];
		line.yOffset = (mLayout.size() * ext[3]) + (ext[4] - ext[2]);
		mLayout.add(line);
		return end + 1;
	}

	private void createLayout(int width) {
		mLayout.clear();
		for (int start = 0; start < mWords.length;) {
			start = consumeLine(start, width);
		}
	}

	private void createDefaultPlan() {
		int width = getWidth() - getCompoundPaddingLeft() - getCompoundPaddingRight();
		createLayout(width);
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
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		createDefaultPlan();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		int width;
		int height;

		if (widthMode == MeasureSpec.EXACTLY) {
			width = widthSize;
		} else {
			int[] ext = NativeRenderer.getTextExtent(getText().toString(), (int) getTextSize());
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
			createLayout(usableWidth);
			height = mLayout.size() * mLineHeight;
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
		int usableWidth = getWidth() - getCompoundPaddingLeft() - getCompoundPaddingRight();
		int usableHeight = getHeight() - getCompoundPaddingTop() - getCompoundPaddingBottom();
		int totalHeight = mLineHeight * mLayout.size();
		StringBuffer sb = new StringBuffer();
		Rect clip = canvas.getClipBounds();
		for (Line line : mLayout) {
			int x = getCompoundPaddingLeft();
			int gravity = getGravity() & Gravity.HORIZONTAL_GRAVITY_MASK;
			if (gravity == Gravity.RIGHT) {
				x += usableWidth - line.width;
			} else if (gravity == Gravity.CENTER_HORIZONTAL) {
				x += (usableWidth - line.width) / 2;
			}

			int y = getCompoundPaddingTop() + line.yOffset;
			gravity = getGravity() & Gravity.VERTICAL_GRAVITY_MASK;
			if (gravity == Gravity.BOTTOM) {
				y += usableHeight - totalHeight;
			} else if (gravity == Gravity.CENTER_VERTICAL) {
				y += (usableHeight - totalHeight) / 2;
			}

			if (!clip.intersects(x, y, x + line.width, y + line.height)) {
				continue;
			}

			joinWords(sb, line.start, line.end);
			Bitmap bitmap = Bitmap.createBitmap(line.width, line.height, Config.ALPHA_8);
			NativeRenderer.renderText(sb.toString(), (int) getTextSize(), bitmap);

			mPaint.setColor(getCurrentTextColor());
			canvas.drawBitmap(bitmap, x, y, mPaint);
		}
	}
}
