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
            <h5>返首充给上级订单</h5>
        </div>

        <div class="ibox-content">
            <div class="btn-group hidden-xs" id="bootstrapTableToolBar" role="group">


<#--                <div class="pull-left" style="margin-right: 10px;">-->
<#--                    <button id="create-btn" type="button" class="btn btn-outline btn-default" title="补单">-->
<#--                        <i class="glyphicon glyphicon-plus" aria-hidden="true"></i> 补单-->
<#--                    </button>-->
<#--                </div>-->

                <div class="pull-left" style="margin-right: 10px;">
                    <input type="text" name="time" id="time" class="form-control input-outline"  placeholder="请选择时间" style="width:200px;">
                </div>

                <div class="pull-left" style="margin-right: 10px;">
                    <input type="text" name="username" id="username" class="form-control input-outline"  placeholder="请输入用户名" style="width:200px;">
                </div>

                <div class="pull-left" style="margin-right: 10px;">
                    <input type="text" name="systemOrderno" id="systemOrderno" class="form-control input-outline"  placeholder="请输入系统订单号" style="width:150px;">
                </div>

<#--                <div class="pull-left" style="margin-right: 10px;">-->
<#--                    <select class="form-control" name="txStatus" >-->
<#--                        <option value="" >---订单状态---</option>-->
<#--                        <option value="new" >new</option>-->
<#--                        <option value="new" >pending</option>-->
<#--                        <option value="waiting" >waiting</option>-->
<#--                        <option value="realized" >realized</option>-->
<#--                        <option value="failed" >failed</option>-->
<#--                    </select>-->
<#--                </div>-->

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
                    <th data-field="no" data-width="5%" data-align="center"
                        class="title">系统订单号
                    </th>
                    <th data-field="outTradeNo" data-width="5%" data-align="center"
                        class="title">外部订单号
                    </th>
                    <th data-field="username" data-width="5%" data-align="center"
                        class="title">用户名
                    </th>
<#--                    <th data-field="currency" data-width="5%" data-align="center"-->
<#--                        class="title">所属币种-->
<#--                    </th>-->
                    <th data-field="amount" data-width="5%" data-align="center"
                        class="title">订单金额
                    </th>
                    <th data-field="status" data-width="5%" data-align="center"
                        class="title">订单状态
                    </th>
                    <th data-field="" data-width="5%" data-align="center" data-formatter="columnFormatterForOrderRemarkMsg"
                        class="title">备注信息
                    </th>
                    <th data-field="createtime" data-width="10%" data-align="center"
                        class="title">创建时间
                    </th>
<#--                    <th data-field="no" data-width="8%" data-align="center" data-formatter="actionFormatter"-->
<#--                        class="title">操作-->
<#--                    </th>-->
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
    //     result += '<a href="javascript:;" class="table-btn passAudit" data-no="' + row.no + '" title="通过">通过</a>';
    //     result += '<a href="javascript:;" class="table-btn refuseAudit" data-no="' + row.no + '" title="修改密码">拒绝</a>';
    //
    //     return result;
    // }

    function actualMoneyFormatter(value, row, index) {
        var amount = row.amount;
        var feemoney = row.feemoney;
        return (Number(amount) - Number(feemoney) ).toFixed(2);
    }

    function beneficiaryFormatter(value, row, index) {
        var rs = '';

        var jsonObj = JSON.parse(row.remark);
        if(jsonObj)
        {
            rs += "Name: " + jsonObj.beneficiaryName + "<br>";
            rs += "IFSC: " + jsonObj.ifsc + "<br>";
            rs += "Account: " + jsonObj.account + "<br>";
            rs += "Email: " + jsonObj.beneficiaryEmail + "<br>";
            rs += "Phone: " + jsonObj.beneficiaryPhone + "<br>";
        }
        return rs;
    }

    function errmsgFormatter(value, row, index) {
        var jsonObj = JSON.parse(row.remark);
        if(jsonObj)
        {
            var msg = jsonObj.errmsg;
            if(msg && msg !== "success")
            {
                return msg;
            }
        }
        return '-';
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
        return $.global.constant.getOrderStatusMsg(value);
    }

    $(function () {

        resetTime();

        var options = TableUtils.getBtDefaultOptions();
        options.url = '/alibaba888/Liv2sky3soLa93vEr62/root_passport_return_first_recharge_up_record/getDataList';
        options.search = false;
        options.showRefresh = true;
        options.queryParams = function (params) {
            params.time = $('input[name="time"]').val();
            params.username = $('input[name="username"]').val();
            params.systemOrderno = $('input[name="systemOrderno"]').val();
            params.type = $('select[name="type"]').val();
            params.txStatus = $('select[name="txStatus"]').val();
            return params;
        };
        var bootstrapTable = $('#bootstrapTable').bootstrapTable(options);

        $(document).on('click', '#create-btn', function () {
            window.location.href = '/alibaba888/Liv2sky3soLa93vEr62/toApplyPlatformSupplyPage';
        });

        $(document).on('click', '.table-btn.passAudit', function () {
            var no = $(this).attr('data-no');
            $('#myModalDel #id').val(no);
            $('#myModalDel #type').attr('action', 'pass');
            $('#myModalDel #tips').text('确定审核通过吗？');
            $('#myModalDel').modal();
        });

        $(document).on('click', '.table-btn.updatePassword', function () {
            var no = $(this).attr('data-no');
            $('#myModalDel #id').val(no);
            $('#myModalDel #type').attr('action', 'refuse');
            $('#myModalDel #tips').text('确定审核拒绝吗？');
            $('#myModalDel').modal();
        });

        $('#delete_submit').click(function () {
            var username = $('#myModalDel #id').val();
            var value = $('#myModalDel #type').val();
            var action = $('#myModalDel #type').attr('action');

            if("action" == action)
            {
                updateUserType(username, value)
            }
            else if("refuse" == action)
            {
                updateUserStatus(username, value)
            }
        });

        $('#search-btn').click(function () {
            refresh();
        });

        $('#reset-btn').click(function () {
            $('input[name="username"]').val('');
            resetTime();
            //refresh();
        });

    });

    function refresh() {
        $('#bootstrapTable').bootstrapTable('refresh');
    }



</script>
</body>
</html>
