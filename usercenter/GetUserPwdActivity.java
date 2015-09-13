package me.wangolf.usercenter;

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
 * 描述 ：使用手机重置密码
 * 
 * 
 * 修订历史 ：
 * 
 * ============================================================
 **/
import org.apache.commons.codec.digest.DigestUtils;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.meigao.mgolf.R;

import me.wangolf.ConstantValues;
import me.wangolf.base.BaseActivity;
import me.wangolf.bean.InfoEntity;
import me.wangolf.factory.ServiceFactory;
import me.wangolf.service.IOAuthCallBack;
import me.wangolf.utils.CheckUtils;
import me.wangolf.utils.DialogUtil;
import me.wangolf.utils.GsonTools;
import me.wangolf.utils.ToastUtils;

public class GetUserPwdActivity extends BaseActivity implements OnClickListener
{
	@ViewInject(R.id.common_back)
	private Button		common_back;		// 后退
	@ViewInject(R.id.common_title)
	private TextView	common_title;		// 标题
	@ViewInject(R.id.common_bt)
	private TextView	common_bt;			// 地图
	@ViewInject(R.id.ed_update_phone)
	private EditText	ed_update_phone;	// 手机号码
	@ViewInject(R.id.bt_get_checknum)
	private Button		bt_get_checknum;	// 获取验证码
	@ViewInject(R.id.ed_update_code)
	private EditText	ed_update_code;	// 验证码
	@ViewInject(R.id.ed_npassword)
	private EditText	ed_npassword;		// 新密码
	@ViewInject(R.id.upPasswowd)
	private Button		upPasswowd;		// 提交重设密码
	private String		phone	= "123";	// 手机号码
	private boolean		isFlag;
	private boolean		checkcode;			// 验证码是否正确
	private String		code;				// 验证码
	private String		password;			// 新密码
	private boolean		isregist;			// 是否已注册
	private Dialog		dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_get_pwd);
		ViewUtils.inject(this);
		initData();
	}

	@Override
	public void initData()
	{
		dialog = DialogUtil.getDialog(this);
		common_back.setVisibility(0);
		common_title.setText(ConstantValues.GETPWD);
		common_back.setOnClickListener(this);
		bt_get_checknum.setOnClickListener(this);
		upPasswowd.setOnClickListener(this);

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

			case R.id.bt_get_checknum:
				// 获取验证码
				phone = ed_update_phone.getText().toString().trim();
				if (phone.length() != 11)
				{
					Toast.makeText(this, "请输入正确的手机号码", 0).show();
					return;
				}

				getResetPwdCode();// 检测手机号是否已注册

//				if (isregist)
//				{
//					updateButton();
//				}

				break;

			case R.id.upPasswowd:

				phone = ed_update_phone.getText().toString().trim();

				code = ed_update_code.getText().toString().trim();

				password = DigestUtils.md5Hex(ed_npassword.getText().toString()
						.trim());
				if (phone.length() != 11)
				{
					Toast.makeText(this, "请输入正确的手机号码", 0).show();
					return;
				}

				if (CheckUtils.checkEmpty(code))
				{
					Toast.makeText(this, "验证码不能为空", 0).show();
					return;
				}

				if (ed_npassword.getText().toString().isEmpty())
				{
					Toast.makeText(this, "新密码不能为空", 0).show();
					return;
				}
				else
				{
					if (ed_npassword.getText().toString().length() < 6 || ed_npassword
							.getText().toString().length() > 16)
					{
						Toast.makeText(this, "密码必须为6-16位字符", 0).show();
						return;
					}
				}
				
				doResetPwd();

				break;
				
			default:
				break;
		}
	}

	// 检查手机号是否注册及获取验证码
	public void getResetPwdCode()
	{
		try
		{
			ServiceFactory.getIUserEngineInstatice()
					.getResetPwdCode(phone, "0", new IOAuthCallBack()
					{

						@Override
						public void getIOAuthCallBack(String result)
						{
							if (result.equals(ConstantValues.FAILURE))
							{
								Toast.makeText(getApplicationContext(), ConstantValues.NONETWORK, 0)
										.show();
							}
							else
							{
								InfoEntity bean = GsonTools.changeGsonToBean(result, InfoEntity.class);

								if ("1".equals(bean.getStatus()))
								{

									ToastUtils.showInfo(getBaseContext(), bean
											.getInfo());

									updateButton();
									
									isregist = true;
								}
								else
								{
									ToastUtils.showInfo(getBaseContext(), bean
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

	// 重置密码
	public void doResetPwd()
	{
		dialog.show();
		try
		{
			ServiceFactory.getIUserEngineInstatice()
					.doResetPwd(phone, password, code, new IOAuthCallBack()
					{

						@Override
						public void getIOAuthCallBack(String result)
						{
							if (result.equals(ConstantValues.FAILURE))
							{
								Toast.makeText(getApplicationContext(), ConstantValues.FAILURE, 0)
										.show();
							}
							else
							{
								InfoEntity bean = GsonTools.changeGsonToBean(result, InfoEntity.class);

								if ("1".equals(bean.getStatus()))
								{
									Toast.makeText(getApplicationContext(), bean
											.getInfo(), 0).show();
									finish();
								}
								else
								{
									Toast.makeText(getApplicationContext(), bean
											.getInfo(), 0).show();
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

	// 防止重复获取
	protected void updateButton()
	{
		bt_get_checknum.setBackgroundColor(getResources()
				.getColor(R.color.gray));
		bt_get_checknum.setClickable(false);
		final Handler ha = new Handler()
		{
			@SuppressLint("NewApi")
			@Override
			public void handleMessage(Message msg)
			{
				super.handleMessage(msg);
				if (msg.what == 1)
				{
					// 更新按钮文本
					int time = msg.arg1;
					if (time == 0)
					{
						bt_get_checknum.setText("重新获取");
						bt_get_checknum.setClickable(true);
						bt_get_checknum
								.setBackground(getResources()
										.getDrawable(R.drawable.bt_green_yuan_all_selector));
					}
					else
					{
						bt_get_checknum.setText(time + "秒");
					}
				}

			}
		};
		new Thread()
		{

			public void run()
			{
				try
				{
					int time = 60;// 60秒
					while (time > 0 && isFlag == false)
					{
						time--;
						Message msg = Message.obtain(ha, 1);
						msg.arg1 = time;
						msg.sendToTarget();
						sleep(1000);
					}
				}
				catch(InterruptedException e)
				{
					// ExceptionUtils.print(e);
				}
			};
		}.start();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		isFlag = true;// 停止计时
	}
}
