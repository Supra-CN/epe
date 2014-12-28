
package tw.supra.epe;

import tw.supra.epe.ColumnDef.ColumnThreads;
import tw.supra.epe.ColumnDef.ColumnTopic;
import tw.supra.epe.ColumnDef.ColumnsAccount;
import tw.supra.epe.ColumnDef.ColumnsDraft;
import tw.supra.epe.ColumnDef.ColumnsEmotion;
import tw.supra.epe.ColumnDef.ColumnsFavForum;
import tw.supra.epe.ColumnDef.ColumnsForum;
import tw.supra.epe.ColumnDef.ColumnsPost;
import tw.supra.epe.ColumnDef.PrefCommon;
import tw.supra.epe.ColumnDef.PrefUser;
import android.provider.BaseColumns;

/**
 * 数据库相关定义；数据库定义，表定义，行定义，创建语句定义，升级语句定义；
 * 
 * @author supra
 */
public class DataDef {

    // ========================================================
    // DataBase Define
    // ========================================================
    public static final String ENABLE_DB_FOREIGN_KEY = "PRAGMA foreign_keys=ON;";

    private abstract static class BaseData {
    }

    private abstract static class BaseTable {
        private BaseTable() {
        }
    }

    private abstract static class BaseColumn implements BaseColumns {
        private BaseColumn() {
        }
    }

    /**
     * 通用数据库定义
     * 
     * @author supra
     */

    public static final class DataCommon extends BaseData {
        public static final String NAME = "common";
        //TODO:显示个人信息，升级数据库版本
        public static final int VERSION = 4;
        public static final String SQL_UPGRADE_V1_CREATE_TABLE_DRAFT = TableDrafts.SQL_CREATE;
        public static final String SQL_UPGRADE_V1_CREATE_TABLES_EMOTION = TableEmotion.SQL_CREATE;
        public static final String SQL_UPGRADE_V2_CREATE_TABLE_POST = TablePost.SQL_CREATE;
        public static final String SQL_UPGRADE_V3_CREATE_TABLE_FAV_FORUMS = TableFavForums.SQL_CREATE;
        public static final String SQL_UPGRADE_V3_CREATE_TABLE_FORUMS = TableForums.SQL_CREATE;

        public static final class Preferences extends BaseColumn implements PrefCommon {
        }

        public static final class TableFavForums extends BaseTable {
            // TODO:加读过的标志位
            public static final String NAME = "fav_forums";

            public static final class Columns extends BaseColumn implements ColumnsFavForum {
                public static final String CONCRETE_OWNER = NAME + "." + OWNER;
                public static final String CONCRETE_FAV = NAME + "." + FAV;
            }

            public static final String SQL_CREATE = "CREATE TABLE IF NOT EXISTS " + NAME + " (" +
                    Columns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE, " +
                    Columns.OWNER + " TEXT NOT NULL , " +
                    Columns.FAV + " TEXT NOT NULL , " +
                    " UNIQUE ( " + Columns.OWNER + " , " + Columns.FAV + " )" + // 唯一约束
                    ");";
        }

        public static final class TableForums extends BaseTable {
            public static final String NAME = "forums";

            public static final class Columns extends BaseColumn implements ColumnsForum {
                public static final String CONCRETE_FORUM_ID = TableForums.NAME + "." + FORUM_ID;
            }

            public static final String SQL_CREATE = "CREATE TABLE IF NOT EXISTS " + NAME + " (" +
                    Columns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE, " +
                    Columns.FORUM_ID + " TEXT NOT NULL UNIQUE, " +
                    Columns.NAME + " TEXT, " +
                    Columns.C_NAME + " TEXT, " +
                    Columns.C_ID + " TEXT, " +
                    Columns.DESC + " TEXT, " +
                    Columns.LOGO + " TEXT, " +
                    Columns.TOTAL + " INTEGER DEFAULT 0, " +
                    Columns.IS_RECOMMEND + " INTEGER DEFAULT 0 " +
                    ");";
        }

        public static final class TablePost {
            public static final String NAME = "posts";
            //TODO:显示个人信息，新增数据库升级语句

            public static final class Columns extends BaseColumn implements ColumnsPost {
            }

