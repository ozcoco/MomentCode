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
 * 描述 ：消费明细
 * 
 * 
 * 修订历史 ：
 * 
 * ============================================================
 **/
import java.util.ArrayList;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.meigao.mgolf.R;

import me.wangolf.ConstantValues;
import me.wangolf.adapter.ConsumerDetailMainAdapter;
import me.wangolf.base.BaseActivity;
import me.wangolf.bean.usercenter.ConsumerDetail;
import me.wangolf.factory.ServiceFactory;
import me.wangolf.service.IOAuthCallBack;
import me.wangolf.utils.CommonUtil;
import me.wangolf.utils.GsonTools;
import me.wangolf.utils.ToastUtils;
import me.wangolf.utils.viewUtils.PullToRefreshBase;
import me.wangolf.utils.viewUtils.PullToRefreshListView;
import me.wangolf.utils.viewUtils.PullToRefreshBase.OnRefreshListener;

public class ConsumerDetailActivity extends BaseActivity implements OnClickListener 
{
	@ViewInject(R.id.common_back)
	private Button common_back; // 后退
	
	@ViewInject(R.id.common_title)
	private TextView common_title;// 标题
	
	@ViewInject(R.id.pull_refresh_list)
	private PullToRefreshListView pull_refresh_list; // 下拉刷新
	
	private ConsumerDetailMainAdapter adapter;
	
	private int page = 1; // 页数
	
	private int number = 10;// 条数
	
	private String user_id;// 用户ID
	
	private boolean ismore;// 是否有更多数据
	
	private boolean isR;// 是否上拉刷新
	
	private boolean ismoredata;// 是否下拉加载

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main_consumer_detail);
		
		ViewUtils.inject(this);
		
		initData();
		
		if (adapter == null) 
		{
			adapter = new ConsumerDetailMainAdapter(this);
			
			pull_refresh_list.getRefreshableView().setAdapter(adapter);
		} 
		else 
		{
			adapter.notifyDataSetChanged();
		}

		pull_refresh_list.setPullLoadEnabled(false);
		// 滚动到底自动加载可用
		pull_refresh_list.setScrollLoadEnabled(true);
		// 得到实际的ListView 设置点击
		pull_refresh_list.getRefreshableView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			}

		});

		// 设置下拉刷新的listener
		pull_refresh_list.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				page = 1;
				isR = true;
				getData();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				if (!ismore) {
					isR = false;
					ismoredata = true;
					page = page + 1;
					getData();
				}
			}
		});
	}

	@Override
	public void initData()
	{
		common_back.setVisibility(0);
		
		common_title.setText(ConstantValues.USERCONSUMER);
		
		common_back.setOnClickListener(this);
		
		user_id = ConstantValues.UID;
		
		getData();
	}

	@Override
	public void getData() 
	{
		try 
		{
			ServiceFactory.getIUserEngineInstatice().getConsumerDetail(user_id, page, number, new IOAuthCallBack() {

				@Override
				public void getIOAuthCallBack(String result) 
				{
					if (result.equals(ConstantValues.FAILURE))
					{
						Toast.makeText(getApplicationContext(), ConstantValues.NONETWORK, 0).show();
					} 
					else 
					{
						ConsumerDetail bean = GsonTools.changeGsonToBean(result, ConsumerDetail.class);
						
						if (bean.getData().size() == 0)
						{
							ismore = true;
							
							onLoaded();
							
							Toast.makeText(getApplicationContext(), ConstantValues.NOMORE, 0).show();
						}
						else 
						{
							ArrayList<ConsumerDetail> data = bean.getData();
							
							ArrayList<ConsumerDetail> list = (ArrayList<ConsumerDetail>) adapter.getmListItems();
							
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
							
							adapter.notifyDataSetChanged();
						}
					}
					
					onLoaded();
					
					setLastUpdateTime();
				}
			});
			
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.common_back:
			finish();
			break;

		default:
			break;
		}
	}

	private void setLastUpdateTime() {
		String text = CommonUtil.getStringDate();
		pull_refresh_list.setLastUpdatedLabel(text);
	}

	private void onLoaded() {
		pull_refresh_list.onPullDownRefreshComplete();
		pull_refresh_list.onPullUpRefreshComplete();
	}
}
