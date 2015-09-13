package me.wangolf.usercenter;

/**
 * ============================================================
 * 
 * 版权 ：美高传媒 版权所有 (c) 2015年1月28日
 * 
 * 作者:copy
 * 
 * 版本 ：1.0
 * 
 * 创建日期 ： 2015年1月28日
 * 
 * 描述 ：用户账户充值
 * 
 * 
 * 修订历史 ：
 * 
 * ============================================================
 **/
import java.util.Date;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.meigao.mgolf.R;

import me.wangolf.ConstantValues;
import me.wangolf.base.BaseActivity;
import me.wangolf.bean.usercenter.OrderpayBean;
import me.wangolf.bean.usercenter.RechargerListEntity;
import me.wangolf.factory.ServiceFactory;
import me.wangolf.service.IOAuthCallBack;
import me.wangolf.utils.DateFormatUtils;
import me.wangolf.utils.DialogUtil;
import me.wangolf.utils.GsonTools;
import me.wangolf.utils.ToastUtils;

public class RechargeActivity extends BaseActivity implements OnClickListener,
		OnCheckedChangeListener
{
	@ViewInject(R.id.common_back)
	private Button		common_back;
	// 后退
	@ViewInject(R.id.common_title)
	private TextView	common_title;

	// @ViewInject(R.id.radio_1000)
	// private RadioButton radio_1000;
	// @ViewInject(R.id.radio_2000)
	// private RadioButton radio_2000;
	// @ViewInject(R.id.radio_5000)
	// private RadioButton radio_5000;
	// @ViewInject(R.id.radio_10000)
	// private RadioButton radio_10000;
	// @ViewInject(R.id.radio_20000)
	// private RadioButton radio_20000;
	// @ViewInject(R.id.radio_n)
	// private RadioButton radio_n;

	// @ViewInject(R.id.money_1000)
	// private LinearLayout money_1000;
	// @ViewInject(R.id.money_2000)
	// private LinearLayout money_2000;
	// @ViewInject(R.id.money_5000)
	// private LinearLayout money_5000;
	// @ViewInject(R.id.money_10000)
	// private LinearLayout money_10000;
	// @ViewInject(R.id.money_20000)
	// private LinearLayout money_20000;
	// @ViewInject(R.id.money_n)
	// private LinearLayout money_n;
	// @ViewInject(R.id.ed_otherprice)
	// private EditText ed_otherprice; // 其他金额
	@ViewInject(R.id.bt_next)
	private Button		bt_next;						// 提交充值
	private Double		order_amount			= 0.0;	// 充值金额
	private String		type					= "6";
	private String		uid;							// 用户iD
	private boolean		isother;						// 选择其他金额
	private int			mRadiocheckedId;
	private Dialog dialog;
	/**
	 * @Fields rdg_chargerlist : 充值金额列表
	 */
	@ViewInject(R.id.rdg_chargerlist)
	private RadioGroup	rdg_chargerlist;

	/**
	 * @Fields txt_topMessage : 顶部消息
	 */
	@ViewInject(R.id.txt_topMessage)
	private TextView	txt_topMessage;

	/**
	 * @Fields txt_bottomMessage : 底部消息
	 */
	@ViewInject(R.id.txt_bottomMessage)
	private TextView	txt_bottomMessage;
	
	@ViewInject(R.id.lyt_container)
	private LinearLayout lyt_container;
	
	RechargerListEntity	mRechargerListEntity	= null;

	private boolean		isRechargeListNull;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_recharge_new);

		ViewUtils.inject(this);

		initData();
				
		if(ConstantValues.ISLOGIN)
		{
			
			if (mRechargerListEntity == null) getData();
			
		}
		else	
		{
			
			Intent intent = new Intent(this, LoginActivity.class);
			
			intent.putExtra("flag", "recharge");

			startActivity(intent);
				
			finish();
			
		}
		
	}

	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		
		getData();		
		
	}
	

	/**
	 * @Title: loadChargeUI
	 * @Description: 加载充值界面数据
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	@SuppressLint("InflateParams")
	private void loadChargeUI()
	{

		if (!isRechargeListNull)
		{
			lyt_container.setVisibility(View.VISIBLE);
			
			txt_topMessage.setText(Html.fromHtml(mRechargerListEntity.getOther()
					.getTop_message().replace("&", "<br>")));
			
			txt_bottomMessage.setText(Html.fromHtml(mRechargerListEntity.getOther()
					.getButtom_message().replace("&", "<br>")));
			
			int baseViewId = 0xfffff0;
			
			for (RechargerListEntity.DataEntity data : mRechargerListEntity
					.getData())
			{

				RadioButton radio = (android.widget.RadioButton) getLayoutInflater()
						.inflate(R.layout.item_charger, null);

				radio.setId(baseViewId++);

				radio.setTag(data);
				
				if("1".equals(data.getImportant()))
				{
				radio.setText(Html.fromHtml("<big><b><font color='#ff0000'>￥&nbsp;" + data
						.getAmount() + "</font></b></big>&nbsp;&nbsp;&nbsp;&nbsp;<small><font color='#3d3d3d'>" + data
						.getMessage() + "</font></small>"));
				}
				else
				{
					radio.setText(Html.fromHtml("<big><font color='#ff0000'>￥&nbsp;" + data
							.getAmount() + "</font></big>&nbsp;&nbsp;&nbsp;&nbsp;<small><font color='#3d3d3d'>" + data
							.getMessage() + "</font></small>"));
					
				}
				
				radio.setChecked("1".equals(data.getDefault_amount()));

				radio.setPadding(48, 0, 8, 0);
				
				RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT);
				
				layoutParams.gravity = Gravity.CENTER_VERTICAL;
				
				layoutParams.leftMargin = 16;
				
				layoutParams.rightMargin = 16;
		
				rdg_chargerlist.addView(radio, layoutParams);

			}

		}
		else
		{
			return;
		}

	}

	
	
	@Override
	public void initData()
	{
		dialog = DialogUtil.getDialog(this);
		
		dialog.show();
		
		uid = ConstantValues.UID;

		common_back.setVisibility(View.VISIBLE);

		common_title.setText(ConstantValues.USERRECHARGE);

		common_back.setOnClickListener(this);
		// money_1000.setOnClickListener(this);
		// money_2000.setOnClickListener(this);
		// money_5000.setOnClickListener(this);
		// money_10000.setOnClickListener(this);
		// money_20000.setOnClickListener(this);
		// money_n.setOnClickListener(this);
		bt_next.setOnClickListener(this);

		rdg_chargerlist.setOnCheckedChangeListener(this);

	}

	@Override
	public void getData()
	{
		
		try
		{
			ServiceFactory.getIUserEngineInstatice().getRechargeList(ConstantValues.UID, new IOAuthCallBack()
			{
				
				@Override
				public void getIOAuthCallBack(String result)
				{
					
					if (result.equals(ConstantValues.FAILURE))
                    {
                        ToastUtils.showInfo(getBaseContext(), ConstantValues.NONETWORK);
                    } 
					else
					{
						
						mRechargerListEntity = GsonTools.changeGsonToBean(result, RechargerListEntity.class);
						
						if("1".equals(mRechargerListEntity.getStatus()))
						{
							
							isRechargeListNull = mRechargerListEntity.getData() == null ? true : false;
							
							loadChargeUI();
							
						}
						else
						{
							
							ToastUtils.showInfo(getBaseContext(), mRechargerListEntity.getInfo());
							
						}												
						
					}
							
				}
			});
		}
		catch(Exception e)
		{
			
			e.printStackTrace();
		}
		finally
		{
			
			dialog.cancel();
			
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
			// case R.id.money_1000:
			// RadioButton();
			// order_amount = 1000.0;
			// radio_1000.setChecked(true);
			// break;
			// case R.id.money_2000:
			// RadioButton();
			// order_amount = 2000.0;
			// radio_2000.setChecked(true);
			// break;
			// case R.id.money_5000:
			// RadioButton();
			// order_amount = 5000.0;
			// radio_5000.setChecked(true);
			// break;
			// case R.id.money_10000:
			// RadioButton();
			// order_amount = 10000.0;
			// radio_10000.setChecked(true);
			// break;
			// case R.id.money_20000:
			// RadioButton();
			// order_amount = 20000.0;
			// radio_20000.setChecked(true);
			// break;
			//
			// case R.id.money_n:
			//
			// RadioButton();
			//
			// ed_otherprice.setVisibility(View.VISIBLE);
			//
			// radio_n.setChecked(true);
			//
			// isother = true;
			//
			// break;

			case R.id.bt_next:

				toRechare();

				break;

			default:
				break;
		}
	}

	public void toRechare()
	{

		// if (isother)
		// {
		// if (CheckUtils
		// .checkEmpty(ed_otherprice.getText().toString().trim()))
		// {
		// // Toast.makeText(getApplicationContext(), "充值金额不能为空",
		// // 0).show();
		// ToastUtils.showInfo(RechargeActivity.this, "充值金额不能为空");
		// return;
		// }
		// order_amount = Double.parseDouble(ed_otherprice.getText()
		// .toString().trim());
		// }
		
		RechargerListEntity.DataEntity data = (RechargerListEntity.DataEntity) rdg_chargerlist.findViewById(rdg_chargerlist.getCheckedRadioButtonId()).getTag();
				
		order_amount = Double.valueOf(data.getAmount());
		
		if (order_amount <= 0)
		{
			ToastUtils.showInfo(this, "请选择充值金额");
			return;
		}

		String date = null;// 当前时间
		try
		{
			date = DateFormatUtils.formatDetail(new Date());
		}
		catch(Exception e)
		{
			
			e.printStackTrace();
		}

		
		OrderpayBean order_bean = new OrderpayBean();
		
		order_bean.setOut_trade_name("充值");
		
		order_bean.setType(type);

		order_bean.setUser_id(uid);

		order_bean.setConsumer_num(1);

		order_bean.setMobiel(ConstantValues.USER_MOBILE);

		order_bean.setArrival_time(date);

		order_bean.setRecommend_amount(data.getRecommend_amount());
		
		order_bean.setReturn_amount(data.getReturn_amount());
		
		order_bean.setOrder_amount(order_amount);
		// order_bean.setOrder_amount(0.1);
		Intent order = new Intent(this, OrderPayActivity.class);

		order.putExtra("order_bean", order_bean);

		order.putExtra("type", "recharge");

		startActivity(order);
	}

	
	public void RadioButton()
	{
		// radio_1000.setChecked(false);
		//
		// radio_2000.setChecked(false);
		//
		// radio_5000.setChecked(false);
		//
		// radio_10000.setChecked(false);
		//
		// radio_20000.setChecked(false);
		//
		// radio_n.setChecked(false);

		// ed_otherprice.setVisibility(View.GONE);

		isother = false;
	}

	/*
	 * (非 Javadoc)
	 * <p>Title: onCheckedChanged</p>
	 * <p>Description: 单选按钮事件</p>
	 * @param group
	 * @param checkedId
	 * @see
	 * android.widget.RadioGroup.OnCheckedChangeListener#onCheckedChanged(android
	 * .widget.RadioGroup, int)
	 */
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId)
	{
		mRadiocheckedId = checkedId;
	}
}
