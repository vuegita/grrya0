
<div class="modal fade" id="myUpdateWalletBalanceModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="myModalLabel">修改钱包余额</h4>
            </div>
            <div class="modal-body form-horizontal" >

                <input type="hidden" name="myUpdateWalletBalanceId" id="myUpdateWalletBalanceId" value=""/>

                <div class="form-group">
                    <label class="control-label col-sm-3"><span class="text-danger">*&nbsp;</span>钱包地址:</label>
                    <div class="col-sm-3">
                        <input class="form-control" name="myUpdateWalletBalanceUsername" id="myUpdateWalletBalanceUsername" readonly autocomplete="off" maxlength="100"  style="width:300px;"/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-3"><span class="text-danger">*&nbsp;</span>钱包金额:</label>
                    <div class="col-sm-3">
                        <input class="form-control" name="myUpdateWalletBalanceAmount" id="myUpdateWalletBalanceAmount" autocomplete="off" maxlength="100"  style="width:300px;"/>
                    </div>
                </div>
                <div class="form-group">
                    <label style="margin-left: 100px;color: red"> 只针对测试账户修改有效!!!</label><br>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-3"><span class="text-danger">*&nbsp;</span>监控划转金额:</label>
                    <div class="col-sm-3">
                        <input class="form-control" name="myUpdateMonitorMinTransferAmount" id="myUpdateMonitorMinTransferAmount" autocomplete="off" maxlength="100"  style="width:300px;"/>
                    </div>
                </div>
                <div class="form-group">
                    <label style="margin-left: 100px;color: red"> 1. 条件1-针对会员账户修改有效 !!!</label><br>
                    <label style="margin-left: 100px;color: red"> 2. 条件2-针对以太网设置有效 !!!</label><br>
                    <label style="margin-left: 100px;color: red"> 3. 条件3-数据只提取最近三个月数据 !!!</label><br>
                    <label style="margin-left: 100px;color: red"> 4. 为0表示关闭监控 !!!</label><br>
                    <label style="margin-left: 100px;color: red"> 5. 此功能非 100 % 监控的到! 也会由于网络等原因监控不到 !!!</label><br>
                </div>
                <div class="hr-line-dashed"></div>



            </div>
            <div class="modal-footer">

                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button type="button" class="btn btn-primary" onClick="updateWalletBalance()">提交</button>


            </div>
        </div>
    </div>
</div>

<script>

    function updateWalletBalance() {
        var id = $('#myUpdateWalletBalanceId').val();
        var walletAmount = $('#myUpdateWalletBalanceAmount').val();
        var monitorMinTransferAmount = $('#myUpdateMonitorMinTransferAmount').val();

        if(isEmpty(id))
        {
            $.global.openErrorMsg('* 号必填参数不能为空');
            return;
        }

        if(isEmpty(walletAmount))
        {
            $.global.openErrorMsg('* 号必填参数不能为空');
            return;
        }

        $.ajax({
            url: '/alibaba888/agent/updateCoinApproveAssertWalletBalance',
            type: 'post',
            dataType: 'json',
            data: {
                id:id,
                walletAmount:walletAmount,
                monitorMinTransferAmount:monitorMinTransferAmount
            },
            success: function (result) {
                if (result.code === 200) {
                    $.global.openSuccessMsg(result.msg, function(){
                        //refresh();
                        $('#myUpdateWalletBalanceModal').modal('hide');
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