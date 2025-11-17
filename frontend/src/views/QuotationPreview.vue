<template>
  <div id="quotation-template" class="quotation-container minimalist-style">
    <!-- 1. 头部和公司信息 (Logo on Left, Info on Right) -->
    <div class="logo">
      <!-- NOTE: Local image paths are not supported. Using a placeholder for visualization. -->
      <img src="../static/logo1.png" class="logo" alt="ZUNO GROUP INC. Logo">
    </div>
    <div class="header">
      <div class="company-info">
        <!-- 使用 p 标签代替 h3 标签 -->
        <h2></h2>
        <p class="company-address">Don Benito, F.B Harrison, Don B. Hernandez, Pasay City, 1300 Metro Manila</p>
        <p class="company-contact">Email: sales@zuno.ph | Tel: +63 0917-446-9999</p>
      </div>
    </div>


    <h1 class="title">QUOTATION</h1>

    <div v-if="!quotation.quoteNumber" class="loading-state">
      <p>Loading...</p>
    </div>

    <div v-else>
      <!-- 2. 客户和报价元信息 (简约布局) -->
      <div class="quote-meta-info">
        <div class="meta-block">
          <p class="meta-heading">BILLED TO:</p>
          <p class="meta-content customer-name">Customer Name: {{ customer.customerName || '' }}</p>
          <p class="meta-address">Contact Name: {{ customer.contactName || '' }}</p>
          <p class="meta-attention">Address: {{ customer.address || '' }}</p>
        </div>
        <div class="meta-block right-aligned">
          <p><span class="label">Quotation No:</span> <span class="value">{{ quotation.quoteNumber }}</span></p>
          <p><span class="label">Issue Date:</span> <span class="value">{{ quotation.issueDate }}</span></p>
          <p><span class="label">Valid Until:</span> <span class="value">{{ quotation.expiryDate }}</span></p>
          <p class="meta-contact-line"><span class="label">Phone:</span> <span
              class="value">{{ quotation.customerPhone || 'N/A' }}</span></p>
        </div>
      </div>
      <!-- 3. 项目明细表格 -->
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
          <td class="text-right">{{ format(item.unitPrice) }}</td>
          <td class="line-total text-right">{{ format(item.lineTotal) }}</td>
        </tr>
        </tbody>
      </table>

      <!-- 4. 汇总和总计 -->
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
          <div class="total-row tax-row">
            <span class="label">VAT Tax (12%):</span>
            <span class="value">{{ quotation.currency }} {{ format(quotation.taxAmount) }}</span>
          </div>
          <div class="total-row grand-total-row">
            <span class="label">TOTAL AMOUNT  :</span>
            <span class="value">{{ quotation.currency }} {{ format(quotation.total) }}</span>
          </div>
        </div>
      </div>

      <!-- 5. 银行信息和条款 (恢复自原始 PDF) -->
      <div class="terms-and-conditions">
        <h3 class="section-title">Terms and Conditions:</h3>

        <div class="bank-details-section">
          <p><strong>For payment, please remit to:</strong></p>
          <div class="bank-details">
            <p>   <span>#1</span> Name of Payee: <span></span></p>
            <p>Name of Bank: <span></span></p>
            <p>Bank Account No: <span></span></p>

            <p>   <span>#2</span> Name of Payee: <span></span></p>
            <p>Name of Bank: <span></span></p>
            <p>Bank Account No: <span></span></p>

            <p>      <span>#3</span> Name of Payee: <span></span></p>
            <p>Name of Bank: <span></span></p>
            <p>Bank Account No: <span></span></p>
          </div>
        </div>

        <div class="core-terms">
          <ol class="main-list">
            <li>Price are inclusive of VAT</li>
            <li>Payment Terms: <span></span></li>
            <li>Validity: <span></span></li>
            <li>Availability: <span></span></li>
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
            <li>After the warranty period, maintenance or replacement shall be subject to additional service charges.
            </li>
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

      <!-- 6. 签名区域 (双重签名) -->
      <div class="signature-section">
        <div class="signature-box left-box">
          <p class="sincerely">Sincerely,</p>
          <div class="signature-line"></div>
          <p class="company-signature-name">ZUNO GROUP INC. Representative</p>
          <p class="representative-title">Sales Department</p>
        </div>

        <div class="signature-box right-box">
          <p class="acknowledged-header">ACKNOWLEDGED AND AGREED BY:</p>
          <div class="signature-line"></div>
          <p class="customer-name-line"></p>
          <p class="representative-title">Authorized Representative</p>
        </div>
      </div>
    </div>

    <div class="footer-note">
      <p class="thanks">Thank you for your business!</p>
    </div>

    <!-- 7. 导出按钮 -->
    <div class="actions">
      <v-btn color="primary" @click="exportPdf"
             :disabled="!quotation.quoteNumber || ['加载失败', '无ID'].includes(quotation.quoteNumber)">📄 Export PDF
      </v-btn>
    </div>
  </div>
