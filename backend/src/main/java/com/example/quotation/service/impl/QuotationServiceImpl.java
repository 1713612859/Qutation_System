package com.example.quotation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.quotation.entity.*;
import com.example.quotation.mapper.*;
import com.example.quotation.service.ApprovalRecordService;
import com.example.quotation.service.QuotationService;
import com.example.quotation.service.SystemSettingsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 报价单服务实现类
 * 负责报价单的CRUD操作、状态流转、金额计算等核心业务逻辑
 * <p>
 * 主要功能：
 * 1. 报价单的创建、更新、删除
 * 2. 从套餐创建报价单
 * 3. 报价单状态流转（提交、审批、拒绝）
 * 4. 报价单金额自动计算（价外税模式）
 * 5. 报价明细项的金额计算
 *
 * @author System
 */
@Service
public class QuotationServiceImpl implements QuotationService {

    @Resource
    private QuotationMapper quotationMapper;
    @Resource
    private QuotationItemMapper quotationItemMapper;
    @Resource
    private PackageMapper packageMapper;
    @Resource
    private PackageItemMapper packageItemMapper;
    @Resource
    private ProductMapper productMapper;
    @Resource
    private CustomerMapper customerMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private ApprovalRecordService approvalRecordService;
    @Resource
    private SystemSettingsService systemSettingsService;

    // 定义精度
    private static final int SCALE = 2;
    private static final int CALCULATE_SCALE = 4; // 计算过程中的精度

    /**
     * 保存报价单（新增或更新）
     * 逻辑：
     * 1. 如果是新增，自动生成报价单号和初始状态
     * 2. 保存报价明细项，并计算每行的金额
     * 3. 计算报价单总价（小计、折扣、税额、总计）
     *
     * @param q 报价单对象
     * @return 保存后的报价单对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Quotation save(Quotation q) {
        if(q.getId() == null) {
            // 新增报价单：生成报价单号、设置初始状态为草稿
            if(!StringUtils.hasText(q.getQuoteNumber())) {
                q.setQuoteNumber(generateQuoteNumber());
            }
            q.setStatus("DRAFT");  //草稿
            q.setCreatedAt(LocalDateTime.now());
            if(q.getIssueDate() == null) {
                q.setIssueDate(LocalDate.now());
            }
            quotationMapper.insert(q);
        } else {
            // 更新报价单：更新更新时间
            q.setCreatedBy(null);
            q.setUpdatedAt(LocalDateTime.now());
            quotationMapper.updateById(q);
        }

        // 保存报价明细项（先删除旧的，再插入新的）
        if(q.getItems() != null && !q.getItems().isEmpty()) {
            // 删除该报价单的所有明细项
            QueryWrapper<QuotationItem> qw = new QueryWrapper<>();
            qw.eq("quotation_id", q.getId());
            quotationItemMapper.delete(qw);

            // 保存新的明细项，并计算每行的金额
            int lineNumber = 1;
            for(QuotationItem item : q.getItems()) {
                item.setQuotationId(q.getId());
                item.setLineNumber(lineNumber++);
                // 计算每行的金额（行小计、税额、行总计）
                calculateLineTotal(item);
                quotationItemMapper.insert(item);
            }
        } else {
            // 如果明细项为空，也需要删除数据库中可能存在的旧明细
            QueryWrapper<QuotationItem> qw = new QueryWrapper<>();
            qw.eq("quotation_id", q.getId());
            quotationItemMapper.delete(qw);
        }

        // 计算报价单总价（汇总所有明细项）
        calculateTotal(q);
        quotationMapper.updateById(q);

        return q;
    }

    /**
     * 从套餐创建报价单
     * 逻辑：
     * 1. 验证套餐和客户是否存在
     * 2. 创建报价单基本信息
     * 3. 从套餐中加载产品明细，转换为报价明细
     * 4. 价格优先级：套餐项价格 > 产品默认价格
     * 5. 税率优先级：套餐项税率 > 产品税率 > 系统默认税率 (均假设为小数形式，如 0.12)
     * 6. 自动计算报价单总价
     *
     * @param packageId  套餐ID
     * @param customerId 客户ID
     * @param userId     创建人ID
     * @return 创建后的报价单对象
     * @throws RuntimeException 如果套餐或客户不存在
     */
    @Override
    @Transactional
    public Quotation createFromPackage(Long packageId, Long customerId, Long userId) {
        // 验证套餐是否存在
        PackageEntity pkg = packageMapper.selectById(packageId);
        if(pkg == null) {
            throw new RuntimeException("Package not found");
        }
        // 验证客户是否存在
        Customer customer = customerMapper.selectById(customerId);
        if(customer == null) {
            throw new RuntimeException("Customer not found");
        }
        // 获取创建人信息
        User user = userMapper.selectById(userId);

        // 创建报价单基本信息
        Quotation quotation = new Quotation();
        quotation.setTitle(pkg.getName());
        quotation.setCustomerId(customerId);
        quotation.setCustomerName(customer.getCompanyName());
        quotation.setCreatedBy(userId);
        quotation.setCreatedByName(user != null ? user.getFullName() : "");
        quotation.setPackageId(packageId);
        quotation.setCurrency(pkg.getCurrency() != null ? pkg.getCurrency() : "PHP");
        quotation.setQuoteNumber(generateQuoteNumber());
        quotation.setStatus("DRAFT");
        quotation.setIssueDate(LocalDate.now());
        quotation.setCreatedAt(LocalDateTime.now());

        quotationMapper.insert(quotation);

        // 从套餐加载产品明细
        QueryWrapper<PackageItem> qw = new QueryWrapper<>();
        qw.eq("package_id", packageId);
        List<PackageItem> packageItems = packageItemMapper.selectList(qw);

        // 将套餐产品转换为报价明细
        List<QuotationItem> quotationItems = new ArrayList<>();
        int lineNumber = 1;

        for(PackageItem pkgItem : packageItems) {
            Product product = productMapper.selectById(pkgItem.getProductId());
            // 只添加启用状态的产品
            if(product != null && (product.getEnabled() == null || product.getEnabled())) {
                QuotationItem item = new QuotationItem();
                item.setQuotationId(quotation.getId());
                item.setLineNumber(lineNumber++);
                item.setProductId(product.getId());
                item.setProductName(product.getName());
                item.setProductSku(product.getSku());
                item.setDescription(product.getDescription());
                item.setQuantity(pkgItem.getQuantity() != null ? pkgItem.getQuantity() : BigDecimal.ONE);
                item.setUnit(product.getUnit());
                // 价格优先级：套餐项价格 > 产品默认价格
                item.setUnitPrice(pkgItem.getUnitPrice() != null ? pkgItem.getUnitPrice() : product.getDefaultPrice());
                item.setDiscount(pkgItem.getDiscount());
                // 税率优先级：套餐项税率 > 产品税率 > 系统默认税率 (假设均为小数，如 0.12)
                item.setTaxRate(pkgItem.getTaxRate() != null ? pkgItem.getTaxRate() : (product.getTaxRate() != null ? product.getTaxRate() : getDefaultTaxRate()));

                // 计算每行的金额
                calculateLineTotal(item);
                quotationItems.add(item);
                quotationItemMapper.insert(item);
            }
        }

        quotation.setItems(quotationItems);
        // 计算报价单总价
        calculateTotal(quotation);
        quotationMapper.updateById(quotation);

        return quotation;
    }

