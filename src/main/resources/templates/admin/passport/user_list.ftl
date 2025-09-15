<!DOCTYPE HTML>
<html>
<head>
    <#include "../../common/head-meta.ftl"/>
    <title>${projectName}后台管理系统-订单管理</title>
    <link type="text/css" rel="stylesheet" href="${STATIC_URL}/plugins/bootstrap-table/bootstrap-table.min.css"/>
</head>

<body class="gray-bg">
<div class="wrapper wrapper-content animated fadeInRight">
    <div class="ibox">
        <div class="ibox-title">
            <h5>会员管理</h5>
        </div>

        <div class="ibox-content">


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

            <div class="pull-left" style="margin-right: 10px;">
                <input type="text" name="time" id="time" class="form-control input-outline"  placeholder="请选择创建时间" style="width:200px;">
            </div>

            <div class="pull-left" style="margin-right: 10px;">
                <input type="text" name="queryUsername" id="queryUsername" class="form-control input-outline" placeholder="请输入用户名">
            </div>

            <div class="pull-left" style="margin-right: 10px;">
                <input type="text" name="inviteCode" id="inviteCode" class="form-control input-outline" placeholder="邀请码">
            </div>


            <div class="pull-left" style="margin-right: 10px;">
                <input type="text" name="agentname" id="agentname" class="form-control input-outline"  placeholder="请输入代理名" style="width:150px;">
            </div>

            <div class="pull-left" style="margin-right: 10px;">
                <input type="text" name="staffname" id="staffname" class="form-control input-outline"  placeholder="请输入员工名" style="width:150px;">
            </div>


            <div class="pull-left" style="margin-right: 10px;">
                <input type="text" name="ancestorUsername" id="ancestorUsername" class="form-control input-outline" readonly placeholder="请点击查看下级">
            </div>

            <div class="pull-left" style="margin-right: 10px;">
                <select class="form-control" name="type" >
                    <option value="" selected>所有类型</option>
                    <option value="member" >会员</option>
                    <option value="staff" >员工</option>
                    <option value="agent">代理</option>
                    <#--                        <option value="promotion">推广</option>-->
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
                    批量设置上级
                </button>
            </div>




            <div class="btn-group hidden-xs" id="bootstrapTableToolBar" role="group">






            </div>
            <table id="bootstrapTable">
                <thead>
                <tr>
                    <th  data-width="2%" data-align="center"  data-checkbox="true"
                         class="title">
                    </th>


                    <th data-field="id"
<#--                        data-formatter="noActionFormatter" -->
                        data-width="5%" data-align="center">ID</th>

                    <th data-field="name" data-width="5%" data-align="center"
                        class="title">用户名
                    </th>

                    <th data-field="agentName" data-width="5%" data-align="center"
                        class="title">所属代理
                    </th>

                    <th data-field="staffName" data-width="5%" data-align="center"
                        class="title">所属员工
                    </th>

                    <th data-field="type" data-width="5%" data-align="center" data-formatter="userTypeFormatter"
                        class="title">类型
                    </th>

                    <th data-field="status" data-width="5%" data-align="center"
                        data-formatter="statusFormatter"
                        class="title">状态
                    </th>

                    <th data-field="name" data-width="15%" data-formatter="userInfoFormatter" data-align="left"
                        class="title">用户信息
                    </th>

<#--                    <th data-field="balance" data-width="10%" data-align="left" data-formatter="detailInfoFormatter"-->
<#--                        class="title">资金明细-->
<#--                    </th>-->

<#--                    <th data-field="freeze" data-width="5%" data-align="center" data-formatter="moneyFormatter"-->
<#--                        class="title">冻结余额-->
<#--                    </th>-->

<#--                    <th data-field="lastlogintime" data-width="10%" data-align="center"-->
<#--                        class="title">最后登陆时间-->
<#--                    </th>-->

