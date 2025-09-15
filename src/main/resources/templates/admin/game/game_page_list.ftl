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
            <h5>游戏列表</h5>
        </div>

        <div class="ibox-content">
            <div class="btn-group hidden-xs" id="bootstrapTableToolBar" role="group">

<#--                <div class="pull-left" style="margin-right: 10px;">-->
<#--                    <select class="form-control" name="type" >-->
<#--                        <option value="" selected>所有分类</option>-->
<#--                        <option value="lottery" >lottery</option>-->
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
                    <th data-field="categoryName" data-width="8%" data-align="center"
                        class="title">分类
                    </th>

                    <th data-field="title" data-width="8%" data-align="center"
                        class="title">名称
                    </th>

                    <th data-field="describe" data-width="8%" data-align="center"
                        class="title">描述
                    </th>

                    <th data-field="sort" data-width="8%" data-align="center"
                        class="title">排序
                    </th>

                    <th data-field="status" data-width="5%" data-align="center"
                        data-formatter="statusFormatter"
                        class="title">状态
                    </th>

                    <th data-field="createtime" data-width="10%" data-align="center"
                        class="title">创建时间
                    </th>

                    <th data-field="id" data-formatter="actionFormatter" data-width="20%" data-align="center">操作</th>
                </tr>
                </thead>
            </table>
        </div>
    </div>
</div>


<#include "../../common/delete_form.ftl">
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

        result += '<a href="javascript:;" class="table-btn editInfo" data-no="' + row.id + '" data-status="disable" title="编辑">编辑</a>';
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


    $(function () {
        // resetTime();

        var options = TableUtils.getBtDefaultOptions();
        options.url = '/alibaba888/Liv2sky3soLa93vEr62/getGameList';
        options.search = false;
        options.showRefresh = true;
        options.queryParams = function (params) {
            params.category = $('select[name="category"]').val();
            return params;
        };
        var bootstrapTable = $('#bootstrapTable').bootstrapTable(options);

        $(document).on('click', '.table-btn.editUserInfo', function () {
            var username = $(this).attr('data-username');
            window.location.href ='/alibaba888/Liv2sky3soLa93vEr62/toEditUserPage?username=' + username;
        });

        $(document).on('click', '.table-btn.updateStatus', function () {
            var status = $(this).attr('data-status');
            var no = $(this).attr('data-no');
            $('#myModalDel #id').val(no);
            $('#myModalDel #type').val(status);
            $('#myModalDel #type').attr('action', 'updateStatus');
            if("enable" == status)
            {
                $('#myModalDel #tips').text('确定启用吗？');
            }
            else
            {
                $('#myModalDel #tips').text('确定禁用吗？');
            }

            $('#myModalDel').modal();

        });

        $(document).on('click', '.table-btn.editInfo', function () {
            var id = $(this).attr('data-no');
            var url = '/alibaba888/Liv2sky3soLa93vEr62/root_game_page/edit/page?id=' + id;
            // window.location.href = url;
            openNewWindow(url);
        });

        $('#delete_submit').click(function () {
            var no = $('#myModalDel #id').val();
            var value = $('#myModalDel #type').val();
            var action = $('#myModalDel #type').attr('action');

            // console.log("action = " + action);

            if("updateUserType" == action)
            {
                updateUserStatus(no, value)
            }
            else if("updateStatus" == action)
            {
                updateUserStatus(no, value)
            }
        });

        $('#search-btn').click(function () {
            // if (isEmpty($('input[name="time"]').val())){
            //     $.global.openErrorMsg('请选择时间范围');
            //     return;
            // }
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

    function updateUserStatus(gameid, status) {
        $.ajax({
            url: '/alibaba888/Liv2sky3soLa93vEr62/updateGameStatus',
            type: 'post',
            dataType: 'json',
            data: {
                status:status,
                gameid:gameid
            },
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
