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
            <h5>添加商家</h5>

            <p style="color: blue"> 　</p>
            <p  style="color: green">1. 最低和最高价格都要为5的倍数!</p>
        </div>

        <div class="ibox-content">

            <form id="form" class="form-horizontal" autocomplete="off">
                <input type="hidden" name="id" id="id" value="<#if entity?exists>${entity.userid}</#if>"/>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>店铺名称:</label>
                    <div class="col-sm-5">
                        <input class="form-control" name="name" id="name" value="<#if entity?exists> ${entity.name} </#if>" autocomplete="off" required maxlength="50"/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>用户名:</label>
                    <div class="col-sm-5">
                        <input class="form-control" name="username" id="username" value="<#if entity?exists> ${entity.username} </#if>" autocomplete="off" required maxlength="50"/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>店铺等级:</label>
                    <div class="col-sm-5">
                        <select class="form-control" id="level" name="level" >
                            <#list mallStoreLevelArr as item>
                                <option value="${item.getKey()}" <#if entity?exists && entity.level == item.getKey()> selected </#if>>${item.getKey()} </option>
                            </#list>
                        </select>
                    </div>
                </div>

                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>状态:</label>
                    <div class="col-sm-5">
                        <div class="radio i-checks">

                            <label>
                                <input type="radio"  name="status" id="status" value="apply"
                                        <#if entity?exists && entity.status == 'apply'> checked </#if>/>
                                <i></i>申请
                            </label>

                            <label>
                                <input type="radio"  name="status" id="status" value="enable"
                                        <#if entity?exists && entity.status == 'enable'> checked </#if>/>
                                <i></i>启用
                            </label>

                            <label><input type="radio"  name="status" id="status" value="disable"
                                        <#if entity?exists && entity.status == 'disable'> checked </#if>/>
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


        $('.usertypeLabel').on("click",function() {
            var input = $(this).find("input");

            var userType = $(input).data('usertype');
            if(userType == 'staff')
            {
                $("#agentNameDIV").show();
            }
            else
            {
                $("#agentNameDIV").hide();
            }
        });


        //确认保存
        $("#submitBtn").click(function () {
            var id = $('input[name="id"]').val();
            var name = $('input[name="name"]').val();
            var username = $('input[name="username"]').val();

            var level = $('select[name="level"]').val();
            var status = $('input[name="status"]:checked').val();

            if ( isEmpty(name) || isEmpty(status) ){
                $.global.openErrorMsg('* 号必填参数不能为空');
                return;
            }

            $.ajax({
                type: "post",
                async: false,
                url: "/alibaba888/Liv2sky3soLa93vEr62/editAdMallStoreInfo",
                data: {
                    id:id,
                    name: name,
                    username:username,
                    level:level.trim(),
                    status:status,
                },
                dataType: "json",
                success: function (data) {
                    if (data != null && data.code == 200) {
                        $.global.openSuccessMsg("保存成功",function(){
                            window.close();
                        });
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

</script>
</body>
</html>
