CREATE TABLE client (
                        id SERIAL PRIMARY KEY,
                        name VARCHAR(100) NOT NULL,
                        email VARCHAR(150) UNIQUE NOT NULL
);

CREATE TABLE account (
                         id SERIAL PRIMARY KEY,
                         number VARCHAR(50) UNIQUE NOT NULL,
                         balance DECIMAL(15, 2) DEFAULT 0.00 NOT NULL,
                         clientId INTEGER NOT NULL,
                         type VARCHAR(20) NOT NULL CHECK (type IN ('CHECKING', 'SAVINGS')),

                         overdraft DECIMAL(15, 2) DEFAULT 0.00,

                         interest DECIMAL(5, 2) DEFAULT 0.00,

                         CONSTRAINT fk_account_client FOREIGN KEY (clientId)
                             REFERENCES client(id)
                             ON DELETE CASCADE
                             ON UPDATE CASCADE
);

CREATE TABLE transaction (
                             id SERIAL PRIMARY KEY,
                             date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             amount DECIMAL(15, 2) NOT NULL CHECK (amount > 0),
                             type VARCHAR(20) NOT NULL CHECK (type IN ('DEPOSIT', 'WITHDRAWAL', 'TRANSFER')),
                             location VARCHAR(100),
                             accountId INTEGER NOT NULL,

                             CONSTRAINT fk_transaction_account FOREIGN KEY (accountId)
                                 REFERENCES account(id)
                                 ON DELETE CASCADE
                                 ON UPDATE CASCADE
);









