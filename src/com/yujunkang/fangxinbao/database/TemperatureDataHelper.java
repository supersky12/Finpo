package com.yujunkang.fangxinbao.database;

import java.util.Date;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.yujunkang.fangxinbao.app.FangXinBaoSettings;
import com.yujunkang.fangxinbao.database.DBConstants.BaseTemperatureDataTable;
import com.yujunkang.fangxinbao.database.DBConstants.CountryTable;
import com.yujunkang.fangxinbao.database.DBConstants.MemoDataTable;
import com.yujunkang.fangxinbao.database.DBConstants.PerDayTemperatureDataTable;
import com.yujunkang.fangxinbao.database.DBConstants.TemperatureHistoryRecordTable;
import com.yujunkang.fangxinbao.database.DBConstants.TemperatureTempDataTable;
import com.yujunkang.fangxinbao.method.Method;
import com.yujunkang.fangxinbao.method.VeDate;
import com.yujunkang.fangxinbao.model.Group;
import com.yujunkang.fangxinbao.model.TemperatureCommonData;
import com.yujunkang.fangxinbao.model.TemperatureData;
import com.yujunkang.fangxinbao.utility.LoggerTool;
import com.yujunkang.fangxinbao.utility.TypeUtils;

/**
 * 
 * @date 2014-7-25
 * @author xieb
 * 
 */
public class TemperatureDataHelper {
	private static final boolean DEBUG = FangXinBaoSettings.DEBUG;
	private static final String TAG = "TemperatureDataHelper";
	private static final String DATABASE_NAME = "temperaturedata.db";
	private static final int DATABASE_VERSION = 5;
	private static boolean isLock = false;

	public static final int MAX_UPLOAD_DATA_COUNT = 50;

	private static TemperatureDataBaseHelper DBHelper;
	private SQLiteDatabase m_DB;
	private static TemperatureDataHelper instance;

	private TemperatureDataHelper(Context ctx) {
		if (DBHelper == null) {
			DBHelper = new TemperatureDataBaseHelper(ctx);
		}
	}

	public boolean IsReady() {
		return DBHelper != null ? true : false;
	}

	public void close() {
		if (DBHelper != null) {
			DBHelper.close();
		}
	}

	public void clearCache() {
		close();
		DBHelper = null;
	}

	public static TemperatureDataHelper getDBInstance(Context context) {
		if (instance == null) {
			instance = new TemperatureDataHelper(context);
		}
		return instance;
	}

	/**
	 * 判断是否存在此温度记录 (babyid和时间戳可以确定唯一)
	 * 
	 * @param babyid
	 * @param time
	 * @return
	 */
	private boolean hasExistData(String babyid, String time) {
		Cursor dataCursor = null;
		try {
			m_DB = DBHelper.getReadableDatabase();
			dataCursor = m_DB.query(TemperatureHistoryRecordTable.TABLE_NAME,
					null, BaseTemperatureDataTable.BABY_ID + "=" + babyid
							+ " and " + BaseTemperatureDataTable.CREATED_DATE
							+ "=" + time, null, null, null, null);
			return dataCursor.moveToFirst();
		} catch (Exception e) {
			if (DEBUG) {
				Log.e(TAG, "hasExistData： " + e.getMessage());
			}
			return false;
		} finally {
			if (dataCursor != null) {
				dataCursor.close();
			}

		}
	}

