<template>
  <div>
    <v-row class="mb-2 mb-sm-4">
      <v-col cols="12" sm="6" md="6">
        <h2 class="text-h5 text-sm-h4">Quotation Management</h2>
      </v-col>
      <v-col cols="12" sm="6" md="6" class="text-left text-sm-right">
        <v-btn color="primary" block class="d-md-none" @click="$router.push('/quotations/new')">
          <v-icon left>mdi-plus</v-icon>
          <span class="d-none d-sm-inline">New Quotation</span>
          <span class="d-sm-none">New</span>
        </v-btn>
        <v-btn color="primary" class="d-none d-md-inline-block" @click="$router.push('/quotations/new')">
          <v-icon left>mdi-plus</v-icon>
          New Quotation
        </v-btn>
      </v-col>
    </v-row>

    <v-card>
      <v-card-title class="pb-3">
        <div class="d-flex flex-column flex-md-row w-100 align-center gap-3">
          <!-- Left: Search Box -->
          <div class="flex-grow-1 w-100" style="max-width: 500px;">
            <v-text-field
              v-model="search"
              prepend-inner-icon="mdi-magnify"
              label="Search by quotation number, title or customer name"
              placeholder="Enter keywords to search..."
              variant="outlined"
              density="comfortable"
              clearable
              hide-details
              class="w-100"
            ></v-text-field>
          </div>

          <!-- Right: Action Buttons -->
          <div class="d-flex flex-wrap gap-2 align-center">
            <!-- Items Per Page Selector -->
            <v-select
              v-model="itemsPerPage"
              :items="itemsPerPageOptions"
              label="Per Page"
              variant="outlined"
              density="comfortable"
              hide-details
              style="min-width: 100px"
            ></v-select>

            <!-- Column Filter Button -->
            <v-menu>
              <template v-slot:activator="{ props }">
                <v-btn
                  color="primary"
                  variant="outlined"
                  v-bind="props"
                >
                  <v-icon left>mdi-filter-variant</v-icon>
                  Column Filter
                </v-btn>
              </template>
              <v-list>
                <v-list-subheader>Show/Hide Columns</v-list-subheader>
                <v-list-item
                  v-for="header in filterableHeaders"
                  :key="header.key"
                  @click="toggleColumn(header.key)"
                >
                  <template v-slot:prepend>
                    <v-checkbox
                      :model-value="isColumnVisible(header.key)"
                      density="compact"
                      hide-details
                    ></v-checkbox>
                  </template>
                  <v-list-item-title>{{ header.title }}</v-list-item-title>
                </v-list-item>
              </v-list>
            </v-menu>

            <!-- Export Button -->
            <v-btn
              color="success"
              variant="elevated"
              @click="exportData"
            >
              <v-icon left>mdi-download</v-icon>
              Export CSV
            </v-btn>
          </div>
        </div>
      </v-card-title>

      <!-- Desktop: Enhanced Data Table -->
      <v-data-table
        :headers="visibleHeaders"
        :items="list"
        :search="search"
        :items-per-page="itemsPerPage"
        :items-per-page-options="itemsPerPageOptions"
        item-key="id"
        class="d-none d-md-block"
        :multi-sort="true"
        :sort-by="[{ key: 'issueDate', order: 'desc' }]"
      >
        <template v-slot:item.total="{ item }">
          {{ item.currency }} {{ item.total }}
        </template>

        <template v-slot:item.status="{ item }">
          <v-chip
            :color="getStatusColor(item.status)"
            dark
            small
          >
            {{ getStatusText(item.status) }}
          </v-chip>
        </template>

        <template v-slot:item.actions="{ item }">
          <v-btn
            icon
            size="small"
            color="primary"
            @click="$router.push(`/quotations/${item.id}`)"
          >
            <v-icon>mdi-eye</v-icon>
          </v-btn>
          <v-btn
            v-if="item.status === 'DRAFT'"
            icon
            size="small"
            color="success"
            @click="submit(item.id)"
          >
            <v-icon>mdi-send</v-icon>
          </v-btn>
          <v-btn
            v-if="item.status === 'SUBMITTED' && isAdmin"
            icon
            size="small"
            color="success"
            @click="approve(item.id)"
          >
            <v-icon>mdi-check</v-icon>
          </v-btn>
          <v-btn
            v-if="item.status === 'SUBMITTED'"
            icon
            size="small"
            color="error"
            @click="reject(item.id)"
          >
            <v-icon>mdi-close</v-icon>
          </v-btn>
          <v-btn
            icon
            size="small"
            color="info"
            @click="exportPdf(item.id)"
          >
            <v-icon>mdi-file-pdf-box</v-icon>
          </v-btn>
        </template>
      </v-data-table>

      <!-- Mobile & Tablet: Card List -->
      <div class="d-md-none pa-2">
        <v-card
          v-for="item in filteredList"
          :key="item.id"
          class="mb-3"
          elevation="2"
        >
          <v-card-title class="text-subtitle-1 pb-1">
            {{ item.title }}
          </v-card-title>
          <v-card-subtitle class="pt-1">
            <div class="d-flex flex-column">
              <span>Number: {{ item.quoteNumber }}</span>
              <span>Customer: {{ item.customerName }}</span>
              <span>Amount: {{ item.currency }} {{ item.total }}</span>
              <span class="mt-1">
                <v-chip
                  :color="getStatusColor(item.status)"
                  dark
                  x-small
                >
                  {{ getStatusText(item.status) }}
                </v-chip>
              </span>
            </div>
          </v-card-subtitle>
          <v-card-actions class="pt-0">
            <v-spacer></v-spacer>
            <v-btn
              icon
              size="small"
              color="primary"
              @click="$router.push(`/quotations/${item.id}`)"
            >
              <v-icon>mdi-eye</v-icon>
            </v-btn>
            <v-btn
              v-if="item.status === 'DRAFT'"
              icon
              size="small"
              color="success"
              @click="submit(item.id)"
            >
              <v-icon>mdi-send</v-icon>
            </v-btn>
            <v-btn
              v-if="item.status === 'SUBMITTED' "
              icon
              size="small"
              color="success"
              @click="approve(item.id)"
            >
              <v-icon>mdi-check</v-icon>
            </v-btn>
            <v-btn
              v-if="item.status === 'SUBMITTED'"
              icon
              size="small"
              color="error"
              @click="reject(item.id)"
            >
              <v-icon>mdi-close</v-icon>
            </v-btn>
            <v-btn
              icon
              size="small"
              color="info"
              @click="exportPdf(item.id)"
            >
              <v-icon>mdi-file-pdf-box</v-icon>
            </v-btn>
          </v-card-actions>
        </v-card>
      </div>
    </v-card>
  </div>
