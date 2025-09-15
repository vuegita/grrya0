<!DOCTYPE HTML>
<html>
<head>
    <#include "../../common/head-meta.ftl"/>
    <title>${projectName}后台管理系统-订单管理</title>
    <link type="text/css" rel="stylesheet" href="${STATIC_URL}/plugins/bootstrap-table/bootstrap-table.min.css"/>

    <style>
        .hiddenDetailInfo{display:none;}
    </style>
</head>

<body class="gray-bg">
<div class="wrapper wrapper-content animated fadeInRight">
    <div class="ibox">
        <div class="ibox-title">
            <h5>当期管理</h5>

            <p style="color: blue"> 　</p>
            <p  style="color: green">开奖请在封盘时间内的最后5秒之前处理</p>
            <p  style="color: green">开奖请在倒计时50秒后设置</p>
        </div>

        <div class="ibox-content">

            <div class="btn-group hidden-xs" id="bootstrapTableToolBar" role="group">

                <div class="pull-left" style="margin-right: 10px;">
                    <select class="form-control" name="type" id="type" >
                        <#list gameList as game>
                            <option value="${game.key}" >${game.getShowLotteryType()}</option>
                        </#list>
                    </select>
                </div>

                <div class="pull-left" style="margin-right: 10px;">
                    <input type="hidden" name="currentIssue" id="currentIssue" class="form-control input-outline" readonly style="width:200px;">
                    <input type="text" name="issueText" id="issueText" class="form-control input-outline"  placeholder="期号: " readonly style="width:200px;">
                </div>


                <div class="pull-left" style="margin-right: 10px;">
                    <input type="text" name="totalBetAmount" id="totalBetAmount" class="form-control input-outline"  placeholder="投注总额: " readonly style="width:200px;">
                </div>

                <div class="pull-left" style="margin-right: 10px;">
                    <input type="text" name="openResult" id="openResult" class="form-control input-outline"  placeholder="开奖结果: " readonly style="width:200px;">
                </div>


                <div class="pull-left" style="margin-right: 10px;">
                    <input type="text" name="countdown" id="countdown" class="form-control input-outline"  placeholder="倒计时: " readonly style="width:200px;">
                </div>

                <button id="search-btn" type="button" class="btn btn-outline btn-default" title="刷新">
                    <i class="glyphicon glyphicon-refresh" aria-hidden="true"></i> 刷新
                </button>

            </div>
            <table id="bootstrapTable">
                <thead>
                <tr>
<#--                    <th data-field="issue" data-width="8%" data-align="center"-->
<#--                        class="title">期号-->
<#--                    </th>-->

                    <th data-field="openResult" data-width="5%" data-align="center"
                        class="title">开奖选项
                    </th>

                    <th data-field="totalBetAmount" data-width="8%" data-align="center" data-formatter="columnFormatterForMoney"
                        class="title">投注总额
                    </th>

                    <th data-field="totalWinAmount" data-width="5%" data-align="center" data-formatter="columnFormatterForMoney"
                        class="title">中奖总额
                    </th>

                    <th data-field="totalFeemoney" data-width="5%" data-align="center" data-formatter="columnFormatterForMoney"
                        class="title">手续费总额
                    </th>

                    <th data-field="platformProfit"  data-width="5%" data-align="center" data-formatter="platformProfitFormatter"
                        class="title">平台盈利
                    </th>

                    <th data-field="betBetOrderCount" data-width="8%" data-align="center"
                        class="title">合计订单数
                    </th>

                    <th data-field="id" data-formatter="actionFormatter" data-width="5%" data-align="center">操作</th>
                </tr>
                </thead>
            </table>
        </div>
    </div>
</div>


