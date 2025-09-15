<!DOCTYPE HTML>
<html>
<head>
<#include "../../common/head-meta.ftl"/>
    <title>${projectName}后台管理系统</title>
</head>
<style>
    input[type="number"]{
        width: 18% !important;
    }

    .minData{
        width:42% !important;
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
        -webkit-box-shadow: inset 0 1px 1px rgba(0,0,0,.075);
        box-shadow: inset 0 1px 1px rgba(0,0,0,.075);
        -webkit-transition: border-color ease-in-out .15s,-webkit-box-shadow ease-in-out .15s;
        -o-transition: border-color ease-in-out .15s,box-shadow ease-in-out .15s;
        transition: border-color ease-in-out .15s,box-shadow ease-in-out .15s;
    }
</style>
<body class="gray-bg">
<div class="wrapper wrapper-content animated fadeInRight">
    <div class="ibox float-e-margins">
        <div class="ibox-title">
            <h5>谷歌验证设置</h5>
        </div>

        <div class="ibox-content">
            <form id="form" class="form-horizontal" autocomplete="off">
                <input type="hidden" name="adminHideAccount" id="adminHideAccount"
                       value="<#if admin?exists>${admin.account !}</#if>">

                <div class="form-group">
                    <label class="control-label col-sm-2">登录谷歌验证:</label>
                    <div class="col-sm-3">
                        <div class="radio i-checks">
                            <label><input type="radio" name="googleValidate" value="1" <#if config.value == "1">checked</#if>> <i></i>开启</label>
                            <label><input type="radio" name="googleValidate" value="0" <#if config.value == "0">checked</#if>> <i></i>关闭</label>
                        </div>
                  </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <div class="col-sm-4 col-sm-offset-2">
                        <input class="btn btn-primary" id="mySaveBtn" type="submit" value="保存"/>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>
<div class="modal fade" id="myModalEdit" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                            aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="myModalLabel">提示</h4>
            </div>
            <div class="modal-body">
                <p>确定要保存吗？</p>
                <p>如果确认保存，请输入谷歌验证码</p>
                <p id="adminAccount">操作者：当前登录的后台账号</p>
                <label style="color:#676a6c ;font-weight:500;">谷歌验证码：</label>
                <input class="form-control-input" type="text" name="googleCode" id="googleCode"
                       autocomplete="off" required maxlength="30" onkeyup="this.value=this.value.replace(/\D/g,'')"
                       onafterpaste="this.value=this.value.replace(/\D/g,'')" value="">
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal"><span
                            class="glyphicon glyphicon-remove" aria-hidden="true"></span>取消
                </button>
                <button type="button" id="btn_submit" class="btn btn-primary" data-dismiss="modal">
                    <span class="glyphicon glyphicon-floppy-disk" aria-hidden="true"></span>确认
                </button>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript" src="${STATIC_URL}/plugins/validate/jquery.validate.min.js"></script>
<script type="text/javascript" src="${STATIC_URL}/plugins/validate/messages_zh.min.js"></script>
<script type="text/javascript" src="${STATIC_URL}/js/validate.defaults.js?v=${version}"></script>
<script type="text/javascript" src="${STATIC_URL}/lib/jquery.ajaxfileupload.js"></script>
<#--<script type="text/javascript" src="../../../../static/gv.js"></script>-->
<script>
    lay('#version').html('-v'+ laydate.v);
    //时间选择器
    laydate.render({
        elem: '#withdrawBeginTime',
        type: 'time',
        format:'HH:mm'
    });
    //时间选择器
    laydate.render({
        elem: '#withdrawEndTime',
        type: 'time',
        format:'HH:mm'
    });
</script>
<script type="text/javascript">

    function dateFormatter(value, row, index) {
        return DateUtils.formatyyyyMMddHHmm(value);
    }

    $("#mySaveBtn").click(function () {
        $('#myModalEdit').modal();
        return false;
    });

    //确认保存
    $("#myModalEdit #btn_submit").click(function () {
        $.ajax({
            type: "post",
            url: "/alibaba888/Liv2sky3soLa93vEr62/updateBasicGoogleVerify",
            data: {
                googleValidate: $('input[name="googleValidate"]:checked').val(),
                googleCode: $("#myModalEdit #googleCode").val()
            },
            dataType: "json",
            success: function (data) {
                $('#myModalEdit').modal("hide");

                if (data != null && data.code == 200) {
                    $.global.openSuccessMsg("保存成功");
                } else {
                    $.global.openErrorMsg(data.msg);
                }
            },

            error: function (e) {
                $('#myModalEdit').modal("hide");
                console.log(e);
                $.global.openErrorMsg('保存失败，请重试');
            }
        })

        return false;
    });

    //删除cookies
    function delCookie(name) {
        var exp = new Date();
        exp.setTime(exp.getTime() - 1);
        var cval = getCookie(name);
        if (cval != null)
            document.cookie = name + "=" + cval + ";expires=" + exp.toGMTString();
    }
    //设置cookies
    function setCookie(name,value)
    {
        var Days = 30;
        var exp = new Date();
        exp.setTime(exp.getTime() + Days*24*60*60*1000);
        document.cookie = name + "="+ escape (value) + ";expires=" + exp.toGMTString();
    }
    //读取cookies
    function getCookie(name) {
        var arr, reg = new RegExp("(^| )" + name + "=([^;]*)(;|$)");

        if (arr = document.cookie.match(reg))

            return unescape(arr[2]);
        else
            return null;
    }
</script>
</body>
</html>
