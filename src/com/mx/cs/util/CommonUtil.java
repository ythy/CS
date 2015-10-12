package com.mx.cs.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.regex.Pattern;

import com.mx.cs.vo.MatrixInfo;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore.Images;

public class CommonUtil {

	private static String SD_PATH = "Android/data/com.mx.cs/data";

	public static Bitmap compressImageFromFile(String srcPath) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		newOpts.inJustDecodeBounds = true;// 只读边,不读内容
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		float ww = 400f;//
		int be = 1;
		if (w > ww)
			be = (int) (newOpts.outWidth / ww);
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置采样率

		newOpts.inPreferredConfig = Config.ARGB_8888;// 该模式是默认的,可不设
		newOpts.inPurgeable = true;// 同时设置才会有效
		newOpts.inInputShareable = true;// 。当系统内存不够时候图片自动被回收

		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		bitmap = reviewPicRotate(bitmap, srcPath);

		return bitmap;
	}

	/**
	 * 获取图片文件的信息，是否旋转了90度，如果是则反转
	 * 
	 * @param bitmap
	 *            需要旋转的图片
	 * @param path
	 *            图片的路径
	 */
	public static Bitmap reviewPicRotate(Bitmap bitmap, String path) {
		int degree = getPicRotate(path);
		if (degree != 0) {
			Matrix m = new Matrix();
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			m.setRotate(degree); // 旋转angle度
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, m, true);// 从新生成图片
		}
		return bitmap;
	}

	/**
	 * 读取图片文件旋转的角度
	 * 
	 * @param path
	 *            图片绝对路径
	 * @return 图片旋转的角度
	 */
	public static int getPicRotate(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	public static Bitmap cutBitmap(Bitmap bitmap, MatrixInfo matrixinfo,
			boolean flag) {
		Bitmap result = null;
		if(matrixinfo.getHeight() == 0 || matrixinfo.getWidth() == 0)
			return bitmap;
		
		if (flag)
			result = Bitmap.createBitmap(bitmap, matrixinfo.getX(),
					matrixinfo.getY(),
					bitmap.getWidth() - matrixinfo.getWidth(),
					bitmap.getHeight() - matrixinfo.getHeight());
		else
		{
			if( (matrixinfo.getX() + matrixinfo.getWidth() > bitmap.getWidth()) ||
					(matrixinfo.getY() + matrixinfo.getHeight() > bitmap.getHeight()))
			{
				matrixinfo.setX(0);
				matrixinfo.setY(0);
				matrixinfo.setWidth(100);
				matrixinfo.setHeight(50);
			}
			result = Bitmap.createBitmap(bitmap, matrixinfo.getX(),
					matrixinfo.getY(), matrixinfo.getWidth(),
					matrixinfo.getHeight());
		}
		return result;
	}

	/**
	 * 
	 * @param context
	 * @param type
	 *            1 image1 2 image2 3, 4, 5 point 6 header
	 * @return
	 */
	public static MatrixInfo getMatrixInfo(Context context, int type) {
		SharedPreferences sp = context.getSharedPreferences("matrixSet" + type,
				Context.MODE_PRIVATE);
		MatrixInfo result = new MatrixInfo();
		result.setX(sp.getInt("x", 0));
		result.setY(sp.getInt("y", 0));
		result.setWidth(sp.getInt("width", type > 2 ? 100 : 0));
		result.setHeight(sp.getInt("height", type > 2 ? 50 : 0));
		return result;
	}

	public static void setMatrixInfo(Context context, int type,
			MatrixInfo matrixInfo) {
		SharedPreferences sp = context.getSharedPreferences("matrixSet" + type,
				Context.MODE_PRIVATE);
		sp.edit().putInt("x", matrixInfo.getX()).commit();
		sp.edit().putInt("y", matrixInfo.getY()).commit();
		sp.edit().putInt("width", matrixInfo.getWidth()).commit();
		sp.edit().putInt("height", matrixInfo.getHeight()).commit();
	}

	public static File generateDataFile(String filename) {
		File path = null;
		if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment
				.getExternalStorageState())) {
			File fileDir = new File(Environment.getExternalStorageDirectory(),
					SD_PATH);
			if (!fileDir.exists()) {
				fileDir.mkdirs();
			}
			File jsonFile = new File(fileDir.getPath(), filename);
			if (!jsonFile.exists()) {
				try {
					jsonFile.createNewFile();
					path = jsonFile;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return path;
	}

	public static void printFile(String str, File file) {
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			fw = new FileWriter(file, true);//
			// 创建FileWriter对象，用来写入字符流
			bw = new BufferedWriter(fw); // 将缓冲对文件的输出
			bw.write(str + "\n"); // 写入文件
			bw.newLine();
			bw.flush(); // 刷新该流的缓冲
			bw.close();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
			try {
				bw.close();
				fw.close();
			} catch (IOException e1) {
			}
		}
	}

	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}

	public static void copyBigDataToSD(Context context, String fromName,
			String toPath) throws IOException {
		InputStream myInput;
		OutputStream myOutput = new FileOutputStream(toPath);
		myInput = context.getAssets().open(fromName);
		byte[] buffer = new byte[1024];
		int length = myInput.read(buffer);
		while (length > 0) {
			myOutput.write(buffer, 0, length);
			length = myInput.read(buffer);
		}

		myOutput.flush();
		myInput.close();
		myOutput.close();
	}

	/**
	 * 图片转灰度
	 * 
	 * @param bmSrc
	 * @return
	 */

	public static Bitmap bitmap2Gray(Bitmap bmSrc)

	{
		int width, height;
		height = bmSrc.getHeight();
		width = bmSrc.getWidth();
		Bitmap bmpGray = null;
		bmpGray = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		Canvas c = new Canvas(bmpGray);
		Paint paint = new Paint();
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		paint.setColorFilter(f);
		c.drawBitmap(bmSrc, 0, 0, paint);
		return bmpGray;
	}

	/**
	 * 图片转线性灰度
	 * 
	 * @param image
	 * @return
	 */
	public static Bitmap lineGrey(Bitmap image) {
		// 得到图像的宽度和长度
		int width = image.getWidth();
		int height = image.getHeight();
		// 创建线性拉升灰度图像
		Bitmap linegray = null;
		linegray = image.copy(Config.ARGB_8888, true);
		// 依次循环对图像的像素进行处理
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				// 得到每点的像素值
				int col = image.getPixel(i, j);
				int alpha = col & 0xFF000000;
				int red = (col & 0x00FF0000) >> 16;
				int green = (col & 0x0000FF00) >> 8;
				int blue = (col & 0x000000FF);
				// 增加了图像的亮度
				red = (int) (1.1 * red + 30);
				green = (int) (1.1 * green + 30);
				blue = (int) (1.1 * blue + 30);
				// 对图像像素越界进行处理
				if (red >= 255) {
					red = 255;
				}

				if (green >= 255) {
					green = 255;
				}

				if (blue >= 255) {
					blue = 255;
				}
				// 新的ARGB
				int newColor = alpha | (red << 16) | (green << 8) | blue;
				// 设置新图像的RGB值
				linegray.setPixel(i, j, newColor);
			}
		}
		return linegray;
	}

	/**
	 * 图片 二值化
	 * 
	 * @param graymap
	 * @return
	 */
	public static Bitmap gray2Binary(Bitmap graymap) {
		// 得到图形的宽度和长度
		int width = graymap.getWidth();
		int height = graymap.getHeight();
		// 创建二值化图像
		Bitmap binarymap = null;
		binarymap = graymap.copy(Config.ARGB_8888, true);
		// 依次循环，对图像的像素进行处理
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				// 得到当前像素的值
				int col = binarymap.getPixel(i, j);
				// 得到alpha通道的值
				int alpha = col & 0xFF000000;
				// 得到图像的像素RGB的值
				int red = (col & 0x00FF0000) >> 16;
				int green = (col & 0x0000FF00) >> 8;
				int blue = (col & 0x000000FF);
				// 用公式X = 0.3×R+0.59×G+0.11×B计算出X代替原来的RGB
				int gray = (int) ((float) red * 0.3 + (float) green * 0.59 + (float) blue * 0.11);
				// 对图像进行二值化处理 阀值设置
				if (gray <= 135) {
					gray = 0;
				} else {
					gray = 255;
				}
				// 新的ARGB
				int newColor = alpha | (gray << 16) | (gray << 8) | gray;
				// 设置新图像的当前像素值
				binarymap.setPixel(i, j, newColor);
			}
		}
		return binarymap;
	}

	// 转化圆形
	public static Bitmap toRoundBitmap(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
			roundPx = width / 2;
			top = 0;
			bottom = width;
			left = 0;
			right = width;
			height = width;
			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
			roundPx = height / 2;
			float clip = (width - height) / 2;
			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;
			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}
		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right,
				(int) bottom);
		final Rect dst = new Rect((int) dst_left, (int) dst_top,
				(int) dst_right, (int) dst_bottom);
		final RectF rectF = new RectF(dst);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN)); // 设置遮罩
		canvas.drawBitmap(bitmap, src, dst, paint);
		return output;
	}
	
	//通过URI删除图片  避免缩略图不能及时删除问题
	public static void deleteImages(Context context, File file)
	{
		Uri uri = Uri.fromFile(file);
        if (uri.getScheme().equals("file")) {
            String path = uri.getEncodedPath();
            if (path != null) {
                path = Uri.decode(path);
                ContentResolver cr = context.getContentResolver();
                StringBuffer buff = new StringBuffer();
                buff.append("(")
                        .append(Images.ImageColumns.DATA)
                        .append("=")
                        .append("'" + path + "'")
                        .append(")");
                Cursor cur = cr.query(
                        Images.Media.EXTERNAL_CONTENT_URI,
                        new String[] { Images.ImageColumns._ID },
                        buff.toString(), null, null);
                int index = 0;
                for (cur.moveToFirst(); !cur.isAfterLast(); cur
                        .moveToNext()) {
                    index = cur.getColumnIndex(Images.ImageColumns._ID);
                    index = cur.getInt(index);
                }
                if (index != 0) {
                    context.getContentResolver().delete(Images.Media.EXTERNAL_CONTENT_URI, Images.ImageColumns._ID + "=" + index, null);
                }
            }
        }
	}
}
