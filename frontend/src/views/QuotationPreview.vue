<template>
  <div id="quotation-template" ref="quotationContent" class="quotation-container minimalist-style">
    <div class="logo">
      <img src="../static/logo1.png" class="logo" alt="ZUNO GROUP INC. Logo">
    </div>
    <div class="header">
      <div class="company-info">
        <h2></h2>
        <p class="company-address">Eastfield Center
          Mall of Asia Complex, Diosdado Macapagal Blvd, Pasay City, 1300 Metro Manila</p>
        <p class="company-contact">Email: sales@zuno.ph | Tel: +63 0917-446-9999</p>
      </div>
    </div>

    <h1 class="title">QUOTATION</h1>

    <div v-if="!quotation.quoteNumber" class="loading-state">
      <p>Loading...</p>
    </div>

    <div v-else>
      <div class="quote-meta-info">
        <div class="meta-block">
          <p class="meta-heading">BILLED TO:</p>
          <p class="meta-content customer-name">Company Name: {{ customer.companyName || '' }}</p>
          <p class="meta-content ">Customer Name: {{ customer.customerName || '' }}</p>
          <p class="meta-address">Contact Name: {{ customer.contactName || '' }}</p>
          <p class="meta-attention">Address: {{ customer.address || '' }}</p>
        </div>
        <div class="meta-block right-aligned">
          <p><span class="label">Quotation No:</span> <span class="value">{{ quotation.quoteNumber }}</span></p>
          <p><span class="label">Issue Date:</span> <span class="value">{{ quotation.issueDate }}</span></p>
          <p><span class="label">Valid Until:</span> <span class="value">{{ quotation.expiryDate }}</span></p>
