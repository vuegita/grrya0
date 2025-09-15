<!DOCTYPE HTML>
<html>
<head>
    <#include "../../common/head-meta.ftl"/>
    <title>${projectName}后台管理系统-订单管理</title>
    <link type="text/css" rel="stylesheet" href="${STATIC_URL}/plugins/bootstrap-table/bootstrap-table.min.css"/>
</head>

<body class="gray-bg">
<div class="wrapper wrapper-content animated fadeInRight">
    <div class="ibox">
        <div class="ibox-title">
            <h5>平台每日统计</h5>

            <p style="color: blue"> 　</p>
            <p  style="color: green">平台盈亏 = 充值 - 提现</p>
        </div>

        <div class="ibox-content">
            <div class="btn-group hidden-xs" id="bootstrapTableToolBar" role="group">

                <div class="pull-left" style="margin-right: 10px;">
                    <input type="text" name="time" id="time" class="form-control input-outline"  placeholder="请选择创建时间" style="width:200px;">
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
                    <th data-field="pdate" data-width="5%" data-align="center"
                        class="title">日期
                    </th>

<#--                    <th data-field="fundKey" data-width="8%" data-align="center"-->
<#--                        class="title">资金账户-->
<#--                    </th>-->

                    <th data-field="currency" data-width="8%" data-align="center"
                        class="title">所属币种
                    </th>

                    <th data-field="recharge" data-width="5%" data-align="center" data-formatter="columnFormatterForMoney"
                        class="title">用户充值
                    </th>

                    <th data-field="withdraw" data-width="5%" data-align="center" data-formatter="columnFormatterForMoney"
                        class="title">用户提现
                    </th>

                    <th data-field="refund" data-width="5%" data-align="center" data-formatter="columnFormatterForMoney"
                        class="title">退款
                    </th>

                    <th data-field="feemoney" data-width="5%" data-align="center" data-formatter="columnFormatterForMoney"
                        class="title">提现手续费
                    </th>

                    <th data-field="platformRecharge" data-width="5%" data-align="center" data-formatter="columnFormatterForMoney"
                        class="title">平台充值
                    </th>

                    <th data-field="platformDeduct" data-width="5%" data-align="center" data-formatter="columnFormatterForMoney"
                        class="title">平台扣款
                    </th>

                    <th data-field="platformPresentation" data-width="5%" data-align="center" data-formatter="columnFormatterForMoney"
                        class="title">平台赠送
                    </th>

                    <th data-field="businessRecharge" data-width="5%" data-align="center" data-formatter="columnFormatterForMoney"
                        class="title">下单返奖
                    </th>

                    <th data-field="businessDeduct" data-width="5%" data-align="center" data-formatter="columnFormatterForMoney"
                        class="title">下单扣款
                    </th>

                    <th data-field="businessFeemoney" data-width="5%" data-align="center" data-formatter="columnFormatterForMoney"
                        class="title">下单手续费总额
                    </th>

                    <th data-field="returnWater" data-width="5%" data-align="center" data-formatter="columnFormatterForMoney"
                        class="title">返佣总额
                    </th>

                    <th data-field="" data-width="5%" data-align="center" data-formatter="platformProfitDataFormatter"
                        class="title">平台盈亏
                    </th>

                </tr>
                </thead>
            </table>
        </div>
    </div>
</div>


<#include "../../common/delete_form.ftl">
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
            if(day>31){
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
        date2.setDate(date1.getDate()-31);
        var time2 = date2.getFullYear()+"-"+(date2.getMonth()+1)+"-"+date2.getDate();
        var date = time2 + " - " + time1;
        $('input[name="time"]').val(date);
    }

    function moneyDataFormatter(value, row, index) {
        return value.toFixed(2);
    }

    function platformProfitDataFormatter(value, row, index) {
        return (row.recharge - row.withdraw + row.refund).toFixed(2);
    }

    function actionFormatter(value, row, index) {
        var result = "";

        result += '<a href="javascript:;" class="table-btn queryChildList" data-username="' + row.username + '" title="查看下级">查看下级</a>';

        return result;
    }

    $(function () {
        resetTime();

        var options = TableUtils.getBtDefaultOptions();
        options.url = '/alibaba888/Liv2sky3soLa93vEr62/getPlatformReportList';
        options.search = false;
        options.showRefresh = true;
        options.queryParams = function (params) {
            params.time = $('input[name="time"]').val();
            return params;
        };
        var bootstrapTable = $('#bootstrapTable').bootstrapTable(options);


        $('#search-btn').click(function () {
            if (isEmpty($('input[name="time"]').val())){
                $.global.openErrorMsg('请选择时间范围');
                return;
            }
            refresh();
        });

        $('#reset-btn').click(function () {
            resetTime();
        });

    });

    function refresh() {
        $('#bootstrapTable').bootstrapTable('refresh');
    }




</script>
</body>
</html>
