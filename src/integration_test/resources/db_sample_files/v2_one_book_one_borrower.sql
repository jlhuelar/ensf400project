;             
CREATE USER IF NOT EXISTS SA SALT '7ab557eb69745223' HASH '8a3855f1eb198976108dae4d0d5cdf5eb907e73321db99c043737af0124852f6' ADMIN;           
CREATE SCHEMA IF NOT EXISTS ADMINISTRATIVE AUTHORIZATION SA;  
CREATE SCHEMA IF NOT EXISTS AUTH AUTHORIZATION SA;            
CREATE SCHEMA IF NOT EXISTS LIBRARY AUTHORIZATION SA;         
CREATE SEQUENCE AUTH.SYSTEM_SEQUENCE_EBB7E93A_A8A2_4BE4_9D42_F602FF467722 START WITH 1 BELONGS_TO_TABLE;      
CREATE SEQUENCE LIBRARY.SYSTEM_SEQUENCE_5EFEED8B_6D6F_4F38_B7F2_220445FE4B5B START WITH 2 BELONGS_TO_TABLE;   
CREATE SEQUENCE LIBRARY.SYSTEM_SEQUENCE_77FCB39D_27DD_4D20_81B0_DB61CDF1A6B0 START WITH 2 BELONGS_TO_TABLE;   
CREATE SEQUENCE LIBRARY.SYSTEM_SEQUENCE_4DC22436_9D2A_4365_80D1_96EAA7B0932E START WITH 1 BELONGS_TO_TABLE;   
CREATE CACHED TABLE ADMINISTRATIVE."flyway_schema_history"(
    "installed_rank" INT NOT NULL,
    "version" VARCHAR(50),
    "description" VARCHAR(200) NOT NULL,
    "type" VARCHAR(20) NOT NULL,
    "script" VARCHAR(1000) NOT NULL,
    "checksum" INT,
    "installed_by" VARCHAR(100) NOT NULL,
    "installed_on" TIMESTAMP DEFAULT CURRENT_TIMESTAMP() NOT NULL,
    "execution_time" INT NOT NULL,
    "success" BOOLEAN NOT NULL
);     
ALTER TABLE ADMINISTRATIVE."flyway_schema_history" ADD CONSTRAINT ADMINISTRATIVE."flyway_schema_history_pk" PRIMARY KEY("installed_rank");    
-- 3 +/- SELECT COUNT(*) FROM ADMINISTRATIVE."flyway_schema_history";         
INSERT INTO ADMINISTRATIVE."flyway_schema_history"("installed_rank", "version", "description", "type", "script", "checksum", "installed_by", "installed_on", "execution_time", "success") VALUES
(0, NULL, '<< Flyway Schema Creation >>', 'SCHEMA', '"ADMINISTRATIVE","LIBRARY","AUTH"', NULL, 'SA', TIMESTAMP '2019-01-31 17:35:59.667', 0, TRUE),
(1, '1', 'Create person table', 'SQL', 'V1__Create_person_table.sql', 2000967190, 'SA', TIMESTAMP '2019-01-31 17:35:59.691', 3, TRUE),
(2, '2', 'Rest of tables for auth and library', 'SQL', 'V2__Rest_of_tables_for_auth_and_library.sql', 1767572289, 'SA', TIMESTAMP '2019-01-31 17:35:59.706', 9, TRUE);         
CREATE INDEX ADMINISTRATIVE."flyway_schema_history_s_idx" ON ADMINISTRATIVE."flyway_schema_history"("success");               
CREATE CACHED TABLE LIBRARY.BORROWER(
    ID INT DEFAULT (NEXT VALUE FOR LIBRARY.SYSTEM_SEQUENCE_5EFEED8B_6D6F_4F38_B7F2_220445FE4B5B) NOT NULL NULL_TO_DEFAULT SEQUENCE LIBRARY.SYSTEM_SEQUENCE_5EFEED8B_6D6F_4F38_B7F2_220445FE4B5B,
    NAME VARCHAR(100) NOT NULL
);   
ALTER TABLE LIBRARY.BORROWER ADD CONSTRAINT LIBRARY.CONSTRAINT_A PRIMARY KEY(ID);             
-- 1 +/- SELECT COUNT(*) FROM LIBRARY.BORROWER;               
INSERT INTO LIBRARY.BORROWER(ID, NAME) VALUES
(1, 'alice');  
CREATE CACHED TABLE LIBRARY.BOOK(
    ID INT DEFAULT (NEXT VALUE FOR LIBRARY.SYSTEM_SEQUENCE_77FCB39D_27DD_4D20_81B0_DB61CDF1A6B0) NOT NULL NULL_TO_DEFAULT SEQUENCE LIBRARY.SYSTEM_SEQUENCE_77FCB39D_27DD_4D20_81B0_DB61CDF1A6B0,
    TITLE VARCHAR(100) NOT NULL
);      
ALTER TABLE LIBRARY.BOOK ADD CONSTRAINT LIBRARY.CONSTRAINT_1 PRIMARY KEY(ID); 
-- 1 +/- SELECT COUNT(*) FROM LIBRARY.BOOK;   
INSERT INTO LIBRARY.BOOK(ID, TITLE) VALUES
(1, 'The DevOps Handbook');       
CREATE CACHED TABLE LIBRARY.LOAN(
    ID INT DEFAULT (NEXT VALUE FOR LIBRARY.SYSTEM_SEQUENCE_4DC22436_9D2A_4365_80D1_96EAA7B0932E) NOT NULL NULL_TO_DEFAULT SEQUENCE LIBRARY.SYSTEM_SEQUENCE_4DC22436_9D2A_4365_80D1_96EAA7B0932E,
    BOOK INT NOT NULL,
    BORROWER INT NOT NULL,
    BORROW_DATE DATE NOT NULL
);    
ALTER TABLE LIBRARY.LOAN ADD CONSTRAINT LIBRARY.CONSTRAINT_2 PRIMARY KEY(ID); 
-- 0 +/- SELECT COUNT(*) FROM LIBRARY.LOAN;   
CREATE CACHED TABLE AUTH."USER"(
    ID INT DEFAULT (NEXT VALUE FOR AUTH.SYSTEM_SEQUENCE_EBB7E93A_A8A2_4BE4_9D42_F602FF467722) NOT NULL NULL_TO_DEFAULT SEQUENCE AUTH.SYSTEM_SEQUENCE_EBB7E93A_A8A2_4BE4_9D42_F602FF467722,
    NAME VARCHAR(100) NOT NULL,
    PASSWORD_HASH VARCHAR(100)
);               
ALTER TABLE AUTH."USER" ADD CONSTRAINT AUTH.CONSTRAINT_2 PRIMARY KEY(ID);       
-- 0 +/- SELECT COUNT(*) FROM AUTH.USER;      
ALTER TABLE LIBRARY.LOAN ADD CONSTRAINT LIBRARY.CONSTRAINT_23B FOREIGN KEY(BORROWER) REFERENCES LIBRARY.BORROWER(ID) NOCHECK; 
ALTER TABLE LIBRARY.LOAN ADD CONSTRAINT LIBRARY.CONSTRAINT_23 FOREIGN KEY(BOOK) REFERENCES LIBRARY.BOOK(ID) NOCHECK;          
