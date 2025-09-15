<div class="ibox-content">

    <div class="btn-group hidden-xs" id="bootstrapTableToolBar" role="group">

<#--        <div class="pull-left" style="margin-right: 10px;">-->
<#--            <input type="text" name="time" id="time" class="form-control input-outline"  placeholder="请选择时间" style="width:200px;">-->
<#--        </div>-->

<#--        <div class="pull-left" style="margin-right: 10px;">-->
<#--            <input type="text" name="issue" id="issue" class="form-control input-outline"  placeholder="请输入期号" style="width:150px;">-->
<#--        </div>-->

<#--        <div class="pull-left" style="margin-right: 10px;">-->
<#--            <input type="text" name="orderno" id="orderno" class="form-control input-outline"  placeholder="请输入订单号" style="width:150px;">-->
<#--        </div>-->

        <div class="pull-left" style="margin-right: 10px;">
            <input type="hidden" name="username" id="username" value="${username}" class="form-control input-outline"  placeholder="请输入用户名" style="width:150px;">
        </div>

<#--        <div class="pull-left" style="margin-right: 10px;">-->
<#--            <select class="form-control" name="type" id="type" >-->
<#--                &lt;#&ndash;                        <option value="" >---请选择彩种类型---</option>&ndash;&gt;-->
<#--                <#list gameList as game>-->
<#--                    <option value="${game.key}" >${game.getShowLotteryType()}</option>-->
<#--                </#list>-->
<#--            </select>-->
<#--        </div>-->

<#--        <div class="pull-left" style="margin-right: 10px;">-->
<#--            <select class="form-control" name="txStatus" >-->
<#--                <option value="" >---请选择状态---</option>-->
<#--                <option value="new" >new</option>-->
<#--                <option value="waiting" >waiting</option>-->
<#--                <option value="realized" >realized</option>-->
<#--                <option value="failed" >failed</option>-->
<#--            </select>-->
<#--        </div>-->

<#--        <button id="search-btn" type="button" class="btn btn-outline btn-default" title="查询">-->
<#--            <i class="glyphicon glyphicon-search" aria-hidden="true"></i> 查询-->
<#--        </button>-->
<#--        <button id="reset-btn" type="button" class="btn btn-outline btn-default" title="重置">-->
<#--            <i class="glyphicon glyphicon-repeat" aria-hidden="true"></i> 重置-->
<#--        </button>-->
    </div>
    <table id="gameRgBootstrapTable">
        <thead>
        <tr>
            <th data-field="issue" data-width="8%" data-align="center"
                class="title">期号
            </th>

            <th data-field="type" data-width="5%" data-align="center"
                class="title">彩种类型
            </th>

            <th data-field="no" data-width="8%" data-align="center"
                class="title">系统订单号
            </th>

            <th data-field="username" data-width="5%" data-align="center"
                class="title">用户名
            </th>

            <th data-field="betAmount" data-width="5%" data-align="center"
                class="title">投注金额
            </th>

            <th data-field="winAmount" data-width="5%" data-align="center"
                class="title">中奖金额
            </th>

            <th data-field="" data-width="5%" data-align="center" data-formatter="platformProfitFormatter"
                class="title">平台盈利
            </th>

            <th data-field="feemoney" data-width="5%" data-align="center"
                class="title">手续费
            </th>

            <th data-field="betItem" data-width="5%" data-align="center"
                class="title">投注项
            </th>

            <th data-field="openResult" data-width="5%" data-align="center"
<#--                data-formatter="openResultFormatter"-->
                class="title">开奖数字
            </th>

            <th data-field="status" data-width="5%" data-align="center"
                class="title">订单状态
            </th>

            <th data-field="createtime" data-width="10%" data-align="center"
                class="title">创建时间
            </th>

            <th data-field="updatetime" data-width="10%" data-align="center"
                class="title">更新时间
            </th>

        </tr>
        </thead>
    </table>
</div>


<script type="text/javascript">

    function platformProfitFormatter(value, row, index) {
        return (row.betAmount - row.winAmount).toFixed(2);
    }

    function openResultFormatter(value, row, index) {
        if (row.status != 'finish') {
            return "-";
        } else {
            return value;
        }
    }

    function getTime() {
        var date1 = new Date();
        var time1 = date1.getFullYear()+"-"+(date1.getMonth()+1)+"-"+date1.getDate();
        var date2 = new Date(date1);
        date2.setDate(date1.getDate()-7);
        var time2 = date2.getFullYear()+"-"+(date2.getMonth()+1)+"-"+date2.getDate();
        var date = time2 + " - " + time1;
        return date;
    }

    $(function () {

        var time = getTime();

        var options = TableUtils.getBtDefaultOptions();
        options.url = '/alibaba888/Liv2sky3soLa93vEr62/getLotteryOrderList';
        options.search = false;
        options.showRefresh = false;
        options.pageSize = 15;
        options.queryParams = function (params) {
            params.time = time;
            params.type = $('select[name="type"]').val();
            params.txStatus = $('select[name="txStatus"]').val();
            params.issue = $('input[name="issue"]').val();
            params.orderno = $('input[name="orderno"]').val();
            params.username = $('input[name="username"]').val();

            return params;
        };
        var bootstrapTable = $('#gameRgBootstrapTable').bootstrapTable(options);
    });


</script>