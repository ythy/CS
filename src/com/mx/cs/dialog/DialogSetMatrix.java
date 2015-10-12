package com.mx.cs.dialog;

import com.mx.cs.R;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mx.cs.util.CommonUtil;
import com.mx.cs.vo.MatrixInfo;

public class DialogSetMatrix {

	 
	public static void show(final Context context, final Handler handler) {
		final AlertDialog dlg = new AlertDialog.Builder(context)
		.create();
		dlg.show();
		Window window = dlg.getWindow();
		window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		window.setContentView(R.layout.dialog_matrix_set);
		
		final EditText x1 = (EditText) window.findViewById(R.id.etX1);
		final EditText y1 = (EditText) window.findViewById(R.id.etY1);
		final EditText width1 = (EditText) window.findViewById(R.id.etWidth1);
		final EditText height1 = (EditText) window.findViewById(R.id.etHeight1);
		
		final EditText x2 = (EditText) window.findViewById(R.id.etX2);
		final EditText y2 = (EditText) window.findViewById(R.id.etY2);
		final EditText width2 = (EditText) window.findViewById(R.id.etWidth2);
		final EditText height2 = (EditText) window.findViewById(R.id.etHeight2);
		
		final EditText x3 = (EditText) window.findViewById(R.id.etX3);
		final EditText y3 = (EditText) window.findViewById(R.id.etY3);
		final EditText width3 = (EditText) window.findViewById(R.id.etWidth3);
		final EditText height3 = (EditText) window.findViewById(R.id.etHeight3);
		
		MatrixInfo image1 = CommonUtil.getMatrixInfo(context, 1);
		x1.setText(String.valueOf(image1.getX()));
		y1.setText(String.valueOf(image1.getY()));
		width1.setText(String.valueOf(image1.getWidth()));
		height1.setText(String.valueOf(image1.getHeight()));
		MatrixInfo image2 = CommonUtil.getMatrixInfo(context, 2);
		x2.setText(String.valueOf(image2.getX()));
		y2.setText(String.valueOf(image2.getY()));
		width2.setText(String.valueOf(image2.getWidth()));
		height2.setText(String.valueOf(image2.getHeight()));
		
		MatrixInfo image3 = CommonUtil.getMatrixInfo(context, 3);
		x3.setText(String.valueOf(image3.getX()));
		y3.setText(String.valueOf(image3.getY()));
		width3.setText(String.valueOf(image3.getWidth()));
		height3.setText(String.valueOf(image3.getHeight()));
		
		Button btnSave = (Button) window.findViewById(R.id.btnSave);
		btnSave.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(Integer.parseInt(x1.getText().toString()) > Integer.parseInt(width1.getText().toString()) ||
						Integer.parseInt(x2.getText().toString()) > Integer.parseInt(width2.getText().toString()) ||
						Integer.parseInt(y1.getText().toString()) > Integer.parseInt(height1.getText().toString()) ||
						Integer.parseInt(y2.getText().toString()) > Integer.parseInt(height2.getText().toString()) )
				{ 
					Toast.makeText(context, "偏移量不能大于减少量", Toast.LENGTH_SHORT)
					.show();
					return;
				}
						
				MatrixInfo matrixInfo1 = new MatrixInfo(Integer.parseInt(x1.getText().toString()),
						Integer.parseInt(y1.getText().toString()),
						Integer.parseInt(width1.getText().toString()),
						Integer.parseInt(height1.getText().toString()));
				CommonUtil.setMatrixInfo(context, 1, matrixInfo1);
				
				MatrixInfo matrixInfo2 = new MatrixInfo(Integer.parseInt(x2.getText().toString()),
						Integer.parseInt(y2.getText().toString()),
						Integer.parseInt(width2.getText().toString()),
						Integer.parseInt(height2.getText().toString()));
				CommonUtil.setMatrixInfo(context, 2, matrixInfo2);
				
				MatrixInfo matrixInfo3 = new MatrixInfo(Integer.parseInt(x3.getText().toString()),
						Integer.parseInt(y3.getText().toString()),
						Integer.parseInt(width3.getText().toString()),
						Integer.parseInt(height3.getText().toString()));
				CommonUtil.setMatrixInfo(context, 3, matrixInfo3);
				
				
				handler.sendEmptyMessage(1);
				dlg.dismiss();
			}
			
		});
		
		Button btnOk = (Button) window.findViewById(R.id.btnOk);
		btnOk.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dlg.dismiss();
			}
		});
	}

}
