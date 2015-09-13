package me.wangolf.usercenter;

import org.apache.commons.codec.digest.DigestUtils;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.meigao.mgolf.R;

import me.wangolf.ConstantValues;
import me.wangolf.base.BaseActivity;
import me.wangolf.bean.InfoEntity;
import me.wangolf.factory.ServiceFactory;
import me.wangolf.service.IOAuthCallBack;
import me.wangolf.utils.CheckUtils;
import me.wangolf.utils.DialogUtil;
import me.wangolf.utils.GsonTools;
import me.wangolf.utils.SharedPreferencesUtils;
import me.wangolf.utils.ToastUtils;

public class UpDataPassword extends BaseActivity implements OnClickListener 
{
    @ViewInject(R.id.common_back)
    private Button common_back; // 后退
    @ViewInject(R.id.common_title)
    private TextView common_title;// 标题
    @ViewInject(R.id.ed_old_pwd)
    private EditText ed_old_pwd;// 旧密码
    @ViewInject(R.id.ed_new_pwd)
    private EditText ed_new_pwd;// 新密码
    @ViewInject(R.id.up_pwd)
    private Button up_pwd;// 保存密码
    @ViewInject(R.id.ed_repeat_pwd)
    private EditText mRepeatPwd;
    private String npassword;
    private String opassword;
    private String repeatpwd;
    private String uid;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.ac_update_pwd);
        
        ViewUtils.inject(this);
        
        initData();
    }

    @Override
    public void initData() 
    {
        dialog = DialogUtil.getDialog(this);
        
        common_back.setVisibility(View.VISIBLE);
        
        common_title.setText("修改密码");
        
        common_back.setOnClickListener(this);
        
        up_pwd.setOnClickListener(this);
        
        uid = ConstantValues.UID;

    }

    @Override
    public void getData() 
    {

    }

    public void upUserPassword() 
    {
        dialog.show();
        
        try 
        {
            ServiceFactory.getIUserEngineInstatice().upUserPassword(uid, opassword, npassword, new IOAuthCallBack() {

                @Override
                public void getIOAuthCallBack(String result)
                {
                	
//                	ToastUtils.showInfo(getApplication(), result);              	
                	
                    if (result.equals(ConstantValues.FAILURE)) 
                    {

                        ToastUtils.showInfo(UpDataPassword.this, ConstantValues.NONETWORK);
                        
                    } 
                    else 
                    {
                        InfoEntity bean = GsonTools.changeGsonToBean(result, InfoEntity.class);
                        
                        if ("1".equals(bean.getStatus())) 
                        {
                            
                        	ToastUtils.showInfo(UpDataPassword.this, bean.getInfo());
                            
                        	SharedPreferencesUtils.saveString(getBaseContext(), "mgolf_p", npassword);//缓存密码
                        	
                            finish();
                            
                        } 
                        else 
                        {
                            ToastUtils.showInfo(UpDataPassword.this, bean.getInfo());
                        }
                    }
                    
                    dialog.cancel();
                }

            });
        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.common_back:
                finish();
                break;
            case R.id.up_pwd:
                opassword = DigestUtils.md5Hex(ed_old_pwd.getText().toString().trim());
                npassword = DigestUtils.md5Hex(ed_new_pwd.getText().toString().trim());
                repeatpwd = DigestUtils.md5Hex(mRepeatPwd.getText().toString().trim());

                if (CheckUtils.checkEmpty(ed_old_pwd.getText().toString().trim())) {
                    ToastUtils.showInfo(this, "旧密码不能为空");
                    return;
                }
                if (CheckUtils.checkEmpty(ed_new_pwd.getText().toString().trim())|ed_new_pwd.getText().toString().trim().length()<6) {
                    ToastUtils.showInfo(this, "新密码空或长度小于6");
                    return;
                }
                if (CheckUtils.checkEmpty(mRepeatPwd.getText().toString().trim())|ed_new_pwd.getText().toString().trim().length()<6) {
                    ToastUtils.showInfo(this, "重复密码为空或长度小于6");
                    return;
                }
                if (!npassword.equals(repeatpwd)) {
                    ToastUtils.showInfo(this, "两次密码不一致");
                    return;
                }

                upUserPassword();
                break;
            default:
                break;
        }
    }
}
