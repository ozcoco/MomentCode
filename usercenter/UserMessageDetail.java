package me.wangolf.usercenter;

import java.util.ArrayList;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.meigao.mgolf.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import me.wangolf.ConstantValues;
import me.wangolf.adapter.UserMessageDetailAdapter;
import me.wangolf.base.BaseActivity;
import me.wangolf.bean.InfoEntity;
import me.wangolf.bean.usercenter.UserMessageDetailEntity;
import me.wangolf.factory.ServiceFactory;
import me.wangolf.service.IOAuthCallBack;
import me.wangolf.utils.CheckUtils;
import me.wangolf.utils.GsonTools;
import me.wangolf.utils.ToastUtils;
import me.wangolf.utils.viewUtils.PullToRefreshBase;
import me.wangolf.utils.viewUtils.PullToRefreshBase.OnRefreshListener;
import me.wangolf.utils.viewUtils.PullToRefreshListView;

public class UserMessageDetail extends BaseActivity implements OnClickListener {
	@ViewInject(R.id.common_title)
	private TextView mTitle;
	@ViewInject(R.id.common_back)
	private TextView mBack;
	@ViewInject(R.id.common_bt)
	private TextView mBt;
	@ViewInject(R.id.pull_refresh_list)
	private PullToRefreshListView mPullList;
	@ViewInject(R.id.list)
	private ListView mDetailList;
	@ViewInject(R.id.c_d_send)
	private Button mSendMessage;// 发送消息
	@ViewInject(R.id.c_d_ed)
	private EditText mEdContent;
	@ViewInject(R.id.message_input)
	private LinearLayout mInput;
	private String user_id;
	private int friend_id;
	private int page = 1;
	private int number = 10;
	private UserMessageDetailAdapter adapter;
	private boolean isRefresh;
	private String mContent;// 发送的内容
	private String mImg_list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_user_myreply);
		ViewUtils.inject(this);
		if (adapter == null) {
			adapter = new UserMessageDetailAdapter(this);
		} else {
			adapter.notifyDataSetChanged();
		}
		mPullList.getRefreshableView().setAdapter(adapter);
		mPullList.getRefreshableView().setDividerHeight(0);
		setPullRefresh();// 初始化下拉刷新
		initData();// 初始化数据
	}

	// 设置下拉刷新==========
	public void setPullRefresh() {
		mPullList.setPullLoadEnabled(false);
		// 滚动到底自动加载可用
		mPullList.setScrollLoadEnabled(true);
		// 得到实际的ListView 设置点击
		mPullList.getRefreshableView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			}
		});

		// 设置下拉刷新的listener
		mPullList.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				isRefresh = true;
				page++;
				getData();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

				// page++;
				// getData();
			}
		});
	}

	// 关闭刷新
	private void onLoaded() {
		mPullList.onPullDownRefreshComplete();
		mPullList.onPullUpRefreshComplete();
	}

	// 初始化数据=====
	@Override
	public void initData() {
		mBack.setVisibility(View.VISIBLE);
		mBt.setVisibility(View.VISIBLE);
		mInput.setVisibility(View.VISIBLE);
		mBt.setText("更多");
		user_id = ConstantValues.UID;
		friend_id = Integer.parseInt(getIntent().getStringExtra("friend_id"));
		mTitle.setText(getIntent().getStringExtra("friend_name"));
		mBack.setOnClickListener(this);
		mSendMessage.setOnClickListener(this);
		mBt.setOnClickListener(this);
		getData();
	}

	// 获取数据=======
	@Override
	public void getData() {
		try {
			ServiceFactory.getCommunityEngineInstatice().getLeaveMessageDetail(user_id, friend_id, page, number, new IOAuthCallBack() {

				@Override
				public void getIOAuthCallBack(String result) {

					if (result.equals(ConstantValues.FAILURE)) {

						ToastUtils.showInfo(getApplicationContext(), ConstantValues.NONETWORK);
					} else {
						UserMessageDetailEntity bean = GsonTools.changeGsonToBean(result, UserMessageDetailEntity.class);
						if (bean.getStatus() == 1) {
							ArrayList<UserMessageDetailEntity> data = bean.getData();
							ArrayList<UserMessageDetailEntity> datas = new ArrayList<UserMessageDetailEntity>();
							for (int i = 0; i < data.size(); i++) {
								datas.add(data.get(data.size() - 1 - i));
							}
							ArrayList<UserMessageDetailEntity> mList = adapter.getmMessage_list();
							if (data.size() == 0) {
								// 没有更多数据
								ToastUtils.showInfo(UserMessageDetail.this, ConstantValues.NOMORE);
								// return;
							}
							if (isRefresh) {
								// 下拉刷新
								isRefresh = false;
								// mList.clear();
								mList.addAll(0, datas);
							} else {
								// mList == null初始化。mList ！= null 加载更多
								if (mList == null) {

									adapter.setmMessage_list(datas);
								} else {

									adapter.setmMessage_list(datas);

								}
								scrollToBottomListItem();
							}
							adapter.notifyDataSetChanged();
							onLoaded();
						} else {
							ToastUtils.showInfo(UserMessageDetail.this, bean.getInfo());
						}

					}

				}
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 发送内容后自动滚动到最底
	private void scrollToBottomListItem() {
		if (mPullList != null) {
			mPullList.getRefreshableView().setSelection(adapter.getCount() + 1);
		}
	}

	// 发送消息
	public void sendData() {
		try {
			ServiceFactory.getCommunityEngineInstatice().writeMessage(user_id, friend_id, mContent, mImg_list, new IOAuthCallBack() {

				@Override
				public void getIOAuthCallBack(String result) {
					if (result.equals(ConstantValues.FAILURE)) {

						ToastUtils.showInfo(getApplicationContext(), ConstantValues.NONETWORK);
					} else {
						InfoEntity bean = GsonTools.changeGsonToBean(result, InfoEntity.class);
						if ("1".equals(bean.getStatus())) {
							ToastUtils.showInfo(UserMessageDetail.this, bean.getInfo());
							page = 1;
							 InputMethodManager();//隐键盘
							getData();
							mEdContent.setText("");// 清空内容
						} else {
							ToastUtils.showInfo(UserMessageDetail.this, bean.getInfo());
						}
					}

				}
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// 确认发送
	public void sendMessage() {
		mContent = mEdContent.getText().toString();
		if (CheckUtils.checkEmpty(mContent)) {
			ToastUtils.showInfo(this, "内容不能为空");
			return;
		}
		sendData();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == 10) {
			page = 1;
			getData();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.common_back:
			finish();
			break;
		case R.id.c_d_send:
			sendMessage();
			break;
		case R.id.common_bt:
			Intent messageclear = new Intent(this, UserMessageClearActivity.class);
			messageclear.putExtra("user_id", user_id);
			messageclear.putExtra("friend_id", friend_id);
			startActivityForResult(messageclear, 10);
			break;
		default:
			break;
		}

	}
	// 显示或隐软争键盘控作
	public void InputMethodManager() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
		if (imm.hideSoftInputFromWindow(mEdContent.getWindowToken(), 0)) {
			imm.showSoftInput(mEdContent, 0);
			imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			// 软键盘已弹出
		} else {
			// 软键盘未弹出
		}
	}
}
