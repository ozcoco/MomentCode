package me.wangolf.usercenter;

import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.alipay.android.app.sdk.AliPay;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.unionpay.UPPayAssistEx;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.ServiceState;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.meigao.mgolf.R;
import com.meigao.mgolf.wxapi.Constants;
import com.meigao.mgolf.wxapi.MyWeiPayUtils;
import com.meigao.mgolf.wxapi.ServerWeiRsEntity;

import me.wangolf.ConstantValues;
import me.wangolf.GlobalConsts;
import me.wangolf.alipay.Keys;
import me.wangolf.alipay.Result;
import me.wangolf.alipay.Rsa;
import me.wangolf.base.BaseActivity;
import me.wangolf.bean.usercenter.OrderpayBean;
import me.wangolf.bean.usercenter.UserInfoEntity;
import me.wangolf.factory.ServiceFactory;
import me.wangolf.service.IOAuthCallBack;
import me.wangolf.utils.CheckUtils;
import me.wangolf.utils.DialogUtil;
import me.wangolf.utils.GsonTools;
import me.wangolf.utils.ToastUtils;

/**
 * @ClassName: OrderPayActivity
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author oz
 * @date 2015-8-12 上午11:16:21
 * 
 */

public class OrderPayActivity extends BaseActivity implements OnClickListener
{

	@ViewInject(R.id.common_back)
	private Button							common_back;		// 后退
	@ViewInject(R.id.common_title)
	private TextView						common_title;		// 标题
	@ViewInject(R.id.common_bt)
	private TextView						common_bt;			// 地图
	@ViewInject(R.id.online_pay)
	private RelativeLayout					online_pay;
	@ViewInject(R.id.pamount_pay)
	private RelativeLayout					pamount_pay;
	@ViewInject(R.id.revouchers_pay)
	private RelativeLayout					revouchers_pay;
	@ViewInject(R.id.needpay_pay)
	private RelativeLayout					needpay_pay;

