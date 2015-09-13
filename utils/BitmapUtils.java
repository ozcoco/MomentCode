package me.wangolf.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class BitmapUtils {
	/**
	 * 按指定宽高 保持纵横比 加载位图
	 * 
	 * @param data
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap getBitmap(byte[] data, int width, int height) {
		Bitmap bm = null;
		// 创建Options,设置仅加载边界属性
		Options opts = new Options();
		opts.inJustDecodeBounds = true;
		// 加载边界信息
		BitmapFactory.decodeByteArray(data, 0, data.length, opts);
		// 获取图片的宽高信息
		int w = opts.outWidth;
		int h = opts.outHeight;
		// 计算收缩比例
		int xScale = w / width;
		int yScale = h / height;
		int scale = xScale > yScale ? xScale : yScale;
		// 设置收缩比例
		opts.inSampleSize = scale;
		opts.inJustDecodeBounds = false;
		// 加载位图
		bm = BitmapFactory.decodeByteArray(data, 0, data.length, opts);

		return bm;
	}

	
	/**
	 * 图片等比缩放3 
	 * @param bitmap
	 * @param w
	 * @param h
	 * @return Drawable
	 */
	public static Drawable resizeImage(Bitmap bm,int newWidth,int newHeight){
		/*
		//load the origial bitmap
		Bitmap BitmapOrg=bitmap;
		
		int width=BitmapOrg.getWidth();
		int height=BitmapOrg.getHeight();
		int newWidth=w;
		int newHeight=h;
		
		//calculate the scale
		float scaleWidth=((float)newWidth)/width;
		float scaleHeight=((float)newHeight)/height;
		
		//create a matrix for the manipulation
		Matrix matrix=new Matrix();
		//resize the Bitmap
		matrix.postScale(scaleWidth, scaleHeight);
		//if you want to rotate the Bitmap
		//matrix.postRotate(45);
		
		//recreate the new Bitmap
*/		Bitmap resizeBitmap=zoomImg(bm, newWidth, newHeight);
		return new BitmapDrawable(resizeBitmap);
	}
	
	/**
	 * 图片等比缩放2
	 * @param bm
	 * @param newWidth
	 * @param newHeight
	 * @return Bitmap
	 */
	public static Bitmap zoomImg(Bitmap bm,int newWidth,int newHeight){
		//获得图片的的宽高
		int width=bm.getWidth();
		int height=bm.getHeight();
		//计算缩放比
		float scaleWidth=((float)newWidth)/width;
		float scaleHeight=((float)newHeight)/height;
		//取得想要缩放的Matrix参数
		Matrix matrix=new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap newbm=Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
		return newbm;
	}
	/**
	 * 从指定文件路径 加载位图到内存
	 * 
	 * @param path
	 * @return
	 */
	public static Bitmap getBitmap(String path) {
		//从指定路径的文件加载位图
		Bitmap bm=null;
		try {
			bm=BitmapFactory.decodeFile(path);
			bm=compressImage(bm);
		} catch (Exception e) {
			//LogUtils.i("info", "第一次从文件夹加载文件不存在《"+path+"》------------"+e);
		}
		return bm;
	}
	
	/**
	 * 保存位图到指定文件
	 * @param bm
	 * @param file
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static void save(Bitmap bm, File file) throws IOException,
			FileNotFoundException {
		// 如果父目录不存在 则创建
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		// 如果文件不存在 则创建文件
		if (!file.exists()) {
			file.createNewFile();
		}
		// 保存图片到文件
		bm.compress(CompressFormat.PNG, 100, new FileOutputStream(file));
	}
	
	public static Bitmap compressImage(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		int options = 100;
		while ( baos.toByteArray().length / 1024>200) {	
			baos.reset();
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);
			options -= 10;
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
		return bitmap;
	}
}
