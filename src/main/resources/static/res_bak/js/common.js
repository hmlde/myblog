/**
 * 公共js
 * @returns
 */
$(function(){
	   $('.user').mouseover(function(){
		   $(this).addClass("open");
	   });
	   
	   $('.user').mouseout(function(){
		   $(this).removeClass("open");
	   });
	   $('.notification').mouseover(function(){
		   $(this).addClass("open");
	   });
	   
	   $('.notification').mouseout(function(){
		   $(this).removeClass("open");
	   });
});
//工具类
var doAjax=function(config){
	var op={
	   	type:"POST",
	   	dataType:"json"
	};
	op=$.extend(op,config);
	$.ajax($.extend(op,{
		   success: function(result){
		       if(result.code==='0000'){
		    	   if(config.success){
		    		   config.success(result.data);
		    	   }else{
		    		   toastr.success(result.msg)
		    	   }
		       }else{
		    	   alert("调用异常");
		       }
		   },
		   error:function(){
			   
		  }
   }));
}