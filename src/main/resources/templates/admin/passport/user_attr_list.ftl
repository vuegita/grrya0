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
            <h5>会员详情列表</h5>
        </div>

        <div class="ibox-content">
            <div class="pull-left" style="margin-right: 10px;">
                <input type="text" name="time" id="time" class="form-control input-outline"  placeholder="请选择时间" style="width:200px;">
            </div>

            <div class="pull-left" style="margin-right: 10px;">
                <input type="text" name="username" id="username" class="form-control input-outline"  placeholder="请输入用户名" style="width:150px;">
            </div>

            <div class="pull-left" style="margin-right: 10px;">
                <input type="text" name="agentname" id="agentname" class="form-control input-outline"  placeholder="请输入代理名" style="width:150px;">
            </div>

            <div class="pull-left" style="margin-right: 10px;">
                <input type="text" name="staffname" id="staffname" class="form-control input-outline"  placeholder="请输入员工名" style="width:150px;">
            </div>

            <div class="pull-left" style="margin-right: 10px;">
                <input type="text" name="parentname" id="parentname" class="form-control input-outline"  placeholder="一级上级"  value="${parantname!}" style="width:150px;">
            </div>

            <div class="pull-left" style="margin-right: 10px;">
                <input type="text" name="grantname" id="grantname" class="form-control input-outline"  placeholder="二级上级" value="${grantname!}" style="width:150px;">
            </div>

            <#--                <div class="pull-left" style="margin-right: 10px;">-->
            <#--                    <select class="form-control" name="type" >-->
            <#--                        <option value="" >---订单类型---</option>-->
            <#--                        <option value="platform_recharge" >平台充值</option>-->
            <#--                        <option value="platform_deduct" >平台扣款</option>-->
            <#--                        <option value="platform_presentation" >平台赠送</option>-->
            <#--                    </select>-->
            <#--                </div>-->
            <div class="pull-left" style="margin-right: 10px;">
                <select class="form-control" name="Status" >
                    <option value="" >---代理状态---</option>
                    <option value="enable" >启用</option>
                    <option value="disable" >禁用</option>
                    <option value="freeze" >冻结</option>

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
<#--                    <th data-field="id" data-formatter="noActionFormatter" data-width="5%" data-align="center">#</th>-->

<#--                    <th data-field="username" data-width="2%" data-align="center"-->
<#--                        class="title">用户名-->
<#--                    </th>-->

                    <th data-field="id" data-formatter="detailInfoFormatter" data-width="10%" data-align="left">用户明细</th>

                    <th data-field="balance" data-width="5%" data-align="center"  data-sortable="true"
                        class="title">账户余额
                    </th>

                    <th data-field="totalRecharge" data-width="5%" data-align="center" data-sortable="true"
                        class="title">总充值
                    </th>

                    <th data-field="totalWithdraw" data-width="5%" data-align="center"  data-sortable="true"
                        class="title">总提现
                    </th>
<#--                    data-formatter="withdrawFormatter"-->

<#--                    <th data-field="totalRefund" data-width="5%" data-align="center"-->
<#--                        class="title">总退款-->
<#--                    </th>-->

                    <th data-field="freeze" data-width="5%" data-align="center"
                        class="title">冻结金额
                    </th>

                    <th data-field="codeAmount" data-width="5%" data-align="center"
                        class="title">可提打码量
                    </th>

                    <th data-field="limitAmount" data-width="5%" data-align="center"
                        class="title">不可提金额
                    </th>
                    <th data-field="limitCode" data-width="5%" data-align="center"
                        class="title">不可提打码
                    </th>


                    <th data-field="level1Count" data-width="5%" data-align="center"  data-sortable="true"
                        class="title">一级下级人数
                    </th>

                    <th data-field="level2Count" data-width="5%" data-align="center"  data-sortable="true"
                        class="title">二级下级人数
                    </th>



