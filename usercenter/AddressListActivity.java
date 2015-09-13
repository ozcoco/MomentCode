package me.wangolf.usercenter;

import java.util.List;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import me.wangolf.ConstantValues;
import me.wangolf.adapter.AddressAdapter;
import me.wangolf.bean.usercenter.RespUserAdrrEntity;
import me.wangolf.bean.usercenter.RespUserAdrrEntity.DataEntity;
import me.wangolf.factory.ServiceFactory;
import me.wangolf.service.IOAuthCallBack;
import me.wangolf.shop.ProAtrrActivity;
import me.wangolf.utils.GsonTools;
import me.wangolf.utils.ToastUtils;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.meigao.mgolf.R;

public class AddressListActivity extends Activity implements
		OnItemClickListener, OnClickListener
{
	@ViewInject(R.id.common_back)
	private Button						common_back;			// 后退

	@ViewInject(R.id.common_title)
	private TextView					common_title;			// 标题

	@ViewInject(R.id.common_bt)
	private TextView					common_bt;				// 地图

	@ViewInject(R.id.address_list)
	private ListView					address_list;

	private String						type;					// 哪里来的

	private String						uid;					// 用户ID

	private AddressAdapter				adapter;

	RefreshReceiver						mRefreshReceiver;

	private static AddressListActivity	mAddressListActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.ac_address);

		mAddressListActivity = this;

		ViewUtils.inject(this);

		adapter = new AddressAdapter(this);

		address_list.setAdapter(adapter);

		address_list.setOnItemClickListener(this);

		initData();

	}

	public void initData()
	{
		common_back.setVisibility(0);

		common_bt.setVisibility(0);

		common_title.setText(ConstantValues.ADDRESS);

		common_bt.setText(ConstantValues.NEWADDRESS);

		common_back.setOnClickListener(this);

		common_bt.setOnClickListener(this);

		type = getIntent().getStringExtra("type");

		uid = ConstantValues.UID;

		getData();

	}

	public void getData()
	{
		try
		{

			ServiceFactory.getIUserEngineInstatice()
					.getUserAddr(uid, new IOAuthCallBack()
					{

						@Override
						public void getIOAuthCallBack(String result)
						{
							if (result.equals(ConstantValues.FAILURE))
							{
								ToastUtils.showInfo(getApplicationContext(), ConstantValues.NONETWORK);
							}
							else
							{

								RespUserAdrrEntity bean = GsonTools
										.changeGsonToBean(result, RespUserAdrrEntity.class);

								List<RespUserAdrrEntity.DataEntity> data = bean
										.getData();

								// if(!adapter.getList().isEmpty())
								// {
								// adapter.getList().clear();
								//
								// adapter.setList(null);
								// }

								adapter.getList().clear();

								adapter.getList().addAll(data);

								adapter.notifyDataSetChanged();
							}
						}
					});
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	{
		if ("proattr".equals(type))
		{
			RespUserAdrrEntity.DataEntity address = adapter.getItem(arg2);

			Intent in = new Intent(AddressListActivity.this, ProAtrrActivity.class);

			in.putExtra("address", address);

			setResult(ConstantValues.ADDRESS_CODE, in);

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

			case R.id.common_bt:

				Intent newaddress = new Intent(this, AddressEditActivity.class);

				startActivity(newaddress);

				break;

			default:

				break;
		}
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		// getData();

	}

	@Override
	protected void onStart()
	{

		super.onStart();

		registerRefreshReceiver();

	}

	@Override
	protected void onDestroy()
	{

		super.onDestroy();

		unregisterReceiver(mRefreshReceiver);

	}

	private void registerRefreshReceiver()
	{

		mRefreshReceiver = new RefreshReceiver();

		IntentFilter filter = new IntentFilter();

		filter.addAction(RefreshReceiver.UPDATE_DATA_ADD);

		filter.addAction(RefreshReceiver.UPDATE_DATA_DELETE);

		filter.addAction(RefreshReceiver.UPDATE_DATA_UPDATE);

		registerReceiver(mRefreshReceiver, filter);

	}

	public static class RefreshReceiver extends BroadcastReceiver
	{

		public final static String	UPDATE_DATA_DELETE	= "action.RefreshReceiver.DELETE";

		public final static String	UPDATE_DATA_UPDATE	= "action.RefreshReceiver.UPDATE";

		public final static String	UPDATE_DATA_ADD		= "action.RefreshReceiver.ADD";

		boolean						isUpdated			= false;

		@Override
		public void onReceive(Context context, Intent intent)
		{

			if (UPDATE_DATA_DELETE.equals(intent.getAction()))
			{

				RespUserAdrrEntity.DataEntity bean = (DataEntity) intent
						.getSerializableExtra(UPDATE_DATA_DELETE);

				delete: for (int i = 0; i < mAddressListActivity.adapter
						.getCount(); i++)
				{
					if (mAddressListActivity.adapter.getList().get(i).getId()
							.equals(bean.getId()))
					{

						mAddressListActivity.adapter.getList().remove(i);

						break delete;

					}

				}

				mAddressListActivity.adapter.notifyDataSetChanged();

			}

			if (UPDATE_DATA_ADD.equals(intent.getAction()))
			{

				FinalHttp http = new FinalHttp();
				
				AjaxParams params = new AjaxParams();
				
				params.put("terminal", "1");
				
				params.put("user_id", ConstantValues.UID);
				
				params.put("unique_key", ConstantValues.UNIQUE_KEY);
				
				http.get(ConstantValues.BaseApi+"webUser/getUserAddress", params, new AjaxCallBack<String>()
				{
					
					@Override
					public void onSuccess(String result)
					{
						
						
						RespUserAdrrEntity bean = GsonTools
								.changeGsonToBean(result, RespUserAdrrEntity.class);

						List<RespUserAdrrEntity.DataEntity> data = bean
								.getData();

						mAddressListActivity.adapter.getList()
								.clear();

						mAddressListActivity.adapter.getList()
								.addAll(data);

						mAddressListActivity.adapter
								.notifyDataSetChanged();
		
						
						super.onSuccess(result);
					}
					
					@Override
					public void onFailure(Throwable t, int errorNo, String strMsg)
					{
						
						super.onFailure(t, errorNo, strMsg);
					}
					
					
				});

			}

			if (UPDATE_DATA_UPDATE.equals(intent.getAction()))
			{

				RespUserAdrrEntity.DataEntity bean = (DataEntity) intent
						.getSerializableExtra(UPDATE_DATA_DELETE);

				update: for (int i = 0; i < mAddressListActivity.adapter
						.getCount(); i++)
				{
					if (mAddressListActivity.adapter.getList().get(i).getId()
							.equals(bean.getId()))
					{

						mAddressListActivity.adapter.getList().set(i, bean);

						break update;

					}

				}

				mAddressListActivity.adapter.notifyDataSetChanged();

			}

		}

	}

}
