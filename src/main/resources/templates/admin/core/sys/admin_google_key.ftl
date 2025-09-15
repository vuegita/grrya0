<!DOCTYPE HTML>
<html>
<head>
    <#include "../../../common/head-meta.ftl"/>
    <title>${projectName}后台管理系统-管理员管理</title>
</head>
<body class="gray-bg">
<div class="wrapper wrapper-content animated fadeInRight">
    <div class="ibox float-e-margins">
        <div class="ibox-title">
            <h5>谷歌身份验证</h5>
        </div>

        <div class="ibox-content">
            <form id="form" class="form-horizontal" autocomplete="off">
                <div class="form-group">
                    <label class="control-label col-sm-2">账号:</label>
                    <div class="col-sm-3">
                        <input class="form-control"  readonly="readonly" name="account" autocomplete="off" required maxlength="50" value="<#if admin?exists>${admin.account !}</#if>"/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>
                <div class="form-group">
                    <label class="control-label col-sm-2">二维码:</label>
                    <img src="/alibaba888/Liv2sky3soLa93vEr62/getGoogleKeyEWM?account=<#if admin?exists>${admin.account!}</#if>"/>
                </div>
                <div class="hr-line-dashed"></div>

<#--                <div class="form-group">-->
<#--                    <label class="control-label col-sm-2">密钥:</label>-->
<#--                    <div class="col-sm-3">-->
<#--                        <input id="googleKey" readonly="readonly" class="form-control" name="googleKey" autocomplete="off" maxlength="255" value="<#if admin?exists>${admin.googlekey !}</#if>"/>-->
<#--                    </div>-->
<#--                </div>-->

                <div class="form-group">
                    <div class="col-sm-4 col-sm-offset-2">
                        <button class="btn btn-white" type="button" onclick="window.history.back();">返回</button>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>

<script type="text/javascript" src="${STATIC_URL}/plugins/validate/jquery.validate.min.js"></script>
<script type="text/javascript" src="${STATIC_URL}/plugins/validate/messages_zh.min.js"></script>
<script type="text/javascript" src="${STATIC_URL}/js/validate.defaults.js?v=${version}"></script>
<script type="text/javascript" src="${STATIC_URL}/js/jquery.md5.js"></script>
</body>
</html>
