<template>
  <v-app>
    <v-main>
      <v-container class="fill-height" fluid>
        <v-row align="center" justify="center" class="pa-2">
          <v-col cols="12" sm="10" md="8" lg="6">
            <v-card class="elevation-12">
              <v-toolbar color="primary" dark flat>
                <v-toolbar-title>User Registration</v-toolbar-title>
              </v-toolbar>
              <v-card-text>
                <v-form ref="formRef" v-model="valid">
                  <v-text-field
                      v-model="form.username"
                      label="Username"
                      prepend-inner-icon="mdi-account"
                      :rules="[rules.required]"
                      required
                  ></v-text-field>
                  <v-text-field
                      v-model="form.password"
                      label="Password"
                      prepend-inner-icon="mdi-lock"
                      type="password"
                      :rules="[rules.required]"
                      required
                  ></v-text-field>
                  <v-text-field
                      v-model="form.confirmPassword"
                      label="Confirm Password"
                      prepend-inner-icon="mdi-lock-check"
                      type="password"
                      :rules="[rules.required, rules.matchPassword]"
                      required
                  ></v-text-field>
                  <v-text-field
                      v-model="form.fullName"
                      label="Full Name"
                      prepend-inner-icon="mdi-account-circle"
                      :rules="[rules.required]"
                      required
                  ></v-text-field>
                  <v-text-field
                      v-model="form.email"
                      label="Email"
                      prepend-inner-icon="mdi-email"
                      :rules="[rules.required, rules.email]"
                      required
                  ></v-text-field>
                </v-form>
              </v-card-text>
              <v-card-actions class="flex-column flex-sm-row pa-2 pa-sm-4">
                <v-spacer></v-spacer>
                <v-btn color="primary" block class="d-sm-inline-block mb-2 mb-sm-0" @click="register"
                       :disabled="!valid">Register
                </v-btn>
                <v-btn color="secondary" variant="text" block class="d-sm-inline-block" @click="$router.push('/login')">
                  Back to Login
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
  name: 'Register',
  data() {
    return {
      valid: false,
      form: {
        username: '',
        password: '',
        confirmPassword: '',
        fullName: '',
        email: ''
      },
      rules: {
        required: value => !!value || 'Required',
        email: value => {
          const pattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
          return pattern.test(value) || 'Please enter a valid email address'
        },
        matchPassword: value => value === this.form.password || 'Passwords do not match'
      }
    }
  },
  methods: {
    async register() {
      const {valid} = await this.$refs.formRef.validate()
      if (valid) {
        try {
          const {confirmPassword, ...registerData} = this.form
          const res = await api.post('/auth/register', registerData)
          if (res.data.code === 200) {
            localStorage.setItem('token', res.data.data.token)
            localStorage.setItem('userInfo', JSON.stringify(res.data.data))
            this.$snackbar?.show('Registration successful')
            this.$router.push('/dashboard')
          } else {
            this.$snackbar?.show(res.data.msg || 'Registration failed', 'error')
          }
        } catch (error) {
          this.$snackbar?.show(error.response?.data?.msg || 'Registration failed', 'error')
        }
      }
    }
  }
}
</script>
