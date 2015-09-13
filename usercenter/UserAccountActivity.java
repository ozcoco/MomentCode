package me.wangolf.usercenter;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meigao.mgolf.R;

import me.wangolf.ConstantValues;
import me.wangolf.base.BaseActivity;
import me.wangolf.utils.ToastUtils;

public class UserAccountActivity extends BaseActivity implements OnClickListener 
{
	@ViewInject(R.id.common_back)
	private Button common_back; // 后退
	@ViewInject(R.id.common_title)
	private TextView common_title;// 标题
	@ViewInject(R.id.my_paydetail)
	private RelativeLayout my_paydetail;// 消费明细	
	@ViewInject(R.id.my_updatepwd)
	private RelativeLayout my_updatepwd;//修改密码
	@ViewInject(R.id.my_ticket)
	private RelativeLayout my_ticket;// 点击代金券
	@ViewInject(R.id.my_rechare)
	private RelativeLayout my_rechare;// 点击用户充值
	@ViewInject(R.id.my_hongbao)
	private RelativeLayout my_hongbao;// 点击我的红包
	@ViewInject(R.id.account)
	private TextView account;// 我的余额
	@ViewInject(R.id.vouchers)
	private TextView vouchers;// 我的代金券
	@ViewInject(R.id.my_vip)
	private RelativeLayout my_vip;//会员

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.ac_account);
		
		ViewUtils.inject(this);
		
		initData();
	}

	@Override
	public void initData() 
	{
		common_back.setVisibility(View.VISIBLE);
		
		common_title.setText(ConstantValues.USERACCOUNT);
		
		common_back.setOnClickListener(this);
		
		my_paydetail.setOnClickListener(this);
		
		my_updatepwd.setOnClickListener(this);
		
		my_ticket.setOnClickListener(this);
		
		my_rechare.setOnClickListener(this);
		
		my_hongbao.setOnClickListener(this);
		
		my_vip.setOnClickListener(this);
		
		account.setText("￥" + (Double.valueOf(getIntent().getStringExtra("account"))).intValue());
		
//		vouchers.setText("￥" + getIntent().getStringExtra("vouchers"));

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
			
		case R.id.my_paydetail:
			
			Intent paydetail = new Intent(this, ConsumerDetailActivity.class);
			
			startActivity(paydetail);
			
			break;
			
		case R.id.my_updatepwd:
			
			Intent updatepwd = new Intent(this, UpDataPassword.class);
			
			startActivity(updatepwd);
			
			
			break;
			
		case R.id.my_ticket:
			
			Intent ticket = new Intent(this, VouchersListActivity.class);
			
			startActivity(ticket);
			
			break;
			
		case R.id.my_rechare:
			
			Intent rechare = new Intent(this, RechargeActivity.class);
			
			startActivity(rechare);
			
			break;
			
		case R.id.my_hongbao:
			
			Intent hongbao = new Intent(this, RedPackListActivity.class);
			
			startActivity(hongbao);
			
			break;
			
		case R.id.my_vip:
			
			ToastUtils.showInfo(UserAccountActivity.this, "暂停VIP开通");
			
			break;
			
		default:
			break;
			
		}

	}
}
