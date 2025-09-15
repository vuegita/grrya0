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
            <h5>会员今日盈亏</h5>
        </div>

        <div class="ibox-content">
            <div class="btn-group hidden-xs" id="bootstrapTableToolBar" >

<#--                {"totalBusinessProfitLoss":8,"userid":0,"businessDeduct":2,"businessRecharge":10,"balance":0,"username":"a1"}-->
<#--                <div class="pull-left" style="margin-right: 10px;">-->
<#--                    <input type="text" name="time" id="time" class="form-control input-outline"  placeholder="请选择创建时间" style="width:200px;">-->
<#--                </div>-->

<#--                <div class="pull-left" style="margin-right: 10px;">-->
<#--                    <input type="text" name="username" id="username" class="form-control input-outline" placeholder="请输入用户名" value="${username!}">-->
<#--                </div>-->


                <div class="pull-left" style="margin-right: 15px;">
                    <select class="form-control" name="type" id="type">
                        <option value=true selected>会员今日盈利榜</option>
                        <option value=false >会员今日亏损榜</option>
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

                    <th data-field="username" data-width="8%" data-align="center"
                        class="title">用户名
                    </th>

                    <th data-field="totalBusinessProfitLoss" data-width="8%" data-align="center"
                        class="title">盈亏金额
                    </th>

                    <th data-field="businessDeduct" data-width="8%" data-align="center"
                        class="title">下单金额
                    </th>

                    <th data-field="businessRecharge" data-width="8%" data-align="center"
                        class="title">下单返奖金额
                    </th>



<#--                    <th data-field="id" data-formatter="actionFormatter3" data-width="10%" data-align="center">操作</th>-->

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

    /**
     * 操作栏的格式化
     */
    function actionFormatter3(value, row, index) {
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

    /**
     * 操作栏的格式化
     */
    function actionFormatter(value, row, index) {
        var result = "";
        if(row.level1Count>0){
            result += '<a style="font-weight: bold;text-decoration:underline" href="javascript:;" class="table-btn selectlevel1Count" data-no="' + row.username + '" title="一级总人数">'+row.level1Count+'</a>';
        }else{
            result=row.level1Count;
        }

        return result;
    }

    function actionFormatter2(value, row, index) {
        var result = "";
        if(row.level2Count>0){
            result += '<a style="font-weight: bold;text-decoration:underline" href="javascript:;" class="table-btn selectlevel2Count" data-no="' + row.username + '" title="二级总人数">'+row.level2Count+'</a>';
        }else{
            result=row.level2Count;
        }

        return result;
    }

    $(function () {
        resetTime();

        var options = TableUtils.getBtDefaultOptions();
        options.url = '/alibaba888/agent/getMemberProfitLossList';
        options.search = false;
        options.showRefresh = true;
        options.queryParams = function (params) {
            params.username = $('input[name="username"]').val();
            params.type = $('select[name="type"]').val();
            return params;
        };
        var bootstrapTable = $('#bootstrapTable').bootstrapTable(options);


        $('#search-btn').click(function () {
            // if (isEmpty($('input[name="time"]').val())){
            //     $.global.openErrorMsg('请选择时间范围');
            //     return;
            // }
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

    $(document).on('click', '.table-btn.selectlevel1Count', function () {
        var username = $(this).attr('data-no');
        var url = "/alibaba888/Liv2sky3soLa93vEr62/root_passport_user_attr?parantname=" + username;
        window.location.href = url;
        //openNewWindow(url);
    });


    $(document).on('click', '.table-btn.selectlevel2Count', function () {
        var username = $(this).attr('data-no');
        var url = "/alibaba888/Liv2sky3soLa93vEr62/root_passport_user_attr?grantname=" + username;
        window.location.href = url;
        // openNewWindow(url);
    });




</script>
</body>
</html>
