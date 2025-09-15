<!DOCTYPE HTML>
<html>
<head>
    <#include "../../../common/head-meta.ftl"/>
    <title>${projectName}后台管理系统-用户管理</title>
</head>
<body class="gray-bg">
<div class="wrapper wrapper-content animated fadeInRight">
    <div class="ibox float-e-margins">
        <div class="ibox-title">
            <h5>编辑</h5>
        </div>

        <div class="ibox-content">

            <form id="form" class="form-horizontal" autocomplete="off">
                <input type="hidden" name="id" id="id" value="<#if entity?exists>${entity.id}</#if>"/>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>用户名:</label>
                    <div class="col-sm-3">
                        <input class="form-control" name="username" id="username"
                               value="<#if entity?exists> ${entity.username!} </#if>"
<#--                                <#if entity?exists> readonly </#if>-->
                              autocomplete="off" required maxlength="50"/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>一级状态:</label>
                    <div class="col-sm-3">
                        <div class="radio i-checks">
                            <label>
                                <input type="radio"  name="lv1RwStatus" id="lv1RwStatus" value="enable"
                                        <#if entity?exists && entity.lv1RwStatus == 'enable'> checked </#if>/>
                                <i></i>启用
                            </label>

                            <label><input type="radio"  name="lv1RwStatus" id="lv1RwStatus" value="disable"
                                        <#if entity?exists && entity.lv1RwStatus == 'disable'> checked </#if>/>
                                <i></i>禁用
                            </label>

                        </div>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>二级状态:</label>
                    <div class="col-sm-3">
                        <div class="radio i-checks">
                            <label>
                                <input type="radio"  name="lv2RwStatus" id="lv2RwStatus" value="enable"
                                        <#if entity?exists && entity.lv2RwStatus == 'enable'> checked </#if>/>
                                <i></i>启用
                            </label>

                            <label><input type="radio"  name="lv2RwStatus" id="lv2RwStatus" value="disable"
                                        <#if entity?exists && entity.lv2RwStatus == 'disable'> checked </#if>/>
                                <i></i>禁用
                            </label>

                        </div>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>系统状态:</label>
                    <div class="col-sm-3">
                        <div class="radio i-checks">

                            <label>
                                <input type="radio"  name="systemStatus" id="systemStatus" value="apply"
                                        <#if entity?exists && entity.systemStatus == 'apply'> checked </#if>/>
                                <i></i>申请
                            </label>

                            <label>
                                <input type="radio"  name="systemStatus" id="systemStatus" value="enable"
                                        <#if entity?exists && entity.systemStatus == 'enable'> checked </#if>/>
                                <i></i>启用
                            </label>

                            <label><input type="radio"  name="systemStatus" id="systemStatus" value="disable"
                                        <#if entity?exists && entity.systemStatus == 'disable'> checked </#if>/>
                                <i></i>禁用
                            </label>

                        </div>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <div class="col-sm-4 col-sm-offset-2">
                        <input class="btn btn-primary" type="button" id="submitBtn" value="保存"/>
                        <button class="btn btn-white" type="button" onclick="window.close();">取消</button>
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
<script type="text/javascript">
    $(function () {

        //确认保存
        $("#submitBtn").click(function () {

            var data = $('#form').serialize();

            $.ajax({
                type: "POST",//方法类型
                dataType: "json",//预期服务器返回的数据类型
                url: "/alibaba888/Liv2sky3soLa93vEr62/updatePassportShareHolderMemberInfo" ,//url
                data: $('#form').serialize(),
                success: function (data) {
                    if (data != null && data.code == 200) {
                        $.global.openSuccessMsg("保存成功");
                        window.close();
                    } else {
                        $.global.openErrorMsg(data.msg);
                    }
                },
                error : function() {
                    $.global.openErrorMsg('保存失败，请重试');
                }
            });

        });
    });

</script>
</body>
</html>
