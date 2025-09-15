<!DOCTYPE HTML>
<html>
<head>
    <#include "../../../../common/head-meta.ftl"/>
    <title>${projectName}后台管理系统-商户配置设置</title>
    <link type="text/css" rel="stylesheet" href="${STATIC_URL}/plugins/bootstrap-table/bootstrap-table.min.css" />
    <script type="text/javascript" src="${STATIC_URL}/plugins/bootstrap-table/bootstrap-table.min.js"></script>
    <script type="text/javascript" src="${STATIC_URL}/plugins/bootstrap-table/locale/bootstrap-table-zh-CN.min.js"></script>
    <script type="text/javascript" src="${STATIC_URL}/plugins/layer/laydate/laydate.js"></script>
</head>
<body class="gray-bg">
<ul id="myTab" class="nav nav-tabs">
    <li class="active">
        <a href="#userInfoDetail" data-toggle="tab">
            会员基本信息
        </a>
    </li>

<#--    <li><a href="#gameRgOrderList" data-toggle="tab">红绿订单</a></li>-->

    <li><a href="#moneyOrderDetailList" data-toggle="tab">账变明细</a></li>
</ul>

<div id="myTabContent" class="tab-content">

<#--    <div class="pull-left" style="margin-right: 10px;">-->
<#--        <input type="hidden" name="time" id="time" class="form-control input-outline"  placeholder="请选择时间" style="width:200px;">-->
<#--    </div>-->

    <div class="tab-pane fade in active" id="userInfoDetail">
        <#include "user_history_detail_basic_info2.ftl"/>
    </div>
<#--    <div class="tab-pane fade" id="gameRgOrderList">-->
<#--        <#include "user_history_detail_game_rg_order_list.ftl"/>-->
<#--    </div>-->
    <div class="tab-pane fade" id="moneyOrderDetailList">
        <#include "user_history_detail_money_order_detail_list.ftl"/>
    </div>
</div>

<script>
    lay('#version').html('-v' + laydate.v);
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
        date2.setDate(date1.getDate()-7);
        var time2 = date2.getFullYear()+"-"+(date2.getMonth()+1)+"-"+date2.getDate();
        var date = time2 + " - " + time1;
        $('input[name="time"]').val(date);
    }
</script>

<script type="text/javascript">

</script>
</body>
</html>