<!--          <p class="meta-contact-line"><span class="label">Phone:</span> <span
              class="value">{{ quotation.customerPhone || 'N/A' }}</span></p>-->
        </div>
      </div>

      <table class="quote-table">
        <thead>
        <tr>
          <th style="width: 5%;">#</th>
          <th style="width: 45%;">Description</th>
          <th style="width: 10%;">Qty</th>
          <th style="width: 15%;" class="text-right">Unit Price</th>
          <th style="width: 25%;" class="text-right">Line Total ({{ quotation.currency }})</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="(item, i) in quotation.items" :key="i">
          <td>{{ i + 1 }}</td>
          <td class="description-cell">
            <div v-if="item.packageName" class="package-tag">
              [{{ item.packageName }}]
            </div>
            <div class="product-name">{{ item.productName }}</div>
            <div v-if="item.description" class="item-description">
              {{ item.description }}
            </div>
          </td>
          <td class="text-center">{{ item.quantity }}</td>
          <td class="text-right">{{ quotation.zeroTax && item.taxRate > 0 ? format(item.unitPrice / (1 + item.taxRate)) : format(item.unitPrice) }}</td>
          <td class="line-total text-right">{{ quotation.zeroTax && item.taxRate > 0 ? format(item.quantity * (item.unitPrice / (1 + item.taxRate))) : format(item.quantity * item.unitPrice) }}</td>
        </tr>
        </tbody>
      </table>

      <div class="totals-wrapper">
        <div class="totals">
          <div class="total-row">
            <span class="label">Subtotal:</span>
            <span class="value">{{ quotation.currency }} {{ format(quotation.subtotal) }}</span>
          </div>
          <div v-if="quotation.discountAmount && quotation.discountAmount > 0" class="total-row discount-row">
            <span class="label">Total Discount:</span>
            <span class="value">-{{ quotation.currency }} {{ format(quotation.discountAmount) }}</span>
          </div>
            <div v-if="quotation.ewtAmount && quotation.ewtAmount > 0" class="total-row discount-row">
            <span class="label">Less EWT Amount:</span>
            <span class="value">-{{ quotation.currency }} {{ format(quotation.ewtAmount) }}</span>
          </div>
          <div v-if="!quotation.zeroTax" class="total-row tax-row">
            <span class="label">VAT Amount (12%):</span>
            <span class="value">{{ quotation.currency }} {{ format(quotation.taxAmount) }}</span>
          </div>
          <div class="total-row grand-total-row">
            <span class="label">TOTAL Amount  :</span>
            <span class="value">{{ quotation.currency }} {{ format(quotation.total) }}</span>
          </div>
        </div>
      </div>

      <div class="terms-and-conditions">
        <h3 class="section-title">Terms and Conditions:</h3>

        <div class="bank-details-section">
          <p><strong>For payment, please remit to:</strong></p>
          <div
              v-for="(bank, index) in quotation.bankAccounts"
              :key="index"
              class="bank-details"
          >
            <div class="form-row">
              <span class="row-label"><span>#{{ index + 1 }}</span> Name of Payee:</span>
              <span v-if="isExporting" class="static-value">{{ bank.payee }}</span>
              <input v-else v-model="bank.payee" class="bank-input" placeholder="Enter Payee" />
            </div>

            <div class="form-row">
              <span class="row-label">Name of Bank:</span>
              <span v-if="isExporting" class="static-value">{{ bank.bankName }}</span>
              <input v-else v-model="bank.bankName" class="bank-input" placeholder="Enter Bank Name" />
            </div>

            <div class="form-row">
              <span class="row-label">Bank Account No:</span>
              <span v-if="isExporting" class="static-value">{{ bank.accountNumber }}</span>
              <input v-else v-model="bank.accountNumber" class="bank-input" placeholder="Enter Account No" />
            </div>

            <div class="bank-actions" v-if="!isExporting && quotation.bankAccounts.length > 1">
              <button @click="removeBank(index)" class="remove-btn">
                Remove
              </button>
            </div>
          </div>

          <div class="add-bank" v-if="!isExporting">
            <button @click="addBank" class="add-btn">
              + Add Another Bank
            </button>
          </div>
        </div>

        <div class="core-terms">
          <ol class="main-list">
            <li>Price are inclusive of VAT</li>

            <li class="list-item-flex">
              <span class="row-label">Payment Terms:</span>
              <span v-if="isExporting" class="static-value term-static">{{ quotation.paymentTerms }}</span>
              <input
                  v-else
                  type="text"
                  v-model="quotation.paymentTerms"
                  class="editable-term-input"
                  placeholder="Enter Payment Terms"
              >
            </li>
            <li class="list-item-flex">
              <span class="row-label">Validity:</span>
              <span v-if="isExporting" class="static-value term-static">{{ quotation.validity }}</span>
              <input
                  v-else
                  type="text"
                  v-model="quotation.validity"
                  class="editable-term-input"
                  placeholder="Enter Validity"
              >
            </li>
            <li class="list-item-flex">
              <span class="row-label">Availability:</span>
              <span v-if="isExporting" class="static-value term-static">{{ quotation.availability }}</span>
              <input
                  v-else
                  type="text"
                  v-model="quotation.availability"
                  class="editable-term-input"
                  placeholder="Enter Availability"
              >
            </li>
          </ol>

          <h4 class="sub-section-title">a. Hardware Terms</h4>
          <ol type="1">
            <li>All newly purchased hardware includes a one (1)-year warranty, effective from the date of delivery.</li>
            <li>Warranty covers manufacturing defects and normal operational failures only.</li>
            <li>The warranty shall not apply in cases of:
              <ul class="sub-ul">
                <li>Force majeure (e.g., natural disasters, fire, lightning, floods, etc.);</li>
                <li>Human error, misuse, negligence, or improper handling;</li>
                <li>Unauthorized modification, disassembly, or repair.</li>
              </ul>
            </li>
            <li>After the warranty period, maintenance or replacement shall be subject to additional service charges.</li>
            <li>For third-party brand equipment, the original manufacturer's warranty terms shall prevail.</li>
            <li>If the Client purchases hardware only, ZUNO shall be responsible solely for the delivery of the
              products. On-site installation, configuration, or setup services are not included and shall be charged
              separately based on actual service and transportation costs.
            </li>
          </ol>

          <h4 class="sub-section-title">b. Software Terms</h4>
          <ol type="1">
            <li>Subscription Period: The subscription shall commence from the date of initial software delivery or
              activation.
            </li>
            <li>Renewal: Upon expiration of the subscription, if the Client fails to renew in a timely manner, ZUNO
              reserves the right to suspend or terminate access to the software and related services until payment is
              received.
            </li>
            <li>Use and Restrictions: The Client shall not modify, alter, copy, reverse-engineer, or interfere with the
              software. Any unauthorized modification, damage, or misuse resulting in malfunction, data loss, or legal
              liabilities shall be borne solely by the Client.
            </li>
            <li>Intellectual Property: All rights, ownership, and intellectual property in the software remain the sole
              property of ZUNO GROUP INC.
            </li>
          </ol>

          <h4 class="sub-section-title">c. Limitation of liability</h4>
          <p class="term-paragraph">ZUNO shall not be liable for any indirect, incidental, or consequential damages,
            including but not limited to loss of data, business interruption, or loss of profits, arising from the use
            or inability to use the software or hardware. ZUNO's total liability shall not exceed the total contract
            amount paid by the Client.</p>

          <h4 class="sub-section-title">d. Confidentiality and Data Privacy (Philippines Data Privacy Act of 2012)</h4>
          <p class="term-paragraph">Both parties shall comply with the **Data Privacy Act of 2012** (Republic Act No.
            10173) of the Philippines. Neither party shall disclose any confidential or business information of the
            other without prior written consent, except as required by law.</p>

          <h4 class="sub-section-title">e. Termination</h4>
          <p class="term-paragraph">ZUNO may suspend or terminate services immediately if: The Client fails to pay the
            required fees on time; The Client violates any terms of this Agreement, including unauthorized software
            modification; or The Client engages in actions that damage ZUNO's systems, reputation, or property. All
            payments made prior to termination shall be non-refundable.</p>
        </div>
      </div>

      <div class="signature-section-final">
        <div class="signature-box-final sales-side-final">
          <p class="sincerely-final">Sincerely,</p>
          <div class="signature-content-final left-align-content">
            <div class="signature-line-final"></div>
            <p class="company-signature-name-final">ZUNO GROUP INC. REPRESENTATIVE</p>
            <p class="representative-title-final">Sales Department</p>
          </div>
        </div>

        <div class="signature-box-final client-side-final">
          <h3 class="acknowledged-header-final">ACKNOWLEDGED AND AGREED BY:</h3>
          <div class="signature-content-final right-align-content">
            <div class="signature-line-final"></div>
            <p class="representative-title-final">Authorized Representative</p>
          </div>
        </div>
      </div>
    </div>

    <div class="footer-note">
      <p class="thanks">Thank you for your business!</p>
    </div>

    <div class="actions" v-if="!isExporting">
      <v-btn color="primary" @click="exportPdf"
             :disabled="!quotation.quoteNumber || ['Load failed', 'No ID'].includes(quotation.quoteNumber)">📄 Export PDF
      </v-btn>
    </div>
  </div>
