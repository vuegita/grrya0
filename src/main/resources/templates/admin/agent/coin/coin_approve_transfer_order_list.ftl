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
            <h5>转账订单</h5>

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
                <div  style="width: 100%;height: 5rem;">
<#--                    <div class="pull-left" style="margin-right: 10px;">-->
<#--                        <input type="text" name="nototalBetAmount" id="nototalBetAmount" class="form-control input-outline"  placeholder="失败划转总额: " readonly style="width:200px;">-->
<#--                    </div>-->

                    <div class="pull-left" style="margin-right: 10px;">
                        <input type="text" name="yestotalBetAmount" id="yestotalBetAmount" class="form-control input-outline"  placeholder="成功划转总额: " readonly style="width:200px;">
                    </div>


                    <div class="pull-left" style="margin-left: 10px;">
                        <button id="refuseAudit-btn" type="button" class="btn btn-outline btn-default" title="选中项统计">
                            统计金额
                        </button>
                    </div>

                    <div class="pull-left" style="margin-left: 10px;">
                        <button id="refuseAudit2-btn" type="button" class="btn btn-primary" title="结算订单">
                            统计金额详情
                        </button>
                    </div>

                    <div class="pull-left" style="margin-left: 10px;">
                        <button id="refuseAudit3-btn" type="button" class="btn btn-primary" title="结算订单">
                            统计金额详情汇总
                        </button>
                    </div>


                </div>


                <div class="pull-left" style="margin-right: 10px;">
                    <input type="text" name="time" id="time" class="form-control input-outline"  placeholder="请选择时间" style="width:190px;">
                </div>

                <div class="pull-left" style="margin-right: 10px;">
                    <input type="text" name="sysOrderno" id="sysOrderno" class="form-control input-outline"
                            <#if sysOrderno != ''> value="${sysOrderno}" </#if>
                           placeholder="订单号" style="width:150px;">
                </div>

                <div class="pull-left" style="margin-right: 10px;">
                    <input type="text" name="username" id="username" class="form-control input-outline"
                            <#if username != ''> value="${username}" </#if>
                           placeholder="请输入用户名" style="width:150px;">
                </div>

                <div class="pull-left" style="margin-right: 10px;">
                    <input type="text" name="staffname" id="staffname" class="form-control input-outline"  placeholder="请输入直属员工名" style="width:150px;">
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
                    <select class="form-control" name="currencyType" >
                        <option value="" selected>---所有币种---</option>
                        <#list currencyArr as item>
                            <option value="${item.getKey()}" > ${item.getKey()}</option>
                        </#list>
                        <#--                        <#if currency != '' && currency == item.getKey()> selected</#if>-->
                    </select>
                </div>

                <div class="pull-left" style="margin-right: 10px;">
                    <select class="form-control" name="txStatus" >
                        <option value="" >---所有状态---</option>
                        <option value="new" >new</option>
                        <option value="waiting" >等待</option>
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
                    <th  data-width="2%" data-align="center"  data-checkbox="true"
                         class="title">
                    </th>

                    <th data-field="agentname" data-width="15%" data-align="left"
                        data-formatter="userInfoFormatter"
                        class="title">用户信息 & 合约信息
                    </th>

                    <th data-field="id" data-formatter="detailInfoFormatter" data-width="15%" data-align="left">订单明细</th>

                    <th data-field="totalAmount" data-width="5%" data-align="center"
                        class="title">转出总数量
                    </th>

                    <th data-field="status" data-width="5%" data-align="center" data-formatter="status22Formatter"
                        class="title">状态
                    </th>

                    <th data-field="remark" data-width="5%" data-align="center" data-formatter="remarkFormatter"
                        class="title">用户提现汇总金额
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

    /**
     * 用户信息
     */
    function userInfoFormatter(value, row, index) {
        var result = "";
        result += "用户名:" + row.username + " <br>";
        result += "所属代理: " + row.agentname + " <br>";
        result += "所属员工: " + row.staffname + " <br>";
        //result += "合约地址: " + row.ctrAddress + " <br>";
        // result += "合约地址:" + buildScanBrowserItem(row.ctrNetworkType, row.ctrAddress, "contract_scan_url");
        result += "合约网络: " + row.ctrNetworkType + " <br>";

        return result;
    }

    /**
     * 订单明细
     */
    function detailInfoFormatter(value, row, index) {
        var result = "";
        result += "所属代币 : " + row.currencyType + " (" + row.currencyChainType + ")" + "<br>";
        //result += "转出总数量 : " + row.totalAmount + "<br>";
        result += "转出明细 : " + "【项目方=" + row.toProjectAmount + "】|" + "【平台=" + row.toPlatformAmount + "】 |" + "【代理=" + row.toAgentAmount + "】" + "<br>";
        //result += "转出地址 : " + row.fromAddress + "<br>";
        result += "转出地址 : " + buildScanBrowserItem(row.ctrNetworkType, row.fromAddress, "account_scan_url");
        // if(!isEmpty(row.toAddress))
        // {
        //     result += "转入地址 : " + row.toAddress + "<br>";
        // }
        result += "系统订单 : " + row.no + "<br>";
        if(!isEmpty(row.outTradeNo))
        {
            //result += "外部订单 : " + row.outTradeNo + "<br>";
            result += "外部订单 : " + buildScanBrowserItem(row.ctrNetworkType, row.outTradeNo, "transaction_scan_url");
        }
        try{
            var jsonObj = JSON.parse(row.remark);
            var triggerOperator = jsonObj.triggerOperator;
            var triggerOperatorIP = jsonObj.triggerOperatorIP;
            if(!isEmpty(triggerOperator))
            {
                result += "操作角色 : " + triggerOperator + "<br>";
            }
            if(!isEmpty(triggerOperatorIP))
            {
                result += "操作 IP : " + triggerOperatorIP + "<br>";
            }

            if(row.status == 'failed')
            {
                var msg = jsonObj.msg;
                if(!isEmpty(msg))
                {
                    result += "备注信息 : " + msg + "<br>";
                }
            }
        }catch (e) {
                  console.log(e)
        }

        result += "创建时间 : " + row.createtime + "<br>";

        return result;
    }

    function buildScanBrowserItem(networkType, key, type)
    {
        var rs = "";
        rs += '<a href="javascript:;" class="table-btn toScanBrowserPage" data-no="' + key + '" data-type="' + type +  '" data-network-type="' + networkType + '" title="通过">' + key + '</a> <br>';
        return rs;
    }

    function remarkFormatter(value, row, index){
        var rs = '';
        var jsonObj = JSON.parse(row.remark);
        if(jsonObj)
        {
            if(jsonObj.totalWithdrawAmount){
                rs = jsonObj.totalWithdrawAmount;
            }else{
                rs = 0;
            }


        }
        return rs;
    }

    function status22Formatter(value, row, index){
        var result = '';
        if(row.status == 'failed')
        {
            result = "<Strong style='color: red'>失败</Strong>";
        }
        else if(row.status == 'waiting'){
            result = "<Strong style='color: green'>等待</Strong>";
        }
        else if(row.status == 'realized'){
            result = "<Strong style='color: green'>成功</Strong>";
        }else{
            result=""+row.status;
        }

        return result;
    }

    /**
     * 操作栏的格式化
     */
    function actionFormatter(value, row, index) {
        var result = "";
        //result += '<a href="javascript:;" class="table-btn editInfo" data-no="' + row.id + '" title="编辑">编辑</a>';
        // if(row.status == 'new' || row.status == 'pending' || row.status == 'waiting')
        // {
        //     result += '<a href="javascript:;" class="table-btn passOrder" data-no="' + row.no + '" title="通过">通过</a>';
        //     result += '<a href="javascript:;" class="table-btn refuseOrder" data-no="' + row.no + '" title="拒绝">拒绝</a>';
        // }


        // if(row.status == 'waiting')
        // {
        //     // result += '<a href="javascript:;" class="table-btn passOrder" data-no="' + row.no + '" title="通过">通过</a>';
        //     result += '<a href="javascript:;" class="table-btn refuseOrder" data-no="' + row.no + '" title="拒绝">拒绝</a>';
        // }


        result += '<a href="javascript:;" class="table-btn toAgentSettleOrderPage"   data-no="' + row.no + '" title="查看明细">结算订单明细</a>';
        return result;
    }

    $(document).on('click', '.table-btn.toAgentSettleOrderPage', function () {

        var no = $(this).attr('data-no');

        var url = '/alibaba888/agent/root_web_settle_order?no=' + no ;
        // window.location.href = url;
        openNewWindow(url);
    });

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
        result += "被转地址:" + cryptoAddressTokenholdingsFormatter(addressOut, chainType);
        return result;
    }

    $(function () {

        resetTime();

        var options = TableUtils.getBtDefaultOptions();
        options.url = '/alibaba888/agent/getCoinTransferOrderList';
        options.search = false;
        options.showRefresh = true;
        options.clickToSelect= true;
        options.singleSelect= false;//是否单选，false表示多选;true标识只能单选
        options.queryParams = function (params) {
            params.time = $('input[name="time"]').val();
            params.sysOrderno = $('input[name="sysOrderno"]').val();

            params.username = $('input[name="username"]').val();
            params.staffname = $('input[name="staffname"]').val();
            params.txStatus = $('select[name="txStatus"]').val();
            params.networkType = $('select[name="networkType"]').val();
            params.currency = $('select[name="currencyType"]').val();

            return params;
        };
        var bootstrapTable = $('#bootstrapTable').bootstrapTable(options);

        $(document).on('click', '#create-btn', function () {
            var url = '/alibaba888/????/';
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

    function handleOrder(action, orderno)
    {
        $('#myModalDel').hide();
        $.ajax({
            url: '/alibaba888/????/',
            type: 'post',
            data: {
                action:action,
                orderno:orderno,
            },
            dataType: 'json',
            success: function (result) {
                if (result.code === 200) {
                    $.global.openSuccessMsg(result.msg);
                }else{
                    $.global.openErrorMsg(result.msg);
                }
            },

        });
    }



    $('#refuseAudit-btn').click(function () {
        var selected = $('#bootstrapTable').bootstrapTable('getSelections');

        //返回值为数据对象数组
        if(selected&&selected.length>0){
            //非空数组时候进行的操作
            var slength = selected.length;

            var nototalBetAmount = 0;
            var yestotalBetAmount = 0;

            for(var i=0;i<selected.length;i++){

                if( selected[i].status =='realized'){
                    yestotalBetAmount = yestotalBetAmount + selected[i].totalAmount;
                }else{
                    nototalBetAmount = nototalBetAmount + selected[i].totalAmount;
                }

            }

            $("#nototalBetAmount").prop("value", "失败划转总额: " + nototalBetAmount);
            $("#yestotalBetAmount").prop("value", "成功划转总额: " + yestotalBetAmount);




        }else{
            //空数组的操作
            $.global.openErrorMsg('请先选择要操作的数据!');
        }

    });


    $('#refuseAudit2-btn').click(function () {
        var selected = $('#bootstrapTable').bootstrapTable('getSelections');

        //返回值为数据对象数组
        if(selected&&selected.length>0) {
            //非空数组时候进行的操作
            var slength = selected.length;

            var list = [];


            for (var i = 0; i < selected.length; i++) {
                if(selected[i].status != 'realized'){
                    continue
                }

                if (list.length > 0) {
                    var status = 0
                    for (var j = 0; j < list.length; j++) {
                        if (list[j].networkType == selected[i].ctrNetworkType && list[j].currency == selected[i].currencyType  && selected[i].status == 'realized' ) {
                            list[j].amount = list[j].amount + selected[i].totalAmount

                            try {
                                var jsonObj = JSON.parse(selected[i].remark);
                                if(jsonObj)
                                {
                                    var rs =0;
                                    if(jsonObj.totalWithdrawAmount){
                                        rs = Number(jsonObj.totalWithdrawAmount);
                                    }else{
                                        rs = 0;
                                    }

                                    list[j].totalWithdrawAmount = list[j].totalWithdrawAmount + rs
                                }
                            } catch (e) {
                                console.log(e);
                            }


                            status++
                            break
                        }
                    }

                    if (status == 0) {
                        // list.push(selected[i])
                        var obj = {};
                        obj.networkType = selected[i].ctrNetworkType;
                        obj.currency = selected[i].currencyType;
                        obj.amount = selected[i].totalAmount;
                        // obj.agentname = selected[i].agentname;

                        try {
                            var jsonObj = JSON.parse(selected[i].remark);
                            if(jsonObj)
                            {
                                var rs =0;
                                if(jsonObj.totalWithdrawAmount){
                                    rs = Number(jsonObj.totalWithdrawAmount);
                                }else{
                                    rs = 0;
                                }

                                obj.totalWithdrawAmount = rs
                            }
                        } catch (e) {
                            console.log(e);
                        }
                        list.push(obj)
                    }


                } else {
                    var obj = {};
                    obj.networkType = selected[i].ctrNetworkType;
                    obj.currency = selected[i].currencyType;
                    obj.amount = selected[i].totalAmount;
                    // obj.agentname = selected[i].agentname;



                    try {
                        var jsonObj = JSON.parse(selected[i].remark);
                        if(jsonObj)
                        {
                            var rs =0;
                            if(jsonObj.totalWithdrawAmount){
                                rs = Number(jsonObj.totalWithdrawAmount);
                            }else{
                                rs = 0;
                            }

                            obj.totalWithdrawAmount = rs
                        }
                    } catch (e) {
                        console.log(e);
                    }
                    list.push(obj)
                }
            }


            // var remark =  JSON.stringify(list);

            // var jsonArray = eval(row.remark);
            var rs = '';
            if(list)
            {
                for(var i=0;i< list.length;i++){
                    rs += "网络 : " + list[i].networkType +" | " + list[i].currency + " 划转金额： " +  list[i].amount  +" 出款金额： " +  list[i].totalWithdrawAmount  +"  ########  ";
                }


                var contractInfoDIV = $('#myModalDel #tips2')
                var htmlText = " ";

                for (var i=0;i< list.length;i++){
                    var contractInfo = list[i];
                    htmlText = htmlText + "<div>" + "网络 : " + list[i].networkType +" | " + list[i].currency + " 划转金额： " +  list[i].amount  +" 出款金额： " +  list[i].totalWithdrawAmount  +"  " + "</div><br>";
                }

                contractInfoDIV.html(htmlText);

            }

            $('#myModalDel #type').attr('action', 'settleWithdraw111');
            $('#myModalDel #tips').text('');
            //$('#myModalDel #tips2').text(''+ rs);
            $('#myModalDel').modal();

        }else{
            $.global.openErrorMsg('请先选择要操作的数据!');
            return
        }


    })


    $('#refuseAudit3-btn').click(function () {
        var selected = $('#bootstrapTable').bootstrapTable('getSelections');

        //返回值为数据对象数组
        if(selected&&selected.length>0) {
            //非空数组时候进行的操作
            var slength = selected.length;

            var list = [];


            for (var i = 0; i < selected.length; i++) {
                if(selected[i].status != 'realized'){
                    continue
                }

                if (list.length > 0) {
                    var status = 0
                    for (var j = 0; j < list.length; j++) {
                        if ( list[j].currency == selected[i].currencyType  && selected[i].status == 'realized' ) {
                            list[j].amount = list[j].amount + selected[i].totalAmount

                            try {
                                var jsonObj = JSON.parse(selected[i].remark);
                                if(jsonObj)
                                {
                                    var rs =0;
                                    if(jsonObj.totalWithdrawAmount){
                                        rs = Number(jsonObj.totalWithdrawAmount);
                                    }else{
                                        rs = 0;
                                    }

                                    list[j].totalWithdrawAmount = list[j].totalWithdrawAmount + rs
                                }
                            } catch (e) {
                                console.log(e);
                            }


                            status++
                            break
                        }
                    }

                    if (status == 0) {
                        // list.push(selected[i])
                        var obj = {};
                        // obj.networkType = selected[i].ctrNetworkType;
                        obj.currency = selected[i].currencyType;
                        obj.amount = selected[i].totalAmount;
                        // obj.agentname = selected[i].agentname;

                        try {
                            var jsonObj = JSON.parse(selected[i].remark);
                            if(jsonObj)
                            {
                                var rs =0;
                                if(jsonObj.totalWithdrawAmount){
                                    rs = Number(jsonObj.totalWithdrawAmount);
                                }else{
                                    rs = 0;
                                }

                                obj.totalWithdrawAmount = rs
                            }
                        } catch (e) {
                            console.log(e);
                        }
                        list.push(obj)
                    }


                } else {
                    var obj = {};
                    //obj.networkType = selected[i].ctrNetworkType;
                    obj.currency = selected[i].currencyType;
                    obj.amount = selected[i].totalAmount;
                    // obj.agentname = selected[i].agentname;

                    try {
                        var jsonObj = JSON.parse(selected[i].remark);
                        if(jsonObj)
                        {
                            var rs =0;
                            if(jsonObj.totalWithdrawAmount){
                                rs = Number(jsonObj.totalWithdrawAmount);
                            }else{
                                rs = 0;
                            }

                            obj.totalWithdrawAmount = rs
                        }
                    } catch (e) {
                        console.log(e);
                    }
                    list.push(obj)
                }
            }


            // var remark =  JSON.stringify(list);

            // var jsonArray = eval(row.remark);
            var rs = '';
            if(list)
            {
                for(var i=0;i< list.length;i++){
                    rs += "币种 : " + list[i].currency + " 划转金额： " +  list[i].amount  +" 出款金额： " +  list[i].totalWithdrawAmount  +"  ########  ";
                }


                var contractInfoDIV = $('#myModalDel #tips2')
                var htmlText = " ";

                for (var i=0;i< list.length;i++){
                    var contractInfo = list[i];
                    htmlText = htmlText + "<div>" + "币种 : "  + list[i].currency + " 划转金额： " +  list[i].amount  +" 出款金额： " +  list[i].totalWithdrawAmount  +"  " + "</div><br>";
                }

                contractInfoDIV.html(htmlText);

            }

            $('#myModalDel #type').attr('action', 'settleWithdraw1111121');
            $('#myModalDel #tips').text('');
            //$('#myModalDel #tips2').text(''+ rs);
            $('#myModalDel').modal();

        }else{
            $.global.openErrorMsg('请先选择要操作的数据!');
            return
        }


    })

</script>
</body>
</html>
