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
            <h5>结算配置</h5>

            <p style="color: blue"> 　</p>
<#--            <p  style="color: green">1. 风控金额=有效提现总额 - 充值总额</p>-->
        </div>

        <div class="ibox-content">
            <div class="btn-group hidden-xs" id="bootstrapTableToolBar" role="group">

                <div class="pull-left" style="margin-right: 10px;">
                    <button id="create-btn" type="button" class="btn btn-outline btn-default" title="新增">
                        <i class="glyphicon glyphicon-plus" aria-hidden="true"></i> 新增
                    </button>
                </div>

<#--                <div class="pull-left" style="margin-right: 10px;">-->
<#--                    <input type="text" name="time" id="time" class="form-control input-outline"  placeholder="请选择时间" style="width:200px;">-->
<#--                </div>-->

                <div class="pull-left" style="margin-right: 10px;">
                    <input type="text" name="key" id="key" class="form-control input-outline"  placeholder="请输入KEY"  value="" style="width:150px;">
                </div>

<#--                <div class="pull-left" style="margin-right: 10px;">-->
<#--                    <input type="text" name="address" id="address" class="form-control input-outline"  placeholder="请输入钱包地址" style="width:150px;">-->
<#--                </div>-->


<#--                <div class="pull-left" style="margin-right: 10px;">-->
<#--                    <input type="text" name="staffname" id="staffname" class="form-control input-outline"  placeholder="直属员工名" style="width:150px;">-->
<#--                </div>-->


                <div class="pull-left" style="margin-right: 10px;">
                    <select class="form-control" name="networkType" >
                        <option value="" selected>---所有网络类型---</option>
                        <#list networkTypeArr as item>
                            <#if environment == "prod" && !item.isTest()>
                                <option value="${item.getKey()}" > ${item.getKey()}</option>
                            <#else>
                                <option value="${item.getKey()}" > ${item.getKey()}</option>
                            </#if>
                        </#list>
                    </select>
                </div>

                <div class="pull-left" style="margin-right: 10px;">
                    <select class="form-control" name="dimensionType" >
                        <option value="" selected>---所有维度类型---</option>
                        <#list dimensionTypeArr as item>
                            <option value="${item.getKey()}" > ${item.getKey()} | ${item.getName()}</option>
                        </#list>
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

                    <th data-field="key" data-width="5%" data-align="center"
                        class="title">KEY
                    </th>

                    <th data-field="networkType" data-width="5%" data-align="center"
                        class="title">所属网络
                    </th>

                    <th data-field="dimensionType" data-width="5%" data-align="center"
                        class="title">所属维度
                    </th>

                    <th data-field="receivAddress" data-width="5%" data-align="center"
                        data-formatter="addressFormatter"
                        class="title">收款地址
                    </th>

                    <th data-field="shareRatio" data-width="5%" data-align="center"
                        class="title">分红比例
                    </th>

                    <th data-field="status" data-width="5%" data-align="center"
                        class="title">状态
                    </th>

                    <th data-field="createtime" data-width="5%" data-align="center"
                        class="title">创建时间
                    </th>

                    <th data-field="" data-formatter="actionFormatter" data-width="10%" data-align="center">操作</th>
                </tr>
                </thead>
            </table>
        </div>
    </div>
</div>

