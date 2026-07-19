// AI功能脚本 - DeepSeek/方舟/群管理/消息增强/自动通过好友
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import java.util.Locale;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.io.*;
import android.os.Handler;
import android.os.Looper;
import android.graphics.drawable.GradientDrawable;
import android.view.WindowManager;
import android.view.Window;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.text.TextWatcher;
import android.text.Editable;
import android.widget.Switch;
import android.widget.CompoundButton;
import android.widget.CheckBox;
import android.media.MediaPlayer;
import android.widget.BaseAdapter;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.text.TextUtils;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.AbsListView;
import android.util.TypedValue;
import android.widget.RadioButton;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.PowerManager;
import android.widget.Button;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import java.text.SimpleDateFormat;
import android.widget.TextView;
import android.widget.ScrollView;
import java.lang.reflect.Method;
import java.util.regex.Pattern;
import android.widget.RadioGroup;
import android.text.InputType;
import android.content.Context;
import android.widget.TimePicker;
import android.widget.DatePicker;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import java.util.concurrent.atomic.AtomicReference;
import android.view.MotionEvent;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.AbsoluteSizeSpan;
import android.os.Build;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import me.hd.wauxv.data.bean.info.FriendInfo;
import me.hd.wauxv.data.bean.info.GroupInfo;

int dp(int d) {
    android.content.Context ctx = hostContext;
    if (ctx == null) return d * 3;
    return (int) (d * ctx.getResources().getDisplayMetrics().density + 0.5f);
}

int c(String hex) {
    return android.graphics.Color.parseColor("#" + hex);
}

android.widget.TextView T(android.content.Context ctx, String text, int size, int color, boolean bold) {
    android.widget.TextView tv = new android.widget.TextView(ctx);
    tv.setText(text);
    tv.setTextSize(size);
    tv.setTextColor(color);
    if (bold) tv.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
    return tv;
}

android.view.View SP(android.content.Context ctx, int h) {
    android.view.View v = new android.view.View(ctx);
    v.setLayoutParams(new android.widget.LinearLayout.LayoutParams(-1, dp(h)));
    return v;
}

android.view.View H(int cl, int h) {
    if (hostContext == null) return new android.view.View(null);
    android.view.View v = new android.view.View(hostContext);
    v.setLayoutParams(new android.widget.LinearLayout.LayoutParams(-1, dp(h)));
    v.setBackgroundColor(cl);
    return v;
}

int[] candyColors() {
    return new int[]{c("FFE4D8F0"), c("FFDCD0F0"), c("FFD8E8F0"), c("FFE0D4EC"), c("FFF0E4D8"), c("FFE8DEF0")};
}

GradientDrawable createGlassBg(int rad) {
    GradientDrawable gd = new GradientDrawable();
    gd.setOrientation(GradientDrawable.Orientation.TL_BR);
    gd.setColors(candyColors());
    gd.setCornerRadius(dp(rad));
    gd.setStroke(dp(1), c("FFC8C0D8"));
    return gd;
}

GradientDrawable createCardBg(int rad) {
    return createGlassBg(rad);
}

GradientDrawable createInputBg() {
    GradientDrawable gd = new GradientDrawable();
    gd.setColor(c("30FFFFFF"));
    gd.setCornerRadius(dp(12));
    gd.setStroke(dp(1), c("FFC8C0D8"));
    return gd;
}

GradientDrawable createGradientInputBg() {
    return createInputBg();
}

GradientDrawable createGlassBtnBg(int rad, String tint, String textColor, String borderColor) {
    GradientDrawable gd = new GradientDrawable(
        GradientDrawable.Orientation.TL_BR,
        candyColors()
    );
    gd.setCornerRadius(dp(rad));
    gd.setStroke(dp(2), c(borderColor));
    return gd;
}

GradientDrawable createPrimaryBtnBg(int rad) {
    return createGlassBtnBg(rad, "00FFFFFF", "FF1E293B", "FF7C6CD0");
}

GradientDrawable createDangerBtnBg(int rad) {
    return createGlassBtnBg(rad, "00FFFFFF", "FF1E293B", "FFD06068");
}

GradientDrawable createSuccessBtnBg(int rad) {
    return createGlassBtnBg(rad, "00FFFFFF", "FF1E293B", "FF50A870");
}

GradientDrawable createOutlineBtnBg(int rad) {
    return createGlassBtnBg(rad, "00FFFFFF", "FF1E293B", "FF8898C8");
}

GradientDrawable createCircleBtnBg(int rad) {
    return createGlassBtnBg(rad, "00FFFFFF", "FF1E293B", "FFB8A8E8");
}

int[] switchTrackColors() {
    return new int[]{c("FFD0C8F0"), c("FFB8ACF0"), c("FFA890F0"), c("FFB8ACF0"), c("FFD0C8F0")};
}

GradientDrawable createSwitchTrackBg(boolean checked) {
    GradientDrawable gd = new GradientDrawable();
    gd.setOrientation(GradientDrawable.Orientation.TL_BR);
    gd.setColors(switchTrackColors());
    gd.setCornerRadius(dp(14));
    return gd;
}

void styleSwitch(Switch sw, boolean checked) {
    sw.setTrackDrawable(createSwitchTrackBg(checked));
    sw.setThumbTintList(android.content.res.ColorStateList.valueOf(c("FFFFFFFF")));
}

void styleSwitch(Switch sw) {
    styleSwitch(sw, true);
}

void loadAvatarAsync(final android.widget.ImageView iv, final String wxid) {
    if (sAvatarDir == null) return;
    new Thread() {
        public void run() {
            try {
                String[] exts = {".jpg", ".png", "_hd.jpg", "_hd.png", ".jpeg"};
                android.graphics.Bitmap bm = null;
                for (int i = 0; i < exts.length && bm == null; i++) {
                    java.io.File f = new java.io.File(sAvatarDir + "/" + wxid + exts[i]);
                    if (f.exists()) bm = android.graphics.BitmapFactory.decodeFile(f.getAbsolutePath());
                }
                if (bm == null) {
                    String url = getAvatarUrl(wxid);
                    if (url != null && !url.isEmpty()) {
                        java.net.URL u = new java.net.URL(url);
                        java.net.HttpURLConnection conn = (java.net.HttpURLConnection) u.openConnection();
                        conn.setConnectTimeout(5000);
                        conn.setReadTimeout(5000);
                        bm = android.graphics.BitmapFactory.decodeStream(conn.getInputStream());
                        conn.disconnect();
                    }
                }
                final android.graphics.Bitmap fbm = bm;
iv.post(new Runnable() {
                    public void run() { if (fbm != null) iv.setImageBitmap(fbm); }
                });
            } catch (Exception e) {}
        }
    }.start();
}

CharSequence getKeyConfigStatusText() {
    android.text.SpannableStringBuilder sb = new android.text.SpannableStringBuilder();
    sb.append("已配置 ");
    String[] names = {"DSkey", "方舟key"};
    boolean[] cfg = {!deepseekApiKey.isEmpty(), !arkApiKey.isEmpty()};
    int green = c("FF009955");
    int red = c("FFE53935");
    for (int i = 0; i < names.length; i++) {
        int s = sb.length();
        sb.append(names[i]);
        sb.setSpan(new android.text.style.ForegroundColorSpan(cfg[i] ? green : red), s, sb.length(), android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (i < names.length - 1) sb.append(" ");
    }
    return sb;
}

void styleNumberPicker(NumberPicker np) {
    try {
        java.lang.reflect.Field f = NumberPicker.class.getDeclaredField("mSelectionDivider");
        f.setAccessible(true);
        f.set(np, new android.graphics.drawable.ColorDrawable(c("FFC0C8E0")));
    } catch (Exception ignored) {}
    try {
        int count = np.getChildCount();
        for (int i = 0; i < count; i++) {
            android.view.View child = np.getChildAt(i);
            if (child instanceof android.widget.EditText) {
                android.widget.EditText et = (android.widget.EditText) child;
                et.setTextSize(14);
                et.setTextColor(c("FF1E293B"));
            }
        }
    } catch (Exception ignored) {}
}

android.app.Dialog MD(android.content.Context ctx, android.view.View v, double widthRatio, int heightDp) {
    android.app.Dialog dialog = new android.app.Dialog(ctx);
    dialog.setCancelable(true);
    dialog.setCanceledOnTouchOutside(true);
    android.widget.ScrollView sv = new android.widget.ScrollView(ctx);
    sv.setFillViewport(false);
    sv.addView(v);
    dialog.setContentView(sv);
    Window w = dialog.getWindow();
    if (w != null) {
        GradientDrawable gd = new GradientDrawable();
        gd.setOrientation(GradientDrawable.Orientation.TL_BR);
        gd.setColors(candyColors());
        gd.setCornerRadius(dp(16));
        gd.setStroke(dp(1), c("FFC8C0D8"));
        w.setBackgroundDrawable(gd);
        w.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.width = (int) (ctx.getResources().getDisplayMetrics().widthPixels * widthRatio);
        if (heightDp > 0) lp.height = dp(heightDp);
        w.setAttributes(lp);
    }
    return dialog;
}

void safeDismiss(android.app.Dialog dg) {
    if (dg == null) return;
    try {
        if (dg.isShowing()) dg.dismiss();
    } catch (Exception e) {}
}

String resolveObjWxid(Object info) {
    String[] getters = {"getWxid", "getUsername", "getChatRoomName", "getChatroomName", "getRoomId", "getTalker"};
    for (int i = 0; i < getters.length; i++) {
        try { String r = (String) info.getClass().getMethod(getters[i]).invoke(info); log("resolveObjWxid[" + getters[i] + "]=" + r); return r; } catch (Exception e) {}
    }

    String[] pubFields = {"wxid", "username", "chatroomName", "chatRoomName", "mUsername"};
    for (int i = 0; i < pubFields.length; i++) {
        try { String r = (String) info.getClass().getField(pubFields[i]).get(info); log("resolveObjWxid[field:" + pubFields[i] + "]=" + r); return r; } catch (Exception e1) {
            try {
                java.lang.reflect.Field f = info.getClass().getDeclaredField(pubFields[i]);
                f.setAccessible(true);
                String r = (String) f.get(info); log("resolveObjWxid[declField:" + pubFields[i] + "]=" + r); return r;
            } catch (Exception e2) {}
        }
    }

    try {
        for (java.lang.reflect.Field f : info.getClass().getDeclaredFields()) {
            try {
                f.setAccessible(true);
                Object val = f.get(info);
                if (val instanceof String) {
                    String s = (String) val;
                    if (s.contains("@chatroom") || s.startsWith("wxid_")) {
                        log("resolveObjWxid[scanField:" + f.getName() + "]=" + s); return s;
                    }
                }
            } catch (Exception e) {}
        }
    } catch (Exception e) {}

    try {
        String str = info.toString();
        int idx = str.indexOf("@chatroom");
        if (idx > 0) {
            int start = idx;
            while (start > 0 && Character.isLetterOrDigit(str.charAt(start-1))) start--;
            String r = str.substring(start, idx + 9); log("resolveObjWxid[toString]=" + r); return r;
        }
    } catch (Exception e) {}

    try { String r = info.getWxid(); log("resolveObjWxid[direct:getWxid]=" + r); return r; } catch (Exception e1) {
        try { String r = info.getUsername(); log("resolveObjWxid[direct:getUsername]=" + r); return r; } catch (Exception e2) {
            try { String r = info.wxid; log("resolveObjWxid[direct:wxid]=" + r); return r; } catch (Exception e3) {
                try { String r = info.chatroomName; log("resolveObjWxid[direct:chatroomName]=" + r); return r; } catch (Exception e4) {}
            }
        }
    }
    log("resolveObjWxid[FAILED]:" + info.getClass().getName()); return null;
}

String resolveSenderName(String senderWxid, String groupWxid) {
    String cacheKey = senderWxid + "@@" + (groupWxid != null ? groupWxid : "");
    if (contactCache.containsKey(cacheKey)) return contactCache.get(cacheKey);
    String name = null;
    if (groupWxid != null) {
        name = getFriendDisplayName(senderWxid, groupWxid);
    }
    if (name == null || name.isEmpty()) {
        name = getFriendRemarkName(senderWxid);
    }
    if (name == null || name.isEmpty()) {
        name = getFriendNickName(senderWxid);
    }
    if (name == null || name.isEmpty()) {
        name = getFriendName(senderWxid);
    }
    if (name == null || name.isEmpty()) {
        name = senderWxid;
        if (name.contains("@")) {
            name = name.substring(0, name.indexOf("@"));
        }
    }
    contactCache.put(cacheKey, name);
    return name;
}


Set<String> WHITE_LIST = new HashSet<>();
boolean masterSwitch = false;


Handler mainHandler = new Handler(Looper.getMainLooper());
Map<String, String> contactCache = new ConcurrentHashMap<>();
String sAvatarDir = null;


String deepseekApiKey = "";
String deepseekBaseUrl = "https://api.deepseek.com/v1";
String deepseekModel = "deepseek-v4-pro";
String deepseekPersona = "你是一个友好的微信助手，请用简洁自然的语言回复。";
boolean deepseekAtReply = false;
boolean deepseekSmartReply = false;
boolean deepseekTranslate = false;
boolean deepseekSummary = false;
boolean deepseekWriting = false;
boolean deepseekQA = false;
Map<String, java.util.ArrayList> deepseekHistory = new ConcurrentHashMap();

String arkApiKey = "";
String arkImageModel = "doubao-seedream-4-5-251128";
String arkImageSize = "2K";
String arkImageFormat = "png";
String arkVideoModel = "doubao-seedance-2-0-260128";
int arkVideoDuration = 8;
String arkVideoResolution = "720p";
boolean imageGenEnabled = false;
boolean videoGenEnabled = false;

interface ImageCallback { void onResult(String url, String errMsg); }

Set<String> sensitiveWords = new HashSet<>();
boolean sensitiveFilterEnabled = false;

boolean welcomeEnabled = false;
String welcomeMsg = "欢迎加入群聊!";
Map<String, Map<String, String>> keywordReplyMap = new ConcurrentHashMap();
boolean keywordReplyEnabled = false;
java.util.ArrayList groupManageList = new java.util.ArrayList();

boolean antiAdEnabled = false;
Set<String> adKeywords = new HashSet<>();

boolean autoKickEnabled = false;
Set<String> kickKeywords = new HashSet<>();
int kickThreshold = 3;
Map<String, Map<String, Integer>> userViolationMap = new ConcurrentHashMap();
String LS_MEDIA_DIR = "/storage/emulated/0/Download/乐少脚本/乐少AI/";
int warnType = 0;
String warnMsg = "请勿发送违规内容，警告！";
int farewellType = 0;
String farewellMsg = "已被移出群聊";
boolean blacklistEnabled = true;
Map<String, JSONObject> blacklistMap = new ConcurrentHashMap();
int BLACKLIST_PAGE_SIZE = 20;
int welcomeType = 0;
int mtTypeMask = 0;




Map<String, JSONObject> activeVotes = new ConcurrentHashMap();

boolean voiceToTextEnabled = false;
boolean linkSummaryEnabled = false;
boolean fileClassifyEnabled = false;
boolean reminderEnabled = false;
Set<String> aiWhitelist = new HashSet<>();
Map<String, JSONObject> reminderTasks = new ConcurrentHashMap();
boolean unreadStatsEnabled = false;
Map<String, Integer> unreadMessageCounts = new ConcurrentHashMap();
Map<String, List> unreadMessageDetails = new ConcurrentHashMap();
boolean recallLogEnabled = false;
Map<String, String> recallLogMap = new ConcurrentHashMap();

boolean aiToolboxEnabled = false;


boolean autoAcceptFriend = false;
String autoAcceptFriendMsg = "你好呀，很高兴认识你!";


List<String> manualInviteGroups = new ArrayList<>();


void loadConfig() {
    masterSwitch = getBoolean("ls_master_switch", false);
    WHITE_LIST = new HashSet<>();
    try {
        if (cacheDir == null) {
            log("cacheDir未找到");
        } else {
            java.io.File wf = new java.io.File(cacheDir, "whitelist.txt");
            if (wf.exists()) {
                java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(wf));
                String line;
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (!line.isEmpty()) {
                        WHITE_LIST.add(line);
                        log(":" + line);
                    }
                }
                br.close();
            } else {
                log(":" + wf.getAbsolutePath());
            }
        }
    } catch (Exception e) {
        log(":" + e.getMessage());
    }
    aiWhitelist = new HashSet<>();
    try {
        java.io.File awf = new java.io.File(cacheDir != null ? cacheDir : android.os.Environment.getExternalStorageDirectory(), "aiwhitelist.txt");
        if (awf.exists()) {
            java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(awf));
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) aiWhitelist.add(line);
            }
            br.close();
        }
    } catch (Exception e) { log("AI白名单加载失败:" + e.getMessage()); }

    deepseekApiKey = getString("ls_ds_apikey", "");
    deepseekBaseUrl = getString("ls_ds_baseurl", "https://api.deepseek.com/v1");
    deepseekModel = getString("ls_ds_model", "deepseek-v4-pro");
    deepseekPersona = getString("ls_ds_persona", "你是一个友好的微信助手，请用简洁自然的语言回复。");
    deepseekAtReply = getBoolean("ls_ds_at_reply", false);
    deepseekSmartReply = getBoolean("ls_ds_smart_reply", false);
    deepseekTranslate = getBoolean("ls_ds_translate", false);
    deepseekSummary = getBoolean("ls_ds_summary", false);
    deepseekWriting = getBoolean("ls_ds_writing", false);
    deepseekQA = getBoolean("ls_ds_qa", false);

    sensitiveFilterEnabled = getBoolean("ls_sensitive_enabled", false);
    sensitiveWords.clear();
    try {
        String swJson = getString("ls_sensitive_words", "[]");
        JSONArray swArr = new JSONArray(swJson);
        for (int i = 0; i < swArr.length(); i++) sensitiveWords.add(swArr.getString(i));
    } catch (Exception e) {}

    welcomeEnabled = getBoolean("ls_welcome_enabled", false);
    welcomeMsg = getString("ls_welcome_msg", "欢迎加入群聊!");

    keywordReplyEnabled = getBoolean("ls_kwreply_enabled", false);
    keywordReplyMap.clear();
    try {
        String kwJson = getString("ls_kwreply_map", "{}");
        JSONObject kwObj = new JSONObject(kwJson);
        java.util.Iterator kwKeys = kwObj.keys();
        while (kwKeys.hasNext()) {
            String kw = (String) kwKeys.next();
            Map m = new HashMap(); m.put("reply", kwObj.optString(kw));
            keywordReplyMap.put(kw, m);
        }
    } catch (Exception e) {}

    antiAdEnabled = getBoolean("ls_antiad_enabled", false);
    adKeywords.clear();
    try {
        String adJson = getString("ls_ad_keywords", "[]");
        JSONArray adArr = new JSONArray(adJson);
        for (int i = 0; i < adArr.length(); i++) adKeywords.add(adArr.getString(i));
    } catch (Exception e) {}

    autoKickEnabled = getBoolean("ls_autokick_enabled", false);
    kickThreshold = Integer.parseInt(getString("ls_kick_threshold", "3"));
    warnType = Integer.parseInt(getString("ls_warn_type", "0"));
    warnMsg = getString("ls_warn_msg", "请勿发送违规内容，警告！");
    farewellType = Integer.parseInt(getString("ls_farewell_type", "0"));
    farewellMsg = getString("ls_farewell_msg", "已被移出群聊");
    kickKeywords.clear();
    try {
        String kkJson = getString("ls_kick_keywords", "[]");
        JSONArray kkArr = new JSONArray(kkJson);
        for (int i = 0; i < kkArr.length(); i++) kickKeywords.add(kkArr.getString(i));
    } catch (Exception e) {}
    userViolationMap.clear();
    try {
        String vmJson = getString("ls_violation_map", "{}");
        JSONObject vmObj = new JSONObject(vmJson);
        java.util.Iterator gKeys = vmObj.keys();
        while (gKeys.hasNext()) {
            String gid = (String) gKeys.next();
            JSONObject members = vmObj.optJSONObject(gid);
            if (members == null) continue;
            Map<String, Integer> mm = new HashMap<String, Integer>();
            java.util.Iterator mKeys = members.keys();
            while (mKeys.hasNext()) {
                String wxid = (String) mKeys.next();
                mm.put(wxid, members.optInt(wxid, 0));
            }
            userViolationMap.put(gid, mm);
        }
    } catch (Exception e) {}
    blacklistEnabled = getBoolean("ls_blacklist_enabled", true);
    blacklistMap.clear();
    try {
        String blJson = getString("ls_blacklist", "[]");
        JSONArray blArr = new JSONArray(blJson);
        for (int i = 0; i < blArr.length(); i++) {
            JSONObject o = blArr.optJSONObject(i);
            if (o != null) {
                String w = o.optString("wxid", "");
                if (!w.isEmpty()) blacklistMap.put(w, o);
            }
        }
    } catch (Exception e) {}
    welcomeType = Integer.parseInt(getString("ls_welcome_type", "0"));
    mtTypeMask = Integer.parseInt(getString("ls_mt_type_mask", "0"));


    groupManageList.clear();
    try {
        String gmJson = getString("ls_group_manage_list", "[]");
        JSONArray gmArr = new JSONArray(gmJson);
        for (int i = 0; i < gmArr.length(); i++) groupManageList.add(gmArr.getString(i));
    } catch (Exception e) {}

    voiceToTextEnabled = getBoolean("ls_v2t_enabled", false);
    linkSummaryEnabled = getBoolean("ls_linksum_enabled", false);
    fileClassifyEnabled = getBoolean("ls_filecls_enabled", false);
    reminderEnabled = getBoolean("ls_reminder_enabled", false);
    unreadStatsEnabled = getBoolean("ls_unread_enabled", false);
    recallLogEnabled = getBoolean("ls_recall_enabled", false);

    aiToolboxEnabled = getBoolean("ls_aitoolbox_enabled", false);
    arkApiKey = getString("ls_ark_apikey", "");
    arkImageModel = getString("ls_ark_img_model", "doubao-seedream-4-5-251128");
    arkImageSize = getString("ls_ark_img_size", "2K");
    arkImageFormat = getString("ls_ark_img_format", "png");
    arkVideoModel = getString("ls_ark_vid_model", "doubao-seedance-2-0-260128");
    arkVideoDuration = Integer.parseInt(getString("ls_ark_vid_duration", "8"));
    arkVideoResolution = getString("ls_ark_vid_resolution", "720p");
    imageGenEnabled = getBoolean("ls_img_gen_enabled", false);
    videoGenEnabled = getBoolean("ls_vid_gen_enabled", false);
    autoAcceptFriend = getBoolean("ls_auto_accept_friend", false);
    autoAcceptFriendMsg = getString("ls_auto_accept_friend_msg", "你好呀，很高兴认识你!");

    log("master=" + masterSwitch + "whitelist=" + WHITE_LIST.size());
}

void saveWhitelist() {
    try {
        if (cacheDir == null) {
            log(":");
            return;
        }
        java.io.File wf = new java.io.File(cacheDir, "whitelist.txt");
        java.io.FileWriter fw = new java.io.FileWriter(wf);
        for (String wxid : WHITE_LIST) {
            log(":" + wxid);
            fw.write(wxid + "\n");
        }
        fw.close();
        log("" + wf.getAbsolutePath() + "(" + WHITE_LIST.size() + ")");
    } catch (Exception e) {
        log(":" + e.getMessage());
    }
}

void saveAiWhitelist() {
    try {
        java.io.File wf = new java.io.File(cacheDir != null ? cacheDir : android.os.Environment.getExternalStorageDirectory(), "aiwhitelist.txt");
        java.io.FileWriter fw = new java.io.FileWriter(wf);
        for (String wxid : aiWhitelist) fw.write(wxid + "\n");
        fw.close();
    } catch (Exception e) { log("AI白名单保存失败:" + e.getMessage()); }
}

void saveAllConfig() {
    try {
        putBoolean("ls_master_switch", masterSwitch);


        putString("ls_ds_apikey", deepseekApiKey);
        putString("ls_ds_baseurl", deepseekBaseUrl);
        putString("ls_ds_model", deepseekModel);
        putString("ls_ds_persona", deepseekPersona);
        putBoolean("ls_ds_at_reply", deepseekAtReply);
        putBoolean("ls_ds_smart_reply", deepseekSmartReply);
        putBoolean("ls_ds_translate", deepseekTranslate);
        putBoolean("ls_ds_summary", deepseekSummary);
        putBoolean("ls_ds_writing", deepseekWriting);
        putBoolean("ls_ds_qa", deepseekQA);
        putBoolean("ls_sensitive_enabled", sensitiveFilterEnabled);
        JSONArray swArr = new JSONArray();
        java.util.Iterator swIt = sensitiveWords.iterator();
        while (swIt.hasNext()) swArr.put(swIt.next());
        putString("ls_sensitive_words", swArr.toString());
        putBoolean("ls_welcome_enabled", welcomeEnabled);
        putString("ls_welcome_msg", welcomeMsg);
        putBoolean("ls_kwreply_enabled", keywordReplyEnabled);
        JSONObject kwObj = new JSONObject();
        java.util.Iterator kwIt = keywordReplyMap.keySet().iterator();
        while (kwIt.hasNext()) {
            String kw = (String) kwIt.next();
            Map m = (Map) keywordReplyMap.get(kw);
            String reply = (m != null) ? (String) m.get("reply") : "";
            kwObj.put(kw, reply == null ? "" : reply);
        }
        putString("ls_kwreply_map", kwObj.toString());
        putBoolean("ls_antiad_enabled", antiAdEnabled);
        JSONArray adArr = new JSONArray();
        java.util.Iterator adIt = adKeywords.iterator();
        while (adIt.hasNext()) adArr.put(adIt.next());
        putString("ls_ad_keywords", adArr.toString());
        putBoolean("ls_autokick_enabled", autoKickEnabled);
        putString("ls_kick_threshold", String.valueOf(kickThreshold));
        putString("ls_warn_type", String.valueOf(warnType));
        putString("ls_warn_msg", warnMsg);
        putString("ls_farewell_type", String.valueOf(farewellType));
        putString("ls_farewell_msg", farewellMsg);
        JSONArray kkArr = new JSONArray();
        java.util.Iterator kkIt = kickKeywords.iterator();
        while (kkIt.hasNext()) kkArr.put(kkIt.next());
        putString("ls_kick_keywords", kkArr.toString());
        JSONObject vmObj = new JSONObject();
        java.util.Iterator vgIt = userViolationMap.keySet().iterator();
        while (vgIt.hasNext()) {
            String gid = (String) vgIt.next();
            Map mm = (Map) userViolationMap.get(gid);
            if (mm == null) continue;
            JSONObject members = new JSONObject();
            java.util.Iterator mIt = mm.keySet().iterator();
            while (mIt.hasNext()) {
                String wxid = (String) mIt.next();
                members.put(wxid, mm.get(wxid));
            }
            vmObj.put(gid, members);
        }
        putString("ls_violation_map", vmObj.toString());
        putBoolean("ls_blacklist_enabled", blacklistEnabled);
        JSONArray blArr = new JSONArray();
        java.util.Iterator blIt = blacklistMap.values().iterator();
        while (blIt.hasNext()) blArr.put(blIt.next());
        putString("ls_blacklist", blArr.toString());
        putString("ls_welcome_type", String.valueOf(welcomeType));
        putString("ls_mt_type_mask", String.valueOf(mtTypeMask));


        JSONArray gmArr = new JSONArray();
        for (int i = 0; i < groupManageList.size(); i++) gmArr.put(groupManageList.get(i));
        putString("ls_group_manage_list", gmArr.toString());
        putBoolean("ls_v2t_enabled", voiceToTextEnabled);
        putBoolean("ls_linksum_enabled", linkSummaryEnabled);
        putBoolean("ls_filecls_enabled", fileClassifyEnabled);
        putBoolean("ls_reminder_enabled", reminderEnabled);
        putBoolean("ls_unread_enabled", unreadStatsEnabled);
        putBoolean("ls_recall_enabled", recallLogEnabled);


        putBoolean("ls_aitoolbox_enabled", aiToolboxEnabled);
        putString("ls_ark_apikey", arkApiKey);
        putString("ls_ark_img_model", arkImageModel);
        putString("ls_ark_img_size", arkImageSize);
        putString("ls_ark_img_format", arkImageFormat);
        putString("ls_ark_vid_model", arkVideoModel);
        putString("ls_ark_vid_duration", String.valueOf(arkVideoDuration));
        putString("ls_ark_vid_resolution", arkVideoResolution);
        putBoolean("ls_img_gen_enabled", imageGenEnabled);
        putBoolean("ls_vid_gen_enabled", videoGenEnabled);
        putBoolean("ls_auto_accept_friend", autoAcceptFriend);
        putString("ls_auto_accept_friend_msg", autoAcceptFriendMsg);


    } catch (Exception e) {
        log("saveAllConfig失败: " + e.getMessage());
    }
}


void saveDeepseekConfig() {
    saveAllConfig();
}

boolean sendNoticeMedia(String talker, int type, String baseName) {
    try {
        if (type == 1) {
            String[] exts = {".png", ".jpg"};
            for (int i = 0; i < exts.length; i++) {
                java.io.File f = new java.io.File(LS_MEDIA_DIR, baseName + exts[i]);
                if (f.exists()) { sendImage(talker, f.getAbsolutePath()); return true; }
            }
            log("媒体缺失(图片): " + LS_MEDIA_DIR + baseName + ".png/.jpg");
            return false;
        } else if (type == 2) {
            java.io.File silkF = new java.io.File(LS_MEDIA_DIR, baseName + ".silk");
            if (silkF.exists()) { sendVoice(talker, silkF.getAbsolutePath()); return true; }
            java.io.File mp3F = new java.io.File(LS_MEDIA_DIR, baseName + ".mp3");
            if (mp3F.exists()) {
                String silk = cacheDir + "/notice_" + System.currentTimeMillis() + ".silk";
                mp3ToSilk(mp3F.getAbsolutePath(), silk);
                sendVoice(talker, silk);
                return true;
            }
            log("媒体缺失(语音): " + LS_MEDIA_DIR + baseName + ".silk/.mp3");
            return false;
        } else if (type == 3) {
            java.io.File mp4F = new java.io.File(LS_MEDIA_DIR, baseName + ".mp4");
            if (mp4F.exists()) { sendVideo(talker, mp4F.getAbsolutePath()); return true; }
            log("媒体缺失(视频): " + LS_MEDIA_DIR + baseName + ".mp4");
            return false;
        }
    } catch (Exception e) {
        log("发送通知媒体失败: " + e.getMessage());
    }
    return false;
}

void sendWelcomeNotice(String groupWxid, String userWxid) {
    if (welcomeType == 1 || welcomeType == 2 || welcomeType == 3) {
        if (sendNoticeMedia(groupWxid, welcomeType, "欢迎语")) return;
    }
    String txt = (welcomeMsg == null || welcomeMsg.trim().isEmpty()) ? "欢迎加入群聊!" : welcomeMsg;
    sendText(groupWxid, "[AtWx=" + userWxid + "] " + txt);
}

void sendWarnNotice(String talker, String atWxid) {
    if (warnType == 1 || warnType == 2) {
        if (sendNoticeMedia(talker, warnType, "警告")) return;
    }
    String nick = resolveSenderName(atWxid, talker);
    String txt = (warnMsg == null || warnMsg.trim().isEmpty()) ? "请勿发送违规内容，警告！" : warnMsg.replace("{userName}", nick);
    sendAtText(talker, "[AtWx=" + atWxid + "] " + txt, atWxid);
}

