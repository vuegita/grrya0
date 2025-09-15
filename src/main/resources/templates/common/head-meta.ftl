
<#assign version = "1.0.0">
<#assign projectName = "${projectName}">
<#assign STATIC_URL = "${static_server}"+"/common">
<#assign STATIC_SERVER = "${static_server}"+"/admin">

<meta charset="utf-8">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="renderer" content="webkit">

<#--<link rel="shortcut icon" href="${STATIC_SERVER}/images/${ENV}logo.png" type="image/x-icon" />-->
<link type="text/css" rel="stylesheet" href="${static_server}/common/plugins/layer/skin/layer.css" />
<link type="text/css" rel="stylesheet" href="${static_server}/common/plugins/layer/skin/layer.ext.css" />
<link type="text/css" rel="stylesheet" href="${static_server}/common/plugins/iCheck/custom.css" />
<link type="text/css" rel="stylesheet" href="${static_server}/common/plugins/chosen/css/chosen.css" />
<link type="text/css" rel="stylesheet" href="${static_server}/common/lib/bootstrap/css/bootstrap-3.3.6.min.css" />
<link type="text/css" rel="stylesheet" href="${static_server}/common/lib/hplus/css/font-awesome.min.css" />
<link type="text/css" rel="stylesheet" href="${static_server}/common/lib/hplus/css/animate.css" />
<link type="text/css" rel="stylesheet" href="${static_server}/common/lib/hplus/css/style.css" />
<link type="text/css" rel="stylesheet" href="${static_server}/common/css/common.css" />

<script type="text/javascript" src="${static_server}/common/lib/jquery/jquery-2.1.4.min.js"></script>
<script type="text/javascript" src="${static_server}/common/lib/bootstrap/js/bootstrap-3.3.6.min.js?"></script>
<script type="text/javascript" src="${static_server}/common/plugins/layer/layer.min.js"></script>
<script type="text/javascript" src="${static_server}/common/plugins/layer/laydate/laydate.js"></script>
<script type="text/javascript" src="${static_server}/common/plugins/layer/extend/layer.ext.js"></script>
<script type="text/javascript" src="${static_server}/common/plugins/iCheck/icheck.min.js"></script>
<script type="text/javascript" src="${static_server}/common/plugins/chosen/js/chosen.jquery.js"></script>
<script type="text/javascript" src="${static_server}/common/js/plugins/webuploader/webuploader.js"></script>
<script type="text/javascript" src="${static_server}/common/js/global.js"></script>
<script type="text/javascript" src="${static_server}/common/js/common.js"></script>


<script>
    var environment = '${environment}';
    var staticCommonUrl = '${static_server}/common';
</script>
