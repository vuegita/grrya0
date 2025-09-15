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
            <h5>股东列表</h5>
        </div>

        <div class="ibox-content">
            <div class="btn-group hidden-xs" id="bootstrapTableToolBar" role="group">

                <div class="pull-left" style="margin-right: 10px;">
                    <button id="create-btn" type="button" class="btn btn-outline btn-default" title="新增">
                        <i class="glyphicon glyphicon-plus" aria-hidden="true"></i> 新增
                    </button>
                </div>

                <div class="pull-left" style="margin-right: 10px;">
                    <input type="text" name="time" id="time" class="form-control input-outline"  placeholder="请选择创建时间" style="width:200px;">
                </div>

                <div class="pull-left" style="margin-right: 10px;">
                    <input type="text" name="queryUsername" id="queryUsername" class="form-control input-outline" placeholder="请输入用户名">
                </div>


<#--                <div class="pull-left" style="margin-right: 10px;">-->
<#--                    <input type="text" name="agentname" id="agentname" class="form-control input-outline"  placeholder="请输入代理名" style="width:150px;">-->
<#--                </div>-->

<#--                <div class="pull-left" style="margin-right: 10px;">-->
<#--                    <input type="text" name="staffname" id="staffname" class="form-control input-outline"  placeholder="请输入员工名" style="width:150px;">-->
<#--                </div>-->


                <div class="pull-left" style="margin-right: 10px;">
                    <select class="form-control" name="systemStatus" >
                        <option value="" selected>所有系统类型</option>
                        <option value="apply" >apply</option>
                        <option value="enable" >enable</option>
                        <option value="disable">disable</option>
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
<#--                    <th  data-width="2%" data-align="center"  data-checkbox="true"-->
<#--                         class="title">-->
<#--                    </th>-->

<#--                    <th data-field="id"-->
<#--                        data-width="5%" data-align="center">ID</th>-->

                    <th data-field="username" data-width="5%" data-align="center"
                        class="title">用户名
                    </th>

<#--                    <th data-field="agentName" data-width="5%" data-align="center"-->
<#--                        class="title">所属代理-->
<#--                    </th>-->

<#--                    <th data-field="staffName" data-width="5%" data-align="center"-->
<#--                        class="title">所属员工-->
<#--                    </th>-->

                    <th data-field="lv1RwStatus" data-width="5%" data-align="center" data-formatter="userTypeFormatter"
                        class="title">一级开启
                    </th>

                    <th data-field="lv2RwStatus" data-width="5%" data-align="center" data-formatter="userTypeFormatter"
                        class="title">二级开启
                    </th>

                    <th data-field="systemStatus" data-width="5%" data-align="center" data-formatter="userTypeFormatter"
                        class="title">状态
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
     * 用户信息明细
     */
    function detailInfoFormatter(value, row, index) {
        var result = "";
        result += "账户类型: " + row.fundKey + " <br>";
        result += "币种类型: " + row.currency + " <br>";

        var toFixedValue = 2;
        <#if systemRunningMode=='crypto'>
            toFixedValue = 6;
        </#if>

        result += "账户余额: " + row.balance.toFixed(toFixedValue) + " <br>";
        result += "冻结余额: " + row.freeze.toFixed(toFixedValue) + " <br>";

        return result;
    }

    /**
     * 操作栏的格式化
     */
    function actionFormatter(value, row, index) {
        var result = "";

        result += '<a href="javascript:;" class="table-btn detailUserInfo" data-username="' + row.username + '" title="详情">详情</a>';
        result += '<a href="javascript:;" class="table-btn editUserInfo" data-username="' + row.username + '" title="编辑">编辑</a>';

        return result;
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


    $(function () {
        resetTime();

        var options = TableUtils.getBtDefaultOptions();
        options.url = '/alibaba888/Liv2sky3soLa93vEr62/getPassportShareHolderMemberList';
        options.search = false;
        options.showRefresh = true;
        options.clickToSelect= true;
        options.singleSelect= false;//是否单选，false表示多选;true标识只能单选
        options.queryParams = function (params) {
            params.username = $('input[name="queryUsername"]').val();
            params.agentname = $('input[name="agentname"]').val();
            params.staffname = $('input[name="staffname"]').val();

            params.ancestorUsername = $('#ancestorUsername').val();

            params.time = $('input[name="time"]').val();
            params.systemStatus = $('select[name="systemStatus"]').val();
            return params;
        };
        var bootstrapTable = $('#bootstrapTable').bootstrapTable(options);

        $(document).on('click', '#create-btn', function(){
            // window.location.href ='/alibaba888/Liv2sky3soLa93vEr62/toEditUserPage';
            var url = '/alibaba888/Liv2sky3soLa93vEr62/toPassportShareHolderMemberPage';
            openNewWindow(url);
        });

        $(document).on('click', '.table-btn.detailUserInfo', function () {
            var username = $(this).attr('data-username');
            var url = '/alibaba888/Liv2sky3soLa93vEr62/passport/user/history_detail/page?username=' + username;
            // window.location.href = url;
            openNewWindow(url);
        });

        $(document).on('click', '.table-btn.googleCode', function () {
            var username = $(this).attr('data-username');
            showGoogleCode(username);
        });

        $(document).on('click', '.table-btn.editUserInfo', function () {
            var username = $(this).attr('data-username');
            var url = '/alibaba888/Liv2sky3soLa93vEr62/toPassportShareHolderMemberPage?username=' + username;
            openNewWindow(url);
        });



        $('#delete_submit').click(function () {
            var username = $('#myModalDel #id').val();
            var value = $('#myModalDel #type').val();
            var action = $('#myModalDel #type').attr('action');

            //console.log("action = " + action);

            if("updateUserType" == action)
            {
                updateUserType(username, value)
            }
            else if("updateStatus" == action)
            {
                updateUserStatus(username, value)
            }
            else if("updateToTest" == action)
            {
                updateUserToTest(username);
            }
        });

        $('#search-btn').click(function () {
            refresh();
        });

        $('#reset-btn').click(function () {
            $('input[name="ancestorUsername"]').val('');
            $('input[name="queryUsername"]').val('');
            resetTime();
        });


    });

    function refresh() {
        $('#bootstrapTable').bootstrapTable('refresh');
    }

    function updateUserType(username, userType) {
        $.ajax({
            url: '/alibaba888/Liv2sky3soLa93vEr62/updateUserType',
            type: 'post',
            dataType: 'json',
            data: {username:username,userType:userType},
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

    function updateUserStatus(username, status) {
        $.ajax({
            url: '/alibaba888/Liv2sky3soLa93vEr62/updateUserStatus',
            type: 'post',
            dataType: 'json',
            data: {username:username,status:status},
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

    function updateUserToTest(username) {
        $.ajax({
            url: '/alibaba888/Liv2sky3soLa93vEr62/updateUserToTest',
            type: 'post',
            dataType: 'json',
            data: {username:username},
            success: function (result) {
                if (result.code === 200) {
                    $.global.openSuccessMsg(result.msg, function(){
                        // refresh();
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