void sendFarewellNotice(String talker, String atWxid) {
    if (farewellType == 1 || farewellType == 2) {
        if (sendNoticeMedia(talker, farewellType, "告别语")) return;
    }
    String nick = resolveSenderName(atWxid, talker);
    String txt = (farewellMsg == null || farewellMsg.trim().isEmpty()) ? "已被移出群聊" : farewellMsg.replace("{userName}", nick);
    sendAtText(talker, "[AtWx=" + atWxid + "] " + txt, atWxid);
}

void addToBlacklist(String wxid, String groupId, String reason) {
    if (wxid == null || wxid.trim().isEmpty()) return;
    if (blacklistMap.containsKey(wxid)) return;
    try {
        JSONObject o = new JSONObject();
        o.put("wxid", wxid);
        o.put("name", resolveSenderName(wxid, groupId));
        o.put("reason", reason == null ? "" : reason);
        o.put("group", groupId == null ? "" : groupId);
        o.put("time", System.currentTimeMillis());
        blacklistMap.put(wxid, o);
        saveAllConfig();
        log("加入黑名单: " + wxid + " 原因: " + reason);
    } catch (Exception e) {
        log("加入黑名单失败: " + e.getMessage());
    }
}

String mtTypeName(int idx) {
    String[] names = {"文本", "图片", "表情", "语音", "视频", "小程序", "视频号", "公众号", "链接", "位置", "文件"};
    return (idx >= 0 && idx < names.length) ? names[idx] : "未知";
}

boolean mtEnabled(int idx) {
    return (mtTypeMask & (1 << idx)) != 0;
}

int detectViolationType(Object msg) {
    String content = "";
    try { content = msg.getContent(); } catch (Exception e) {}
    if (content == null) content = "";
    int rt = -1;
    try { rt = msg.getType(); } catch (Exception e) {}
    String low = content.toLowerCase();

    int appType = -1;
    if (content.contains("<appmsg")) {
        try {
            java.util.regex.Matcher m = java.util.regex.Pattern.compile("<type>\\s*(\\d+)\\s*</type>").matcher(content);
            if (m.find()) appType = Integer.parseInt(m.group(1));
        } catch (Exception e) {}
    }

    if (mtEnabled(6) && (rt == 51 || low.contains("finderfeed") || low.contains("finderlive") || low.contains("<finder"))) return 6;
    if (mtEnabled(7)) {
        boolean off = false;
        try { off = msg.isOfficialAccount(); } catch (Exception e) {}
        if (off || low.contains("<mmreader") || content.contains("gh_")) return 7;
    }
    if (mtEnabled(5) && (appType == 33 || appType == 36 || low.contains("<weappinfo") || low.contains("wxapp"))) return 5;
    if (mtEnabled(10)) { boolean f = false; try { f = msg.isFile(); } catch (Exception e) {} if (f || appType == 6) return 10; }
    if (mtEnabled(8)) { boolean l = false; try { l = msg.isLink(); } catch (Exception e) {} if (l || appType == 5) return 8; }
    if (mtEnabled(1)) { boolean im = false; try { im = msg.isImage(); } catch (Exception e) {} if (im || rt == 3) return 1; }
    if (mtEnabled(2)) { boolean em = false; try { em = msg.isEmoji(); } catch (Exception e) {} if (em || rt == 47 || appType == 8) return 2; }
    if (mtEnabled(3)) { boolean vo = false; try { vo = msg.isVoice(); } catch (Exception e) {} if (vo || rt == 34) return 3; }
    if (mtEnabled(4)) { boolean vd = false; try { vd = msg.isVideo(); } catch (Exception e) {} if (vd || rt == 43) return 4; }
    if (mtEnabled(9)) { boolean lo = false; try { lo = msg.isLocation(); } catch (Exception e) {} if (lo || rt == 48) return 9; }
    if (mtEnabled(0)) { boolean tx = false; try { tx = msg.isText(); } catch (Exception e) {} if (tx || rt == 1) return 0; }
    return -1;
}

void handleMessageTypeKick(Object msg) {
    if (mtTypeMask == 0) return;
    boolean isGroup = false;
    try { isGroup = msg.isGroupChat(); } catch (Exception e) { return; }
    if (!isGroup) return;
    boolean isSend = false;
    try { isSend = msg.isSend(); } catch (Exception e) {}
    if (isSend) return;
    String talker = null;
    try { talker = msg.getTalker(); } catch (Exception e) { return; }
    if (talker == null || talker.isEmpty()) return;
    String sender;
    try { sender = msg.getSendTalker(); } catch (Exception e) { sender = talker; }
    if (sender == null || sender.isEmpty()) return;

    int vt = detectViolationType(msg);
    if (vt < 0) return;

    Map groupMap = userViolationMap.get(talker);
    if (groupMap == null) { groupMap = new HashMap(); userViolationMap.put(talker, groupMap); }
    Integer count = (Integer) groupMap.get(sender);
    if (count == null) count = 0;
    count = count + 1;
    groupMap.put(sender, count);
    sendWarnNotice(talker, sender);
    if (count >= kickThreshold) {
        final String fT = talker;
        final String fW = sender;
        final Map fg = groupMap;
        addToBlacklist(sender, talker, "发送禁止类型消息[" + mtTypeName(vt) + "]");
        sendFarewellNotice(talker, sender);
        delay(3000, new Runnable() { public void run() {
            try { delChatroomMember(fT, fW); } catch (Exception e) { log("踢人失败: " + e.getMessage()); }
            fg.remove(fW);
            saveAllConfig();
        }});
    } else {
        saveAllConfig();
    }
}


// onLoad and onUnload merged into mass-send lifecycle below


boolean onClickSendBtn(String x) {
    if (x == null || x.trim().isEmpty()) return false;
    String t = x.trim();
    if (t.equals("乐少AI")) {
        showControlPanel();
        return true;
    }
    if (t.equals("DeepSeek配置") || t.equals("AI配置") || t.equals("deepseek配置")) {
        showDeepseekSettingsDialog();
        return true;
    }
    if (t.equals("AI设置") || t.equals("对话设置") || t.equals("AI对话")) {
        showAIChatSettings();
        return true;
    }
    if (t.equals("群管理") || t.equals("群管设置") || t.equals("群组管理")) {
        showGroupManageSettings();
        return true;
    }
    if (t.equals("消息增强") || t.equals("消息处理") || t.equals("增强设置")) {
        showMsgEnhanceSettings();
        return true;
    }
    if (aiToolboxEnabled && (t.equals("AI工具箱") || t.equals("工具箱") || t.equals("工具"))) {
        showAIToolbox();
        return true;
    }
    if (t.equals("防撤回") || t.equals("撤回记录")) {
        recallLogEnabled = !recallLogEnabled;
        saveAllConfig();
        toast("防撤回记录" + (recallLogEnabled ? "已开启" : "已关闭"));
        return true;
    }
    if (t.equals("翻译")) {
        deepseekTranslate = !deepseekTranslate;
        saveAllConfig();
        toast("翻译模式" + (deepseekTranslate ? "已开启" : "已关闭"));
        return true;
    }
    if (t.equals("摘要")) {
        deepseekSummary = !deepseekSummary;
        saveAllConfig();
        toast("摘要模式" + (deepseekSummary ? "已开启" : "已关闭"));
        return true;
    }
    if (t.equals("@我回复")) {
        deepseekAtReply = !deepseekAtReply;
        saveAllConfig();
        toast("@我回复" + (deepseekAtReply ? "已开启" : "已关闭"));
        return true;
    }
    if (t.equals("智能聊天")) {
        deepseekSmartReply = !deepseekSmartReply;
        saveAllConfig();
        toast("智能聊天" + (deepseekSmartReply ? "已开启" : "已关闭"));
        return true;
    }
    if (t.startsWith("生成图片 ")) {
        if (!imageGenEnabled) {
            sendText(getTargetTalker(), "请先在AI设置中开启\"图片生成\"功能");
            return true;
        }
        String prompt = t.substring(5).trim();
        if (prompt.isEmpty()) {
            sendText(getTargetTalker(), "用法: 生成图片 <描述文字>\n例如: 生成图片 一只坐在太空的猫");
            return true;
        }
        handleImageGen(prompt);
        return true;
    }
    if (t.startsWith("生成视频 ")) {
        if (!videoGenEnabled) {
            sendText(getTargetTalker(), "请先在AI设置中开启\"视频生成\"功能");
            return true;
        }
        String prompt = t.substring(5).trim();
        if (prompt.isEmpty()) {
            sendText(getTargetTalker(), "用法: 生成视频 <描述文字>");
            return true;
        }
        handleVideoGen(prompt);
        return true;
    }
    return false;
}


String callDeepSeekApi(String systemPrompt, String userMessage) {
    return callDeepSeekApiWithHistory(systemPrompt, null, userMessage);
}

String callDeepSeekApiWithHistory(String systemPrompt, String historyKey, String userMessage) {
    if (deepseekApiKey.isEmpty()) return null;

    java.net.HttpURLConnection conn = null;
    try {
        java.net.URL url = new java.net.URL(deepseekBaseUrl + "/chat/completions");
        conn = (java.net.HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + deepseekApiKey);
        conn.setConnectTimeout(20000);
        conn.setReadTimeout(45000);
        conn.setDoOutput(true);

        JSONObject body = new JSONObject();
        body.put("model", deepseekModel);
        body.put("temperature", 0.7);
        body.put("max_tokens", 1024);

        JSONArray msgs = new JSONArray();
        if (systemPrompt != null && !systemPrompt.isEmpty()) {
            JSONObject sys = new JSONObject();
            sys.put("role", "system");
            sys.put("content", systemPrompt);
            msgs.put(sys);
        }

        java.util.ArrayList hist = null;
        if (historyKey != null) {
            synchronized (deepseekHistory) {
                hist = deepseekHistory.get(historyKey);
            }
        }
        if (hist != null) {
            for (int i = 0; i < hist.size(); i++) {
                msgs.put(hist.get(i));
            }
        }

        JSONObject userMsg = new JSONObject();
        userMsg.put("role", "user");
        userMsg.put("content", userMessage);
        msgs.put(userMsg);

        body.put("messages", msgs);

        java.io.OutputStream os = conn.getOutputStream();
        os.write(body.toString().getBytes("UTF-8"));
        os.flush();
        os.close();

        int code = conn.getResponseCode();
        if (code == 200) {
            java.io.InputStream is = conn.getInputStream();
            java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(is, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            br.close();

            JSONObject resp = new JSONObject(sb.toString());
            JSONArray choices = resp.getJSONArray("choices");
            if (choices.length() > 0) {
                String reply = choices.getJSONObject(0).getJSONObject("message").getString("content");
                if (reply != null && historyKey != null) {
                    synchronized (deepseekHistory) {
                        java.util.ArrayList h = deepseekHistory.get(historyKey);
                        if (h == null) { h = new java.util.ArrayList(); deepseekHistory.put(historyKey, h); }
                        JSONObject u = new JSONObject(); u.put("role", "user"); u.put("content", userMessage); h.add(u);
                        JSONObject a = new JSONObject(); a.put("role", "assistant"); a.put("content", reply); h.add(a);
                        while (h.size() > 20) { h.remove(0); h.remove(0); }
                    }
                }
                return reply;
            }
        } else {
            log("DeepSeek API err " + code + ": " + conn.getResponseMessage());
        }
    } catch (Exception e) {
        log("DeepSeek API exception: " + e.getMessage());
    } finally {
        if (conn != null) conn.disconnect();
    }
    return null;
}

void handleDeepSeekMsg(Object msgInfoBean) {
    if (deepseekApiKey.isEmpty()) return;

    boolean isText = false;
    try { isText = msgInfoBean.isText(); } catch (Exception e) { return; }
    if (!isText) return;

    String talker = msgInfoBean.getTalker();
    String senderTalker;
    try { senderTalker = msgInfoBean.getSendTalker(); } catch (Exception e) { senderTalker = talker; }
    boolean isGroup;
    try { isGroup = msgInfoBean.isGroupChat(); } catch (Exception e) { isGroup = false; }

    String content;
    try { content = msgInfoBean.getContent(); } catch (Exception e) { content = ""; }
    if (content == null || content.trim().isEmpty()) return;

    if (!aiWhitelist.isEmpty() && !aiWhitelist.contains(talker)) {
        log("AI白名单拦截DeepSeek: " + talker);
        return;
    }

    boolean isAtMe = false;
    try { isAtMe = msgInfoBean.isAtMe(); } catch (Exception e) { isAtMe = false; }

    String replyTarget = isGroup ? talker : senderTalker;
    String historyKey = isGroup ? talker : senderTalker;
    String senderName = resolveSenderName(senderTalker, isGroup ? talker : null);
    String ct = content.trim();

    if (deepseekAtReply && isAtMe) {
        String reply = callDeepSeekApiWithHistory(deepseekPersona, historyKey, senderName + "说：" + ct);
        if (reply != null && !reply.trim().isEmpty()) {
            String msg = reply.trim();
            if (isGroup) msg = "[AtWx=" + senderTalker + "] " + msg;
            sendText(replyTarget, msg);
        }
        return;
    }

    if (deepseekSmartReply) {
        String reply = callDeepSeekApiWithHistory(deepseekPersona, historyKey, senderName + "说：" + ct);
        if (reply != null && !reply.trim().isEmpty()) sendText(replyTarget, reply.trim());
        return;
    }

    if (deepseekTranslate && ct.length() > 0) {
        String reply = callDeepSeekApi("只输出翻译结果", ct);
        if (reply != null && !reply.trim().isEmpty()) sendText(replyTarget, reply.trim());
        return;
    }

    if (deepseekSummary && (ct.length() > 200 || isGroup)) {
        String reply = callDeepSeekApi("用一句话总结", ct);
        if (reply != null && !reply.trim().isEmpty()) sendText(replyTarget, reply.trim());
        return;
    }

    if (deepseekWriting && ct.length() > 5 && !isAtMe) {
        String reply = callDeepSeekApi("根据用户内容续写或润色，保持自然", ct);
        if (reply != null && !reply.trim().isEmpty()) sendText(replyTarget, reply.trim());
        return;
    }

    if (deepseekQA && ct.length() > 3 && !isAtMe) {
        String reply = callDeepSeekApi("简洁准确回答用户问题", ct);
        if (reply != null && !reply.trim().isEmpty()) sendText(replyTarget, reply.trim());
        return;
    }
}

boolean isGenerationAllowed() {
    String talker = getTargetTalker();
    if (talker == null || talker.isEmpty()) return false;
    if (!masterSwitch) { sendText(talker, "AI 功能未开启"); return false; }
    if (!aiWhitelist.isEmpty()) {
        if (!aiWhitelist.contains(talker)) { sendText(talker, "AI 白名单未授权"); return false; }
    }
    return true;
}

void callArkImageApi(String prompt, ImageCallback cb) {
    new Thread(new Runnable() { public void run() {
        String resultUrl = null;
        String resultErr = null;
        try {
            if (prompt != null && prompt.length() > 800) {
                prompt = prompt.substring(0, 800);
            }
            java.net.URL url = new java.net.URL("https://ark.cn-beijing.volces.com/api/v3/images/generations");
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + arkApiKey);
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(60000);
            conn.setDoOutput(true);

            JSONObject body = new JSONObject();
            body.put("model", arkImageModel);
            body.put("prompt", prompt);
            body.put("size", arkImageSize);
            body.put("response_format", "url");
            body.put("sequential_image_generation", "disabled");
            body.put("stream", false);
            body.put("watermark", true);

            java.io.OutputStream os = conn.getOutputStream();
            os.write(body.toString().getBytes("UTF-8"));
            os.flush();
            os.close();

            int code = conn.getResponseCode();
            if (code == 200) {
                java.io.InputStream is = conn.getInputStream();
                java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(is, "UTF-8"));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
                br.close();
                JSONObject resp = new JSONObject(sb.toString());
                JSONArray data = resp.optJSONArray("data");
                if (data != null && data.length() > 0) {
                    resultUrl = data.getJSONObject(0).optString("url");
                }
            } else {
                try {
                    java.io.InputStream es = conn.getErrorStream();
                    if (es != null) {
                        java.io.BufferedReader ebr = new java.io.BufferedReader(new java.io.InputStreamReader(es, "UTF-8"));
                        StringBuilder esb = new StringBuilder();
                        String eline;
                        while ((eline = ebr.readLine()) != null) esb.append(eline);
                        ebr.close();
                        resultErr = esb.toString();
                    }
                } catch (Exception ee) {
                    resultErr = ee.getMessage();
                }
                if (resultErr == null) resultErr = "HTTP " + code;
                log("ArkImage error " + code + ": " + resultErr);
            }
            conn.disconnect();
        } catch (Exception e) {
            resultErr = e.getMessage();
            log("ArkImage exception: " + resultErr);
        } finally {
            final String finalUrl = resultUrl;
            final String finalErr = resultErr;
            mainHandler.post(new Runnable() { public void run() { cb.onResult(finalUrl, finalErr); } });
        }
    }}).start();
}

void saveToMediaDir(java.io.File src) {
    try {
        java.io.File mediaDir = new java.io.File(LS_MEDIA_DIR);
        if (!mediaDir.exists()) mediaDir.mkdirs();
        String ext = src.getName().substring(src.getName().lastIndexOf("."));
        java.io.File dest = new java.io.File(mediaDir, "arkImg_" + System.currentTimeMillis() + ext);
        java.io.FileInputStream fis = new java.io.FileInputStream(src);
        java.io.FileOutputStream fos = new java.io.FileOutputStream(dest);
        byte[] buf = new byte[8192];
        int len;
        while ((len = fis.read(buf)) > 0) fos.write(buf, 0, len);
        fis.close();
        fos.close();
        log("源文件已保存: " + dest.getAbsolutePath());
    } catch (Exception e) { log("保存源文件失败: " + e.getMessage()); }
}

void handleImageGen(String prompt) {
    if (prompt == null || prompt.isEmpty()) {
        sendText(getTargetTalker(), "用法: 生成图片 <描述文字>\n例如: 生成图片 一只坐在太空的猫");
        return;
    }
    if (arkApiKey.isEmpty()) { sendText(getTargetTalker(), "请先配置火山方舟 API Key"); return; }
    if (!isGenerationAllowed()) return;

    String talker = getTargetTalker();
    sendText(talker, "正在生成图片...");
    callArkImageApi(prompt, (url, errMsg) -> {
        if (url != null && !url.isEmpty()) {
            String path = cacheDir + "/arkImg_" + System.currentTimeMillis() + "." + arkImageFormat;
            download(url, path, null, file -> {
                sendImage(talker, ((java.io.File) file).getAbsolutePath());
                saveToMediaDir((java.io.File) file);
            });
        } else {
            String info = errMsg != null && !errMsg.isEmpty() ? "错误: " + errMsg : "请稍后重试";
            if (info.length() > 200) info = info.substring(0, 200);
            sendText(talker, "图片生成失败，" + info);
        }
    });
}

void createVideoTask(String prompt, String talker) {
    new Thread(new Runnable() { public void run() {
        String taskId = null;
        try {
            java.net.URL url = new java.net.URL("https://ark.cn-beijing.volces.com/api/v3/contents/generations/tasks");
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + arkApiKey);
                        conn.setConnectTimeout(15000);
                        conn.setReadTimeout(100000);
            conn.setDoOutput(true);

            JSONObject body = new JSONObject();
            body.put("model", arkVideoModel);
            JSONArray content = new JSONArray();
            JSONObject textItem = new JSONObject();
            textItem.put("type", "text");
            textItem.put("text", prompt);
            content.put(textItem);
            body.put("content", content);
            body.put("duration", arkVideoDuration);
            body.put("resolution", arkVideoResolution);
            body.put("ratio", "16:9");

            java.io.OutputStream os = conn.getOutputStream();
            os.write(body.toString().getBytes("UTF-8"));
            os.flush();
            os.close();

            int code = conn.getResponseCode();
            if (code == 200) {
                java.io.InputStream is = conn.getInputStream();
                java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(is, "UTF-8"));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
                br.close();
                JSONObject resp = new JSONObject(sb.toString());
                taskId = resp.optString("id");
            } else {
                final int errorCode = code;
                mainHandler.post(new Runnable() { public void run() { sendText(talker, "视频任务创建失败: HTTP " + errorCode); } });
                return;
            }
            conn.disconnect();
        } catch (Exception e) {
            final String errMsg = e.getMessage();
            mainHandler.post(new Runnable() { public void run() { sendText(talker, "视频任务创建异常: " + errMsg); } });
            return;
        }

        if (taskId != null && !taskId.isEmpty()) {
            pollVideoTask(taskId, talker, 0);
        }
    }}).start();
}

void pollVideoTask(String taskId, String talker, int pollCount) {
    if (pollCount >= 30) {
        mainHandler.post(new Runnable() { public void run() { sendText(talker, "视频生成超时，请稍后重试"); } });
        return;
    }
    if (pollCount > 0 && pollCount % 3 == 0) {
        int waited = pollCount * 10;
        mainHandler.post(new Runnable() { public void run() { sendText(talker, "视频生成中，已等待 " + waited + " 秒..."); } });
    }
    try { Thread.sleep(10000); } catch (Exception e) {}

    new Thread(new Runnable() { public void run() {
        String videoUrl = null;
        String status = null;
        try {
            java.net.URL url = new java.net.URL("https://ark.cn-beijing.volces.com/api/v3/contents/generations/tasks/" + taskId);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + arkApiKey);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(30000);

            int code = conn.getResponseCode();
            if (code == 200) {
                java.io.InputStream is = conn.getInputStream();
                java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(is, "UTF-8"));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
                br.close();
                JSONObject resp = new JSONObject(sb.toString());
                status = resp.optString("status");
                if ("completed".equals(status)) {
                    JSONObject ct = resp.optJSONObject("content");
                    if (ct != null) videoUrl = ct.optString("video_url");
                }
            }
            conn.disconnect();
        } catch (Exception e) {
            final String errMsg = e.getMessage();
            mainHandler.post(new Runnable() { public void run() { sendText(talker, "视频查询异常: " + errMsg); } });
            return;
        }

        if ("completed".equals(status) && videoUrl != null && !videoUrl.isEmpty()) {
            final String finalUrl = videoUrl;
            mainHandler.post(new Runnable() { public void run() {
                String path = cacheDir + "/arkVid_" + System.currentTimeMillis() + ".mp4";
                download(finalUrl, path, null, file -> {
                    sendVideo(talker, ((java.io.File) file).getAbsolutePath());
                });
            }});
        } else if ("failed".equals(status) || "cancelled".equals(status)) {
            final String finalStatus = status;
            mainHandler.post(new Runnable() { public void run() { sendText(talker, "视频生成失败，状态: " + finalStatus); } });
        } else {
            pollVideoTask(taskId, talker, pollCount + 1);
        }
    }}).start();
}

void handleVideoGen(String prompt) {
    if (prompt == null || prompt.isEmpty()) {
        sendText(getTargetTalker(), "用法: 生成视频 <描述文字>\n例如: 生成视频 一只猫在海边奔跑");
        return;
    }
    if (arkApiKey.isEmpty()) { sendText(getTargetTalker(), "请先配置火山方舟 API Key"); return; }
    if (!isGenerationAllowed()) return;

    String talker = getTargetTalker();
    sendText(talker, "视频生成中，预计 1-5 分钟...");
    createVideoTask(prompt, talker);
}

void handleGroupManagement(Object msgInfoBean) {
    boolean isText = false;
    try { isText = msgInfoBean.isText(); } catch (Exception e) { return; }
    if (!isText) return;

    boolean isGroup = false;
    try { isGroup = msgInfoBean.isGroupChat(); } catch (Exception e) { return; }
    if (!isGroup) return;

    String talker = msgInfoBean.getTalker();
    String senderTalker;
    try { senderTalker = msgInfoBean.getSendTalker(); } catch (Exception e) { senderTalker = talker; }

    String content;
    try { content = msgInfoBean.getContent(); } catch (Exception e) { content = ""; }
    if (content == null || content.trim().isEmpty()) return;
    String ct = content.trim();

    if (voteEnabled && ct.startsWith("投票 ")) {
        String voteContent = ct.substring(3).trim();
        createVoteForGroup(voteContent, talker);
        return;
    }

    if (keywordReplyEnabled && !keywordReplyMap.isEmpty()) {
        java.util.Iterator it = keywordReplyMap.keySet().iterator();
        while (it.hasNext()) {
            String kw = (String) it.next();
            if (ct.contains(kw)) {
                Map m = (Map) keywordReplyMap.get(kw);
                if (m != null) {
                    String reply = (String) m.get("reply");
                    if (reply != null && !reply.isEmpty()) sendText(talker, reply);
                }
                return;
            }
        }
    }

    if (antiAdEnabled && !adKeywords.isEmpty()) {
        java.util.Iterator it = adKeywords.iterator();
        while (it.hasNext()) {
            String adk = (String) it.next();
            if (ct.contains(adk)) {
                sendText(talker, "[AtWx=" + senderTalker + "] 检测到广告内容，请注意!");
                log("防广告触发: " + adk + " from " + senderTalker);
                return;
            }
        }
    }

    if (autoKickEnabled && !kickKeywords.isEmpty()) {
        java.util.Iterator it = kickKeywords.iterator();
        while (it.hasNext()) {
            String kk = (String) it.next();
            if (ct.contains(kk)) {
                Map groupMap = userViolationMap.get(talker);
                if (groupMap == null) { groupMap = new HashMap(); userViolationMap.put(talker, groupMap); }
                Integer count = (Integer) groupMap.get(senderTalker);
                if (count == null) count = 0;
                count = count + 1;
                groupMap.put(senderTalker, count);
                sendWarnNotice(talker, senderTalker);
                if (count >= kickThreshold) {
                    final String fTalker = talker;
                    final String fWxid = senderTalker;
                    final Map fGroupMap = groupMap;
                    String snippet = ct.length() > 30 ? ct.substring(0, 30) + "..." : ct;
                    addToBlacklist(senderTalker, talker, "发送敏感词[" + kk + "]: " + snippet);
                    sendFarewellNotice(talker, senderTalker);
                    delay(3000, new Runnable() { public void run() {
                        try { delChatroomMember(fTalker, fWxid); } catch (Exception e) { log("踢人失败: " + e.getMessage()); }
                        fGroupMap.remove(fWxid);
                        saveAllConfig();
                    }});
                } else {
                    saveAllConfig();
                }
                return;
            }
        }
    }

    if (voteEnabled && activeVotes.containsKey(talker)) {
        JSONObject vote = activeVotes.get(talker);
        JSONArray options = vote.optJSONArray("options");
        if (options != null) {
            for (int i = 0; i < options.length(); i++) {
                String opt = options.optString(i);
                String optNum = String.valueOf(i + 1);
                if (ct.equals(optNum) || ct.equals(opt)) {
                    JSONObject results = vote.optJSONObject("results");
                    if (results == null) { results = new JSONObject(); vote.put("results", results); }
                    int cur = results.optInt(optNum, 0);
                    results.put(optNum, cur + 1);
                    log("投票: " + senderTalker + " -> " + opt);
                }
            }
        }
        if (ct.equals("结束投票")) {
            JSONObject res = vote.optJSONObject("results");
            JSONArray opts = vote.optJSONArray("options");
            StringBuilder sb = new StringBuilder("投票结果:\n");
            for (int i = 0; i < opts.length(); i++) {
                String optNum = String.valueOf(i + 1);
                sb.append(optNum + ". " + opts.optString(i) + " : " + res.optInt(optNum, 0) + "票\n");
            }
            sendText(talker, sb.toString().trim());
            activeVotes.remove(talker);
        }
    }
}

void handleMessageEnhancement(Object msgInfoBean) {
    if (recallLogEnabled) {
        boolean recalled = false;
        try {
            String ct = msgInfoBean.getContent();
            if (ct != null && ct.contains("revokemsg")) recalled = true;
        } catch (Exception e) {}
        if (recalled) {
            String talker = "";
            String content = "";
            try { talker = msgInfoBean.getTalker(); } catch (Exception e) {}
            try { content = msgInfoBean.getContent(); } catch (Exception e) {}
            long msgId = 0;
            try { msgId = msgInfoBean.getMsgId(); } catch (Exception e) {}
            recallLogMap.put(String.valueOf(msgId), talker + ": " + content);
            log("撤回消息记录: " + talker + " -> " + content);
        }
    }

    if (voiceToTextEnabled) {
        boolean isVoice = false;
        try { isVoice = msgInfoBean.isVoice(); } catch (Exception e) {}
        if (isVoice) {
            String talker = "";
            try { talker = msgInfoBean.getTalker(); } catch (Exception e) {}
            if (aiWhitelist.contains(talker)) {
                sendText(talker, "[语音消息] 语音转文字功能需要配合第三方语音识别服务使用");
            }
        }
    }

    if (linkSummaryEnabled) {
        boolean isLink = false;
        try { isLink = msgInfoBean.isLink(); } catch (Exception e) {}
        if (isLink && !deepseekApiKey.isEmpty()) {
            String content = "";
            String talker = "";
            try { talker = msgInfoBean.getTalker(); } catch (Exception e) { talker = ""; }
            try { content = msgInfoBean.getContent(); } catch (Exception e) { content = ""; }
            if (aiWhitelist.contains(talker)) {
                final String fTalker = talker;
                final String fContent = content;
                new Thread(new Runnable() {
                    public void run() {
                        String reply = callDeepSeekApi("请用一句话描述这个链接的内容", fContent);
                        if (reply != null && !reply.trim().isEmpty()) {
                            if (!fTalker.isEmpty()) sendText(fTalker, reply.trim());
                        }
                    }
                }).start();
            }
        }
    }

    if (fileClassifyEnabled) {
        boolean isFile = false;
        try { isFile = msgInfoBean.isFile(); } catch (Exception e) {}
        if (isFile) {
            String talker = "";
            try { talker = msgInfoBean.getTalker(); } catch (Exception e) {}
            if (aiWhitelist.contains(talker)) {
                log("文件消息检测到");
                try {
                    Object fMsg = msgInfoBean.getFileMsg();
                    if (fMsg != null) {
                        String title = ""; long size = 0; String ext = "";
                        try { title = fMsg.getTitle(); } catch (Exception e) {}
                        try { size = fMsg.getSize(); } catch (Exception e) {}
                        try { ext = fMsg.getExt(); } catch (Exception e) {}
                        String displayName = getFriendName(talker);
                        if (displayName == null || displayName.isEmpty()) displayName = talker;
                        String timeStr = new java.text.SimpleDateFormat("MM-dd HH:mm").format(new java.util.Date());
                        String rec = timeStr + " " + displayName + " " + title + " " + size + "B ." + ext;
                        log("文件: " + title + " 大小: " + size + " 后缀: " + ext);
                        String saved = getString("ls_file_log", "");
                        String newLog = saved + rec + "\n";
                        if (newLog.length() > 8000) {
                            int cut = newLog.indexOf("\n", newLog.length() - 7000);
                            if (cut > 0) newLog = newLog.substring(cut + 1);
                        }
                        putString("ls_file_log", newLog);
                    }
                } catch (Exception ex) {}
            }
        }
    }

    if (unreadStatsEnabled) {
        String talker = "";
        try { talker = msgInfoBean.getTalker(); } catch (Exception e) {}
        Integer c = unreadMessageCounts.get(talker);
        unreadMessageCounts.put(talker, c == null ? 1 : c + 1);
        String content = "";
        try { content = msgInfoBean.getContent(); } catch (Exception e) {}
        if (content != null && !content.isEmpty()) {
            List msgs = unreadMessageDetails.get(talker);
            if (msgs == null) {
                msgs = new java.util.ArrayList();
                unreadMessageDetails.put(talker, msgs);
            }
            if (msgs.size() >= 30) msgs.remove(0);
            msgs.add(content);
        }
    }
}

