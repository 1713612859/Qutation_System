<template>
  <div>
    <v-row class="mb-2 mb-sm-4">
      <v-col cols="12" sm="6" md="6">
        <h2 class="text-h5 text-sm-h4">Product Management</h2>
      </v-col>
      <v-col cols="12" sm="6" md="6" class="text-left text-sm-right">
        <v-btn color="primary" @click="openDialog" class="w-100 w-sm-auto">
          <v-icon left>mdi-plus</v-icon> Add Product
        </v-btn>
      </v-col>
    </v-row>

    <v-card>
      <v-card-title class="pb-3">
        <div class="d-flex flex-column flex-md-row w-100 align-center gap-3">
          <v-text-field
              v-model="search"
              prepend-inner-icon="mdi-magnify"
              label="Search SKU, Product Name, Category or Brand"
              placeholder="Enter keywords to search..."
              variant="outlined"
              density="comfortable"
              clearable
              hide-details
              class="w-100"
          ></v-text-field>
        </div>
      </v-card-title>

      <v-data-table
          :headers="headers"
          :items="filteredList"
          :search="search"
          item-key="id"
          class="d-none d-md-block"
          :items-per-page="10"
      >
        <template v-slot:item.categoryName="{ item }">
          {{ getCategoryFullName(item.categoryId) }}
        </template>

        <template v-slot:item.brandName="{ item }">
          {{ getBrandName(item.brandId) }}
        </template>

        <template v-slot:item.minimumPrice="{ item }">
          {{ item.minimumPrice ? item.minimumPrice.toFixed(2) : '0.00' }}
        </template>

        <template v-slot:item.excludingTaxPrice="{ item }">
          {{ item.excludingTaxPrice ? item.excludingTaxPrice.toFixed(2) : '0.00' }}
        </template>

        <template v-slot:item.currentStock="{ item }">
          {{ item.currentStock ? item.currentStock.toFixed(2) : '0.00' }}
        </template>

        <template v-slot:item.taxRate="{ item }">
          {{ item.taxRate ? (item.taxRate * 100).toFixed(2) : '0.00' }}%
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
          <v-btn icon size="small" color="error" @click="remove(item.id)">
            <v-icon>mdi-delete</v-icon>
          </v-btn>
        </template>
      </v-data-table>

      <div class="d-md-none pa-2">
        <v-card
            v-for="item in filteredList"
            :key="item.id"
            class="mb-3"
            elevation="2"
        >
          <v-card-title class="text-subtitle-1 pb-1">{{ item.name }}</v-card-title>
          <v-card-subtitle class="pt-1">
            <div class="d-flex flex-column">
              <span class="text-body-2">SKU: {{ item.sku }}</span>
              <span class="text-body-2">Category: {{ getCategoryFullName(item.categoryId) }}</span>
              <span class="text-body-2">Brand: {{ getBrandName(item.brandId) }}</span>
              <span class="text-body-2">Retail Price: ¥{{ item.defaultPrice?.toFixed(2) || '0.00' }}</span>
              <span class="text-body-2">Excluding Tax Price: ¥{{ item.excludingTaxPrice?.toFixed(2) || '0.00' }}</span>
              <span class="text-body-2">Stock: {{ item.currentStock?.toFixed(2) || '0' }}</span>
              <span class="text-body-2" v-if="item.remark">Remark: {{ item.remark }}</span>
              <v-chip :color="item.enabled ? 'success' : 'error'" dark x-small class="mt-1">
                {{ item.enabled ? 'Enabled' : 'Disabled' }}
              </v-chip>
            </div>
          </v-card-subtitle>
          <v-card-actions class="pt-0">
            <v-spacer></v-spacer>
            <v-btn icon size="small" color="primary" @click="edit(item)">
              <v-icon>mdi-pencil</v-icon>
            </v-btn>
            <v-btn icon size="small" color="error" @click="remove(item.id)">
              <v-icon>mdi-delete</v-icon>
            </v-btn>
          </v-card-actions>
        </v-card>
      </div>
    </v-card>

    <v-dialog v-model="showDialog" max-width="600" scrollable>
      <v-card>
        <v-card-title class="text-subtitle-1 text-sm-h6">
          {{ form.id ? 'Edit Product' : 'Add Product' }}
        </v-card-title>

        <v-card-text class="pa-2 pa-sm-4">
          <v-form ref="formRef" v-model="formValid">
            <v-text-field
                v-model="form.sku"
                label="SKU"
                required
                :rules="[rules.required]"
                density="compact"
            ></v-text-field>

            <v-text-field
                v-model="form.name"
                label="Product Name"
                required
                :rules="[rules.required]"
                density="compact"
            ></v-text-field>

            <v-row dense>
              <v-col cols="12" sm="6">
                <v-select
                    v-model="form.categoryId"
                    :items="processedCategories"
                    item-title="fullName" item-value="id"
                    label="Category (Hierarchical)"
                    clearable
                    density="compact"
                    :rules="[rules.required]"
                ></v-select>
              </v-col>
              <v-col cols="12" sm="6">
                <v-select
                    v-model="form.brandId"
                    :items="brands"
                    item-title="name"
                    item-value="id"
                    label="Brand"
                    clearable
                    density="compact"
                ></v-select>
              </v-col>
            </v-row>

            <v-textarea
                v-model="form.description"
                label="Product Description/Notes"
                placeholder="Please enter detailed description..."
                rows="4"
                variant="outlined"
                density="comfortable"
                auto-grow
            ></v-textarea>

            <v-row dense>
              <v-col cols="12" sm="6">
                <v-text-field v-model="form.unit" label="Unit" density="compact"></v-text-field>
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field
                    v-model.number="form.defaultPrice"
                    type="number"
                    label="Default Price"
                    step="0.01"
                    :rules="[rules.required, rules.nonNegative]"
                    density="compact"
                ></v-text-field>
              </v-col>
            </v-row>

            <v-row dense>
              <v-col cols="12" sm="6">
                <v-select
                    v-model="form.taxRate"
                    :items="taxRateOptions"
                    item-title="title"
                    item-value="value"
                    label="Tax Rate"
                    density="compact"
                ></v-select>
              </v-col>
              <v-col cols="12" sm="6">
                <v-switch
                    v-model="form.enabled"
                    :label="form.enabled ? 'Enabled' : 'Disabled'"
                    :color="form.enabled ? 'success' : 'error'"
                    density="compact"
                />
              </v-col>
            </v-row>

            <v-row dense>
              <v-col cols="12" sm="6">
                <v-text-field
                    v-model.number="form.minimumPrice"
                    type="number"
                    label="Minimum Price"
                    step="0.01"
                    
                    density="compact"
                ></v-text-field>
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field
                    v-model.number="form.excludingTaxPrice"
                    type="number"
                    label="Excluding Tax Price"
                    step="0.01"
                    :readonly="true"
                    density="compact"
                ></v-text-field>
              </v-col>
            </v-row>

            <v-row dense>
              <v-col cols="12" sm="6">
                <v-text-field
                    v-model.number="form.currentStock"
                    type="number"
                    label="Current Stock"
                    step="0.01"
                    :rules="[rules.nonNegative]"
                    density="compact"
                ></v-text-field>
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field
                    v-model="form.remark"
                    label="Remark"
                    maxlength="200"
                    counter
                    density="compact"
                ></v-text-field>
              </v-col>
            </v-row>
          </v-form>
        </v-card-text>

        <v-card-actions class="pa-2 pa-sm-4">
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
  name: 'ProductList',
  data() {
    return {
      search: '',
      list: [],
      rawCategoriesTree: [], // 存储后端返回的原始树形结构
      processedCategories: [], // 处理后的扁平列表 (包含 fullName)
      brands: [],     // 品牌列表
      showDialog: false,
      formValid: false,
      taxRateOptions: [
        { title: '0%', value: 0 },
        { title: '12%', value: 0.12 }
      ],
      form: {},
      formDefault: {
        sku: '',
        name: '',
        description: '',
        categoryId: null,
        brandId: null,
        unit: '',
        defaultPrice: 0,
        minimumPrice: 0,
        excludingTaxPrice: 0,
        taxRate: 0.12,
        currentStock: 0,
        remark: '',
        enabled: true,
      },
      headers: [
        { title: 'ID', key: 'id', sortable: true },
        { title: 'SKU', key: 'sku', sortable: true },
        { title: 'Product Name', key: 'name', sortable: true },
        { title: 'Category', key: 'categoryName', sortable: true },
        { title: 'Brand', key: 'brandName', sortable: true },
        { title: 'Unit', key: 'unit', sortable: true },
        { title: 'Retail Price', key: 'defaultPrice', sortable: true },
        { title: 'Excluding Tax Price', key: 'excludingTaxPrice', sortable: true },
        {title: 'Minimum Price', key: 'minimumPrice', sortable: true },
        { title: 'Current Stock', key: 'currentStock', sortable: true },
        { title: 'Tax Rate (%)', key: 'taxRate', sortable: true },
        { title: 'Status', key: 'enabled', sortable: true },
        {title: 'Remark', key: 'remark', sortable: false },
        { title: 'Actions', key: 'actions', sortable: false, align: 'end' },
      ],
      rules: {
        required: (v) => !!v || 'Required field cannot be empty',
        nonNegative: (v) => (v !== null && v !== '' && Number(v) >= 0) || 'Value cannot be less than 0',
        checkSku:  (v) => {
          if (!v) return 'SKU cannot be empty'
       
        },
        minPriceLessThanDefault: (v) => {
          if (!v || !this.form.defaultPrice) return true
          return Number(v) <= Number(this.form.defaultPrice) || 'Minimum price cannot exceed default price'
        },
        descriptionMaxLength: (v) => !v || v.length <= 500 || 'Description cannot exceed 500 characters',
      },
    }
  },
  computed: {
    filteredList() {
      if (!this.search) return this.list
      const s = this.search.toLowerCase()

      return this.list.filter(
          (item) =>
              item.sku?.toLowerCase().includes(s) ||
              item.name?.toLowerCase().includes(s) ||
              item.description?.toLowerCase().includes(s) ||
              item.remark?.toLowerCase().includes(s) ||
              this.getCategoryFullName(item.categoryId)?.toLowerCase().includes(s) ||
              this.getBrandName(item.brandId)?.toLowerCase().includes(s) ||
              item.unit?.toLowerCase().includes(s)
      )
    },
  },
  mounted() {
    this.loadInitialData()
  },
  methods: {
    async loadInitialData() {
      await Promise.all([
        this.loadProducts(),
        this.loadCategories(), // 加载和处理分类树
        this.loadBrands(),
      ])
    },

    async loadProducts() {
      try {
        const res = await api.get('/products')
        this.list = res.data.data.map(item => ({
          ...this.formDefault,  // 保证所有字段都存在
          ...item,
          categoryId: item.categoryId || null,
          brandId: item.brandId || null,
          taxRate: typeof item.taxRate === 'number' ? item.taxRate : 0.12,
        }))
      } catch (e) {
        this.$snackbar.show('Failed to load products', 'error')
      }
    },

    /**
     * 加载分类列表 (假设返回树形结构)
     */
    async loadCategories() {
      try {
        const res = await api.get('/categories')
        this.rawCategoriesTree = res.data.data || []

        // 将树形结构转换为带有全路径的扁平列表
        this.processedCategories = this.flattenAndProcessTree(this.rawCategoriesTree);

      } catch (e) {
        this.$snackbar.show('Failed to load categories', 'error')
      }
    },

    /**
     * 递归函数：遍历树形结构，将其展平，并生成带有完整层级路径的名称 (fullName)
     */
    flattenAndProcessTree(tree, parentPath = '') {
      let result = [];

      tree.forEach(item => {
        // 1. 生成当前分类的完整路径名 (例如: "电子产品 > 手机")
        const currentPath = parentPath ? `${parentPath} > ${item.name}` : item.name;

        // 2. 将当前分类添加到结果列表
        result.push({
          id: item.id,
          name: item.name,
          fullName: currentPath,
          parentId: item.parentId,
          sortOrder: item.sortOrder,
          enabled: item.enabled
        });

        // 3. 递归处理子节点
        if (item.children && item.children.length) {
          result = result.concat(this.flattenAndProcessTree(item.children, currentPath));
        }
      });

      return result;
    },

    /**
     * 根据 ID 获取分类的完整路径名称
     */
    getCategoryFullName(id) {
      const category = this.processedCategories.find(c => c.id === id)
      return category ? category.fullName : 'N/A'
    },

    async loadBrands() {
      try {
        const res = await api.get('/brands')
        this.brands = res.data.data || []
      } catch (e) {
        this.$snackbar.show('Failed to load brands', 'error')
      }
    },

    getBrandName(id) {
      const brand = this.brands.find(b => b.id === id)
      return brand ? brand.name : 'N/A'
    },

    openDialog() {
      this.form = JSON.parse(JSON.stringify(this.formDefault))
      this.showDialog = true
      this.$nextTick(() => this.$refs.formRef?.resetValidation())
    },
    edit(row) {
      // 深拷贝并合并默认值，确保所有字段都存在
      this.form = {
        ...this.formDefault,
        ...row,
        categoryId: row.categoryId || null,
        brandId: row.brandId || null,
        taxRate: typeof row.taxRate === 'number' ? row.taxRate : 0.12,
      }
      this.showDialog = true
      this.$nextTick(() => this.$refs.formRef?.resetValidation())
    },
    closeDialog() {
      this.showDialog = false
      this.$refs.formRef?.resetValidation()
    },
    async save() {
      const { valid } = await this.$refs.formRef.validate()
      if (!valid) {
        this.$snackbar.show('Please check required fields', 'error')
        return
      }
      try {
        const dataToSubmit = {
          ...this.form,
          categoryId: this.form.categoryId || null,
          brandId: this.form.brandId || null,
        }

        if (dataToSubmit.id) {
          const res = await api.put(`/products/${dataToSubmit.id}`, dataToSubmit)
          if (res.data.code === 200){
            this.$snackbar.show('Successfully modified')
            this.showDialog = false
            await this.loadProducts()
          }else {
            this.$snackbar.show(res.data?.msg||'Modification failed', 'error')
          }
        } else {
          const res =   await api.post('/products', dataToSubmit)
          if (res.data.code === 200){
            this.$snackbar.show('Saved successfully')
            this.showDialog = false
            await this.loadProducts()
          }else {
            this.$snackbar.show(res.data?.msg||'Save failed', 'error')
          }
        }

      } catch (error) {
        this.$snackbar.show('Save failed', 'error')
      }
    },
    async remove(id) {
      if (confirm('Are you sure you want to delete this product?')) {
        try {
          const res = await api.delete(`/products/${id}`)
          if (res.data.code !== 200){
            this.$snackbar.show(res.data?.msg||'Delete failed', 'error')
            return
          }
          this.$snackbar.show('Deleted successfully')
          this.loadProducts()
        } catch {
          this.$snackbar.show('Delete failed', 'error')
        }
      }
    },
  },
}
</script>

<style scoped>
.v-data-table {
  font-size: 14px;
}
</style>
