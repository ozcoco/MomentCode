package me.wangolf.usercenter;

import java.io.File;

import me.wangolf.ConstantValues;
import me.wangolf.base.BaseActivity;
import me.wangolf.bean.InfoEntity;
import me.wangolf.bean.usercenter.ApkInfo;
import me.wangolf.factory.ServiceFactory;
import me.wangolf.newfragment.MainActivityNew;
import me.wangolf.newfragment.UserCentenFra;
import me.wangolf.service.IOAuthCallBack;
import me.wangolf.utils.CheckApkUtils;
import me.wangolf.utils.DeviceUtils;
import me.wangolf.utils.GsonTools;
import me.wangolf.utils.SharedPreferencesUtils;
import me.wangolf.utils.ToastUtils;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.meigao.mgolf.R;

/**
 * ============================================================
 * 
 * 版权 ：美高传媒 版权所有 (c) 2015年1月27日
 * 
 * 作者:copy
 * 
 * 版本 ：1.0
 * 
 * 创建日期 ： 2015年1月27日
 * 
 * 描述 ：用户设置
 * 
 * 
 * 修订历史 ：
 * 
 * ============================================================
 **/

public class UserSet extends BaseActivity implements OnClickListener
{
	@ViewInject(R.id.common_back)
	private Button			common_back;	// 后退

	@ViewInject(R.id.common_title)
	private TextView		common_title;	// 标题

	@ViewInject(R.id.common_bt)
	private TextView		common_bt;		// 地图

	@ViewInject(R.id.my_comment)
	private RelativeLayout	my_comment;		// 我的意见

	@ViewInject(R.id.cooperation)
	private RelativeLayout	cooperation;	// 招商合作

	@ViewInject(R.id.about)
	private RelativeLayout	about;			// 关于我们

	@ViewInject(R.id.loginout)
	private Button			loginout;		// 退出

	@ViewInject(R.id.checkup)
	private RelativeLayout	checkup;		// 检测更新

	@ViewInject(R.id.load_apk)
	private TextView		load_apk;		// 更新信息

	@ViewInject(R.id.my_account)
	private RelativeLayout	mAccount;

	private String			uid;

	private String			download_url;	// 下载链接

