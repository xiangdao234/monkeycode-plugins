// ============================================================
// Weather v1.0 for WeKit (wttr.in)
// 部署: <模块数据>/scripts/weather.js
// 命令: 天气 北京
// ============================================================

function onLoad() {
    log.i("Weather v1.0 已加载");
}

function onMessage(talker, content, type, isSend) {
    if (type !== 1) return null;
    if (isSend === 1) return null;

    var clean = content.trim();
    // 群聊消息格式兼容
    var match = clean.match(/^wxid_[^:]+:\n(.*)$/s);
    if (match) clean = match[1].trim();

    if (clean.indexOf("天气 ") === 0) {
        var city = clean.substring(3).trim();
        if (!city) {
            wechat.replyText("请输入城市名，如: 天气 北京");
            return null;
        }
        var url = "https://wttr.in/" + encodeURIComponent(city) + "?format=j1&lang=zh";
        log.i("Weather: " + url);
        var resp = http.get(url);
        if (!resp.ok || !resp.body) {
            wechat.replyText("网络请求失败");
            return null;
        }
        try {
            var data = JSON.parse(resp.body);
            var now = data.current_condition[0];
            if (!now) {
                wechat.replyText("未找到城市");
                return null;
            }
            var temp = now.temp_C;
            var desc = now.weatherDesc[0].value;
            wechat.replyText("[天气] " + desc + " " + temp + "C");
        } catch (e) {
            wechat.replyText("查询失败");
            log.e("Weather error: " + e);
        }
    }

    return null;
}
