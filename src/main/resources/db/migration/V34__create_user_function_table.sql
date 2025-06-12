CREATE TABLE IF NOT EXISTS user_function (
    user_id UUID NOT NULL,
    function_id UUID NOT NULL,
    PRIMARY KEY (user_id, function_id),
    CONSTRAINT fk_user_function_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_user_function_function FOREIGN KEY (function_id) REFERENCES function(id)
);