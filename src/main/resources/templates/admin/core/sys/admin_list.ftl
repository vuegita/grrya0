<!DOCTYPE HTML>
<html>
<head>
    <#include "../../../common/head-meta.ftl"/>
    <title>${projectName}后台管理系统-管理员管理</title>
    <link type="text/css" rel="stylesheet" href="${STATIC_URL}/plugins/bootstrap-table/bootstrap-table.min.css" />
</head>

<body class="gray-bg">
    <div class="wrapper wrapper-content animated fadeInRight">
        <div class="ibox">
            <div class="ibox-title">
                <h5>管理员管理</h5>
            </div>

            <div class="ibox-content">
                <div class="btn-group hidden-xs" id="bootstrapTableToolBar" role="group">
                    <div class="pull-left" style="margin-right: 10px;">
                        <button id="create-btn" type="button" class="btn btn-outline btn-default" title="新增">
                            <i class="glyphicon glyphicon-plus" aria-hidden="true"></i> 新增
                        </button>
                    </div>
                    <div class="pull-left" style="margin-right: 10px;">
                        <input type="text" name="account" class="form-control input-outline"  placeholder="请输入账号">
                    </div>
                    <div class="pull-left" style="margin-right: 10px;">
<#--                        <select class="form-control" name="roleid" >-->
<#--                            <option value="" >所属角色</option>-->
<#--                            <#list roleList as role>-->
<#--                                 <option value="${role.id}" >${role.name}</option>-->
<#--                            </#list>-->
<#--                        </select>-->
                    </div>

                    <button id="search-btn" type="button" class="btn btn-outline btn-default" title="查询">
                        <i class="glyphicon glyphicon-search" aria-hidden="true"></i> 查询
                    </button>
                    <button id="reset-btn" type="button" class="btn btn-outline btn-default" title="重置">
                        <i class="glyphicon glyphicon-repeat" aria-hidden="true"></i>
                         重置
                    </button>
                </div>
                <table id="bootstrapTable">
                    <thead>
                    <tr>
                        <th data-field="account" data-width="15%" data-align="center" class="title">账号</th>
                        <th data-field="rolename" data-width="15%" data-align="center" class="title">角色</th>
                        <#--<th data-field="phone" data-width="15%" data-align="center"  class="title">手机号</th>-->
                        <th data-field="lastlogintime" data-width="15%" data-align="center"  class="title">最后登录时间</th>
                        <th data-field="lastloginip" data-width="15%" data-align="center"  class="title">最后登录ip</th>
                        <th data-field="lastloginarea" data-width="10%" data-align="center"  class="title">登录地区</th>
                        <th data-field="remark" data-width="15%" data-align="center"  class="title">备注</th>
                        <th data-field="account" data-formatter="actionFormatter" data-width="10%" data-align="center">操作</th>
                    </tr>
                    </thead>
                </table>
            </div>
        </div>
    </div>

    <div class="modal fade" id="myModalDel" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
        <input type="hidden" name="adminId" class="form-control" id="adminId" placeholder="adminId">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                    <h4 class="modal-title" id="myModalLabel">提示</h4>
                </div>
                <div class="modal-body">
                    <p>确定要删除吗？</p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span>取消</button>
                    <button type="button" id="btn_submit" class="btn btn-primary" data-dismiss="modal">
                        <span class="glyphicon glyphicon-floppy-disk" aria-hidden="true"></span>确认
                    </button>
                </div>
            </div>
        </div>
    </div>

    <script type="text/javascript" src="${STATIC_URL}/plugins/bootstrap-table/bootstrap-table.min.js"></script>
    <script type="text/javascript" src="${STATIC_URL}/plugins/bootstrap-table/locale/bootstrap-table-zh-CN.min.js"></script>
    <script type="text/javascript">

        /**
         * 操作栏的格式化
         */
        function actionFormatter(value, row, index) {
            var result = "";
            result += '<a href="javascript:;" class="table-btn googleKey" data-id="'+ value +'" title="谷歌验证">谷歌验证</a>';
            result += '<a href="javascript:;" class="table-btn edit" data-id="'+ value +'" title="编辑">编辑</a>';
            result += '<a href="javascript:;" class="table-btn del" data-id="'+ value +'" title="删除">删除</a>';
            return result;
        }

        function dateFormatter(value, row, index) {
            if (value == null){
                return "";
            }else{
                return DateUtils.formatyyyyMMddHHmmss(value);
            }
        }


        $(function(){
            var options = TableUtils.getBtDefaultOptions();
            options.url = '/alibaba888/Liv2sky3soLa93vEr62/getAdminList';
            options.search = false;
            options.showRefresh = false;
            options.showRefresh = true;
            options.queryParams = function(params) {
                params.account = $('input[name="account"]').val();
                params.roleid = $('select[name="roleid"]').val();
                return params;
            };
            var bootstrapTable = $('#bootstrapTable').bootstrapTable(options);

            $(document).on('click', '#create-btn', function(){
                window.location.href ='/alibaba888/Liv2sky3soLa93vEr62/toAddOrEditAdminPage';
            });


            $(document).on('click', '.table-btn.googleKey', function(){
                var account = $(this).attr('data-id');
                window.location.href ='/alibaba888/Liv2sky3soLa93vEr62/toAdminGoogleKey?account='+ account;
            });

            $(document).on('click', '.table-btn.edit', function(){
                var account = $(this).attr('data-id');
                window.location.href ='/alibaba888/Liv2sky3soLa93vEr62/toAddOrEditAdminPage?account='+ account;
            });

            $(document).on('click', '.table-btn.del', function(){
                $("#adminId").val($(this).attr('data-id'));
                $('#myModalDel').modal();
                /*var url = '/alibaba888/del';
                var data = {id : $(this).attr('data-id')};
                $.global.ajaxPost(url, data,"refresh");*/
            });
            //确认删除
            $("#myModalDel #btn_submit").click(function () {
                var url = '/alibaba888/Liv2sky3soLa93vEr62/deleteAdmin';
                var data = {account : $("#myModalDel #adminId").val()};
                $.global.ajaxPost(url, data,"refresh");
            });

            $('#search-btn').click(function(){
                refresh();
            });
            $('#reset-btn').click(function(){
                $('input[name="account"]').val('');
                $('select[name="roleid"]').val('');
                refresh();
            });
        });
        function refresh(){
            $('#bootstrapTable').bootstrapTable('refresh');
        }
    </script>
</body>
</html>
