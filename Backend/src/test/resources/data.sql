-- Actors
INSERT INTO public.actor (active, first_name, last_name, last_update)
VALUES  (true, 'John', 'Doe', CURRENT_TIMESTAMP),
       (true, 'Jane', 'Smith', CURRENT_TIMESTAMP),
       (true, 'Michael', 'Johnson', CURRENT_TIMESTAMP);



-- Countries
INSERT INTO public.country (country, active, last_update)
VALUES ('Tunisia', true, CURRENT_TIMESTAMP),
       ('United Kingdom', true, CURRENT_TIMESTAMP),
       ('Canada', true, CURRENT_TIMESTAMP);

-- Languages
INSERT INTO public.language (language_id,name, active, last_update)
VALUES ('lan1','English', true, CURRENT_TIMESTAMP),
       ('lan2','Spanish', true, CURRENT_TIMESTAMP),
       ('lan3','French', true, CURRENT_TIMESTAMP);
-- Addresses
INSERT INTO public.address (address, address2, district, postal_code, phone,country_id, last_update)
VALUES ('123 Main St', 'Apt 101', 'Downtown',  '12345', '555-1234', 1 ,CURRENT_TIMESTAMP),
       ('456 Elm St', NULL, 'Suburbia',  '67890', '555-5678', 1 ,CURRENT_TIMESTAMP),
       ('789 Oak St', 'Unit 3', 'Rural',  '54321', '555-9876', 1, CURRENT_TIMESTAMP);