void onHandleMsg(Object msgInfoBean) {
    log("onHandleMsg : talker=" + msgInfoBean.getTalker() + "master=" + masterSwitch + "isSend=" + msgInfoBean.isSend());

    if (!masterSwitch) {
        log("onHandleMsg : masterSwitch=false");
        return;
    }

    if (msgInfoBean.isSend()) {
        log("onHandleMsg : isSend=true");
        return;
    }

    if (sensitiveFilterEnabled && !sensitiveWords.isEmpty()) {
        try {
            boolean isText = msgInfoBean.isText();
            if (isText) {
                String sc = msgInfoBean.getContent();
                if (sc != null) {
                    java.util.Iterator swIt = sensitiveWords.iterator();
                    while (swIt.hasNext()) {
                        if (sc.contains((String) swIt.next())) {
                            log("敏感词过滤: 消息被拦截");
                            return;
                        }
                    }
                }
            }
        } catch (Exception e) {}
    }

    if (imageGenEnabled || videoGenEnabled) {
        try {
            if (msgInfoBean.isText()) {
                String ct = msgInfoBean.getContent();
                if (ct != null) {
                    String t = ct.trim();
                    String talker = msgInfoBean.getTalker();
                    if (!aiWhitelist.isEmpty() && !aiWhitelist.contains(talker)) {
                        if (t.startsWith("生成图片 ") || t.startsWith("生成视频 ")) {
                            sendText(talker, "AI 白名单未授权");
                            return;
                        }
                    }
                    if (imageGenEnabled && t.startsWith("生成图片 ")) {
                        String prompt = t.substring(5).trim();
                        if (prompt.isEmpty()) {
                            sendText(talker, "用法: 生成图片 <描述文字>");
                            return;
                        }
                        if (arkApiKey.isEmpty()) { sendText(talker, "请先配置火山方舟 API Key"); return; }
                        sendText(talker, "正在生成图片...");
                        final String fTalker = talker;
                        callArkImageApi(prompt, (url, errMsg) -> {
                            if (url != null && !url.isEmpty()) {
                                String path = cacheDir + "/arkImg_" + System.currentTimeMillis() + "." + arkImageFormat;
                                download(url, path, null, file -> {
                                    sendImage(fTalker, ((java.io.File) file).getAbsolutePath());
                                    saveToMediaDir((java.io.File) file);
                                });
                            } else {
                                String info = errMsg != null && !errMsg.isEmpty() ? "错误: " + errMsg : "请稍后重试";
                                if (info.length() > 200) info = info.substring(0, 200);
                                sendText(fTalker, "图片生成失败，" + info);
                            }
                        });
                        return;
                    }
                    if (videoGenEnabled && t.startsWith("生成视频 ")) {
                        String prompt = t.substring(5).trim();
                        if (prompt.isEmpty()) {
                            sendText(talker, "用法: 生成视频 <描述文字>");
                            return;
                        }
                        if (arkApiKey.isEmpty()) { sendText(talker, "请先配置火山方舟 API Key"); return; }
                        sendText(talker, "视频生成中，预计 1-5 分钟...");
                        createVideoTask(prompt, talker);
                        return;
                    }
                }
            }
        } catch (Exception e) {}
    }

    if (deepseekAtReply || deepseekSmartReply || deepseekTranslate || deepseekSummary || deepseekWriting || deepseekQA) {
        final Object mib = msgInfoBean;
        new Thread(new Runnable() {
            public void run() {
                handleDeepSeekMsg(mib);
            }
        }).start();
    }

    new Thread(new Runnable() {
        public void run() {
            try { handleMessageTypeKick(msgInfoBean); } catch (Throwable e) {}
            try { handleGroupManagement(msgInfoBean); } catch (Throwable e) {}
            try { handleMessageEnhancement(msgInfoBean); } catch (Throwable e) {}
        }
    }).start();

    int msgType = msgInfoBean.getType();
    log(": isText=" + msgInfoBean.isText() + "type=" + msgType + "talker=" + msgInfoBean.getTalker());
    if (!msgInfoBean.isText()) return;
}

void showDeepseekSettingsDialog() {
    android.app.Activity act = getTopActivity();
    if (act == null) return;
    android.content.Context c = act;

    android.widget.LinearLayout root = new android.widget.LinearLayout(c);
    root.setOrientation(1);
    root.setPadding(dp(14), dp(12), dp(14), dp(12));

    android.widget.TextView tvTitle = T(c, "第三方接口调用配置", 17, c("FF5B4C8C"), true);
    tvTitle.setGravity(android.view.Gravity.CENTER);
    tvTitle.setPadding(0, 0, 0, dp(8));
    root.addView(tvTitle);

    // === DeepSeek ===
    final android.widget.TextView tvDsStatus = T(c, deepseekApiKey.isEmpty() ? " 无效" : " 有效", 11,
        c(deepseekApiKey.isEmpty() ? "FFE53935" : "FF009955"), false);
    android.widget.LinearLayout dsLbl = new android.widget.LinearLayout(c);
    dsLbl.setOrientation(0);
    dsLbl.setGravity(android.view.Gravity.CENTER_VERTICAL);
    dsLbl.addView(T(c, "DeepSeek Key", 12, c("FF4A5568"), false));
    dsLbl.addView(tvDsStatus);
    root.addView(dsLbl);
    root.addView(SP(c, 2));

    final android.widget.EditText etDs = new android.widget.EditText(c);
    etDs.setText(deepseekApiKey);
    etDs.setHint("DeepSeek API Key");
    etDs.setHintTextColor(c("FFBBBBBB"));
    etDs.setTextSize(13);
    etDs.setTextColor(c("FF1E293B"));
    etDs.setSingleLine(true);
    etDs.setBackgroundDrawable(createInputBg());
    etDs.setPadding(dp(10), dp(8), dp(10), dp(8));
    root.addView(etDs);
    root.addView(SP(c, 2));

    final android.widget.EditText etDsUrl = new android.widget.EditText(c);
    etDsUrl.setText(deepseekBaseUrl);
    etDsUrl.setHint("API地址 (默认: https://api.deepseek.com/v1)");
    etDsUrl.setHintTextColor(c("FFBBBBBB"));
    etDsUrl.setTextSize(12);
    etDsUrl.setTextColor(c("FF1E293B"));
    etDsUrl.setSingleLine(true);
    etDsUrl.setBackgroundDrawable(createInputBg());
    etDsUrl.setPadding(dp(10), dp(8), dp(10), dp(8));
    root.addView(etDsUrl);
    root.addView(SP(c, 3));

    android.widget.LinearLayout dsBtns = new android.widget.LinearLayout(c);
    dsBtns.setOrientation(0);
    android.widget.Button btnDsSave = new android.widget.Button(c);
    btnDsSave.setText("保存");
    btnDsSave.setTextSize(11);
    btnDsSave.setAllCaps(false);
    btnDsSave.setBackgroundDrawable(createPrimaryBtnBg(4));
    btnDsSave.setTextColor(c("FF1E293B"));
    btnDsSave.setPadding(dp(12), dp(4), dp(12), dp(4));
    btnDsSave.setOnClickListener(new android.view.View.OnClickListener() {
        public void onClick(android.view.View v) {
            String key = etDs.getText().toString().trim();
            String baseUrl = etDsUrl.getText().toString().trim();
            deepseekApiKey = key;
            if (!baseUrl.isEmpty()) deepseekBaseUrl = baseUrl;
            saveAllConfig();
            tvDsStatus.setText(key.isEmpty() ? " 无效" : " 有效");
            tvDsStatus.setTextColor(c(key.isEmpty() ? "FFE53935" : "FF009955"));
            toast("DeepSeek Key 已保存");
        }
    });
    dsBtns.addView(btnDsSave);
    android.widget.Button btnDsCheck = new android.widget.Button(c);
    btnDsCheck.setText("检测");
    btnDsCheck.setTextSize(11);
    btnDsCheck.setAllCaps(false);
    btnDsCheck.setBackgroundDrawable(createGlassBtnBg(4, "00FFFFFF", "FF1E293B", "FF50A870"));
    btnDsCheck.setTextColor(c("FF1E293B"));
    btnDsCheck.setPadding(dp(10), dp(4), dp(10), dp(4));
    btnDsCheck.setOnClickListener(new android.view.View.OnClickListener() {
        public void onClick(android.view.View v) {
            String key = etDs.getText().toString().trim();
            if (key.isEmpty()) { toast("请先输入 DeepSeek Key"); return; }
            toast("正在检测 DeepSeek Key...");
            new Thread() {
                public void run() {
                    try {
                        java.net.URL u = new java.net.URL("https://api.deepseek.com/v1/chat/completions");
                        java.net.HttpURLConnection conn = (java.net.HttpURLConnection) u.openConnection();
                        conn.setRequestMethod("POST");
                        conn.setConnectTimeout(8000);
                        conn.setReadTimeout(8000);
                        conn.setDoOutput(true);
                        conn.setRequestProperty("Content-Type", "application/json");
                        conn.setRequestProperty("Authorization", "Bearer " + key);
                        java.util.Map m = new java.util.HashMap();
                        java.util.List msgs = new java.util.ArrayList();
                        java.util.Map msg = new java.util.HashMap();
                        msg.put("role", "user");
                        msg.put("content", "test");
                        msgs.add(msg);
                        m.put("model", "deepseek-chat");
                        m.put("messages", msgs);
                        m.put("max_tokens", 1);
                        String body = new org.json.JSONObject(m).toString();
                        java.io.OutputStream os = conn.getOutputStream();
                        os.write(body.getBytes("UTF-8")); os.flush(); os.close();
                        final int code = conn.getResponseCode();
                        if (code == 200) {
                            mainHandler.post(new Runnable() { public void run() {
                                tvDsStatus.setText(" 有效");
                                tvDsStatus.setTextColor(c("FF009955"));
                                toast("DeepSeek Key 检测通过");
                            }});
                        } else {
                            String errBody = "";
                            try {
                                java.io.InputStream es = conn.getErrorStream();
                                if (es != null) {
                                    java.io.BufferedReader ebr = new java.io.BufferedReader(new java.io.InputStreamReader(es, "UTF-8"));
                                    StringBuilder esb = new StringBuilder();
                                    String el;
                                    while ((el = ebr.readLine()) != null) esb.append(el);
                                    ebr.close();
                                    errBody = esb.toString();
                                    if (errBody.length() > 120) errBody = errBody.substring(0, 120);
                                }
                            } catch (Exception ignored) {}
                            final String fErr = errBody;
                            mainHandler.post(new Runnable() { public void run() {
                                tvDsStatus.setText(" 无效");
                                tvDsStatus.setTextColor(c("FFE53935"));
                                String tip = "DeepSeek Key 无效 (" + code + ")";
                                if (!fErr.isEmpty()) tip += " " + fErr;
                                toast(tip);
                            }});
                        }
                        conn.disconnect();
                    } catch (Exception e) {
                        mainHandler.post(new Runnable() { public void run() {
                            toast("检测失败: " + e.getMessage());
                        }});
                    }
                }
            }.start();
        }
    });
    dsBtns.addView(btnDsCheck);
    android.widget.Button btnDsModel = new android.widget.Button(c);
    btnDsModel.setText(deepseekModel.endsWith("pro") ? "v4-pro" : "v4-flash");
    btnDsModel.setTextSize(11);
    btnDsModel.setAllCaps(false);
    btnDsModel.setBackgroundDrawable(createOutlineBtnBg(4));
    btnDsModel.setTextColor(c("FF1E293B"));
    btnDsModel.setPadding(dp(10), dp(4), dp(10), dp(4));
    btnDsModel.setOnClickListener(new android.view.View.OnClickListener() {
        public void onClick(android.view.View v) {
            AlertDialog ad = new AlertDialog.Builder(c)
                .setTitle("选择模型")
                .setSingleChoiceItems(new String[]{"v4-pro", "v4-flash"},
                    "deepseek-v4-pro".equals(deepseekModel) ? 0 : 1,
                    new android.content.DialogInterface.OnClickListener() {
                        public void onClick(android.content.DialogInterface d, int w) {
                            deepseekModel = w == 0 ? "deepseek-v4-pro" : "deepseek-v4-flash";
                            btnDsModel.setText(w == 0 ? "v4-pro" : "v4-flash");
                            saveAllConfig();
                            toast("模型已切换: " + (w == 0 ? "v4-pro" : "v4-flash"));
                            d.dismiss();
                        }
                    })
                .create();
            ad.setOnShowListener(new android.content.DialogInterface.OnShowListener() {
                public void onShow(android.content.DialogInterface d) { setupUnifiedDialog(ad); }
            });
            ad.show();
        }
    });
    dsBtns.addView(btnDsModel);
    root.addView(dsBtns);
    root.addView(SP(c, 6));

    // === 火山方舟 ===
    root.addView(H(c("FFB0B8D0"), 1));
    root.addView(SP(c, dp(6)));
    final android.widget.TextView tvArkStatus = T(c, arkApiKey.isEmpty() ? " 无效" : " 有效", 11,
        c(arkApiKey.isEmpty() ? "FFE53935" : "FF009955"), false);
    android.widget.LinearLayout arkLbl = new android.widget.LinearLayout(c);
    arkLbl.setOrientation(0);
    arkLbl.setGravity(android.view.Gravity.CENTER_VERTICAL);
    arkLbl.addView(T(c, "火山方舟 Key（图片/视频）", 12, c("FF4A5568"), false));
    arkLbl.addView(tvArkStatus);
    root.addView(arkLbl);
    root.addView(SP(c, 2));

    final android.widget.EditText etArk = new android.widget.EditText(c);
    etArk.setText(arkApiKey);
    etArk.setHint("火山方舟 API Key");
    etArk.setHintTextColor(c("FFBBBBBB"));
    etArk.setTextSize(13);
    etArk.setTextColor(c("FF1E293B"));
    etArk.setSingleLine(true);
    etArk.setBackgroundDrawable(createInputBg());
    etArk.setPadding(dp(10), dp(8), dp(10), dp(8));
    root.addView(etArk);
    root.addView(SP(c, 3));

    android.widget.LinearLayout arkBtns = new android.widget.LinearLayout(c);
    arkBtns.setOrientation(0);
    android.widget.Button btnArkSave = new android.widget.Button(c);
    btnArkSave.setText("保存");
    btnArkSave.setTextSize(11);
    btnArkSave.setAllCaps(false);
    btnArkSave.setBackgroundDrawable(createPrimaryBtnBg(4));
    btnArkSave.setTextColor(c("FF1E293B"));
    btnArkSave.setPadding(dp(12), dp(4), dp(12), dp(4));
    btnArkSave.setOnClickListener(new android.view.View.OnClickListener() {
        public void onClick(android.view.View v) {
            String key = etArk.getText().toString().trim();
            arkApiKey = key;
            saveAllConfig();
            tvArkStatus.setText(key.isEmpty() ? " 无效" : " 有效");
            tvArkStatus.setTextColor(c(key.isEmpty() ? "FFE53935" : "FF009955"));
            toast("火山方舟 Key 已保存");
        }
    });
    arkBtns.addView(btnArkSave);
    android.widget.Button btnArkCheck = new android.widget.Button(c);
    btnArkCheck.setText("检测");
    btnArkCheck.setTextSize(11);
    btnArkCheck.setAllCaps(false);
    btnArkCheck.setBackgroundDrawable(createGlassBtnBg(4, "00FFFFFF", "FF1E293B", "FF50A870"));
    btnArkCheck.setTextColor(c("FF1E293B"));
    btnArkCheck.setPadding(dp(10), dp(4), dp(10), dp(4));
    btnArkCheck.setOnClickListener(new android.view.View.OnClickListener() {
        public void onClick(android.view.View v) {
            String key = etArk.getText().toString().trim();
            if (key.isEmpty()) { toast("请先输入火山方舟 Key"); return; }
            toast("正在检测火山方舟 Key...");
            new Thread() {
                public void run() {
                    try {
                        java.net.URL u = new java.net.URL("https://ark.cn-beijing.volces.com/api/v3/models");
                        java.net.HttpURLConnection conn = (java.net.HttpURLConnection) u.openConnection();
                        conn.setRequestMethod("GET");
                        conn.setConnectTimeout(8000);
                        conn.setReadTimeout(8000);
                        conn.setRequestProperty("Authorization", "Bearer " + key);
                        final int code = conn.getResponseCode();
                        conn.disconnect();
                        if (code == 200 || code == 400 || code == 403) {
                            mainHandler.post(new Runnable() { public void run() {
                                tvArkStatus.setText(" 有效");
                                tvArkStatus.setTextColor(c("FF009955"));
                                toast("火山方舟 Key 检测通过" + (code == 403 ? " (端点限制)" : ""));
                            }});
                        } else {
                            mainHandler.post(new Runnable() { public void run() {
                                tvArkStatus.setText(" 无效");
                                tvArkStatus.setTextColor(c("FFE53935"));
                                toast("火山方舟 Key 无效 (" + code + ")");
                            }});
                        }
                    } catch (Exception e) {
                        mainHandler.post(new Runnable() { public void run() {
                            toast("检测失败: " + e.getMessage());
                        }});
                    }
                }
            }.start();
        }
    });
    arkBtns.addView(btnArkCheck);
    root.addView(arkBtns);
    root.addView(SP(c, 6));

    // === 底部按钮栏 ===
    android.widget.LinearLayout btnRow = new android.widget.LinearLayout(c);
    btnRow.setOrientation(0);
    btnRow.setGravity(android.view.Gravity.CENTER_VERTICAL);
    btnRow.setLayoutParams(new android.widget.LinearLayout.LayoutParams(-1, -2));

    android.widget.Button btnSaveDs = new android.widget.Button(c);
    btnSaveDs.setText("保存配置");
    btnSaveDs.setTextSize(13);
    btnSaveDs.setAllCaps(false);
    btnSaveDs.setBackgroundDrawable(createPrimaryBtnBg(8));
    btnSaveDs.setTextColor(c("FF1E293B"));
    btnSaveDs.setPadding(dp(18), dp(6), dp(18), dp(6));
    btnRow.addView(btnSaveDs);

    android.widget.LinearLayout.LayoutParams spacerMid = new android.widget.LinearLayout.LayoutParams(0, 1, 1);
    btnRow.addView(SP(c, 0), spacerMid);

    android.widget.Button btnClose = new android.widget.Button(c);
    btnClose.setText("返回");
    btnClose.setTextSize(13);
    btnClose.setAllCaps(false);
    btnClose.setBackgroundDrawable(createOutlineBtnBg(8));
    btnClose.setTextColor(c("FF1E293B"));
    btnClose.setPadding(dp(18), dp(6), dp(18), dp(6));
    btnRow.addView(btnClose);
    root.addView(btnRow);

    root.addView(SP(c, dp(6)));
    android.widget.TextView dsDesc = T(c, "配置 DeepSeek 和火山方舟的 API Key。\nDeepSeek Key 用于 AI 对话功能，方舟 Key 用于图片/视频生成。", 10, c("FF999999"), false);
    dsDesc.setGravity(android.view.Gravity.CENTER);
    root.addView(dsDesc);

    final android.app.Dialog dlg = MD(c, root, 0.88, 0);
    btnSaveDs.setOnClickListener(new android.view.View.OnClickListener() {
        public void onClick(android.view.View v) {
            deepseekApiKey = etDs.getText().toString().trim();
            saveAllConfig();
            toast("DeepSeek设置已保存");
            safeDismiss(dlg);
        }
    });
    btnClose.setOnClickListener(new android.view.View.OnClickListener() {
        public void onClick(android.view.View v) { safeDismiss(dlg); }
    });
    dlg.show();
}

void showAIChatSettings() {
    android.app.Activity act = getTopActivity();
    if (act == null) return;
    android.content.Context c = act;
    loadConfig();

    final boolean[] localAtReply = {deepseekAtReply};
    final boolean[] localSmartReply = {deepseekSmartReply};
    final boolean[] localTranslate = {deepseekTranslate};
    final boolean[] localSummary = {deepseekSummary};
    final boolean[] localWriting = {deepseekWriting};
    final boolean[] localQA = {deepseekQA};

    android.widget.LinearLayout root = new android.widget.LinearLayout(c);
    root.setOrientation(1);
    root.setPadding(dp(14), dp(12), dp(14), dp(12));

    android.widget.TextView tvTitle = T(c, "AI 智能对话设置", 17, c("FF5B4C8C"), true);
    tvTitle.setGravity(android.view.Gravity.CENTER);
    tvTitle.setPadding(0, 0, 0, dp(8));
    root.addView(tvTitle);

    // --- AI人设编辑 ---
    android.widget.LinearLayout personaRow = new android.widget.LinearLayout(c);
    personaRow.setOrientation(0);
    personaRow.setGravity(android.view.Gravity.CENTER_VERTICAL);
    personaRow.setPadding(dp(8), dp(10), dp(8), dp(10));
    personaRow.setBackgroundDrawable(createCardBg(8));

    android.widget.LinearLayout pcol = new android.widget.LinearLayout(c);
    pcol.setOrientation(1);
    pcol.setGravity(android.view.Gravity.CENTER);
    pcol.setLayoutParams(new android.widget.LinearLayout.LayoutParams(0, -2, 1));
    pcol.addView(T(c, "AI 人设编辑", 14, c("FF1E293B"), true));
    String pv = deepseekPersona.length() > 30 ? deepseekPersona.substring(0, 30) + "..." : deepseekPersona;
    pcol.addView(T(c, pv, 10, c("FF6B7A8C"), false));
    personaRow.addView(pcol);

    android.widget.Button btnPersona = new android.widget.Button(c);
    btnPersona.setText("编辑");
    btnPersona.setTextSize(11);
    btnPersona.setAllCaps(false);
    btnPersona.setBackgroundDrawable(createPrimaryBtnBg(6));
    btnPersona.setTextColor(c("FF1E293B"));
    btnPersona.setPadding(dp(12), dp(4), dp(12), dp(4));
    btnPersona.setOnClickListener(new android.view.View.OnClickListener() {
        public void onClick(android.view.View v) { showPersonaEditor(); }
    });
    personaRow.addView(btnPersona);
    root.addView(personaRow);
    root.addView(SP(c, dp(6)));

    // --- AI功能开关 ---
    root.addView(H(c("FFB0B8D0"), 1));
    root.addView(SP(c, dp(6)));

    for (int i = 0; i < 6; i++) {
        String label;
        String hint;
        final boolean[] ref;
        final CompoundButton.OnCheckedChangeListener listener;

        if (i == 0) {
            label = "@回复";
            hint = "被@时自动用AI回复";
            ref = localAtReply;
            listener = new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton btn, boolean checked) {
                    localAtReply[0] = checked;
                    deepseekAtReply = checked;
                    saveDeepseekConfig();
                }
            };
        } else if (i == 1) {
            label = "智能聊天";
            hint = "私聊/群聊中的智能对话";
            ref = localSmartReply;
            listener = new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton btn, boolean checked) {
                    localSmartReply[0] = checked;
                    deepseekSmartReply = checked;
                    saveDeepseekConfig();
                }
            };
        } else if (i == 2) {
            label = "翻译";
            hint = "自动翻译外语消息";
            ref = localTranslate;
            listener = new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton btn, boolean checked) {
                    localTranslate[0] = checked;
                    deepseekTranslate = checked;
                    saveDeepseekConfig();
                }
            };
        } else if (i == 3) {
            label = "摘要";
            hint = "长消息智能摘要";
            ref = localSummary;
            listener = new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton btn, boolean checked) {
                    localSummary[0] = checked;
                    deepseekSummary = checked;
                    saveDeepseekConfig();
                }
            };
        } else if (i == 4) {
            label = "写作";
            hint = "AI辅助写作";
            ref = localWriting;
            listener = new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton btn, boolean checked) {
                    localWriting[0] = checked;
                    deepseekWriting = checked;
                    saveDeepseekConfig();
                }
            };
        } else {
            label = "问答";
            hint = "通用AI问答";
            ref = localQA;
            listener = new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton btn, boolean checked) {
                    localQA[0] = checked;
                    deepseekQA = checked;
                    saveDeepseekConfig();
                }
            };
        }

        android.widget.LinearLayout row = new android.widget.LinearLayout(c);
        row.setOrientation(0);
        row.setLayoutParams(new android.widget.LinearLayout.LayoutParams(-1, -2));
        row.setGravity(android.view.Gravity.CENTER_VERTICAL);
        row.setPadding(0, dp(4), 0, dp(4));

        android.widget.LinearLayout col = new android.widget.LinearLayout(c);
        col.setOrientation(1);
        col.setGravity(android.view.Gravity.CENTER);
        col.setLayoutParams(new android.widget.LinearLayout.LayoutParams(0, -2, 1));
        col.addView(T(c, label, 13, c("FF1E293B"), true));
        android.widget.TextView tvHint = T(c, hint, 10, c("FF6B7A8C"), false);
        tvHint.setPadding(0, dp(1), 0, 0);
        col.addView(tvHint);
        row.addView(col);

        Switch sw = new Switch(c);
        sw.setChecked(ref[0]);
        styleSwitch(sw);
        sw.setOnCheckedChangeListener(listener);
        row.addView(sw);
        root.addView(row);
        if (i < 5) root.addView(H(c("FFB0B8D0"), 1));
    }

    root.addView(SP(c, dp(10)));

    // 图片生成开关
    final boolean[] localImageGen = {imageGenEnabled};
    android.widget.LinearLayout imgSwitchRow = new android.widget.LinearLayout(c);
    imgSwitchRow.setOrientation(0);
    imgSwitchRow.setGravity(android.view.Gravity.CENTER_VERTICAL);
    imgSwitchRow.setPadding(0, dp(4), 0, dp(4));
    android.widget.LinearLayout imgLabelCol = new android.widget.LinearLayout(c);
    imgLabelCol.setOrientation(1);
    imgLabelCol.setLayoutParams(new android.widget.LinearLayout.LayoutParams(0, -2, 1));
    imgLabelCol.addView(T(c, "图片生成", 13, c("FF1E293B"), true));
    imgLabelCol.addView(T(c, "发送「生成图片 xxx」指令生成图片", 10, c("FF6B7A8C"), false));
    imgSwitchRow.addView(imgLabelCol);
    Switch swImageGen = new Switch(c);
    swImageGen.setChecked(imageGenEnabled);
    styleSwitch(swImageGen);
    swImageGen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton btn, boolean checked) {
            localImageGen[0] = checked;
            imageGenEnabled = checked;
            saveAllConfig();
        }
    });
    imgSwitchRow.addView(swImageGen);
    root.addView(imgSwitchRow);

    // 图片模型选择
    final String[] imgModels = {"doubao-seedream-4-5-251128", "doubao-seedream-5-0-pro-260628", "doubao-seedream-5-0-lite-260628"};
    final String[] imgModelNames = {"Seedream 4.5", "Seedream 5.0 Pro", "Seedream 5.0 Lite"};
    final String[] imgSizes = {"1K", "2K", "4K"};
    final String[] imgFormats = {"png", "jpeg"};
    final int[] imgModelIdx = {0};
    final int[] imgSizeIdx = {1};
    final int[] imgFormatIdx = {0};
    for (int j = 0; j < imgModels.length; j++) { if (imgModels[j].equals(arkImageModel)) imgModelIdx[0] = j; }
    for (int j = 0; j < imgSizes.length; j++) { if (imgSizes[j].equals(arkImageSize)) imgSizeIdx[0] = j; }
    for (int j = 0; j < imgFormats.length; j++) { if (imgFormats[j].equals(arkImageFormat)) imgFormatIdx[0] = j; }

    android.widget.LinearLayout imgOptRow = new android.widget.LinearLayout(c);
    imgOptRow.setOrientation(0);
    imgOptRow.setGravity(android.view.Gravity.CENTER);
    imgOptRow.setPadding(0, dp(2), 0, dp(2));

    android.widget.Button btnImgModel = new android.widget.Button(c);
    btnImgModel.setText("模型: " + imgModelNames[imgModelIdx[0]]);
    btnImgModel.setTextSize(10);
    btnImgModel.setAllCaps(false);
    btnImgModel.setBackgroundDrawable(createOutlineBtnBg(4));
    btnImgModel.setTextColor(c("FF5B4C8C"));
    btnImgModel.setPadding(dp(6), dp(2), dp(6), dp(2));
    btnImgModel.setOnClickListener(new android.view.View.OnClickListener() { public void onClick(android.view.View v) {
        android.app.AlertDialog.Builder ab = new android.app.AlertDialog.Builder(c);
        ab.setTitle("选择图片模型");
        ab.setItems(imgModelNames, new android.content.DialogInterface.OnClickListener() { public void onClick(android.content.DialogInterface d, int w) {
            imgModelIdx[0] = w;
            arkImageModel = imgModels[w];
            btnImgModel.setText("模型: " + imgModelNames[w]);
            saveAllConfig();
        }});
        AlertDialog ad = ab.create();
        ad.setOnShowListener(new android.content.DialogInterface.OnShowListener() { public void onShow(android.content.DialogInterface d) { setupUnifiedDialog(ad); } });
        ad.show();
    }});
    imgOptRow.addView(btnImgModel);
    imgOptRow.addView(SP(c, dp(4)));

    android.widget.Button btnImgSize = new android.widget.Button(c);
    btnImgSize.setText(imgSizes[imgSizeIdx[0]]);
    btnImgSize.setTextSize(10);
    btnImgSize.setAllCaps(false);
    btnImgSize.setBackgroundDrawable(createOutlineBtnBg(4));
    btnImgSize.setTextColor(c("FF5B4C8C"));
    btnImgSize.setPadding(dp(6), dp(2), dp(6), dp(2));
    btnImgSize.setOnClickListener(new android.view.View.OnClickListener() { public void onClick(android.view.View v) {
        android.app.AlertDialog.Builder ab = new android.app.AlertDialog.Builder(c);
        ab.setTitle("选择图片分辨率");
        ab.setItems(imgSizes, new android.content.DialogInterface.OnClickListener() { public void onClick(android.content.DialogInterface d, int w) {
            imgSizeIdx[0] = w;
            arkImageSize = imgSizes[w];
            btnImgSize.setText(imgSizes[w]);
            saveAllConfig();
        }});
        AlertDialog ad = ab.create();
        ad.setOnShowListener(new android.content.DialogInterface.OnShowListener() { public void onShow(android.content.DialogInterface d) { setupUnifiedDialog(ad); } });
        ad.show();
    }});
    imgOptRow.addView(btnImgSize);
    imgOptRow.addView(SP(c, dp(4)));

    android.widget.Button btnImgFormat = new android.widget.Button(c);
    btnImgFormat.setText(imgFormats[imgFormatIdx[0]].toUpperCase());
    btnImgFormat.setTextSize(10);
    btnImgFormat.setAllCaps(false);
    btnImgFormat.setBackgroundDrawable(createOutlineBtnBg(4));
    btnImgFormat.setTextColor(c("FF5B4C8C"));
    btnImgFormat.setPadding(dp(6), dp(2), dp(6), dp(2));
    btnImgFormat.setOnClickListener(new android.view.View.OnClickListener() { public void onClick(android.view.View v) {
        android.app.AlertDialog.Builder ab = new android.app.AlertDialog.Builder(c);
        ab.setTitle("选择图片格式");
        ab.setItems(new String[]{"PNG", "JPEG"}, new android.content.DialogInterface.OnClickListener() { public void onClick(android.content.DialogInterface d, int w) {
            imgFormatIdx[0] = w;
            arkImageFormat = imgFormats[w];
            btnImgFormat.setText(imgFormats[w].toUpperCase());
            saveAllConfig();
        }});
        AlertDialog ad = ab.create();
        ad.setOnShowListener(new android.content.DialogInterface.OnShowListener() { public void onShow(android.content.DialogInterface d) { setupUnifiedDialog(ad); } });
        ad.show();
    }});
    imgOptRow.addView(btnImgFormat);
    root.addView(imgOptRow);
    root.addView(SP(c, dp(6)));

    // 视频生成开关
    final boolean[] localVideoGen = {videoGenEnabled};
    android.widget.LinearLayout vidSwitchRow = new android.widget.LinearLayout(c);
    vidSwitchRow.setOrientation(0);
    vidSwitchRow.setGravity(android.view.Gravity.CENTER_VERTICAL);
    vidSwitchRow.setPadding(0, dp(4), 0, dp(4));
    android.widget.LinearLayout vidLabelCol = new android.widget.LinearLayout(c);
    vidLabelCol.setOrientation(1);
    vidLabelCol.setLayoutParams(new android.widget.LinearLayout.LayoutParams(0, -2, 1));
    vidLabelCol.addView(T(c, "视频生成", 13, c("FF1E293B"), true));
    vidLabelCol.addView(T(c, "发送「生成视频 xxx」指令生成视频", 10, c("FF6B7A8C"), false));
    vidSwitchRow.addView(vidLabelCol);
    Switch swVideoGen = new Switch(c);
    swVideoGen.setChecked(videoGenEnabled);
    styleSwitch(swVideoGen);
    swVideoGen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton btn, boolean checked) {
            localVideoGen[0] = checked;
            videoGenEnabled = checked;
            saveAllConfig();
        }
    });
    vidSwitchRow.addView(swVideoGen);
    root.addView(vidSwitchRow);

    // 视频模型/时长/分辨率选择
    final String[] vidModels = {"doubao-seedance-2-0-260128", "doubao-seedance-2-0-fast-260128"};
    final String[] vidModelNames = {"Seedance 2.0", "Seedance 2.0 Fast"};
    final String[] vidResolutions = {"480p", "720p", "1080p"};
    final String[] vidDurations = {"4", "5", "6", "8", "10", "12", "15"};
    final int[] vidModelIdx = {0};
    final int[] vidDurationIdx = {0};
    final int[] vidResIdx = {1};
    for (int j = 0; j < vidModels.length; j++) { if (vidModels[j].equals(arkVideoModel)) vidModelIdx[0] = j; }
    for (int j = 0; j < vidDurations.length; j++) { if (vidDurations[j].equals(String.valueOf(arkVideoDuration))) vidDurationIdx[0] = j; }
    for (int j = 0; j < vidResolutions.length; j++) { if (vidResolutions[j].equals(arkVideoResolution)) vidResIdx[0] = j; }

    android.widget.LinearLayout vidOptRow = new android.widget.LinearLayout(c);
    vidOptRow.setOrientation(0);
    vidOptRow.setGravity(android.view.Gravity.CENTER);
    vidOptRow.setPadding(0, dp(2), 0, dp(2));

    android.widget.Button btnVidModel = new android.widget.Button(c);
    btnVidModel.setText("模型: " + vidModelNames[vidModelIdx[0]]);
    btnVidModel.setTextSize(10);
    btnVidModel.setAllCaps(false);
    btnVidModel.setBackgroundDrawable(createOutlineBtnBg(4));
    btnVidModel.setTextColor(c("FFD06068"));
    btnVidModel.setPadding(dp(6), dp(2), dp(6), dp(2));
    btnVidModel.setOnClickListener(new android.view.View.OnClickListener() { public void onClick(android.view.View v) {
        android.app.AlertDialog.Builder ab = new android.app.AlertDialog.Builder(c);
        ab.setTitle("选择视频模型");
        ab.setItems(vidModelNames, new android.content.DialogInterface.OnClickListener() { public void onClick(android.content.DialogInterface d, int w) {
            vidModelIdx[0] = w;
            arkVideoModel = vidModels[w];
            btnVidModel.setText("模型: " + vidModelNames[w]);
            saveAllConfig();
        }});
        AlertDialog ad = ab.create();
        ad.setOnShowListener(new android.content.DialogInterface.OnShowListener() { public void onShow(android.content.DialogInterface d) { setupUnifiedDialog(ad); } });
        ad.show();
    }});
    vidOptRow.addView(btnVidModel);
    vidOptRow.addView(SP(c, dp(4)));

    android.widget.Button btnVidDuration = new android.widget.Button(c);
    btnVidDuration.setText(vidDurations[vidDurationIdx[0]] + "s");
    btnVidDuration.setTextSize(10);
    btnVidDuration.setAllCaps(false);
    btnVidDuration.setBackgroundDrawable(createOutlineBtnBg(4));
    btnVidDuration.setTextColor(c("FFD06068"));
    btnVidDuration.setPadding(dp(6), dp(2), dp(6), dp(2));
    btnVidDuration.setOnClickListener(new android.view.View.OnClickListener() { public void onClick(android.view.View v) {
        android.app.AlertDialog.Builder ab = new android.app.AlertDialog.Builder(c);
        ab.setTitle("选择视频时长");
        ab.setItems(new String[]{"4 秒", "5 秒", "6 秒", "8 秒", "10 秒", "12 秒", "15 秒"}, new android.content.DialogInterface.OnClickListener() { public void onClick(android.content.DialogInterface d, int w) {
            vidDurationIdx[0] = w;
            arkVideoDuration = Integer.parseInt(vidDurations[w]);
            btnVidDuration.setText(vidDurations[w] + "s");
            saveAllConfig();
        }});
        AlertDialog ad = ab.create();
        ad.setOnShowListener(new android.content.DialogInterface.OnShowListener() { public void onShow(android.content.DialogInterface d) { setupUnifiedDialog(ad); } });
        ad.show();
    }});
    vidOptRow.addView(btnVidDuration);
    vidOptRow.addView(SP(c, dp(4)));

    android.widget.Button btnVidRes = new android.widget.Button(c);
    btnVidRes.setText(vidResolutions[vidResIdx[0]]);
    btnVidRes.setTextSize(10);
    btnVidRes.setAllCaps(false);
    btnVidRes.setBackgroundDrawable(createOutlineBtnBg(4));
    btnVidRes.setTextColor(c("FFD06068"));
    btnVidRes.setPadding(dp(6), dp(2), dp(6), dp(2));
    btnVidRes.setOnClickListener(new android.view.View.OnClickListener() { public void onClick(android.view.View v) {
        android.app.AlertDialog.Builder ab = new android.app.AlertDialog.Builder(c);
        ab.setTitle("选择视频分辨率");
        ab.setItems(vidResolutions, new android.content.DialogInterface.OnClickListener() { public void onClick(android.content.DialogInterface d, int w) {
            vidResIdx[0] = w;
            arkVideoResolution = vidResolutions[w];
            btnVidRes.setText(vidResolutions[w]);
            saveAllConfig();
        }});
        AlertDialog ad = ab.create();
        ad.setOnShowListener(new android.content.DialogInterface.OnShowListener() { public void onShow(android.content.DialogInterface d) { setupUnifiedDialog(ad); } });
        ad.show();
    }});
    vidOptRow.addView(btnVidRes);
    root.addView(vidOptRow);
    root.addView(SP(c, dp(6)));

    // --- 保存按钮 ---

    android.widget.LinearLayout btnRow = new android.widget.LinearLayout(c);
    btnRow.setOrientation(0);
    btnRow.setLayoutParams(new android.widget.LinearLayout.LayoutParams(-1, -2));

    android.widget.Button btnSave = new android.widget.Button(c);
    btnSave.setText("保存配置");
    btnSave.setTextSize(13);
    btnSave.setAllCaps(false);
    btnSave.setBackgroundDrawable(createPrimaryBtnBg(8));
    btnSave.setTextColor(c("FF1E293B"));
    btnSave.setPadding(dp(18), dp(6), dp(18), dp(6));
    btnRow.addView(btnSave);

    android.widget.LinearLayout.LayoutParams spMid = new android.widget.LinearLayout.LayoutParams(0, 1, 1);
    btnRow.addView(new android.view.View(c), spMid);

    android.widget.Button btnBack = new android.widget.Button(c);
    btnBack.setText("返回");
    btnBack.setTextSize(13);
    btnBack.setAllCaps(false);
    btnBack.setBackgroundDrawable(createOutlineBtnBg(8));
    btnBack.setTextColor(c("FF1E293B"));
    btnBack.setPadding(dp(18), dp(6), dp(18), dp(6));
    btnRow.addView(btnBack);
    root.addView(btnRow);

    root.addView(SP(c, dp(6)));
    android.widget.TextView desc = T(c, "需要先配置有效的 DeepSeek API Key 方可使用。\n各功能独立开关，按需启用。", 10, c("FF999999"), false);
    desc.setGravity(android.view.Gravity.CENTER);
    root.addView(desc);

    final android.app.Dialog dlg = MD(c, root, 0.92, 0);
    btnSave.setOnClickListener(new android.view.View.OnClickListener() {
        public void onClick(android.view.View v) {
            saveDeepseekConfig();
            saveAllConfig();
            toast("AI功能开关已保存");
            safeDismiss(dlg);
        }
    });
    btnBack.setOnClickListener(new android.view.View.OnClickListener() {
        public void onClick(android.view.View v) { safeDismiss(dlg); }
    });
    dlg.show();
}

