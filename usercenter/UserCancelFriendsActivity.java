package me.wangolf.usercenter;

/**
 * ============================================================
 * 
 * 版权 ：美高传媒 版权所有 (c) 下午3:07:17
 * 
 * 作者:copy
 * 
 * 版本 ：1.0
 * 
 * 创建日期 ： 下午3:07:17
 * 
 * 描述 ： 取消关注好友接口
 *  
 * 
 * 修订历史 ：
 * 
 * ============================================================
 **/

import me.wangolf.ConstantValues;
import me.wangolf.base.BaseActivity;
import me.wangolf.bean.InfoEntity;
import me.wangolf.factory.ServiceFactory;
import me.wangolf.service.IOAuthCallBack;
import me.wangolf.utils.GsonTools;
import me.wangolf.utils.ToastUtils;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.meigao.mgolf.R;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class UserCancelFriendsActivity extends BaseActivity implements OnClickListener {

	@ViewInject(R.id.message_clear)
	private Button mClear;// 请空聊天记录
	@ViewInject(R.id.btn_cancel)
	private Button mCancel;// 关闭
	private String user_id;// 用户ID
	private int friend_id;// 好友ID
	private int mAttention_status;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_user_message_clear);
		ViewUtils.inject(this);
		initData();
	}

	@Override
	public void initData() {
		mCancel.setOnClickListener(this);
		mClear.setOnClickListener(this);
		user_id = getIntent().getStringExtra("user_id");
		friend_id = getIntent().getIntExtra("friend_id", 0);
		mAttention_status = getIntent().getIntExtra("mAttention_status", 0);
		switch (mAttention_status) {
		case 1:
			mClear.setText("取消关注");
			break;
		case 2:
			mClear.setText("关注Ta");
			break;
		default:
			break;
		}

	}

	@Override
	public void getData() {

	}

	// 点击事伯的监听
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.message_clear:
			Attention_status();
			break;
		case R.id.btn_cancel:
			finish();
			break;
		default:
			break;
		}
	}

	// 关注的操作
	public void Attention_status() {
		switch (mAttention_status) {
		case 1:
			// 取消关注
			cancelFriends();
			break;
		case 2:
			// 加关注
			toAttentionFriends();
			break;
		default:
			break;
		}
	}

	// 关注好友
	public void toAttentionFriends() {

		try {
			ServiceFactory.getCommunityEngineInstatice().toAttentionFriends(user_id, friend_id, new IOAuthCallBack() {

				@Override
				public void getIOAuthCallBack(String result) {
					if (result.equals(ConstantValues.FAILURE)) {
						Toast.makeText(getApplicationContext(), ConstantValues.NONETWORK, 0).show();
					} else {
						InfoEntity bean = GsonTools.changeGsonToBean(result, InfoEntity.class);
						if ("1".equals(bean.getStatus())) {
							ToastUtils.showInfo(UserCancelFriendsActivity.this, bean.getInfo());
							setResult(10);
							finish();
						} else {
							ToastUtils.showInfo(UserCancelFriendsActivity.this, bean.getInfo());
						}
					}
				}
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 取消关注好友
	public void cancelFriends() {
		try {
			ServiceFactory.getCommunityEngineInstatice().CancelFriends(user_id, friend_id, new IOAuthCallBack() {

				@Override
				public void getIOAuthCallBack(String result) {
					if (result.equals(ConstantValues.FAILURE)) {
						Toast.makeText(getApplicationContext(), ConstantValues.NONETWORK, 0).show();
					} else {
						InfoEntity bean = GsonTools.changeGsonToBean(result, InfoEntity.class);
						if ("1".equals(bean.getStatus())) {
							ToastUtils.showInfo(UserCancelFriendsActivity.this, bean.getInfo());
							setResult(10);
							finish();
						} else {
							ToastUtils.showInfo(UserCancelFriendsActivity.this, bean.getInfo());
						}
					}

				}
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (data == null & resultCode == 200) {
			return;
		}

		super.onActivityResult(requestCode, resultCode, data);

	}
}
