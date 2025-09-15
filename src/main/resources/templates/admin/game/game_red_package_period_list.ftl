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
            <h5>红包列表</h5>
        </div>

        <div class="ibox-content">

            <div class="btn-group hidden-xs" id="bootstrapTableToolBar" role="group">

                <div class="pull-left" style="margin-right: 10px;">
                    <button id="create-btn" type="button" class="btn btn-outline btn-default" title="新增">
                        <i class="glyphicon glyphicon-plus" aria-hidden="true"></i> 新增
                    </button>
                </div>

                <div class="pull-left" style="margin-right: 10px;">
                    <input type="text" name="time" id="time" class="form-control input-outline"  placeholder="请选择时间" style="width:200px;">
                </div>

                <div class="pull-left" style="margin-right: 10px;">
                    <input type="text" name="issue" id="issue" class="form-control input-outline"  placeholder="请输入红包ID" style="width:150px;">
                </div>

<#--                <div class="pull-left" style="margin-right: 10px;">-->
<#--                    <input type="text" name="username" id="username" class="form-control input-outline"  placeholder="请输入用户名" style="width:150px;">-->
<#--                </div>-->

<#--                <div class="pull-left" style="margin-right: 10px;">-->
<#--                    <select class="form-control" name="type" id="type" >-->
<#--                        <#list gameList as game>-->
<#--                            <option value="${game.key}" >${game.getShowLotteryType()}</option>-->
<#--                        </#list>-->
<#--                    </select>-->
<#--                </div>-->

                <div class="pull-left" style="margin-right: 10px;">
                    <select class="form-control" name="status" >
                        <option value="" >---请选择状态---</option>
                        <option value="pending" >pending</option>
                        <option value="waiting" >waiting</option>
                        <option value="finish" >finish</option>
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
                    <th data-field="id" data-width="5%" data-align="center"
                        class="title">ID
                    </th>

                    <th data-field="totalAmount" data-width="5%" data-align="center"
                        class="title">红包金额
                    </th>
                    <th data-field="totalCount" data-width="5%" data-align="center"
                        class="title">红包总数
                    </th>
                    <th data-field="rpType" data-width="5%" data-align="center"
                        class="title">红包类型
                    </th>

<#--                    <th data-field="" data-width="10%" data-align="center"-->
<#--                        data-formatter="betOverviewFormatter"-->
<#--                        class="title">投注概况-->
<#--                    </th>-->


                    <th data-field="" data-width="5%" data-align="center" data-formatter="platformProfitFormatter"
                        class="title">平台盈利
                    </th>

<#--                    <th data-field="totalFeemoney" data-width="5%" data-align="center"-->
<#--                        class="title">手续费-->
<#--                    </th>-->

<#--                    <th data-field="" data-width="8%" data-align="center"-->
<#--                        data-formatter="orderCountOverviewFormatter"-->
<#--                        class="title">订单统计-->
<#--                    </th>-->

<#--                    <th data-field="totalBetCount" data-width="8%" data-align="center"-->
<#--                        class="title">合计订单数-->
<#--                    </th>-->

<#--                    <th data-field="totalWinCount" data-width="5%" data-align="center"-->
<#--                        class="title">中奖订单数-->
<#--                    </th>-->

<#--                    <th data-field="openResult" data-width="5%" data-align="center"-->
<#--                        class="title">开奖结果-->
<#--                    </th>-->

<#--                    <th data-field="openMode" data-width="5%" data-align="center"-->
<#--                        class="title">开奖方式-->
<#--                    </th>-->

                    <th data-field="status" data-width="5%" data-align="center"
                        class="title">状态
                    </th>

                    <th data-field="createtime" data-width="12%" data-align="center"
                        class="title">创建时间
                    </th>

                    <th data-field="endtime" data-width="12%" data-align="center"
                        class="title">结束时间
                    </th>

                    <th data-field="" data-width="12%" data-align="left"
                        data-formatter="remarkFormatter"
                        class="title">备注
                    </th>

                    <th data-field="id" data-formatter="actionFormatter" data-width="20%" data-align="center">操作</th>
                </tr>
                </thead>
            </table>
        </div>
    </div>
</div>


