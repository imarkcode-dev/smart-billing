INSERT INTO login_user
(email, password_hash, user_name, last_name, auth_provider, google_id, role_user, created_at, updated_at, last_login)
VALUES(1, 'anakin@java.com', '$2a$10$YmCAQoEMWek2G.HgqZAMoejxjGAfMsm0gxxiRKdbi1qXoOIvJ0njS', 'Anakin', 'Skywalker', 'LOCAL', NULL, 'ROLE_ADMIN', '2026-05-08 14:54:58.952', '2026-05-08 14:54:58.952', '2026-05-08 14:54:58.947')
;

INSERT INTO login_user
(email, password_hash, user_name, last_name, auth_provider, google_id, role_user, created_at, updated_at, last_login)
VALUES(2, 'obiwan_kenobi@spring.com', '$2a$10$o23wGhzcCspyBe0MVTb6bepW1yUs53LyL8k0o52gF5ctTJ2EJPE4q', 'Obiwan', 'Kenobi', 'LOCAL', NULL, 'ROLE_USER', '2026-05-08 14:59:05.977', '2026-05-08 14:59:05.977', '2026-05-08 14:59:05.976');


/* Customers */
INSERT INTO public.customer
(tax_id, name_customer, email, phone, risk_score, status, created_at, updated_at)
VALUES('RFC123456789', 'Advanced Technologies Inc', 'contacto@adtech.com', '9991234567', 0.00, 'ACTIVE', '2026-05-08 22:52:22.784', '2026-05-08 22:52:22.784');
INSERT INTO public.customer
(tax_id, name_customer, email, phone, risk_score, status, created_at, updated_at)
VALUES('RFC993456789', 'SystemDev', 'contacto@systemdev.com', '7771234567', 0.00, 'ACTIVE', '2026-05-08 23:11:16.614', '2026-05-08 23:11:16.614');
INSERT INTO public.customer
(tax_id, name_customer, email, phone, risk_score, status, created_at, updated_at)
VALUES('RFC223456789', 'NetCore', 'contacto@netcore.com', '2221234561', 0.00, 'ACTIVE', '2026-05-08 23:11:16.614', '2026-05-08 23:11:16.614');
INSERT INTO public.customer
(tax_id, name_customer, email, phone, risk_score, status, created_at, updated_at)
VALUES('TAX-MX-1001', 'Alpha Technologies SA de CV', 'billing@alpha.mx', '4421234567', 12.50, 'ACTIVE', '2026-01-02 09:00:00.000', '2026-05-14 09:27:41.895');
INSERT INTO public.customer
(tax_id, name_customer, email, phone, risk_score, status, created_at, updated_at)
VALUES('TAX-MX-1002', 'Logística del Bajío', 'pagos@logbajio.com', '4429876543', 45.80, 'ACTIVE', '2026-01-05 10:30:00.000', '2026-05-14 09:27:41.895');
INSERT INTO public.customer
(tax_id, name_customer, email, phone, risk_score, status, created_at, updated_at)
VALUES('TAX-MX-1003', 'Soluciones de Software Querétaro', 'admin@ssq.mx', '4425551234', 85.00, 'ACTIVE', '2026-01-10 11:15:00.000', '2026-05-14 09:27:41.895');
INSERT INTO public.customer
(tax_id, name_customer, email, phone, risk_score, status, created_at, updated_at)
VALUES('TAX-MX-1004', 'Industrias Metalúrgicas Altiplano', 'finance@indaltiplano.com', '4428889900', 5.00, 'ACTIVE', '2026-01-15 08:45:00.000', '2026-05-14 09:27:41.895');
INSERT INTO public.customer
(tax_id, name_customer, email, phone, risk_score, status, created_at, updated_at)
VALUES('TAX-MX-1005', 'Comercializadora del Centro', 'contacto@comcentro.mx', '4424443322', 28.30, 'ACTIVE', '2026-02-01 14:00:00.000', '2026-05-14 09:27:41.895');


