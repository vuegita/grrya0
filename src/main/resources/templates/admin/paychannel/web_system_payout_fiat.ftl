<!DOCTYPE HTML>
<html>
<head>
    <#include "../../common/head-meta.ftl"/>
    <title>${projectName}后台管理系统-订单管理</title>
    <link type="text/css" rel="stylesheet" href="${STATIC_URL}/plugins/bootstrap-table/bootstrap-table.min.css"/>
</head>
<body class="gray-bg">
<div class="wrapper wrapper-content animated fadeInRight">
    <div class="ibox float-e-margins">
        <div class="ibox-title">
            <h5>系统出款</h5>
<#--            <p style="color: blue"> 　</p>-->
<#--            <p  style="color: green">1. 代付测试卡 【TEST123456789666】, 如果用这个提交审核之后直接Waiting, 不提上游!</p>-->
        </div>

        <div class="ibox-content">
            <form id="form" class="form-horizontal" autocomplete="off">

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>用户名:</label>
                    <div class="col-sm-3">
                        <input class="form-control" id="username" name="username" autocomplete="off" required maxlength="50" value="${username!}"/>
                    </div>
                    <button id="search-btn" type="button" class="btn btn-outline btn-default" title="查询">
                        <i class="glyphicon glyphicon-search" aria-hidden="true"></i> 搜索
                    </button>
                </div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger"></span>余额:</label>
                    <div class="col-sm-3">
                        <input name="balance" id="balance" class="form-control" autocomplete="off" required maxlength="50" disabled/>
                    </div>
                </div>
                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger"></span>冻结金额:</label>
                    <div class="col-sm-3">
                        <input name="freeze" id="freeze" class="form-control" autocomplete="off" required maxlength="50" disabled/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>受益人名称:</label>
                    <div class="col-sm-3">
                        <input class="form-control" id="beneficiaryName" name="beneficiaryName" autocomplete="off" required maxlength="50" value="${beneficiaryName!}"/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>受益人邮箱:</label>
                    <div class="col-sm-3">
                        <input class="form-control" id="beneficiaryEmail" name="beneficiaryEmail" autocomplete="off" required maxlength="50" value="${beneficiaryEmail!}"/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>受益人手机:</label>
                    <div class="col-sm-3">
                        <input class="form-control" id="beneficiaryPhone" name="beneficiaryPhone" autocomplete="off" required maxlength="50" value="${beneficiaryPhone!}"/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>银行卡号:</label>
                    <div class="col-sm-3">
                        <input class="form-control" id="bankNumber" name="bankNumber" autocomplete="off" required maxlength="50" value="${bankNumber!}"/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>ifsc:</label>
                    <div class="col-sm-3">
                        <input class="form-control" id="bankIfsc" name="bankIfsc" autocomplete="off" required maxlength="50" value="${bankIfsc!}"/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>出款金额:</label>
                    <div class="col-sm-3">
                        <input  class="form-control" name="money" id="money" autocomplete="off" required maxlength="50"
                                onkeyup="this.value=this.value.replace(/[^\d]/g,'');" value=""/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>验证码:</label>
                    <div class="col-sm-3">
                        <input class="form-control" id="googlecode" name="googlecode" autocomplete="off" required maxlength="50" value=""/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <div class="col-sm-4 col-sm-offset-2">
                        <input class="btn btn-primary" type="submit" id="submit" value="提交"/>
                    </div>
                </div>

            </form>
        </div>
    </div>
</div>


<div class="modal fade" id="myModalSubmit" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <input type="hidden" name="adminId" class="form-control" id="adminId" placeholder="adminId">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <h4 class="modal-title" id="myModalLabel">提示</h4>
            </div>
            <div class="modal-body">
                <p>确定要提交吗？</p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span>取消</button>
                <button type="button" id="btn_submit_payout" class="btn btn-primary" data-dismiss="modal">
                    <span class="glyphicon glyphicon-floppy-disk" aria-hidden="true"></span>确认
                </button>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript" src="${STATIC_URL}/plugins/validate/jquery.validate.min.js"></script>
<script type="text/javascript" src="${STATIC_URL}/plugins/validate/messages_zh.min.js"></script>
<script type="text/javascript" src="${STATIC_URL}/js/validate.defaults.js?v=${version}"></script>
<script type="text/javascript">
    $(function () {

        $('#submit').click(function(){
            $('#myModalSubmit').modal();
            return false;
        });

        $('#search-btn').click(function(){
            $.ajax({
                url: '/alibaba888/Liv2sky3soLa93vEr62/findUserInfo',
                type: 'post',
                dataType: 'json',
                data:{
                    username: function () {
                        return $('input[name="username"]').val();
                    },
                    channelType : "payout"
                },
                success: function(result){
                    if(result && result.code == 200)
                    {
                        $('input[name="balance"]').val(result.data.balance);
                        $('input[name="freeze"]').val(result.data.freeze);
                    }
                },
                error: function(){
                    $('input[name="balance"]').val('0')
                    $('input[name="freeze"]').val('0');
                }
            });
        });

        function isEmpty(obj) {
            if (typeof obj === 'undefined' || obj == null || obj === '') {
                return true;
            } else {
                return false;
            }
        }

        // doSignParams
        $('#btn_submit_payout').click(function(){
            var username = $("#username").val();

            var beneficiaryName = $("#beneficiaryName").val();
            var beneficiaryEmail = $("#beneficiaryEmail").val();
            var beneficiaryPhone = $("#beneficiaryPhone").val();

            var bankNumber = $("#bankNumber").val();
            var bankIfsc = $("#bankIfsc").val();

            var money = $("#money").val();

            var googlecode = $("#googlecode").val();

            if(isEmpty(username) ||
                isEmpty(beneficiaryName) || isEmpty(beneficiaryEmail) || isEmpty(beneficiaryPhone) ||
                isEmpty(bankNumber) || isEmpty(bankIfsc) || isEmpty(money) || isEmpty(googlecode)
            )
            {
                $.global.openErrorMsg('* 必填不能为空');
                return;
            }

            $.ajax({
                type: "Post",
                url: "/alibaba888/Liv2sky3soLa93vEr62/submit_system_payout",
                data: {
                    username: username,

                    beneficiaryName: beneficiaryName,
                    beneficiaryEmail: beneficiaryEmail,
                    beneficiaryPhone: beneficiaryPhone,

                    bankNumber: bankNumber,
                    bankIfsc: bankIfsc,
                    amount: money,
                    googlecode:googlecode
                },
                dataType: "json",
                success: function (data) {
                    console.log(data);
                    if (data != null && data.code === 200) {
                        $.global.openSuccessMsg("sign success");
                    } else {
                        $.global.openErrorMsg(data.msg);
                    }
                },

                error: function (e) {
                    console.log("error = ", e);
                    $.global.openErrorMsg('提交失败，请稍后重试！');
                }
            });

        });


    });





</script>
</body>
</html>
