<template>
  <div id="quotation-preview-area">
    <v-form ref="form" v-model="valid" lazy-validation>

      <div class="d-flex align-center mb-2 mb-sm-4">
        <v-btn
            icon
            variant="text"
            size="large"
            class="mr-3"
            @click="$router.go(-1)"
            color="grey-darken-2"
            aria-label="Back"
        >
          <v-icon>mdi-arrow-left</v-icon>
        </v-btn>
        <h2 class="text-h5 text-sm-h4 ma-0">
          {{ quotation.id ? 'Edit Quotation' : 'New Quotation' }}
        </h2>
      </div>

      <v-card class="mb-2 mb-sm-4">
        <v-card-text>
          <v-row>
            <v-col cols="12" md="6">
              <v-text-field
                  v-model="quotation.quoteNumber"
                  label="Quote Number"
                  disabled
                  density="compact"
                  class="mb-2 mb-sm-0"
              ></v-text-field>
            </v-col>
            <v-col cols="12" md="6">
              <v-text-field
                  v-model="quotation.title"
                  :rules="[rules.required]"
                  label="Title"
                  density="compact"
              ></v-text-field>
            </v-col>
          </v-row>
          <v-row>
            <v-col cols="12" md="2">
              <v-select
                  v-model="quotation.salutation"
                  :items="salutations"
                  label="Salutation"
                  density="compact"
              ></v-select>
            </v-col>
            <v-col cols="12" md="4">
              <v-autocomplete
                  v-model="quotation.customerId"
                  :items="customers"
                  item-title="customerName"
                  item-value="id"
                  label="Customer Name"
                  density="compact"
                  :rules="[rules.required]"
                  required
                  @update:model-value="onCustomerChange"
                  clearable
              >
                <template v-slot:item="{ props, item: customerItem }">
                  <v-list-item v-bind="props">
                    <template v-slot:title>
                      {{ customerItem.raw.companyName }}
                    </template>
                    <template v-slot:subtitle>
                      <span v-if="customerItem.raw.customerName">
                        Attn: {{ customerItem.raw.customerName }}
                      </span>
                      <span v-if="customerItem.raw.contactName">
                        | Contact: {{ customerItem.raw.contactName }}
                      </span>
                    </template>
                  </v-list-item>
                </template>
              </v-autocomplete>
            </v-col>
            <v-col cols="12" md="6">
              <v-text-field
                  v-model="quotation.currency"
                  label="Currency"
                  density="compact"
              ></v-text-field>
            </v-col>
          </v-row>
          <v-row>
            <v-col cols="12" md="6">
              <v-text-field
                  v-model="quotation.issueDate"
                  type="date"
                  label="Issue Date"
                  density="compact"
                  :rules="[rules.required]"
                  required
              ></v-text-field>
            </v-col>
            <v-col cols="12" md="6">
              <v-text-field
                  v-model="quotation.expiryDate"
                  type="date"
                  label="Expiry Date"
                  density="compact"
                  :rules="[rules.required]"
                  required
              ></v-text-field>
            </v-col>
          </v-row>
          <v-row>
            <v-col cols="12">
              <div class="d-flex align-center">
                <span class="mr-4 font-weight-medium">EWT (Withholding Tax):</span>
                <v-radio-group v-model.number="quotation.ewt" inline class="mt-0" @update:model-value="recalculateAll">
                  <v-radio label="None" :value="0"></v-radio>
                  <v-radio label="1% (Products)" :value="0.01"></v-radio>
                  <v-radio label="2% (Services)" :value="0.02"></v-radio>
                </v-radio-group>
              </div>
            </v-col>
          </v-row>
          <v-row>
            <v-col cols="12">
              <v-select
                  v-model="selectedPackageId"
                  :items="packages"
                  item-title="name"
                  item-value="id"
                  label="Load from Package"
                  placeholder="Select Package"
                  density="compact"
                  clearable
                  @update:model-value="loadFromPackage"
              ></v-select>
            </v-col>
          </v-row>
          <v-row>
            <v-col cols="12">
              <v-textarea
                  v-model="quotation.notes"
                  label="Notes"
                  placeholder="Please enter quotation notes..."
                  rows="5"
                  variant="outlined"
                  density="comfortable"
                  auto-grow
              ></v-textarea>
            </v-col>
          </v-row>
        </v-card-text>
      </v-card>

      <v-card class="mb-2 mb-sm-4">
        <v-card-title class="d-flex flex-column flex-sm-row justify-space-between align-start align-sm-center">
          <span class="text-subtitle-1 text-sm-h6 mb-2 mb-sm-0">Quotation Details</span>
          <v-btn color="primary" size="small" class="align-self-start align-self-sm-auto" @click="addItem">
            <v-icon left size="small">mdi-plus</v-icon>
            <span class="d-none d-sm-inline">Add Product</span>
            <span class="d-sm-none">Add</span>
          </v-btn>
        </v-card-title>

        <v-card-text class="pa-1 pa-sm-4">

          <v-data-table
              :headers="headers"
              :items="quotation.items"
              item-value="lineNumber"
              :items-per-page="-1"
              hide-default-footer
              class="d-none d-md-block elevation-1"
          >
            <template v-slot:item.lineNumber="{ index }">
              {{ index + 1 }}
            </template>

            <template v-slot:item.product="{ item, index }">
              <div class="product-name-wrapper">
                <v-autocomplete
                    v-model="item.productId"
                    :items="products"
                    item-title="name"
                    item-value="id"
                    density="compact"
                    hide-details="auto"
                    :rules="[rules.required]"
                    @update:model-value="() => onProductChange(item, index)"
                    :style="{ minWidth: '180px' }"
                    clearable
                    placeholder="Select Product"
                >
                  <template v-slot:item="{ props, item: productItem }">
                    <v-list-item v-bind="props">
                      <template v-slot:title>
                        {{ productItem.raw.name }}
                      </template>
                      <template v-slot:subtitle>
                        SKU: {{ productItem.raw.sku || 'N/A' }}
                      </template>
                    </v-list-item>
                  </template>
                </v-autocomplete>
                <p v-if="item.packageName || item.description" class="text-caption text-grey-darken-1 mt-1 mb-0 text-left">
                  <span v-if="item.packageName" class="text-primary mr-1 font-weight-bold">
                        [{{ item.packageName }}]
                  </span>
                  {{ item.description }}
                </p>
                <p v-if="item.specifications" class="text-caption text-info mt-1 mb-0 text-left">
                  <strong>Specs:</strong> {{ item.specifications }}
                </p>
              </div>
            </template>

            <template v-slot:item.quantity="{ item }">
              <v-text-field
                  v-model.number="item.quantity"
                  type="number"
                  density="compact"
                  hide-details="auto"
                  :rules="[rules.required, rules.positive]"
                  @update:model-value="calculateLine(item)"
              ></v-text-field>
            </template>

            <template v-slot:item.unitPrice="{ item }">
              <v-text-field
                  v-model.number="item.unitPrice"
                  type="number"
                  density="compact"
                  hide-details="auto"
                  :rules="[rules.required]"
                  @update:model-value="calculateLine(item)"
              ></v-text-field>
            </template>

            <template v-slot:item.discount="{ item }">
              <v-text-field
                  v-if="isSuperAdmin"
                  v-model.number="item.discount"
                  type="number"
                  density="compact"
                  hide-details="auto"
                  @update:model-value="calculateLine(item)"
              ></v-text-field>
              <span v-else class="text-body-2">{{ item.discount || 0 }}%</span>
            </template>

            <template v-slot:item.taxRate="{ item }">
              {{ (item.taxRate * 100).toFixed(0) || 0 }}%
            </template>

            <template v-slot:item.lineTax="{ item }">
              {{ item.lineTax || '0.00' }}
            </template>

            <template v-slot:item.lineTotal="{ item }">
              {{ item.lineTotal }}
            </template>

            <template v-slot:item.actions="{ index }">
              <v-btn icon size="small" color="error" @click="removeItem(index)">
                <v-icon>mdi-delete</v-icon>
              </v-btn>
            </template>
          </v-data-table>

          <div class="d-md-none">
            <v-list density="compact" class="pa-0">
              <v-list-item v-for="(item, index) in quotation.items" :key="index" class="mb-3 pa-0 mobile-item-card">
                <v-card variant="outlined">
                  <v-card-text>
                    <div class="d-flex justify-space-between align-center mb-2">
                      <span class="text-subtitle-2 font-weight-bold">#{{ index + 1 }} {{ item.productName || 'Select Product' }}</span>
                      <v-btn icon size="x-small" color="error" @click="removeItem(index)">
                        <v-icon size="small">mdi-delete</v-icon>
                      </v-btn>
                    </div>

                    <p v-if="item.packageName || item.description" class="text-caption text-grey-darken-1 mb-2">
                      <span v-if="item.packageName" class="text-primary mr-1 font-weight-bold">
                            [{{ item.packageName }}]
                      </span>
                      {{ item.description }}
                    </p>

                    <p v-if="item.specifications" class="text-caption text-info mb-2">
                      <strong>Specs:</strong> {{ item.specifications }}
                    </p>

                    <v-row dense>
                      <v-col cols="12">
                        <v-autocomplete
                            v-model="item.productId"
                            :items="products"
                            item-title="name"
                            item-value="id"
                            label="Product"
                            density="compact"
                            hide-details="auto"
                            :rules="[rules.required]"
                            @update:model-value="() => onProductChange(item, index)"
                            clearable
                        >
                          <template v-slot:item="{ props, item: productItem }">
                            <v-list-item v-bind="props">
                              <template v-slot:title>
                                {{ productItem.raw.name }}
                              </template>
                              <template v-slot:subtitle>
                                SKU: {{ productItem.raw.sku || 'N/A' }}
                              </template>
                            </v-list-item>
                          </template>
                        </v-autocomplete>
                      </v-col>
                      <v-col cols="6">
                        <v-text-field
                            v-model.number="item.quantity"
                            type="number"
                            label="Quantity"
                            density="compact"
                            hide-details="auto"
                            :rules="[rules.required, rules.positive]"
                            @update:model-value="calculateLine(item)"
                        ></v-text-field>
                      </v-col>
                      <v-col cols="6">
                        <v-text-field
                            v-model.number="item.unitPrice"
                            type="number"
                            label="Unit Price"
                            density="compact"
                            :disabled="!isSuperAdmin"
                            hide-details="auto"
                            :rules="[rules.required]"
                            @update:model-value="calculateLine(item)"
                        ></v-text-field>
                      </v-col>
                      <v-col cols="6" v-if="isSuperAdmin">
                        <v-text-field v-model.number="item.discount" type="number" label="Discount (%)" density="compact" hide-details="auto" @update:model-value="calculateLine(item)"></v-text-field>
                      </v-col>
                      <v-col cols="6" v-else>
                        <v-text-field :model-value="item.discount || 0" label="Discount (%)" density="compact" hide-details readonly></v-text-field>
                      </v-col>
                      <v-col cols="6">
                        <v-text-field :model-value="(item.taxRate * 100).toFixed(0) || 0" label="Tax Rate (%)" density="compact" hide-details readonly></v-text-field>
                      </v-col>
                      <v-col cols="6">
                        <v-text-field :model-value="item.lineTax || '0.00'" label="Tax Amount" density="compact" hide-details readonly></v-text-field>
                      </v-col>
                    </v-row>

                    <div class="text-right mt-3 pt-2 border-top">
                      <span class="font-weight-bold text-subtitle-1">Subtotal (Net): {{ quotation.currency }} {{ item.lineTotal }}</span>
                    </div>
                  </v-card-text>
                </v-card>
              </v-list-item>
            </v-list>
            <div v-if="quotation.items.length === 0" class="text-center text-grey py-4">
              No quotation details yet. Please click "Add Product".
            </div>
          </div>

        </v-card-text>
      </v-card>

      <v-card class="mb-2 mb-sm-4">
        <v-card-text class="pa-2 pa-sm-4 text-right">
          <h3>Subtotal (Net): {{ quotation.currency }} {{ quotation.subtotal }}</h3>
          <h3 v-if="quotation.discountAmount > 0">Discount: -{{ quotation.currency }} {{ quotation.discountAmount }}</h3>
          <h3 v-if="quotation.ewtAmount > 0">EWT: -{{ quotation.currency }} {{ quotation.ewtAmount }}</h3>
          <h3>Tax Amount: +{{ quotation.currency }} {{ quotation.taxAmount }}</h3>
          <v-divider class="my-2"></v-divider>
          <h2>Total (Gross): {{ quotation.currency }} {{ quotation.total }}</h2>
        </v-card-text>
      </v-card>

      <div class="d-flex flex-column flex-md-row justify-end gap-2">
        <v-btn color="primary" v-if="quotation.status==='DRAFT'" @click="save">Save</v-btn>
      </div>

    </v-form> </div>
