package me.wangolf.usercenter;

/**
 * ============================================================
 * 
 * 版权 ：美高传媒 版权所有 (c) 上午11:49:45
 * 
 * 作者:copy
 * 
 * 版本 ：1.0
 * 
 * 创建日期 ： 上午11:49:45
 * 
 * 描述 ：用户编辑信息
 * 
 * 
 * 修订历史 ：
 * 
 * ============================================================
 **/

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.ViewPager.LayoutParams;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.meigao.mgolf.R;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import me.wangolf.ConstantValues;
import me.wangolf.base.BaseActivity;
import me.wangolf.bean.InfoEntity;
import me.wangolf.bean.usercenter.UserInfoNewEntity;
import me.wangolf.factory.ServiceFactory;
import me.wangolf.service.IOAuthCallBack;
import me.wangolf.utils.CheckUtils;
import me.wangolf.utils.DialogUtil;
import me.wangolf.utils.FileUtils;
import me.wangolf.utils.GsonTools;
import me.wangolf.utils.ImageUtils;
import me.wangolf.utils.ImageViewUtil;
import me.wangolf.utils.ShowPickUtils;
import me.wangolf.utils.ToastUtils;

public class UserInfoEditInfoActivity extends BaseActivity implements OnClickListener 
{
	@ViewInject(R.id.common_title)
	private TextView mTitle;
	
	@ViewInject(R.id.common_back)
	private TextView mBack;
	
	@ViewInject(R.id.common_bt)
	private TextView mBt;
	
	@ViewInject(R.id.et_nickname)
	private EditText mNickname;// ed昵称
	
	@ViewInject(R.id.et_gender)
	private EditText mGender;// ed性别
	
	@ViewInject(R.id.et_my_intro)
	private EditText mMyintro;// ed签名
	
	@ViewInject(R.id.enter_up)
	private Button mEnter;// 确认修改
	
	@ViewInject(R.id.user_photo)
	private CircleImageView mPhoto;
	
	@ViewInject(R.id.gender_m)
	private TextView mGender_m;
	
	@ViewInject(R.id.gender_w)
	private TextView mGender_w;
	
	@ViewInject(R.id.popwin)
	private RelativeLayout mPopwin;
	
	@ViewInject(R.id.user_editinfo)
	private RelativeLayout mUser_editinfo;
	
	private String user_id;// 用户id
	
	private String my_intro;// 我的简介
	
	private int gender = 0;// 性别0代表女，1代表男
	
	private String nick_name;// 呢称
	
	private UserInfoNewEntity bean;
	
	private PopupWindow mPopuWin;//
	
	private View view;
	
    private Dialog dialog;
    
	private int page=1;
	
	private int number=1;
	
	private ArrayList<String> tDataList;// 选择后的图片地址
	
