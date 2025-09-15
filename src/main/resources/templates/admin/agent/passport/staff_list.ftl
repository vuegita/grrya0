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
            <h5>员工管理</h5>
        </div>

        <div class="ibox-content">
            <div class="btn-group hidden-xs" id="bootstrapTableToolBar" role="group">

                <div class="pull-left" style="margin-right: 10px;">
                    <input type="hidden" name="ancestorUsername" id="ancestorUsername" class="form-control input-outline" readonly placeholder="查看下级会员使用">
                </div>

<#--                <div class="pull-left" style="margin-right: 10px;">-->
<#--                    <select class="form-control" name="type" >-->
<#--                        <option value="" selected>所有类型</option>-->
<#--                        <option value="staff" >员工</option>-->
<#--                        <option value="agent">代理</option>-->
<#--                    </select>-->
<#--                </div>-->

                <#if isAgent=='1'>
                    <div class="pull-left" style="margin-right: 10px;">
                        <button id="create-btn" type="button" class="btn btn-outline btn-default" title="新增">
                            <i class="glyphicon glyphicon-plus" aria-hidden="true"></i> 新增
                        </button>
                    </div>
                </#if>

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
                    <th data-field="name" data-width="8%" data-align="center"
                        class="title">用户名
                    </th>

<#--                    <th data-field="inviteCode" data-width="8%" data-align="center"-->
<#--                        class="title">推广码-->
<#--                    </th>-->

                    <th data-field="type" data-width="8%" data-align="center" data-formatter="typeFormatter"
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

                    <th data-field="lastlogintime" data-width="10%" data-align="center"
                        class="title">最后登陆时间
                    </th>

                    <th data-field="lastloginip" data-width="10%" data-align="center"
                        class="title">最后登陆IP
                    </th>

                    <th data-field="createtime" data-width="10%" data-align="center"
                        class="title">创建时间
                    </th>

                    <th data-field="id" data-formatter="actionFormatter" data-width="15%" data-align="center">操作</th>
                </tr>
                </thead>
            </table>
        </div>
    </div>
</div>

