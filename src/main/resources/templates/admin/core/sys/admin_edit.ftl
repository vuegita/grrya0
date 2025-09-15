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
            <h5><#if newAdmin?exists >编辑<#else>新增</#if>管理员</h5>
        </div>

        <div class="ibox-content">
            <form id="form" class="form-horizontal" autocomplete="off">
                <input type="hidden" name="adminId" id="adminId" value="<#if newAdmin?exists>${newAdmin.id !}</#if>">

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>账号:</label>
                    <div class="col-sm-3">
                        <input class="form-control" name="account" autocomplete="off" required maxlength="50"
                               <#if newAdmin?exists>readonly</#if> value="<#if newAdmin?exists>${newAdmin.account !}</#if>"/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>密码:</label>
                    <div class="col-sm-3">
                        <input type="password" class="form-control" minlength="6" name="password" id="password" autocomplete="off"
                               maxlength="50" value=""/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>角色:</label>
                    <div class="col-sm-3">
                        <select class="form-control" name="roleName" required>
                        <#if roleList?exists>
                            <#list roleList as role>
                                <option value="${role.name}" <#if newAdmin?exists><#if role.id == newAdmin.roleid >selected</#if></#if>>${role.name}</option>
                            </#list>
                        <#else >
                            <option value="">请先添加管理员角色</option>
                        </#if>
                        </select>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2">备注:</label>
                    <div class="col-sm-3">
                        <input class="form-control" name="remark" autocomplete="off" maxlength="255"
                               value="<#if newAdmin?exists>${newAdmin.remark !}</#if>"/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <div class="col-sm-4 col-sm-offset-2">
                        <input class="btn btn-primary" type="button" id="submitBtn" value="保存"/>
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
<script type="text/javascript" src="${STATIC_URL}/js/jquery.md5.js"></script>
<script type="text/javascript">
    $(function () {
        //确认保存
        $("#submitBtn").click(function () {
            var adminId = $("#adminId").val();
            var account = $('input[name="account"]').val();
            var password = $('input[name="password"]').val();
            var roleName = $('select[name="roleName"]').val();
            if (isEmpty(account)){
                $.global.openErrorMsg('账号不能为空！');
                return;
            }
            if (isEmpty(roleName)){
                $.global.openErrorMsg('请选择角色! ');
                return;
            }

            if(!isEmpty(password))
            {
                password = $.md5(password);
            }

            var url = "/alibaba888/Liv2sky3soLa93vEr62/addAdmin";
            if(!isEmpty(adminId))
            {
                url = "/alibaba888/Liv2sky3soLa93vEr62/editAdmin";
            }

            $.ajax({
                type: "post",
                async: false,
                url: url,
                data: {
                    account: account,
                    password: password,
                    roleName: $('select[name="roleName"]').val(),
                    remark: $('input[name="remark"]').val(),
                },
                dataType: "json",
                success: function (data) {
                    if (data != null && data.code == 200) {
                        $.global.openSuccessMsg("保存成功");
                        setTimeout(function () {
                            window.history.go(-1);
                        }, 500);
                    } else {
                        $.global.openErrorMsg(data.msg);
                    }
                },
                error: function () {
                    $.global.openErrorMsg('保存失败，请重试');
                }
            })
        });
    });


    function isEmpty(obj) {
        if (typeof obj === 'undefined' || obj == null || obj === '') {
            return true;
        } else {
            return false;
        }
    }

</script>
</body>
</html>
