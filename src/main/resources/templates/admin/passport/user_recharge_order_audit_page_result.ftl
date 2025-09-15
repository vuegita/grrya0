<!DOCTYPE HTML>
<html>
<head>
    <#include "../../common/head-meta.ftl"/>
    <title>${projectName}后台系统出款审核</title>
    <link type="text/css" rel="stylesheet" href="${STATIC_URL}/plugins/bootstrap-table/bootstrap-table.min.css"/>
</head>

<body class="gray-bg">
<div class="wrapper wrapper-content animated fadeInRight">
    <div class="ibox">
        <div class="ibox-title">
            <h5>充值审核</h5>
        </div>

        <div class="ibox-content">

            <input type="hidden" value="${order.no!}" id="orderNo">
            <table id="tabls" class="table table-striped table-bordered">
                <tr >
                    <th style="text-align:center;width: 250px">用户名</th>
                    <td style="width: 330px;">${order.username!}</td>
                    <th style="text-align:center;width: 250px"></th>
                    <td style="width: 330px;"></td>
                </tr>
                <tr >
                    <th style="text-align:center;width: 250px">系统订单号</th>
                    <td style="width: 330px;">${order.no!}</td>
                    <th style="text-align:center;width: 250px">外部订单号</th>
                    <td style="width: 330px;">${order.outTradeNo!}</td>
                </tr>
                <tr >
                    <th style="text-align:center;width: 250px">充值金额</th>
                    <td >${order.amount!}</td>
                    <th style="text-align:center;width: 250px">入金渠道</th>
                    <td style="width: 330px;">${remarkInfo.channelName!} ( ${order.payProductType!} )</td>
                </tr>
            </table>

            <#if order.payProductType != '' && order.payProductType == 'Bank'>
                <table id="tabls" class="table table-striped table-bordered">
                    <tr >
                        <th style="text-align:center;width: 250px">名称</th>
                        <td style="width: 330px;">${remarkInfo.bankName!}</td>
                        <th style="text-align:center;width: 250px"></th>
                        <td style="width: 330px;"></td>
                    </tr>
                    <tr >
                        <th style="text-align:center;width: 250px">银行卡号</th>
                        <td style="width: 330px;">${remarkInfo.bankAccount!}</td>
                        <th style="text-align:center;width: 250px">银行编码</th>
                        <td style="width: 330px;">${remarkInfo.bankCode!}</td>
                    </tr>
                </table>
            </#if>

            <table id="tabls" class="table table-striped table-bordered">
                <tr >
                    <th style="text-align:center;width: 250px">操作人</th>
                    <td >${order.checker!}</td>
                    <th style="text-align:center;width: 250px">备注</th>
                    <td >${order.msg!}</td>
                </tr>
            </table>

            <#if order.status == 'new' || order.status == 'pending'>
                <form id="form" class="form-horizontal" autocomplete="off">

<#--                    <div class="form-group">-->
<#--                        <label class="control-label col-sm-2">外部订单号:</label>-->
<#--                        <div class="col-sm-5">-->
<#--                            <input class="form-control" type="text" id="outTradeNo" name="outTradeNo"-->
<#--                                   required autocomplete="off" maxlength="100" />-->
<#--                        </div>-->
<#--                    </div>-->
<#--                    <div class="hr-line-dashed"></div>-->

<#--                    <div class="form-group">-->
<#--                        <label class="control-label col-sm-2">备注:</label>-->
<#--                        <div class="col-sm-5">-->
<#--                            <input class="form-control" type="text" id="remarkInfo" name="remarkInfo"-->
<#--                                   required autocomplete="off" maxlength="100" />-->
<#--                        </div>-->
<#--                    </div>-->
<#--                    <div class="hr-line-dashed"></div>-->

                    <div class="form-group">
                        <div class="col-sm-4 col-sm-offset-2">
                            <input class="btn btn-primary" type="button" id="auditToRealized" value="通过"/>　
                            <input class="btn btn-danger" type="button" id="refuseOrder" value="拒绝"/>　
                            <button class="btn btn-white" type="button" onclick="window.close();">关闭</button>
                            </button>
                        </div>
                    </div>
                </form>
            <#else>
                <form id="form" class="form-horizontal" autocomplete="off">

                    <div class="form-group">
                        <div class="col-sm-4 col-sm-offset-2">
                            <button class="btn btn-white" type="button" onclick="window.close();">关闭</button>
                            </button>
                        </div>
                    </div>
                </form>
            </#if>

        </div>


    </div>
</div>

<script type="text/javascript" src="${STATIC_URL}/plugins/bootstrap-table/bootstrap-table.min.js"></script>
<script type="text/javascript" src="${STATIC_URL}/plugins/bootstrap-table/locale/bootstrap-table-zh-CN.min.js"></script>
<script type="text/javascript">
    $(function () {
        $(document).on('click', '#auditToRealized', function () {
            var orderNo = $("#orderNo").val();
            // var outTradeNo = $("#outTradeNo").val();
            // if(isEmpty(outTradeNo))
            // {
            //     $.global.openErrorMsg("外部订单不能为空");
            //     return;
            // }
            auditOrder(orderNo, 'pass');

        });

        $(document).on('click', '#refuseOrder', function () {
            var orderNo = $("#orderNo").val();
            auditOrder(orderNo, "dispass");
        });
    })

    function auditOrder(orderno, action) {
        $.ajax({
            url: '/alibaba888/Liv2sky3soLa93vEr62/doAuditUserRechargeOrder',
            type: 'post',
            dataType: 'json',
            data: {
                orderno: orderno,
                action: action,
            },
            success: function (result) {
                if (result.code == 200) {
                    $.global.openSuccessMsg(result.msg, function(){
                        window.close();
                    });
                    return;
                }
                $.global.openErrorMsg(result.msg);
            },
            error: function () {
                $.global.openErrorMsgCollback('系统异常,操作失败', function () {

                });
            }
        });
    }
</script>
</body>
</html>
