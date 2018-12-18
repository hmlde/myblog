/**
 * 公共js
 **/
$(function(){
    showSlogan();

})

//每日一句
function showSlogan(){
    $.get("/todaySlogan",function(data){
         var ymd=data.ymd;
         var title=ymd.substring(0,4)+'年'+ymd.substring(4,6)+"月"+ymd.substring(6,8)+"日   "+data.weekDay;
        $("#s_ymd_week").html(title);
        $("#slogan").html(data.slogan);
    },"json")
}