	private int				version;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.ac_setup);

		ViewUtils.inject(this);

		initData();
	}

	@Override
	public void initData()
	{
		common_back.setVisibility(View.VISIBLE);

		common_title.setText(ConstantValues.USERSET);

		common_back.setOnClickListener(this);

		my_comment.setOnClickListener(this);

		cooperation.setOnClickListener(this);

		about.setOnClickListener(this);

		loginout.setOnClickListener(this);

		checkup.setOnClickListener(this);

		uid = ConstantValues.UID;

	}

	@Override
	public void getData()
	{
	}

	/**
	 * @Title: isLogin
	 * @Description: 判断是否登陆
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	private void isLogin()
	{

		if (!ConstantValues.ISLOGIN)
		{
			// 去登录
			Intent toLogin = new Intent(this, LoginActivity.class);

			toLogin.putExtra("flag", "usercenter");

			this.startActivityForResult(toLogin, 100);

			return;

		}
	}

	@Override
	public void onClick(View v)
	{

		switch (v.getId())
		{
			case R.id.common_back:

				finish();

				break;

			case R.id.my_comment:

				Intent my_comment = new Intent(this, UserComment.class);

				startActivity(my_comment);

				break;

			case R.id.cooperation:

				Intent cooperation = new Intent(this, AboutActivity.class);

				cooperation.putExtra("type", "2");

				cooperation.putExtra("title", ConstantValues.COOPERATION);

				startActivity(cooperation);

				break;

			case R.id.about:

				Intent about = new Intent(this, AboutActivity.class);

				about.putExtra("type", "3");

				about.putExtra("title", ConstantValues.ABOUT);

				startActivity(about);

				break;

			case R.id.loginout:

				MainActivityNew.checkedRadioButtonId = MainActivityNew.SHOUYE_ID;

				ConstantValues.ISLOGIN = false;

				ConstantValues.UID = null;

				SharedPreferencesUtils.saveString(this, "mgolf_n", null);

				SharedPreferencesUtils.saveString(this, "mgolf_p", null);

				SharedPreferencesUtils.saveString(this, "wx_open_id", null);

				SharedPreferencesUtils.saveString(this, "mgolf_uid", null);

				setResult(101);
				
				finish();

				break;

			case R.id.checkup:
				// 检测更新版本
				toCheckup();

				break;

			// case R.id.my_account:
			// //账号管理
			// Intent account = new Intent(this,UserAccountSet.class);
			//
			// startActivity(account);
			//
			// break;

			default:
				break;
		}
	}

	
	public void toLoginOut()
	{
		try
		{
			ServiceFactory.getIUserEngineInstatice()
					.UserLogout(uid, new IOAuthCallBack()
					{

						@Override
						public void getIOAuthCallBack(String result)
						{

							if (result.equals(ConstantValues.FAILURE))
							{
								ToastUtils.showInfo(UserSet.this, ConstantValues.NONETWORK);

							}
							else
							{
								InfoEntity bean = GsonTools
										.changeGsonToBean(result, InfoEntity.class);
								
								if ("1".equals(bean.getStatus()))
								{
									ToastUtils.showInfo(UserSet.this, bean
											.getInfo());
									
									ConstantValues.ISLOGIN = false;
									
									Platform weibo = ShareSDK
											.getPlatform(SinaWeibo.NAME);
									
									weibo.removeAccount();

									
									
									
									finish();
								}
								else
								{
									ToastUtils.showInfo(UserSet.this, bean
											.getInfo());
								}
							}

						}
					});
		}
		catch(Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 检查是否有新版本
	public void toCheckup()
	{
		
		final String imei_code = DeviceUtils.getDeviceIMEI(this);
		
		final String current_version = "" + getVersionCode();
		
		try
		{
			ServiceFactory.getIUserEngineInstatice()
					.enter(imei_code, current_version, new IOAuthCallBack()
					{

						@Override
						public void getIOAuthCallBack(String result)
						{

							if (result.equals(ConstantValues.FAILURE))
							{

								ToastUtils.showInfo(UserSet.this, ConstantValues.NONETWORK);
							}
							else
							{
								ApkInfo bean = GsonTools
										.changeGsonToBean(result, ApkInfo.class);
								if ("1".equals(bean.getStatus()))
								{
									ApkInfo.DataEntity data = bean.getData().get(0);
									ToastUtils.showInfo(UserSet.this, bean
											.getInfo());
									download_url = data.getVer_url();
									
									version = Integer.valueOf(data.getVer_code());
									
									if (version != 0 & version > getVersionCode())
									{
										CheckApkUtils.CheckApi(UserSet.this);
									}
									else
									{
										ToastUtils
												.showInfo(UserSet.this, "已经是最新版本");
									}
								}
								else
								{
									ToastUtils.showInfo(UserSet.this, bean
											.getInfo());
								}
							}

						}
					});
		}
		catch(Exception e)
		{

			e.printStackTrace();
		}
	}

	public void loadApk()
	{

		// 下载APK，并且替换安装
		if (Environment.getExternalStorageState()
				.equals(Environment.MEDIA_MOUNTED))
		{
			// sdcard存在
			// afnal
			HttpUtils http = new HttpUtils();
			int p = download_url.lastIndexOf("/");
			String apkname = download_url.substring(p);
			File file = new File("/sdcard/" + apkname);
			if (file.isFile() && file.exists())
			{
				file.delete();
			}

			HttpHandler hand = http
					.download(download_url, "/sdcard/" + apkname, true, // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
							true, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
							new RequestCallBack<File>()
							{

								@Override
								public void onStart()
								{
									load_apk.setVisibility(View.VISIBLE);
									load_apk.setText("开始下载");

								}

								@Override
								public void onLoading(long total, long current, boolean isUploading)
								{
									load_apk.setText(current + "/" + total);
								}

								@Override
								public void onSuccess(ResponseInfo<File> responseInfo)
								{
									load_apk.setText("下载完成");
									installAPK(responseInfo.result);
								}

								@Override
								public void onFailure(HttpException error, String msg)
								{
									load_apk.setText("下载出错");

								}

								/**
								 * 安装APK
								 * 
								 * @param t
								 */
								private void installAPK(File t)
								{
									Intent intent = new Intent();
									intent.setAction("android.intent.action.VIEW");
									intent.addCategory("android.intent.category.DEFAULT");
									intent.setDataAndType(Uri.fromFile(t), "application/vnd.android.package-archive");
									startActivity(intent);

								}
							});
		}
		else
		{

			ToastUtils.showInfo(UserSet.this, "没有sdcard，请安装上在试");
			return;
		}

	}

	/**
	 * 得到应用程序的版本名称
	 */

	private int getVersionCode()
	{
		// 用来管理手机的APK
		PackageManager pm = getPackageManager();

		try
		{
			// 得到知道APK的功能清单文件
			PackageInfo info = pm.getPackageInfo(getPackageName(), 0);

			return info.versionCode;
		}
		catch(Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}

}
