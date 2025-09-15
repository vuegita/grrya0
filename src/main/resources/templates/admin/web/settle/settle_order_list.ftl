<!DOCTYPE HTML>
<html>
<head>
    <#include "../../../common/head-meta.ftl"/>
    <title>${projectName}商户后台管理系统-订单管理</title>
    <link type="text/css" rel="stylesheet" href="${STATIC_URL}/plugins/bootstrap-table/bootstrap-table.min.css"/>
</head>

<body class="gray-bg">
<div class="wrapper wrapper-content animated fadeInRight">
    <div class="ibox">
        <div class="ibox-title">
            <h5>结算订单</h5>
            <p style="color: blue"> 　</p>
            <p  style="color: red">1. 未划转的用户出款订单不可结算,已划转的用户出款订单可结算!</p>
        </div>

        <div class="ibox-content">
            <div class="btn-group hidden-xs" id="bootstrapTableToolBar" role="group">


<#--                <div class="pull-left" style="margin-right: 10px;">-->
<#--                    <button id="create-btn" type="button" class="btn btn-outline btn-default" title="补单">-->
<#--                        <i class="glyphicon glyphicon-plus" aria-hidden="true"></i> 补单-->
<#--                    </button>-->
<#--                </div>-->

                <div  style="width: 100%;height: 5rem;">
                    <div class="pull-left" style="margin-right: 10px;">
                        <input type="text" name="nototalBetAmount" id="nototalBetAmount" class="form-control input-outline"  placeholder="不可结算总额: " readonly style="width:200px;">
                    </div>

                    <div class="pull-left" style="margin-right: 10px;">
                        <input type="text" name="yestotalBetAmount" id="yestotalBetAmount" class="form-control input-outline"  placeholder="可结算总额: " readonly style="width:200px;">
                    </div>

                <div class="pull-left" style="margin-right: 10px;">
                    <input type="text" name="wtotalBetAmount" id="wtotalBetAmount" class="form-control input-outline"  placeholder="已结算总额: " readonly style="width:200px;">
                </div>

                <div class="pull-left" style="margin-right: 10px;">
                    <input type="text" name="rtotalBetAmount" id="rtotalBetAmount" class="form-control input-outline"  placeholder="未结算总额: " readonly style="width:200px;">
                </div>

                    <div class="pull-left" style="margin-right: 10px;">
                        <input type="text" name="wtihdrowtotalBetAmount" id="wtihdrowtotalBetAmount" class="form-control input-outline"  placeholder="总打款金额: " readonly style="width:200px;">
                    </div>

                <div class="pull-left" style="margin-left: 10px;">
                    <button id="refuseAudit-btn" type="button" class="btn btn-outline btn-default" title="选中项统计">
                        统计金额
                    </button>
                </div>

                    <div class="pull-left" style="margin-left: 10px;">
                        <button id="refuseAudit2-btn" type="button" class="btn btn-primary" title="结算订单">
                            结算订单
                        </button>
                    </div>

            </div>


                <div  style="width: 100%;height: 5rem;">
                <div class="pull-left" style="margin-right: 10px;">
                    <input type="text" name="time" id="time" class="form-control input-outline"
                           placeholder="请选择时间" style="width:200px;">
                </div>

                <div class="pull-left" style="margin-right: 10px;">
                    <input type="text" name="username" id="username" class="form-control input-outline"  placeholder="请输入用户名" style="width:200px;">
                </div>

                <div class="pull-left" style="margin-right: 10px;">
                    <input type="text" name="agentname" id="agentname" class="form-control input-outline"
                           <#if agentname != ''> value="${agentname}" </#if>
                           placeholder="请输入代理用户名" style="width:150px;">
                </div>

                <div class="pull-left" style="margin-right: 10px;">
                    <input type="text" name="staffname" id="staffname" class="form-control input-outline"
                            <#if staffname != ''> value="${staffname}" </#if>
                           placeholder="请输入直属员工名" style="width:150px;">
                </div>

                <div class="pull-left" style="margin-right: 10px;">
                    <input type="text" name="systemOrderno" id="systemOrderno" class="form-control input-outline"  placeholder="请输入系统订单号" style="width:150px;">
                </div>

                <div class="pull-left" style="margin-right: 10px;">
                    <input type="text" name="transferNo" id="transferNo" class="form-control input-outline"
                            <#if no != ''> value="${no}" </#if>
                           placeholder="请输入划转订单号" style="width:150px;">
                </div>

                <div class="pull-left" style="margin-right: 10px;">
                    <input type="text" name="beneficiaryAccount" id="beneficiaryAccount" class="form-control input-outline"  placeholder="请输入账户" style="width:150px;">
                </div>

                    <div class="pull-left" style="margin-right: 10px;">
                        <input type="text" name="reportid" id="reportid" class="form-control input-outline"
                                <#if reportid != ''> value="${reportid}" </#if>
                               placeholder="请输入结算记录id" style="width:150px;">
                    </div>


                </div>

                <div class="pull-left" style="margin-right: 10px;">
                    <select class="form-control" name="currencyType" >
                        <option value="" selected>---所有币种---</option>
                        <#list cryptoCurrencyArr as item>
                            <option value="${item.getKey()}" <#if currency != '' && currency == item.getKey()> selected</#if>> ${item.getKey()}</option>
                        </#list>
                    </select>
                </div>

                <div class="pull-left" style="margin-right: 10px;">
                    <select class="form-control" name="txStatus" >
                        <option value="" >---订单状态---</option>
                        <option value="new" >new</option>
                        <option value="new" >pending</option>
                        <option value="waiting" >不可结算</option>
                        <option value="realized" >可结算</option>
                        <option value="failed" >failed</option>
                    </select>
                </div>

                <div class="pull-left" style="margin-right: 10px;">
                    <select class="form-control" name="settleStatus" >
                        <option value="" >---结算状态---</option>
                        <option value="waiting" >未结算</option>
                        <option value="realized" >已结算</option>
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
<#--                    <th data-field="no" data-width="5%" data-align="center"-->
<#--                        class="title">系统订单号-->
<#--                    </th>-->
<#--                    <th data-field="outTradeNo" data-width="5%" data-align="center"-->
<#--                        class="title">外部订单号-->
<#--                    </th>-->