    /**
     * 提交报价单审批
     * 状态流转：DRAFT -> SUBMITTED
     * 逻辑：
     * 1. 验证报价单存在且状态为草稿
     * 2. 更新状态为已提交
     * 3. 记录审批操作历史
     *
     * @param id     报价单ID
     * @param userId 提交人ID
     * @return 更新后的报价单对象
     * @throws RuntimeException 如果报价单不存在或状态不正确
     */
    @Override
    @Transactional
    public Quotation submit(Long id, Long userId) {
        Quotation quotation = quotationMapper.selectById(id);
        if(quotation == null) {
            throw new RuntimeException("Quotation not found");
        }
        // 只能提交草稿状态的报价单
        if(!"DRAFT".equals(quotation.getStatus())) {
            throw new RuntimeException("Only draft quotations can be submitted");
        }

        // 更新状态为已提交
        quotation.setStatus("SUBMITTED");
        quotation.setUpdatedAt(LocalDateTime.now());
        quotationMapper.updateById(quotation);

        // 记录审批操作历史
        User user = userMapper.selectById(userId);
        ApprovalRecord record = new ApprovalRecord();
        record.setQuotationId(id);
        record.setApproverId(userId);
        record.setApproverName(user != null ? user.getFullName() : "");
        record.setAction("SUBMIT");

        record.setComment("Submit for approval");
        approvalRecordService.save(record);

        return quotation;
    }