</template>

<script setup>
import jsPDF from 'jspdf'
import api from "../api";
import html2canvas from 'html2canvas'
import { ref, nextTick } from 'vue'
import { useRoute } from "vue-router";

const route = useRoute();
const quoteId = route.query.quoteId;
const customer = ref({})

const isExporting = ref(false);
const quotationContent = ref(null);

const quotation = ref({
  quoteNumber: null,
  issueDate: null,
  expiryDate: null,
  customerId: null,
  customerName: null,
  currency: 'PHP',
  items: [],
  subtotal: 0,
  taxAmount: 0,
  total: 0,
  notes: null,
  discountAmount: 0,
  paymentTerms: 'Either 100% Full Payment before delivery or 50% DP 50% before delivery',
  validity: '1, 2 or 3 months',
  availability: 'Either On-stock or Order Basis' ,
  bankAccounts: [
    {
      payee: '',
      bankName: '',
      accountNumber: ''
    }
  ],
});

if (quoteId) {
  try {
    const { data: res } = await api.get(`/quotations/${quoteId}`);
    if (res && res.data) {
      const apiData = res.data;
      quotation.value = {
        ...quotation.value,
        ...apiData.quotation,
        items: apiData.items || apiData.quotation?.items || [],
        paymentTerms: apiData.quotation.paymentTerms || quotation.value.paymentTerms,
        validity: apiData.quotation.validity || quotation.value.validity,
        availability: apiData.quotation.availability || quotation.value.availability,
      };

      if (!quotation.value.bankAccounts || quotation.value.bankAccounts.length === 0) {
        quotation.value.bankAccounts = [{ payee: '', bankName: '', accountNumber: '' }];
      }

      quotation.value.items.forEach(item => {
        item.lineTotal = parseFloat(item.lineTotal) || 0;
        item.unitPrice = parseFloat(item.unitPrice) || 0;
        item.taxRate = parseFloat(item.taxRate) || 0;
        item.packageName = item.packageName || null;
      });

      quotation.value.subtotal = parseFloat(quotation.value.subtotal) || 0;
      quotation.value.taxAmount = parseFloat(quotation.value.taxAmount) || 0;
      quotation.value.total = parseFloat(quotation.value.total) || 0;
      quotation.value.discountAmount = parseFloat(quotation.value.discountAmount) || 0;

      api.get(`/customers/${quotation.value.customerId}`).then(response => {
        customer.value = response.data.data;
      });
    }
  } catch (e) {
    console.error(e);
    quotation.value.quoteNumber = '加载失败';
  }
} else {
  quotation.value.quoteNumber = '无ID';
}

