-- ============================================================
-- MODULE 2: SALES PIPELINE - SQL SERVER DDL SCRIPT
-- CRM System - SWP391_SE1972_Group3
-- ============================================================

USE CRM;
GO

-- ------------------------------------------------------------
-- 1. PIPELINES
-- ------------------------------------------------------------
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='pipelines' AND xtype='U')
CREATE TABLE pipelines (
    id          INT IDENTITY(1,1) PRIMARY KEY,
    name        NVARCHAR(200) NOT NULL,
    description NVARCHAR(500),
    is_default  BIT DEFAULT 0,
    created_by  INT REFERENCES staffs(id),
    created_at  DATETIME DEFAULT GETDATE()
);
GO

-- ------------------------------------------------------------
-- 2. PIPELINE STAGES
-- ------------------------------------------------------------
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='pipeline_stages' AND xtype='U')
CREATE TABLE pipeline_stages (
    id          INT IDENTITY(1,1) PRIMARY KEY,
    pipeline_id INT NOT NULL REFERENCES pipelines(id) ON DELETE CASCADE,
    stage_name  NVARCHAR(100) NOT NULL,
    order_index INT NOT NULL,
    color       NVARCHAR(20) DEFAULT '#6c757d',
    is_won      BIT DEFAULT 0,
    is_lost     BIT DEFAULT 0
);
GO

-- ------------------------------------------------------------
-- 3. LOST REASONS
-- ------------------------------------------------------------
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='lost_reasons' AND xtype='U')
CREATE TABLE lost_reasons (
    id        INT IDENTITY(1,1) PRIMARY KEY,
    reason    NVARCHAR(300) NOT NULL,
    is_active BIT DEFAULT 1
);
GO

-- ------------------------------------------------------------
-- 4. OPPORTUNITIES
-- ------------------------------------------------------------
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='opportunities' AND xtype='U')
CREATE TABLE opportunities (
    id                   INT IDENTITY(1,1) PRIMARY KEY,
    title                NVARCHAR(300) NOT NULL,
    customer_id          INT REFERENCES customers(id),
    lead_id              BIGINT REFERENCES leads(id),
    assigned_sales_id    INT NOT NULL REFERENCES staffs(id),
    stage                NVARCHAR(100) DEFAULT N'Qualification',
    status               NVARCHAR(50)  DEFAULT N'Open',
    expected_value       DECIMAL(18,2) DEFAULT 0,
    close_probability    FLOAT DEFAULT 0,
    expected_close_date  DATE,
    source               NVARCHAR(100),
    campaign_id          INT REFERENCES campaigns(id),
    pipeline_id          INT REFERENCES pipelines(id),
    lost_reason          NVARCHAR(500),
    notes                NVARCHAR(MAX),
    created_by           INT REFERENCES staffs(id),
    created_at           DATETIME DEFAULT GETDATE(),
    updated_at           DATETIME DEFAULT GETDATE()
);
GO

-- ------------------------------------------------------------
-- 5. PRODUCTS
-- ------------------------------------------------------------
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='products' AND xtype='U')
CREATE TABLE products (
    id          INT IDENTITY(1,1) PRIMARY KEY,
    name        NVARCHAR(300) NOT NULL,
    category    NVARCHAR(100),
    sku         NVARCHAR(50) UNIQUE,
    base_price  DECIMAL(18,2) NOT NULL,
    is_active   BIT DEFAULT 1,
    description NVARCHAR(MAX),
    image_url   NVARCHAR(500)
);
GO

-- ------------------------------------------------------------
-- 6. PRICING RULES
-- ------------------------------------------------------------
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='pricing_rules' AND xtype='U')
CREATE TABLE pricing_rules (
    id         INT IDENTITY(1,1) PRIMARY KEY,
    name       NVARCHAR(200) NOT NULL,
    rule_type  NVARCHAR(50),   -- DISCOUNT / GROUP_PRICE / CAMPAIGN_PRICE
    value      DECIMAL(18,2),
    is_percent BIT DEFAULT 1,
    start_date DATE,
    end_date   DATE,
    is_active  BIT DEFAULT 1
);
GO

-- ------------------------------------------------------------
-- 7. QUOTATIONS
-- ------------------------------------------------------------
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='quotations' AND xtype='U')
CREATE TABLE quotations (
    id               INT IDENTITY(1,1) PRIMARY KEY,
    opportunity_id   INT NOT NULL REFERENCES opportunities(id),
    quotation_code   NVARCHAR(50) UNIQUE,
    version          INT DEFAULT 1,
    status           NVARCHAR(50) DEFAULT N'Draft',
    valid_until      DATE,
    total_amount     DECIMAL(18,2) DEFAULT 0,
    notes            NVARCHAR(MAX),
    created_by       INT REFERENCES staffs(id),
    approved_by      INT REFERENCES staffs(id),
    created_at       DATETIME DEFAULT GETDATE(),
    updated_at       DATETIME DEFAULT GETDATE()
);
GO

