
package tw.supra.epe;

// sohu club obj// Identifiers
public interface UriDef {

    // schemes
    public final static String SCHEME_CONTENT = "content";
    public final static String SCHEME_EPE = "eoip";

    // authority
    public static final String AUTHORITY_EPE = "tw.supra.epe.provider";

    public static final class Obj {
        public final static String USER = "user";
        public final static String FORUM = "forum";
        public final static String THREAD = "thread";
        public final static String POST = "post";
        public final static String DRAFT = "draft";
        public final static String TOPIC = "topic";
        public final static String TAG = "tag";
    }

    // host : 提供资源的包名
    public final static String HOST_SOHU_CLUB = "com.sohu.club";

    // path
    public static final String PATH_TOPIC = Obj.TOPIC;
    public static final String PATH_TAG = Obj.TAG;
    public final static String PATH_USER = Obj.USER;
    public final static String PATH_FORUM = Obj.FORUM;
    public final static String PATH_THREAD = Obj.THREAD;
    public final static String PATH_POST = Obj.POST;
    public final static String PATH_DRAFT = Obj.DRAFT;

    // parameters
    public final static String PARAMETERS_DRAFT_ID = "draft_id";
    public final static String PARAMETERS_FORUM_ID = "forum_id";
    public final static String PARAMETERS_THREAD_ID = "thread_id";
    public final static String PARAMETERS_FLOOR = "floor";
    public final static String PARAMETERS_PASSPORT = "passport";
}
