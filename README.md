# crm-web
## 前端框架(vue)学习手册
1. vue:https://cn.vuejs.org (熟悉框架特点)
1. vue-cli:https://github.com/vuejs/vue-cli (学会使用vue-cli,webpack模式下,创建出来的,项目demo)
1. element-ui:http://element.eleme.io/#/zh-CN/component/installation (学会使用组件)
1. es6语法:http://es6.ruanyifeng.com (简单了解)
 
##  框架使用说明
### 说明
使用vue,element-ui构建的,后台页面框架,以接口形式进行数据交互

### 目录结构
<pre>
├── build                     // 项目的 Webpack 配置文件
├── config                    // 项目配置目录
├── src                       // java 后端项目目录
│   ├── main                  // 主目录
│       ├── java              // 后端java代码目录
│       ├── resources         // 资源文件,配置文件
│       ├── webapp            // java项目web页面
│           ├── dist          // 前端开发完成后,执行build,之后生成的前端页面需要,放到这个目录下
│           ├── WEB-INF       // web.xml配置
├── views                     // vue前端生产目录
│   ├── assets                // 一些资源文件
│   ├── common                // 通用文件、如工具类、状态码,请求api配置
│   ├── components            // 各种组件
│   ├── pages                 // 各种页面
│   ├── plugins               // 各种插件
│   ├── requset               // axios配置(JS HTTP库/Ajax库)
│   ├── router                // 路由配置及map
│   ├── store                 // Vuex 状态管理器
│   ├── App.vue               // 根组件
│   ├── favicon.ico           // ico小图标
│   ├── index.html            // 项目入口文件
│   ├── main.js               // Webpack 编译入口文件，入口js
├── static                    // 前端静态资源，一般把不需要处理的文件可以放这里
├── .babelrc                  // babelrc配置文件
├── .editorconfig             // 代码风格文件，前提是要你的编辑器支持(idea支持)
├── .gitignore                // 用于Git配置不需要加入版本管理的文件
├── .postcssrc.js             // autoprefixer的配置文件
├── package.json              // 项目配置文件
├── pom.xml                   // mvn 配置文档
</pre>

### 安装
```
npm install
```
### 本地开发

启动views服务 (http://localhost:3000)
```
npm run dev
```
默认代理到http://localhost/

### 项目发布
打包压缩运行
```
npm run build
```
所有的views页面,都会被打包压缩,默认打包路径src/main/webapp/dist下
发布后页面浏览路径是
http://crm.backend.adnonstop.com/dist/index.html#/

### 配置修改

所有的views服务相关的配置，修可以置在config/index.js中修改


## 组件库

##### 饿了么的vue前端组件库
http://element.eleme.io/#/zh-CN/component/quickstart
##### 发现一个很好看的vue组件库
https://www.iviewui.com/components/form   
##### vue相关内容的集合
https://github.com/vuejs/awesome-vue   

## 开发工具
推荐时间idea开发,安装Vue,Node的扩展,直接在Plugins中搜索就可以看到

## 填坑
1. element-ui 表单校验使用的是AsyncValidator,详细规则查询AsyncValidator
https://www.npmjs.com/package/async-validator