/* Contracts */
INSERT INTO public.contract
(customer_id, title, start_date, end_date, monthly_fee, currency, status, created_at, updated_at)
VALUES(1, 'Service Contract 1', '2026-05-01', '2027-05-01', 1500.00, 'USD', 'ACTIVE', '2026-05-09 16:02:15.972', '2026-05-09 16:02:15.972');
INSERT INTO public.contract
(customer_id, title, start_date, end_date, monthly_fee, currency, status, created_at, updated_at)
VALUES(1, 'Service Contract 2', '2026-05-12', NULL, 1.00, 'USD', 'ACTIVE', '2026-05-12 00:18:35.802', '2026-05-12 00:26:08.375');
INSERT INTO public.contract
(customer_id, title, start_date, end_date, monthly_fee, currency, status, created_at, updated_at)
VALUES(3, 'Service Contract 3', '2026-05-12', NULL, 1.00, 'USD', 'ACTIVE', '2026-05-12 00:31:19.780', '2026-05-12 00:31:19.780');
INSERT INTO public.contract
(customer_id, title, start_date, end_date, monthly_fee, currency, status, created_at, updated_at)
VALUES(1, 'Service Contract 4', '2026-05-01', '2027-05-01', 5500.00, 'USD', 'ACTIVE', '2026-05-09 16:02:15.972', '2026-12-09 16:02:15.972');
INSERT INTO public.contract
(customer_id, title, start_date, end_date, monthly_fee, currency, status, created_at, updated_at)
VALUES(17, 'Service Contract X1', '2026-05-01', '2027-05-01', 6600.00, 'USD', 'ACTIVE', '2026-05-09 16:02:15.972', '2026-12-09 16:02:15.972');
INSERT INTO public.contract
(customer_id, title, start_date, end_date, monthly_fee, currency, status, created_at, updated_at)
VALUES(18, 'Soporte Cloud Enterprise', '2026-01-05', '2026-12-31', 45000.00, 'MXN', 'ACTIVE', '2026-01-05 10:00:00.000', '2026-05-14 09:29:05.949');
INSERT INTO public.contract
(customer_id, title, start_date, end_date, monthly_fee, currency, status, created_at, updated_at)
VALUES(19, 'Distribución Dedicada Ene-May', '2026-01-10', '2026-05-31', 32000.00, 'MXN', 'ACTIVE', '2026-01-10 12:00:00.000', '2026-05-14 09:29:05.949');
INSERT INTO public.contract
(customer_id, title, start_date, end_date, monthly_fee, currency, status, created_at, updated_at)
VALUES(20, 'Fábrica de Software - Fase 1', '2026-01-15', '2026-06-15', 75000.00, 'MXN', 'ACTIVE', '2026-01-15 13:00:00.000', '2026-05-14 09:29:05.949');
INSERT INTO public.contract
(customer_id, title, start_date, end_date, monthly_fee, currency, status, created_at, updated_at)
VALUES(21, 'Mantenimiento de Maquinaria', '2026-01-20', '2027-01-20', 18000.00, 'USD', 'ACTIVE', '2026-01-20 09:30:00.000', '2026-05-14 09:29:05.949');
INSERT INTO public.contract
(customer_id, title, start_date, end_date, monthly_fee, currency, status, created_at, updated_at)
VALUES(22, 'Consultoría TI Mensual', '2026-02-05', '2026-08-05', 25000.00, 'MXN', 'ACTIVE', '2026-02-05 15:00:00.000', '2026-05-14 09:29:05.949');


/* Invoices */

