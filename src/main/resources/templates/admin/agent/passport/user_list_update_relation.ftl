
<div class="modal fade" id="myUpdateRelationModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="myModalLabel">设置上级</h4>
            </div>
            <div class="modal-body form-horizontal" >


                <div class="form-group">
                    <label class="control-label col-sm-3"><span class="text-danger">*&nbsp;</span>上级员工名:</label>
                    <div class="col-sm-3">
                        <input class="form-control" name="parentUsername" id="parentUsername" autocomplete="off" maxlength="100" placeholder="请输入父级员工名"  style="width:300px;"/>
                    </div>
                </div>

                <div class="form-group">
                    <label class="control-label" style="color: red;margin-left: 100px">父级用户名不为空，表示当前子级用户的上级!</label><br>
                </div>

                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-3"><span class="text-danger">*&nbsp;</span>子级用户名:</label>
                    <div class="col-sm-3">
                        <input class="form-control" name="childUsername" id="childUsername" readonly autocomplete="off" maxlength="100" style="width:300px;" />
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

            </div>
            <div class="modal-footer">

                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button type="button" class="btn btn-primary" onClick="updateUserRelation()">提交</button>


            </div>
        </div>
    </div>
</div>

<script>

    function updateUserRelation() {
        var parentUsername = $('#parentUsername').val();
        var childUsername = $('#childUsername').val();

        if(isEmpty(parentUsername) || isEmpty(parentUsername))
        {
            $.global.openErrorMsg('* 号必填参数不能为空');
            return;
        }

        $.ajax({
            url: '/alibaba888/agent/passport/updateUserRelation',
            type: 'post',
            dataType: 'json',
            data: {
                parentUsername:parentUsername,
                childUsername:childUsername
            },
            success: function (result) {
                if (result.code === 200) {
                    $.global.openSuccessMsg(result.msg, function(){
                        //refresh();
                        $('#myUpdateRelationModal').modal('hide');
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

    function loadParentRelation(childUsername) {

        if(isEmpty(childUsername))
        {
            $.global.openErrorMsg('* 号必填参数不能为空');
            return;
        }

        $.ajax({
            url: '/alibaba888/agent/passport/getParentUserInfo',
            type: 'post',
            dataType: 'json',
            data: {
                childUsername:childUsername
            },
            success: function (result) {
                //console.log(result);
                if (result.code === 200) {
                    var data = result.data;
                    if(data && !isEmpty(data.directStaffname))
                    {
                        // 直属员工上级
                        $('#parentUsername').prop("value", data.directStaffname);
                    }

                    $('#myUpdateRelationModal').modal();
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