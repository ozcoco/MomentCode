package me.wangolf.usercenter;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.meigao.mgolf.R;

import me.wangolf.ConstantValues;
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
 * 描述 ：用户的建议或意见
 * 
 * 
 * 修订历史 ：
 * 
 * ============================================================
 **/
import me.wangolf.base.BaseActivity;
import me.wangolf.bean.InfoEntity;
import me.wangolf.factory.ServiceFactory;
import me.wangolf.service.IOAuthCallBack;
import me.wangolf.utils.CheckUtils;
import me.wangolf.utils.GsonTools;

public class UserComment extends BaseActivity implements OnClickListener
{
	@ViewInject(R.id.common_back)
	private Button		common_back;	// 后退
	@ViewInject(R.id.common_title)
	private TextView	common_title;	// 标题
	@ViewInject(R.id.common_bt)
	private TextView	common_bt;		// 地图
	@ViewInject(R.id.ed_ball_name)
	private EditText	ed_ball_name;	// 意见内容
	@ViewInject(R.id.ivdel)
	private ImageView	ivdel;			// 清空内容
	private String		content;		// 发送的内容
	private String		uid;			// 用户iD

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_remarkers_content);
		ViewUtils.inject(this);
		initData();
	}

	@Override
	public void initData()
	{
		common_back.setVisibility(0);

		common_bt.setVisibility(0);

		common_title.setText(ConstantValues.USERCOMMENT);

		common_bt.setText("发送");

		common_back.setOnClickListener(this);

		common_bt.setOnClickListener(this);

		ivdel.setOnClickListener(this);
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

			case R.id.common_bt:
				// 发送内容
				content = ed_ball_name.getText().toString().trim();

				uid = ConstantValues.UID;

				if (CheckUtils.checkEmpty(content))
				{
					Toast.makeText(this, "请输入你的意见或建议", 0).show();
				}

				sendSuggestion();

				break;

			case R.id.ivdel:
				// 清空内容
				ed_ball_name.setText("");

			default:
				break;
		}
	}

	// 发送意见到服务器
	public void sendSuggestion()
	{
		try
		{
			ServiceFactory.getIUserEngineInstatice()
					.sendSuggestion(content, uid, new IOAuthCallBack()
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
									Toast.makeText(getApplicationContext(), "发送成功", 0)
											.show();
									// ed_ball_name.setText("");
									finish();
								}
								else
								{
									Toast.makeText(getApplicationContext(), bean
											.getInfo(), 0).show();
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
