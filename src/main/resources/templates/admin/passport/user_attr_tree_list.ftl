<!DOCTYPE HTML>
<html>
<head>
    <#include "../../common/head-meta.ftl"/>
    <title>${projectName}后台管理系统-订单管理</title>
    <link type="text/css" rel="stylesheet" href="${STATIC_URL}/plugins/bootstrap-table/bootstrap-table.min.css"/>
    <meta charset="UTF-8">
    <!-- import CSS -->
    <link rel="stylesheet" href="https://unpkg.com/element-ui/lib/theme-chalk/index.css">
</head>
<body>
<div id="app">
    <div>----------------------关系树---------------------------------</div>
<#--    <el-button @click="visible = true">Button</el-button>-->
    <el-dialog :visible.sync="visible" title="Hello world">
        <p>Try Element</p>
    </el-dialog>
    <el-tree
            :data="data"
            :props="defaultProps"
            accordion
            @node-click="handleNodeClick">
    </el-tree>



</div>
</body>
<!-- import Vue before Element -->
<script src="https://unpkg.com/vue/dist/vue.js"></script>
<!-- import JavaScript -->
<script src="https://unpkg.com/element-ui/lib/index.js"></script>
<script>
    new Vue({
        el: '#app',
        data: function() {
            return {
                selectName:"",
                visible: false,
                recordData:"",
                data: [{
                    label: '一级手机号123123123',
                    children: [{
                        label: '二级 手机号123123123',
                        // children: [{
                        //     label: '三级 手机号123123123'
                        // }]
                    }]
                }, {
                    label: '一级 2',
                    children: [{
                        label: '二级 2-1',
                        children: [{
                            label: '三级 2-1-1'
                        }]
                    }, {
                        label: '二级 2-2',
                        children: [{
                            label: '三级 2-2-1'
                        }]
                    }]
                }, {
                    label: '一级 3',
                    children: [{
                        label: '二级 3-1',
                        children: [{
                            label: '三级 3-1-1'
                        }]
                    }, {
                        label: '二级 3-2',
                        children: [{
                            label: '三级 3-2-1'
                        }]
                    }]
                }],
                defaultProps: {
                    children: 'children',
                    label: 'label'
                }
            };
        },
        created() {
            this.startData();
        },
        methods: {
            startData(){
                var myThis=this
                $.ajax({
                    url: '/alibaba888/Liv2sky3soLa93vEr62/getUserList',
                    type: 'post',
                    dataType: 'json',
                    data: {
                        type:"staff",
                        offset:0,
                        limit:1000,
                    },
                    success: function (result) {
                        if (result.code === 200) {
                            console.log(result);
                         let list= result.data.list
                            for(let i=0;i<list.length;i++){
                                list[i].label=list[i].name
                                list[i].type=1
                                list[i].children=[{label: ''}]
                            }
                            myThis.data=list;
                            return;
                        }
                        $.global.openErrorMsg(result.msg);
                    },
                    error: function () {
                        $.global.openErrorMsgCollback('系统异常,操作失败!', function () {

                        });
                    }
                });
            },
            recursion (node, arr) {
                debugger

                if( node.label== this.selectName){
                    node.children=arr
                }else{
                    if ((!node.children) || node.children=="") {
                        //先判断 node节点是否含有children节点，为true 为最后一级，取id ，push到arr数组中
                        // arr.push(node.id)
                        node.children=""

                    } else {
                        // false时 非最后一级，含有children，则继续循环children
                        node.children.forEach(item => this.recursion(item, arr))
                    }
                }


            },

            // recursion (recordlist, restlist) {
            //     var obj={};
            //     for(var i=recordlist.length;i>0;i--){
            //         var obj2={};
            //         if(i==recordlist.length){
            //             obj2.label=recordlist[i].label
            //             obj2.children=restlist
            //         }
            //
            //     }
            //
            // },


            handleNodeClick(data) {
                this.selectName=data.label;
                if(data.type==1){
                    this.recordData= data.label;
                }
                // for(let i=0;i<datalist.length;i++){
                //     if(statrList[j].label==name){
                //         this.recordData[0]= statrList[j];
                //     }
                // }
debugger
                var datalist=this.data;
                var myThis=this;
                var name= data.label;
                var data=data;


               if(data.type==1){
                   $.ajax({
                       url: '/alibaba888/Liv2sky3soLa93vEr62/getUserAttrList',
                       type: 'post',
                       dataType: 'json',
                       data: {
                           staffname:name,
                           offset:0,
                           limit:1000,
                       },
                       success: function (result) {

                           if (result.code === 200) {
                               debugger
                               console.log(result);
                               let list= result.data.list
                               let list2=[]
                               for(let i=0;i<list.length;i++){
                                   list[i].label=list[i].username
                                   list[i].type=2
                                   list[i].children=[{label: ''}]
                                   if(list[i].parentid==0 && list[i].grantfatherid==0){
                                       list2.push(list[i])
                                   }

                               }


                               let statrList=datalist;
                               for(let j=0;j<statrList.length;j++){
                                   if( statrList[j].label==name){
                                       statrList[j].children=list2

                                       myThis.recordData[0]=statrList[j]
                                   }
                               }


                               myThis.data=statrList;

                               return;
                           }
                           $.global.openErrorMsg(result.msg);
                       },
                       error: function () {
                           $.global.openErrorMsgCollback('系统异常,操作失败!', function () {

                           });
                       }
                   });


               }else{
debugger
                   $.ajax({
                       url: '/alibaba888/Liv2sky3soLa93vEr62/getUserAttrList',
                       type: 'post',
                       dataType: 'json',
                       data: {
                           parentname:name,
                           offset:0,
                           limit:10000,
                       },
                       success: function (result) {
                           if (result.code === 200) {
                               console.log(result);
                               let list= result.data.list
                               for(let i=0;i<list.length;i++){
                                   list[i].label=list[i].username
                                   list[i].type=data.type+1
                                   list[i].children=[{label: ''}]
                               }



debugger
                               let statrList=datalist;
                               for(let i=0;i<datalist.length;i++){
                                   if(statrList[i].label== myThis.recordData){
                                       myThis.recursion(statrList[i],list)
                                   }
                               }

                               // debugger
                               // for(let j=0;j<statrList.length;j++){
                               //     if( statrList[j].label==name){
                               //
                               //         statrList[j].children=list
                               //     }
                               // }


                               myThis.data=statrList;

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

                console.log(data);
            }
        }

    })
</script>
</html>
