<!DOCTYPE HTML>
<html>
<head>
    <#include "../../common/head-meta.ftl"/>
    <title>${projectName}后台管理系统-订单管理</title>
    <link type="text/css" rel="stylesheet" href="${STATIC_URL}/plugins/bootstrap-table/bootstrap-table.min.css"/>
    <link type="text/css" rel="stylesheet" href="${STATIC_URL}/css/plugins/webuploader/webuploader.css"/>
    <link type="text/css" rel="stylesheet" href="${STATIC_URL}/css/uploadImg.css"/>
    <script type="text/javascript" src="${STATIC_URL}/js/plugins/webuploader/webuploader.js"></script>

</head>
<body class="gray-bg">
<div class="wrapper wrapper-content animated fadeInRight">
    <div class="ibox float-e-margins">
        <div class="ibox-title">
            <h5>编辑客服</h5>
        </div>

        <div class="ibox-content">
            <form id="form" class="form-horizontal" autocomplete="off">

                <#if memberInfo??>
                    <input type="hidden" id="memberid" name="memberid" value="${memberInfo.id}" autocomplete="off" required maxlength="50"/>
                </#if>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>标题:</label>
                    <div class="col-sm-3">
                        <input type="text" class="form-control" id="title" name="title" value="<#if memberInfo??>${memberInfo.title}</#if>" autocomplete="off" maxlength="255"
                               value=""/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>
                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>whatsapp或telegram账号:</label>
                    <div class="col-sm-3">
                        <input class="form-control" id="name" name="name" value="<#if memberInfo??>${memberInfo.name}</#if>" autocomplete="off" required maxlength="50"/>
                    </div>
                </div>
<#--                <div class="form-group">-->
<#--                    <label class="control-label" style="color: red;margin-left: 200px">前端的客服显示！！！</label><br>-->
<#--                </div>-->
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>描述:</label>
                    <div class="col-sm-3">
                        <input type="text" class="form-control" id="describe" name="describe" value="<#if memberInfo??>${memberInfo.describe}</#if>" autocomplete="off" maxlength="255"
                               value=""/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>客服分组:</label>
                    <div class="col-sm-3">
                        <select class="form-control" id="groupid" name="groupid" >
                            <option value="">请选择分组</option>
                            <#list groupList as group>
                                <option value="${group.id}" <#if memberInfo?exists && memberInfo.groupid == group.id> selected </#if> >${group.name}</option>
                            </#list>
                        </select>
                    </div>
                </div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>whatsapp链接:</label>
                    <div class="col-sm-3">
                        <input type="text" class="form-control" id="whatsapp" name="whatsapp" value="<#if memberInfo??>${memberInfo.whatsapp}</#if>" autocomplete="off" maxlength="255"
                               value=""/>
                    </div>
                </div>
                <div class="form-group">
                    <label class="control-label" style="color: red;margin-left: 200px">添加telegram类型时，请填写0！！！</label><br>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>telegram链接:</label>
                    <div class="col-sm-3">
                        <input type="text" class="form-control" id="telegram" name="telegram" value="<#if memberInfo??>${memberInfo.telegram}</#if>" autocomplete="off" maxlength="255"
                               value=""/>
                    </div>
                </div>
                <div class="form-group">
                    <label class="control-label" style="color: red;margin-left: 200px">添加whatsapp类型时，请填写0！！！</label><br>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2">状态:</label>
                    <div class="col-sm-3">
                        <div class="radio i-checks">
                            <#if !memberInfo??>
                                <label><input type="radio" name="status" value="enable" checked/> <i></i>启用</label>
                                <label><input type="radio" name="status" value="disable" /> <i></i>禁用</label>
                            <#else>
                                <label><input type="radio" name="status" value="enable" <#if memberInfo.status == "enable"> checked </#if> /> <i></i>启用</label>
                                <label><input type="radio" name="status" value="disable" <#if memberInfo.status == "disable"> checked </#if> /> <i></i>禁用</label>
                            </#if>
                        </div>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <div class="col-sm-4 col-sm-offset-2">
                        <input class="btn btn-primary" type="button" id="mysubmit" value="保存"/>
                        <button class="btn btn-white" type="button" onclick="window.history.back();">取消</button>
                    </div>
                </div>


            </form>
        </div>
    </div>