<#--                    <th data-field="inviteFriendTotalAmount" data-width="8%" data-align="center"-->
<#--                        class="title">邀请赠送总额-->
<#--                    </th>-->

<#--                    <th data-field="returnWater" data-width="5%" data-align="center"-->
<#--                        class="title">返佣比例-->
<#--                    </th>-->

<#--                    <th data-field="agentname" data-width="5%" data-align="center"-->
<#--                        class="title">所属代理-->
<#--                    </th>-->

<#--                    <th data-field="directStaffname" data-width="5%" data-align="center"-->
<#--                        class="title">直属员工上级-->
<#--                    </th>-->

<#--                    <th data-field="parentname" data-width="5%" data-align="center"-->
<#--                        class="title">父级上级-->
<#--                    </th>-->

<#--                    <th data-field="grantfathername" data-width="5%" data-align="center"-->
<#--                        class="title">祖父级上级-->
<#--                    </th>-->

<#--                    <th data-field="firstRechargeOrderno" data-width="5%" data-align="center"-->
<#--                        class="title">首充订单号-->
<#--                    </th>-->




<#--                    <th data-field="level" data-width="5%" data-align="center"-->
<#--                        class="title">等级-->
<#--                    </th>-->

<#--                    <th data-field="remark" data-width="8%" data-align="center"-->
<#--                        class="title">备注-->
<#--                    </th>-->




<#--                    <th data-field="agentAdminLoginStatus" data-width="8%" data-align="center"-->
<#--                        class="title">代理状态-->
<#--                    </th>-->

                    <th data-field="userLastlogintime" data-width="8%" data-align="center"  data-sortable="true"
                        class="title">最近登录时间
                    </th>



                    <th data-field="id" data-formatter="actionFormatter" data-width="10%" data-align="center">操作</th>
                </tr>
                </thead>
            </table>
        </div>
    </div>
</div>

