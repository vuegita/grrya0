<!DOCTYPE HTML>
<html>
<head>
    <#include "../../common/head-meta.ftl"/>
    <title>${projectName}商户后台管理系统-订单管理</title>
    <link type="text/css" rel="stylesheet" href="${STATIC_URL}/plugins/bootstrap-table/bootstrap-table.min.css"/>
</head>

<body class="gray-bg">
<div class="wrapper wrapper-content animated fadeInRight">
    <div class="ibox">
        <div class="ibox-title">
            <h5>提现审核</h5>
            <p style="color: blue"> 　</p>
            <p  style="color: green">1. 【TEST123456789666】-开发人员测试使用|运营人员忽略!</p>
        </div>

        <div class="ibox-content">
            <div class="btn-group hidden-xs" id="bootstrapTableToolBar" role="group">


<#--                <div class="pull-left" style="margin-right: 10px;">-->
<#--                    <button id="create-btn" type="button" class="btn btn-outline btn-default" title="补单">-->
<#--                        <i class="glyphicon glyphicon-plus" aria-hidden="true"></i> 补单-->
<#--                    </button>-->
<#--                </div>-->

                <div class="pull-left" style="margin-right: 10px;">
                    <input type="text" name="time" id="time" class="form-control input-outline"  placeholder="请选择时间" style="width:200px;">
                </div>

                <div class="pull-left" style="margin-right: 10px;">
                    <input type="text" name="username" id="username" class="form-control input-outline"  placeholder="请输入用户名" style="width:200px;">
                </div>

                <div class="pull-left" style="margin-right: 10px;">
                    <input type="text" name="agentname" id="agentname" class="form-control input-outline"  placeholder="请输入代理用户名" style="width:150px;">
                </div>

                <div class="pull-left" style="margin-right: 10px;">
                    <input type="text" name="staffname" id="staffname" class="form-control input-outline"  placeholder="请输入直属员工名" style="width:150px;">
                </div>

                <div class="pull-left" style="margin-right: 10px;">
                    <input type="text" name="systemOrderno" id="systemOrderno" class="form-control input-outline"  placeholder="请输入系统订单号" style="width:150px;">
                </div>

                <div class="pull-left" style="margin-right: 10px;">
                    <select class="form-control" name="type" >
                        <option value="" >---订单类型---</option>
                        <option value="platform_recharge" >平台充值</option>
                        <option value="platform_deduct" >平台扣款</option>
                        <option value="platform_presentation" >平台赠送</option>
                    </select>
                </div>

                <div class="pull-left" style="margin-right: 10px;">
                    <select class="form-control" name="txStatus" >
                        <option value="" >---订单状态---</option>
                        <option value="new" >new</option>
                        <option value="realized" >realized</option>
                        <option value="failed" >failed</option>
                    </select>
                </div>

                <button id="search-btn" type="button" class="btn btn-outline btn-default" title="查询">
                    <i class="glyphicon glyphicon-search" aria-hidden="true"></i> 查询
                </button>
                <button id="reset-btn" type="button" class="btn btn-outline btn-default" title="重置">
                    <i class="glyphicon glyphicon-repeat" aria-hidden="true"></i> 重置
                </button>

                <div class="pull-left" style="margin-left: 10px;">
                <button id="passAudit-btn" type="button" class="btn btn-outline btn-default" title="选中项通过">
                     一键通过
                </button>
                </div>

                <div class="pull-left" style="margin-left: 10px;">
                    <button id="refuseAudit-btn" type="button" class="btn btn-outline btn-default" title="选中项拒绝">
                        一键拒绝
                    </button>
                </div>

                <div class="pull-left" style="margin-left: 10px;">
                    <button id="withdraw-auth-btn" type="button" class="btn btn-outline btn-default" title="提现认证">
                        提现认证
                    </button>
                </div>

            </div>
            <table id="bootstrapTable">
                <thead>
                <tr>
                    <th  data-width="2%" data-align="center"  data-checkbox="true"
                        class="title">
                    </th>