<#include "game_lottery_rg_period_list_preset_openresult.ftl">
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
     * 投注概况
     */
    function betOverviewFormatter(value, row, index)
    {
        var result = "投注总额: " + row.totalBetAmount.toFixed(2) + "<br>";
        result += "中奖总额: " + row.totalWinAmount.toFixed(2) + "<br>";
        result += "手续费总额: " + row.totalFeemoney.toFixed(2) + "<br>";
        return result;
    }

    function orderCountOverviewFormatter(value, row, index)
    {
        var result = "合计总数: " + row.totalBetCount + "<br>";
        result += "中奖总数: " + row.totalWinCount + "<br>";
        return result;
    }

    function remarkFormatter(value, row, index)
    {
        var result = "";

        if(isEmpty(row.username))
        {
            result = "创建人: 系统" + "<br>";
        }

        try {
            var remark = row.remark;
            if(!isEmpty(remark))
            {
                var obj = JSON.parse(remark);
                result += "备注: " + obj.msg + "<br>";
            }
        }
        catch (e) {

        }

        return result;
    }

    /**
     * 操作栏的格式化
     */
    function actionFormatter(value, row, index) {
        var result = "";

        result += '<a href="javascript:;" class="table-btn reSettleOrder" data-no="' + row.id + '" data-status="enable" title="重新结算">重新结算</a>';
        result += '<a href="javascript:;" class="table-btn redPUrl"  data-issue="' + row.id + '" title="红包链接">红包链接</a> ';
        result += '<a href="javascript:;" class="table-btn redPUrl" data-type="game_btc"  data-issue="' + row.id + '" title="红包链接">BTC红包链接</a>';
        result += '<a href="javascript:;" class="table-btn redPUrl" data-type="game_v3"  data-issue="' + row.id + '" title="红包链接">红包链接-v3</a>';
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
        options.url = '/alibaba888/Liv2sky3soLa93vEr62/getGameRedPeriodList';
        options.search = false;
        options.showRefresh = true;
        options.pageSize = 15;
        options.queryParams = function (params) {
            params.time = $('input[name="time"]').val();
            params.username = $('input[name="username"]').val();
            params.type = $('select[name="type"]').val();
            params.status = $('select[name="status"]').val();
            params.issue = $('input[name="issue"]').val();

            return params;
        };
        var bootstrapTable = $('#bootstrapTable').bootstrapTable(options);

        $(document).on('click', '#create-btn', function(){
            var url = '/alibaba888/Liv2sky3soLa93vEr62/game/red_package/create/page';
            // window.location.href =;
            openNewWindow(url);
        });

        $('#type').change(function(){
            refresh();
        });

        $(document).on('click', '.table-btn.presetOpenResult', function () {
            var no = $(this).attr('data-no');
            $('#presetOpenResultIssue').prop("value", no);
            $('#openResult').prop("value", '');
            loadPresetOpenResult(no);
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
            if (isEmpty($('input[name="time"]').val())){
                $.global.openErrorMsg('请选择时间范围');
                return;
            }

            refresh();
        });

        $('#reset-btn').click(function () {
            $('select[name="status"]').val('');
            $('input[name="issue"]').val('');
            resetTime();
        });

        $(document).on('click', '.table-btn.redPUrl', function () {
            var issue = $(this).attr('data-issue');
            var gameType = $(this).attr('data-type');
            $.ajax({
                url: '/alibaba888/Liv2sky3soLa93vEr62/encryptRedPackageId',
                type: 'post',
                dataType: 'json',
                data: {
                    issue:issue,
                },
                success: function (result) {
                    if (result.code === 200) {
                        var shareUrl=window.location.host+"/mining/#/Bonus?issue="+result.data  //STATIC_URL 前端域名
                        if(gameType == "game_btc")
                        {
                            shareUrl="https://"+window.location.host+"/#/Bonus?issue="+result.data
                        }
                        else if(gameType == "game_v3")
                        {
                            shareUrl="https://" + window.location.host+"/?page=user_red_package&issue="+result.data
                        }

                        const input = document.createElement('input');
                        document.body.appendChild(input);
                        input.setAttribute('value', shareUrl);
                        input.select();
                        if (document.execCommand('copy')) {
                            document.execCommand('copy');

                            $.global.openSuccessMsg("复制成功");
                        }
                        document.body.removeChild(input);
                        return;
                    }
                    $.global.openErrorMsg(result.msg);
                },
                error: function () {
                    $.global.openErrorMsgCollback('系统异常,操作失败!', function () {
                    });
                }
            });


        });

    });

    function refresh() {
        $('#bootstrapTable').bootstrapTable('refresh');
    }

    function reSettleAllLotteryOrder(issue) {
        $.ajax({
            url: '/alibaba888/Liv2sky3soLa93vEr62/reSettleAllRedPOrder',
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
