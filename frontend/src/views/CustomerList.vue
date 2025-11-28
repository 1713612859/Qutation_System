<template>
  <div>
    <v-row class="mb-2 mb-sm-4">
      <v-col cols="12" sm="6">
        <h2 class="text-h5 text-sm-h4">Customer Management</h2>
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

      <!-- PC Table -->
      <v-data-table
          :headers="headers"
          :items="list"
          :items-per-page="options.itemsPerPage"
          :page="options.page"
          :items-per-page-options="[10, 20, 50, 100]"
          :server-items-length="totalItems"
          @update:options="handleOptionsUpdate"
          item-key="id"
          class="d-none d-md-block"
      >
        <!-- 头像 + 高亮 -->
        <template #item.companyName="{ item }">
          <v-avatar class="mr-2" color="primary" size="28">
            {{ item.companyName?.charAt(0).toUpperCase() }}
          </v-avatar>
          <span v-html="highlight(item.companyName)"></span>
        </template>

        <template #item.customerName="{ item }">
          <span v-html="highlight(item.customerName)"></span>
        </template>

        <template #item.contactName="{ item }">
          <span v-html="highlight(item.contactName)"></span>
        </template>

        <template #item.contactPhone="{ item }">
          <span v-html="highlight(item.contactPhone)"></span>
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

      <!-- Mobile Card List -->
      <div class="d-md-none pa-2">
        <v-card
            v-for="item in list"
            :key="item.id"
            class="mb-3"
            elevation="2"
        >
          <v-card-title class="text-subtitle-1 pb-1">
            <v-avatar color="primary" size="32" class="mr-2">
              {{ item.companyName?.charAt(0).toUpperCase() }}
            </v-avatar>
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
            <v-spacer/>
            <v-btn icon size="small" color="primary" @click="edit(item)">
              <v-icon>mdi-pencil</v-icon>
            </v-btn>
            <v-btn icon size="small" color="error" @click="remove(item.id)">
              <v-icon>mdi-delete</v-icon>
            </v-btn>
          </v-card-actions>
        </v-card>

        <!-- Pagination -->
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

    <!-- Dialog -->
    <v-dialog v-model="showDialog" max-width="650" scrollable>
      <v-card>
        <v-card-title class="text-subtitle-1 text-sm-h6">
          {{ form.id ? 'Edit Customer' : 'Add Customer' }}
        </v-card-title>

        <v-card-text class="pa-2 pa-sm-4">
          <v-form ref="formRef" v-model="valid">
            <v-text-field v-model="form.companyName" label="Company Name" :rules="[rules.required]" required
                          density="compact"/>
            <v-text-field v-model="form.customerName" label="Customer Name (Attention)" :rules="[rules.required]"
                          required density="compact"/>
            <v-text-field v-model="form.contactName" label="Contact" :rules="[rules.required]" required
                          density="compact"/>
            <v-text-field v-model="form.contactPhone" label="Phone" :rules="[rules.required]" required
                          density="compact"/>
            <v-text-field v-model="form.contactEmail" label="Email" type="email" :rules="[rules.email]"
                          density="compact"/>

            <v-textarea
                v-model="form.address"
                label="Address"
                rows="2"
                density="compact"
                @input="copyAddress && (form.shippingAddress = form.address)"
            />

            <!-- Copy checkbox -->
            <v-checkbox
                v-model="copyAddress"
                label="Shipping address same as company address"
                density="compact"
                @change="syncShippingAddress"
            />

            <!-- New shipping address -->
            <v-textarea
                v-model="form.shippingAddress"
                label="Shipping Address"
                rows="2"
                density="compact"
                hint="Customer shipping address (if different)"
                persistent-hint
            />

            <v-row dense>
              <v-col cols="12" sm="6">
                <v-text-field v-model="form.city" label="City" density="compact"/>
              </v-col>
              <v-col cols="12" sm="6">
                <v-text-field v-model="form.province" label="Province" density="compact"/>
              </v-col>
            </v-row>
          </v-form>
        </v-card-text>

        <v-card-actions class="pa-2 pa-sm-4">
          <v-spacer/>
          <v-btn size="small" @click="closeDialog">Cancel</v-btn>
          <v-btn color="primary" size="small" @click="save(false)">Save</v-btn>
          <v-btn color="secondary" size="small" @click="save(true)">Save & Add Another</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>
<script>
import api from '../api'

export default {
  name: 'CustomerList',

  data() {
    return {
      search: '',
      list: [],
      totalItems: 0,

      showDialog: false,
      valid: false,
      form: {},

      copyAddress: false,

      options: {
        page: 1,
        itemsPerPage: 10,
        sortBy: []
      },

      rules: {
        required: v => !!v || 'This field is required',
        email: v => !v || /.+@.+\..+/.test(v) || 'Invalid email format'
      },

      headers: [
        {title: 'ID', key: 'id', sortable: true},
        {
          title: 'Company Name',
          key: 'companyName',
          sortable: true
        },
        {title: 'Customer Name (Attn)', key: 'customerName', sortable: true},
        {title: 'Contact', key: 'contactName', sortable: true},
        {title: 'Phone', key: 'contactPhone', sortable: true},
        {title: 'Email', key: 'contactEmail', sortable: true},
        {title: 'City', key: 'city', sortable: true},
        {title: 'Actions', key: 'actions', align: 'end', sortable: false}
      ]
    }
  },

  mounted() {
    this.load()
  },

  methods: {
    highlight(text) {
      if (!this.search) return text
      return text?.replace(
          new RegExp(this.search, 'gi'),
          m => `<mark>${m}</mark>`
      )
    },

    async load() {
      const {page, itemsPerPage, sortBy} = this.options

      const params = {
        page,
        limit: itemsPerPage,
        search: this.search,
        sortBy: sortBy[0]?.key,
        sortDesc: sortBy[0]?.order === 'desc'
      }

      try {
        const res = await api.get('/customers', {params})

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

    handleOptionsUpdate(newOptions) {
      this.options = newOptions
      this.load()
    },

    handleSearch() {
      this.options.page = 1
      this.load()
    },

    syncShippingAddress() {
      if (this.copyAddress) {
        this.form.shippingAddress = this.form.address
      }
    },

    openDialog() {
      this.form = {}
      this.copyAddress = false
      this.showDialog = true
      this.$nextTick(() => this.$refs.formRef?.resetValidation())
    },

    closeDialog() {
      this.showDialog = false
      this.$refs.formRef?.resetValidation()
    },

    edit(item) {
      this.form = {...item}
      this.copyAddress = false
      this.showDialog = true
      this.$nextTick(() => this.$refs.formRef?.resetValidation())
    },

    async save(keepOpen = false) {
      const {valid} = await this.$refs.formRef.validate()
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

          if (!keepOpen) {
            this.showDialog = false
          } else {
            this.form = {}
            this.copyAddress = false
            this.$nextTick(() => this.$refs.formRef.resetValidation())
          }

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

          if (this.list.length === 1 && this.options.page > 1) {
            this.options.page--
          }

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

mark {
  background-color: #ffeb3b66;
  padding: 0 2px;
  border-radius: 2px;
}
</style>
