// ============================================================
// WeatherPro v1.0 for WeKit (JavaScript / Rhino)
// 部署: <模块数据>/scripts/weather_pro.js
// 命令: 天气 北京 | 预报 北京 | 订阅 北京 | 天气设置 | 天气帮助
// ============================================================

function onLoad() {
    log.i("WeatherPro v1.0 已加载");

    var key = storage.getOrDefault("weather_api_key", "");
    var sub = storage.getOrDefault("weather_subscribed", "");
    log.i("WeatherPro: key=" + (key ? "已设置" : "无") + " 订阅=" + (sub ? sub.split(";").length : 0) + "城");
}

// ==================== 消息处理 ====================

function onMessage(talker, content, type, isSend) {
    if (type !== 1) return null;    // 仅处理文本消息
    if (isSend === 1) return null;  // 不处理自己发的消息

    var clean = getCleanContent(content).trim();

    if (!isChatAllowed(talker)) return null;

    // --- 天气查询 ---
    if (startsWith(clean, "天气 ") || startsWith(clean, "天气查询 ")) {
        var city = getArgAfter(clean);
        if (!city) { reply("请输入城市名，如: 天气 北京"); return null; }
        var key = getApiKey();
        if (!key) { reply("请先设置 API Key。命令: 天气设置 key <Key>\n免费获取: dev.qweather.com"); return null; }
        doWeatherQuery(city);
        return null;
    }

    // --- 预报查询 ---
    if (startsWith(clean, "预报 ") || startsWith(clean, "天气预报 ")) {
        var city = getArgAfter(clean);
        if (!city) { reply("请输入城市名，如: 预报 北京"); return null; }
        if (!getApiKey()) { reply("请先设置 API Key"); return null; }
        doForecastQuery(city);
        return null;
    }

    // --- 城市订阅 ---
    if (startsWith(clean, "订阅 ") || startsWith(clean, "订阅天气 ")) {
        var city = getArgAfter(clean);
        if (!city) { reply("请输入城市名，如: 订阅 北京"); return null; }
        if (!getApiKey()) { reply("请先设置 API Key"); return null; }
        doSubscribeCity(talker, city);
        return null;
    }

    // --- 取消订阅 ---
    if (clean === "取消订阅" || clean === "取消天气") {
        storage.set("weather_subscribed", "");
        storage.set("weather_push_enabled", false);
        reply("已取消所有订阅");
        return null;
    }

    // --- 我的订阅 ---
    if (clean === "我的订阅" || clean === "订阅列表") {
        var sub = storage.getOrDefault("weather_subscribed", "");
        if (!sub) { reply("无订阅。命令: 订阅 <城市>"); return null; }
        var cities = sub.split(";");
        var sb = "订阅列表(" + cities.length + "):\n";
        for (var i = 0; i < cities.length; i++) {
            sb += (i + 1) + ". " + cities[i].split("|")[0] + "\n";
        }
        var push = storage.getOrDefault("weather_push_enabled", false);
        var time = storage.getOrDefault("weather_push_time", "08:00");
        sb += "推送: " + (push ? "开" : "关") + " | 时间: " + time;
        reply(sb);
        return null;
    }

    // --- 天气设置 (命令) ---
    if (startsWith(clean, "天气设置")) {
        var parts = clean.split(/\s+/);
        if (parts.length >= 3) {
            if (parts[1] === "key") {
                storage.set("weather_api_key", parts[2]);
                reply("API Key 已设置");
                return null;
            }
            if (parts[1] === "时间" || parts[1] === "推送时间") {
                storage.set("weather_push_time", parts[2]);
                reply("推送时间已设为 " + parts[2]);
                return null;
            }
            if (parts[1] === "推送" || parts[1] === "开关") {
                var on = parts[2] === "开" || parts[2] === "开启" || parts[2] === "on" || parts[2] === "1";
                storage.set("weather_push_enabled", on);
                reply("推送已" + (on ? "开启" : "关闭"));
                return null;
            }
        }
        // 无参数 -> 尝试可视化面板, 失败则文字
        showSettingsPanel();
        return null;
    }

    // --- 聊天过滤 ---
    if (clean === "天气启用") {
        addEnabledChat(talker);
        reply("已启用当前聊天");
        return null;
    }
    if (clean === "天气停用") {
        removeEnabledChat(talker);
        reply("已停用当前聊天");
        return null;
    }
    if (clean === "天气过滤 开") {
        storage.set("weather_filter_enabled", true);
        reply("聊天过滤已开启");
        return null;
    }
    if (clean === "天气过滤 关") {
        storage.set("weather_filter_enabled", false);
        reply("聊天过滤已关闭");
        return null;
    }

    // --- 帮助 ---
    if (clean === "天气帮助" || clean === "天气Pro" || clean === "天气pro") {
        reply("命令:\n天气 <城市>  查询天气\n预报 <城市>  3日预报\n订阅 <城市>  订阅推送\n天气设置    打开设置\n天气启用    启用当前聊天\n天气停用    停用当前聊天\n天气过滤 开/关  开关过滤\n取消订阅/我的订阅\n天气帮助 帮助");
        return null;
    }

    return null;
}

