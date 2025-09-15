<!DOCTYPE HTML>
<html>
<head>
    <#include "../../../common/head-meta.ftl"/>
    <title>${projectName}后台管理系统</title>
    <link type="text/css" rel="stylesheet" href="${STATIC_URL}/plugins/bootstrap-table/bootstrap-table.min.css"/>
</head>

<body class="gray-bg">
<div class="wrapper wrapper-content animated fadeInRight">
    <div class="ibox">
        <div class="ibox-title">
            <div style="font-weight: bold">出款列表</div>
            <div style="color: red">1.不配置出款信息用户将不能发起提款</div>
            <div style="color: red">2.确保每个钱包中有足够的主币和要给用户出款的币种，否则会引起出款失败</div>
            <div style="color: red">3.删除出款信息是请先点击编辑把状态改为关闭  </div>
            <div style="color: red">4.每个网络类型都需要配置一条出款信息  </div>
        </div>

        <div class="ibox-content">
            <div class="btn-group hidden-xs" id="bootstrapTableToolBar" role="group">

                <div class="pull-left hidden" style="margin-right: 10px;">
                    <button id="return-btn" type="button" class="btn btn-outline btn-default" title="返回">
                        <i class="glyphicon" aria-hidden="true"></i> 返回
                    </button>
                    <input type="hidden" name="parentUsernameStack" id="parentUsernameStack" class="form-control input-outline" disabled>
                </div>

<#--                <#if isShowAction == "true" >-->
                <div class="pull-left" style="margin-right: 10px;">
                    <button id="create-btn" type="button" class="btn btn-outline btn-default" title="新增">
                        <i class="glyphicon glyphicon-plus" aria-hidden="true"></i> 新增
                    </button>
                </div>
<#--                </#if>-->

<#--                <div class="pull-left" style="margin-right: 10px;">-->
<#--                    <input type="text" name="time" id="time" class="form-control input-outline"  placeholder="请选择创建时间" style="width:200px;">-->
<#--                </div>-->

<#--                <div class="pull-left" style="margin-right: 10px;">-->
<#--                    <input type="text" name="agentname" id="agentname" class="form-control input-outline" placeholder="请输入代理名">-->
<#--                </div>-->

                <div class="pull-left" style="margin-right: 10px;">
                    <select class="form-control" name="status" >
                        <option value="" selected>所有状态</option>
                        <#list statusArray as statusInfo>
                            <#if !statusInfo.isAdminPermit() || (statusInfo.isAdminPermit() && isShowAction == 'true') >
                                <option value="${statusInfo.getKey()}" >${statusInfo.getTitle()}</option>
                            </#if>
                        </#list>
                    </select>
                </div>

<#--                <div class="pull-left" style="margin-right: 10px;">-->
<#--                    <select class="form-control" name="type" >-->
<#--                        <option value="">所有类型</option>-->
<#--                        <option value="payin">代收</option>-->
<#--                        <option value="payout">代付</option>-->
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

                    <th data-field="name" data-width="5%" data-align="center"
                        class="title">名称
                    </th>

                    <th data-field="type" data-width="5%" data-align="center" data-formatter="typeFormatter"
                        class="title">通道类型
                    </th>

                    <th data-field="productType" data-width="5%" data-align="center"
                        class="title">产品类型
                    </th>

                    <th data-field="status" data-width="5%" data-align="center"
                        data-formatter="statusFormatter"
                        class="title">状态
                    </th>

<#--                    <th data-field="sort" data-width="5%" data-align="center"-->
<#--                        class="title">排序-->
<#--                    </th>-->

                    <th data-field="createtime" data-width="5%" data-align="center"
                        class="title">创建时间
                    </th>

<#--                    <th data-field="remark" data-width="5%" data-align="center"-->
<#--                        class="title">备注-->
<#--                    </th>-->

                    <th data-field="id" data-formatter="actionFormatter" data-width="5%" data-align="center">操作</th>
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
<#--        <#if isShowAction == "true" >-->
            result += '<a href="javascript:;" class="table-btn editChannelInfo" data-channelid="' + row.id + '" title="编辑">编辑</a>';
            // result += '<a href="javascript:;" class="table-btn deleteChannelInfo" data-channelid="' + row.id + '" title="删除">删除</a>';
<#--        </#if>-->
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
        if(value == "payin")
        {
            return "代收";
        }
        return "代付";
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
        if(value == 'hidden')
        {
            return "隐藏"
        }
        return "测试";
    }


    $(function () {
        // resetTime();

        var options = TableUtils.getBtDefaultOptions();
        options.url = '/alibaba888/agent/getPayChannelList';
        options.search = false;
        options.showRefresh = true;
        options.queryParams = function (params) {
            // params.username = $('input[name="queryUsername"]').val();
            // params.agentname = $('#agentname').val();
            // params.time = $('input[name="time"]').val();

            params.type = $('select[name="type"]').val();
            params.status = $('select[name="status"]').val();
            return params;
        };
        var bootstrapTable = $('#bootstrapTable').bootstrapTable(options);

        $(document).on('click', '#create-btn', function(){
            var url = "/alibaba888/agent/toEditPayChannelPage";
            openNewWindow(url);
        });

        $(document).on('click', '.table-btn.editChannelInfo', function () {
            var channelid = $(this).attr('data-channelid');
            var url = "/alibaba888/agent/toEditPayChannelPage?channelid=" + channelid;
            openNewWindow(url);
        });

        $(document).on('click', '.table-btn.deleteChannelInfo', function () {
            var channelid = $(this).attr('data-channelid');
            $('#myModalDel #id').val(channelid);
            $('#myModalDel #type').attr('action', 'deleteChannelInfo');
            $('#myModalDel #tips').text('确定删除通道吗？');
            $('#myModalDel').modal();
        });

        $('#delete_submit').click(function () {
            var username = $('#myModalDel #id').val();
            var value = $('#myModalDel #type').val();
            var action = $('#myModalDel #type').attr('action');

            if("deleteChannelInfo" == action)
            {
                deletePayChannel(username)
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

    });

    function refresh() {
        $('#bootstrapTable').bootstrapTable('refresh');
    }

    function deletePayChannel(channelid) {
        $.ajax({
            url: '/alibaba888/agent/deletePayChannel',
            type: 'post',
            dataType: 'json',
            data: {channelid:channelid},
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
