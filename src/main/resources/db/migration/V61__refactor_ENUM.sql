ALTER TABLE corporate_phone
    ALTER COLUMN carrier TYPE carrier_type USING carrier::carrier_type,
    ALTER COLUMN plan_type TYPE plan_type USING plan_type::plan_type,
    ALTER COLUMN status TYPE phone_status USING status::phone_status;