package me.wangolf.utils;

import com.meigao.mgolf.R;
import me.wangolf.event.EventDetailActivity;
import me.wangolf.event.EventEnrolingActivity;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class DialogUtils {
	private static Context con;
	private static Dialog dialog;
	private static Activity event;
	private static String flag;

	public DialogUtils(Context context) {
		super();
	}

	public static void tel(Context context, String title, String flags) {
		con = context;
		event = (Activity) context;
		flag = flags;
		dialog = new Dialog(context, R.style.MyDialogTheme);
		View view = View.inflate(context, R.layout.dialog_event, null);
		dialog.setContentView(view);
		TextView tvdTitle = (TextView) view.findViewById(R.id.dialog_title);
		tvdTitle.setText(title);
		Button btcancle = (Button) view.findViewById(R.id.bt_cancle);
		btcancle.setText("取消");
		Button btok = (Button) view.findViewById(R.id.bt_ok);
		btok.setText("确定");
		btcancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.cancel();
			}
		});
		btok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.cancel();
				if ("enroling".equals(flag)) {
					((EventEnrolingActivity) event).toEventJoin();
				} else {
					((EventDetailActivity) event).toEventJoin();
				}
			}
		});
		dialog.show();
	}

}
