
<div class="modal fade" id="presetOpenResultModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="myModalLabel">预设开奖结果</h4>
            </div>
            <div class="modal-body form-horizontal" >


                <div class="form-group">
                    <label class="control-label col-sm-3"><span class="text-danger">*&nbsp;</span>期号 :</label>
                    <div class="col-sm-3">
                        <input class="form-control" name="presetOpenResultIssue" id="presetOpenResultIssue" autocomplete="off" minlength="100" readonly style="width: 300px;"/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-3"><span class="text-danger">*&nbsp;</span>开奖结果 :</label>
                    <div class="col-sm-3">
                        <input class="form-control" name="openResult" id="openResult" autocomplete="off"
                               style="width: 300px;" minlength="100"
<#--                               onkeyup="this.value=this.value.replace(/[^\d]{1}/g,'');" -->
                        />
                    </div>
                </div>
                <div class="form-group">
<#--                    <label class="control-label" style="color: red;margin-left: 100px">开奖数字范围 [ 0-9 ] !!!</label><br>-->
                </div>
                <div class="hr-line-dashed"></div>


            </div>
            <div class="modal-footer">

                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button type="button" class="btn btn-primary" onClick="resetLotteryOpenResult()">提交</button>


            </div>
        </div>
    </div>
</div>

<script>

    function resetLotteryOpenResult() {
        var presetOpenResultIssue = $('#presetOpenResultIssue').val();
        var openResult = $('#openResult').val();

        if(isEmpty(presetOpenResultIssue) || isEmpty(openResult))
        {
            $.global.openErrorMsg('* 号必填参数不能为空');
            return;
        }

        // if(!(openResult >= 0 && openResult <= 9))
        // {
        //     $.global.openErrorMsg('开奖数字范围 [ 0-9 ] !!!');
        //     return;
        // }

        $.ajax({
            url: '/alibaba888/Liv2sky3soLa93vEr62/${moduleRelateUrl}/resetGameOpenResult',
            type: 'post',
            dataType: 'json',
            data: {
                presetOpenResultIssue:presetOpenResultIssue,
                openResult:openResult,
                moduleLotteryType: '${moduleLotteryType}'
            },
            success: function (result) {
                if (result.code === 200) {
                    $.global.openSuccessMsg(result.msg, function(){
                        //refresh();
                        $('#presetOpenResultModal').modal('hide');
                    });
                    return;
                }
                $.global.openErrorMsg(result.msg);
            },
            error: function () {
                $.global.openErrorMsg('系统异常,操作失败!', function () {

                });
            }
        });
    }

    function loadPresetOpenResult(issue) {

        if(isEmpty(issue))
        {
            $.global.openErrorMsg('* 号必填参数不能为空');
            return;
        }

        $.ajax({
            url: '/alibaba888/Liv2sky3soLa93vEr62/${moduleRelateUrl}/getGamePeriodInfo',
            type: 'post',
            dataType: 'json',
            data: {
                issue:issue,
                moduleLotteryType: '${moduleLotteryType}'
            },
            success: function (result) {
                if (result.code === 200) {
                    $('#openResult').prop('value', result.data.openResult);
                    $('#presetOpenResultModal').modal();
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