    /**
     * 审批通过报价单
     * 状态流转：SUBMITTED -> APPROVED
     * 逻辑：
     * 1. 验证报价单存在且状态为已提交
     * 2. 更新状态为已审批通过
     * 3. 记录审批操作历史
     *
     * @param id         报价单ID
     * @param approverId 审批人ID
     * @param comment    审批意见
     * @return 更新后的报价单对象
     * @throws RuntimeException 如果报价单不存在或状态不正确
     */
    @Override
    @Transactional
    public Quotation approve(Long id, Long approverId, String comment) {
        Quotation quotation = quotationMapper.selectById(id);
        if(quotation == null) {
            throw new RuntimeException("Quotation not found");
        }
        // 只能审批已提交状态的报价单
        if(!"SUBMITTED".equals(quotation.getStatus())) {
            throw new RuntimeException("Only submitted quotations can be approved");
        }

        // 更新状态为已审批通过
        quotation.setStatus("APPROVED");
        quotation.setUpdatedAt(LocalDateTime.now());
        quotationMapper.updateById(quotation);

        // 记录审批操作历史
        User user = userMapper.selectById(approverId);
        ApprovalRecord record = new ApprovalRecord();
        record.setQuotationId(id);
        record.setApproverId(approverId);
        record.setApproverName(user != null ? user.getFullName() : "");
        record.setAction("APPROVE");
        record.setComment(comment);
        approvalRecordService.save(record);

        return quotation;
    }

    /**
     * 拒绝报价单
     * 状态流转：SUBMITTED -> REJECTED
     * 逻辑：
     * 1. 验证报价单存在且状态为已提交
     * 2. 更新状态为已拒绝
     * 3. 记录审批操作历史
     * 注意：被拒绝的报价单可以修改后重新提交
     *
     * @param id         报价单ID
     * @param approverId 审批人ID
     * @param comment    拒绝原因
     * @return 更新后的报价单对象
     * @throws RuntimeException 如果报价单不存在或状态不正确
     */
    @Override
    @Transactional
    public Quotation reject(Long id, Long approverId, String comment) {
        Quotation quotation = quotationMapper.selectById(id);
        if(quotation == null) {
            throw new RuntimeException("Quotation not found");
        }
        // 只能拒绝已提交状态的报价单
        if(!"SUBMITTED".equals(quotation.getStatus())) {
            throw new RuntimeException("Only submitted quotations can be rejected");
        }

        // 更新状态为已拒绝
        quotation.setStatus("REJECTED");
        quotation.setUpdatedAt(LocalDateTime.now());
        quotationMapper.updateById(quotation);

        // 记录审批操作历史
        User user = userMapper.selectById(approverId);
        ApprovalRecord record = new ApprovalRecord();
        record.setQuotationId(id);
        record.setApproverId(approverId);
        record.setApproverName(user != null ? user.getFullName() : "");
        record.setAction("REJECT");
        record.setComment(comment);
        approvalRecordService.save(record);

        return quotation;
    }

    @Override
    public List<Quotation> listAll() {

        return quotationMapper.selectList(null);
    }

    @Override
    public List<Quotation> listByStatus(String status) {
        QueryWrapper<Quotation> qw = new QueryWrapper<>();
        qw.eq("status", status);
        qw.orderByDesc("created_at");
        return quotationMapper.selectList(qw);
    }

    @Override
    public List<Quotation> listByCustomerId(Long customerId) {
        QueryWrapper<Quotation> qw = new QueryWrapper<>();
        qw.eq("customer_id", customerId);
        qw.orderByDesc("created_at");
        return quotationMapper.selectList(qw);
    }

    @Override
    public Quotation findById(Long id) {
        Quotation quotation = quotationMapper.selectById(id);
        if(quotation != null) {
            quotation.setItems(listItems(id));
        }
        return quotation;
    }

    @Override
    public List<QuotationItem> listItems(Long quotationId) {
        QueryWrapper<QuotationItem> qw = new QueryWrapper<>();
        qw.eq("quotation_id", quotationId);
        qw.orderByAsc("line_number");
        return quotationItemMapper.selectList(qw);
    }

