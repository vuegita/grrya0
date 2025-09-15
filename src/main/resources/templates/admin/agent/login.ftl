<!DOCTYPE HTML>
<html>
<head>
    <#include "../../common/head-meta.ftl"/>
    <title>${projectName}商户后台管理系统-登录</title>
</head>

<body class="gray-bg">
    <div class="middle-box text-center loginscreen  animated fadeInDown">
<#--        <div>-->
<#--            <img src="${STATIC_SERVER}/images/${ENV}loginLogoOne.png"/>-->
<#--        </div>-->
<#--        <h3>欢迎使用 ${projectName}商户后台管理系统</h3>-->
        <h3>Welcome</h3>
        <form class="m-t" id="login-form">
            <div class="form-group">
                <input type="text" class="form-control" required name="account" id="account" autocomplete="off" placeholder="Account" />
            </div>
            <div class="form-group">
                <input type="password" class="form-control" required  name="password" id="password" autocomplete="off" placeholder="Password" />
            </div>
            <div class="form-group" id="imgCodeDIV">
                <div class="input-group">
                <input type="text" maxlength="4" id="imgcode" name="imgcode" class="form-control" placeholder="Verify code">
                <a class="input-group-addon" onclick="return false;" href="javascript:;">
                    <img onclick="this.src='/alibaba888/agent/refreshImageVerifyCode?t='+Math.random();" src="/alibaba888/agent/refreshImageVerifyCode" >
                </a>
                </div>
            </div>
            <div class="form-group hidden" id="googleCodeDIV">
                <input type="text" class="form-control" required  name="googlecode" id="googlecode" autocomplete="off" placeholder="Google code" />
            </div>
            <input type="button" class="btn btn-primary block full-width m-b" id="submit" value="Login"/>
        </form>
    </div>
    <script type="text/javascript" src="${STATIC_URL}/js/jquery.md5.js"></script>
    <script type="text/javascript">

        window.onload = function(){
            if (window.top!=null && window.top.document.URL!=document.URL){
                window.top.location.href = document.URL;
            }
        };

        $(function(){

            $("#verCode").keypress(function (e) {
                if (e.which == 13) {
                    document.getElementById("submit").click();
                }
            });

            $(document).on('click', '#submit', function(){
                var password = $("#password").val();
                if(password.length == 0){
                    $.global.openErrorMsg('Please input password!');
                }
                $("#password").val($.md5(password));

                var formData = $('#login-form').serialize();
                var $loginBtn = $(this);
                $loginBtn.attr('disabled', true);
                $.ajax({
                    url:'/alibaba888/agent/login',
                    type: 'post',
                    dataType: 'json',
                    data: formData,
                    success: function(result){
                        if(result.code === 200){
                            if (result.data){
                                //window.location.href ='/alibaba888/merchant/backstage/toGoogleLogin';

                                $("#googleCodeDIV").removeClass("hidden");
                                $("#imgCodeDIV").addClass("hidden");

                                return;
                            }
                            window.location.href ='/alibaba888/agent/toIndex';
                            return;
                        }
                        $.global.openErrorMsg(result.msg);
                    },
                    error: function(e){
                        console.log("login error: ", e);
                        $.global.openErrorMsg('System Error, Login Fair!');
                    },
                    complete: function(){
                        $loginBtn.attr('disabled', false);
                        $("#password").val(password)
                    }
                });
            });
        });
    </script>
</body>
</html>
