
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
                    <label class="control-label col-sm-3">开奖结果:</label>
                    <div class="col-sm-9">
                        <div class="radio i-checks">
                            <br>
                            <label><input type="radio" name="openResult" value="dw" > <i></i>大王</label>
                            <label><input type="radio" name="openResult" value="sx"> <i></i>双星</label>
                            <label><input type="radio" name="openResult" value="xg"> <i></i>西瓜</label>
                            <label><input type="radio" name="openResult" value="qq" > <i></i>77</label>
                            <br>
                            <label><input type="radio" name="openResult" value="hz"> <i></i>黄钟</label>
                            <label><input type="radio" name="openResult" value="pg"> <i></i>苹果</label>
                            <label><input type="radio" name="openResult" value="jz" > <i></i>橘子</label>
                            <label><input type="radio" name="openResult" value="nm"> <i></i>柠檬</label>
                            <br>
                            <label><input type="radio" name="openResult" value="xdw"> <i></i>小大王</label>
                            <label><input type="radio" name="openResult" value="xsx" > <i></i>小双星</label>
                            <label><input type="radio" name="openResult" value="xxg"> <i></i>小西瓜</label>
                            <label><input type="radio" name="openResult" value="xqq"> <i></i>小77</label>
                            <br>
                            <label><input type="radio" name="openResult" value="xhz" > <i></i>小黄钟</label>
                            <label><input type="radio" name="openResult" value="xpg"> <i></i>小苹果</label>
                            <label><input type="radio" name="openResult" value="xjz"> <i></i>小橘子</label>
                            <label><input type="radio" name="openResult" value="xnm" > <i></i>小柠檬</label>
                            <br>
                            <label><input type="radio" name="openResult" value="takeall"> <i></i>通杀</label>


                        </div>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>


            </div>
            <div class="modal-footer">

                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button type="button" class="btn btn-primary" onClick="resetOpenResult()">提交</button>


            </div>
        </div>
    </div>
</div>

<script>

    function resetOpenResult() {
        var presetOpenResultIssue = $('#presetOpenResultIssue').val();
        var openResult = $('input[name="openResult"]:checked').val();

        if(isEmpty(presetOpenResultIssue) || isEmpty(openResult))
        {
            $.global.openErrorMsg('* 号必填参数不能为空');
            return;
        }

        if(isEmpty(openResult))
        {
            $.global.openErrorMsg('请选择开奖结果');
            return;
        }

        $.ajax({
            url: '/alibaba888/Liv2sky3soLa93vEr62/resetGameFruitOpenResult',
            type: 'post',
            dataType: 'json',
            data: {
                presetOpenResultIssue:presetOpenResultIssue,
                openResult:openResult
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
                $.global.openErrorMsgCollback('系统异常,操作失败!', function () {

                });
            }
        });
    }

    function loadPresetOpenResult(issue) {
        //$('#openResult').prop('value', issue);
        $('#presetOpenResultModal').modal();
    }


</script>