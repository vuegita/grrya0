<div class="modal fade" id="myModalPassword" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <input type="hidden" name="passwordType" class="form-control" id="passwordType"  value="login">
    <div class="modal-dialog"  role="document">
        <div class="modal-content" >
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="myModalLabel">修改密码</h4>
            </div>
            <div class="modal-body"  >

                <form id="form" class="form-horizontal" autocomplete="off">
                <div class="form-group">
                    <label class="control-label col-sm-3"><span class="text-danger">*&nbsp;</span>旧密码:</label>
                    <div class="col-sm-6">
                        <input class="form-control" name="oldPassword" id="oldPassword" autocomplete="off" required
                               type="password" placeholder="请输入旧密码"  minlength="6" maxlength="32"
                               value="" />
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-3"><span class="text-danger">*&nbsp;</span>新密码:</label>
                    <div class="col-sm-6">
                        <input class="form-control" name="newPassword" id="newPassword" autocomplete="off" required
                               type="password" placeholder="请输入新密码" minlength="6" maxlength="32"
                               value="" />
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-3"><span class="text-danger">*&nbsp;</span>确认密码:</label>
                    <div class="col-sm-6">
                        <input class="form-control" name="confirmPassword" id="confirmPassword" autocomplete="off" required
                               type="password" placeholder="请输入确认密码" minlength="6" maxlength="32"
                               value="" />
                    </div>
                </div>
                <div class="hr-line-dashed"></div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal"><span
                            class="glyphicon glyphicon-confirm" aria-hidden="true"></span>取消
                </button>
                <button type="button" id="btn_submit" class="btn btn-primary" data-dismiss="modal">
                    <span class="glyphicon glyphicon-floppy-disk" aria-hidden="true"></span>确认
                </button>
            </div>
        </div>
    </div>
</div>