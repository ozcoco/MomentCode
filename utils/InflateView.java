package me.wangolf.utils;

import android.os.Handler;
import android.view.View;

public class InflateView {

	/**
	 * 自动滚动ScrollView视图到最下方
	 * 
	 * @param scroll
	 * @param inner
	 */
	public static void scrollToBottom(final View scroll, final View inner) {

		Handler mHandler = new Handler();

		mHandler.post(new Runnable() {
			public void run() {
				if (scroll == null || inner == null) {
					return;
				}
				System.out.println("****************************");
				int offset = inner.getMeasuredHeight() - scroll.getHeight();
				if (offset < 0) {
					offset = 0;
				}

				scroll.scrollTo(0, offset);
			}
		});
	}

}
