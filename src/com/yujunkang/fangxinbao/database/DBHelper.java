package com.yujunkang.fangxinbao.database;

import java.util.Collection;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.yujunkang.fangxinbao.R;
import com.yujunkang.fangxinbao.app.FangXinBaoSettings;
import com.yujunkang.fangxinbao.database.DBConstants.CountryTable;
import com.yujunkang.fangxinbao.database.DBConstants.TemperatureHistoryRecordTable;
import com.yujunkang.fangxinbao.method.VeDate;
import com.yujunkang.fangxinbao.model.Country;
import com.yujunkang.fangxinbao.model.Group;
import com.yujunkang.fangxinbao.model.TemperatureData;
import com.yujunkang.fangxinbao.utility.LoggerTool;

/**
 * 
 * @date 2014-5-24
 * @author xieb
 * 
 */
public class DBHelper {
	private static final boolean DEBUG = FangXinBaoSettings.DEBUG;
	private static final String TAG = "DBHelper";
	private static final String DATABASE_NAME = "fangxinbao.db";
	private static final int DATABASE_VERSION = 8;
	private static boolean isLock = false;

	public static final int MAX_UPLOAD_DATA_COUNT = 50;

	private static DataBaseHelper DBHelper;
	private SQLiteDatabase m_DB;
	private static DBHelper instance;

