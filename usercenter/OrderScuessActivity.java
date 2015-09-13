package me.wangolf.usercenter;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.meigao.mgolf.R;

import me.wangolf.ConstantValues;
import me.wangolf.base.BaseActivity;
import me.wangolf.bean.usercenter.PlaySuccessEntity;
import me.wangolf.factory.ServiceFactory;
import me.wangolf.service.IOAuthCallBack;
import me.wangolf.utils.CheckUtils;
import me.wangolf.utils.FileUtils;
import me.wangolf.utils.GsonTools;
import me.wangolf.utils.ShareUtils;
import me.wangolf.utils.ShowPickUtils;
import me.wangolf.utils.ToastUtils;

public class OrderScuessActivity extends BaseActivity implements
		OnClickListener
{
	@ViewInject(R.id.common_back)
	private Button		common_back;	// 后退
	@ViewInject(R.id.common_title)
	private TextView	common_title;	// 标题
	@ViewInject(R.id.common_bt)
	private TextView	common_bt;		// 电话
	@ViewInject(R.id.tv_message)
	private TextView	tv_message;		// 信息
	@ViewInject(R.id.btok)
	private Button		btok;			// 查看订单
	@ViewInject(R.id.tv_tip)
	private TextView	tv_tip;			// 提示
	@ViewInject(R.id.tv_pop)
	private TextView	mPop;
	@ViewInject(R.id.tv_success_title)
	private TextView	mTitle;
	@ViewInject(R.id.tv_success_content)
	private TextView	mComtent;
	@ViewInject(R.id.tv_close_share)
	private Button		mColse;
	@ViewInject(R.id.tv_share)
	private Button		mShare;
	private String		sn;
	private String		user_id;
	private String		payment;
	private String		title;
	private String		type;
	private String		message;
	private String		flag;
	private PopupWindow	mWindows;
	private String		shareUrl;
	private String		sharetitle;
	private String		share_content;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.ac_opt_ball_result);

		ViewUtils.inject(this);
		
		initData();
	}

	@Override
	public void initData()
	{
		View layout = View
				.inflate(this, R.layout.item_ordersuccess_popwindows, null);
		
		ViewUtils.inject(this, layout);
		
		common_back.setVisibility(View.VISIBLE);
		
		// common_bt.setVisibility(View.VISIBLE);
		
		common_title.setText("支付结果");
		
		common_bt.setBackgroundResource(R.drawable.bt_phone_selector);
		
		common_back.setOnClickListener(this);
		
		common_bt.setOnClickListener(this);
		
		btok.setOnClickListener(this);
		
		mColse.setOnClickListener(this);
		
		mShare.setOnClickListener(this);
		
		sn = getIntent().getStringExtra("sn");
		
		user_id = getIntent().getStringExtra("user_id");
		
		payment = getIntent().getStringExtra("payment");
		
		message = getIntent().getStringExtra("message");
		
		title = getIntent().getStringExtra("title");
		
		type = getIntent().getStringExtra("type");
		
		flag = getIntent().getStringExtra("flag");
		
		tv_tip.setText(title);
		
		tv_message.setText(message);

		mWindows = new PopupWindow(layout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

		getData();
	}

	@Override
	public void getData()
	{

		try
		{

			ServiceFactory.getIUserEngineInstatice()
					.toPay(sn, user_id, payment, new IOAuthCallBack()
					{

						@Override
						public void getIOAuthCallBack(String result)
						{
							PlaySuccessEntity beans = GsonTools.changeGsonToBean(result, PlaySuccessEntity.class);						
							
							if (1 == beans.getStatus())
							{
								if(beans.getData().isEmpty()) return;
								
								PlaySuccessEntity bean = beans.getData().get(0);

								if ("0".equals(bean.getGift()))
								{
									mWindows.showAsDropDown(mPop);

									mWindows.isShowing();

									mComtent.setText(bean.getShare_content());

									mTitle.setText(bean.getShare_title());

									sharetitle = bean.getShow_title();

									share_content = bean.getShow_content();

									shareUrl = bean.getWeb_app_uri();
								}
							}
							else
							{
								ToastUtils
										.showInfo(OrderScuessActivity.this, beans
												.getInfo());
							}

						}
					});
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

	// 查看订单列表
	public void toMyOrderlist()
	{
		Intent my_order = new Intent(this, OrderListActivity.class);

		if ("order_center1".equals(flag))
		{
			// 来自订单中心 付款后关闭
			// ConstantValues.ISTOPAY = true;// 用于返回刷新
			// Intent my_event = new Intent(this, UserEventListActivity.class);
			// my_event.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			// startActivity(my_event);
			// finish();
			// return;
		}
		if ("6".equals(type))
		{
			// 充值成功关闭
			finish();
		}
		else if ("4".equals(type))
		{
			Intent my_event = new Intent(this, UserEventListActivity.class);
			my_event.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(my_event);
			finish();
		}
		else
		{
			// 其他跳转后关闭
			if ("3".equals(type)) type = "2";
			my_order.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			my_order.putExtra("type", type);
			startActivity(my_order);
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
			case R.id.btok:
				toMyOrderlist();
				break;
			case R.id.tv_close_share:
				ShowPickUtils.ShowShareDialog(this);
				break;
			case R.id.tv_share:
				if (!FileUtils.isFile("redpack_img.jpg"))
					FileUtils
							.saveBitToSD(FileUtils
									.drawableToBitamp(getResources()
											.getDrawable(R.drawable.app_logo)), "redpack_img");
				String imagename = "redpack_img.jpg";
				ShareUtils
						.showShareandUrl(sharetitle, share_content, shareUrl, this, CheckUtils
								.checkEmpty(imagename) ? "" : imagename);
				break;
			default:
				break;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void closeShare()
	{
		mWindows.dismiss();
	}
}