String getAiFeatureSummary() {
    StringBuilder sb = new StringBuilder();
    if (deepseekAtReply) sb.append("@回复 ");
    if (deepseekSmartReply) sb.append("智能聊天 ");
    if (deepseekTranslate) sb.append("翻译 ");
    if (deepseekSummary) sb.append("摘要 ");
    if (deepseekWriting) sb.append("写作 ");
    if (deepseekQA) sb.append("问答");
    if (sb.length() == 0) sb.append("全部关闭");
    return sb.toString();
}

void showAiWhitelistDialog() {
    try {
        android.app.Activity act = getTopActivity();
        if (act == null) {
            toast("请先打开微信主界面");
            return;
        }
        android.content.Context c = act;
        openAiWhitelistDialog(c, null, null);
    } catch (Exception e) {
        toast("白名单加载失败: " + e.getMessage());
    }
}

void showAIFeatureSwitches() {
    android.app.Activity act = getTopActivity();
    if (act == null) return;
    android.content.Context c = act;
    loadConfig();

    final boolean[] localAtReply = {deepseekAtReply};
    final boolean[] localSmartReply = {deepseekSmartReply};
    final boolean[] localTranslate = {deepseekTranslate};
    final boolean[] localSummary = {deepseekSummary};
    final boolean[] localWriting = {deepseekWriting};
    final boolean[] localQA = {deepseekQA};

    android.widget.LinearLayout root = new android.widget.LinearLayout(c);
    root.setOrientation(1);
    root.setPadding(dp(14), dp(12), dp(14), dp(12));

    root.addView(T(c, "AI 功能开关", 17, c("FF5B4C8C"), true));
    root.addView(SP(c, dp(8)));

    for (int i = 0; i < 6; i++) {
        String label;
        String hint;
        final boolean[] ref;
        final CompoundButton.OnCheckedChangeListener listener;

        if (i == 0) {
            label = "@回复";
            hint = "被@时自动用AI回复";
            ref = localAtReply;
            listener = new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton btn, boolean checked) {
                    localAtReply[0] = checked;
                    deepseekAtReply = checked;
                    saveDeepseekConfig();
                }
            };
        } else if (i == 1) {
            label = "智能聊天";
            hint = "私聊/群聊中的智能对话";
            ref = localSmartReply;
            listener = new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton btn, boolean checked) {
                    localSmartReply[0] = checked;
                    deepseekSmartReply = checked;
                    saveDeepseekConfig();
                }
            };
        } else if (i == 2) {
            label = "翻译";
            hint = "自动翻译外语消息";
            ref = localTranslate;
            listener = new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton btn, boolean checked) {
                    localTranslate[0] = checked;
                    deepseekTranslate = checked;
                    saveDeepseekConfig();
                }
            };
        } else if (i == 3) {
            label = "摘要";
            hint = "长消息智能摘要";
            ref = localSummary;
            listener = new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton btn, boolean checked) {
                    localSummary[0] = checked;
                    deepseekSummary = checked;
                    saveDeepseekConfig();
                }
            };
        } else if (i == 4) {
            label = "写作";
            hint = "AI辅助写作";
            ref = localWriting;
            listener = new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton btn, boolean checked) {
                    localWriting[0] = checked;
                    deepseekWriting = checked;
                    saveDeepseekConfig();
                }
            };
        } else {
            label = "问答";
            hint = "通用AI问答";
            ref = localQA;
            listener = new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton btn, boolean checked) {
                    localQA[0] = checked;
                    deepseekQA = checked;
                    saveDeepseekConfig();
                }
            };
        }

        android.widget.LinearLayout row = new android.widget.LinearLayout(c);
        row.setOrientation(0);
        row.setLayoutParams(new android.widget.LinearLayout.LayoutParams(-1, -2));
        row.setGravity(android.view.Gravity.CENTER_VERTICAL);
        row.setPadding(0, dp(4), 0, dp(4));

        android.widget.LinearLayout col = new android.widget.LinearLayout(c);
        col.setOrientation(1);
        col.setGravity(android.view.Gravity.CENTER);
        col.setLayoutParams(new android.widget.LinearLayout.LayoutParams(0, -2, 1));
        col.addView(T(c, label, 13, c("FF1E293B"), true));
        android.widget.TextView tvHint = T(c, hint, 10, c("FF6B7A8C"), false);
        tvHint.setPadding(0, dp(1), 0, 0);
        col.addView(tvHint);
        row.addView(col);

        Switch sw = new Switch(c);
        sw.setChecked(ref[0]);
        styleSwitch(sw);
        sw.setOnCheckedChangeListener(listener);
        row.addView(sw);
        root.addView(row);
        if (i < 5) root.addView(H(c("FFB0B8D0"), 1));
    }

    root.addView(SP(c, dp(10)));

    android.widget.LinearLayout btnRow = new android.widget.LinearLayout(c);
    btnRow.setOrientation(0);
    btnRow.setGravity(android.view.Gravity.CENTER_VERTICAL);
    btnRow.setLayoutParams(new android.widget.LinearLayout.LayoutParams(-1, -2));

    android.widget.Button btnSave = new android.widget.Button(c);
    btnSave.setText("保存配置");
    btnSave.setTextSize(13);
    btnSave.setAllCaps(false);
    btnSave.setBackgroundDrawable(createPrimaryBtnBg(8));
    btnSave.setTextColor(c("FF1E293B"));
    btnSave.setPadding(dp(18), dp(6), dp(18), dp(6));
    btnRow.addView(btnSave);

    android.widget.LinearLayout.LayoutParams spMid = new android.widget.LinearLayout.LayoutParams(0, 1, 1);
    btnRow.addView(new android.view.View(c), spMid);

    android.widget.Button btnBack = new android.widget.Button(c);
    btnBack.setText("返回");
    btnBack.setTextSize(13);
    btnBack.setAllCaps(false);
    btnBack.setBackgroundDrawable(createOutlineBtnBg(8));
    btnBack.setTextColor(c("FF1E293B"));
    btnBack.setPadding(dp(18), dp(6), dp(18), dp(6));
    btnRow.addView(btnBack);
    root.addView(btnRow);

    root.addView(SP(c, dp(8)));
    root.addView(H(c("FFB0B8D0"), 1));
    root.addView(SP(c, dp(6)));
    android.widget.TextView desc2 = T(c, "需要先配置有效的 DeepSeek API Key 方可使用。各功能独立开关，按需启用。", 10, c("FF999999"), false);
    desc2.setGravity(android.view.Gravity.CENTER);
    root.addView(desc2);
    dlg.show();
}

void showPersonaEditor() {
    android.app.Activity act = getTopActivity();
    if (act == null) return;
    android.content.Context c = act;
    loadConfig();
    final String[] localPersona = {deepseekPersona};

    android.widget.LinearLayout root = new android.widget.LinearLayout(c);
    root.setOrientation(1);
    root.setPadding(dp(14), dp(12), dp(14), dp(12));

    root.addView(T(c, "AI 人设编辑", 17, c("FF5B4C8C"), true));
    root.addView(SP(c, dp(8)));

    root.addView(T(c, "System Prompt（描述AI的行为风格和身份）", 11, c("FF4A5568"), false));
    root.addView(SP(c, dp(4)));
    final android.widget.EditText etPersona = new android.widget.EditText(c);
    etPersona.setText(localPersona[0]);
    etPersona.setHint("你是一个友好的微信助手，请用简洁自然的语言回复。");
    etPersona.setHintTextColor(c("FFBBBBBB"));
    etPersona.setTextSize(12);
    etPersona.setTextColor(c("FF1E293B"));
    etPersona.setMaxLines(6);
    etPersona.setMinLines(3);
    etPersona.setBackgroundDrawable(createInputBg());
    etPersona.setPadding(dp(10), dp(8), dp(10), dp(8));
    etPersona.setGravity(android.view.Gravity.TOP);
    root.addView(etPersona);

    root.addView(SP(c, dp(10)));

    android.widget.LinearLayout btnRow = new android.widget.LinearLayout(c);
    btnRow.setOrientation(0);
    btnRow.setLayoutParams(new android.widget.LinearLayout.LayoutParams(-1, -2));

    android.widget.Button btnReset = new android.widget.Button(c);
    btnReset.setText("重置默认");
    btnReset.setTextSize(12);
    btnReset.setAllCaps(false);
    btnReset.setBackgroundDrawable(createGlassBtnBg(4, "00FFFFFF", "CC666666", "CC666666"));
    btnReset.setTextColor(c("FF666666"));
    btnReset.setPadding(dp(14), dp(6), dp(14), dp(6));
    btnRow.addView(btnReset);

    android.widget.LinearLayout.LayoutParams spacer1 = new android.widget.LinearLayout.LayoutParams(0, 1, 1);
    btnRow.addView(new android.view.View(c), spacer1);

    android.widget.Button btnSave = new android.widget.Button(c);
    btnSave.setText("保存配置");
    btnSave.setTextSize(14);
    btnSave.setAllCaps(false);
    btnSave.setBackgroundDrawable(createPrimaryBtnBg(8));
    btnSave.setTextColor(c("FF1E293B"));
    btnSave.setPadding(dp(28), dp(8), dp(28), dp(8));
    btnRow.addView(btnSave);

    android.widget.LinearLayout.LayoutParams spacer2 = new android.widget.LinearLayout.LayoutParams(0, 1, 1);
    btnRow.addView(new android.view.View(c), spacer2);

    android.widget.Button btnBack = new android.widget.Button(c);
    btnBack.setText("返回");
    btnBack.setTextSize(12);
    btnBack.setAllCaps(false);
    btnBack.setBackgroundDrawable(createOutlineBtnBg(8));
    btnBack.setTextColor(c("FF1E293B"));
    btnBack.setPadding(dp(14), dp(6), dp(14), dp(6));
    btnRow.addView(btnBack);
    root.addView(btnRow);

    final android.app.Dialog dlg = MD(c, root, 0.92, 0);
    btnReset.setOnClickListener(new android.view.View.OnClickListener() {
        public void onClick(android.view.View v) {
            etPersona.setText("你是一个友好的微信助手，请用简洁自然的语言回复。");
        }
    });
    btnSave.setOnClickListener(new android.view.View.OnClickListener() {
        public void onClick(android.view.View v) {
            deepseekPersona = etPersona.getText().toString().trim();
            putString("ls_ds_persona", deepseekPersona);
            toast("人设已保存");
            safeDismiss(dlg);
        }
    });
    btnBack.setOnClickListener(new android.view.View.OnClickListener() {
        public void onClick(android.view.View v) { safeDismiss(dlg); }
    });
    root.addView(SP(c, dp(8)));
    root.addView(H(c("FFB0B8D0"), 1));
    root.addView(SP(c, dp(6)));
    android.widget.TextView pDesc = T(c, "System Prompt 影响 AI 的回答风格和身份定位。\n例如设定「你是一个专业知识问答助手」或「你是一个活泼的朋友」。", 10, c("FF999999"), false);
    pDesc.setGravity(android.view.Gravity.CENTER);
    root.addView(pDesc);
    dlg.show();
}

void showGroupManageSettings() {
    android.app.Activity act = getTopActivity();
    if (act == null) return;
    android.content.Context c = act;

    loadConfig();
    final boolean[] localWelcome = {welcomeEnabled};
    final boolean[] localKeyword = {keywordReplyEnabled};
    final boolean[] localAntiAd = {antiAdEnabled};
    final boolean[] localKick = {autoKickEnabled};
    final boolean[] localUnread = {unreadStatsEnabled};
    final boolean[] localRecall = {recallLogEnabled};
    String[] labels = {"入群欢迎", "关键词回复", "防广告", "自动踢人", "未读消息统计", "撤回记录查看"};
    String[] hints = {"新人入群自动发送欢迎消息", "根据关键词自动回复", "检测并处理广告消息", "自动踢出违规群成员", "统计群内未读消息数量", "记录并查看被撤回的消息"};
    String[] btnTexts = {"配置", "配置", "配置", "配置", "查看", "查看"};

    android.widget.LinearLayout root = new android.widget.LinearLayout(c);
    root.setOrientation(1);
    root.setPadding(dp(14), dp(12), dp(14), dp(12));

    android.widget.TextView tvTitle = T(c, "群管理设置", 17, c("FF5B4C8C"), true);
    tvTitle.setGravity(android.view.Gravity.CENTER);
    tvTitle.setPadding(0, 0, 0, dp(6));
    root.addView(tvTitle);

    for (int i = 0; i < 6; i++) {
        String label = labels[i];
        String hint = hints[i];
        final boolean[] ref;
        final CompoundButton.OnCheckedChangeListener listener;

        if (i == 0) {
            ref = localWelcome;
            listener = new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton btn, boolean checked) {
                    localWelcome[0] = checked;
                    welcomeEnabled = checked;
                    saveAllConfig();
                }
            };
        } else if (i == 1) {
            ref = localKeyword;
            listener = new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton btn, boolean checked) {
                    localKeyword[0] = checked;
                    keywordReplyEnabled = checked;
                    saveAllConfig();
                }
            };
        } else if (i == 2) {
            ref = localAntiAd;
            listener = new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton btn, boolean checked) {
                    localAntiAd[0] = checked;
                    antiAdEnabled = checked;
                    saveAllConfig();
                }
            };
        } else if (i == 3) {
            ref = localKick;
            listener = new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton btn, boolean checked) {
                    localKick[0] = checked;
                    autoKickEnabled = checked;
                    saveAllConfig();
                }
            };
        } else if (i == 4) {
            ref = localUnread;
            listener = new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton btn, boolean checked) {
                    localUnread[0] = checked;
                    unreadStatsEnabled = checked;
                    saveAllConfig();
                }
            };
        } else {
            ref = localRecall;
            listener = new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton btn, boolean checked) {
                    localRecall[0] = checked;
                    recallLogEnabled = checked;
                    saveAllConfig();
                }
            };
        }

        android.widget.LinearLayout row = new android.widget.LinearLayout(c);
        row.setOrientation(0);
        row.setLayoutParams(new android.widget.LinearLayout.LayoutParams(-1, -2));
        row.setGravity(android.view.Gravity.CENTER_VERTICAL);
        row.setPadding(0, dp(4), 0, dp(4));

        android.widget.LinearLayout col = new android.widget.LinearLayout(c);
        col.setOrientation(1);
        col.setGravity(android.view.Gravity.CENTER);
        col.setLayoutParams(new android.widget.LinearLayout.LayoutParams(0, -2, 1));
        col.addView(T(c, label, 13, c("FF1E293B"), true));
        android.widget.TextView tvHint = T(c, hint, 10, c("FF6B7A8C"), false);
        tvHint.setPadding(0, dp(1), 0, 0);
        col.addView(tvHint);
        row.addView(col);

        Switch sw = new Switch(c);
        sw.setChecked(ref[0]);
        styleSwitch(sw);
        sw.setOnCheckedChangeListener(listener);
        row.addView(sw);

        android.widget.Button btnCfg = new android.widget.Button(c);
        btnCfg.setText(btnTexts[i]);
        btnCfg.setTextSize(10);
        btnCfg.setAllCaps(false);
        btnCfg.setBackgroundDrawable(createGlassBtnBg(4, "00FFFFFF", "FF1E293B", "FF7C6CD0"));
        btnCfg.setTextColor(c("FF1E293B"));
        btnCfg.setPadding(dp(8), dp(2), dp(8), dp(2));
        btnCfg.setTag(Integer.valueOf(i));
        btnCfg.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(android.view.View v) {
                int fi = ((Integer)v.getTag()).intValue();
                if (fi == 0) {
                    android.app.AlertDialog.Builder wb = new android.app.AlertDialog.Builder(c);
                    android.widget.LinearLayout wv = new android.widget.LinearLayout(c);
                    wv.setOrientation(1);
                    wv.setPadding(dp(16), dp(8), dp(16), dp(8));
                    final int[] wTypeRef = {welcomeType};
                    android.widget.RadioGroup rgW = new android.widget.RadioGroup(c);
                    rgW.setOrientation(0);
                    final android.widget.RadioButton wbText = new android.widget.RadioButton(c);
                    wbText.setText("文字"); wbText.setId(3001); wbText.setTextSize(12);
                    final android.widget.RadioButton wbImg = new android.widget.RadioButton(c);
                    wbImg.setText("图片"); wbImg.setId(3002); wbImg.setTextSize(12);
                    final android.widget.RadioButton wbVoice = new android.widget.RadioButton(c);
                    wbVoice.setText("语音"); wbVoice.setId(3003); wbVoice.setTextSize(12);
                    final android.widget.RadioButton wbVideo = new android.widget.RadioButton(c);
                    wbVideo.setText("视频"); wbVideo.setId(3004); wbVideo.setTextSize(12);
                    rgW.addView(wbText); rgW.addView(wbImg); rgW.addView(wbVoice); rgW.addView(wbVideo);
                    rgW.check(welcomeType == 1 ? 3002 : (welcomeType == 2 ? 3003 : (welcomeType == 3 ? 3004 : 3001)));
                    rgW.setOnCheckedChangeListener(new android.widget.RadioGroup.OnCheckedChangeListener() {
                        public void onCheckedChanged(android.widget.RadioGroup g, int id) {
                            wTypeRef[0] = (id == 3002) ? 1 : ((id == 3003) ? 2 : ((id == 3004) ? 3 : 0));
                        }
                    });
                    wv.addView(rgW);
                    wv.addView(T(c, "图片/语音/视频须放于 乐少助手AI存储文件 目录,命名 欢迎语.png/.jpg/.mp3/.silk/.mp4", 9, c("FF888888"), false));
                    final android.widget.EditText wel = new android.widget.EditText(c);
                    wel.setText(welcomeMsg);
                    wel.setHint("文字欢迎内容(会自动@新成员)");
                    wel.setTextSize(13);
                    wel.setPadding(dp(10), dp(8), dp(10), dp(8));
                    wv.addView(wel);
                    wb.setTitle("入群欢迎设置");
                    wb.setView(wv);
                    wb.setPositiveButton("保存", new android.content.DialogInterface.OnClickListener() {
                        public void onClick(android.content.DialogInterface d, int which) {
                            welcomeType = wTypeRef[0];
                            welcomeMsg = wel.getText().toString().trim();
                            putString("ls_welcome_msg", welcomeMsg);
                            toast("欢迎设置已保存");
                            localWelcome[0] = true;
                            saveAllConfig();
                        }
                    });
                    wb.setNegativeButton("禁用", new android.content.DialogInterface.OnClickListener() {
                        public void onClick(android.content.DialogInterface d, int which) {
                            localWelcome[0] = false;
                            saveAllConfig();
                            toast("欢迎消息已禁用");
                        }
                    });
                    wb.setNeutralButton("取消", null);
                    AlertDialog wd = wb.create();
                    wd.setOnShowListener(new android.content.DialogInterface.OnShowListener() { public void onShow(android.content.DialogInterface d) { setupUnifiedDialog(wd); } });
                    wd.show();
                } else if (fi == 1) {
                    showKeywordReplyEditor();
                } else if (fi == 2) {
                    showAntiAdEditor();
                } else if (fi == 3) {
                    showAutoKickEditor();
                } else if (fi == 4) {
                    showUnreadStatsViewer();
                } else if (fi == 5) {
                    showRecallLogViewer();
                }
            }
        });
        row.addView(btnCfg);

        root.addView(row);
        if (i < labels.length - 1) root.addView(H(c("FFB0B8D0"), 1));
    }

    root.addView(SP(c, dp(10)));

    android.widget.LinearLayout btnRow = new android.widget.LinearLayout(c);
    btnRow.setOrientation(0);
    btnRow.setGravity(android.view.Gravity.RIGHT);
    btnRow.setLayoutParams(new android.widget.LinearLayout.LayoutParams(-1, -2));

    android.widget.Button btnBack = new android.widget.Button(c);
    btnBack.setText("返回");
    btnBack.setTextSize(12);
    btnBack.setAllCaps(false);
    btnBack.setBackgroundDrawable(createOutlineBtnBg(8));
    btnBack.setTextColor(c("FF1E293B"));
    btnBack.setPadding(dp(18), dp(6), dp(18), dp(6));
    btnRow.addView(btnBack);
    root.addView(btnRow);

    root.addView(SP(c, dp(6)));
    android.widget.TextView desc = T(c, "各项功能开关即时生效，无需手动保存。", 10, c("FF999999"), false);
    desc.setGravity(android.view.Gravity.CENTER);
    root.addView(desc);

    final android.app.Dialog dlg2 = MD(c, root, 0.92, 0);
    btnBack.setOnClickListener(new android.view.View.OnClickListener() {
        public void onClick(android.view.View v) { safeDismiss(dlg2); }
    });
    dlg2.show();
}

void showKeywordReplyEditor() {
    android.app.Activity act = getTopActivity();
    if (act == null) return;
    android.content.Context c = act;

    android.widget.LinearLayout root = new android.widget.LinearLayout(c);
    root.setOrientation(1);
    root.setPadding(dp(14), dp(10), dp(14), dp(10));

    root.addView(T(c, "关键词回复管理", 17, c("FF5B4C8C"), true));
    root.addView(SP(c, dp(8)));

    root.addView(T(c, "添加规则(一行一对,格式: 关键词/回复)", 11, c("FF4A5568"), false));
    final android.widget.EditText etAdd = new android.widget.EditText(c);
    etAdd.setHint("你好美女/大哥你好啊");
    etAdd.setTextSize(12);
    etAdd.setTextColor(c("FF1E293B"));
    etAdd.setBackgroundDrawable(createInputBg());
    etAdd.setPadding(dp(8), dp(6), dp(8), dp(6));
    root.addView(etAdd);
    root.addView(SP(c, dp(4)));

    android.widget.Button btnAdd = new android.widget.Button(c);
    btnAdd.setText("添加规则");
    btnAdd.setTextSize(12);
    btnAdd.setAllCaps(false);
    btnAdd.setBackgroundDrawable(createPrimaryBtnBg(6));
    btnAdd.setTextColor(c("FF1E293B"));
    btnAdd.setPadding(dp(16), dp(6), dp(16), dp(6));
    root.addView(btnAdd);

    root.addView(SP(c, dp(8)));
    root.addView(H(c("FFB0B8D0"), 1));
    root.addView(SP(c, dp(6)));

    root.addView(T(c, "已有规则:", 12, c("FF4A5568"), false));
    final android.widget.LinearLayout list = new android.widget.LinearLayout(c);
    list.setOrientation(1);
    root.addView(list);

    final String[] delKw = {""};
    final android.app.Dialog dlg = MD(c, root, 0.92, 0);

    Runnable refreshList = new Runnable() {
        public void run() {
            list.removeAllViews();
            if (keywordReplyMap.isEmpty()) {
                list.addView(T(c, "(暂无规则)", 11, c("FF999999"), false));
            }
            java.util.Iterator it = keywordReplyMap.keySet().iterator();
            while (it.hasNext()) {
                final String kw = (String) it.next();
                Map m = keywordReplyMap.get(kw);
                String reply = m != null ? (String) m.get("reply") : "";
                android.widget.LinearLayout row = new android.widget.LinearLayout(c);
                row.setOrientation(0);
                row.setGravity(android.view.Gravity.CENTER_VERTICAL);
                row.setPadding(0, dp(2), 0, dp(2));
                android.widget.TextView tv = T(c, kw + " / " + reply, 11, c("FF1E293B"), false);
                row.addView(tv);
                android.widget.Button btnDel = new android.widget.Button(c);
                btnDel.setText("X");
                btnDel.setTextSize(10);
                btnDel.setTextColor(c("FFFFFFFF"));
                btnDel.setBackgroundDrawable(createDangerBtnBg(3));
                btnDel.setPadding(dp(4), dp(1), dp(4), dp(1));
                btnDel.setOnClickListener(new android.view.View.OnClickListener() {
                    public void onClick(android.view.View v) {
                        keywordReplyMap.remove(kw);
                        saveAllConfig();
                        mainHandler.post(refreshList);
                    }
                });
                row.addView(btnDel);
                list.addView(row);
            }
        }
    };
    mainHandler.post(refreshList);

    btnAdd.setOnClickListener(new android.view.View.OnClickListener() {
        public void onClick(android.view.View v) {
            String input = etAdd.getText().toString().trim();
            if (input.isEmpty() || !input.contains("/")) {
                toast("格式: 关键词/回复内容");
                return;
            }
            int idx = input.indexOf("/");
            String kw = input.substring(0, idx).trim();
            String reply = input.substring(idx + 1).trim();
            if (kw.isEmpty() || reply.isEmpty()) return;
            Map m = new HashMap();
            m.put("reply", reply);
            keywordReplyMap.put(kw, m);
            saveAllConfig();
            etAdd.setText("");
            mainHandler.post(refreshList);
        }
    });

    android.widget.LinearLayout btnRow = new android.widget.LinearLayout(c);
    btnRow.setOrientation(0);
    btnRow.setGravity(android.view.Gravity.RIGHT);
    btnRow.setLayoutParams(new android.widget.LinearLayout.LayoutParams(-1, -2));
    android.widget.Button btnBack = new android.widget.Button(c);
    btnBack.setText("返回");
    btnBack.setTextSize(12);
    btnBack.setAllCaps(false);
    btnBack.setBackgroundDrawable(createOutlineBtnBg(8));
    btnBack.setTextColor(c("FF1E293B"));
    btnBack.setPadding(dp(18), dp(6), dp(18), dp(6));
    btnBack.setOnClickListener(new android.view.View.OnClickListener() {
        public void onClick(android.view.View v) { safeDismiss(dlg); }
    });
    btnRow.addView(btnBack);
    root.addView(btnRow);

    dlg.show();
}

