
<div class="modal fade" id="myUpdateModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="myModalLabel">编辑信息</h4>
            </div>
            <div class="modal-body form-horizontal" >


                <div class="form-group">
                    <label class="control-label col-sm-3"><span class="text-danger">*&nbsp;</span>用户名:</label>
                    <div class="col-sm-3">
                        <input class="form-control" name="editUsername" id="editUsername" readonly autocomplete="off" maxlength="100"  style="width:300px;"/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-3"><span class="text-danger">*&nbsp;</span>等级:</label>
                    <div class="col-sm-5">
                        <label><input type="radio" value="high" name="editLevel" id="editLevel" /><i></i>高级</label>
                        <label><input type="radio" value="middle" name="editLevel" id="editLevel" /><i></i>中级</label>
                        <label><input type="radio" value="normal" name="editLevel" id="editLevel" /><i></i>正常</label>
                        <label><input type="radio" value="bad" name="editLevel" id="editLevel" /><i></i>劣质</label>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-3">备注:</label>
                    <div class="col-sm-3">
                        <input type="text" class="form-control" minlength="6" name="editRemark" id="editRemark" autocomplete="off"
                               maxlength="50" placeholder="最长50个字符" style="width:300px;"/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

            </div>
            <div class="modal-footer">

                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button type="button" class="btn btn-primary" onClick="updateInfo()">提交</button>


            </div>
        </div>
    </div>
</div>

<script>

    function showEditModal(username, level, remark) {
        console.log("level = " + level);
        $('#editUsername').prop('value', username);
        // $("input[name=editLevel][value=" + level + "]").prop("checked", true);
        // $("input[name='editLevel'][value=" + level +"]").attr("checked",true);
        $("input[name='editLevel'][value='"+level+"']").attr("checked",true);
        // $('#editLevel').prop('checked', true);
        $('#editRemark').prop('value', remark);
        $('#myUpdateModal').modal();
    }

    function updateInfo(item) {
        var username = $('#editUsername').val();
        var level = $('input[name=editLevel]:checked').val();
        var remark = $('#editRemark').val();

        if(isEmpty(username) || isEmpty(level))
        {
            $.global.openErrorMsg('* 号必填参数不能为空');
            return;
        }


        $.ajax({
            url: '/alibaba888/Liv2sky3soLa93vEr62/updateUserAttrLevelAndRemark',
            type: 'post',
            dataType: 'json',
            data: {
                username:username,
                level:level,
                remark:remark
            },
            success: function (result) {
                if (result.code === 200) {
                    $.global.openSuccessMsg("更新成功, 请手动刷新页面!", function(){
                        //refresh();
                        $('#myUpdateModal').modal('hide');
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