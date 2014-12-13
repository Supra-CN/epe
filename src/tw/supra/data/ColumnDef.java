
package tw.supra.data;

import android.provider.BaseColumns;

public class ColumnDef {
    public static interface PreferencesCommon {
        public static final String GID = "gid";
        public static final String OS_TYPE = "os_type";
        public static final String APP_ID = "app_id";
        public static final String MODEL_TYPE = "model_type";
        public static final String MAC_ADDRESS = "mac_address";
        public static final String IMEI = "imei";
        public static final String IMSI = "imsi";
        public static final String UUID = "uuid";
        public static final String PASSPORT_MD5 = "passport_md5";
        public static final String CURRENT_USER = "current_user";
        public static final String TRAFFIC_CTRL = "traffic_ctrl";
        public static final String LARGE_TEXT = "large_text";
        public static final String FIRST_LAUNCH = "first_launch";
        public static final String UPDATE_APK_DOWNLOAD_ID = "update_apk_download_id";
        public static final String DATE_OF_EMOTION_UPDATED = "date_of_emotion_updated";
        public static final String DATE_OF_VERSION_CHECK = "date_of_version_check";
    }

    public static interface ColumnsAccount  {
        public static final String UID = "uid";
        public static final String NICK_NAME = "nike_name";
        public static final String GENDER = "gender";
        public static final String DESCRIPTION = "description";
        public static final String SCORE = "score";
        public static final String GRADE = "grade";
        public static final String CHARM = "charm";
        public static final String ELITE = "elite";
        public static final String ART = "art";
        public static final String TITLE = "title";
        public static final String LINE_TIME = "line_time";
        public static final String LOGIN_COUNT = "login_count";
        public static final String LAST_LOGIN = "last_login";
        public static final String REG_DATE = "reg_date";
        public static final String PROVINCE = "province";
        public static final String MESSAGE_TOTAL = "message_total";
        public static final String FRIENDS_TOTAL = "friends_total";
        public static final String FOLLOWERS_TOTAL = "followers_total";
        public static final String AVATAR = "avatar";
    }

    public static interface ColumnsDraft {
        public static final String DRAFT_ID = "draft_id";
        public static final String FORUM_ID = "forum_id";
        public static final String THREAD_ID = "thread_id";
        public static final String REF_FLOOR = "ref_floor";
        public static final String SAVE_TIME = "save_time";
        public static final String TITLE = "title";
        public static final String CONTENT = "content";
        public static final String IMAGE_LIST = "image_list";
        public static final String IMAGE_UPLOADED = "image_uploaded_map";
    }

    public static interface ColumnsEmotion {
        public static final String PKG = "pkg";
        public static final String SCR = "src";
        public static final String DESC = "desc";
    }

    public static interface PreferencesUser {
        public static final String UID = "uid";
        public static final String TYPE = "type";
        public static final String SIG = "sig";
        public static final String TOKEN = "token";
        public static final String PP_TOKEN = "pp_token";
        public static final String DATE_OF_FORUMS_UPDATED = "date_of_forums_updated";
    }
    
    
    public static interface ColumnsFavForum {
        public static final String OWNER = "owner";
        public static final String FAV = "fav";
    }
    public static interface ColumnsForum {
        public static final String FORUM_ID = "forum_id";
        public static final String C_ID = "c_id";
        public static final String NAME = "name";
        public static final String C_NAME = "c_name";
        public static final String DESC = "desc";
        public static final String LOGO = "logo";
        public static final String TOTAL = "total";
        public static final String IS_READED = "is_readed";
//        public static final String IS_FAVORITE = "is_favorite";
        public static final String IS_RECOMMEND = "is_recommend";
    }

    public static interface ColumnThreads {
        public static final String THREAD_ID = "thread_id";
        public static final String FORUM_ID = "forum_id";
        public static final String TITLE = "title";
        public static final String FORUM_NAME = "forum_name";
        public static final String PASSPORT = "passport";
        public static final String TYPE = "type";
        public static final String NICK_NAME = "nike_name";
        public static final String AVATAR = "avatar";
        public static final String VIEW_COUNT = "view_count";
        public static final String REPLY_COUNT = "reply_count";
        public static final String LIKE_COUNT = "like_count";
        public static final String PHOTO_COUNT = "photo_count";
        public static final String IS_PRISED = "is_prised";
        public static final String IS_READED = "is_readed";
        public static final String PAGE_FORUM = "page_forum";
        public static final String PAGE_MY_THREADS = "page_my_threads";
        public static final String PAGE_PRISED = "page_prised";
        public static final String THREAD_INIT_PAGE = "thread_init_page";
        public static final String C_TIME = "c_time";
        public static final String R_TIME = "r_time";
        public static final String SRC_URL = "src_url";
        public static final String USER_FOLLOWING = "user_following";
        public static final String USER_FOLLOWED_BY = "user_followed_by";
    }
    
    public interface ColumnTag extends BaseColumns {
        String TAG = "tag";
    }

    
    public interface ColumnTopic extends BaseColumns {
        String TOPIC_ID = "topic_id";
        String TOPIC_LOGO = "topic_logo";
        String TOPIC_TITLE = "topic_title";
    }

    public interface ColumnTopicMembership{
        public static final String RAW_CONTACT_ID = ColumnTag.TAG;
        public static final String GROUP_ROW_ID = ColumnTopic.TOPIC_ID;
    }
    
    public static interface ColumnsPost {
        //TODO:显示个人信息，新增passport行
        public static final String THREAD_ID = "thread_id";
        public static final String FLOOR = "floor";
        public static final String PAGE = "page";
        public static final String PAGE_P = "page_p";// 只看此人的私有页码
        public static final String POST_TIME = "post_time";
        public static final String BRIEF = "brief";
        public static final String CONTENT = "content";
        public static final String NICK_NAME = "nick_name";
        public static final String AVATAR = "avatar";
        public static final String QUOTE = "quote";
        public static final String IS_OP = "is_op";// original poster楼主
    }


}
