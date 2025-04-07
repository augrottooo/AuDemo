<template>
  <!-- 根容器 -->
  <div id="app">
    <div class="all-form">
      <!-- 登录/注册选择区域 -->
      <div class="select-buttons">
        <!-- 登录按钮，点击时设置 showLogin 为 true，并根据 showLogin 状态添加 active 类 -->
        <button v-on:click="showLogin = true" :class="{ active: showLogin }">登录</button>
        <!-- 注册按钮，点击时设置 showLogin 为 false，并根据 showLogin 状态添加 active 类 -->
        <button @click="showLogin = false" :class="{ active: !showLogin }">注册</button>
      </div>

      <!-- 登录表单，当 showLogin 为 true 时显示 -->
      <form v-if="showLogin" @submit.prevent="handleLogin">
        <h1>登录</h1>
        <!-- 邮箱输入框 -->
        <div class="form-group">
          <label for="">用户名(学生为学号，管理员为姓名)<br /></label>
          <!-- 双向绑定到 loginEmail 数据 -->
          <input type="text" v-model="loginUsername" />
        </div>
        <!-- 密码输入框 -->
        <div class="form-group">
          <label for="">密码<br /></label>
          <!-- 双向绑定到 loginPassword 数据 -->
          <input type="password" v-model="loginPassword" />
        </div>
        <!-- 登录提交按钮，点击时触发 handleLogin 方法 -->
        <input type="submit" value="确定" v-on:click="handleLogin" />
      </form>

      <!-- 注册表单，当 showLogin 为 false 时显示 -->
      <form v-else @submit.prevent="handleRegister">
        <h1>注册</h1>
        <!-- 角色输入框 -->
        <div class="form-group">
          <label for="">角色(输入 1 代表学生,2 代表管理员)<br /></label>
          <!-- 双向绑定到 registerEmail 数据 -->
          <input type="text" v-model="registerUsername" />
        </div>
        <!-- 用户名输入框 -->
        <div class="form-group">
          <label for="">用户名(学生为学号，管理员为姓名)<br /></label>
          <!-- 双向绑定到 registerEmail 数据 -->
          <input type="text" v-model="registerUsername" />
        </div>
        <!-- 手机号输入框 -->
        <div class="form-group">
          <label for="">手机号<br /></label>
          <!-- 双向绑定到 registerPhone 数据 -->
          <input type="text" v-model="registerPhone" />
        </div>
        <!-- 密码输入框 -->
        <div class="form-group">
          <label for="">密码<br /></label>
          <!-- 双向绑定到 registerPassword 数据 -->
          <input type="password" v-model="registerPassword" />
        </div>
        <!-- 确认密码输入框 -->
        <div class="form-group">
          <label for="">确认密码<br /></label>
          <!-- 双向绑定到 registerConfirmPassword 数据 -->
          <input type="password" v-model="registerConfirmPassword" />
        </div>
        <!-- 注册提交按钮，点击时触发 handleRegister 方法 -->
        <input type="submit" value="确定" v-on:click="handleRegister" />
      </form>
    </div>
  </div>
</template>

<script>
import axios from './axios/index';

// 定义组件选项对象
const options = {
  // 组件数据
  data() {
    return {
      showLogin: true, // 控制显示登录还是注册表单，默认显示登录
      loginUsername: "",
      loginPassword: "",
      registerUsername: "",
      registerPhone: "",
      registerPassword: "",
      registerConfirmPassword: "",
    };
  },

  // 组件方法
  methods: {
    // 处理登录逻辑
    async handleLogin() {
      // 检查邮箱和密码是否都已填写
      if (this.loginUsername && this.loginPassword) {
        try {
          const response = await axios.post('/api/login', {
            username: this.loginUsername,
            password: this.loginPassword
          });
          if (response.data.success) {
            alert("登录成功！");
          } else {
            alert(response.data.message);
          }
        } catch (error) {
          alert("登录失败，请稍后重试！");
        }
      } else {
        alert("请填写所有必填项！");
      }
    },

    // 处理注册逻辑
    async handleRegister() {
      // 检查所有必填项是否都已填写
      if (
        this.registerUsername &&
        this.registerPhone &&
        this.registerPassword &&
        this.registerConfirmPassword
      ) {
        // 检查两次输入的密码是否一致
        if (this.registerPassword !== this.registerConfirmPassword) {
          alert("两次输入的密码不一致！");
        } else {
          try {
            const response = await axios.post('/api/register', {
              username: this.registerUsername,
              phone: this.registerPhone,
              password: this.registerPassword
            });
            if (response.data.success) {
              alert("注册成功！");
            } else {
              alert(response.data.message);
            }
          } catch (error) {
            alert("注册失败，请稍后重试！");
          }
        }
      } else {
        alert("请填写所有必填项！");
      }
    },

    sendrequst(){
      axios.get('/api/students')
    }
  },
};
// 导出组件选项
export default options;
</script>