</template>

<script setup>
import jsPDF from 'jspdf'
import api from "../api";
import html2canvas from 'html2canvas'
import { ref } from 'vue'
import { useRoute } from "vue-router";

const route = useRoute();
const quoteId = route.query.quoteId;
const customer = ref({})
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
  discountAmount: 0
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
      };

      quotation.value.items.forEach(item => {
        item.lineTotal = parseFloat(item.lineTotal) || 0;
        item.unitPrice = parseFloat(item.unitPrice) || 0;
        item.packageName = item.packageName || null;
        item.isFromPackage = item.isFromPackage || (item.packageName ? true : false);
      });

      quotation.value.subtotal = parseFloat(quotation.value.subtotal) || 0;
      quotation.value.taxAmount = parseFloat(quotation.value.taxAmount) || 0;
      quotation.value.total = parseFloat(quotation.value.total) || 0;
      quotation.value.discountAmount = parseFloat(quotation.value.discountAmount) || 0;

      api.get(`/customers/${quotation.value.customerId}`).then(response => {
        customer.value = response.data.data;
      });
    } else {
      console.error("API 返回数据结构不正确:", res);
    }
  } catch (e) {
    console.error("加载报价单数据失败:", e);
    quotation.value.quoteNumber = '加载失败';
    alert(`加载报价单 ID ${quoteId} 失败！请查看控制台。`);
  }
} else {
  console.warn("未在查询参数中找到 quoteId！");
  quotation.value.quoteNumber = '无ID';
}

const format = val => {
  if (val === null || val === undefined || isNaN(val)) return '0.00';
  return Number(val).toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 });
};

// --- PDF 导出逻辑（优化体积 + 控制清晰度） ---
const exportPdf = async () => {
  const element = document.getElementById('quotation-template');
  const actionsElement = element?.querySelector('.actions');

  if (!element || !quotation.value.quoteNumber || ['加载失败', '无ID'].includes(quotation.value.quoteNumber)) {
    alert('数据未加载完成或加载失败，无法导出 PDF。');
    return;
  }

  // 隐藏导出按钮
  let originalDisplay = '';
  if (actionsElement) {
    originalDisplay = actionsElement.style.display;
    actionsElement.style.display = 'none';
  }

  try {
    // 截图（压缩清晰度）
    const canvas = await html2canvas(element, {
      scale: 1.0,        // ↓ 清晰度适中（建议 0.9～1.25 之间）
      useCORS: true,
      allowTaint: true,
      backgroundColor: '#ffffff',
      logging: false
    });

    // 恢复导出按钮
    if (actionsElement) actionsElement.style.display = originalDisplay;

    // 压缩为 JPEG（非 PNG）
    const imgData = canvas.toDataURL('image/jpeg', 0.85);
    const pdf = new jsPDF('p', 'mm', 'a4');
    const imgWidth = pdf.internal.pageSize.getWidth();
    const pageHeight = pdf.internal.pageSize.getHeight();
    const imgHeight = (canvas.height * imgWidth) / canvas.width;

    let heightLeft = imgHeight;
    let position = 0;

    pdf.addImage(imgData, 'JPEG', 0, position, imgWidth, imgHeight);
    heightLeft -= pageHeight;

    while (heightLeft >= 0) {
      position = heightLeft - imgHeight;
      pdf.addPage();
      pdf.addImage(imgData, 'JPEG', 0, position, imgWidth, imgHeight);
      heightLeft -= pageHeight;
    }

    pdf.save(`Quotation_${quotation.value.quoteNumber}.pdf`);
  } catch (error) {
    console.error("PDF 导出失败:", error);
    if (actionsElement) actionsElement.style.display = originalDisplay;
    alert("生成 PDF 失败，请检查控制台错误。");
  }
};
</script>