            //TODO:显示个人信息，新增passport创建语句；
            public static final String SQL_CREATE = "CREATE TABLE IF NOT EXISTS " + NAME + " (" +
                    Columns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE, " +
                    Columns.THREAD_ID + " TEXT NOT NULL, " +
                    Columns.FLOOR + " INTEGER DEFAULT 0 NOT NULL, " +
                    Columns.PAGE + " INTEGER DEFAULT 0, " +
                    Columns.PAGE_P + " INTEGER DEFAULT 0, " +
                    Columns.NICK_NAME + " TEXT, " +
                    Columns.AVATAR + " TEXT, " +
                    Columns.IS_OP + " INTEGER DEFAULT 0, " +
                    Columns.POST_TIME + " LONG DEFAULT 0, " +
                    Columns.BRIEF + "  TEXT, " +
                    Columns.CONTENT + " LONG TEXT, " +
                    Columns.QUOTE + " INTEGER DEFAULT 0, " +
                    " UNIQUE ( " + Columns.THREAD_ID + " , " + Columns.FLOOR + " )" + // 唯一约束
                    ");";
        }

        public static final class TableAccounts extends BaseTable {
            public static final String NAME = "accounts";

            public static final class Columns extends BaseColumn implements ColumnsAccount {
            }

            public static final String SQL_CREATE = "CREATE TABLE IF NOT EXISTS " + NAME + " (" +
                    Columns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE, " +
                    Columns.UID + " TEXT NOT NULL UNIQUE, " +
                    Columns.AUTH + " TEXT, " +
                    Columns.NAME + " TEXT, " +
                    Columns.SCORE + " INTEGER DEFAULT 0, " +
                    Columns.FANS_COUNT + " INTEGER DEFAULT 0, " +
                    Columns.ATTENTION_COUNT + " INTEGER DEFAULT 0, " +
//                    Columns.GENDER + " TEXT, " +
//                    Columns.DESCRIPTION + " TEXT, " +
//                    Columns.GRADE + " INTEGER DEFAULT 0, " +
//                    Columns.CHARM + " INTEGER DEFAULT 0, " +
//                    Columns.ELITE + " INTEGER DEFAULT 0, " +
//                    Columns.ART + " INTEGER DEFAULT 0, " +
//                    Columns.TITLE + " TEXT, " +
//                    Columns.LINE_TIME + " INTEGER DEFAULT 0, " +
//                    Columns.LOGIN_COUNT + " INTEGER DEFAULT 0, " +
//                    Columns.LAST_LOGIN + " TEXT, " +
//                    Columns.REG_DATE + " LONG DEFAULT 0, " +
//                    Columns.PROVINCE + " TEXT DEFAULT 0, " +
//                    Columns.MESSAGE_TOTAL + " INTEGER DEFAULT 0, " +
//                    Columns.FRIENDS_TOTAL + " INTEGER DEFAULT 0, " +
//                    Columns.FOLLOWERS_TOTAL + " INTEGER DEFAULT 0, " +
                    Columns.AVATAR + " TEXT " +
                    ");";
        }

        public static final class TableDrafts extends BaseTable {
            public static final String NAME = "drafts";

            public static final class Columns extends BaseColumn implements ColumnsDraft {
            }

            public static final String SQL_CREATE = "CREATE TABLE IF NOT EXISTS " + NAME + " (" +
                    Columns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE, " +
                    Columns.DRAFT_ID + " TEXT NOT NULL UNIQUE, " +
                    Columns.FORUM_ID + " TEXT NOT NULL, " +
                    Columns.THREAD_ID + " TEXT, " +
                    Columns.REF_FLOOR + " LONG DEFAULT 0, " +
                    Columns.SAVE_TIME + " LONG DEFAULT 0, " +
                    Columns.TITLE + " TEXT, " +
                    Columns.CONTENT + " LONG TEXT, " +
                    Columns.IMAGE_LIST + " LONG TEXT, " + // Gson
                    // TypeToken<ArrayList<path>>
                    Columns.IMAGE_UPLOADED + " LONG TEXT " + // Gson
                    // TypeToken<Map<url,
                    // String>>
                    ");";
        }

        public static final class TableEmotion extends BaseTable {
            public static final String NAME = "emotions";

            public static final String SQL_CREATE = "CREATE TABLE IF NOT EXISTS " + NAME + " (" +
                    Columns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE, " +
                    Columns.SCR + " TEXT, " +
                    Columns.PKG + " TEXT, " +
                    Columns.DESC + " TEXT " +
                    ");";

            public static final class Columns extends BaseColumn implements ColumnsEmotion {
            }
        }

        public static final class TableTopic extends BaseTable {
            public static final String NAME = "emotions";

            public static final class Column extends BaseColumn implements ColumnTopic {
            }

