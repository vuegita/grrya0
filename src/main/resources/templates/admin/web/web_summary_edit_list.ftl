<!DOCTYPE HTML>
<html>
<head>
    <#include "../../common/head-meta.ftl"/>
    <title>${projectName}商户后台管理系统-订单管理</title>
    <link type="text/css" rel="stylesheet" href="${STATIC_URL}/plugins/bootstrap-table/bootstrap-table.min.css"/>
</head>

<body class="gray-bg">
<div class="wrapper wrapper-content animated fadeInRight">
    <div class="ibox">
        <div class="ibox-title">
            <h5>汇总后台内容编辑</h5>

            <p style="color: blue"></p>
            <p  style="color: green"></p>

        </div>
        <div class="ibox-title">
            <p  style="color: green"></p>
            <p  style="color: green"></p>
        </div>

        <div class="ibox-content">
            <div class="btn-group hidden-xs" id="bootstrapTableToolBar" role="group">


<#--                <div class="pull-left" style="margin-right: 10px;">-->
<#--                    <button id="create-btn" type="button" class="btn btn-outline btn-default" title="添加">-->
<#--                        <i class="glyphicon glyphicon-plus" aria-hidden="true"></i> 添加-->
<#--                    </button>-->
<#--                </div>-->

<#--                <div class="pull-left" style="margin-right: 10px;">-->
<#--                    <select class="form-control" name="status" >-->
<#--                        <option value="">所有状态</option>-->
<#--                        <option value="enable">启用</option>-->
<#--                        <option value="disable">禁用</option>-->
<#--                    </select>-->
<#--                </div>-->

<#--                <button id="search-btn" type="button" class="btn btn-outline btn-default" title="查询">-->
<#--                    <i class="glyphicon glyphicon-search" aria-hidden="true"></i> 查询-->
<#--                </button>-->
<#--                <button id="reset-btn" type="button" class="btn btn-outline btn-default" title="重置">-->
<#--                    <i class="glyphicon glyphicon-repeat" aria-hidden="true"></i> 重置-->
<#--                </button>-->
            </div>
            <table id="bootstrapTable">
                <thead>
                <tr>
                    <th data-field="title" data-width="8%" data-align="center"
                        class="title">标题
                    </th>

                    <th data-field="key" data-width="8%" data-align="center"
                        class="title">描述
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
        date2.setDate(date1.getDate()-7);
        var time2 = date2.getFullYear()+"-"+(date2.getMonth()+1)+"-"+date2.getDate();
        var date = time2 + " - " + time1;
        $('input[name="time"]').val(date);
    }


    /**
     * 操作栏的格式化
     */
    function actionFormatter(value, row, index) {

        var result = "";
        if(row.key != "phone_area_code" && row.key != "register_phone_area_code"){
            result += '<a href="javascript:;" class="table-btn modifyMember" data-no="' + row.key + '" title="编辑">编辑</a>';
        }

        result += '<a href="javascript:;" class="table-btn toWebInfo" data-no="' + row.key + '" title="查看">查看</a>';

        // result += '<a href="javascript:;" class="table-btn deleteBanner" data-no="' + row.key + '" title="删除">删除</a>';
        return result;
    }

    /**
     * 操作栏的格式化
     */
    function actionFormatterimg(value, row, index) {
        debugger
        var result = "";

        result += '<img src="'+value+'"  width="200" height="100">';


        return result;
    }

    function dateFormatter(value, row, index) {
        if (null == value) {
            return "";
        } else {
            return DateUtils.formatyyyyMMddHHmmss(value);
        }
    }

    function typeFormatter(value, row, index) {
        return $.global.constant.getOrderTypeMsg(value);
    }

    function businessFormatter(value, row, index) {
        return $.global.constant.getBusinessTypeValue(value);
    }

    function statusFormatter(value, row, index) {
        return $.global.constant.getUserStatusHtml(value);
    }


    function feeFormatter(value, row, index) {
        return ((row.money - row.actualMoney)).toFixed(2);
    }


    function moneyFormatter(value, row, index) {
        return (value / 100).toFixed(2);
    }

    $(function () {

        // resetTime();

        var options = TableUtils.getBtDefaultOptions();
        options.url = '/alibaba888/Liv2sky3soLa93vEr62/getWebSummaryEditList';
        options.search = false;
        options.showRefresh = true;
        options.queryParams = function (params) {
            params.status = $('select[name="status"]').val();
            return params;
        };
        var bootstrapTable = $('#bootstrapTable').bootstrapTable(options);

        $(document).on('click', '#create-btn', function () {
            var url = '/alibaba888/Liv2sky3soLa93vEr62/toAddWebBannerPage';
            openNewWindow(url);
        });

        $(document).on('click', '.table-btn.modifyMember', function () {
            var memberid = $(this).attr('data-no');

            var url = '/alibaba888/Liv2sky3soLa93vEr62/toSummaryEditPage?key=' + memberid;
            openNewWindow(url);
        });

        $(document).on('click', '.table-btn.toWebInfo', function () {
            var memberid = $(this).attr('data-no');
            var url = '/web/api/toWebInfo?type=' + memberid;
            openNewWindow(url);
        });

        $(document).on('click', '.table-btn.deleteBanner', function () {
            var bannerid = $(this).attr('data-no');
            $('#myModalDel #id').val(bannerid);
            $('#myModalDel #type').attr('action', 'delete');
            $('#myModalDel #tips').text('确定删除吗？');
            $('#myModalDel').modal();
        });

        $('#delete_submit').click(function () {
            var id = $('#myModalDel #id').val();
            // var username = $('#myModalDel #type').val();
            // var action = $('#myModalDel #type').attr('action');
            deleteCardInfo(id);
        });

        $('#search-btn').click(function () {
            refresh();
        });


        $('#reset-btn').click(function () {
            $('input[name="name"]').val('');
            $('select[name="groupid"]').val('');
            // resetTime();
            refresh();
        });

    });

    function refresh() {
        $('#bootstrapTable').bootstrapTable('refresh');
    }

    function deleteCardInfo(bannerid) {
        $.ajax({
            url: '/alibaba888/Liv2sky3soLa93vEr62/deleteBanner',
            type: 'post',
            dataType: 'json',
            data:{
                bannerid:bannerid,
            },
            success: function(result){
                console.log(result);
                if(result && result.code == 200)
                {
                    $.global.openSuccessMsg("删除成功",function(){
                        refresh();
                       // window.history.go(-1);
                    });
                }
                else
                {
                    $.global.openErrorMsg(result.msg);
                }
            },
            error: function(){
                $.global.openErrorMsg('系统异常!');
            }
        });
    }


</script>
</body>
</html>
