
<div class="modal fade" id="myUpWithdrawlQuoteModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="myModalLabel">更新提现额度</h4>
            </div>
            <div class="modal-body form-horizontal" >

                <input type="hidden" name="upWithdrawlQuoteUsername" id="upWithdrawlQuoteUsername" />

                <div class="form-group">
                    <label class="control-label col-sm-2">提现额度:</label>
                    <div class="col-sm-10">
                        <input class="form-control" name="withdrawlQuote" id="withdrawlQuote" autocomplete="off"
                               required maxlength="10" type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');" />
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

            </div>
            <div class="modal-footer">

                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button type="button" class="btn btn-primary" onClick="updateWithdrawlQuote()">提交</button>


            </div>
        </div>
    </div>
</div>

<script>

    function updateWithdrawlQuote() {
        var upWithdrawlQuoteUsername = $('#upWithdrawlQuoteUsername').val();
        var withdrawlQuote = $('#withdrawlQuote').val();

        if(isEmpty(upWithdrawlQuoteUsername) || isEmpty(withdrawlQuote))
        {
            $.global.openErrorMsg('* 号必填参数不能为空');
            return;
        }

        $.ajax({
            url: '/alibaba888/Liv2sky3soLa93vEr62/updateUserVIPWithdrawlQuote',
            type: 'post',
            dataType: 'json',
            data: {
                username:upWithdrawlQuoteUsername,
                quote:withdrawlQuote
            },
            success: function (result) {
                if (result.code === 200) {
                    $.global.openSuccessMsg(result.msg, function(){
                        //refresh();
                        $('#myUpWithdrawlQuoteModal').modal('hide');
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