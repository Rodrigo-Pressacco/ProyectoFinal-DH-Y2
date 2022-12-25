DROP PROCEDURE IF EXISTS PRC_GET_FILTERED_TRANSACTIONS;

DELIMITER $$
CREATE PROCEDURE PRC_GET_FILTERED_TRANSACTIONS(
	IN p_account_id BIGINT,
    IN p_type VARCHAR(32),
    IN p_flow VARCHAR(32),
    IN p_min_amount DOUBLE,
    IN p_max_amount DOUBLE,
    IN p_min_date DATE,
    IN p_max_date DATE,
	IN p_limit INT
)
BEGIN
	IF (p_limit IS NOT NULL) THEN
		SELECT * FROM transactions t
		WHERE ( t.account_id = IFNULL(p_account_id, t.account_id)
				OR t.account_id_destination = IFNULL(p_account_id, t.account_id_destination) )
		AND CASE
				WHEN p_flow = 'inflow' THEN t.account_id_destination = IFNULL(p_account_id, t.account_id_destination)
                WHEN p_flow = 'outflow' THEN t.account_id = IFNULL(p_account_id, t.account_id)
                ELSE TRUE
			END
		AND t.type = IFNULL(p_type, t.type)
		AND t.amount >= IFNULL(p_min_amount, t.amount)
		AND t.amount <= IFNULL(p_max_amount, t.amount)
		AND t.dated >= IFNULL(p_min_date, t.dated)
		AND t.dated <= IFNULL(DATE_ADD(p_max_date, INTERVAL 1 DAY), t.dated)
		ORDER BY t.dated DESC
		LIMIT p_limit;
	ELSE
		SELECT * FROM transactions t
		WHERE (	t.account_id = IFNULL(p_account_id, t.account_id)
				OR t.account_id_destination = IFNULL(p_account_id, t.account_id_destination) )
		AND CASE
				WHEN p_flow = 'inflow' THEN t.account_id_destination = IFNULL(p_account_id, t.account_id_destination)
                WHEN p_flow = 'outflow' THEN t.account_id = IFNULL(p_account_id, t.account_id)
                ELSE TRUE
			END
		AND t.type = IFNULL(p_type, t.type)
		AND t.amount >= IFNULL(p_min_amount, t.amount)
		AND t.amount <= IFNULL(p_max_amount, t.amount)
		AND t.dated >= IFNULL(p_min_date, t.dated)
		AND t.dated <= IFNULL(DATE_ADD(p_max_date, INTERVAL 1 DAY), t.dated)
		ORDER BY t.dated DESC;
	END IF;
END $$
DELIMITER ;

