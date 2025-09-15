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
            <h5>投诉与建议</h5>
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

<#--                <div class="pull-left" style="margin-right: 10px;">-->
<#--                    <input type="text" name="systemOrderno" id="systemOrderno" class="form-control input-outline"  placeholder="请输入系统订单号" style="width:150px;">-->
<#--                </div>-->

<#--                SUGGESTION("suggestion", "建议"),-->
<#--                CONSULT("consult", "咨询"),-->
<#--                RECHARGE("recharge", "充值问题"),-->
<#--                WITHDRAW("withdraw", "提现问题"),-->
<#--                ORDER("order", "订单问题"),-->
<#--                OTHER("Other", "其他"),-->
                <div class="pull-left" style="margin-right: 10px;">
                    <select class="form-control" name="type" >
                        <option value="" >---订单类型---</option>
                        <option value="suggestion" >建议</option>
                        <option value="consult" >咨询</option>
                        <option value="recharge" >充值问题</option>
                        <option value="withdraw" >提现问题</option>
                        <option value="order" >订单问题</option>
                        <option value="other" >其他</option>
                    </select>
                </div>

                <div class="pull-left" style="margin-right: 10px;">
                    <select class="form-control" name="txStatus" >
                        <option value="" >---反馈状态---</option>
                        <option value="waiting" >等待回复</option>
                        <option value="finish" >已回复</option>
                    </select>
                </div>

                <button id="search-btn" type="button" class="btn btn-outline btn-default" title="查询">
                    <i class="glyphicon glyphicon-search" aria-hidden="true"></i> 查询
                </button>
                <button id="reset-btn" type="button" class="btn btn-outline btn-default" title="重置">
                    <i class="glyphicon glyphicon-repeat" aria-hidden="true"></i> 重置
                </button>


                <div class="pull-left" style="margin-left: 10px;">
                    <button id="refuseAudit-btn" type="button" class="btn btn-outline btn-default" title="选中项统计">
                        一键回复
                    </button>
                </div>
            </div>
            <table id="bootstrapTable">
                <thead>
                <tr>
                    <th  data-width="2%" data-align="center"  data-checkbox="true"
                         class="title">
                    </th>

                    <th data-field="username" data-width="8%" data-align="center"
                        class="title">用户名
                    </th>

<#--                    <th data-field="agentname" data-width="5%" data-align="center"-->
<#--                        class="title">所属代理-->
<#--                    </th>-->
                    <th data-field="staffname" data-width="5%" data-align="center"
                        class="title">所属员工
                    </th>
                    <th data-field="status" data-width="4%" data-align="center"
                        class="title">回复状态
                    </th>
                    <th data-field="type" data-width="4%" data-align="center"
                        class="status">反馈类型
                    </th>
                    <th data-field="title" data-width="8%" data-align="center"
                        class="title">whatsapp账号
                    </th>
                    <th data-field="content" data-width="12%" data-align="center"
                        class="title">用户反馈内容
                    </th>

                    <th data-field="reply" data-width="12%" data-align="center"
                        class="title">回复内容
                    </th>

                    <th data-field="createtime" data-width="10%" data-align="center"
                        class="title">创建时间
                    </th>

                    <th data-field="no" data-width="8%" data-align="center" data-formatter="actionFormatter"
                        class="title">操作
                    </th>



<#--                    <th data-field="id" data-formatter="actionFormatter" data-width="10%" data-align="center">操作</th>-->
                </tr>
                </thead>
            </table>
        </div>
    </div>
</div>

