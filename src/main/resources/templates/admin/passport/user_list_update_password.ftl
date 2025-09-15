
<div class="modal fade" id="myUpdatePasswordModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="myModalLabel">修改密码</h4>
            </div>
            <div class="modal-body form-horizontal" >


                <div class="form-group">
                    <label class="control-label col-sm-3"><span class="text-danger">*&nbsp;</span>用户名:</label>
                    <div class="col-sm-3">
                        <input class="form-control" name="upPwdUsername" id="upPwdUsername" readonly autocomplete="off" maxlength="100"  style="width:300px;"/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-3"><span class="text-danger">*&nbsp;</span>登陆密码:</label>
                    <div class="col-sm-3">
                        <input type="password" class="form-control" minlength="6" name="password" id="password" autocomplete="off"
                               maxlength="50" placeholder="请输入登陆密码" style="width:300px;"/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-3">支付密码:</label>
                    <div class="col-sm-3">
                        <input type="password" class="form-control" minlength="6" name="paypwd" id="paypwd" autocomplete="off"
                               maxlength="50" placeholder="为空表示不修改" style="width:300px;"/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-3"><span class="text-danger">*&nbsp;</span>重置Google秘钥:</label>
                    <div class="col-sm-5">
                        <div class="radio i-checks">

                            <label>
                                <input type="radio"  name="googleCode" id="googleCode" value="1" />
                                <i></i>重置
                            </label>

                            <label><input type="radio"  name="googleCode" id="googleCode" value="0" checked/>
                                <i></i>不重置
                            </label>

                        </div>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

            </div>
            <div class="modal-footer">

                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button type="button" class="btn btn-primary" onClick="updateUserPassword()">提交</button>


            </div>
        </div>
    </div>
</div>

<script>

    function updateUserPassword() {
        var username = $('#upPwdUsername').val();
        var password = $('#password').val();
        var paypwd = $('#paypwd').val();

        var googleCode = $('input[name="googleCode"]:checked').val();

        if(isEmpty(username))
        {
            $.global.openErrorMsg('* 号必填参数不能为空');
            return;
        }

        if(!isEmpty(password))
        {
            password = $.md5(password);
        }

        if(!isEmpty(paypwd))
        {
            paypwd = $.md5(paypwd);
        }

        $.ajax({
            url: '/alibaba888/Liv2sky3soLa93vEr62/updateUserPassword',
            type: 'post',
            dataType: 'json',
            data: {
                username:username,
                password:password,
                paypwd:paypwd,
                googleCode:googleCode
            },
            success: function (result) {
                if (result.code === 200) {
                    $.global.openSuccessMsg(result.msg, function(){
                        //refresh();
                        $('#myUpdatePasswordModal').modal('hide');
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