void showAntiAdEditor() {
    android.app.Activity act = getTopActivity();
    if (act == null) return;
    android.content.Context c = act;

    android.widget.LinearLayout root = new android.widget.LinearLayout(c);
    root.setOrientation(1);
    root.setPadding(dp(14), dp(10), dp(14), dp(10));

    root.addView(T(c, "防广告关键词", 17, c("FF5B4C8C"), true));
    root.addView(SP(c, dp(8)));

    root.addView(T(c, "添加关键词:", 11, c("FF4A5568"), false));
    final android.widget.EditText etAdd = new android.widget.EditText(c);
    etAdd.setHint("广告关键词,逗号分隔多个");
    etAdd.setTextSize(12);
    etAdd.setTextColor(c("FF1E293B"));
    etAdd.setBackgroundDrawable(createInputBg());
    etAdd.setPadding(dp(8), dp(6), dp(8), dp(6));
    root.addView(etAdd);
    root.addView(SP(c, dp(4)));

    android.widget.Button btnAdd = new android.widget.Button(c);
    btnAdd.setText("添加");
    btnAdd.setTextSize(12);
    btnAdd.setAllCaps(false);
    btnAdd.setBackgroundDrawable(createPrimaryBtnBg(6));
    btnAdd.setTextColor(c("FF1E293B"));
    btnAdd.setPadding(dp(16), dp(6), dp(16), dp(6));
    root.addView(btnAdd);

    root.addView(SP(c, dp(8)));
    root.addView(H(c("FFB0B8D0"), 1));
    root.addView(SP(c, dp(6)));

    root.addView(T(c, "当前关键词:", 12, c("FF4A5568"), false));
    final android.widget.LinearLayout list = new android.widget.LinearLayout(c);
    list.setOrientation(1);
    root.addView(list);

    final android.app.Dialog dlg = MD(c, root, 0.92, 0);

    Runnable refreshList = new Runnable() {
        public void run() {
            list.removeAllViews();
            if (adKeywords.isEmpty()) {
                list.addView(T(c, "(暂无关键词)", 11, c("FF999999"), false));
            }
            java.util.Iterator it = adKeywords.iterator();
            while (it.hasNext()) {
                final String kw = (String) it.next();
                android.widget.LinearLayout row = new android.widget.LinearLayout(c);
                row.setOrientation(0);
                row.setGravity(android.view.Gravity.CENTER_VERTICAL);
                row.setPadding(0, dp(2), 0, dp(2));
                row.addView(T(c, kw, 11, c("FF1E293B"), false));
                android.widget.Button btnDel = new android.widget.Button(c);
                btnDel.setText("X");
                btnDel.setTextSize(10);
                btnDel.setTextColor(c("FFFFFFFF"));
                btnDel.setBackgroundDrawable(createDangerBtnBg(3));
                btnDel.setPadding(dp(4), dp(1), dp(4), dp(1));
                btnDel.setOnClickListener(new android.view.View.OnClickListener() {
                    public void onClick(android.view.View v) {
                        adKeywords.remove(kw);
                        saveAllConfig();
                        mainHandler.post(refreshList);
                    }
                });
                row.addView(btnDel);
                list.addView(row);
            }
        }
    };
    mainHandler.post(refreshList);

    btnAdd.setOnClickListener(new android.view.View.OnClickListener() {
        public void onClick(android.view.View v) {
            String input = etAdd.getText().toString().trim();
            if (input.isEmpty()) return;
            String[] parts = input.split(",");
            for (String part : parts) {
                String kw = part.trim();
                if (!kw.isEmpty()) adKeywords.add(kw);
            }
            saveAllConfig();
            etAdd.setText("");
            mainHandler.post(refreshList);
        }
    });

    android.widget.LinearLayout btnRow = new android.widget.LinearLayout(c);
    btnRow.setOrientation(0);
    btnRow.setGravity(android.view.Gravity.CENTER_VERTICAL);
    btnRow.setLayoutParams(new android.widget.LinearLayout.LayoutParams(-1, -2));
    android.widget.Button btnSave = new android.widget.Button(c);
    btnSave.setText("保存");
    btnSave.setTextSize(12);
    btnSave.setAllCaps(false);
    btnSave.setBackgroundDrawable(createPrimaryBtnBg(8));
    btnSave.setTextColor(c("FF1E293B"));
    btnSave.setPadding(dp(18), dp(6), dp(18), dp(6));
    btnSave.setOnClickListener(new android.view.View.OnClickListener() {
        public void onClick(android.view.View v) {
            saveAllConfig();
            toast("防广告配置已保存");
        }
    });
    btnRow.addView(btnSave);
    android.widget.LinearLayout.LayoutParams filler = new android.widget.LinearLayout.LayoutParams(0, 1, 1);
    btnRow.addView(new android.view.View(c), filler);
    android.widget.Button btnBack = new android.widget.Button(c);
    btnBack.setText("返回");
    btnBack.setTextSize(12);
    btnBack.setAllCaps(false);
    btnBack.setBackgroundDrawable(createOutlineBtnBg(8));
    btnBack.setTextColor(c("FF1E293B"));
    btnBack.setPadding(dp(18), dp(6), dp(18), dp(6));
    btnBack.setOnClickListener(new android.view.View.OnClickListener() {
        public void onClick(android.view.View v) { safeDismiss(dlg); }
    });
    btnRow.addView(btnBack);
    root.addView(btnRow);

    dlg.show();
}

void showBlacklistDialog() {
    android.app.Activity act = getTopActivity();
    if (act == null) return;
    final android.content.Context c = act;

    android.widget.LinearLayout root = new android.widget.LinearLayout(c);
    root.setOrientation(1);
    root.setPadding(dp(14), dp(10), dp(14), dp(10));

    root.addView(T(c, "全局黑名单", 17, c("FF5B4C8C"), true));
    root.addView(SP(c, dp(6)));

    android.widget.LinearLayout swRow = new android.widget.LinearLayout(c);
    swRow.setOrientation(0);
    swRow.setGravity(android.view.Gravity.CENTER_VERTICAL);
    android.widget.TextView swLbl = T(c, "启用黑名单(进群自动移出)", 12, c("FF1E293B"), true);
    swLbl.setLayoutParams(new android.widget.LinearLayout.LayoutParams(0, -2, 1));
    swRow.addView(swLbl);
    Switch swBl = new Switch(c);
    swBl.setChecked(blacklistEnabled);
    styleSwitch(swBl);
    swBl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton btn, boolean checked) {
            blacklistEnabled = checked;
            saveAllConfig();
        }
    });
    swRow.addView(swBl);
    root.addView(swRow);
    root.addView(H(c("FFB0B8D0"), 1));
    root.addView(SP(c, dp(4)));

    final android.widget.LinearLayout container = new android.widget.LinearLayout(c);
    container.setOrientation(1);
    root.addView(container);

    final android.app.Dialog dlg = MD(c, root, 0.94, 0);
    final int[] currentPage = {0};

    final Runnable[] refreshHolder = new Runnable[1];
    Runnable refresh = new Runnable() {
        public void run() {
            container.removeAllViews();
            java.util.List recs = new java.util.ArrayList(blacklistMap.values());
            java.util.Collections.sort(recs, new java.util.Comparator() {
                public int compare(Object a, Object b) {
                    long ta = ((JSONObject) a).optLong("time", 0);
                    long tb = ((JSONObject) b).optLong("time", 0);
                    return (tb > ta) ? 1 : ((tb < ta) ? -1 : 0);
                }
            });
            int total = recs.size();
            int totalPages = total == 0 ? 1 : ((total + BLACKLIST_PAGE_SIZE - 1) / BLACKLIST_PAGE_SIZE);
            if (currentPage[0] >= totalPages) currentPage[0] = totalPages - 1;
            if (currentPage[0] < 0) currentPage[0] = 0;

            if (total == 0) {
                container.addView(T(c, "(黑名单为空)", 11, c("FF999999"), false));
            } else {
                int start = currentPage[0] * BLACKLIST_PAGE_SIZE;
                int end = Math.min(start + BLACKLIST_PAGE_SIZE, total);
                for (int i = start; i < end; i++) {
                    JSONObject o = (JSONObject) recs.get(i);
                    final String wxid = o.optString("wxid", "");
                    String name = o.optString("name", "");
                    if (name == null || name.isEmpty()) name = wxid;
                    String reason = o.optString("reason", "");

                    android.widget.LinearLayout row = new android.widget.LinearLayout(c);
                    row.setOrientation(0);
                    row.setGravity(android.view.Gravity.CENTER_VERTICAL);
                    row.setPadding(0, dp(4), 0, dp(4));

                    android.widget.ImageView iv = new android.widget.ImageView(c);
                    iv.setScaleType(android.widget.ImageView.ScaleType.CENTER_CROP);
                    android.widget.LinearLayout.LayoutParams lpIv = new android.widget.LinearLayout.LayoutParams(dp(40), dp(40));
                    lpIv.rightMargin = dp(8);
                    iv.setLayoutParams(lpIv);
                    iv.setImageDrawable(createInputBg());
                    loadAvatarAsync(iv, wxid);
                    row.addView(iv);

                    android.widget.LinearLayout col = new android.widget.LinearLayout(c);
                    col.setOrientation(1);
                    col.setLayoutParams(new android.widget.LinearLayout.LayoutParams(0, -2, 1));
                    col.addView(T(c, name, 12, c("FF1E293B"), true));
                    col.addView(T(c, wxid, 9, c("FF888888"), false));
                    col.addView(T(c, "原因: " + reason, 10, c("FFE05B5B"), false));
                    row.addView(col);

                    android.widget.Button btnRm = new android.widget.Button(c);
                    btnRm.setText("移除");
                    btnRm.setTextSize(10);
                    btnRm.setAllCaps(false);
                    btnRm.setTextColor(c("FFFFFFFF"));
                    btnRm.setBackgroundDrawable(createDangerBtnBg(4));
                    btnRm.setPadding(dp(8), dp(3), dp(8), dp(3));
                    btnRm.setOnClickListener(new android.view.View.OnClickListener() {
                        public void onClick(android.view.View v) {
                            blacklistMap.remove(wxid);
                            saveAllConfig();
                            toast("已从黑名单移除");
                            mainHandler.post(refreshHolder[0]);
                        }
    });

    // ========== 修复: showBlacklistDialog 正确结尾 ==========
                    row.addView(btnRm);
                    container.addView(row);
                    container.addView(SP(c, 2));
                }

                android.widget.LinearLayout pageRow = new android.widget.LinearLayout(c);
                pageRow.setOrientation(0);
                pageRow.setGravity(android.view.Gravity.CENTER);
                pageRow.setPadding(0, dp(8), 0, 0);

                android.widget.Button btnPrev = new android.widget.Button(c);
                btnPrev.setText("< 上一页");
                btnPrev.setTextSize(11);
                btnPrev.setAllCaps(false);
                btnPrev.setBackgroundDrawable(createOutlineBtnBg(6));
                btnPrev.setTextColor(c("FF1E293B"));
                btnPrev.setPadding(dp(10), dp(4), dp(10), dp(4));
                btnPrev.setEnabled(currentPage[0] > 0);
                btnPrev.setOnClickListener(new android.view.View.OnClickListener() {
                    public void onClick(android.view.View v) {
                        currentPage[0]--;
                        refresh.run();
                    }
                });
                pageRow.addView(btnPrev);

                android.widget.TextView tvPage = T(c, " " + (currentPage[0] + 1) + "/" + totalPages + " ", 11, c("FF4A5568"), false);
                tvPage.setPadding(dp(8), 0, dp(8), 0);
                pageRow.addView(tvPage);

                android.widget.Button btnNext = new android.widget.Button(c);
                btnNext.setText("下一页 >");
                btnNext.setTextSize(11);
                btnNext.setAllCaps(false);
                btnNext.setBackgroundDrawable(createOutlineBtnBg(6));
                btnNext.setTextColor(c("FF1E293B"));
                btnNext.setPadding(dp(10), dp(4), dp(10), dp(4));
                btnNext.setEnabled(currentPage[0] < totalPages - 1);
                btnNext.setOnClickListener(new android.view.View.OnClickListener() {
                    public void onClick(android.view.View v) {
                        currentPage[0]++;
                        refresh.run();
                    }
                });
                pageRow.addView(btnNext);
                container.addView(pageRow);
            }
        }
    };
    refreshHolder[0] = refresh;
    mainHandler.post(refresh);

    root.addView(SP(c, dp(6)));

    android.widget.LinearLayout btnRow = new android.widget.LinearLayout(c);
    btnRow.setOrientation(0);
    btnRow.setGravity(android.view.Gravity.RIGHT);
    android.widget.Button btnBack = new android.widget.Button(c);
    btnBack.setText("返回");
    btnBack.setTextSize(12);
    btnBack.setAllCaps(false);
    btnBack.setBackgroundDrawable(createOutlineBtnBg(8));
    btnBack.setTextColor(c("FF1E293B"));
    btnBack.setPadding(dp(18), dp(6), dp(18), dp(6));
    btnBack.setOnClickListener(new android.view.View.OnClickListener() {
        public void onClick(android.view.View v) { safeDismiss(dlg); }
    });
    btnRow.addView(btnBack);
    root.addView(btnRow);

    dlg.show();
    // ========== 修复结束 ==========
}

void showVoiceToTextConfig() {
    android.app.Activity act = getTopActivity();
    if (act == null) return;
    android.content.Context c = act;
    loadConfig();

    android.widget.LinearLayout root = new android.widget.LinearLayout(c);
    root.setOrientation(1);
    root.setPadding(dp(14), dp(12), dp(14), dp(12));

    root.addView(T(c, "语音转文字设置", 17, c("FF5B4C8C"), true));
    root.addView(SP(c, dp(8)));

    root.addView(T(c, "收到语音消息时自动转为文字回复。\n当前为演示模式，需要接入第三方语音识别服务。", 11, c("FF4A5568"), false));
    root.addView(SP(c, dp(8)));

    root.addView(T(c, "识别语言", 11, c("FF4A5568"), false));
    final android.widget.EditText etLang = new android.widget.EditText(c);
    etLang.setText(getString("ls_v2t_lang", "zh-CN"));
    etLang.setTextSize(12);
    etLang.setTextColor(c("FF1E293B"));
    etLang.setBackgroundDrawable(createInputBg());
    etLang.setPadding(dp(8), dp(6), dp(8), dp(6));
    root.addView(etLang);

    root.addView(SP(c, dp(12)));

    android.widget.Button btnSave = new android.widget.Button(c);
    btnSave.setText("保存配置");
    btnSave.setTextSize(14);
    btnSave.setAllCaps(false);
    btnSave.setBackgroundDrawable(createPrimaryBtnBg(8));
    btnSave.setTextColor(c("FF1E293B"));
    btnSave.setPadding(dp(28), dp(8), dp(28), dp(8));
    root.addView(btnSave);

    android.widget.TextView desc = T(c, "Demo功能，暂不支持真实语音转文字（需外部API）", 9, c("FF999999"), false);
    desc.setGravity(android.view.Gravity.CENTER);
    desc.setPadding(0, dp(4), 0, 0);
    root.addView(desc);

    final android.app.Dialog dlg = MD(c, root, 0.88, 0);

    btnSave.setOnClickListener(new android.view.View.OnClickListener() {
        public void onClick(android.view.View v) {
            putString("ls_v2t_lang", etLang.getText().toString().trim());
            toast("语音转文字设置已保存");
            safeDismiss(dlg);
        }
    });

    android.widget.Button btnBackV2t = new android.widget.Button(c);
    btnBackV2t.setText("返回");
    btnBackV2t.setTextSize(13);
    btnBackV2t.setAllCaps(false);
    btnBackV2t.setBackgroundDrawable(createOutlineBtnBg(8));
    btnBackV2t.setTextColor(c("FF1E293B"));
    btnBackV2t.setPadding(dp(28), dp(6), dp(28), dp(6));
    root.addView(btnBackV2t);
    btnBackV2t.setOnClickListener(new android.view.View.OnClickListener() {
        public void onClick(android.view.View v) { safeDismiss(dlg); }
    });

    dlg.show();
}

void showLinkSummaryConfig() {
    android.app.Activity act = getTopActivity();
    if (act == null) return;
    android.content.Context c = act;
    loadConfig();

    android.widget.LinearLayout root = new android.widget.LinearLayout(c);
    root.setOrientation(1);
    root.setPadding(dp(14), dp(12), dp(14), dp(12));

    root.addView(T(c, "链接内容摘要设置", 17, c("FF5B4C8C"), true));
    root.addView(SP(c, dp(8)));

    String dsStat = deepseekApiKey.isEmpty() ? "未配置" : "已配置(" + deepseekApiKey.substring(0, Math.min(8, deepseekApiKey.length())) + "***)";
    root.addView(T(c, "DeepSeek API Key: " + dsStat, 11, c(deepseekApiKey.isEmpty() ? "FFE53935" : "FF1D4ED8"), false));
    root.addView(SP(c, dp(4)));

    android.widget.Button btnKey = new android.widget.Button(c);
    btnKey.setText("配置 API Key");
    btnKey.setTextSize(12);
    btnKey.setAllCaps(false);
    btnKey.setBackgroundDrawable(createPrimaryBtnBg(6));
    btnKey.setTextColor(c("FF1E293B"));
    btnKey.setPadding(dp(16), dp(6), dp(16), dp(6));
    root.addView(btnKey);

    root.addView(SP(c, dp(8)));

    root.addView(T(c, "摘要提示词", 11, c("FF4A5568"), false));
    final android.widget.EditText etPrompt = new android.widget.EditText(c);
    etPrompt.setText(getString("ls_linksum_prompt", "请用一句话描述这个链接的内容"));
    etPrompt.setTextSize(12);
    etPrompt.setTextColor(c("FF1E293B"));
    etPrompt.setBackgroundDrawable(createInputBg());
    etPrompt.setPadding(dp(8), dp(6), dp(8), dp(6));
    root.addView(etPrompt);

    root.addView(SP(c, dp(12)));

    android.widget.Button btnSave = new android.widget.Button(c);
    btnSave.setText("保存配置");
    btnSave.setTextSize(14);
    btnSave.setAllCaps(false);
    btnSave.setBackgroundDrawable(createPrimaryBtnBg(8));
    btnSave.setTextColor(c("FF1E293B"));
    btnSave.setPadding(dp(28), dp(8), dp(28), dp(8));
    root.addView(btnSave);

    android.widget.TextView desc = T(c, "链接摘要功能依赖有效的 DeepSeek API Key", 10, c("FF999999"), false);
    desc.setGravity(android.view.Gravity.CENTER);
    desc.setPadding(0, dp(4), 0, 0);
    root.addView(desc);

    final android.app.Dialog dlg = MD(c, root, 0.88, 0);
    btnKey.setOnClickListener(new android.view.View.OnClickListener() {
        public void onClick(android.view.View v) {
            showDeepseekSettingsDialog();
            safeDismiss(dlg);
        }
    });
    btnSave.setOnClickListener(new android.view.View.OnClickListener() {
        public void onClick(android.view.View v) {
            putString("ls_linksum_prompt", etPrompt.getText().toString().trim());
            toast("链接摘要设置已保存");
            safeDismiss(dlg);
        }
    });

    android.widget.Button btnBackLinkSum = new android.widget.Button(c);
    btnBackLinkSum.setText("返回");
    btnBackLinkSum.setTextSize(13);
    btnBackLinkSum.setAllCaps(false);
    btnBackLinkSum.setBackgroundDrawable(createOutlineBtnBg(8));
    btnBackLinkSum.setTextColor(c("FF1E293B"));
    btnBackLinkSum.setPadding(dp(28), dp(6), dp(28), dp(6));
    root.addView(btnBackLinkSum);
    btnBackLinkSum.setOnClickListener(new android.view.View.OnClickListener() {
        public void onClick(android.view.View v) { safeDismiss(dlg); }
    });

    dlg.show();
}

void showFileClassifyViewer() {
    android.app.Activity act = getTopActivity();
    if (act == null) return;
    android.content.Context c = act;

    java.util.List fileLogs = new java.util.ArrayList();
    try {
        String saved = getString("ls_file_log", "");
        if (saved != null && !saved.isEmpty()) {
            String[] entries = saved.split("\n");
            for (String e : entries) {
                if (e.trim().length() > 0) fileLogs.add(e.trim());
            }
        }
    } catch (Exception ex) {}

    android.widget.LinearLayout root = new android.widget.LinearLayout(c);
    root.setOrientation(1);
    root.setPadding(dp(14), dp(10), dp(14), dp(10));

    root.addView(T(c, "文件分类记录", 17, c("FF5B4C8C"), true));
    root.addView(SP(c, dp(8)));

    android.widget.ScrollView sv = new android.widget.ScrollView(c);
    android.widget.LinearLayout.LayoutParams svlp = new android.widget.LinearLayout.LayoutParams(-1, 0, 1);
    sv.setLayoutParams(svlp);
    sv.setFillViewport(true);
    android.widget.LinearLayout content = new android.widget.LinearLayout(c);
    content.setOrientation(1);

    if (fileLogs.isEmpty()) {
        content.addView(T(c, "(暂无文件记录)", 12, c("FF999999"), false));
    } else {
        for (int i = 0; i < fileLogs.size() && i < 50; i++) {
            String entry = (String) fileLogs.get(i);
            android.widget.LinearLayout row = new android.widget.LinearLayout(c);
            row.setOrientation(0);
            row.setPadding(dp(6), dp(4), dp(6), dp(4));
            GradientDrawable rowBg = new GradientDrawable();
            rowBg.setCornerRadius(dp(6));
            rowBg.setColor(c("FFF0F0F8"));
            row.setBackgroundDrawable(rowBg);
            android.widget.TextView tv = new android.widget.TextView(c);
            tv.setText(entry);
            tv.setTextSize(10);
            tv.setTextColor(c("FF1E293B"));
            row.addView(tv);
            content.addView(row);
            content.addView(SP(c, 2));
        }
    }

    sv.addView(content);
    root.addView(sv);
    root.addView(SP(c, dp(6)));

    final android.app.Dialog dlg = MD(c, root, 0.88, 0);

    android.widget.LinearLayout btnRow = new android.widget.LinearLayout(c);
    btnRow.setOrientation(0);
    btnRow.setGravity(android.view.Gravity.RIGHT);
    btnRow.setLayoutParams(new android.widget.LinearLayout.LayoutParams(-1, -2));

    if (!fileLogs.isEmpty()) {
        android.widget.Button btnClear = new android.widget.Button(c);
        btnClear.setText("清空记录");
        btnClear.setTextSize(11);
        btnClear.setAllCaps(false);
        btnClear.setBackgroundDrawable(createGlassBtnBg(4, "00FFFFFF", "FFE53935", "FFFF5252"));
        btnClear.setTextColor(c("FFE53935"));
        btnClear.setPadding(dp(12), dp(4), dp(12), dp(4));
        btnRow.addView(btnClear);
        btnClear.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(android.view.View v) {
                putString("ls_file_log", "");
                toast("文件记录已清空");
                safeDismiss(dlg);
            }
        });
    }

    android.widget.Button btnBack = new android.widget.Button(c);
    btnBack.setText("返回");
    btnBack.setTextSize(11);
    btnBack.setAllCaps(false);
    btnBack.setBackgroundDrawable(createOutlineBtnBg(8));
    btnBack.setTextColor(c("FF1E293B"));
    btnBack.setPadding(dp(12), dp(4), dp(12), dp(4));
    btnRow.addView(btnBack);
    root.addView(btnRow);

    btnBack.setOnClickListener(new android.view.View.OnClickListener() {
        public void onClick(android.view.View v) { safeDismiss(dlg); }
    });
    dlg.show();
}

void showReminderConfig() {
    android.app.Activity act = getTopActivity();
    if (act == null) return;
    android.content.Context c = act;
    loadConfig();

    android.widget.LinearLayout root = new android.widget.LinearLayout(c);
    root.setOrientation(1);
    root.setPadding(dp(14), dp(12), dp(14), dp(12));

    root.addView(T(c, "消息定时提醒设置", 17, c("FF5B4C8C"), true));
    root.addView(SP(c, dp(6)));

    root.addView(T(c, "触发格式: #提醒 时长 提醒内容\n例如: #提醒 30 记得休息一下 (30秒后提醒)", 10, c("FF4A5568"), false));
    root.addView(SP(c, dp(8)));

    android.widget.TextView tvCount = T(c, "当前活跃提醒任务: " + reminderTasks.size() + " 个", 12, c("FF1D4ED8"), false);
    root.addView(tvCount);
    root.addView(SP(c, dp(8)));

    root.addView(T(c, "自定义默认提醒词(检测到包含这些词的消息时自动创建提醒)", 10, c("FF4A5568"), false));
    final android.widget.EditText etKw = new android.widget.EditText(c);
    etKw.setText(getString("ls_reminder_kw", ""));
    etKw.setHint("关键词,逗号分隔");
    etKw.setTextSize(12);
    etKw.setTextColor(c("FF1E293B"));
    etKw.setBackgroundDrawable(createInputBg());
    etKw.setPadding(dp(8), dp(6), dp(8), dp(6));
    root.addView(etKw);

    root.addView(SP(c, dp(12)));

    android.widget.LinearLayout btnRow = new android.widget.LinearLayout(c);
    btnRow.setOrientation(0);
    btnRow.setLayoutParams(new android.widget.LinearLayout.LayoutParams(-1, -2));

    android.widget.Button btnSave = new android.widget.Button(c);
    btnSave.setText("保存配置");
    btnSave.setTextSize(14);
    btnSave.setAllCaps(false);
    btnSave.setBackgroundDrawable(createPrimaryBtnBg(8));
    btnSave.setTextColor(c("FF1E293B"));
    btnSave.setPadding(dp(28), dp(8), dp(28), dp(8));
    btnRow.addView(btnSave);

    android.widget.LinearLayout.LayoutParams spMid = new android.widget.LinearLayout.LayoutParams(0, 1, 1);
    btnRow.addView(new android.view.View(c), spMid);

    android.widget.Button btnBack = new android.widget.Button(c);
    btnBack.setText("返回");
    btnBack.setTextSize(12);
    btnBack.setAllCaps(false);
    btnBack.setBackgroundDrawable(createOutlineBtnBg(8));
    btnBack.setTextColor(c("FF1E293B"));
    btnBack.setPadding(dp(14), dp(6), dp(14), dp(6));
    btnRow.addView(btnBack);
    root.addView(btnRow);

    final android.app.Dialog dlg = MD(c, root, 0.88, 0);
    btnSave.setOnClickListener(new android.view.View.OnClickListener() {
        public void onClick(android.view.View v) {
            putString("ls_reminder_kw", etKw.getText().toString().trim());
            toast("提醒设置已保存");
            safeDismiss(dlg);
        }
    });
    btnBack.setOnClickListener(new android.view.View.OnClickListener() {
        public void onClick(android.view.View v) { safeDismiss(dlg); }
    });
    dlg.show();
}

