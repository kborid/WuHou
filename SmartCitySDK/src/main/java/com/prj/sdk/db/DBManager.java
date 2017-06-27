package com.prj.sdk.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * SQLite数据库管理类
 * 
 * 主要负责数据库资源的初始化,开启,关闭,以及获得DatabaseHelper帮助类操作
 * 
 * @author LiaoBo
 * 
 */
public class DBManager {

	// 本地Context对象
	private Context				mContext		= null;

	private static DBManager	dBManager		= null;
	private DBOpenHelper		dbOpenHelper	= null;
	private String				databaseName;
	private final int			VERSION			= 1;

	/**
	 * 构造函数
	 * 
	 * @param mContext
	 */
	private DBManager(Context mContext, String databaseName) {
		super();
		this.mContext = mContext;
		this.dbOpenHelper = new DBOpenHelper(mContext, databaseName, null, VERSION);// 获取DataBaseHelper
	}

	public static void destory() {
		dBManager = null;
	}

	public static DBManager getInstance(Context mContext, String databaseName) {
		if (null == dBManager) {
			synchronized (DBManager.class) {
				if (null == dBManager) {
					if (databaseName == null || "".equals(databaseName.trim())) {
						dBManager = new DBManager(mContext, "my_down_log.db");
					} else {
						dBManager = new DBManager(mContext, databaseName);
					}
				}
			}
		}
		return dBManager;
	}

	/**
	 * 关闭数据库 注意:当事务成功或者一次性操作完毕时候再关闭
	 */
	public void closeDatabase(SQLiteDatabase dataBase, Cursor cursor) {
		if (null != dataBase) {
			dataBase.close();
		}
		if (null != cursor) {
			cursor.close();
		}
	}

	/**
	 * 打开数据库 注:SQLiteDatabase资源一旦被关闭,该底层会重新产生一个新的SQLiteDatabase
	 */
	public SQLiteDatabase openDatabase() {
		return dbOpenHelper.getWritableDatabase();
	}

	/**
	 * 读取数据
	 */
	public SQLiteDatabase getReadableDatabase() {
		return dbOpenHelper.getReadableDatabase();
	}

	/**
	 * 获取DataBaseHelper
	 * 
	 * @return
	 */
	public DBOpenHelper getDatabaseHelper() {
		return new DBOpenHelper(mContext, this.databaseName, null, VERSION);
	}

}