<#include "../../common/delete_form.ftl">
<#include "user_attr_list_edit_info.ftl">
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


    function noActionFormatter(value, row, index) {

        var pageSize=$('#bootstrapTable').bootstrapTable('getOptions').pageSize;//通过表的#id 可以得到每页多少条
        var pageNumber=$('#bootstrapTable').bootstrapTable('getOptions').pageNumber;//通过表的#id 可以得到当前第几页
        return pageSize * (pageNumber - 1) + index + 1;//返回每条的序号： 每页条数 * （当前页 - 1 ）+ 序号

    }

    /**
     * 用户明细
     */
    function detailInfoFormatter(value, row, index) {

        var result = "";

        var toFixedValue = 2;
        <#if systemRunningMode=='crypto'>
            toFixedValue = 6;
        </#if>
        // result += "账户余额: " + row.balance.toFixed(toFixedValue) + " <br>";
        // result += "冻结余额: " + row.freeze.toFixed(toFixedValue) + " <br>";
        result += "用 户 名: " + row.username + " <br>";
        result += "所属代理: " + row.agentname + " <br>";
        result += "所属员工: " + row.directStaffname + " <br>";
        result += "一级上级: " + row.parentname + " <br>";
        result += "二级上级: " + row.grantfathername + " <br>";
        result += "币种类型: " + row.currency + " | (" + row.fundKey + ")" + " <br>";
        result += "用户等级: " + row.level + " <br>";
        result += "首充订单: " + row.firstRechargeOrderno + " <br>";
        result += "注册时间: " + row.regtime + " <br>";

        result += "<br>自定义返佣状态: " + row.returnLevelStatus + " <br>";
        result += "返佣Lv1利率: " + row.returnLv1Rate + " <br>";
        result += "返佣Lv2利率: " + row.returnLv2Rate + " <br>";
        result += "接受Lv1利率: " + row.receivLv1Rate + " <br>";
        result += "接受Lv2利率: " + row.receivLv2Rate + " <br>";

        result += "备注信息: " + row.remark + " <br>";

       if(row.userRemark!=undefined && row.userRemark!=null && row.userRemark!=""){

           var jsonObj = JSON.parse(row.userRemark);
           if(jsonObj)
           {
               if(jsonObj.telegramName==undefined || jsonObj.telegramName==null ){
                   result += "telegram用户名: " + " " + "<br>";
               }else{
                   result += "telegram用户名: " + jsonObj.telegramName + "<br>";
               }

           }
       }


        return result;
    }



    /**
     * 操作栏的格式化
     */
    function actionFormatter(value, row, index) {
        var result = "";
        if(row.username == 'super')
        {
            return  "系统内部账号";
        }
        result += '<a href="javascript:;" class="table-btn detailUserInfo" data-no="' + row.username + '" title="详情">详情</a>';
        result += '<a href="javascript:;" class="table-btn editInfo" data-no="' + row.username + '" data-level="' + row.level + '" data-remark="' + row.remark + '" title="编辑">编辑</a>';
        result += '<a href="javascript:;" class="table-btn updateCodeAmount" data-no="' + row.username + '" title="更新打码量">更新打码量</a>';
        result += '<a href="javascript:;" class="table-btn returnwaterstats" data-no="' + row.username + '" title="返佣">返佣</a>';
        result += '<a href="javascript:;" class="table-btn totaluser" data-username="' + row.username + '" title="查看下级汇总">查看下级汇总</a>';
        result += '<a href="javascript:;" class="table-btn editUserAttrInfo" data-username="' + row.username + '" title="设置属性">设置属性</a>';
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

        //resetTime();

        var options = TableUtils.getBtDefaultOptions();
        options.url = '/alibaba888/Liv2sky3soLa93vEr62/getUserAttrList';
        options.search = false;
        options.showRefresh = true;
        options.queryParams = function (params) {
            params.time = $('input[name="time"]').val();
            params.username = $('input[name="username"]').val();
            params.agentname = $('input[name="agentname"]').val();
            params.staffname = $('input[name="staffname"]').val();
            params.parentname = $('input[name="parentname"]').val();
            params.grantname = $('input[name="grantname"]').val();
            params.strStatus= $('select[name="Status"]').val();

            params.sortName=params.sort;
            params.sortOrder=params.order;
            return params;
        };
        var bootstrapTable = $('#bootstrapTable').bootstrapTable(options);

        // $(document).on('click', '#create-btn', function () {
        //     window.location.href = '/alibaba888/Liv2sky3soLa93vEr62/toAddUserBankCardPage';
        // });
        //

        $(document).on('click', '.table-btn.detailUserInfo', function () {
            var username = $(this).attr('data-no');
            var url = '/alibaba888/Liv2sky3soLa93vEr62/passport/user/history_detail/page?username=' + username;
            // window.location.href = url;
            openNewWindow(url);
        });

        $(document).on('click', '.table-btn.editUserAttrInfo', function () {
            var username = $(this).attr('data-username');
            var url = '/alibaba888/Liv2sky3soLa93vEr62/toEditUserAttrPage?username=' + username;
            openNewWindow(url);
        });

        $(document).on('click', '.table-btn.editInfo', function () {
            var username = $(this).attr('data-no');
            var level = $(this).attr('data-level');
            var remark = $(this).attr('data-remark');
            showEditModal(username, level, remark)
        });
        $(document).on('click', '.table-btn.updateCodeAmount', function () {
            var username = $(this).attr('data-no');
            var url = "/alibaba888/Liv2sky3soLa93vEr62/toUpdateUserAttrCodeAmountPage?username=" + username;
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

    function refresh() {
        $('#bootstrapTable').bootstrapTable('refresh');
    }

    $(document).on('click', '.table-btn.totaluser', function () {
        var username = $(this).attr('data-username');

        $.ajax({
            url: '/alibaba888/Liv2sky3soLa93vEr62/getUserAttr',
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
