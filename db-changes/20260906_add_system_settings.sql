INSERT INTO `system_settings`
(`created_time`, `updated_time`, `code`, `description`, `editable_status`, `icon`, `input_type`, `sequence`, `value`, `sync_to_operator`)
VALUES
(NOW(), NOW(), 'ALLOW_UNKNOW_NUMBER', 'Allow unknow number for entry and edit manually', '1', 'bi bi-question-circle', '2', '1', '1', 1),
(NOW(), NOW(), 'FTP_PATH',            'FTP server path used to deliver visitor photos',  '1', 'bi bi-hdd-network',     '1', '2', '', 1),
(NOW(), NOW(), 'FTP_USER',            'FTP username for visitor photo upload',           '1', 'bi bi-person',          '1', '3', '', 1),
(NOW(), NOW(), 'FTP_PASSWORD',        'FTP password for visitor photo upload',           '1', 'bi bi-lock',            '5', '4', '', 1),
(NOW(), NOW(), 'FTP_IMAGE_PATH',      'Remote folder path for visitor image storage',    '1', 'bi bi-image',           '1', '5', '', 1);