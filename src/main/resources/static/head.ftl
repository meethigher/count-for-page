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
<#if headList??>
    <table class="layui-table">
        <colgroup>
            <col>
            <col>
        </colgroup>
        <thead>
        <tr>
            <th>请求头</th>
            <th>请求值</th>
        </tr>
        </thead>
        <tbody>
        <#list headList as item>
            <tr>
                <td>${item.key!"null"}</td>
                <td>${item.value!"null"}</td>
            </tr>
        </#list>
        </tbody>
    </table>
<#else >
    <h1 align="center">null</h1>
</#if>
<script src="layui/layui.js"></script>
</body>
</html>
