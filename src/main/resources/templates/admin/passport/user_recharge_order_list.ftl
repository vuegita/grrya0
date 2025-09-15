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
            <h5>充值管理</h5>

            <p style="color: blue"> 　</p>
            <p  style="color: green">1. Bank支付渠道为C2C个卡充值! </p>
            <p  style="color: green">2. pending状态需要人工审核确认并上分! </p>
<#--            <p  style="color: green">2. 上游三方平台一般都会有风控 !</p>-->
<#--            <p  style="color: green">3. 个人支付每天都有上限，具体看用户所用的服务!</p>-->
<#--            <p  style="color: green">4. 网银支付每天限额会高一点!</p>-->
        </div>

        <div class="ibox-content">

            <div  style="width: 100%;height: 5rem;">
                <#--                    <div class="pull-left" style="margin-right: 10px;">-->
                <#--                        <input type="text" name="nototalBetAmount" id="nototalBetAmount" class="form-control input-outline"  placeholder="失败划转总额: " readonly style="width:200px;">-->
                <#--                    </div>-->

                <div class="pull-left" style="margin-right: 10px;">
                    <input type="text" name="yestotalBetAmount" id="yestotalBetAmount" class="form-control input-outline"  placeholder="成功充值汇总: " readonly style="width:200px;">
                </div>


                <div class="pull-left" style="margin-left: 10px;">
                    <button id="refuseAudit-btn" type="button" class="btn btn-outline btn-default" title="选中项统计">
                        统计金额
                    </button>
                </div>

                <#--                    <div class="pull-left" style="margin-left: 10px;">-->
                <#--                        <button id="refuseAudit2-btn" type="button" class="btn btn-primary" title="结算订单">-->
                <#--                            统计金额详情-->
                <#--                        </button>-->
                <#--                    </div>-->

                <#--                    <div class="pull-left" style="margin-left: 10px;">-->
                <#--                        <button id="refuseAudit3-btn" type="button" class="btn btn-primary" title="结算订单">-->
                <#--                            统计金额详情汇总-->
                <#--                        </button>-->
                <#--                    </div>-->


            </div>


            <div class="pull-left" style="margin-right: 10px;">
                <input type="text" name="time" id="time" class="form-control input-outline"  placeholder="请选择时间" style="width:200px;">
            </div>

            <div class="pull-left" style="margin-right: 10px;">
                <input type="text" name="username" id="username" class="form-control input-outline"  placeholder="请输入用户名" style="width:200px;">
            </div>

            <div class="pull-left" style="margin-right: 10px;">
                <input type="text" name="agentname" id="agentname" class="form-control input-outline"  placeholder="请输入代理用户名" style="width:150px;">
            </div>

            <div class="pull-left" style="margin-right: 10px;">
                <input type="text" name="staffname" id="staffname" class="form-control input-outline"  placeholder="请输入直属员工名" style="width:150px;">
            </div>

            <div class="pull-left" style="margin-right: 10px;">
                <input type="text" name="systemOrderno" id="systemOrderno" class="form-control input-outline"  placeholder="请输入系统订单号" style="width:150px;">
            </div>

            <div class="pull-left" style="margin-right: 10px;">
                <input type="text" name="outTradeNo" id="outTradeNo" class="form-control input-outline"  placeholder="请输入外部订单号" style="width:150px;">
            </div>

            <div class="pull-left" style="margin-right: 10px;">
                <select class="form-control" name="txStatus" >
                    <option value="" >---订单状态---</option>
                    <option value="new" >new</option>
                    <option value="pending" >pending</option>
                    <option value="captured" >captured</option>
                    <option value="realized" >realized</option>
                    <option value="failed" >failed</option>
                </select>
            </div>

            <div class="pull-left" style="margin-right: 10px;">
                <select class="form-control" name="channelid" >
                    <option value="" >---所有通道---</option>
                    <#list channelList as item>
                        <option value="${item.id}" >${item.name}</option>
                    </#list>
                </select>
            </div>


            <button id="search-btn" type="button" class="btn btn-outline btn-default" title="查询">
                <i class="glyphicon glyphicon-search" aria-hidden="true"></i> 查询
            </button>
            <button id="reset-btn" type="button" class="btn btn-outline btn-default" title="重置">
                <i class="glyphicon glyphicon-repeat" aria-hidden="true"></i> 重置
            </button>





            <div class="btn-group hidden-xs" id="bootstrapTableToolBar" role="group">





            </div>
            <table id="bootstrapTable">
                <thead>
                <tr>
                    <th  data-width="2%" data-align="center"  data-checkbox="true"
                         class="title">
                    </th>

                    <th data-field="no" data-width="8%" data-align="center"
                        class="title">系统订单号
                    </th>

                    <th data-field="outTradeNo" data-width="8%" data-align="center"
                        class="title">外部订单号
                    </th>

