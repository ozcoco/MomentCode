package me.wangolf.utils;

import java.util.List;

import me.wangolf.GlobalConsts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.webkit.URLUtil;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.PlatformListFakeActivity.OnShareButtonClickListener;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;

import com.meigao.mgolf.R;

@SuppressLint("SdCardPath")
public class ShareUtils
{
	public static void showShare(String sharetitle, Context context, String picfile)
	{
		ShareSDK.initSDK(context);

		OnekeyShare oks = new OnekeyShare();
		// 关闭sso授权
		oks.disableSSOWhenAuthorize();

		// 分享时Notification的图标和文字
		oks.setNotification(R.drawable.logo_new, context
				.getString(R.string.app_name));
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle(context.getString(R.string.share));
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl(GlobalConsts.DOWN_APK_URL);
		// text是分享文本，所有平台都需要这个字段
		oks.setText(sharetitle + "  " + "___优惠尽在【打球App】.");
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		oks.setImagePath("/sdcard/wangolf/" + picfile);// 确保SDcard下面存在此张图片
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl(GlobalConsts.DOWN_APK_URL);
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		oks.setComment("我是测试评论文本");
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite(context.getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl(GlobalConsts.DOWN_APK_URL);

		// 启动分享GUI
		oks.show(context);
	}

	@SuppressLint("SdCardPath")
	public static void showShareandUrl(final String sharetitle, final String url, final Context context, final String picfile)
	{
		ShareSDK.initSDK(context);

		OnekeyShare oks = new OnekeyShare();
		// 关闭sso授权
		oks.disableSSOWhenAuthorize();
		// 分享时Notification的图标和文字
		oks.setNotification(R.drawable.small_logo, context
				.getString(R.string.app_name));			
			
		oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback()
		{

			@Override
			public void onShare(Platform platform, ShareParams paramsToShare)
			{

				if (QQ.NAME.equals(platform.getName()))
				{
					
//					 imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
					if (!CheckUtils.checkEmpty(picfile))
					{
						
						if(!URLUtil.isNetworkUrl(picfile))
							
						paramsToShare.setImagePath("/sdcard/wangolf/" + picfile);// 确保SDcard下面存在此张图片
						
						else
							
						paramsToShare.setImageUrl(picfile);
					
					}	
						
					// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
//					paramsToShare.setTitle(CheckUtils.checkEmpty(sharetitle) ? context
//							.getString(R.string.share) : sharetitle);
					paramsToShare.setTitle("全民高尔夫");
					// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
					paramsToShare.setTitleUrl(CheckUtils.checkEmpty(url) ? GlobalConsts.DOWN_APK_URL : url);
					// text是分享文本，所有平台都需要这个字段
					paramsToShare.setText(sharetitle + "  " + "___优惠尽在【全民高尔夫】.");
					// url仅在微信（包括好友和朋友圈）中使用
					paramsToShare.setUrl(CheckUtils.checkEmpty(url) ? GlobalConsts.DOWN_APK_URL : url);
					// comment是我对这条分享的评论，仅在人人网和QQ空间使用
					paramsToShare.setComment("");
					// site是分享此内容的网站名称，仅在QQ空间使用
					paramsToShare.setSite(context.getString(R.string.app_name));
					// siteUrl是分享此内容的网站地址，仅在QQ空间使用
					paramsToShare.setSiteUrl(GlobalConsts.DOWN_APK_URL);
					
					return;

				}
				else if (Wechat.NAME.equals(platform.getName()))
				{

					// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
					if (!CheckUtils.checkEmpty(picfile))
					{
						
						if(!URLUtil.isNetworkUrl(picfile))
							
						paramsToShare.setImagePath("/sdcard/wangolf/" + picfile);// 确保SDcard下面存在此张图片
						
						else
							
						paramsToShare.setImageUrl(picfile);
					
					}	

					// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
//					paramsToShare.setTitle(CheckUtils.checkEmpty(sharetitle) ? context
//					.getString(R.string.share) : sharetitle);
					paramsToShare.setTitle("全民高尔夫");
					// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
					paramsToShare.setTitleUrl(CheckUtils.checkEmpty(url) ? GlobalConsts.DOWN_APK_URL : url);
					// text是分享文本，所有平台都需要这个字段
					paramsToShare.setText(sharetitle + "  "+" ___优惠尽在【全民高尔夫】.");
					// url仅在微信（包括好友和朋友圈）中使用
					paramsToShare.setUrl(CheckUtils.checkEmpty(url) ? GlobalConsts.DOWN_APK_URL : url);
					// comment是我对这条分享的评论，仅在人人网和QQ空间使用
					paramsToShare.setComment("");
					// site是分享此内容的网站名称，仅在QQ空间使用
					paramsToShare.setSite(context.getString(R.string.app_name));
					// siteUrl是分享此内容的网站地址，仅在QQ空间使用
					paramsToShare.setSiteUrl(GlobalConsts.DOWN_APK_URL);
					
					paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
					

				}
				else
				{

					// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
					if (!CheckUtils.checkEmpty(picfile))
					{
						
						if(!URLUtil.isNetworkUrl(picfile))
							
						paramsToShare.setImagePath("/sdcard/wangolf/" + picfile);// 确保SDcard下面存在此张图片
						
						else
							
						paramsToShare.setImageUrl(picfile);
					
					}	

					// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
//					paramsToShare.setTitle(CheckUtils.checkEmpty(sharetitle) ? context
//					.getString(R.string.share) : sharetitle);
					paramsToShare.setTitle("全民高尔夫");
					// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
					paramsToShare.setTitleUrl(CheckUtils.checkEmpty(url) ? GlobalConsts.DOWN_APK_URL : url);
					// text是分享文本，所有平台都需要这个字段
					paramsToShare.setText(sharetitle + "  " + "___优惠尽在【全民高尔夫】.");
					// url仅在微信（包括好友和朋友圈）中使用
					paramsToShare.setUrl(CheckUtils.checkEmpty(url) ? GlobalConsts.DOWN_APK_URL : url);
					// comment是我对这条分享的评论，仅在人人网和QQ空间使用
					paramsToShare.setComment("");
					// site是分享此内容的网站名称，仅在QQ空间使用
					paramsToShare.setSite(context.getString(R.string.app_name));
					// siteUrl是分享此内容的网站地址，仅在QQ空间使用
					paramsToShare.setSiteUrl(GlobalConsts.DOWN_APK_URL);

					paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
					
				}

			}

		});

		// 启动分享GUI
		oks.show(context);
		
	}

