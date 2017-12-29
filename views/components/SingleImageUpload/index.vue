/**
*
* 上传单个图片组件
*
* @Date: 2017/12/26
* @Author: XiaoLuo
*
*/
<template>
 <div>
   <el-upload
     class="avatar-uploader"
     :action="action"
     :show-file-list="false"
     :http-request="httpRequest"
     :before-upload="beforeAvatarUpload">
     <img v-if="imageUrl" :src="imageUrl" class="avatar">
     <i v-else class="el-icon-plus avatar-uploader-icon"></i>
   </el-upload>
  </div>
</template>
<style lang="scss" type="text/scss" rel="stylesheet/scss">
  .avatar-uploader .el-upload {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
  }
  .avatar-uploader .el-upload:hover {
    border-color: #409EFF;
  }
  .avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 178px;
    height: 178px;
    line-height: 178px;
    text-align: center;
  }
  .avatar {
    width: 178px;
    height: 178px;
    display: block;
  }
</style>

<script>
  export default {
    props: {
      action: {
        type: String,
        require: true
      },
      autoUpload: {
        type: Boolean,
        require: true
      },
      multiple: {
        type: Boolean,
        require: false
      },
      imageUrlInit: {
        type: String,
        require: true
      }
    },
    data() {
      return {
        imageUrl:''
      };
    },
    created(){
      this.initData()
    },
    watch: {
      imageUrlInit(val) {
        this.initData()
      },
    },
    methods: {
      initData() {
        this.imageUrl = this.imageUrlInit
      },
      beforeAvatarUpload(file) {
        //image/jpg, image/jpeg, image/png'
        const isJPG = file.type === 'image/jpeg';
        const isLt2M = file.size / 1024 / 1024 < 2;
        if (!isJPG) {
          this.$message.error('上传头像图片只能是 jpg 格式!');
        }
        if (!isLt2M) {
          this.$message.error('上传头像图片大小不能超过 2MB!');
        }
        return isJPG && isLt2M;
      },
      async httpRequest(option) {//自定义上传方法
        let imageData = new FormData();
        imageData.append(option.filename, option.file);
        this.axios.post(option.action, imageData)
          .then(({ data: responseData }) => {
            if (responseData !== null) {
              this.imageUrl = responseData;
              this.$emit('uploadResponse', responseData)
            }
          }).catch(() => {
        })
      },
    }
  }
</script>
