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
            <h5>数字货币收益订单</h5>

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
                    <input type="text" name="time" id="time" class="form-control input-outline"  placeholder="请选择时间" style="width:190px;">
                </div>

                <div class="pull-left" style="margin-right: 10px;">
                    <input type="text" name="sysOrderno" id="sysOrderno" class="form-control input-outline"  placeholder="订单号" style="width:150px;">
                </div>

                <div class="pull-left" style="margin-right: 10px;">
                    <input type="text" name="username" id="username" class="form-control input-outline"  placeholder="请输入用户名" style="width:150px;">
                </div>

                <div class="pull-left" style="margin-right: 10px;">
                    <input type="text" name="staffname" id="staffname" class="form-control input-outline"  placeholder="请输入员工名" style="width:150px;">
                </div>

<#--                <div class="pull-left" style="margin-right: 10px;">-->
<#--                    <select class="form-control" name="eventType" >-->
<#--                        <option value="" selected>---所有事件类型---</option>-->
<#--                        <#list adEventTypeList as item>-->
<#--                            <option value="${item.getKey()}" >${item.getName()} | ${item.getKey()}</option>-->
<#--                        </#list>-->
<#--                    </select>-->
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
                    <select class="form-control" name="txStatus" >
                        <option value="" >---所有状态---</option>
                        <option value="new" >创建中</option>
                        <option value="waiting" >等待中</option>
                        <option value="realized" >成功</option>
                        <option value="failed" >失败</option>
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
                    <th data-field="agentname" data-width="15%" data-align="left"
                        data-formatter="userInfoFormatter"
                        class="title">用户信息 & 代币信息
                    </th>

                    <th data-field="id" data-formatter="detailInfoFormatter" data-width="15%" data-align="left">订单明细</th>

                    <th data-field="currencyType"  data-width="5%" data-align="center" class="title">所属代币</th>

                    <th data-field="amount"  data-width="5%" data-align="center" class="title">订单金额</th>
                    <#--                    data-field="type"-->
                    <th  data-width="5%" data-align="center" data-formatter="typeactionFormatter"
                         class="title">订单类型
                    </th>

                    <th data-field="status" data-width="5%" data-align="center" data-formatter="statusactionFormatter"
                        class="title">状态
                    </th>

                    <th data-field="createtime" data-width="8%" data-align="center"
                        class="title">创建时间
                    </th>

                    <th data-field="id" data-formatter="actionFormatter" data-width="10%" data-align="center">操作</th>
                </tr>
                </thead>
            </table>
        </div>
    </div>
</div>

