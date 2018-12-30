package group12.thequickapp.model;

import group12.thequickapp.model.TableData.TableInfo2;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseOperationForTemp extends SQLiteOpenHelper {

	public DatabaseOperationForTemp(Context context) {
		super(context, TableInfo2.DATABASE_NAME, null, 1);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS temp;");
		db.execSQL("CREATE TABLE temp (x INTEGER, y INTEGER, intent VARCHAR);");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}
	
	public void putInformation(DatabaseOperations dbo,int x,int y){
		SQLiteDatabase sq = dbo.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("x", x);
		cv.put("y", y);
		cv.put("intent", " ");
		sq.insert("temp", null, cv);
	}
	
	public void updateInformation(DatabaseOperations dbo){
		
	}

}
