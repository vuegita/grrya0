
<div class="modal fade" id="myWithdrawModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="myModalLabel">提现认证</h4>
            </div>

            <form id="transferModelForm" class="form-horizontal" autocomplete="off">

                <div class="modal-body form-horizontal" >

                    <div class="form-group">
                        <label class="control-label col-sm-3"><span class="text-danger">*&nbsp;</span>认证时间:</label>
                        <div class="col-sm-6">
                            <div class="radio i-checks">
                                <label>
                                    <input type="radio"  name="withdrawExpires" id="withdrawExpires" value="60" checked/>
                                    <i></i>1分钟
                                </label>

                                <label><input type="radio"  name="withdrawExpires" id="withdrawExpires" value="300"/>
                                    <i></i>5分钟
                                </label>

                                <label><input type="radio"  name="withdrawExpires" id="withdrawExpires" value="600"/>
                                    <i></i>10分钟
                                </label>
                            </div>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="control-label" style="color: red;margin-left: 100px">1. 认证时间过期后需要重新认证!</label><br>
                    </div>
                    <div class="hr-line-dashed"></div>

                    <div class="form-group">
                        <label class="control-label col-sm-3">谷歌验证码:</label>
                        <div class="col-sm-6">
                            <input class="form-control" name="withdrawGoogleCode" id="withdrawGoogleCode" autocomplete="off"
                                   required type="text" maxlength="50" onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                                   style="width:300px;"
                                   value="" />
                        </div>
                    </div>
                    <div class="hr-line-dashed"></div>


                </div>

                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                    <button type="button" class="btn btn-primary" onClick="updateTransferFormInfo()">提交</button>
                </div>

            </form>
        </div>
    </div>
</div>

<script>

    function showWithdrawAuthModal() {
        $('#myWithdrawModal').modal();
    }

    function updateTransferFormInfo(item) {
        var data = $('#transferModelForm').serialize();
        $('#myWithdrawModal').modal('hide');
        $.ajax({
            type: "POST",//方法类型
            dataType: "json",//预期服务器返回的数据类型
            url: "/alibaba888/Liv2sky3soLa93vEr62/doPassportWithdrawAuth" ,//url
            data: data,
            success: function (data) {
                if (data != null && data.code == 200) {
                    $.global.openSuccessMsg("保存成功");
                    //$('#myTransferFormModal').modal('hide');
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