package me.wangolf.usercenter;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.meigao.mgolf.R;

import me.wangolf.ConstantValues;
import me.wangolf.base.BaseActivity;
import me.wangolf.bean.InfoEntity;
import me.wangolf.factory.ServiceFactory;
import me.wangolf.service.IOAuthCallBack;
import me.wangolf.utils.GsonTools;

public class VoucherAddDialog extends BaseActivity implements OnClickListener {
	@ViewInject(R.id.cancle)
	private ImageView cancle;// 关闭
	@ViewInject(R.id.edsn)
	private EditText edsn;// 输入框
	@ViewInject(R.id.addOk)
	private Button addOk;// 确定
	private String uid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_voucher_dialog);
		ViewUtils.inject(this);
		initData();
	}

	@Override
	public void initData() {
		uid = ConstantValues.UID;
		cancle.setOnClickListener(this);
		addOk.setOnClickListener(this);
	}

	@Override
	public void getData() {

	}

	// 添加代金券
	public void addVouchers(String sn) {
		try {
			ServiceFactory.getIUserEngineInstatice().addVouchers(uid, sn, new IOAuthCallBack() {

				@Override
				public void getIOAuthCallBack(String result) {
					if (result.equals(ConstantValues.FAILURE)) {
						Toast.makeText(getApplicationContext(), ConstantValues.NONETWORK, 0).show();
					} else {
						InfoEntity bean = GsonTools.changeGsonToBean(result, InfoEntity.class);
						if ("1".equals(bean.getStatus())) {
							Toast.makeText(getApplicationContext(), bean.getInfo(), 0).show();
							finish();
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
		case R.id.cancle:
			finish();
			break;
		case R.id.addOk:
			String sn = edsn.getText().toString().trim();
			addVouchers(sn);
			break;
		default:
			break;
		}

	}
}
