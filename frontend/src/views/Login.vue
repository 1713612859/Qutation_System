<template>
  <v-app>
    <v-main>
      <v-container class="fill-height" fluid>
        <v-row align="center" justify="center" class="pa-2">
          <v-col cols="12" sm="10" md="6" lg="4">
            <v-card class="elevation-12 rounded-2xl">
              <!-- 顶部标题栏 -->
              <v-toolbar color="primary" dark flat>
                <v-toolbar-title>Quotation Management System</v-toolbar-title>
              </v-toolbar>

              <!-- 表单 -->
              <v-card-text>
                <v-form ref="formRef" v-model="valid">
                  <v-text-field
                      v-model="form.username"
                      label="Username"
                      prepend-inner-icon="mdi-account"
                      :rules="[rules.required]"
                      required
                      @keyup.enter="login"
                  ></v-text-field>

                  <v-text-field
                      v-model="form.password"
                      label="Password"
                      prepend-inner-icon="mdi-lock"
                      type="password"
                      :rules="[rules.required]"
                      required
                      @keyup.enter="login"
                  ></v-text-field>
                </v-form>
              </v-card-text>

              <!-- 按钮 -->
              <v-card-actions class="flex-column flex-sm-row pa-2 pa-sm-4">
                <v-spacer></v-spacer>
                <v-btn
                    color="primary"
                    block
                    class="d-sm-inline-block mb-2 mb-sm-0"
                    @click="login"
                    :disabled="!valid"
                >
                  Sign
                </v-btn>
                <v-btn
                    color="secondary"
                    variant="text"
                    block
                    class="d-sm-inline-block"
                    @click="$router.push('/register')"
                >
                  Register
                </v-btn>
              </v-card-actions>
            </v-card>
          </v-col>
        </v-row>
      </v-container>

    </v-main>
  </v-app>
</template>

<script>
import api from '../api.js'

export default {
  name: 'Login',
  data() {
    return {
      valid: false,
      form: {
        username: '',
        password: ''
      },
      rules: {
        required: value => !!value || 'required'
      }
    }
  },
  methods: {
    async login() {
      const {valid} = await this.$refs.formRef.validate()
      if (valid) {
        try {
          const res = await api.post('/auth/login', this.form)
          if (res.data.code === 200) {
            // 保存 Token & 用户信息
            localStorage.setItem('token', res.data.data.token)
            localStorage.setItem('userInfo', JSON.stringify(res.data.data))
            this.$snackbar.show(res.data.msg, 'success')
            // 跳转页面
            setTimeout(() => {
              this.$router.push('/dashboard')
            }, 800)
          } else {
            this.$snackbar.show(res.data.msg, 'error')
          }
        } catch (error) {
          this.$snackbar.show(res.data.msg, 'error')
        }
      }
    }
  }
}
</script>

<style scoped>
.fill-height {
  min-height: 100vh;
  background: linear-gradient(135deg, #e3f2fd, #bbdefb);
}
</style>