	@SuppressLint("SdCardPath")
	public static void showShareandUrl(String sharetitle, String content, String url, Context context, String picfile)
	{
		ShareSDK.initSDK(context);
		OnekeyShare oks = new OnekeyShare();
		// 关闭sso授权
		oks.disableSSOWhenAuthorize();

		// 分享时Notification的图标和文字
		oks.setNotification(R.drawable.small_logo, context
				.getString(R.string.app_name));
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle(CheckUtils.checkEmpty(sharetitle) ? context
				.getString(R.string.share) : sharetitle);
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl(CheckUtils.checkEmpty(url) ? GlobalConsts.DOWN_APK_URL : url);

		// text是分享文本，所有平台都需要这个字段
		oks.setText(content);
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		// Log.i("wangolf",picfile+"picfile");
		if (!CheckUtils.checkEmpty(picfile))
		{
			oks.setImagePath("/sdcard/wangolf/" + picfile);// 确保SDcard下面存在此张图片
		}

		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl(CheckUtils.checkEmpty(url) ? GlobalConsts.DOWN_APK_URL : url);
		LogUtils.i(url);
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		oks.setComment("我是测试评论文本");
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite(context.getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl(GlobalConsts.DOWN_APK_URL);

		// 启动分享GUI
		oks.show(context);
	}
}