void showAutoKickEditor() {
    android.app.Activity act = getTopActivity();
    if (act == null) return;
    android.content.Context c = act;
    loadConfig();

    final boolean[] mtStates = new boolean[11];
    for (int i = 0; i < 11; i++) mtStates[i] = mtEnabled(i);
    final String[] mtLabels = {"文本", "图片", "表情", "语音", "视频", "小程序", "视频号", "公众号", "链接", "位置", "文件"};
    final int[] localWarnType = {warnType};
    final String localWarnMsg = warnMsg != null ? warnMsg : "";
    final int[] localFarewellType = {farewellType};
    final String localFarewellMsg = farewellMsg != null ? farewellMsg : "";
    final int[] localThreshold = {kickThreshold};

    android.widget.ScrollView sv = new android.widget.ScrollView(c);
    android.widget.LinearLayout root = new android.widget.LinearLayout(c);
    root.setOrientation(1);
    root.setPadding(dp(14), dp(10), dp(14), dp(10));

    root.addView(T(c, "自动踢人配置", 17, c("FF5B4C8C"), true));
    root.addView(SP(c, dp(8)));

    root.addView(T(c, "违规次数阈值:", 12, c("FF4A5568"), false));
    final android.widget.EditText etThreshold = new android.widget.EditText(c);
    etThreshold.setText(String.valueOf(kickThreshold));
    etThreshold.setTextSize(13);
    etThreshold.setTextColor(c("FF1E293B"));
    etThreshold.setBackgroundDrawable(createInputBg());
    etThreshold.setPadding(dp(10), dp(6), dp(10), dp(6));
    etThreshold.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
    root.addView(etThreshold);
    root.addView(SP(c, dp(8)));

    root.addView(T(c, "警告类型:", 12, c("FF4A5568"), false));
    android.widget.RadioGroup rgWarn = new android.widget.RadioGroup(c);
    rgWarn.setOrientation(0);
    final android.widget.RadioButton rbWarnText = new android.widget.RadioButton(c);
    rbWarnText.setText("文字"); rbWarnText.setId(4001); rbWarnText.setTextSize(12);
    final android.widget.RadioButton rbWarnImg = new android.widget.RadioButton(c);
    rbWarnImg.setText("图片"); rbWarnImg.setId(4002); rbWarnImg.setTextSize(12);
    final android.widget.RadioButton rbWarnVoice = new android.widget.RadioButton(c);
    rbWarnVoice.setText("语音"); rbWarnVoice.setId(4003); rbWarnVoice.setTextSize(12);
    rgWarn.addView(rbWarnText); rgWarn.addView(rbWarnImg); rgWarn.addView(rbWarnVoice);
    rgWarn.check(warnType == 1 ? 4002 : (warnType == 2 ? 4003 : 4001));
    rgWarn.setOnCheckedChangeListener(new android.widget.RadioGroup.OnCheckedChangeListener() {
        public void onCheckedChanged(android.widget.RadioGroup g, int id) {
            localWarnType[0] = (id == 4002) ? 1 : ((id == 4003) ? 2 : 0);
        }
    });
    root.addView(rgWarn);
    root.addView(SP(c, dp(4)));

    root.addView(T(c, "警告内容:", 12, c("FF4A5568"), false));
    final android.widget.EditText etWarnMsg = new android.widget.EditText(c);
    etWarnMsg.setText(localWarnMsg);
    etWarnMsg.setHint("支持{userName}替换成员昵称");
    etWarnMsg.setTextSize(12);
    etWarnMsg.setTextColor(c("FF1E293B"));
    etWarnMsg.setBackgroundDrawable(createInputBg());
    etWarnMsg.setPadding(dp(10), dp(6), dp(10), dp(6));
    root.addView(etWarnMsg);
    root.addView(SP(c, dp(8)));

    root.addView(T(c, "踢出通知类型:", 12, c("FF4A5568"), false));
    android.widget.RadioGroup rgFarewell = new android.widget.RadioGroup(c);
    rgFarewell.setOrientation(0);
    final android.widget.RadioButton rbFwText = new android.widget.RadioButton(c);
    rbFwText.setText("文字"); rbFwText.setId(5001); rbFwText.setTextSize(12);
    final android.widget.RadioButton rbFwImg = new android.widget.RadioButton(c);
    rbFwImg.setText("图片"); rbFwImg.setId(5002); rbFwImg.setTextSize(12);
    final android.widget.RadioButton rbFwVoice = new android.widget.RadioButton(c);
    rbFwVoice.setText("语音"); rbFwVoice.setId(5003); rbFwVoice.setTextSize(12);
    rgFarewell.addView(rbFwText); rgFarewell.addView(rbFwImg); rgFarewell.addView(rbFwVoice);
    rgFarewell.check(farewellType == 1 ? 5002 : (farewellType == 2 ? 5003 : 5001));
    rgFarewell.setOnCheckedChangeListener(new android.widget.RadioGroup.OnCheckedChangeListener() {
        public void onCheckedChanged(android.widget.RadioGroup g, int id) {
            localFarewellType[0] = (id == 5002) ? 1 : ((id == 5003) ? 2 : 0);
        }
    });
    root.addView(rgFarewell);
    root.addView(SP(c, dp(4)));

    root.addView(T(c, "踢出通知内容:", 12, c("FF4A5568"), false));
    final android.widget.EditText etFarewellMsg = new android.widget.EditText(c);
    etFarewellMsg.setText(localFarewellMsg);
    etFarewellMsg.setHint("支持{userName}替换成员昵称");
    etFarewellMsg.setTextSize(12);
    etFarewellMsg.setTextColor(c("FF1E293B"));
    etFarewellMsg.setBackgroundDrawable(createInputBg());
    etFarewellMsg.setPadding(dp(10), dp(6), dp(10), dp(6));
    root.addView(etFarewellMsg);
    root.addView(SP(c, dp(10)));

    root.addView(T(c, "监控消息类型(开哪项,成员发该类型消息被警告):", 11, c("FF4A5568"), false));
    root.addView(SP(c, dp(3)));

    for (int ri = 0; ri < 6; ri++) {
        android.widget.LinearLayout mtRow = new android.widget.LinearLayout(c);
        mtRow.setOrientation(0);
        mtRow.setGravity(android.view.Gravity.CENTER_VERTICAL);
        mtRow.setPadding(0, dp(2), 0, dp(2));
        for (int col = 0; col < 2 && ri * 2 + col < 11; col++) {
            final int bi = ri * 2 + col;
            android.widget.LinearLayout cell = new android.widget.LinearLayout(c);
            cell.setOrientation(0);
            cell.setGravity(android.view.Gravity.CENTER_VERTICAL);
            cell.setLayoutParams(new android.widget.LinearLayout.LayoutParams(0, -2, 1));
            Switch sw = new Switch(c);
            sw.setChecked(mtStates[bi]);
            styleSwitch(sw);
            sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton btn, boolean ck) {
                    mtStates[bi] = ck;
                    if (ck) mtTypeMask |= (1 << bi); else mtTypeMask &= ~(1 << bi);
                    saveAllConfig();
                }
            });
            cell.addView(sw);
            android.widget.TextView tv = T(c, mtLabels[bi], 10, c("FF4A5568"), false);
            tv.setPadding(dp(3), 0, dp(3), 0);
            cell.addView(tv);
            mtRow.addView(cell);
        }
        root.addView(mtRow);
    }
    root.addView(T(c, "以上类型开哪项,成员发送该类型消息就会被警告并累计违规次数", 9, c("FF888888"), false));
    root.addView(SP(c, dp(4)));

    root.addView(H(c("FFB0B8D0"), 1));
    root.addView(SP(c, dp(8)));

    android.widget.LinearLayout btnRow = new android.widget.LinearLayout(c);
    btnRow.setOrientation(0);
    btnRow.setGravity(android.view.Gravity.CENTER_VERTICAL);
    btnRow.setLayoutParams(new android.widget.LinearLayout.LayoutParams(-1, -2));

    android.widget.Button btnSave = new android.widget.Button(c);
    btnSave.setText("保存配置");
    btnSave.setTextSize(14);
    btnSave.setAllCaps(false);
    btnSave.setBackgroundDrawable(createPrimaryBtnBg(8));
    btnSave.setTextColor(c("FF1E293B"));
    btnSave.setPadding(dp(28), dp(8), dp(28), dp(8));
    btnRow.addView(btnSave);

    android.widget.LinearLayout.LayoutParams spFiller = new android.widget.LinearLayout.LayoutParams(0, 1, 1);
    btnRow.addView(new android.view.View(c), spFiller);

    android.widget.Button btnBack = new android.widget.Button(c);
    btnBack.setText("返回");
    btnBack.setTextSize(13);
    btnBack.setAllCaps(false);
    btnBack.setBackgroundDrawable(createOutlineBtnBg(8));
    btnBack.setTextColor(c("FF1E293B"));
    btnBack.setPadding(dp(18), dp(5), dp(18), dp(5));
    btnRow.addView(btnBack);
    root.addView(btnRow);

    sv.addView(root);
    final android.app.Dialog dlg = MD(c, sv, 0.94, 0);

    btnSave.setOnClickListener(new android.view.View.OnClickListener() {
        public void onClick(android.view.View v) {
            try {
                int th = Integer.parseInt(etThreshold.getText().toString().trim());
                if (th >= 1 && th <= 100) kickThreshold = th;
            } catch (Exception e) {}
            warnType = localWarnType[0];
            warnMsg = etWarnMsg.getText().toString().trim();
            farewellType = localFarewellType[0];
            farewellMsg = etFarewellMsg.getText().toString().trim();
            for (int i = 0; i < 11; i++) {
                if (mtStates[i]) mtTypeMask |= (1 << i); else mtTypeMask &= ~(1 << i);
            }
            saveAllConfig();
            toast("自动踢人配置已保存");
            safeDismiss(dlg);
        }
    });
    btnBack.setOnClickListener(new android.view.View.OnClickListener() {
        public void onClick(android.view.View v) { safeDismiss(dlg); }
    });
    dlg.show();
}

void openAiWhitelistDialog(android.content.Context c, final android.app.Dialog parentDlg, final android.widget.TextView labelView) {
    android.widget.LinearLayout subRoot = new android.widget.LinearLayout(c);
    subRoot.setOrientation(1);
    subRoot.setGravity(android.view.Gravity.CENTER);
    subRoot.setPadding(dp(24), dp(20), dp(24), dp(24));
    subRoot.setBackgroundDrawable(createCardBg(14));

    android.widget.TextView tvSubTitle = T(c, "选择需要AI回复的类型", 16, c("FF212121"), true);
    tvSubTitle.setGravity(android.view.Gravity.CENTER);
    tvSubTitle.setPadding(0, 0, 0, dp(14));
    subRoot.addView(tvSubTitle);

    final List<String> wlList = new ArrayList(aiWhitelist);

    android.widget.Button btnSubFriend = new android.widget.Button(c);
    btnSubFriend.setText("选择好友");
    btnSubFriend.setTextSize(14);
    btnSubFriend.setAllCaps(false);
    btnSubFriend.setBackgroundDrawable(createGlassBtnBg(10, "00FFFFFF", "FF1E293B", "FF7C6CD0"));
    btnSubFriend.setTextColor(c("FF1E293B"));
    btnSubFriend.setPadding(dp(28), dp(10), dp(28), dp(10));
    android.widget.LinearLayout.LayoutParams lpF = new android.widget.LinearLayout.LayoutParams(-1, -2);
    lpF.setMargins(0, 0, 0, dp(10));
    btnSubFriend.setLayoutParams(lpF);
    subRoot.addView(btnSubFriend);

    android.widget.Button btnSubGroup = new android.widget.Button(c);
    btnSubGroup.setText("选择群聊");
    btnSubGroup.setTextSize(14);
    btnSubGroup.setAllCaps(false);
    btnSubGroup.setBackgroundDrawable(createGlassBtnBg(10, "00FFFFFF", "FF1E293B", "FF8898C8"));
    btnSubGroup.setTextColor(c("FF1E293B"));
    btnSubGroup.setPadding(dp(28), dp(10), dp(28), dp(10));
    subRoot.addView(btnSubGroup);

    final android.app.Dialog subDlg = MD(c, subRoot, 0.78, 0);
    subDlg.show();

    btnSubFriend.setOnClickListener(new android.view.View.OnClickListener() {
        public void onClick(android.view.View vv) {
            safeDismiss(subDlg);
            Set<String> init = new java.util.HashSet(wlList);
            showSelectDialog(c, "选择好友", false, init, wlList, new Runnable() {
                public void run() {
                    aiWhitelist.clear();
                    aiWhitelist.addAll(wlList);
                    saveAiWhitelist();
                    if (labelView != null) {
                        String wlText = aiWhitelist.isEmpty() ? "AI白名单: 未设置(AI功能已禁用)" : ("AI白名单: 已选" + aiWhitelist.size() + "位联系人/群聊");
                        labelView.setText(wlText);
                        labelView.setTextColor(c(aiWhitelist.isEmpty() ? "FF999999" : "FF5B4C8C"));
                    }
                }
            }, null, null, null);
        }
    });
    btnSubGroup.setOnClickListener(new android.view.View.OnClickListener() {
        public void onClick(android.view.View vv) {
            safeDismiss(subDlg);
            Set<String> init = new java.util.HashSet(wlList);
            showSelectDialog(c, "选择群聊", true, init, wlList, new Runnable() {
                public void run() {
                    aiWhitelist.clear();
                    aiWhitelist.addAll(wlList);
                    saveAiWhitelist();
                    if (labelView != null) {
                        String wlText = aiWhitelist.isEmpty() ? "AI白名单: 未设置(AI功能已禁用)" : ("AI白名单: 已选" + aiWhitelist.size() + "位联系人/群聊");
                        labelView.setText(wlText);
                        labelView.setTextColor(c(aiWhitelist.isEmpty() ? "FF999999" : "FF5B4C8C"));
                    }
                }
            }, null, null, null);
        }
    });
}

void showUnreadStatsViewer() {
    android.app.Activity act = getTopActivity();
    if (act == null) return;
    android.content.Context c = act;

    android.widget.LinearLayout root = new android.widget.LinearLayout(c);
    root.setOrientation(1);
    root.setPadding(dp(14), dp(10), dp(14), dp(10));

    android.widget.TextView tvTitle = T(c, "未读消息统计", 17, c("FF5B4C8C"), true);
    tvTitle.setGravity(android.view.Gravity.CENTER);
    tvTitle.setPadding(0, 0, 0, dp(8));
    root.addView(tvTitle);

    android.widget.ScrollView sv = new android.widget.ScrollView(c);
    android.widget.LinearLayout.LayoutParams svlp = new android.widget.LinearLayout.LayoutParams(-1, 0, 1);
    sv.setLayoutParams(svlp);
    sv.setFillViewport(true);
    android.widget.LinearLayout content = new android.widget.LinearLayout(c);
    content.setOrientation(1);

    int unreadTotal = 0;
    if (unreadMessageCounts.isEmpty()) {
        content.addView(T(c, "(暂无未读消息统计数据)", 12, c("FF999999"), false));
    } else {
        Object[] keys = unreadMessageCounts.keySet().toArray();
        for (int i = 0; i < keys.length; i++) {
            String talker = String.valueOf(keys[i]);
            int cnt = 0;
            Object v = unreadMessageCounts.get(talker);
            if (v instanceof Integer) cnt = ((Integer)v).intValue();
            unreadTotal += cnt;

            String displayName = getFriendName(talker);
            if (displayName == null || displayName.isEmpty()) displayName = getFriendNickName(talker);
            if (displayName == null || displayName.isEmpty()) displayName = talker;

            android.widget.LinearLayout row = new android.widget.LinearLayout(c);
            row.setOrientation(0);
            row.setGravity(android.view.Gravity.CENTER_VERTICAL);
            row.setPadding(0, dp(3), 0, dp(3));

            int avatarSize = dp(28);
            android.widget.ImageView ivAvatar = new android.widget.ImageView(c);
            ivAvatar.setScaleType(android.widget.ImageView.ScaleType.CENTER_CROP);
            android.widget.LinearLayout.LayoutParams avlp = new android.widget.LinearLayout.LayoutParams(avatarSize, avatarSize);
            avlp.rightMargin = dp(8);
            ivAvatar.setLayoutParams(avlp);
            GradientDrawable avBg = new GradientDrawable();
            avBg.setCornerRadius(avatarSize);
            avBg.setColor(c("FFD8D8E8"));
            ivAvatar.setBackgroundDrawable(avBg);
            row.addView(ivAvatar);
            loadAvatarAsync(ivAvatar, talker);

            android.widget.TextView tvName = new android.widget.TextView(c);
            tvName.setText(displayName);
            tvName.setTextSize(12);
            tvName.setTextColor(c("FF1E293B"));
            tvName.setSingleLine(true);
            tvName.setEllipsize(android.text.TextUtils.TruncateAt.END);
            android.widget.LinearLayout.LayoutParams np = new android.widget.LinearLayout.LayoutParams(0, -2, 1);
            tvName.setLayoutParams(np);
            row.addView(tvName);

            android.widget.TextView tvCnt = new android.widget.TextView(c);
            tvCnt.setText(cnt + " 条");
            tvCnt.setTextSize(11);
            tvCnt.setTextColor(c("FF4A5568"));
            tvCnt.setPadding(dp(8), 0, dp(8), 0);
            row.addView(tvCnt);

            final String ftalker = talker;
            android.widget.Button btnDetail = new android.widget.Button(c);
            btnDetail.setText("详情");
            btnDetail.setTextSize(10);
            btnDetail.setAllCaps(false);
            btnDetail.setBackgroundDrawable(createGlassBtnBg(4, "00FFFFFF", "FF1E293B", "FF5B4C8C"));
            btnDetail.setTextColor(c("FF1E293B"));
            btnDetail.setPadding(dp(8), dp(2), dp(8), dp(2));
            btnDetail.setOnClickListener(new android.view.View.OnClickListener() {
                public void onClick(android.view.View v) {
                    toast("联系人详情功能暂不可用");
                }
            });
            row.addView(btnDetail);

            content.addView(row);
            content.addView(SP(c, 2));
        }
    }

    sv.addView(content);
    root.addView(sv);
    root.addView(SP(c, 6));

    android.widget.TextView tvSummary = T(c, "总计 " + unreadTotal + " 条未读消息", 11, c("FF4A5568"), false);
    tvSummary.setGravity(android.view.Gravity.CENTER);
    root.addView(tvSummary);
    root.addView(SP(c, 8));

    android.widget.TextView tvDesc = T(c, "统计各联系人/群聊中尚未读取的消息数量。\n开启后持续记录，关闭即停止。", 10, c("FF999999"), false);
    tvDesc.setGravity(android.view.Gravity.CENTER);
    root.addView(tvDesc);

    final android.app.Dialog dlg = MD(c, root, 0.92, 0);

    android.widget.Button btnBackUnread = new android.widget.Button(c);
    btnBackUnread.setText("返回");
    btnBackUnread.setTextSize(12);
    btnBackUnread.setAllCaps(false);
    btnBackUnread.setBackgroundDrawable(createOutlineBtnBg(8));
    btnBackUnread.setTextColor(c("FF1E293B"));
    btnBackUnread.setPadding(dp(14), dp(6), dp(14), dp(6));
    root.addView(btnBackUnread);
    btnBackUnread.setOnClickListener(new android.view.View.OnClickListener() {
        public void onClick(android.view.View v) { safeDismiss(dlg); }
    });

    dlg.show();
}

void showRecallLogViewer() {
    android.app.Activity act = getTopActivity();
    if (act == null) return;
    android.content.Context c = act;

    android.widget.LinearLayout root = new android.widget.LinearLayout(c);
    root.setOrientation(1);
    root.setPadding(dp(14), dp(10), dp(14), dp(10));

    android.widget.TextView tvTitle = T(c, "撤回记录查看", 17, c("FF5B4C8C"), true);
    tvTitle.setGravity(android.view.Gravity.CENTER);
    tvTitle.setPadding(0, 0, 0, dp(8));
    root.addView(tvTitle);

    android.widget.ScrollView sv = new android.widget.ScrollView(c);
    android.widget.LinearLayout.LayoutParams svlp = new android.widget.LinearLayout.LayoutParams(-1, 0, 1);
    sv.setLayoutParams(svlp);
    sv.setFillViewport(true);
    android.widget.LinearLayout content = new android.widget.LinearLayout(c);
    content.setOrientation(1);

    int recallTotal = recallLogMap.size();
    if (recallLogMap.isEmpty()) {
        content.addView(T(c, "(暂无撤回消息记录)", 12, c("FF999999"), false));
    } else {
        Object[] recKeys = recallLogMap.keySet().toArray();
        for (int i = 0; i < recKeys.length; i++) {
            String msgId = String.valueOf(recKeys[i]);
            String raw = String.valueOf(recallLogMap.get(msgId));
            int delim = raw.indexOf(": ");
            String recTalker = delim > 0 ? raw.substring(0, delim) : "";
            String recContent = delim > 0 ? raw.substring(delim + 2) : raw;

            String recDisplay = getFriendName(recTalker);
            if (recDisplay == null || recDisplay.isEmpty()) recDisplay = getFriendNickName(recTalker);
            if (recDisplay == null || recDisplay.isEmpty()) recDisplay = recTalker;

            android.widget.LinearLayout recRow = new android.widget.LinearLayout(c);
            recRow.setOrientation(0);
            recRow.setPadding(0, dp(3), 0, dp(3));

            int avSize = dp(24);
            android.widget.ImageView ivAv = new android.widget.ImageView(c);
            ivAv.setScaleType(android.widget.ImageView.ScaleType.CENTER_CROP);
            android.widget.LinearLayout.LayoutParams avlp2 = new android.widget.LinearLayout.LayoutParams(avSize, avSize);
            avlp2.rightMargin = dp(8);
            ivAv.setLayoutParams(avlp2);
            GradientDrawable avBg2 = new GradientDrawable();
            avBg2.setCornerRadius(avSize);
            avBg2.setColor(c("FFD8D8E8"));
            ivAv.setBackgroundDrawable(avBg2);
            recRow.addView(ivAv);
            loadAvatarAsync(ivAv, recTalker);

            android.widget.LinearLayout col = new android.widget.LinearLayout(c);
            col.setOrientation(1);
            android.widget.LinearLayout.LayoutParams colp = new android.widget.LinearLayout.LayoutParams(0, -2, 1);
            col.setLayoutParams(colp);

            android.widget.TextView tvSender = new android.widget.TextView(c);
            tvSender.setText("发送者: " + recDisplay);
            tvSender.setTextSize(11);
            tvSender.setTextColor(c("FF5B4C8C"));
            col.addView(tvSender);

            android.widget.TextView tvContent = new android.widget.TextView(c);
            tvContent.setText(recContent.length() > 80 ? recContent.substring(0, 80) + "..." : recContent);
            tvContent.setTextSize(11);
            tvContent.setTextColor(c("FF4A5568"));
            col.addView(tvContent);

            recRow.addView(col);
            content.addView(recRow);
            content.addView(SP(c, 2));
        }
    }

    sv.addView(content);
    root.addView(sv);
    root.addView(SP(c, 6));

    android.widget.TextView tvSummary = T(c, "累计拦截 " + recallTotal + " 条撤回消息", 11, c("FF4A5568"), false);
    tvSummary.setGravity(android.view.Gravity.CENTER);
    root.addView(tvSummary);
    root.addView(SP(c, 8));

    android.widget.LinearLayout btnRow = new android.widget.LinearLayout(c);
    btnRow.setOrientation(0);
    btnRow.setGravity(android.view.Gravity.CENTER);

    android.widget.Button btnClear = new android.widget.Button(c);
    btnClear.setText("清空记录");
    btnClear.setTextSize(12);
    btnClear.setAllCaps(false);
    btnClear.setBackgroundDrawable(createDangerBtnBg(8));
    btnClear.setTextColor(c("FF1E293B"));
    btnClear.setPadding(dp(14), dp(5), dp(14), dp(5));
    btnClear.setOnClickListener(new android.view.View.OnClickListener() {
        public void onClick(android.view.View v) {
            recallLogMap.clear();
            toast("撤回记录已清空");
            safeDismiss(dlg);
        }
    });
    btnRow.addView(btnClear);
    root.addView(btnRow);
    root.addView(SP(c, 6));

    android.widget.TextView tvDesc = T(c, "记录并查看群聊中被撤回的消息内容。\n开启后持续监控，关闭即停止。", 10, c("FF999999"), false);
    tvDesc.setGravity(android.view.Gravity.CENTER);
    root.addView(tvDesc);

    final android.app.Dialog dlg = MD(c, root, 0.92, 0);
    dlg.show();
}


void showAIToolbox() {
    android.app.Activity act = getTopActivity();
    if (act == null) return;
    android.content.Context c = act;

    if (deepseekApiKey.isEmpty()) { toast("请先配置DeepSeek API Key"); return; }
    if (!masterSwitch) { toast("AI总开关未开启"); return; }
    if (aiWhitelist.isEmpty()) { toast("AI白名单为空，AI功能已禁用"); return; }

    android.widget.LinearLayout root = new android.widget.LinearLayout(c);
    root.setOrientation(1);
    root.setPadding(dp(14), dp(12), dp(14), dp(12));

    android.widget.TextView tvTitle = T(c, "AI 工具箱", 17, c("FF5B4C8C"), true);
    tvTitle.setGravity(android.view.Gravity.CENTER);
    tvTitle.setPadding(0, 0, 0, dp(8));
    root.addView(tvTitle);

    android.widget.TextView tvDesc = T(c, "输入文本内容，选择下方功能按钮调用 AI 处理", 11, c("FF4A5B78"), false);
    tvDesc.setGravity(android.view.Gravity.CENTER);
    tvDesc.setPadding(0, 0, 0, dp(6));
    root.addView(tvDesc);

    android.widget.EditText etInput = new android.widget.EditText(c);
    etInput.setHint("输入要处理的内容...");
    etInput.setTextSize(13);
    etInput.setTextColor(c("FF1E293B"));
    etInput.setHintTextColor(c("FF7A8BA0"));
    etInput.setBackgroundDrawable(createInputBg());
    etInput.setPadding(dp(8), dp(6), dp(8), dp(6));
    etInput.setMinLines(3);
    root.addView(etInput);
    root.addView(SP(c, 4));

    android.widget.EditText etResult = new android.widget.EditText(c);
    etResult.setHint("AI 回复结果...");
    etResult.setTextSize(12);
    etResult.setTextColor(c("FF1E293B"));
    etResult.setHintTextColor(c("FF7A8BA0"));
    etResult.setBackgroundDrawable(createInputBg());
    etResult.setPadding(dp(8), dp(6), dp(8), dp(6));
    etResult.setMinLines(5);
    etResult.setFocusable(false);
    root.addView(etResult);
    root.addView(SP(c, 6));

    android.widget.LinearLayout btnGrid = new android.widget.LinearLayout(c);
    btnGrid.setOrientation(0);
    btnGrid.setGravity(android.view.Gravity.CENTER);

    String[] toolLabels = {"翻译", "摘要", "润色", "问答", "扩写"};
    String[] toolColors = {"FF7C6CD0", "FF50A870", "FFD06068", "FF4A90D8", "FFE08840"};

    for (int i = 0; i < toolLabels.length; i++) {
        android.widget.Button btn = new android.widget.Button(c);
        btn.setText(toolLabels[i]);
        btn.setTextSize(11);
        btn.setAllCaps(false);
        btn.setBackgroundDrawable(createGlassBtnBg(6, "00FFFFFF", "FF1E293B", toolColors[i]));
        btn.setTextColor(c("FF1E293B"));
        btn.setPadding(dp(10), dp(4), dp(10), dp(4));
        final String prompt = toolLabels[i];
        btn.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(android.view.View v) {
                String input = etInput.getText().toString().trim();
                if (input.isEmpty()) { toast("请先输入内容"); return; }
                String sysPrompt;
                if (prompt.equals("翻译")) sysPrompt = "翻译为中文，只输出译文";
                else if (prompt.equals("摘要")) sysPrompt = "用一句话总结";
                else if (prompt.equals("润色")) sysPrompt = "润色文本使其更流畅自然";
                else if (prompt.equals("问答")) sysPrompt = "简洁准确回答问题";
                else sysPrompt = "根据内容扩写，保持风格一致";
                String result = callDeepSeekApi(sysPrompt, input);
                if (result != null) etResult.setText(result.trim());
                else etResult.setText("调用失败，请检查API Key");
            }
        });
        btnGrid.addView(btn);
        if (i < toolLabels.length - 1) btnGrid.addView(SP(c, 4));
    }
    root.addView(btnGrid);
    root.addView(SP(c, 8));

    android.widget.LinearLayout btnRow = new android.widget.LinearLayout(c);
    btnRow.setOrientation(0);
    btnRow.setGravity(android.view.Gravity.CENTER_VERTICAL);
    btnRow.setLayoutParams(new android.widget.LinearLayout.LayoutParams(-1, -2));

    android.widget.LinearLayout.LayoutParams spacer1 = new android.widget.LinearLayout.LayoutParams(0, 1, 1);
    btnRow.addView(SP(c, 0), spacer1);

    android.widget.Button btnBack = new android.widget.Button(c);
    btnBack.setText("返回");
    btnBack.setTextSize(13);
    btnBack.setAllCaps(false);
    btnBack.setBackgroundDrawable(createOutlineBtnBg(8));
    btnBack.setTextColor(c("FF1E293B"));
    btnBack.setPadding(dp(18), dp(5), dp(18), dp(5));
    btnRow.addView(btnBack);
    root.addView(btnRow);

    final android.app.Dialog dlg = MD(c, root, 0.88, 0);

    btnBack.setOnClickListener(new android.view.View.OnClickListener() {
        public void onClick(android.view.View v) { safeDismiss(dlg); }
    });

    dlg.show();
}


