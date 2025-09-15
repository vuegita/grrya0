<!DOCTYPE HTML>
<html>
<head>
    <#include "../../../common/head-meta.ftl"/>
    <title>${projectName}后台管理系统-菜单管理</title>
</head>
<body class="gray-bg">
<div class="wrapper wrapper-content animated fadeInRight">
    <div class="ibox float-e-margins">
        <div class="ibox-title">
            <h5><#if menu? exists >编辑<#else>新增</#if>菜单</h5>
        </div>

        <div class="ibox-content">
            <form id="form" class="form-horizontal" autocomplete="off">
                <input type="hidden" name="key" value="<#if menu?exists>${menu.key !}</#if>">
                <#if menuList?exists>
                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>上级菜单:</label>
                    <div class="col-sm-3">
                        <select class="form-control" name="pkey" >
                            <#list menuList as parentMenu>
                                <option value="${parentMenu.key}" <#if menu.pkey == parentMenu.key >selected</#if>  >
                                    ${parentMenu.name}
                                </option>
                            </#list>
                        </select>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>
                </#if>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>菜单排序:</label>
                    <div class="col-sm-3">
                        <input class="form-control" name="sort" autocomplete="off" required maxlength="50" value="<#if menu?exists>${menu.sort !}</#if>"/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>菜单名称:</label>
                    <div class="col-sm-3">
                        <input type="text" class="form-control" name="name" autocomplete="off" required maxlength="50" value="<#if menu?exists>${menu.name !}</#if>"/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>



                <div class="form-group">
                    <label class="control-label col-sm-2">菜单链接:</label>
                    <div class="col-sm-3">
                        <input class="form-control" name="link" autocomplete="off" maxlength="255" readonly value="<#if menu?exists>${menu.link !}</#if>"
                          <#if menu?exists><#if menu.level == 0>disabled</#if></#if>/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

<#--                <div class="form-group">-->
<#--                    <label class="control-label col-sm-2">是否安全:</label>-->
<#--                    <div class="col-sm-5">-->
<#--                        <div class="radio i-checks">-->
<#--                            <label><input type="radio" value="safe" name="safeStatus" id="safeStatus"-->
<#--                                          <#if menu?exists><#if menu.safeStatus == 'safe'>checked</#if></#if>>-->
<#--                                <i></i>安全</label>-->
<#--                            <label><input type="radio" value="unsafe" name="safeStatus" id="safeStatus"-->
<#--                                          <#if menu?exists><#if menu.safeStatus == 'unsafe'>checked</#if><#else >checked</#if>>-->
<#--                                <i></i>非安全</label>-->
<#--                        </div>-->
<#--                    </div>-->
<#--                </div>-->
<#--                <div class="hr-line-dashed"></div>-->

                <div class="form-group">
                    <div class="col-sm-4 col-sm-offset-2">
                        <input class="btn btn-primary" type="submit" value="保存"/>
                        <button class="btn btn-white" type="button" onclick="window.history.back();">取消</button>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>

<script type="text/javascript" src="${STATIC_URL}/plugins/validate/jquery.validate.min.js"></script>
<script type="text/javascript" src="${STATIC_URL}/plugins/validate/messages_zh.min.js"></script>
<script type="text/javascript" src="${STATIC_URL}/js/validate.defaults.js?v=${version}"></script>
<script type="text/javascript">
    $(function(){
        var formId = '#form';
        var url ='/alibaba888/Liv2sky3soLa93vEr62/editMenu';
        var validator = $(formId).validate({
            rules: {
            },
            messages: {
            },
            submitHandler:function(form){
                $.global.ajaxSubmitForm(formId, url);
                return false;
            }
        });
    });
</script>
</body>
</html>