// ==================== 工具函数 ====================

function getCleanContent(content) {
    // 群聊消息格式: wxid_xxx:\n实际内容
    var match = content.match(/^wxid_[^:]+:\n(.*)$/s);
    return match ? match[1] : content;
}

function startsWith(content, prefix) {
    if (content.indexOf(prefix) === 0) return true;
    // 全角空格兼容
    return content.indexOf(prefix.replace(' ', '\u3000')) === 0;
}

function getArgAfter(content) {
    var idx = content.indexOf(" ");
    if (idx < 0) idx = content.indexOf("\u3000");
    if (idx < 0) return "";
    return content.substring(idx + 1).trim();
}

function reply(text) {
    wechat.replyText(text);
}

function getApiKey() {
    return storage.getOrDefault("weather_api_key", "");
}

// ==================== 聊天过滤 ====================

function isChatAllowed(talker) {
    var filterOn = storage.getOrDefault("weather_filter_enabled", false);
    if (!filterOn) return true;
    var list = storage.getOrDefault("weather_enabled_chats", "");
    if (!list) return false;
    return list.split(";").indexOf(talker) >= 0;
}

function addEnabledChat(talker) {
    var list = storage.getOrDefault("weather_enabled_chats", "");
    var chats = list ? list.split(";") : [];
    if (chats.indexOf(talker) < 0) chats.push(talker);
    storage.set("weather_enabled_chats", chats.join(";"));
    log.i("WeatherPro 启用: " + talker);
}

function removeEnabledChat(talker) {
    var list = storage.getOrDefault("weather_enabled_chats", "");
    if (!list) return;
    var chats = list.split(";");
    var filtered = [];
    for (var i = 0; i < chats.length; i++) {
        if (chats[i] !== talker) filtered.push(chats[i]);
    }
    storage.set("weather_enabled_chats", filtered.join(";"));
    log.i("WeatherPro 停用: " + talker);
}

// ==================== 天气查询 ====================

function doWeatherQuery(city) {
    var key = getApiKey();
    var url = "https://geoapi.qweather.com/v2/city/lookup?location=" + encodeURIComponent(city) + "&key=" + key;
    log.i("WeatherPro GEO: " + url);

    var resp = http.get(url);
    if (!resp.ok || !resp.body) {
        reply("网络请求失败，请检查网络后重试");
        return;
    }
    try {
        var geoData = JSON.parse(resp.body);
        if (geoData.code !== "200") {
            reply("未找到城市: " + city);
            return;
        }
        var loc = geoData.location[0];
        var cityId = loc.id;
        var name = loc.name + "," + loc.adm1;

        var nowUrl = "https://devapi.qweather.com/v7/weather/now?location=" + cityId + "&key=" + key;
        log.i("WeatherPro NOW: " + nowUrl);

        var nowResp = http.get(nowUrl);
        if (!nowResp.ok || !nowResp.body) {
            reply("获取天气失败，请重试");
            return;
        }
        var nowData = JSON.parse(nowResp.body);
        var n = nowData.now;
        if (!n) {
            reply("天气数据为空，请检查 API Key");
            return;
        }
        var sb = "[天气] " + name + "\n";
        sb += n.text + " " + n.temp + "C (体感" + n.feelsLike + "C)\n";
        sb += "风向 " + n.windDir + " " + n.windScale + "级 风速" + n.windSpeed + "km/h\n";
        sb += "湿度 " + n.humidity + "% 能见度" + n.vis + "km 气压" + n.pressure + "hPa";
        reply(sb);
    } catch (e) {
        reply("查询失败: " + e);
        log.e("WeatherPro 查询失败: " + e);
    }
}

// ==================== 多日预报 ====================

