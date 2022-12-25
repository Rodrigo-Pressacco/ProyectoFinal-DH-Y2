SELECT acc_trans_aux.name, acc_trans_aux.cvu, MAX(acc_trans_aux.dated) AS last_interaction_date
FROM (SELECT IF(t.account_id = ?1, t.name_destination, t.name_origin) AS name,
			 IF(t.account_id = ?1, t.destination, t.origin) AS cvu,
			 dated
	  FROM transactions t
	  WHERE (t.account_id = ?1 OR t.account_id_destination = ?1) AND
	  t.type = 'Transfer') AS acc_trans_aux
GROUP BY acc_trans_aux.name, acc_trans_aux.cvu
ORDER BY MAX(acc_trans_aux.dated) DESC
LIMIT 5;
