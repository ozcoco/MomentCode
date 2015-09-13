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
 * 描述 ：代金券
 * 
 * 
 * 修订历史 ：
 * 
 * ============================================================
 **/
import java.util.ArrayList;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.meigao.mgolf.R;

import me.wangolf.ConstantValues;
import me.wangolf.GlobalConsts;
import me.wangolf.adapter.VouchersListAdapter;
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
 * 描述 ：代金券页面
 * 
 * 
 * 修订历史 ：
 * 
 * ============================================================
 **/
import me.wangolf.base.BaseActivity;
import me.wangolf.bean.usercenter.VouchersListEntity;
import me.wangolf.factory.ServiceFactory;
import me.wangolf.service.IOAuthCallBack;
import me.wangolf.utils.CheckUtils;
import me.wangolf.utils.GsonTools;

public class VouchersListActivity extends BaseActivity implements OnClickListener, OnItemClickListener {
	@ViewInject(R.id.common_back)
	private Button common_back; // 后退
	@ViewInject(R.id.common_title)
	private TextView common_title;// 标题
	@ViewInject(R.id.common_bt)
	private TextView common_bt;// 添加
	@ViewInject(R.id.lv)
	private ListView lv;// 列表
	@ViewInject(R.id.one)
	private LinearLayout one;// 可用代金券
	@ViewInject(R.id.two)
	private LinearLayout two;// 过期代金券
	@ViewInject(R.id.tv1)
	private TextView tv1;
	@ViewInject(R.id.tv2)
	private TextView tv2;
	private String uid; // 用户ID
	private String type = "0";// 代金券类型0代表可用代金券,1代表已过期或者未到期或者已使用完毕的代金券
	private VouchersListAdapter adapter;
	private String flag;// 支付页面过来的标记

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_vouchers_list);
		ViewUtils.inject(this);
		if (adapter == null) {
			adapter = new VouchersListAdapter(this);
		} else {
			adapter.notifyDataSetChanged();
		}
		initData();
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(this);
	}

	@Override
	public void initData() {
		common_back.setVisibility(0);
		common_bt.setVisibility(0);
		common_title.setText(ConstantValues.VOUCHERS);
		common_bt.setText("添加");
		common_back.setOnClickListener(this);
		common_bt.setOnClickListener(this);
		one.setOnClickListener(this);
		two.setOnClickListener(this);
		uid = ConstantValues.UID;
		flag = getIntent().getStringExtra("flag");
		getData();

	}

	@Override
	public void getData() {
		try {
			ServiceFactory.getIUserEngineInstatice().getVouchersList(uid, type, new IOAuthCallBack() {

				@Override
				public void getIOAuthCallBack(String result) {
					if (result.equals(ConstantValues.FAILURE)) {
						Toast.makeText(getApplicationContext(), ConstantValues.NONETWORK, 0).show();
					} else {
						VouchersListEntity bean = GsonTools.changeGsonToBean(result, VouchersListEntity.class);
						if ("1".equals(bean.getStatus())) {
							ArrayList<VouchersListEntity> data = bean.getData();
							adapter.setList(data);
							adapter.notifyDataSetChanged();

						} else {
							Toast.makeText(getApplicationContext(), bean.getInfo(), 0).show();
						}

					}

				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.common_back:
			Intent in = new Intent(this, OrderPayActivity.class);
			in.putExtra("vouchers_sn", "");
			in.putExtra("vouchers_amount", "");
			setResult(ConstantValues.VOUCHERSCODE, in);
			finish();
			break;
		case R.id.common_bt:
			Intent intent = new Intent(this, VoucherAddDialog.class);
			startActivityForResult(intent, GlobalConsts.addVoucherCode);
			break;
		case R.id.one:
			tv1.setTextColor(getResources().getColor(R.color.txt_green));
			tv2.setTextColor(getResources().getColor(R.color.txt_gray));
			type = "0";
			getData();
			break;
		case R.id.two:
			tv1.setTextColor(getResources().getColor(R.color.txt_gray));
			tv2.setTextColor(getResources().getColor(R.color.txt_green));
			type = "1";
			getData();
			break;
		default:
			break;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		getData();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (data != null && resultCode == GlobalConsts.addVoucherCode) {
			String sn = data.getStringExtra("sn");
			if (!CheckUtils.checkEmpty(sn)) {
				// 添加代金卷
				// addVoucher(sn);
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int arg2, long arg3) {
		if ("revouchers".equals(flag) & "0".equals(type)) {
			// 可用的代金券才能点
			VouchersListEntity bean = adapter.getItem(arg2);
			if (bean != null) {
				String vouchers_sn = bean.getSn();
				String vouchers_amount = bean.getMoney();
				Intent in = new Intent(this, OrderPayActivity.class);
				in.putExtra("vouchers_sn", vouchers_sn);
				in.putExtra("vouchers_amount", vouchers_amount);
				setResult(ConstantValues.VOUCHERSCODE, in);
				finish();
			}
		}
	}

}
