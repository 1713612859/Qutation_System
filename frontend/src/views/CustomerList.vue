<template>
  <div>
    <v-row class="mb-2 mb-sm-4">
      <v-col cols="12" sm="6">
        <h2 class="text-h5  text-sm-h4">Customer Management</h2>
      </v-col>
      <v-col cols="12" sm="6" class="text-left text-sm-right">
        <v-btn color="primary" class="w-100 w-sm-auto" @click="openDialog">
          <v-icon left>mdi-plus</v-icon>
          Add Customer
        </v-btn>
      </v-col>
    </v-row>

    <v-card>
      <v-card-title class="pb-3">
        <v-text-field
            v-model="search"
            prepend-inner-icon="mdi-magnify"
            label="Search company name, customer name, contact or phone"
            placeholder="Enter keywords to search..."
            variant="outlined"
            density="comfortable"
            clearable
            hide-details
            class="max-w-[500px] w-100"
            @update:model-value="handleSearch"
        />
      </v-card-title>

      <v-data-table
          :headers="headers"
          :items="list"
          :items-per-page="options.itemsPerPage"
          :page="options.page"
          :items-per-page-options="[5, 10, 25 ,50 ]"
          :server-items-length="totalItems"
          @update:options="handleOptionsUpdate"
          item-key="id"
          class="d-none d-md-block"
      >
        <template #item.actions="{ item }">
          <v-btn icon size="small" color="primary" @click="edit(item)">
            <v-icon>mdi-pencil</v-icon>
          </v-btn>
          <v-btn icon size="small" color="error" @click="remove(item.id)">
            <v-icon>mdi-delete</v-icon>
          </v-btn>
        </template>
      </v-data-table>

      <div class="d-md-none pa-2">
        <v-card
            v-for="item in list" :key="item.id"
            class="mb-3"
            elevation="2"
        >
          <v-card-title class="text-subtitle-1 pb-1">
            {{ item.companyName }}
          </v-card-title>
          <v-card-subtitle>
            <div class="text-body-2">
              <div>Customer Name (Attn): {{ item.customerName || '-' }}</div>
              <div>Contact: {{ item.contactName || '-' }}</div>
              <div>Phone: {{ item.contactPhone || '-' }}</div>
              <div>Email: {{ item.contactEmail || '-' }}</div>
              <div>City: {{ item.city || '-' }}</div>
            </div>
          </v-card-subtitle>
          <v-card-actions>
            <v-spacer />
            <v-btn icon size="small" color="primary" @click="edit(item)">
              <v-icon>mdi-pencil</v-icon>
            </v-btn>
            <v-btn icon size="small" color="error" @click="remove(item.id)">
              <v-icon>mdi-delete</v-icon>
            </v-btn>
          </v-card-actions>
        </v-card>

        <div class="text-center pa-4">
          <v-pagination
              v-model="options.page"
              :length="Math.ceil(totalItems / options.itemsPerPage)"
              :total-visible="5"
              @update:model-value="load"
          />
        </div>
      </div>
    </v-card>

    <v-dialog v-model="showDialog" max-width="600" scrollable>
      <v-card>
        <v-card-title class="text-subtitle-1 text-sm-h6">
          {{ form.id ? 'Edit Customer' : 'Add Customer' }}
        </v-card-title>
        <v-card-text class="pa-2 pa-sm-4">
          <v-form ref="formRef" v-model="valid">
            <v-text-field v-model="form.companyName" label="Company Name" :rules="[rules.required]" required density="compact" />
            <v-text-field v-model="form.customerName" label="Customer Name (Attention)" :rules="[rules.required]" required density="compact" hint="The person/department to attention" persistent-hint />
            <v-text-field v-model="form.contactName" label="Contact" :rules="[rules.required]" required density="compact" />
            <v-text-field v-model="form.contactPhone" label="Phone" :rules="[rules.required]" required density="compact" />
            <v-text-field v-model="form.contactEmail" label="Email" type="email" :rules="[rules.email]" density="compact" />
            <v-textarea v-model="form.address" label="Address" rows="2" density="compact" />
            <v-row dense>
              <v-col cols="12" sm="6">
                <v-text-field v-model="form.city" label="City" density="compact" />
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field v-model="form.province" label="Province" density="compact" />
              </v-col>
            </v-row>
          </v-form>
        </v-card-text>
        <v-card-actions class="pa-2 pa-sm-4">
          <v-spacer />
          <v-btn size="small" @click="closeDialog">Cancel</v-btn>
          <v-btn color="primary" size="small" @click="save">Save</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script>
// 假设 '../api' 是一个封装好的 axios 实例
import api from '../api'

