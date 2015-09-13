package me.wangolf.usercenter;

import me.wangolf.ConstantValues;
import me.wangolf.base.BaseActivity;
import me.wangolf.bean.InfoEntity;
import me.wangolf.factory.ServiceFactory;
import me.wangolf.service.IOAuthCallBack;
import me.wangolf.utils.DialogUtil;
import me.wangolf.utils.GsonTools;
import me.wangolf.utils.ToastUtils;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.meigao.mgolf.R;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class UserMessageClearActivity extends BaseActivity implements OnClickListener {

    @ViewInject(R.id.message_clear)
    private Button mClear;// 请空聊天记录
    @ViewInject(R.id.btn_cancel)
    private Button mCancel;// 关闭
    private String user_id;// 用户ID
    private int friend_id;// 好友ID
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_user_message_clear);
        ViewUtils.inject(this);
        initData();
    }

    @Override
    public void initData() {
        dialog = DialogUtil.getDialog(this);
        mClear.setText("清空聊天记录");
        mCancel.setOnClickListener(this);
        mClear.setOnClickListener(this);
        user_id = getIntent().getStringExtra("user_id");
        friend_id = getIntent().getIntExtra("friend_id", 0);
    }

    @Override
    public void getData() {

    }

    public void clearMessage() {
        dialog.show();
        try {
            ServiceFactory.getCommunityEngineInstatice().clearMessage(user_id, friend_id, new IOAuthCallBack() {

                @Override
                public void getIOAuthCallBack(String result) {
                    if (result.equals(ConstantValues.FAILURE)) {
                        Toast.makeText(getApplicationContext(), ConstantValues.NONETWORK, 0).show();
                    } else {
                        InfoEntity bean = GsonTools.changeGsonToBean(result, InfoEntity.class);
                        if ("1".equals(bean.getStatus())) {
                            ToastUtils.showInfo(UserMessageClearActivity.this, bean.getInfo());
                            setResult(10);
                            finish();
                        } else {
                            ToastUtils.showInfo(UserMessageClearActivity.this, bean.getInfo());
                        }
                        dialog.cancel();
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

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.message_clear:
                clearMessage();
                break;
            case R.id.btn_cancel:
                finish();
                break;
            default:
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (data == null & resultCode == 200) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);

    }
}
