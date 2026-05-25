CREATE TABLE public.login_user (
	id int4 GENERATED ALWAYS AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 2147483647 START 1 CACHE 1 NO CYCLE) NOT NULL,
	email varchar(255) NOT NULL,
	password_hash varchar(255) NULL,
	user_name varchar(255) NOT NULL,
	last_name varchar(255) NOT NULL,
	auth_provider varchar(255) DEFAULT 'local'::character varying NULL,
	google_id varchar(255) NULL,
	role_user varchar(255) DEFAULT 'USER'::character varying NULL,
	created_at timestamp DEFAULT CURRENT_TIMESTAMP NULL,
	updated_at timestamp DEFAULT CURRENT_TIMESTAMP NULL,
	last_login timestamp NULL,
	CONSTRAINT login_user_email_key UNIQUE (email),
	CONSTRAINT login_user_google_id_key UNIQUE (google_id),
	CONSTRAINT login_user_pkey PRIMARY KEY (id)
);

CREATE TABLE public.customer (
	id int4 GENERATED ALWAYS AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 2147483647 START 1 CACHE 1 NO CYCLE) NOT NULL,
	tax_id varchar(255) NOT NULL,
	name_customer varchar(255) NOT NULL,
	email varchar(255) NULL,
	phone varchar(255) NULL,
	risk_score numeric(5, 2) DEFAULT 0.0 NULL,
	status varchar(255) DEFAULT 'ACTIVE'::character varying NULL,
	created_at timestamp DEFAULT CURRENT_TIMESTAMP NULL,
	updated_at timestamp DEFAULT CURRENT_TIMESTAMP NULL,
	CONSTRAINT customer_pkey PRIMARY KEY (id),
	CONSTRAINT customer_tax_id_key UNIQUE (tax_id)
);


CREATE TABLE public.contract (
	id int4 GENERATED ALWAYS AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 2147483647 START 1 CACHE 1 NO CYCLE) NOT NULL,
	customer_id int4 NOT NULL,
	title varchar(255) NULL,
	start_date date NOT NULL,
	end_date date NULL,
	monthly_fee numeric(15, 2) NOT NULL,
	currency varchar(255) DEFAULT 'USD'::character varying NULL,
	status varchar(255) DEFAULT 'ACTIVE'::character varying NULL,
	created_at timestamp DEFAULT CURRENT_TIMESTAMP NULL,
	updated_at timestamp DEFAULT CURRENT_TIMESTAMP NULL,
	CONSTRAINT contract_pkey PRIMARY KEY (id)
);


ALTER TABLE public.contract ADD CONSTRAINT fk_customer FOREIGN KEY (customer_id) REFERENCES public.customer(id) ON DELETE CASCADE;

CREATE TABLE public.invoice (
	id int4 GENERATED ALWAYS AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 2147483647 START 1 CACHE 1 NO CYCLE) NOT NULL,
	contract_id int4 NOT NULL,
	invoice_number varchar(255) NOT NULL,
	issue_date timestamp NOT NULL,
	due_date timestamp NOT NULL,
	total_amount numeric(15, 2) NOT NULL,
	penalty_amount numeric(15, 2) DEFAULT 0.0 NULL,
	status varchar(255) DEFAULT 'PENDING'::character varying NULL,
	created_at timestamp DEFAULT CURRENT_TIMESTAMP NULL,
	updated_at timestamp DEFAULT CURRENT_TIMESTAMP NULL,
	CONSTRAINT invoice_invoice_number_key UNIQUE (invoice_number),
	CONSTRAINT invoice_pkey PRIMARY KEY (id)
);


ALTER TABLE public.invoice ADD CONSTRAINT fk_contract FOREIGN KEY (contract_id) REFERENCES public.contract(id) ON DELETE CASCADE;

CREATE TABLE public.payment (
	id int4 GENERATED ALWAYS AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 2147483647 START 1 CACHE 1 NO CYCLE) NOT NULL,
	invoice_id int4 NOT NULL,
	payment_date timestamp DEFAULT CURRENT_TIMESTAMP NULL,
	amount_paid numeric(15, 2) NOT NULL,
	payment_method varchar(255) NULL,
	reference_number varchar(255) NULL,
	created_at timestamp DEFAULT CURRENT_TIMESTAMP NULL,
	updated_at timestamp DEFAULT CURRENT_TIMESTAMP NULL,
	CONSTRAINT payment_pkey PRIMARY KEY (id)
);


ALTER TABLE public.payment ADD CONSTRAINT fk_invoice FOREIGN KEY (invoice_id) REFERENCES public.invoice(id) ON DELETE CASCADE;