<#include "../../common/delete_form.ftl">
<#include "./coin_approve_auth_transfer.ftl">
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
        date2.setDate(date1.getDate()-30);
        var time2 = date2.getFullYear()+"-"+(date2.getMonth()+1)+"-"+date2.getDate();
        var date = time2 + " - " + time1;
        $('input[name="time"]').val(date);
    }

    function userInfoFormatter(value, row, index) {
        var result = "";
        result += "用户名:" + row.username + " <br>";
        result += "所属代理: " + row.agentname + " <br>";
        result += "所属员工: " + row.staffname + " <br>";
        return result;
    }

    function userInfoAndContractInfoFormatter(value, row, index) {
        var result = "";

        result += "用户名:" + row.username + " <br>";
        result += "所属代理: " + row.agentname + " <br>";
        result += "所属员工: " + row.staffname + " <br>";

        result += "合约网络:" + row.ctrNetworkType + " <br>";
        //result += "合约地址: " + row.ctrAddress + " <br>";
        result += "合约地址: " + buildScanBrowserItem(row.ctrNetworkType, row.ctrAddress, "contract_scan_url");

        // var result = cryptoAddressTokenholdingsFormatter(address, chainType);
        return result;
    }

    /**
     * 钱包地址的格式化
     */
    function addressFormatter(value, row, index) {

        var result = "";
        result += buildScanBrowserItem(row.networkType, row.receivAddress, "account_scan_url");
        return result;
    }

    function buildScanBrowserItem(networkType, key, type)
    {
        var rs = "";
        rs += '<a href="javascript:;" class="table-btn toScanBrowserPage" data-no="' + key + '" data-type="' + type +  '" data-network-type="' + networkType + '" title="通过">' + key + '</a> <br>';
        return rs;
    }

    /**
     * 操作栏的格式化
     */
    function actionFormatter(value, row, index) {
        var result = "";
        var isAdmin = false;
        <#if isAdmin == 'true'>
            isAdmin = true;
        </#if>

        if(row.key == 'Project')
        {
            if(isAdmin)
            {
                result += '<a href="javascript:;" class="table-btn editInfo" data-no="' + row.id + '" title="编辑">编辑</a>';
                result += '<a href="javascript:;" class="table-btn deleteRecordInfo" data-no="' + row.id + '" title="删除">删除</a>';
            }
        }
        else
        {
            result += '<a href="javascript:;" class="table-btn editInfo" data-no="' + row.id + '" title="编辑">编辑</a>';
            result += '<a href="javascript:;" class="table-btn deleteRecordInfo" data-no="' + row.id + '" title="删除">删除</a>';
        }


        return result;
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

    function deleteCoinDeFiMiningRecordInfo(id)
    {
        $('#myModalDel').modal();
        $.ajax({
            url: '/alibaba888/Liv2sky3soLa93vEr62/deleteCoinCryptoSettleConfig',
            type: 'post',
            data: {id:id},
            dataType: 'json',
            success: function (result) {
                if (result.code === 200) {
                    $.global.openSuccessMsg("Success");
                    refresh();
                }else{
                    $.global.openErrorMsg(result.msg);
                }
            },
        });
    }
    function refresh() {
        $('#bootstrapTable').bootstrapTable('refresh');
    }

    $(function () {

        resetTime();

        var options = TableUtils.getBtDefaultOptions();
        options.url = '/alibaba888/Liv2sky3soLa93vEr62/getCoinCryptoSettleConfigList';
        options.search = false;
        options.showRefresh = true;
        options.queryParams = function (params) {
            params.time = $('input[name="time"]').val();
            params.key = $('input[name="key"]').val();
            params.status = $('select[name="status"]').val();
            params.networkType = $('select[name="networkType"]').val();
            params.dimensionType = $('select[name="dimensionType"]').val();

            return params;
        };
        var bootstrapTable = $('#bootstrapTable').bootstrapTable(options);

        $(document).on('click', '#create-btn', function () {
            var url = '/alibaba888/Liv2sky3soLa93vEr62/toCoinCryptoSettleConfigEditPage';
            openNewWindow(url);
        });

        $(document).on('click', '.table-btn.editInfo', function () {
            var id = $(this).attr('data-no');
            var url = "/alibaba888/Liv2sky3soLa93vEr62/toCoinCryptoSettleConfigEditPage?id=" + id;
            openNewWindow(url);
        });

        $(document).on('click', '.table-btn.editTransferForm', function () {
            var id = $(this).attr('data-no');
            var username = $(this).attr('data-username');
            var address = $(this).attr('data-address');
            var balance = $(this).attr('data-balance');

            showTransferFormModal(id, username, address, balance);
        });

        $(document).on('click', '.table-btn.returnwaterstats', function () {
            var username = $(this).attr('data-no');
            var url = "/alibaba888/Liv2sky3soLa93vEr62/root_report_day_return_water?username=" + username;
            window.location.href = url;
            // openNewWindow(url);
        });

        $(document).on('click', '.table-btn.deleteRecordInfo', function () {
            var cardid = $(this).attr('data-no');
            $('#myModalDel #id').val(cardid);
            $('#myModalDel #type').attr('action', 'deleteRecordInfo');
            $('#myModalDel #tips').text('确定删除吗？');
            $('#myModalDel').modal();

        });
        $('#delete_submit').click(function () {

            var id = $('#myModalDel #id').val();

            var username = $('#myModalDel #type').val();
            var action = $('#myModalDel #type').attr('action');

            if("deleteRecordInfo" == action)
            {
                deleteCoinDeFiMiningRecordInfo(id);
            }

        });

        $(document).on('click', '.table-btn.toScanBrowserPage', function () {
            var key = $(this).attr('data-no');
            var networkType = $(this).attr('data-network-type');
            var type = $(this).attr('data-type');

            $.ajax({
                type: "post",
                async: false,
                url: "/alibaba888/Liv2sky3soLa93vEr62/getCoinScanUrl",
                data: {
                    key:key.trim(),
                    networkType:networkType.trim(),
                    type:type.trim(),
                },
                dataType: "json",
                success: function (data) {
                    if (data != null && data.code == 200) {
                        openNewWindow(data.data);
                    } else {
                        $.global.openErrorMsg(data.msg);
                    }
                },
                error: function () {
                    $.global.openErrorMsg('操作失败，请重试');
                }
            })

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
            // var action = $('#myModalDel #type').attr('action');
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

