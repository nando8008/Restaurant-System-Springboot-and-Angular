package com.example.restaurant.Service;

import com.example.restaurant.DTO.BillRequestDto;
import com.example.restaurant.Entity.*;
import com.example.restaurant.Repository.*;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class BillingService {

    private static final BigDecimal TAX_RATE = BigDecimal.valueOf(0.12);
    private static final BigDecimal SERVICE_CHARGE_RATE = BigDecimal.valueOf(0.10);
    private static final BigDecimal DISCOUNT_RATE = BigDecimal.valueOf(0.10);
    private static final String DISCOUNT_CODE = "KNOWOWNER";

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TablesRepository tablesRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private BillingRepository billingRepository;

    public void generateBill(BillRequestDto dto) {
        List<Order> orders = orderRepository.findByTableIdAndStatus(dto.getTableId(), "COMPLETED");

        if (orders.isEmpty()) {
            throw new RuntimeException("No completed orders found for table " + dto.getTableId());
        }

        List<Integer> orderIds = orders.stream().map(Order::getId).toList();

        List<Billing> existingBills = billingRepository.findByOrderIdIn(orderIds);
        for (Billing bill : existingBills) {
            bill.setPaymentStatus("CANCELLED");
        }
        billingRepository.saveAll(existingBills);

        List<OrderItem> allItems = orders.stream()
                .flatMap(order -> orderItemRepository.findByOrder(order).stream())
                .toList();

        BigDecimal subtotal = allItems.stream()
                .map(item -> {
                    if (item.getFood() == null) throw new RuntimeException("Food item not found for OrderItem ID: " + item.getId());
                    return item.getFood().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal tax = subtotal.multiply(TAX_RATE);
        BigDecimal discount = DISCOUNT_CODE.equalsIgnoreCase(dto.getDiscountCode()) ? subtotal.multiply(DISCOUNT_RATE) : BigDecimal.ZERO;
        BigDecimal serviceCharge = dto.isApplyServiceCharge() ? subtotal.multiply(SERVICE_CHARGE_RATE) : BigDecimal.ZERO;
        BigDecimal total = subtotal.add(tax).add(serviceCharge).subtract(discount);

        Billing bill = new Billing();
        bill.setOrder(orders.getFirst());
        bill.setPlacedById(dto.getPlacedById());
        bill.setPaymentMode(dto.getPaymentMode());
        bill.setPaymentStatus("UNPAID");
        bill.setSubtotal(subtotal);
        bill.setTax(tax);
        bill.setDiscount(discount);
        bill.setServiceCharge(serviceCharge);
        bill.setTotal(total);
        bill.setPaidAt(null);

        billingRepository.save(bill);

    }

    public byte[] generatePdfForTable(Integer tableId, String discountCode, boolean applyServiceCharge) {
        List<Order> orders = orderRepository.findByTableIdAndStatus(tableId, "COMPLETED");

        if (orders.isEmpty()) {
            throw new RuntimeException("No billed orders found for table " + tableId);
        }

        List<OrderItem> items = orders.stream()
                .flatMap(order -> orderItemRepository.findByOrder(order).stream())
                .toList();

        BigDecimal subtotal = items.stream()
                .map(item -> item.getFood().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal tax = subtotal.multiply(TAX_RATE);
        BigDecimal discount = DISCOUNT_CODE.equalsIgnoreCase(discountCode) ? subtotal.multiply(DISCOUNT_RATE) : BigDecimal.ZERO;
        BigDecimal serviceCharge = applyServiceCharge ? subtotal.multiply(SERVICE_CHARGE_RATE) : BigDecimal.ZERO;
        BigDecimal total = subtotal.add(tax).add(serviceCharge).subtract(discount);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4);
        PdfWriter.getInstance(doc, out);
        doc.open();

        Font headingFont = new Font(Font.HELVETICA, 20, Font.BOLD);
        Font regularFont = new Font(Font.COURIER, 12);
        Font boldFont = new Font(Font.COURIER, 12, Font.BOLD);

        String nowFormatted = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"));


        Paragraph title = new Paragraph("LeRestau", headingFont);
        title.setAlignment(Element.ALIGN_CENTER);
        doc.add(title);
        doc.add(new Paragraph("123 Gourmet Street, Flavor Town", regularFont));
        doc.add(new Paragraph("Phone: +91 98765 43210", regularFont));
        doc.add(new Paragraph("GSTIN: 27ABCDE1234F2Z5", regularFont));
        doc.add(new Paragraph("FSSAI: 12345678901234", regularFont));
        doc.add(new Paragraph(" ", regularFont));
        doc.add(new Paragraph("Table No: " + tableId, regularFont));
        doc.add(new Paragraph("Date: " + nowFormatted, regularFont));
        doc.add(new Paragraph("Invoice No: " + orders.get(0).getId(), regularFont));
        doc.add(new Paragraph("------------------------------------------------------------------------", regularFont));


        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setWidths(new float[]{4f, 1f, 2f, 2f});
        table.getDefaultCell().setPadding(5f);
        addTableHeader(table, boldFont, "Item", "Qty", "Price", "Total");

        for (OrderItem item : items) {
            String itemName = item.getFood().getName();
            BigDecimal unitPrice = item.getFood().getPrice();
            int qty = item.getQuantity();
            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(qty));

            PdfPCell nameCell = new PdfPCell(new Phrase(itemName, regularFont));
            nameCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            nameCell.setPadding(5f);
            nameCell.setBorder(PdfPCell.NO_BORDER); // No border
            table.addCell(nameCell);
            table.addCell(createRightAlignedCell("x " + String.valueOf(qty), regularFont));
            table.addCell(createRightAlignedCell("₹" + unitPrice.setScale(2), regularFont));
            table.addCell(createRightAlignedCell("₹" + lineTotal.setScale(2), regularFont));
        }

        doc.add(table);


        PdfPTable totalsTable = new PdfPTable(2);
        totalsTable.setWidthPercentage(100);
        totalsTable.setWidths(new float[]{6f, 2f}); // Label wide, amount narrow

        totalsTable.addCell(createTotalsLabelCell("Subtotal:", regularFont));
        totalsTable.addCell(createTotalsAmountCell(subtotal, regularFont));

        totalsTable.addCell(createTotalsLabelCell("Tax (12%):", regularFont));
        totalsTable.addCell(createTotalsAmountCell(tax, regularFont));

        if (applyServiceCharge) {
            totalsTable.addCell(createTotalsLabelCell("Service Charge (10%):", regularFont));
            totalsTable.addCell(createTotalsAmountCell(serviceCharge, regularFont));
        }

        if (discount.compareTo(BigDecimal.ZERO) > 0) {
            totalsTable.addCell(createTotalsLabelCell("Discount (10%):", regularFont));
            totalsTable.addCell(createTotalsAmountCell(discount.negate(), regularFont));
        }

        totalsTable.addCell(createTotalsLabelCell("Total Amount:", boldFont));
        totalsTable.addCell(createTotalsAmountCell(total, boldFont));

        doc.add(new Paragraph("------------------------------------------------------------------------", regularFont));
        doc.add(totalsTable);

        doc.add(new Paragraph(" ", regularFont));
        doc.add(new Paragraph("Thank you for dining with us!", regularFont));
        doc.add(new Paragraph("Visit again! 😊", regularFont));

        doc.close();
        return out.toByteArray();
    }


    private void addTableHeader(PdfPTable table, Font font, String... headers) {
        for (int i = 0; i < headers.length; i++) {
            PdfPCell cell = new PdfPCell(new Phrase(headers[i], font));
            cell.setBorder(PdfPCell.NO_BORDER);
            if (i == 0)
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            else
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setPadding(5f);
            table.addCell(cell);
        }
    }

    private PdfPCell createRightAlignedCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setPadding(5f);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }

    private Paragraph createAmountLine(String label, BigDecimal amount, Font font) {
        String line = String.format("%-25s %15s", label + ":", "₹" + amount.setScale(2));
        return new Paragraph(line, font);
    }
    private PdfPCell createTotalsLabelCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setPadding(5f);
        return cell;
    }

    private PdfPCell createTotalsAmountCell(BigDecimal amount, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase("₹" + amount.setScale(2), font));
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setPadding(5f);
        return cell;
    }

    @Transactional
    public void payBillForTable(Integer tableId) {
        List<Order> orders = orderRepository.findByTableIdAndStatus(tableId, "COMPLETED");
        if (orders.isEmpty()) throw new RuntimeException("No completed orders found for this table");

        List<Billing> bills = billingRepository.findByOrderIn(orders);
        if (bills.isEmpty()) throw new RuntimeException("No billing record found");

        Billing latestBill = bills.stream()
                .max((a, b) -> a.getId().compareTo(b.getId()))
                .orElseThrow();

        latestBill.setPaymentStatus("PAID");
        latestBill.setPaidAt(java.time.LocalDateTime.now());
        billingRepository.save(latestBill);

        for (Order order : orders) {
            order.setStatus("BILLED");
        }
        orderRepository.saveAll(orders);

        Tables table = orders.get(0).getTable();
        table.setStatus(Tables.Status.NEEDS_ATTENTION);
        tablesRepository.save(table);
    }













    public void generateBillForOrder(Integer orderId, BillRequestDto dto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!"COMPLETED".equals(order.getStatus())) {
            throw new RuntimeException("Order is not completed yet");
        }

        List<OrderItem> items = orderItemRepository.findByOrder(order);

        BigDecimal subtotal = items.stream()
                .map(i -> i.getFood().getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal tax = subtotal.multiply(TAX_RATE);
        BigDecimal discount = DISCOUNT_CODE.equalsIgnoreCase(dto.getDiscountCode()) ? subtotal.multiply(DISCOUNT_RATE) : BigDecimal.ZERO;
        BigDecimal total = subtotal.add(tax).subtract(discount);

        Billing bill = new Billing();
        bill.setOrder(order);
        bill.setPlacedById(dto.getPlacedById());
        bill.setPaymentMode(dto.getPaymentMode());
        bill.setPaymentStatus("UNPAID");
        bill.setSubtotal(subtotal);
        bill.setTax(tax);
        bill.setDiscount(discount);
        bill.setTotal(total);
        bill.setPaidAt(null);

        billingRepository.save(bill);
    }


    public byte[] generatePdfFromBilling(Integer orderId) {
        Billing billing = billingRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("No billing record found for this order"));

        Order order = billing.getOrder();
        List<OrderItem> items = orderItemRepository.findByOrder(order);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4);
        PdfWriter.getInstance(doc, out);
        doc.open();

        Font headingFont = new Font(Font.HELVETICA, 20, Font.BOLD);
        Font regularFont = new Font(Font.COURIER, 12);
        Font boldFont = new Font(Font.COURIER, 12, Font.BOLD);

        String nowFormatted = java.time.LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"));

        doc.add(new Paragraph("LeRestau", headingFont));
        doc.add(new Paragraph("123 Gourmet Street, Flavor Town", regularFont));
        doc.add(new Paragraph("Phone: +91 98765 43210", regularFont));
        doc.add(new Paragraph(" ", regularFont));
        doc.add(new Paragraph("Order ID: " + orderId, regularFont));
        doc.add(new Paragraph("Date: " + nowFormatted, regularFont));
        doc.add(new Paragraph("Invoice No: " + billing.getId(), regularFont));
        doc.add(new Paragraph("------------------------------------------------------------------------", regularFont));

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setWidths(new float[]{4f, 1f, 2f, 2f});
        addTableHeader(table, boldFont, "Item", "Qty", "Price", "Total");

        for (OrderItem item : items) {
            String name = item.getFood().getName();
            int qty = item.getQuantity();
            BigDecimal unitPrice = item.getFood().getPrice();
            BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(qty));

            PdfPCell nameCell = new PdfPCell(new Phrase(name, regularFont));
            nameCell.setBorder(PdfPCell.NO_BORDER);
            nameCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            nameCell.setPadding(5f);

            table.addCell(nameCell);
            table.addCell(createRightAlignedCell("x " + qty, regularFont));
            table.addCell(createRightAlignedCell("₹" + unitPrice.setScale(2), regularFont));
            table.addCell(createRightAlignedCell("₹" + totalPrice.setScale(2), regularFont));
        }

        doc.add(table);

        PdfPTable totalsTable = new PdfPTable(2);
        totalsTable.setWidthPercentage(100);
        totalsTable.setWidths(new float[]{6f, 2f});

        totalsTable.addCell(createTotalsLabelCell("Subtotal:", regularFont));
        totalsTable.addCell(createTotalsAmountCell(billing.getSubtotal(), regularFont));

        totalsTable.addCell(createTotalsLabelCell("Tax (12%):", regularFont));
        totalsTable.addCell(createTotalsAmountCell(billing.getTax(), regularFont));

        if (billing.getServiceCharge() != null && billing.getServiceCharge().compareTo(BigDecimal.ZERO) > 0) {
            totalsTable.addCell(createTotalsLabelCell("Service Charge (10%):", regularFont));
            totalsTable.addCell(createTotalsAmountCell(billing.getServiceCharge(), regularFont));
        }

        if (billing.getDiscount() != null && billing.getDiscount().compareTo(BigDecimal.ZERO) > 0) {
            totalsTable.addCell(createTotalsLabelCell("Discount (10%):", regularFont));
            totalsTable.addCell(createTotalsAmountCell(billing.getDiscount().negate(), regularFont));
        }

        totalsTable.addCell(createTotalsLabelCell("Total Amount:", boldFont));
        totalsTable.addCell(createTotalsAmountCell(billing.getTotal(), boldFont));

        doc.add(new Paragraph("------------------------------------------------------------------------", regularFont));
        doc.add(totalsTable);
        doc.add(new Paragraph(" ", regularFont));
        doc.add(new Paragraph("Thank you for dining with us!", regularFont));
        doc.close();

        return out.toByteArray();
    }





}
