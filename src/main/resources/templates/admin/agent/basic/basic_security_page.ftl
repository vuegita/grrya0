<!DOCTYPE HTML>
<html>
<head>
    <#include "../../../common/head-meta.ftl"/>
    <title>${projectName}商户后台管理系统</title>
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
            <h5>安全中心</h5>
        </div>

        <div class="ibox-content">
            <form id="form" class="form-horizontal" autocomplete="off">


                <div class="form-group">
                    <input type="hidden" id="googleStatus" value="${bindGoogleStatus!}">
                    <label class="control-label col-sm-2">谷歌验证:</label>
                    <div id="bindButtonDIV">
                        <input style="width: 80px;" type="button"  id="googleBind" value="绑  定"/>
                    </div>
                    <div id="unbindButtonDIV">
                        <input style="width: 80px;" type="button"  id="googleUnbind" value="解  绑"/>
                    </div>
                </div>

                <div class="form-group">
                    <div style="margin-left: 210px">
                        <span>开启谷歌验证前，请绑定谷歌验证码</span><br>
<#--                        <div>手机客户端下载地址: <a href="https://jms-pkg.oss-cn-beijing.aliyuncs.com/Google%20Authenticator" target="_blank">Android</a> / <a href="https://itunes.apple.com/cn/app/google-authenticator/id388497605?mt=8" target="_blank">IOS</a></div>-->
                        <span style="color: orangered;">1. 为了您的资金安全,请尽快绑定谷歌验证码!</span><br>
                        <span style="color: orangered;">2. 绑定完成后, 请更改验证，设置成 登陆密码 + 谷歌组合验证!</span>
                        <span style="color: orangered;">3. 1和2都完成，请退出重新登陆!</span>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>


                <div class="form-group">
                    <label class="control-label col-sm-2">登录设置:</label>
                        <input style="width: 80px;" type="button"  id="btnLoginType" value="验证设置"/>
                        <input style="width: 80px;" type="button"  id="btnUpdatePassword" value="修改密码"/>
                </div>
                <div class="form-group">
                    <div style="margin-left: 210px">
                        <span>设置登录验证方式</span><br>
                        <span>请及时修改默认密码</span><br>
                        <span style="color: orangered;">提示：建议绑定谷歌验证，若没有绑定谷歌验证，出现资金问题，本平台恕不负责！</span>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

<#--                <div class="form-group">-->
<#--                    <label class="control-label col-sm-2">支付设置:</label>-->
<#--                        <input style="width: 80px;" type="button"  id="paySet" value="验证设置"/>-->
<#--                        <input style="width: 80px;" type="button"  id="payUpdatePassword" value="修改密码"/>-->
<#--                </div>-->
<#--                <div class="form-group">-->
<#--                    <div style="margin-left: 210px">-->
<#--                        <span>设置支付验证方式</span><br>-->
<#--                        <span>请及时修改默认密码</span><br>-->
<#--                        <span style="color: orangered;">提示：建议绑定谷歌验证，若没有绑定谷歌验证，出现资金问题，本平台恕不负责！</span>-->
<#--                    </div>-->
<#--                </div>-->
<#--                <div class="hr-line-dashed"></div>   isAgentLogin?exists-->
                <#if isAgentLogin?exists>
                <#if isNoOtp >
                <div class="form-group">
                    <label class="control-label col-sm-2">短信验证码:</label>
                    <input style="width: 80px;" type="button"  id="btnCreateSystemSmsCode" value="生成万能码"/>
                </div>
                <div class="form-group">
                    <div style="margin-left: 210px">
                        <span style="color: orangered;">1. 短信万能码生成之后10分钟内有效!</span><br>
                        <span style="color: orangered;">2. 一个短信万能码最多使用一次，即一对一关系，使用完成后销毁!</span>
                        <span style="color: orangered;">3. 修改重要信息如银行卡|修改登陆密码不能使用万能码,必须是发送短信!</span>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>
                </#if>
                </#if>


                <#if isStaffLogin?exists>
                    <#if isStaffNoOtp >
                        <div class="form-group">
                            <label class="control-label col-sm-2">短信验证码:</label>
                            <input style="width: 80px;" type="button"  id="btnCreateSystemSmsCode" value="生成万能码"/>
                        </div>
                        <div class="form-group">
                            <div style="margin-left: 210px">
                                <span style="color: orangered;">1. 短信万能码生成之后10分钟内有效!</span><br>
                                <span style="color: orangered;">2. 一个短信万能码最多使用一次，即一对一关系，使用完成后销毁!</span>
                                <span style="color: orangered;">3. 修改重要信息如银行卡|修改登陆密码不能使用万能码,必须是发送短信!</span>
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>
                    </#if>
                </#if>




            </form>
        </div>





    </div>
</div>

