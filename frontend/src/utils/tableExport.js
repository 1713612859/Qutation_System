/**
 * 表格导出工具函数
 * 提供类似DataTables的导出功能
 */

/**
 * 导出为CSV格式
 * @param {Array} headers - 表头配置数组
 * @param {Array} items - 数据项数组
 * @param {Function} formatter - 数据格式化函数 (可选)
 * @param {String} filename - 文件名
 */
export function exportToCSV(headers, items, formatter = null, filename = 'export') {
  // 过滤掉操作列和隐藏的列
  const visibleHeaders = headers.filter(h => h.key !== 'actions')
  
  // 生成表头行
  const headerRow = visibleHeaders.map(h => h.title).join(',')
  
  // 生成数据行
  const dataRows = items.map(item => {
    return visibleHeaders.map(header => {
      let value = item[header.key]
      
      // 如果有格式化函数，使用格式化函数
      if (formatter && typeof formatter === 'function') {
        value = formatter(header.key, value, item)
      }
      
      // 处理特殊字符，CSV格式需要转义双引号
      const stringValue = String(value || '').replace(/"/g, '""')
      return `"${stringValue}"`
    }).join(',')
  })
  
  // 合并所有行，添加BOM以支持中文
  const csvContent = '\ufeff' + [headerRow, ...dataRows].join('\n')
  
  // 创建Blob并下载
  const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')
  const url = URL.createObjectURL(blob)
  
  link.setAttribute('href', url)
  link.setAttribute('download', `${filename}_${new Date().toISOString().split('T')[0]}.csv`)
  link.style.visibility = 'hidden'
  
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  
  // 释放URL对象
  URL.revokeObjectURL(url)
}

/**
 * 导出为Excel格式（实际上是CSV格式，但用Excel打开）
 * @param {Array} headers - 表头配置数组
 * @param {Array} items - 数据项数组
 * @param {Function} formatter - 数据格式化函数 (可选)
 * @param {String} filename - 文件名
 */
export function exportToExcel(headers, items, formatter = null, filename = 'export') {
  exportToCSV(headers, items, formatter, filename)
}

