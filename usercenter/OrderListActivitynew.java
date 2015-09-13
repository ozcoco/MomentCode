package me.wangolf.usercenter;

import com.lidroid.xutils.ViewUtils;
import com.meigao.mgolf.R;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class OrderListActivitynew extends FragmentActivity
{

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		
		setContentView(R.layout.order_list_main);

		ViewUtils.inject(this);
		
		
	}
	
	
	
	
	
	
}
