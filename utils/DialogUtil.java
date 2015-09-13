package me.wangolf.utils;

import com.meigao.mgolf.R;
import android.app.Dialog;
import android.content.Context;

public class DialogUtil extends Dialog {
	private static Dialog dialog;
	private static Context context;
	public DialogUtil(Context context) {
		super(context);
		this.context=context;
	}
	@Override
	public void cancel() {
		super.cancel();
	}
	@Override
	public void show() {
		super.show();
	}

	
	public static Dialog getDialog(Context context){
		dialog = new Dialog(context,R.style.MyDialogTheme);
		dialog.setContentView(R.layout.dialog);
		return dialog;
	}
}