<#include "../../../common/delete_form.ftl">
<#--<#include "../passport/user_attr_list_edit_info.ftl">-->
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
            if(day>30){
                layTime.hint('最多选择30天');
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
        date2.setDate(date1.getDate()-30);
        var time2 = date2.getFullYear()+"-"+(date2.getMonth()+1)+"-"+date2.getDate();
        var date = time2 + " - " + time1;
        $('input[name="time"]').val(date);
    }

    /**
     * 用户信息
     */
    function userInfoFormatter(value, row, index) {
        var result = "";
        result += "用户名:" + row.username + " <br>";
        result += "所属代理: " + row.agentname + " <br>";
        result += "所属员工: " + row.staffname + " <br>";
        //result += "合约地址: " + row.ctrAddress + " <br>";
        //result += "合约地址:" + buildScanBrowserItem(row.ctrNetworkType, row.ctrAddress, "contract_scan_url");
        result += "所属网络: " + row.networkType + " <br>";
        result += "所属代币: " + row.currencyType + " <br>";

        return result;
    }

    /**
     * 订单明细
     */
    function detailInfoFormatter(value, row, index) {
        var result = "";
        result += "所属网络 : " + row.networkType + "<br>";
        // result += "所属代币 : " + row.currencyType + "<br>";
        // result += "订单金额 : " + row.amount + "<br>";
        // result += "订单类型 : " + row.type + "<br>";
        //result += "转出明细 : " + "【项目方=" + row.toProjectAmount + "】|" + "【平台=" + row.toPlatformAmount + "】 |" + "【代理=" + row.toAgentAmount + "】" + "<br>";
        //result += "转出地址 : " + row.fromAddress + "<br>";
        //result += "转出地址 : " + buildScanBrowserItem(row.ctrNetworkType, row.fromAddress, "account_scan_url");
        result += "系统订单 : " + row.no + "<br>";
        if(!isEmpty(row.outTradeNo))
        {
            result += "外部订单 : " + row.outTradeNo + "<br>";
            //result += "外部订单 : " + buildScanBrowserItem(row.ctrNetworkType, row.outTradeNo, "transaction_scan_url");
        }

        return result;
    }

    function buildScanBrowserItem(networkType, key, type)
    {
        var rs = "";
        rs += '<a href="javascript:;" class="table-btn toScanBrowserPage" data-no="' + key + '" data-type="' + type +  '" data-network-type="' + networkType + '" title="通过">' + key + '</a> <br>';
        return rs;
    }


    function typeactionFormatter(value, row, index){
        var result = "";
        result += '';
        if(row.type == 'reward')
        {
            result += 'Defi挖矿收益';
        }
        else if(row.type == 'Staking'){
            result += '质押收益';
        }
        else if(row.type == 'voucher_node_not_settle'){
            result += 'DeFi代金收益(不结算)';
        }
        else{
            result += row.type;
        }
        return result;
    }

    function statusactionFormatter(value, row, index){
        var result = "";
        result += '';
        if(row.status == 'realized')
        {
            result += '成功';
        }else if(row.status == 'failed'){
            result += '失败';
        }else if(row.status == 'waiting'){
            result += '等待中';
        }else if(row.status == 'new'){
            result += '创建中';
        }else{
            result += row.status;
        }
        return result;
    }

    /**
     * 操作栏的格式化
     */
    function actionFormatter(value, row, index) {
        var result = "";
        //result += '<a href="javascript:;" class="table-btn editInfo" data-no="' + row.id + '" title="编辑">编辑</a>';
        if(row.status == 'new' || row.status == 'pending')
        {
            //result += '<a href="javascript:;" class="table-btn passOrder" data-no="' + row.no + '" title="通过">通过</a>';
            //result += '<a href="javascript:;" class="table-btn refuseOrder" data-no="' + row.no + '" title="拒绝">拒绝</a>';
        }
        return result;
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

    /**
     * 钱包地址的格式化
     */
    function addressFormatter(value, row, index) {
        var addressIn = row.addressIn;
        var addressOut = row.addressOut;
        var chainType = row.chainType;
        var result = "转入地址:" + cryptoAddressTokenholdingsFormatter(addressIn, chainType) + "<br>";
        result += "转出地址:" + cryptoAddressTokenholdingsFormatter(addressOut, chainType);
        return result;
    }

    $(function () {

        resetTime();

        var options = TableUtils.getBtDefaultOptions();
        options.url = '/alibaba888/agent/getCoinMiningOrderInfoList';
        options.search = false;
        options.showRefresh = true;
        options.queryParams = function (params) {
            params.time = $('input[name="time"]').val();
            params.sysOrderno = $('input[name="sysOrderno"]').val();

            params.username = $('input[name="username"]').val();
            params.staffname = $('input[name="staffname"]').val();
            params.txStatus = $('select[name="txStatus"]').val();
            params.networkType = $('select[name="networkType"]').val();


            return params;
        };
        var bootstrapTable = $('#bootstrapTable').bootstrapTable(options);

        $(document).on('click', '#create-btn', function () {
            var url = '/alibaba888/?????/toEditAdCategoryPage';
            openNewWindow(url);
        });



        //
        $(document).on('click', '.table-btn.passOrder', function () {
            var id = $(this).attr('data-no');
            $('#myModalDel #id').val(id);
            $('#myModalDel #type').attr('action', 'passOrder');
            $('#myModalDel #tips').text('确定通过吗？');
            $('#myModalDel').modal();
        });

        $(document).on('click', '.table-btn.refuseOrder', function () {
            var id = $(this).attr('data-no');
            $('#myModalDel #id').val(id);
            $('#myModalDel #type').attr('action', 'refuseOrder');
            $('#myModalDel #tips').text('确定拒绝吗？');
            $('#myModalDel').modal();
        });


        // $(document).on('click', '.table-btn.toScanBrowserPage', function () {
        //     var key = $(this).attr('data-no');
        //     var networkType = $(this).attr('data-network-type');
        //     var type = $(this).attr('data-type');
        //
        //     $.ajax({
        //         type: "post",
        //         async: false,
        //         url: "/alibaba888/?????/getCoinScanUrl",
        //         data: {
        //             key:key.trim(),
        //             networkType:networkType.trim(),
        //             type:type.trim(),
        //         },
        //         dataType: "json",
        //         success: function (data) {
        //             if (data != null && data.code == 200) {
        //                 openNewWindow(data.data);
        //             } else {
        //                 $.global.openErrorMsg(data.msg);
        //             }
        //         },
        //         error: function () {
        //             $.global.openErrorMsg('操作失败，请重试');
        //         }
        //     })
        //
        // });

        $('#delete_submit').click(function () {
            var id = $('#myModalDel #id').val();
            // var username = $('#myModalDel #type').val();
            var action = $('#myModalDel #type').attr('action');
            if(action == 'passOrder' || action == 'refuseOrder')
            {
                handleOrder(action, id);
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

            resetTime();

            $('input[name="sysOrderno"]').val('');
            $('input[name="materielid"]').val('');
            $('select[name="eventType"]').val('');
            $('select[name="txStatus"]').val('');

            // resetTime();
            //refresh();
        });

    });

    function refresh() {
        $('#bootstrapTable').bootstrapTable('refresh');
    }





</script>
</body>
</html>