const addBank = () => {
  quotation.value.bankAccounts.push({ payee: '', bankName: '', accountNumber: '' });
};

const removeBank = (index) => {
  quotation.value.bankAccounts.splice(index, 1);
};

const format = val => {
  if (val === null || val === undefined || isNaN(val)) return '0.00';
  return Number(val).toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 });
};

const exportPdf = async () => {
  if (!quotation.value.quoteNumber) return;
  const res = await api.post('/quotations', quotation.value)
  if(res && !res.code === 200) {
    console.log(res.data , 'export failed ' ); // 执行 保存配置逻辑
    return ;
  }
  try {
    isExporting.value = true;
    await nextTick();

    window.scrollTo(0, 0);
    const element = document.getElementById('quotation-template');

    const canvas = await html2canvas(element, {
      scale: 2,
      useCORS: true,
      backgroundColor: '#ffffff',
      windowWidth: element.scrollWidth,
      windowHeight: element.scrollHeight,
      scrollY: 0,
      onclone: (clonedDoc) => {
        // 确保 Input 变身后的 Span 具有良好的行高，避免截断
        const staticValues = clonedDoc.querySelectorAll('.static-value');
        staticValues.forEach(el => {
          el.style.lineHeight = '1.6';
          el.style.paddingBottom = '3px';
          // 关键：强制 baseline 对齐
          el.style.verticalAlign = 'baseline';
        });
      }
    });

    const imgData = canvas.toDataURL("image/jpeg", 0.95);
    const pdf = new jsPDF("p", "mm", "a4");

    const pageWidth = pdf.internal.pageSize.getWidth();
    const pageHeight = pdf.internal.pageSize.getHeight();
    const imgWidth = pageWidth;
    const imgHeight = (canvas.height * pageWidth) / canvas.width;

    // --- Smart page break: scan canvas pixels to find white rows as safe cut points ---
    // This avoids relying on DOM layout measurements which can differ from html2canvas rendering.
    const pageHeightPx = Math.round((pageHeight / imgHeight) * canvas.height);
    // Search up to 25% of page height backwards to find a white gap row
    const searchRangePx = Math.round(pageHeightPx * 0.25);
    const LIGHT = 242; // pixel brightness threshold for "background white"
    const STEP = 6;    // sample every 6th pixel horizontally for speed
    const ctx2d = canvas.getContext('2d');

    const isWhiteRow = (y) => {
      const d = ctx2d.getImageData(0, y, canvas.width, 1).data;
      for (let i = 0; i < d.length; i += 4 * STEP) {
        if (d[i] < LIGHT || d[i + 1] < LIGHT || d[i + 2] < LIGHT) return false;
      }
      return true;
    };

    const pageStartsPx = [0];
    let targetPx = pageHeightPx;
    while (targetPx < canvas.height) {
      const lastPx = pageStartsPx[pageStartsPx.length - 1];
      let cutPx = targetPx;
      // Walk backwards from the target cut to find the nearest all-white row
      for (let y = targetPx; y > Math.max(targetPx - searchRangePx, lastPx + 1); y--) {
        if (isWhiteRow(y)) { cutPx = y; break; }
      }
      pageStartsPx.push(cutPx);
      targetPx = cutPx + pageHeightPx;
    }

    pageStartsPx.forEach((startPx, idx) => {
      const startMm = (startPx / canvas.height) * imgHeight;
      if (idx > 0) pdf.addPage();
      pdf.addImage(imgData, "JPEG", 0, -startMm, imgWidth, imgHeight);
    });

    pdf.save(`Quotation_${quotation.value.quoteNumber}.pdf`);

  } catch (error) {
    console.error(error);
    alert("生成 PDF 失败");
  } finally {
    isExporting.value = false;
  }
};
</script>
<style scoped>
/* 定义品牌色变量 */
:root {
  --brand-color: #004d99;
  --accent-color: #007bff;
  --light-gray: #f9f9f9;
  --medium-gray: #eee;
  --text-dark: #2c3e50;
  --text-light: #6c757d;
}

