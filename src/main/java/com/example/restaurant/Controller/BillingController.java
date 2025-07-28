package com.example.restaurant.Controller;

import com.example.restaurant.DTO.BillRequestDto;
import com.example.restaurant.Service.BillingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/billing")
public class BillingController {

    @Autowired
    private BillingService billingService;

    @PostMapping("/generate")
    public ResponseEntity<Map<String, String>> generateBill(@RequestBody BillRequestDto dto) {
        try {
            billingService.generateBill(dto);
            Map<String, String> response = new HashMap<>();
            response.put("message", "✅ Bill generated for table " + dto.getTableId());
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            Map<String, String> error = new HashMap<>();
            error.put("error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/pdf/{tableId}")
    public ResponseEntity<byte[]> getBillPdf(
            @PathVariable Integer tableId,
            @RequestParam(required = false) String discountCode,
            @RequestParam(defaultValue = "true") boolean serviceCharge
    ) {
        try {
            byte[] pdf = billingService.generatePdfForTable(tableId, discountCode, serviceCharge);
            String fileName = "table-" + tableId + "-bill.pdf";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + fileName)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(("Error generating PDF: " + ex.getMessage()).getBytes());
        }
    }

    @PutMapping("/pay/{tableId}")
    public ResponseEntity<String> payBill(@PathVariable Integer tableId) {
        billingService.payBillForTable(tableId);
        return ResponseEntity.ok("Payment successful");
    }

    @GetMapping("/pdf/order/{orderId}")
    public ResponseEntity<byte[]> getPdfForOrder(@PathVariable Integer orderId) {
        try {
            byte[] pdf = billingService.generatePdfFromBilling(orderId);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=order-" + orderId + "-bill.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(("Error generating PDF: " + e.getMessage()).getBytes());
        }
    }

}
