package me.wangolf.usercenter;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.meigao.mgolf.R;

import me.wangolf.ConstantValues;
import me.wangolf.base.BaseActivity;
import me.wangolf.bean.InfoEntity;
import me.wangolf.community.CommunityImgActivit;
import me.wangolf.factory.ServiceFactory;
import me.wangolf.service.IOAuthCallBack;
import me.wangolf.utils.CheckUtils;
import me.wangolf.utils.DialogUtil;
import me.wangolf.utils.GsonTools;
import me.wangolf.utils.ToastUtils;

public class UserImagesClearActivity extends BaseActivity implements OnClickListener {

    @ViewInject(R.id.images_clear)
    private Button mClear;// 请空聊天记录
    @ViewInject(R.id.btn_cancel)
    private Button mCancel;// 关闭
    @ViewInject(R.id.show_image)
    private Button mShowImage;
    private String user_id;// 用户ID
    private int images_id;// 图片ID
    private String images_url;// 图片ID
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_user_images_clear);
        ViewUtils.inject(this);
        initData();
    }

    @Override
    public void initData() {
        dialog = DialogUtil.getDialog(this);
        mCancel.setOnClickListener(this);
        mClear.setOnClickListener(this);
        mShowImage.setOnClickListener(this);
        user_id = ConstantValues.UID;
        images_id = getIntent().getIntExtra("image_id", 0);
        images_url = getIntent().getStringExtra("images_url");
    }

    @Override
    public void getData() {

    }

    public void clearImages() {
        dialog.show();
        try {
            ServiceFactory.getCommunityEngineInstatice().deleteImages(user_id, images_id, new IOAuthCallBack() {

                @Override
                public void getIOAuthCallBack(String result) {
                    if (result.equals(ConstantValues.FAILURE)) {
                        ToastUtils.showInfo(UserImagesClearActivity.this, ConstantValues.NONETWORK);
                    } else {
                        InfoEntity bean = GsonTools.changeGsonToBean(result, InfoEntity.class);
                        if ("1".equals(bean.getStatus())) {
                            ToastUtils.showInfo(UserImagesClearActivity.this, bean.getInfo());
                            setResult(100);
                            finish();
                        } else {
                            ToastUtils.showInfo(UserImagesClearActivity.this, bean.getInfo());
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return true;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.images_clear:
                clearImages();
                break;
            case R.id.show_image:
                if (!CheckUtils.checkEmpty(images_url)) {
                    ArrayList<String> urlList = new ArrayList<String>(Arrays.asList(images_url));
                    Intent intent = new Intent(this, CommunityImgActivit.class);
                    intent.putStringArrayListExtra("url", urlList);
                    intent.putExtra("setCurrentItem", "0");
                    startActivity(intent);
                    finish();
                }
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
