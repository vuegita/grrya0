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
            <h5>关注列表</h5>
        </div>

        <div class="ibox-content">
            <div class="btn-group hidden-xs" id="bootstrapTableToolBar" role="group">

                <div class="pull-left hidden" style="margin-right: 10px;">
                    <button id="return-btn" type="button" class="btn btn-outline btn-default" title="返回">
                        <i class="glyphicon" aria-hidden="true"></i> 返回
                    </button>
                    <input type="hidden" name="parentUsernameStack" id="parentUsernameStack" class="form-control input-outline" disabled>
                </div>

                <div class="pull-left" style="margin-right: 10px;">
                    <button id="create-btn" type="button" class="btn btn-outline btn-default" title="新增">
                        <i class="glyphicon glyphicon-plus" aria-hidden="true"></i> 新增
                    </button>
                </div>

<#--                <div class="pull-left" style="margin-right: 10px;">-->
<#--                    <input type="text" name="time" id="time" class="form-control input-outline"  placeholder="请选择创建时间" style="width:200px;">-->
<#--                </div>-->

                <div class="pull-left" style="margin-right: 10px;">
                    <input type="text" name="queryUsername" id="queryUsername" class="form-control input-outline" placeholder="请输入用户名">
                </div>



<#--                <div class="pull-left" style="margin-right: 10px;">-->
<#--                    <input type="text" name="agentname" id="agentname" class="form-control input-outline" placeholder="请输入代理名">-->
<#--                </div>-->

                <div class="pull-left" style="margin-right: 10px;">
                    <select class="form-control" name="type" >
                        <option value="" selected>所有类型</option>
                        <#list followArray as followType>
                            <option value="${followType.getKey()}" >${followType.getTitle()}</option>
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
                    <th data-field="name" data-width="5%" data-align="center"
                        class="title">用户名
                    </th>

<#--                    <th data-field="coinAddress" data-width="5%" data-align="center"-->
<#--                        class="title">钱包地址-->
<#--                    </th>-->
                    <th data-field="" data-width="5%" data-align="left"
                        data-formatter="walletInfoFormatter"
                        class="title">账户地址
                    </th>

                    <th data-field="networkType" data-width="5%" data-align="center"
                        class="title">所属网络
                    </th>

                    <th data-field="type" data-width="5%" data-align="center" data-formatter="typeFormatter"
                        class="title">类型
                    </th>

<#--                    <th data-field="balance" data-width="5%" data-align="center" data-formatter="moneyFormatter"-->
<#--                        class="title">账户余额-->
<#--                    </th>-->

<#--                    <th data-field="freeze" data-width="5%" data-align="center" data-formatter="moneyFormatter"-->
<#--                        class="title">冻结余额-->
<#--                    </th>-->

                    <th data-field="status" data-width="5%" data-align="center"
                        data-formatter="statusFormatter"
                        class="title">状态
                    </th>

<#--                    <th data-field="lastlogintime" data-width="10%" data-align="center"-->
<#--                        class="title">最后登陆时间-->
<#--                    </th>-->

<#--                    <th data-field="lastloginip" data-width="10%" data-align="center"-->
<#--                        class="title">最后登陆IP-->
<#--                    </th>-->

                    <th data-field="createtime" data-width="10%" data-align="center"
                        class="title">创建时间
                    </th>

                    <th data-field="remark" data-width="10%" data-align="center"
                        class="title">备注
                    </th>

                    <th data-field="id" data-formatter="actionFormatter" data-width="15%" data-align="center">操作</th>
                </tr>
                </thead>
            </table>
        </div>
    </div>
</div>



