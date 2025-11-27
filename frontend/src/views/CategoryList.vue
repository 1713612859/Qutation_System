<template>
  <div>
    <v-row class="mb-2 mb-sm-4">
      <v-col cols="12" sm="6">
        <h2 class="text-h5 text-sm-h4">Category Management</h2>
      </v-col>
      <v-col cols="12" sm="6" class="text-left text-sm-right">
        <v-btn color="primary" class="w-100 w-sm-auto" @click="openDialog()">
          <v-icon left>mdi-plus</v-icon>
          Add Root Category
        </v-btn>
      </v-col>
    </v-row>

    <v-card>
      <v-card-title class="pb-3">
        <v-text-field
            v-model="search"
            prepend-inner-icon="mdi-magnify"
            label="Search category name"
            placeholder="Enter keywords to filter..."
            variant="outlined"
            density="comfortable"
            clearable
            hide-details
            class="max-w-[500px] w-100"
        />
      </v-card-title>

      <v-card-text>
        <v-treeview
            :items="treeData"
            :search="search"
            item-key="id"
            item-title="name"
            item-children="children"
            :open-all="!!search"
            open-on-click
            activatable
        >
          <template #label="{ item }">
            <div class="d-flex align-center py-2">
              <span>{{ item.name }}</span>
              <v-spacer></v-spacer>
              <v-chip :color="item.enabled ? 'green' : 'red'" size="small" class="ml-4">
                {{ item.enabled ? 'Enabled' : 'Disabled' }}
              </v-chip>
            </div>
          </template>

          <template #append="{ item }">
            <div class="d-flex align-center">
              <v-tooltip text="Add Sub Category">
                <template #activator="{ props }">
                  <v-btn v-bind="props" icon size="mini" color="success" class="ml-3" @click.stop="openDialog(item.id)">
                    <v-icon>mdi-plus</v-icon>
                  </v-btn>
                </template>
              </v-tooltip>

              <v-tooltip text="Edit Category">
                <template #activator="{ props }">
                  <v-btn v-bind="props" icon size="mini" color="primary" class="ml-3" @click.stop="edit(item)">
                    <v-icon>mdi-pencil</v-icon>
                  </v-btn>
                </template>
              </v-tooltip>

              <v-tooltip text="Delete Category">
                <template #activator="{ props }">
                  <v-btn v-bind="props" icon size="mini" color="error" class="ml-3" @click.stop="remove(item.id)">
                    <v-icon>mdi-delete</v-icon>
                  </v-btn>
                </template>
              </v-tooltip>
            </div>
          </template>

        </v-treeview>
      </v-card-text>
    </v-card>

    <v-dialog v-model="showDialog" max-width="600" scrollable>
      <v-card>
        <v-card-title class="text-subtitle-1 text-sm-h6">
          {{ form.id ? 'Edit Category' : 'Add Category' }}
        </v-card-title>
        <v-card-text class="pa-2 pa-sm-4">
          <v-form ref="formRef" v-model="valid">
            <v-text-field
                v-model="form.name"
                label="Category Name"
                :rules="[rules.required]"
                required
                density="compact"
            />
            <v-select
                v-model="form.parentId"
                :items="parentCategoryOptions"
                item-title="name"
                item-value="id"
                label="Parent Category (Optional)"
                density="compact"
                clearable
                hint="Selecting a parent category creates a sub-category."
                persistent-hint
            />
            <v-textarea v-model="form.description" label="Description" rows="2" density="compact" />
            <v-text-field
                v-model="form.sortOrder"
                label="Sort Order"
                type="number"
                density="compact"
                hint="Lower number means higher priority."
                persistent-hint
            />
            <v-checkbox
                v-model="form.enabled"
                label="Enable Category"
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

const defaultForm = {
  id: null,
  name: '',
  parentId: null,
  description: '',
  sortOrder: 0,
  enabled: true,
}