<#--                    <th data-field="payProductType" data-width="5%" data-align="center"-->
<#--                        class="title">支付渠道-->
<#--                    </th>-->
                    <th  data-width="2%" data-align="center"  data-checkbox="true"
                         class="title">
                    </th>

                    <th data-field="username" data-width="10%" data-align="left"
                        data-formatter="userInfoFormatter"
                        class="title">用户信息 & 订单明细
                    </th>

<#--                    <th data-field="username" data-width="10%" data-align="left"-->
<#--                        data-formatter="orderDetailInfoFormatter"-->
<#--                        class="title">订单明细-->
<#--                    </th>-->

                    <th data-field="networkType" data-width="5%" data-align="center"
                        class="title">所属网络
                    </th>

                    <th data-field="currency" data-width="5%" data-align="center"
                        class="title">所属币种
                    </th>

                    <th data-field="username" data-width="5%" data-align="center"
                        data-formatter="actualMoneyFormatter"
                        class="title">打款金额
                    </th>

                    <th data-field="transferAmount" data-width="5%" data-align="center"
                        class="title">划转金额
                    </th>

                    <th data-field="transferNo" data-width="5%" data-align="center"    data-formatter="transferNoFormatter"
                        class="title">划转订单号
                    </th>

<#--                    <th data-field="" data-width="10%" data-align="left"-->
<#--                        data-formatter="beneficiaryFormatter"-->
<#--                        class="title">受益人信息-->
<#--                    </th>-->

                    <th data-field="status" data-width="5%" data-align="center"  data-formatter="statusddFormatter"
                        class="title">订单状态
                    </th>

                    <th data-field="settleStatus" data-width="5%" data-align="center"  data-formatter="settleStatusFormatter"
                        class="title">结算状态
                    </th>

                    <th data-field="createtime" data-width="5%" data-align="center"
                        class="title">时间
                    </th>

                    <th data-field="username" data-width="10%" data-align="left"
                        data-formatter="remarkInfoFormatter"
                        class="title">备注信息
                    </th>

