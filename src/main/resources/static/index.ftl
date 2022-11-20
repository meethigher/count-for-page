<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <title>${title!"null"}</title>
    <link rel="stylesheet" href="layui/css/layui.css">
</head>
<body>
<#--https://www.cnblogs.com/panchanggui/p/9342246.html-->
<#if today??>
    <table class="layui-table">
        <colgroup>
            <col width="150">
            <col width="150">
            <col width="150">
            <col>
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
            <th>来源</th>
        </tr>
        </thead>
        <tbody>
        <#list today as item>
            <tr>
                <td>${item.ipAddr!"null"}</td>
                <td>${item.ipLoc!"null"}</td>
                <td>${item.firstVisitTime!"null"}</td>
                <td>${item.device!"null"}</td>
                <td>${item.targetLink!"null"}</td>
                <td>${item.originLink!"null"}</td>
            </tr>
        </#list>
        </tbody>
    </table>
<#else >
    <h1 align="center">nobody</h1>
</#if>
<script src="layui/layui.js"></script>
</body>
</html>