export default {
  name: 'CategoryList',
  data() {
    return {
      search: '',
      allFlatList: [], // 扁平化的所有分类数据 (用于表单下拉和构建树)
      treeData: [], // 嵌套的树形结构数据 (用于 v-treeview)
      showDialog: false,
      valid: false,
      form: { ...defaultForm },
      rules: {
        required: v => !!v || 'This field is required',
      },
    }
  },
  computed: {
    // 用于父分类下拉选择框的选项 (保持不变)
    parentCategoryOptions() {
      // 添加一个 'None' 选项表示根分类
      const options = [{ id: null, name: '(None/Root Category)' }]

      // 排除当前正在编辑的分类本身
      const filtered = this.allFlatList.filter(
          item => item.id !== this.form.id
      )

      // 为了下拉菜单友好，对数据进行排序
      return options.concat(
          filtered.sort((a, b) => (a.sortOrder || 0) - (b.sortOrder || 0))
      )
    },
  },
  mounted() {
    this.load()
  },
  methods: {
    // --- 树形结构构建工具函数 ---

    /**
     * 将扁平数组转换为嵌套的树形结构
     * @param {Array} data - 扁平分类列表
     * @param {number | null} parentId - 当前查找的父ID
     * @returns {Array} 树形结构数组
     */
    buildTree(data, parentId = null) {
      const tree = [];

      // 过滤出当前层级的节点
      const children = data
          .filter(item => item.parentId === parentId)
          .sort((a, b) => (a.sortOrder || 0) - (b.sortOrder || 0)); // 按 Sort Order 排序

      for (const item of children) {
        // 递归查找子节点
        const subChildren = this.buildTree(data, item.id);

        const newItem = { ...item };
        if (subChildren.length) {
          newItem.children = subChildren;
        }
        tree.push(newItem);
      }
      return tree;
    },

    // 核心数据加载方法 (获取所有分类，构建树形结构)
    async load() {
      try {
        // 假设 API 返回所有扁平数据
        const res = await api.get('/categories')

        if (res.data.code === 200) {
          // 1. 扁平化处理数据，确保 parentId 格式正确
          this.allFlatList = res.data.data.map(item => ({
            ...item,
            // 确保 parentId 为 null 或数字
            parentId: item.parentId ? (parseInt(item.parentId) || null) : null,
            sortOrder: item.sortOrder || 0,
            enabled: !!item.enabled
          }));

          // 2. 构建嵌套的树形列表用于 v-treeview
          this.treeData = this.buildTree(this.allFlatList, null);

        } else {
          this.$snackbar?.show(res.data?.msg || 'Failed to load categories', 'error')
        }
      } catch {
        this.$snackbar?.show('Failed to load categories', 'error')
      }
    },

    // --- CRUD 对话框操作 ---
    // 添加 parentId 参数，方便从父节点直接创建子节点
    openDialog(parentId = null) {
      this.form = { ...defaultForm, parentId: parentId }
      this.showDialog = true
      this.$nextTick(() => this.$refs.formRef?.resetValidation())
    },
    closeDialog() {
      this.showDialog = false
      this.$refs.formRef?.resetValidation()
    },
    edit(item) {
      console.log('Edit item', item);
      // 查找原始扁平数据进行编辑，防止缺少字段

      this.form = {
        ...item,
      }
      this.showDialog = true
      this.$nextTick(() => this.$refs.formRef?.resetValidation())
    },
    async save() {
      const { valid } = await this.$refs.formRef.validate()
      if (!valid) {
        this.$snackbar.show('Please check required fields', 'warning')
        return
      }

      const dataToSubmit = {
        ...this.form,
        // 将 null 或 0 传给后端
        parentId: this.form.parentId === 0 ? null : this.form.parentId,
      }

      try {
        const isEdit = !!this.form.id
        const apiCall = isEdit
            ? api.put(`/categories`, dataToSubmit)
            : api.post('/categories', dataToSubmit)

        const res = await apiCall

        if (res.data.code === 200) {
          this.$snackbar.show(isEdit ? 'Category updated successfully' : 'Category added successfully')
          this.closeDialog()
          await this.load() // 成功后重新加载并重建树
        } else {
          this.$snackbar.show(res.data?.msg || 'Failed to save category', 'error')
        }
      } catch {
        this.$snackbar.show('Failed to save category', 'error')
      }
    },
    async remove(id) {
      try {
        if (!confirm('Are you sure to delete this category? Deletion might fail if there are products or sub-categories linked.')) return

        const res = await api.delete(`/categories/${id}`)

        if (res.data.code === 200) {
          this.$snackbar.show('Category deleted successfully')
          await this.load()
        } else {
          this.$snackbar.show(res.data?.msg || 'Failed to delete category', 'error')
        }
      } catch {
        this.$snackbar.show('Failed to delete category', 'error')
      }
    },
  },
}
</script>

<style scoped>
/* 保持基本样式 */
.max-w-\[500px\] {
  max-width: 500px;
}
.w-100 {
  width: 100%;
}
.w-sm-auto {
  width: auto;
}
</style>
