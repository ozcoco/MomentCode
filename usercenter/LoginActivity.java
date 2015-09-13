package me.wangolf.usercenter;

import org.apache.commons.codec.digest.DigestUtils;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.cache.MD5FileNameGenerator;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.meigao.mgolf.R;
import com.meigao.mgolf.wxapi.Constants;
import com.meigao.mgolf.wxapi.MyWeiPayUtils;
import com.meigao.mgolf.wxapi.SendWXActivity;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;
import me.wangolf.ConstantValues;
import me.wangolf.bean.usercenter.User;
import me.wangolf.bean.usercenter.UserInfoEntity;
import me.wangolf.factory.ServiceFactory;
import me.wangolf.practice.OrderDialogPractice;
import me.wangolf.service.IOAuthCallBack;
import me.wangolf.utils.CheckUtils;
import me.wangolf.utils.DialogUtil;
import me.wangolf.utils.GsonTools;
import me.wangolf.utils.MD5Utils;
import me.wangolf.utils.SharedPreferencesUtils;
import me.wangolf.utils.ShowPickUtils;
import me.wangolf.utils.ToastUtils;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class LoginActivity extends Activity implements OnClickListener,
		PlatformActionListener
{
	private FragmentManager					manager;

	private android.app.FragmentTransaction	transaction;

	private static final int				MSG_SMSSDK_CALLBACK	= 1;

	private static final int				MSG_AUTH_CANCEL		= 2;

	private static final int				MSG_AUTH_ERROR		= 3;

	private static final int				MSG_AUTH_COMPLETE	= 4;
	@ViewInject(R.id.common_title)
	private TextView						common_title;
	@ViewInject(R.id.common_back)
	private Button							common_back;
	@ViewInject(R.id.bt_login)
	private Button							bt_login;
	@ViewInject(R.id.ed_phone)
	private EditText						ed_phone;
	@ViewInject(R.id.ed_pwd)
	private EditText						ed_pwd;
	@ViewInject(R.id.bt_regist)
	private Button							bt_regist;					// 注册
	@ViewInject(R.id.bt_forgot_pwd)
	private TextView						bt_forgot_pwd;				// 忘记密码
	@ViewInject(R.id.wx_login)
	private Button							mWxLogin;
	private String							phone;						// 手机号码
	private String							password;					// 密码
	private String							flag;						// 传过过来的标记
	private Dialog							dialog;
	private IWXAPI							api;
	private Platform						bean;

	private Handler							handler				= new Handler()
																{

																	@Override
																	public void handleMessage(Message msg)
																	{
																		// Log.i("wangolf","*******************"+msg.what);
																		switch (msg.what)
																		{
																			case MSG_AUTH_CANCEL:
																			{
																				// 取消授权
																				Toast.makeText(LoginActivity.this, R.string.auth_cancel, Toast.LENGTH_SHORT)
																						.show();
																			}
																				break;

																			case MSG_AUTH_ERROR:
																			{
																				// 授权失败
																				Toast.makeText(LoginActivity.this, R.string.auth_error, Toast.LENGTH_SHORT)
																						.show();
																			}
																				break;

																			case MSG_AUTH_COMPLETE:
																			{
																				// 授权成功
																				Toast.makeText(LoginActivity.this, R.string.auth_complete, Toast.LENGTH_SHORT)
																						.show();

																				Object[] objs = (Object[]) msg.obj;

																				String platform = (String) objs[0];

																				HashMap<String, Object> res = (HashMap<String, Object>) objs[1];

																				bean = ShareSDK
																						.getPlatform(platform);

																				toWxLogin(bean);

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

		setContentView(R.layout.ac_login);

		ViewUtils.inject(this);

		initData();

	}

	public void initData()
	{
		dialog = DialogUtil.getDialog(LoginActivity.this);

		flag = getIntent().getStringExtra("flag");

		common_back.setVisibility(View.VISIBLE);

		common_title.setText(ConstantValues.USER_LOGIN);

		common_back.setOnClickListener(this);

		bt_login.setOnClickListener(this);

		bt_regist.setOnClickListener(this);

		bt_forgot_pwd.setOnClickListener(this);

		mWxLogin.setOnClickListener(this);
	}

	// 实现数据传递
	public void getString(Callback callback)
	{
		callback.getString(false);

		this.finish();
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

	// 创建接口
	public interface Callback
	{
		public void getString(boolean msg);
	}

	// 用户登录
	public void login()
	{
		final User u = new User();

		u.setUsername(phone);

		u.setPassword(password);

		try
		{
			ServiceFactory.getIUserEngineInstatice()
					.UserLogin(u, new IOAuthCallBack()
					{

						@Override
						public void getIOAuthCallBack(String result)
						{
							// Log.i("wangolf", result);
							if (result.equals(ConstantValues.FAILURE))
							{
								ToastUtils.showInfo(LoginActivity.this, ConstantValues.NONETWORK);
							}
							else
							{
								UserInfoEntity user = GsonTools
										.changeGsonToBean(result, UserInfoEntity.class);

								if ("1".equals(user.getStatus()))
								{
									UserInfoEntity.DataEntity userinfo = user
											.getData().get(0);

									ConstantValues.ISLOGIN = true;

									ConstantValues.USER_MOBILE = phone;

									ConstantValues.UNIQUE_KEY = userinfo
											.getUnique_key();

									// if(
									// !CheckUtils.checkEmpty(userinfo.getWeixin_open_id()))
									// ConstantValues.ISWXlOGIN =true;
									// else
									// ConstantValues.ISWXlOGIN =false;

									ConstantValues.UID = userinfo.getUser_id();

									ConstantValues.PASSWORD = password;

									ToastUtils
											.showInfo(LoginActivity.this, "登录成功");

									setCache("mgolf_n", phone);// 缓存用户名

									setCache("mgolf_p", password);// 缓存密码

									loginResult();

									if (!CheckUtils.checkEmpty(userinfo
											.getNick_name()) & !CheckUtils
											.checkEmpty(userinfo.getMobile()))
										ConstantValues.ISCOMPLETEINFO = true;
									else
										ConstantValues.ISCOMPLETEINFO = false;
								}
								else if (user.getStatus().equals("-1"))
								{
									ShowPickUtils
											.ShowDialog(LoginActivity.this, user
													.getInfo());
									// ToastUtils.showInfo(LoginActivity.this,
									// user.getInfo());
								}
							}
							dialog.cancel();
						}

					});

		}
		catch(Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 微信登陆
	private void toWxLogin(Platform data)
	{
		PlatformDb bean = data.getDb();
		bean.putExpiresIn(3600 * 24 * 30);

		String weixin_gender = "m".equals(bean.getUserGender()) ? "1" : "0";
		if (bean != null)
			try
			{
				ServiceFactory
						.getIUserEngineInstatice()
						.toWxLogin(bean.getUserId(), bean.getUserIcon(), bean.getUserName(), weixin_gender, new IOAuthCallBack()
						{
							@Override
							public void getIOAuthCallBack(String result)
							{
								if (result.equals(ConstantValues.FAILURE))
								{
									ToastUtils
											.showInfo(LoginActivity.this, ConstantValues.NONETWORK);
								}
								else
								{
									UserInfoEntity bean = GsonTools
											.changeGsonToBean(result, UserInfoEntity.class);
									if ("1".equals(bean.getStatus()))
									{
										// 登录成功且已绑定手机号

										UserInfoEntity.DataEntity userinfo = bean
												.getData().get(0);

										setCache("mgolf_uid", userinfo
												.getUser_id() + "");// 缓存用户名

										// setCache("wx_open_id",
										// userinfo.getWeixin_open_id());//缓存用户名

										ConstantValues.ISLOGIN = true;

										ConstantValues.USER_MOBILE = userinfo
												.getMobile();

										ConstantValues.ISWXlOGIN = true;

										// ConstantValues.OPEN_ID=userinfo.getWeixin_open_id();

										ConstantValues.UID = userinfo
												.getUser_id();

										ToastUtils
												.showInfo(LoginActivity.this, "登录成功");

										loginResult();

										// |!CheckUtils.checkEmpty(userinfo.getWeixin_avatar())
										if (!CheckUtils.checkEmpty(userinfo
												.getNick_name()) & (!CheckUtils
												.checkEmpty(userinfo
														.getMobile())))

											ConstantValues.ISCOMPLETEINFO = true;
										else

											ConstantValues.ISCOMPLETEINFO = false;
									}
									else if ("-1".equals(bean.getStatus()))
									{
										// 登录成功且未绑定手机号
										ToastUtils
												.showInfo(LoginActivity.this, bean
														.getInfo());

										Intent bindMobile = new Intent(LoginActivity.this, UserBindMobileActivity.class);

										String uid = bean.getData().get(0)
												.getUser_id();

										bindMobile.putExtra("uid", uid);

										LoginActivity.this
												.startActivityForResult(bindMobile, 105);

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

	// 登录成功后返回结果给请求页面
	public void loginResult()
	{
		ConstantValues.HOME_ISLOGIN = true;

		if (flag.equals("orderPrac"))
		{
			Intent in = new Intent(this, OrderDialogPractice.class);

			setResult(ConstantValues.ORDERPRAC, in);

			finish();

		}
		else if (flag.equals("usercenter") | "regist".equals(flag))
		{
			ConstantValues.USERCENT_ISLOGIN = true;

			finish();

		}
		else if(flag.equals("recharge"))
		{
			
			ConstantValues.USERCENT_ISLOGIN = true;

			setResult(1);
			
			finish();
			
		}
		else if (flag.equals("other"))
		{
			Intent in = new Intent(this, OrderDialogPractice.class);

			setResult(ConstantValues.ORDERPRAC, in);

			finish();

		}
		else if (flag.equals("userlogin"))
		{
			setResult(100);

			finish();
		}
		else if(flag.equals("index"))
		{
			
			setResult(0);
			
			finish();
						
		}
		

	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.bt_login:

				phone = ed_phone.getText().toString().trim();

				password = DigestUtils.md5Hex(ed_pwd.getText().toString()
						.trim());

				dialog.show();

				login();

				break;

			case R.id.common_back:

				setResult(99);

				finish();

				break;

			case R.id.bt_regist:

				Intent regist = new Intent(this, RegistActivity.class);

				regist.putExtra("flag", "regist");

				startActivityForResult(regist, ConstantValues.USERREGIST);

				break;

			case R.id.bt_forgot_pwd:

				Intent getpwd = new Intent(this, GetUserPwdActivity.class);

				startActivity(getpwd);

				break;

			case R.id.wx_login:

				dialog.show();

				MyWeiPayUtils.iswxLogin = true;

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
		super.onActivityResult(requestCode, resultCode, data);
		if (data == null) { return; }
		if (requestCode == ConstantValues.USERREGIST & !MyWeiPayUtils.iswxLogin)
		{
			flag = data.getStringExtra("flag");
			phone = data.getStringExtra("phone");
			password = data.getStringExtra("password");
			login();
		}
		switch (resultCode)
		{
			case 105:
				toWxLogin(bean);
				break;
		}

	}

	public void exit()
	{
		this.finish();
	}

	// 缓存用户名密码
	public void setCache(String name, String result)
	{
		SharedPreferencesUtils.saveString(this, name, result);
	}
}
