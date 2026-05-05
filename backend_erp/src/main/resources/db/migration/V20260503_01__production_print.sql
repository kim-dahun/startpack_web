create schema if not exists erp_service;

create table if not exists erp_service.erp_processes (
    id bigserial primary key,
    code varchar(50) not null unique,
    name varchar(100) not null,
    process_type varchar(30) not null,
    description varchar(200),
    enabled boolean not null default true
);

create table if not exists erp_service.erp_equipments (
    id bigserial primary key,
    code varchar(50) not null unique,
    name varchar(100) not null,
    equipment_type varchar(30) not null,
    warehouse_id bigint references erp_service.erp_warehouses(id),
    location varchar(200),
    status varchar(20) not null,
    enabled boolean not null default true
);

create table if not exists erp_service.erp_equipment_processes (
    id bigserial primary key,
    equipment_id bigint not null references erp_service.erp_equipments(id),
    process_id bigint not null references erp_service.erp_processes(id),
    enabled boolean not null default true,
    constraint uk_equipment_process unique (equipment_id, process_id)
);

create table if not exists erp_service.erp_routes (
    id bigserial primary key,
    code varchar(50) not null unique,
    name varchar(100) not null,
    item_id bigint references erp_service.erp_items(id),
    enabled boolean not null default true
);

create table if not exists erp_service.erp_route_steps (
    id bigserial primary key,
    route_id bigint not null references erp_service.erp_routes(id),
    sequence_no integer not null,
    process_id bigint not null references erp_service.erp_processes(id),
    default_equipment_id bigint references erp_service.erp_equipments(id),
    standard_lead_time_minutes integer,
    description varchar(200),
    enabled boolean not null default true,
    constraint uk_route_step_sequence unique (route_id, sequence_no)
);

alter table if exists erp_service.erp_production_orders
    add column if not exists route_id bigint references erp_service.erp_routes(id),
    add column if not exists planned_process_id bigint references erp_service.erp_processes(id),
    add column if not exists planned_equipment_id bigint references erp_service.erp_equipments(id);

create table if not exists erp_service.erp_production_order_steps (
    id bigserial primary key,
    production_order_id bigint not null references erp_service.erp_production_orders(id),
    route_step_id bigint references erp_service.erp_route_steps(id),
    sequence_no integer not null,
    process_id bigint not null references erp_service.erp_processes(id),
    planned_equipment_id bigint references erp_service.erp_equipments(id),
    status varchar(20) not null,
    planned_start_at timestamp,
    planned_end_at timestamp
);

alter table if exists erp_service.erp_production_results
    add column if not exists route_id bigint references erp_service.erp_routes(id),
    add column if not exists route_step_id bigint references erp_service.erp_route_steps(id),
    add column if not exists process_id bigint references erp_service.erp_processes(id),
    add column if not exists equipment_id bigint references erp_service.erp_equipments(id),
    add column if not exists work_started_at timestamp,
    add column if not exists work_ended_at timestamp;

create table if not exists erp_service.erp_production_result_steps (
    id bigserial primary key,
    production_result_id bigint not null references erp_service.erp_production_results(id),
    production_order_step_id bigint references erp_service.erp_production_order_steps(id),
    sequence_no integer not null,
    process_id bigint not null references erp_service.erp_processes(id),
    equipment_id bigint references erp_service.erp_equipments(id),
    work_started_at timestamp,
    work_ended_at timestamp
);

create table if not exists erp_service.erp_print_barcodes (
    id bigserial primary key,
    barcode_value varchar(120) not null,
    document_type varchar(40) not null,
    document_key varchar(120) not null,
    enabled boolean not null default true,
    constraint uk_print_barcode_value unique (barcode_value)
);

create index if not exists idx_erp_production_orders_route
    on erp_service.erp_production_orders(route_id);
create index if not exists idx_erp_production_results_process
    on erp_service.erp_production_results(process_id);
create index if not exists idx_erp_print_barcodes_document
    on erp_service.erp_print_barcodes(document_type, document_key);
