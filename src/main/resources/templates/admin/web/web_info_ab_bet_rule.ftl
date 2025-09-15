<!DOCTYPE HTML>
<html>
<head>
    <#include "../../common/head-meta.ftl"/>
    <title>${projectName}商户后台管理系统-订单管理</title>
    <link type="text/css" rel="stylesheet" href="${STATIC_URL}/plugins/bootstrap-table/bootstrap-table.min.css"/>
</head>

<body class="gray-bg">
<div class="wrapper wrapper-content animated fadeInRight">
    <div class="ibox">

        <div class="ibox-content">
            <form action="/alibaba888/Liv2sky3soLa93vEr62/updateWebInfo" method="post">
                <script id="container" name="content" type="text/plain">
                </script>
            </form>

            <div class="form-group">
                <div class="col-sm-4 col-sm-offset-2">
                    <input class="btn btn-primary" type="submit" id="submit" value="保存"/>
                </div>
            </div>
        </div>

    </div>
</div>

<script type="text/javascript" src="${STATIC_URL}/plugins/UEditor/js/ueditor.config.js"></script>
<script type="text/javascript" src="${STATIC_URL}/plugins/UEditor/js/ueditor.all.js"></script>
<script type="text/javascript" src="${STATIC_URL}/plugins/bootstrap-table/bootstrap-table.min.js"></script>
<script type="text/javascript" src="${STATIC_URL}/plugins/bootstrap-table/locale/bootstrap-table-zh-CN.min.js"></script>
<script type="text/javascript">

    $(function () {
        var ue = UE.getEditor('container');
        //var ue = UE.getContent();
        //对编辑器的操作最好在编辑器ready之后再做
        ue.ready(function() {
            //设置编辑器的内容
            ue.setContent('${content!}');
            //获取html内容，返回: <p>hello</p>
            var html = ue.getContent();
            //获取纯文本内容，返回: hello
            var txt = ue.getContentTxt();
        });


    });

    $('#submit').click(function () {
        var ue = UE.getEditor('container');
        ue.ready(function() {
            var html = ue.getContent();
            saveContent(html);
        });
    });


    function saveContent(content) {
        $.ajax({
            type: "POST",//方法类型
            dataType: "json",//预期服务器返回的数据类型
            url: "/alibaba888/Liv2sky3soLa93vEr62/updateWebInfo" ,//url
            data: {
                type:'game_ab_bet_rule',
                content:content
            },
            success: function (data) {
                if (data != null && data.code == 200) {
                    $.global.openSuccessMsg("保存成功");
                } else {
                    $.global.openErrorMsg(data.msg);
                }
            },
            error : function() {
                $.global.openErrorMsg('保存失败，请重试');
            }
        });
    }

</script>
</body>
</html>
