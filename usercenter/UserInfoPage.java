package me.wangolf.usercenter;

import java.io.File;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.meigao.mgolf.R;

import me.wangolf.ConstantValues;
import me.wangolf.GlobalConsts;
import me.wangolf.base.Mo_BasePage;
import me.wangolf.bean.InfoEntity;
import me.wangolf.bean.usercenter.UserInfoEntity;
import me.wangolf.factory.ServiceFactory;
import me.wangolf.service.IOAuthCallBack;
import me.wangolf.utils.BitmapUtils;
import me.wangolf.utils.CheckUtils;
import me.wangolf.utils.GsonTools;
import me.wangolf.utils.HttpUtils;
import me.wangolf.utils.ImageViewUtil;
import me.wangolf.utils.ShowPickUtils;
import me.wangolf.utils.TelUtils;
import me.wangolf.utils.ToastUtils;

public class UserInfoPage extends Mo_BasePage implements OnClickListener
{
	@ViewInject(R.id.common_title)
	private TextView common_title;// 标题
	
	private View view;
	
	DisplayImageOptions options;
	
	private boolean flag;
	
	LayoutInflater inflater;
	
	@ViewInject(R.id.name)
	private TextView name;// 呢称
	
	@ViewInject(R.id.account)
	private TextView account;// 余额
	
	@ViewInject(R.id.ib)
	private de.hdodenhof.circleimageview.CircleImageView ib; // 圆形图片
	
	@ViewInject(R.id.reletag)
	private ImageView reletag;
	
	@ViewInject(R.id.orderlist)
	private RelativeLayout orderlist;// 订单列表
	
	@ViewInject(R.id.my_account)
	private RelativeLayout my_account;// 我的账户
	
	@ViewInject(R.id.my_set)
	private RelativeLayout my_set;// 设置
	
	@ViewInject(R.id.my_event)
	private RelativeLayout my_event;// 我的活动
	
	@ViewInject(R.id.up_userinfo)
	private RelativeLayout up_userinfo;// 更新资料
	
	@ViewInject(R.id.common_bt)
	private TextView mMessage;// 消息
	
	@ViewInject(R.id.user_new_info)
	private Button mUserInfo;// 个人中心
	
	@ViewInject(R.id.user_customer)
	private Button mCustomer;// 客服中心
	
	private Double my_accounts;// 我的账户余额
	
	private Double my_vouchers;// 我的代金券余额
	
	private String path;
	
	private String uid;

	public UserInfoPage(Context context) {
		super(context);

	}

	@Override
	public View initView(LayoutInflater inflater) 
	{
		view = inflater.inflate(R.layout.ac_user_center, null);
		
		ViewUtils.inject(this, view);
		
		initData();
		
		return view;

	}

	@Override
	public void initData()
	{
		common_title.setText("发现");
		
		mMessage.setVisibility(0);
		
		mMessage.setText("消息");
		
		orderlist.setOnClickListener(this);
		
		my_set.setOnClickListener(this);
		
		my_account.setOnClickListener(this);
		
		my_event.setOnClickListener(this);
		
		up_userinfo.setOnClickListener(this);
		
		ib.setOnClickListener(this);
		
		mMessage.setOnClickListener(this);
		
		mUserInfo.setOnClickListener(this);
		
		mCustomer.setOnClickListener(this);
		
		uid = ConstantValues.UID;
		
		if (uid == null)
			return;
		
		getData();
		
	}

	
	