	/**
	 * 批量从服务器插入一组温度信息
	 * 
	 * @param userid
	 * @param babyid
	 * @param datas
	 * @param isupload
	 * @return
	 */
	public boolean batchInsertTemperatureData(String userid, String babyid,
			Group<TemperatureData> datas, boolean isupload) {
		LoggerTool.d(TAG, "METHOD BEGIN: batchInsertTemperatureData()");
		boolean isHasNewData = false;
		try {
			m_DB = DBHelper.getWritableDatabase();
			m_DB.beginTransaction();
			if (datas != null && datas.size() > 0) {
				int dataCount = datas.size();
				String tableName = isupload ? TemperatureHistoryRecordTable.TABLE_NAME
						: TemperatureTempDataTable.TABLE_NAME;
				for (int index = 0; index < dataCount; index++) {

					TemperatureData tempData = datas.get(index);
					long effectRowCount = m_DB.delete(tableName,
							BaseTemperatureDataTable.BABY_ID + "=" + babyid
									+ " and "
									+ BaseTemperatureDataTable.CREATED_DATE
									+ "=" + tempData.getTime(), null);
					if (effectRowCount > 0) {
						LoggerTool.d(TAG, String.format(
								"delete temperature = %s,time = %s",
								tempData.getTemperature(), tempData.getTime()));
					} else {
						isHasNewData = true;
					}
					effectRowCount = m_DB.insert(
							tableName,
							null,
							generateTemperatureContent(userid, babyid,
									tempData, isupload));

					if (effectRowCount > 0) {
						LoggerTool.d(TAG, String.format(
								"insert temperature = %s,time = %s",
								tempData.getTemperature(), tempData.getTime()));
					}

				}
			} else {
				LoggerTool.d(TAG, "no data need to insert.");
			}
			m_DB.setTransactionSuccessful();
		} catch (Exception e) {
			LoggerTool.e(TAG, "batchInsertTemperatureData : " + e.getMessage());
		} finally {
			if (m_DB != null && m_DB.inTransaction()) {
				m_DB.endTransaction();
			}
		}
		return isHasNewData;
	}

	/**
	 * 插入温度信息
	 * 
	 * @param userid
	 *            用户id
	 * @param babyid
	 *            宝宝id
	 * @param data
	 *            温度数据
	 * @param isupload
	 *            温度数据是否已经上传到服务器
	 */
	public void insertTemperatureData(String userid, String babyid,
			TemperatureData data, boolean isupload) {
		LoggerTool.d(TAG, "METHOD BEGIN: insertTemperatureData()");
		try {
			m_DB = DBHelper.getWritableDatabase();
			if (data != null && !TextUtils.isEmpty(userid)
					&& !TextUtils.isEmpty(babyid)) {
				String tableName = isupload ? TemperatureHistoryRecordTable.TABLE_NAME
						: TemperatureTempDataTable.TABLE_NAME;
				long effectRowCount = m_DB.delete(tableName,
						BaseTemperatureDataTable.BABY_ID + "=" + babyid
								+ " and "
								+ BaseTemperatureDataTable.CREATED_DATE + "="
								+ data.getTime(), null);
				if (effectRowCount > 0) {
					LoggerTool.d(TAG, String.format(
							"delete temperature = %s,time = %s",
							data.getTemperature(), data.getTime()));
				}
				effectRowCount = m_DB.insert(
						tableName,
						null,
						generateTemperatureContent(userid, babyid, data,
								isupload));

				if (effectRowCount > 0) {
					LoggerTool.d(TAG, String.format(
							"insert temperature = %s,time = %s",
							data.getTemperature(), data.getTime()));
				}
			} else {
				LoggerTool.d(TAG, "no data need to insert.");
			}
		} catch (Exception e) {
			LoggerTool.e(TAG, e.getMessage());
		}

	}

	private ContentValues generateTemperatureContent(String userid,
			String babyid, TemperatureData data) {
		return generateTemperatureContent(userid, babyid, data, false);
	}

	private ContentValues generateTemperatureContent(String userid,
			String babyid, TemperatureData data, boolean isupload) {
		try {
			if (data != null) {
				ContentValues cv = new ContentValues();
				String temperature = data.getTemperature();
				String time = data.getTime();
				cv.put(BaseTemperatureDataTable.USER_ID, userid);
				cv.put(BaseTemperatureDataTable.BABY_ID, babyid);
				cv.put(BaseTemperatureDataTable.CREATED_DATE, time);
				cv.put(BaseTemperatureDataTable.TEMPERATURE, temperature);

				return cv;
			}
		} catch (Exception e) {
			LoggerTool.e(TAG, e.getMessage());
		}
		return null;
	}

