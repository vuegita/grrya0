<!DOCTYPE HTML>
<html>
<head>
    <#include "../common/head-meta.ftl"/>
    <title>${projectName}后台管理系统-订单管理</title>
    <link type="text/css" rel="stylesheet" href="${STATIC_URL}/plugins/bootstrap-table/bootstrap-table.min.css"/>
</head>

<body class="gray-bg">
<div class="wrapper wrapper-content animated fadeInRight">
    <div class="ibox">
        <div class="ibox-title">
            <h5>会员管理</h5>
        </div>

        <div class="ibox-content">
            <div class="btn-group hidden-xs" id="bootstrapTableToolBar" role="group">
                <p class="pull-left" style="margin-right: 10px;font-size: 15px">请选择查询组合:</p>
                <div class="pull-left" style="margin-right: 10px;">
                    <select class="form-control" name="selectGroup" onchange="changeSelect()" >
                        <option value="timeAndUsernameAndStatus" selected>订单时间 + 商户账号</option>
                        <option value="orderNo" >订单编号</option>
                        <option value="usernameAndOutOrderNo" >商户账号 + 商户订单编号</option>
                    </select>
                </div>

                <div class="pull-left" style="margin-right: 10px;">
                    <input type="text" name="time" id="time" class="form-control input-outline"  placeholder="请选择创建时间" style="width:200px;">
                </div>

                <div class="pull-left" style="margin-right: 10px;">
                    <input type="text" name="orderNo" class="form-control input-outline" placeholder="请输入订单编号">
                </div>

                <div class="pull-left" style="margin-right: 10px;">
                    <input type="text" name="username" class="form-control input-outline" placeholder="请输入商户账号">
                </div>

                <div class="pull-left" style="margin-right: 10px;">
                    <input type="text" name="outOrderNo" class="form-control input-outline" placeholder="请输入商户订单编号">
                </div>



                <div class="pull-left" style="margin-right: 10px;">
                    <select class="form-control" name="type" >
                        <option value="payin" selected>代收</option>
                        <option value="payout" >代付</option>
                    </select>
                </div>


                <div class="pull-left" style="margin-right: 10px;">
                    <select class="form-control" name="status" >
                            <option value="new" selected>new</option>
                    </select>
                </div>


                <button id="search-btn" type="button" class="btn btn-outline btn-default" title="查询">
                    <i class="glyphicon glyphicon-search" aria-hidden="true"></i> 查询
                </button>
                <button id="reset-btn" type="button" class="btn btn-outline btn-default" title="重置">
                    <i class="glyphicon glyphicon-repeat" aria-hidden="true"></i> 重置
                </button>
            </div>
            <table id="bootstrapTable">
                <thead>
                <tr>
                    <th data-field="no" data-width="8%" data-align="center"
                        class="title">订单编号
                    </th>
                    <th data-field="outTradeNo" data-width="8%" data-align="center"
                        class="title">商户订单编号
                    </th>
                    <th data-field="username" data-width="5%" data-align="center"
                        class="title">商户账号
                    </th>
                    <th data-field="type" data-width="5%" data-align="center" data-formatter="typeFormatter"
                        class="title">订单类型
                    </th>
                    <th data-field="status" data-width="5%" data-align="center"
<#--                        data-formatter="statusFormatter"-->
                        class="title">订单状态
                    </th>

                    <th data-field="amount" data-width="5%" data-align="center"
                        class="title">交易金额
                    </th>
                    <th data-field="feeRate" data-width="5%" data-align="center"
                        class="title">手续费率(%)
                    </th>
<#--                    <th data-field="remark" data-width="10%" data-align="center"-->
<#--                        class="title">备注-->
<#--                    </th>-->
                    <th data-field="createtime" data-width="10%" data-align="center"
                        class="title">创建时间
                    </th>

                    <th data-field="updatetime" data-width="10%" data-align="center"
                        class="title">变更时间
                    </th>

                    <th data-field="balance" data-width="5%" data-align="center" data-formatter="balanceFormatter"
                        class="title">账户余额
                    </th>

                    <th data-field="id" data-formatter="actionFormatter" data-width="20%" data-align="center">操作</th>
                </tr>
                </thead>
            </table>
        </div>
    </div>
