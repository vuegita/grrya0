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
            <h5>提现记录</h5>
        </div>

        <div class="ibox-content">


            <div class="pull-left" style="margin-right: 10px;">
                <input type="text" name="time" id="time" class="form-control input-outline"  placeholder="请选择时间" style="width:200px;">
            </div>

            <div class="pull-left" style="margin-right: 10px;">
                <input type="text" name="username" id="username" class="form-control input-outline"  placeholder="请输入用户名" style="width:200px;">
            </div>

            <#if iscrypto>
                <div class="pull-left" style="margin-right: 10px;">
                    <input type="text" name="address" id="address" class="form-control input-outline"  placeholder="请输入钱包地址" style="width:200px;">
                </div>
            </#if>

            <div class="pull-left" style="margin-right: 10px;">
                <input type="text" name="staffname" id="staffname" class="form-control input-outline"  placeholder="请输入直属员工名" style="width:150px;">
            </div>

            <div class="pull-left" style="margin-right: 10px;">
                <input type="text" name="systemOrderno" id="systemOrderno" class="form-control input-outline"  placeholder="请输入系统订单号" style="width:150px;">
            </div>

            <div class="pull-left" style="margin-right: 10px;">
                <select class="form-control" name="txStatus" >
                    <option value="" >---订单状态---</option>
                    <option value="new" >new</option>
                    <option value="new" >pending</option>
                    <option value="waiting" >waiting</option>
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


            <div class="btn-group hidden-xs" id="bootstrapTableToolBar" role="group">


<#--                <div class="pull-left" style="margin-right: 10px;">-->
<#--                    <button id="create-btn" type="button" class="btn btn-outline btn-default" title="补单">-->
<#--                        <i class="glyphicon glyphicon-plus" aria-hidden="true"></i> 补单-->
<#--                    </button>-->
<#--                </div>-->


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
<#--                    <th data-field="username" data-width="5%" data-align="center"-->
<#--                        class="title">用户名-->
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

<#--                    <th data-field="no" data-width="5%" data-align="center"  data-formatter="beneficiaryAccountFormatter"-->
<#--                        class="title">受益人账户-->
<#--                    </th>-->

<#--                    <th data-field="" data-width="10%" data-align="left"-->
<#--                        data-formatter="beneficiaryFormatter"-->
<#--                        class="title">受益人信息-->
<#--                    </th>-->

<#--                    <th data-field="no" data-width="5%" data-align="center"  data-formatter="beneficiaryNameFormatter"-->
<#--                        class="title">受益人名称-->
<#--                    </th>-->

<#--                    <th data-field="no" data-width="5%" data-align="center"  data-formatter="beneficiaryEmailFormatter"-->
<#--                        class="title">受益人邮件-->
<#--                    </th>-->

<#--                    <th data-field="no" data-width="5%" data-align="center"  data-formatter="beneficiaryPhoneFormatter"-->
<#--                        class="title">受益人手机-->
<#--                    </th>-->

<#--                    <th data-field="no" data-width="5%" data-align="center" data-formatter="actionFormatter"-->
<#--                        class="title">-->
<#--                    </th>-->

