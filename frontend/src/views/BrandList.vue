<template>
  <div>
    <v-row class="mb-2 mb-sm-4">
      <v-col cols="12" sm="6">
        <h2 class="text-h5 text-sm-h4">Brand Management</h2>
      </v-col>
      <v-col cols="12" sm="6" class="text-left text-sm-right">
        <v-btn color="primary" class="w-100 w-sm-auto" @click="openDialog">
          <v-icon left>mdi-plus</v-icon>
          Add Brand
        </v-btn>
      </v-col>
    </v-row>

    <v-card>
      <v-card-title class="pb-3">
        <v-text-field
          v-model="search"
          prepend-inner-icon="mdi-magnify"
          label="Search by brand name"
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
        :items-per-page-options="[5, 10, 25, 50]"
        :server-items-length="totalItems"
        @update:options="handleOptionsUpdate"
        item-key="id"
        class="d-none d-md-block"
      >
        <template #item.enabled="{ item }">
          <v-chip :color="item.enabled ? 'green' : 'red'" small>
            {{ item.enabled ? 'Enabled' : 'Disabled' }}
          </v-chip>
        </template>
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
        <v-card v-for="item in list" :key="item.id" class="mb-3" elevation="2">
          <v-card-title class="text-subtitle-1 pb-1">
            {{ item.name }}
          </v-card-title>
          <v-card-subtitle>
            <div class="text-body-2">
              <div>ID: {{ item.id }}</div>
              <div>Description: {{ item.description || '-' }}</div>
              <div>
                Status:
                <v-chip :color="item.enabled ? 'green' : 'red'" x-small>
                  {{ item.enabled ? 'Enabled' : 'Disabled' }}
                </v-chip>
              </div>
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

    <v-dialog v-model="showDialog" max-width="500" scrollable>
      <v-card>
        <v-card-title class="text-subtitle-1 text-sm-h6">
          {{ form.id ? 'Edit Brand' : 'Add Brand' }}
        </v-card-title>
        <v-card-text class="pa-2 pa-sm-4">
          <v-form ref="formRef" v-model="valid">
            <v-text-field
              v-model="form.name"
              label="Brand Name"
              :rules="[rules.required]"
              required
              density="compact"
            />
            <v-textarea v-model="form.description" label="Description" rows="2" density="compact" />
            <v-checkbox
              v-model="form.enabled"
              label="Enable Brand"
              hide-details
              density="compact"
            />
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
import api from '../api'

// 默认表单结构
const defaultForm = {
  id: null,
  name: '',
  description: '',
  enabled: true,
}

export default {
  name: 'BrandList',
  data() {
    return {
      search: '',
      list: [],
      totalItems: 0,
      showDialog: false,
      valid: false,
      form: { ...defaultForm },
      options: {
        page: 1,
        itemsPerPage: 10,
        sortBy: [],
      },
      rules: {
        required: v => !!v || 'This field is required',
      },
      headers: [
        { title: 'ID', key: 'id', sortable: true },
        { title: 'Brand Name', key: 'name', sortable: true },
        { title: 'Description', key: 'description', sortable: false },
        { title: 'Status', key: 'enabled', sortable: true },
        { title: 'Actions', key: 'actions', align: 'end', sortable: false },
      ],
    }
  },
  mounted() {
    this.load()
  },
  methods: {
    // 核心数据加载方法
    async load() {
      const { page, itemsPerPage, sortBy } = this.options
      const params = {
        page: page,
        limit: itemsPerPage,
        name: this.search, // 后端应支持按名称搜索
        // 排序参数需要根据您的后端API调整
        // sortBy: sortBy.length ? sortBy[0].key : undefined,
      }

      try {
        const res = await api.get('/brands', { params })

        if (res.data.code === 200) {
          this.list = res.data.data
          this.totalItems = res.data.total
        } else {
          this.$snackbar?.show(res.data?.msg || 'Failed to load brands', 'error')
        }
      } catch {
        this.$snackbar?.show('Failed to load brands', 'error')
      }
    },

    // PC端 v-data-table 的选项更新事件
    handleOptionsUpdate(newOptions) {
      this.options = newOptions
      this.load()
    },

    // 搜索框输入处理
    handleSearch() {
      this.options.page = 1
      this.load()
    },

    // --- CRUD 对话框操作 ---
    openDialog() {
      this.form = { ...defaultForm }
      this.showDialog = true
      this.$nextTick(() => this.$refs.formRef?.resetValidation())
    },
    closeDialog() {
      this.showDialog = false
      this.$refs.formRef?.resetValidation()
    },
    edit(item) {
      // 注意：确保 item 中的 enabled 字段是布尔值
      this.form = { ...item, enabled: !!item.enabled }
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
        const apiCall = isEdit
          ? api.put(`/brands`, this.form)
          : api.post('/brands', this.form)

        const res = await apiCall

        if (res.data.code === 200) {
          this.$snackbar.show(isEdit ? 'Brand updated successfully' : 'Brand added successfully')
          this.closeDialog()
          // 成功后重新加载数据
          await this.load()
        } else {
          this.$snackbar.show(res.data?.msg || 'Failed to save brand', 'error')
        }
      } catch {
        this.$snackbar.show('Failed to save brand', 'error')
      }
    },
    async remove(id) {
      try {
        if (!confirm('Are you sure to delete this brand?')) return

        // 注意：删除品牌时，后端应检查是否有产品正在使用该品牌。
        const res = await api.delete(`/brands/${id}`)

        if (res.data.code === 200) {
          this.$snackbar.show('Brand deleted successfully')
          await this.load()
        } else {
          this.$snackbar.show(res.data?.msg || 'Failed to delete brand', 'error')
        }
      } catch {
        this.$snackbar.show('Failed to delete brand', 'error')
      }
    },
  },
}
</script>

<style scoped>
.v-data-table {
  font-size: 14px;
}
/* CustomerList 中的样式，此处保留 */
.max-w-\[500px\] {
  max-width: 500px;
}
.w-100 {
  width: 100%;
}
.w-sm-auto {
  /* 在 sm 屏幕上宽度自动，否则全宽 */
  width: auto;
}
</style>
