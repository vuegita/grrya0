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
            <h5>编辑员工红包设置</h5>
        </div>

        <div class="ibox-content">
            <form id="form" class="form-horizontal" autocomplete="off">

                <#if redPStaffLimit??>
                    <input type="hidden" id="id" name="id" value="${redPStaffLimit.id}" autocomplete="off" required maxlength="50"/>
                </#if>
                <#if redPStaffLimit??>
                    <input type="hidden" id="staffid" name="staffid" value="${redPStaffLimit.staffid}" autocomplete="off" required maxlength="50"/>
                </#if>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>员工名:</label>
                    <div class="col-sm-3">
                        <input class="form-control" id="staffname" name="staffname" autocomplete="off" required maxlength="50" value="<#if redPStaffLimit??>${redPStaffLimit.staffname}</#if>" />
                    </div>
<#--                    <button id="search-btn" type="button" class="btn btn-outline btn-default" title="查询">-->
<#--                        <i class="glyphicon glyphicon-search" aria-hidden="true"></i> 搜索-->
<#--                    </button>-->
                </div>

                <div class="form-group">
                    <label class="control-label col-sm-2">单笔最大金额:</label>
                    <div class="col-sm-3">
                        <input class="form-control" name="maxMoneyOfSingle" id="maxMoneyOfSingle" autocomplete="off"
                               required type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');" value="<#if redPStaffLimit??>${redPStaffLimit.maxMoneyOfSingle}</#if>" placeholder="" />
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>每天最大金额:</label>
                    <div class="col-sm-3">
                        <input class="form-control" name="maxMoneyOfDay" id="maxMoneyOfDay" autocomplete="off"
                               required  type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');" value="<#if redPStaffLimit??>${redPStaffLimit.maxMoneyOfDay}</#if>" />
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>每天发送红包次数:</label>
                    <div class="col-sm-3">
                        <input class="form-control" name="maxCountOfDay" id="maxCountOfDay" autocomplete="off"
                               required  type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');" value="<#if redPStaffLimit??>${redPStaffLimit.maxCountOfDay}</#if>" />
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2">状态:</label>
                    <div class="col-sm-3">
                        <div class="radio i-checks">
                            <#if !redPStaffLimit??>
                                <label><input type="radio" name="status" value="enable" checked/> <i></i>启用</label>
                                <label><input type="radio" name="status" value="disable" /> <i></i>禁用</label>
                            <#else>
                                <label><input type="radio" name="status" value="enable" <#if redPStaffLimit.status == "enable"> checked </#if> /> <i></i>启用</label>
                                <label><input type="radio" name="status" value="disable" <#if redPStaffLimit.status == "disable"> checked </#if> /> <i></i>禁用</label>
                            </#if>
                        </div>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <div class="col-sm-4 col-sm-offset-2">
                        <input class="btn btn-primary" type="button" id="mysubmit" value="保存"/>
                        <button class="btn btn-white" type="button" onclick="window.close();">取消</button>
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

            var username = $('input[name="staffname"]').val();

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
                    username: staffname,
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
            var staffname = $('input[name="staffname"]').val();
            var maxMoneyOfSingle =  $('input[name="maxMoneyOfSingle"]').val();
            var maxMoneyOfDay = $('input[name="maxMoneyOfDay"]').val();
            var maxCountOfDay =  $('input[name="maxCountOfDay"]').val();
            var status = $('input[name="status"]:checked').val();

            if(isEmpty(staffname) || isEmpty(maxMoneyOfSingle) || isEmpty(maxMoneyOfDay) || isEmpty(maxCountOfDay) || isEmpty(status))
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
        var id = $('input[name="id"]').val();
        var staffname = $('input[name="staffname"]').val();
        var maxMoneyOfSingle =  $('input[name="maxMoneyOfSingle"]').val();
        var maxMoneyOfDay = $('input[name="maxMoneyOfDay"]').val();
        var maxCountOfDay =  $('input[name="maxCountOfDay"]').val();
        var status = $('input[name="status"]:checked').val();

        if(isEmpty(staffname) || isEmpty(maxMoneyOfSingle) || isEmpty(maxMoneyOfDay) || isEmpty(maxCountOfDay)  || isEmpty(status))
        {
            $.global.openErrorMsg('* 号必填参数不能为空');
            return false;
        }

        $.ajax({
            url: '/alibaba888/Liv2sky3soLa93vEr62/editStaffLimit',
            type: 'post',
            dataType: 'json',
            data:{
                id:id,
                staffname:staffname,
                maxMoneyOfSingle:maxMoneyOfSingle,
                maxMoneyOfDay:maxMoneyOfDay,
                maxCountOfDay:maxCountOfDay,
                status:status
            },
            success: function(result){
                console.log(result);
                if(result && result.code == 200)
                {
                    $.global.openSuccessMsg("添加成功",function(){
                        window.close();
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
