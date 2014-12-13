
package tw.supra.epe.account;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import tw.supra.data.DataDef.DataUser;
import tw.supra.data.DataDef.DataUser.TableThreads;
import tw.supra.data.LocalData;
import tw.supra.epe.App;
import tw.supra.utils.Log;

import java.util.HashMap;

/**
 * 用户数据库，里面存放和账户有关的数据，包括匿名用户
 * 
 * @author supra
 */
public class UserData extends LocalData {
    private static final String LOG_TAG = UserData.class.getSimpleName();
    private static final HashMap<String, UserData> USER_DATAS = new HashMap<String, UserData>();

    /**
     * 获得普通用户数据库
     * 
     * @return
     */
    public static UserData getUserData(String passport) {
        UserData userData = USER_DATAS
                .get(TextUtils.isEmpty(passport) ? Account.ANONYMOUS_UID : passport);
        userData = new UserData(App.getInstance(), passport);
        USER_DATAS.put(passport, userData);
        return userData;
    }

    /**
     * 关闭被打开的数据库
     */
    public static void closeAllDB() {
        for (UserData data : USER_DATAS.values()) {
            if (null != data) {
                data.close();
            }
        }
    }

    private UserData(Context context, String passport) {
        super(context, passport, DataUser.VERSION);
    }

    @Override
    public void onDbCreate(SQLiteDatabase db) {
        // db.execSQL(DBDefine.UserDB.SQL_CREARE);
        // db.execSQL(TableForums.SQL_CREATE);
        // db.execSQL(DataDefine.UserDB.PostsTable.SQL_CREATE);
        db.execSQL(TableThreads.SQL_CREATE);
        // db.execSQL(DataDefine.UserDB.DraftsTableBak.SQL_CREATE);
        Log.i(LOG_TAG, "SupraDebug onInitForums : " + NAME);
        // onInitForums(db);
        Log.i(LOG_TAG, "SupraDebug onInitForumsEnd : " + NAME);
    }

    /**
     * 升级语句规范：SQL_UPGRADE_V%d_%s V%d是指针对某个数据库版本做的升级； %s是指针对某一个功能做的升级；
     */
    @Override
    public void onDbUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (int v = oldVersion; v <= newVersion; v++) {
            switch (v) {
                case 1:
                    // db.execSQL(DataDefine.UserDB.PostsTable.SQL_UPGRADE_V1);
                    // TODO:处理数据版本升级
                    break;
                case 2:
                    // db.execSQL(TableForums.SQL_UPGRADE_V2_ADD_DESC);
                    // db.execSQL(TableForums.SQL_UPGRADE_V2_ADD_LOGO);
                    // db.execSQL(DataDefine.UserDB.PostsTable.SQL_UPGRADE_V2);
                    // TODO:处理数据版本升级
                    break;
                case 3:
                    // db.execSQL(DataDefine.UserDB.PostsTable.SQL_UPGRADE_V3);
                    break;

                case 4:
                    db.execSQL(DataUser.SQL_UPGRADE_V4_DROP_DRAFTS);
                    db.execSQL(DataUser.SQL_UPGRADE_V4_DROP_POSTS);
                    break;
                case 5:
                    db.execSQL(DataUser.SQL_UPGRADE_V5_DROP_FORUMS);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onDbConfigure(SQLiteDatabase db) {
    }

    @Override
    public void onDbOpen(SQLiteDatabase db) {
    }

}
