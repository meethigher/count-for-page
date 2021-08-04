<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <title>${title}</title>
    <link rel="stylesheet" href="layui/css/layui.css">
</head>
<body>
<table class="layui-table">
    <colgroup>
        <col width="150">
        <col width="150">
        <col width="150">
        <col>
        <col>
    </colgroup>
    <thead>
    <tr>
        <th>ip</th>
        <th>位置</th>
        <th>时间</th>
        <th>设备</th>
        <th>访问</th>
    </tr>
    </thead>
    <tbody>
    <#list today as item>
        <tr>
            <td>${item.ip}</td>
            <td>${item.location}</td>
            <td>${item.firstVisitTime}</td>
            <td>${item.userAgent}</td>
            <td>${item.url}</td>
        </tr>
    </#list>
    </tbody>
</table>
<script src="layui/layui.js"></script>
</body>
</html>