<#--                    <th data-field="lastlogin" data-width="10%" data-align="center"-->
<#--                        class="title">最后登陆IP-->
<#--                    </th>-->

<#--                    <th data-field="createtime" data-width="10%" data-align="center"-->
<#--                        class="title">创建时间-->
<#--                    </th>-->

                    <th data-field="id" data-formatter="actionFormatter" data-width="10%" data-align="center">操作</th>
                </tr>
                </thead>
            </table>
        </div>
    </div>
</div>


<div class="modal fade" id="myUpdatePasswordModal2" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="ibox-title">
                <h5>设置上级</h5>
            </div>

            <div class="ibox-content">
                <form id="form" class="form-horizontal" autocomplete="off">


                    <div class="form-group">
                        <label class="control-label col-sm-3"><span class="text-danger">*&nbsp;</span>上级员工名:</label>
                        <div class="col-sm-3">
                            <input class="form-control" name="parentUsername2" id="parentUsername2" autocomplete="off" maxlength="100" placeholder="请输入父级员工名"  style="width:300px;"/>
                        </div>
                    </div>
                    <div class="hr-line-dashed"></div>


                    <div class="form-group">
                        <div class="col-sm-4 col-sm-offset-2">
                            <input class="btn btn-primary" type="button" id="mysubmit" value="提交"/>
                            <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                        </div>
                    </div>


                </form>
            </div>

        </div>
    </div>
</div>

<#include "./user_security_google_info.ftl">
<#include "./user_list_update_relation.ftl">
<#include "./user_list_update_password.ftl">