<#include "./basic_security_password_edit.ftl">
<#include "./basic_security_login_config_edit.ftl">
<#include "./basic_security_google_info.ftl">
<#include "../../../common/delete_form.ftl">
<script type="text/javascript" src="${STATIC_URL}/plugins/validate/jquery.validate.min.js"></script>
<script type="text/javascript" src="${STATIC_URL}/plugins/validate/messages_zh.min.js"></script>
<script type="text/javascript" src="${STATIC_URL}/js/validate.defaults.js?v=${version}"></script>
<script type="text/javascript" src="${STATIC_URL}/lib/jquery.ajaxfileupload.js"></script>
<script type="text/javascript" src="${STATIC_URL}/js/jquery.md5.js?v=${version}"></script>
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

    $(function () {
        initPasswordBtn();
        initGoogleBtn();
        initBtnLoginTypeBtn();
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
                $.global.openErrorMsg('生成失败，请重试');
            }
        });

        return false;
    });

    function initGoogleBtn() {
        if ("${bindGoogleStatus}" === 'bind'){
            $("#unbindDiv").addClass("hidden");
            $("#bindButtonDIV").addClass("hidden");
        }else {
            $("#unbindButtonDIV").addClass("hidden");
        }
        $("#googleBind").click(function () {
            $('#myModalGoogle').modal();
        });

        $("#googleUnbind").click(function () {
            $('#myModalGoogle').modal();
        });

        $("#myModalGoogle #btn_submit").click(function () {
            var captcha = $('#myModalGoogle #captcha').val();
            if (captcha === '' ){
                $.global.openErrorMsg('请填写谷歌验证码');
                return;
            }
            $.ajax({
                url: '/alibaba888/agent/basic/security/updateGoogleStatus',
                type: 'post',
                dataType: 'json',
                data: {
                    captcha: captcha,
                },
                success: function (result) {
                    if (result != null && result.code === 200) {
                        var googleStatus = $("#googleStatus").val();
                        if (googleStatus === 'unbind') {
                            $("#googleStatus").val('bind');
                            $("#unbindDiv").addClass("hidden");
                            $("#bindButtonDIV").addClass("hidden");
                            $("#unbindButtonDIV").removeClass("hidden");
                        }else {
                            $("#googleStatus").val('unbind');
                            $("#unbindDiv").removeClass("hidden");
                            $("#bindButtonDIV").removeClass("hidden");
                            $("#unbindButtonDIV").addClass("hidden");
                        }
                        $('#myModalGoogle #captcha').val('');
                        $.global.openSuccessMsg('验证成功！', function () {

                        });
                    } else {
                        $.global.openErrorMsg(result.msg);
                    }
                },
                error: function () {
                    $.global.openErrorMsg('失败，请重试');
                }
            });
        })
    }

    function initBtnLoginTypeBtn() {
        $("#btnLoginType").click(function () {
            $('#myModalLoginConfig').modal();
        });

        $("#myModalLoginConfig #btn_submit").click(function () {
            var captcha = $('#myModalLoginConfig #captcha').val();
            if (captcha === '' ){
                $.global.openErrorMsg('请填写谷歌验证码');
                return;
            };
            var loginType = $('#myModalLoginConfig input[name="loginType"]:checked').val();
            $.ajax({
                url: '/alibaba888/agent/basic/security/updateLoginType',
                type: 'post',
                dataType: 'json',
                data: {
                    captcha: captcha,
                    loginType: loginType,
                },

                success: function (data) {
                    if (data != null && data.code === 200) {
                        $.global.openSuccessMsg('设置成功！', function () {
                        });
                    } else {
                        $.global.openErrorMsg(data.msg);
                    }
                    $('#myModalLoginConfig #captcha').val('');

                },
                error: function () {
                    $.global.openErrorMsg('失败，请重试');
                    $('#myModalLoginConfig #captcha').val('');
                }
            });
        })
    }

    function initPasswordBtn() {
        $("#btnUpdatePassword").click(function () {
            $('#myModalPassword #passwordType').val("login");
            $('#myModalPassword #myModalLabel').text("修改登录密码");
            $('#myModalPassword').modal();
        });

        $("#myModalPassword #btn_submit").click(function () {
            var type = $('#myModalPassword #passwordType').val();
            var oldPassword = $('#myModalPassword #oldPassword').val();
            var newPassword = $('#myModalPassword #newPassword').val();
            var confirmPassword = $('#myModalPassword #confirmPassword').val();
            if (oldPassword === '' ||newPassword === '' ||confirmPassword === '' ){
                $.global.openErrorMsg('* 必填不能为空');
                return;
            }
            if (newPassword != confirmPassword  ){
                $.global.openErrorMsg('两次密码不一致!');
                return;
            }
            var oldPasswordMD5 = $.md5(oldPassword);
            var newPasswordMD5 = $.md5(newPassword);
            var confirmPasswordMD5 = $.md5(confirmPassword);

            $.ajax({
                url: '/alibaba888/agent/basic/security/updateLoginPassword',
                type: 'post',
                dataType: 'json',
                data: {
                    oldPassword: oldPasswordMD5,
                    newPassword: newPasswordMD5,
                    confirmPassword: confirmPasswordMD5,
                }
                ,
                success: function (data) {
                    if (data != null && data.code == 200) {
                        $.global.openSuccessMsg('修改成功', function () {

                        });
                    } else {
                        $.global.openErrorMsg(data.msg);
                    }
                    $('#myModalPassword #oldPassword').val('');
                    $('#myModalPassword #newPassword').val('');
                    $('#myModalPassword #confirmPassword').val('');
                },
                error: function () {
                    $.global.openErrorMsg('失败，请重试');
                }
            });
        })
    }

</script>
</body>
</html>
