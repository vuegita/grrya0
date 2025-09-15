<!DOCTYPE HTML>
<html>
<head>
    <#include "../../../common/head-meta.ftl"/>
    <title>${projectName}后台管理系统</title>
</head>

<body class="fixed-sidebar full-height-layout gray-bg" style="overflow:hidden">
    <div id="wrapper">
        <#include "menu.ftl"/>
        <#include "main.ftl"/>
        <#include "sidebar.ftl"/>
    </div>

    <script type="text/javascript" src="${static_server}/common/plugins/metisMenu/jquery.metisMenu.js"></script>
    <script type="text/javascript" src="${static_server}/common/plugins/slimscroll/jquery.slimscroll.min.js"></script>

    <script type="text/javascript" src="${static_server}/common/lib/hplus/hplus.js"></script>
    <script type="text/javascript" src="${static_server}/common/lib/hplus/contabs.js"></script>

    <script type="text/javascript" src="${static_server}/common/plugins/pace/pace.min.js"></script>
</body>
</html>
