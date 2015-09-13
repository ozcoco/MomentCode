package me.wangolf.usercenter;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.meigao.mgolf.R;

import me.wangolf.ConstantValues;
import me.wangolf.adapter.OrderListAdapter;
import me.wangolf.base.BaseActivity;
import me.wangolf.bean.InfoEntity;
import me.wangolf.bean.usercenter.OrBallListEntity;
import me.wangolf.bean.usercenter.OrBallListEntity.DataEntity;
import me.wangolf.factory.ServiceFactory;
import me.wangolf.service.IOAuthCallBack;
import me.wangolf.utils.CheckUtils;
import me.wangolf.utils.CommonUtil;
import me.wangolf.utils.DialogUtil;
import me.wangolf.utils.GsonTools;
import me.wangolf.utils.ToastUtils;
import me.wangolf.utils.viewUtils.PullToRefreshBase;
import me.wangolf.utils.viewUtils.PullToRefreshListView;
import me.wangolf.utils.viewUtils.PullToRefreshBase.OnRefreshListener;

public class OrderListActivity extends BaseActivity implements OnClickListener
{
	@ViewInject(R.id.common_back)
	private Button											common_back;									// 后退

	@ViewInject(R.id.common_title)
	private TextView										common_title;									// 标题

	@ViewInject(R.id.common_bt)
	private TextView										common_bt;										// 地图

	private String											user_id;										// 用户ID

	private int												type				= 0;						// 类型0练习场1球场2商品

	private int												page				= 1;						// 页码

	private int												number				= 10;						// 大小

	@ViewInject(R.id.pull_refresh_list)
	private PullToRefreshListView							pull_refresh_list;								// 下拉刷新

	private OrderListAdapter<OrBallListEntity.DataEntity>	adapter;

	private List<OrBallListEntity.DataEntity>				data = new ArrayList<OrBallListEntity.DataEntity>();

	@ViewInject(R.id.rb_practice)
	private RadioButton										rb_practice;									// 练习场

	@ViewInject(R.id.rb_ball)
	private RadioButton										rb_ball;										// 球场

	@ViewInject(R.id.rb_shop)
	private RadioButton										rb_shop;										// 商品

	@ViewInject(R.id.relayout)
	private RelativeLayout									relayout;										// 没有更多
																											// 数据
	private boolean											ismore;										// 是否有更多数据

	private boolean											isR;											// 是否上拉刷新

	private boolean											ismoredata;									// 是否下拉加载

	private Dialog											dialog;

	/**
	 * @Fields mRefreshReceiver : 更新数据
	 */
	RefreshReceiver											mRefreshReceiver	= null;

