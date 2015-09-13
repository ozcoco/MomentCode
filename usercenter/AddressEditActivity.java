package me.wangolf.usercenter;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.meigao.mgolf.R;

import me.wangolf.ConstantValues;
import me.wangolf.GlobalConsts;
import me.wangolf.base.BaseActivity;
import me.wangolf.bean.InfoEntity;
import me.wangolf.bean.usercenter.AddressBean;
import me.wangolf.bean.usercenter.RespUserAdrrEntity;
import me.wangolf.dao.CityDao;
import me.wangolf.factory.ServiceFactory;
import me.wangolf.service.IOAuthCallBack;
import me.wangolf.time.ProCityXianActivity;
import me.wangolf.utils.CheckUtils;
import me.wangolf.utils.GsonTools;
import me.wangolf.utils.ToastUtils;

/** 
* @ClassName: AddressEditActivity 
* @Description: 用户地址修改及新增页面  
* @author oz 
* @date 2015-8-12 下午6:58:27 
*  
*/

public class AddressEditActivity extends BaseActivity implements OnClickListener
{
	@ViewInject(R.id.btn_del_addr)
	private Button btn_del_addr;
	
	@ViewInject(R.id.common_back)
	private Button common_back; // 后退
	
	@ViewInject(R.id.common_title)
	private TextView common_title;// 标题
	
	@ViewInject(R.id.common_bt)
	private TextView common_bt;// 地图
	
	@ViewInject(R.id.edconsignee)
	private EditText edconsignee;// 收货人姓名
	
	@ViewInject(R.id.edmobile)
	private EditText edmobile;// 收货人手机号
	
	@ViewInject(R.id.edzip)
	private EditText edzip;// 邮编
	
	@ViewInject(R.id.shenshiqu)
	private TextView shenshiqu;
	
	@ViewInject(R.id.toShowProCityXian)
	private RelativeLayout toShowProCityXian;// 省市
	
	@ViewInject(R.id.edaddress)
	private EditText edaddress;// 详细地址
	
	@ViewInject(R.id.cbox_switch)
	private CheckBox cbox_switch;// 设为默认

