const express = require('express');
const bodyParser = require('body-parser');

// 创建 Express 应用实例
const app = express();
// 定义服务器端口
const port = 8080;

// 使用 body-parser 中间件解析 JSON 数据
app.use(bodyParser.json());

// 模拟用户数据库，这里使用一个数组来存储用户信息
const users = [];

// 登录 API
app.post('/login', (req, res) => {
    // 从请求体中获取邮箱和密码
    const { email, password } = req.body;
    // 在模拟数据库中查找匹配的用户
    const user = users.find(u => u.email === email && u.password === password);
    if (user) {
        // 如果找到匹配用户，返回登录成功的响应
        res.json({ success: true, message: '登录成功' });
    } else {
        // 如果未找到匹配用户，返回登录失败的响应
        res.json({ success: false, message: '邮箱或密码错误' });
    }
});

// 注册 API
app.post('/register', (req, res) => {
    // 从请求体中获取邮箱、手机号和密码
    const { email, phone, password } = req.body;
    // 检查邮箱是否已被注册
    const existingUser = users.find(u => u.email === email);
    if (existingUser) {
        // 如果邮箱已被注册，返回注册失败的响应
        res.json({ success: false, message: '该邮箱已被注册' });
    } else {
        // 如果邮箱未被注册，将新用户信息添加到模拟数据库中
        users.push({ email, phone, password });
        // 返回注册成功的响应
        res.json({ success: true, message: '注册成功' });
    }
});

// 启动服务器
app.listen(port, () => {
    console.log(`服务器运行在 http://localhost:${port}`);
});