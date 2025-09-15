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
            <h5>期号管理</h5>
        </div>

        <div class="ibox-content">

            <div class="btn-group hidden-xs" id="bootstrapTableToolBar" role="group">

<#--                <div class="pull-left" style="margin-right: 10px;">-->
<#--                    <input type="text" name="time" id="time" class="form-control input-outline"  placeholder="请选择时间" style="width:200px;">-->
<#--                </div>-->

                <div class="pull-left" style="margin-right: 10px;">
                    <input type="text" name="fromTime" id="fromTime" class="form-control input-outline"  placeholder="开始时间" style="width:175px;">
                </div>

                <div class="pull-left" style="margin-right: 10px;">
                    <input type="text" name="toTime" id="toTime" class="form-control input-outline"  placeholder="结束时间" style="width:175px;">
                </div>


                <div class="pull-left" style="margin-right: 10px;">
                    <input type="text" name="issue" id="issue" class="form-control input-outline"  placeholder="请输入期号" style="width:150px;">
                </div>

                <div class="pull-left" style="margin-right: 10px;">
                    <select class="form-control" name="type" id="type" >
                        <#list gameList as game>
                            <option value="${game.key}" >${game.getShowLotteryType()}</option>
                        </#list>
                    </select>
                </div>

                <div class="pull-left" style="margin-right: 10px;">
                    <select class="form-control" name="status" >
                        <option value="" >---请选择状态---</option>
                        <option value="pending" >pending</option>
                        <option value="waiting" >waiting</option>
                        <option value="finish" >finish</option>
                    </select>
                </div>


                <div class="pull-left" style="margin-right: 10px;">
                <button id="search-btn" type="button" class="btn btn-outline btn-default" title="查询">
                    <i class="glyphicon glyphicon-search" aria-hidden="true"></i> 查询
                </button>
                <button id="reset-btn" type="button" class="btn btn-outline btn-default" title="重置">
                    <i class="glyphicon glyphicon-repeat" aria-hidden="true"></i> 重置
                </button>
                </div>

                <div class="pull-left" style="margin-right: 10px;">
                    <button id="batchOpenResult-btn" type="button" class="btn btn-outline btn-default" title="批量生成开奖结果">
                        <i class="glyphicon glyphicon-plus" aria-hidden="true"></i> 批量生成开奖结果
                    </button>
                </div>
            </div>
            <table id="bootstrapTable">
                <thead>
                <tr>
                    <th data-field="issue" data-width="8%" data-align="center"
                        class="title">期号
                    </th>

                    <th data-field="totalBetAmount" data-width="8%" data-align="center"
                        class="title">投注总额
                    </th>

                    <th data-field="totalWinAmount" data-width="5%" data-align="center"
                        class="title">中奖总额
                    </th>

                    <th data-field="totalFeemoney" data-width="5%" data-align="center"
                        class="title">手续费总额
                    </th>

                    <th data-field="" data-width="5%" data-align="center" data-formatter="platformProfitFormatter"
                        class="title">平台盈利
                    </th>

<#--                    <th data-field="totalFeemoney" data-width="5%" data-align="center"-->
<#--                        class="title">手续费-->
<#--                    </th>-->

                    <th data-field="totalBetCount" data-width="8%" data-align="center"
                        class="title">合计订单数
                    </th>

                    <th data-field="totalWinCount" data-width="5%" data-align="center"
                        class="title">中奖订单数
                    </th>

                    <th data-field="referencePrice" data-width="5%" data-align="center"
                        class="title">参考价格
                    </th>

                    <th data-field="openResult" data-width="5%" data-align="center"
                        data-formatter="openResultFormatter"
                        class="title">开奖数字
                    </th>

                    <th data-field="openMode" data-width="5%" data-align="center"
                        class="title">开奖方式
                    </th>

                    <th data-field="status" data-width="5%" data-align="center"
                        class="title">状态
                    </th>

                    <th data-field="starttime" data-width="12%" data-align="center"
                        class="title">开盘时间
                    </th>

                    <th data-field="endtime" data-width="12%" data-align="center"
                        class="title">封盘时间
                    </th>

                    <th data-field="id" data-formatter="actionFormatter" data-width="20%" data-align="center">操作</th>
                </tr>
                </thead>
            </table>
        </div>
    </div>
</div>


