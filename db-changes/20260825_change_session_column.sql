ALTER TABLE parking_sessions RENAME COLUMN operator_shift TO entry_shift_id;
ALTER TABLE parking_sessions ADD COLUMN exit_shift_id BIGINT NULL REFERENCES operator_shift(id);