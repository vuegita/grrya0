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
            <h5>会员增长统计</h5>

            <p style="color: blue"> 　</p>
<#--            <p  style="color: green">1. 统计上级其所拥有下级员工数据(会员数据已累加到会员中)</p>-->
        </div>

        <div class="ibox-content">
            <div class="btn-group hidden-xs" id="bootstrapTableToolBar" role="group">

                <div class="pull-left" style="margin-right: 10px;">
                    <input type="text" name="time" id="time" class="form-control input-outline"  placeholder="请选择创建时间" style="width:200px;">
                </div>


<#--                <div class="pull-left" style="margin-right: 10px;">-->
<#--                    <select class="form-control" name="type" >-->
<#--                        <option value="" selected>所有类型</option>-->
<#--                        <#list typeList as item>-->
<#--                            <option value="${item.getKey()}">${item.getTitle()}</option>-->
<#--                        </#list>-->
<#--                    </select>-->
<#--                </div>-->

                <div class="pull-left" style="margin-right: 10px;">
                    <input type="text" name="staffname" id="staffname" class="form-control input-outline" placeholder="请输入员工名">
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

                    <th data-field="staffname" data-width="5%" data-align="center"
                        class="title">所属员工
                    </th>

                    <th data-field="registerCount" data-width="5%" data-align="center"
                        class="title">注册人数
                    </th>

                    <th data-field="splitCount" data-width="5%" data-align="center"
                        class="title">裂变人数
                    </th>

                    <th data-field="activeCount" data-width="5%" data-align="center"
                        class="title">活跃人数
                    </th>

                    <th data-field="totalRechargeCount" data-width="5%" data-align="center"
                        class="title">充值总数
                    </th>

                    <th data-field="userRechargeCount" data-width="5%" data-align="center"
                        class="title">充值人数
                    </th>

                    <th data-field="firstRechargeCount" data-width="5%" data-align="center"
                        class="title">首充人数
                    </th>

                    <th data-field="firstRechargeAmount" data-width="5%" data-align="center"
                        class="title">首充金额
                    </th>

                    <th data-field="totalWithdrawCount" data-width="5%" data-align="center"
                        class="title">提现次数
                    </th>

                    <th data-field="userWithdrawCount" data-width="5%" data-align="center"
                        class="title">提现人数
                    </th>

<#--                    <th data-field="pdate" data-width="10%" data-align="center"-->
<#--                        class="title">日期-->
<#--                    </th>-->

<#--                    <th data-field="id" data-formatter="actionFormatter" data-width="8%" data-align="center">操作</th>-->

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
        date2.setDate(date1.getDate()-15);
        var time2 = date2.getFullYear()+"-"+(date2.getMonth()+1)+"-"+date2.getDate();
        var date = time2 + " - " + time1;
        $('input[name="time"]').val(date);
    }

    function moneyDataFormatter(value, row, index) {
        return value.toFixed(2);
    }

    function platformProfitDataFormatter(value, row, index) {
        return (row.betAmount - row.winAmount).toFixed(2);
    }

    function actionFormatter(value, row, index) {
        var result = "";

        // result += '<a href="javascript:;" class="table-btn queryChildList" data-username="' + row.username + '" title="查看下级">查看下级</a>';


        return result;
    }

    $(function () {
        resetTime();

        var options = TableUtils.getBtDefaultOptions();
        options.url = '/alibaba888/agent/getUserStatusDayReportList';
        options.search = false;
        options.showRefresh = true;
        options.queryParams = function (params) {
            params.username = $('input[name="username"]').val();
            params.staffname = $('input[name="staffname"]').val();

            params.time = $('input[name="time"]').val();
            params.type = $('select[name="type"]').val();
            return params;
        };
        var bootstrapTable = $('#bootstrapTable').bootstrapTable(options);

        $(document).on('click', '.table-btn.queryChildList', function () {
            var username = $(this).attr('data-username');
            $('#ancestorUsername').prop('value', username);
            $('input[name="username"]').val('');
            refresh();
        });

        $('#search-btn').click(function () {
            if (isEmpty($('input[name="time"]').val())){
                $.global.openErrorMsg('请选择时间范围');
                return;
            }
            refresh();
        });

        $('#reset-btn').click(function () {
            $('input[name="username"]').val('');
            $('input[name="ancestorUsername"]').val('');
            resetTime();
        });

    });

    function refresh() {
        $('#bootstrapTable').bootstrapTable('refresh');
    }




</script>
</body>
</html>
