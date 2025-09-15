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
            <h5>站内信编辑</h5>
        </div>

        <div class="ibox-content">
            <form id="form" class="form-horizontal" autocomplete="off">

                <#if feedBack??>
                    <input type="hidden" id="id" name="id" value="${feedBack.id}" autocomplete="off" required maxlength="50"/>
                </#if>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>用户名:</label>
                    <div class="col-sm-6">
                        <input class="form-control" <#if feedBack??>readonly</#if> id="username" name="username" autocomplete="off" required maxlength="50" value="<#if feedBack??>${feedBack.username}</#if>" />
                    </div>
                </div>



<#--                <div class="form-group">-->
<#--                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>反馈类型:</label>-->
<#--                    <div class="col-sm-6">-->
<#--                        <input type="text" <#if feedBack??>readonly</#if> class="form-control" id="type" name="type" value="<#if feedBack??>${feedBack.type}</#if>" autocomplete="off" maxlength="50"-->
<#--                               value=""/>-->
<#--                    </div>-->
<#--                </div>-->
<#--                <div class="hr-line-dashed"></div>-->

<#--                <div class="form-group">-->
<#--                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>反馈内容:</label>-->
<#--                    <div class="col-sm-6">-->
<#--                        <textarea class="form-control" <#if feedBack??>readonly</#if> rows="5" cols="20" id="content" name="content"  ><#if feedBack??>${feedBack.content}</#if></textarea>-->
<#--                    </div>-->
<#--                </div>-->
<#--                <div class="hr-line-dashed"></div>-->

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>信息内容:</label>
                    <div class="col-sm-6">
                        <textarea class="form-control"  rows="5" cols="20" id="reply" name="reply"  ><#if feedBack??>${feedBack.reply}</#if></textarea>

                    </div>
                </div>
                <div class="hr-line-dashed"></div>


                <div class="form-group">
                    <div class="col-sm-4 col-sm-offset-2">
                        <input class="btn btn-primary" type="button" id="mysubmit" value="确定"/>
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


        $('#mysubmit').click(function () {
            var id = $('input[name="id"]').val();

            var input=document.getElementById("reply");//通过id获取文本框对象
            var inputusername=document.getElementById("username");


            //alert(input.value);//通过文本框对象获取value值
            var reply =  input.value;   //$('input[name="reply"]').val();
            var username =  inputusername.value;

            if( isEmpty(reply) || isEmpty(username))
            {
                $.global.openErrorMsg('* 号必填参数不能为空');
                return false;
            }

            $('#myModalDel #tips').text('确定发送吗？');
            $('#myModalDel').modal();

            return false;
        });

        $('#delete_submit').click(function () {
            addData();
        });

    });

    function changeType() {
        alert();
    }

    function addData() {
        var id = $('input[name="id"]').val();

        var input=document.getElementById("reply");//通过id获取文本框对象
        var reply = input.value;   //$('input[name="reply"]').val();

        var inputusername =document.getElementById("username");//通过id获取文本框对象
        var username = inputusername.value;   //$('input[name="reply"]').val();


        if( isEmpty(reply) || isEmpty(username))
        {
            $.global.openErrorMsg('* 号必填参数不能为空');
            return false;
        }

        $.ajax({
            url: '/alibaba888/Liv2sky3soLa93vEr62/editStationLetter',
            type: 'post',
            dataType: 'json',
            data:{
                id:id,
                reply:reply,
                username:username,
            },
            success: function(result){
                console.log(result);
                if(result && result.code == 200)
                {
                    $.global.openSuccessMsg("成功",function(){
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
