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
            <h5>限额配置</h5>

            <p style="color: blue"> 　</p>
            <p  style="color: green">1. 邀请好友个数表示当日免费额度做完，如果要继续做,则要邀请好友!</p>
<#--            <p  style="color: green">2. 强制购买VIP表示邀请的好友(未)!</p>-->
            <p  style="color: green">2. 无会员时单笔最大金额为5!</p>
            <p  style="color: green">3. 今日总上限 = VIP价格 / 回本周期 !</p>
            <p  style="color: green">4. 批量生成请先把VIP状态更新为Enable !</p>
            <p  style="color: green">5. 返佣比例指的是下级购买VIP返佣比例 !</p>
<#--            <p  style="color: green">6. 返提现额度率指的是下级购买VIP返到提现额度里(相当于提现打码) !</p>-->

        </div>

        <div class="ibox-content">
            <div class="btn-group hidden-xs" id="bootstrapTableToolBar" role="group">

<#--                <div class="pull-left" style="margin-right: 10px;">-->
<#--                    <button id="create-btn" type="button" class="btn btn-outline btn-default" title="新增">-->
<#--                        <i class="glyphicon glyphicon-plus" aria-hidden="true"></i> 新增-->
<#--                    </button>-->
<#--                </div>-->

<#--                <div class="pull-left" style="margin-right: 10px;">-->
<#--                    <input type="text" name="time" id="time" class="form-control input-outline"  placeholder="请选择时间" style="width:200px;">-->
<#--                </div>-->

<#--                <div class="pull-left" style="margin-right: 10px;">-->
<#--                    <input type="text" name="username" id="username" class="form-control input-outline"  placeholder="请输入用户名" style="width:150px;">-->
<#--                </div>-->

<#--                <div class="pull-left" style="margin-right: 10px;">-->
<#--                    <input type="text" name="agentname" id="agentname" class="form-control input-outline"  placeholder="请输入代理用户名" style="width:150px;">-->
<#--                </div>-->

<#--                <div class="pull-left" style="margin-right: 10px;">-->
<#--                    <input type="text" name="staffname" id="staffname" class="form-control input-outline"  placeholder="请输入直属员工名" style="width:150px;">-->
<#--                </div>-->

<#--                <div class="pull-left" style="margin-right: 10px;">-->
<#--                    <input type="text" name="parentname" id="parentname" class="form-control input-outline"  placeholder="请输入父级用户名"  value="${parantname!}" style="width:150px;">-->
<#--                </div>-->

<#--                <div class="pull-left" style="margin-right: 10px;">-->
<#--                    <input type="text" name="grantname" id="grantname" class="form-control input-outline"  placeholder="请输入祖父级用户名" value="${grantname!}" style="width:150px;">-->
<#--                </div>-->

<#--                <div class="pull-left" style="margin-right: 10px;">-->
<#--                    <select class="form-control" name="type" >-->
<#--                        <#list vipTypeList as item>-->
<#--                            <#if item_index == 0>-->
<#--                                <option value="${item.getKey()}" selected>${item.getName()}</option>-->
<#--                            <#else >-->
<#--                                <option value="${item.getKey()}" selected>${item.getName()}</option>-->
<#--                            </#if>-->
<#--                        </#list>-->
<#--                    </select>-->
<#--                </div>-->

<#--                <div class="pull-left" style="margin-right: 10px;">-->
<#--                    <select class="form-control" name="status" >-->
<#--                        <option value="" >---所有状态---</option>-->
<#--                        <option value="enable" >enable</option>-->
<#--                        <option value="disable" >disable</option>-->
<#--                    </select>-->
<#--                </div>-->

                <button id="search-btn" type="button" class="btn btn-outline btn-default" title="查询">
                    <i class="glyphicon glyphicon-search" aria-hidden="true"></i> 查询
                </button>
                <button id="reset-btn" type="button" class="btn btn-outline btn-default" title="重置">
                    <i class="glyphicon glyphicon-repeat" aria-hidden="true"></i> 重置
                </button>
                <button id="batch-btn" type="button" class="btn btn-outline btn-default" title="重置">
                    <i class="glyphicon glyphicon-ok" aria-hidden="true"></i> 批量生成
                </button>

            </div>
            <table id="bootstrapTable">
                <thead>
                <tr>