.quotation-container {
  width: 794px;
  min-height: 1123px;
  margin: auto;
  background: #fff;
  padding: 30px;
  font-family: 'Inter', 'Arial', sans-serif;
  color: var(--text-dark);
  box-sizing: border-box;
  overflow: visible;
  /* 确保全局容器在最上层 */
  position: relative;
  z-index: 10;
}

/* Header & Logo */
.header {
  justify-content: space-between;
  align-items: center;
  border-bottom: 5px solid var(--brand-color);
  padding-bottom: 15px;
  margin-bottom: 30px;
  text-align: center;
  align-content: center;
}
.logo {
  width: 100%;
  justify-content: center;
  text-align: center;
  max-height: 100px;
  object-fit: contain;
}
.company-info {
  align-content: center;
  text-align: center;
  line-height: 1.3;
}
.company-info h2 {
  margin-bottom: 2px;
  font-size: 1.7em;
  color: #000;
  text-transform: uppercase;
}
.company-info p {
  margin: 0;
  font-size: 0.8em;
  color: var(--text-light);
  align-items: center;
  align-content: center;
  text-align: center;
}
.company-address {
  font-style: italic;
  font-size: 0.75em !important;
}
.company-contact {
  margin-top: 5px !important;
  font-weight: bold;
}
.title {
  text-align: left;
  margin-top: 0;
  margin-bottom: 20px;
  font-size: 2.2em;
  color: var(--brand-color);
  font-weight: bold;
  letter-spacing: 2px;
}

/* Meta Info */
.quote-meta-info {
  display: flex;
  justify-content: space-between;
  margin-bottom: 30px;
  font-size: 0.9em;
}
.meta-block {
  padding: 10px 15px;
  border-left: 5px solid var(--brand-color);
  background-color: var(--light-gray);
  flex-basis: 48%;
  border-radius: 4px;
}
.meta-block.right-aligned {
  text-align: right;
  border-left: none;
  border-right: 5px solid var(--brand-color);
  background-color: #f0f7ff;
}
.meta-heading {
  font-size: 0.9em;
  color: var(--text-light);
  margin-bottom: 5px;
  font-weight: bold;
  text-transform: uppercase;
}
.customer-name {
  font-size: 1.2em;
  color: #000;
  font-weight: bold;
  margin-top: 5px;
  margin-bottom: 5px;
}
.meta-address, .meta-attention {
  font-size: 0.9em;
  color: var(--text-dark);
}
.meta-block p {
  margin: 3px 0;
}
.meta-block .label {
  font-weight: 500;
  color: var(--text-light);
  display: inline-block;
  width: 100px;
}
.meta-block .value {
  font-weight: bold;
  color: var(--text-dark);
}

