CREATE TABLE IF NOT EXISTS investor (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    username VARCHAR(40) NOT NULL,
    password VARCHAR(200) NOT NULL,
    personal_id INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_investor_personal_id ON investor(personal_id);

-- Create a function to update the updated_at field
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DO $$
BEGIN
    -- Create a trigger to update the updated_at field before any update
    CREATE TRIGGER set_timestamp
    BEFORE UPDATE ON investor
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();
EXCEPTION
    WHEN duplicate_object THEN
        RAISE NOTICE 'Trigger already exists. Ignoring...';
END$$;

CREATE TABLE IF NOT EXISTS resource (
    id SERIAL PRIMARY KEY,
    code VARCHAR(40) NOT NULL,
    description VARCHAR(200) NOT NULL,
    investor_id INTEGER not null ,
    constraint fk_resource_investor foreign key (investor_id) references investor(id)
);

CREATE INDEX IF NOT EXISTS idx_resource_investor_id ON resource(investor_id);

CREATE TABLE IF NOT EXISTS asset (
    id SERIAL PRIMARY KEY,
    code VARCHAR(40) NOT NULL,
    code_isin VARCHAR(40) NOT NULL,
    description VARCHAR(120) NOT NULL,
    industry VARCHAR(40),
    sector VARCHAR(40),
    segment VARCHAR(40),
    type VARCHAR(40),
    price NUMERIC(16,2)
);

CREATE INDEX IF NOT EXISTS idx_asset_type ON asset(type);
CREATE INDEX IF NOT EXISTS idx_asset_sector ON asset(sector);
CREATE INDEX IF NOT EXISTS idx_asset_segment ON asset(segment);

CREATE TABLE IF NOT EXISTS portfolio (
    id SERIAL PRIMARY KEY,
    investor_id INTEGER NOT NULL,
    name VARCHAR(40) NOT NULL,
    description VARCHAR(120),
    constraint fk_portfolio_investor foreign key (investor_id) references investor(id)
);

CREATE INDEX IF NOT EXISTS idx_portfolio_investor_id ON portfolio(investor_id);

CREATE TABLE IF NOT EXISTS portfolio_position (
    id SERIAL PRIMARY KEY,
    portfolio_id INTEGER NOT NULL,
    code VARCHAR(40) NOT NULL,
    code_isin VARCHAR(40),
    description VARCHAR(120),
    quantity NUMERIC(10,2) NOT NULL,
    price_avarage NUMERIC(10,2),
    constraint fk_portfolio_position_portfolio foreign key (portfolio_id) references portfolio(id)
);

CREATE INDEX IF NOT EXISTS idx_portfolio_position_portfolio_id ON portfolio_position(portfolio_id);