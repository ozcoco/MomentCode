package me.wangolf.usercenter;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.meigao.mgolf.R;

import me.wangolf.ConstantValues;
import me.wangolf.adapter.EventListAdapter;
import me.wangolf.adapter.MyEventListAdapter;
import me.wangolf.base.BaseActivity;
import me.wangolf.bean.InfoEntity;
import me.wangolf.bean.event.PublishEventEntity;
import me.wangolf.bean.usercenter.BaseOrderEntity;
import me.wangolf.bean.usercenter.OrBallListEntity;
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

public class UserEventListActivity extends BaseActivity implements
		OnClickListener
{

	@ViewInject(R.id.common_back)
	private Button											common_back;		// 后退
	@ViewInject(R.id.common_title)
	private TextView										common_title;		// 标题
	@ViewInject(R.id.common_bt)
	private TextView										common_bt;			// 地图
	private String											user_id;			// 用户ID
	private int												type	= 3;		// 类型0练习场1球场2商品3活动
	private int												page	= 1;		// 页码
	private int												number	= 10;		// 大小
	@ViewInject(R.id.pull_refresh_list)
	private PullToRefreshListView							pull_refresh_list;	// 下拉刷新
	private EventListAdapter<OrBallListEntity.DataEntity>	adapter;
	private List<OrBallListEntity.DataEntity>				data;
	private MyEventListAdapter								my_adapter;
	@ViewInject(R.id.rb_event)
	private TextView										rb_event;			// 我参加的活动
	@ViewInject(R.id.rb_PublishEvent)
	private TextView										rb_PublishEvent;	// 我发起的活动
	@ViewInject(R.id.relayout)
	private RelativeLayout									relayout;			// 没有更多
																				// 数据
	private boolean											ismore;				// 是否有更多数据
	private boolean											isR;				// 是否上拉刷新
	private boolean											ismoredata;			// 是否下拉加载
	private List<OrBallListEntity.DataEntity>				list;				// 参加的活动
	private ArrayList<PublishEventEntity>					my_event;			// 发起的活动
	private int												flag	= 0;		// 0为参加的活用1为发起的活动
																				// 用于判断跳转
	private Dialog											dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event_list_main);
		ViewUtils.inject(this);
		if (adapter == null)
		{
			adapter = new EventListAdapter<OrBallListEntity.DataEntity>(this);
			my_adapter = new MyEventListAdapter(this);
			pull_refresh_list.getRefreshableView().setAdapter(adapter);

		}
		else
		{
			my_adapter.notifyDataSetChanged();
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
						switch (flag)
						{
							case 0:
								// 参加的活动跳转
								OrBallListEntity.DataEntity bean = (OrBallListEntity.DataEntity) adapter.getItem(position);
								
								if (bean != null)
								{
									Intent order_info = new Intent(getApplicationContext(), OrderInfoActivity.class);
									
									order_info.putExtra("type", type + "");
									
									order_info.putExtra("bean", bean);
									
									startActivity(order_info);
								}
								break;
							case 1:
								// 发起的活动跳转
								break;
							default:
								break;
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
						if (flag == 0)
						{
							getData();
						}
						else
						{
							getMyPublishEvent();
						}

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
							if (flag == 0)
							{
								getData();
							}
							else
							{
								getMyPublishEvent();
							}

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
		rb_event.setOnClickListener(this);
		rb_PublishEvent.setOnClickListener(this);
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

								Log.i("特惠订单列表", result);
								
								OrBallListEntity bean = GsonTools
										.changeGsonToBean(result, OrBallListEntity.class);

								if (bean.getData().size() == 0)
								{
									ismore = true;

									onLoaded();

									if (!ismoredata) relayout.setVisibility(0);

									Toast.makeText(getApplicationContext(), ConstantValues.NOMORE, 0)
											.show();

								}
								else
								{
									relayout.setVisibility(8);

									data = bean.getData();

									list = adapter.getmListItems();

									if (isR)
									{
										list.clear();

										list.addAll(data);
									}
									else
									{
										if (list != null & ismoredata)
										{
											list.addAll(data);
										}
										else
										{
											adapter.setmListItems(data);
										}
									}

								}

								adapter.notifyDataSetChanged();
							}

							onLoaded();

							setLastUpdateTime();

							dialog.cancel();
						}
					});
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

	// 我发布的活动
	public void getMyPublishEvent()
	{
		dialog.show();
		try
		{
			ServiceFactory
					.getIUserEngineInstatice()
					.getMyPublishEvent(user_id, page, number, new IOAuthCallBack()
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
								PublishEventEntity bean = GsonTools
										.changeGsonToBean(result, PublishEventEntity.class);
								if ("1".equals(bean.getStatus()))
								{
									if (bean.getData().size() == 0)
									{
										ismore = true;
										onLoaded();
										Toast.makeText(getApplicationContext(), ConstantValues.NOMORE, 0)
												.show();
									}
									else
									{
										ArrayList<PublishEventEntity> data = bean
												.getData();
										my_event = my_adapter.getmListItems();
										if (isR)
										{
											if (my_event != null)
												my_event.clear();
											my_event.addAll(data);
										}
										else
										{
											if (my_event != null & ismoredata)
											{
												my_event.addAll(data);
											}
											else
											{
												my_adapter.setmListItems(data);
											}
										}

									}
									my_adapter.notifyDataSetChanged();

								}
								else
								{
									ToastUtils
											.showInfo(UserEventListActivity.this, bean
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

	@SuppressLint("NewApi")
	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.rb_event:
				setRadioButton();
				rb_event.setTextColor(getResources().getColor(R.color.white));
				rb_event.setBackground(getResources()
						.getDrawable(R.drawable.rd_message_l));
				type = 3;
				page = 1;
				flag = 0;
				isR = false;
				ismore = false;
				ismoredata = false;
				my_event = my_adapter.getmListItems();
				if (my_event != null) my_event.clear();// 清获数据
				my_adapter.notifyDataSetChanged();
				getData();
				pull_refresh_list.getRefreshableView().setAdapter(adapter);
				break;
			case R.id.rb_PublishEvent:
				setRadioButton();
				rb_PublishEvent.setTextColor(getResources()
						.getColor(R.color.white));
				rb_PublishEvent.setBackground(getResources()
						.getDrawable(R.drawable.rd_message_r));
				page = 1;
				flag = 1;
				isR = false;
				ismoredata = false;
				ismore = false;
				list = adapter.getmListItems();

				if (list != null) list.clear();// 清获数据

				adapter.notifyDataSetChanged();

				getMyPublishEvent();

				pull_refresh_list.getRefreshableView().setAdapter(my_adapter);

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
									getData();
									adapter.notifyDataSetChanged();
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
			pay_order.putExtra("type", (type + 1) + "");
			pay_order.putExtra("order_amount", order_amount);
			this.startActivity(pay_order);
		}

	}

	@Override
	public void onResume()
	{

		super.onResume();
		if (ConstantValues.ISTOPAY)
		{
			ConstantValues.ISTOPAY = false;// 使用完 回默认
			page = 1;
			isR = true;
			ismore = false;
			getData();
		}

	}

	// 设radiobutton 字体色
	public void setRadioButton()
	{
		rb_event.setBackgroundColor(getResources()
				.getColor(R.color.transparent));
		rb_PublishEvent.setBackgroundColor(getResources()
				.getColor(R.color.transparent));
		rb_event.setTextColor(getResources().getColor(R.color.common_text_h1));
		rb_PublishEvent.setTextColor(getResources()
				.getColor(R.color.common_text_h1));

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

}
