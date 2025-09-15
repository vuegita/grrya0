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
            <h5>推广信息</h5>

            <p style="color: blue"> 　</p>
<#--            <p  style="color: green">1. 每个代理都有唯一一个绑定!</p>-->
        </div>

        <div class="ibox-content">
            <div class="btn-group hidden-xs" id="bootstrapTableToolBar" role="group">


                <div class="pull-left" style="margin-right: 10px;">
                    <button id="create-btn" type="button" class="btn btn-outline btn-default" title="添加">
                        <i class="glyphicon glyphicon-plus" aria-hidden="true"></i> 添加
                    </button>
                </div>

                <div class="pull-left" style="margin-right: 10px;">
                    <input type="text" name="time" id="time" class="form-control input-outline"  placeholder="请选择时间" style="width:200px;">
                </div>

<#--                <div class="pull-left" style="margin-right: 10px;">-->
<#--                    <input type="text" name="agentname" id="agentname" class="form-control input-outline"  placeholder="请输入代理名" style="width:200px;">-->
<#--                </div>-->

                <div class="pull-left" style="margin-right: 10px;">
                    <input type="text" name="staffname" id="staffname" class="form-control input-outline"  placeholder="请输入员工" style="width:200px;">
                </div>

                <div class="pull-left" style="margin-right: 10px;">
                    <input type="text" name="name" id="name" class="form-control input-outline"  placeholder="请输入名称" style="width:200px;">
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

                    <th data-field="agentname" data-width="8%" data-align="center"
                        class="title">所属代理
                    </th>

                    <th data-field="staffname" data-width="8%" data-align="center"
                        class="title">所属员工
                    </th>

                    <th data-field="type" data-width="8%" data-align="center"
                        class="title">类型
                    </th>

                    <th data-field="name" data-width="8%" data-align="center"
                        class="title">名称
                    </th>

                    <th data-field="contact" data-width="8%" data-align="center"
                        class="title">联系方式
                    </th>


                    <th data-field="url" data-width="8%" data-align="center"
                        class="title">地址
                    </th>

                    <th data-field="subscribeCount" data-width="8%" data-align="center"
                        class="title">订阅数
                    </th>

                    <th data-field="viewCount" data-width="8%" data-align="center"
                        class="title">浏览数
                    </th>

                    <th data-field="amount" data-width="8%" data-align="center"
                        class="title">金额
                    </th>

                    <th data-field="status" data-width="5%" data-align="center" data-formatter="statusFormatter"
                        class="title">状态
                    </th>
                    <th data-field="createtime" data-width="10%" data-align="center"
                        class="title">创建时间
                    </th>
                    <th data-field="remark" data-width="10%" data-align="center"
                        class="title">备注
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

        result += '<a href="javascript:;" class="table-btn modifyMember" data-no="' + row.id + '" title="编辑">编辑</a>';
        // result += '<a href="javascript:;" class="table-btn deleteMember" data-id="'+ row.id+'" title="删除">删除</a>'
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

        resetTime();

        var options = TableUtils.getBtDefaultOptions();
        options.url = '/alibaba888/agent/root_web_promotion_channel/getDataList';
        options.search = false;
        options.showRefresh = true;
        options.queryParams = function (params) {
            params.time = $('input[name="time"]').val();
            params.agentname = $('input[name="agentname"]').val();
            params.staffname = $('input[name="staffname"]').val();
            params.name = $('input[name="name"]').val();

            return params;
        };
        var bootstrapTable = $('#bootstrapTable').bootstrapTable(options);

        $(document).on('click', '#create-btn', function () {
            var url = '/alibaba888/agent/root_web_promotion_channel/edit/page';
            openNewWindow(url);
        });

        $(document).on('click', '.table-btn.modifyMember', function () {
            var memberid = $(this).attr('data-no');

            var url = '/alibaba888/agent/root_web_promotion_channel/edit/page?id=' + memberid;
            openNewWindow(url);
        });

        $(document).on('click', '.table-btn.deleteMember', function () {

            var groupid = $(this).attr('data-id');
            $('#myModalDel #id').val(groupid);
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

    function deleteCardInfo(memberid) {
        $.ajax({
            url: '/alibaba888/agent/deleteAgentTips',
            type: 'post',
            dataType: 'json',
            data:{
                tipsid:memberid,
            },
            success: function(result){
                console.log(result);
                if(result && result.code == 200)
                {
                    $.global.openSuccessMsg("删除成功",function(){
                        refresh();
                        //window.history.go(-1);
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