<#include "../../common/delete_form.ftl">
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

    function userTypeFormatter(value, row, index) {
        if(value == "agent")
        {
            return "代理";
        }
        else if(value == "staff")
        {
            return "员工";
        }
        else if(value == "member")
        {
            if(row.subType=="simple"){
                return "会员";
            }else if(row.subType=="promotion"){
                return "推广员";
            }
            return "会员";
        }
        else if(value == "promotion")
        {
            return "推广";
        }
        else if(value == "test")
        {
            return "测试";
        }
        else if(value == "robot")
        {
            return "机器人";
        }
        return value;
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
     * 用户信息
     */
    function userInfoFormatter(value, row, index) {
        var result = "";
        // result += "用户ID: " + row.id + " <br>";
        // result += "用户名: " + row.name + " - (" + userTypeFormatter(row.type, row, index) + ")" + " <br>";
        result += "推广码: " + row.inviteCode + " <br>";
        result += "手机号: " + row.phone + " <br>";
        result += "邮箱地址: " + row.email + " <br>";
        result += "注册来源: " + row.registerpath + " <br>";

        result += "创建时间: " + row.createtime + " <br>";
        result += "注册 IP: " + row.registerip + " <br>";
        if(!isEmpty(row.lastlogintime))
        {
            result += "最后登陆时间: " + row.lastlogintime + " <br>";
            result += "最后登陆IP: " + row.lastloginip + " <br>";
        }

        return result;
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

        if(row.name == 'super')
        {
            return  "系统内部账号";
        }

        result += '<a href="javascript:;" class="table-btn detailUserInfo" data-username="' + row.name + '" title="详情">详情</a>';
        result += '<a href="javascript:;" class="table-btn editUserInfo" data-username="' + row.name + '" title="编辑">编辑</a>';
        result += '<a href="javascript:;" class="table-btn googleCode" data-username="' + row.name + '" title="谷歌验证">谷歌验证</a>';
        // if(row.status == 'enable')
        // {
        //     result += '<a href="javascript:;" class="table-btn updateStatus" data-username="' + row.name + '" data-status="disabled" title="禁用">禁用</a>';
        // }
        // else
        // {
        //     result += '<a href="javascript:;" class="table-btn updateStatus" data-username="' + row.name + '" data-status="enable" title="启用">启用</a>';
        // }

        result += '<a href="javascript:;" class="table-btn updatePassword" data-username="' + row.name + '" title="修改密码">修改密码</a>';

        if(row.type == 'agent' || row.type == 'staff')
        {
            result += '<a href="javascript:;" class="table-btn queryChildList" data-username="' + row.name + '" data-usertype="' + row.type + '" title="查看下级">查看下级</a>';
            <#if isSuperAdmin=="true">
            result += '<a href="javascript:;" class="table-btn copyshareUrl" data-username="' + row.name + '" title="推广链接">登录代理或员工</a>'
            </#if>
        }

        // row.type == 'staff' ||
<#--        <#if environment == 'dev'>-->
            if(row.type == 'member')
            {
                result += '<a href="javascript:;" class="table-btn updateRelation" data-username="' + row.name + '" title="设置上级">设置上级</a>';
                result += '<a href="javascript:;" class="table-btn updateToTest" data-username="' + row.name + '" title="设为测试号">设为测试号</a>';
            }
<#--        </#if>-->


        // result += '<a href="javascript:;" class="table-btn findParent" data-username="' + row.name + '" title="查看上级">查看上级</a>';

        // result += '<a href="javascript:;" class="table-btn updateUserType" data-username="' + row.name + '" data-usertype="' + row.type + '" title="变更类型">变更类型</a>';

        // if(row.type == 'member')
        // {
        //     result += '<a href="javascript:;" class="table-btn updateUserType" data-username="' + row.name + '" data-usertype="staff" title="转为员工">转为员工</a>';
        // }
        // else if(row.type == 'staff')
        // {
        //     result += '<a href="javascript:;" class="table-btn updateUserType" data-username="' + row.name + '" data-usertype="agent" title="设为代理">设为代理</a>';
        // }

        return result;
    }

    function dateFormatter(value, row, index) {
        if (null == value) {
            return "";
        } else {
            return DateUtils.formatyyyyMMddHHmmss(value);
        }
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
        options.url = '/alibaba888/Liv2sky3soLa93vEr62/getUserList';
        options.search = false;
        options.showRefresh = true;
        options.clickToSelect= true;
        options.singleSelect= false;//是否单选，false表示多选;true标识只能单选
        options.queryParams = function (params) {
            params.username = $('input[name="queryUsername"]').val();
            params.agentname = $('input[name="agentname"]').val();
            params.staffname = $('input[name="staffname"]').val();
            params.inviteCode = $('input[name="inviteCode"]').val();


            params.ancestorUsername = $('#ancestorUsername').val();

            params.time = $('input[name="time"]').val();
            params.type = $('select[name="type"]').val();
            return params;
        };
        var bootstrapTable = $('#bootstrapTable').bootstrapTable(options);

        $(document).on('click', '#create-btn', function(){
            // window.location.href ='/alibaba888/Liv2sky3soLa93vEr62/toEditUserPage';
            var url = '/alibaba888/Liv2sky3soLa93vEr62/toEditUserPage';
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
            var url = '/alibaba888/Liv2sky3soLa93vEr62/toEditUserPage?username=' + username;
            openNewWindow(url);
        });

        $(document).on('click', '.table-btn.updatePassword', function () {
            var username = $(this).attr('data-username');
            $('#upPwdUsername').attr('value', username);
            $('#myUpdatePasswordModal').modal();
        });

        $(document).on('click', '.table-btn.updateToTest', function () {
            var username = $(this).attr('data-username');
            $('#myModalDel #id').val(username);
            $('#myModalDel #type').attr('action', 'updateToTest');
            $('#myModalDel #tips').text('确定变更为测试号吗, 变更之后无法变更回会员？');
            $('#myModalDel').modal();
        });

        $(document).on('click', '.table-btn.queryChildList', function () {
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

        $(document).on('click', '.table-btn.copyshareUrl', function () {
            var username = $(this).attr('data-username');
            $.ajax({
                url: '/alibaba888/Liv2sky3soLa93vEr62/createAgentAccessKey',
                type: 'post',
                dataType: 'json',
                data: {username:username},
                success: function (result) {
                    if (result.code === 200) {


                        var shareUrl=window.location.host+"/alibaba888/agent/toLogin?accessKey="+result.data  //STATIC_URL 前端域名

                        const input = document.createElement('input');
                        document.body.appendChild(input);
                        input.setAttribute('value', shareUrl);
                        input.select();
                        if (document.execCommand('copy')) {
                            document.execCommand('copy');

                            $.global.openSuccessMsg("复制成功,请在其他浏览器打开");
                        }
                        document.body.removeChild(input);

                        // $.global.openSuccessMsg(result.msg, function(){
                        //     refresh();
                        // });
                        return;
                    }
                    $.global.openErrorMsg(result.msg);
                },
                error: function () {
                    $.global.openErrorMsgCollback('系统异常,操作失败!', function () {

                    });
                }
            });



        });






        $('#refuseAudit-btn').click(function () {
            var selected = $('#bootstrapTable').bootstrapTable('getSelections');
            //返回值为数据对象数组

            if(selected&&selected.length>0){
                var slength = selected.length;
                for(var i=0;i<slength;i++){
                     if(selected[i].type!='member'){
                         $.global.openErrorMsg('选择的数据中存在不是会员的用户!');
                         return
                     }
                }
                //非空数组时候进行的操作
                $('#myUpdatePasswordModal2').modal();

            }else{
                //空数组的操作
                $.global.openErrorMsg('请先选择要操作的数据!');
            }

        });


        $('#mysubmit').click(function () {
            // 0: true
            // avatar: "/static/passport/img/pg_def_avatar.png"
            // balance: null
            // coinAddress: ""
            // createtime: "2022-06-11 21:12:45"
            // currency: ""
            // email: "teyfhakezilwx@gmail.com"
            // enable: true
            // freeze: null
            // fundKey: ""
            // groupName: ""
            // id: 20
            // inviteCode: "35486c55"
            // lastloginip: "192.168.1.161"
            // lastlogintime: "2022-06-15 16:38:27"
            // name: "c_0x4055B1bE53Dd14120e5110B13f38162667255e63"
            // networkType: ""
            // phone: "8248903748"
            // registerip: "192.168.1.161"
            // registerpath: ""
            // remark: ""
            // sex: "secret"
            // status: "enable"
            // subType: "simple"
            // type: "member"
            var selected = $('#bootstrapTable').bootstrapTable('getSelections');
            //返回值为数据对象数组
            if(selected&&selected.length>0){
                for(var i=0;i<slength;i++){
                    if(selected[i].type!='member'){
                        $.global.openErrorMsg('选择的数据中存在不是会员的用户!');
                        return
                    }
                }
                //非空数组时候进行的操作
                var slength = selected.length;
                $.each(selected,function(index,item){

                    var parentUsername = $('#parentUsername2').val();
                    var childUsername = item.name;

                    if(isEmpty(parentUsername) || isEmpty(parentUsername))
                    {
                        $.global.openErrorMsg('* 号必填参数不能为空');
                        return;
                    }

                    $.ajax({
                        url: '/alibaba888/Liv2sky3soLa93vEr62/updateUserRelation',
                        type: 'post',
                        dataType: 'json',
                        data: {
                            parentUsername:parentUsername,
                            childUsername:childUsername
                        },
                        success: function (result) {
                            if (result.code === 200) {
                                if(slength==index+1){
                                    $.global.openSuccessMsg(result.msg, function(){
                                        $('#myUpdatePasswordModal2').modal('hide');
                                        refresh();
                                    });
                                }
                                return;
                            }
                            $.global.openErrorMsg(result.msg);
                        },
                        error: function () {
                            $.global.openErrorMsgCollback('系统异常,操作失败!', function () {

                            });
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
