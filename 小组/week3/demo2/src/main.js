import Vue from 'vue'
import App from './App.vue'
import './styles/main.css'
import axios from 'axios'

Vue.prototype.$axios = axios;
Vue.config.productionTip = false;
Vue.config.devtools = true;

new Vue({
  render: h => h(App),
}).$mount('#app')