function doForecastQuery(city) {
    var key = getApiKey();
    var url = "https://geoapi.qweather.com/v2/city/lookup?location=" + encodeURIComponent(city) + "&key=" + key;

    var resp = http.get(url);
    if (!resp.ok || !resp.body) { reply("网络请求失败"); return; }
    try {
        var geoData = JSON.parse(resp.body);
        if (geoData.code !== "200") { reply("未找到: " + city); return; }
        var loc = geoData.location[0];
        var name = loc.name + "," + loc.adm1;

        var fcUrl = "https://devapi.qweather.com/v7/weather/3d?location=" + loc.id + "&key=" + key;
        var fcResp = http.get(fcUrl);
        if (!fcResp.ok || !fcResp.body) { reply("获取预报失败"); return; }
        var fcData = JSON.parse(fcResp.body);
        var daily = fcData.daily;
        if (!daily || daily.length === 0) { reply("预报数据为空"); return; }

        var sb = "[3日预报] " + name;
        for (var i = 0; i < daily.length; i++) {
            var d = daily[i];
            sb += "\n\n" + d.fxDate + " " + getWeekDay(d.fxDate);
            sb += "\n  白天 " + d.textDay + " " + d.tempMax + "C";
            sb += "\n  夜间 " + d.textNight + " " + d.tempMin + "C";
            sb += "\n  " + d.windDirDay + d.windScaleDay + "级 湿度" + d.humidity + "%";
            sb += "\n  紫外" + d.uvIndex + " 日出" + d.sunrise + " 日落" + d.sunset;
        }
        reply(sb);
    } catch (e) {
        reply("预报查询失败");
        log.e("WeatherPro 预报失败: " + e);
    }
}

// ==================== 城市订阅 ====================

function doSubscribeCity(talker, city) {
    var key = getApiKey();
    var url = "https://geoapi.qweather.com/v2/city/lookup?location=" + encodeURIComponent(city) + "&key=" + key;

    var resp = http.get(url);
    if (!resp.ok || !resp.body) { reply("网络请求失败"); return; }
    try {
        var geoData = JSON.parse(resp.body);
        if (geoData.code !== "200") { reply("未找到: " + city); return; }
        var loc = geoData.location[0];
        var cityId = loc.id;
        var name = loc.name + "," + loc.adm1;

        var sub = storage.getOrDefault("weather_subscribed", "");
        if (sub.indexOf(cityId) >= 0) { reply("已订阅 " + name); return; }
        var entry = name + "|" + cityId;
        sub = sub ? sub + ";" + entry : entry;
        storage.set("weather_subscribed", sub);
        storage.set("weather_push_enabled", true);
        var time = storage.getOrDefault("weather_push_time", "08:00");
        reply("已订阅 " + name + " 每日" + time + "推送");
    } catch (e) {
        reply("订阅失败");
        log.e("WeatherPro 订阅失败: " + e);
    }
}

// ==================== 设置面板 (UI优先, 文字兜底) ====================

function showSettingsPanel() {
    try {
        showSettingsUI();
    } catch (e) {
        log.i("WeatherPro UI失败, 使用文字: " + e);
        showSettingsText();
    }
}