</template>

<script>
import api from '../api.js'
import jsPDF from 'jspdf'
import html2canvas from 'html2canvas'

export default {
  name: 'QuotationForm',
  // ===== 常量定义 =====
  CONSTANTS: {
    MIN_QUANTITY: 0.01,
    MAX_DISCOUNT: 100,
    MIN_DISCOUNT: 0,
    MAX_TAX_RATE: 1,
    MIN_TAX_RATE: 0,
    DECIMAL_PLACES: 2,
    DEBOUNCE_WAIT: 300,
    TIMEOUT_MS: 10000
  },
  data() {
    return {
      valid: false,
      isLoading: false, // 添加加载状态
      rules: {
        required: value => !!value || 'Required.',
        positive: value => (value && Number(value) > 0) || 'Must be > 0.',
        validDiscount: value => {
          const num = Number(value)
          return (num >= 0 && num <= 100) || 'Discount must be between 0 and 100'
        },
        validQuantity: value => {
          const num = Number(value)
          return (num > 0) || 'Quantity must be greater than 0'
        },
        validPrice: value => {
          const num = Number(value)
          return (num >= 0) || 'Price cannot be negative'
        }
      },
      salutations: ['Mr.', 'Mrs.', 'Ms.', 'Dr.', 'Prof.'],
      isSuperAdmin: false,
      headers: [
        { title: 'No.', key: 'lineNumber', sortable: false, width: '50px' },
        { title: 'Product', key: 'product', sortable: false, width: '250px' },
        { title: 'Qty', key: 'quantity', sortable: false, width: '80px' },
        { title: 'Unit Price', key: 'unitPrice', sortable: false, width: '100px' },
        { title: 'Disc (%)', key: 'discount', sortable: false, width: '80px' },
        { title: 'Tax Rate', key: 'taxRate', sortable: false, width: '80px' },
        { title: 'Tax Amount', key: 'lineTax', sortable: false, width: '100px' },
        { title: 'Total (Net)', key: 'lineTotal', sortable: false, width: '100px' },
        { title: 'Actions', key: 'actions', sortable: false, width: '80px' },
      ],
      quotation: {
        id: null,
        quoteNumber: '',
        title: '',
        items: [],
        currency: 'PHP',
        status: 'DRAFT',
        subtotal: 0,
        discountAmount: 0,
        ewtAmount: 0,
        taxAmount: 0,
        total: 0,
        operator: null,
        salutation: 'Mr.',
        customerId: null,
        issueDate: this.getTodayDate(),
        expiryDate: this.getDateAfterDays(7),
        ewt: 0,
        notes: '',
        bankAccounts: [
          {
            payee: 'ZUNO GROUP INC.',
            bankName: 'AUB (Asia United Bank)',
            accountNumber: '013-01-008037-5'
          }
        ]
      },
      customers: [],
      products: [],
      packages: [],
      selectedPackageId: null
    }
  },
  mounted() {
    this.loadInitialData()
    if (this.$route.params.id) this.loadQuotation(this.$route.params.id)
  },
  methods: {
    // ===== 工具函数 =====
    getTodayDate() {
      const today = new Date()
      return today.toISOString().split('T')[0]
    },
    getDateAfterDays(days) {
      const date = new Date()
      date.setDate(date.getDate() + days)
      return date.toISOString().split('T')[0]
    },
    /**
     * 精确的浮点数舍入
     */
    roundNumber(num, decimals = 2) {
      if (typeof num !== 'number' || isNaN(num)) return 0
      return Math.round(num * Math.pow(10, decimals)) / Math.pow(10, decimals)
    },
    /**
     * 验证数字范围
     */
    validateNumberRange(value, min, max, fieldName = 'Value') {
      const num = Number(value)
      if (isNaN(num)) return `${fieldName} must be a number`
      if (num < min) return `${fieldName} cannot be less than ${min}`
      if (num > max) return `${fieldName} cannot be greater than ${max}`
      return null
    },
    /**
     * 安全获取嵌套对象属性
     */
    safeGet(obj, path, defaultValue = null) {
      try {
        return path.split('.').reduce((acc, part) => acc?.[part], obj) ?? defaultValue
      } catch (e) {
        return defaultValue
      }
    },
    /**
     * 加载初始数据
     */
    async loadInitialData() {
      try {
        this.isLoading = true
        await Promise.all([
          this.loadCustomers(),
          this.loadProducts(),
          this.loadPackages(),
          this.me()
        ])
      } catch (error) {
        console.error('Failed to load initial data:', error)
        this.$snackbar?.show('Failed to load some data. Please refresh.', 'error')
      } finally {
        this.isLoading = false
      }
    },
    async previewPdf() {
      // PDF Preview Logic (不变)
      const element = document.getElementById('quotation-preview-area')
      if (!element) return alert('Content not found')

      const actionsDiv = element.querySelector('.d-flex.flex-column.flex-md-row.justify-end.gap-2');
      let originalActionsDisplay = '';
      if (actionsDiv) {
        originalActionsDisplay = actionsDiv.style.display;
        actionsDiv.style.display = 'none';
      }

      const mobileListDiv = element.querySelector('.d-md-none');
      let originalMobileDisplay = '';
      if (mobileListDiv) {
        originalMobileDisplay = mobileListDiv.style.display;
        mobileListDiv.style.display = 'none';
      }

      try {
        const canvas = await html2canvas(element, { scale: 2, useCORS: true })
        const imgData = canvas.toDataURL('image/png')
        const pdf = new jsPDF('p', 'mm', 'a4')

        const pageWidth = pdf.internal.pageSize.getWidth()
        const pageHeight = pdf.internal.pageSize.getHeight()
        const imgWidth = pageWidth
        const imgHeight = (canvas.height * pageWidth) / canvas.width

        let heightLeft = imgHeight
        let position = 0

        pdf.addImage(imgData, 'PNG', 0, position, imgWidth, imgHeight)
        heightLeft -= pageHeight

        while (heightLeft > 0) {
          position = heightLeft - imgHeight
          pdf.addPage()
          pdf.addImage(imgData, 'PNG', 0, position, imgWidth, imgHeight)
          heightLeft -= pageHeight
        }

        const pdfBlob = pdf.output('blob')
        const pdfUrl = URL.createObjectURL(pdfBlob)
        window.open(pdfUrl, '_blank')
      } catch (e) {
        console.error(e)
        alert('Failed to generate PDF')
      } finally {
        if (actionsDiv) {
          actionsDiv.style.display = originalActionsDisplay;
        }
        if (mobileListDiv) {
          mobileListDiv.style.display = originalMobileDisplay;
        }
      }
    },

    async loadCustomers() {
      try {
        const res = await api.get('/customers')
        this.customers = Array.isArray(res.data?.data) ? res.data.data : []
      } catch (error) {
        console.error('Failed to load customers:', error)
        this.customers = []
        this.$snackbar?.show('Failed to load customers', 'error')
      }
    },

    async loadProducts() {
      try {
        const res = await api.get('/products')
        const data = Array.isArray(res.data?.data) ? res.data.data : []
        // 过滤并规范化数据
        this.products = data
          .filter(p => p.enabled !== false && p.id)
          .map(p => ({
            ...p,
            taxRate: this.roundNumber(parseFloat(p.taxRate) || 0),
            defaultPrice: this.roundNumber(parseFloat(p.defaultPrice) || 0)
          }))
      } catch (error) {
        console.error('Failed to load products:', error)
        this.products = []
        this.$snackbar?.show('Failed to load products', 'error')
      }
    },

    async me() {
      try {
        const res = await api.get('/auth/me')
        this.quotation.operator = res.data.data?.username || res.data.data?.fullName || 'Unknown'
        this.isSuperAdmin = res.data.data?.role === 'ADMIN'
      } catch (error) {
        console.error('Failed to fetch user info:', error)
        this.quotation.operator = 'Unknown'
        this.isSuperAdmin = false
      }
    },

    async loadPackages() {
      try {
        const res = await api.get('/packages')
        this.packages = Array.isArray(res.data?.data) 
          ? res.data.data.filter(p => p.enabled !== false && p.id)
          : []
      } catch (error) {
        console.error('Failed to load packages:', error)
        this.packages = []
        this.$snackbar?.show('Failed to load packages', 'error')
      }
    },

    async loadQuotation(id) {
      if (!id) return
      try {
        this.isLoading = true
        const res = await api.get(`/quotations/${id}`)
        const data = res.data?.data
        
        if (!data?.quotation) {
          throw new Error('Invalid quotation data')
        }

        this.quotation = data.quotation
        this.quotation.items = Array.isArray(data.items) ? data.items : []

        // 规范化并验证每个item
        this.quotation.items.forEach((item, idx) => {
          if (!item.productId) return
          
          const productInfo = this.products.find(p => p.id === item.productId)
          item.taxRate = this.roundNumber(parseFloat(item.taxRate) ?? (productInfo?.taxRate || 0))
          item.quantity = this.roundNumber(parseFloat(item.quantity) || 0, 4)
          item.unitPrice = this.roundNumber(parseFloat(item.unitPrice) || 0)
          item.discount = this.roundNumber(Math.min(Math.max(parseFloat(item.discount) || 0, 0), 100))
          item.lineNumber = idx + 1
          
          this.calculateLine(item)
        })

        this.calculateTotal()
      } catch (error) {
        console.error('Failed to load quotation:', error)
        this.$snackbar?.show('Failed to load quotation', 'error')
        // 重定向回列表页面
        setTimeout(() => this.$router.push('/quotations'), 1500)
      } finally {
        this.isLoading = false
      }
    },

    async loadFromPackage(packageId) {
      if (!packageId) return

      try {
        this.isLoading = true
        const res = await api.get(`/packages/${packageId}`)
        const pkg = res.data?.data

        if (!pkg?.items || !Array.isArray(pkg.items) || pkg.items.length === 0) {
          this.$snackbar?.show('This package has no detail items.', 'warning')
          this.selectedPackageId = null
          return
        }

        this.quotation.items = []
        this.quotation.title = pkg.name || 'Untitled'
        this.quotation.currency = pkg.currency || 'PHP'
        this.quotation.packageId = packageId

        const currentPackageName = pkg.name || ''

        pkg.items.forEach((pkgItem, index) => {
          if (!pkgItem.productId) return // 跳过无效项
          
          const productInfo = this.products.find(p => p.id === pkgItem.productId)
          if (!productInfo) {
            console.warn(`Product ${pkgItem.productId} not found in loaded products`)
            return
          }

          const unitPrice = this.roundNumber(pkgItem.unitPrice ?? productInfo.defaultPrice)
          const taxRate = this.roundNumber(pkgItem.taxRate ?? productInfo.taxRate)
          const quantity = this.roundNumber(Math.max(parseFloat(pkgItem.quantity) || 1, 0.01), 4)
          const discount = this.roundNumber(Math.min(Math.max(parseFloat(pkgItem.discount) || 0, 0), 100))

          const newItem = {
            lineNumber: index + 1,
            productId: pkgItem.productId,
            productName: productInfo.name || 'Unknown',
            description: pkgItem.description || productInfo.description || '',
            specifications: productInfo.specifications || '',
            packageName: currentPackageName,
            packageId: packageId,
            quantity: quantity,
            unitPrice: unitPrice,
            discount: discount,
            taxRate: taxRate,
            lineTax: 0,
            lineTotal: 0,
            isFromPackage: true
          }

          this.quotation.items.push(newItem)
          this.calculateLine(newItem)
        })

        this.calculateTotal()
        this.selectedPackageId = null
        this.$snackbar?.show('Package loaded successfully', 'success')
      } catch (error) {
        console.error('Failed to load package:', error)
        this.$snackbar?.show('Failed to load package information.', 'error')
      } finally {
        this.isLoading = false
      }
    },

    onCustomerChange(customerId) {
      if (!customerId) return
      
      const customer = this.customers.find(x => x?.id === customerId)
      if (!customer) {
        console.warn(`Customer ${customerId} not found`)
        return
      }
      
      this.quotation.customerId = customerId
      this.quotation.customerName = customer.customerName || ''
      this.quotation.customerFullName = customer.customerName || ''
      this.quotation.salutation = customer.salutation || 'Mr.'
      this.quotation.title = customer.companyName || 'Unknown'
    },

    onProductChange(row) {
      if (!row?.productId) {
        // 清空产品信息
        row.productName = ''
        row.unitPrice = 0
        row.description = ''
        row.specifications = ''
        row.taxRate = 0
        row.isFromPackage = false
        row.packageName = null
        this.calculateLine(row)
        return
      }
      
      const product = this.products.find(x => x?.id === row.productId)
      if (!product) {
        console.warn(`Product ${row.productId} not found`)
        row.taxRate = 0
        this.calculateLine(row)
        return
      }
      
      // 更新产品信息
      row.productName = product.name || ''
      row.unitPrice = this.roundNumber(product.defaultPrice || 0)
      row.description = product.description || ''
      row.specifications = product.specifications || ''
      row.taxRate = this.roundNumber(product.taxRate || 0)
      row.isFromPackage = false
      row.packageName = null
      
      this.calculateLine(row)
    },

    addItem() {
      const newItem = {
        lineNumber: this.quotation.items.length + 1,
        productId: null,
        productName: '',
        quantity: 1,
        unitPrice: 0,
        discount: 0,
        taxRate: 0,
        lineTax: 0,
        lineEWT: 0,
        lineTotal: 0,
        description: '',
        specifications: '',
        isFromPackage: false,
        packageName: null
      }
      this.quotation.items.push(newItem)
      this.calculateTotal()
    },

    removeItem(i) {
      this.quotation.items.splice(i, 1)
      this.quotation.items.forEach((item, index) => {
        item.lineNumber = index + 1
      })
      this.calculateTotal()
    },

    /**
     * 重新计算所有行（当报价单级别的设置改变时调用）
     */
    recalculateAll() {
      if (!Array.isArray(this.quotation.items)) return
      this.quotation.items.forEach(item => {
        if (item) this.calculateLine(item)
      })
      this.calculateTotal()
    },

    /**
     * 计算报价明细行的金额 (价外税+EWT)
     * 计算流程：
     *   1. 反算不含税单价（如果有税）
     *   2. 计算行不含税总额 = Qty × 不含税单价
     *   3. 应用折扣
     *   4. 计算EWT（基于折扣后不含税价）
     *   5. 计算税额（基于折扣后不含税价）
     *   6. lineTotal = 折扣后不含税价 - EWT（供汇总用）
     *   7. lineTax = 税额（独立显示和计算）
     */
    calculateLine(row) {
      if (!row) return
      
      try {
        // 1. 安全转换和验证数值
        let q = parseFloat(row.quantity) || 0
        let p = parseFloat(row.unitPrice) || 0
        let d = this.isSuperAdmin ? (parseFloat(row.discount) || 0) : 0
        let ewt = Math.min(Math.max(parseFloat(this.quotation.ewt) || 0, 0), 1)
        let taxRate = parseFloat(row.taxRate) || 0

        // 2. 边界检查和纠正
        q = Math.max(q, 0)
        p = Math.max(p, 0)
        d = Math.min(Math.max(d, 0), 100)
        taxRate = Math.min(Math.max(taxRate, 0), 1)

        // 3. 关键：根据税率判断 unitPrice 的含义
        let netUnitPrice = p
        
        if (taxRate > 0) {
          // unitPrice 是含税价，反算不含税单价
          netUnitPrice = this.roundNumber(p / (1 + taxRate))
        }
        // taxRate = 0 时，unitPrice 已是不含税价

        // 4. 计算不含税行总额
        const rawNetLineTotal = this.roundNumber(q * netUnitPrice)
        
        // 5. 应用行折扣
        const lineDiscount = this.roundNumber(rawNetLineTotal * (d / 100))
        const netLineTotalAfterDiscount = this.roundNumber(rawNetLineTotal - lineDiscount)
        
        // 6. 计算EWT（基于折扣后不含税价）
        const ewtAmount = this.roundNumber(netLineTotalAfterDiscount * ewt)
        
        // 7. 计算税额（基于折扣后不含税价）
        const lineTax = this.roundNumber(netLineTotalAfterDiscount * taxRate)
        
        // 8. lineTotal用于汇总时的净价（减EWT后）
        const lineTotal = this.roundNumber(netLineTotalAfterDiscount - ewtAmount)

        // 9. 更新数据
        row.quantity = this.roundNumber(q, 4)
        row.unitPrice = this.roundNumber(p)
        row.discount = this.roundNumber(d)
        row.taxRate = this.roundNumber(taxRate)
        row.lineTotal = lineTotal.toFixed(2)
        row.lineTax = lineTax.toFixed(2)
        row.lineEWT = ewtAmount.toFixed(2)
      } catch (error) {
        console.error('Error calculating line:', error, row)
        row.lineTotal = '0.00'
        row.lineTax = '0.00'
      }

      this.calculateTotal()
    },

    /**
     * 计算报价单总价（包含EWT计算）
     * subtotal = 所有行EWT后的价格之和
     * taxAmount = 所有行税额之和（仅用于显示）
     * total = subtotal（不包含税额）
     */
    calculateTotal() {
      try {
        let subtotal = 0
        let discountAmount = 0
        let ewtAmount = 0
        let totalTaxAmount = 0

        // 遍历并累计
        if (Array.isArray(this.quotation.items)) {
          this.quotation.items.forEach(i => {
            if (!i) return
            
            const q = Math.max(parseFloat(i.quantity) || 0, 0)
            const p = Math.max(parseFloat(i.unitPrice) || 0, 0)
            const d = Math.min(Math.max(parseFloat(i.discount) || 0, 0), 100)
            const t = Math.min(Math.max(parseFloat(i.taxRate) || 0, 0), 1)
            const ewt = Math.min(Math.max(parseFloat(this.quotation.ewt) || 0, 0), 1)

            // 根据税率判断unitPrice含义：反算不含税单价
            let netUnitPrice = p
            if (t > 0) {
              netUnitPrice = this.roundNumber(p / (1 + t))
            }

            // 计算各环节金额
            const rawNetLineTotal = this.roundNumber(q * netUnitPrice)
            const lineDiscount = this.roundNumber(rawNetLineTotal * (d / 100))
            const netLineTotalAfterDiscount = this.roundNumber(rawNetLineTotal - lineDiscount)
            const lineEWT = this.roundNumber(netLineTotalAfterDiscount * ewt)
            const lineTax = this.roundNumber(netLineTotalAfterDiscount * t)

            subtotal += netLineTotalAfterDiscount
            discountAmount += lineDiscount
            ewtAmount += lineEWT
            totalTaxAmount += lineTax
          })
        }

        // 精确舍入
        subtotal = this.roundNumber(subtotal)
        discountAmount = this.roundNumber(discountAmount)
        ewtAmount = this.roundNumber(ewtAmount)
        totalTaxAmount = this.roundNumber(totalTaxAmount)
        
        // 最终应付 = 不含税净价 - EWT + 税额
        const finalTotal = this.roundNumber(subtotal - ewtAmount + totalTaxAmount)

        // 更新
        this.quotation.subtotal = subtotal.toFixed(2)
        this.quotation.discountAmount = discountAmount.toFixed(2)
        this.quotation.ewtAmount = ewtAmount.toFixed(2)
        this.quotation.taxAmount = totalTaxAmount.toFixed(2)
        this.quotation.total = finalTotal.toFixed(2)
      } catch (error) {
        console.error('Error calculating total:', error)
        this.quotation.subtotal = '0.00'
        this.quotation.discountAmount = '0.00'
        this.quotation.ewtAmount = '0.00'
        this.quotation.taxAmount = '0.00'
        this.quotation.total = '0.00'
      }
    },

    async save() {
      try {
        // 1. 表单验证
        const { valid } = await this.$refs.form.validate()
        if (!valid) {
          this.$snackbar?.show('Please check the fields marked in red.', 'error')
          return
        }

        // 2. 业务逻辑验证
        const validationError = this.validateQuotation()
        if (validationError) {
          this.$snackbar?.show(validationError, 'error')
          return
        }

        // 3. 准备数据
        const dataToSave = JSON.parse(JSON.stringify(this.quotation))
        dataToSave.items = (dataToSave.items || []).map(item => {
          if (!item.productId) {
            throw new Error('All items must have a product selected')
          }
          return {
            ...item,
            quantity: this.roundNumber(parseFloat(item.quantity) || 0, 4),
            unitPrice: this.roundNumber(parseFloat(item.unitPrice) || 0),
            discount: this.isSuperAdmin ? this.roundNumber(Math.min(Math.max(parseFloat(item.discount) || 0, 0), 100)) : 0,
            taxRate: this.roundNumber(parseFloat(item.taxRate) || 0),
            lineTax: this.roundNumber(parseFloat(item.lineTax) || 0),
            lineEWT: this.roundNumber(parseFloat(item.lineEWT) || 0),
            lineTotal: this.roundNumber(parseFloat(item.lineTotal) || 0)
          }
        })

        // 4. 保存
        this.isLoading = true
        const res = await api.post('/quotations', dataToSave)
        
        if (res.data?.code === 200 || res.status === 200) {
          this.$snackbar?.show('Saved successfully', 'success')
          setTimeout(() => this.$router.push('/quotations'), 500)
        } else {
          throw new Error(res.data?.msg || 'Save failed')
        }
      } catch (error) {
        console.error('Failed to save quotation:', error)
        const errorMsg = error.message || 'Failed to save quotation, please check the data.'
        this.$snackbar?.show(errorMsg, 'error')
      } finally {
        this.isLoading = false
      }
    },
    
    /**
     * 验证整个报价单的有效性
     */
    validateQuotation() {
      const q = this.quotation
      
      // 检查必填字段
      if (!q.title) return 'Quotation title is required'
      if (!q.customerId) return 'Customer is required'
      if (!q.issueDate) return 'Issue date is required'
      if (!q.expiryDate) return 'Expiry date is required'
      if (!q.items || q.items.length === 0) return 'Please add at least one quotation item'
      
      // 检查日期逻辑
      if (new Date(q.issueDate) > new Date(q.expiryDate)) {
        return 'Expiry date must be after issue date'
      }
      
      // 检查item有效性
      for (let i = 0; i < q.items.length; i++) {
        const item = q.items[i]
        if (!item.productId) return `Item ${i + 1}: Product is required`
        if (!item.quantity || parseFloat(item.quantity) <= 0) return `Item ${i + 1}: Quantity must be > 0`
        if (parseFloat(item.unitPrice) < 0) return `Item ${i + 1}: Unit price cannot be negative`
      }
      
      return null
    },
  }
}
</script>

<style scoped>
.gap-2 {
  gap: 8px;
}

.product-name-wrapper {
  text-align: left;
}

:deep(.v-data-table-header th) {
  font-size: 0.8rem !important;
  font-weight: bold !important;
  color: #333 !important;
  padding: 8px 4px !important;
}

:deep(.v-data-table-row td) {
  padding: 4px 4px !important;
  vertical-align: top;
}

.mobile-item-card {
  border-bottom: 1px solid #eee;
  padding-bottom: 12px;
}

.border-top {
  border-top: 1px solid #eee;
}
</style>
