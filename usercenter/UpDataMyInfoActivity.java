package me.wangolf.usercenter;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.meigao.mgolf.R;

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

public class UpDataMyInfoActivity extends BaseActivity implements OnClickListener 
{
	@ViewInject(R.id.common_back)
	private Button common_back; // 后退
	@ViewInject(R.id.common_title)
	private TextView common_title;// 标题
	@ViewInject(R.id.common_bt)
	private TextView common_bt;// 地图
	@ViewInject(R.id.ednickname)
	private EditText ednickname;// 姓名
	@ViewInject(R.id.rdman)
	private RadioButton rdman;// 男
	@ViewInject(R.id.rdwoman)
	private RadioButton rdwoman;// 女
	@ViewInject(R.id.btsave)
	private Button btsave;// 保存
	private String nickname;// 呢程
	private String sex = "1";
	private Dialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_update_myinfo);
		ViewUtils.inject(this);
		initData();
	}

	@Override
	public void initData()
	{
		dialog = DialogUtil.getDialog(this);
		common_back.setVisibility(0);
		common_title.setText("修改资料");
		common_back.setOnClickListener(this);
		rdman.setOnClickListener(this);
		rdwoman.setOnClickListener(this);
		btsave.setOnClickListener(this);

	}

	@Override
	public void getData() {

	}

	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.common_back:
			finish();
			break;
		case R.id.rdman:
			rdwoman.setTextColor(getResources().getColor(R.color.common_text));
			rdman.setTextColor(getResources().getColor(R.color.white));
			rdman.setBackground(getResources().getDrawable(R.drawable.bt_red_rdman));
			rdwoman.setBackgroundColor(getResources().getColor(R.color.transparent));
			sex = "1";
			break;
		case R.id.rdwoman:
			rdman.setTextColor(getResources().getColor(R.color.common_text));
			rdwoman.setTextColor(getResources().getColor(R.color.white));
			rdwoman.setBackground(getResources().getDrawable(R.drawable.bt_red_rdwoman));
			rdman.setBackgroundColor(getResources().getColor(R.color.transparent));
			sex = "0";
			break;
		case R.id.btsave:
			upUserInfo();
			break;
		default:
			break;
		}
	}

	
	public void upUserInfo()
	{
		nickname = ednickname.getText().toString().trim();
		
		if (CheckUtils.checkEmpty(nickname)) 
		{
			ToastUtils.showInfo(UpDataMyInfoActivity.this, "请输入姓名");
			
			return;
		}

		UserInfoEntity.DataEntity bean = new UserInfoEntity.DataEntity();
		
		bean.setUser_id(ConstantValues.UID);
		
		bean.setNick_name(nickname);
		
		bean.setGender(sex);
		
		dialog.show();
		
		try 
		{
			ServiceFactory.getIUserEngineInstatice().upUserInfo(bean, new IOAuthCallBack() 
			{

				@Override
				public void getIOAuthCallBack(String result) 
				{
					if (result.equals(ConstantValues.FAILURE)) 
					{
						Toast.makeText(getApplicationContext(), ConstantValues.NONETWORK, 0).show();
					} 
					else 
					{
						InfoEntity bean = GsonTools.changeGsonToBean(result, InfoEntity.class);
						if ("1".equals(bean.getStatus())) 
						{
							ToastUtils.showInfo(UpDataMyInfoActivity.this, bean.getInfo());
							finish();
						} 
						else 
						{
							ToastUtils.showInfo(UpDataMyInfoActivity.this, bean.getInfo());
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
