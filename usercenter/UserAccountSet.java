package me.wangolf.usercenter;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.meigao.mgolf.R;
import com.meigao.mgolf.wxapi.MyWeiPayUtils;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;
import me.wangolf.ConstantValues;
import me.wangolf.base.BaseActivity;
import me.wangolf.bean.InfoEntity;
import me.wangolf.bean.usercenter.UserInfoEntity;
import me.wangolf.factory.ServiceFactory;
import me.wangolf.service.IOAuthCallBack;
import me.wangolf.utils.CheckUtils;
import me.wangolf.utils.DialogUtil;
import me.wangolf.utils.GsonTools;
import me.wangolf.utils.ToastUtils;

public class UserAccountSet extends BaseActivity implements OnClickListener,
		PlatformActionListener
{
	@ViewInject(R.id.common_back)
	private Button				common_back;				// 后退
	@ViewInject(R.id.common_title)
	private TextView			common_title;				// 标题
	@ViewInject(R.id.tv_phone)
	private TextView			mPhone;
	@ViewInject(R.id.tv_wx)
	private TextView			mWx;
	@ViewInject(R.id.my_pwd)
	private RelativeLayout		mUserPwd;
	@ViewInject(R.id.my_account)
	private RelativeLayout		mMy_account;
	@ViewInject(R.id.checkup)
	private RelativeLayout		mCheckup;
	private static final int	MSG_SMSSDK_CALLBACK	= 1;
	private static final int	MSG_AUTH_CANCEL		= 2;
	private static final int	MSG_AUTH_ERROR		= 3;
	private static final int	MSG_AUTH_COMPLETE	= 4;
	private Dialog				dialog;
	private Platform			bean;
	private Handler				handler				= new Handler()
													{

														@Override
														public void handleMessage(Message msg)
														{
															// Log.i("wangolf",
															// "*******************"
															// + msg.what);
															switch (msg.what)
															{
																case MSG_AUTH_CANCEL:
																{
																	// 取消授权
																	Toast.makeText(UserAccountSet.this, R.string.auth_cancel, Toast.LENGTH_SHORT)
																			.show();
																}
																	break;
																case MSG_AUTH_ERROR:
																{
																	// 授权失败
																	Toast.makeText(UserAccountSet.this, R.string.auth_error, Toast.LENGTH_SHORT)
																			.show();
																}
																	break;
																case MSG_AUTH_COMPLETE:
																{
																	// 授权成功
																	Toast.makeText(UserAccountSet.this, R.string.auth_complete, Toast.LENGTH_SHORT)
																			.show();
																	Object[] objs = (Object[]) msg.obj;
																	String platform = (String) objs[0];
																	HashMap<String, Object> res = (HashMap<String, Object>) objs[1];
																	bean = ShareSDK
																			.getPlatform(platform);
																	binWx(bean);
																}
																	break;

															}
															dialog.cancel();
															// finish();
															super.handleMessage(msg);
														}

													};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_account_set);
		ViewUtils.inject(this);
		initData();
	}

	@Override
	public void initData()
	{
		dialog = DialogUtil.getDialog(this);
		common_back.setVisibility(View.VISIBLE);
		common_title.setText("我的账号");
		common_back.setOnClickListener(this);
		mPhone.setText(ConstantValues.USER_MOBILE);
		mWx.setText(ConstantValues.ISWXlOGIN ? "已绑定" : "未绑定");
		mUserPwd.setOnClickListener(this);
		mMy_account.setOnClickListener(this);

		mCheckup.setOnClickListener(this);

	}

	@Override
	public void getData()
	{

	}

	@Override
	public void onClick(View v)
	{

		switch (v.getId())
		{
			case R.id.common_back:
				finish();
				break;
			case R.id.my_pwd:
//				Intent upPwd = new Intent(this, UpDataPassword.class);
//				startActivity(upPwd);
				break;
			case R.id.my_account:
				Intent bin = new Intent(this, UserBindMobileActivity.class);
				bin.putExtra("uid", Integer.parseInt(ConstantValues.UID));
				bin.putExtra("flag", "binWx");
				startActivityForResult(bin, 105);
				break;
			case R.id.checkup:
				if (ConstantValues.ISWXlOGIN) { return; }
				dialog.show();
				ShareSDK.initSDK(this);
				Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
				authorize(wechat);
				break;
			default:
				break;
		}
	}

	// 执行授权,获取用户信息
	// 文档：http://wiki.mob.com/Android_%E8%8E%B7%E5%8F%96%E7%94%A8%E6%88%B7%E8%B5%84%E6%96%99
	private void authorize(Platform plat)
	{
		if (plat == null) {

		return; }
		plat.setPlatformActionListener(this);
		// 关闭SSO授权
		plat.SSOSetting(true);
		plat.showUser(null);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch (resultCode)
		{
			case 105:
				initData();
				break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap)
	{
		if (i == Platform.ACTION_USER_INFOR)
		{
			Message msg = new Message();
			msg.what = MSG_AUTH_COMPLETE;
			msg.obj = new Object[]
			{ platform.getName(), hashMap };
			handler.sendMessage(msg);
		}
	}

	@Override
	public void onError(Platform platform, int i, Throwable throwable)
	{

	}

	@Override
	public void onCancel(Platform platform, int i)
	{

	}

	// 微信登陆
	private void binWx(Platform data)
	{
		PlatformDb bean = data.getDb();
		String weixin_gender = "m".equals(bean.getUserGender()) ? "1" : "0";
		if (bean != null)
			try
			{
				ServiceFactory
						.getIUserEngineInstatice()
						.bindWeixin(ConstantValues.UID, ConstantValues.PASSWORD, bean
								.getUserId(), bean.getUserIcon(), bean
								.getUserName(), weixin_gender, new IOAuthCallBack()
						{
							@Override
							public void getIOAuthCallBack(String result)
							{
								if (result.equals(ConstantValues.FAILURE))
								{
									ToastUtils
											.showInfo(UserAccountSet.this, ConstantValues.NONETWORK);
								}
								else
								{
									UserInfoEntity bean = GsonTools
											.changeGsonToBean(result, UserInfoEntity.class);
									if ("1".equals(bean.getStatus()))
									{
										ConstantValues.ISWXlOGIN = true;
										initData();
										ToastUtils
												.showInfo(UserAccountSet.this, bean
														.getInfo());
									}
									else
									{
										ToastUtils
												.showInfo(UserAccountSet.this, bean
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
}
