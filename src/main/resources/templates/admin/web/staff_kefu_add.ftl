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

                <#if staffKefu??>
                    <input type="hidden" id="id" name="id" value="${staffKefu.id}" autocomplete="off" required maxlength="50"/>
                </#if>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>员工名:</label>
                    <div class="col-sm-3">
                        <input class="form-control" id="staffname" name="staffname" autocomplete="off" required maxlength="50" value="<#if staffKefu??>${staffKefu.staffname}</#if>" />
                    </div>
<#--                    <button id="search-btn" type="button" class="btn btn-outline btn-default" title="查询">-->
<#--                        <i class="glyphicon glyphicon-search" aria-hidden="true"></i> 搜索-->
<#--                    </button>-->
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>客服类型:</label>
                    <div class="col-sm-3">
                        <select class="form-control" id="describe" name="describe" >
                            <option value="">请选择客服类型</option>
                            <#list staffkefuTypeArr as group>
                                <option value="${group.key}" <#if staffKefu?exists && staffKefu.describe == group.key> selected </#if> >${group.key}</option>
                            </#list>
                        </select>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>客服号:</label>
                    <div class="col-sm-3">
                        <input type="text" class="form-control" id="title" name="title" value="<#if staffKefu??>${staffKefu.title}</#if>" autocomplete="off" maxlength="50"
                               value=""/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>




                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>客服链接:</label>
                    <div class="col-sm-6">
                        <input type="text" class="form-control" id="whatsapp" name="whatsapp" value="<#if staffKefu??>${staffKefu.whatsapp}</#if>" autocomplete="off" maxlength="255"
                               value=""/>
                    </div>
                </div>
<#--                <div class="form-group">-->
<#--                    <label class="control-label" style="color: red;margin-left: 200px">添加telegram类型时，请填写0！！！</label><br>-->
<#--                </div>-->
                <div class="hr-line-dashed"></div>



<#--                <div class="form-group">-->
<#--                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>telegram号:</label>-->
<#--                    <div class="col-sm-3">-->
<#--                        <input type="text" class="form-control" id="describe" name="describe" value="<#if staffKefu??>${staffKefu.describe}</#if>" autocomplete="off" maxlength="100"-->
<#--                               value=""/>-->
<#--                    </div>-->
<#--                </div>-->
<#--                <div class="hr-line-dashed"></div>-->

<#--                <div class="form-group">-->
<#--                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>telegram链接:</label>-->
<#--                    <div class="col-sm-6">-->
<#--                        <input type="text" class="form-control" id="telegram" name="telegram" value="<#if staffKefu??>${staffKefu.telegram}</#if>" autocomplete="off" maxlength="255"-->
<#--                               value=""/>-->
<#--                    </div>-->
<#--                </div>-->
<#--                <div class="form-group">-->
<#--                    <label class="control-label" style="color: red;margin-left: 200px">添加whatsapp类型时，请填写0！！！</label><br>-->
<#--                </div>-->
<#--                <div class="hr-line-dashed"></div>-->

                <div class="form-group">
                    <label class="control-label col-sm-2">状态:</label>
                    <div class="col-sm-3">
                        <div class="radio i-checks">
                            <#if !staffKefu??>
                                <label><input type="radio" name="status" value="enable" checked/> <i></i>启用</label>
                                <label><input type="radio" name="status" value="disable" /> <i></i>禁用</label>
                            <#else>
                                <label><input type="radio" name="status" value="enable" <#if staffKefu.status == "enable"> checked </#if> /> <i></i>启用</label>
                                <label><input type="radio" name="status" value="disable" <#if staffKefu.status == "disable"> checked </#if> /> <i></i>禁用</label>
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
            debugger
            var staffname = $('input[name="staffname"]').val();
            var title =  $('input[name="title"]').val();
           // var describe = $('input[name="describe"]').val();
            var describe =$('select[name="describe"]').val();
            var whatsapp =  $('input[name="whatsapp"]').val();
            var telegram = "0";//$('input[name="telegram"]').val();
            var status = $('input[name="status"]:checked').val();

            if(isEmpty(staffname) || isEmpty(title) || isEmpty(describe) || isEmpty(whatsapp) || isEmpty(telegram) || isEmpty(status))
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
        var title =  $('input[name="title"]').val();
       // var describe = $('input[name="describe"]').val();
        var describe =$('select[name="describe"]').val();
        var whatsapp =  $('input[name="whatsapp"]').val();
        var telegram = "0"; //$('input[name="telegram"]').val();
        var status = $('input[name="status"]:checked').val();

        if(isEmpty(staffname) || isEmpty(title) || isEmpty(describe) || isEmpty(whatsapp) || isEmpty(telegram) || isEmpty(status))
        {
            $.global.openErrorMsg('* 号必填参数不能为空');
            return false;
        }

        $.ajax({
            url: '/alibaba888/Liv2sky3soLa93vEr62/editWebStaffKefu',
            type: 'post',
            dataType: 'json',
            data:{
                id:id,
                staffname:staffname,
                title:title,
                describe:describe,
                whatsapp:whatsapp,
                telegram:telegram,
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
