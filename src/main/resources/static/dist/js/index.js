var svg_id=1;
// document.onkeydown = function(e) {
//         var theEvent = window.event || e;
//         var code = theEvent.keyCode || theEvent.which;
//         if (code == 13) {
//             $("#search").click();
//         }
//     }  


$(document).ready(function(){
  // search()
  $('#name1').typeahead({
    source: namelist,
    items:15
    })
  $('#name2').typeahead({
    source: namelist,
    items:15
    })
});

function search(){
    console.log($("#name1").val())
    // if ($('#name1').val()=='') { console.log(($.inArray($('#name2').val(),namelist)==-1))}
    if ( ($.inArray($('#name2').val(),namelist)!=-1 && ($('#name1').val()==''||$('#name1').val()=='*') )||($.inArray($('#name1').val(),namelist)!=-1 &&($('#name2').val()=='' ||$('#name2').val()=='*') )) {
        if($('#name1').val()==''){ $('#name1').val($('#name2').val()) ; $('#name2').val('*') }
        if($('#name2').val()==''){  $('#name2').val('*') }
        if ($("#algorithm").val()!='个人关系网') {
            $('#tishi').text('空的值只能够使用算法："个人关系网"')
            $('#myModal').modal('toggle')
            return
        }
    }
    else{
        if ($.inArray($('#name1').val(),namelist)==-1 && $.inArray($('#name2').val(),namelist)==-1 ) {
            $('#tishi').text("您输入的第 1 个明星和第 2 个明星未被我们收录！")
            $('#myModal').modal('toggle')
            return
        }
        if ($('#name1')==$('#name2').val() ) {
            $('#tishi').text("您输入的两个明星重复！")
            $('#myModal').modal('toggle')
            return
        }
        if ($.inArray($('#name1').val(),namelist)==-1) {
            $('#tishi').text("您输入的第 1 个明星未被我们收录！")
            $('#myModal').modal('toggle')
            return
        }
        if ($.inArray($('#name2').val(),namelist)==-1) {
            $('#tishi').text("您输入的第 2 个明星未被我们收录！")
            $('#myModal').modal('toggle')
            return
        }
        if ($("#algorithm").val()=='个人关系网') {
            $('#tishi').text('"个人关系网"算法 输入值必须有一个位空')
            $('#myModal').modal('toggle')
            return
        }
    }
   
    $.ajax({
      url: '/api/getRelation',
      // url:'relation.json',
      type: 'get',
      timeout:10000,
      dataType: 'json',
      data: {temp: new Date().toString(),name1:$('#name1').val(),name2:$('#name2').val(),algorithm:$("#algorithm").val(),ip:cip,city:cname}
    })
    .done(function(data) {
      // console.log(data)
      draw(data);
    })
    .fail(function() {
      console.log("error");
    })
    .always(function() {
      console.log("complete");
    });
}

function draw(root){
        svg_id=svg_id+1;
        // 追加新的svg
        $('#svgzhuijia').after('<div class="box box-success"><div class="box-header ui-sortable-handle" style="cursor: move;"> <h3 class="box-title"> <span style="color: #00c0ef">'+$('#name1').val()+'</span>和<span style="color: #00c0ef">'+$('#name2').val()+'</span>关系图</h3><div class="pull-right box-tools"><button type="button" class="btn btn-success btn-sm" data-widget="collapse"><i class="fa fa-minus"></i> </button>  <button type="button" class="btn btn-success btn-sm" data-widget="remove"><i class="fa fa-times"></i></button> </div></div><div class="box-body" id="mygraph'+svg_id+'" style="height:400px"><div class="box-footer clearfix no-border"></div></div><div class="box-footer clearfix no-border"></div>')



        // var width = 600;
        // var height = 600;
        var width = $('#mygraph'+svg_id).width();
        var height = $('#mygraph'+svg_id).height();
        var img_w = 60;
        var img_h = 60;
        
        var svg = d3.select("#mygraph"+svg_id).append("svg")
                                .attr("id","svg"+svg_id)
                                .attr("width",width)
                                .attr("height",height);
        console.log(root);
        
        var force = d3.layout.force()
                        .nodes(root.nodes)
                        .links(root.edges)
                        .size([width,height])
                        .linkDistance(200)
                        .charge(-2000)
                        .start();
                        
        var edges_line = svg.selectAll("#svg"+svg_id+" line")
                            .data(root.edges)
                            .enter()
                            .append("line")
                            .style("stroke","#ccc")
                            .style("stroke-width",5);
                            
        var edges_text = svg.selectAll("#svg"+svg_id+" .linetext")
                            .data(root.edges)
                            .enter()
                            .append("text")
                            .attr("class","linetext")
                            .text(function(d){
                                return d.relation;
                            });
        
                            
        var nodes_img = svg.selectAll("#svg"+svg_id+" image")
                            .data(root.nodes)
                            .enter()
                            .append("image")
                            .attr("width",img_w)
                            .attr("height",img_h)
                            .attr("xlink:href",function(d){
                                // return d.image;
                                // console.log(d)
                                return "dist/img/"+d.name+"_"+d.id+".jpg"
                            })
                            // .on("mouseover",function(d,i){
                            //     //显示连接线上的文字
                            //     edges_text.style("fill-opacity",function(edge){
                            //         if( edge.source === d || edge.target === d ){
                            //             return 1.0;
                            //         }
                            //     });
                            // })
                            // .on("mouseout",function(d,i){
                            //     //隐去连接线上的文字
                            //     edges_text.style("fill-opacity",function(edge){
                            //         if( edge.source === d || edge.target === d ){
                            //             return 0.0;
                            //         }
                            //     });
                            // })
                            .call(force.drag);
        
        var text_dx = -20;
        var text_dy = 15;
        
        var nodes_text = svg.selectAll("#svg"+svg_id+" .nodetext")
                            .data(root.nodes)
                            .enter()
                            .append("text")
                            .attr("class","nodetext")
                            .attr("dx",text_dx)
                            .attr("dy",text_dy)
                            .text(function(d){
                                return d.name;
                            });
        
                            
        force.on("tick", function(){
            
            //限制结点的边界
            root.nodes.forEach(function(d,i){
                d.x = d.x - img_w/2 < 0     ? img_w/2 : d.x ;
                d.x = d.x + img_w/2 > width ? width - img_w/2 : d.x ;
                d.y = d.y - img_h/2 < 0      ? img_h/2 : d.y ;
                d.y = d.y + img_h/2 + text_dy > height ? height - img_h/2 - text_dy : d.y ;
            });
        
            //更新连接线的位置
             edges_line.attr("x1",function(d){ return d.source.x; });
             edges_line.attr("y1",function(d){ return d.source.y; });
             edges_line.attr("x2",function(d){ return d.target.x; });
             edges_line.attr("y2",function(d){ return d.target.y; });
             
             //更新连接线上文字的位置
             edges_text.attr("x",function(d){ return (d.source.x + d.target.x) / 2 ; });
             edges_text.attr("y",function(d){ return (d.source.y + d.target.y) / 2 ; });
             
             
             //更新结点图片和文字
             nodes_img.attr("x",function(d){ return d.x - img_w/2; });
             nodes_img.attr("y",function(d){ return d.y - img_h/2; });
             
             nodes_text.attr("x",function(d){ return d.x });
             nodes_text.attr("y",function(d){ return d.y + img_w/2; });
        });
}