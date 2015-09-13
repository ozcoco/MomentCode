package me.wangolf.utils;

import java.io.File;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;

import com.meigao.mgolf.R;

public class FileUtils
{

	/**
	 * 保存图片到指定的目录
	 * 
	 * @param bit
	 * @param fileName
	 *            文件名
	 * @return
	 */
	public static String saveBitToSD(Bitmap bit, String fileName)
	{
		if (bit == null || bit.isRecycled()) return "";

		File file = new File(Environment.getExternalStorageDirectory(), "/wangolf");
		File dirFile = new File(file.getAbsolutePath());
		if (!dirFile.exists())
		{
			dirFile.mkdirs();
		}
		File pathFile = new File(dirFile, fileName + ".jpg");

		if (pathFile.exists())
		{
			return pathFile.getAbsolutePath();
		}
		else
		{
			ImageUtils.Bitmap2File(bit, pathFile.getAbsolutePath());
			return pathFile.getAbsolutePath();
		}
	}

	/**
	 * // 删除文件夹下的图片
	 */
	public static void clearImage()
	{
		File file = new File(Environment.getExternalStorageDirectory(), "/wangolf");
		if (file.exists())
		{
			String[] childFilePaths = file.list();
			if (childFilePaths.length > 0)
			{
				for (String childFilePath : childFilePaths)
				{
					File childFile = new File(file.getAbsolutePath() + "/" + childFilePath);

					deleteImage(childFile);
				}
			}
		}
	}

	// 删除图片
	public static void deleteImage(File file)
	{
		if (file.isFile())
		{
			file.delete();
		}
		file.exists();
	}

	public static String saveBitToPNG(Bitmap bit, String fileName)
	{
		if (bit == null || bit.isRecycled()) return "";

		File file = new File(Environment.getExternalStorageDirectory(), "/wangolf");
		File dirFile = new File(file.getAbsolutePath());
		if (!dirFile.exists())
		{
			dirFile.mkdirs();
		}
		File pathFile = new File(dirFile, fileName + ".png");

		if (pathFile.exists())
		{
			return pathFile.getAbsolutePath();
		}
		else
		{
			ImageUtils.Bitmap2File(bit, pathFile.getAbsolutePath());
			return pathFile.getAbsolutePath();
		}
	}

	public static String getPath()
	{

		String path = Environment.getExternalStorageDirectory() + "/wangolf/";

		return path;
	}

	/**
	 * 判断文件是否存在
	 * 1、v如果存在且大小相等返回true
	 * 2、如果不存在或大小不相等返回flase,重新下载
	 * 
	 * @param
	 * @return
	 */
	public static boolean isFile(String name)
	{
		File file = new File(Environment.getExternalStorageDirectory() + "/wangolf/" + name);
		// File dirFile = new File(file.getAbsolutePath());
		file.length();
		// Log.i("wangolf", file.getPath() + "file.length()");
		if (file.exists())
		{

			return true;
		}
		else
		{
			// deleteImage(file);
			return false;
		}
	}

	/**
	 * 在二维码中间添加Logo图案
	 */
	public static Bitmap addLogo(Bitmap src, Bitmap logo)
	{
		if (src == null) { return null; }

		if (logo == null) { return src; }

		// 获取图片的宽高
		int srcWidth = src.getWidth();
		int srcHeight = src.getHeight();
		int logoWidth = logo.getWidth();
		int logoHeight = logo.getHeight();

		if (srcWidth == 0 || srcHeight == 0) { return null; }

		if (logoWidth == 0 || logoHeight == 0) { return src; }
		// Log.w("wangolf","*******//获取图片的宽高*****"+srcWidth+logoWidth);
		// logo大小为二维码整体大小的1/5
		float scaleFactor = srcWidth * 1.0f / 5 / logoWidth;
		Bitmap bitmap = Bitmap
				.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
		try
		{
			Canvas canvas = new Canvas(bitmap);
			canvas.drawBitmap(src, 0, 0, null);
			canvas.scale(scaleFactor, scaleFactor, srcWidth / 2, srcHeight / 2);
			canvas.drawBitmap(logo, (srcWidth - logoWidth) / 2, (srcHeight - logoHeight) / 2, null);

			canvas.save(Canvas.ALL_SAVE_FLAG);
			canvas.restore();
		}
		catch(Exception e)
		{
			bitmap = null;
			e.getStackTrace();
		}

		return bitmap;
	}

	/**
	 * 获取头像路径
	 * 
	 * @param path
	 * @return
	 */
	public static String getPhotoPath(String path)
	{
		String paths;
		if (path.contains("wx.qlogo.cn"))
		{
			paths = path + ".png";
		}
		else
		{
			paths = path.substring(0, path.lastIndexOf(".")) + "_180_180" + path
					.substring(path.lastIndexOf("."));

		}
		return paths;
	}

	/**
	 * Drawable转换为Bitmap
	 * 
	 * @param drawable
	 */
	public static Bitmap drawableToBitamp(Drawable drawable)
	{
		BitmapDrawable bd = (BitmapDrawable) drawable;
		Bitmap bitmap = bd.getBitmap();
		return bitmap;
	}
}
