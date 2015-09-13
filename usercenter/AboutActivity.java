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
 * 描述 ：  关于我们: 0代表使用指南，1代表支付帮助2代表招商合作3代表关于我们
 * 
 * 
 * 修订历史 ：
 * 
 * ============================================================
 **/
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.meigao.mgolf.R;

import me.wangolf.ConstantValues;
import me.wangolf.base.BaseActivity;
import me.wangolf.bean.usercenter.AboutEntity;
import me.wangolf.factory.ServiceFactory;
import me.wangolf.service.IOAuthCallBack;
import me.wangolf.utils.GsonTools;
import me.wangolf.utils.ToastUtils;

@SuppressLint("SetJavaScriptEnabled")
public class AboutActivity extends BaseActivity implements OnClickListener 
{
	@ViewInject(R.id.common_back)
	private Button common_back; // 后退
	@ViewInject(R.id.common_title)
	private TextView common_title;// 标题
	@ViewInject(R.id.pre_webview)
	private WebView pre_webview;// web
	private String type;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.ac_preferbuy_pictxt);
		
		ViewUtils.inject(this);
		
		initData();
	}

	@Override
	public void initData() 
	{
		type = getIntent().getStringExtra("type");
		
		String title = getIntent().getStringExtra("title");
		
		common_back.setVisibility(0);
		
		common_title.setText(title);
		
		common_back.setOnClickListener(this);
		
		pre_webview.getSettings().setJavaScriptEnabled(true);
		
		getData();
	}

	
	@Override
	public void getData() 
	{

		try 
		{
			ServiceFactory.getIUserEngineInstatice().getAbout(type, new IOAuthCallBack() 
			{
				@SuppressLint("SetJavaScriptEnabled")
				@Override
				public void getIOAuthCallBack(String result)
				{
					if (result.equals(ConstantValues.FAILURE))
					{
						Toast.makeText(getApplicationContext(), ConstantValues.NONETWORK, 0).show();
					} 
					else 
					{						
						
						AboutEntity bean = GsonTools.changeGsonToBean(result, AboutEntity.class);
						
						if ("1".equals(bean.getStatus())) 
						{													
							
							AboutEntity data = bean.getData().get(0);

							LogUtils.i(data.getContent());
							
							pre_webview.loadDataWithBaseURL(null, data.getContent(), "text/html", "utf-8", null);
							
						}
					}
				}
			});
		} 
		catch (Exception e) 
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