-- ------------------------------------------------------------
-- 8. QUOTATION ITEMS
-- ------------------------------------------------------------
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='quotation_items' AND xtype='U')
CREATE TABLE quotation_items (
    id             INT IDENTITY(1,1) PRIMARY KEY,
    quotation_id   INT NOT NULL REFERENCES quotations(id) ON DELETE CASCADE,
    product_id     INT REFERENCES products(id),
    product_name   NVARCHAR(300),
    quantity       INT DEFAULT 1,
    unit_price     DECIMAL(18,2) NOT NULL,
    discount       DECIMAL(5,2) DEFAULT 0,
    tax_rate       DECIMAL(5,2) DEFAULT 0,
    line_total     DECIMAL(18,2)
);
GO

-- ------------------------------------------------------------
-- 9. SALES ORDERS
-- ------------------------------------------------------------
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='sales_orders' AND xtype='U')
CREATE TABLE sales_orders (
    id               INT IDENTITY(1,1) PRIMARY KEY,
    quotation_id     INT REFERENCES quotations(id),
    opportunity_id   INT REFERENCES opportunities(id),
    order_code       NVARCHAR(50) UNIQUE,
    status           NVARCHAR(50) DEFAULT N'Confirmed',
    total_amount     DECIMAL(18,2),
    order_date       DATE DEFAULT CAST(GETDATE() AS DATE),
    delivery_date    DATE,
    shipping_address NVARCHAR(500),
    payment_method   NVARCHAR(100),
    payment_status   NVARCHAR(50) DEFAULT N'Pending',
    created_by       INT REFERENCES staffs(id),
    created_at       DATETIME DEFAULT GETDATE(),
    updated_at       DATETIME DEFAULT GETDATE()
);
GO

-- ------------------------------------------------------------
-- 10. TRANSACTION HISTORY (Customer Core integration)
-- ------------------------------------------------------------
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='transaction_history' AND xtype='U')
CREATE TABLE transaction_history (
    id               INT IDENTITY(1,1) PRIMARY KEY,
    customer_id      INT REFERENCES customers(id),
    order_id         INT REFERENCES sales_orders(id),
    transaction_date DATETIME DEFAULT GETDATE(),
    amount           DECIMAL(18,2),
    type             NVARCHAR(50),
    description      NVARCHAR(500)
);
GO

-- ------------------------------------------------------------
-- SEED DATA: Default Pipeline
-- ------------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM pipelines WHERE is_default = 1)
BEGIN
    INSERT INTO pipelines (name, description, is_default, created_at)
    VALUES (N'Standard Sales Pipeline', N'Quy trình bán hàng chuẩn', 1, GETDATE());

    DECLARE @pid INT = SCOPE_IDENTITY();
    INSERT INTO pipeline_stages (pipeline_id, stage_name, order_index, color, is_won, is_lost) VALUES
        (@pid, N'Qualification',     1, '#6c757d', 0, 0),
        (@pid, N'Need Analysis',     2, '#0dcaf0', 0, 0),
        (@pid, N'Product Proposal',  3, '#0d6efd', 0, 0),
        (@pid, N'Quotation',         4, '#ffc107', 0, 0),
        (@pid, N'Negotiation',       5, '#fd7e14', 0, 0),
        (@pid, N'Closed Won',        6, '#198754', 1, 0),
        (@pid, N'Closed Lost',       7, '#dc3545', 0, 1);
END;
GO

-- SEED: Lost Reasons
IF NOT EXISTS (SELECT 1 FROM lost_reasons WHERE is_active = 1)
BEGIN
    INSERT INTO lost_reasons (reason, is_active) VALUES
        (N'Giá quá cao',                    1),
        (N'Chọn đối thủ cạnh tranh',        1),
        (N'Không có nhu cầu',               1),
        (N'Ngân sách hạn hẹp',              1),
        (N'Sản phẩm không phù hợp',         1),
        (N'Mất liên lạc với khách hàng',    1);
END;
GO

-- SEED: Sample Products (Kitchen equipment)
IF NOT EXISTS (SELECT 1 FROM products)
BEGIN
    INSERT INTO products (name, category, sku, base_price, is_active, description) VALUES
        (N'Nồi áp suất điện Supor 6L',    N'Nồi',    'NOI-001', 1250000, 1, N'Nồi áp suất điện đa năng 6 lít'),
        (N'Chảo chống dính Happycall 28cm',N'Chảo',   'CHA-001', 890000,  1, N'Chảo chống dính cao cấp Hàn Quốc'),
        (N'Máy xay sinh tố Panasonic 2L', N'Máy xay', 'MAY-001', 1650000, 1, N'Máy xay sinh tố công suất 1000W'),
        (N'Bếp từ đôi Sunhouse 4200W',    N'Bếp điện','BEP-001', 2200000, 1, N'Bếp từ đôi công suất cao'),
        (N'Bộ dao nhà bếp 5 món',          N'Phụ kiện','PHU-001', 450000,  1, N'Bộ dao inox không gỉ'),
        (N'Combo bếp thông minh 5 món',    N'Combo',   'COM-001', 5500000, 1, N'Combo đầy đủ thiết bị bếp');
END;
GO
