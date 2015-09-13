package me.wangolf.usercenter;

/**
 * ============================================================
 * 
 * 版权 ：美高传媒 版权所有 (c) 下午2:30:30
 * 
 * 作者:copy
 * 
 * 版本 ：1.0
 * 
 * 创建日期 ： 下午2:30:30
 * 
 * 描述 ：
 * 
 * 
 * 修订历史 ：
 * 
 * ============================================================
 **/

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.meigao.mgolf.R;

import de.hdodenhof.circleimageview.CircleImageView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import me.wangolf.ConstantValues;
import me.wangolf.base.BaseActivity;
import me.wangolf.bean.InfoEntity;
import me.wangolf.bean.usercenter.UserInfoNewEntity;
import me.wangolf.bean.usercenter.UserMyFansEntity;
import me.wangolf.bean.usercenter.UserMyFriendsEntity;
import me.wangolf.factory.ServiceFactory;
import me.wangolf.service.IOAuthCallBack;
import me.wangolf.utils.CheckUtils;
import me.wangolf.utils.DialogUtil;
import me.wangolf.utils.FileUtils;
import me.wangolf.utils.GsonTools;
import me.wangolf.utils.ImageViewUtil;
import me.wangolf.utils.ToastUtils;

public class UserFriendsInfoActivity extends BaseActivity implements
		OnClickListener
{
	@ViewInject(R.id.common_title)
	private TextView			mTitle;				// 标题
	@ViewInject(R.id.common_back)
	private TextView			mBack;				// 返回
	@ViewInject(R.id.common_bt)
	private TextView			mBt;				// 更多
	@ViewInject(R.id.myposts_num)
	private TextView			mMypostsNum;		// 我的帖子数量
	@ViewInject(R.id.myreply_num)
	private TextView			mMyreplyNum;		// 回帖数量
	@ViewInject(R.id.myfriends_num)
	private TextView			mMyfriendsNum;		// 我的关注数量
	@ViewInject(R.id.myfans_num)
	private TextView			mMyfansNum;			// 我的粉丝数量
	@ViewInject(R.id.myposts)
	private RelativeLayout		mMyposts;			// 我的帖子
	@ViewInject(R.id.myreply)
	private RelativeLayout		mMyreply;			// 回帖
	@ViewInject(R.id.myfriends)
	private RelativeLayout		mMyfriends;			// 我的关注
	@ViewInject(R.id.myfans)
	private RelativeLayout		mMyfans;			// 我的粉丝
	@ViewInject(R.id.send_message)
	private Button				mSendMessage;		// 发消息
	@ViewInject(R.id.photo)
	private CircleImageView		mPhoto;
	@ViewInject(R.id.user_name)
	private TextView			mUserName;			// 昵称
	@ViewInject(R.id.user_intro)
	private TextView			mUserIntro;			// 签名
	private String				user_id;			// 用户iD
	public int					mAttention_status;	// 字段 1 留言 2关注
	private int					page;
	private int					number;
	private UserMyFriendsEntity	bean;
	private UserMyFansEntity	beanf;
	private int					friend_id;
	private int					flag;				// 0社区 ,1我的关注，2我的粉丝
	private String				friend_name;
	private Dialog				dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.ac_user_friend_info);

		ViewUtils.inject(this);

		initData();
	}

	@Override
	public void initData()
	{
		dialog = DialogUtil.getDialog(this);

		mBt.setText("更多");

		mBack.setVisibility(View.VISIBLE);

		mBt.setVisibility(View.VISIBLE);

		mBack.setOnClickListener(this);

		mBt.setOnClickListener(this);

		mMyposts.setOnClickListener(this);

		mMyreply.setOnClickListener(this);

		mMyfriends.setOnClickListener(this);

		mMyfans.setOnClickListener(this);

		mSendMessage.setOnClickListener(this);

		flag = getIntent().getIntExtra("flag", -1);

		switchFrom();
	}

	// 获取数据
	@Override
	public void getData()
	{
		dialog.show();
		try
		{
			ServiceFactory
					.getCommunityEngineInstatice()
					.getUserInfo(user_id, friend_id, page, number, new IOAuthCallBack()
					{
						@Override
						public void getIOAuthCallBack(String result)
						{

							if (result.equals(ConstantValues.FAILURE))
							{
								ToastUtils
										.showInfo(UserFriendsInfoActivity.this, ConstantValues.NONETWORK);
							}
							else
							{
								UserInfoNewEntity bean = GsonTools
										.changeGsonToBean(result, UserInfoNewEntity.class);
								if (bean.getStatus() == 1)
								{
									if (bean.getData() != null)
									{
										UserInfoNewEntity data = bean.getData()
												.get(0);
										setUserInfo(data);// 更新页面
									}
								}
								else
								{
									ToastUtils
											.showInfo(UserFriendsInfoActivity.this, bean
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

	// 点击事件
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
				myreply.putExtra("user_id", friend_id + "");
				startActivity(myreply);
				break;
			case R.id.common_bt:
				// 更多
				Intent cancelFriends = new Intent(this, UserCancelFriendsActivity.class);
				cancelFriends.putExtra("user_id", user_id);
				cancelFriends.putExtra("friend_id", friend_id);
				cancelFriends.putExtra("mAttention_status", mAttention_status);
				startActivityForResult(cancelFriends, 10);
				break;
			case R.id.myposts:
				// 他的帖子
				Intent myposts = new Intent(this, UserMyPostsActivity.class);
				myposts.putExtra("user_id", friend_id + "");
				startActivity(myposts);
				break;
			case R.id.myfriends:
				// 他的关注
				Intent myfriends = new Intent(this, UserMyfriendsActivity.class);
				myfriends.putExtra("user_id", friend_id + "");
				startActivity(myfriends);
				break;
			case R.id.myfans:
				// 他的粉丝
				Intent myfans = new Intent(this, UserMyfansActivity.class);
				myfans.putExtra("user_id", friend_id + "");
				startActivity(myfans);
				break;
			case R.id.send_message:
				// 关注或留言
				AttentionStatus(mAttention_status);
				break;
			default:
				break;
		}
	}

	// 判断来自哪里 0社区 ,1我的关注，2我的粉丝
	public void switchFrom()
	{
		switch (flag)
		{
			case 0:

				friend_id = getIntent().getIntExtra("user_id", 0);
				friend_name = getIntent().getStringExtra("friend_name");
				user_id = ConstantValues.UID;
				mTitle.setText(friend_name);
				break;
			case 1:
				bean = (UserMyFriendsEntity) getIntent()
						.getSerializableExtra("bean");
				if (bean != null)
				{
					mTitle.setText(bean.getFriend_name());
					user_id = ConstantValues.UID;
					friend_id = bean.getFriend_id();
					friend_name = bean.getFriend_name();
				}
				break;
			case 2:
				beanf = (UserMyFansEntity) getIntent()
						.getSerializableExtra("bean");
				if (beanf != null)
				{
					mTitle.setText(beanf.getFans_name());
					user_id = ConstantValues.UID;;
					friend_id = beanf.getFans_id();
					friend_name = beanf.getFans_name();
				}
				break;
			default:
				break;
		}
		getData();
	}

	// 更新界面
	public void setUserInfo(UserInfoNewEntity bean)
	{

		if (bean != null)
		{
			mMypostsNum.setText(bean.getPosts_count() + "");
			mMyreplyNum.setText(bean.getReply_count() + "");
			mMyfriendsNum.setText(bean.getAttention_count() + "");
			mMyfansNum.setText(bean.getFans_count() + "");
			mUserName.setText(CheckUtils.checkEmpty(bean.getNick_name()) ? bean
					.getMobile() : bean.getNick_name());
			mTitle.setText(CheckUtils.checkEmpty(bean.getNick_name()) ? bean
					.getMobile() : bean.getNick_name());
			mUserIntro.setText(bean.getMy_intro());
			String path = bean.getPhoto();
			if (!CheckUtils.checkEmpty(path))
			{
				path = FileUtils.getPhotoPath(path);
			}
			ImageViewUtil.loadimg(path, mPhoto, this);
			switch (bean.getAttention_status())
			{
				case 1:
					mSendMessage.setText("发送消息");
					mAttention_status = 1;
					break;
				case 2:
					mAttention_status = 2;
					mSendMessage.setText("关注");
					break;
				default:
					break;
			}
		}
	}

	// 关注或留言
	public void AttentionStatus(int status)
	{

		switch (status)
		{
			case 1:
				// 留言
				Intent sendmessage = new Intent(this, UserMessageDetail.class);
				sendmessage.putExtra("friend_id", friend_id + "");
				sendmessage.putExtra("friend_name", friend_name);
				startActivity(sendmessage);
				break;
			case 2:
				// 关注
				toAttentionFriends();
				break;
			default:
				break;
		}
	}

	// 关注好友
	public void toAttentionFriends()
	{
		dialog.show();
		try
		{
			ServiceFactory
					.getCommunityEngineInstatice()
					.toAttentionFriends(user_id, friend_id, new IOAuthCallBack()
					{

						@Override
						public void getIOAuthCallBack(String result)
						{
							if (result.equals(ConstantValues.FAILURE))
							{
								ToastUtils
										.showInfo(UserFriendsInfoActivity.this, ConstantValues.NONETWORK);
							}
							else
							{
								InfoEntity bean = GsonTools
										.changeGsonToBean(result, InfoEntity.class);
								if ("1".equals(bean.getStatus()))
								{
									mSendMessage.setText("发送消息");
									mAttention_status = 1;
									ToastUtils
											.showInfo(UserFriendsInfoActivity.this, bean
													.getInfo());
								}
								else
								{
									ToastUtils
											.showInfo(UserFriendsInfoActivity.this, bean
													.getInfo());
								}
								dialog.cancel();
							}
						}
					});
		}
		catch(Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 获取返回 更新界面
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (resultCode == 10)
		{
			getData();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
