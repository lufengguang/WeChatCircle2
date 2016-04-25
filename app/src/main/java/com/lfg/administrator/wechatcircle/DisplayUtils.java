package com.lfg.administrator.wechatcircle;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

public class DisplayUtils {
	/**
	 * 将px值转换为dp或dp值
	 */
	public static int px2dp(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 将dp或dp值转换为px值
	 */
	public static int dp2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 将px值转换为sp值
	 */
	public static int px2sp(Context context, float pxValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}

	/**
	 * 将sp值转换为px值
	 */
	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

	/**
	 * 将sp值转换为dp值
	 */
	public static int sp2dp(Context context, float spValue) {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		float scale = metrics.scaledDensity / metrics.density;
		return (int) (spValue / scale + 0.5f);
	}

	/**
	 * 将dp值转换为sp值
	 */
	public static int dp2sp(Context context, float dpValue) {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		float scale = metrics.scaledDensity / metrics.density;
		return (int) (dpValue * scale + 0.5f);
	}
	
	public static int getScreenWidthPixels(Activity activity) {
		 DisplayMetrics metric = new DisplayMetrics();
		 activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
		return metric.widthPixels;
	}
	
	public static int getScreenHeightPixels(Activity activity) {
		DisplayMetrics metric = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
		return metric.heightPixels;
	}
}
