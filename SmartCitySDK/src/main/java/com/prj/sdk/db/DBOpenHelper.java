package com.prj.sdk.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 建立数据库及表
 * 
 * @author liaobo
 * 
 */
public class DBOpenHelper extends SQLiteOpenHelper {

	public DBOpenHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		// 下载记录表
		db.execSQL("CREATE TABLE [down_log] ([id] NVARCHAR(200), [internalver] INTEGER NOT NULL DEFAULT 0 , [appver] NVARCHAR(200) );");
	}
	/**
	 * 数据库更新时调用
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// 注：生产环境上不能做删除操作
		if (newVersion > oldVersion) {
			db.execSQL("DROP TABLE IF EXISTS down_log");
			onCreate(db);
		}
	}
}