<!DOCTYPE HTML>
<html>
<head>
    <#include "../../common/head-meta.ftl"/>
    <title>${projectName}后台管理系统</title>
</head>
<style>
    input[type="number"] {
        width: 18% !important;
    }

    .minData {
        width: 42% !important;
    }

    .form-control-input {
        width: 100%;
        height: 34px;
        padding: 6px 12px;
        font-size: 14px;
        line-height: 1.42857143;
        color: #555;
        background-color: #fff;
        background-image: none;
        border: 1px solid #ccc;
        border-radius: 4px;
        -webkit-box-shadow: inset 0 1px 1px rgba(0, 0, 0, .075);
        box-shadow: inset 0 1px 1px rgba(0, 0, 0, .075);
        -webkit-transition: border-color ease-in-out .15s, -webkit-box-shadow ease-in-out .15s;
        -o-transition: border-color ease-in-out .15s, box-shadow ease-in-out .15s;
        transition: border-color ease-in-out .15s, box-shadow ease-in-out .15s;
    }
</style>
<body class="gray-bg">
<div class="wrapper wrapper-content animated fadeInRight">
    <div class="ibox float-e-margins">
        <div class="ibox-title">
            <h5></h5>
            <p style="color: blue"> 　</p>
            <p  style="color: green">1. 短信万能码生成之后10分钟内有效!</p>
            <p  style="color: green">2. 一个短信万能码最多使用一次，即一对一关系，使用完成后销毁!</p>
        </div>

        <div class="ibox-content">
            <form id="form" class="form-horizontal" autocomplete="off">

                <div class="panel panel-danger">

                    <div class="panel-heading">
                        <h3 class="panel-title">APP设置</h3>
                    </div>
                    <div class="panel-body">

                        <div class="form-group">
                            <label class="control-label col-sm-2">版本号:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="version" id="version" autocomplete="off"
                                       required maxlength="100" type="text"
                                       value="<#if config?exists>${config.version !}</#if>" />
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>

                        <div class="form-group">
                            <label class="control-label col-sm-2">版本说明:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="desc" id="desc" autocomplete="off"
                                       required maxlength="10" type="text"
                                       value="<#if config?exists>${config.desc !}</#if>" />
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>

                        <div class="form-group">
                            <label class="control-label col-sm-2">下载链接:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="download_url" id="download_url" readonly autocomplete="off"
                                       required maxlength="10" type="text"
                                       value="<#if config?exists>${config.download_url !}</#if>" />
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>


                    </div>

                </div>

                <div class="form-group">
                    <div class="col-sm-4 col-sm-offset-2">
                        <input class="btn btn-primary" type="submit" id="submit" value="保存"/>
                        <input class="btn btn-primary" type="submit" id="btnCreateSystemSmsCode" value="生成短信万能码"/>
                    </div>
                </div>
            </form>
        </div>

        <div class="ibox-content">
            <form id="formAPP" class="form-horizontal" autocomplete="off" enctype="multipart/form-data">

                <div class="panel panel-danger">

                    <div class="panel-heading">
                        <h3 class="panel-title">上传APP</h3>
                    </div>
                    <div class="panel-body">

                        <div class="form-group">
                            <label class="control-label col-sm-2">上传APP:</label>
                            <div class="col-sm-10">
                                <input  name="file" id="input-file" type="file"/>
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>


                    </div>

                </div>

                <div class="form-group">
                    <div class="col-sm-4 col-sm-offset-2">
                        <input class="btn btn-primary" type="submit" id="uploadAPP" value="上传APP"/>
                    </div>
                </div>
            </form>
        </div>




    </div>
</div>
<!-- loading -->
<div class="modal fade" id="loading" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" data-backdrop='static'>
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="myModalLabel">提示</h4>
            </div>
            <div class="modal-body">
                上传中。。。<span id="result"></span>
            </div>
        </div>
    </div>
</div>