	public static String									ACTION_REFRESH_DATA	= "action.refresh.data";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.order_list_main);

		ViewUtils.inject(this);

		if (adapter == null)
		{
			adapter = new OrderListAdapter<OrBallListEntity.DataEntity>(this);

			adapter.setmListItems(data);
			
			pull_refresh_list.getRefreshableView().setAdapter(adapter);
		}
		else
		{
			adapter.notifyDataSetChanged();
		}

		initData();

		pull_refresh_list.setPullLoadEnabled(false);
		// 滚动到底自动加载可用
		pull_refresh_list.setScrollLoadEnabled(true);
		// 得到实际的ListView 设置点击
		pull_refresh_list.getRefreshableView()
				.setOnItemClickListener(new OnItemClickListener()
				{

					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id)
					{

						OrBallListEntity.DataEntity bean = (OrBallListEntity.DataEntity) adapter.getItem(position);

						if (bean != null)
						{
							Intent order_info = new Intent(getApplicationContext(), OrderInfoActivity.class);

							order_info.putExtra("type", type + "");

							order_info.putExtra("bean", bean);

							startActivity(order_info);
						}
					}

				});

		// 设置下拉刷新的listener
		pull_refresh_list
				.setOnRefreshListener(new OnRefreshListener<ListView>()
				{

					@Override
					public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
					{
						// 上拉
						page = 1;

						isR = true;

						ismore = false;

						getData();
						
//						refreshData();
					}

					@Override
					public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
					{
						// 下拉
						if (!ismore)
						{
							isR = false;
							
							ismoredata = true;
							
							page = page + 1;
							
							getData();
							
//							refreshData();
						}
					}
				});
	}

	
	@Override
	public void initData()
	{
		dialog = DialogUtil.getDialog(this);

		common_back.setVisibility(0);

		common_title.setText(ConstantValues.MY_ORDER);

		common_back.setOnClickListener(this);

		user_id = ConstantValues.UID;

		if (!CheckUtils.checkEmpty(getIntent().getStringExtra("type")))
			type = Integer.parseInt(getIntent().getStringExtra("type"));

		rb_practice.setOnClickListener(this);

		rb_ball.setOnClickListener(this);

		rb_shop.setOnClickListener(this);
		// 查看订单列表时
		switch (type)
		{
			case 0:

				setRadioButton();

				rb_practice.setChecked(true);

				rb_practice
						.setTextColor(getResources().getColor(R.color.white));

				break;

			case 1:

				setRadioButton();

				rb_ball.setChecked(true);

				rb_ball.setTextColor(getResources().getColor(R.color.white));

				break;

			case 2:

				setRadioButton();

				rb_shop.setChecked(true);

				rb_shop.setTextColor(getResources().getColor(R.color.white));

				break;

			default:
				break;

		}

		getData();
	}


	
	@Override
	public void getData()
	{

		dialog.show();

		try
		{
			ServiceFactory
					.getIUserEngineInstatice()
					.getOrderList(user_id, type, page, number, new IOAuthCallBack()
					{

						private List<DataEntity>	list;

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
								
								OrBallListEntity bean = GsonTools
										.changeGsonToBean(result, OrBallListEntity.class);

								if (bean.getData().size() == 0)
								{
									ismore = true;

									if (!ismoredata) relayout.setVisibility(0);

									onLoaded();

									Toast.makeText(getApplicationContext(), ConstantValues.NOMORE, 0)
											.show();
								}
								else
								{		
									list = adapter.getmListItems();

									relayout.setVisibility(8);

									if (isR)
									{
										list.clear();

										list.addAll(bean.getData());
									}
									else
									{
										if (list != null & ismoredata)
										{
											list.addAll(bean.getData());
										}
										else
										{
											adapter.setmListItems(bean.getData());
										}
									}

								}

								adapter.notifyDataSetChanged();
							}

							dialog.cancel();

							onLoaded();

							setLastUpdateTime();

						}
					});

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.rb_practice:

				setRadioButton();

				rb_practice
						.setTextColor(getResources().getColor(R.color.white));

				type = 0;

				page = 1;

				ismore = false;

				List<OrBallListEntity.DataEntity> practice_lists = adapter
						.getmListItems();

				if (practice_lists != null) practice_lists.clear();// 清获数据

//				getData();

				refreshData();
				
				break;

			case R.id.rb_ball:

				setRadioButton();

				rb_ball.setTextColor(getResources().getColor(R.color.white));

				type = 1;

				page = 1;

				ismore = false;

				List<OrBallListEntity.DataEntity> ball_lists = adapter
						.getmListItems();

				if (ball_lists != null) ball_lists.clear();// 清获数据

//				getData();

				refreshData();
				
				break;

			case R.id.rb_shop:

				setRadioButton();

				rb_shop.setTextColor(getResources().getColor(R.color.white));

				type = 2;

				page = 1;

				ismore = false;

				List<OrBallListEntity.DataEntity> shop_lists = adapter
						.getmListItems();

				if (shop_lists != null) shop_lists.clear();// 清获数据