	private RespUserAdrrEntity.DataEntity bean;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.ac_address_edit);
		
		ViewUtils.inject(this);
		
		initData();
	}

	@Override
	public void initData() 
	{
		bean = (RespUserAdrrEntity.DataEntity) getIntent().getSerializableExtra("respUserAdrr");
		
		common_back.setVisibility(0);
		
		common_bt.setVisibility(0);
		
		common_title.setText(ConstantValues.NEWADDRESS);
		
		common_bt.setText(ConstantValues.SAVEADDRESS);
		
		common_back.setOnClickListener(this);
		
		common_bt.setOnClickListener(this);
		
		toShowProCityXian.setOnClickListener(this);
		
		if (bean == null) 
		{
			
			bean = new RespUserAdrrEntity.DataEntity();
			
			btn_del_addr.setVisibility(View.GONE);
		} 
		else 
		{			
			
			edconsignee.setText(bean.getConsignee());
			
			edmobile.setText(bean.getMobile());
			
			edzip.setText(bean.getZip());
			
			String procitxian = findCityName(null, bean.getProvince_id(), bean.getCity_id(), bean.getArea_id());
			
			shenshiqu.setText(procitxian);
			
			edaddress.setText(bean.getAddress());
		}
		
		bean.setUser_id(ConstantValues.UID);

	}

	
	
	@Override
	public void getData() 
	{
		try {
			
			ServiceFactory.getIUserEngineInstatice().upAddrdata(bean, new IOAuthCallBack()
			{
				@Override
				public void getIOAuthCallBack(String result) 
				{
					if (result.equals(ConstantValues.FAILURE))
					{
						Toast.makeText(getApplicationContext(), ConstantValues.NONETWORK, 0).show();
					} 
					else 
					{
						
						InfoEntity bean = GsonTools.changeGsonToBean(result, InfoEntity.class);
						
						if ("1".equals(bean.getStatus())) 
						{
							Toast.makeText(getApplicationContext(), bean.getInfo(), 0).show();
							
							if(AddressEditActivity.this.bean.getId().equals(""))
							{
								
								Intent intent  =  new Intent(AddressListActivity.RefreshReceiver.UPDATE_DATA_ADD);
								
								sendBroadcast(intent);
								
							}
							else
							{
								
								Intent intent  =  new Intent(AddressListActivity.RefreshReceiver.UPDATE_DATA_UPDATE);
								
								intent.putExtra(AddressListActivity.RefreshReceiver.UPDATE_DATA_DELETE, AddressEditActivity.this.bean);
								
								sendBroadcast(intent);						
								
							}
							
							finish();							
						}
						else
						{
							Toast.makeText(getApplicationContext(), bean.getInfo(), 0).show();
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
			
		case R.id.common_bt:
			
			saveAddress(); // 保存填写的地址
			
			break;
			
		case R.id.toShowProCityXian:
			
			Intent intent = new Intent(this, ProCityXianActivity.class);
			
			startActivityForResult(intent, GlobalConsts.proCityAreaCode);
			
		default:
			break;
		}
	}

	public void saveAddress() 
	{ // 获得数据

		if (CheckUtils.checkEmpty(edconsignee.getText().toString().trim())) 
		{
			ToastUtils.showInfo(AddressEditActivity.this, "收货人不能为空");
			
			return;
			
		} 
		else if (CheckUtils.checkEmpty(edmobile.getText().toString().trim()) || edmobile.getText().toString().trim().length() < 11) 
		{
			ToastUtils.showInfo(AddressEditActivity.this, "手机号码不正确");
			
			return;
			
		}
		else if (CheckUtils.checkEmpty(edzip.getText().toString().trim()) || edzip.getText().toString().trim().length() < 6) 
		{
			ToastUtils.showInfo(AddressEditActivity.this, "邮政编码不正确");
			return;
		} 
		else if (CheckUtils.checkEmpty(edaddress.getText().toString().trim()))
		{
			ToastUtils.showInfo(AddressEditActivity.this, "详细地址不能为空");
			
			return;
			
		} else if (CheckUtils.checkEmpty(shenshiqu.getText().toString().trim())) 
		{
			ToastUtils.showInfo(AddressEditActivity.this, "请选择省市区");
			
			return;
		}
		
		bean.setConsignee(edconsignee.getText().toString().trim());
		
		bean.setMobile(edmobile.getText().toString().trim());
		
		bean.setZip(edzip.getText().toString().trim());
		
		bean.setAddress(edaddress.getText().toString().trim());
		
		String type = cbox_switch.isChecked() ? "1" : "0";
		
		bean.setType(type);
//
//		System.out.println("uid=" + bean.getUid() + "adi=" + bean.getAid() + "rid1=" + bean.getRid1() + "rid2=" + bean.getRid2() + "rid3="
//				+ bean.getRid3() + "rd4=" + bean.getRd4() + "type=" + bean.getType() + "address=" + bean.getAddress() + "zip=" + bean.getZip()
//				+ "naem=" + bean.getConsignee());
		getData();// 上传到服务器
	}

	/**
	 * 到显示省市区
	 * 
	 * @param v
	 */

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode == GlobalConsts.proCityAreaCode) 
		{
			if (data != null) 
			{
				String contry = "";
				
				String provice = data.getStringExtra("reprovice");
				
				String city = data.getStringExtra("recity");
				
				String area = data.getStringExtra("rearea");
				
				String show = provice + " " + city + " " + area;
				
				shenshiqu.setText(show);
				
				findCityId(contry, provice, city, area);
			}
		}
	}

	// 通过ID查城市名
	public void findCityId(String contry, String provice, String city, String area) 
	{
		HashMap<String, String> map = new CityDao(getApplicationContext()).getUpdateAddr(contry, provice, city, area);
		
//		bean.setRid1(map.get("rid1"));
		
		bean.setProvince_id(map.get("rid2"));
		
		bean.setCity_id(map.get("rid3"));
		
		bean.setArea_id(map.get("rid4"));
	}

	// 通过城市ID查名
	public String findCityName(String rid1, String rid2, String rid3, String rid4)
	{
		HashMap<String, String> map = new CityDao(getApplicationContext()).getResUserAdrr(rid1, rid2, rid3, rid4);
		
		String provice = map.get("province") == null ? "" : map.get("province");
		
		String city = map.get("city") == null ? "" : map.get("city");
		
		String xian = map.get("xian") == null ? "" : map.get("xian");
		
		String procitxian = provice + " " + city + " " + xian;
		
		return procitxian;
	}
	
	
	
	/** 
	* @Title: onDeleteAddress 
	* @Description: 删除地址按钮的单击回调方法
	* @param @param view    任意控件 
	* @return void    返回类型 
	* @throws 
	*/
	public void onDeleteAddress(View view)
	{
		
		deleteAddr();
		
	}
	
	
	
	private void deleteAddr()
	{
		
		try
		{
			ServiceFactory.getIUserEngineInstatice().deleteAddrdata(null, bean.getId(), new IOAuthCallBack()
			{
				
				@Override
				public void getIOAuthCallBack(String result)
				{
					
					try
					{
						if("1".equals(new JSONObject(result).getString("status")))
						{							
							
							ToastUtils.showInfo(getBaseContext(), "删除成功");
							
							Intent intent  =  new Intent(AddressListActivity.RefreshReceiver.UPDATE_DATA_DELETE);
							
							intent.putExtra(AddressListActivity.RefreshReceiver.UPDATE_DATA_DELETE, bean);
							
							sendBroadcast(intent);
							
							finish();
						}
						else
						{							
							ToastUtils.showInfo(getBaseContext(), "删除失败");
						}
					}
					catch(JSONException e)
					{
						
						e.printStackTrace();
					}
				}
			});
		}
		catch(Exception e)
		{
			
			e.printStackTrace();
		}
		
		
	}
	
}