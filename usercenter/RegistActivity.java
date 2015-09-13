package me.wangolf.usercenter;

import org.apache.commons.codec.digest.DigestUtils;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.meigao.mgolf.R;

import me.wangolf.ConstantValues;
import me.wangolf.base.BaseActivity;
import me.wangolf.bean.CodeEntity;
import me.wangolf.bean.InfoEntity;
import me.wangolf.bean.usercenter.RegistEntity;
import me.wangolf.bean.usercenter.UserOptionEntity;
import me.wangolf.factory.ServiceFactory;
import me.wangolf.service.IOAuthCallBack;
import me.wangolf.utils.CheckUtils;
import me.wangolf.utils.DialogUtil;
import me.wangolf.utils.GsonTools;
import me.wangolf.utils.ShowPickUtils;
import me.wangolf.utils.ToastUtils;

public class RegistActivity extends BaseActivity implements OnClickListener
{
	@ViewInject(R.id.common_back)
	private Button			common_back;		// 后退

	@ViewInject(R.id.common_title)
	private TextView		common_title;		// 标题

	@ViewInject(R.id.common_bt)
	private TextView		common_bt;			// 地图

	@ViewInject(R.id.relativeLayoutNum)
	private RelativeLayout	relativeLayoutNum;	// 显示或隐 验证码

	@ViewInject(R.id.relayoutRecommend)
	private RelativeLayout	relayoutRecommend;	// 推存人

	@ViewInject(R.id.ed_reg_code)
	private EditText		ed_reg_code;		// 码证码

	@ViewInject(R.id.ed_reg_phone)
	private EditText		ed_reg_phone;		// 手机号

	@ViewInject(R.id.ed_pwd)
	private EditText		ed_pwd;				// 密码

	@ViewInject(R.id.bt_regist)
	private Button			bt_regist;			// 注册

	@ViewInject(R.id.getcode)
	private Button			getcode;			// 获取码证码

	@ViewInject(R.id.ed_recommend)
	private EditText		ed_recommend;		// 推存人号码

	@ViewInject(R.id.toAgreement)
	private TextView		toAgreement;		// 注册协议

	@ViewInject(R.id.checkBox1)
	private CheckBox		checkBox1;

	@ViewInject(R.id.tv_speech)
	private TextView		mSpeech;