<#--                    <th data-field="no" data-width="5%" data-align="center"-->
<#--                        class="title">系统订单号-->
<#--                    </th>-->

<#--                    <th data-field="payProductType" data-width="5%" data-align="center"-->
<#--                        class="title">支付渠道-->
<#--                    </th>-->

                    <th data-field="username" data-width="15%" data-align="left"
                        data-formatter="userInfoFormatter"
                        class="title">用户信息
                    </th>

                    <th data-field="username" data-width="15%" data-align="left"
                        data-formatter="orderDetailInfoFormatter"
                        class="title">订单明细
                    </th>
                    <th data-field="agentname" data-width="5%" data-align="center"
                        class="title">所属代理
                    </th>

                    <th data-field="staffname" data-width="5%" data-align="center"
                        class="title">所属员工
                    </th>


<#--                    <th data-field="balance" data-width="5%" data-align="center"-->
<#--                        class="title">余额-->
<#--                    </th>-->

<#--                    <th data-field="totalWithdraw" data-width="5%" data-align="center"-->
<#--                        class="title">总提现-->
<#--                    </th>-->

<#--                    <th data-field="totalRecharge" data-width="5%" data-align="center"-->
<#--                        class="title">总充值-->
<#--                    </th>-->
<#--                    <th data-field="staffname" data-width="5%" data-align="center" class="title">所属员工</th>-->

<#--                    <th data-field="agentname"  data-width="5%" data-align="center">所属代理</th>-->

<#--                    <th data-field="userid" data-width="5%" data-align="center"-->
<#--                        class="title">用户ID-->
<#--                    </th>-->
<#--                    <th data-field="amount" data-width="5%" data-align="center"-->
<#--                        class="title">订单金额-->
<#--                    </th>-->
<#--                    <th data-field="feemoney" data-width="5%" data-align="center"-->
<#--                        class="title">手续费-->
<#--                    </th>-->
<#--                    <th data-field="" data-width="5%" data-align="center" data-formatter="actualMoneyFormatter"-->
<#--                        class="title">打款金额-->
<#--                    </th>-->

<#--                    <th data-field="" data-width="10%" data-align="left"-->
<#--                        data-formatter="beneficiaryFormatter"-->
<#--                        class="title">受益人信息-->
<#--                    </th>-->

<#--                    <th data-field="createtime" data-width="10%" data-align="center"-->
<#--                        class="title">创建时间-->
<#--                    </th>-->
                    <th data-field="no" data-width="8%" data-align="center" data-formatter="actionFormatter"
                        class="title">操作
                    </th>
<#--                    <th data-field="id" data-formatter="actionFormatter" data-width="10%" data-align="center">操作</th>-->
                </tr>
                </thead>
            </table>
        </div>
    </div>
</div>

