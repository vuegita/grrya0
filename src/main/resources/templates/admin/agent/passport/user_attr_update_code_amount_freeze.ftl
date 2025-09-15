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
            <h5></h5>

            <p style="color: blue"> 　</p>
            <p  style="color: green">1. 设置完推广员不可变更!</p>
        </div>

        <div class="ibox-content">
            <form id="form" class="form-horizontal" autocomplete="off">
                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>用户名:</label>
                    <div class="col-sm-3">
                        <input class="form-control" name="username" id="username" value="${username}" readonly autocomplete="off" maxlength="100"  style="width:300px;"/>
                    </div>
                </div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>会员子类型:</label>
                    <div class="col-sm-5">
                        <div class="radio i-checks" >

                            <label>
                                <input type="radio"  name="subType" id="subType" value="simple" disabled
                                        <#if userInfo?exists && userInfo.subType == 'simple'> checked </#if>
                                >
                                <i></i>普通会员
                            </label>

                            <label><input type="radio"  name="subType" id="subType" value="promotion" data-usertype="promotion"
                                        <#if userInfo?exists && userInfo.subType == 'promotion'> checked </#if>>
                                <i></i>推广会员
                            </label>

                        </div>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>



                <div class="form-group">
                    <label class="control-label col-sm-2">可提打码量:</label>
                    <div class="col-sm-5">
                        <input class="form-control" name="codeAmount" id="codeAmount" value="${moneyInfo.codeAmount!}" autocomplete="off"
                               required maxlength="10" type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');" />
                    </div>
                </div>
                <div class="form-group">
                    <label class="control-label col-sm-2">不可提打码量:</label>
                    <div class="col-sm-5">
                        <input class="form-control" name="limitCode" id="limitCode" value="${moneyInfo.limitCode!}" autocomplete="off"
                               required maxlength="10" type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');" />
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2">冻结金额:</label>
                    <div class="col-sm-5">
                        <input class="form-control" name="freezeAmount" id="freezeAmount" value="${moneyInfo.freeze!}" autocomplete="off"
                               required maxlength="10" type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');" />
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
            var username = $('#username').val();
            var codeAmount = $('#codeAmount').val();
            var limitCode = $('#limitCode').val();

            var freezeAmount = $('#freezeAmount').val();

            var subType = $('input[name="subType"]:checked').val();

            if(isEmpty(username))
            {
                $.global.openErrorMsg('* 号必填参数不能为空');
                return;
            }

            if(isEmpty(freezeAmount) && isEmpty(codeAmount))
            {
                $.global.openErrorMsg('* 号必填参数不能为空');
                return;
            }


            $.ajax({
                url: '/alibaba888/agent/passport/updateUserAttrCodeAmountAndFreezeAmount',
                type: 'post',
                dataType: 'json',
                data: {
                    username:username,
                    freezeAmount:freezeAmount,
                    codeAmount:codeAmount,
                    limitCode:limitCode,
                    subType:subType,
                },
                success: function (result) {
                    if (result.code === 200) {
                        $.global.openSuccessMsg("更新成功, 请手动刷新页面!", function(){
                            window.close();
                        });
                        return;
                    }
                    $.global.openErrorMsg(result.msg);
                },
                error: function () {
                    $.global.openErrorMsgCollback('系统异常,操作失败!', function () {

                    });
                }
            });
        });
    });

</script>
</body>
</html>