INSERT INTO public.invoice
(contract_id, invoice_number, issue_date, due_date, total_amount, penalty_amount, status, created_at, updated_at)
VALUES(1, 'INV-2026-001', '2026-05-10 00:00:00.000', '2026-05-25 00:00:00.000', 1500.00, 0.00, 'PAID', '2026-05-09 23:02:01.687', '2026-05-10 14:48:56.984');
INSERT INTO public.invoice
(contract_id, invoice_number, issue_date, due_date, total_amount, penalty_amount, status, created_at, updated_at)
VALUES(1, 'INV-2026-002', '2026-04-10 00:00:00.000', '2026-06-25 00:00:00.000', 2000.00, 0.00, 'PAID', '2026-05-09 23:02:01.687', '2026-05-10 14:48:56.984');
INSERT INTO public.invoice
(contract_id, invoice_number, issue_date, due_date, total_amount, penalty_amount, status, created_at, updated_at)
VALUES(1, 'INV-2026-ZY01-REV', '2026-05-01 10:30:00.000', '2026-05-31 23:59:00.000', 9200.00, 15.50, 'PAID', '2026-05-13 12:00:21.477', '2026-05-13 20:07:13.292');
INSERT INTO public.invoice
(contract_id, invoice_number, issue_date, due_date, total_amount, penalty_amount, status, created_at, updated_at)
VALUES(4, 'INV-2026-XYZ', '2026-04-30 06:00:00.000', '2026-06-30 06:00:00.000', 8500.00, 8.00, 'PAID', '2026-05-09 23:02:01.687', '2026-05-13 20:08:02.732');
INSERT INTO public.invoice
(contract_id, invoice_number, issue_date, due_date, total_amount, penalty_amount, status, created_at, updated_at)
VALUES(14, 'FAC-2026-001', '2026-01-06 08:00:00.000', '2026-01-21 18:00:00.000', 45000.00, 0.00, 'PAID', '2026-01-06 08:00:00.000', '2026-05-14 09:31:03.321');
INSERT INTO public.invoice
(contract_id, invoice_number, issue_date, due_date, total_amount, penalty_amount, status, created_at, updated_at)
VALUES(15, 'FAC-2026-002', '2026-01-11 09:00:00.000', '2026-01-26 18:00:00.000', 32000.00, 0.00, 'PAID', '2026-01-11 09:00:00.000', '2026-05-14 09:31:03.321');
INSERT INTO public.invoice
(contract_id, invoice_number, issue_date, due_date, total_amount, penalty_amount, status, created_at, updated_at)
VALUES(16, 'FAC-2026-003', '2026-01-16 10:00:00.000', '2026-01-31 18:00:00.000', 75000.00, 1500.00, 'PAID', '2026-01-16 10:00:00.000', '2026-05-14 09:31:03.321');
INSERT INTO public.invoice
(contract_id, invoice_number, issue_date, due_date, total_amount, penalty_amount, status, created_at, updated_at)
VALUES(14, 'FAC-2026-004', '2026-02-05 08:00:00.000', '2026-02-20 18:00:00.000', 45000.00, 0.00, 'PAID', '2026-02-05 08:00:00.000', '2026-05-14 09:31:03.321');
INSERT INTO public.invoice
(contract_id, invoice_number, issue_date, due_date, total_amount, penalty_amount, status, created_at, updated_at)
VALUES(15, 'FAC-2026-005', '2026-02-10 09:00:00.000', '2026-02-25 18:00:00.000', 32000.00, 0.00, 'PAID', '2026-02-10 09:00:00.000', '2026-05-14 09:31:03.321');
INSERT INTO public.invoice
(contract_id, invoice_number, issue_date, due_date, total_amount, penalty_amount, status, created_at, updated_at)
VALUES(17, 'FAC-2026-006', '2026-02-20 08:30:00.000', '2026-03-07 18:00:00.000', 18000.00, 0.00, 'PAID', '2026-02-20 08:30:00.000', '2026-05-14 09:31:03.321');
INSERT INTO public.invoice
(contract_id, invoice_number, issue_date, due_date, total_amount, penalty_amount, status, created_at, updated_at)
VALUES(14, 'FAC-2026-007', '2026-03-05 08:00:00.000', '2026-03-20 18:00:00.000', 45000.00, 0.00, 'PAID', '2026-03-05 08:00:00.000', '2026-05-14 09:31:03.321');
INSERT INTO public.invoice
(contract_id, invoice_number, issue_date, due_date, total_amount, penalty_amount, status, created_at, updated_at)
VALUES(16, 'FAC-2026-008', '2026-03-15 10:00:00.000', '2026-03-30 18:00:00.000', 75000.00, 3500.00, 'OVERDUE', '2026-03-15 10:00:00.000', '2026-05-14 09:31:03.321');
INSERT INTO public.invoice
(contract_id, invoice_number, issue_date, due_date, total_amount, penalty_amount, status, created_at, updated_at)
VALUES(18, 'FAC-2026-009', '2026-03-05 14:00:00.000', '2026-03-20 18:00:00.000', 25000.00, 0.00, 'PAID', '2026-03-05 14:00:00.000', '2026-05-14 09:31:03.321');
INSERT INTO public.invoice
(contract_id, invoice_number, issue_date, due_date, total_amount, penalty_amount, status, created_at, updated_at)
VALUES(14, 'FAC-2026-010', '2026-04-05 08:00:00.000', '2026-04-20 18:00:00.000', 45000.00, 0.00, 'PAID', '2026-04-05 08:00:00.000', '2026-05-14 09:32:59.207');
INSERT INTO public.invoice
(contract_id, invoice_number, issue_date, due_date, total_amount, penalty_amount, status, created_at, updated_at)
VALUES(15, 'FAC-2026-011', '2026-04-10 09:00:00.000', '2026-04-25 18:00:00.000', 32000.00, 0.00, 'PENDING', '2026-04-10 09:00:00.000', '2026-05-14 09:32:59.207');
INSERT INTO public.invoice
(contract_id, invoice_number, issue_date, due_date, total_amount, penalty_amount, status, created_at, updated_at)
VALUES(18, 'FAC-2026-012', '2026-05-05 11:00:00.000', '2026-05-20 18:00:00.000', 25000.00, 0.00, 'PENDING', '2026-05-05 11:00:00.000', '2026-05-14 09:32:59.207');


