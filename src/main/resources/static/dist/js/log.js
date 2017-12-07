 var app = angular.module('myApp', []);

// var tabledata
// var table = $('#table');


app.controller('menu', function($scope, $http) {
    console.log(11111)
    $.ajax({
            url: '/log/getCount',
            type: 'get',
            dataType: 'json',
            cache:false,
            data: {}
            ,contentType:"application/x-www-form-urlencoded; charset=UTF-8" 
        })
        .done(function(data) {
            $scope.avgUsetime=String(data.avgUsetime).substring(0,6)+"ms"
            $scope.people=data.people
            $scope.total=data.total
            $scope.Top=data.Top
            $scope.PersonTop=data.PersonTop
            console.log($scope.Top)
            $scope.$apply();
        })

           
   function initTable(){
            $('#test-table').bootstrapTable({
                method: 'get',
                toolbar: '#toolbar',    //工具按钮用哪个容器
                striped: true,      //是否显示行间隔色
                striped: true,  //表格显示条纹  
                cache: false,      //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
                pagination: true,     //是否显示分页（*）
                // sortName:"q",
                sortable: true,      //是否启用排序
                sortOrder: "asc",     //排序方式
                pageNumber:1,      //初始化加载第一页，默认第一页
                pageSize: 10,      //每页的记录行数（*）
                pageList: [10, 25, 50, 100],  //可供选择的每页的行数（*）
                url: "/log/getHistoryList",//这个接口需要处理bootstrap table传递的固定参数
                contentType:"application/x-www-form-urlencoded; charset=UTF-8" ,
                queryParamsType:'', //默认值为 'limit' ,在默认情况下 传给服务端的参数为：offset,limit,sort
                                    // 设置为 ''  在这种情况下传给服务器的参数为：pageSize,pageNumber
                //queryParams: queryParams,//前端调用服务时，会默认传递上边提到的参数，如果需要添加自定义参数，可以自定义一个函数返回请求参数
                sidePagination: "server",   //分页方式：client客户端分页，server服务端分页（*）
                // search: true,      //是否显示表格搜索，此搜索是客户端搜索，不会进服务端，所以，个人感觉意义不大
                strictSearch: true,
                showColumns: true,     //是否显示所有的列
                showRefresh: true,     //是否显示刷新按钮
                minimumCountColumns: 2,    //最少允许的列数
                clickToSelect: true,    //是否启用点击选中行
                searchOnEnterKey: true,
                paginationLoop: false,   //设置为 true 启用分页条无限循环的功能。
                showToggle: true,   //名片格式
                onLoadSuccess: function(){  //加载成功时执行  
                  console.log("加载成功");  
                }, 
                rowStyle:function(row,index) {

                            // var classes = ['active', 'success', 'info', 'warning', 'danger'];
                            var classes = ['active', 'info'];
                            return {
                                    classes: classes[index %2]
                                };
                      }, 
                columns: [{
                    title: '搜索关键字',
                    align: 'center',
                    formatter:function(value,row,index){
                        //通过formatter可以自定义列显示的内容
                        //value：当前field的值，即id
                        //row：当前行的数据
                        return row.name1+'<->'+row.name2;
                    } 
                }, {
                    field: 'algorithm',
                    title: '算法',
                    align: 'center'
                }
                , {
                    field: 'time',
                    title: '搜索时间',
                    align: 'center'
                }, {
                    field: 'ip',
                    title: 'ip',
                    align: 'center'
                }, {
                    field: 'city',
                    title: 'ip所在地',
                    align: 'center'
                },
                {
                    field: 'usetime',
                    title: '查询用时',
                    align: 'center',
                    formatter:function(value,row,index){
                        //通过formatter可以自定义列显示的内容
                        //value：当前field的值，即id
                        //row：当前行的数据
                        return value+"ms";
                    } 
                }
                // {
                //     field: '用时',
                //     title: '操作',
                //     align: 'center',
                //     formatter:function(value,row,index){
                //         //通过formatter可以自定义列显示的内容
                //         //value：当前field的值，即id
                //         //row：当前行的数据
                //         return '<a href="" >测试</a>';
                //     } 
                // }
                ],
                pagination:true
            });
        }
    initTable()
});