<#include "game_lottery_rg_period_list_preset_openresult.ftl">
<#include "game_lottery_rg_period_list_batch_preset_openresult.ftl">
<#include "../../common/delete_form.ftl">
<script type="text/javascript" src="${STATIC_URL}/plugins/bootstrap-table/bootstrap-table.min.js"></script>
<script type="text/javascript" src="${STATIC_URL}/plugins/bootstrap-table/locale/bootstrap-table-zh-CN.min.js"></script>
<script type="text/javascript">
    //对获取的日期格式化的方法
    Date.prototype.Format = function (fmt) {
        var o = {
            "M+": this.getMonth() + 1, //月份
            "d+": this.getDate(), //日
            "H+": this.getHours(), //小时
            "m+": this.getMinutes(), //分
            "s+": this.getSeconds(), //秒
            "q+": Math.floor((this.getMonth() + 3) / 3), //季度
            "S": this.getMilliseconds() //毫秒
        };
        if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
        for (var k in o)
            if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
        return fmt;
    }

    lay('#version').html('-v'+ laydate.v);
    //时间选择器
    laydate.render({
        elem: '#fromTime'
        ,type: 'datetime'
    });
    laydate.render({
        elem: '#toTime'
        ,type: 'datetime'
    });


    layTime = laydate.render({
        elem: '#time',
        //type: 'date',
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
        date2.setDate(date1.getDate()-1);
        var time2 = date2.getFullYear()+"-"+(date2.getMonth()+1)+"-"+date2.getDate();
        var date = time2 + " - " + time1;
        $('input[name="time"]').val(date);


        var date3 = new Date(date1);
        date3.setDate(date1.getDate()+1);
        var time3 = date3.getFullYear()+"-"+(date3.getMonth()+1)+"-"+date3.getDate()+" "+"23:59:59";

        var nowtime =new Date(new Date().getTime() - (2 * 60+40) * 60 * 1000).Format("yyyy-MM-dd HH:mm:ss");

        //var nowtime = new Date().Format("yyyy-MM-dd HH:mm:ss");
        $('input[name="fromTime"]').val(nowtime);
        $('input[name="toTime"]').val(time3);
    }

    /**
     * 操作栏的格式化
     */
    function actionFormatter(value, row, index) {
        var result = "";

        result += '<a href="javascript:;" class="table-btn reSettleOrder" data-no="' + row.issue + '" data-status="enable" title="重新结算">重新结算</a>';
        if(row.status == 'pending')
        {
            result += '<a href="javascript:;" class="table-btn presetOpenResult" data-no="' + row.issue + '" data-status="disable" title="预设开奖">预设开奖</a>';
        }
        return result;
    }

    function platformProfitFormatter(value, row, index) {
        return (row.totalBetAmount - row.totalWinAmount).toFixed(2);
    }


    function dateFormatter(value, row, index) {
        if (null == value) {
            return "";
        } else {
            return DateUtils.formatyyyyMMddHHmmss(value);
        }
    }

    function openResultFormatter(value, row, index) {
        if (value == -1) {
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
        options.url = '/alibaba888/Liv2sky3soLa93vEr62/getLotteryPeriodList';
        options.search = false;
        options.showRefresh = true;
        options.pageSize = 15;
        options.queryParams = function (params) {
            params.time = $('input[name="time"]').val();
            params.type = $('select[name="type"]').val();
            params.status = $('select[name="status"]').val();
            params.issue = $('input[name="issue"]').val();

            params.fromTime = $('input[name="fromTime"]').val();
            params.toTime = $('input[name="toTime"]').val();

            return params;
        };
        var bootstrapTable = $('#bootstrapTable').bootstrapTable(options);

        $('#type').change(function(){
            refresh();
        });

        $(document).on('click', '.table-btn.presetOpenResult', function () {
            var no = $(this).attr('data-no');
            $('#presetOpenResultIssue').prop("value", no);
            $('#openResult').prop("value", '');
            loadPresetOpenResult(no);
        });

        $('#batchOpenResult-btn').click(function () {
            loadBatchPresetOpenResult();
        });

        $(document).on('click', '.table-btn.reSettleOrder', function () {
            var no = $(this).attr('data-no');
            $('#myModalDel #id').val(no);
            $('#myModalDel #type').attr('action', 'reSettleOrder');
            $('#myModalDel #tips').text('确定重新结算吗？');
            $('#myModalDel').modal();
        });

        $('#delete_submit').click(function () {
            var no = $('#myModalDel #id').val();
            var value = $('#myModalDel #type').val();
            var action = $('#myModalDel #type').attr('action');

            // console.log("action = " + action);

            if("reSettleOrder" == action)
            {
                reSettleAllLotteryOrder(no)
            }
        });

        $('#search-btn').click(function () {
            // if (isEmpty($('input[name="time"]').val())){
            //     $.global.openErrorMsg('请选择时间范围');
            //     return;
            // }
            if (isEmpty($('input[name="fromTime"]').val())){
                $.global.openErrorMsg('请选择开始时间');
                return;
            }
            if (isEmpty($('input[name="toTime"]').val())){
                $.global.openErrorMsg('请选择结束时间');
                return;
            }

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
    }

    function reSettleAllLotteryOrder(issue) {
        $.ajax({
            url: '/alibaba888/Liv2sky3soLa93vEr62/reSettleAllLotteryOrder',
            type: 'post',
            dataType: 'json',
            data: {
                issue:issue,
            },
            success: function (result) {
                if (result.code === 200) {
                    $.global.openSuccessMsg(result.msg, function(){
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