<#--                    <th data-field="id" data-width="5%" data-align="center"-->
<#--                        class="title">ID-->
<#--                    </th>-->

                    <th data-field="vipName" data-width="5%" data-align="center"
                        class="title">VIP名称
                    </th>

                    <th data-field="vipLevel" data-width="5%" data-align="center"
                        class="title">VIP等级
                    </th>

                    <th data-field="paybackPeriod" data-width="5%" data-align="center"
                        class="title">回本周期
                    </th>

                    <th data-field="totalMoneyOfDay" data-width="5%" data-align="center"
                        class="title">今日总上限
                    </th>

                    <th data-field="freeMoneyOfDay" data-width="5%" data-align="center"
                        class="title">今日免费额度
                    </th>

                    <th data-field="maxMoneyOfSingle" data-width="5%" data-align="center"
                        class="title">单笔最大金额
                    </th>

                    <th data-field="inviteCountOfDay" data-width="5%" data-align="center"
                        class="title">邀请好友个数
                    </th>

                    <th data-field="inviteMoneyOfDay" data-width="5%" data-align="center"
                        class="title">邀请额度
                    </th>

<#--                    <th data-field="buyCountOfDay" data-width="5%" data-align="center"-->
<#--                        class="title">购买VIP个数-->
<#--                    </th>-->

                    <th data-field="buyMoneyOfDay" data-width="5%" data-align="center"
                        class="title">购买VIP额度/每个
                    </th>

                    <th data-field="" data-width="5%" data-align="center" data-formatter="rebateBalanceFormatter"
                        class="title">返佣比例(%)
                    </th>

                    <th data-field="" data-width="5%" data-align="center" data-formatter="rebateWithdrawlFormatter"
                        class="title">返提现额度率(%)
                    </th>

                    <th data-field="status" data-width="5%" data-align="center" data-formatter="statusFormatter22"
                        class="title">状态 
                    </th>

<#--                    <th data-field="forceBuyVip" data-width="5%" data-align="center"-->
<#--                        class="title">强制买VIP-->
<#--                    </th>-->

<#--                    <th data-field="createtime" data-width="8%" data-align="center"-->
<#--                        class="title">创建时间-->
<#--                    </th>-->

                    <th data-field="id" data-formatter="actionFormatter" data-width="10%" data-align="center">操作</th>
                </tr>
                </thead>
            </table>
        </div>
    </div>
</div>

