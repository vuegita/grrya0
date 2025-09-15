<!DOCTYPE HTML>
<html>
<head>
    <#include "../../common/head-meta.ftl"/>
    <title>${projectName}后台管理系统-登录限制</title>
    <link type="text/css" rel="stylesheet" href="${STATIC_URL}/css/plugins/webuploader/webuploader.css"/>
    <link type="text/css" rel="stylesheet" href="${STATIC_URL}/css/uploadImg.css"/>
    <script type="text/javascript" src="${STATIC_URL}/js/plugins/webuploader/webuploader.js"></script>

</head>
<style>
    .rm-img{
        width:30%;
        height: 30%;
    }
</style>
<body class="gray-bg">
<div class="wrapper wrapper-content animated fadeInRight">
    <div class="ibox float-e-margins">
        <div class="ibox-title">
            <h5>解除限制</h5>
        </div>

        <div class="ibox-content">
            <form id="form" class="form-horizontal" autocomplete="off">


                <div class="form-group">
                    <label style="margin-left: 230px"><span class="text-danger">* 登录错误多次,IP或账号将被限制登录</span></label>
                    <br><label style="margin-left: 230px"><span class="text-danger">* 此处用于解除限制</span></label>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2">账号:</label>
                    <div class="col-sm-6">
                        <input class="form-control" type="text" id="admin" name="admin"
                              required autocomplete="off" maxlength="50" />
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2">IP:</label>
                    <div class="col-sm-6">
                        <input class="form-control" type="text" id="ip" name="ip"
                               required autocomplete="off" maxlength="50" />
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <div class="col-sm-4 col-sm-offset-2">
                        <input class="btn btn-primary" type="submit" id="submit" value="解除限制"/>
                        </button>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>

<script type="text/javascript" src="${STATIC_URL}/plugins/validate/jquery.validate.min.js"></script>
<script type="text/javascript" src="${STATIC_URL}/plugins/validate/messages_zh.min.js"></script>
<script type="text/javascript" src="${STATIC_URL}/js/validate.defaults.js?v=${version}"></script>
<script type="text/javascript" src="${STATIC_URL}/js/jquery.md5.js?v=${version}"></script>
<script type="text/javascript" src="${STATIC_URL}/lib/jquery.ajaxfileupload.js"></script>
<script type="text/javascript">
    $(function () {
        $(document).on('click', '#submit', function(){
            var ip = $("#ip").val();
            var admin = $("#admin").val();
            var $submitBtn = $(this);
            $submitBtn.attr('disabled', true);
            $.ajax({
                url:'/alibaba888/Liv2sky3soLa93vEr62/removeAdminLoginLimit',
                type: 'post',
                dataType: 'json',
                data: {
                    admin:admin,
                    ip:ip,
                },
                success: function(result){
                    if(result.code == 200){
                        $.global.openSuccessMsg('解除限制成功');
                        return;
                    }
                    $.global.openErrorMsg(result.msg);
                },
                error: function(e){
                    console.log("error:",e);
                },
                complete: function(e){
                    console.log(e);
                    $submitBtn.attr('disabled', false);
                }
            });

            return false;
        });

    });


    function isEmpty(obj) {
        if (typeof obj === 'undefined' || obj == null || obj === '') {
            return true;
        } else {
            return false;
        }
    }
</script>
</body>
</html>