//				getData();

				refreshData();
				
				break;

			case R.id.common_back:
				
				finish();
				
			default:
				break;
		}
	}

	// 回调 取消订单
	public void toOrderCancel(String sn)
	{
		try
		{
			ServiceFactory.getIUserEngineInstatice()
					.toOrderCancel(sn, new IOAuthCallBack()
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
									ToastUtils
											.showInfo(getApplicationContext(), bean
													.getInfo());

									getBaseContext()
											.sendBroadcast(new Intent(ACTION_REFRESH_DATA));

								}
								else
								{
									ToastUtils
											.showInfo(getApplicationContext(), bean
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

	// 去支付订单
	public void toPayOrder(String sn, String order_amount)
	{
		if (!CheckUtils.checkEmpty(sn))
		{
			Intent pay_order = new Intent(this, OrderPayActivity.class);

			pay_order.putExtra("sn", sn);

			pay_order.putExtra("flag", "order_center");

			pay_order.putExtra("type", type + "");

			pay_order.putExtra("order_amount", order_amount);

			this.startActivity(pay_order);
		}

	}

	@Override
	public void onResume()
	{

		if (ConstantValues.ISTOPAY)
		{
			ConstantValues.ISTOPAY = false;// 使用完 回默认

			page = 1;

			isR = true;

			ismore = false;

			getData();

		}

		super.onResume();
	}

	// 设radiobutton 字体色
	public void setRadioButton()
	{
		rb_practice.setTextColor(getResources().getColor(R.color.gray));

		rb_ball.setTextColor(getResources().getColor(R.color.gray));

		rb_shop.setTextColor(getResources().getColor(R.color.gray));
	}

	private void setLastUpdateTime()
	{
		String text = CommonUtil.getStringDate();

		pull_refresh_list.setLastUpdatedLabel(text);
	}

	private void onLoaded()
	{
		pull_refresh_list.onPullDownRefreshComplete();

		pull_refresh_list.onPullUpRefreshComplete();
	}

	/**
	 * @Title: initRefreshReceiver
	 * @Description: 更新数据
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	private void initRefreshReceiver()
	{

		mRefreshReceiver = new RefreshReceiver();

		IntentFilter filter = new IntentFilter(ACTION_REFRESH_DATA);

		registerReceiver(mRefreshReceiver, filter);

	}

	/**
	 * @Title: refreshData
	 * @Description: 刷新数据
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	void refreshData()
	{

		dialog.show();
		
		FinalHttp http = new FinalHttp();
		
		AjaxParams params = new AjaxParams();
		
		params.put("terminal", "1");
		
		params.put("user_id", ConstantValues.UID);
		
		params.put("unique_key", ConstantValues.UNIQUE_KEY);
		
		params.put("type", "" + type);
		
		params.put("page", "" + page);
		
		params.put("number", "" + number);
				
		http.get(ConstantValues.BaseApi+"webUserOrder/orderList", params, new AjaxCallBack<String>()
		{
			
			@Override
			public void onSuccess(String result)
			{
				
				OrBallListEntity bean = GsonTools
						.changeGsonToBean(result, OrBallListEntity.class);
				
				if(page == 1) adapter.getmListItems().clear();
				
				adapter.getmListItems().addAll(bean.getData());
				
				adapter.notifyDataSetChanged();
				
				pull_refresh_list.invalidate();
				
				dialog.cancel();	
				
				super.onSuccess(result);
			}
			
			
			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg)
			{
				
				super.onFailure(t, errorNo, strMsg);
			}
			
		});
		
	}

	@Override
	protected void onStart()
	{
		initRefreshReceiver();

		super.onStart();
	}

	@Override
	protected void onDestroy()
	{

		unregisterReceiver(mRefreshReceiver);

		super.onDestroy();
	}

	public final class RefreshReceiver extends BroadcastReceiver
	{

		@Override
		public void onReceive(Context context, Intent intent)
		{

			refreshData();

		}

	}

}
