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
            <h5>结算记录</h5>
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

<#--                <div class="pull-left" style="margin-right: 10px;">-->
<#--                    <input type="text" name="username" id="username" class="form-control input-outline"  placeholder="请输入用户名" style="width:200px;">-->
<#--                </div>-->

                <div class="pull-left" style="margin-right: 10px;">
                    <input type="text" name="agentname" id="agentname" class="form-control input-outline"  placeholder="请输入代理用户名" style="width:150px;">
                </div>

                <div class="pull-left" style="margin-right: 10px;">
                    <input type="text" name="staffname" id="staffname" class="form-control input-outline"  placeholder="请输入员工名" style="width:150px;">
                </div>

<#--                <div class="pull-left" style="margin-right: 10px;">-->
<#--                    <input type="text" name="systemOrderno" id="systemOrderno" class="form-control input-outline"  placeholder="请输入系统订单号" style="width:150px;">-->
<#--                </div>-->

<#--                <div class="pull-left" style="margin-right: 10px;">-->
<#--                    <input type="text" name="beneficiaryAccount" id="beneficiaryAccount" class="form-control input-outline"  placeholder="请输入账户" style="width:150px;">-->
<#--                </div>-->

                <div class="pull-left" style="margin-right: 10px;">
                    <select class="form-control" name="currencyType" >
                        <option value="" selected>---所有币种---</option>
                        <#list cryptoCurrencyArr as item>
                            <option value="${item.getKey()}" > ${item.getKey()}</option>
                        </#list>
                    </select>
                </div>

                <div class="pull-left" style="margin-right: 10px;">
                    <select class="form-control" name="dimensionType" >
                        <option value="agent" >---代理维度---</option>
                        <option value="staff" >---员工维度---</option>
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


                    <th data-field="pdate" data-width="5%" data-align="center"
                        class="title">所属日期
                    </th>

                    <th data-field="agentname" data-width="5%" data-align="center"
                        class="title">所属代理
                    </th>

                    <th data-field="staffname" data-width="5%" data-align="center"
                        class="title">所属员工
                    </th>

                    <th data-field="currency" data-width="5%" data-align="center"
                        class="title">所属币种
                    </th>

                    <th data-field="amount" data-width="5%" data-align="center"
                        class="title">订单总额
                    </th>

                    <th data-field="feemoney" data-width="5%" data-align="center"
                        class="title">手续费用
                    </th>

<#--                    <th data-field="username" data-width="10%" data-align="left"-->
<#--                        data-formatter="userInfoFormatter"-->
<#--                        class="title">用户信息-->
<#--                    </th>-->

<#--                    <th data-field="username" data-width="10%" data-align="left"-->
<#--                        data-formatter="orderDetailInfoFormatter"-->
<#--                        class="title">金额明细-->
<#--                    </th>-->

<#--                    <th data-field="" data-width="10%" data-align="left"-->
<#--                        data-formatter="beneficiaryFormatter"-->
<#--                        class="title">受益人信息-->
<#--                    </th>-->

<#--                    <th data-field="status" data-width="5%" data-align="center"-->
<#--                        class="title">订单状态-->
<#--                    </th>-->

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
            if(day>60){
                layTime.hint('最多选择60天');
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
        date2.setDate(date1.getDate()-60);
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
        //     result += '<a href="javascript:;" class="table-btn passOrder" data-no="' + row.no + '" title="通过">通过</a>';
        //     result += '<a href="javascript:;" class="table-btn refuseOrder" data-no="' + row.no + '" title="拒绝">拒绝</a>';
        // }

        result += '<a href="javascript:;" class="table-btn toAgentSettleOrderPage" data-pdate="' + row.pdate + '" data-agentname=' + row.agentname + ' data-currency="' + row.currency + '" title="查看明细">代理维度明细</a>';
        if(row.staffname && row.staffname.length > 0)
        {
            result += ' |  <a href="javascript:;" class="table-btn toStaffSettleOrderPage" data-pdate="' + row.pdate + '" data-staffname=' + row.staffname + ' data-currency="' + row.currency + '" title="查看明细">员工维度明细</a>';
        }

        // result += '<a href="javascript:;" class="table-btn detailUserInfo" data-username="' + row.username + '" title="详情">详情</a>';
        return result;
    }
    $(document).on('click', '.table-btn.detailUserInfo', function () {
        var username = $(this).attr('data-username');
        var url = '/alibaba888/Liv2sky3soLa93vEr62/passport/user/history_detail/page?username=' + username;
        // window.location.href = url;
        openNewWindow(url);
    });

    $(document).on('click', '.table-btn.toAgentSettleOrderPage', function () {
        var pdate = $(this).attr('data-pdate');
        var agentname = $(this).attr('data-agentname');
        var currency = $(this).attr('data-currency');
        var url = '/alibaba888/Liv2sky3soLa93vEr62/root_web_settle_order?pdate=' + pdate + "&agentname=" + agentname + "&currency=" + currency;
        // window.location.href = url;
        openNewWindow(url);
    });

    $(document).on('click', '.table-btn.toStaffSettleOrderPage', function () {
        var pdate = $(this).attr('data-pdate');
        var staffname = $(this).attr('data-staffname');
        var currency = $(this).attr('data-currency');
        var url = '/alibaba888/Liv2sky3soLa93vEr62/root_web_settle_order?pdate=' + pdate + "&staffname=" + staffname + "&currency=" + currency;
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
        // rs += "用 户 名: " + row.username + "<br>";
        rs += "所属代理: " + row.agentname + "<br>";
        rs += "所属员工: " + row.staffname + "<br>";
        // rs += "受益账户: " + row.beneficiaryAccount + "<br>";
        // rs += "创建时间 : " + row.updatetime + "<br>";

        return rs;
    }

    function orderDetailInfoFormatter(value, row, index)
    {
        var rs = '';
        rs += "订单金额 : " + row.amount + "<br>";
        rs += "手续费用 : " + row.feemoney + "<br>";
        rs += "所属币种 : " + row.currency + "<br>";

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

        var options = TableUtils.getBtDefaultOptions();
        options.url = '/alibaba888/Liv2sky3soLa93vEr62/getWebSettleRecordList';
        options.search = false;
        options.showRefresh = true;
        options.queryParams = function (params) {
            params.time = $('input[name="time"]').val();
            params.username = $('input[name="username"]').val();
            params.agentname = $('input[name="agentname"]').val();
            params.staffname = $('input[name="staffname"]').val();
            params.parentname = $('input[name="parentname"]').val();
            params.grantname = $('input[name="grantname"]').val();

            params.systemOrderno = $('input[name="systemOrderno"]').val();

            params.beneficiaryAccount = $('input[name="beneficiaryAccount"]').val();
            params.beneficiaryIdcard = $('input[name="beneficiaryIdcard"]').val();

            params.currencyType = $('select[name="currencyType"]').val();
            params.dimensionType = $('select[name="dimensionType"]').val();
            params.txStatus = $('select[name="txStatus"]').val();
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


</script>
</body>
</html>