	private DBHelper(Context ctx) {
		if (DBHelper == null) {
			DBHelper = new DataBaseHelper(ctx);
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

	public static DBHelper getDBInstance(Context context) {
		if (instance == null) {
			instance = new DBHelper(context);
		}
		return instance;
	}

	/**
	 * 批量插入一组国家信息
	 * 
	 * @param message
	 * @return
	 */
	public boolean batchInsertCountry(Collection<Country> datas) {
		LoggerTool.d(TAG, "METHOD BEGIN: batchInsertCountry()");
		try {
			m_DB = DBHelper.getWritableDatabase();
			m_DB.beginTransaction();
			for (Country item : datas) {
				insertOrUpdateCountry(item);
			}
			m_DB.setTransactionSuccessful();
			return true;
		} catch (Exception e) {
			LoggerTool.e(TAG, e.getMessage());
			return false;
		} finally {
			m_DB.endTransaction();
		}

	}

	/**
	 * 根据拼音首字母获取国家信息
	 * 
	 * @param letter
	 * @return
	 */
	public Group<Country> queryCountryByFirstLetter(String letter) {
		Group<Country> result = new Group<Country>();
		Cursor cursor = null;
		try {
			m_DB = DBHelper.getReadableDatabase();
			cursor = m_DB.query(CountryTable.TABLE_NAME, null,
					CountryTable.COUNTRY_FIRST_LETTER + " = ?",
					new String[] { letter.toUpperCase() }, null, null,
					CountryTable.DEFAULT_SORT_ORDER);
			if (cursor != null && cursor.moveToFirst()) {
				while (!cursor.isAfterLast()) {
					Country country = new Country();
					country.setCountryCode(cursor.getString(cursor
							.getColumnIndex(CountryTable.COUNTRY_CODE)));
					country.setCountrySimpleCode(cursor.getString(cursor
							.getColumnIndex(CountryTable.COUNTRY_SIMPLE_CODE)));
					country.setEngName(cursor.getString(cursor
							.getColumnIndex(CountryTable.ENGNAME)));
					country.setName(cursor.getString(cursor
							.getColumnIndex(CountryTable.NAME)));
					country.setFirstLetter(cursor.getString(cursor
							.getColumnIndex(CountryTable.COUNTRY_FIRST_LETTER)));
					result.add(country);
					cursor.moveToNext();
				}
			}
			return result;
		} catch (Exception e) {
			LoggerTool.e(TAG, "", e);
			return null;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

	}

	public Country queryCountryBySimpleCode(String simplecode) {
		Country result = new Country();
		Cursor cursor = null;
		try {
			m_DB = DBHelper.getReadableDatabase();
			cursor = m_DB.query(CountryTable.TABLE_NAME, null,
					CountryTable.COUNTRY_SIMPLE_CODE + " = ?",
					new String[] { simplecode.toUpperCase() }, null, null,
					CountryTable.DEFAULT_SORT_ORDER);
			if (cursor != null && cursor.moveToFirst()) {
				result.setCountryCode(cursor.getString(cursor
						.getColumnIndex(CountryTable.COUNTRY_CODE)));
				result.setCountrySimpleCode(cursor.getString(cursor
						.getColumnIndex(CountryTable.COUNTRY_SIMPLE_CODE)));
				result.setEngName(cursor.getString(cursor
						.getColumnIndex(CountryTable.ENGNAME)));
				result.setName(cursor.getString(cursor
						.getColumnIndex(CountryTable.NAME)));
				result.setFirstLetter(cursor.getString(cursor
						.getColumnIndex(CountryTable.COUNTRY_FIRST_LETTER)));
				return result;
			}

		} catch (Exception e) {
			LoggerTool.e(TAG, "", e);

		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return null;
	}

	public Group<Country> queryCountryByInputCondition(String inputCondition) {
		Group<Country> result = new Group<Country>();
		Cursor cursor = null;
		StringBuilder where = new StringBuilder();
		try {
			m_DB = DBHelper.getReadableDatabase();
			String[] whereValue = null;

			if (inputCondition.matches("^[a-zA-Z_0-9]*")) {
				LoggerTool.d(TAG, "input letters");
				// 如果输入的字母和数字则按国家简称，英文名字来匹配
				where.append("(" + CountryTable.ENGNAME + " like ? or "
						+ CountryTable.COUNTRY_SIMPLE_CODE + " like ? or "
						+ CountryTable.COUNTRY_FIRST_LETTER + " like ? )");
				whereValue = new String[] {
				"%" + inputCondition + "%", "%" + inputCondition + "%",
						"%" + inputCondition + "%" };
			} else {
				LoggerTool.d(TAG, "input chinese");
				// 如果输入是中文则按 城市,机场名,国家来匹配
				where.append("(" + CountryTable.NAME + " like ? )");
				whereValue = new String[] { "%" + inputCondition + "%"};
			}
			cursor = m_DB.query(CountryTable.TABLE_NAME, null,
					where.toString(),
					whereValue, null, null,
					CountryTable.DEFAULT_SORT_ORDER);
			if (cursor != null && cursor.moveToFirst()) {
				while (!cursor.isAfterLast()) {
					Country country = new Country();
					country.setCountryCode(cursor.getString(cursor
							.getColumnIndex(CountryTable.COUNTRY_CODE)));
					country.setCountrySimpleCode(cursor.getString(cursor
							.getColumnIndex(CountryTable.COUNTRY_SIMPLE_CODE)));
					country.setEngName(cursor.getString(cursor
							.getColumnIndex(CountryTable.ENGNAME)));
					country.setName(cursor.getString(cursor
							.getColumnIndex(CountryTable.NAME)));
					country.setFirstLetter(cursor.getString(cursor
							.getColumnIndex(CountryTable.COUNTRY_FIRST_LETTER)));
					result.add(country);
					cursor.moveToNext();
				}
			}

		} catch (Exception e) {
			LoggerTool.e(TAG, "", e);

		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return result;
	}

	/**
	 * 根据国家代码获取国家信息
	 * 
	 * @param message
	 * @return
	 */
	public Country queryCountryByCode(String code) {
		Country result = new Country();
		Cursor cursor = null;
		try {
			m_DB = DBHelper.getReadableDatabase();
			cursor = m_DB.query(CountryTable.TABLE_NAME, null,
					CountryTable.COUNTRY_CODE + " = ?", new String[] { code },
					null, null, CountryTable.DEFAULT_SORT_ORDER);
			if (cursor != null && cursor.moveToFirst()) {

				result.setCountryCode(cursor.getString(cursor
						.getColumnIndex(CountryTable.COUNTRY_CODE)));
				result.setCountrySimpleCode(cursor.getString(cursor
						.getColumnIndex(CountryTable.COUNTRY_SIMPLE_CODE)));
				result.setEngName(cursor.getString(cursor
						.getColumnIndex(CountryTable.ENGNAME)));
				result.setName(cursor.getString(cursor
						.getColumnIndex(CountryTable.NAME)));
				result.setFirstLetter(cursor.getString(cursor
						.getColumnIndex(CountryTable.COUNTRY_FIRST_LETTER)));
				return result;
			}

		} catch (Exception e) {
			LoggerTool.e(TAG, "", e);

		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return null;
	}

	/**
	 * 获取所有国家信息
	 * 
	 * @return
	 */
	public Group<Country> queryAllCountries() {
		Group<Country> result = new Group<Country>();
		Cursor cursor = null;
		try {
			m_DB = DBHelper.getReadableDatabase();
			cursor = m_DB.query(CountryTable.TABLE_NAME, null,
					CountryTable.COUNTRY_CODE + " is not null", null, null,
					null, CountryTable.DEFAULT_SORT_ORDER);
			if (cursor != null && cursor.moveToFirst()) {
				while (!cursor.isAfterLast()) {
					Country country = new Country();
					country.setCountryCode(cursor.getString(cursor
							.getColumnIndex(CountryTable.COUNTRY_CODE)));
					country.setCountrySimpleCode(cursor.getString(cursor
							.getColumnIndex(CountryTable.COUNTRY_SIMPLE_CODE)));
					country.setEngName(cursor.getString(cursor
							.getColumnIndex(CountryTable.ENGNAME)));
					country.setName(cursor.getString(cursor
							.getColumnIndex(CountryTable.NAME)));
					country.setFirstLetter(cursor.getString(cursor
							.getColumnIndex(CountryTable.COUNTRY_FIRST_LETTER)));
					result.add(country);
					cursor.moveToNext();
				}
			}
			return result;
		} catch (Exception e) {
			LoggerTool.e(TAG, "", e);
			return null;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

	}

	/**
	 * 插入或更新一条城市机场信息
	 * 
	 * @param message
	 * @return
	 */
	public void insertOrUpdateCountry(Country data) {
		LoggerTool.d(TAG, "METHOD BEGIN: insertOrUpdateCountry()");
		deleteCountry(data);
		insertCountry(data);

	}

	/**
	 * 删除一条国家信息
	 * 
	 * @param country
	 */
	public void deleteCountry(Country country) {
		LoggerTool.d(TAG, "METHOD BEGIN: deleteCountry()");
		try {
			if (m_DB.inTransaction() == false) {
				m_DB = DBHelper.getWritableDatabase();
			}
			String where = CountryTable.COUNTRY_CODE + " =? ";
			String[] whereValue = { country.getCountryCode() };
			int effectRow = m_DB.delete(CountryTable.TABLE_NAME, where,
					whereValue);
			if (effectRow > 0) {
				LoggerTool.d(TAG, "METHOD deleteCountry: successful");
			}
		} catch (Exception e) {
			LoggerTool.e(TAG, "", e);
		} finally {

		}

	}

	/**
	 * 插入一条国家信息
	 * 
	 * @param message
	 * @return
	 */
	public void insertCountry(Country country) {
		LoggerTool.d(TAG, "METHOD BEGIN: insertCountry()");
		try {
			if (m_DB.inTransaction() == false) {
				m_DB = DBHelper.getWritableDatabase();
			}
			ContentValues cv = new ContentValues();
			String countryCode = country.getCountryCode();
			cv.put(CountryTable.COUNTRY_CODE, countryCode);
			cv.put(CountryTable.COUNTRY_SIMPLE_CODE, country
					.getCountrySimpleCode().toUpperCase());
			cv.put(CountryTable.CREATED_DATE, VeDate.getStringDateShort());
			cv.put(CountryTable.NAME, country.getName());
			cv.put(CountryTable.ENGNAME, country.getEngName());
			cv.put(CountryTable.COUNTRY_FIRST_LETTER, country.getFirstLetter()
					.toUpperCase());
			long effectRowCount = m_DB
					.insert(CountryTable.TABLE_NAME, null, cv);
			if (effectRowCount > 0) {
				LoggerTool.d(TAG, "insertSuccessful: insertCountry()");
			}

		} catch (Exception ex) {

		}

	}

	public static class DataBaseHelper extends SQLiteOpenHelper {
		DataBaseHelper(Context context) {
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
						+ CountryTable.TABLE_NAME + " (" + CountryTable._ID
						+ " INTEGER PRIMARY KEY," + CountryTable.COUNTRY_CODE
						+ " TEXT," + CountryTable.COUNTRY_SIMPLE_CODE
						+ " TEXT," + CountryTable.CREATED_DATE + " TEXT,"
						+ CountryTable.COUNTRY_FIRST_LETTER + " TEXT,"
						+ CountryTable.NAME + " TEXT," + CountryTable.ENGNAME
						+ " TEXT" + ");");
			} catch (SQLException e) {
				// TODO: handle exception
				Log.e(TAG, e.getMessage());
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + CountryTable.TABLE_NAME);
			onCreate(db);
		}
	}

}
