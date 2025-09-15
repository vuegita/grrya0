<!DOCTYPE HTML>
<html>
<head>
    <#include "../../../common/head-meta.ftl"/>
    <title>${projectName}后台管理系统-角色管理</title>
    <link type="text/css" rel="stylesheet" href="${STATIC_URL}/plugins/bootstrap-table/bootstrap-table.min.css" />
</head>

<body class="gray-bg">
    <div class="wrapper wrapper-content animated fadeInRight">
        <div class="ibox">
            <div class="ibox-title">
                <h5>角色管理</h5>
            </div>

            <div class="ibox-content">
                <div class="btn-group hidden-xs" id="bootstrapTableToolBar" role="group">
                    <div class="pull-left" style="margin-right: 10px;">
                        <button id="create-btn" type="button" class="btn btn-outline btn-default" title="新增">
                            <i class="glyphicon glyphicon-plus" aria-hidden="true"></i> 新增
                        </button>
                    </div>
                </div>
                <table id="bootstrapTable">
                    <thead>
                    <tr>
                        <th data-field="name" data-width="8%" data-align="center"  class="title">角色名称</th>
                        <th data-field="num" data-width="8%" data-align="center"  class="title">数量</th>
                        <th data-field="remark" data-width="8%" data-align="center"  class="title">备注</th>
                        <th data-field="createtime" data-width="8%" data-align="center"  class="title">创建时间</th>
                        <th data-field="name" data-formatter="actionFormatter" data-width="10%" data-align="center">操作</th>
                    </tr>
                    </thead>
                </table>
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
            result += '<a href="javascript:;" class="table-btn edit" data-id="'+ value +'" title="编辑">编辑</a>';
            result += '<a href="javascript:;" class="table-btn editPermission" data-id="'+ value +'" title="权限设置">权限设置</a>';
            result += '<a href="javascript:;" class="table-btn del" data-id="'+ value +'" title="删除">删除</a>';

            return result;
        }

        function dateFormatter(value, row, index) {
            return DateUtils.formatyyyyMMddHHmm(value);
        }

        $(function(){
            var options = TableUtils.getBtDefaultOptions();
            options.url ='/alibaba888/Liv2sky3soLa93vEr62/getRoleList';
            options.search = false;
            options.showRefresh = false;
            options.showRefresh = true;
            var bootstrapTable = $('#bootstrapTable').bootstrapTable(options);

            $(document).on('click', '#create-btn', function(){
                window.location.href ='/alibaba888/Liv2sky3soLa93vEr62/toEditRole';
            });

            $(document).on('click', '.table-btn.edit', function(){
                var id = $(this).attr('data-id');
                window.location.href ='/alibaba888/Liv2sky3soLa93vEr62//toEditRole?roleName='+ id;
            });

            $(document).on('click', '.table-btn.del', function(){
                var newUrl =  '/alibaba888/Liv2sky3soLa93vEr62/getAdminSizeByRole';
                var data = {roleName : $(this).attr('data-id')};
                $.ajax({
                    url: newUrl,
                    type: 'post',
                    dataType: 'json',
                    data: data,
                    success: function(result){
                        var count = result.data;
                        if (count > 0){
                            if(window.confirm('当前共有' + count + '人属于该角色组，若删除本组则也会删除管理员账号。确定删除吗？？')){
                                del(data)
                            }

                        }else {
                            if(window.confirm('确定要删除该角色组吗？')){
                                del(data)
                            }
                        }
                    },
                    error: function(err){
                        $.global.openErrorMsgCollback('系统异常,操作失败',function () {
                        });
                    }
                });
            });

            $(document).on('click', '.table-btn.editPermission', function(){
                var id = $(this).attr('data-id');
                window.location.href ='/alibaba888/Liv2sky3soLa93vEr62/toEditRolePermissionPage?roleName=' + id;
            });
        });
        function refresh(){
            $('#bootstrapTable').bootstrapTable('refresh');
        }

        function del(data) {
            var newUrl =  '/alibaba888/Liv2sky3soLa93vEr62/deleteRole';
            $.global.ajaxPost(newUrl, data,'refresh');
        }

        function isInt(value) {
            var aint=parseInt(value);
            return aint >= 0&& (aint+"")==value;
        }
    </script>
</body>
</html>
