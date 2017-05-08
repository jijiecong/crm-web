/**
 * Created by woqumai on 16/5/12.
 */
seajs.use(["$", "common", "ubase", "ueditor", "dialog"], function ($, C, ubase, Ueditor, dialog) {

     editor=window.editor=ubase.setueditor("#editor",{
        toolbars: ['undo', 'redo', /*'fontsize',*/ 'bold', 'underline', 'italic', 'forecolor', 'justifyleft', 'justifycenter', 'justifyright'],
        initialStyle:'p{line-height:1em; font-size: 12px; }',
         initialFrameHeight:260,

        autoHeight:true,
        uploadimage: true,
        link:true,
        cc_img_div_css:"image-package",
        check_img: function(file, callback){
            file.size/1024/1024 > 4 ? dialog.alert("单张图片请大小请保持在4M以内的!", 1) : callback();
        },
        image:{
            //上传图片的url
            url: "/pic/missionDetailPic",

            //上传图片的时候提交的参数
            data: {
                req: "",
            },
            //传图片的最大数量
            limit: 9,
            //是否开启图片多选
            multiple: false,
            //选择图片以后是否自动上传 true / false
            auto_upload: true,
            fileVal: 'file',
            pasteplain: true,
            retainOnlyLabelPasted: true
        },


    });

    //加载保存的内容  linyf(移动文件位置)
    setTimeout(function(){
        var threadMeta = window.localStorage.getItem(TM_KEY, JSON.stringify(threadMeta))
        threadMeta = JSON.parse(threadMeta)
        $('select[name=quan_id]').val(threadMeta.quan)
        $('input[name=title]').val(threadMeta.title)
        window.editor.editor.setContent(threadMeta.content)
    },1000);
});

$("#submitBtn").on("click", function(){

    if(!confirm('确认发布帖子？')){
        return false;
    }

    var FlickrImg = $("#Flickr li img");
    var Flickr   = $("#Flickr");

    var _i = 0;
    var elemt = "";
    for(_i = 0; _i < FlickrImg.length; _i++){
        elemt = '<input type="hidden" name="imgs[]" value="'+FlickrImg[_i].src +'">';
        Flickr.append(elemt);
    }

    var content  = window.editor.editor.getContent();
    $('#editor').val(content);

    var postData = $("#postForm").serialize();

    $.ajax({
        type: "POST",
        url: "index.php?r=thread/todoCreate",
        data: postData,
        success: function(response){
            if(response.code==200){
                if(response.data.status.code==0){
                    alert("操作成功");
                    clearPageMeta()
                    //window.location.reload();
        }else{
            alert(response.data.status.msg);
}

}else{
        alert( "操作失败" );
    }
        }
    });
    console.log(postData);
})



var TM_KEY = 'thread.metadata';

//设置帖子的 localStorage
function setThreadMeta(quan, title, content){
    var threadMeta = {
        quan: $('select[name=quan_id]').val(),
        title: $('input[name=title]').val(),
        content: window.editor.editor.getContent()
    }

    window.localStorage.setItem(TM_KEY, JSON.stringify(threadMeta))
}

//清空页面及localStorage
function clearPageMeta(){
    $('select[name=quan_id]').val(0)
    $('input[name=title]').val('')
    window.editor.editor.setContent('')
    setThreadMeta(0, '', '')
}

//保存帖子
$('#localSave').on('click', function(){
    var quan = $('select[name=quan_id]').val()
    var title = $('input[name=title]').val()
    var content = window.editor.editor.getContent()
    setThreadMeta(quan, title, content)
    alert('保存成功\n\r仅在当前客户端有效!')
})

//清空内容
$('#clearMeta').on('click', function(){
    if(confirm('确定清空所有内容？\n\r所有输入和保存的内容都将不可恢复')){
        clearPageMeta();
    }
})

//去除标签
function getContent(html){
    return html;
}

