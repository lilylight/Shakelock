package jp.co.sae.android.banana.common;

import jp.co.sae.android.banana.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

public class InfoDialog {

	private Context mContext;
	/** 開発者名 */
	private static final String COMPANY_NAME = "Sunrise Advanced Engineering co.,ltd";

	public InfoDialog(Context c) {
		mContext = c;
	}

	public void show(String title, String[] messages) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
		StringBuffer str = new StringBuffer();
		for (int i = 0; i < messages.length; i++)
			str.append(messages[i] + "\n");

		alertDialogBuilder.setTitle(title);
		alertDialogBuilder.setIcon(R.drawable.icon);
		alertDialogBuilder.setMessage(str.toString());
		alertDialogBuilder.setPositiveButton(mContext.getString(R.string.OK),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
		alertDialogBuilder.setNeutralButton(mContext.getString(R.string.Market),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent("android.intent.action.VIEW",
								Uri.parse("market://search?q=pub:\"" + COMPANY_NAME + "\""));
						mContext.startActivity(intent);
					}
				});
		alertDialogBuilder.setOnCancelListener(
				new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
					}
				});
		alertDialogBuilder.create().show();
	}
	
	public void show(int title, int messages) {
		show(mContext.getString(title), 
			 mContext.getResources().getStringArray(messages));
	}
	
	public void show(String title, int messages) {
		show(title, 
			 mContext.getResources().getStringArray(messages));
	}
	
	public void show(int title, String[] messages) {
		show(mContext.getString(title), 
			 messages);
	}
}