	private String password;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.ac_user_editinfo);
		
		view = View.inflate(this, R.layout.item_userinfo_gender, null);
		
		ViewUtils.inject(this);
		
		ViewUtils.inject(this, view);
		
		initData();// 初始化数据
	}

	// 初始化数据=====
	@Override
	public void initData() 
	{
        dialog = DialogUtil.getDialog(this);
        
		mTitle.setText("编辑资料");
		
		mBack.setVisibility(View.VISIBLE);
		
		bean = (UserInfoNewEntity) getIntent().getSerializableExtra("bean");
		
		user_id = ConstantValues.UID;
		
		mBack.setOnClickListener(this);
		
		mEnter.setOnClickListener(this);
		
		mGender.setOnClickListener(this);
		
		mGender_m.setOnClickListener(this);
		
		mGender_w.setOnClickListener(this);
		
		mPopwin.setOnClickListener(this);
		
		mPhoto.setOnClickListener(this);
		
		password= CheckUtils.checkEmpty(ConstantValues.PASSWORD)?ConstantValues.OPEN_ID:ConstantValues.PASSWORD;
		//ToastUtils.showInfo(this,user_id+"");
//		if (bean != null){
//			setUserInfo(bean);
//		}else {
//			getData();
//
//		};
		getData();
	}

	// 获取数据=======
	@Override
	public void getData() {

		try {
			ServiceFactory.getCommunityEngineInstatice().getUserInfo(user_id, Integer.parseInt(user_id), page, number, new IOAuthCallBack() {

				@Override
				public void getIOAuthCallBack(String result)
				{

					if (result.equals(ConstantValues.FAILURE)) 
					{
						ToastUtils.showInfo(UserInfoEditInfoActivity.this, ConstantValues.NONETWORK);
					} 
					else 
					{
						UserInfoNewEntity beans = GsonTools.changeGsonToBean(result, UserInfoNewEntity.class);
						
						if (beans.getStatus() == 1) 
						{
							if (beans.getData() != null) 
							{
								UserInfoNewEntity data = beans.getData().get(0);
								
								bean = data;
								
								setUserInfo(data);// 更新页面
							}
							
						} 
						else 
						{
							ToastUtils.showInfo(UserInfoEditInfoActivity.this, bean.getInfo());
						}

					}
					
					dialog.cancel();
				}
			});
			
		}
		catch (Exception e)
		{
			
			e.printStackTrace();
			
		}
	}

	// 设置数据
	public void setUserInfo(UserInfoNewEntity bean) {
		mNickname.setText(bean.getNick_name());

		mGender.setText(bean.getGender() == 0 ? "女" : "男");
		mMyintro.setText(bean.getMy_intro());
		String path = bean.getPhoto();
		if (!CheckUtils.checkEmpty(path)) {
			path = FileUtils.getPhotoPath(path);
		}
		ImageViewUtil.loadimg(path, mPhoto, this);
	}

	// 更新数据
	public void upData() {
        dialog.show();
		try {
			ServiceFactory.getIUserEngineInstatice().updateUserInfo(user_id,password, my_intro,  gender, nick_name, new IOAuthCallBack() {

				@Override
				public void getIOAuthCallBack(String result) {
					if (result.equals(ConstantValues.FAILURE)) {
						ToastUtils.showInfo(UserInfoEditInfoActivity.this, ConstantValues.NONETWORK);
					} else {
						InfoEntity bean = GsonTools.changeGsonToBean(result, InfoEntity.class);
						if ("1".equals(bean.getStatus())) {
							getUserInfo();
							ToastUtils.showInfo(UserInfoEditInfoActivity.this, bean.getInfo());
						} else {
							ToastUtils.showInfo(UserInfoEditInfoActivity.this, bean.getInfo());
						}
						dialog.cancel();
					}

				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void getUserInfo(){
		try {
			ServiceFactory.getCommunityEngineInstatice().getUserInfo(user_id, Integer.parseInt(user_id), page, number, new IOAuthCallBack() {

				@Override
				public void getIOAuthCallBack(String result) {
				//	Log.i("wangolf",result);
					if (result.equals(ConstantValues.FAILURE)) {
						ToastUtils.showInfo(UserInfoEditInfoActivity.this, ConstantValues.NONETWORK);
					} else {
						UserInfoNewEntity beans = GsonTools.changeGsonToBean(result, UserInfoNewEntity.class);
						if (beans.getStatus() == 1) {
							if (beans.getData() != null) {
								UserInfoNewEntity data = beans.getData().get(0);
								bean = data;
								if (!CheckUtils.checkEmpty(data.getNick_name()) & !CheckUtils.checkEmpty(data.getPhoto()))
								ConstantValues.ISCOMPLETEINFO = true;else ConstantValues.ISCOMPLETEINFO = false;
								Intent results = new Intent();
								setResult(10, results);
								finish();
							}
						} else {
							ToastUtils.showInfo(UserInfoEditInfoActivity.this, bean.getInfo());
						}

					}
					dialog.cancel();
				}
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 确认修改用户数据
	public void toUpData() {
		if (CheckUtils.checkEmpty(bean.getPhoto())) {
		//ToastUtils.showInfo(UserInfoEditInfoActivity.this, "头像不能为空");
		ShowPickUtils.ShowDialog(this, "请上传头像");
		return;
	}
		nick_name = mNickname.getText().toString().trim();
		my_intro = mMyintro.getText().toString().trim();
		gender = "女".equals(mGender.getText().toString()) ? 0 : 1;
		if (CheckUtils.checkEmpty(nick_name)) {
			//ToastUtils.showInfo(UserInfoEditInfoActivity.this, "昵称不能为空");
			ShowPickUtils.ShowDialog(this,"请填写昵称");
			return;
		}

//		if (CheckUtils.checkEmpty(my_intro)) {
//			//ToastUtils.showInfo(UserInfoEditInfoActivity.this, "签名不能为空");
//			ShowPickUtils.ShowDialog(this, "请填写签名");
//			return;
//		}
		upData();
	}

	// pop显示男女
	public void showGender() {
		mPopuWin = new PopupWindow(view, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		mPopuWin.setContentView(view);
		mPopuWin.showAtLocation(mUser_editinfo, 0, 0, 0);
	}

	// 点击事件===
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.common_back:
			finish();
			break;
		case R.id.enter_up:
			toUpData();
			break;
		case R.id.et_gender:
			showGender();
			break;
		case R.id.gender_m:
			mGender.setText("男");
			mPopuWin.dismiss();
			break;
		case R.id.gender_w:
			mGender.setText("女");
			mPopuWin.dismiss();
			break;
		case R.id.popwin:
			if (mPopuWin != null && mPopuWin.isShowing()) {
				mPopuWin.dismiss();
				mPopuWin = null;
			}
			break;
			case R.id.user_photo:
				int mflag = 1;
				Intent photo = new Intent(this, SelectPhotoImages.class);
				photo.putExtra("flag", mflag);
				startActivityForResult(photo, 100);
				break;
		default:
			break;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode){
			case 101:
				// 上传头像
				String type1 = data.getStringExtra("type");
				if ("photo".equals(type1) & data != null) {
					Bundle bundle3 = data.getExtras();
					ArrayList<String> datalist = (ArrayList<String>) bundle3.getSerializable("dataList");
					tDataList = new ArrayList<String>();
					for (int i = 0; i < datalist.size(); i++) {
						Bitmap bitmap = ImageUtils.getSmallBitmap(datalist.get(i));
						String string = FileUtils.saveBitToSD(bitmap, System.currentTimeMillis() + "");

						tDataList.add(string);
					}
				} else {
					String cameraImagePath = data.getStringExtra("cameraImagePath");
					tDataList = new ArrayList<String>();
					tDataList.add(cameraImagePath);
				}
				dialog.show();
				upLoad(tDataList.get(0));

		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	// 更新头像
	public void upLoad(String path) {
		final String photo = ImageUtils.compressImage(path);
		final Date d = new Date();
		try {

		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			ServiceFactory.getIUserEngineInstatice().upLoad(user_id, photo, new IOAuthCallBack() {
				@Override
				public void getIOAuthCallBack(String result) {
					if (result.equals(ConstantValues.FAILURE)) {
						ToastUtils.showInfo(UserInfoEditInfoActivity.this, ConstantValues.NONETWORK);
					} else {
						InfoEntity bean = GsonTools.changeGsonToBean(result, InfoEntity.class);
						if ("1".equals(bean.getStatus())) {
							getData();
							ToastUtils.showInfo(UserInfoEditInfoActivity.this, "更新" + bean.getInfo());
							try {

							} catch (Exception e) {
								e.printStackTrace();
							}
						} else {
							ToastUtils.showInfo(UserInfoEditInfoActivity.this, bean.getInfo());
						}
						// 上传后清空图片
						dialog.cancel();
						FileUtils.clearImage();
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
