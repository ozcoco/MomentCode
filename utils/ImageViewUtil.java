package me.wangolf.utils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.meigao.mgolf.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class ImageViewUtil {
	private static ImageLoader imageLoader;
	static DisplayImageOptions options;
	private static ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	
	public ImageViewUtil() {
		super();
	
	}

	public static void loadimg(String imageUrlStr ,ImageView image,Context context) {
		//animateFirstListener = new AnimateFirstDisplayListener();
		options = new DisplayImageOptions.Builder()
		//.showImageOnLoading(R.drawable.ic_stub)
		.showImageOnLoading(R.drawable.loading_img)
		//.showImageForEmptyUri(R.drawable.ic_empty)
		.showImageForEmptyUri(R.drawable.photo_default)
		
		.showImageForEmptyUri(R.drawable.photo_default)
		//.showImageOnFail(R.drawable.ic_error)
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		//.displayer(new RoundedBitmapDisplayer(0))
		//.displayer(new SimpleBitmapDisplayer())
		.build();
		imageLoader= ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		if(imageUrlStr!=null&&imageUrlStr.length()>0){
			String []s=imageUrlStr.split(",");
			imageUrlStr=s[0];
			imageLoader.displayImage(imageUrlStr,image, options, animateFirstListener);
			image.setBackgroundDrawable(null);
		}else{
			imageLoader.displayImage(imageUrlStr+"dd",image, options, animateFirstListener);
		}
	}
	private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}
}
