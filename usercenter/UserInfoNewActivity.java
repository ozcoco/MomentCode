package me.wangolf.usercenter;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import me.wangolf.ConstantValues;
import me.wangolf.adapter.UserImagesAdapter;
import me.wangolf.base.BaseActivity;
import me.wangolf.bean.InfoEntity;
import me.wangolf.bean.community.ImgInfoEntity;
import me.wangolf.bean.usercenter.UserInfoNewEntity;
import me.wangolf.factory.ServiceFactory;
import me.wangolf.service.IOAuthCallBack;
import me.wangolf.utils.CheckUtils;
import me.wangolf.utils.DialogUtil;
import me.wangolf.utils.FileUtils;
import me.wangolf.utils.GsonTools;
import me.wangolf.utils.ImageUtils;
import me.wangolf.utils.ToastUtils;
import me.wangolf.utils.Xutils;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.meigao.mgolf.R;

public class UserInfoNewActivity extends BaseActivity implements
		OnClickListener, OnItemClickListener
{
	@ViewInject(R.id.common_title)
	private TextView				mTitle;		// 标题

	@ViewInject(R.id.common_back)
	private TextView				mBack;			// 返回

	@ViewInject(R.id.common_bt)
	private TextView				mBt;			// 编辑

	@ViewInject(R.id.myposts_num)
	private TextView				mMypostsNum;	// 我的帖子数量

	@ViewInject(R.id.myreply_num)
	private TextView				mMyreplyNum;	// 回帖数量

	@ViewInject(R.id.myfriends_num)
	private TextView				mMyfriendsNum;	// 我的关注数量

	@ViewInject(R.id.myfans_num)
	private TextView				mMyfansNum;	// 我的粉丝数量

	@ViewInject(R.id.myposts)
	private RelativeLayout			mMyposts;		// 我的帖子

	@ViewInject(R.id.myreply)
	private RelativeLayout			mMyreply;		// 回帖

	@ViewInject(R.id.myfriends)
	private RelativeLayout			mMyfriends;	// 我的关注

	@ViewInject(R.id.myfans)
	private RelativeLayout			mMyfans;		// 我的粉丝

	@ViewInject(R.id.photo)
	private ImageView				mPhoto;		// 头像

	@ViewInject(R.id.user_name)
	private TextView				mUserName;		// 昵称

	@ViewInject(R.id.user_intro)
	private TextView				mUserIntro;	// 签名

	@ViewInject(R.id.gv_images)
	private GridView				mGv;

	@ViewInject(R.id.user_upimage)
	private ImageView				mUpImage;

	@ViewInject(R.id.hsview)
	private HorizontalScrollView	mHsview;

	private String					user_id;		// 用户iD

	private int						page;

	private int						number;

	private UserInfoNewEntity		data;

	private UserImagesAdapter		adapter;

	private ArrayList<String>		tDataList;		// 选择后的图片地址

	private ArrayList<String>		mList;

	private String					images;		// 相册路径(上传多图以半角逗号隔开)

	private int						mflag	= 1;	// 0表示已上传头像 1表示未上传

	private int						imagesize;

	private Dialog					dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.ac_user_info);

		ViewUtils.inject(this);

		if (adapter == null)
		{
			adapter = new UserImagesAdapter(this);
		}

		adapter.notifyDataSetChanged();

		mGv.setAdapter(adapter);

		mGv.setNumColumns(adapter.getCount());

		initData();
	}

	// 初始化喽
	@Override
	public void initData()
	{
		dialog = DialogUtil.getDialog(this);

		mTitle.setText("个人中心");

		mBt.setText("编辑");

		mBack.setVisibility(View.VISIBLE);

		mBt.setVisibility(View.VISIBLE);

		mBack.setOnClickListener(this);

		mBt.setOnClickListener(this);

		mMyposts.setOnClickListener(this);

		mMyreply.setOnClickListener(this);

		mMyfriends.setOnClickListener(this);

		mMyfans.setOnClickListener(this);

		mUpImage.setOnClickListener(this);

		mPhoto.setOnClickListener(this);

		mGv.setOnItemClickListener(this);

		user_id = ConstantValues.UID;

		getData();
		
//		 getUserImages();
	}

	
	// 获取用户数据
	@Override
	public void getData()
	{
		try
		{
			ServiceFactory
					.getCommunityEngineInstatice()
					.getUserInfo(user_id, Integer.parseInt(user_id), page, number, new IOAuthCallBack()
					{

						@Override
						public void getIOAuthCallBack(String result)
						{

							if (result.equals(ConstantValues.FAILURE))
							{
								ToastUtils
										.showInfo(UserInfoNewActivity.this, ConstantValues.NONETWORK);
							}
							else
							{
								
								UserInfoNewEntity bean = GsonTools
										.changeGsonToBean(result, UserInfoNewEntity.class);
								if (bean.getStatus() == 1)
								{
									if (bean.getData() != null)
									{
										data = bean.getData().get(0);
										
										setUserInfo(data);// 更新页面
									}
								}
								else
								{
									ToastUtils
											.showInfo(UserInfoNewActivity.this, bean
													.getInfo());
								}

							}
							dialog.cancel();
						}
					});
		}
		catch(Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 上传相册数据
	public void getUserImages()
	{
		try
		{
			ServiceFactory.getCommunityEngineInstatice()
					.upDateImages(user_id, images, new IOAuthCallBack()
					{
						@Override
						public void getIOAuthCallBack(String result)
						{
							if (result.equals(ConstantValues.FAILURE))
							{
								ToastUtils.showInfo(UserInfoNewActivity.this, ConstantValues.NONETWORK);
							}
							else
							{
								InfoEntity bean = GsonTools
										.changeGsonToBean(result, InfoEntity.class);

								if ("1".equals(bean.getStatus()))
								{
									getData();
								}
								else
								{
									ToastUtils
											.showInfo(UserInfoNewActivity.this, bean
													.getInfo());
								}
								dialog.cancel();
							}
						}
					});
		}
		catch(Exception e)
		{

			e.printStackTrace();
		}
	}

	

	
	 void upImageNative(String path)
	{
		 
		final String photo = ImageUtils.compressImage(path);
		
		String api = ConstantValues.BaseApi + "webImage/avatar";	
		
		api += "?terminal=1&user_id="+ ConstantValues.UID+"&unique_key=" + ConstantValues.UNIQUE_KEY;
			
		HttpUtils http = new HttpUtils();
		
		RequestParams params = new RequestParams("utf-8");
					
		params.addBodyParameter("avatar_file", new File(photo), "image/jpeg");	

		
		http.send(HttpRequest.HttpMethod.POST, api, params, new RequestCallBack<String>()
		{

			@Override
			public void onStart()
			{

			}

			@Override
			public void onLoading(long total, long current, boolean isUploading)
			{
				
				Log.i("进度", current+"/"+total+"......");
				
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo)
			{				

				Log.i("结果", responseInfo.result);

				InfoEntity bean = GsonTools
						.changeGsonToBean(responseInfo.result, InfoEntity.class);

				Log.i("解析结果", bean.getData().get(0).getUrl());
				
				if ("1".equals(bean.getStatus()))
				{
					getData();
			
					ToastUtils
							.showInfo(UserInfoNewActivity.this, "更新" + bean
									.getInfo());
				}
				else
				{
					ToastUtils
							.showInfo(UserInfoNewActivity.this, bean
									.getInfo());
				}

				// 上传后清空图片
				dialog.cancel();

				FileUtils.clearImage();				
				
			}

			@Override
			public void onFailure(HttpException error, String msg)
			{
			
				ToastUtils.showInfo(UserInfoNewActivity.this, ConstantValues.NONETWORK);
				
			}
		});
		
		
	}
	
	// 更新头像
	public void upLoad(String path)
	{
		
		upImageNative(path);
		
//		upHead(path);
//
//		String photo = ImageUtils.compressImage(path);
//
//		try
//		{
//			ServiceFactory.getIUserEngineInstatice()
//					.upLoad(user_id, photo, new IOAuthCallBack()
//					{
//						@Override
//						public void getIOAuthCallBack(String result)
//						{
//							
//							Log.i("结果", result);
//							
//							if (result.equals(ConstantValues.FAILURE))
//							{
//								ToastUtils.showInfo(UserInfoNewActivity.this, ConstantValues.NONETWORK);
//							}
//							else
//							{
//								Log.i("结果", result);
//
//								InfoEntity bean = GsonTools
//										.changeGsonToBean(result, InfoEntity.class);
//
//								if ("1".equals(bean.getStatus()))
//								{
//									getData();
//
//									ToastUtils
//											.showInfo(UserInfoNewActivity.this, "更新" + bean
//													.getInfo());
//								}
//								else
//								{
//									ToastUtils
//											.showInfo(UserInfoNewActivity.this, bean
//													.getInfo());
//								}
//
//								// 上传后清空图片
//								dialog.cancel();
//
//								FileUtils.clearImage();
//							}
//						}
//					});
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}
	}

	
	// ***图片上传
	public void loadPostsImg(ArrayList<String> avatar_file, int i)
	{
		ArrayList<String> image = ImageUtils.compressImages(avatar_file);// 压缩图片
		try
		{
			ServiceFactory.getCommunityEngineInstatice()
					.loadPostsImg(image, i, new IOAuthCallBack()
					{

						@Override
						public void getIOAuthCallBack(String result)
						{

							if (result.equals(ConstantValues.FAILURE))
							{
								ToastUtils.showInfo(UserInfoNewActivity.this, ConstantValues.NONETWORK);
							}
							else
							{

								ImgInfoEntity bean = GsonTools
										.changeGsonToBean(result, ImgInfoEntity.class);
								if ("1".equals(bean.getStatus()))
								{
									images = bean.getData().get(0).getLogo();
									ToastUtils
											.showInfo(UserInfoNewActivity.this, "上传" + bean
													.getInfo());
									getUserImages();
								}
								else
								{
									ToastUtils
											.showInfo(UserInfoNewActivity.this, bean
													.getInfo());
								}
								// 上传后清空图片
								FileUtils.clearImage();
							}
						}
					});
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	// 更新用户界面
	public void setUserInfo(UserInfoNewEntity bean)
	{
		if (bean != null)
		{
			mMypostsNum.setText(bean.getPosts_count() + "");
			
			mMyreplyNum.setText(bean.getReply_count() + "");
			
			mMyfriendsNum.setText(bean.getAttention_count() + "");
			
			mMyfansNum.setText(bean.getFans_count() + "");
			
			mUserName.setText(CheckUtils.checkEmpty(bean.getNick_name()) ? bean
					.getMobile() + "" : bean.getNick_name());
			
			mUserIntro.setText(bean.getMy_intro());
			
			String path = bean.getPhoto();
			
			if (!CheckUtils.checkEmpty(path))
			{
				path = FileUtils.getPhotoPath(path);
				
				Xutils.getHeadBitmap(this, mPhoto, path);
				
				mflag = 0;
			}
			else
			{
				
				mPhoto.setImageResource(R.drawable.head_icon);
				
			}						

			mUpImage.setVisibility(bean.getImages().size() >= 10 ? View.GONE : View.VISIBLE);

			imagesize = bean.getImages().size();

			adapter.setmList(bean.getImages());

			mGv.setNumColumns(adapter.getCount());

			int width = ConstantValues.SCREENWIDTH;

			LayoutParams params = new LayoutParams(width < 800 ? width < 720 ? adapter.getCount() * 115 : adapter.getCount() * 155 : adapter.getCount() * 230, LayoutParams.WRAP_CONTENT);

			mGv.setLayoutParams(params);

			adapter.notifyDataSetChanged();

			mHsview.smoothScrollTo(1, 1);
		}
	}

	// Grideview item点击事件
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	{

		Intent images = new Intent(UserInfoNewActivity.this, UserImagesClearActivity.class);
		UserInfoNewEntity bean = (UserInfoNewEntity) adapter.getItem(arg2);
		images.putExtra("image_id", bean.getId());
		images.putExtra("images_url", bean.getUrl());
		startActivityForResult(images, 100);
	}

	// 上传
	public void selectPhotoImage()
	{
		switch (mflag)
		{
			case 0:
				// 上传相册
				dialog.show();

				loadPostsImg(tDataList, tDataList.size());

				break;

			case 1:
				// 上传头像
				dialog.show();
				upLoad(tDataList.get(0));
				mflag = 0;
				break;
			default:
				break;
		}
	}

	// 点击事件的监听
	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.common_back:

				finish();

				break;

			case R.id.myreply:
				// 我的回复
				Intent myreply = new Intent(this, UserMyReplyActivity.class);

				startActivity(myreply);

				break;

			case R.id.common_bt:
				// 编辑
				if (data != null)
				{
					Intent editinfo = new Intent(this, UserInfoEditInfoActivity.class);

					editinfo.putExtra("bean", data);

					startActivityForResult(editinfo, 10);
				}

				break;

			case R.id.myposts:
				// 我的帖子
				Intent myposts = new Intent(this, UserMyPostsActivity.class);
				startActivity(myposts);
				break;
			case R.id.myfriends:
				// 我的关注
				Intent myfriends = new Intent(this, UserMyfriendsActivity.class);
				startActivity(myfriends);
				break;
			case R.id.myfans:
				// 我的粉丝
				Intent myfans = new Intent(this, UserMyfansActivity.class);
				startActivity(myfans);
				break;
			case R.id.user_upimage:
				// 选择相片
				mflag = 0;
				Intent selectpir = new Intent(this, SelectPhotoImages.class);
				selectpir.putExtra("flag", mflag);
				selectpir.putExtra("imagesize", imagesize);
				startActivityForResult(selectpir, 100);
				break;

			case R.id.photo:

				mflag = 1;

				Intent photo = new Intent(UserInfoNewActivity.this, SelectPhotoImages.class);

				photo.putExtra("flag", mflag);

				startActivityForResult(photo, 100);

				break;

			default:
				break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch (resultCode)
		{
			case 10:
				// 编辑后新数据
				getData();
				break;
			case 200:
				// 上传相册片后
				String type = data.getStringExtra("type");

				if ("photo".equals(type) & data != null)
				{
					Bundle bundle2 = data.getExtras();

					ArrayList<String> datalist = (ArrayList<String>) bundle2
							.getSerializable("dataList");

					tDataList = new ArrayList<String>();

					for (int i = 0; i < datalist.size(); i++)
					{
						Bitmap bitmap = ImageUtils.getSmallBitmap(datalist
								.get(i));
						String string = FileUtils.saveBitToSD(bitmap, System
								.currentTimeMillis() + "");

						tDataList.add(string);
					}

				}
				else
				{
					String cameraImagePath = data
							.getStringExtra("cameraImagePath");

					tDataList = new ArrayList<String>();

					tDataList.add(cameraImagePath);
				}

				selectPhotoImage();

				break;
			case 101:
				// 上传头像
				String type1 = data.getStringExtra("type");

				if ("photo".equals(type1) & data != null)
				{
					Bundle bundle3 = data.getExtras();

					ArrayList<String> datalist = (ArrayList<String>) bundle3
							.getSerializable("dataList");

					tDataList = new ArrayList<String>();

					for (int i = 0; i < datalist.size(); i++)
					{
						Bitmap bitmap = ImageUtils.getSmallBitmap(datalist
								.get(i));

						String string = FileUtils.saveBitToSD(bitmap, System
								.currentTimeMillis() + "");

						tDataList.add(string);
					}
				}
				else
				{
					String cameraImagePath = data
							.getStringExtra("cameraImagePath");

					tDataList = new ArrayList<String>();

					tDataList.add(cameraImagePath);
				}

				selectPhotoImage();

				break;
			case 100:
				getData();
				break;
			default:
				break;
		}

		super.onActivityResult(requestCode, resultCode, data);
	}
}
