<template>
  <div>
    <v-row class="mb-2 mb-sm-4">
      <v-col cols="12" sm="6" md="6">
        <h2 class="text-h5 text-sm-h4">Package Management</h2>
      </v-col>
      <v-col cols="12" sm="6" md="6" class="text-left text-sm-right">
        <v-btn color="primary" block class="d-md-none" @click="openDialog()">
          <v-icon left>mdi-plus</v-icon>
          <span class="d-none d-sm-inline">Add Package</span>
          <span class="d-sm-none">Add</span>
        </v-btn>
        <v-btn color="primary" class="d-none d-md-inline-block" @click="openDialog()">
          <v-icon left>mdi-plus</v-icon>
          Add Package
        </v-btn>
      </v-col>
    </v-row>

    <v-card>
      <v-card-title class="pb-3">
        <div class="d-flex flex-column flex-md-row w-100 align-center gap-3">
          <!-- 左侧：搜索框 -->
          <div class="flex-grow-1 w-100" style="max-width: 500px;">
            <v-text-field
                v-model="search"
                prepend-inner-icon="mdi-magnify"
                label="Search package or code "
                placeholder="Enter keywords to search..."
                variant="outlined"
                density="comfortable"
                clearable
                hide-details
                class="w-100"
            ></v-text-field>
          </div>
        </div>
      </v-card-title>

      <!-- 桌面端：数据表格 -->
      <v-data-table
          :headers="headers"
          :items="list"
          :search="search"
          item-key="id"
          class="d-none d-md-block"
          :items-per-page="10"
      >
        <template v-slot:item.actions="{ item }">
          <v-btn
              icon
              size="small"
              color="primary"
              @click="edit(item)"
          >
            <v-icon>mdi-pencil</v-icon>
          </v-btn>
          <v-btn
              icon
              size="small"
              color="error"
              @click="remove(item.id)"
          >
            <v-icon>mdi-delete</v-icon>
          </v-btn>
        </template>
      </v-data-table>

      <!-- 移动端和平板：卡片列表 -->
      <div class="d-md-none pa-2">
        <v-card
            v-for="item in filteredList"
            :key="item.id"
            class="mb-3"
            elevation="2"
        >
          <v-card-title class="text-subtitle-1 pb-1">
            {{ item.name }}
          </v-card-title>
          <v-card-subtitle class="pt-1">
            <div class="d-flex flex-column">
              <span class="text-body-2">Code: {{ item.code }}</span>
              <span class="text-body-2">Currency: {{ item.currency || 'PHP' }}</span>
              <span class="text-body-2">Discount: {{ item.discount || 0 }}%</span>
              <span class="text-body-2">Item count: {{ item.itemCount || 0 }}</span>
            </div>
          </v-card-subtitle>
          <v-card-actions class="pt-0">
            <v-spacer></v-spacer>
            <v-btn
                icon
                size="small"
                color="primary"
                @click="edit(item)"
            >
              <v-icon>mdi-pencil</v-icon>
            </v-btn>
            <v-btn
                icon
                size="small"
                color="error"
                @click="remove(item.id)"
            >
              <v-icon>mdi-delete</v-icon>
            </v-btn>
          </v-card-actions>
        </v-card>
      </div>
    </v-card>

    <!-- 套餐表单对话框 -->
    <v-dialog v-model="showDialog" :fullscreen="$vuetify.display.mobile" max-width="1100" scrollable>
      <v-card>
        <v-card-title class="text-subtitle-1 text-sm-h6">
          {{ form.id ? 'Edit Package' : 'Add Package' }}
        </v-card-title>
        <v-card-text class="pa-2 pa-sm-4">
          <v-form ref="formRef">
            <v-row dense>
              <v-col cols="12" sm="6">
                <v-text-field
                    v-model="form.code"
                    label="Package code"
                    required
                    density="compact"
                ></v-text-field>
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field
                    v-model="form.name"
                    label="Package Name"
                    required
                    density="compact"
                ></v-text-field>
              </v-col>
            </v-row>
            <v-row dense>
              <v-col cols="12" sm="4">
                <v-text-field
                    v-model="form.currency"
                    label="Currency"
                    hint="PHP"
                    density="compact"
                ></v-text-field>
              </v-col>
              <v-col cols="12" sm="4">
                <v-text-field
                    v-model.number="form.discount"
                    type="number"
                    label="Discount(%)"
                    step="0.01"
                    hint="Package overall discount"
                    density="compact"
                ></v-text-field>
              </v-col>
              <v-col cols="12" sm="4">
                <v-text-field
                    v-model.number="form.taxRate"
                    type="number"
                    label="Tax Rate(%)"
                    step="0.01"
                    hint="Default Tax rate"
                    density="compact"
                ></v-text-field>
              </v-col>
            </v-row>
            <v-row dense>
              <v-col cols="12">
                <v-textarea
                    v-model="form.description"
                    label="Description"
                    placeholder="Please enter description..."
                    rows="5"
                    variant="outlined"
                    density="comfortable"
                    auto-grow
                ></v-textarea>
              </v-col>
            </v-row>
            <!-- Product List Management -->
            <v-divider class="my-2"></v-divider>
            <div class="d-flex flex-column flex-sm-row justify-space-between align-start align-sm-center mb-2">
              <h3 class="text-subtitle-1 text-sm-h6 mb-2 mb-sm-0">Package Items</h3>
              <v-btn color="primary" size="small" @click="addPackageItem">
                <v-icon left size="small">mdi-plus</v-icon>
                <span class="d-none d-sm-inline">Add Item</span>
                <span class="d-sm-none">Add</span>
              </v-btn>
            </div>

            <!-- Desktop: Table -->
            <v-table v-if="form.items && form.items.length > 0" class="d-none d-md-block">
              <thead>
              <tr>
                <th>Product</th>
                <th>Quantity</th>
                <th>Unit Price</th>
                <th>Discount(%)</th>
                <th>Tax rate(%)</th>
                <th>Description</th>
                <th>Operate</th>
              </tr>
              </thead>
              <tbody>
              <tr v-for="(item, index) in form.items" :key="index">
                <td style="min-width: 200px;">
                  <v-select
                      size="mini"
                      v-model="item.productId"
                      :items="products"
                      item-title="name"
                      item-value="id"
                      variant="outlined"
                      density="comfortable"
                      hide-details
                      :menu-props="{ attach: 'body', maxHeight: 400 }"
                      @update:model-value="() => onProductSelect(item)"
                  ></v-select>
                </td>

                <td>
                  <v-text-field
                      size="mini"
                      v-model.number="item.quantity"
                      type="number"
                      density="compact"
                      hide-details
                      step="0.01"
                  ></v-text-field>
                </td>
                <td>
                  <v-text-field
                      size="small"
                      v-model.number="item.unitPrice"
                      type="number"
                      density="compact"
                      hide-details
                      step="0.01"
                  ></v-text-field>
                </td>
                <td>
                  <v-text-field
                      size="mini"
                      v-model.number="item.discount"
                      type="number"
                      density="compact"
                      hide-details
                      step="0.01"
                  ></v-text-field>
                </td>
                <td>
                  <v-text-field
                      size="mini"
                      v-model.number="item.taxRate"
                      type="number"
                      density="compact"
                      hide-details
                      step="0.01"
                  ></v-text-field>
                </td>
                <td>
                  <v-text-field
                      size="mini"
                      v-model="item.note"
                      density="compact"
                      hide-details
                  ></v-text-field>
                </td>
                <td>
                  <v-btn
                      icon
                      size="mini"
                      color="error"
                      @click="removePackageItem(index)"
                  >
                    <v-icon>mdi-delete</v-icon>
                  </v-btn>
                </td>
              </tr>
              </tbody>
            </v-table>

            <!-- 移动端和平板：卡片 -->
            <div v-if="form.items && form.items.length > 0" class="d-md-none">
              <v-card
                  v-for="(item, index) in form.items"
                  :key="index"
                  class="mb-2"
                  variant="outlined"
              >
                <v-card-title class="text-subtitle-2 py-1">
                  Product {{ index + 1 }}
                </v-card-title>
                <v-card-text class="py-2">
                  <v-select
                      v-model="item.productId"
                      :items="products"
                      item-title="name"
                      item-value="id"
                      label="Product"
                      density="compact"
                      @update:model-value="() => onProductSelect(item)"
                  ></v-select>
                  <v-row dense class="mt-1">
                    <v-col cols="6">
                      <v-text-field
                          v-model.number="item.quantity"
                          type="number"
                          label="Quantity"
                          density="compact"
                          step="0.01"
                      ></v-text-field>
                    </v-col>
                    <v-col cols="6">
                      <v-text-field
                          v-model.number="item.unitPrice"
                          type="number"
                          label="Unit Price"
                          density="compact"
                          step="0.01"
                      ></v-text-field>
                    </v-col>
                  </v-row>
                  <v-row dense>
                    <v-col cols="6">
                      <v-text-field
                          v-model.number="item.discount"
                          type="number"
                          label="Discount(%)"
                          density="compact"
                          step="0.01"
                      ></v-text-field>
                    </v-col>
                    <v-col cols="6">
                      <v-text-field
                          v-model.number="item.taxRate"
                          type="number"
                          label="Tax Rate(%)"
                          density="compact"
                          step="0.01"
                      ></v-text-field>
                    </v-col>
                  </v-row>
                  <v-text-field
                      v-model="item.note"
                      label="Remarks"
                      density="compact"
                  ></v-text-field>
                </v-card-text>
                <v-card-actions class="pt-0">
                  <v-spacer></v-spacer>
                  <v-btn
                      icon
                      size="small"
                      color="error"
                      @click="removePackageItem(index)"
                  >
                    <v-icon>mdi-delete</v-icon>
                  </v-btn>
                </v-card-actions>
              </v-card>
            </div>
            <v-alert v-else type="info" variant="tonal" class="mt-2">
              No items yet, please click "Add" button to add
            </v-alert>
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
  name: 'PackageList',
  data() {
    return {
      search: '',
      list: [],
      showDialog: false,
      form: {
        currency: 'PHP',
        enabled: true,
        items: []
      },
      products: [],
      headers: [
        {title: 'ID', key: 'id', sortable: true},
        {title: 'Code', key: 'code', sortable: true},
        {title: 'Name', key: 'name', sortable: true},
        {title: 'Currency', key: 'currency', sortable: true},
        {title: 'Discount(%)', key: 'discount', sortable: true},
        {title: 'ItemCount', key: 'itemCount', sortable: false},
        {title: 'Actions', key: 'actions', sortable: false, align: 'end'}
      ]
    }
  },
  computed: {
    filteredList() {
      if (!this.search) return this.list
      const searchLower = this.search.toLowerCase()
      return this.list.filter(item => {
        return (
            (item.code && item.code.toLowerCase().includes(searchLower)) ||
            (item.name && item.name.toLowerCase().includes(searchLower))
        )
      })
    }
  },
  mounted() {
    this.load()
    this.loadProducts()
  },
  methods: {
    async load() {
      try {
        const res = await api.get('/packages')
        // 加载每个套餐的产品数量
        this.list = await Promise.all(res.data.data.map(async (pkg) => {
          try {
            const itemsRes = await api.get(`/packages/${pkg.id}/items`)
            return {
              ...pkg,
              itemCount: itemsRes.data.data ? itemsRes.data.data.length : 0
            }
          } catch (e) {
            return {
              ...pkg,
              itemCount: 0
            }
          }
        }))
      } catch (error) {
        this.$snackbar.show('Loading failed', 'error')
      }
    },
    async loadProducts() {
      try {
        const res = await api.get('/products')
        this.products = res.data.data.filter(p => p.enabled !== false)
      } catch (error) {
        console.error('Loading products failed', error)
        this.$snackbar.show('Loading products failed', 'error')
      }
    },
    openDialog() {
      this.form = {
        currency: 'PHP',
        enabled: true,
        items: []
      }
      this.showDialog = true
    },
    edit(row) {
      // 加载完整的套餐信息（包含items）
      api.get(`/packages/${row.id}`).then(res => {
        this.form = {
          ...res.data.data,
          items: res.data.data.items || []
        }
        this.showDialog = true
      }).catch(error => {
        this.$snackbar.show('Loading package detail failed', 'error')
      })
    },
    closeDialog() {
      this.showDialog = false
      this.form = {
        currency: 'PHP',
        enabled: true,
        items: []
      }
    },
    addPackageItem() {
      if (!this.form.items) {
        this.form.items = []
      }
      this.form.items.push({
        productId: null,
        quantity: 1,
        unitPrice: 0,
        discount: 0,
        taxRate: this.form.taxRate || 12,
        note: ''
      })
    },
    removePackageItem(index) {
      this.form.items.splice(index, 1)
    },
    onProductSelect(item) {
      // 当选择产品时，自动填充产品信息
      const product = this.products.find(p => p.id === item.productId)
      if (product) {
        item.productName = product.name
        item.productSku = product.sku
        if (!item.unitPrice && product.defaultPrice) {
          item.unitPrice = product.defaultPrice
        }
        if (!item.taxRate && product.taxRate) {
          item.taxRate = product.taxRate
        }
      }
    },
    async save() {
      try {
        // 确保items数组存在
        if (!this.form.items) {
          this.form.items = []
        }

        if (this.form.id) {
          // 更新
          await api.put(`/packages/${this.form.id}`, this.form)
        } else {
          // 新增
          await api.post('/packages', this.form)
        }
        this.$snackbar.show('Add success', 'success')
        this.closeDialog()
        this.load()
      } catch (error) {
        this.$snackbar.show(error.response?.data?.msg || 'Add failed', 'error')
        console.error('Add failed', error)
      }
    },
    async remove(id) {
      if (confirm('Are you sure you want to delete this package? This will also remove all product associations under the package.')) {
        try {
          await api.delete(`/packages/${id}`)
          this.$snackbar.show('Package deleted successfully', 'success')
          await this.load()
        } catch (error) {
          this.$snackbar.show('Failed to delete package', 'error')
        }
      }
    }
  }
}
</script>
