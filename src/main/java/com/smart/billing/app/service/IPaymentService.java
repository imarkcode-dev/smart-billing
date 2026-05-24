package com.smart.billing.app.service;

import java.util.List;

import com.smart.billing.app.dto.PaymentRequestDTO;
import com.smart.billing.app.dto.PaymentResponseDTO;

public interface IPaymentService {

    List<PaymentResponseDTO> findAllInvoice();

    PaymentResponseDTO findById(Integer id);

    PaymentResponseDTO create(PaymentRequestDTO dto);

    PaymentResponseDTO update(Integer id, PaymentRequestDTO dto);

    void delete(Integer id);



}
