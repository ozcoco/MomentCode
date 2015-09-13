package me.wangolf.utils;

import java.io.File;

import com.meigao.mgolf.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import me.wangolf.bean.usercenter.RegistEntity;
import me.wangolf.community.CommunityDetailActivity;
import me.wangolf.newfragment.CommunityFra;
import me.wangolf.usercenter.OrderScuessActivity;
import me.wangolf.usercenter.RegistActivity;
import me.wangolf.usercenter.UserAccountSet;
import me.wangolf.usercenter.UserBindMobileActivity;
import me.wangolf.usercenter.UserInfoEditInfoActivity;

public class ShowPickUtils {
	private static Activity activity;
	private static Context con;
	private static Dialog dialog;

	/**
	 * 选择提示对话框
	 */
	public static void ShowPickDialog(Context context) {
		activity = (Activity) context;

		con = context;
		dialog = new Dialog(context, R.style.MyDialogTheme);
		String title = "上传封面图片...";
		View view = View.inflate(context, R.layout.dialog_photo, null);
		dialog.setContentView(view);
		TextView tvdTitle = (TextView) view.findViewById(R.id.dialog_title);
		tvdTitle.setText(title);
		Button btcancle = (Button) view.findViewById(R.id.bt_cancle);
		btcancle.setText("相册");
		Button btok = (Button) view.findViewById(R.id.bt_ok);
		btok.setText("拍照");
		btcancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				Intent intent = new Intent(Intent.ACTION_PICK, null);

				intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
				activity.startActivityForResult(intent, 1);
			}
		});
		btok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				// 下面这句指定调用相机拍照后的照片存储的路径
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "avatar_file.jpg")));
				activity.startActivityForResult(intent, 2);
			}
		});
		dialog.show();

	}

	/**
	 * 裁剪图片方法实现 &nbsp;
	 * 
	 * @param uri
	 */

	public static void startPhotoZoom(Context context, Uri uri) {
		activity = (Activity) context;
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 150);
		intent.putExtra("outputY", 150);
		intent.putExtra("return-data", true);
		activity.startActivityForResult(intent, 3);
	}
	/**
	 * 选择提示对话框
	 */
	public static void ShowDialog(Context context,String info) 
	{
		activity = (Activity) context;

		con = context;
		
		dialog = new Dialog(context, R.style.MyDialogTheme);
		
		String title = info;
		
		View view = View.inflate(context, R.layout.dialog_bian_phone, null);
		
		dialog.setContentView(view);
		
		TextView tvdTitle = (TextView) view.findViewById(R.id.dialog_title);
		tvdTitle.setText(title);
		
		Button btcancle = (Button) view.findViewById(R.id.bt_cancle);
		btcancle.setText("关闭");
		
		btcancle.setOnClickListener(new OnClickListener() 
		{

			@Override
			public void onClick(View arg0)
			{
				dialog.dismiss();

			}
		});

		dialog.show();

	}
	public static void ShowDialogComm( final Context context,String info) {
		activity = (Activity) context;

		con = context;
		dialog = new Dialog(context, R.style.MyDialogTheme);
		String title = info;
		View view = View.inflate(context, R.layout.dialog_bian_phone, null);
		dialog.setContentView(view);
		TextView tvdTitle = (TextView) view.findViewById(R.id.dialog_title);
		tvdTitle.setText(title);
		Button btcancle = (Button) view.findViewById(R.id.bt_cancle);
		btcancle.setText("确定");
		btcancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

						//((CommunityDetailActivity) context).toEditUserInfo();
				Intent editinfo = new Intent(context, UserInfoEditInfoActivity.class);
				context.startActivity(editinfo);
				dialog.dismiss();
			}
		});

		dialog.show();

	}
	/**
	 * 选择提示对话框
	 */
	public static void ShowShareDialog(final Context context) {
		activity = (Activity) context;

		con = context;
		dialog = new Dialog(context, R.style.MyDialogTheme);
		String title = "取消分享";
		View view = View.inflate(context, R.layout.dialog_photo, null);
		dialog.setContentView(view);
		TextView tvdTitle = (TextView) view.findViewById(R.id.dialog_title);
		tvdTitle.setText("取消后将视为您放弃此给好友发红包的机会，此条订单将不能再次分享。");
		Button btcancle = (Button) view.findViewById(R.id.bt_cancle);
		btcancle.setText("我点错了");
		Button btok = (Button) view.findViewById(R.id.bt_ok);
		btok.setText("取消分享");
		btcancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();

			}
		});
		btok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				((OrderScuessActivity) context).closeShare();

			}
		});
		dialog.show();

	}

	/**
	 * 选择提示对话框
	 */
	public static void ShowSpeechDialog(final Context context, final int flag) {
		activity = (Activity) context;

		con = context;
		dialog = new Dialog(context, R.style.MyDialogTheme);
		String title = "温馨提示";
		View view = View.inflate(context, R.layout.dialog_speech, null);
		dialog.setContentView(view);
		TextView tvdTitle = (TextView) view.findViewById(R.id.dialog_title);
		//tvdTitle.setText("我们将拨打您的电话，通过语音告知您验证码，请注意接听，不要挂短。");
		Button btcancle = (Button) view.findViewById(R.id.bt_cancle);
		btcancle.setText("确定");
		Button btok = (Button) view.findViewById(R.id.bt_ok);
		btok.setText("取消");
		btcancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				if (flag == 0) {
					((RegistActivity) context).getSpeechCode();
				} else if (flag == 1) {
					((UserBindMobileActivity) context).getSpeechCode();
				}
			}
		});
		btok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();


			}
		});
		dialog.show();

	}

	/**
	 * 选择提示对话框
	 */
	public static void ShowRegistDialog(RegistEntity.DataEntity bean,final Context context) {
		activity = (Activity) context;

		con = context;
		dialog = new Dialog(context, R.style.MyDialogTheme);
		View view = View.inflate(context, R.layout.dialog_regist, null);
       TextView mInfo = (TextView) view.findViewById(R.id.tv_info);
		TextView mWwnprice = (TextView) view.findViewById(R.id.tv_wwnprice);
		TextView mOtherinfo = (TextView) view.findViewById(R.id.tv_otherinfo);
		if(bean!=null){
			mInfo.setText(bean.getSelf_info());
			mWwnprice.setText(bean.getSelf_amount()+"元");
			mOtherinfo.setText(bean.getRecommend_info());
		}

		dialog.setContentView(view);
		Button enter = (Button) view.findViewById(R.id.but_enter);
		enter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				((RegistActivity)context).closeDialog();
				dialog.dismiss();

			}
		});
		dialog.show();

	}
}