<#include "../../common/delete_form.ftl">
<#include "./user_withdraw_auth.ftl">
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
            if(day>7){
                layTime.hint('最多选择7天');
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
        date2.setDate(date1.getDate()-7);
        var time2 = date2.getFullYear()+"-"+(date2.getMonth()+1)+"-"+date2.getDate();
        var date = time2 + " - " + time1;
        $('input[name="time"]').val(date);
    }


    /**
     * 操作栏的格式化
     */
    function actionFormatter(value, row, index) {
        var result = "";

        result += '<a href="javascript:;" class="table-btn auditOrder" data-no="' + row.no + '" title="审核">审核</a>';
        result += '<a href="javascript:;" class="table-btn detailUserInfo" data-username="' + row.username + '" title="详情">详情</a>';
        result += '<a href="javascript:;" class="table-btn passAudit" data-no="' + row.no + '" title="通过">通过</a>';
        result += '<a href="javascript:;" class="table-btn refuseAudit" data-no="' + row.no + '" title="修改密码">拒绝</a>';

        return result;
    }

    function actualMoneyFormatter(value, row, index) {
        var amount = row.amount;
        var feemoney = row.feemoney;
        return (Number(amount) - Number(feemoney) ).toFixed(2);
    }

    function userInfoFormatter(value, row, index)
    {
        var rs = '';
        rs += "用户名: " + row.username + "<br>";
        rs += "总充值: " + row.totalRecharge + "<br>";
        rs += "总提现: " + row.totalWithdraw + "<br>";
        rs += "用户余额: " + row.balance + "<br>";
        if(row.submitCount > 0)
        {
            var str = "<Strong style='color: red'>" + row.submitCount + "</Strong>";
            rs += "提交次数: " + str + "<br>";
        }

        var jsonObj = JSON.parse(row.remark);
        if(jsonObj)
        {
            var type = jsonObj.type;
            rs += "通道类型: " + jsonObj.type + "<br>";

            if(type == "Coin" || type == "Fiat2StableCoin")
            {
                rs += "所属网络: " + jsonObj.ifsc + " | " + jsonObj.currencyType + "<br>";
                rs += "受益地址: " + jsonObj.account + "<br>";
                // rs += "代币地址: " + jsonObj.currencyCtrAddress + "<br>";

                if(jsonObj.transferAmount)
                {
                    rs += "转币数量: " + jsonObj.transferAmount + "<br>";
                }
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
                }
                else{
                    rs += "ID: " + jsonObj.idcard + "<br>";
                }

                if(jsonObj.transferAmount)
                {

                    rs += "实际转账金额: " + jsonObj.transferAmount + " ( " + jsonObj.currencyType + " )<br>";
                }

            }

        }
        return rs;
    }

    function orderDetailInfoFormatter(value, row, index)
    {
        var amount = row.amount;
        var feemoney = row.feemoney;
        var actualMoney = (Number(amount) - Number(feemoney) ).toFixed(6);

        var rs = '';
        rs += "订单金额: " + row.amount + "<br>";
        rs += "打款金额: " + actualMoney + "<br>";
        rs += "手续费用: " + row.feemoney + "<br>";
        rs += "订单状态: " + row.status + "<br>";
        rs += "创建时间: " + row.createtime + "<br>";

        var msg = columnFormatterForOrderRemarkMsg(value, row, index);
        if(!isEmpty(msg) && msg.length > 0)
        {
            var str = "<Strong style='color: red'>" + msg + "</Strong>";
            rs += "备注信息: " + str + "<br>";
        }

        rs += "系统订单号: " + row.no + "<br>";
        if(!isEmpty(row.outTradeNo))
        {
            rs += "外部订单号: " + row.outTradeNo + "<br>";
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

    $(function () {

        resetTime();
        var options = TableUtils.getBtDefaultOptions();
        debugger
        options.url = '/alibaba888/Liv2sky3soLa93vEr62/getAuditUserWithdrawList';
        options.search = false;
        options.showRefresh = true;
        options.clickToSelect= true;
        options.singleSelect= false;//是否单选，false表示多选;true标识只能单选
            // options.striped= true;
            options.queryParams = function (params) {
            params.time = $('input[name="time"]').val();
            params.username = $('input[name="username"]').val();
            params.agentname = $('input[name="agentname"]').val();
            params.staffname = $('input[name="staffname"]').val();
            params.parentname = $('input[name="parentname"]').val();
            params.grantname = $('input[name="grantname"]').val();

            params.systemOrderno = $('input[name="systemOrderno"]').val();
            params.type = $('select[name="type"]').val();
            params.txStatus = $('select[name="txStatus"]').val();
            return params;
        };
        var bootstrapTable = $('#bootstrapTable').bootstrapTable(options);

        $(document).on('click', '#create-btn', function () {
            window.location.href = '/alibaba888/Liv2sky3soLa93vEr62/toApplyPlatformSupplyPage';
        });

        $(document).on('click', '#withdraw-auth-btn', function () {
            showWithdrawAuthModal();
        });

        $(document).on('click', '.table-btn.detailUserInfo', function () {
            var username = $(this).attr('data-username');
            var url = '/alibaba888/Liv2sky3soLa93vEr62/passport/user/history_detail/page?username=' + username;
            openNewWindow(url);
        });

        $(document).on('click', '.table-btn.passAudit', function () {
            var no = $(this).attr('data-no');
            $('#myModalDel #id').val(no);
            $('#myModalDel #type').attr('action', 'pass');
            $('#myModalDel #tips').text('确定审核通过吗？');
            $('#myModalDel').modal();
        });

        $(document).on('click', '.table-btn.refuseAudit', function () {
            var no = $(this).attr('data-no');
            $('#myModalDel #id').val(no);
            $('#myModalDel #type').attr('action', 'refuse');
            $('#myModalDel #tips').text('确定审核拒绝吗？');
            $('#myModalDel').modal();
        });

        $('#delete_submit').click(function () {
            var no = $('#myModalDel #id').val();
            var value = $('#myModalDel #type').val();
            var action = $('#myModalDel #type').attr('action');

            auditWithdrawOrder(no, action);
        });

        $('#search-btn').click(function () {
            refresh();
        });

        $('#reset-btn').click(function () {
            $('input[name="username"]').val('');
            resetTime();
            //refresh();
        });

        $(document).on('click', '.table-btn.auditOrder', function () {
            var no = $(this).attr('data-no');
            var url = '/alibaba888/Liv2sky3soLa93vEr62/root_passport_user_withdraw_order_audit_waiting_result_page?orderno=' + no;
            openNewWindow(url);
        });


        $('#passAudit-btn').click(function () {
            var selected = $('#bootstrapTable').bootstrapTable('getSelections');
            //返回值为数据对象数组
            if(selected&&selected.length>0){
                //非空数组时候进行的操作
                var slength=selected.length
                $.each(selected,function(index,item){
                    $.ajax({
                        url: '/alibaba888/Liv2sky3soLa93vEr62/doAuditUserWithdrawOrder',
                        type: 'post',
                        dataType: 'json',
                        data: {orderno:item.no,action:"pass"},
                        success: function (result) {
                            if (result.code === 200) {
                                if(slength==index+1){
                                    $.global.openSuccessMsg(result.msg, function(){
                                        refresh();
                                    });
                                }
                                return;
                            }
                            $.global.openErrorMsg(result.msg);
                        },
                        error: function () {
                            $.global.openErrorMsgCollback('系统异常,操作失败!', function () {

                            });
                        }
                    });
                })
            }else{
                //空数组的操作
                $.global.openErrorMsg('请先选择要操作的数据!');

            }

        });


        $('#refuseAudit-btn').click(function () {
            var selected = $('#bootstrapTable').bootstrapTable('getSelections');
            //返回值为数据对象数组
            if(selected&&selected.length>0){
                //非空数组时候进行的操作
                var slength=selected.length
                $.each(selected,function(index,item){
                    $.ajax({
                        url: '/alibaba888/Liv2sky3soLa93vEr62/doAuditUserWithdrawOrder',
                        type: 'post',
                        dataType: 'json',
                        data: {orderno:item.no,action:"refuse"},
                        success: function (result) {
                            if (result.code === 200) {
                                if(slength==index+1){
                                    $.global.openSuccessMsg(result.msg, function(){
                                        refresh();
                                    });
                                }
                                // $.global.openSuccessMsg(result.msg, function(){
                                //     refresh();
                                // });
                                return;
                            }
                            $.global.openErrorMsg(result.msg);
                        },
                        error: function () {
                            $.global.openErrorMsgCollback('系统异常,操作失败!', function () {

                            });
                        }
                    });
                })
            }else{
                //空数组的操作
                $.global.openErrorMsg('请先选择要操作的数据!');
            }

        });



    });

    function refresh() {
        $('#bootstrapTable').bootstrapTable('refresh');
    }

    function auditWithdrawOrder(orderno, action) {
        $.ajax({
            url: '/alibaba888/Liv2sky3soLa93vEr62/doAuditUserWithdrawOrder',
            type: 'post',
            dataType: 'json',
            data: {orderno:orderno,action:action},
            success: function (result) {
                if (result.code === 200) {
                    $.global.openSuccessMsg(result.msg, function(){
                        refresh();
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
    }



</script>
</body>
</html>
