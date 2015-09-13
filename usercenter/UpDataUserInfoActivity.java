package me.wangolf.usercenter;

import java.io.File;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.meigao.mgolf.R;

import me.wangolf.ConstantValues;
import me.wangolf.GlobalConsts;
import me.wangolf.base.BaseActivity;
import me.wangolf.bean.InfoEntity;
import me.wangolf.factory.ServiceFactory;
import me.wangolf.service.IOAuthCallBack;
import me.wangolf.utils.BitmapUtils;
import me.wangolf.utils.DialogUtil;
import me.wangolf.utils.GsonTools;
import me.wangolf.utils.HttpUtils;
import me.wangolf.utils.ShowPickUtils;
import me.wangolf.utils.ToastUtils;

public class UpDataUserInfoActivity extends BaseActivity implements OnClickListener {
	@ViewInject(R.id.common_back)
	private Button common_back; // 后退
	@ViewInject(R.id.common_title)
	private TextView common_title;// 标题
	@ViewInject(R.id.common_bt)
	private TextView common_bt;// 地图
	@ViewInject(R.id.my_info)
	private RelativeLayout my_info;// 更新资料
	@ViewInject(R.id.reupphto)
	private RelativeLayout reupphto;// 更新头像
	@ViewInject(R.id.remypwd)
	private RelativeLayout remypwd;// 更新密码
	private String uid;
	private String path;
	private Dialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_update_userinfo_list);
		ViewUtils.inject(this);
		initData();
	}

	@Override
	public void initData() {
		dialog = DialogUtil.getDialog(this);
		common_back.setVisibility(0);
		common_title.setText("账户资料");
		common_back.setOnClickListener(this);
		my_info.setOnClickListener(this);
		reupphto.setOnClickListener(this);
		remypwd.setOnClickListener(this);
		uid = ConstantValues.UID;

	}

	@Override
	public void getData() {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.common_back:
			finish();
			break;
		case R.id.my_info:
			Intent my_info = new Intent(UpDataUserInfoActivity.this, UpDataMyInfoActivity.class);
			startActivity(my_info);
			break;
		case R.id.reupphto:
			ShowPickUtils.ShowPickDialog(UpDataUserInfoActivity.this);
			break;
		case R.id.remypwd:
			Intent remypwd = new Intent(UpDataUserInfoActivity.this, UpDataPassword.class);
			startActivity(remypwd);
			break;
		default:
			break;
		}
	}

	// 更新头像
	public void upLoad(Intent picdata) {
		Bundle extras = picdata.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			photo = BitmapUtils.compressImage(photo);
			Drawable drawable = new BitmapDrawable(photo);
			// TODO 保存到SDcard
			HttpUtils.SavePicInLocal(photo);
			File img = new File(Environment.getExternalStorageDirectory(), "avatar_file.jpg");
			final String imagepath = img.getPath(); // 图片路径
			path = imagepath;
			dialog.show();
			try {
				ServiceFactory.getIUserEngineInstatice().upLoad(uid, path, new IOAuthCallBack() {

					@Override
					public void getIOAuthCallBack(String result) {
						if (result.equals(ConstantValues.FAILURE)) {
							Toast.makeText(getApplicationContext(), ConstantValues.NONETWORK, 0).show();
						} else {
							InfoEntity bean = GsonTools.changeGsonToBean(result, InfoEntity.class);
							if ("1".equals(bean.getStatus())) {
								ToastUtils.showInfo(UpDataUserInfoActivity.this, bean.getInfo());
							} else {
								ToastUtils.showInfo(UpDataUserInfoActivity.this, bean.getInfo());
							}
						}
						dialog.cancel();
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		/*************** 照片相关操作start ********************/
		if (requestCode != -1 && requestCode != GlobalConsts.eventYMDCode) {

			switch (requestCode) {
			// 如果是直接从相册获取
			case 1:
				if (data != null) {
					ShowPickUtils.startPhotoZoom(UpDataUserInfoActivity.this, data.getData());
				}
				break;
			// 如果是调用相机拍照时
			case 2:
				File temp = new File(Environment.getExternalStorageDirectory() + "/avatar_file.jpg");
				ShowPickUtils.startPhotoZoom(UpDataUserInfoActivity.this, Uri.fromFile(temp));

				break;
			// 取得裁剪后的图片
			case 3:
				if (data != null) {
					upLoad(data);
				}
				break;
			}
			/*************** 照片相关操作end ********************/

		}
	}
}