<#include "../../../common/delete_form.ftl">
<script type="text/javascript" src="${STATIC_URL}/plugins/bootstrap-table/bootstrap-table.min.js"></script>
<script type="text/javascript" src="${STATIC_URL}/plugins/bootstrap-table/locale/bootstrap-table-zh-CN.min.js"></script>
<script type="text/javascript" src="${STATIC_URL}/plugins/validate/jquery.validate.min.js"></script>
<script type="text/javascript" src="${STATIC_URL}/plugins/validate/messages_zh.min.js"></script>
<script type="text/javascript" src="${STATIC_URL}/js/validate.defaults.js?v=${version}"></script>
<script type="text/javascript" src="${STATIC_URL}/js/jquery.md5.js"></script>

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
        <#if isCrypto>
        if(row.networkType!="TRX (TronGrid)"){
            result +=  buildScanBrowserItem2("ETH (Mainnet)", row.coinAddress, "account_scan_url");
            result +=  buildScanBrowserItem2("MATIC (Polygon)", row.coinAddress, "account_scan_url");
            result +=  buildScanBrowserItem2("BNB (Mainnet)", row.coinAddress, "account_scan_url") +'<br>';
        }else{
            result +=  buildScanBrowserItem2("TRX (TronGrid)", row.coinAddress, "account_scan_url")+'<br>';
        }

        result += '<a href="javascript:;" class="table-btn toApprovePage" data-no="' + row.coinAddress + '" title="查看本地授权">查看本地授权</a>';
        </#if>

        result += '<a href="javascript:;" class="table-btn detailUserInfo" data-username="' + row.name + '" title="详情">详情</a>';
        result += '<a href="javascript:;" class="table-btn deleteFollow" data-username="' + row.name + '" title="删除">删除</a>';

        return result;
    }
    function buildScanBrowserItem2(networkType, key, type)
    {
        var rs = "";
        if(networkType=="BNB (Mainnet)")
        {
            rs += '<a href="javascript:;" class="table-btn toScanBrowserPage" data-no="' + key + '" data-type="' + type +  '" data-network-type="' + networkType + '" title="BSCScan">BSCScan</a> '+' | ';
        }
        else if(networkType=="ETH (Mainnet)" )
        {
            rs += '<a href="javascript:;" class="table-btn toScanBrowserPage" data-no="' + key + '" data-type="' + type +  '" data-network-type="' + networkType + '" title="ETHScan">ETHScan</a>'+' | ';
        }
        else if(networkType=="MATIC (Polygon)" )
        {
            rs += '<a href="javascript:;" class="table-btn toScanBrowserPage" data-no="' + key + '" data-type="' + type +  '" data-network-type="' + networkType + '" title="ETHScan">PolygonScan</a>'+' | ';
        }
        else if(networkType=="TRX (TronGrid)" )
        {
            rs += '<a href="javascript:;" class="table-btn toScanBrowserPage" data-no="' + key + '" data-type="' + type +  '" data-network-type="' + networkType + '" title="TRONScan">TRONScan</a>'+' | ';
        }

        return rs;
    }

    $(document).on('click', '.table-btn.toApprovePage', function () {
        var address = $(this).attr('data-no');
        var url = '/alibaba888/agent/root_coin_crypto_approve_auth?address=' + address;
        // window.location.href = url;
        openNewWindow(url);
    });

    /**
     * 钱包地址的格式化
     */
    function walletInfoFormatter(value, row, index) {

        var result = "";
        // result += "所属代币 : " + row.currencyType + " (" + row.currencyChainType + ")" + " | 【所属网络: " + row.ctrNetworkType + "】 <br>";
        //result += "钱包地址 : " + row.senderAddress + " <br>";
        result +=  buildScanBrowserItem(row.networkType, row.coinAddress, "account_scan_url");
        // result += "最新余额 : " + row.balance + " <br>";
        // result += "授权额度 : " + row.allowance + " <br>";
        // result += "创建时间 : " + row.createtime + " <br>";


        return result;
    }
    function buildScanBrowserItem(networkType, key, type)
    {
        var rs = "";
        rs += '<a href="javascript:;" class="table-btn toScanBrowserPage" data-no="' + key + '" data-type="' + type +  '" data-network-type="' + networkType + '" title="通过">' + key + '</a> <br>';
        return rs;
    }

    function dateFormatter(value, row, index) {
        if (null == value) {
            return "";
        } else {
            return DateUtils.formatyyyyMMddHHmmss(value);
        }
    }

    function typeFormatter(value, row, index) {
        if(value == "agent")
        {
            return "代理";
        }
        if(value == "staff")
        {
            return "员工";
        }
        return "会员";
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

    function moneyFormatter(value, row, index) {
        return value.toFixed(2);;
    }

    function balanceFormatter(value, row, index) {
        if (isEmpty(value) || value === 0 || value === "0"){
            return "-";
        }
        return value;
    }


    $(function () {
        // resetTime();

        var options = TableUtils.getBtDefaultOptions();
        options.url = '/alibaba888/agent/getUserSystemFollowList';
        options.search = false;
        options.showRefresh = true;
        options.queryParams = function (params) {
            params.username = $('input[name="queryUsername"]').val();
            params.agentname = $('#agentname').val();

            params.time = $('input[name="time"]').val();
            params.type = $('select[name="type"]').val();
            return params;
        };
        var bootstrapTable = $('#bootstrapTable').bootstrapTable(options);

        $(document).on('click', '#create-btn', function(){
            var url = "/alibaba888/agent/toEditSystemUserFollowPage";
            //window.location.href ='/alibaba888/?????/toEditSystemUserFollowPage';
            openNewWindow(url);
        });

        $(document).on('click', '.table-btn.detailUserInfo', function () {
            var username = $(this).attr('data-username');
            var url = '/alibaba888/Liv2sky3soLa93vEr62/passport/user/history_detail/page?username=' + username;
            openNewWindow(url);
        });

        $(document).on('click', '.table-btn.deleteFollow', function () {
            var username = $(this).attr('data-username');
            $('#myModalDel #id').val(username);
            $('#myModalDel #type').attr('action', 'deleteFollow');
            $('#myModalDel #tips').text('确定删除关注吗？');
            $('#myModalDel').modal();
        });

        $('#delete_submit').click(function () {
            var username = $('#myModalDel #id').val();
            var value = $('#myModalDel #type').val();
            var action = $('#myModalDel #type').attr('action');

            if("deleteFollow" == action)
            {
                deleteFollow(username, value)
            }
        });

        $('#search-btn').click(function () {
            refresh();
        });

        $('#reset-btn').click(function () {
            $('input[name="agentname"]').val('');
            $('input[name="queryUsername"]').val('');
            resetTime();
        });


        $(document).on('click', '.table-btn.toScanBrowserPage', function () {
            var key = $(this).attr('data-no');
            var networkType = $(this).attr('data-network-type');
            var type = $(this).attr('data-type');

            $.ajax({
                type: "post",
                async: false,
                url: "/alibaba888/agent/getCoinScanUrl",
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

    });

    function refresh() {
        $('#bootstrapTable').bootstrapTable('refresh');
    }

    function deleteFollow(username) {
        $.ajax({
            url: '/alibaba888/agent/deleteUserSystemFollow',
            type: 'post',
            dataType: 'json',
            data: {username:username},
            success: function (result) {
                if (result.code === 200) {
                    $.global.openSuccessMsg(result.msg, function(){
                        refresh();
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