	public void getData() 
	{
		try 
		{
			ServiceFactory.getIUserEngineInstatice().getUserInfo(uid, new IOAuthCallBack()
			{
				@Override
				public void getIOAuthCallBack(String result) 
				{
					
					UserInfoEntity user = GsonTools.changeGsonToBean(result, UserInfoEntity.class);
					
					UserInfoEntity.DataEntity userinfo = user.getData().get(0);
					
					name.setText(CheckUtils.checkEmpty(userinfo.getNick_name()) ? userinfo.getMobile() : userinfo.getNick_name());
					
					account.setText("￥" + userinfo.getAccount());
					
					my_accounts = Double.valueOf(userinfo.getAccount());
					
//					my_vouchers = userinfo.getVouchers();
					
					String path_1 = userinfo.getAvatar();
					
//					if (!CheckUtils.checkEmpty(path_1)) 
//					{
//						path_1 = path_1.substring(0, path_1.lastIndexOf(".")) + "_180_180" + path_1.substring(path_1.lastIndexOf("."));
//					}
					
					ImageViewUtil.loadimg(path_1, ib, context);
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
		case R.id.orderlist:
			
			Intent order = new Intent(context, OrderListActivity.class);
			
			context.startActivity(order);
			
			break;
			
		case R.id.my_account:
			
			if (my_accounts >= 0) 
			{
				Intent account = new Intent(context, UserAccountActivity.class);
				
				account.putExtra("account", my_accounts.toString());
				
				//vouchers取消的字段
				account.putExtra("vouchers", my_vouchers.toString());
				
				context.startActivity(account);				
			}
			
			break;
			
		case R.id.my_set:
			
			Intent my_set = new Intent(context, UserSet.class);
			
			context.startActivity(my_set);
			
			break;
			
		case R.id.my_event:
			
			Intent event = new Intent(context, UserEventListActivity.class);
			
			context.startActivity(event);
			
			break;
			
		case R.id.up_userinfo:
			
			Intent up_userinfo = new Intent(context, UpDataUserInfoActivity.class);
			
			context.startActivity(up_userinfo);
			break;
			
		case R.id.ib:
			
			Intent ib = new Intent(context, UpDataUserInfoActivity.class);
			
			context.startActivity(ib);
			// ShowPickUtils.ShowPickDialog(context);
			// Intent intent = new Intent(Intent.ACTION_PICK, null);
			//
			// intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
			// "image/*");
			// contextstartActivityForResult(intent, 1);
			break;
			
		case R.id.common_bt:
			// Intent message = new Intent(context,
			// UserNotificationActivity.class);
			Intent message = new Intent(context, UserNotificationMessageActivity.class);
			
			context.startActivity(message);
			
			break;
			
		case R.id.user_new_info:
			
			Intent userinfo = new Intent(context, UserInfoNewActivity.class);
			
			context.startActivity(userinfo);
			
			break;
			
		case R.id.user_customer:
			
			TelUtils.tel(context, "13302311999");
			
			break;
			
		default:
			break;
			
		}
	}

	// 更新头像
	public void upLoad(Intent picdata)
	{
		Bundle extras = picdata.getExtras();
		
		if (extras != null) 
		{
			Bitmap photo = extras.getParcelable("data");
			
			photo = BitmapUtils.compressImage(photo);
			
			Drawable drawable = new BitmapDrawable(photo);
			// TODO 保存到SDcard
			HttpUtils.SavePicInLocal(photo);
			
			File img = new File(Environment.getExternalStorageDirectory(), "avatar_file.jpg");
			
			final String imagepath = img.getPath(); // 图片路径
			
			path = imagepath;
			
			try {
				
				ServiceFactory.getIUserEngineInstatice().upLoad(uid, path, new IOAuthCallBack() 
				{

					@Override
					public void getIOAuthCallBack(String result) 
					{
						if (result.equals(ConstantValues.FAILURE)) 
						{
							Toast.makeText(context, ConstantValues.NONETWORK, 0).show();
						} 
						else 
						{
							InfoEntity bean = GsonTools.changeGsonToBean(result, InfoEntity.class);
							
							if ("1".equals(bean.getStatus()))
							{
								ToastUtils.showInfo(context, bean.getInfo());
							} 
							else 
							{
								ToastUtils.showInfo(context, bean.getInfo());
							}
						}

					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		/*************** 照片相关操作start ********************/
		if (requestCode != -1 && requestCode != GlobalConsts.eventYMDCode) {

			switch (requestCode) {
			// 如果是直接从相册获取
			case 1:
				if (data != null) {
					ShowPickUtils.startPhotoZoom(context, data.getData());
				}
				break;
			// 如果是调用相机拍照时
			case 2:
				File temp = new File(Environment.getExternalStorageDirectory() + "/avatar_file.jpg");
				ShowPickUtils.startPhotoZoom(context, Uri.fromFile(temp));

				break;
			// 取得裁剪后的图片
			case 3:
				if (data != null) {
					upLoad(data);
				}
				break;
			}
			/*************** 照片相关操作end ********************/

		}
	}
}