            public static final String CREATE_TABLE_TOPICS = "CREATE TABLE IF NOT EXISTS "
                    + NAME + " (" +
                    Column._ID + " INTEGER KEY AUTOINCREMENT," +
                    Column.TOPIC_ID + " TEXT PRIMARY KEY, " +
                    Column.TOPIC_TITLE + " TEXT, " +
                    Column.TOPIC_LOGO + " TEXT " +
                    ");";
        }

    }

    /**
     * 用户数据库定义
     * 
     * @author supra
     */
    public static final class DataUser extends BaseData {
        public static final int VERSION = 6;
        public static final String ANONYMOUS = "anonymous";

        public static final String SQL_UPGRADE_V5_DROP_FORUMS = "DROP TABLE IF EXISTS forums;";
        public static final String SQL_UPGRADE_V4_DROP_POSTS = "DROP TABLE IF EXISTS posts;";
        public static final String SQL_UPGRADE_V4_DROP_DRAFTS = "DROP TABLE IF EXISTS drafts;";

        public static final class Preferences extends BaseColumn implements PrefUser {
        }

//         public static final class TableForums extends BaseTable {
//         // TODO:加读过的标志位
//         public static final String NAME = "forums";
//         public static final String SQL_UPGRADE_V2_ADD_DESC = "ALTER TABLE " + NAME
//         + " ADD COLUMN " + Columns.DESC + " TEXT;";
//         public static final String SQL_UPGRADE_V2_ADD_LOGO = "ALTER TABLE " + NAME
//         + " ADD COLUMN " + Columns.LOGO + " TEXT;";
//        
//         public static final class Columns extends BaseColumn implements ColumnsForum {
//         }
//        
//         public static final String SQL_CREATE = "CREATE TABLE IF NOT EXISTS " + NAME + " (" +
//         Columns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE, " +
//         Columns.FORUM_ID + " TEXT NOT NULL UNIQUE, " +
//         Columns.NAME + " TEXT, " +
//         Columns.C_NAME + " TEXT, " +
//         Columns.C_ID + " TEXT, " +
//         Columns.DESC + " TEXT, " +
//         Columns.LOGO + " TEXT, " +
//         Columns.TOTAL + " INTEGER DEFAULT 0, " +
//         Columns.IS_READED + " INTEGER DEFAULT 0, " +
//         Columns.IS_FAVORITE + " INTEGER DEFAULT 0, " +
//         Columns.IS_RECOMMEND + " INTEGER DEFAULT 0 " +
//         ");";
//         }

        public static final class TableThreads extends BaseTable {
            public static final String NAME = "threads";

            public static class Columns extends BaseColumn implements ColumnThreads {
            }

            public static final String SQL_CREATE = "CREATE TABLE IF NOT EXISTS " + NAME + " (" +
                    Columns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE, " +
                    Columns.THREAD_ID + " TEXT NOT NULL UNIQUE, " +
                    Columns.FORUM_ID + " TEXT NOT NULL, " +
                    Columns.TITLE + " TEXT, " +
                    Columns.FORUM_NAME + " TEXT, " +
                    Columns.PASSPORT + " TEXT, " +
                    Columns.SRC_URL + " TEXT, " +
                    Columns.TYPE + " INTEGER DEFAULT 0, " +
                    Columns.NICK_NAME + " TEXT, " +
                    Columns.AVATAR + " TEXT, " +
                    Columns.VIEW_COUNT + " INTEGER DEFAULT 0, " +
                    Columns.REPLY_COUNT + " INTEGER DEFAULT 0, " +
                    Columns.LIKE_COUNT + " INTEGER DEFAULT 0, " +
                    Columns.PHOTO_COUNT + " INTEGER DEFAULT 0, " +
                    Columns.IS_READED + " INTEGER DEFAULT 0, " +
                    Columns.IS_PRISED + " INTEGER DEFAULT 0, " +
                    Columns.PAGE_FORUM + " INTEGER DEFAULT 0, " +
                    Columns.PAGE_MY_THREADS + " INTEGER DEFAULT 0, " +
                    Columns.PAGE_PRISED + " INTEGER DEFAULT 0, " +
                    Columns.USER_FOLLOWING + " INTEGER DEFAULT 0, " +
                    Columns.USER_FOLLOWED_BY + " INTEGER DEFAULT 0, " +
                    Columns.THREAD_INIT_PAGE + " INTEGER DEFAULT 1, " +
                    Columns.C_TIME + " LONG DEFAULT 0, " +
                    Columns.R_TIME + " LONG DEFAULT 0 " +
                    ");";
        }
    }

}
