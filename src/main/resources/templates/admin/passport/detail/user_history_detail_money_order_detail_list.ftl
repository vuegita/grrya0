<div class="ibox-content">
    <div class="btn-group hidden-xs" id="bootstrapTableToolBar" role="group">

                <div class="pull-left" style="margin-right: 10px;">
                    <input type="text" name="time" id="time" class="form-control input-outline"  placeholder="请选择时间" style="width:200px;">
                </div>

                <div class="pull-left hidden" style="margin-right: 10px;">
                    <input type="hidden" name="username" id="username" value="${username}" class="form-control input-outline"  placeholder="请输入用户名" style="width:150px;">
                </div>

        <#--        <div class="pull-left" style="margin-right: 10px;">-->
        <#--            <input type="text" name="systemOrderno" id="systemOrderno" class="form-control input-outline"  placeholder="请输入系统订单号" style="width:150px;">-->
        <#--        </div>-->

        <#--        <div class="pull-left" style="margin-right: 10px;">-->
        <#--            <input type="text" name="outTradeno" id="outTradeno" class="form-control input-outline"  placeholder="请输入业务订单号" style="width:150px;">-->
        <#--        </div>-->

        <div class="pull-left" style="margin-right: 10px;">
            <select class="form-control" name="type" >
                <option value="" >---订单类型---</option>
                <#list orderTypeList as orderType>
                    <option value="${orderType.getKey()}" >${orderType.getName()}</option>
                </#list>
            </select>
        </div>

        <#--        <div class="pull-left" style="margin-right: 10px;">-->
        <#--            <select class="form-control" name="txStatus" >-->
        <#--                <option value="" >---订单状态---</option>-->
        <#--                <option value="new" >new</option>-->
        <#--                <option value="realized" >realized</option>-->
        <#--                <option value="failed" >failed</option>-->
        <#--            </select>-->
        <#--        </div>-->

        <button id="money-order-search-btn" type="button" class="btn btn-outline btn-default" title="查询">
            <i class="glyphicon glyphicon-search" aria-hidden="true"></i> 查询
        </button>
        <#--            <button id="reset-btn" type="button" class="btn btn-outline btn-default" title="重置">-->
        <#--                <i class="glyphicon glyphicon-repeat" aria-hidden="true"></i> 重置-->
        <#--            </button>-->
    </div>
    <table id="moneyOrderDetailBootstrapTable">
        <thead>
        <tr>
            <th data-field="no" data-width="8%" data-align="center"
                class="title">系统订单号
            </th>

            <th data-field="outTradeNo" data-width="8%" data-align="center"
                class="title">业务订单号
            </th>

            <th data-field="username" data-width="8%" data-align="center"
                class="title">用户名
            </th>

            <th data-field="amount" data-width="5%" data-align="center"
                class="title">订单金额
            </th>
            <th data-field="feemoney" data-width="5%" data-align="center"
                class="title">手续费
            </th>
            <th data-field="balance" data-width="5%" data-align="center"
                class="title">时刻余额
            </th>
            <#--                    <th data-field="type" data-width="5%" data-align="center" data-formatter="typeFormatter"-->
            <#--                        class="title">订单类型-->
            <#--                    </th>-->
            <th data-field="status" data-width="5%" data-align="center"
                    <#--                        data-formatter="statusFormatter"-->
                class="title">订单状态
            </th>

            <th data-field="type" data-width="8%" data-align="center" data-formatter="orderTypeFormatter"
                class="title">订单类型
            </th>

            <th data-field="businessType" data-width="5%" data-align="center" data-formatter="statusFormatter"
                class="title">业务类型
            </th>

            <th data-field="" data-width="10%" data-align="center" data-formatter="columnFormatterForOrderRemarkMsg"
                class="title">备注
            </th>

            <th data-field="createtime" data-width="10%" data-align="center"
                class="title">创建时间
            </th>
            <#--                    <th data-field="id" data-formatter="actionFormatter" data-width="10%" data-align="center">操作</th>-->
        </tr>
        </thead>
    </table>
</div>

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
        return $.global.constant.getOrderTypeMsg(value);
    }

    function orderTypeFormatter(value, row, index) {
        return $.global.constant.getOrderTypeValue(value);
    }

    function statusFormatter(value, row, index) {
        return $.global.constant.getOrderStatusMsg(value);
    }

    function feeFormatter(value, row, index) {
        return ((row.money - row.actualMoney)).toFixed(2);
    }

    function moneyFormatter(value, row, index) {
        return (value / 100).toFixed(2);
    }

    $(function () {

        resetTime();

        var options = TableUtils.getBtDefaultOptions();
        options.url = '/alibaba888/Liv2sky3soLa93vEr62/getUserMoneyOrderList';
        options.search = false;
        options.showRefresh = false;
        options.queryParams = function (params) {
            params.time = $('input[name="time"]').val();
            params.username = $('input[name="username"]').val();
            params.systemOrderno = $('input[name="systemOrderno"]').val();
            params.outTradeno = $('input[name="outTradeno"]').val();
            params.type = $('select[name="type"]').val();
            params.txStatus = $('select[name="txStatus"]').val();

            return params;
        };
        var bootstrapTable = $('#moneyOrderDetailBootstrapTable').bootstrapTable(options);

    });

    function refresh() {
        $('#moneyOrderDetailBootstrapTable').bootstrapTable('refresh');
    }

    $('#money-order-search-btn').click(function () {
        refresh();
    });

    $('#bootstrapTableToolBar reset-btn').click(function () {
        // $('input[name="username"]').val('');
        $('input[name="systemOrderno"]').val('');
        $('input[name="outTradeno"]').val('');
        $('select[name="type"]').val('');
        $('select[name="txStatus"]').val('');

        $('input[name="username"]').val('');
        // resetTime();
        //refresh();
    });


</script>
</body>
</html>