<#include "../../common/delete_form.ftl">
<script type="text/javascript" src="${STATIC_URL}/plugins/validate/jquery.validate.min.js"></script>
<script type="text/javascript" src="${STATIC_URL}/plugins/validate/messages_zh.min.js"></script>
<script type="text/javascript" src="${STATIC_URL}/js/validate.defaults.js?v=${version}"></script>
<script type="text/javascript" src="${STATIC_URL}/lib/jquery.ajaxfileupload.js"></script>
<#--<script type="text/javascript" src="../../../../static/gv.js"></script>-->
<script>
    lay('#version').html('-v' + laydate.v);
    //时间选择器
    laydate.render({
        elem: '#user_withdraw_start_time',
        type: 'time',
        format: 'HH:mm'
    });
    //时间选择器
    laydate.render({
        elem: '#user_withdraw_end_time',
        type: 'time',
        format: 'HH:mm'
    });
    //input输入框只能输入数字和 小数点后两位
    function inputNum(obj,val){
        obj.value = obj.value.replace(/[^\d.]/g,""); //清除"数字"和"."以外的字符
        obj.value = obj.value.replace(/^\./g,""); //验证第一个字符是数字
        obj.value = obj.value.replace(/\.{2,}/g,""); //只保留第一个, 清除多余的
        obj.value = obj.value.replace(/^(\-)*(\d+)\.(\d).*$/,'$1$2.$3'); //只能输入两个小数
        if(obj.value.indexOf(".")< 0 && obj.value !=""){//以上已经过滤，此处控制的是如果没有小数点，首位不能为类似于 01、02的金额
            obj.value= parseFloat(obj.value);
        }
    }
</script>
<script type="text/javascript">

    function dateFormatter(value, row, index) {
        return DateUtils.formatyyyyMMddHHmm(value);
    }

    $('#submit').click(function () {

        $('#myModalDel #tips').text('确定保存吗？');
        $('#myModalDel #type').attr('action', 'updateBasicMobileAPPConfig');
        $('#myModalDel').modal();

        return false;
    });

    $('#uploadAPP').click(function () {

        var $file1 = $("input[name='file']").val();//用户文件内容(文件)
        // 判断文件是否为空
        if ($file1 == "") {
            alert("请选择上传的目标文件! ")
            return false;
        }

        $('#myModalDel #tips').text('确定上传吗？');
        $('#myModalDel #type').attr('action', 'uploadAPPFile');
        $('#myModalDel').modal();

        return false;
    });

    $('#btnCreateSystemSmsCode').click(function () {

        $.ajax({
            type: "GET",//方法类型
            dataType: "json",//预期服务器返回的数据类型
            url: "/alibaba888/Liv2sky3soLa93vEr62/createBasicMobileSmsCode" ,//url
            success: function (data) {
                if (data != null && data.code == 200) {
                    $('#myModalDel #type').attr('action', 'createBasicMobileSmsCode');
                    $('#myModalDel #tips').text('已生成唯一万能码 【' + data.data + '】, 10分钟内有效!');
                    $('#myModalDel').modal();
                } else {
                    $.global.openErrorMsg(data.msg);
                }
            },
            error : function() {
                $.global.openErrorMsg('保存失败，请重试');
            }
        });

        return false;
    });

    $('#delete_submit').click(function () {

        var data = $('#form').serialize();
        var dataapp = $('#formAPP')
        var action = $('#myModalDel #type').attr('action');

        if("createBasicMobileSmsCode" == action)
        {

        }
        else if("updateBasicMobileAPPConfig" == action)
        {
            $.ajax({
                type: "POST",//方法类型
                dataType: "json",//预期服务器返回的数据类型
                url: "/alibaba888/Liv2sky3soLa93vEr62/updateBasicMobileAPPConfig" ,//url
                data: $('#form').serialize(),
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
        else if("uploadAPPFile" == action)
        {//显示
            $('#loading').modal('show');
            var fileObj = document.getElementById("input-file").files[0];
            var formData = new FormData();
            formData.append("file", fileObj);

            $.ajax({
                type : "post",
                dataType: "json",//预期服务器返回的数据类型
                url: "/alibaba888/Liv2sky3soLa93vEr62/uploadAPPFile" ,//url
                data : formData,
                processData : false,
                contentType : false,

                success: function (data) {
                    if (data != null && data.code == 200) {
                        //隐藏
                        $('#loading').modal('hide');
                        $.global.openSuccessMsg("上传成功");

                        setTimeout(()=>{
                            location.reload();
                        },1000)


                    } else {
                        //隐藏
                        $('#loading').modal('hide');
                        $.global.openErrorMsg(data.msg);
                    }
                },
                error : function() {
                    //隐藏
                    $('#loading').modal('hide');
                    $.global.openErrorMsg('上传失败，请重试');
                }
            });

        }


    });


</script>
</body>
</html>
