package me.wangolf.usercenter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.meigao.mgolf.R;

import me.wangolf.ConstantValues;
import me.wangolf.base.BaseActivity;
import me.wangolf.bean.InfoEntity;
import me.wangolf.factory.ServiceFactory;
import me.wangolf.service.IOAuthCallBack;
import me.wangolf.utils.CheckUtils;
import me.wangolf.utils.GsonTools;
import me.wangolf.utils.ShowPickUtils;
import me.wangolf.utils.ToastUtils;

/**
 * Created by Administrator on 2015/6/10.
 */
public class UserBindMobileActivity extends BaseActivity implements View.OnClickListener {
    @ViewInject(R.id.common_title)
    private TextView mTitle;
    @ViewInject(R.id.common_back)
    private Button mBack;
    @ViewInject(R.id.ed_phone)
    private EditText mPhone;
    @ViewInject(R.id.ed_code)
    private EditText meCode;
    @ViewInject(R.id.getcode)
    private Button mGetcode;
    @ViewInject(R.id.bt_bindMobile)
    private Button mBindMobile;
    @ViewInject(R.id.tv_binInfo)
    private TextView mBinInfo;
    @ViewInject(R.id.tv_speech)
    private TextView mSpeech;
    private String phone;
    private String code;
    private int user_id;
    private boolean isFlag;
    private String codeType="0";
    private String flag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_bindmobile);
        ViewUtils.inject(this);
        initData();
    }


    @Override
    public void initData() {
        user_id= getIntent().getIntExtra("uid", 0);
        flag = getIntent().getStringExtra("flag");
        mTitle.setText("绑定手机号");
        mBack.setVisibility(View.VISIBLE);
        mBack.setOnClickListener(this);
        mGetcode.setOnClickListener(this);
        mBindMobile.setOnClickListener(this);
        mSpeech.setOnClickListener(this);
        if("binWx".equals(flag)){
            mBinInfo.setText("更换绑定的手机号");
        }
    }

    @Override
    public void getData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.getcode:
                if(CheckUtils.checkEmpty(mPhone.getText().toString().trim())){
                    ToastUtils.showInfo(this,"手机号码不能为空");
                    return;
                }
                phone=mPhone.getText().toString().trim();
                if (phone.length() != 11) {
                    ToastUtils.showInfo(this,"请输入正确的手机号码");
                    return;
                }

                getCode(phone);
                break;
            case R.id.bt_bindMobile:
                codeType="0";
                if(CheckUtils.checkEmpty(mPhone.getText().toString().trim())){
                    ToastUtils.showInfo(this,"手机号码不能为空");
                    return;
                }
                phone=mPhone.getText().toString().trim();
                if (phone.length() != 11) {
                    ToastUtils.showInfo(this,"请输入正确的手机号码");
                    return;
                }
                if(CheckUtils.checkEmpty(meCode.getText().toString().trim())){
                    ToastUtils.showInfo(this,"验证码不能为空");
                    return;
                }
                code=meCode.getText().toString().trim();
                toBindMobile(phone,code);
                break;
            case R.id.common_back:
                finish();
                break;
            case R.id.tv_speech:
                ShowPickUtils.ShowSpeechDialog(this,1);
                break;
            default:
                break;
        }

    }
//获取验证码
    private void getCode(String phone){
        try {
            ServiceFactory.getIUserEngineInstatice().toCheckMobileRegist(phone,codeType, new IOAuthCallBack() {
                @Override
                public void getIOAuthCallBack(String result) {
                    InfoEntity bean = GsonTools.jsonToBean(result, InfoEntity.class);
                    if ("1".equals(bean.getStatus())) {
                        if("0".equals(codeType))
                            updateButton();
                        ToastUtils.showInfo(UserBindMobileActivity.this, bean.getInfo());
                    } else {
                        ShowPickUtils.ShowDialog(UserBindMobileActivity.this,bean.getInfo());
                        //ToastUtils.showInfo(UserBindMobileActivity.this, bean.getInfo());
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //绑定手机号
    private void toBindMobile(final String phone,String code){

        try {
            ServiceFactory.getIUserEngineInstatice().toBindMobile(user_id, phone, code, new IOAuthCallBack() {
                @Override
                public void getIOAuthCallBack(String result) {
                    InfoEntity bean = GsonTools.jsonToBean(result, InfoEntity.class);
                    if ("1".equals(bean.getStatus())) {
                        ToastUtils.showInfo(UserBindMobileActivity.this, bean.getInfo());
                        Intent bindResult = new Intent();
                        bindResult.putExtra("result", "1");
                        ConstantValues.USER_MOBILE = phone;
                        setResult(105, bindResult);
                        finish();
                    } else {

                        ToastUtils.showInfo(UserBindMobileActivity.this, bean.getInfo());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    protected void updateButton() {
        mGetcode.setBackgroundColor(getResources().getColor(R.color.gray));
        mGetcode.setClickable(false);
        final Handler ha = new Handler() {
            @SuppressLint("NewApi")
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1) {
                    // 更新按钮文本
                    int time = msg.arg1;
                    if (time == 0) {
                        codeType="0";
                        //mGetcode.setText("语音验证码");
                        mGetcode.setText("重新获取");
                        mGetcode.setClickable(true);
                        mGetcode.setBackground(getResources().getDrawable(R.drawable.bt_icode_selector));
                    } else {
                        mGetcode.setText(time + "秒");
                    }
                }

            }
        };
        new Thread() {
            public void run() {
                try {
                    int time = 30;// 60秒
                    while (time > 0 && isFlag == false) {
                        time--;
                        Message msg = Message.obtain(ha, 1);
                        msg.arg1 = time;
                        msg.sendToTarget();
                        sleep(1000);
                    }
                } catch (InterruptedException e) {
                    // ExceptionUtils.print(e);
                }
            };
        }.start();
    }
    public void getSpeechCode(){
        codeType="2";
        phone = mPhone.getText().toString().trim();
        if (phone.length() != 11) {
            ToastUtils.showInfo(UserBindMobileActivity.this,"请输入正确的手机号码");
            return;
        }
        getCode(phone);

    }
}
