package me.wangolf.usercenter;

import java.io.File;
import java.util.ArrayList;

import me.wangolf.community.AlbumActivity;
import me.wangolf.utils.CommonDefine;
import me.wangolf.utils.FileUtils;
import me.wangolf.utils.ImageUtils;

import com.meigao.mgolf.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class SelectPhotoImages extends Activity implements OnClickListener
{

	private Button				btn_take_photo, btn_pick_photo, btn_cancel;
	private LinearLayout		layout;
	private Uri					uri;
	private ArrayList<String>	dataList;
	private int					flag;
	private int					imagesize;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alert_dialog);
		btn_take_photo = (Button) this.findViewById(R.id.btn_take_photo);
		btn_pick_photo = (Button) this.findViewById(R.id.btn_pick_photo);
		btn_cancel = (Button) this.findViewById(R.id.btn_cancel);
		layout = (LinearLayout) findViewById(R.id.pop_layout);
		flag = getIntent().getIntExtra("flag", -1);
		imagesize = getIntent().getIntExtra("imagesize", -1);
		dataList = new ArrayList<String>();
		layout.setOnClickListener(new OnClickListener()
		{

			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "点击空白处退出", Toast.LENGTH_SHORT)
						.show();
			}
		});

		btn_cancel.setOnClickListener(this);
		btn_pick_photo.setOnClickListener(this);
		btn_take_photo.setOnClickListener(this);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		finish();
		return true;
	}

	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.btn_take_photo:
				Intent cameraIntent = new Intent();
				cameraIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
				cameraIntent.addCategory(Intent.CATEGORY_DEFAULT);
				// 根据文件地址创建文件
				File file = new File(CommonDefine.FILE_PATH);
				if (file.exists())
				{
					file.delete();
				}
				uri = Uri.fromFile(file);
				// 设置系统相机拍摄照片完成后图片文件的存放地址
				cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

				// 开启系统拍照的Activity
				startActivityForResult(cameraIntent, CommonDefine.TAKE_PICTURE_FROM_CAMERA);
				break;
			case R.id.btn_pick_photo:
				Intent intent = new Intent(this, AlbumActivity.class);
				Bundle bundle = new Bundle();
				bundle.putStringArrayList("dataList", getIntent()
						.getStringArrayListExtra("dataList"));
				bundle.putString("editContent", "");
				bundle.putInt("flag", flag);
				bundle.putInt("imagesize", imagesize);
				intent.putExtras(bundle);
				startActivityForResult(intent, CommonDefine.TAKE_PICTURE_FROM_GALLERY);
				// startActivity(intent);
				break;
			case R.id.btn_cancel:
				finish();
				break;
			default:
				break;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{

		if (data == null & resultCode == 200) { return; }
		switch (resultCode)
		{
		// case CommonDefine.TAKE_PICTURE_FROM_CAMERA:
			case -1:

				String sdStatus = Environment.getExternalStorageState();
				
				if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { return; }

				Bitmap bitmap = ImageUtils.getUriBitmap(this, uri, 400, 400);
				
				String cameraImagePath = FileUtils.saveBitToSD(bitmap, System
						.currentTimeMillis() + "");

				data = new Intent();
				
				data.putExtra("cameraImagePath", cameraImagePath);
				
				data.putExtra("tpye", "camera");
				
				setResult(data);
				
				break;
				
			case CommonDefine.TAKE_PICTURE_FROM_GALLERY:
				
				data.putExtra("type", "photo");
				
				setResult(data);
				
				break;
				
			default:
				break;
		}
		
		finish();
		
		super.onActivityResult(requestCode, resultCode, data);
	}

	// 设置结果
	public void setResult(Intent data)
	{
		switch (flag)
		{
			case 0:
				setResult(200, data);
				break;
			case 1:
				setResult(101, data);
				break;
			default:
				break;
		}
	}
}