	@ViewInject(R.id.price)
	private TextView						tv_price;			// 总金额
	@ViewInject(R.id.pamount)
	private TextView						pamount;			// 用户余款
	@ViewInject(R.id.tv_kou_yue)
	private TextView						tv_kou_yue;		// 使用余款
	@ViewInject(R.id.btPay)
	private Button							btPay;				// 立即支付
	@ViewInject(R.id.checkbox)
	private CheckBox						checkbox;			// 使用余款支付方式
	@ViewInject(R.id.rgpay)
	private RadioGroup						rgpay;				// 支付分组
	@ViewInject(R.id.rdAlipay)
	private RadioButton						rdAlipay;			// 支付宝支付
	@ViewInject(R.id.rdUnionPay)
	private RadioButton						rdUnionPay;		// 网银支付
	@ViewInject(R.id.rdWeipay)
	private RadioButton						rdWeipay;			// 微信支付
	@ViewInject(R.id.snprice)
	private TextView						snprice;			// 显示的代金券
	@ViewInject(R.id.rd)
	private RadioButton						rd;				// 备用
	@ViewInject(R.id.needpay)
	private TextView						needpay;			// 还需支付
	private OrderpayBean					order_bean;
	private String							sn;				// 返回的订单ID
	private double							self_amount;		// 自己的余额
	private String							vouchers_sn;		// 代金券代码
	private double							vouchers_amount;	// 代金券价值
	private double							order_amount;		// 订单总价
	private String							user_id;			// 用户ID
	private String							payment		= "1";	// 支付方式(默认网银)
	private boolean							isFlag;
	protected String						TAG;
	private boolean							use_amount	= true;
	private static final int				RQF_PAY		= 1;
	private static final int				RQF_LOGIN	= 2;
	private int								pay_type	= 3;	// 支付方式标记(默认网银/
																// 1、网银2、支付宝3、微信)
	private IWXAPI							api;
	private ServerWeiRsEntity	serverWeiRsEntity;
	public static Product[]					sProducts;
	private String							type;				// 类型0练习场1球场2套餐3商品4活动,5开通会员,6充值
	private String							flag;				// 来源（购买页order,或个人中心订单页:order_center）
	private double							pay_amount;		// 网银支付的钱
	private Dialog							dialog;
	private String	out_trade_name;
	private String	out_trade_no;
	private String	total_fee;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.ac_order_ball_add);

		ViewUtils.inject(this);

		initData();
	}

	/*
	 * (非 Javadoc)
	 * <p>Title: initData</p>
	 * <p>Description: </p>
	 * @see me.wangolf.base.BaseActivity#initData()
	 */
	@Override
	public void initData()
	{
		dialog = DialogUtil.getDialog(this);

		api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);

		common_back.setVisibility(View.VISIBLE);

		common_title.setText(ConstantValues.ONLINEPLAY);

		common_back.setOnClickListener(this);

		btPay.setOnClickListener(this);

		flag = getIntent().getStringExtra("flag");

		user_id = ConstantValues.UID;

		if ("order_center".equals(flag))
		{
			// 回自订单列表 已经有订单号
			getData();

			sn = getIntent().getStringExtra("sn");

			type = getIntent().getStringExtra("type");

			order_amount = (int)Double.parseDouble(getIntent()
					.getStringExtra("order_amount"));

		}
		else
		{
			// ToastUtils.showInfo(getBaseContext(), "来自购买或充值!!!!!!!");
			// 来自购买或充值
			order_bean = (OrderpayBean) getIntent()
					.getSerializableExtra("order_bean");
			// order_bean.setOrder_amount(0.1);
			
			order_amount = order_bean.getOrder_amount();
			
//			order_amount = order_bean.getOrder_amount() - Double.valueOf(""
//					.equals(order_bean.getReturn_amount()) ? "0" : order_bean
//					.getReturn_amount());

			order_bean.setUser_id(user_id);
			
			type = order_bean.getType();

			out_trade_name = order_bean.getOut_trade_name();
					
			if ("6".equals(type))
			{
				// 6为充值 隐余额 不用获取
				pamount_pay.setVisibility(View.GONE);

				revouchers_pay.setVisibility(View.GONE);

				needpay_pay.setVisibility(View.GONE);

				Prepay();// 直接生成订单

			}
			else
			{
				getData();

				sn = getIntent().getStringExtra("sn");

				if (CheckUtils.checkEmpty(sn))
				{

					Prepay();// 直接生成订单
				}
			}
		}

		if (order_amount > 0.0)
		{
			// 订单总额为0则无需选择支付方式
			pamount_pay.setOnClickListener(this);

			rdAlipay.setOnClickListener(this);

			rdUnionPay.setOnClickListener(this);

			rdWeipay.setOnClickListener(this);

			revouchers_pay.setOnClickListener(this);

		}
		else
		{
			rdUnionPay.setCompoundDrawables(null, null, null, null);
		}

		tv_price.setText("￥" + (int) order_amount);

		needpay.setText("￥" + (int) order_amount);
		// testBean();
	}

	@Override
	public void getData()
	{
		// 获取用户余款额据
		try
		{
			ServiceFactory.getIUserEngineInstatice()
					.getUserInfo(user_id, new IOAuthCallBack()
					{

						@Override
						public void getIOAuthCallBack(String result)
						{
							if (result.equals(ConstantValues.FAILURE))
							{
								ToastUtils.showInfo(OrderPayActivity.this, ConstantValues.NONETWORK);
							}
							else
							{
								UserInfoEntity bean = GsonTools
										.changeGsonToBean(result, UserInfoEntity.class);

								if ("1".equals(bean.getStatus()))
								{

									UserInfoEntity.DataEntity data = bean
											.getData().get(0);

									self_amount = Double.valueOf(data
											.getAccount());
									// self_amount=10;
									pamount.setText("￥" + (int) self_amount);

									if (self_amount > order_amount)
									{
										tv_kou_yue
												.setText("使用￥" + (int) order_amount);
									}
									else
									{
										tv_kou_yue
												.setText("使用￥" + (int) self_amount);
									}

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

	// 生成订单
	public void Prepay()
	{
		dialog.show();

		// ToastUtils.showInfo(getBaseContext(), order_bean.toString());
		//
		// Log.w("order_bean", order_bean.toString().replace(",", "&"));

		try
		{
			ServiceFactory.getIUserEngineInstatice()
					.toPrepay(order_bean, new IOAuthCallBack()
					{
						@Override
						public void getIOAuthCallBack(String result)
						{

							JSONObject jsonObj;

							Log.i("生成订单", result);
							
							try
							{

								jsonObj = new JSONObject(result);

								String status = jsonObj.getString("status");

								if ("1".equals(status))
								{
									JSONArray jsonArray = jsonObj.getJSONArray("data");

									if (jsonArray.length() > 0)
									{
										JSONObject obj = jsonArray
												.getJSONObject(0);

										sn = obj.getString("out_trade_no");
									}

									if (sn != null)
									{

									}
									else
									{
										ToastUtils
												.showInfo(OrderPayActivity.this, "生成订单失败，请重试！");
									}

								}
								else
								{
									
									ToastUtils.showInfo(getBaseContext(), jsonObj.getString("info"));
									
								}
								
								

							}
							catch(JSONException e)
							{
								e.printStackTrace();
							}

							dialog.cancel();
						}
					});
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	// 修改订单添加代金券、余额接口(新版),提交订单后返回值中拿到订单ID 然后判断用户是否有余款及代金券（余额支付）
	public void payExtra(final double self_amount)
	{
		try
		{
			ServiceFactory
					.getIUserEngineInstatice()
					.topayExtra(sn, self_amount, order_amount, new IOAuthCallBack()
					{

						@Override
						public void getIOAuthCallBack(String result)
						{

							JSONObject jsonObj;
							try
							{
								jsonObj = new JSONObject(result);

								String status = jsonObj.getString("status");

								String info = jsonObj.getString("info");

								if ("1".equals(status))
								{
									if (pay_type == 4)
									{
										// 全余款支付
										Intent intent = new Intent(getApplicationContext(), OrderScuessActivity.class);

										intent.putExtra("sn", sn);

										intent.putExtra("user_id", user_id);

										intent.putExtra("payment", payment);

										intent.putExtra("message", "您的订单号(" + sn + ")已支付成功，请到个人中心我的订单查看！");

										intent.putExtra("title", "支付成功");

										intent.putExtra("flag", flag);

										intent.putExtra("type", type);// 用于查看订单列表
										// intent.putExtra("type",
										// (Integer.parseInt(order_bean.getType()))
										// + "");// 用于查看订单列表
										startActivity(intent);

										finish();
									}
								}
							}
							catch(JSONException e)
							{
								e.printStackTrace();
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
	 * 
	 * 银联支付开始==================
	 * 
	 * @param sn
	 *            订单号
	 */
	protected void uinpay(String sn)
	{
		String url = GlobalConsts.YinLianURL;
		String money = pay_amount + "";// 需要支付的金额(总金额-代金券-余额)
		RequestParams params = new RequestParams();
		params.addBodyParameter("orderid", sn);
		params.addBodyParameter("money", money);
		HttpUtils http = new HttpUtils();
		http.send(HttpMethod.POST, url, params, new RequestCallBack<String>()
		{

			@Override
			public void onStart()
			{
			}

			@Override
			public void onLoading(long total, long current, boolean isUploading)
			{
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo)
			{
				if (!"".equals(responseInfo.result))
				{
					JSONObject jsonObj;
					try
					{
						jsonObj = new JSONObject(responseInfo.result);
						String status = jsonObj.getString("status");
						String info = jsonObj.getString("info");
						JSONArray jsonArray = jsonObj.getJSONArray("data");
						String tn = null;
						if (jsonArray.length() > 0)
						{
							JSONObject obj = jsonArray.getJSONObject(0);
							tn = obj.getString("tn");
							if ("1".equals(status))
							{
								if (!CheckUtils.checkEmpty(tn))
								{
									requestPay(tn);
								}
							}
							else
							{

								ToastUtils
										.showInfo(OrderPayActivity.this, info);
							}
						}
					}
					catch(JSONException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}

			@Override
			public void onFailure(HttpException error, String msg)
			{
			}
		});

	}

	protected void requestPay(String tn)
	{
		/*************************************************
		 * 
		 * 步骤2：通过银联工具类启动支付插件
		 * 
		 ************************************************/
		// mMode参数解释：
		// 0 - 启动银联正式环境
		// 1 - 连接银联测试环境
		int ret = UPPayAssistEx
				.startPay(OrderPayActivity.this, null, null, tn, GlobalConsts.mMode);
		if (ret == GlobalConsts.PLUGIN_NEED_UPGRADE || ret == GlobalConsts.PLUGIN_NOT_INSTALLED)
		{
			// 需要重新安装控件
			// LogUtils.i(LOG_TAG, " plugin not found or need upgrade!!!");

			AlertDialog.Builder builder = new AlertDialog.Builder(OrderPayActivity.this);
			builder.setTitle("提示");
			builder.setMessage("完成购买需要安装银联支付控件，是否安装？");

			builder.setNegativeButton("确定", new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					// dialog.dismiss();
					UPPayAssistEx.installUPPayPlugin(OrderPayActivity.this);
				}
			});

			builder.setPositiveButton("取消", new DialogInterface.OnClickListener()
			{

				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					dialog.dismiss();
				}
			});
			builder.create().show();

		}
		// LogUtils.i(LOG_TAG, "" + ret);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{

		if (data == null) { return; }
		/*************************************************
		 * 
		 * 步骤3：处理银联手机支付控件返回的支付结果
		 * 
		 ************************************************/

		String msg = "";
		/*
		 * 支付控件返回字符串:success、fail、cancel 分别代表支付成功，支付失败，支付取消
		 */
		String str = data.getExtras().getString("pay_result");
		if (!CheckUtils.checkEmpty(str))
		{
			if (str.equalsIgnoreCase("success"))
			{
				msg = "支付成功";
				// TODO 支付成功
				Intent intent = new Intent(OrderPayActivity.this, OrderScuessActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra("title", msg);
				intent.putExtra("sn", sn);
				intent.putExtra("payment", payment + "");
				intent.putExtra("flag", flag);
				intent.putExtra("type", type);// 用于查看订单列表
				intent.putExtra("message", "您的订单号(" + sn + ")已支付成功，请到个人中心我的订单查看！");
				startActivity(intent);
				finish();
			}

			else if (str.equalsIgnoreCase("fail"))
			{
				msg = "支付失败";
				Intent intent = new Intent(OrderPayActivity.this, OrderScuessActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra("title", "支付失败");
				intent.putExtra("sn", sn);
				intent.putExtra("flag", flag);
				intent.putExtra("type", type);// 用于查看订单列表
				intent.putExtra("message", "您的订单号(" + sn + ")已支付失败。");
				startActivity(intent);
				finish();
			}
			else if (str.equalsIgnoreCase("cancel"))
			{
				msg = "支付已取消";
				Intent intent = new Intent(OrderPayActivity.this, OrderScuessActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra("title", "支付已取消");
				intent.putExtra("sn", sn);
				intent.putExtra("type", type);// 用于查看订单列表
				intent.putExtra("flag", flag);
				intent.putExtra("message", "您的订单号(" + sn + ")已取消支付。");
				startActivity(intent);
				finish();
			}
		}
		// 处理代金券回来数据
		if (resultCode == ConstantValues.VOUCHERSCODE)
		{
			// 获取代金券号码
			vouchers_sn = data.getStringExtra("vouchers_sn");

			if (!CheckUtils.checkEmpty(vouchers_sn))
			{
				// System.out.println("有代金");
				vouchers_amount = Double.parseDouble(data
						.getStringExtra("vouchers_amount"));
				snprice.setText("￥" + (int) vouchers_amount);

				if (checkbox.isChecked())
				{
					if (self_amount > order_amount)
					{
						needpay.setText("￥" + ("0"));
						tv_kou_yue
								.setText("使用￥" + (int) (order_amount - vouchers_amount));
					}
					else
					{
						tv_kou_yue.setText("使用￥" + (int) (self_amount));
						needpay.setText("￥" + (int) ((order_amount - self_amount - vouchers_amount) > 0.0 ? order_amount - self_amount - vouchers_amount : 0));
					}
				}
				else
				{
					needpay.setText("￥" + (int) ((order_amount - vouchers_amount) > 0 ? (order_amount - vouchers_amount) : 0));
					if (order_amount - vouchers_amount < 0)
					{
						pamount_pay.setClickable(false);
						rdAlipay.setClickable(false);
						rdUnionPay.setClickable(false);
						rdWeipay.setClickable(false);
						setDrawable(rd);// 代金大于订单 网银支付不可点
					}
				}
			}
			else
			{
				// System.out.println("无代金");
				vouchers_amount = 0.0;

				pamount_pay.setClickable(true);

				rdAlipay.setClickable(true);

				rdUnionPay.setClickable(true);

				rdWeipay.setClickable(true);

				if (checkbox.isChecked())
				{
					if (self_amount > order_amount)
					{
						needpay.setText("￥" + ("0"));
					}
					else
					{
						needpay.setText("￥" + (int) (order_amount - self_amount - vouchers_amount));
					}
				}
				else
				{
					needpay.setText("￥" + (int) (order_amount - vouchers_amount));
				}
				snprice.setText("");
			}
		}
	}

	int startpay(Activity act, String tn, int serverIdentifier)
	{
		return 0;
	}

	// TODO 支付宝start============================t============================
	/**
	 * 支付宝支付
	 * 
	 * @param
	 */
	protected void alipay(final String sn)
	{
		final Handler mHandler = new Handler()
		{
			public void handleMessage(android.os.Message msg)
			{
				Result result = new Result((String) msg.obj);

				switch (msg.what)
				{
					case RQF_PAY:
					case RQF_LOGIN:
					{

						// 处理返回的字符窜取得状态码
						String[] reStrings = result.mResult.toString()
								.split(";");
						String stastr = reStrings[0].replace("{", "")
								.replace("}", "");
						stastr = stastr.replace("resultStatus=", "");
						if (stastr.equals("9000"))
						{
							// TODO 支付成功
							Intent intent = new Intent(OrderPayActivity.this, OrderScuessActivity.class);
							intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							intent.putExtra("title", "支付成功");
							intent.putExtra("sn", sn);
							intent.putExtra("payment", payment + "");
							intent.putExtra("flag", flag);
							intent.putExtra("type", type);// 用于查看订单列表
							intent.putExtra("message", "您的订单号(" + sn + ")已支付成功，请到个人中心我的订单查看！");
							startActivity(intent);
							finish();
							return;
						}
						else if (stastr.equals("4000") || stastr.equals("4003") || stastr
								.equals("4006") || stastr.equals("6000"))
						{
							Intent intent = new Intent(OrderPayActivity.this, OrderScuessActivity.class);
							intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							intent.putExtra("title", "支付失败");
							intent.putExtra("sn", sn);
							intent.putExtra("flag", flag);
							intent.putExtra("type", type);// 用于查看订单列表
							intent.putExtra("message", "您的订单号(" + sn + ")已支付失败。");
							startActivity(intent);
							finish();

						}
						else
						{
							Intent intent = new Intent(OrderPayActivity.this, OrderScuessActivity.class);
							intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							intent.putExtra("title", "支付已取消");
							intent.putExtra("sn", sn);
							intent.putExtra("flag", flag);
							intent.putExtra("type", type);// 用于查看订单列表
							intent.putExtra("message", "您的订单号(" + sn + ")已取消支付。");
							startActivity(intent);
							finish();
						}
					}
						break;
					default:
						break;
				}
			};
		};

		try
		{
			String info = getNewOrderInfo(sn);
			String sign = Rsa.sign(info, Keys.PRIVATE);
			sign = URLEncoder.encode(sign);
			info += "&sign=\"" + sign + "\"&" + getSignType();

			final String orderInfo = info;
			new Thread()
			{
				public void run()
				{
					AliPay alipay = new AliPay(OrderPayActivity.this, mHandler);

					// 设置为沙箱模式，不设置默认为线上环境
					// alipay.setSandBox(true);

					String result = alipay.pay(orderInfo);
					// Log.i(TAG, "result = " + result);
					Message msg = new Message();
					msg.what = RQF_PAY;
					msg.obj = result;
					mHandler.sendMessage(msg);
				}
			}.start();

		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			Toast.makeText(OrderPayActivity.this, R.string.remote_call_failed, Toast.LENGTH_SHORT)
					.show();
		}

	}

	private String getNewOrderInfo(String sn)
	{

		StringBuilder sb = new StringBuilder();
		sb.append("partner=\"");
		sb.append(Keys.DEFAULT_PARTNER);
		sb.append("\"&out_trade_no=\"");
		sb.append(sn);
		sb.append("\"&subject=\"");
		sb.append(getResources().getString(R.string.buypracorder));// 产品名称
		sb.append("\"&body=\"");
		sb.append("您的订单号为" + sn);// 预定说明
		sb.append("\"&total_fee=\"");
		String money = pay_amount + "";//
		// 需要支付的金额(总金额-代金券-余额)
		sb.append(money);// TODO 价格********
		sb.append("\"&notify_url=\"");

		// 网址需要做URL编码
		sb.append(URLEncoder.encode(GlobalConsts.notify_url));
		sb.append("\"&service=\"mobile.securitypay.pay");
		sb.append("\"&_input_charset=\"UTF-8");
		sb.append("\"&return_url=\"");
		sb.append(URLEncoder.encode("http://m.alipay.com"));
		sb.append("\"&payment_type=\"1");
		sb.append("\"&seller_id=\"");
		sb.append(Keys.DEFAULT_SELLER);

		// 如果show_url值为空，可不传
		// sb.append("\"&show_url=\"");
		sb.append("\"&it_b_pay=\"1m");
		sb.append("\"");
		// System.out.println(sb);
		return new String(sb);
	}

	/*
	 * private String getOutTradeNo() { SimpleDateFormat format = new
	 * SimpleDateFormat("MMddHHmmss"); Date date = new Date(); String key =
	 * format.format(date);
	 * java.util.Random r = new java.util.Random(); key += r.nextInt(); key =
	 * key.substring(0, 15); Log.d("pay---------->", "outTradeNo: " + key);
	 * return key; }
	 */

	private String getSignType()
	{
		return "sign_type=\"RSA\"";
	}

	public static class Product
	{
		public String	subject;
		public String	body;
		public String	price;
	}

		
	// TODO 支付宝end=============================================

	/************************************** 微信支付start **************************/
	
	protected void weChatPay(String sn)
	{

		out_trade_no = sn;
		
		total_fee = (int) pay_amount + "";// 需要支付的金额(总金额-代金券-余额)
		
		try
		{
			ServiceFactory.getIUserEngineInstatice().weChatPay(out_trade_name, out_trade_no, total_fee, new IOAuthCallBack()
			{
				
				@Override
				public void getIOAuthCallBack(String result)
				{
				
					if (!"".equals(result))
					{

						Log.i("结果值", result);

						serverWeiRsEntity = GsonTools
								.changeGsonToBean(result, ServerWeiRsEntity.class);
												
						if (serverWeiRsEntity != null)
						{
							weipay();
							// 调用支付控件
						}
						else
						{

							ToastUtils.showInfo(OrderPayActivity.this, "支付失败");
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
	
	
	
	
	// 请求相关的信息
	protected void weipay(String sn)
	{
		dialog.show();
		// System.out.println("******微信支付******");
		// 检测微信是否可用
		boolean isPaySupported = api.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;

		if (isPaySupported == false)
		{
			ToastUtils
					.showInfo(OrderPayActivity.this, "请下载安装最新版本微信，或使用其他方式支付！");
			if (dialog.isShowing())
			{
				dialog.cancel();
			}
			return;
		}

		String url = GlobalConsts.WeiXinURL;
		
		String money = (int) pay_amount + "";// 需要支付的金额(总金额-代金券-余额)
		
		String uid = ConstantValues.UID;
		
		RequestParams params = new RequestParams();
		
		params.addBodyParameter("orderid", sn);
		
		params.addBodyParameter("money", money);
		
		params.addBodyParameter("uid", uid);
		
		HttpUtils http = new HttpUtils();
		
		http.send(HttpMethod.POST, url, params, new RequestCallBack<String>()
		{
			@Override
			public void onStart()
			{
			}

			@Override
			public void onLoading(long total, long current, boolean isUploading)
			{
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo)
			{
				if (!"".equals(responseInfo.result))
				{

					Log.i("结果值", responseInfo.result);

					ServerWeiRsEntity bean = GsonTools
							.changeGsonToBean(responseInfo.result, ServerWeiRsEntity.class);
					
					if (bean != null)
					{

						weipay();// 调用支付控件
					}
					else
					{			

						ToastUtils.showInfo(OrderPayActivity.this, "支付失败");
					}
				}
			}

			@Override
			public void onFailure(HttpException error, String msg)
			{

			}
		});

	}

	@Override
	public void onPause()
	{
		super.onPause();
		// TODO 关闭微信支付打开的dialog
		if (dialog.isShowing())
		{
			dialog.cancel();
		}
	}

	
	
	/**
	 * 调用支付控件
	 */
	
	/***
	 * 
	 * {"status":1,"info":"ok","data":[
	 * {"retcode":1,
	 * "retmsg":"ok",
	 * "appid":"wxabfbba326a013705",
	 * "noncestr":"2e98077c6892111fcb48144c1724de4a",
	 * "package":"Sign=WXPay",
	 * "prepayid":"12010000001509012c7601e20d87f495",
	 * "timestamp":1441074798,
	 * "sign":"01e1c9e028d0e8d8d210f008029dae982b35d546"}]}
	 * 
	 *  PARTNER_ID = "1220538401"
	 * 
	 * */
	
	protected void weipay()
	{
		
		PayReq req = new PayReq();
		
		req.appId = serverWeiRsEntity.getAppid();
		
		req.partnerId = Constants.PARTNER_ID;
		
		req.prepayId = serverWeiRsEntity.getPrepayid();
		
		req.nonceStr = serverWeiRsEntity.getNoncestr();
		
		req.timeStamp = serverWeiRsEntity.getTimestamp();
		
		req.packageValue = "Sign=WXPay";
		
		req.sign = serverWeiRsEntity.getSign();
		
		MyWeiPayUtils.payment = payment + "";
		
		MyWeiPayUtils.sn = sn;
		
		MyWeiPayUtils.type = Integer.parseInt(type);
		
		MyWeiPayUtils.flag = flag;
		
		api.sendReq(req);	
		// finish();
	}

	/************************************** 微信支付end **************************/
	@Override
	public void onClick(View v)
	{

		switch (v.getId())
		{
			case R.id.btPay:

				updateButton((Button) v);// 防止重复提交

				btPay();
				// testBean();
				break;

			case R.id.pamount_pay:
				// System.out.println(self_amount);
				// 点击余款支付
				if (checkbox.isChecked())
				{
					checkbox.setChecked(false);
				}
				else
				{
					checkbox.setChecked(true);
				}

				if (self_amount >= order_amount)
				{
					// 余款够用时全用余款或全用网上支付
					if (!checkbox.isChecked())
					{
						// 网银支付
						tv_kou_yue.setVisibility(View.GONE);
						needpay.setText("￥" + (int) (order_amount - vouchers_amount));
						payment = "1";
						setDrawable(rdWeipay);
						use_amount = true;
					}
					else
					{
						// 使用余额
						tv_kou_yue.setVisibility(View.VISIBLE);
						tv_kou_yue
								.setText("使用￥" + (int) (order_amount - vouchers_amount));
						setDrawable(rd);
						payment = "4";
						use_amount = false;
						needpay.setText(self_amount > order_amount ? "￥0" : "￥" + (int) (order_amount - self_amount - vouchers_amount));
					}
				}
				else if (self_amount > 0)
				{
					// 余款不够用时部份余款或部份网上支付
					if (!checkbox.isChecked())
					{
						tv_kou_yue.setVisibility(View.GONE);
						needpay.setText("￥" + (int) (order_amount - vouchers_amount));
						payment = "1";
						setDrawable(rdWeipay);
						use_amount = true;// 使用余款+网银
					}
					else
					{
						setDrawable(rdWeipay);
						tv_kou_yue.setVisibility(View.VISIBLE);
						payment = "1";
						use_amount = false;// 使用余额
						needpay.setText(self_amount > order_amount ? "￥0" : "￥" + (int) (order_amount - self_amount - vouchers_amount));
					}

				}
				else
				{
					checkbox.setChecked(false);

					ToastUtils.showInfo(OrderPayActivity.this, "你的余款为￥0");

				}

				break;

			case R.id.rdUnionPay:
				// 点击网银支付
				needpay.setText(self_amount > order_amount ? "￥" + (int) (order_amount - vouchers_amount) : "￥" + (int) (order_amount - self_amount - vouchers_amount));
				if (!use_amount)
				{
					checkbox.setChecked(self_amount > order_amount ? false : true);
					tv_kou_yue
							.setVisibility(self_amount > order_amount ? View.GONE : View.VISIBLE);
					use_amount = self_amount > order_amount ? true : false;
				}
				else
				{
					needpay.setText("￥" + (int) (order_amount - vouchers_amount));
					checkbox.setChecked(false);
					tv_kou_yue.setVisibility(View.GONE);
				}

				payment = "1";

				pay_type = 1;

				setDrawable(rdUnionPay);

				break;

			case R.id.rdAlipay:
				// 点击支付宝支付
				needpay.setText(self_amount > order_amount ? "￥" + (int) (order_amount - vouchers_amount) : "￥" + (int) (order_amount - self_amount - vouchers_amount));
				if (!use_amount)
				{
					checkbox.setChecked(self_amount > order_amount ? false : true);
					tv_kou_yue
							.setVisibility(self_amount > order_amount ? View.GONE : View.VISIBLE);
					use_amount = self_amount > order_amount ? true : false;
				}
				else
				{
					needpay.setText("￥" + (int) (order_amount - vouchers_amount));
					checkbox.setChecked(false);
					tv_kou_yue.setVisibility(View.GONE);
				}

				payment = "2";
				pay_type = 2;
				setDrawable(rdAlipay);
				break;
			case R.id.rdWeipay:
				// 点击微信支付
				if (!use_amount)
				{
					needpay.setText(self_amount > order_amount ? "￥" + (int) (order_amount - vouchers_amount) : "￥" + (int) (order_amount - self_amount - vouchers_amount));
					checkbox.setChecked(self_amount > order_amount ? false : true);
					use_amount = self_amount > order_amount ? true : false;
					tv_kou_yue
							.setVisibility(self_amount > order_amount ? View.GONE : View.VISIBLE);
				}
				else
				{
					needpay.setText("￥" + (int) (order_amount - vouchers_amount));
					checkbox.setChecked(false);
					tv_kou_yue.setVisibility(View.GONE);
				}
				payment = "3";
				pay_type = 3;
				setDrawable(rdWeipay);
				break;
			case R.id.revouchers_pay:
				// 选择代金券
				Intent revouchers = new Intent(this, VouchersListActivity.class);
				revouchers.putExtra("flag", "revouchers");
				startActivityForResult(revouchers, ConstantValues.VOUCHERSCODE);
				break;
			case R.id.common_back:
				finish();
				break;
			default:
				break;
		}
	}

	public void btPay()
	{
		// 点击付款
		// weipay("12111111111");// 微信支付

		Log.i("sn", "sn = " + sn);

		if (!CheckUtils.checkEmpty(sn))
		{
			toPay();// 生成订单后去支付
		}
		else
		{
			ToastUtils.showInfo(this, "请稍等...");
		}

	}

	/**
	 * @Title: toPay
	 * @Description: 付款计算
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void toPay()
	{
		// if (vouchers_amount >= order_amount)
		// {
		// payment = "4";
		//
		// pay_type = 4;
		//
		// payExtra(self_amount);
		//
		// return;
		// }

		if (self_amount >= order_amount)
		{
			// 余款大于订单总额
			if (!use_amount)
			{
				// 使用余款或余额+代金券
				payment = "4";

				pay_type = 4;

				payExtra(order_amount);

				// System.out.println("余额+代金");
			}
			else
			{
				// 使用网络支付或网银+代金券
				// self_amount = 0;
				payExtra(0.0);// 先使用代金券（余额传空给服务器）

				pay_amount = order_amount - vouchers_amount; // 减支余款

				switch (pay_type)
				{
					case 1:
						uinpay(sn);// 使用网银
						// System.out.println(pay_amount + "余额大于订+网银支付代金+代金");
						// Log.i("wangolf", pay_amount + "余额大于订+使用网银+代金");
						break;
					case 2:
						alipay(sn);// 支付宝支付
						// System.out.println(pay_amount + "余额大于订+支付宝支付+代金");
						break;
					case 3:					
						weChatPay(sn);// 微信支付						
//						weipay(sn);
						// System.out.println(pay_amount + "余额大于订+微信支付代金+代金");
						// Log.i("wangolf", pay_amount + "余额大于订+微信支付代金+代金");
						break;
					default:
						break;
				}
			}

		}
		else
		{
			// 余款小于订单总额
			if (!use_amount)
			{
				// 使用余款+网银
				payExtra(self_amount);// 余额或余款+代金券

				// pay_amount = order_amount - self_amount - vouchers_amount; //
				// 减去余款

				pay_amount = order_amount - self_amount;

				switch (pay_type)
				{
					case 1:
						uinpay(sn);// 使用网银
						// System.out.println(pay_amount + "网银支付+余额+代金");
						break;
					case 2:
						alipay(sn);// 支付宝支付
						// System.out.println(pay_amount + "支付宝支付+余额+代金");
						break;
					case 3:
						weChatPay(sn);// 微信支付
						
//						weipay(sn);
						// System.out.println(pay_amount + "微信支付+余额+代金");
						break;
					default:
						break;
				}

			}
			else
			{
				// payExtra(0.0);// 先使用代金券（余额传空给服务器）
				pay_amount = order_amount - vouchers_amount; // 减去余额

				switch (pay_type)
				{
					case 1:
						uinpay(sn);// 使用网银
						// System.out.println(pay_amount + "网银支付");
						break;
					case 2:
						alipay(sn);// 支付宝支付
						// System.out.println(pay_amount + "支付宝支付");
						break;
					case 3:
						weChatPay(sn);
//						weipay(sn);// 微信支付
						break;
					default:
						break;
				}
				// 只使用网银

			}
		}
	}

	/**
	 * 防止重复点击
	 * 
	 * @param btn
	 */
	protected void updateButton(final Button btn)
	{

		btn.setBackgroundColor(getResources().getColor(R.color.gray));

		btn.setClickable(false);

		final Handler ha = new Handler()
		{
			@SuppressLint("NewApi")
			@Override
			public void handleMessage(Message msg)
			{
				super.handleMessage(msg);

				if (msg.what == 1)
				{
					// 更新按钮文本
					int time = msg.arg1;
					if (time == 0)
					{
						btn.setClickable(true);

						btn.setBackground(getResources()
								.getDrawable(R.drawable.bt_green_yuan_all_selector));
					}
				}

			}
		};

		new Thread()
		{

			public void run()
			{
				try
				{
					int time = 10;// 10秒
					while (time > 0 && isFlag == false)
					{
						time--;
						Message msg = Message.obtain(ha, 1);
						msg.arg1 = time;
						msg.sendToTarget();
						sleep(1000);
					}
				}
				catch(InterruptedException e)
				{
					e.printStackTrace();
				}
			};
		}.start();
	}

	/**
	 * @Title: setDrawable
	 * @Description: 更改RadioButton图标为自定义（原因：因为需要左边写文字右边是图标）
	 * @param @param v 单选按钮实例
	 * @return void 返回类型
	 * @throws
	 */
	public void setDrawable(RadioButton v)
	{
		rdUnionPay.setCompoundDrawables(null, null, null, null);
		rdAlipay.setCompoundDrawables(null, null, null, null);
		rdWeipay.setCompoundDrawables(null, null, null, null);
		Drawable drawable = getResources().getDrawable(R.drawable.select_icon);
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable
				.getMinimumHeight()); // 设置边界
		v.setCompoundDrawables(null, null, drawable, null);
	}

	public void testBean()
	{
		// System.out.println("type=" + order_bean.getType() + "supplier_id" +
		// order_bean.getSupplier_id() + "user_id=" + order_bean.getUser_id()
		// + "mobile=" + order_bean.getMobiel() + "consumer_name=" +
		// order_bean.getConsumer_name() + "consumer_num="
		// + order_bean.getConsumer_num() + "product_id=" +
		// order_bean.getProduct_id() + "order_amount=" +
		// order_bean.getOrder_amount()
		// + "arrival_time=" + order_bean.getArrival_time() + "court_id=" +
		// order_bean.getCourt_id() + "address=" + order_bean.getAddress()
		// + "zip=" + order_bean.getZip() + "product_attr=" +
		// order_bean.getProduct_attr());
	}
}