<#--                    <th data-field="payProductType" data-width="5%" data-align="center"-->
<#--                        class="title">支付渠道-->
<#--                    </th>-->

<#--                    <th data-field="userid" data-width="5%" data-align="center"-->
<#--                        class="title">用户ID-->
<#--                    </th>-->

                    <th data-field="username" data-width="5%" data-align="center"
                        class="title">用户名
                    </th>

                    <th data-field="agentname" data-width="5%" data-align="center"
                        class="title">所属代理
                    </th>

                    <th data-field="staffname" data-width="5%" data-align="center"
                        class="title">所属员工
                    </th>


                    <th data-field="amount" data-width="5%" data-align="center" data-sortable="true"
                        class="title">订单金额
                    </th>
<#--                    <th data-field="type" data-width="5%" data-align="center" data-formatter="typeFormatter"-->
<#--                        class="title">订单类型-->
<#--                    </th>-->
                    <th data-field="status" data-width="5%" data-align="center"
<#--                        data-formatter="statusFormatter"-->
                        class="title">订单状态
                    </th>
                    <th data-field="" data-width="5%" data-align="center" data-formatter="channelFormatter"
                        class="title">支付渠道
                    </th>
                    <th data-field="createtime" data-width="10%" data-align="center"
                        class="title">创建时间
                    </th>
