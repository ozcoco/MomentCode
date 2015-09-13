package me.wangolf.usercenter;

/**
 * ============================================================
 * 
 * 版权 ：美高传媒 版权所有 (c) 2015年1月27日
 * 
 * 作者:copy
 * 
 * 版本 ：1.0
 * 
 * 创建日期 ： 2015年1月27日
 * 
 * 描述 ：注册协议
 * 
 * 
 * 修订历史 ：
 * 
 * ============================================================
 **/
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.meigao.mgolf.R;

import me.wangolf.ConstantValues;
import me.wangolf.GlobalConsts;
import me.wangolf.base.BaseActivity;

public class RegistProtocolActivity extends BaseActivity implements OnClickListener {
	@ViewInject(R.id.common_back)
	private Button common_back; // 后退
	@ViewInject(R.id.common_title)
	private TextView common_title;// 标题
	@ViewInject(R.id.pre_webview)
	private WebView pre_webview;// web
	private String url;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_preferbuy_pictxt);
		ViewUtils.inject(this);
		initData();
	}

	@Override
	public void initData() {
		common_back.setVisibility(0);
		common_title.setText(ConstantValues.PROTOCOL);
		common_back.setOnClickListener(this);
		url = GlobalConsts.RegistURL;
		getData();
	}

	@Override
	public void getData() {
		pre_webview.loadUrl(url);
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