    /**
     * 计算报价单总价
     * 计算逻辑：
     * 1. 汇总所有明细项的行小计（已含行级折扣，且不含税）得到报价单小计
     * 2. 汇总所有明细项的税额得到报价单总税额
     * 3. 应用报价单级别的折扣（如果有）
     * 4. 总价 = 小计（扣除报价单折扣后）+ 总税额
     *
     * @param quotation 报价单对象（需要先保存明细项）
     */
    @Override
    public void calculateTotal(Quotation quotation) {
        List<QuotationItem> items = listItems(quotation.getId());

        // 如果明细项列表为空，则将金额清零并返回。
        if(CollectionUtils.isEmpty(items)) {
            quotation.setSubtotal(BigDecimal.ZERO.setScale(SCALE, RoundingMode.HALF_UP));
            quotation.setDiscountAmount(BigDecimal.ZERO.setScale(SCALE, RoundingMode.HALF_UP));
            quotation.setTaxAmount(BigDecimal.ZERO.setScale(SCALE, RoundingMode.HALF_UP));
            quotation.setTotal(BigDecimal.ZERO.setScale(SCALE, RoundingMode.HALF_UP));
            return;
        }

        BigDecimal subtotalBeforeQuoteDiscount = BigDecimal.ZERO; // 所有行不含税净价之和
        BigDecimal totalTax = BigDecimal.ZERO;

        // 汇总所有明细项的金额
        for(QuotationItem item : items) {
            // 累计行小计（LineSubtotal 存储的是不含税净价）
            subtotalBeforeQuoteDiscount = subtotalBeforeQuoteDiscount.add(item.getLineSubtotal() != null ? item.getLineSubtotal() : BigDecimal.ZERO);
            // 累计税额（LineTax 存储的是该行的税额）
            totalTax = totalTax.add(item.getLineTax() != null ? item.getLineTax() : BigDecimal.ZERO);
        }

        BigDecimal finalSubtotal = subtotalBeforeQuoteDiscount;
        BigDecimal quoteDiscountAmount = BigDecimal.ZERO;

        // 应用报价单级别的折扣 (注意: 此处折扣逻辑取决于您的业务规则，假设折扣是直接减去一个金额)
        if(quotation.getDiscountAmount() != null && quotation.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
            quoteDiscountAmount = quotation.getDiscountAmount().setScale(SCALE, RoundingMode.HALF_UP);
            // 最终小计 = 汇总行小计 - 报价单级别折扣
            finalSubtotal = finalSubtotal.subtract(quoteDiscountAmount);
        }

        // 总价 = 最终小计 + 总税额
        BigDecimal finalTotal = finalSubtotal.add(totalTax);

        // 设置计算结果，并使用四舍五入
        quotation.setSubtotal(finalSubtotal.setScale(SCALE, RoundingMode.HALF_UP));
        quotation.setDiscountAmount(quoteDiscountAmount.setScale(SCALE, RoundingMode.HALF_UP));
        quotation.setTaxAmount(totalTax.setScale(SCALE, RoundingMode.HALF_UP));
        quotation.setTotal(finalTotal.setScale(SCALE, RoundingMode.HALF_UP));
    }

