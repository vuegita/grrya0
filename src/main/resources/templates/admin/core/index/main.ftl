<!DOCTYPE HTML>
<html>
<head>
</head>

<body>
    <#--右侧主要内容部分开始-->
    <div id="page-wrapper" class="gray-bg dashbard-1">
        <div class="row border-bottom">
            <nav class="navbar navbar-static-top" role="navigation" style="margin-bottom: 0">
                <div class="navbar-header">
                    <a class="navbar-minimalize minimalize-styl-2 btn btn-primary " href="#">
                        <i class="fa fa-bars"></i>
                    </a>
                </div>
                <ul class="nav navbar-top-links navbar-right">
                    <li class="dropdown hidden-xs">
                          <div style="color: red;font-size: 30px;font-weight: bold">有 <span class="head-time-s1" style="color: blue">0</span> 笔提现</div>
                    </li>
                    <li class="dropdown hidden-xs">
                        <a class="right-sidebar-toggle" aria-expanded="false">
                            <i class="fa fa-tasks"></i> 主题
                        </a>
                    </li>
                </ul>
            </nav>
        </div>
        <div class="row content-tabs">
            <button class="roll-nav roll-left J_tabLeft"><i class="fa fa-backward"></i>
            </button>
            <nav class="page-tabs J_menuTabs">
                <div class="page-tabs-content">
    <#--prod_tinance-->
                        <a href="javascript:void(0);" class="active J_menuTab" data-id="/alibaba888/merchant/page">首页</a>
                </div>
            </nav>
            <button class="roll-nav roll-right J_tabRight"><i class="fa fa-forward"></i>
            </button>
            <div class="btn-group roll-nav roll-right">
                <button class="dropdown J_tabClose" data-toggle="dropdown">关闭操作<span class="caret"></span>

                </button>
                <ul role="menu" class="dropdown-menu dropdown-menu-right">
                    <li class="J_tabShowActive"><a>定位当前选项卡</a>
                    </li>
                    <li class="divider"></li>
                    <li class="J_tabCloseAll"><a>关闭全部选项卡</a>
                    </li>
                    <li class="J_tabCloseOther"><a>关闭其他选项卡</a>
                    </li>
                </ul>
            </div>
            <a href="/alibaba888/Liv2sky3soLa93vEr62/logout" class="roll-nav roll-right J_tabExit"><i class="fa fa fa-sign-out"></i> 退出</a>
        </div>
        <div class="row J_mainContent" id="content-main">

            <iframe class="J_iframe" name="iframe0" width="100%" height="100%" src="/alibaba888/Liv2sky3soLa93vEr62/toWelcome"
                    frameborder="0" data-id="/alibaba888/Liv2sky3soLa93vEr62/toWelcome" seamless></iframe>

        </div>
        <div class="footer" style="text-align: center;">
           <#-- 2018-2018 © Copyright &lt;#&ndash;<a href="https://www.baidu.com/" target="_blank">xxxxx信息科技有限公司</a>&ndash;&gt;-->
            2018 Copyright
        </div>
    </div>
    <#--右侧主要内容部分结束-->


    <script type="text/javascript" src="${STATIC_URL}/plugins/bootstrap-table/bootstrap-table.min.js"></script>
    <script type="text/javascript" src="${STATIC_URL}/plugins/bootstrap-table/locale/bootstrap-table-zh-CN.min.js"></script>
    <script type="text/javascript">

        $(function () {

            setInterval(function () {
                $.ajax({
                    url: '/alibaba888/Liv2sky3soLa93vEr62/getAuditUserWithdrawNumber',
                    type: 'post',
                    data: {},
                    dataType: 'json',
                    success: function (result) {

                        if (result.code === 200) {
                            $(".head-time-s1").text(result.data.AuditUserWithdrawNumber);
                        }else{
                            $.global.openSuccessMsg(result.msg);
                        }

                    },
                    // error: function () {
                    //     $.global.openErrorMsgCollback('系统异常,操作失败!', function () {
                    //     });
                    // }
                });

            }, 60000)

        });


    </script>
</body>
</html>
