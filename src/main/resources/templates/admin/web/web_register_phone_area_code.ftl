<!DOCTYPE HTML>
<html>
<head>
    <#include "../../common/head-meta.ftl"/>
    <title>${projectName}商户后台管理系统-订单管理</title>
    <link type="text/css" rel="stylesheet" href="${STATIC_URL}/plugins/bootstrap-table/bootstrap-table.min.css"/>
</head>

<body class="gray-bg">
<div class="wrapper wrapper-content animated fadeInRight">
    <div class="ibox">

        <div class="form-group">
            <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>信息内容:</label>
            <div class="col-sm-12">
                <textarea class="form-control"  rows="20" cols="20" id="reply" name="reply"  ><#if content??>${content}</#if></textarea>

            </div>
        </div>
        <div class="hr-line-dashed"></div>


        <div class="form-group" style="margin-top: 2rem">
            <div class="col-sm-4 col-sm-offset-2">
                <input class="btn btn-primary" type="submit" id="submit" value="保存"/>
            </div>
        </div>

    </div>
</div>

<script type="text/javascript" src="${STATIC_URL}/plugins/UEditor/js/ueditor.config.js"></script>
<script type="text/javascript" src="${STATIC_URL}/plugins/UEditor/js/ueditor.all.js"></script>
<script type="text/javascript" src="${STATIC_URL}/plugins/bootstrap-table/bootstrap-table.min.js"></script>
<script type="text/javascript" src="${STATIC_URL}/plugins/bootstrap-table/locale/bootstrap-table-zh-CN.min.js"></script>
<script type="text/javascript">

    $(function () {

    });

    $('#submit').click(function () {

        saveContent();
    });


    function saveContent() {

        var input=document.getElementById("reply");//通过id获取文本框对象
        var reply =  input.value;
        if( isEmpty(reply) )
        {
            $.global.openErrorMsg('* 号必填参数不能为空');
            return false;
        }

        $.ajax({
            type: "POST",//方法类型
            dataType: "json",//预期服务器返回的数据类型
            url: "/alibaba888/Liv2sky3soLa93vEr62/updateWebInfo" ,//url
            data: {
                type:'register_phone_area_code',
                content:reply
            },
            success: function (data) {
                if (data != null && data.code == 200) {
                    $.global.openSuccessMsg("保存成功");
                } else {
                    $.global.openErrorMsg(data.msg);
                }
            },
            error : function() {
                $.global.openErrorMsg('保存失败，请重试');
            }
        });
    }

</script>
</body>
</html>
