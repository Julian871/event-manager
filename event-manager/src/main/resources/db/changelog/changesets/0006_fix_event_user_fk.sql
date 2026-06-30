ALTER TABLE events DROP CONSTRAINT fk_event_user;

ALTER TABLE events
    ADD CONSTRAINT fk_event_user FOREIGN KEY (user_id) REFERENCES user_account(id) ON DELETE RESTRICT;