/* Table */
.quote-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
  table-layout: fixed;
}
.quote-table th {
  background-color: var(--brand-color);
  color: #fff;
  font-weight: bold;
  text-transform: uppercase;
  padding: 12px 8px;
  font-size: 0.85em;
  border-bottom: 1px solid #000;
}
.quote-table td {
  border: none;
  border-bottom: 1px solid #eee;
  padding: 10px 8px;
  word-wrap: break-word;
  vertical-align: top;
  font-size: 0.85em;
  line-height: 1.4;
}
.description-cell {
  text-align: left !important;
  padding-left: 10px !important;
}
.package-tag {
  display: inline-block;
  font-size: 0.7em;
  font-weight: bold;
  color: var(--brand-color);
  background-color: #e6f0ff;
  border: 1px solid var(--accent-color);
  padding: 1px 5px;
  border-radius: 3px;
  margin-bottom: 2px;
  text-transform: uppercase;
}
.product-name {
  font-weight: bold;
  color: #000;
  line-height: 1.3;
}
.item-description {
  font-size: 0.75em;
  color: var(--text-light);
  margin-top: 3px;
  font-style: italic;
}
.line-total {
  font-weight: bold;
  color: #222;
}

/* Totals */
.totals-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
.totals {
  width: 350px;
  font-size: 1em;
  border: 1px solid var(--medium-gray);
  border-radius: 4px;
}
.total-row {
  display: flex;
  justify-content: space-between;
  padding: 10px 15px;
  border-bottom: 1px dashed #eee;
}
.total-row:last-child {
  border-bottom: none;
}
.total-row .label {
  font-weight: 500;
  color: var(--text-dark);
}
.total-row .value {
  font-weight: bold;
  color: #000;
}
.grand-total-row {
  background-color: var(--brand-color);
  color: #000000;
  font-size: 1.2em;
  padding: 15px;
  border-radius: 0 0 4px 4px;
}
.grand-total-row .label, .grand-total-row .value {
  color: #000000;
}
.tax-row {
  font-style: italic;
  color: #666;
}
.discount-row .value {
  color: #dc3545;
}

/* Terms & Conditions */
.terms-and-conditions {
  margin-top: 50px;
  padding-top: 20px;
  border-top: 2px solid var(--medium-gray);
  font-size: 0.85em;
  line-height: 1.6;
}
.section-title {
  font-size: 1.3em;
  color: var(--brand-color);
  margin-bottom: 15px;
  padding-bottom: 5px;
  border-bottom: 1px solid #ddd;
}

/* --- 银行信息容器修复：隔离与裁剪 --- */
.bank-details-section {
  background-color: var(--light-gray);
  padding: 15px;
  border-radius: 5px;
  margin-bottom: 20px;
  overflow: hidden;
}
.bank-details-section strong {
  font-size: 1.1em;
  color: var(--text-dark);
}

/* 动态银行信息块修复：强制硬件加速和调整间距 */
.bank-details {
  position: relative;
  z-index: 2;
  /* 响应用户要求，减少块间距 */
  margin-bottom: 5px;
  /* 终极修复：强制硬件加速，解决渲染瑕疵 */
  transform: translateZ(0);
  -webkit-transform: translateZ(0);
}

/* --- Flex Row 布局实现对齐和换行 --- */
.form-row {
  display: flex;
  align-items: baseline;
  margin-bottom: 5px;
  padding-left: 20px;
  color: #333;
}

.list-item-flex {
  display: flex;
  align-items: baseline;
  margin-bottom: 5px;
}

/* 关键修复 1: 设定 Label 固定宽度，实现整齐对齐 */
.row-label {
  white-space: nowrap;
  width: 150px;
  min-width: 150px;
  margin-right: 10px;
  text-align: right;
}