</template>

<script>
import api from '../api.js'
import { exportToCSV } from '../utils/tableExport.js'

export default {
  name: 'QuotationList',
  data() {
    return {
      search: '',
      list: [],
      itemsPerPage: 10,
      itemsPerPageOptions: [5, 10, 25, 50, 100],
      visibleColumns: {},
      userInfo:{},
      headers: [
        { title: 'Quotation Number', key: 'quoteNumber', sortable: true },
        { title: 'Title', key: 'title', sortable: true },
        { title: 'Customer', key: 'customerName', sortable: true },
        { title: 'Total Amount', key: 'total', sortable: true },
        { title: 'Status', key: 'status', sortable: true },
        { title: 'Issue Date', key: 'issueDate', sortable: true },
        { title: 'Operator', key: 'operatorName', sortable: false },
        { title: 'Operation Time', key: 'createdAt', sortable: true },
        { title: 'Actions', key: 'actions', sortable: false, align: 'end' },
      ]
    }
  },
  computed: {
    filteredList() {
      if (!this.search) return this.list
      const searchLower = this.search.toLowerCase()
      return this.list.filter(item => {
        return (
          (item.quoteNumber && item.quoteNumber.toLowerCase().includes(searchLower)) ||
          (item.title && item.title.toLowerCase().includes(searchLower)) ||
          (item.customerName && item.customerName.toLowerCase().includes(searchLower))
        )
      })
    },
    filterableHeaders() {
      return this.headers.filter(h => h.key !== 'actions')
    },
    visibleHeaders() {
      return this.headers.filter(header => {
        if (header.key === 'actions') return true
        return this.visibleColumns[header.key] !== false
      })
    },
    isAdmin(){
      return  this.userInfo?.role==='ADMIN'
    }
  },
  mounted() {
    this.loadUserInfo()
    this.load()
    // Initialize all columns as visible
    this.headers.forEach(header => {
      if (header.key !== 'actions') {
        this.visibleColumns[header.key] = true
      }
    })
  },
  methods: {
    // 加载用户信息
    async loadUserInfo() {
      try {
        const res = await api.get('/auth/me')
        this.userInfo = res.data.data
      } catch (error) {
        this.$snackbar.show('Failed to load current user', 'error')
      }
    },

    async load() {
      try {
        const res = await api.get('/quotations')
        this.list = res.data.data
      } catch (error) {
        this.$snackbar.show('Failed to load', 'error')
      }
    },
    getStatusColor(status) {
      const map = {
        'DRAFT': 'grey',
        'SUBMITTED': 'orange',
        'APPROVED': 'green',
        'REJECTED': 'red',
        'CANCELLED': 'grey'
      }
      return map[status] || 'grey'
    },
    getStatusText(status) {
      const map = {
        'DRAFT': 'Draft',
        'SUBMITTED': 'Pending Approval',
        'APPROVED': 'Approved',
        'REJECTED': 'Rejected',
        'CANCELLED': 'Cancelled'
      }
      return map[status] || status
    },
    isColumnVisible(key) {
      return this.visibleColumns[key] !== false
    },
    toggleColumn(key) {
      if (key === 'actions') return
      this.visibleColumns[key] = !this.visibleColumns[key]
    },
    exportData() {
      // Use utility function to export
      const formatter = (key, value, item) => {
        if (key === 'total') {
          return `${item.currency} ${value}`
        } else if (key === 'status') {
          return this.getStatusText(value)
        }
        return value
      }
      exportToCSV(this.visibleHeaders, this.list, formatter, 'Quotation List')
    },
    async submit(id) {
      try {
        await api.post(`/quotations/${id}/submit`)
        this.$snackbar.show('Submitted successfully')
        this.load()
      } catch (error) {
        this.$snackbar.show(error.response?.data?.msg || 'Submission failed','error')
      }
    },
    async approve(id) {
      try {
        await api.post(`/quotations/${id}/approve`, { comment: 'Approved' })
        this.$snackbar.show('Approved successfully')
        this.load()
      } catch (error) {
        this.$snackbar.show(error.response?.data?.msg || 'Approval failed','error')
      }
    },
    async reject(id) {
      try {
        await api.post(`/quotations/${id}/reject`, { comment: 'Rejected' })
        this.$snackbar.show('Rejected','error')
        this.load()
      } catch (error) {
        this.$snackbar.show(error.response?.data?.msg || 'Operation failed','error')
      }
    },
    exportPdf(id) {
      this.$router.push({
        path: `/quotations/${id}/preview`,
        query: {
          quoteId: id // Pass ID as query parameter
        }
      })
    }
  }
}
</script>

<style scoped>
.gap-2 {
  gap: 8px;
}
.gap-3 {
  gap: 12px;
}
.w-100 {
  width: 100%;
}
</style>