    /**
     * 删除报价单
     * 逻辑：
     * 1. 验证报价单ID有效性
     * 2. 验证报价单存在
     * 3. 验证报价单状态（只能删除草稿和已拒绝状态的报价单）
     * 4. 删除所有关联的明细项
     * 5. 删除报价单主记录
     *
     * @param id 报价单ID
     * @throws RuntimeException 如果ID无效、报价单不存在或状态不允许删除
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        // 验证ID有效性
        if(id == null) {
            throw new RuntimeException("Invalid ID");
        }
        // 验证报价单存在
        Quotation quotation = quotationMapper.selectById(id);
        if(quotation == null) {
            throw new RuntimeException("Quotation not found");
        }
        // 验证报价单状态：只能删除草稿和已拒绝状态的报价单
        String status = quotation.getStatus();
        if("SUBMITTED".equalsIgnoreCase(status)) {
            throw new RuntimeException("Cannot delete submitted quotations");
        }
        if("APPROVED".equalsIgnoreCase(status)) {
            throw new RuntimeException("Cannot delete approved quotations");
        }
        // 删除所有关联的报价明细项
        QueryWrapper<QuotationItem> qw = new QueryWrapper<>();
        qw.eq("quotation_id", id);
        quotationItemMapper.delete(qw);
        // 删除报价单主记录
        quotationMapper.deleteById(id);
    }

    @Override
    public List<Quotation> listByUserId(Long id) {
        QueryWrapper<Quotation> qw = new QueryWrapper<>();
        qw.eq("created_by", id);
        return quotationMapper.selectList(qw);
    }

    /**
     * 【已修改】计算报价明细行的金额 (价外税模式)
     * 计算逻辑：
     * 1. 原始行不含税小计 = 数量 × 单价
     * 2. 行折扣金额 = 原始行不含税小计 × 折扣百分比 / 100  (统一将折扣视为百分比)
     * 3. 最终行小计（LineSubtotal，不含税净价）= 原始行不含税小计 - 行折扣金额
     * 4. 税额（LineTax） = 最终行小计 × 税率 (假设税率已是小数，如 0.12)
     * 5. 行总计（LineTotal）= 最终行小计 + 税额
     *
     * @param item 报价明细项对象
     */
    private void calculateLineTotal(QuotationItem item) {
        BigDecimal quantity = item.getQuantity() != null ? item.getQuantity() : BigDecimal.ONE;
        BigDecimal unitPrice = item.getUnitPrice() != null ? item.getUnitPrice() : BigDecimal.ZERO;
        // 折扣：假设前端传入的是百分比数值 (如 10 代表 10%)
        BigDecimal discountPercentage = item.getDiscount() != null ? item.getDiscount() : BigDecimal.ZERO;
        // 税率：假设存储的是小数 (如 0.12 代表 12%)
        BigDecimal taxRate = item.getTaxRate() != null ? item.getTaxRate() : BigDecimal.ZERO;

        // 1. 原始行不含税小计 (Raw Net Subtotal)
        BigDecimal rawSubtotal = quantity.multiply(unitPrice).setScale(CALCULATE_SCALE, RoundingMode.HALF_UP);

        BigDecimal finalDiscountAmount = BigDecimal.ZERO;
        BigDecimal lineSubtotal = rawSubtotal; // 默认等于原始小计

        // 2. 计算折扣金额 (统一按百分比处理)
        if(discountPercentage.compareTo(BigDecimal.ZERO) > 0) {
            // 折扣金额 = 原始小计 * 折扣百分比 / 100
            finalDiscountAmount = rawSubtotal.multiply(discountPercentage)
                    .divide(new BigDecimal("100"), CALCULATE_SCALE, RoundingMode.HALF_UP);

            // 最终行不含税净价 = 原始小计 - 折扣金额
            lineSubtotal = rawSubtotal.subtract(finalDiscountAmount).setScale(SCALE, RoundingMode.HALF_UP);
        } else {
            lineSubtotal = lineSubtotal.setScale(SCALE, RoundingMode.HALF_UP);
        }

        // 3. 设置折扣金额和最终行小计（LineSubtotal 为不含税净价）
        item.setDiscountAmount(finalDiscountAmount.setScale(SCALE, RoundingMode.HALF_UP));
        item.setLineSubtotal(lineSubtotal);

        // 4. 计算税额 (价外税模式：税额 = 不含税净价 * 税率)
        // 假设 taxRate 已经是小数 (0.12)，无需再除以 100
        BigDecimal lineTax = lineSubtotal.multiply(taxRate).setScale(SCALE, RoundingMode.HALF_UP);
        item.setLineTax(lineTax);

        // 5. 计算行总计 = 最终行小计 + 税额
        item.setLineTotal(lineSubtotal.add(lineTax).setScale(SCALE, RoundingMode.HALF_UP));
    }

    /**
     * 生成报价单号
     * 格式：QT + 日期（yyyyMMdd）+ 4位序列号
     * 例如：QT202401011234
     *
     * @return 报价单号
     */
    private String generateQuoteNumber() {
        String prefix = "QT";
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        // 使用当前时间戳的后4位作为序列号（简化处理，实际应使用数据库序列）
        String sequence = String.format("%04d", System.currentTimeMillis() % 10000);
        return prefix + dateStr + sequence;
    }

    /**
     * 【已修改】获取系统默认税率 (返回小数形式，如 0.12)
     * 优先级：系统设置 > 默认值0.12
     *
     * @return 默认税率（小数形式，如 0.12）
     */
    private BigDecimal getDefaultTaxRate() {
        String taxRateStr = systemSettingsService.getSetting("default_tax_rate");
        // 如果系统设置存储的是百分比（如 12），则需要转换
        // 如果系统设置存储的是小数（如 0.12），则直接使用
        if(taxRateStr != null) {
            try {
                BigDecimal rate = new BigDecimal(taxRateStr);
                // 假设系统设置存储的是百分比 (如12)，则进行转换
                if (rate.compareTo(BigDecimal.ONE) >= 0) {
                    return rate.divide(new BigDecimal("100"), SCALE, RoundingMode.HALF_UP);
                }
                return rate.setScale(SCALE, RoundingMode.HALF_UP);
            } catch(Exception e) {
                // 忽略解析错误，使用默认值
            }
        }
        return new BigDecimal("0.12"); // 默认 0.12 (12%)
    }
}