INSERT INTO public.payment
(invoice_id, payment_date, amount_paid, payment_method, reference_number, created_at, updated_at)
VALUES(1, '2026-05-10 14:53:57.170', 2700.00, 'CREDIT_CARD', 'REF-987654', '2026-05-10 14:53:57.170', '2026-05-10 14:53:57.170');
INSERT INTO public.payment
(invoice_id, payment_date, amount_paid, payment_method, reference_number, created_at, updated_at)
VALUES(8, '2026-05-13 22:48:52.967', 400.00, 'TRANSFER', 'TXN-12345', '2026-05-13 22:48:52.941', '2026-05-13 22:48:52.942');
INSERT INTO public.payment
(invoice_id, payment_date, amount_paid, payment_method, reference_number, created_at, updated_at)
VALUES(12, '2026-01-18 14:20:00.000', 45000.00, 'TRANSFER', 'REF-STP-99211', '2026-01-18 14:20:00.000', '2026-05-14 09:36:31.335');
INSERT INTO public.payment
(invoice_id, payment_date, amount_paid, payment_method, reference_number, created_at, updated_at)
VALUES(13, '2026-01-25 11:05:00.000', 32000.00, 'TRANSFER', 'REF-STP-99225', '2026-01-25 11:05:00.000', '2026-05-14 09:36:31.335');
INSERT INTO public.payment
(invoice_id, payment_date, amount_paid, payment_method, reference_number, created_at, updated_at)
VALUES(14, '2026-02-02 16:40:00.000', 76500.00, 'CREDIT_CARD', 'REF-AMEX-0012', '2026-02-02 16:40:00.000', '2026-05-14 09:36:31.335');
INSERT INTO public.payment
(invoice_id, payment_date, amount_paid, payment_method, reference_number, created_at, updated_at)
VALUES(15, '2026-02-18 10:15:00.000', 45000.00, 'TRANSFER', 'REF-STP-99344', '2026-02-18 10:15:00.000', '2026-05-14 09:36:31.335');
INSERT INTO public.payment
(invoice_id, payment_date, amount_paid, payment_method, reference_number, created_at, updated_at)
VALUES(16, '2026-02-24 12:00:00.000', 32000.00, 'TRANSFER', 'REF-STP-99390', '2026-02-24 12:00:00.000', '2026-05-14 09:36:31.335');
INSERT INTO public.payment
(id, invoice_id, payment_date, amount_paid, payment_method, reference_number, created_at, updated_at)
VALUES(17, '2026-03-02 09:30:00.000', 18000.00, 'TRANSFER', 'REF-WIRE-8812', '2026-03-02 09:30:00.000', '2026-05-14 09:36:31.335');
INSERT INTO public.payment
(invoice_id, payment_date, amount_paid, payment_method, reference_number, created_at, updated_at)
VALUES(18, '2026-03-19 15:10:00.000', 45000.00, 'TRANSFER', 'REF-STP-99412', '2026-03-19 15:10:00.000', '2026-05-14 09:36:31.335');
INSERT INTO public.payment
(invoice_id, payment_date, amount_paid, payment_method, reference_number, created_at, updated_at)
VALUES(19, '2026-03-18 11:00:00.000', 25000.00, 'DEBIT_CARD', 'REF-BANCOP-441', '2026-03-18 11:00:00.000', '2026-05-14 09:36:31.335');
INSERT INTO public.payment
(invoice_id, payment_date, amount_paid, payment_method, reference_number, created_at, updated_at)
VALUES(20, '2026-04-18 13:00:00.000', 45000.00, 'TRANSFER', 'REF-STP-99501', '2026-04-18 13:00:00.000', '2026-05-14 09:36:31.335');