	private String			phone;
	private String			code;
	private boolean			checkcode;			// true为正确
	private boolean			isregist;			// 是否开始验证
	private String			password;			// 密码
	private String			recommend;			// 推荐人号码
	private boolean			isFlag;
	private String			flag;
	private Dialog			dialog;
	private String			codeType	= "0";	// 验证码类型 短信或语音

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.ac_regist);
		
		ViewUtils.inject(this);
		
		initData();
	}

	@Override
	public void initData()
	{
		dialog = DialogUtil.getDialog(this);

		flag = getIntent().getStringExtra("flag");

		common_back.setVisibility(View.VISIBLE);

		common_title.setText(ConstantValues.REGIST);

		common_back.setOnClickListener(this);

		bt_regist.setOnClickListener(this);

		getcode.setOnClickListener(this);

		toAgreement.setOnClickListener(this);

		mSpeech.setOnClickListener(this);

		getData();

	}

	@Override
	public void getData()
	{
		dialog.show();
		try
		{

			ServiceFactory.getIUserEngineInstatice()
					.getUserOption(new IOAuthCallBack()
					{

						@Override
						public void getIOAuthCallBack(String result)
						{
							if (result.equals(ConstantValues.FAILURE))
							{
								ToastUtils.showInfo(RegistActivity.this, ConstantValues.NONETWORK);
							}
							else
							{

								UserOptionEntity bean = GsonTools
										.changeGsonToBean(result, UserOptionEntity.class);								
								
								if ("1".equals(bean.getStatus()))
								{
									UserOptionEntity data = bean.getData()
											.get(0);

									relativeLayoutNum
											.setVisibility(data.getRegist() == 0 ? View.VISIBLE : View.GONE);// 显示或隐

									relayoutRecommend
											.setVisibility(data.getPack() == 0 ? View.VISIBLE : View.GONE);

									// System.out.println(data.getRegist()+"*****");

									if (data.getRegist() == 0) isregist = true;// 开启验证

								}
							}

							dialog.cancel();
						}
					});

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	// 注册
	public void toRegist()
	{
		if (!checkBox1.isChecked())
		{
			ToastUtils.showInfo(this, "请先阅读使用条款");
			return;
		}
		phone = ed_reg_phone.getText().toString().trim();
		code = ed_reg_code.getText().toString().trim();
		password = DigestUtils.md5Hex(ed_pwd.getText().toString().trim());
		recommend = ed_recommend.getText().toString().trim();
		if (phone.length() != 11)
		{

			ToastUtils.showInfo(this, "请输入正确的手机号码");
			return;
		}
		if (ed_pwd.getText().toString().trim().length() < 6)
		{
			ToastUtils.showInfo(this, "用户密码不能为空");
			return;
		}

		if (isregist)
		{
			if (CheckUtils.checkEmpty(code))
			{
				ToastUtils.showInfo(this, "验证码不能为空");
				return;
			}
			doRegist();// 注册
		}
		else
		{
			doRegist();// 注册
		}
	}

	// 验证号码是否已经注册:如已经注册 提示"已经注册 返回"，如未注册：发送验证码。
	public void checkMobileRegist()
	{
		dialog.show();

		try
		{
			ServiceFactory.getIUserEngineInstatice()
					.toCheckMobileRegist(phone, codeType, new IOAuthCallBack()
					{

						@Override
						public void getIOAuthCallBack(String result)
						{

							if (result.equals(ConstantValues.FAILURE))
							{

								ToastUtils.showInfo(RegistActivity.this, ConstantValues.FAILURE);
							}
							else
							{
								CodeEntity bean = GsonTools
										.changeGsonToBean(result, CodeEntity.class);

								if ("1".equals(bean.getStatus()))
								{

									if ("0".equals(codeType)) updateButton();

									ToastUtils
											.showInfo(RegistActivity.this, bean
													.getInfo());

								}
								else
								{
									ShowPickUtils
											.ShowDialog(RegistActivity.this, bean
													.getInfo());
								}

							}
							dialog.cancel();
						}
					});
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

	// 提交注册
	public void doRegist()
	{
		dialog.show();
		try
		{
			ServiceFactory
					.getIUserEngineInstatice()
					.doRegist(phone, password, code, recommend, new IOAuthCallBack()
					{
						@Override
						public void getIOAuthCallBack(String result)
						{
							if (result.equals(ConstantValues.FAILURE))
							{

								ToastUtils
										.showInfo(RegistActivity.this, ConstantValues.FAILURE);
							}
							else
							{
								 
								RegistEntity bean = GsonTools
										.changeGsonToBean(result, RegistEntity.class);
								
								if ("1".equals(bean.getStatus()))
								{

									ToastUtils
											.showInfo(RegistActivity.this, bean
													.getInfo());
									Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
									
									intent.putExtra("phone", phone);
									
									intent.putExtra("password", password);
									
									intent.putExtra("flag", flag);
									
									setResult(ConstantValues.USERREGIST, intent);
									
									upView(bean.getData().get(0));
								}
								else
								{
									ToastUtils
											.showInfo(RegistActivity.this, bean
													.getInfo());

								}
							}
							dialog.cancel();
						}
					});
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @param bean
	 */
	private void upView(RegistEntity.DataEntity bean)
	{

		if ("0".equals(bean.getIs_effect()))
		{
			ShowPickUtils.ShowRegistDialog(bean, this);
		}
		else
		{
			finish();
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

			case R.id.bt_regist:

				toRegist();

				break;

			case R.id.getcode:

				codeType = "0";

				phone = ed_reg_phone.getText().toString().trim();

				if (phone.length() != 11)
				{

					ToastUtils.showInfo(RegistActivity.this, "请输入正确的手机号码");

					return;
				}

				checkMobileRegist();

				break;

			case R.id.toAgreement:

				Intent protocol = new Intent(this, RegistProtocolActivity.class);

				startActivity(protocol);

				break;

			case R.id.checkBox1:

				if (checkBox1.isChecked())
				{
					checkBox1.setChecked(false);
				}
				else
				{
					checkBox1.setChecked(true);
				}

				break;

			case R.id.tv_speech:

				ShowPickUtils.ShowSpeechDialog(this, 0);

				break;

			default:
				break;

		}

	}

	protected void updateButton()
	{
		getcode.setBackgroundColor(getResources().getColor(R.color.gray));
		getcode.setClickable(false);
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
						codeType = "0";
						// ed_reg_code.setHint("收不到短信,使用语音验证");
						// getcode.setText("语音验证码");
						getcode.setText("重新获取");

						getcode.setClickable(true);
						getcode.setBackground(getResources()
								.getDrawable(R.drawable.bt_green_yuan_all_selector));
					}
					else
					{
						getcode.setText(time + "秒");
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

	public void getSpeechCode()
	{
		codeType = "2";
		phone = ed_reg_phone.getText().toString().trim();
		if (phone.length() != 11)
		{
			ToastUtils.showInfo(RegistActivity.this, "请输入正确的手机号码");
			return;
		}

		checkMobileRegist();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		isFlag = true;// 停止计时
	}

	public void closeDialog()
	{
		finish();
	}

}