void showControlPanel() {
    android.app.Activity act = getTopActivity();
    if (act == null) {
        toast("无法获取顶层 Activity，请重新打开微信后再试");
        return;
    }
    android.content.Context c = act;

    loadConfig();
    final boolean[] localMasterSwitch = {masterSwitch};
    final List localWhitelist = new ArrayList(WHITE_LIST);

    android.widget.LinearLayout root = new android.widget.LinearLayout(c);
    root.setOrientation(1);
    root.setGravity(android.view.Gravity.CENTER);
    root.setPadding(dp(12), dp(8), dp(12), dp(8));
    root.setBackgroundDrawable(createGlassBg(0));

    // ========== 标题栏 ==========
    android.widget.LinearLayout titleRow = new android.widget.LinearLayout(c);
    titleRow.setOrientation(0);
    titleRow.setGravity(android.view.Gravity.CENTER);
    titleRow.setLayoutParams(new android.widget.LinearLayout.LayoutParams(-1, -2));
    titleRow.setPadding(dp(6), dp(6), dp(6), dp(6));
    titleRow.setBackgroundDrawable(createGlassBg(12));

    android.widget.TextView tvTitle = T(c, "AI功能控制面板", 18, c("FF5B4C8C"), true);
    tvTitle.setGravity(android.view.Gravity.CENTER);
    titleRow.addView(tvTitle);

    root.addView(titleRow);

    // Key状态变量(仅用于回调引用,不显示在主界面)
    final android.widget.TextView tvPyStatus = new android.widget.TextView(c);
    final android.widget.TextView tvWsStatus = new android.widget.TextView(c);
    final android.widget.TextView tvKeyConfigStatus = new android.widget.TextView(c);

    root.addView(SP(c, dp(3)));

    // ========== 模块总开关卡片 ==========
    android.widget.LinearLayout cardMasterSwitch = new android.widget.LinearLayout(c);
    cardMasterSwitch.setOrientation(1);
    cardMasterSwitch.setGravity(android.view.Gravity.CENTER);
    cardMasterSwitch.setPadding(dp(7), dp(4), dp(7), dp(4));
    cardMasterSwitch.setBackgroundDrawable(createCardBg(10));

    android.widget.LinearLayout rowMasterSwitch = new android.widget.LinearLayout(c);
    rowMasterSwitch.setOrientation(0);
    rowMasterSwitch.setLayoutParams(new android.widget.LinearLayout.LayoutParams(-1, -2));
    rowMasterSwitch.setGravity(android.view.Gravity.CENTER_VERTICAL);
    rowMasterSwitch.setPadding(0, dp(3), 0, dp(3));

    android.widget.LinearLayout masterTextCol = new android.widget.LinearLayout(c);
    masterTextCol.setOrientation(1);
    masterTextCol.setGravity(android.view.Gravity.CENTER);
    masterTextCol.setLayoutParams(new android.widget.LinearLayout.LayoutParams(0, -2, 1));
    android.widget.TextView tvMasterLabel = T(c, "模块总开关", 13, c("FF212121"), true);
    masterTextCol.addView(tvMasterLabel);
    android.widget.TextView tvMasterHint = T(c, "控制所有AI功能的启用/停用", 10, c("FF4B5562"), false);
    tvMasterHint.setPadding(0, dp(1), 0, 0);
    masterTextCol.addView(tvMasterHint);
    rowMasterSwitch.addView(masterTextCol);

    Switch swMaster = new Switch(c);
    swMaster.setChecked(localMasterSwitch[0]);
    styleSwitch(swMaster);
    swMaster.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton btn, boolean checked) {
            localMasterSwitch[0] = checked;
            masterSwitch = checked;
            saveAllConfig();
        }
    });
    rowMasterSwitch.addView(swMaster);
    cardMasterSwitch.addView(rowMasterSwitch);
    root.addView(cardMasterSwitch);

    // ========== 第三方接口调用配置卡片 (语音Key + DeepSeek 合并) ==========
    android.widget.LinearLayout cardThirdParty = new android.widget.LinearLayout(c);
    cardThirdParty.setOrientation(1);
    cardThirdParty.setGravity(android.view.Gravity.CENTER);
    cardThirdParty.setPadding(dp(7), dp(4), dp(7), dp(4));
    cardThirdParty.setBackgroundDrawable(createCardBg(10));

    android.widget.LinearLayout rowThirdParty = new android.widget.LinearLayout(c);
    rowThirdParty.setOrientation(0);
    rowThirdParty.setLayoutParams(new android.widget.LinearLayout.LayoutParams(-1, -2));
    rowThirdParty.setGravity(android.view.Gravity.CENTER_VERTICAL);
    rowThirdParty.setPadding(0, dp(3), 0, dp(3));

    android.widget.LinearLayout tpTextCol = new android.widget.LinearLayout(c);
    tpTextCol.setOrientation(1);
    tpTextCol.setGravity(android.view.Gravity.CENTER);
    tpTextCol.setLayoutParams(new android.widget.LinearLayout.LayoutParams(0, -2, 1));
    android.widget.TextView tvTPLabel = T(c, "第三方接口调用配置", 13, c("FF212121"), true);
    tpTextCol.addView(tvTPLabel);

    android.text.SpannableStringBuilder keySb = new android.text.SpannableStringBuilder();
    keySb.append("已配置 ");
    String[] kNames = {"DSkey", "方舟key"};
    boolean[] kCfg = {!deepseekApiKey.isEmpty(), !arkApiKey.isEmpty()};
    int kGreen = c("FF009955");
    int kRed = c("FFE53935");
    for (int i = 0; i < kNames.length; i++) {
        int s = keySb.length();
        keySb.append(kNames[i]);
        keySb.setSpan(new android.text.style.ForegroundColorSpan(kCfg[i] ? kGreen : kRed), s, keySb.length(), android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (i < kNames.length - 1) keySb.append(" ");
    }

    final android.widget.TextView[] tvTPHintRef = {new android.widget.TextView(c)};
    tvTPHintRef[0].setText(keySb);
    tvTPHintRef[0].setTextSize(10);
    tvTPHintRef[0].setTextColor(c("FF1D4ED8"));
    tvTPHintRef[0].setPadding(0, dp(1), 0, 0);
    tpTextCol.addView(tvTPHintRef[0]);
    rowThirdParty.addView(tpTextCol);

    android.widget.Button btnTPSettings = new android.widget.Button(c);
    btnTPSettings.setText("设置");
    btnTPSettings.setTextSize(12);
    btnTPSettings.setAllCaps(false);
    btnTPSettings.setBackgroundDrawable(createPrimaryBtnBg(6));
    btnTPSettings.setTextColor(c("FF1E293B"));
    btnTPSettings.setPadding(dp(12), dp(4), dp(12), dp(4));
    btnTPSettings.setOnClickListener(new android.view.View.OnClickListener() {
        public void onClick(android.view.View v) {
            showDeepseekSettingsDialog();
        }
    });
    rowThirdParty.addView(btnTPSettings);
    cardThirdParty.addView(rowThirdParty);
    root.addView(cardThirdParty);

    root.addView(H(c("FFB0B8D0"), 1));
    root.addView(SP(c, dp(3)));

    // ========== AI工具 ==========
    {
        android.widget.LinearLayout titleBar = new android.widget.LinearLayout(c);
        titleBar.setOrientation(0);
        titleBar.setGravity(android.view.Gravity.CENTER);
        titleBar.setPadding(dp(10), dp(6), dp(10), dp(6));
        titleBar.setBackgroundDrawable(createGlassBg(10));
        android.widget.TextView titleText = T(c, "AI工具", 14, c("FF5B4C8C"), true);
        titleBar.addView(titleText);
        root.addView(titleBar);
    }
    root.addView(SP(c, dp(4)));

    // ========== AI 联系人/群聊选择器 ==========
    {
        android.widget.LinearLayout card = new android.widget.LinearLayout(c);
        card.setOrientation(1);
        card.setGravity(android.view.Gravity.CENTER);
        card.setPadding(dp(7), dp(4), dp(7), dp(4));
        card.setBackgroundDrawable(createCardBg(10));

        android.widget.LinearLayout row = new android.widget.LinearLayout(c);
        row.setOrientation(0);
        row.setLayoutParams(new android.widget.LinearLayout.LayoutParams(-1, -2));
        row.setGravity(android.view.Gravity.CENTER_VERTICAL);
        row.setPadding(0, dp(3), 0, dp(3));

        android.widget.LinearLayout col = new android.widget.LinearLayout(c);
        col.setOrientation(1);
        col.setGravity(android.view.Gravity.CENTER);
        col.setLayoutParams(new android.widget.LinearLayout.LayoutParams(0, -2, 1));
        android.widget.TextView lbl = T(c, "AI 对话白名单", 13, c("FF212121"), true);
        col.addView(lbl);
        String wlSummary = aiWhitelist.isEmpty() ? "未设置(AI已禁用)" : ("已选 " + aiWhitelist.size() + " 个");
        final android.widget.TextView hint = T(c, "仅勾选的联系人/群聊触发AI回复", 10, c("FF1D4ED8"), false);
        hint.setPadding(0, dp(1), 0, 0);
        col.addView(hint);
        row.addView(col);

        android.widget.TextView tvCount = new android.widget.TextView(c);
        tvCount.setText(wlSummary);
        tvCount.setTextSize(10);
        tvCount.setTextColor(c("FF4A5568"));
        tvCount.setPadding(0, 0, dp(6), 0);
        row.addView(tvCount);

        android.widget.Button btnWL = new android.widget.Button(c);
        btnWL.setText("选择");
        btnWL.setTextSize(11);
        btnWL.setAllCaps(false);
        btnWL.setBackgroundDrawable(createPrimaryBtnBg(6));
        btnWL.setTextColor(c("FF1E293B"));
        btnWL.setPadding(dp(10), dp(4), dp(10), dp(4));
        row.addView(btnWL);
        card.addView(row);
        root.addView(card);

        btnWL.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(android.view.View v) {
                showAiWhitelistDialog();
            }
        });
    }

    // ========== AI 对话设置卡片 ==========
    {
        android.widget.LinearLayout card = new android.widget.LinearLayout(c);
        card.setOrientation(1);
        card.setGravity(android.view.Gravity.CENTER);
        card.setPadding(dp(7), dp(4), dp(7), dp(4));
        card.setBackgroundDrawable(createCardBg(10));

        android.widget.LinearLayout row = new android.widget.LinearLayout(c);
        row.setOrientation(0);
        row.setLayoutParams(new android.widget.LinearLayout.LayoutParams(-1, -2));
        row.setGravity(android.view.Gravity.CENTER_VERTICAL);
        row.setPadding(0, dp(3), 0, dp(3));

        android.widget.LinearLayout col = new android.widget.LinearLayout(c);
        col.setOrientation(1);
        col.setGravity(android.view.Gravity.CENTER);
        col.setLayoutParams(new android.widget.LinearLayout.LayoutParams(0, -2, 1));
        android.widget.TextView lbl = T(c, "AI 智能对话", 13, c("FF212121"), true);
        col.addView(lbl);
        String aiStr = "";
        if (deepseekAtReply) aiStr += "@回复 ";
        if (deepseekSmartReply) aiStr += "聊天 ";
        if (deepseekTranslate) aiStr += "翻译 ";
        if (deepseekSummary) aiStr += "摘要 ";
        if (deepseekWriting) aiStr += "写作 ";
        if (deepseekQA) aiStr += "问答";
        if (aiStr.isEmpty()) aiStr = "未启用";
        android.widget.TextView hint = T(c, aiStr, 10, c("FF1D4ED8"), false);
        hint.setPadding(0, dp(1), 0, 0);
        col.addView(hint);
        row.addView(col);

        android.widget.Button btnAI = new android.widget.Button(c);
        btnAI.setText("设置");
        btnAI.setTextSize(12);
        btnAI.setAllCaps(false);
        btnAI.setBackgroundDrawable(createPrimaryBtnBg(6));
        btnAI.setTextColor(c("FF1E293B"));
        btnAI.setPadding(dp(12), dp(4), dp(12), dp(4));
        row.addView(btnAI);
        card.addView(row);
        root.addView(card);

        btnAI.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(android.view.View v) { showAIChatSettings(); }
        });
    }

    // ========== 群管理设置卡片 ==========
    {
        android.widget.LinearLayout card = new android.widget.LinearLayout(c);
        card.setOrientation(1);
        card.setGravity(android.view.Gravity.CENTER);
        card.setPadding(dp(7), dp(4), dp(7), dp(4));
        card.setBackgroundDrawable(createCardBg(10));

        android.widget.LinearLayout row = new android.widget.LinearLayout(c);
        row.setOrientation(0);
        row.setLayoutParams(new android.widget.LinearLayout.LayoutParams(-1, -2));
        row.setGravity(android.view.Gravity.CENTER_VERTICAL);
        row.setPadding(0, dp(3), 0, dp(3));

        android.widget.LinearLayout col = new android.widget.LinearLayout(c);
        col.setOrientation(1);
        col.setGravity(android.view.Gravity.CENTER);
        col.setLayoutParams(new android.widget.LinearLayout.LayoutParams(0, -2, 1));
        android.widget.TextView lbl = T(c, "群管理工具", 13, c("FF212121"), true);
        col.addView(lbl);
        String gmStr = "";
        if (welcomeEnabled) gmStr += "欢迎语 ";
        if (keywordReplyEnabled) gmStr += "关键词 ";
        if (antiAdEnabled) gmStr += "防广告 ";
        if (autoKickEnabled) gmStr += "踢人 ";
        if (unreadStatsEnabled) gmStr += "未读 ";
        if (recallLogEnabled) gmStr += "撤回";
        if (gmStr.isEmpty()) gmStr = "未启用";
        android.widget.TextView hint = T(c, gmStr, 10, c("FF1D4ED8"), false);
        hint.setPadding(0, dp(1), 0, 0);
        col.addView(hint);
        row.addView(col);

        android.widget.Button btnGM = new android.widget.Button(c);
        btnGM.setText("设置");
        btnGM.setTextSize(12);
        btnGM.setAllCaps(false);
        btnGM.setBackgroundDrawable(createPrimaryBtnBg(6));
        btnGM.setTextColor(c("FF1E293B"));
        btnGM.setPadding(dp(12), dp(4), dp(12), dp(4));
        row.addView(btnGM);
        card.addView(row);
        root.addView(card);

        btnGM.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(android.view.View v) { showGroupManageSettings(); }
        });
    }

    // ========== 消息增强卡片 ==========
    {
        android.widget.LinearLayout card = new android.widget.LinearLayout(c);
        card.setOrientation(1);
        card.setGravity(android.view.Gravity.CENTER);
        card.setPadding(dp(7), dp(4), dp(7), dp(4));
        card.setBackgroundDrawable(createCardBg(10));

        android.widget.LinearLayout row = new android.widget.LinearLayout(c);
        row.setOrientation(0);
        row.setLayoutParams(new android.widget.LinearLayout.LayoutParams(-1, -2));
        row.setGravity(android.view.Gravity.CENTER_VERTICAL);
        row.setPadding(0, dp(3), 0, dp(3));

        android.widget.LinearLayout col = new android.widget.LinearLayout(c);
        col.setOrientation(1);
        col.setGravity(android.view.Gravity.CENTER);
        col.setLayoutParams(new android.widget.LinearLayout.LayoutParams(0, -2, 1));
        android.widget.TextView lbl = T(c, "消息增强处理", 13, c("FF212121"), true);
        col.addView(lbl);
        String meStr = "";
        if (voiceToTextEnabled) meStr += "语音 ";
        if (linkSummaryEnabled) meStr += "链接 ";
        if (fileClassifyEnabled) meStr += "文件 ";
        if (reminderEnabled) meStr += "提醒";
        if (meStr.isEmpty()) meStr = "未启用";
        android.widget.TextView hint = T(c, meStr, 10, c("FF1D4ED8"), false);
        hint.setPadding(0, dp(1), 0, 0);
        col.addView(hint);
        row.addView(col);

        android.widget.Button btnME = new android.widget.Button(c);
        btnME.setText("设置");
        btnME.setTextSize(12);
        btnME.setAllCaps(false);
        btnME.setBackgroundDrawable(createPrimaryBtnBg(6));
        btnME.setTextColor(c("FF1E293B"));
        btnME.setPadding(dp(12), dp(4), dp(12), dp(4));
        row.addView(btnME);
        card.addView(row);
        root.addView(card);

        btnME.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(android.view.View v) { showMsgEnhanceSettings(); }
        });

        if (aiToolboxEnabled) {
            android.widget.Button btnTB = new android.widget.Button(c);
            btnTB.setText("工具箱");
            btnTB.setTextSize(11);
            btnTB.setAllCaps(false);
            btnTB.setBackgroundDrawable(createCircleBtnBg(6));
            btnTB.setTextColor(c("FF1E293B"));
            btnTB.setPadding(dp(8), dp(4), dp(8), dp(4));
            btnTB.setOnClickListener(new android.view.View.OnClickListener() {
                public void onClick(android.view.View v) { showAIToolbox(); }
            });
            row.addView(btnTB);
        }
    }

    // ========== dlgPanel 提前创建 ==========
    final android.app.Dialog dlgPanel = MD(c, root, 0.92, 0);
    dlgPanel.setCancelable(false);
    dlgPanel.setCanceledOnTouchOutside(false);

    android.widget.LinearLayout bottomBar = new android.widget.LinearLayout(c);
    bottomBar.setOrientation(0);
    bottomBar.setGravity(android.view.Gravity.CENTER_VERTICAL);
    bottomBar.setLayoutParams(new android.widget.LinearLayout.LayoutParams(-1, -2));
    bottomBar.setPadding(0, dp(6), 0, 0);

    android.widget.Button btnClearConfig = new android.widget.Button(c);
    btnClearConfig.setText("清空配置");
    btnClearConfig.setTextSize(13);
    btnClearConfig.setAllCaps(false);
    btnClearConfig.setBackgroundDrawable(createDangerBtnBg(8));
    btnClearConfig.setTextColor(c("FF1E293B"));
    btnClearConfig.setPadding(dp(14), dp(6), dp(14), dp(6));
    btnClearConfig.setOnClickListener(new android.view.View.OnClickListener(){
        public void onClick(android.view.View v){
            android.app.AlertDialog.Builder cb = new android.app.AlertDialog.Builder(c);
            cb.setTitle("确认清空");
            cb.setMessage("确认清空本脚本所有配置？");
            cb.setPositiveButton("确认", new android.content.DialogInterface.OnClickListener(){
                public void onClick(android.content.DialogInterface d, int w){
                    localWhitelist.clear();
                    WHITE_LIST.clear();
                    masterSwitch = true;
                    localMasterSwitch[0] = true;

                    deepseekApiKey = "";
                    deepseekBaseUrl = "https://api.deepseek.com/v1";
                    deepseekModel = "deepseek-v4-pro";
                    deepseekPersona = "你是一个友好的微信助手，请用简洁自然的语言回复。";
                    deepseekAtReply = false;
                    deepseekSmartReply = false;
                    deepseekTranslate = false;
                    deepseekSummary = false;
                    deepseekWriting = false;
                    deepseekQA = false;
                    sensitiveFilterEnabled = false;
                    aiToolboxEnabled = false;
                    arkApiKey = "";
                    arkImageModel = "doubao-seedream-4-5-251128";
                    arkImageSize = "2K";
                    arkImageFormat = "png";
                    arkVideoModel = "doubao-seedance-2-0-260128";
                    arkVideoDuration = 8;
                    arkVideoResolution = "720p";
                    imageGenEnabled = false;
                    videoGenEnabled = false;
                    saveAllConfig();
                    saveWhitelist();
                    if (tvKeyConfigStatus != null) tvKeyConfigStatus.setText(getKeyConfigStatusText());
                    toast("所有配置已清空");
                }
            });
            cb.setNegativeButton("取消", null);
            AlertDialog cd = cb.create();
            cd.setOnShowListener(new android.content.DialogInterface.OnShowListener() { public void onShow(android.content.DialogInterface d) { setupUnifiedDialog(cd); } });
            cd.show();
        }
    });
    bottomBar.addView(btnClearConfig);

    android.widget.LinearLayout.LayoutParams spacerB2 = new android.widget.LinearLayout.LayoutParams(0, 1, 1);
    bottomBar.addView(SP(c, 0), spacerB2);

    android.widget.Button btnCancel = new android.widget.Button(c);
    btnCancel.setText("取消并退出");
    btnCancel.setTextSize(13);
    btnCancel.setAllCaps(false);
    btnCancel.setBackgroundDrawable(createOutlineBtnBg(8));
    btnCancel.setTextColor(c("FF1E293B"));
    btnCancel.setPadding(dp(14), dp(6), dp(14), dp(6));
    btnCancel.setOnClickListener(new android.view.View.OnClickListener(){
        public void onClick(android.view.View v){
            safeDismiss(dlgPanel);
        }
    });
    bottomBar.addView(btnCancel);

    android.widget.Button btnClose = new android.widget.Button(c);
    btnClose.setText("保存退出");
    btnClose.setTextSize(13);
    btnClose.setAllCaps(false);
    btnClose.setBackgroundDrawable(createDangerBtnBg(8));
    btnClose.setTextColor(c("FF1E293B"));
    btnClose.setPadding(dp(14), dp(6), dp(14), dp(6));
    btnClose.setOnClickListener(new android.view.View.OnClickListener(){
        public void onClick(android.view.View v){
            masterSwitch = localMasterSwitch[0];
            WHITE_LIST.clear();
            WHITE_LIST.addAll(localWhitelist);
            saveWhitelist();
            saveAllConfig();
            toast("已保存 " + WHITE_LIST.size() + " 项白名单");
            safeDismiss(dlgPanel);
        }
    });
    bottomBar.addView(btnClose);

    root.addView(bottomBar);

    dlgPanel.show();
}


void showSelectDialog(android.content.Context c, String title, boolean isGroup, Set<String> selectedSet, List targetList, Runnable onSaveCallback, Set<String> hideSet, List overrideList, String confirmText) {
    final Set currentSelected = new HashSet();
    if (selectedSet != null) {
        currentSelected.addAll(selectedSet);
    }
    if (hideSet != null && !hideSet.isEmpty()) log("hideSet=" + hideSet.size() + " 候选=" + (selectedSet != null ? selectedSet.size() : 0));
    final List allIds = new ArrayList();
    final List allNames = new ArrayList();
    final List allObjs = new ArrayList();
    final List allRawUsers = new ArrayList();
    final List allAvatarUrls = new ArrayList();
    final List allRows = new ArrayList();
    final List allCheckboxes = new ArrayList();
    final List allAvatarViews = new ArrayList();
    final List filteredIds = new ArrayList();
    final List filteredRows = new ArrayList();


     android.widget.EditText etSearch = new android.widget.EditText(c);
          etSearch.setHint(isGroup ? "搜索" : "搜索好友");
     etSearch.setSingleLine(true);
     etSearch.setGravity(android.view.Gravity.CENTER);
     etSearch.setPadding(dp(10), dp(8), dp(10), dp(8));
     etSearch.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 14);
     etSearch.setLayoutParams(new android.widget.LinearLayout.LayoutParams(-1, -2));
    GradientDrawable gdSearch = new GradientDrawable();
    gdSearch.setOrientation(GradientDrawable.Orientation.TL_BR);
    gdSearch.setColors(candyColors());
    gdSearch.setCornerRadius(dp(12));
    gdSearch.setStroke(dp(2), c("FFC8C0D8"));
    etSearch.setBackgroundDrawable(gdSearch);


    final android.widget.ListView listView = new android.widget.ListView(c);
    listView.setDivider(new android.graphics.drawable.ColorDrawable(c("FFA0A8C0")));
    listView.setDividerHeight(dp(1));
    GradientDrawable gdList = new GradientDrawable();
    gdList.setOrientation(GradientDrawable.Orientation.TL_BR);
    gdList.setColors(candyColors());
    listView.setBackgroundDrawable(gdList);
    android.widget.LinearLayout.LayoutParams lvParams = new android.widget.LinearLayout.LayoutParams(-1, 0, 1);
    listView.setLayoutParams(lvParams);

    final android.widget.BaseAdapter adapter = new android.widget.BaseAdapter() {
        public int getCount() { return filteredRows.size(); }
        public Object getItem(int pos) { return null; }
        public long getItemId(int pos) { return pos; }
        public android.view.View getView(int pos, android.view.View v, android.view.ViewGroup p) {
            return (android.view.View) filteredRows.get(pos);
        }
    };
    listView.setAdapter(adapter);

    final Runnable updateList = new Runnable() {
        public void run() {
            String s = etSearch.getText().toString().toLowerCase();
            filteredIds.clear();
            filteredRows.clear();
            for (int i = 0; i < allIds.size(); i++) {
                String id = (String) allIds.get(i);
                String n = (String) allNames.get(i);
                if (s.isEmpty() || n.toLowerCase().contains(s) || id.toLowerCase().contains(s)) {
                    filteredIds.add(id);
                    filteredRows.add(allRows.get(i));
                }
            }
            adapter.notifyDataSetChanged();
        }
    };

    etSearch.addTextChangedListener(new android.text.TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        public void onTextChanged(CharSequence s, int start, int before, int count) { updateList.run(); }
        public void afterTextChanged(android.text.Editable s) {}
    });

    listView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
        public void onItemClick(android.widget.AdapterView parent, android.view.View view, int pos, long id) {
            if (pos < filteredIds.size()) {
                String itemId = (String) filteredIds.get(pos);
                if (currentSelected.contains(itemId)) {
                    currentSelected.remove(itemId);
                } else {
                    currentSelected.add(itemId);
                }
                if (isGroup) {
                    for (int i = 0; i < allIds.size(); i++) {
                        if (itemId.equals(allIds.get(i))) {
                            Object cb = allCheckboxes.get(i);
                            if (cb != null) ((android.widget.CheckBox) cb).setChecked(currentSelected.contains(itemId));
                            break;
                        }
                    }
                } else {
                    for (int i = 0; i < allIds.size(); i++) {
                        if (itemId.equals(allIds.get(i))) {
                            ((android.widget.LinearLayout) allRows.get(i)).setBackgroundColor(currentSelected.contains(itemId) ? c("FFEBE4F7") : 0x00000000);
                            break;
                        }
                    }
                }
                updateCount.run();
            }
        }
    });

    android.widget.LinearLayout dialogLayout = new android.widget.LinearLayout(c);
    dialogLayout.setOrientation(1);
    dialogLayout.setPadding(dp(14), dp(14), dp(14), dp(4));

    android.widget.LinearLayout titleRow = new android.widget.LinearLayout(c);
    titleRow.setOrientation(0);
    titleRow.setGravity(android.view.Gravity.CENTER_VERTICAL);
    titleRow.setPadding(0, 0, 0, dp(10));

    android.widget.TextView tvTitle = new android.widget.TextView(c);
    tvTitle.setText(title);
    tvTitle.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 16);
    tvTitle.setTextColor(c("FF1E293B"));
    tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
    titleRow.addView(tvTitle);

    android.widget.LinearLayout.LayoutParams titleSpacer = new android.widget.LinearLayout.LayoutParams(0, 1, 1);
    titleRow.addView(new android.view.View(c), titleSpacer);

    final android.widget.TextView tvCount = new android.widget.TextView(c);
    tvCount.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 12);
    tvCount.setTextColor(c("FF5B4C8C"));
    tvCount.setGravity(android.view.Gravity.CENTER_VERTICAL | android.view.Gravity.RIGHT);
    titleRow.addView(tvCount);

    final Runnable[] updateCountHolder = new Runnable[1];

    final Runnable updateCount = new Runnable() {
        public void run() {
            int n = currentSelected.size();
            tvCount.setText(n > 0 ? "已勾选" + n + "个" : "");
        }
    };
    updateCountHolder[0] = updateCount;
    updateCount.run();

    dialogLayout.addView(titleRow);

    dialogLayout.addView(etSearch);
    dialogLayout.addView(listView);

    android.widget.LinearLayout btnRow = new android.widget.LinearLayout(c);
    btnRow.setOrientation(0);
    btnRow.setGravity(android.view.Gravity.CENTER_VERTICAL);
    btnRow.setPadding(0, dp(10), 0, 0);

    java.util.concurrent.atomic.AtomicReference dlgRef = new java.util.concurrent.atomic.AtomicReference();
    final android.widget.Button btnSelectAll = new android.widget.Button(c);
    btnSelectAll.setText("全部勾选");
    btnSelectAll.setTextColor(c("FF1E293B"));
    btnSelectAll.setTextSize(13);
    btnSelectAll.setAllCaps(false);
    btnSelectAll.setBackgroundDrawable(createOutlineBtnBg(8));
    btnSelectAll.setPadding(dp(22), dp(6), dp(22), dp(6));
    btnSelectAll.setOnClickListener(new android.view.View.OnClickListener() {
        public void onClick(android.view.View v) {
            if ("全部勾选".equals(btnSelectAll.getText().toString())) {
                for (int i = 0; i < filteredIds.size(); i++) {
                    currentSelected.add((String) filteredIds.get(i));
                }
                for (int i = 0; i < allIds.size(); i++) {
                    if (isGroup) {
                        Object cb = allCheckboxes.get(i);
                        if (cb != null) ((android.widget.CheckBox) cb).setChecked(currentSelected.contains(allIds.get(i)));
                    } else {
                        ((android.widget.LinearLayout) allRows.get(i)).setBackgroundColor(c("FFEBE4F7"));
                    }
                }
                btnSelectAll.setText("取消全选");
            } else {
                currentSelected.clear();
                for (int i = 0; i < allIds.size(); i++) {
                    if (isGroup) {
                        Object cb = allCheckboxes.get(i);
                        if (cb != null) ((android.widget.CheckBox) cb).setChecked(false);
                    } else {
                        ((android.widget.LinearLayout) allRows.get(i)).setBackgroundColor(0x00000000);
                    }
                }
                btnSelectAll.setText("全部勾选");
            }
            if (updateCountHolder[0] != null) updateCountHolder[0].run();
        }
    });
    btnRow.addView(btnSelectAll);

    android.widget.LinearLayout.LayoutParams spacer1 = new android.widget.LinearLayout.LayoutParams(0, 1, 1);
    btnRow.addView(new android.view.View(c), spacer1);

    android.widget.Button btnConfirm = new android.widget.Button(c);
    btnConfirm.setText(confirmText != null ? confirmText : "保存");
    btnConfirm.setTextColor(c("FF1E293B"));
    btnConfirm.setTextSize(13);
    btnConfirm.setAllCaps(false);
    btnConfirm.setBackgroundDrawable(createPrimaryBtnBg(8));
    btnConfirm.setPadding(dp(22), dp(6), dp(22), dp(6));
    btnConfirm.setOnClickListener(new android.view.View.OnClickListener() {
        public void onClick(android.view.View v) {
            targetList.clear();
            targetList.addAll(currentSelected);
            toast("已保存 " + currentSelected.size() + " 项");
            Object dlg = dlgRef.get();
            if (dlg != null) try { ((android.app.Dialog)dlg).dismiss(); } catch (Exception e) {}
            if (onSaveCallback != null) onSaveCallback.run();
        }
    });
    btnRow.addView(btnConfirm);

    android.widget.LinearLayout.LayoutParams spacer2 = new android.widget.LinearLayout.LayoutParams(0, 1, 1);
    btnRow.addView(new android.view.View(c), spacer2);

    android.widget.Button btnBack = new android.widget.Button(c);
    btnBack.setText("返回");
    btnBack.setTextColor(c("FF1E293B"));
    btnBack.setTextSize(13);
    btnBack.setAllCaps(false);
    btnBack.setBackgroundDrawable(createOutlineBtnBg(8));
    btnBack.setPadding(dp(22), dp(6), dp(22), dp(6));
    btnBack.setOnClickListener(new android.view.View.OnClickListener() {
        public void onClick(android.view.View v) {
            Object dlg = dlgRef.get();
            if (dlg != null) try { ((android.app.Dialog)dlg).dismiss(); } catch (Exception e) {}
        }
    });
    btnRow.addView(btnBack);

    dialogLayout.addView(btnRow);

    int screenHeight = c.getResources().getDisplayMetrics().heightPixels;
    int dialogHeightDp = (int) (screenHeight * 0.8f / c.getResources().getDisplayMetrics().density);
    android.app.Dialog dlg = new android.app.Dialog(c);
    dlg.setCancelable(true);
    dlg.setCanceledOnTouchOutside(true);
    dlg.setContentView(dialogLayout);
    Window w = dlg.getWindow();
    if (w != null) {
        GradientDrawable gd = new GradientDrawable();
        gd.setOrientation(GradientDrawable.Orientation.TL_BR);
        gd.setColors(candyColors());
        gd.setCornerRadius(dp(16));
        gd.setStroke(dp(1), c("FFC8C0D8"));
        w.setBackgroundDrawable(gd);
        w.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.width = (int) (c.getResources().getDisplayMetrics().widthPixels * 0.92);
        lp.height = dp(dialogHeightDp);
        w.setAttributes(lp);
    }
    dlgRef.set(dlg);
    dlg.show();

    mainHandler.post(new Runnable() {
        public void run() {
             final Object preloadList = overrideList != null ? overrideList : (isGroup ? getGroupList() : getFriendList());
            new Thread(new Runnable() {
                public void run() {
                    try {
                        if (preloadList != null) {
                            List pl = (List) preloadList;
                            for (int i = 0; i < pl.size(); i++) {
                                Object obj = pl.get(i);
                                String wxid = resolveObjWxid(obj);
                                if (wxid == null || wxid.isEmpty()) continue;
                                if (hideSet != null && hideSet.contains(wxid)) { log("HIDE " + wxid); continue; }
                                String name = null;
                                if (isGroup) {
                                    name = getFriendName(wxid);
                                    if (name == null || name.isEmpty()) name = getFriendNickName(wxid);
                                } else {
                                    String remark = getFriendRemarkName(wxid);
                                    String nick = getFriendNickName(wxid);
                                    if (remark != null && !remark.isEmpty()) name = remark;
                                    else if (nick != null && !nick.isEmpty()) name = nick;
                                }
                                if (name == null || name.isEmpty()) name = wxid;
                                allIds.add(wxid);
                                allNames.add(name);
                                allObjs.add(obj);
                                String rawUser = null;
                                try { rawUser = (String) obj.getClass().getMethod("getUsername").invoke(obj); } catch (Exception e) {}
                                allRawUsers.add(rawUser != null && !rawUser.equals(wxid) ? rawUser : null);
                            }
                        }
                    } catch (Exception e) {
                        log(":" + e.getMessage());
                    }
                    mainHandler.post(new Runnable() {
                        public void run() {
                            try {
                                float density = c.getResources().getDisplayMetrics().density;
                                int avatarS = (int)(32 * density + 0.5f);
                                for (int i = 0; i < allIds.size(); i++) {
                                    String wxid = (String) allIds.get(i);
                                    String name = (String) allNames.get(i);

                                    android.widget.LinearLayout row = new android.widget.LinearLayout(c);
                                    row.setOrientation(0);
                                    row.setGravity(android.view.Gravity.CENTER_VERTICAL);
                                    row.setPadding(dp(7), dp(3), dp(7), dp(3));
                                    row.setLayoutParams(new android.widget.AbsListView.LayoutParams(
                                        android.widget.AbsListView.LayoutParams.MATCH_PARENT,
                                        android.widget.AbsListView.LayoutParams.WRAP_CONTENT));

                                    android.widget.CheckBox cb = null;
                                    if (isGroup) {
                                        cb = new android.widget.CheckBox(c);
                                        cb.setClickable(false);
                                        cb.setFocusable(false);
                                        cb.setChecked(currentSelected.contains(wxid));
                                        row.addView(cb);
                                    }
                                    allCheckboxes.add(cb);

                                    android.widget.FrameLayout avFrame = new android.widget.FrameLayout(c);
                                    android.widget.LinearLayout.LayoutParams afp = new android.widget.LinearLayout.LayoutParams(avatarS, avatarS);
                                    afp.leftMargin = dp(8);
                                    avFrame.setLayoutParams(afp);

                                    android.widget.ImageView iv = new android.widget.ImageView(c);
                                    iv.setScaleType(android.widget.ImageView.ScaleType.CENTER_CROP);
                                    avFrame.addView(iv, new android.widget.FrameLayout.LayoutParams(avatarS, avatarS, android.view.Gravity.CENTER));
                                    row.addView(avFrame);

                                    android.widget.TextView tvN = new android.widget.TextView(c);
                                    tvN.setText(name);
                                    tvN.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 13);
                                    tvN.setTextColor(c("FF1E293B"));
                                    tvN.setSingleLine(true);
                                    tvN.setEllipsize(android.text.TextUtils.TruncateAt.END);
                                    android.widget.LinearLayout.LayoutParams np = new android.widget.LinearLayout.LayoutParams(
                                        0, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, 1);
                                    np.leftMargin = dp(10);
                                    tvN.setLayoutParams(np);
                                    row.addView(tvN);

                                    allRows.add(row);
                                    allAvatarViews.add(iv);
                                    if (!isGroup && currentSelected.contains(wxid)) row.setBackgroundColor(c("FFEBE4F7"));

                                    Object obj = allObjs.get(i);
                                    String rawUser = (String) allRawUsers.get(i);
                                    String avatarUrl = null;

                                    avatarUrl = getAvatarUrl(wxid, true);
                                    if (avatarUrl == null || avatarUrl.isEmpty()) avatarUrl = getAvatarUrl(wxid);
                                    if ((avatarUrl == null || avatarUrl.isEmpty()) && rawUser != null) {
                                        avatarUrl = getAvatarUrl(rawUser, true);
                                        if (avatarUrl == null || avatarUrl.isEmpty()) avatarUrl = getAvatarUrl(rawUser);
                                    }
                                    if (i == 0) log("URL[" + wxid + "]:" + avatarUrl + ":" + obj.getClass().getSimpleName() + "rawUser:" + rawUser);
                                    allAvatarUrls.add(avatarUrl);
                                }
                                updateList.run();
                                log(":" + allIds.size() + "/" + currentSelected.size());

                                new Thread(new Runnable() {
                                    public void run() {
                                        try {
                                        final android.graphics.Bitmap[] bitmaps = new android.graphics.Bitmap[allAvatarUrls.size()];
                                        String cacheDir = c.getCacheDir() + "/wa_avatars";
                                        new java.io.File(cacheDir).mkdirs();
                                        int ok = 0, fail = 0, cacheHit = 0, wxHit = 0;
                                        String[] wxExts = {".jpg", ".png", "_hd.jpg", "_hd.png", ".jpeg"};
                                        for (int i = 0; i < allAvatarUrls.size(); i++) {
                                            String avatarUrl = (String) allAvatarUrls.get(i);
                                            String wxid = (String) allIds.get(i);
                                            String cachePath = cacheDir + "/" + wxid.replaceAll("[^a-zA-Z0-9_@\\-]", "_") + ".png";
                                            try {
                                                android.graphics.Bitmap bm = null;
                                                if (sAvatarDir != null) {
                                                    for (int ei = 0; ei < wxExts.length && bm == null; ei++) {
                                                        java.io.File af = new java.io.File(sAvatarDir + "/" + wxid + wxExts[ei]);
                                                        if (af.exists()) {
                                                            bm = android.graphics.BitmapFactory.decodeFile(af.getAbsolutePath());
                                                            if (bm != null) { wxHit++; bitmaps[i] = bm; break; }
                                                        }
                                                    }
                                                }
                                                if (bm != null) continue;
                                                java.io.File cf = new java.io.File(cachePath);
                                                if (cf.exists()) {
                                                    bm = android.graphics.BitmapFactory.decodeFile(cachePath);
                                                    if (bm != null) { cacheHit++; bitmaps[i] = bm; continue; }
                                                }
                                                if (avatarUrl == null || avatarUrl.isEmpty()) { fail++; continue; }
                                                if (avatarUrl.startsWith("/")) {
                                                    bm = android.graphics.BitmapFactory.decodeFile(avatarUrl);
                                                } else if (avatarUrl.startsWith("file://")) {
                                                    bm = android.graphics.BitmapFactory.decodeFile(avatarUrl.substring(7));
                                                } else {
                                                    java.net.URL urlObj = new java.net.URL(avatarUrl);
                                                    java.net.HttpURLConnection conn = (java.net.HttpURLConnection) urlObj.openConnection();
                                                    conn.setConnectTimeout(5000);
                                                    conn.setReadTimeout(5000);
                                                    conn.connect();
                                                    java.io.InputStream is = conn.getInputStream();
                                                    bm = android.graphics.BitmapFactory.decodeStream(is);
                                                    is.close();
                                                    conn.disconnect();
                                                }
                                                if (bm != null) {
                                                    ok++;
                                                    try {
                                                        java.io.FileOutputStream fos = new java.io.FileOutputStream(cf);
                                                        bm.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, fos);
                                                        fos.close();
                                                    } catch (Exception e) {}
                                                    bitmaps[i] = bm;
                                                } else { fail++; }
                                            } catch (Exception e) {
                                                fail++;
                                                if (fail <= 5) log("[" + wxid + "]:" + e.getMessage());
                                            }
                                        }
                                        log("ok=" + ok + " wxHit=" + wxHit + " cacheHit=" + cacheHit + " fail=" + fail);
                                        mainHandler.post(new Runnable() {
                                            public void run() {
                                                for (int idx = 0; idx < bitmaps.length; idx++) {
                                                    if (bitmaps[idx] != null) {
                                                        ((android.widget.ImageView) allAvatarViews.get(idx)).setImageBitmap(bitmaps[idx]);
                                                    }
                                                }
                                                log(".");
                                            }
                                        });
                                    } catch (Exception e) {
                                log(":" + e.getMessage());
                            }
        }
    }).start();
                        } catch (Exception e) {
                            log(":" + e.getMessage());
                        }
                    }
                });
            }
        }).start();
}
    });
}

