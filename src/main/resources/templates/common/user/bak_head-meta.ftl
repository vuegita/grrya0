<#assign version = "1.0.0">
<#assign projectName = "${projectName}">
<#assign STATIC_URL = "${static_server}"+"/common">
<#assign STATIC_SERVER = "${static_server}"+"/user">

<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="renderer" content="webkit">

<link type="text/css" rel="stylesheet" href="${STATIC_URL}/plugins/layer/skin/layer.css" />
<link type="text/css" rel="stylesheet" href="${STATIC_URL}/plugins/layer/skin/layer.ext.css" />
<link type="text/css" rel="stylesheet" href="${STATIC_URL}/plugins/iCheck/custom.css" />
<link type="text/css" rel="stylesheet" href="${STATIC_URL}/plugins/chosen/css/chosen.css" />
<link type="text/css" rel="stylesheet" href="${STATIC_URL}/lib/bootstrap/css/bootstrap-3.3.6.min.css" />
<link type="text/css" rel="stylesheet" href="${STATIC_URL}/lib/hplus/css/font-awesome.min.css" />
<link type="text/css" rel="stylesheet" href="${STATIC_URL}/lib/hplus/css/animate.css" />
<link type="text/css" rel="stylesheet" href="${STATIC_URL}/lib/hplus/css/style.css" />
<link type="text/css" rel="stylesheet" href="${STATIC_URL}/css/common.css" />


<script type="text/javascript" src="${STATIC_URL}/lib/jquery/jquery-2.1.4.min.js"></script>
<script type="text/javascript" src="${STATIC_URL}/lib/bootstrap/js/bootstrap-3.3.6.min.js?"></script>
<script type="text/javascript" src="${STATIC_URL}/plugins/layer/layer.min.js"></script>
<script type="text/javascript" src="${STATIC_URL}/plugins/layer/laydate/laydate.js"></script>
<script type="text/javascript" src="${STATIC_URL}/plugins/layer/extend/layer.ext.js"></script>
<script type="text/javascript" src="${STATIC_URL}/plugins/iCheck/icheck.min.js"></script>
<script type="text/javascript" src="${STATIC_URL}/plugins/chosen/js/chosen.jquery.js"></script>
<script type="text/javascript" src="${STATIC_URL}/js/global.js"></script>
<script type="text/javascript" src="${STATIC_URL}/js/common.js"></script>
<script>
    var user_upload_server = '${user_upload_server}';
    var static_server = '${STATIC_SERVER}';
    var web_upload_server = '${web_upload_server}';
</script>