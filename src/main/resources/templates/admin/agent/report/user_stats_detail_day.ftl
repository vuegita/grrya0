<!DOCTYPE HTML>
<html>
<head>
    <#include "../../../common/head-meta.ftl"/>
    <title>${projectName}后台管理系统-订单管理</title>
    <link type="text/css" rel="stylesheet" href="${STATIC_URL}/plugins/bootstrap-table/bootstrap-table.min.css"/>
</head>

<body class="gray-bg">
<div class="wrapper wrapper-content animated fadeInRight">
    <div class="ibox">
        <div class="ibox-title">
            <h5>会员每是详细统计</h5>

            <p style="color: blue"> 　</p>
<#--            <p  style="color: green">1. 会员数据已累加到会员中</p>-->
<#--            <p  style="color: green">2. 查看会员总余额，数据来自后端每10分钟统计一次, 非实时数据!</p>-->
        </div>

        <div class="ibox-content">
            <div class="btn-group hidden-xs" id="bootstrapTableToolBar" role="group">

                <div class="pull-left" style="margin-right: 10px;">
                    <input type="text" name="time" id="time" class="form-control input-outline"  placeholder="请选择创建时间" style="width:200px;">
                </div>

                <div class="pull-left" style="margin-right: 10px;">
                    <input type="text" name="agentname" id="agentname" class="form-control input-outline" placeholder="请输入代理名">
                </div>

                <div class="pull-left" style="margin-right: 10px;">
                    <input type="text" name="staffname" id="staffname" class="form-control input-outline" placeholder="请输入员工名">
                </div>

                <div class="pull-left" style="margin-right: 10px;">
                    <input type="text" name="username" id="username" class="form-control input-outline" placeholder="请输入用户名">
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

                    <th data-field="agentname" data-width="8%" data-align="center"
                        class="title">代理名
                    </th>

                    <th data-field="staffname" data-width="8%" data-align="center"
                        class="title">员工名
                    </th>

                    <th data-field="username" data-width="8%" data-align="center"
                        class="title">用户名
                    </th>


                    <th data-field="totalLv1Count" data-width="5%" data-align="center"
                        class="title">Lv1邀请总数
                    </th>

                    <th data-field="totalLv2Count" data-width="5%" data-align="center"
                        class="title">Lv2邀请总数
                    </th>

                    <th data-field="validLv1Count" data-width="5%" data-align="center"
                        class="title">Lv1有效邀请
                    </th>

                    <th data-field="validLv2Count" data-width="5%" data-align="center"
                        class="title">Lv2有效邀请
                    </th>

                    <th data-field="tradeLv1Volumn" data-width="5%" data-align="center"
                        class="title">Lv1流水总额
                    </th>

                    <th data-field="tradeLv2Volumn" data-width="5%" data-align="center"
                        class="title">Lv2流水总额
                    </th>

                    <th data-field="tradeAmountBig" data-width="5%" data-align="center" data-formatter="columnFormatterForMoney"
                        class="title">大
                    </th>

                    <th data-field="tradeAmountSmall" data-width="5%" data-align="center" data-formatter="columnFormatterForMoney"
                        class="title">小
                    </th>

                    <th data-field="tradeAmountOdd" data-width="5%" data-align="center" data-formatter="columnFormatterForMoney"
                        class="title">单
                    </th>

                    <th data-field="tradeAmountEven" data-width="5%" data-align="center" data-formatter="columnFormatterForMoney"
                        class="title">双
                    </th>

                    <th data-field="tradeAmountNumber" data-width="5%" data-align="center"  data-formatter="columnFormatterForMoney"
                        class="title">数字
                    </th>

<#--                    <th data-field="" data-width="5%" data-align="center" data-formatter="platformProfitDataFormatter"-->
<#--                        class="title">平台盈亏-->
<#--                    </th>-->
                    <th data-field="id" data-formatter="actionFormatter3" data-width="10%" data-align="center">操作</th>
                </tr>
                </thead>
            </table>
        </div>
    </div>
</div>
<!-- loading -->
<#--<div class="modal fade" id="loading" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" data-backdrop='static'>-->
<#--    <div class="modal-dialog" role="document">-->
<#--        <div class="modal-content">-->
<#--            <div class="modal-header">-->
<#--                <h4 class="modal-title" id="myModalLabel">提示</h4>-->
<#--            </div>-->
<#--            <div class="modal-body">-->
<#--                <span id="result">会员余额:</span> <span class="head-time-s1">0</span>-->
<#--            </div>-->
<#--        </div>-->
<#--    </div>-->
<#--</div>-->

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
        date2.setDate(date1.getDate()-7);
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

    $(function () {
        resetTime();

        var options = TableUtils.getBtDefaultOptions();
        options.url = '/alibaba888/agent/root_report_day_user_stats_detail_v2/getDataList';
        options.search = false;
        options.showRefresh = true;
        /**options.showFooter=true;**/
        options.queryParams = function (params) {
            params.username = $('input[name="username"]').val();
            params.agentname = $('input[name="agentname"]').val();
            params.staffname = $('input[name="staffname"]').val();
            params.time = $('input[name="time"]').val();
            params.userType = "staff";
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
            $('input[name="username"]').val('');
            resetTime();
        });

    });

    function refresh() {
        $('#bootstrapTable').bootstrapTable('refresh');
    }

    /**
     * 操作栏的格式化
     */
    function actionFormatter3(value, row, index) {
        var result = "";

        result += '<a href="javascript:;" class="table-btn detailUserInfo" data-username="' + row.username + '" title="详情">详情</a>';
        // result += '<a href="javascript:;" class="table-btn detailUserInfo" data-username="' + row.username + '" title="查看会员余额">查看会员余额</a>';
        return result;
    }

    $(document).on('click', '.table-btn.detailUserInfo', function () {
        var username = $(this).attr('data-username');
        var url = '/alibaba888/agent/passport/user/history_detail/page?username=' + username;
        // window.location.href = url;
        openNewWindow(url);
    });



</script>
</body>
</html>

