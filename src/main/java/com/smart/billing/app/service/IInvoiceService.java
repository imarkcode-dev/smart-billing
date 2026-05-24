package com.smart.billing.app.service;

import java.util.List;

import com.smart.billing.app.dto.InvoiceRequestDTO;
import com.smart.billing.app.dto.InvoiceResponseDTO;

public interface IInvoiceService {

    List<InvoiceResponseDTO> findAll();

    InvoiceResponseDTO findById(Integer id);

    InvoiceResponseDTO create(InvoiceRequestDTO dto);

    InvoiceResponseDTO update(Integer id, InvoiceRequestDTO dto);

     void delete(Integer id);


}
