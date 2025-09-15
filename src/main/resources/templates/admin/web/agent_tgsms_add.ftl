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
            <h5>编辑TG配置</h5>
        </div>

        <div class="ibox-content">
            <form id="form" class="form-horizontal" autocomplete="off">

                <#if agnetTips??>
                    <input type="hidden" id="id" name="id" value="${agnetTips.id}" autocomplete="off" required maxlength="50"/>
                </#if>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>用戶名:</label>
                    <div class="col-sm-3">
                        <input class="form-control" id="agentname" name="agentname" autocomplete="off" required maxlength="50" value="<#if agnetTips??>${agnetTips.staffname}</#if>" />
                    </div>
<#--                    <button id="search-btn" type="button" class="btn btn-outline btn-default" title="查询">-->
<#--                        <i class="glyphicon glyphicon-search" aria-hidden="true"></i> 搜索-->
<#--                    </button>-->
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>公告类型:</label>
                    <div class="col-sm-3">
                        <select class="form-control" id="type" name="type" >
                            <option value="">请选择公告类型</option>
                            <#list TipsTypeArr as group>
                                <option value="${group.key}" <#if agnetTips?exists && agnetTips.type == group.key > selected </#if> >${group.key}</option>
                            </#list>
                        </select>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>机器人token:</label>
                    <div class="col-sm-6">
                        <input type="text" class="form-control" id="rbtoken" name="rbtoken" value="<#if agnetTips??>${agnetTips.rbtoken}</#if>" autocomplete="off" maxlength="255"
                               value=""/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>


                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>TG群chatid:</label>
                    <div class="col-sm-6">
                        <input type="text" class="form-control" id="chatid" name="chatid" value="<#if agnetTips??>${agnetTips.chatid}</#if>" autocomplete="off" maxlength="255"
                               value=""/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>




<#--                <div class="form-group">-->
<#--                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>内容:</label>-->
<#--                    <div class="col-sm-6">-->
<#--                        <input type="text" class="form-control" id="content" name="content" value="<#if agnetTips??>${agnetTips.content}</#if>" autocomplete="off" maxlength="255"-->
<#--                               value=""/>-->
<#--                    </div>-->
<#--                </div>-->


                <div class="hr-line-dashed"></div>



                <div class="form-group">
                    <label class="control-label col-sm-2">状态:</label>
                    <div class="col-sm-3">
                        <div class="radio i-checks">
                            <#if !agnetTips??>
                                <label><input type="radio" name="status" value="enable" checked/> <i></i>启用</label>
                                <label><input type="radio" name="status" value="disable" /> <i></i>禁用</label>
                            <#else>
                                <label><input type="radio" name="status" value="enable" <#if agnetTips.status == "enable"> checked </#if> /> <i></i>启用</label>
                                <label><input type="radio" name="status" value="disable" <#if agnetTips.status == "disable"> checked </#if> /> <i></i>禁用</label>
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

<script type="text/javascript" src="${STATIC_URL}/plugins/UEditor/js/ueditor.config.js"></script>
<script type="text/javascript" src="${STATIC_URL}/plugins/UEditor/js/ueditor.all.js"></script>
<script type="text/javascript" src="${STATIC_URL}/plugins/validate/jquery.validate.min.js"></script>
<script type="text/javascript" src="${STATIC_URL}/plugins/validate/messages_zh.min.js"></script>
<script type="text/javascript" src="${STATIC_URL}/js/validate.defaults.js?v=${version}"></script>
<script type="text/javascript" src="${STATIC_URL}/js/jquery.md5.js?v=${version}"></script>
<script type="text/javascript" src="${STATIC_URL}/lib/jquery.ajaxfileupload.js"></script>
<script type="text/javascript">
    $(function () {
        <#--var ue = UE.getEditor('container');-->
        <#--//var ue = UE.getContent();-->
        <#--//对编辑器的操作最好在编辑器ready之后再做-->
        <#--ue.ready(function() {-->
        <#--    //设置编辑器的内容-->
        <#--    ue.setContent('${content!}');-->
        <#--    //获取html内容，返回: <p>hello</p>-->
        <#--    var html = ue.getContent();-->
        <#--    //获取纯文本内容，返回: hello-->
        <#--    var txt = ue.getContentTxt();-->
        <#--});-->


        $('#search-btn').click(function(){

            var username = $('input[name="agentname"]').val();

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

            var agentname = $('input[name="agentname"]').val();
            var rbtoken =  $('input[name="rbtoken"]').val();
            var chatid =  $('input[name="chatid"]').val();
            var type =$('select[name="type"]').val();
           // var content =  $('input[name="content"]').val();
           //  var ue = UE.getEditor('container');
           //  var content = ue.getContent();
            //var input=document.getElementById("container");//通过id获取文本框对象
            //alert(input.value);//通过文本框对象获取value值
           // var content =  input.value;   //$('input[name="reply"]').val();

            var status = $('input[name="status"]:checked').val();

            if(isEmpty(agentname) || isEmpty(rbtoken) || isEmpty(chatid) ||  isEmpty(status)  ||  isEmpty(type))
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
        var agentname = $('input[name="agentname"]').val();
        var rbtoken =  $('input[name="rbtoken"]').val();

        var chatid =  $('input[name="chatid"]').val();
        // var describe =$('select[name="describe"]').val();
        var type =$('select[name="type"]').val(); // var type="agentTips";
       // var content =  $('input[name="content"]').val();
       //  var ue = UE.getEditor('container');
       //  var content = ue.getContent();
       // var input=document.getElementById("container");//通过id获取文本框对象
        //alert(input.value);//通过文本框对象获取value值
        //var content =  input.value;   //$('input[name="reply"]').val();

        var status = $('input[name="status"]:checked').val();

        if(isEmpty(agentname) || isEmpty(rbtoken) || isEmpty(chatid) ||  isEmpty(status))
        {
            $.global.openErrorMsg('* 号必填参数不能为空');
            return false;
        }


        $.ajax({
            url: '/alibaba888/Liv2sky3soLa93vEr62/editWebAgentTgsms',
            type: 'post',
            dataType: 'json',
            data:{
                id:id,
                agentname:agentname,
                rbtoken:rbtoken,
                type:type,
                chatid:chatid,
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