<#--                    <th data-field="" data-width="5%" data-align="center" data-formatter="columnFormatterForOrderRemarkMsg"-->
<#--                        class="title">错误原因-->
<#--                    </th>-->


<#--                    <th data-field="no" data-width="8%" data-align="center" data-formatter="actionFormatter"-->
<#--                        class="title">操作-->
<#--                    </th>-->
                    <th data-field="id" data-formatter="actionFormatter" data-width="5%" data-align="center">操作</th>
                </tr>
                </thead>
            </table>
        </div>
    </div>
</div>

<#include "../../../common/delete_form.ftl">
<script type="text/javascript" src="${STATIC_URL}/plugins/bootstrap-table/bootstrap-table.min.js"></script>
<script type="text/javascript" src="${STATIC_URL}/plugins/bootstrap-table/locale/bootstrap-table-zh-CN.min.js"></script>
<script type="text/javascript">
    lay('#version').html('-v'+ laydate.v);
    //时间选择器
    layTime = laydate.render({
        elem: '#time',
        // type: 'date',
        range: true,
        format:'yyyy-MM-dd',
        max:0,
        change:function(value, date, endDate){

            var s = new Date(date.year+'-'+date.month+'-'+date.date);
            var e = new Date(endDate.year+'-'+endDate.month+'-'+endDate.date);
            //计算两个时间间隔天数
            var day=(e-s)/(1000*60*60*24);
            //console.log(date.year+'-'+date.month+'-'+date.date);
            //console.log(endDate.year+'-'+endDate.month+'-'+endDate.date);
            //console.log(day);
            if(day>120){
                layTime.hint('最多选择120天');
            }
        }
    });

    //计算天数差
    function differenceData(predata,lastdata) {
        var pdate = new Date(predata);
        var ldata = new Date(lastdata);
        var days = ldata.getTime() - pdate.getTime();
        var day = parseInt(days / (1000 * 60 * 60 * 24));
        return day;
    }

    function resetTime()
    {
        var date1 = new Date();
        var time1 = date1.getFullYear()+"-"+(date1.getMonth()+1)+"-"+date1.getDate();
        var date2 = new Date(date1);
        date2.setDate(date1.getDate()-120);
        var time2 = date2.getFullYear()+"-"+(date2.getMonth()+1)+"-"+date2.getDate();
        var date = time2 + " - " + time1;
        $('input[name="time"]').val(date);
    }


    /**
     * 操作栏的格式化
     */
    function actionFormatter(value, row, index) {
        var result = "";

        // if(row.status == 'waiting' || row.status == 'pending')
        // {
        //     result += '<a href="javascript:;" class="table-btn passOrder" data-no="' + row.no + '" title="通过可结算">通过可结算</a>';
        //     result += '<a href="javascript:;" class="table-btn refuseOrder" data-no="' + row.no + '" title="拒绝">拒绝</a>';
        // }

        result += '<a href="javascript:;" class="table-btn detailUserInfo" data-username="' + row.username + '" title="详情">详情</a>';
        return result;
    }
    function transferNoFormatter(value, row, index) {
        var result = "";

        result += '<a href="javascript:;" class="table-btn toAgentSettleOrderPage" sysOrderno="' + row.transferNo + '"  username="' + row.username + '" title="查看">'+row.transferNo+'</a>';

        return result;

    }


    function statusddFormatter(value, row, index) {
        var result = "";

        if(row.status == 'waiting')
        {
           result = "<Strong style='color: red'>不可结算</Strong>";
        }else if(row.status == 'realized'){
           result = "<Strong style='color: green'>可结算</Strong>";
        }else{
            result=""+row.status;
        }

        return result;

    }

    function settleStatusFormatter(value, row, index) {
        var result = "";

        if(row.settleStatus == 'waiting')
        {
            result = "<Strong style='color: red'>未结算</Strong>";
        }else if(row.settleStatus == 'realized'){
            result = "<Strong style='color: green'>已结算</Strong>";
        }else{
            result=""+row.settleStatus;
        }

        return result;

    }

    $(document).on('click', '.table-btn.toAgentSettleOrderPage', function () {
        var sysOrderno = $(this).attr('sysOrderno');
        var username = $(this).attr('username');

        var url = '/alibaba888/Liv2sky3soLa93vEr62/root_coin_crypto_approve_transfer?sysOrderno=' + sysOrderno +"&username="+ username;
        // window.location.href = url;
        openNewWindow(url);
    });


    $(document).on('click', '.table-btn.detailUserInfo', function () {
        var username = $(this).attr('data-username');
        var url = '/alibaba888/Liv2sky3soLa93vEr62/passport/user/history_detail/page?username=' + username;
        // window.location.href = url;
        openNewWindow(url);
    });

    $(document).on('click', '.table-btn.reWithdrawOrder', function () {
        var no = $(this).attr('data-no');
        $('#myModalDel #id').val(no);
        $('#myModalDel #type').attr('action', 'reWithdrawOrder');
        $('#myModalDel #tips').text('确定重新发起提现吗？');
        $('#myModalDel').modal();
    });




    $(document).on('click', '.table-btn.reWithdrawOrder2', function () {
        var no = $(this).attr('data-no');
        $('#myModalDel #id').val(no);
        $('#myModalDel #type').attr('action', 'reWithdrawOrder2');
        $('#myModalDel #tips').text('确定重新发起提现吗？');
        $('#myModalDel').modal();
    });

    function reWithdrawOrder(no) {

        $.ajax({
            type: "post",
            async: false,
            url: "/alibaba888/Liv2sky3soLa93vEr62/reWithdrawOrder",
            data: {
                no:no.trim(),
            },
            dataType: "json",
            success: function (data) {
                if (data != null && data.code == 200) {
                    $.global.openSuccessMsg("操作成功");
                } else {
                    $.global.openErrorMsg("操作失败，请重试");
                }
            },
            error: function () {
                $.global.openErrorMsg('操作失败，请重试');
            }
        })

    }

    function reWithdrawOrder2(no) {

        $.ajax({
            type: "post",
            async: false,
            url: "/alibaba888/Liv2sky3soLa93vEr62/backgroundRewithdraw",
            data: {
                no:no.trim(),
            },
            dataType: "json",
            success: function (data) {
                if (data != null && data.code == 200) {
                    $.global.openSuccessMsg("操作成功");
                } else {
                    $.global.openErrorMsg("操作失败，请重试");
                }
            },
            error: function () {
                $.global.openErrorMsg('操作失败，请重试');
            }
        })

    }

    function userInfoFormatter(value, row, index)
    {
        var rs = '';
        rs += "用 户 名: " + row.username + "<br>";
        rs += "所属代理: " + row.agentname + "<br>";
        rs += "所属员工: " + row.staffname + "<br>";
        rs += "受益账户: " + row.beneficiaryAccount + "<br>";

        rs += "订单金额 : " + row.amount + "<br>";
        rs += "手续费用 : " + row.feemoney + "<br>";

        rs += "创建时间 : " + row.updatetime + "<br>";
        rs += "系统订单号 : " + row.no + "<br>";
        rs += "外部订单号 : " + row.outTradeNo + "<br>";

        // var jsonObj = JSON.parse(row.remark);
        // if(jsonObj)
        // {
        //     var type = jsonObj.type;
        //     rs += "通道类型: " + jsonObj.type + "<br>";
        //
        //     if(type == "Coin")
        //     {
        //         rs += "所属网络: " + jsonObj.ifsc + " | " + jsonObj.currencyType + "<br>";
        //         rs += "受益地址: " + jsonObj.account + "<br>";
        //         rs += "代币地址: " + jsonObj.currencyCtrAddress + "<br>";
        //     }
        //     else
        //     {
        //         rs += "Name: " + jsonObj.beneficiaryName + "<br>";
        //         rs += "IFSC: " + jsonObj.ifsc + "<br>";
        //
        //         if(jsonObj.name!=undefined && jsonObj.name!=null && jsonObj.name!=""){
        //             rs += "bankName: " + jsonObj.name + "<br>";
        //         }
        //
        //         rs += "Account: " + jsonObj.account + "<br>";
        //         rs += "Email: " + jsonObj.beneficiaryEmail + "<br>";
        //         rs += "Phone: " + jsonObj.beneficiaryPhone + "<br>";
        //         rs += "Channel: " + jsonObj.channelName + "<br>";
        //         if(jsonObj.idcard==undefined || jsonObj.idcard==null ){
        //             rs += "ID: " + " " + "<br>";
        //         }else{
        //             rs += "ID: " + jsonObj.idcard + "<br>";
        //         }
        //     }
        //
        // }

        return rs;
    }

    function orderDetailInfoFormatter(value, row, index)
    {
        var amount = row.amount;
        var feemoney = row.feemoney;
        var actualMoney = (Number(amount) - Number(feemoney) ).toFixed(6);

        var rs = '';
        rs += "订单金额 : " + row.amount + "<br>";
        rs += "手续费用 : " + row.feemoney + "<br>";
        rs += "打款金额 : " + actualMoney + "<br>";
        rs += "所属币种 : " + row.currency + "<br>";
        rs += "系统订单号 : " + row.no + "<br>";
        rs += "外部订单号 : " + row.outTradeNo + "<br>";

        // var networkType = '';
        // var jsonObj = JSON.parse(row.remark);
        // if(jsonObj && jsonObj.type == "Coin")
        // {
        //     var type = jsonObj.type;
        //     if(type == "Coin")
        //     {
        //         networkType = jsonObj.ifsc;
        //     }
        // }

        // rs += "外部订单号 : " + buildScanBrowserItem(networkType, row.outTradeNo, "transaction_scan_url");

        return rs;
    }

    function actualMoneyFormatter(value, row, index)
    {
        var amount = row.amount;
        var feemoney = row.feemoney;
        var actualMoney = (Number(amount) - Number(feemoney) ).toFixed(6);
        return actualMoney;
    }

    function remarkInfoFormatter(value, row, index)
    {

        var rs = '';

        try {
            var jsonObj = JSON.parse(row.remark);
            if(jsonObj)
            {
                rs += "划转订单 : " + jsonObj.followOrderno + "<br>";
                rs += "划转金额 : " + jsonObj.followAmount + "<br>";
            }
        } catch (e) {
        }
        return rs;
    }

    function beneficiaryFormatter(value, row, index) {
        var rs = '';
        var jsonObj = JSON.parse(row.remark);
        if(jsonObj)
        {
            var type = jsonObj.type;
            rs += "Type: " + jsonObj.type + "<br>";

            if(type == "Coin")
            {
                rs += "所属网络: " + jsonObj.ifsc + "<br>";
                rs += "受益地址: " + jsonObj.account + "<br>";
            }
            else
            {
                rs += "Name: " + jsonObj.beneficiaryName + "<br>";
                rs += "IFSC: " + jsonObj.ifsc + "<br>";

                if(jsonObj.name!=undefined && jsonObj.name!=null && jsonObj.name!=""){
                    rs += "bankName: " + jsonObj.name + "<br>";
                }

                rs += "Account: " + jsonObj.account + "<br>";
                rs += "Email: " + jsonObj.beneficiaryEmail + "<br>";
                rs += "Phone: " + jsonObj.beneficiaryPhone + "<br>";
                rs += "Channel: " + jsonObj.channelName + "<br>";
                if(jsonObj.idcard==undefined || jsonObj.idcard==null ){
                    rs += "ID: " + " " + "<br>";
                }else{
                    rs += "ID: " + jsonObj.idcard + "<br>";
                }
            }

        }
        return rs;
    }

    function errmsgFormatter(value, row, index) {
        var jsonObj = JSON.parse(row.remark);
        if(jsonObj)
        {
            var msg = jsonObj.errmsg;
            if(msg && msg !== "success")
            {
                return msg;
            }
        }
        return '-';
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

    function businessFormatter(value, row, index) {
        return $.global.constant.getBusinessTypeValue(value);
    }

    function statusFormatter(value, row, index) {
        return $.global.constant.getOrderStatusMsg(value);
    }


    function buildScanBrowserItem(networkType, key, type)
    {
        var rs = "";
        rs += '<a href="javascript:;" class="table-btn toScanBrowserPage" data-no="' + key + '" data-type="' + type +  '" data-network-type="' + networkType + '" title="通过">' + key + '</a> <br>';
        return rs;
    }

    $(function () {

        resetTime();

        <#if pdate != ''>
            var date = "${pdate} - ${pdate}";
            $('input[name="time"]').val(date);
        </#if>

        var options = TableUtils.getBtDefaultOptions();
        options.url = '/alibaba888/Liv2sky3soLa93vEr62/getWebSettleOrderList';
        options.search = false;
        options.showRefresh = true;
        options.clickToSelect= true;
        options.singleSelect= false;//是否单选，false表示多选;true标识只能单选
        options.queryParams = function (params) {
            params.time = $('input[name="time"]').val();
            params.username = $('input[name="username"]').val();
            params.agentname = $('input[name="agentname"]').val();
            params.staffname = $('input[name="staffname"]').val();
            params.parentname = $('input[name="parentname"]').val();
            params.grantname = $('input[name="grantname"]').val();

            params.systemOrderno = $('input[name="systemOrderno"]').val();
            params.transferNo = $('input[name="transferNo"]').val();


            params.beneficiaryAccount = $('input[name="beneficiaryAccount"]').val();
            params.beneficiaryIdcard = $('input[name="beneficiaryIdcard"]').val();

            params.currencyType = $('select[name="currencyType"]').val();
            params.txStatus = $('select[name="txStatus"]').val();
            params.settleStatus = $('select[name="settleStatus"]').val();

            params.reportid = $('input[name="reportid"]').val();

            return params;
        };
        var bootstrapTable = $('#bootstrapTable').bootstrapTable(options);

        $(document).on('click', '#create-btn', function () {
            window.location.href = '/alibaba888/Liv2sky3soLa93vEr62/toApplyPlatformSupplyPage';
        });

        $(document).on('click', '.table-btn.auditOrder', function () {
            var no = $(this).attr('data-no');
            var url = '/alibaba888/Liv2sky3soLa93vEr62/root_passport_user_withdraw_order_audit_waiting_result_page?orderno=' + no;
            openNewWindow(url);
        });

        $(document).on('click', '.table-btn.updatePassword', function () {
            var no = $(this).attr('data-no');
            $('#myModalDel #id').val(no);
            $('#myModalDel #type').attr('action', 'refuse');
            $('#myModalDel #tips').text('确定审核拒绝吗？');
            $('#myModalDel').modal();
        });

        $(document).on('click', '.table-btn.passOrder', function () {
            var no = $(this).attr('data-no');
            $('#myModalDel #id').val(no);
            $('#myModalDel #type').attr('action', 'passOrder');
            $('#myModalDel #tips').text('确定审核通过吗？');
            $('#myModalDel').modal();
        });

        $(document).on('click', '.table-btn.refuseOrder', function () {
            var no = $(this).attr('data-no');
            $('#myModalDel #id').val(no);
            $('#myModalDel #type').attr('action', 'refuseOrder');
            $('#myModalDel #tips').text('确定审核拒绝吗？');
            $('#myModalDel').modal();
        });

        $(document).on('click', '.table-btn.toScanBrowserPage', function () {
            var key = $(this).attr('data-no');
            var networkType = $(this).attr('data-network-type');
            var type = $(this).attr('data-type');

            $.ajax({
                type: "post",
                async: false,
                url: "/alibaba888/Liv2sky3soLa93vEr62/getCoinScanUrl",
                data: {
                    key:key.trim(),
                    networkType:networkType.trim(),
                    type:type.trim(),
                },
                dataType: "json",
                success: function (data) {
                    if (data != null && data.code == 200) {
                        openNewWindow(data.data);
                    } else {
                        $.global.openErrorMsg(data.msg);
                    }
                },
                error: function () {
                    $.global.openErrorMsg('操作失败，请重试');
                }
            })

        });

        $('#delete_submit').click(function () {
            var username = $('#myModalDel #id').val();
            var value = $('#myModalDel #type').val();
            var action = $('#myModalDel #type').attr('action');


            var no =  $('#myModalDel #id').val();

            if("passOrder" == action)
            {
                handleOrderStatus(no, "passOrder")
            }else if("refuseOrder" == action)
            {
                handleOrderStatus(no, "refuseOrder")
            }

            else if("settleWithdraw" == action)
            {
                settleWithdraw()
            }

        });

        $('#search-btn').click(function () {
            refresh();
        });

        $('#reset-btn').click(function () {
            $('input[name="username"]').val('');
            resetTime();
            //refresh();
        });

    });

    function refresh() {
        $('#bootstrapTable').bootstrapTable('refresh');
    }

    function handleOrderStatus(orderno, action) {
        $.ajax({
            type: "post",
            // async: false,
            url: "/alibaba888/Liv2sky3soLa93vEr62/updateWebSettleOrderStatus",
            data: {
                orderno:orderno.trim(),
                action:action.trim(),
            },
            dataType: "json",
            success: function (data) {
                if (data != null && data.code == 200) {
                    $.global.openSuccessMsg("Success!")
                } else {
                    $.global.openErrorMsg(data.msg);
                }
            },
            error: function () {
                $.global.openErrorMsg('操作失败，请重试');
            }
        })
    }


    $('#refuseAudit-btn').click(function () {
        var selected = $('#bootstrapTable').bootstrapTable('getSelections');

        //返回值为数据对象数组
        if(selected&&selected.length>0){
            //非空数组时候进行的操作
            var slength = selected.length;

            var nototalBetAmount = 0;
            var yestotalBetAmount = 0;
            var wtotalBetAmount = 0;
            var rtotalBetAmount = 0;
            var wtihdrowtotalBetAmount = 0;
            for(var i=0;i<selected.length;i++){
                wtihdrowtotalBetAmount = wtihdrowtotalBetAmount + selected[i].amount;
                if( selected[i].settleStatus =='realized'){
                    rtotalBetAmount = rtotalBetAmount + selected[i].amount;
                }else{
                    wtotalBetAmount = wtotalBetAmount + selected[i].amount;
                }

                if( selected[i].status =='realized'){
                    yestotalBetAmount = yestotalBetAmount + selected[i].amount;
                }else{
                    nototalBetAmount = nototalBetAmount + selected[i].amount;
                }

            }

            $("#nototalBetAmount").prop("value", "不可结算总额: " + nototalBetAmount);
            $("#yestotalBetAmount").prop("value", "可结算总额: " + yestotalBetAmount);
            $("#wtotalBetAmount").prop("value", "未结算总额: " + wtotalBetAmount);
            $("#rtotalBetAmount").prop("value", "已结算总额: " + rtotalBetAmount);
            $("#wtihdrowtotalBetAmount").prop("value", "总打款金额: " + wtihdrowtotalBetAmount);



        }else{
            //空数组的操作
            $.global.openErrorMsg('请先选择要操作的数据!');
        }

    });

    // $(document).on('click', '.table-btn.settleWithdraw', function () {
    //     var no = $(this).attr('data-no');
    //     $('#myModalDel #id').val(no);
    //     $('#myModalDel #type').attr('action', 'refuseOrder');
    //     $('#myModalDel #tips').text('确定审核拒绝吗？');
    //     $('#myModalDel').modal();
    // });

    $('#refuseAudit2-btn').click(function () {
        var selected = $('#bootstrapTable').bootstrapTable('getSelections');

        //返回值为数据对象数组
        if(selected&&selected.length>0) {
            //非空数组时候进行的操作
            var slength = selected.length;

            var list = [];


            for (var i = 0; i < selected.length; i++) {
                if (selected[0].agentname != selected[i].agentname) {
                    return $.global.openErrorMsg('存在不同代理订单，结算失败!')
                }
                if (selected[i].status != 'realized') {
                    return $.global.openErrorMsg('存在不可结算订单，结算失败!')
                }
                if (selected[i].settleStatus == 'realized') {
                    return $.global.openErrorMsg('存在已结算订单，结算失败!')
                }

                if (list.length > 0) {
                    var status = 0
                    for (var j = 0; j < list.length; j++) {
                        if (list[j].networkType == selected[i].networkType && list[j].currency == selected[i].currency) {
                            list[j].amount = list[j].amount + selected[i].amount
                            status++
                            break
                        }
                    }

                    if (status == 0) {
                        // list.push(selected[i])
                        var obj = {};
                        obj.networkType = selected[i].networkType;
                        obj.currency = selected[i].currency;
                        obj.amount = selected[i].amount;
                        obj.agentname = selected[i].agentname;
                        list.push(obj)
                    }


                } else {
                    var obj = {};
                    obj.networkType = selected[i].networkType;
                    obj.currency = selected[i].currency;
                    obj.amount = selected[i].amount;
                    obj.agentname = selected[i].agentname;
                    list.push(obj)
                }
            }


           // var remark =  JSON.stringify(list);

           // var jsonArray = eval(row.remark);
            var rs = '';
            if(list)
            {
                // for(var i=0;i< list.length;i++){
                //     rs += "网络 : " + list[i].networkType +" | " + list[i].currency + " 金额： " +  list[i].amount  +"  ########  ";
                // }


                var contractInfoDIV = $('#myModalDel #tips2')
                var htmlText = " ";

                for (var i=0;i< list.length;i++){
                    var contractInfo = list[i];
                    htmlText = htmlText + "<div>" + "网络 : " + list[i].networkType +" | " + list[i].currency + " 出款金额： " +  list[i].amount   +"  " + "</div><br>";
                }

                contractInfoDIV.html(htmlText);

            }

            $('#myModalDel #type').attr('action', 'settleWithdraw');
            $('#myModalDel #tips').text('确定结算订单吗？');
            //$('#myModalDel #tips2').text(''+ rs);
            $('#myModalDel').modal();

        }else{
            $.global.openErrorMsg('请先选择要操作的数据!');
            return
        }


    })

    function settleWithdraw() {
        var selected = $('#bootstrapTable').bootstrapTable('getSelections');

        //返回值为数据对象数组
        if(selected&&selected.length>0){
            //非空数组时候进行的操作
            var slength = selected.length;

            var list = [];


            for(var i=0;i<selected.length;i++){
                if(selected[0].agentname!= selected[i].agentname){
                    return  $.global.openErrorMsg('存在不同代理订单，结算失败!')
                }
                if(selected[i].status !='realized' ){
                    return  $.global.openErrorMsg('存在不可结算订单，结算失败!')
                }
                if(selected[i].settleStatus =='realized' ){
                    return  $.global.openErrorMsg('存在已结算订单，结算失败!')
                }

                if(list.length >0){
                    var status = 0
                    for(var j=0; j<list.length; j++){
                        if(list[j].networkType == selected[i].networkType && list[j].currency == selected[i].currency ){
                            list[j].amount = list[j].amount + selected[i].amount
                            status++
                            break
                        }
                    }

                    if(status == 0){
                        // list.push(selected[i])
                        var obj = {};
                        obj.networkType = selected[i].networkType;
                        obj.currency = selected[i].currency;
                        obj.amount = selected[i].amount;
                        obj.agentname = selected[i].agentname;
                        list.push(obj)
                    }


                }else{
                    var obj = {};
                    obj.networkType = selected[i].networkType;
                    obj.currency = selected[i].currency;
                    obj.amount = selected[i].amount;
                    obj.agentname = selected[i].agentname;
                    list.push(obj)
                }
            }

            var remark =  JSON.stringify(list);
            var StringSelected =  JSON.stringify(selected);

            $.ajax({
                type: "post",
                // async: false,
                url: "/alibaba888/Liv2sky3soLa93vEr62/addSettleWithdrawReport",
                data: {
                    orderno:selected[0].no,
                    remark:remark,
                    StringSelected:StringSelected,
                },
                dataType: "json",
                success: function (data) {
                    if (data != null && data.code == 200) {
                        $.global.openSuccessMsg("结算成功，请查看结算订单记录!")
                        refresh();
                    } else {
                        $.global.openErrorMsg(data.msg);
                    }
                },
                error: function () {
                    $.global.openErrorMsg('操作失败，请重试');
                }
            })

        }else{
            //空数组的操作
            $.global.openErrorMsg('请先选择要操作的数据!');
        }
    }


    // $('#refuseAudit2-btn').click(function () {
    //
    //
    // });


</script>
</body>
</html>
