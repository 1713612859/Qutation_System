<template>
  <div>
    <!-- Title and Button -->
    <v-row class="mb-2 mb-sm-4">
      <v-col cols="12" sm="6">
        <h2 class="text-h5 text-sm-h4">User Management</h2>
      </v-col>
      <v-col cols="12" sm="6" class="text-left text-sm-right">
        <v-btn color="primary" @click="openDialog">
          <v-icon left>mdi-plus</v-icon>
          Add User
        </v-btn>
      </v-col>
    </v-row>

    <!-- Search and Table -->
    <v-card>
      <v-card-title class="pb-3">
        <v-text-field
            v-model="search"
            prepend-inner-icon="mdi-magnify"
            label="Search username, name or email"
            placeholder="Enter keywords to search..."
            variant="outlined"
            density="comfortable"
            clearable
            hide-details
            style="max-width: 500px"
        />
      </v-card-title>

      <v-data-table
          :headers="headers"
          :items="filteredList"
          :search="search"
          item-key="id"
          :items-per-page="10"
      >
        <template v-slot:item.role="{ item }">
          <v-chip :color="getRoleColor(item.role)" dark small>
            {{ getRoleText(item.role) }}
          </v-chip>
        </template>

        <template v-slot:item.enabled="{ item }">
          <v-chip :color="item.enabled ? 'success' : 'error'" dark small>
            {{ item.enabled ? 'Enabled' : 'Disabled' }}
          </v-chip>
        </template>

        <template v-slot:item.actions="{ item }">
          <v-btn icon size="small" color="primary" @click="edit(item)">
            <v-icon>mdi-pencil</v-icon>
          </v-btn>
        </template>
      </v-data-table>
    </v-card>

    <!-- Form Dialog -->
    <v-dialog v-model="showDialog" max-width="600" scrollable>
      <v-card>
        <v-card-title>
          {{ form.id ? 'Edit User' : 'Add User' }}
        </v-card-title>
        <v-card-text>
          <v-form ref="formRef" v-model="valid">
            <v-text-field
                v-model="form.username"
                label="Username"
                :disabled="!!form.id"
                :rules="[v => !!v || 'Username is required']"
                required
                density="compact"
            />
            <v-text-field
                v-if="!form.id"
                v-model="form.password"
                label="Password"
                type="password"
                :rules="[v => !!v || 'Password is required']"
                required
                density="compact"
            />
            <v-text-field
                v-if="form.id"
                v-model="form.newPassword"
                label="New Password"
                type="password"
                hint="Leave blank to keep unchanged"
                density="compact"
            />
            <v-text-field
                v-model="form.fullName"
                label="Full Name"
                density="compact"
            />
            <v-text-field
                v-model="form.email"
                label="Email"
                type="email"
                :rules="[v => !v || /.+@.+\..+/.test(v) || 'Invalid email format']"
                density="compact"
            />
            <v-select
                v-model="form.role"
                :items="roles"
                item-title="label"
                item-value="value"
                label="Role"
                density="compact"
            />
            <v-switch
                v-model="form.enabled"
                label="Enable"
                color="success"
                density="compact"
            />
          </v-form>
        </v-card-text>

        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn size="small" @click="closeDialog">Cancel</v-btn>
          <v-btn color="primary" size="small" @click="save">Save</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script>
import api from '../api.js'

export default {
  name: 'UserList',
  data() {
    return {
      search: '',
      list: [],
      showDialog: false,
      valid: false,
      form: { role: 'SALES', enabled: true },
      roles: [
        { label: 'Administrator', value: 'ADMIN' },
        { label: 'Sales', value: 'SALES' },
        { label: 'Approver', value: 'APPROVER' },
        { label: 'Viewer', value: 'VIEWER' }
      ],
      headers: [
        { title: 'ID', key: 'id', sortable: true },
        { title: 'Username', key: 'username', sortable: true },
        { title: 'Full Name', key: 'fullName', sortable: true },
        { title: 'Email', key: 'email', sortable: true },
        { title: 'Role', key: 'role', sortable: true },
        { title: 'Status', key: 'enabled', sortable: true },
        { title: 'Actions', key: 'actions', align: 'end' }
      ]
    }
  },
  computed: {
    filteredList() {
      if (!this.search) return this.list
      const s = this.search.toLowerCase()
      return this.list.filter(i =>
          (i.username && i.username.toLowerCase().includes(s)) ||
          (i.fullName && i.fullName.toLowerCase().includes(s)) ||
          (i.email && i.email.toLowerCase().includes(s))
      )
    }
  },
  mounted() {
    this.load()
  },
  methods: {
    async load() {
      try {
        const res = await api.get('/users')
        this.list = res.data.data
      } catch (error) {
        this.$snackbar?.show('Failed to load', 'error')
      }
    },
    openDialog() {
      this.form = { role: 'SALES', enabled: true }
      this.showDialog = true
    },
    closeDialog() {
      this.showDialog = false
      this.form = { role: 'SALES', enabled: true }
    },
    getRoleColor(role) {
      return {
        ADMIN: 'error',
        SALES: 'primary',
        APPROVER: 'success',
        VIEWER: 'info'
      }[role] || 'grey'
    },
    getRoleText(role) {
      return {
        ADMIN: 'Administrator',
        SALES: 'Sales',
        APPROVER: 'Approver',
        VIEWER: 'Viewer'
      }[role] || role
    },
    edit(row) {
      this.form = { ...row, newPassword: '' }
      this.showDialog = true
    },
    async save() {
      const result = await this.$refs.formRef.validate()
      if (!result.valid) {
        this.$snackbar?.show('Please fill in required fields', 'warning')
        return
      }

      try {
        const data = { ...this.form }
        if (data.newPassword) data.password = data.newPassword

        if (data.id) {
          await api.put(`/users/${data.id}`, data)
        } else {
          await api.post('/users', data)
        }

        this.$snackbar?.show('Saved successfully')
        this.closeDialog()
        this.load()
      } catch (error) {
        this.$snackbar?.show('Failed to save', 'error')
      }
    }
  }
}
</script>
