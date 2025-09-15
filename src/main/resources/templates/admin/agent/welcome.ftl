<!DOCTYPE HTML>
<html>
<head>
    <#include "../../common/head-meta.ftl"/>
    <title>${projectName}后台管理系统-首页</title>
</head>

<body class="gray-bg">
    <div class="wrapper wrapper-content animated fadeInRight">
        <div class="ibox float-e-margins">
            <div class="ibox-title">
                <h5>代理后台首页</h5>
            </div>

            <div class="ibox-content">
                <div class="row row-lg">
                    <h1>欢迎光临</h1>
                </div>
            </div>

        </div>
    </div>

<script type="text/javascript">

    $(function(){
        var config = {
            '.chosen-select': {},
            '.chosen-select-deselect': {
                allow_single_deselect: true
            },
            '.chosen-select-no-single': {
                disable_search_threshold: 10
            },
            '.chosen-select-no-results': {
                no_results_text: 'Oops, nothing found!'
            },
            '.chosen-select-width': {
                width: "95%"
            }
        };

        for (var selector in config) {
            $(selector).chosen(config[selector]);
        }


    });
</script>

</body>
</html>