<#include "./game_andar_bahar_period_list_preset_openresult.ftl">
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
            if(day>7){
                layTime.hint('最多选择7天');
            }
        }
    });

    // 倒计时
    var countdownSeconds = 0;
    var countdownTimer = null;
    // 每秒执行一次
    function updateCountdownTimer()
    {
        console.log("========countdownSeconds = " + countdownSeconds);
        if(countdownTimer != null)
        {
            window.clearInterval(countdownTimer);
        }
        $("#countdown").prop("value", "倒计时: " + formatSeconds(countdownSeconds));
        countdownTimer = window.setInterval(() => {
            countdownSeconds --;
            if(countdownSeconds <= 0)
            {
                countdownSeconds = 0;
                window.clearInterval(countdownTimer);
            }

            $("#countdown").prop("value", "倒计时: " + formatSeconds(countdownSeconds) );
            if(countdownSeconds == 0)
            {
                refresh();
            }
        }, 1000);
    }

    function formatSeconds(value) {
        var theTime = parseInt(value);// 秒
        var middle= 0;// 分
        var hour= 0;// 小时

        if(theTime > 60) {
            middle= parseInt(theTime/60);
            theTime = parseInt(theTime%60);
            if(middle> 60) {
                hour= parseInt(middle/60);
                middle= parseInt(middle%60);
            }
        }
        var result = ":"+parseInt(theTime);
        if(parseInt(theTime)<10){
            result= ":0"+parseInt(theTime);
        }
        if(parseInt(theTime)==0){
            result= ":00";
        }

        if(middle > 0 && parseInt(middle)>10) {
            result = parseInt(middle)+result;
        }
        if(parseInt(middle)<10  && parseInt(middle)>0){
            result= "0"+parseInt(middle)+result;
        }
        if(parseInt(middle)==0){
            result= "00"+result;
        }
        if(hour> 0) {
            result = parseInt(hour)+result;
        }
        return result;
    }



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
        date2.setDate(date1.getDate()-1);
        var time2 = date2.getFullYear()+"-"+(date2.getMonth()+1)+"-"+date2.getDate();
        var date = time2 + " - " + time1;
        $('input[name="time"]').val(date);
    }

    /**
     * 操作栏的格式化
     */
    function actionFormatter(value, row, index) {
        var result = "";

        var openResult = row.openResult;
        if(openResult == "Red" || openResult == "Green" || openResult == "Violet")
        {
            return "";
        }
        else
        {
            result = '<a href="javascript:;" class="table-btn presetOpenResult" data-openResult="' + openResult + '" title="开">开</a>';
        }
        return result;
    }

    function platformProfitFormatter(value, row, index) {
        var openResult = row.openResult;
        return row.platformProfit.toFixed(2);
    }

    function totalWinAmountFormatter(value, row, index) {
        return value.toFixed(2);
    }


    function dateFormatter(value, row, index) {
        if (null == value) {
            return "";
        } else {
            return DateUtils.formatyyyyMMddHHmmss(value);
        }
    }

    function openResultFormatter(value, row, index) {
        if (row.status != 'finish') {
            return "-";
        } else {
            return value;
        }
    }

    function statusFormatter(value, row, index) {
        if(value == 'enable')
        {
            return "启用"
        }
        if(value == 'disable')
        {
            return "禁用"
        }
        return "冻结";
    }


    $(function () {
        resetTime();

        var options = TableUtils.getBtDefaultOptions();
        options.url = '/alibaba888/Liv2sky3soLa93vEr62/getCurrentGameABPeriodRunningReportList';
        options.search = false;
        options.showRefresh = false;
        options.pageSize = 15;
        options.paginationDetailHAlign = ' hiddenDetailInfo';
        options.queryParams = function (params) {
            params.time = $('input[name="time"]').val();
            params.type = $('select[name="type"]').val();

            return params;
        };
        var bootstrapTable = $('#bootstrapTable').bootstrapTable(options);

        loadGameRunningStatus();

        $('#type').change(function(){
            refresh();
        });

        $(document).on('click', '.table-btn.presetOpenResult', function () {
            var openResult = $(this).attr('data-openResult');

            $('#myModalDel #id').val(openResult);
            $('#myModalDel #type').attr('action', 'presetOpenResult');
            $('#myModalDel #tips').text('确定开 ' + openResult + ' ？');
            $('#myModalDel').modal();
        });

        $('#delete_submit').click(function () {
            var openResult = $('#myModalDel #id').val();
            updateOpenResult(openResult);
        });


        $('#search-btn').click(function () {
            refresh();
        });

        $('#reset-btn').click(function () {
            $('select[name="status"]').val('');
            $('input[name="issue"]').val('');
            resetTime();
        });

    });

    function refresh() {
        $('#bootstrapTable').bootstrapTable('refresh');
        loadGameRunningStatus();
    }

    function loadGameRunningStatus() {
        $.ajax({
            url: '/alibaba888/Liv2sky3soLa93vEr62/getGameABLotteryStatus',
            type: 'post',
            dataType: 'json',
            data: {
                lotteryType:$('select[name="type"]').val(),
            },
            success: function (result) {
                if (result.code === 200) {

                    var issue = result.data.issue;
                    var totalBetAmount = result.data.totalBetAmount;
                    var openResult =result.data.openResult;

                    countdownSeconds = result.data.countdownSeconds;

                    $("#issueText").prop("value", "期号: " + issue);
                    $("#currentIssue").prop("value", issue);
                    $("#totalBetAmount").prop("value", "投注总额: " + totalBetAmount);
                    $("#openResult").prop("value", "开奖结果: " + openResult);

                    updateCountdownTimer();
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

    function updateOpenResult(openResult) {

        var issue = $('input[name="currentIssue"]').val();

        $.ajax({
            url: '/alibaba888/Liv2sky3soLa93vEr62/updateLotteryCurrentABOpenResult',
            type: 'post',
            dataType: 'json',
            data: {
                issue:issue,
                openResult:openResult
            },
            success: function (result) {
                if (result.code === 200) {
                    $.global.openSuccessMsg(result.msg, function(){
                    });
                    refresh();
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
