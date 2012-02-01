package jp.co.sae.android.banana.common;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;

public class BitmapResizable {

	private BitmapDrawable bitmapDrawable;
	private Bitmap bitmap;
	private Rect rect;
	private int x = 0;
	private int y = 0;
	
	public BitmapResizable(Bitmap bitmap){
		this.bitmap = bitmap;
		this.bitmapDrawable = new BitmapDrawable(this.bitmap);
	}
	
	/**
	 * サイズ変換
	 * @param width リサイズ後の幅
	 * @param height リサイズ後の高さ
	 * @return リサイズ後の画像
	 */
	public Bitmap resize(float width, float height){
		float resizeScaleWidth;
		float resizeScaleheight;
		
		Matrix matrix = new Matrix();
		resizeScaleWidth = width / bitmap.getWidth();
		resizeScaleheight = height / bitmap.getHeight();
		matrix.postScale(resizeScaleWidth, resizeScaleheight);
		Bitmap resizeBitmap = Bitmap.createBitmap(
				bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		bitmapDrawable = new BitmapDrawable(resizeBitmap);
		
		return resizeBitmap;
	}
	
	public void draw(int x, int y, Canvas canvas){
		rect = new Rect(x, y,
				x + bitmapDrawable.getIntrinsicWidth(), 
				y + bitmapDrawable.getIntrinsicHeight());
		bitmapDrawable.setBounds(rect);
		bitmapDrawable.draw(canvas);
	}
	
	public void draw(Canvas canvas){
		rect = new Rect(this.x, this.y,
				this.x + bitmapDrawable.getIntrinsicWidth(), 
				this.y + bitmapDrawable.getIntrinsicHeight());
		bitmapDrawable.setBounds(rect);
		bitmapDrawable.draw(canvas);
	}
	
	public BitmapDrawable getBitmapDrawble(){
		return bitmapDrawable;
	}
	
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

}
