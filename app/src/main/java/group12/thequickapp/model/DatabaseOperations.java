package group12.thequickapp.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import group12.thequickapp.model.TableData.TableInfo1;
import group12.thequickapp.model.TableData.TableInfo2;
import group12.thequickapp.model.TableData.TableInfo3;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseOperations extends SQLiteOpenHelper {
	GridTranslater gridTranslater = new GridTranslater();
	public static final int DATABASE_VERSION = 2;
	public SQLiteDatabase db;
	public static String CREATE_TABLE1 = "CREATE TABLE " + TableInfo1.TABLE_NAME + "(" + TableInfo1.RELATION_NAME + " TEXT," + TableInfo1.PATTERN_TYPE + " TEXT," + TableInfo1.APP_NAME + " TEXT );";
	public static String CREATE_TABLE3 = "CREATE TABLE " + TableInfo3.TABLE_NAME + "(" + TableInfo3.ID + " TEXT," + TableInfo3.SOUNDFILE + " TEXT," + TableInfo3.THRESHOLD + " INTEGER );";

	public DatabaseOperations(Context context) {
		super(context, TableInfo1.DATABSE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("DROP TABLE IF EXISTS "+ TableInfo1.TABLE_NAME+";");
		db.execSQL("DROP TABLE IF EXISTS "+ TableInfo2.TABLE_NAME+";");
		db.execSQL("DROP TABLE IF EXISTS "+ TableInfo3.TABLE_NAME+";");

		db.execSQL(CREATE_TABLE1);
		db.execSQL(createTableGrid(16, gridTranslater));
		db.execSQL(CREATE_TABLE3);}



	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS "+ TableInfo1.TABLE_NAME+";");
		db.execSQL("DROP TABLE IF EXISTS "+ TableInfo2.TABLE_NAME+";");
		db.execSQL("DROP TABLE IF EXISTS "+ TableInfo3.TABLE_NAME+";");
		db.execSQL(CREATE_TABLE1);
		db.execSQL(createTableGrid(16, gridTranslater));
		db.execSQL(CREATE_TABLE3);
	}

	public void putInformation(DatabaseOperations dbo, String relationName, String patternType, String appName){
		SQLiteDatabase sq = dbo.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(TableInfo1.RELATION_NAME, relationName);
		cv.put(TableInfo1.PATTERN_TYPE, patternType);
		cv.put(TableInfo1.APP_NAME, appName);
		sq.insert(TableInfo1.TABLE_NAME, null, cv);
	}

	public void putInformationToGridDB(DatabaseOperations dbo, String relationName, Set<Position> positions){
		SQLiteDatabase sq = dbo.getWritableDatabase();
		ContentValues cv = new ContentValues();
		List<Integer> list = gridTranslater.translateToDatabase(positions);
		cv.put(TableInfo2.ID, relationName);
		int i = 0;
		for(int existOrNot : list){
			if(existOrNot == 1){
				cv.put(gridTranslater.gridTableLocationTranslate2(i+1), 1);
			}else{
				cv.put(gridTranslater.gridTableLocationTranslate2(i+1), 0);
			}
			i=i+1;
		}
		sq.insert(TableInfo2.TABLE_NAME, null, cv);
	}
	
	public void putInformationToSoundDB(DatabaseOperations dbo, String relationName, String soundFile, Integer threshold){
		SQLiteDatabase sq = dbo.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(TableInfo3.ID, relationName);
		cv.put(TableInfo3.SOUNDFILE, soundFile);
		cv.put(TableInfo3.THRESHOLD, threshold);
		sq.insert(TableInfo3.TABLE_NAME, null, cv);
	}

	public Cursor getInformation(DatabaseOperations dop){
		SQLiteDatabase sq = dop.getReadableDatabase();
		String[] columns = {TableInfo1.RELATION_NAME, TableInfo1.PATTERN_TYPE, TableInfo1.APP_NAME};
		Cursor cr = sq.query(TableInfo1.TABLE_NAME, columns, null, null, null, null, null);
		return cr;
	}

	public Cursor getInformationFromGridDB(DatabaseOperations dop){
		SQLiteDatabase sq = dop.getReadableDatabase();
		List<String> columns = new ArrayList<String>();
		columns.add(TableInfo2.ID);
		for(int i=1; i<=16; i++){
			columns.add(gridTranslater.gridTableLocationTranslate2(i));
		}
		Cursor cr = sq.query(TableInfo2.TABLE_NAME, columns.toArray(new String[columns.size()]), null, null, null, null, null);
		return cr;
	}
	
	public Cursor getInformationFromSoundDB(DatabaseOperations dop){
		SQLiteDatabase sq = dop.getReadableDatabase();
		String[] columns = {TableInfo3.ID, TableInfo3.SOUNDFILE, TableInfo3.THRESHOLD};
		Cursor cr = sq.query(TableInfo3.TABLE_NAME, columns, null, null, null, null, null);
		return cr;

	}

	public void updateTable(DatabaseOperations dop, String relationName, String patternType, String appName){
		SQLiteDatabase sq = dop.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(TableInfo1.RELATION_NAME, relationName);
		cv.put(TableInfo1.PATTERN_TYPE, patternType);
		cv.put(TableInfo1.APP_NAME, appName);
		sq.update(TableInfo1.TABLE_NAME, cv, null, null);
	}

	public void deleteDatabase(Context context){
		context.deleteDatabase(TableInfo1.DATABSE_NAME);
	}

	public void deleteRows(DatabaseOperations dop, String relationName){
		SQLiteDatabase db = dop.getWritableDatabase();
		try{
			db.delete(TableInfo1.TABLE_NAME, TableInfo1.RELATION_NAME + " = ?" , new String[] {relationName});
			db.delete(TableInfo2.TABLE_NAME, TableInfo2.ID, new String[] {relationName});
			db.delete(TableInfo3.TABLE_NAME, TableInfo3.ID, new String [] {relationName});
		}catch(Exception e){

		}
		finally{
			db.close();
		}
	}

	public String createTableGrid(Integer length, GridTranslater gridTranslater){
		String output = "CREATE TABLE "+ TableInfo2.TABLE_NAME + "(" + TableInfo2.ID + " TEXT,";
		for (int i = 1; i<=length-1 ; i++){
			output = output + gridTranslater.gridTableLocationTranslate(i);
		}
		output = output + TableInfo2.L + length.toString() + ");";
		return output;
	}
}