<#--                    <th data-field="businessName" data-width="5%" data-align="center" data-formatter="businessFormatter"-->
<#--                        class="title">订单类型-->
<#--                    </th>-->
<#--                    <th data-field="type" data-width="5%" data-align="center" data-formatter="typeFormatter"-->
<#--                        class="title">订单类型-->
<#--                    </th>-->
<#--                    <th data-field="checker" data-width="5%" data-align="center"-->
<#--                        class="title">操作人-->
<#--                    </th>-->
                    <th data-field="staffname" data-width="10%" data-align="left"
                         class="title">所属员工
                    </th>
                    <th data-field="username" data-width="10%" data-align="left"
                        data-formatter="userInfoFormatter"
                        class="title">用户信息 & 受益人信息
                    </th>

                    <th data-field="username" data-width="10%" data-align="left"
                        data-formatter="orderDetailInfoFormatter"
                        class="title">订单明细
                    </th>

                    <th data-field="username" data-width="5%" data-align="center"
                        data-formatter="currencyTypeFormatter"
                        class="title">所属网络|币种
                    </th>

                    <th data-field="username" data-width="5%" data-align="center"
                        data-formatter="actualMoneyFormatter"
                        class="title">打款金额
                    </th>


                    <th data-field="status" data-width="5%" data-align="center"
                        class="title">订单状态
                    </th>

                    <th data-field="" data-width="5%" data-align="center" data-formatter="columnFormatterForOrderRemarkMsg"
                        class="title">错误原因
                    </th>
                    <th data-field="createtime" data-width="10%" data-align="center"
                        class="title">创建时间
                    </th>
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
            if(day>3){
                layTime.hint('最多选择3天');
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
    function userInfoFormatter(value, row, index)
    {
        var rs = '';
        rs += "用户名: " + row.username + "<br>";
        rs += "总充值: " + row.totalRecharge + "<br>";
        rs += "总提现: " + row.totalWithdraw + "<br>";
        rs += "用户余额: " + row.balance + "<br>";

        var jsonObj = JSON.parse(row.remark);
        if(jsonObj)
        {
            var type = jsonObj.type;
            rs += "通道类型: " + jsonObj.type + "<br>";

            if(type == "Coin" || type == "Fiat2StableCoin")
            {
                rs += "所属网络: " + jsonObj.ifsc + " | " + jsonObj.currencyType + "<br>";
                // rs += "受益地址: " + jsonObj.account + "<br>";
                rs += "受益地址: " + buildScanBrowserItem(jsonObj.ifsc, jsonObj.account, "account_scan_url");
                rs += "代币地址: " + jsonObj.currencyCtrAddress + "<br>";

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
                }else{
                    rs += "ID: " + jsonObj.idcard + "<br>";
                }
            }

        }

        return rs;
    }

    function currencyTypeFormatter(value, row, index) {
        var rs = '';
        var jsonObj = JSON.parse(row.remark);
        if(jsonObj)
        {
            var type = jsonObj.type;
            //rs += "Type: " + jsonObj.type + "<br>";

            if(type == "Coin")
            {
                rs += jsonObj.ifsc + " | " + jsonObj.currencyType;
            }
            else
            {
                rs += " - ";

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
        rs += "订单金额 : " + row.amount + "<br>";
        rs += "打款金额 : " + actualMoney + "<br>";
        rs += "手续费用 : " + row.feemoney + "<br>";
        rs += "订单状态 : " + row.status + "<br>";
        rs += "创建时间 : " + row.createtime + "<br>";
        rs += "系统订单号 : " + row.no + "<br>";

        var networkType = '';
        var jsonObj = JSON.parse(row.remark);
        if(jsonObj && jsonObj.type == "Coin")
        {
            var type = jsonObj.type;
            if(type == "Coin")
            {
                networkType = jsonObj.ifsc;
            }
        }

        if(!isEmpty(row.outTradeNo))
        {
            if(!isEmpty(networkType) && !isEmpty(row.outTradeNo) && row.outTradeNo.length >= 60)
            {
                rs += "外部订单号 : " + buildScanBrowserItem(networkType, row.outTradeNo, "transaction_scan_url");
            }
            else
            {
                rs += "外部订单号 : " + row.outTradeNo + "<br>";
            }
        }
        return rs;
    }
    function buildScanBrowserItem(networkType, key, type)
    {
        var rs = "";
        rs += '<a href="javascript:;" class="table-btn toScanBrowserPage" data-no="' + key + '" data-type="' + type +  '" data-network-type="' + networkType + '" title="通过">' + key + '</a> <br>';
        return rs;
    }

    /**
     * 操作栏的格式化
     */
    function actionFormatter(value, row, index) {
        var result = "";

       // result += '<a href="javascript:;" class="table-btn detailUserInfo" data-username="' + row.username + '" title="详情">详情</a>';
        <#if iscrypto>
        if( row.status == 'failed')
        {
            result += '<a href="javascript:;"  id="reWithdrawOrder-btn" class="table-btn reWithdrawOrder" data-no="' + row.no + '" title="重新发起提现">重新发起提现</a>';
        }
        </#if>

        return result;
    }
    $(document).on('click', '.table-btn.reWithdrawOrder', function () {
        var no = $(this).attr('data-no');
        $('#myModalDel #id').val(no);
        $('#myModalDel #type').attr('action', 'reWithdrawOrder');
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
                    $.global.openSuccessMsg("操作失败，请重试");
                }
            },
            error: function () {
                $.global.openErrorMsg('操作失败，请重试');
            }
        })

    }
    $('#delete_submit').click(function () {
        var username = $('#myModalDel #id').val();
        var value = $('#myModalDel #type').val();
        var action = $('#myModalDel #type').attr('action');


        var no =  $('#myModalDel #id').val();

       if("reWithdrawOrder" == action)
        {
            reWithdrawOrder(no)
        }

    });



    $(document).on('click', '.table-btn.detailUserInfo', function () {
        var username = $(this).attr('data-username');
        var url = '/alibaba888/Liv2sky3soLa93vEr62/passport/user/history_detail/page?username=' + username;
        // window.location.href = url;
        openNewWindow(url);
    });
    function resetTime()
    {
        var date1 = new Date();
        var time1 = date1.getFullYear()+"-"+(date1.getMonth()+1)+"-"+date1.getDate();
        var date2 = new Date(date1);
        date2.setDate(date1.getDate()-1);
        var time2 = date2.getFullYear()+"-"+(date2.getMonth()+1)+"-"+date2.getDate();
        var date = time2 + " - " + time1;
        $('input[name="time"]').val(date);
    }


    function actualMoneyFormatter(value, row, index) {
        var amount = row.amount;
        var feemoney = row.feemoney;
        return (Number(amount) - Number(feemoney) ).toFixed(2);
    }

    function beneficiaryFormatter(value, row, index) {
        var rs = '';

        var jsonObj = JSON.parse(row.remark);
        if(jsonObj)
        {
            rs += "Type: " + jsonObj.type + "<br>";
            rs += "Name: " + jsonObj.beneficiaryName + "<br>";
            rs += "IFSC: " + jsonObj.ifsc + "<br>";
            if(jsonObj.name!=undefined && jsonObj.name!=null && jsonObj.name!=""){
                rs += "bankName: " + jsonObj.name + "<br>";
            }
            rs += "Account: " + jsonObj.account + "<br>";
            rs += "Email: " + jsonObj.beneficiaryEmail + "<br>";
            rs += "Phone: " + jsonObj.beneficiaryPhone + "<br>";
            if(jsonObj.idcard==undefined || jsonObj.idcard==null ){
                rs += "ID: " + " " + "<br>";
            }else{
                rs += "ID: " + jsonObj.idcard + "<br>";
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

    $(function () {

        resetTime();

        var options = TableUtils.getBtDefaultOptions();
        options.url = '/alibaba888/agent/passport/getUserWithdrawRecordList';
        options.search = false;
        options.showRefresh = true;
        options.queryParams = function (params) {
            params.time = $('input[name="time"]').val();
            params.username = $('input[name="username"]').val();
            params.systemOrderno = $('input[name="systemOrderno"]').val();
            params.type = $('select[name="type"]').val();
            params.txStatus = $('select[name="txStatus"]').val();
            params.staffname = $('input[name="staffname"]').val();

            return params;
        };
        var bootstrapTable = $('#bootstrapTable').bootstrapTable(options);

        $('#search-btn').click(function () {
            refresh();
        });

        $('#reset-btn').click(function () {
            $('input[name="username"]').val('');
            resetTime();
            //refresh();
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

    });

    function refresh() {
        $('#bootstrapTable').bootstrapTable('refresh');
    }



</script>
</body>
</html>
