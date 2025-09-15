
    var gv = gv || {};
    gv.getColor = function(userType,value){
        var result="";
        if(userType == 1){
            if (value == null){
                value = '会员'
            }
            result = '<span"><i class="glyphicon " aria-hidden="true" ></i>&nbsp;'+value+'</span>';
        }else if(userType ==2){
            if (value == null){
                value = '员工'
            }
            result = '<span style="color: red"><i class="glyphicon " aria-hidden="true" ></i>&nbsp;'+value+'</span>';
        }else if(userType == 3){
            if (value == null){
                value = '代理'
            }
            result = '<span style="color: blue"><i class="glyphicon " aria-hidden="true" ></i>&nbsp;'+value+'</span>';
        }else if(userType == 4){
            if (value == null){
                value = '测试'
            }
            result = '<span style="color: red"><i class="glyphicon " aria-hidden="true" ></i>&nbsp;'+value+'</span>';
        }
        return result;
    };


