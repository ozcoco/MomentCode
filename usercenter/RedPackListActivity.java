package me.wangolf.usercenter;

import java.util.ArrayList;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

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

import com.meigao.mgolf.R;

import me.wangolf.ConstantValues;
import me.wangolf.adapter.RedPackListAdapter;
import me.wangolf.base.BaseActivity;
import me.wangolf.bean.usercenter.RedPackEntity;
import me.wangolf.factory.ServiceFactory;
import me.wangolf.service.IOAuthCallBack;
import me.wangolf.utils.CheckUtils;
import me.wangolf.utils.CommonUtil;
import me.wangolf.utils.FileUtils;
import me.wangolf.utils.GsonTools;
import me.wangolf.utils.InflateView;
import me.wangolf.utils.ShareUtils;
import me.wangolf.utils.ToastUtils;
import me.wangolf.utils.Xutils;
import me.wangolf.utils.viewUtils.PullToRefreshBase;
import me.wangolf.utils.viewUtils.PullToRefreshListView;
import me.wangolf.utils.viewUtils.PullToRefreshBase.OnRefreshListener;

public class RedPackListActivity extends BaseActivity implements
		OnClickListener
{
	@ViewInject(R.id.common_back)
	private Button						common_back;		// 后退
	@ViewInject(R.id.common_title)
	private TextView					common_title;		// 标题
	@ViewInject(R.id.common_bt)
	private TextView					mBt;
	@ViewInject(R.id.get_red)
	private TextView					get_red;			// 获得的红包
	@ViewInject(R.id.share_red)
	private TextView					share_red;			// 分享的红包
	@ViewInject(R.id.pull_refresh_list)
	private PullToRefreshListView		pull_refresh_list;	// 下拉刷新
	@ViewInject(R.id.tv_amount)
	private TextView					mtAmount;
	private RedPackListAdapter			adapter;
	private int							page	= 1;		// 页数
	private int							number	= 10;		// 条数
	private String						user_id;			// 用户ID
	private boolean						ismore;				// 是否有更多数据
	private boolean						isR;				// 是否上拉刷新
	private boolean						ismoredata;			// 是否下拉加载
	private String						type	= "1";		// 1我获得的红包2我分享的红包
	private ArrayList<RedPackEntity>	list;
	private String						sharetitle;
	private String						picfile;
	private String						shareUrl;
	private String						imagename;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_redpacklsit);
		ViewUtils.inject(this);
		RelativeLayout head = (RelativeLayout) View
				.inflate(this, R.layout.item_redpack_head, null);
		ViewUtils.inject(this, head);
		pull_refresh_list.getRefreshableView().addHeaderView(head);
		initData();

		if (adapter == null)
		{
			adapter = new RedPackListAdapter(this);
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
		pull_refresh_list.getRefreshableView()
				.setOnItemClickListener(new OnItemClickListener()
				{
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id)
					{
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
						getData();
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
						}
					}
				});
	}

	@Override
	public void initData()
	{
		// 初始化数据
		common_back.setVisibility(View.VISIBLE);
		mBt.setVisibility(View.VISIBLE);
		mBt.setText(ConstantValues.SHARE);
		common_title.setText(ConstantValues.USERREDPACK);
		common_back.setOnClickListener(this);
		get_red.setOnClickListener(this);
		share_red.setOnClickListener(this);
		user_id = ConstantValues.UID;
		mBt.setOnClickListener(this);
		getData();
		sharetitle = "好友分享的红包";
	}

	@Override
	public void getData()
	{
		// 拿数据
		try
		{
			ServiceFactory
					.getIUserEngineInstatice()
					.getPacksList(user_id, type, page, number, new IOAuthCallBack()
					{

						@Override
						public void getIOAuthCallBack(String result)
						{
							if (result.equals(ConstantValues.FAILURE))
							{

								ToastUtils
										.showInfo(RedPackListActivity.this, ConstantValues.NONETWORK);

							}
							else
							{
								RedPackEntity bean = GsonTools
										.changeGsonToBean(result, RedPackEntity.class);

								if (bean != null && bean.getData().size() > 0)
								{

									mtAmount.setText("￥" + bean.getData()
											.get(0).getTotal_amount() + ".00");

									shareUrl = bean.getData().get(0)
											.getWeb_app_uri();

								}

								if (bean.getData().size() == 0)
								{
									ismore = true;

									onLoaded();

									ToastUtils
											.showInfo(RedPackListActivity.this, ConstantValues.NOMORE);

								}
								else
								{
									ArrayList<RedPackEntity> data = bean
											.getData();

									list = (ArrayList<RedPackEntity>) adapter
											.getList();
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
											adapter.setList(data);
										}
									}

								}

								adapter.notifyDataSetChanged();
							}

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

	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.common_back:
				finish();
				break;
			case R.id.get_red:
				// share_red.setBackgroundDrawable(getResources().getDrawable(R.drawable.bt_white_selector));
				// share_red.setTextColor(getResources().getColor(R.color.common_text));
				// get_red.setTextColor(getResources().getColor(R.color.white));
				// get_red.setBackgroundDrawable(getResources().getDrawable(R.drawable.bt_gray_semi_selector));
				// type = "1";
				// ArrayList<RedPackEntity> share_list =
				// (ArrayList<RedPackEntity>) adapter.getList();
				// if (share_list != null)
				// share_list.clear();// 清空分享红包数据
				// getData();
				break;
			case R.id.share_red:
//				 get_red.setBackgroundDrawable(getResources().getDrawable(R.drawable.bt_white_selector));
//				 get_red.setTextColor(getResources().getColor(R.color.common_text));
//				 share_red.setTextColor(getResources().getColor(R.color.white));
//				 share_red.setBackgroundDrawable(getResources().getDrawable(R.drawable.bt_gray_semi_selector));
//				 type = "2";
//				 ArrayList<RedPackEntity> getred_lists =
//				 (ArrayList<RedPackEntity>) adapter.getList();
//				 if (getred_lists != null)
//				 getred_lists.clear();// 清获得红包数据
//				 getData();
				break;
			case R.id.common_bt:

				sharetitle = "快来围观我在”全民高尔夫”获得的红包";
				if (!FileUtils.isFile("redpack.jpg"))
					FileUtils
							.saveBitToSD(FileUtils
									.drawableToBitamp(getResources()
											.getDrawable(R.drawable.dialog_optredpack)), "redpack");
				imagename = "redpack.jpg";
				ShareUtils
						.showShareandUrl(sharetitle, shareUrl, this, CheckUtils
								.checkEmpty(imagename) ? "" : imagename);
				break;
			default:
				break;
		}
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
