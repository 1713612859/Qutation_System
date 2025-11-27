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
                            hide-details="auto"
                            :rules="[rules.required]"
                            @update:model-value="calculateLine(item)"
                        ></v-text-field>
                      </v-col>
                      <v-col cols="12" v-if="isSuperAdmin">
                        <v-text-field v-model.number="item.discount" type="number" label="Discount (%)" density="compact" hide-details="auto" @update:model-value="calculateLine(item)"></v-text-field>
                      </v-col>
                      <v-col cols="12" v-else>
                        <v-text-field :model-value="item.discount || 0" label="Discount (%)" density="compact" hide-details readonly></v-text-field>
                      </v-col>
                    </v-row>

                    <div class="text-right mt-3 pt-2 border-top">
                      <span class="font-weight-bold text-subtitle-1">Subtotal: {{ quotation.currency }} {{ item.lineTotal }}</span>
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
          <h3>Subtotal: {{ quotation.currency }} {{ quotation.subtotal }}</h3>
          <h3 v-if="quotation.discountAmount > 0">Discount: -{{ quotation.currency }} {{ quotation.discountAmount }}</h3>
          <h2>Total: {{ quotation.currency }} {{ quotation.total }}</h2>
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
  data() {
    return {
      valid: false, // 表单校验状态
      // 验证规则
      rules: {
        required: value => !!value || 'Required.',
        positive: value => (value && value > 0) || 'Must be > 0.'
      },
      salutations: ['Mr.', 'Mrs.', 'Ms.', 'Dr.', 'Prof.'],
      isSuperAdmin: false,
      headers: [
        { title: 'No.', key: 'lineNumber', sortable: false, width: '50px' },
        { title: 'Product', key: 'product', sortable: false, width: '250px' },
        { title: 'Qty', key: 'quantity', sortable: false, width: '80px' },
        { title: 'Unit Price', key: 'unitPrice', sortable: false, width: '100px' },
        { title: 'Disc (%)', key: 'discount', sortable: false, width: '80px' },
        { title: 'Total', key: 'lineTotal', sortable: false, width: '100px' },
        { title: 'Actions', key: 'actions', sortable: false, width: '80px' },
      ],
      quotation: {
        items: [],
        currency: 'PHP',
        status: 'DRAFT',
        subtotal: 0,
        discountAmount: 0,
        total: 0,
        operator: null,
        salutation: 'Mr.'
      },
      customers: [],
      products: [],
      packages: [],
      selectedPackageId: null
    }
  },
  mounted() {
    this.loadCustomers()
    this.loadProducts()
    this.loadPackages()
    this.me()
    if (this.$route.params.id) this.loadQuotation(this.$route.params.id)
  },
  methods: {
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
        this.customers = res.data.data || []
      } catch (error) {
        console.error('Failed to load customers:', error)
        this.$snackbar?.show('Failed to load customers', 'error')
      }
    },

    async loadProducts() {
      try {
        const res = await api.get('/products')
        this.products = res.data.data.filter(p => p.enabled !== false)
      } catch (error) {
        console.error('Failed to load products:', error)
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
        this.packages = res.data.data.filter(p => p.enabled !== false)
      } catch (error) {
        console.error('Failed to load packages:', error)
        this.$snackbar?.show('Failed to load packages', 'error')
      }
    },

    async loadQuotation(id) {
      try {
        const res = await api.get(`/quotations/${id}`)
        this.quotation = res.data.data.quotation
        this.quotation.items = res.data.data.items || []
        this.calculateTotal()
      } catch (error) {
        console.error('Failed to load quotation:', error)
        this.$snackbar?.show('Failed to load quotation', 'error')
      }
    },

    async loadFromPackage(packageId) {
      if (!packageId) return

      try {
        const res = await api.get(`/packages/${packageId}`)
        const pkg = res.data.data

        if (pkg && pkg.items && pkg.items.length > 0) {
          this.quotation.items = []
          this.quotation.title = pkg.name
          this.quotation.currency = pkg.currency || 'PHP'
          this.quotation.packageId = packageId

          const currentPackageName = pkg.name

          pkg.items.forEach((pkgItem, index) => {
            const productInfo = this.products.find(p => p.id === pkgItem.productId)

            const unitPrice = pkgItem.unitPrice !== undefined ? pkgItem.unitPrice : (productInfo ? productInfo.defaultPrice : 0)

            const newItem = {
              lineNumber: index + 1,
              productId: pkgItem.productId,
              productName: productInfo ? productInfo.name : 'Unknown Product',
              description: pkgItem.description || (productInfo ? productInfo.description : ''),
              specifications: productInfo ? productInfo.specifications : '',
              packageName: currentPackageName,
              packageId: packageId,
              quantity: pkgItem.quantity || 1,
              unitPrice: unitPrice,
              discount: pkgItem.discount || 0,
              lineTotal: 0,
              isFromPackage: true
            }

            this.quotation.items.push(newItem)
            this.calculateLine(newItem)
          })

          this.calculateTotal()
          this.selectedPackageId = null

        } else {
          this.$snackbar?.show('This package has no detail items.', 'warning')
        }
      } catch (e) {
        console.error('Failed to load package:', e)
        this.$snackbar?.show('Failed to load package information.', 'error')
      }
    },

    onCustomerChange(customerId) {
      const c = this.customers.find(x => x.id === customerId)
      if (c) {
        this.quotation.customerName = c.customerName
        this.quotation.customerFullName = c.customerName
        this.quotation.salutation = c.salutation || 'Mr.'
        this.quotation.title = c.companyName || 'Unknown'
      }
    },

    onProductChange(row) {
      const p = this.products.find(x => x.id === row.productId)
      if (p) {
        row.productName = p.name
        row.unitPrice = p.defaultPrice
        row.description = p.description || ''
        row.specifications = p.specifications || ''
        row.isFromPackage = false
        row.packageName = null
      }
      this.calculateLine(row)
    },

    addItem() {
      this.quotation.items.push({
        lineNumber: this.quotation.items.length + 1,
        quantity: 1,
        unitPrice: 0,
        discount: 0,
        lineTotal: 0,
        description: '',
        specifications: '',
        isFromPackage: false,
        packageName: null
      })
      this.calculateTotal()
    },

    removeItem(i) {
      this.quotation.items.splice(i, 1)
      this.quotation.items.forEach((item, index) => {
        item.lineNumber = index + 1
      })
      this.calculateTotal()
    },

    calculateLine(row) {
      const q = parseFloat(row.quantity) || 0
      const p = parseFloat(row.unitPrice) || 0
      const d = this.isSuperAdmin ? (parseFloat(row.discount) || 0) : 0

      const originalPrice = q * p
      const lineDiscount = originalPrice * (d / 100)
      const lineNetPrice = originalPrice - lineDiscount

      row.lineTotal = lineNetPrice.toFixed(2)
      this.calculateTotal()
    },

    calculateTotal() {
      let subtotal = 0
      let discountAmount = 0

      this.quotation.items.forEach(i => {
        const q = parseFloat(i.quantity) || 0
        const p = parseFloat(i.unitPrice) || 0
        const d = this.isSuperAdmin ? (parseFloat(i.discount) || 0) : 0

        const originalPrice = q * p
        const lineDiscount = originalPrice * (d / 100)
        discountAmount += lineDiscount

        const lineNetPrice = originalPrice - lineDiscount
        subtotal += lineNetPrice
      })

      this.quotation.subtotal = subtotal.toFixed(2)
      this.quotation.discountAmount = discountAmount.toFixed(2)
      this.quotation.total = subtotal.toFixed(2)
    },

    async save() {
      // 1. 验证表单字段
      const { valid } = await this.$refs.form.validate()

      if (!valid) {
        this.$snackbar?.show('Please check the fields marked in red.', 'error')
        return
      }

      // 2. 逻辑验证：必须有产品
      if (this.quotation.items.length === 0) {
        this.$snackbar?.show('Please add at least one quotation item.', 'warning')
        return
      }

      const dataToSave = JSON.parse(JSON.stringify(this.quotation))
      dataToSave.items = dataToSave.items.map(item => ({
        ...item,
        quantity: parseFloat(item.quantity) || 0,
        unitPrice: parseFloat(item.unitPrice) || 0,
        discount: this.isSuperAdmin ? (parseFloat(item.discount) || 0) : 0,
      }))

      try {
        await api.post('/quotations', dataToSave)
        this.$snackbar?.show('Saved successfully', 'success')
        this.$router.push('/quotations')
      } catch (e) {
        console.error('Failed to save quotation:', e)
        this.$snackbar?.show('Failed to save quotation, please check the data.', 'error')
      }
    }
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
