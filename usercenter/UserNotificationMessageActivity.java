package me.wangolf.usercenter;

/**
 * ============================================================
 *
 * 版权 ：美高传媒 版权所有 (c) 下午3:48:29
 *
 * 作者:copy
 *
 * 版本 ：1.0
 *
 * 创建日期 ： 下午3:48:29
 *
 * 描述 ：留言列表
 *
 *
 * 修订历史 ：
 *
 * ============================================================
 **/

import java.util.ArrayList;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.meigao.mgolf.R;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import me.wangolf.ConstantValues;
import me.wangolf.adapter.UserMessageAdapter;
import me.wangolf.adapter.UserNotificationAdapter;
/**
 * ============================================================
 *
 * 版权 ：美高传媒 版权所有 (c) 下午2:33:34
 *
 * 作者:copy
 *
 * 版本 ：1.0
 *
 * 创建日期 ： 下午2:33:34
 *
 * 描述 ：个人中心消息
 *
 *
 * 修订历史 ：
 *
 * ============================================================
 **/
import me.wangolf.base.BaseActivity;
import me.wangolf.bean.usercenter.UserMessageListEntity;
import me.wangolf.bean.usercenter.UserNotificationEntity;
import me.wangolf.factory.ServiceFactory;
import me.wangolf.service.IOAuthCallBack;
import me.wangolf.utils.DialogUtil;
import me.wangolf.utils.GsonTools;
import me.wangolf.utils.ToastUtils;
import me.wangolf.utils.viewUtils.PullToRefreshBase;
import me.wangolf.utils.viewUtils.PullToRefreshBase.OnRefreshListener;
import me.wangolf.utils.viewUtils.PullToRefreshListView;

