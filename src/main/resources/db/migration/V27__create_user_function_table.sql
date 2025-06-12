-- VXX__create_user_function_table.sql

CREATE TABLE function (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    department_id UUID,
    CONSTRAINT fk_function_department FOREIGN KEY (department_id) REFERENCES department(id)
);


CREATE TABLE user_function (
    user_id UUID NOT NULL,
    function_id UUID NOT NULL,
    PRIMARY KEY (user_id, function_id),
    CONSTRAINT fk_user_function_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_user_function_function FOREIGN KEY (function_id) REFERENCES function(id)
);
