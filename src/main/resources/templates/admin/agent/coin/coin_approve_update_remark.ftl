
<div class="modal fade" id="myUpdateRemarkModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="myModalLabel"></h4>
            </div>
            <div class="modal-body form-horizontal" >

                <input type="hidden" name="myUpdateRemarkId" id="myUpdateRemarkId" value=""/>

                <div class="form-group">
                    <label class="control-label col-sm-3"><span class="text-danger">*&nbsp;</span>备注:</label>
                    <div class="col-sm-5">
                        <input class="form-control" name="myUpdateRemarkUsername"
                               placeholder="最多100个字符"
                               id="myUpdateRemarkUsername"  autocomplete="off" maxlength="100"  style="width:300px;"/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>


                <div class="hr-line-dashed"></div>



            </div>
            <div class="modal-footer">

                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button type="button" class="btn btn-primary" onClick="updateRemark()">提交</button>


            </div>
        </div>
    </div>
</div>

<script>

    function updateRemark() {
        var id = $('#myUpdateRemarkId').val();
        var remark = $('#myUpdateRemarkUsername').val();

        if(isEmpty(id))
        {
            $.global.openErrorMsg('* 号必填参数不能为空');
            return;
        }

        $.ajax({
            url: '/alibaba888/agent/updateCoinApproveRemark',
            type: 'post',
            dataType: 'json',
            data: {
                id:id,
                remark:remark,
            },
            success: function (result) {
                if (result.code === 200) {
                    $.global.openSuccessMsg(result.msg, function(){
                        //refresh();
                        $('#myUpdateRemarkModal').modal('hide');
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