</div>


<div class="modal fade" id="myModalChannel" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="myModalLabel">通道信息</h4>
            </div>
            <div class="modal-body" >
                <#include "channel_info.ftl"/>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal"><span
                            class="glyphicon glyphicon-remove" aria-hidden="true"></span>取消
                </button>

            </div>
        </div>
    </div>
</div>
<#include "../../delete_form.ftl">
<script type="text/javascript" src="${STATIC_URL}/plugins/bootstrap-table/bootstrap-table.min.js"></script>
<script type="text/javascript" src="${STATIC_URL}/plugins/bootstrap-table/locale/bootstrap-table-zh-CN.min.js"></script>
<script type="text/javascript">
    lay('#version').html('-v'+ laydate.v);
    //时间选择器
    laydate.render({
        elem: '#time',
        type: 'date',
        range: true,
        format:'yyyy-MM-dd'
        ,max:0
    });
    /**
     * 操作栏的格式化
     */


    function actionFormatter(value, row, index) {
        var result = "";
        result += '<a href="javascript:;" class="table-btn channelInfo" data-username="' + row.username + '" data-no="' + row.outTradeNo + '" title="查看通道">查看通道</a>';
        result += '<a href="javascript:;" class="table-btn auditSync" data-username="' + row.username + '" data-no="' + row.outTradeNo + '" title="通过">同步</a>';
        result += '<a href="javascript:;" class="table-btn auditPass" data-username="' + row.username + '" data-no="' + row.outTradeNo + '" title="通过">通过</a>';
        result += '<a href="javascript:;" class="table-btn auditDispass" data-username="' + row.username + '" data-no="' + row.outTradeNo + '" title="拒绝">拒绝</a>';
        return result;
    }

    function dateFormatter(value, row, index) {
        if (null == value) {
            return "";
        } else {
            return DateUtils.formatyyyyMMddHHmmss(value);
        }
    }

    function typeFormatter(value, row, index) {
        return $.global.constant.getOrderTypeMsg(value);
    }

    function statusFormatter(value, row, index) {
        return $.global.constant.getOrderStatusMsg(value);
    }

    function moneyFormatter(value, row, index) {
        return (value / 100).toFixed(2);
    }

    function balanceFormatter(value, row, index) {
        if (isEmpty(value) || value === 0 || value === "0"){
            return "-";
        }
        return value;
    }


    $(function () {
        var date1 = new Date();
        var time1 = date1.getFullYear()+"-"+(date1.getMonth()+1)+"-"+date1.getDate();
        var date2 = new Date(date1);
        date2.setDate(date1.getDate()-0);
        var time2 = date2.getFullYear()+"-"+(date2.getMonth()+1)+"-"+date2.getDate();
        var date = time2 + " - " + time1;
        $('input[name="time"]').val(date);

        changeSelect()

        var options = TableUtils.getBtDefaultOptions();
        options.url = '/alibaba888/order/audit/list';
        options.search = false;
        options.showRefresh = true;
        options.queryParams = function (params) {
            params.username = $('input[name="username"]').val();
            params.time = $('input[name="time"]').val();
            params.orderNo = $('input[name="orderNo"]').val();
            params.outOrderNo = $('input[name="outOrderNo"]').val();
            params.status = $('select[name="status"]').val();
            params.type = $('select[name="type"]').val();
            params.selectGroup = $('select[name="selectGroup"]').val();
            return params;
        };
        var bootstrapTable = $('#bootstrapTable').bootstrapTable(options);

        $(document).on('click', '.table-btn.channelInfo', function () {
            var outTradeNo = $(this).attr('data-no');
            var username = $(this).attr('data-username');
            channelRefresh(outTradeNo, username);

        });
        $(document).on('click', '.table-btn.auditSync', function () {
            var outTradeNo = $(this).attr('data-no');
            var username = $(this).attr('data-username');
            $('#myModalDel #id').val(outTradeNo);
            $('#myModalDel #type').val(username);
            $('#myModalDel #type').attr('action', 'sync');
            $('#myModalDel #tips').text('确定同步订单状态吗？');
            $('#myModalDel').modal();

        });
        $(document).on('click', '.table-btn.auditPass', function () {
            var outTradeNo = $(this).attr('data-no');
            var username = $(this).attr('data-username');
            $('#myModalDel #id').val(outTradeNo);
            $('#myModalDel #type').val(username);
            $('#myModalDel #type').attr('action', 'pass');
            $('#myModalDel #tips').text('确定审核通过吗？');
            $('#myModalDel').modal();

        });
        $(document).on('click', '.table-btn.auditDispass', function () {
            var outTradeNo = $(this).attr('data-no');
            var username = $(this).attr('data-username');
            $('#myModalDel #id').val(outTradeNo);
            $('#myModalDel #type').val(username);
            $('#myModalDel #type').attr('action', 'dispass');
            $('#myModalDel #tips').text('确定审核拒绝吗？');
            $('#myModalDel').modal();
        });

        $('#delete_submit').click(function () {
            var outTradeNo = $('#myModalDel #id').val();
            var username = $('#myModalDel #type').val();
            var action = $('#myModalDel #type').attr('action');
            auditOrder(outTradeNo, username, action)
        });

        $('#search-btn').click(function () {
            var selectVal = $('select[name="selectGroup"]').val();
            if ('timeAndUsernameAndStatus' === selectVal){
                if (isEmpty($('input[name="time"]').val())){
                    $.global.openErrorMsg('请选择时间范围');
                    return;
                }
            }else if('orderNo' === selectVal){
                if (isEmpty($('input[name="orderNo"]').val())){
                    $.global.openErrorMsg('请填写订单编号');
                    return;
                }
            }else if('usernameAndOutOrderNo' === selectVal){
                if (isEmpty($('input[name="username"]').val())){
                    $.global.openErrorMsg('请填写商户账号');
                    return;
                }
            }
            refresh();
        });

        $('#reset-btn').click(function () {
            $('input[name="username"]').val('');
            $('input[name="time"]').val('');
            $('input[name="orderNo"]').val('');
            $('input[name="outOrderNo"]').val('');
            // $('select[name="status"]').val('');
            // refresh();
        });

    });

    function refresh() {
        $('#bootstrapTable').bootstrapTable('refresh');
    }

    function channelRefresh(outTradeNo, username) {
        $.ajax({
            url: '/alibaba888/order/channel/info',
            type: 'post',
            dataType: 'json',
            data: {outTradeNo: outTradeNo, username: username, type: $('select[name="type"]').val()},
            success: function (result) {
                if (result.code === 0) {
                    setChannelInfo(result.data);
                    $('#myModalChannel').modal();
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

    function auditOrder(outTradeNo, username, action) {
        $.ajax({
            url: '/alibaba888/order/audit',
            type: 'post',
            dataType: 'json',
            data: {outTradeNo: outTradeNo,username:username,action:action,type:$('select[name="type"]').val()},
            success: function (result) {
                if (result.code === 0) {
                    $.global.openSuccessMsg(result.msg, function(){
                        refresh();
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

    function setChannelInfo(data){
        $("#channelName").text(data.channelInfo.name);
        $("#channelGroup").text(data.channelInfo.groupName);
        $("#channelStatus").text(data.channelInfo.status);
        $("#channelProduct").text(data.channelInfo.productType);
        $("#todayMoney").text(data.todayMoney);
    }


    function changeSelect(){
        var selectVal = $('select[name="selectGroup"]').val();
        if ('timeAndUsernameAndStatus' === selectVal){
            $('input[name="time"]').removeClass("hidden");
            $('input[name="username"]').removeClass("hidden");
            $('input[name="orderNo"]').addClass("hidden");
            $('input[name="outOrderNo"]').addClass("hidden");
        }else if('orderNo' === selectVal){
            $('input[name="orderNo"]').removeClass("hidden");
            $('input[name="time"]').addClass("hidden");
            $('input[name="username"]').addClass("hidden");
            $('input[name="outOrderNo"]').addClass("hidden");
        }else if('usernameAndOutOrderNo' === selectVal){
            $('input[name="username"]').removeClass("hidden");
            $('input[name="outOrderNo"]').removeClass("hidden");
            $('input[name="time"]').addClass("hidden");
            $('input[name="orderNo"]').addClass("hidden");
        }
    }

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
