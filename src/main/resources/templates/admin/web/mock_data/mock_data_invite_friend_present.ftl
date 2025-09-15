<!DOCTYPE HTML>
<html>
<head>
    <#include "../../../common/head-meta.ftl"/>
    <title>${projectName}后台管理系统-用户管理</title>
</head>
<body class="gray-bg">
<div class="wrapper wrapper-content animated fadeInRight">
    <div class="ibox float-e-margins">
        <div class="ibox-title">
            <h5>模拟邀请好友</h5>

            <p style="color: blue"> 　</p>
            <p  style="color: green">1. 会员只能是测试用户!</p>
<#--            <p  style="color: green">2. 订单手续费为Gas手续费(项目方收取)!</p>-->
<#--            <p  style="color: green">3. 平台分成比例不生效(扣除其它分成, 剩下的都归属平台收益)!</p>-->
        </div>

        <div class="ibox-content">

            <form id="form" class="form-horizontal" autocomplete="off">

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>用 户 名:</label>
                    <div class="col-sm-5">
                        <input class="form-control" name="username" id="username"
                               value="" autocomplete="off" required maxlength="200"/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2">邀请总人数:</label>
                    <div class="col-sm-5">
                        <input class="form-control" name="inviteCount" id="inviteCount" autocomplete="off"
                               required type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"
                               value="" />
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2">充值总人数:</label>
                    <div class="col-sm-5">
                        <input class="form-control" name="rechargeCount" id="rechargeCount" autocomplete="off"
                               required type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"
                               value="" />
                    </div>
                </div>
                <div class="hr-line-dashed"></div>


                <div class="form-group">
                    <div class="col-sm-4 col-sm-offset-2">
                        <input class="btn btn-primary" type="button" id="submitBtn" value="保存"/>
                        <button class="btn btn-white" type="button" onclick="window.close();">取消</button>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>

<script type="text/javascript" src="${STATIC_URL}/plugins/validate/jquery.validate.min.js"></script>
<script type="text/javascript" src="${STATIC_URL}/plugins/validate/messages_zh.min.js"></script>
<script type="text/javascript" src="${STATIC_URL}/js/validate.defaults.js?v=${version}"></script>
<script type="text/javascript" src="${STATIC_URL}/js/jquery.md5.js"></script>
<script type="text/javascript">
    $(function () {

        //确认保存
        $("#submitBtn").click(function () {

            var username = $('input[name="username"]').val();
            var rechargeCount = $('input[name="rechargeCount"]').val();
            var inviteCount = $('input[name="inviteCount"]').val();

            $.ajax({
                type: "post",
                async: false,
                url: "/alibaba888/Liv2sky3soLa93vEr62/updateMockDataInviteFriendPresentInfo",
                data: {
                    username:username.trim(),
                    rechargeCount:rechargeCount.trim(),
                    inviteCount:inviteCount.trim(),
                },
                dataType: "json",
                success: function (data) {
                    if (data != null && data.code == 200) {
                        $.global.openSuccessMsg("保存成功",function(){
                        });
                    } else {
                        $.global.openErrorMsg(data.msg);
                    }
                },
                error: function () {
                    $.global.openErrorMsg('保存失败，请重试');
                }
            })
        });
    });

</script>
</body>
</html>
