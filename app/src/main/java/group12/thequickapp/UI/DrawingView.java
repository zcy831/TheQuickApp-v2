package group12.thequickapp.UI;

import group12.thequickapp.model.DrawingModel;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;

public class DrawingView extends View{	
	public final int WIDTH = 2;
	// public final int LEN = 400;
	private int areaSize;
	private int viewWidth;
	private int viewHeight;
	public int left;
	public int top;
	DrawingModel model;
	private Paint paint;
	private Paint p;
	private Path margin;
	public Path path;
	public boolean press;
	public long timestamp;
	public Context context;
	
	public DrawingView(Context context, DrawingModel model) {
		super(context);
		this.context = context;
		this.model = model;
		this.paint = new Paint();
		left = WIDTH;
		top = WIDTH;
		WindowManager wm = (WindowManager) context.getSystemService(context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		viewWidth = display.getWidth()/4*3;
		//viewHeight = top+WIDTH*model.N+50;
		viewHeight = display.getHeight()/2;
		setAreaSize(viewWidth, viewHeight, model.N);
		Log.d("chengyuan", "view width " + viewWidth + " view height " + viewHeight + " total unit " + model.N + " area size " + getAreaSize());

		// Log.d("chengyuan", String.valueOf(display.getHeight()));
		// Log.d("chengyuan", String.valueOf(viewHeight));
		this.setLayoutParams(new LayoutParams(viewWidth, viewHeight));
		
		paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setStrokeWidth(WIDTH);
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);

		margin = new Path();
		margin.moveTo(0, 0);
		margin.lineTo(left+areaSize, 0);
		margin.lineTo(left+areaSize, top+areaSize);
		margin.lineTo(0, top+areaSize);
		margin.lineTo(0, 0);
		path = new Path();
		press = false;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawPath(margin, paint);
		canvas.drawPath(path, paint);
		// Toast.makeText(this.context, String.valueOf(this.press), Toast.LENGTH_SHORT).show();
		if(!this.press){
			/*p = new Paint();
			p.setStrokeWidth(0);
			p.setColor(Color.BLACK);
			int t = viewHeight+WIDTH-50;
			for(int i=0;i<model.N;i++)
				for(int j=0;j<model.N;j++){
					if(model.get(j, i)==0){
						p.setColor(Color.WHITE);
					}else{
						p.setColor(Color.BLACK);
					}
					int x = j*WIDTH+left;
					int y = i*WIDTH+t;
					Rect r = new Rect(x, y, x+WIDTH, y+WIDTH);
					canvas.drawRect(r, p);
				}*/
			model.clear();
		}
	}

	private void setAreaSize(int viewWidth, int viewHeight, int totalUnit) {
		areaSize = Math.min(
				(viewWidth - WIDTH * 2) / totalUnit * totalUnit,
				(viewHeight - WIDTH * 2) / totalUnit * totalUnit);
	}

	public int getAreaSize() {
		return areaSize;
	}
}
