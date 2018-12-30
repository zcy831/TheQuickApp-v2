package group12.thequickapp.UI;

import group12.thequickapp.model.DrawingModel;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;

public class TestView extends View{
	public final int WIDTH = 1;
	private int viewWidth;
	private int viewHeight;
	public int left;
	public int top;
	DrawingModel model;
	private Paint paint;
	private Path margin;
	public Path path;
	
	public TestView(Context context, DrawingModel model){
		super(context);
		this.model = model;
		this.paint = new Paint();
		WindowManager wm = (WindowManager) context.getSystemService(context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		viewWidth = display.getWidth()/4*3;
		viewHeight = display.getHeight()/2;
		this.setLayoutParams(new LayoutParams(viewWidth, viewHeight));
		left = WIDTH;
		top = viewWidth+WIDTH;
		
		paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setStrokeWidth(WIDTH);
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		margin = new Path();
		margin.moveTo(0, viewWidth); margin.lineTo(left+WIDTH*model.N, viewWidth); margin.lineTo(left+WIDTH*model.N, top+WIDTH*model.N);
		margin.lineTo(0, top+WIDTH*model.N); margin.lineTo(0, viewWidth);
		paint = new Paint();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		paint.setStrokeWidth(0);
		paint.setColor(Color.BLACK);
		for(int i=0;i<model.N;i++)
			for(int j=0;j<model.N;j++){
				if(model.get(j, i)==1.0){
					paint.setColor(Color.BLACK);
				}else{
					paint.setColor(Color.WHITE);
				}
				int x = j*WIDTH+left;
				int y = i*WIDTH+top;
				Rect r = new Rect(x, y, x+WIDTH, y+WIDTH);
				canvas.drawRect(r, paint);
			}
	}
}
