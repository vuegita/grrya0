<!DOCTYPE HTML>
<html>
<head>
    <#include "../../../common/head-meta.ftl"/>
    <title>${projectName}后台管理系统-菜单管理</title>
    <link type="text/css" rel="stylesheet" href="${STATIC_URL}/plugins/bootstrap-table/bootstrap-table.min.css" />
</head>

<body class="gray-bg">
    <div class="wrapper wrapper-content animated fadeInRight">
        <div class="ibox">
            <div class="ibox-title">
                <h5>菜单管理</h5>
            </div>

            <div class="ibox-content">
                <div class="btn-group hidden-xs" id="bootstrapTableToolBar" role="group">
                   <#-- <div class="pull-left" style="margin-right: 10px;">
                        <button id="create-btn" type="button" class="btn btn-outline btn-default" title="新增">
                            <i class="glyphicon glyphicon-plus" aria-hidden="true"></i> 新增
                        </button>
                    </div>-->
                </div>
                <table id="bootstrapTable">
                    <thead>
                    <tr>
                        <th data-field="level" data-formatter="levelFormatter"  data-width="20%" data-align="center" class="title">菜单级别</th>
                        <th data-field="name" data-width="20%" data-align="center" class="title">菜单名称</th>
                        <th data-field="sort" data-width="20%" data-formatter="sortFormatter"  data-align="center"  class="title">菜单排序</th>
                        <th data-field="link" data-width="20%" data-align="center"  class="title">菜单链接</th>
<#--                        <th data-field="safeStatus" data-width="10%" data-formatter="safeStatusFormatter" data-align="center"  class="title">安全状态</th>-->
                        <th data-field="key" data-formatter="actionFormatter" data-width="10%" data-align="center">操作</th>
                    </tr>
                    </thead>
                </table>
            </div>
        </div>
    </div>

    <div class="modal fade" id="myModalDel" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
        <input type="hidden" name="menuId" class="form-control" id="menuId" placeholder="menuId">
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
            result += '<a href="javascript:;" class="table-btn edit" data-id="'+ value +'" title="编辑">编辑</a>';
            // result += '<a href="javascript:;" class="table-btn del" data-id="'+ value +'" title="删除">删除</a>';
            return result;
        }

        function dateFormatter(value, row, index) {
            return DateUtils.formatyyyyMMddHHmm(value);
        }

        function levelFormatter(value, row, index) {
            var v = value + 1;
            if (v == 2){
                return '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<i class="glyphicon glyphicon-arrow-right" aria-hidden="true"></i>'+v;
            }
            return v;

        }
        function sortFormatter(value, row, index) {
            if(row.level != 0){
                return '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<i class="glyphicon glyphicon-arrow-right" aria-hidden="true"></i>'+value;
            }else{
                return value;
            }
        }

        function safeStatusFormatter(value, row, index) {
            if (value === 'safe'){
                return '<span style="color: green">安全</span>';
            }
            return '<span style="color: red">非安全</span>';
        }

        $(function(){
            var options = TableUtils.getBtDefaultOptions();
            options.url = '/alibaba888/Liv2sky3soLa93vEr62/getMenuList';
            options.search = false;
            options.showRefresh = false;
            options.showRefresh = true;
            options.queryParams = function(params) {
                params.account = $('input[name="account"]').val();
                params.roleKey = $('select[name="roleKey"]').val();
                return params;
            };
            var bootstrapTable = $('#bootstrapTable').bootstrapTable(options);

            $(document).on('click', '.table-btn.edit', function(){
                var id = $(this).attr('data-id');
                window.location.href = '/alibaba888/Liv2sky3soLa93vEr62/toEditMenu?key='+ id;
            });
            $(document).on('click', '.table-btn.del', function(){
                $("#menuId").val($(this).attr('data-id'));
                $('#myModalDel').modal();
                /*var url = '/alibaba888/menu/del';
                var data = {id : $(this).attr('data-id')};
                $.global.ajaxPost(url, data);
                window.location.href="/alibaba888/toLogin"*/
            });
            //确认删除
            $("#myModalDel #btn_submit").click(function () {
                var url = '/alibaba888/menu/del';
                var data = {id : $("#myModalDel #menuId").val()};
                $.global.ajaxPost(url, data, "refresh");
                //window.location.href="/alibaba888/toLogin"
            });

        });
        function refresh(){
            $('#bootstrapTable').bootstrapTable('refresh');
        }
    </script>
</body>
</html>