<#include "../../common/delete_form.ftl">
<#include "../passport/user_attr_list_edit_info.ftl">
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


    /**
     * 操作栏的格式化
     */
    function actionFormatter(value, row, index) {
        var result = "";
        result += '<a href="javascript:;" class="table-btn editInfo" data-no="' + row.id + '" title="编辑">编辑</a>';
        // result += '<a href="javascript:;" class="table-btn addMateriel" data-no="' + row.id + '" title="新增物料">新增物料</a>';
        return result;
    }

    function statusFormatter22(value, row, index) {
        return $.global.constant.getUserStatusHtml(value);
    }

    function withdrawFormatter(value, row, index) {
        var totalWithdraw = row.totalWithdraw;
        var totalRefund = row.totalRefund;
        var rs = totalWithdraw - totalRefund;
        return rs.toFixed(2)
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

    function rebateBalanceFormatter(value, row, index) {
        var rs = "";
        rs += "LV1: " + row.lv1RebateBalanceRate + " % <br>";
        rs += "LV2: " + row.lv2RebateBalanceRate + " % <br>";
        return rs;
    }

    function rebateWithdrawlFormatter(value, row, index) {
        var rs = "";
        rs += "LV1: " + row.lv1RebateWithdrawlRate + " % <br>";
        // rs += "LV2: " + row.lv2RebateWithdrawlRate + " % <br>";
        return rs;
    }

    $(function () {

        //resetTime();

        var options = TableUtils.getBtDefaultOptions();
        options.url = '/alibaba888/Liv2sky3soLa93vEr62/getAdVipLimitList';
        options.search = false;
        options.showRefresh = true;
        options.queryParams = function (params) {
            params.time = $('input[name="time"]').val();
            params.type = $('select[name="type"]').val();
            params.status = $('select[name="status"]').val();
            return params;
        };
        var bootstrapTable = $('#bootstrapTable').bootstrapTable(options);

        $(document).on('click', '#create-btn', function () {
            var url = '/alibaba888/Liv2sky3soLa93vEr62/toEditAdVipLimitPage';
            openNewWindow(url);
        });

        $(document).on('click', '#batch-btn', function () {
            //var url = '/alibaba888/Liv2sky3soLa93vEr62/batchCreateAdVipLimitInfo';

            // $('#myModalDel #id').val(cardid);
            $('#myModalDel #type').attr('action', 'batchCreateAdVipLimitInfo');
            $('#myModalDel #tips').text('确定批量生成？');
            $('#myModalDel').modal();
        });


        $(document).on('click', '.table-btn.detailUserInfo', function () {
            var username = $(this).attr('data-no');
            var url = '/alibaba888/Liv2sky3soLa93vEr62/passport/user/history_detail/page?username=' + username;
            // window.location.href = url;
            openNewWindow(url);
        });

        $(document).on('click', '.table-btn.editInfo', function () {
            var id = $(this).attr('data-no');
            var url = "/alibaba888/Liv2sky3soLa93vEr62/toEditAdVipLimitPage?id=" + id;
            openNewWindow(url);
        });
        $(document).on('click', '.table-btn.updateCodeAmount', function () {
            var username = $(this).attr('data-no');
            var url = "/alibaba888/Liv2sky3soLa93vEr62/toUpdateUserAttrCodeAmountPage?username=" + username;
            openNewWindow(url);
        });

        $(document).on('click', '.table-btn.addMateriel', function () {
            var id = $(this).attr('data-no');
            var url = "/alibaba888/Liv2sky3soLa93vEr62/toEditAdMaterielPage?categoryid=" + id;
            openNewWindow(url);
        });



        $(document).on('click', '.table-btn.returnwaterstats', function () {
            var username = $(this).attr('data-no');
            var url = "/alibaba888/Liv2sky3soLa93vEr62/root_report_day_return_water?username=" + username;
            window.location.href = url;
            // openNewWindow(url);
        });



        //
        // $(document).on('click', '.table-btn.deleteBanCardInfo', function () {
        //     var cardid = $(this).attr('data-no');
        //     $('#myModalDel #id').val(cardid);
        //     $('#myModalDel #type').attr('action', 'delete');
        //     $('#myModalDel #tips').text('确定删除吗？');
        //     $('#myModalDel').modal();
        //
        // });

        $('#delete_submit').click(function () {
            var id = $('#myModalDel #id').val();
            // var username = $('#myModalDel #type').val();
            var action = $('#myModalDel #type').attr('action');
            if("batchCreateAdVipLimitInfo" == action)
            {
                batchCreateAdVipLimitInfo();
            }
        });

        $('#search-btn').click(function () {
            refresh();
        });


        $('#reset-btn').click(function () {
            $('input[name="username"]').val('');
            $('input[name="agentname"]').val('');
            $('input[name="staffname"]').val('');
            $('input[name="parentname"]').val('');
            $('input[name="grantname"]').val('');

            // resetTime();
            //refresh();
        });

    });

    function refresh() {
        $('#bootstrapTable').bootstrapTable('refresh');
    }

    function batchCreateAdVipLimitInfo()
    {
        $.ajax({
            url: '/alibaba888/Liv2sky3soLa93vEr62/batchCreateAdVipLimitInfo',
            type: 'post',
            //data: {username:username},
            dataType: 'json',
            success: function (result) {
                if (result.code === 200) {
                    $.global.openSuccessMsg(result.msg);
                    $('#myModalDel').hide();
                }else{
                    $.global.openErrorMsg(result.msg);
                }
            },
        });
    }

    $(document).on('click', '.table-btn.totaluser', function () {
        var username = $(this).attr('data-username');

        $.ajax({
            url: '/alibaba888/Liv2sky3soLa93vEr62/getUserAttrList',
            type: 'post',
            data: {username:username},
            dataType: 'json',
            success: function (result) {
                debugger
                if (result.code === 200) {
                    if(result.data.totalRecharge==null){
                        $.global.openErrorMsg('当前用户没有下级');
                    }
                    // $('#loading').modal('show');codeAmount
                    // $(".head-time-s1").text(result.data.totalBalance);
                    $('#myModalDel #tips').text('当前【' + username + '】的下级会员 总充值：' + result.data.totalRecharge.toFixed(2)
                        +'   总提现：' + (result.data.totalWithdraw-result.data.totalRefund).toFixed(2)
                        +'   总余额：' + result.data.balance.toFixed(2)
                    );
                    $('#myModalDel').modal();
                }else{
                    $.global.openSuccessMsg(result.msg);
                }

            },

        });
    });

</script>
</body>
</html>