void showAutoAcceptFriendDialog(Context c) {
    final boolean[] localEnable = {autoAcceptFriend};
    final String[] localMsg = {autoAcceptFriendMsg};

    android.widget.LinearLayout root = new android.widget.LinearLayout(c);
    root.setOrientation(1);
    root.setGravity(android.view.Gravity.CENTER);
    root.setPadding(dp(22), dp(16), dp(22), dp(16));

    android.widget.TextView tvTitle = T(c, "自动通过好友", 16, c("FF5B4C8C"), true);
    tvTitle.setGravity(android.view.Gravity.CENTER);
    root.addView(tvTitle);
    root.addView(SP(c, dp(10)));

    android.widget.LinearLayout switchRow = new android.widget.LinearLayout(c);
    switchRow.setOrientation(0);
    switchRow.setGravity(android.view.Gravity.CENTER_VERTICAL);
    switchRow.setLayoutParams(new android.widget.LinearLayout.LayoutParams(-1, -2));
    android.widget.TextView tvSwLabel = T(c, "启用自动通过好友", 13, c("FF212121"), false);
    switchRow.addView(tvSwLabel);
    android.widget.LinearLayout.LayoutParams swSpacer = new android.widget.LinearLayout.LayoutParams(0, 1, 1);
    switchRow.addView(new android.view.View(c), swSpacer);
    android.widget.Switch sw = new android.widget.Switch(c);
    sw.setChecked(localEnable[0]);
    switchRow.addView(sw);
    root.addView(switchRow);
    root.addView(SP(c, dp(8)));

    android.widget.TextView tvMsgLabel = T(c, "欢迎消息内容", 12, c("FF4A5568"), false);
    tvMsgLabel.setPadding(0, 0, 0, dp(4));
    root.addView(tvMsgLabel);

    final android.widget.EditText etMsg = new android.widget.EditText(c);
    etMsg.setText(localMsg[0]);
    etMsg.setTextSize(13);
    etMsg.setTextColor(c("FF1E293B"));
    etMsg.setPadding(dp(10), dp(8), dp(10), dp(8));
    etMsg.setBackgroundDrawable(createGlassBg(8));
    etMsg.setMaxLines(4);
    etMsg.setLayoutParams(new android.widget.LinearLayout.LayoutParams(-1, -2));
    root.addView(etMsg);
    root.addView(SP(c, dp(14)));

    android.widget.LinearLayout btnRow = new android.widget.LinearLayout(c);
    btnRow.setOrientation(0);
    btnRow.setGravity(android.view.Gravity.CENTER_VERTICAL);
    btnRow.setLayoutParams(new android.widget.LinearLayout.LayoutParams(-1, -2));

    android.widget.Button btnSave = new android.widget.Button(c);
    btnSave.setText("保存");
    btnSave.setTextSize(13);
    btnSave.setAllCaps(false);
    btnSave.setBackgroundDrawable(createPrimaryBtnBg(8));
    btnSave.setTextColor(c("FF1E293B"));
    btnSave.setPadding(dp(18), dp(6), dp(18), dp(6));
    btnRow.addView(btnSave);

    android.widget.LinearLayout.LayoutParams spMid = new android.widget.LinearLayout.LayoutParams(0, 1, 1);
    btnRow.addView(new android.view.View(c), spMid);

    android.widget.Button btnBack = new android.widget.Button(c);
    btnBack.setText("返回");
    btnBack.setTextSize(13);
    btnBack.setAllCaps(false);
    btnBack.setBackgroundDrawable(createOutlineBtnBg(8));
    btnBack.setTextColor(c("FF1E293B"));
    btnBack.setPadding(dp(18), dp(6), dp(18), dp(6));
    btnRow.addView(btnBack);
    root.addView(btnRow);

    final android.app.Dialog dlg = MD(c, root, 0.92, 0);
    btnSave.setOnClickListener(new android.view.View.OnClickListener() {
        public void onClick(android.view.View v) {
            autoAcceptFriend = localEnable[0];
            autoAcceptFriendMsg = etMsg.getText().toString().trim();
            if (autoAcceptFriendMsg.isEmpty()) autoAcceptFriendMsg = "你好呀，很高兴认识你!";
            saveAllConfig();
            toast("自动通过好友配置已保存");
            safeDismiss(dlg);
        }
    });
    btnBack.setOnClickListener(new android.view.View.OnClickListener() {
        public void onClick(android.view.View v) {
            safeDismiss(dlg);
        }
    });
    sw.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(android.widget.CompoundButton cb, boolean isChecked) { localEnable[0] = isChecked; }
    });
    dlg.show();
}


boolean isUserInChatroom(String gid, String wxid) {
    try { getFriendDisplayName(wxid, gid); return true; } catch (Exception e) {}
    try { getFriendName(wxid, gid); return true; } catch (Exception e) {}
    try { java.util.List m = getGroupMemberList(gid); return m != null && m.contains(wxid); } catch (Exception e) {}
    return false;
}


// === 文件/文件夹浏览与多选 ===
final String DEFAULT_LAST_FOLDER_SP_AUTO = "last_folder_for_media_auto";
final String ROOT_FOLDER = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();

// 回调接口（必须定义在使用之前）
interface MediaSelectionCallback { void onSelected(ArrayList<String> selectedFiles); }
interface SendAction { void run() throws Exception; }

/* ========== 单例模式文件夹浏览器全局变量 ========== */
AlertDialog gFolderDialogAuto = null;
ArrayAdapter gFolderAdapterAuto = null;
ArrayList gFolderNamesAuto = new ArrayList();
ArrayList gFolderFilesAuto = new ArrayList();
File gCurrentFolderAuto = null;
String gWantedExtFilterAuto = "";
String gCurrentSelectionAuto = "";
MediaSelectionCallback gMediaCallbackAuto = null;
boolean gAllowFolderSelectAuto = false;

// ==========================================
// ========== 🕒 乐少定时群发助手 (多任务增强版) ==========
// ==========================================

// 全局变量 - 发送配置
Set<String> massSendTargetWxids = new HashSet<String>();
int massSendType = 0; // 0:文本, 1:图片, 2:视频, 3:文件, 4:表情
String massSendTextContent = "";
List<String> massSendMediaPaths = new ArrayList<String>();
long massSendInterval = 0; // 发送对象间隔(秒)
long massSendMediaInterval = 0; // 多媒体文件间隔(秒)
int massSendRepeatType = 0; // 0:不重复(单次), 1:每天重复, 2:每周重复
boolean massSendWakeOnTime = false; // 是否到点唤醒微信执行
boolean massSendSendOnTimeout = true; // 任务超时后是否补发

// 定时任务相关 - 改为多任务支持
Map scheduledTasks = new HashMap(); // taskId -> JSONObject
Map scheduledRunnables = new HashMap(); // taskId -> Runnable
Map taskPrewarmRunnables = new HashMap(); // taskId -> Runnable
boolean isTaskRunning = false;
Handler scheduleHandler = new Handler(Looper.getMainLooper());
boolean sendPipelineWarmed = false;
final Object sendWarmLock = new Object();
PowerManager.WakeLock massSendWakeLock = null;
int massSendWakeLockRefs = 0;
final Object massSendWakeLockLock = new Object();

// 常量定义
final int SEND_TYPE_TEXT = 0;
final int SEND_TYPE_IMAGE = 1;
final int SEND_TYPE_VIDEO = 2;
final int SEND_TYPE_FILE = 3;
final int SEND_TYPE_EMOJI = 4;
final int SEND_TYPE_VOICE = 5;
final int SEND_TYPE_MOMENTS_TEXT = 6;      // 朋友圈纯文本
final int SEND_TYPE_MOMENTS_IMAGE = 7;     // 朋友圈图文
final int SEND_TYPE_XML = 8;               // XML消息
final long WAKEUP_LEAD_MS = 1000L;         // 提前唤醒1秒
final int SEND_MAX_RETRY = 3;              // 冷态首发重试次数（全类型）
final long SEND_RETRY_BASE_MS = 2000L;

// 存储Key
final String CONFIG_KEY = "scheduled_send_multi_v2";
final String KEY_LABELS = "saved_target_labels";
final String KEY_TASKS = "scheduled_tasks"; // 存储多任务列表
final String KEY_DEFAULT_SEND_ON_TIMEOUT = "default_send_on_timeout";

// 缓存列表
private List sCachedFriendList = null;
private List sCachedGroupList = null;

// ==========================================
// ========== ♻️ 生命周期与核心逻辑 ==========
// ==========================================

void onMemberChange(String type, String groupWxid, String userWxid, String userName) {
    if (type.equals("join") && blacklistEnabled && blacklistMap.containsKey(userWxid)) {
        try { delChatroomMember(groupWxid, userWxid); } catch (Exception e) { log("黑名单移出失败: " + e.getMessage()); }
        log("黑名单成员尝试进群已自动移出: " + userWxid + " @ " + groupWxid);
        return;
    }
    if (welcomeEnabled && type.equals("join")) {
        sendWelcomeNotice(groupWxid, userWxid);
    }
    if (blacklistEnabled && type.equals("left")) {
        addToBlacklist(userWxid, groupWxid, "被移出/退出群聊");
    }
}

void onNewFriend(String wxid, String ticket, int scene) {
    log("新好友申请: " + wxid + " scene=" + scene);
    if (autoAcceptFriend) {
        try {
            verifyUser(wxid, ticket, scene);
            log("已自动通过好友: " + wxid);
            final String fWxid = wxid;
            delay(2000, new Runnable() { public void run() {
                sendText(fWxid, autoAcceptFriendMsg);
                log("已发送欢迎消息: " + fWxid);
            }});
        } catch (Exception e) {
            log("自动通过好友失败: " + e.getMessage());
        }
    }
}


java.util.Set getGroupMemberWxids(String gid) {
    java.util.Set result = new java.util.HashSet();
    try {
        java.util.List members = getGroupMemberList(gid);
        if (members != null) result.addAll(members);
    } catch (Exception e) {}
    return result;
}

boolean isUserInChatroom(String gid, String wxid) {
    try {
        return getGroupMemberList(gid).contains(wxid);
    } catch (Exception e) {}
    return false;
}

java.util.List checkGroupsForFriend(String wxid) {
    java.util.List notInGroups = new java.util.ArrayList();
    for (int i = 0; i < manualInviteGroups.size(); i++) {
        String gid = (String) manualInviteGroups.get(i);
        try {
            int cnt = getGroupMemberCount(gid);
            if (cnt >= manualInviteMaxMembers) continue;
            if (!isUserInChatroom(gid, wxid)) {
                notInGroups.add(gid);
            }
        } catch (Exception e) {}
    }
    return notInGroups;
}


void openSettings() {
    showControlPanel();
}

/**
 * 插件加载时调用
 * 用于恢复所有未完成的定时任务
 */
public void onLoad() {
    try {
        java.io.File cd = hostContext.getCacheDir();
        if (cd != null) cacheDir = cd.getAbsolutePath();
    } catch (Exception e) {}
    if (cacheDir == null) {
        try {
            java.io.File fd = hostContext.getFilesDir();
            if (fd != null) cacheDir = fd.getAbsolutePath();
        } catch (Exception e2) {}
    }
    if (cacheDir == null) {
        try {
            java.io.File ed = hostContext.getExternalFilesDir(null);
            if (ed != null) cacheDir = ed.getAbsolutePath();
        } catch (Exception e3) {}
    }
    if (cacheDir == null) {
        cacheDir = "/storage/emulated/0/Download/乐少脚本/乐少AI";
    }
    log(":" + cacheDir);
    try {
        java.io.File mmf = new java.io.File("/data/data/com.tencent.mm/MicroMsg");
        if (mmf.exists() && mmf.isDirectory()) {
            java.io.File[] subs = mmf.listFiles();
            if (subs != null) {
                for (int si = 0; si < subs.length; si++) {
                    java.io.File avd = new java.io.File(subs[si], "avatar");
                    if (avd.exists() && avd.isDirectory()) {
                        sAvatarDir = avd.getAbsolutePath();
                        break;
                    }
                }
            }
        }
    } catch (Exception e) {}
    if (sAvatarDir != null) log(":" + sAvatarDir);
    else log("avatar目录未找到");
    loadConfig();
    log("v2.0");


}

public void onUnload() {
    log("插件卸载中");

    contactCache.clear();
    try {
        Iterator it = scheduledRunnables.values().iterator();
        while (it.hasNext()) {
            try { scheduleHandler.removeCallbacks((Runnable) it.next()); } catch (Exception ignored) {}
        }
        scheduledRunnables.clear();
    } catch (Exception ignored) {}
    try {
        Iterator it2 = taskPrewarmRunnables.values().iterator();
        while (it2.hasNext()) {
            try { scheduleHandler.removeCallbacks((Runnable) it2.next()); } catch (Exception ignored) {}
        }
        taskPrewarmRunnables.clear();
    } catch (Exception ignored) {}

}

/**
 * 恢复所有定时任务
 */


/**
 * 【专为语音设计的同步锁】
 */


/**
 * 高精度定时执行（精确到毫秒级，误差<100ms）
 */


/**
 * 精确执行单个任务
 */


/**
 * 计算重复任务的下一次执行时间
 */


/**
 * 格式化周天显示
 */


/**
 * 取消单个任务的定时器
 */


/**
 * 为任务设置系统级闹钟（尽量在后台也能按时唤醒微信）
 */


/**
 * 取消任务对应的系统级闹钟
 */


private Context getBestContext() {
    try {
        Context c = getTopActivity();
        if (c != null) return c;
    } catch (Exception ignored) {}
    try {
        Class at = Class.forName("android.app.ActivityThread");
        java.lang.reflect.Method m = at.getMethod("currentApplication", new Class[0]);
        Object app = m.invoke(null, new Object[0]);
        if (app instanceof Context) return (Context) app;
    } catch (Exception ignored) {}
    return null;
}

private android.content.Intent buildWeChatLaunchIntent(Context ctx) {
    try {
        android.content.Intent it = ctx.getPackageManager().getLaunchIntentForPackage("com.tencent.mm");
        if (it == null) {
            it = new android.content.Intent();
            it.setClassName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
            it.setAction(android.content.Intent.ACTION_MAIN);
            it.addCategory(android.content.Intent.CATEGORY_LAUNCHER);
        }
        it.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                | android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
                | android.content.Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        return it;
    } catch (Exception e) {
        log("构建微信启动Intent失败:" + e.getMessage());
        return null;
    }
}

private android.app.PendingIntent buildTaskAlarmPendingIntent(Context ctx, String taskId, android.content.Intent launchIntent) {
    int flags = (Build.VERSION.SDK_INT >= 23)
            ? (android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE)
            : android.app.PendingIntent.FLAG_UPDATE_CURRENT;
    return android.app.PendingIntent.getActivity(ctx, taskId.hashCode(), launchIntent, flags);
}

/**
 * 保存所有任务到存储
 */


// ==========================================
// ========== 📱 UI 界面逻辑 ==========
// ==========================================


/**
 * 创建并保存新任务
 */


/**
 * 立即执行发送
 */


/**
 * 更新任务统计显示
 */


// ==========================================
// ========== 📋 任务列表管理 ==========
// ==========================================


// ==========================================
// ========== 🕐 时间选择器 (支持秒) ==========
// ==========================================

interface DatePickerCallback {
    void onTimeSelected(long timestamp);
}


// ==========================================
// ========== 🏷️ 标签管理功能 ==========
// ==========================================


// ==========================================
// ========== 👥 目标选择功能 ==========
// ==========================================


// ==========================================
// ========== 📁 媒体选择功能（单例模式） ==========
// ==========================================


private Object[] getMediaSelectTagForMassSend(int type) {
    String extFilter = "";
    switch (type) {
        case SEND_TYPE_IMAGE: extFilter = ".jpg,.png,.jpeg,.gif,.bmp"; break;
        case SEND_TYPE_VIDEO: extFilter = ".mp4"; break;
        case SEND_TYPE_EMOJI: extFilter = ".gif"; break;
        case SEND_TYPE_FILE: extFilter = ""; break;
        case SEND_TYPE_VOICE: extFilter = ".silk"; break;
        case SEND_TYPE_MOMENTS_IMAGE: extFilter = ".jpg,.png,.jpeg,.gif,.bmp"; break;
    }
    return new Object[]{extFilter, false, false, true};
}

// ==========================================
// ========== 👤 联系人和群聊辅助功能 ==========
// ==========================================

private String getContactName(String wxid) {
    try {
        if (wxid.endsWith("@chatroom")) {
            if (sCachedGroupList == null) sCachedGroupList = getGroupList();
            for (int i = 0; i < sCachedGroupList.size(); i++) {
                GroupInfo g = (GroupInfo) sCachedGroupList.get(i);
                if (g.getRoomId().equals(wxid)) return g.getName();
            }
        } else {
            return getFriendDisplayName(wxid);
        }
    } catch (Exception e) {}
    return wxid;
}

private String getFriendDisplayName(String friendWxid) {
    try {
        if (sCachedFriendList == null) sCachedFriendList = getFriendList();
        if (sCachedFriendList != null) {
            for (int i = 0; i < sCachedFriendList.size(); i++) {
                FriendInfo f = (FriendInfo) sCachedFriendList.get(i);
                if (friendWxid.equals(f.getWxid())) {
                    String remark = f.getRemark();
                    if (!TextUtils.isEmpty(remark)) return remark;
                    String nickname = f.getNickname();
                    return TextUtils.isEmpty(nickname) ? friendWxid : nickname;
                }
            }
        }
    } catch (Exception e) {}
    return getFriendName(friendWxid);
}

private String getGroupName(String groupWxid) {
    try {
        if (sCachedGroupList == null) sCachedGroupList = getGroupList();
        if (sCachedGroupList != null) {
            for (int i = 0; i < sCachedGroupList.size(); i++) {
                GroupInfo g = (GroupInfo) sCachedGroupList.get(i);
                if (groupWxid.equals(g.getRoomId())) return g.getName();
            }
        }
    } catch (Exception e) {}
    return "未知群聊";
}

private void updateTargetCountText(TextView tv) {
    if (tv != null) tv.setText("当前已选:" + massSendTargetWxids.size() + "个目标 (好友/群聊混合)");
}

// ==========================================
// ========== 🎨 UI 样式方法 ==========
// ==========================================


private String stripEmoji(String s) {
    if (s == null) return "";
    StringBuilder sb = new StringBuilder();
    int len = s.length();
    for (int i = 0; i < len; i++) {
        int cp = s.codePointAt(i);
        if (Character.isSupplementaryCodePoint(cp)) { i++; }
        if (cp == 0xFE0F || cp == 0x200D) continue;
        if (cp >= 0x1F300 && cp <= 0x1F9FF) continue;
        if (cp >= 0x1FA00 && cp <= 0x1FAFF) continue;
        if (cp >= 0x2600 && cp <= 0x27BF) continue;
        if (cp >= 0x2300 && cp <= 0x23FF) continue;
        if (cp >= 0x2B50 && cp <= 0x2B55) continue;
        if (cp >= 0xFE00 && cp <= 0xFE0F) continue;
        if (cp >= 0x2702 && cp <= 0x27B0) continue;
        sb.appendCodePoint(cp);
    }
    return sb.toString().trim();
}


private TextView createTextView(Context context, String text, int textSize, int paddingBottom) {
    TextView textView = new TextView(context);
    textView.setText(text);
    textView.setTextColor(c("FF1E293B"));
    if (textSize > 0) textView.setTextSize(textSize);
    textView.setPadding(0, 0, 0, paddingBottom);
    return textView;
}


private AlertDialog buildCommonAlertDialog(Context context, String title, View view, String posBtn, DialogInterface.OnClickListener posLsn, String negBtn, DialogInterface.OnClickListener negLsn, String neuBtn, DialogInterface.OnClickListener neuLsn) {
    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    builder.setTitle(title);
    builder.setView(view);
    if (posBtn != null) builder.setPositiveButton(posBtn, posLsn);
    if (negBtn != null) builder.setNegativeButton(negBtn, negLsn);
    if (neuBtn != null) builder.setNeutralButton(neuBtn, neuLsn);
    final AlertDialog dialog = builder.create();
    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
        public void onShow(DialogInterface d) { setupUnifiedDialog(dialog); }
    });
    return dialog;
}

private void setupUnifiedDialog(AlertDialog dialog) {
    GradientDrawable dialogBg = new GradientDrawable();
    dialogBg.setOrientation(GradientDrawable.Orientation.TL_BR);
    dialogBg.setColors(candyColors());
    dialogBg.setCornerRadius(dp(16));
    dialogBg.setStroke(dp(1), c("FFC8C0D8"));
    if (dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawable(dialogBg);
    styleDialogButtons(dialog);
}

private void styleDialogButtons(AlertDialog dialog) {
    Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
    if (positiveButton != null) {
        positiveButton.setTextSize(13);
        positiveButton.setAllCaps(false);
        positiveButton.setTextColor(c("FF1E293B"));
        positiveButton.setBackgroundDrawable(createPrimaryBtnBg(8));
        positiveButton.setPadding(dp(20), dp(6), dp(20), dp(6));
    }
    Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
    if (negativeButton != null) {
        negativeButton.setTextSize(13);
        negativeButton.setAllCaps(false);
        negativeButton.setTextColor(c("FF1E293B"));
        negativeButton.setBackgroundDrawable(createOutlineBtnBg(8));
        negativeButton.setPadding(dp(20), dp(6), dp(20), dp(6));
    }
    Button neutralButton = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
    if (neutralButton != null) {
        neutralButton.setTextSize(13);
        neutralButton.setAllCaps(false);
        neutralButton.setTextColor(c("FF1E293B"));
        neutralButton.setBackgroundDrawable(createOutlineBtnBg(8));
        neutralButton.setPadding(dp(20), dp(6), dp(20), dp(6));
    }
}

private void showLoadingDialog(String title, String message, final Runnable dataLoadTask) {
    LinearLayout initialLayout = new LinearLayout(getTopActivity());
    initialLayout.setOrientation(LinearLayout.HORIZONTAL);
    initialLayout.setPadding(50, 50, 50, 50);
    initialLayout.setGravity(Gravity.CENTER_VERTICAL);
    ProgressBar progressBar = new ProgressBar(getTopActivity());
    initialLayout.addView(progressBar);
    TextView loadingText = new TextView(getTopActivity());
    loadingText.setText(message);
    loadingText.setPadding(20, 0, 0, 0);  // ← 修复这里
    initialLayout.addView(loadingText);
    final AlertDialog loadingDialog = buildCommonAlertDialog(getTopActivity(), title, initialLayout, null, null, "取消", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface d, int w) { d.dismiss(); }
    }, null, null);
    loadingDialog.setCancelable(false);
    loadingDialog.show();
    new Thread(new Runnable() {
        public void run() {
            try {
                dataLoadTask.run();
            } finally {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    public void run() {
                        try { loadingDialog.dismiss(); } catch (Exception e) {}
                    }
                });
            }
        }
    }).start();
}
private int dpToPx(int dp) {
    return (int) (dp * getTopActivity().getResources().getDisplayMetrics().density);
}

private void setupListViewTouchForScroll(ListView listView) {
    listView.setOnTouchListener(new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
    }
    return false;
}
    });
}

// ========== 黄狗音乐 (酷我) ==========


// ========== 蓝狗会员曲库 (酷狗) ==========


// ==========================================
// ========== 💾 配置读写方法 ==========
// ==========================================

private void putString(String setName, String itemName, String value) {
    String existingData = getString(setName, "{}");
    try {
        JSONObject json = parseJsonObjectSafe(existingData);
        if (json == null) json = new JSONObject();
        json.put(itemName, value);
        putString(setName, json.toString());
    } catch (Exception e) {
        try {
            JSONObject json = new JSONObject();
            json.put(itemName, value);
            putString(setName, json.toString());
        } catch (Exception ex) {}
    }
}

private String getString(String setName, String itemName, String defaultValue) {
    String data = getString(setName, "{}");
    try {
        JSONObject json = parseJsonObjectSafe(data);
        if (json != null && json.has(itemName)) return json.optString(itemName, defaultValue);
    } catch (Exception e) {}
    return defaultValue;
}

// ==========================================
// ========== 🧩 JSON辅助方法 ==========
// ==========================================

private JSONObject parseJsonObjectSafe(String text) {
    try {
        if (TextUtils.isEmpty(text)) return new JSONObject();
        return new JSONObject(text);
    } catch (Exception e) {
        return new JSONObject();
    }
}

private JSONArray listToJsonArray(List list) {
    JSONArray arr = new JSONArray();
    if (list == null) return arr;
    for (int i = 0; i < list.size(); i++) {
        try { arr.put(list.get(i)); } catch (Exception e) {}
    }
    return arr;
}

private JSONArray setToJsonArray(Set set) {
    JSONArray arr = new JSONArray();
    if (set == null) return arr;
    Iterator it = set.iterator();
    while (it.hasNext()) {
        try { arr.put(it.next()); } catch (Exception e) {}
    }
    return arr;
}

private List<String> jsonObjectKeysToList(JSONObject obj) {
    List<String> list = new ArrayList<String>();
    if (obj == null) return list;
    Iterator it = obj.keys();
    while (it.hasNext()) {
        list.add(String.valueOf(it.next()));
    }
    return list;
}

private void removeJsonKey(JSONObject obj, String key) {
    if (obj == null || key == null) return;
    try {
        if (Build.VERSION.SDK_INT >= 19) {
            obj.remove(key);
            return;
        }
    } catch (Exception e) {}

    try {
        JSONObject newObj = new JSONObject();
        Iterator it = obj.keys();
        while (it.hasNext()) {
            String k = String.valueOf(it.next());
            if (!key.equals(k)) {
                newObj.put(k, obj.opt(k));
            }
        }

        Iterator it2 = obj.keys();
        ArrayList<String> oldKeys = new ArrayList<String>();
        while (it2.hasNext()) oldKeys.add(String.valueOf(it2.next()));
        for (int i = 0; i < oldKeys.size(); i++) {
            try { obj.remove(oldKeys.get(i)); } catch (Exception e) {}
        }

        Iterator it3 = newObj.keys();
        while (it3.hasNext()) {
            String k = String.valueOf(it3.next());
            obj.put(k, newObj.opt(k));
        }
    } catch (Exception e) {}
}