public class UserNotificationMessageActivity extends BaseActivity implements OnClickListener {
    @ViewInject(R.id.common_title)
    private TextView mTitle;
    @ViewInject(R.id.common_back)
    private TextView mBack;
    @ViewInject(R.id.common_bt)
    private TextView mBt;
    @ViewInject(R.id.pull_refresh_list)
    private PullToRefreshListView mPullList;
    @ViewInject(R.id.rb_notification)
    private TextView mNotification;// 通知
    @ViewInject(R.id.rb_message)
    private TextView mMessage;// 留言
    private UserMessageAdapter adapter_m;
    private UserNotificationAdapter adapter_n;
    private String user_id;// 用户iD
    private int page = 1;// 页
    private int number = 10;// 每页数量
    private int isFalg = 0;// 标记 0通知 1 留言
    private boolean isRefresh;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_user_message);
        ViewUtils.inject(this);
        if (adapter_m == null) {
            adapter_m = new UserMessageAdapter(this);
            adapter_n = new UserNotificationAdapter(this);
            mPullList.getRefreshableView().setAdapter(adapter_n);
        } else {
            adapter_m.notifyDataSetChanged();
            adapter_n.notifyDataSetChanged();
        }
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

                if (isFalg == 1) {
                    Intent detail = new Intent(UserNotificationMessageActivity.this, UserMessageDetail.class);
                    UserMessageListEntity bean = (UserMessageListEntity) adapter_m.getItem(position);
                    int friend_id = bean.getFriend_id();
                    detail.putExtra("friend_id", friend_id + "");
                    detail.putExtra("friend_name", bean.getName());
                    startActivity(detail);
                }
            }
        });

        // 设置下拉刷新的listener
        mPullList.setOnRefreshListener(new OnRefreshListener<ListView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                isRefresh = true;
                page = 1;
                getData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page++;
                getData();
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
        dialog = DialogUtil.getDialog(this);
        mTitle.setText("消息");
        mBack.setVisibility(View.VISIBLE);
        mBack.setOnClickListener(this);
        mNotification.setOnClickListener(this);
        mMessage.setOnClickListener(this);
        user_id = ConstantValues.UID;
        getNotificationData();
    }

    // 获取数据=======
    @Override
    public void getData() {
        switch (isFalg) {
            case 0:
                getNotificationData();
                break;
            case 1:
                getMessageData();
                break;

            default:
                break;
        }
    }

    // 点击事件
    @SuppressLint("NewApi")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.common_back:
                finish();
                break;
            case R.id.rb_notification:
                isFalg = 0;
                page = 1;
                if (adapter_m.getmMessage_list() != null)
                    adapter_m.getmMessage_list().clear();
                adapter_m.notifyDataSetChanged();
                mPullList.getRefreshableView().setAdapter(adapter_n);
                setRadioButton();
                mNotification.setBackground(getResources().getDrawable(R.drawable.rd_message_l));
                mNotification.setTextColor(getResources().getColor(R.color.white));
                getData();
                break;
            case R.id.rb_message:
                isFalg = 1;
                page = 1;
                if (adapter_n.getmMessage_list() != null)
                    adapter_n.getmMessage_list().clear();
                adapter_n.notifyDataSetChanged();
                mPullList.getRefreshableView().setAdapter(adapter_m);
                setRadioButton();
                mMessage.setBackground(getResources().getDrawable(R.drawable.rd_message_r));
                mMessage.setTextColor(getResources().getColor(R.color.white));
                getData();
                break;
            default:
                break;
        }
    }

    // 获取留言数据
    public void getNotificationData() {
        dialog.show();
        try {
            ServiceFactory.getCommunityEngineInstatice().getNotification(user_id, page, number, new IOAuthCallBack() {

                @Override
                public void getIOAuthCallBack(String result) {

                    if (result.equals(ConstantValues.FAILURE)) {
                        ToastUtils.showInfo(UserNotificationMessageActivity.this, ConstantValues.NONETWORK);
                    } else {
                        UserNotificationEntity bean = GsonTools.changeGsonToBean(result, UserNotificationEntity.class);
                        if (bean.getStatus() == 1) {
                            ArrayList<UserNotificationEntity> data = bean.getData();
                            ArrayList<UserNotificationEntity> mList = adapter_n.getmMessage_list();
                            if (data.size() == 0) {
                                // 没有更多数据
                                ToastUtils.showInfo(UserNotificationMessageActivity.this, ConstantValues.NOMORE);
                                dialog.cancel();
                                return;
                            }
                            if (isRefresh) {
                                // 下拉刷新
                                isRefresh = false;
                                mList.clear();
                                adapter_n.setmMessage_list(data);
                            } else {
                                // mList == null初始化。mList ！= null 加载更多
                                if (mList == null) {
                                    adapter_n.setmMessage_list(data);
                                } else {
                                    mList.addAll(data);
                                }
                            }
                            adapter_n.notifyDataSetChanged();

                        } else {
                            ToastUtils.showInfo(UserNotificationMessageActivity.this, bean.getInfo());
                        }

                    }
                    dialog.cancel();
                    onLoaded();

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // 获取消息数据
    public void getMessageData() {
        dialog.show();
        try {
            ServiceFactory.getCommunityEngineInstatice().getLeaveMessageList(user_id, page, number, new IOAuthCallBack() {
                @Override
                public void getIOAuthCallBack(String result) {

                    if (result.equals(ConstantValues.FAILURE)) {
                        ToastUtils.showInfo(UserNotificationMessageActivity.this, ConstantValues.NONETWORK);
                    } else {
                        UserMessageListEntity bean = GsonTools.changeGsonToBean(result, UserMessageListEntity.class);
                        if (bean.getStatus() == 1) {
                            ArrayList<UserMessageListEntity> data = bean.getData();
                            ArrayList<UserMessageListEntity> mList = adapter_m.getmMessage_list();
                            if (data.size() == 0) {
                                // 没有更多数据
                                ToastUtils.showInfo(UserNotificationMessageActivity.this, ConstantValues.NOMORE);
                                dialog.cancel();
                                return;
                            }
                            if (isRefresh) {
                                // 下拉刷新
                                isRefresh = false;
                                mList.clear();
                                adapter_m.setmMessage_list(data);
                            } else {
                                // mList == null初始化。mList ！= null 加载更多
                                if (mList == null) {
                                    adapter_m.setmMessage_list(data);
                                } else {
                                    mList.addAll(data);
                                }
                            }
                            adapter_m.notifyDataSetChanged();

                        } else {
                            ToastUtils.showInfo(UserNotificationMessageActivity.this, bean.getInfo());
                        }

                    }
                    dialog.cancel();
                    onLoaded();

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 设字体色
    public void setRadioButton() {
        mNotification.setBackgroundColor(getResources().getColor(R.color.transparent));
        mMessage.setBackgroundColor(getResources().getColor(R.color.transparent));
        mNotification.setTextColor(getResources().getColor(R.color.common_text));
        mMessage.setTextColor(getResources().getColor(R.color.common_text));
    }

}
