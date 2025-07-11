-- Sample Instruments
INSERT INTO instrument (symbol, company_name, exchange, exchange_type, current_price, lot_size) VALUES
('RELIANCE', 'Reliance Industries Ltd', 'NSE', 'Equity', 2450.50, 1),
('TCS', 'Tata Consultancy Services', 'NSE', 'Equity', 3850.75, 1),
('NIFTY25FEB23000CE', 'NIFTY Call Option', 'NSE', 'Options', 125.50, 50),
('NIFTY25FEB23000PE', 'NIFTY Put Option', 'NSE', 'Options', 95.25, 50),
('USDINR25FEB', 'USD-INR Future', 'NSE', 'Currency', 82.75, 1000),
('GOLD25FEB', 'Gold Future', 'MCX', 'Commodity', 62500.00, 100),
('INFY', 'Infosys Ltd', 'NSE', 'Equity', 1650.25, 1),
('HDFC', 'HDFC Bank Ltd', 'NSE', 'Equity', 1580.00, 1),
('BANKNIFTY25FEB', 'Bank Nifty Future', 'NSE', 'Futures', 43250.75, 25),
('EURINR25FEB', 'EUR-INR Future', 'NSE', 'Currency', 89.45, 1000);

-- Sample Clients
INSERT INTO client (client_code, name, email, phone, pan, kyc_status, status) VALUES
('C001', 'John Doe', 'john.doe@email.com', '9876543210', 'ABCDE1234F', 'COMPLETED', 'ACTIVE'),
('C002', 'Jane Smith', 'jane.smith@email.com', '9876543211', 'FGHIJ5678K', 'COMPLETED', 'ACTIVE');

-- Sample WatchLists
INSERT INTO watchlist (client_id, name, is_default, created_date) VALUES
(1, 'John’s Equity List', TRUE, CURRENT_TIMESTAMP),
(1, 'John’s Options List', FALSE, CURRENT_TIMESTAMP),
(2, 'Jane’s Equity Picks', TRUE, CURRENT_TIMESTAMP);

-- Sample WatchListItems
INSERT INTO watchlist_item (watchlist_id, instrument_id, added_date) VALUES
(1, 1, CURRENT_TIMESTAMP), -- RELIANCE
(1, 2, CURRENT_TIMESTAMP), -- TCS
(2, 3, CURRENT_TIMESTAMP), -- NIFTY Call
(3, 7, CURRENT_TIMESTAMP); -- INFY

-- Sample Orders
INSERT INTO orders (client_id, instrument_id, order_type, quantity, price, status, order_date, validity) VALUES
(1, 1, 'BUY', 10, 2450.50, 'PENDING', CURRENT_TIMESTAMP, 'DAY'),
(1, 2, 'SELL', 5, 3850.75, 'EXECUTED', CURRENT_TIMESTAMP, 'DAY'),
(2, 7, 'BUY', 20, 1650.25, 'PENDING', CURRENT_TIMESTAMP, 'IOC');