function showSettingsUI() {
    var ctx = hostinfo.application;
    var handler = new Packages.android.os.Handler(Packages.android.os.Looper.getMainLooper());
    handler.post(function() {
        try {
            var root = new Packages.android.widget.LinearLayout(ctx);
            root.setOrientation(1); // VERTICAL
            root.setPadding(50, 40, 50, 40);

            // Title
            var title = new Packages.android.widget.TextView(ctx);
            title.setText("天气Pro 设置 v1.0");
            title.setTextSize(20);
            title.setTextColor(0xFF333333);
            root.addView(title);
            addPad(root, 20);

            // API Key
            addLabel(root, "和风天气 API Key:");
            addPad(root, 4);
            var keyInput = new Packages.android.widget.EditText(ctx);
            keyInput.setText(getApiKey());
            keyInput.setHint("免费获取: dev.qweather.com");
            keyInput.setSingleLine(true);
            root.addView(keyInput);
            addPad(root, 20);

            // Push settings
            addLabel(root, "每日推送:");
            addPad(root, 4);
            var pushRow = new Packages.android.widget.LinearLayout(ctx);
            pushRow.setOrientation(0); // HORIZONTAL
            pushRow.setGravity(16); // CENTER_VERTICAL
            var pushSwitch = new Packages.android.widget.Switch(ctx);
            pushSwitch.setChecked(storage.getOrDefault("weather_push_enabled", false));
            pushRow.addView(pushSwitch);
            var timeInput = new Packages.android.widget.EditText(ctx);
            timeInput.setText(storage.getOrDefault("weather_push_time", "08:00"));
            timeInput.setHint("08:00");
            timeInput.setSingleLine(true);
            timeInput.setWidth(120);
            var tl = new Packages.android.widget.LinearLayout.LayoutParams(-2, -2);
            tl.leftMargin = 16;
            pushRow.addView(timeInput, tl);
            root.addView(pushRow);
            addPad(root, 20);

            // Filter settings
            addLabel(root, "聊天过滤:");
            addPad(root, 4);
            var filterRow = new Packages.android.widget.LinearLayout(ctx);
            filterRow.setOrientation(0);
            filterRow.setGravity(16);
            var filterSwitch = new Packages.android.widget.Switch(ctx);
            filterSwitch.setChecked(storage.getOrDefault("weather_filter_enabled", false));
            filterRow.addView(filterSwitch);
            var fh = new Packages.android.widget.TextView(ctx);
            fh.setText("  开启后仅启用列表响应");
            fh.setTextSize(12);
            fh.setTextColor(0xFF888888);
            filterRow.addView(fh);
            root.addView(filterRow);
            addPad(root, 8);

            addLabel(root, "启用聊天列表 (wxid):");
            addPad(root, 4);
            var chatInput = new Packages.android.widget.EditText(ctx);
            chatInput.setText(storage.getOrDefault("weather_enabled_chats", "").replace(/;/g, "\n"));
            chatInput.setMinLines(3);
            chatInput.setGravity(48); // TOP
            root.addView(chatInput);
            addPad(root, 20);

            // Save
            var saveBtn = new Packages.android.widget.Button(ctx);
            saveBtn.setText("保存设置");
            saveBtn.setOnClickListener(new Packages.android.view.View.OnClickListener({
                    onClick: function(v) {
                        storage.set("weather_api_key", keyInput.getText().toString().trim());
                        storage.set("weather_push_enabled", pushSwitch.isChecked());
                        var t = timeInput.getText().toString().trim();
                        if (!t) t = "08:00";
                        storage.set("weather_push_time", t);
                        storage.set("weather_filter_enabled", filterSwitch.isChecked());
                        var text = chatInput.getText().toString().trim();
                        var chats = text ? text.split("\n") : [];
                        var cleaned = [];
                        for (var i = 0; i < chats.length; i++) {
                            var line = chats[i].trim();
                            if (line) cleaned.push(line);
                        }
                        storage.set("weather_enabled_chats", cleaned.join(";"));
                        Packages.android.widget.Toast.makeText(ctx, "设置已保存", 0).show();
                    }
                }));
            root.addView(saveBtn);

            var builder = new Packages.android.app.AlertDialog.Builder(ctx);
            var dlg = builder.create();
            dlg.setView(root);
            dlg.setButton(Packages.android.content.DialogInterface.BUTTON_POSITIVE, "关闭",
                new Packages.android.content.DialogInterface.OnClickListener({
                    onClick: function(d, w) {}
                }));
            dlg.show();
        } catch (e) {
            log.e("WeatherPro UI错误: " + e);
            showSettingsText();
        }
    });
}

function addPad(parent, h) {
    var v = new Packages.android.widget.TextView(parent.getContext());
    v.setHeight(h);
    parent.addView(v);
}

function addLabel(parent, text) {
    var tv = new Packages.android.widget.TextView(parent.getContext());
    tv.setText(text);
    tv.setTextSize(14);
    tv.setTextColor(0xFF555555);
    parent.addView(tv);
}

function showSettingsText() {
    var key = getApiKey();
    var masked = key ? (key.length > 8 ? key.substring(0, 4) + "****" + key.substring(key.length - 4) : "****") : "未设置";
    var push = storage.getOrDefault("weather_push_enabled", false);
    var time = storage.getOrDefault("weather_push_time", "08:00");
    var filterOn = storage.getOrDefault("weather_filter_enabled", false);
    var sub = storage.getOrDefault("weather_subscribed", "");
    var subCount = sub ? sub.split(";").length : 0;
    var chatList = storage.getOrDefault("weather_enabled_chats", "");
    var chatCount = chatList ? chatList.split(";").length : 0;

    var sb = "[WeatherPro 设置 v1.0]\n";
    sb += "API Key: " + masked + "\n";
    sb += "每日推送: " + (push ? "开" : "关") + " | 时间: " + time + "\n";
    sb += "聊天过滤: " + (filterOn ? "开" : "关") + " (" + chatCount + "个聊天)\n";
    sb += "订阅城市: " + subCount + "个\n";
    sb += "\n命令:\n天气设置 key <Key>  设置API Key\n天气设置 时间 <HH:mm>  推送时间\n天气设置 推送 开/关  开关推送\n天气过滤 开/关  开关过滤\n天气启用/停用  管理聊天";
    reply(sb);
}

// ==================== 辅助函数 ====================

function getWeekDay(dateStr) {
    try {
        var d = new Date(dateStr);
        var days = ["周日", "周一", "周二", "周三", "周四", "周五", "周六"];
        return days[d.getDay()];
    } catch (e) { return ""; }
}
