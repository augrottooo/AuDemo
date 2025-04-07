//引入
import axios from 'axios';

//配置信息
let config = {
    baseURL: "http://localhost:8080/servlet/", // 后端服务器地址
    //请求时间
    timeout: 3000,
    //每次请求携带cookie
    withCredentials: true 
}

//创建实例
const instance = axios.create(config)

//导出
export default instance