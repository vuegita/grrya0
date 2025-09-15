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
            <h5>红绿投注订单</h5>
        </div>

        <div class="ibox-content">

            <div class="btn-group hidden-xs" id="bootstrapTableToolBar" role="group">

                <div class="pull-left" style="margin-right: 10px;">
                    <input type="text" name="time" id="time" class="form-control input-outline"  placeholder="请选择时间" style="width:200px;">
                </div>

                <div class="pull-left" style="margin-right: 10px;">
                    <input type="text" name="issue" id="issue" class="form-control input-outline"  placeholder="请输入期号" style="width:150px;">
                </div>

                <div class="pull-left" style="margin-right: 10px;">
                    <input type="text" name="orderno" id="orderno" class="form-control input-outline"  placeholder="请输入订单号" style="width:150px;">
                </div>

                <div class="pull-left" style="margin-right: 10px;">
                    <input type="text" name="username" id="username" class="form-control input-outline"  placeholder="请输入用户名" style="width:150px;">
                </div>

                <div class="pull-left" style="margin-right: 10px;">
                    <select class="form-control" name="type" id="type" >
                        <option value="" >---请选择彩种类型---</option>
                        <#list gameList as game>
                            <option value="${game.key}" >${game.getShowLotteryType()}</option>
                        </#list>
                    </select>
                </div>

                <div class="pull-left" style="margin-right: 10px;">
                    <select class="form-control" name="txStatus" >
                        <option value="" >---请选择状态---</option>
                        <option value="new" >new</option>
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
            </div>
            <table id="bootstrapTable">
                <thead>
                <tr>
                    <th data-field="issue" data-width="8%" data-align="center"
                        class="title">期号
                    </th>

                    <th data-field="type" data-width="5%" data-align="center"
                        class="title">彩种类型
                    </th>

                    <th data-field="no" data-width="8%" data-align="center"
                        class="title">系统订单号
                    </th>

                    <th data-field="username" data-width="5%" data-align="center"
                        class="title">用户名
                    </th>

                    <th data-field="staffname" data-width="5%" data-align="center" class="title">所属员工</th>

<#--                    <th data-field="agentname"  data-width="5%" data-align="center">所属代理</th>-->

                    <th data-field="betAmount" data-width="5%" data-align="center"  data-sortable="true"
                        class="title">投注金额
                    </th>

                    <th data-field="winAmount" data-width="5%" data-align="center"
                        class="title">中奖金额
                    </th>

                    <th data-field="" data-width="5%" data-align="center" data-formatter="platformProfitFormatter"
                        class="title">平台盈利
                    </th>

                    <th data-field="feemoney" data-width="5%" data-align="center"
                        class="title">手续费
                    </th>

                    <th data-field="betItem" data-width="5%" data-align="center"
                        class="title">投注项
                    </th>

                    <th data-field="openResult" data-width="5%" data-align="center"
                        data-formatter="openResultFormatter"
                        class="title">开奖数字
                    </th>

                    <th data-field="status" data-width="5%" data-align="center"
                        class="title">订单状态
                    </th>

                    <th data-field="balance" data-width="5%" data-align="center"
                        class="title">余额
                    </th>

                    <th data-field="totalWithdraw" data-width="5%" data-align="center"
                        class="title">总提现
                    </th>

                    <th data-field="totalRecharge" data-width="5%" data-align="center"
                        class="title">总充值
                    </th>

                    <th data-field="createtime" data-width="10%" data-align="center"
                        class="title">创建时间
                    </th>

                    <th data-field="updatetime" data-width="10%" data-align="center"
                        class="title">更新时间
                    </th>

                    <th data-field="id" data-formatter="actionFormatter" data-width="10%" data-align="center">操作</th>
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
        date2.setDate(date1.getDate()-0);
        var time2 = date2.getFullYear()+"-"+(date2.getMonth()+1)+"-"+date2.getDate();
        var date = time2 + " - " + time1;
        $('input[name="time"]').val(date);
    }

    /**
     * 操作栏的格式化
     */
    function actionFormatter(value, row, index) {
        var result = "";

        result += '<a href="javascript:;" class="table-btn detailUserInfo" data-username="' + row.username + '" title="详情">详情</a>';
        return result;
    }
    $(document).on('click', '.table-btn.detailUserInfo', function () {
        var username = $(this).attr('data-username');
        var url = '/alibaba888/Liv2sky3soLa93vEr62/passport/user/history_detail/page?username=' + username;
        // window.location.href = url;
        openNewWindow(url);
    });

    // /**
    //  * 操作栏的格式化
    //  */
    // function actionFormatter(value, row, index) {
    //     var result = "";
    //
    //     if(row.status == 'waiting')
    //     {
    //         // result += '<a href="javascript:;" class="table-btn revokeOrder" data-no="' + row.no + '" data-status="enable" title="撤单">撤单</a>';
    //     }
    //
    //     // if(row.status == 'pending')
    //     // {
    //     //     result += '<a href="javascript:;" class="table-btn presetOpenResult" data-no="' + row.issue + '" data-status="disable" title="预设开奖">预设开奖</a>';
    //     // }
    //     return result;
    // }

    function platformProfitFormatter(value, row, index) {
        if(row.status == 'failed' || row.status == 'realized')
        {
            return (row.betAmount - row.winAmount).toFixed(2);
        }
        return '-';
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
        options.url = '/alibaba888/Liv2sky3soLa93vEr62/getLotteryOrderList';
        options.search = false;
        options.showRefresh = true;
        options.pageSize = 15;
        options.queryParams = function (params) {
            params.time = $('input[name="time"]').val();
            params.type = $('select[name="type"]').val();
            params.txStatus = $('select[name="txStatus"]').val();
            params.issue = $('input[name="issue"]').val();
            params.orderno = $('input[name="orderno"]').val();
            params.username = $('input[name="username"]').val();

            params.sortName=params.sort;
            params.sortOrder=params.order;

            return params;
        };
        var bootstrapTable = $('#bootstrapTable').bootstrapTable(options);

        $('#type').change(function(){
            refresh();
        });

        $(document).on('click', '.table-btn.revokeOrder', function () {
            var no = $(this).attr('data-no');
            $('#myModalDel #id').val(no);
            $('#myModalDel #type').attr('action', 'revokeOrder');
            $('#myModalDel #tips').text('确定撤单吗？');
            $('#myModalDel').modal();
        });

        $('#delete_submit').click(function () {
            var no = $('#myModalDel #id').val();
            var value = $('#myModalDel #type').val();
            var action = $('#myModalDel #type').attr('action');

            // console.log("action = " + action);

            if("revokeOrder" == action)
            {
                revokeOrder(no)
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
            $('input[name="username"]').val('');
            $('input[name="orderno"]').val('');
            resetTime();
        });

    });

    function refresh() {
        $('#bootstrapTable').bootstrapTable('refresh');
    }

    function revokeOrder(orderno) {
        $.ajax({
            url: '/alibaba888/Liv2sky3soLa93vEr62/revokeLotteryOrder',
            type: 'post',
            dataType: 'json',
            data: {
                orderno:orderno,
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