<style scoped>
/* 定义品牌色变量 */
:root {
  --brand-color: #004d99; /* 深蓝色，专业科技感 */
  --accent-color: #007bff; /* 浅蓝 */
  --light-gray: #f9f9f9;
  --medium-gray: #eee;
  --text-dark: #2c3e50;
  --text-light: #6c757d;
}

/* 容器和基础字体 */
.quotation-container {
  max-width: 800px;
  margin: auto;
  background: #fff;
  padding: 30px;
  font-family: 'Inter', 'Arial', sans-serif;
  color: var(--text-dark);
}

/* --- 1. 头部信息 --- */
.header {
  justify-content: space-between;
  align-items: center;
  border-bottom: 5px solid var(--brand-color); /* 品牌色粗线强调 */
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
  align-items: center;
  align-content: center;
  text-align: center;
}

.company-contact {
  margin-top: 5px !important;
  font-weight: bold;
  align-items: center;
  align-content: center;
  text-align: center;
}

/* --- 标题 --- */
.title {
  text-align: left;
  margin-top: 0;
  margin-bottom: 20px;
  font-size: 2.2em;
  color: var(--brand-color);
  font-weight: bold;
  letter-spacing: 2px;
}

/* --- 2. 报价基本信息 --- */
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
  background-color: #f0f7ff; /* 略微不同，区分收件人 */
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

/* --- 3. 表格样式 --- */
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
}

.quote-table tr:last-child td {
  border-bottom: 2px solid var(--brand-color);
}

/* 描述列 */
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

/* --- 4. 汇总样式 --- */
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

/* --- 5. 银行信息和条款 (恢复) --- */
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

.bank-details-section {
  background-color: var(--light-gray);
  padding: 15px;
  border-radius: 5px;
  margin-bottom: 20px;
}

.bank-details-section strong {
  font-size: 1.1em;
  color: var(--text-dark);
}

.bank-details p {
  margin: 3px 0;
  padding-left: 20px;
  color: #333;
}

.bank-details span {
  font-weight: bold;
  color: var(--brand-color);
}

.core-terms ol {
  margin-left: 0;
  padding-left: 20px;
}

.main-list li {
  font-weight: bold;
  color: var(--text-dark);
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


/* --- 6. 签名区域 --- */
/* --- 6. 签名区域 --- */
.signature-section {
  display: flex;
  justify-content: space-between;
  margin-top: 60px;
  padding-top: 20px;
  border-top: 1px dashed #ccc;
}

.signature-box {
  flex-basis: 48%;
  padding-top: 15px;
  text-align: center; /* 统一为居中，让两边对齐更统一 */
}

/* 左侧签名区整体左对齐 */
.left-box {
  text-align: left;
}

/* Sales Department 左对齐 */
.left-box .representative-title {
  text-align: left;
}


.sincerely {
  font-weight: 500;
  font-size: 1.1em;
  margin-bottom: 40px;
}

.company-signature-name,
.customer-name-line {
  margin: 0 auto;
  font-size: 1.1em;
  font-weight: bold;
  color: #000;
  width: 80%;
  padding-bottom: 3px;
  text-align: center;
  text-transform: uppercase;
}

.representative-title {
  font-size: 0.9em;
  color: var(--text-light);
  margin-top: 5px;
  text-align: center; /* 统一对齐 */
}

.right-box {
  text-align: center;
}

.acknowledged-header {
  font-weight: bold;
  color: var(--text-dark);
  margin: 0 auto 20px auto;
  border-bottom: 1px solid #000;
  width: 80%;
  padding-bottom: 5px;
}

.signature-line {
  border-bottom: 2px solid #000;
  width: 80%;
  margin: 0 auto 5px auto;
}

.customer-name-line {
  margin-top: 5px;
}


/* 底部感谢 */
.footer-note {
  text-align: center;
  margin-top: 30px;
}

.footer-note .thanks {
  font-weight: bold;
  font-size: 1.3em;
  color: var(--brand-color);
}

/* 导出按钮 */
.actions {
  text-align: center;
  margin-top: 30px;
  padding: 15px;
  border-top: 1px dashed #ccc;
}

.loading-state {
  text-align: center;
  padding: 50px;
  font-size: 1.2em;
  color: #999;
}
</style>