/
<#include "./user_list_update_password.ftl">
<#include "./staff_security_google_info.ftl">
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

        <#if isAgent == "1">
        result += '<a href="javascript:;" class="table-btn editUserInfo" data-username="' + row.name + '" title="编辑">编辑</a>';
        result += '<a href="javascript:;" class="table-btn googleCode" data-username="' + row.name + '" title="谷歌验证">谷歌验证</a>';
        </#if>

        if(row.type == 'staff')
        {
            result += '<a href="javascript:;" class="table-btn updatePassword" data-username="' + row.name + '" title="修改密码">修改密码</a>';
            result += '<a href="javascript:;" class="table-btn queryMemberChildList" data-username="' + row.name + '" data-usertype="' + row.type + '" title="会员下级">会员下级</a>';
            result += '<a href="javascript:;" class="table-btn copyshareUrl" data-username="' + row.name + '" data-inviteCode="' + row.inviteCode + '" title="推广链接">推广链接</a>';
            result += '<a href="javascript:;" class="table-btn detailMemberInfo" data-username="' + row.name + '" data-usertype="' + row.type + '" title="会员详情">会员详情</a>';
        }
        else if(row.type == 'member')
        {
            result += '<a href="javascript:;" class="table-btn detailMemberInfo" data-username="' + row.name + '" data-usertype="' + row.type + '" title="会员详情">会员详情</a>';
        }

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
        if(value == "agent")
        {
            return "代理";
        }
        if(value == "staff")
        {
            return "员工";
        }
        if(value == "test")
        {
            return "测试号";
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
        options.url = '/alibaba888/agent/passport/getStaffList';
        options.search = false;
        options.showRefresh = true;
        options.queryParams = function (params) {
            params.username = $('input[name="queryUsername"]').val();
            params.ancestorUsername = $('#ancestorUsername').val();

            params.time = $('input[name="time"]').val();
            params.type = $('select[name="type"]').val();
            return params;
        };
        var bootstrapTable = $('#bootstrapTable').bootstrapTable(options);

        $(document).on('click', '#create-btn', function(){
            var url = '/alibaba888/agent/passport/toEditUserPage';
            //window.location.href ='/alibaba888/agent/passport/toEditUserPage';
            openNewWindow(url);
        });

        // google code
        $(document).on('click', '.table-btn.googleCode', function () {
            var username = $(this).attr('data-username');

            var imgSrc = "/alibaba888/agent/basic/security/getGoogleKeyEWMByAgent?username=" + username+"&time=" + Math.random();;
            $('#googleCodeImg').attr("src", imgSrc);

            $('#myModalGoogle').modal();
        });

        $(document).on('click', '.table-btn.editUserInfo', function () {
            var username = $(this).attr('data-username');
            var url = '/alibaba888/agent/passport/toEditUserPage?username=' + username;
            // window.location.href ='/alibaba888/Liv2sky3soLa93vEr62/toEditUserPage?username=' + username;
            openNewWindow(url);
        });

        $(document).on('click', '.table-btn.detailMemberInfo', function () {
            var username = $(this).attr('data-username');
            var url = '/alibaba888/Liv2sky3soLa93vEr62/passport/user/history_detail/page?username=' + username;
            openNewWindow(url);
        });

        $(document).on('click', '.table-btn.updatePassword', function () {
            var username = $(this).attr('data-username');
            $('#upPwdUsername').attr('value', username);
            $('#myUpdatePasswordModal').modal();
        });

        $(document).on('click', '.table-btn.copyshareUrl', function () {
            var inviteCode = $(this).attr('data-inviteCode');

            $.ajax({
                url: '/alibaba888/agent/basic/overview/getSystemConfig',
                type: 'post',
                dataType: 'json',
                data: {inviteCode:inviteCode},
                success: function (re) {
                    if(re.code==200){

                    var shareUrl="https://"+window.location.host+re.data.shareUrl  //STATIC_URL 前端域名
                    const input = document.createElement('input');
                    document.body.appendChild(input);
                    input.setAttribute('value', shareUrl);
                    input.select();
                    if (document.execCommand('copy')) {
                        document.execCommand('copy');

                        $.global.openSuccessMsg("复制成功");
                    }
                    document.body.removeChild(input);
                    }

                },
                error: function () {
                    $.global.openErrorMsgCollback('系统异常,操作失败!', function () {

                    });
                }
            });





        });

        $(document).on('click', '.table-btn.queryMemberChildList', function () {
            var username = $(this).attr('data-username');
            var type = $(this).attr('data-usertype');
            $('#ancestorUsername').prop('value', username);

            if(type == 'member')
            {
                $.global.openErrorMsg("会员无下级!");
                return;
            }
            refresh();
        });

        $(document).on('click', '.table-btn.updateUserType', function () {
            var usertype = $(this).attr('data-usertype');
            var username = $(this).attr('data-username');
            $('#myModalDel #id').val(username);
            $('#myModalDel #type').attr('action', 'updateUserType');
            if(usertype == 'member')
            {
                $('#myModalDel #type').val('staff');
                $('#myModalDel #tips').text('确定转为员工吗？');
            }
            else if(usertype == 'staff')
            {
                $('#myModalDel #type').val('agent');
                $('#myModalDel #tips').text('确定转为代理吗？');
            }
            else
            {
                $.global.openErrorMsgCollback('当前已为代理，无法修改！', function () {

                });
                return;
            }
            $('#myModalDel').modal();
        });

        $(document).on('click', '.table-btn.updateStatus', function () {
            var status = $(this).attr('data-status');
            var username = $(this).attr('data-username');
            $('#myModalDel #id').val(username);
            $('#myModalDel #type').val(status);
            $('#myModalDel #type').attr('action', 'updateStatus');
            if("enable" == status)
            {
                $('#myModalDel #tips').text('确定启用账户吗？');
            }
            else
            {
                $('#myModalDel #tips').text('确定禁用账户吗？');
            }

            $('#myModalDel').modal();

        });

        $(document).on('click', '.table-btn.updateRelation', function () {
            var username = $(this).attr('data-username');
            $('#parentUsername').prop('value', '');
            $('#childUsername').attr('value', username);

            loadParentRelation(username);

            //$('#myUpdateRelationModal').modal();
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
        });

        $('#search-btn').click(function () {
            refresh();
        });

        $('#reset-btn').click(function () {
            $('input[name="ancestorUsername"]').val('');
            $('input[name="queryUsername"]').val('');
            resetTime();
            refresh();
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



</script>
</body>
</html>
