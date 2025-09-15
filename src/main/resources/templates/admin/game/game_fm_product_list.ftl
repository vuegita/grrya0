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
            <h5>理财产品</h5>
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
                    <input type="text" name="issue" id="issue" class="form-control input-outline"  placeholder="请输入ID" style="width:150px;">
                </div>


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
                        <option value="new" >new</option>
                        <option value="saling" >销售中</option>
                        <option value="saled" >已售磬</option>
                        <option value="realized" >已完成</option>
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

                    <th data-field="title" data-width="10%" data-align="center"
                        class="title">标题
                    </th>
<#--                    <th data-field="desc" data-width="10%" data-align="center"-->
<#--                        class="title">描述-->
<#--                    </th>-->
                    <th data-field="timeHorizon" data-width="5%" data-align="center"
                        class="title">投资期限/(天)
                    </th>

                    <th data-field="" data-width="10%" data-align="center"
                        data-formatter="returnRateFormatter"
                        class="title">收益率
                    </th>

                    <th data-field="returnRealInterest" data-width="5%" data-align="center"
                        class="title">利息支出
                    </th>

<#--                    <th data-field="" data-width="5%" data-align="center" data-formatter="platformProfitFormatter"-->
<#--                        class="title">平台盈利-->
<#--                    </th>-->

                    <th data-field="" data-width="8%" data-align="center"
                        data-formatter="saleFormatter"
                        class="title">份额
                    </th>

                    <th data-field="" data-width="8%" data-align="center"
                        data-formatter="limitFormatter"
                        class="title">认购条件
                    </th>

                    <th data-field="status" data-width="5%" data-align="center" data-formatter="orderTypeFormatter"
                        class="title">状态
                    </th>

                    <th data-field="" data-width="12%" data-align="center"
                        data-formatter="timeFormatter"
                        class="title">开售时间
                    </th>

<#--                    <th data-field="endtime" data-width="10%" data-align="center"-->
<#--                        class="title">结束时间-->
<#--                    </th>-->

<#--                    <th data-field="" data-width="12%" data-align="center"-->
<#--                        data-formatter="remarkFormatter"-->
<#--                        class="title">备注-->
<#--                    </th>-->

                    <th data-field="id" data-formatter="actionFormatter" data-width="10%" data-align="center">操作</th>
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
            if(day>360){
                layTime.hint('最多选择30天');
            }
        }
    });
    function orderTypeFormatter(value, row, index) {

        if(value==="new"){
            return "草稿";
        }
        if(value==="realized"){
            return "已完成";
        }
        if(value==="saled"){
            return "已售磬";
        }
        if(value==="saling"){
            return "销售中";
        }


        return value;
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
        date2.setDate(date1.getDate()-360);
        var time2 = date2.getFullYear()+"-"+(date2.getMonth()+1)+"-"+date2.getDate();
        var date = time2 + " - " + time1;
        $('input[name="time"]').val(date);
    }

    function returnRateFormatter(value, row, index) {
        var result = '';
        result += "预期最低: " + row.returnExpectedStart + "<br>";
        result += "预期最高: " + row.returnExpectedEnd + "<br>";
        result += "实际收益: " + row.returnRealRate + "<br>";
        return result;
    }

    function saleFormatter(value, row, index) {
        var result = '';
        result += "预售: " + row.saleEstimate + "<br>";
        result += "已售: " + row.saleActual + "<br>";
        return result;
    }

    function limitFormatter(value, row, index) {
        var result = '';
        result += "最小金额: " + row.limitMinSale + "<br>";
        result += "最大金额: " + row.limitMaxSale + "<br>";
        result += "最低投注: " + row.limitMinBets + "<br>";
        result += "最低余额: " + row.limitMinBalance + "<br>";
        return result;
    }

    function timeFormatter(value, row, index) {
        var result = '';
        result += "创建时间: " + row.createtime + "<br>";
        result += "开售时间: " + row.beginSaleTime + "<br>";
        // result += "停售时间: " + row.endSaleTime + "<br>";
        // result += "结束时间: " + row.endtime + "<br>";
        return result;
    }

    /**
     * 操作栏的格式化
     */
    function actionFormatter(value, row, index) {
        var result = "";

        result += '<a href="javascript:;" class="table-btn editProduct" data-no="' + row.id + '" data-status="enable" title="编辑">编辑</a>';
        result += '<a href="javascript:;" class="table-btn reSettleOrder" data-no="' + row.id + '" data-status="enable" title="重新结算">重新结算</a>';
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
        options.url = '/alibaba888/Liv2sky3soLa93vEr62/getGameFMProductList';
        options.search = false;
        options.showRefresh = true;
        options.pageSize = 15;
        options.queryParams = function (params) {
            params.time = $('input[name="time"]').val();
            params.username = $('input[name="username"]').val();
            params.type = $('select[name="type"]').val();
            params.status = $('select[name="status"]').val();
            params.id = $('input[name="id"]').val();

            return params;
        };
        var bootstrapTable = $('#bootstrapTable').bootstrapTable(options);

        $(document).on('click', '#create-btn', function(){
            var url = '/alibaba888/Liv2sky3soLa93vEr62/game/fm_product/add/page';
            openNewWindow(url)
            //window.location.href ='/alibaba888/Liv2sky3soLa93vEr62/game/fm_product/add/page';
        });

        $('#type').change(function(){
            refresh();
        });

        $(document).on('click', '.table-btn.editProduct', function () {
            var no = $(this).attr('data-no');
            var url = '/alibaba888/Liv2sky3soLa93vEr62/game/fm_product/add/page?id=' + no;
            openNewWindow(url)
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

    });

    function refresh() {
        $('#bootstrapTable').bootstrapTable('refresh');
    }

    function reSettleAllLotteryOrder(issue) {
        $.ajax({
            url: '/alibaba888/Liv2sky3soLa93vEr62/game/fm_product/reSettle',
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