	/**
	 * 根据时间查询温度数据
	 * 
	 * @param babyid
	 * @param queryDate
	 *            日期:yyyy-MM-dd
	 */
	public Group<TemperatureData> queryTemperatureDataByDate(String babyid,
			String queryDate) {
		LoggerTool.d(TAG, "METHOD BEGIN: queryTemperatureData()");
		Group<TemperatureData> result = new Group<TemperatureData>();
		Cursor cursor = null;
		try {
			StringBuilder sqlSb = new StringBuilder();
			m_DB = DBHelper.getReadableDatabase();
			// 日期函数date(1092941466, 'unixepoch')
			String where = BaseTemperatureDataTable.BABY_ID + " = ? and "
					+ "date(" + BaseTemperatureDataTable.CREATED_DATE
					+ ", ? ,?) = ?";
			// 保存到数据库的时间是long型
			String sql = "select strftime(?,datetime("
					+ BaseTemperatureDataTable.CREATED_DATE
					+ ", ?,?)) as createtime , "
					+ BaseTemperatureDataTable.TEMPERATURE + " from "
					+ TemperatureHistoryRecordTable.TABLE_NAME + " where ";
			String sqlTemp = "select strftime(?,datetime("
					+ BaseTemperatureDataTable.CREATED_DATE
					+ ", ?,?)) as createtime , "
					+ BaseTemperatureDataTable.TEMPERATURE + " from "
					+ TemperatureTempDataTable.TABLE_NAME + " where ";

			String sort = " ORDER BY createtime asc";
			sqlSb.append(sql);
			sqlSb.append(where);
			sqlSb.append(" UNION ALL ");
			sqlSb.append(sqlTemp);
			sqlSb.append(where);
			sqlSb.append(sort);

			cursor = m_DB.rawQuery(sqlSb.toString(), new String[] { "%H:%M",
					"unixepoch", "localtime", babyid, "unixepoch", "localtime",
					queryDate, "%H:%M", "unixepoch", "localtime", babyid,
					"unixepoch", "localtime", queryDate });
			if (cursor != null && cursor.moveToFirst()) {
				result = new Group<TemperatureData>();
				while (!cursor.isAfterLast()) {
					TemperatureData item = new TemperatureData();
					item.setTemperature(cursor.getString(cursor
							.getColumnIndex(BaseTemperatureDataTable.TEMPERATURE)));
					item.setTime(cursor.getString(cursor
							.getColumnIndex("createtime")));
					result.add(item);
					cursor.moveToNext();
				}
			}
		} catch (Exception e) {
			LoggerTool.e(TAG, e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return result;
	}

	/**
	 * 查询最近72小时的温度数据
	 * 
	 * @param context
	 * @param babyid
	 * @return
	 */
	public Group<TemperatureData> queryRecentThreeDaysTemperatureData(
			Context context, String babyid) {
		LoggerTool
				.d(TAG, "METHOD BEGIN: queryRecentThreeDaysTemperatureData()");
		Group<TemperatureData> result = new Group<TemperatureData>();
		Cursor cursor = null;
		try {
			StringBuilder sqlSb = new StringBuilder();
			m_DB = DBHelper.getReadableDatabase();
			long now = System.currentTimeMillis();
			long threeDays = now - 3 * 24 * 60 * 60 * 1000;

			// 日期函数date(1092941466, 'unixepoch')
			String where = BaseTemperatureDataTable.BABY_ID + " = ? and "
					+ BaseTemperatureDataTable.CREATED_DATE + " > ?";
			// 保存到数据库的时间是long型
			String sql = "select " + BaseTemperatureDataTable.CREATED_DATE
					+ "," + BaseTemperatureDataTable.TEMPERATURE + " from "
					+ TemperatureHistoryRecordTable.TABLE_NAME + " where ";
			String sqlTemp = "select " + BaseTemperatureDataTable.CREATED_DATE
					+ ", " + BaseTemperatureDataTable.TEMPERATURE + " from "
					+ TemperatureTempDataTable.TABLE_NAME + " where ";

			String sort = " ORDER BY " + BaseTemperatureDataTable.CREATED_DATE
					+ " asc";
			sqlSb.append(sql);
			sqlSb.append(where);
			sqlSb.append(" UNION ALL ");
			sqlSb.append(sqlTemp);
			sqlSb.append(where);
			sqlSb.append(sort);

			cursor = m_DB.rawQuery(sqlSb.toString(),
					new String[] { babyid, String.valueOf((threeDays / 1000)),
							babyid, String.valueOf((threeDays / 1000)) });
			if (cursor != null && cursor.moveToFirst()) {
				result = new Group<TemperatureData>();
				while (!cursor.isAfterLast()) {
					TemperatureData item = new TemperatureData();
					item.setTemperature(cursor.getString(cursor
							.getColumnIndex(BaseTemperatureDataTable.TEMPERATURE)));
					item.setTime(cursor.getString(cursor
							.getColumnIndex(BaseTemperatureDataTable.CREATED_DATE)));
					TemperatureCommonData temperatureStatus = Method
							.getTemperatureLevelStatus(
									Float.parseFloat(item.getTemperature()),
									context);
					if (temperatureStatus != null) {
						item.setTemperatureLevel(temperatureStatus
								.getTemperatureLevel());
					}
					result.add(item);
					cursor.moveToNext();
				}
			}
		} catch (Exception e) {
			LoggerTool.e(TAG, e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return result;
	}

	/**
	 * 查询未上传的温度数据
	 * 
	 * @param babyid
	 * @return
	 */
	public Group<TemperatureData> queryUnUpLoadTemperatureDataByBabyID(
			String babyid) {

		Group<TemperatureData> result = new Group<TemperatureData>();
		Cursor cursor = null;
		try {
			m_DB = DBHelper.getReadableDatabase();
			cursor = m_DB.query(TemperatureTempDataTable.TABLE_NAME,
					new String[] { BaseTemperatureDataTable.BABY_ID,
							BaseTemperatureDataTable.TEMPERATURE,
							BaseTemperatureDataTable.CREATED_DATE },
					BaseTemperatureDataTable.BABY_ID + " = ? ",
					new String[] { babyid }, null, null, null, null);
			if (cursor != null && cursor.moveToFirst()) {
				result = new Group<TemperatureData>();
				while (!cursor.isAfterLast()) {
					TemperatureData item = new TemperatureData();
					item.setTemperature(cursor.getString(cursor
							.getColumnIndex(BaseTemperatureDataTable.TEMPERATURE)));
					item.setTime(cursor.getString(cursor
							.getColumnIndex(BaseTemperatureDataTable.CREATED_DATE)));
					result.add(item);
					cursor.moveToNext();
				}
			}
		} catch (Exception e) {
			LoggerTool.e(TAG, e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return result;
	}

	/**
	 * 把已经上传的温度数据转移到温度数据表中
	 * 
	 * @param babyid
	 * @return
	 */
	public boolean updateTemperatureDataStatus(String babyid) {
		try {
			m_DB = DBHelper.getWritableDatabase();
			m_DB.beginTransaction();
			String sql = String.format(" Insert into "
					+ TemperatureHistoryRecordTable.TABLE_NAME + "  ("
					+ BaseTemperatureDataTable.USER_ID + ","
					+ BaseTemperatureDataTable.BABY_ID + ","
					+ BaseTemperatureDataTable.TEMPERATURE + ","
					+ BaseTemperatureDataTable.CREATED_DATE + ") select "
					+ BaseTemperatureDataTable.USER_ID + ","
					+ BaseTemperatureDataTable.BABY_ID + ","
					+ BaseTemperatureDataTable.TEMPERATURE + ","
					+ BaseTemperatureDataTable.CREATED_DATE + " from "
					+ TemperatureTempDataTable.TABLE_NAME + " where "
					+ BaseTemperatureDataTable.BABY_ID + "= %s", babyid);
			m_DB.execSQL(sql);
			m_DB.delete(TemperatureTempDataTable.TABLE_NAME,
					BaseTemperatureDataTable.BABY_ID + "=?",
					new String[] { babyid });
			LoggerTool.d(TAG, "update temperature data successful.");
			m_DB.setTransactionSuccessful();
			return true;
		} catch (Exception e) {
			LoggerTool.e(TAG, e.getMessage());
		} finally {
			if (m_DB.inTransaction()) {
				m_DB.endTransaction();
			}
		}
		return false;
	}

	/**
	 * 获取最高温度记录
	 * 
	 * @param babyid
	 * @return
	 */
	public HashMap<String, String> getMAXTemperatureDataByMonth(String babyid,
			String queryDate) {
		HashMap<String, String> result = new HashMap<String, String>();
		Cursor cursor = null;
		try {
			m_DB = DBHelper.getReadableDatabase();
			String whereClause = PerDayTemperatureDataTable.BABY_ID + "= "
					+ babyid;
			if (!TextUtils.isEmpty(queryDate)) {
				whereClause = whereClause
						+ " and  strftime('%Y-%m',created) = strftime('%Y-%m',"
						+ queryDate + ")";
			}
			cursor = m_DB.query(PerDayTemperatureDataTable.TABLE_NAME,
					new String[] { PerDayTemperatureDataTable.TEMPERATURE,
							PerDayTemperatureDataTable.CREATED_DATE },
					whereClause, null, null, null, null, null);
			if (cursor != null && cursor.moveToFirst()) {
				while (!cursor.isAfterLast()) {
					result.put(
							cursor.getString(cursor
									.getColumnIndex(BaseTemperatureDataTable.CREATED_DATE)),
							cursor.getString(cursor
									.getColumnIndex(BaseTemperatureDataTable.TEMPERATURE)));
					cursor.moveToNext();
				}
			}
		} catch (Exception e) {
			LoggerTool.e(TAG, e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return result;
	}

	public String getMemoInfoByDay(String babyid, String queryDate) {
		String result = "";
		Cursor cursor = null;
		try {
			m_DB = DBHelper.getReadableDatabase();
			cursor = m_DB.query(MemoDataTable.TABLE_NAME,
					new String[] { MemoDataTable.MEMO }, MemoDataTable.BABY_ID
							+ " = ? and " + MemoDataTable.CREATED_DATE + "= ?",
					new String[] { babyid, queryDate }, null, null, null, null);
			if (cursor != null && cursor.moveToFirst()) {
				result = cursor.getString(cursor
						.getColumnIndex(MemoDataTable.MEMO));
			}
		} catch (Exception e) {
			LoggerTool.e(TAG, e.getMessage());
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return result;
	}

	public boolean updatePerDayMemo(String userid, String babyid, String info,
			String date) {
		try {
			m_DB = DBHelper.getWritableDatabase();
			ContentValues cv = new ContentValues();
			cv.put(MemoDataTable.MEMO, info);

			long effectRow = m_DB.update(MemoDataTable.TABLE_NAME, cv,
					MemoDataTable.BABY_ID + "= ? and "
							+ MemoDataTable.CREATED_DATE + " = ?",
					new String[] { babyid, date });
			// 没有记录
			if (effectRow == 0) {
				cv.put(MemoDataTable.BABY_ID, babyid);
				cv.put(MemoDataTable.USER_ID, userid);
				cv.put(MemoDataTable.CREATED_DATE, date);
				effectRow = m_DB.insert(MemoDataTable.TABLE_NAME, null, cv);
				if (effectRow > 0) {
					LoggerTool.e(TAG, "insert memo data successful.");
				}
			} else {
				LoggerTool.e(TAG, "update memo data successful.");
			}
		} catch (Exception e) {
			LoggerTool.e(TAG, e.getMessage());
			return false;
		}
		return true;
	}

	public boolean batchInsertMonthStatisticsData(Group<TemperatureData> datas,
			String userid) {
		LoggerTool.d(TAG, "METHOD BEGIN: batchInsertMonthStatisticsData()");
		try {
			m_DB = DBHelper.getWritableDatabase();
			m_DB.beginTransaction();
			if (datas != null && datas.size() > 0) {
				int dataCount = datas.size();

				for (int index = 0; index < dataCount; index++) {
					TemperatureData tempData = datas.get(index);
					String babyid = tempData.getBabyid();
					String createdate = VeDate.DateToStr(
							TypeUtils.StringToLong(tempData.getTime()) * 1000,
							"yyyy-MM-dd");
					String temperature = tempData.getTemperature();
					String memo = tempData.getMemo();
					long effectRowCount = m_DB.delete(
							PerDayTemperatureDataTable.TABLE_NAME,
							PerDayTemperatureDataTable.BABY_ID + "=" + babyid
									+ " and "
									+ PerDayTemperatureDataTable.CREATED_DATE
									+ "=" + createdate, null);
					if (effectRowCount > 0) {
						LoggerTool.d(TAG, String.format(
								"delete temperature = %s,time = %s",
								tempData.getTemperature(), tempData.getTime()));
					}
					effectRowCount = m_DB.insert(
							PerDayTemperatureDataTable.TABLE_NAME,
							null,
							generateMonthTemperatureContent(userid, babyid,
									createdate, temperature));

					if (effectRowCount > 0) {
						LoggerTool.d(TAG, String.format(
								"insert temperature = %s,time = %s",
								temperature, createdate));
					}
					// 操作备忘录
					// ContentValues cv = new ContentValues();
					// cv.put(MemoDataTable.MEMO, memo);
					//
					// long effectRow = m_DB.update(MemoDataTable.TABLE_NAME,
					// cv,
					// MemoDataTable.BABY_ID + "= ? and "
					// + MemoDataTable.CREATED_DATE + " = ?",
					// new String[] { babyid, createdate });
					// // 没有记录
					// if (effectRow == 0) {
					// cv.put(MemoDataTable.BABY_ID, babyid);
					// cv.put(MemoDataTable.USER_ID, userid);
					// cv.put(MemoDataTable.CREATED_DATE, createdate);
					// effectRow = m_DB.insert(MemoDataTable.TABLE_NAME, null,
					// cv);
					// if (effectRow > 0) {
					// LoggerTool.e(TAG, "insert memo data successful.");
					// }
					// } else {
					// LoggerTool.e(TAG, "update memo data successful.");
					// }
				}
			} else {
				LoggerTool.d(TAG, "no data need to insert.");
			}
			m_DB.setTransactionSuccessful();
			return true;
		} catch (Exception e) {
			LoggerTool.e(TAG,
					"batchInsertMonthStatisticsData : " + e.getMessage());
		} finally {
			if (m_DB != null && m_DB.inTransaction()) {
				m_DB.endTransaction();
			}
		}
		return false;

	}

	private ContentValues generateMonthTemperatureContent(String userid,
			String babyid, String createtime, String temperature) {
		try {
			ContentValues cv = new ContentValues();
			cv.put(PerDayTemperatureDataTable.USER_ID, userid);
			cv.put(PerDayTemperatureDataTable.BABY_ID, babyid);
			cv.put(PerDayTemperatureDataTable.CREATED_DATE, createtime);
			cv.put(PerDayTemperatureDataTable.TEMPERATURE, temperature);
			return cv;
		} catch (Exception e) {
			LoggerTool.e(TAG, e.getMessage());
		}
		return null;
	}

	public static class TemperatureDataBaseHelper extends SQLiteOpenHelper {
		TemperatureDataBaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			if (DEBUG) {
				Log.d(TAG, "DataBaseHelper Initializing.");
			}
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			try {
				Log.d(TAG, "create table");
				db.execSQL("CREATE TABLE IF NOT EXISTS "
						+ TemperatureHistoryRecordTable.TABLE_NAME + " ("
						+ TemperatureHistoryRecordTable._ID
						+ " INTEGER PRIMARY KEY autoincrement,"
						+ TemperatureHistoryRecordTable.BABY_ID + " TEXT,"
						+ TemperatureHistoryRecordTable.USER_ID + " TEXT,"
						+ TemperatureHistoryRecordTable.TEMPERATURE + " TEXT,"
						+ TemperatureHistoryRecordTable.CREATED_DATE
						+ " INTEGER" + ");");
				db.execSQL("CREATE TABLE IF NOT EXISTS "
						+ TemperatureTempDataTable.TABLE_NAME + " ("
						+ TemperatureTempDataTable._ID
						+ " INTEGER PRIMARY KEY autoincrement,"
						+ TemperatureTempDataTable.BABY_ID + " TEXT,"
						+ TemperatureTempDataTable.USER_ID + " TEXT,"
						+ TemperatureTempDataTable.TEMPERATURE + " TEXT,"
						+ TemperatureTempDataTable.CREATED_DATE + " INTEGER"
						+ ");");
				db.execSQL("CREATE TABLE IF NOT EXISTS "
						+ PerDayTemperatureDataTable.TABLE_NAME + " ("
						+ PerDayTemperatureDataTable._ID
						+ " INTEGER PRIMARY KEY autoincrement,"
						+ PerDayTemperatureDataTable.BABY_ID + " TEXT,"
						+ PerDayTemperatureDataTable.USER_ID + " TEXT,"
						+ PerDayTemperatureDataTable.TEMPERATURE + " TEXT,"
						+ PerDayTemperatureDataTable.CREATED_DATE + " TEXT"
						+ ");");
				db.execSQL("CREATE TABLE IF NOT EXISTS "
						+ MemoDataTable.TABLE_NAME + " (" + MemoDataTable._ID
						+ " INTEGER PRIMARY KEY autoincrement,"
						+ MemoDataTable.BABY_ID + " TEXT,"
						+ MemoDataTable.USER_ID + " TEXT," + MemoDataTable.MEMO
						+ " TEXT," + MemoDataTable.CREATED_DATE + " TEXT"
						+ ");");
			} catch (SQLException e) {
				// TODO: handle exception
				Log.e(TAG, e.getMessage());
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS "
					+ TemperatureHistoryRecordTable.TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS "
					+ TemperatureTempDataTable.TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS "
					+ PerDayTemperatureDataTable.TABLE_NAME);
			onCreate(db);
		}
	}

}
