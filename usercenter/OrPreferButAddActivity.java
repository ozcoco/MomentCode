package me.wangolf.usercenter;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.meigao.mgolf.R;

import me.wangolf.ConstantValues;
import me.wangolf.base.BaseActivity;

public class OrPreferButAddActivity extends BaseActivity implements OnClickListener {
	@ViewInject(R.id.common_title)
	public TextView common_title;
	@ViewInject(R.id.common_back)
	public Button common_back;
	@ViewInject(R.id.common_bt)
	public TextView common_bt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_order_ball_add);
		ViewUtils.inject(this);
		initData();
	}

	@Override
	public void initData() {
		super.initData();
		common_back.setVisibility(0);
		common_bt.setVisibility(0);
		common_title.setText(ConstantValues.ONLINEPLAY);
		common_bt.setText(ConstantValues.ONLINEPLAY_PHONE);
		common_back.setOnClickListener(this);
	}

	@Override
	public void getData() {
		super.getData();
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

}
