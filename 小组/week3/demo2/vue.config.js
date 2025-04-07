const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  publicPath: process.env.NODE_ENV === 'production'? '/servlet/' : '/',
  devServer:{
    port:7070,
    proxy:{

      '/ws': {
        target: 'http://10.41.26.125:7071',
        ws: true, // 启用 WebSocket 代理
        changeOrigin: true
      },
      '/api':{
        target:'http://loalhost:8080',
        changeOrigin:true
      }
    }
  }
})