export default {
  name: 'CustomerList',
  data() {
    return {
      search: '',
      list: [], // 存储当前页数据
      totalItems: 0, // 存储总数据量 (从 API 获取)
      showDialog: false,
      valid: false,
      form: {},
      options: { // 存储分页、排序状态
        page: 1,
        itemsPerPage: 10,
        sortBy: [], // [{ key: 'companyName', order: 'asc' }]
      },
      rules: {
        required: v => !!v || 'This field is required',
        email: v => !v || /.+@.+\..+/.test(v) || 'Invalid email format'
      },
      headers: [
        { title: 'ID', key: 'id', sortable: true },
        { title: 'Company Name', key: 'companyName', sortable: true },
        { title: 'Customer Name (Attn)', key: 'customerName', sortable: true },
        { title: 'Contact', key: 'contactName', sortable: true },
        { title: 'Phone', key: 'contactPhone', sortable: true },
        { title: 'Email', key: 'contactEmail', sortable: true },
        { title: 'City', key: 'city', sortable: true },
        { title: 'Actions', key: 'actions', align: 'end', sortable: false }
      ]
    }
  },
  // 移除了 original filteredList computed 属性
  mounted() {
    // 首次加载，使用默认 options
    this.load()
  },
  methods: {
    // 核心数据加载方法，负责向后端发送请求
    async load() {
      const { page, itemsPerPage, sortBy } = this.options

      // 构造 API 请求参数
      const params = {
        page: page,
        limit: itemsPerPage,
        search: this.search,
        // 示例：添加排序参数
        // sortBy: sortBy.length ? sortBy[0].key : undefined,
        // sortDesc: sortBy.length ? (sortBy[0].order === 'desc' ? 1 : 0) : undefined
      }

      try {
        // ⚠️ 您的后端 API 需要支持以下参数并返回分页数据和总数
        const res = await api.get('/customers', { params })

        // 假设 API 返回结构为 { data: [{...}], total: 100, code: 200 }
        if (res.data.code === 200) {
          this.list = res.data.data
          this.totalItems = res.data.total
        } else {
          this.$snackbar?.show(res.data?.msg || 'Failed to load data', 'error')
        }
      } catch {
        this.$snackbar?.show('Failed to load data', 'error')
      }
    },

    // PC端 v-data-table 的选项更新事件
    handleOptionsUpdate(newOptions) {
      // v-data-table 会自动更新 options，我们只需要调用 load 即可
      this.options = newOptions
      this.load()
    },

    // 搜索框输入处理 (使用 debounce 或 simple update)
    handleSearch() {
      // 搜索时，重置页码为 1
      this.options.page = 1
      // 为了避免频繁请求，您可以添加 debounce (节流/防抖)
      // 这里的示例是立即加载
      this.load()
    },

    // --- 其他 CRUD 方法保持不变，但在保存/删除后应调用 load() ---
    openDialog() {
      this.form = {}
      this.showDialog = true
      this.$nextTick(() => this.$refs.formRef?.resetValidation())
    },
    closeDialog() {
      this.showDialog = false
      this.$refs.formRef?.resetValidation()
    },
    edit(item) {
      this.form = { ...item }
      this.showDialog = true
      this.$nextTick(() => this.$refs.formRef?.resetValidation())
    },
    async save() {
      const { valid } = await this.$refs.formRef.validate()
      if (!valid) {
        this.$snackbar.show('Please check required fields', 'warning')
        return
      }

      try {
        const isEdit = !!this.form.id
        const res = isEdit
            ? await api.put(`/customers/${this.form.id}`, this.form)
            : await api.post('/customers', this.form)

        if (res.data.code === 200) {
          this.$snackbar.show(isEdit ? 'Updated successfully' : 'Added successfully')
          this.showDialog = false
          // 保存成功后，重新加载当前页数据
          await this.load()
        } else {
          this.$snackbar.show(res.data?.msg || 'Failed to save', 'error')
        }
      } catch {
        this.$snackbar.show('Failed to save', 'error')
      }
    },
    async remove(id) {
      try {
        if (!confirm('Are you sure to delete this customer?')) return
        const res = await api.delete(`/customers/${id}`)
        if (res.data.code === 200) {
          this.$snackbar.show('Deleted successfully')
          // 删除成功后，重新加载当前页数据
          await this.load()
        } else {
          this.$snackbar.show(res.data?.msg || 'Failed to delete', 'error')
        }
      } catch {
        this.$snackbar.show('Failed to delete', 'error')
      }
    }
  }
}
</script>

<style scoped>
.v-data-table {
  font-size: 14px;
}
</style>
