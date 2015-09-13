package me.wangolf.usercenter;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.meigao.mgolf.R;

import java.io.File;
import java.util.HashMap;

import me.wangolf.ConstantValues;
import me.wangolf.base.BaseActivity;
import me.wangolf.bean.InfoEntity;
import me.wangolf.bean.usercenter.OrBallListEntity;
import me.wangolf.factory.ServiceFactory;
import me.wangolf.service.IOAuthCallBack;
import me.wangolf.utils.CheckUtils;
import me.wangolf.utils.FileUtils;
import me.wangolf.utils.GsonTools;
import me.wangolf.utils.TelUtils;
import me.wangolf.utils.ToastUtils;
import me.wangolf.utils.Xutils;

public class OrderInfoActivity extends BaseActivity implements OnClickListener
{
	@ViewInject(R.id.common_back)
	private Button						common_back;			// 后退
	@ViewInject(R.id.common_title)
	private TextView					common_title;			// 标题
	@ViewInject(R.id.common_bt)
	private TextView					common_bt;				// 电话
	@ViewInject(R.id.single_time)
	private TextView					single_time;			// 下单时间
	@ViewInject(R.id.order_amount)
	private TextView					order_amount;			// 订单总金额
	@ViewInject(R.id.order_status)
	private TextView					order_status;			// 订单状态
	@ViewInject(R.id.icon)
	private ImageView					icon;					// 图标
	@ViewInject(R.id.title)
	private TextView					title;
	@ViewInject(R.id.info)
	private TextView					info;
	@ViewInject(R.id.attime)
	private TextView					attime;					// 开球时间
	@ViewInject(R.id.consumer_num)
	private TextView					consumer_num;			// 数量
	@ViewInject(R.id.consumer_name)
	private TextView					consumer_name;			// 联系人
	@ViewInject(R.id.phone_num)
	private TextView					phone_num;				// 电话号码
	@ViewInject(R.id.range_code)
	private TextView					range_code;				// 验证码
	@ViewInject(R.id.book)
	private TextView					book;					// 备注
	@ViewInject(R.id.expire_time)
	private TextView					expire_time;			// 过期时间
	@ViewInject(R.id.bt_enter)
	private Button						bt_enter;				// 付款
	@ViewInject(R.id.bt_cancel)
	private Button						bt_cancel;
	@ViewInject(R.id.iv_qrcode)
	private ImageView					mQrcode;
	@ViewInject(R.id.popdown)
	private View						mPop;
	@ViewInject(R.id.iv_pop_qrcode)
	private ImageView					mPowQrcode;
	@ViewInject(R.id.tv_info)
	private TextView					mInfo;
	private OrBallListEntity.DataEntity	bean;
	private String						type;
	private String						sn;						// 订单号
	private static final int			WHITE	= 0xFFFFFFFF;
	private static final int			BLACK	= 0xFF000000;
	private PopupWindow					pwMyPopWindow;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.ac_order_info);

		View layout = View.inflate(this, R.layout.item_order_popwindw, null);

		ViewUtils.inject(this);

		ViewUtils.inject(this, layout);

		initData();

		pwMyPopWindow = new PopupWindow(layout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

	}

	@Override
	public void initData()
	{
		type = getIntent().getStringExtra("type");

		bean = (OrBallListEntity.DataEntity) getIntent()
				.getSerializableExtra("bean");

		common_back.setVisibility(View.VISIBLE);

		common_bt.setVisibility(View.VISIBLE);

		common_title.setText(ConstantValues.ORDERINFO);

		common_bt.setText("电话");

		common_back.setOnClickListener(this);

		common_bt.setOnClickListener(this);

		bt_enter.setOnClickListener(this);

		bt_cancel.setOnClickListener(this);

		mQrcode.setOnClickListener(this);

		mPowQrcode.setOnClickListener(this);

		sn = bean.getOut_trade_no();

		ProcessData();

	}

	@Override
	public void getData()
	{

	}

	// 处理传过来的数据
	@SuppressLint("NewApi")
	public void ProcessData()
	{
		if (bean != null)
		{
			order_amount.setText("￥" + bean.getOrder_amount());

			String num = bean.getStatus();

			String status = "";

			if ("1".equals(num))
			{
				status = "待付款";

				bt_enter.setVisibility(View.VISIBLE);

				bt_cancel.setVisibility(View.VISIBLE);

			}
			else if ("7".equals(num))
			{
				status = "已取消";
				// holder.status.setBackgroundResource(R.drawable.tv_all_round_gray);
			}
			else if ("3".equals(num))
			{
				status = "已付款";

				if (!CheckUtils.checkEmpty(bean.getQrcode_url()))
					createCode(bean.getQrcode_url());

			}
			else if ("8".equals(num))
			{
				status = "已撤消";

			}
			else if ("10".equals(num))
			{
				status = "已付款";

				if (!CheckUtils.checkEmpty(bean.getQrcode_url()))
					createCode(bean.getQrcode_url());

			}
			else
			{
				status = "确认中";
				bt_cancel.setVisibility(View.VISIBLE);
			}

			order_status.setText(status);

			single_time.setText(bean.getSingle_time());

			Xutils.getBitmap(getApplicationContext(), icon, bean.getIcon());

			title.setText(bean.getCommodity_name());

			info.setText(bean.getDescription());

			// if ("1".equals(type))
			// {
			// attime.setText("开球时间：" + bean.getArrival_time());
			// }
			consumer_num.setText("数量：" + bean.getConsumer_num());
			phone_num.setText("电话：" + bean.getMobile());
			consumer_name.setText("联系人：" + bean.getConsumer_name());
			if (!"2".equals(type))
				range_code.setText("验证码：" + (CheckUtils.checkEmpty(bean
						.getRange_code()) ? "无" : bean.getRange_code()));
			// book.setText(bean.getBook());
			// int date = Integer.parseInt(bean.getExpire_time());
			// int day = date / 1440;
			// int hour = (date - (day * 1440)) / 60;
			// int minute = date - ((day * 1440) + (hour * 60));
			// expire_time.setText((CheckUtils.checkEmpty(bean.getExpire_time())
			// | "0".equals(bean.getExpire_time())) ? "" : day + "天" + hour +
			// "时"
			// + minute + "分后，" + "此订单过期");

		}

	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.common_back:
				finish();
				break;
			case R.id.common_bt:
				TelUtils.tel(this, ConstantValues.TEL_PHONE);
				break;
			case R.id.bt_enter:
				// 订单支付
				if (!CheckUtils.checkEmpty(sn))
				{
					Intent pay_order = new Intent(this, OrderPayActivity.class);
					pay_order.putExtra("sn", sn);
					pay_order.putExtra("flag", "order_center");
					pay_order.putExtra("type", "4");
					pay_order.putExtra("order_amount", bean.getOrder_amount());
					this.startActivity(pay_order);
					ConstantValues.ISTOPAY = true;// 用于刷新订单中心
					finish();
				}
				break;
			case R.id.bt_cancel:
				// 取消订单
				toOrderCancel(sn);
				break;
			case R.id.iv_qrcode:
				if (pwMyPopWindow.isShowing())
				{
					pwMyPopWindow.dismiss();// 关闭
				}
				else
				{
					pwMyPopWindow.showAsDropDown(mPop);// 显示

				}
				break;
			case R.id.iv_pop_qrcode:
				if (pwMyPopWindow.isShowing())
				{
					pwMyPopWindow.dismiss();// 关闭
				}
			default:
				break;
		}
	}

	// 取消订单
	public void toOrderCancel(String sn)
	{
		try
		{
			ServiceFactory.getIUserEngineInstatice()
					.toOrderCancel(sn, new IOAuthCallBack()
					{
						@Override
						public void getIOAuthCallBack(String result)
						{

							if (result.equals(ConstantValues.FAILURE))
							{

								ToastUtils.showInfo(OrderInfoActivity.this, ConstantValues.NONETWORK);
							}
							else
							{
								InfoEntity bean = GsonTools
										.changeGsonToBean(result, InfoEntity.class);
								if ("1".equals(bean.getStatus()))
								{
									ToastUtils
											.showInfo(getApplicationContext(), bean
													.getInfo());
									// getData();
									bt_enter.setVisibility(View.GONE);
									bt_cancel.setVisibility(View.GONE);
									ConstantValues.ISTOPAY = true;// 用于刷新订单中心
								}
								else
								{
									ToastUtils
											.showInfo(getApplicationContext(), bean
													.getInfo());

								}
							}
						}
					});
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 二维码的生成
	 */
	public void createCode(String url)
	{
		mInfo.setVisibility(View.VISIBLE);
		int width = 500;
		int height = 500;
		// 二维码的图片格式
		String format = "png";
		/**
		 * 设置二维码的参数
		 */
		HashMap hints = new HashMap();
		// 内容所使用编码
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
		try
		{
			if (FileUtils.isFile(sn + ".png"))
			{
				Xutils.getBitmap(this, mQrcode, FileUtils.getPath() + sn + ".png");
				Xutils.getBitmap(this, mPowQrcode, FileUtils.getPath() + sn + ".png");
			}
			else
			{
				BitMatrix bitMatrix = new MultiFormatWriter()
						.encode(url, BarcodeFormat.QR_CODE, width, height, hints);
				// 生成二维码
				// Resources res = getResources();
				// Bitmap bmp = BitmapFactory.decodeResource(res,
				// R.drawable.app_logo);
				FileUtils.saveBitToPNG(toBitmap(bitMatrix), sn);
				if (FileUtils.isFile(sn + ".png"))
				{
					Xutils.getBitmap(this, mQrcode, FileUtils.getPath() + sn + ".png");
					Xutils.getBitmap(this, mPowQrcode, FileUtils.getPath() + sn + ".png");
				}
			}

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

	private Bitmap toBitmap(BitMatrix bitMatrix)
	{
		int width = bitMatrix.getWidth();
		int height = bitMatrix.getHeight();
		int[] pixels = new int[width * height];
		for (int y = 0; y < height; y++)
		{
			int offset = y * width;
			for (int x = 0; x < width; x++)
			{
				pixels[offset + x] = bitMatrix.get(x, y) ? BLACK : WHITE;
			}
		}

		Bitmap bitmap = Bitmap
				.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}
}
