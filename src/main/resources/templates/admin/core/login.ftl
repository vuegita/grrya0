<!DOCTYPE HTML>
<html>
<head>
    <#include "../../common/head-meta.ftl"/>
    <title>${projectName}后台管理系统-登录</title>
</head>

<body class="gray-bg">
<div class="middle-box text-center loginscreen  animated fadeInDown">
    <h3>Welcome!</h3>
    <form class="m-t" id="login-form">

        <div class="form-group">
            <input type="text" class="form-control" name="username" id="username" autocomplete="off"
                   placeholder="Account"/>
        </div>
        <div class="form-group">
            <input type="password" class="form-control" name="password" id="password" autocomplete="off"
                   placeholder="Password"/>
        </div>
    <#if googleValidate?exists>
        <#if googleValidate == '1'>
            <div class="form-group">
                <input type="text" class="form-control" name="googleCode" id="googleCode" autocomplete="off"
                       placeholder="Secret"/>
            </div>
        </#if>
    </#if>
    <#if googleValidate?exists>
        <#if googleValidate == '0'>
            <div class="form-group">
                <div class="input-group">
                    <input type="text" maxlength="4" id="verCode" name="verCode" class="form-control"
                           placeholder="Image code">
                    <a class="input-group-addon" onclick="return false;" href="javascript:;">
                        <img onclick="this.src='/alibaba888/Liv2sky3soLa93vEr62/verCodeImg?t='+Math.random();" src="/alibaba888/Liv2sky3soLa93vEr62/verCodeImg">
                    </a>
                </div>
            </div>
        </#if>
    </#if>
        <input type="button" class="btn btn-primary block full-width m-b" id="submit1" value="Login"/>
    </form>
</div>
<script type="text/javascript" src="${STATIC_URL}/js/jquery.md5.js"></script>
<script type="text/javascript">

    window.onload = function () {
        if (window.top != null && window.top.document.URL != document.URL) {
            window.top.location.href = document.URL;
        }
    };



    $(function () {


        $("#verCode").keypress(function (e) {
            if (e.which == 13) {
                document.getElementById("submit1").click();
            }
        });

        $("#googleCode").keypress(function (e) {
            if (e.which == 13) {
                document.getElementById("submit1").click();
            }
        });

        $(document).on('click', '#submit1', function () {
            var username = $("#username").val();
            if (username.length == 0) {
                $.global.openErrorMsg('Account is not be empty!');
                return;
            }
            var password = $("#password").val();
            if (password.length == 0) {
                $.global.openErrorMsg('Password is not be empty!');
                return;
            }


            var googleCode = $("#googleCode").val();
            if (googleCode != null && googleCode.length == 0) {
                $.global.openErrorMsg('Secret is not be empty!');
                return;
            }
            $("#password").val($.md5(password));
            var formData = $('#login-form').serialize();
            var $loginBtn = $(this);
            $loginBtn.attr('disabled', true);
            $.ajax({
                url: '/alibaba888/Liv2sky3soLa93vEr62/login',
                type: 'post',
                dataType: 'json',
                data: formData,
                success: function (result) {
                    if (result.code === 200) {
                        window.location.href = '/alibaba888/Liv2sky3soLa93vEr62/toIndex';
                        return;
                    }
                    $.global.openErrorMsg(result.msg);
                },
                error: function (e) {
                    console.log("error : ", e);
                    $.global.openErrorMsg('Sorry, Login Error! You can try againt after some times!');
                },
                complete: function () {
                    $loginBtn.attr('disabled', false);
                    $("#password").val(password)
                }
            });
        });
    });
</script>
</body>
</html>