.row-label span {
  font-weight: bold;
  color: var(--brand-color);
  margin-right: 5px;
}

/* Input & Static Value */
.bank-input,
.editable-term-input {
  flex-grow: 1;
  font-weight: bold;
  color: var(--brand-color);
  border: none;
  border-bottom: 1px dashed #999;
  padding: 0 5px;
  background-color: transparent;
  font-size: 1em;
  line-height: 1.5;
  outline: none;
  word-break: break-word;
}

.bank-input {
  min-width: 200px;
}
.editable-term-input {
  width: 100%;
}

/* 导出时的静态文本样式 (Span) - 修复截断 + 保持对齐 */
.static-value {
  flex-grow: 1;
  display: inline-block;
  font-weight: bold;
  color: var(--brand-color);
  padding: 0 5px;
  border-bottom: 1px dashed #999;
  min-width: 200px;

  /* 解决截断 */
  line-height: 1.5;
  padding-bottom: 2px;
  /* 允许在长单词/数字处换行，防止溢出或分割符问题 */
  word-break: break-word;
}

.term-static {
  min-width: 50px;
}

/* List Styles */
.core-terms ol {
  margin-left: 0;
  padding-left: 20px;
}
.main-list li {
  font-weight: bold;
  color: var(--text-dark);
  margin-bottom: 5px;
}
.sub-section-title {
  font-size: 1.1em;
  color: var(--text-dark);
  margin-top: 15px;
  margin-bottom: 8px;
  font-weight: bold;
}
.term-paragraph {
  margin-bottom: 15px;
}
.sub-ul {
  margin: 5px 0 10px 0;
  list-style-type: disc;
  padding-left: 20px;
  font-style: italic;
}

/* Signatures */
.signature-section-final {
  display: flex;
  justify-content: space-between;
  margin-top: 80px;
  width: 100%;
  page-break-inside: avoid;
  break-inside: avoid;
}
.signature-box-final {
  flex-basis: 48%;
  position: relative;
}
.sales-side-final {
  text-align: left;
}
.sincerely-final {
  font-weight: 500;
  font-size: 1.1em;
  margin: 0;
  margin-bottom: 5px;
  display: block;
}
.left-align-content {
  text-align: left;
  margin-top: 50px;
}
.client-side-final {
  text-align: right;
}
.acknowledged-header-final {
  font-weight: bold;
  color: var(--text-dark);
  font-size: 1.1em;
  margin-bottom: 5px;
  margin-top: 0;
  text-align: right;
}
.right-align-content {
  text-align: right;
  margin-top: 50px;
}
.signature-line-final {
  border-bottom: 1px solid #000;
  width: 350px;
  height: 1px;
  margin-bottom: 5px;
}
.left-align-content .signature-line-final {
  margin-left: 0;
  margin-right: auto;
}
.right-align-content .signature-line-final {
  margin-left: auto;
  margin-right: 0;
}
.company-signature-name-final {
  font-size: 1em;
  font-weight: bold;
  color: #000;
  margin: 3px 0;
  text-transform: uppercase;
}
.representative-title-final {
  font-size: 0.9em;
  color: var(--text-light);
  margin-top: 3px;
}

/* Footer & Actions */
.footer-note {
  text-align: center;
  margin-top: 80px;
}
.actions {
  text-align: center;
  margin-top: 30px;
  padding: 15px;
  border-top: 1px dashed #ccc;
}
.bank-actions {
  text-align: right;
  margin-top: 6px;
  padding-right: 20px;
}
.remove-btn {
  background: #dc3545;
  color: white;
  border: none;
  padding: 3px 8px;
  font-size: 12px;
  cursor: pointer;
  border-radius: 3px;
}
.add-bank {
  text-align: right;
  margin-top: 10px;
}
.add-btn {
  background: var(--brand-color, #003366);
  color: white;
  border: none;
  padding: 5px 10px;
  font-size: 13px;
  cursor: pointer;
  border-radius: 3px;
}
.loading-state {
  text-align: center;
  padding: 50px;
  font-size: 1.2em;
  color: #999;
}
</style>
