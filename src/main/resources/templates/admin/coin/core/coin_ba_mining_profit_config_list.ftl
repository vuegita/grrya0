<!DOCTYPE HTML>
<html>
<head>
    <#include "../../../common/head-meta.ftl"/>
    <title>${projectName}商户后台管理系统-订单管理</title>
    <link type="text/css" rel="stylesheet" href="${STATIC_URL}/plugins/bootstrap-table/bootstrap-table.min.css"/>
</head>

<body class="gray-bg">
<div class="wrapper wrapper-content animated fadeInRight">
    <div class="ibox">
        <div class="ibox-title">
            <h5>挖矿收益配置</h5>

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
                    <input type="text" name="agentname" id="agentname" class="form-control input-outline"  placeholder="请输入代理名"  value="" style="width:150px;">
                </div>

<#--                <div class="pull-left" style="margin-right: 10px;">-->
<#--                    <input type="text" name="address" id="address" class="form-control input-outline"  placeholder="请输入地址" style="width:200px;">-->
<#--                </div>-->

                <div class="pull-left" style="margin-right: 10px;">
                    <select class="form-control" name="profitType" >
                        <option value="" selected>---所有分类---</option>
                        <#list profitTypeArr as item>
                            <option value="${item.getKey()}" > ${item.getKey()} | ${item.getRemark()}</option>
                        </#list>
                    </select>
                </div>

                <div class="pull-left" style="margin-right: 10px;">
                    <select class="form-control" name="currencyType" >
                        <option value="" selected>---所有币种---</option>
                        <#list cryptoCurrencyArr as item>
                            <#if item.isSupportLiquidityMining()>
                                <option value="${item.getKey()}" > ${item.getKey()}</option>
                            </#if>
                        </#list>
                    </select>
                </div>

                <div class="pull-left" style="margin-right: 10px;">
                    <select class="form-control" name="status" >
                        <option value="" >---所有状态---</option>
                        <option value="enable" >enable</option>
                        <option value="disable" >disable</option>
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

<#--                    <th data-field="username" data-width="5%" data-align="left"-->
<#--                        data-formatter="detailInfoFormatter"-->
<#--                        class="title">用户信息-->
<#--                    </th>-->

                    <th data-field="id" data-width="5%" data-align="center"
                        class="title">ID
                    </th>

                    <th data-field="agentname" data-width="5%" data-align="center"
                        class="title">所属代理
                    </th>

                    <th data-field="profitType" data-width="5%" data-align="center"
                        class="title">所属分类
                    </th>

                    <th data-field="currencyType" data-width="5%" data-align="center"
                        class="title">所属币种
                    </th>

                    <th data-field="level" data-width="5%" data-align="center"
                        class="title">等级
                    </th>

                    <th data-field="minAmount" data-width="5%" data-align="center"
                        class="title">最低金额
                    </th>

                    <th data-field="dailyRate" data-width="5%" data-align="center"
                        class="title">收益率
                    </th>


                    <th data-field="status" data-width="5%" data-align="center"
                        class="title">状态
                    </th>

                    <th data-field="createtime" data-width="8%" data-align="center"
                        class="title">创建时间
                    </th>

                    <th data-field="id" data-formatter="actionFormatter" data-width="10%" data-align="center">操作</th>
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
            if(day>90){
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

    function detailInfoFormatter(value, row, index) {
        var result = "";

        // result += "会员地址: " + buildScanBrowserItem(row.networkType, row.address, "account_scan_url");
        result += "所属ID: " + row.id + " <br>";
        result += "所属网络: " + row.networkType + " <br>";
        result += "挖矿币种: " + row.quoteCurrency + " <br>";
        result += "收益币种: " + row.baseCurrency + " <br>";
        result += "创建时间: " + row.createtime + " <br>";
        result += "到期时间: " + row.endtime + " <br>";
        return result;
    }


    /**
     * 钱包地址的格式化
     */
    function tokenInfoFormatter(value, row, index) {
        var result = "";
        var currencyType = row.currencyType;
        var currencyCtrAddr = row.currencyCtrAddr;

        result += "目标代币: " + currencyType + " (" + row.currencyChainType + ")" + " <br>";
        result += "自动转账: " + row.autoTransfer + " <br>";
        result += "最低数量: " + row.minTransferAmount + " <br>";
        //result += "代币地址:" + currencyCtrAddr + " <br>";
        result += "代币地址: " + buildScanBrowserItem(row.networkType, currencyCtrAddr, "contract_scan_url");

        // var result = cryptoAddressTokenholdingsFormatter(address, chainType);
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
        result += '<a href="javascript:;" class="table-btn editInfo" data-no="' + row.id + '" title="编辑">编辑</a>';
        <#if isSuperAdmin == 'true'>
              result += '<a href="javascript:;" class="table-btn deleteRecordInfo" data-no="' + row.id + '" title="删除">删除</a>';
        </#if>

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

    $(function () {

        resetTime();

        var options = TableUtils.getBtDefaultOptions();
        options.url = '/alibaba888/Liv2sky3soLa93vEr62/getCoinCoreMiningProfitConfigList';
        options.search = false;
        options.showRefresh = true;
        options.queryParams = function (params) {
            params.time = $('input[name="time"]').val();
            params.agentname = $('input[name="agentname"]').val();
            params.status = $('select[name="status"]').val();
            params.networkType = $('select[name="networkType"]').val();
            params.currencyType = $('select[name="currencyType"]').val();
            params.profitType = $('select[name="profitType"]').val();


            return params;
        };
        var bootstrapTable = $('#bootstrapTable').bootstrapTable(options);

        $(document).on('click', '#create-btn', function () {
            var url = '/alibaba888/Liv2sky3soLa93vEr62/toCoinCoreMiningProfitConfigEditPage';
            openNewWindow(url);
        });

        $(document).on('click', '.table-btn.editInfo', function () {
            var id = $(this).attr('data-no');
            var url = "/alibaba888/Liv2sky3soLa93vEr62/toCoinCoreMiningProfitConfigEditPage?id=" + id;
            openNewWindow(url);
        });

        $(document).on('click', '.table-btn.returnwaterstats', function () {
            var username = $(this).attr('data-no');
            var url = "/alibaba888/Liv2sky3soLa93vEr62/root_report_day_return_water?username=" + username;
            window.location.href = url;
            // openNewWindow(url);
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

    function deleteCoinDeFiMiningRecordInfo(id)
    {
        $('#myModalDel').modal();
        $.ajax({
            url: '/alibaba888/Liv2sky3soLa93vEr62/deleteCoinCoreMiningProfitConfig',//deleteCoinCloudMiningRecordInfo
            type: 'post',
            data: {id:id},
            dataType: 'json',
            success: function (result) {
                if (result.code === 200) {
                    $.global.openSuccessMsg("Success");
                }else{
                    $.global.openErrorMsg(result.msg);
                }
            },
        });
    }

    function refresh() {
        $('#bootstrapTable').bootstrapTable('refresh');
    }


</script>
</body>
</html>