<div class="modal fade" id="myUpdatePasswordModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="ibox-title">
                <h5>投诉与建议回复</h5>
            </div>

            <div class="ibox-content">
                <form id="form" class="form-horizontal" autocomplete="off">

                    <#--                    <#if feedBack??>-->
                    <#--                        <input type="hidden" id="id" name="id" value="${feedBack.id}" autocomplete="off" required maxlength="50"/>-->
                    <#--                    </#if>-->

                    <#--                    <div class="form-group">-->
                    <#--                        <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>用户名:</label>-->
                    <#--                        <div class="col-sm-6">-->
                    <#--                            <input class="form-control" readonly id="username" name="username" autocomplete="off" required maxlength="50" value="<#if feedBack??>${feedBack.username}</#if>" />-->
                    <#--                        </div>-->
                    <#--                    </div>-->

                    <#--                    <div class="form-group">-->
                    <#--                        <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>反馈类型:</label>-->
                    <#--                        <div class="col-sm-6">-->
                    <#--                            <input type="text" readonly class="form-control" id="type" name="type" value="<#if feedBack??>${feedBack.type}</#if>" autocomplete="off" maxlength="50"-->
                    <#--                                   value=""/>-->
                    <#--                        </div>-->
                    <#--                    </div>-->
                    <#--                    <div class="hr-line-dashed"></div>-->

                    <#--                    <div class="form-group">-->
                    <#--                        <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>反馈内容:</label>-->
                    <#--                        <div class="col-sm-6">-->
                    <#--                            <textarea class="form-control" readonly rows="5" cols="20" id="content" name="content"  ><#if feedBack??>${feedBack.content}</#if></textarea>-->

                    <#--                        </div>-->
                    <#--                    </div>-->
                    <#--                    <div class="hr-line-dashed"></div>-->

                    <div class="form-group">
                        <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>回复内容:</label>
                        <div class="col-sm-6">
                            <textarea class="form-control"  rows="5" cols="20" id="reply" name="reply"  ><#if feedBack??>${feedBack.reply}</#if></textarea>

                        </div>
                    </div>
                    <div class="hr-line-dashed"></div>


                    <div class="form-group">
                        <div class="col-sm-4 col-sm-offset-2">
                            <input class="btn btn-primary" type="button" id="mysubmit" value="回复"/>
                            <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                        </div>
                    </div>


                </form>
            </div>

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


        // result += '<a href="javascript:;" class="table-btn detailUserInfo" data-username="' + row.username + '" title="详情">详情</a>';
        // result += '<a href="javascript:;" class="table-btn passAudit" data-no="' + row.no + '" title="通过">通过</a>';
        if(row.status=="waiting"){
            result += '<a href="javascript:;" class="table-btn returnfeedback" data-no="' + row.id + '" title="回复">回复</a>';
        }

        if(row.status=="finish"){
            result += '<a href="javascript:;" class="table-btn returnfeedback" data-no="' + row.id + '" title="修改回复">修改回复</a>';
        }

        // result += '<a href="javascript:;" class="table-btn deletefeedback" data-no="' + row.id + '" title="删除">删除</a>';

        return result;
    }

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
        options.url = '/alibaba888/agent/web/getFeedBackList';
        options.search = false;
        options.showRefresh = true;
        options.singleSelect= false;
        options.queryParams = function (params) {
            params.time = $('input[name="time"]').val();
            params.username = $('input[name="username"]').val();
            // params.systemOrderno = $('input[name="systemOrderno"]').val();
            params.type = $('select[name="type"]').val();
            params.txStatus = $('select[name="txStatus"]').val();
            return params;
        };
        var bootstrapTable = $('#bootstrapTable').bootstrapTable(options);

        $(document).on('click', '#create-btn', function () {
            window.location.href = '/alibaba888/Liv2sky3soLa93vEr62/toApplyPlatformSupplyPage';
        });

        $(document).on('click', '.table-btn.returnfeedback', function () {
            var id = $(this).attr('data-no');
            var url = '/alibaba888/agent/web/toReturnFeedBackPage?id=' + id;
            openNewWindow(url);
        });


        $(document).on('click', '.table-btn.detailUserInfo', function () {
            var username = $(this).attr('data-username');
            var url = '/alibaba888/Liv2sky3soLa93vEr62/passport/user/history_detail/page?username=' + username;
            openNewWindow(url);
        });

        $(document).on('click', '.table-btn.passAudit', function () {
            var no = $(this).attr('data-no');
            $('#myModalDel #id').val(no);
            $('#myModalDel #type').attr('action', 'pass');
            $('#myModalDel #tips').text('确定审核通过吗？');
            $('#myModalDel').modal();
        });

        $(document).on('click', '.table-btn.refuseAudit', function () {
            var no = $(this).attr('data-no');
            $('#myModalDel #id').val(no);
            $('#myModalDel #type').attr('action', 'refuse');
            $('#myModalDel #tips').text('确定审核拒绝吗？');
            $('#myModalDel').modal();
        });

        $('#delete_submit').click(function () {
            var no = $('#myModalDel #id').val();
            var value = $('#myModalDel #type').val();
            var action = $('#myModalDel #type').attr('action');

            auditWithdrawOrder(no, action);
        });

        $('#search-btn').click(function () {
            refresh();
        });

        $('#reset-btn').click(function () {
            $('input[name="username"]').val('');
            resetTime();
            //refresh();
        });


        $('#refuseAudit-btn').click(function () {
            var selected = $('#bootstrapTable').bootstrapTable('getSelections');
            //返回值为数据对象数组

            if(selected&&selected.length>0){
                //非空数组时候进行的操作
                $('#myUpdatePasswordModal').modal();

            }else{
                //空数组的操作
                $.global.openErrorMsg('请先选择要操作的数据!');
            }

        });


        $('#mysubmit').click(function () {
            // agentid: 0
            // agentname: ""
            // content: "4897ds8978fa96"
            // createtime: "2022-06-14 22:42:56"
            // id: 2
            // remark: ""
            // reply: "454564564"
            // staffid: 0
            // staffname: ""
            // status: "finish"
            // title: "214341"
            // type: "suggestion"
            // userid: 12
            // username: "up9997845613"
            var selected = $('#bootstrapTable').bootstrapTable('getSelections');
            //返回值为数据对象数组

            if(selected&&selected.length>0){
                //非空数组时候进行的操作
                var slength = selected.length;
                $.each(selected,function(index,item){
                    debugger
                    var id = item.id;
                    var input=document.getElementById("reply");//通过id获取文本框对象
                    var reply = input.value;   //$('input[name="reply"]').val();


                    if(isEmpty(id) || isEmpty(reply))
                    {
                        $.global.openErrorMsg('* 号必填参数不能为空');
                        return false;
                    }

                    $.ajax({
                        url: '/alibaba888/Liv2sky3soLa93vEr62/editFeedBack',
                        type: 'post',
                        dataType: 'json',
                        data:{
                            id:id,
                            reply:reply,
                        },
                        success: function(result){
                            console.log(result);
                            if (result && result.code === 200) {
                                if(slength==index+1){
                                    $.global.openSuccessMsg(result.msg, function(){
                                        $('#myUpdatePasswordModal').modal('hide');
                                        refresh();
                                    });
                                }
                                return;
                            }
                            $.global.openErrorMsg(result.msg);
                        },
                        error: function(){
                            $.global.openErrorMsg('系统异常!');
                        }
                    });

                })


            }else{
                //空数组的操作
                $.global.openErrorMsg('请先选择要操作的数据!');
            }

        });

    });

    function refresh() {
        $('#bootstrapTable').bootstrapTable('refresh');
    }

    function auditWithdrawOrder(orderno, action) {
        $.ajax({
            url: '/alibaba888/Liv2sky3soLa93vEr62/doAuditUserWithdrawOrder',
            type: 'post',
            dataType: 'json',
            data: {orderno:orderno,action:action},
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