<#--                    data-sortable="true"-->
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

        result += '<a href="javascript:;" class="table-btn toaAditOrderPage" data-no="' + row.no + '" title="查看">查看</a>';
        if(row.status == 'new' || row.status == 'pending' || row.status == 'captured')
        {
            result += '<a href="javascript:;" class="table-btn detailUserInfo" data-username="' + row.username + '" title="详情">详情</a>';
            result += '<a href="javascript:;" class="table-btn auditPass" data-username="' + row.username + '" data-no="' + row.no + '" title="通过">通过</a>';
            result += '<a href="javascript:;" class="table-btn auditDispass" data-username="' + row.username + '" data-no="' + row.no + '" title="拒绝">拒绝</a>';
        }
        else
        {
            //result = "-";
            result += '<a href="javascript:;" class="table-btn detailUserInfo" data-username="' + row.username + '" title="详情">详情</a>';
        }


        return result;
    }
    $(document).on('click', '.table-btn.detailUserInfo', function () {
        var username = $(this).attr('data-username');
        var url = '/alibaba888/Liv2sky3soLa93vEr62/passport/user/history_detail/page?username=' + username;
        // window.location.href = url;
        openNewWindow(url);
    });

    function channelFormatter(value, row, index) {
        var rs = '';
        var jsonObj = JSON.parse(row.remark);
        if(jsonObj)
        {
            if(!isEmpty(jsonObj.channelName))
            {
                return jsonObj.channelName;
            }
        }
        return row.payProductType;
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

    function statusFormatter(value, row, index) {
        return $.global.constant.getOrderStatusMsg(value);
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
        options.url = '/alibaba888/Liv2sky3soLa93vEr62/getRechargeOrderList';
        options.search = false;
        options.clickToSelect= true;
        options.showRefresh = true;
        options.queryParams = function (params) {
            params.time = $('input[name="time"]').val();
            params.username = $('input[name="username"]').val();
            params.agentname = $('input[name="agentname"]').val();
            params.staffname = $('input[name="staffname"]').val();
            params.parentname = $('input[name="parentname"]').val();
            params.grantname = $('input[name="grantname"]').val();

            params.systemOrderno = $('input[name="systemOrderno"]').val();
            params.outTradeNo = $('input[name="outTradeNo"]').val();

            params.txStatus = $('select[name="txStatus"]').val();
            params.channelid = $('select[name="channelid"]').val();

            params.sortName=params.sort;
            params.sortOrder=params.order;
            return params;
        };
        var bootstrapTable = $('#bootstrapTable').bootstrapTable(options);

        $(document).on('click', '#create-btn', function () {
            window.location.href = '/alibaba888/Liv2sky3soLa93vEr62/toApplyPlatformSupplyPage';
        });


        $('#search-btn').click(function () {
            refresh();
        });

        $('#reset-btn').click(function () {
            $('input[name="username"]').val('');
            resetTime();
            //refresh();
        });

        $(document).on('click', '.table-btn.toaAditOrderPage', function () {
            var no = $(this).attr('data-no');
            var url = '/alibaba888/Liv2sky3soLa93vEr62/root_passport_user_recharge_order_audit_result_page?orderno=' + no;
            openNewWindow(url);
        });

        $(document).on('click', '.table-btn.auditPass', function () {
            var tradeno = $(this).attr('data-no');
            var username = $(this).attr('data-username');
            $('#myModalDel #id').val(tradeno);
            $('#myModalDel #type').val(username);
            $('#myModalDel #type').attr('action', 'pass');
            $('#myModalDel #tips').text('确定审核通过吗？');
            $('#myModalDel').modal();

        });

        $(document).on('click', '.table-btn.auditDispass', function () {
            var tradeno = $(this).attr('data-no');
            var username = $(this).attr('data-username');
            $('#myModalDel #id').val(tradeno);
            $('#myModalDel #type').val(username);
            $('#myModalDel #type').attr('action', 'dispass');
            $('#myModalDel #tips').text('确定审核拒绝吗？');
            $('#myModalDel').modal();

        });

        $('#delete_submit').click(function () {
            var outTradeNo = $('#myModalDel #id').val();
            var username = $('#myModalDel #type').val();
            var action = $('#myModalDel #type').attr('action');
            auditOrder(outTradeNo, username, action)
        });

    });

    function refresh() {
        $('#bootstrapTable').bootstrapTable('refresh');
    }

    function auditOrder(orderno, username, action) {
        $.ajax({
            url: '/alibaba888/Liv2sky3soLa93vEr62/doAuditUserRechargeOrder',
            type: 'post',
            dataType: 'json',
            data: {
                orderno: orderno,
                action: action,
            },
            success: function (result) {
                if (result.code == 200) {
                    $.global.openSuccessMsg(result.msg, function(){
                        refresh();
                    });
                    return;
                }
                $.global.openErrorMsg(result.msg);
            },
            error: function () {
                $.global.openErrorMsgCollback('系统异常,操作失败', function () {

                });
            }
        });
    }

    $('#refuseAudit-btn').click(function () {
        var selected = $('#bootstrapTable').bootstrapTable('getSelections');

        //返回值为数据对象数组
        if(selected&&selected.length>0){
            //非空数组时候进行的操作
            var slength = selected.length;

            var nototalBetAmount = 0;
            var yestotalBetAmount = 0;

            for(var i=0;i<selected.length;i++){

                if( selected[i].status =='realized'){
                    yestotalBetAmount = yestotalBetAmount + selected[i].amount;
                }else{
                    nototalBetAmount = nototalBetAmount + selected[i].totalAmount;
                }

            }

           // $("#nototalBetAmount").prop("value", "失败划转总额: " + nototalBetAmount);
            $("#yestotalBetAmount").prop("value", "成功充值汇总: " + yestotalBetAmount);




        }else{
            //空数组的操作
            $.global.openErrorMsg('请先选择要操作的数据!');
        }

    });


</script>
</body>
</html>
