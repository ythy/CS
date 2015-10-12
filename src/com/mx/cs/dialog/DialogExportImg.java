package com.mx.cs.dialog;

import java.io.File;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.mx.cs.R;
import com.mx.cs.util.CommonUtil;
import com.mx.cs.vo.MatrixInfo;

public class DialogExportImg {

	private static String SD_PATH = "Android/data/com.mx.cs/images";
	
	public static void show(final Context context, int nid,
			final Handler handler) {
		final AlertDialog dlg = new AlertDialog.Builder(context).create();
		dlg.show();
		Window window = dlg.getWindow();
		window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		window.setContentView(R.layout.dialog_export_img);

		final EditText x1 = (EditText) window.findViewById(R.id.etX1);
		final EditText y1 = (EditText) window.findViewById(R.id.etY1);
		final EditText width1 = (EditText) window.findViewById(R.id.etWidth1);
		final EditText height1 = (EditText) window.findViewById(R.id.etHeight1);

		MatrixInfo sets = CommonUtil.getMatrixInfo(context, 6);
		x1.setText(String.valueOf(sets.getX()));
		y1.setText(String.valueOf(sets.getY()));
		width1.setText(String.valueOf(sets.getWidth()));
		height1.setText(String.valueOf(sets.getHeight()));

		Bitmap compress = null;
		if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment
				.getExternalStorageState())) {
			File fileDir = new File(Environment.getExternalStorageDirectory(),
					SD_PATH);
			if (!fileDir.exists()) {
				fileDir.mkdirs();
			}
			File imageFile = new File(fileDir.getPath(), nid + "_1.png");
			if (imageFile.exists()) {
				compress = CommonUtil
						.compressImageFromFile(imageFile.getPath());
			}
		}
		if (compress == null) {
			dlg.dismiss();
			return;
		}

		final Bitmap compressfinal = compress;
		final ImageView imageView = (ImageView) window
				.findViewById(R.id.imgHeader);
		imageView.setImageBitmap(CommonUtil.toRoundBitmap(CommonUtil.cutBitmap(compress,
				sets, false)));

		Button btnSave = (Button) window.findViewById(R.id.btnSave);
		btnSave.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				MatrixInfo matrixInfo1 = new MatrixInfo(Integer.parseInt(x1
						.getText().toString()), Integer.parseInt(y1.getText()
						.toString()), Integer.parseInt(width1.getText()
						.toString()), Integer.parseInt(height1.getText()
						.toString()));
				CommonUtil.setMatrixInfo(context, 6, matrixInfo1);

				imageView.setImageBitmap(CommonUtil.toRoundBitmap(CommonUtil.cutBitmap(
						compressfinal, matrixInfo1, false)));
			}

		});
		
		Button btnOk = (Button) window.findViewById(R.id.btnOk);
		btnOk.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dlg.dismiss();
			}
		});
		
		Button btnExport = (Button) window.findViewById(R.id.btnExport);
		btnExport.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				handler.sendEmptyMessage(2);
				dlg.dismiss();
			}
			
		});
		
		Button btnClear = (Button) window.findViewById(R.id.btnCover);
		btnClear.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				handler.sendEmptyMessage(4);
				dlg.dismiss();
			}
			
		});
		
		
	}

}