</div>


<#include "../../common/delete_form.ftl">

<script type="text/javascript" src="${STATIC_URL}/plugins/validate/jquery.validate.min.js"></script>
<script type="text/javascript" src="${STATIC_URL}/plugins/validate/messages_zh.min.js"></script>
<script type="text/javascript" src="${STATIC_URL}/js/validate.defaults.js?v=${version}"></script>
<script type="text/javascript" src="${STATIC_URL}/js/jquery.md5.js?v=${version}"></script>
<script type="text/javascript" src="${STATIC_URL}/lib/jquery.ajaxfileupload.js"></script>
<script type="text/javascript">
    $(function () {


        $('#search-btn').click(function(){

            var username = $('input[name="username"]').val();

            if(isEmpty(username))
            {
                $.global.openErrorMsg('* 号必填参数不能为空');
                return;
            }

            $.ajax({
                url: '/alibaba888/Liv2sky3soLa93vEr62/findUserInfo',
                type: 'post',
                dataType: 'json',
                data:{
                    username: username,
                },
                success: function(result){
                    console.log(result);
                    if(result && result.code == 200)
                    {
                        var balance = result.data.balance;
                        if(balance == null)
                        {
                            balance = 0;
                        }
                        $("#balance").prop("value", balance);
                    }
                    else
                    {
                        $.global.openErrorMsg(result.msg);
                    }
                },
                error: function(){
                    $.global.openErrorMsg('系统异常!');
                }
            });
        });

        $('#mysubmit').click(function () {
            var name = $('input[name="name"]').val();
            var title =  $('input[name="title"]').val();
            var describe = $('input[name="describe"]').val();
            var groupid = $('select[name="groupid"]').val();
            var whatsapp =  $('input[name="whatsapp"]').val();
            var telegram = $('input[name="telegram"]').val();
            var status = $('input[name="status"]:checked').val();

            if(isEmpty(name) || isEmpty(title) || isEmpty(describe) || isEmpty(groupid) || isEmpty(whatsapp) || isEmpty(telegram) || isEmpty(status))
            {
                $.global.openErrorMsg('* 号必填参数不能为空');
                return false;
            }

            $('#myModalDel #tips').text('确定提交吗？');
            $('#myModalDel').modal();

            return false;
        });

        $('#delete_submit').click(function () {
            addData();
        });

    });

    function changeType() {
        alert();
        // var type = $('input:radio[name="type"]:checked').val();
        //
        // console.log("type = " + type)
        //
        // if(type === 'bank'){
        //     $("#ifscDIV").removeClass("hidden");
        // }
        // else
        // {
        //     $("#ifscDIV").addClass("hidden");
        // }
    }

    function addData() {
        var memberid = $('input[name="memberid"]').val();
        var name = $('input[name="name"]').val();
        var title =  $('input[name="title"]').val();
        var describe = $('input[name="describe"]').val();
        var groupid = $('select[name="groupid"]').val();
        var whatsapp =  $('input[name="whatsapp"]').val();
        var telegram = $('input[name="telegram"]').val();
        var status = $('input[name="status"]:checked').val();

        if(isEmpty(name) || isEmpty(title) || isEmpty(describe) || isEmpty(groupid) || isEmpty(whatsapp) || isEmpty(telegram) || isEmpty(status))
        {
            $.global.openErrorMsg('* 号必填参数不能为空');
            return false;
        }

        $.ajax({
            url: '/alibaba888/Liv2sky3soLa93vEr62/editKefuMember',
            type: 'post',
            dataType: 'json',
            data:{
                memberid:memberid,
                name: name,
                title:title,
                describe:describe,
                groupid:groupid,
                whatsapp:whatsapp,
                telegram:telegram,
                status:status
            },
            success: function(result){
                console.log(result);
                if(result && result.code == 200)
                {
                    $.global.openSuccessMsg("添加成功",function(){
                        window.history.go(-1);
                    });
                }
                else
                {
                    $.global.openErrorMsg(result.msg);
                }
            },
            error: function(){
                $.global.openErrorMsg('系统异常!');
            }
        });
    }





</script>
</body>
</html>
