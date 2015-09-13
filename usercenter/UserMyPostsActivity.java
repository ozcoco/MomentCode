package me.wangolf.usercenter;

/**
 * ============================================================
 * 
 * 版权 ：美高传媒 版权所有 (c) 下午2:31:05
 * 
 * 作者:copy
 * 
 * 版本 ：1.0
 * 
 * 创建日期 ： 下午2:31:05
 * 
 * 描述 ：16粉丝列表接口activity
 * 
 * 
 * 修订历史 ：
 * 
 * ============================================================
 **/

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.meigao.mgolf.R;

import me.wangolf.ConstantValues;
import me.wangolf.adapter.UserMyPostsAdapter;
import me.wangolf.base.BaseActivity;
import me.wangolf.bean.community.CommunityPostsEntity;
import me.wangolf.community.CommunityDetailActivity;
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

public class UserMyPostsActivity extends BaseActivity implements
		OnClickListener
{
	@ViewInject(R.id.common_title)
	private TextView				mTitle;
	@ViewInject(R.id.common_back)
	private TextView				mBack;
	@ViewInject(R.id.common_bt)
	private TextView				mBt;
	@ViewInject(R.id.pull_refresh_list)
	private PullToRefreshListView	mPullList;
	private String					user_id;
	private int						page	= 1;
	private int						number	= 10;
	private UserMyPostsAdapter		adapter;
	private boolean					isRefresh;		// 是否刷新
	private Dialog					dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_user_myposts);
		ViewUtils.inject(this);
		setPullRefresh();// 初始化下拉刷新
		initData();// 初始化数据
		if (adapter == null)
		{
			adapter = new UserMyPostsAdapter(this);
		}
		else
		{
			adapter.notifyDataSetChanged();
		}
		mPullList.getRefreshableView().setAdapter(adapter);

	}

	// 设置下拉刷新==========
	public void setPullRefresh()
	{
		mPullList.setPullLoadEnabled(false);
		// 滚动到底自动加载可用
		mPullList.setScrollLoadEnabled(true);
		// 得到实际的ListView 设置点击
		mPullList.getRefreshableView()
				.setOnItemClickListener(new OnItemClickListener()
				{

					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id)
					{

						if (CommonUtil.isNetworkAvailable(UserMyPostsActivity.this) == 0)
						{
							ToastUtils
									.showInfo(UserMyPostsActivity.this, ConstantValues.NONETWORK);
						}
						else
						{
							CommunityPostsEntity bean = (CommunityPostsEntity) adapter
									.getItem(position);
							Intent intent = new Intent(UserMyPostsActivity.this, CommunityDetailActivity.class);
							intent.putExtra("posts_id", bean.getId() + "");
							UserMyPostsActivity.this.startActivity(intent);
						}

					}
				});

		// 设置下拉刷新的listener
		mPullList.setOnRefreshListener(new OnRefreshListener<ListView>()
		{

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
			{
				isRefresh = true;
				page = 1;
				getData();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
			{

				page++;
				getData();
			}
		});
	}

	// 关闭刷新
	private void onLoaded()
	{
		mPullList.onPullDownRefreshComplete();
		mPullList.onPullUpRefreshComplete();
	}

	// 初始化数据=====
	@Override
	public void initData()
	{
		dialog = DialogUtil.getDialog(this);
		mBack.setVisibility(View.VISIBLE);
		mBack.setOnClickListener(this);
		user_id = CheckUtils.checkEmpty(getIntent().getStringExtra("user_id")) ? ConstantValues.UID : getIntent()
				.getStringExtra("user_id");
		mTitle.setText(CheckUtils.checkEmpty(getIntent()
				.getStringExtra("user_id")) ? "我的帖子" : "他的帖子");
		getData();
	}

	// 获取数据=======
	@Override
	public void getData()
	{
		dialog.show();
		try
		{
			ServiceFactory
					.getCommunityEngineInstatice()
					.getSelfPostsList(user_id, user_id, "", page, number, new IOAuthCallBack()
					{

						@Override
						public void getIOAuthCallBack(String result)
						{

							if (result.equals(ConstantValues.FAILURE))
							{
								ToastUtils
										.showInfo(UserMyPostsActivity.this, ConstantValues.NONETWORK);
							}
							else
							{
								CommunityPostsEntity bean = GsonTools
										.changeGsonToBean(result, CommunityPostsEntity.class);
								if ("1".equals(bean.getStatus()))
								{
									ArrayList<CommunityPostsEntity> data = bean
											.getData();
									ArrayList<CommunityPostsEntity> mList = adapter
											.getPosts_list();
									if (data.size() == 0)
									{
										// 没有更多数据
										ToastUtils
												.showInfo(UserMyPostsActivity.this, ConstantValues.NOMORE);
										dialog.cancel();
										onLoaded();
										return;
									}
									if (isRefresh)
									{
										// 下拉刷新
										isRefresh = false;
										// mList.clear();
										adapter.setPosts_list(data);
									}
									else
									{
										// mList == null初始化。mList ！= null 加载更多
										if (mList == null)
										{
											adapter.setPosts_list(data);
										}
										else
										{
											mList.addAll(data);
										}
									}
									adapter.notifyDataSetChanged();
								}
							}
							dialog.cancel();
							onLoaded();
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
			case R.id.common_back:
				finish();
				break;

			default:
				break;
		}

	}

}
