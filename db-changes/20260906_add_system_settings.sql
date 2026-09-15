INSERT INTO `system_settings`
(`created_time`, `updated_time`, `code`, `description`, `editable_status`, `icon`, `input_type`, `sequence`, `value`, `sync_to_operator`)
VALUES
(NOW(), NOW(), 'ALLOW_UNKNOW_NUMBER', 'Allow unknow number for entry and edit manually', '1', 'bi bi-question-circle', '2', '1', '1', 1),
(NOW(), NOW(), 'FTP_PATH',            'FTP server path used to deliver visitor photos',  '1', 'bi bi-hdd-network',     '1', '2', '', 1),
(NOW(), NOW(), 'FTP_USER',            'FTP username for visitor photo upload',           '1', 'bi bi-person',          '1', '3', '', 1),
(NOW(), NOW(), 'FTP_PASSWORD',        'FTP password for visitor photo upload',           '1', 'bi bi-lock',            '5', '4', '', 1),
(NOW(), NOW(), 'FTP_IMAGE_PATH',      'Remote folder path for visitor image storage',    '1', 'bi bi-image',           '1', '5', '', 1);

INSERT INTO `system_settings`
(`created_time`, `updated_time`, `code`, `description`, `editable_status`, `icon`, `input_type`, `sequence`, `value`, `sync_to_operator`)
VALUES
(NOW(), NOW(), 'AUTH_METHOD_PASSWORD', 'Allow username and password authentication', '1', 'bi bi-key',                    '2', '6', '1', 1),
(NOW(), NOW(), 'AUTH_METHOD_RFID',     'Allow RFID card authentication',             '1', 'bi bi-credit-card-2-front', '2', '7', '1', 1),
(NOW(), NOW(), 'AUTH_METHOD_QR',       'Allow QR code authentication',               '1', 'bi bi-qr-code',              '2', '8', '1', 1),
(NOW(), NOW(), 'AUTH_METHOD_SWIPE',    'Allow magnetic stripe authentication',      '1', 'bi bi-credit-card',          '2', '9', '1', 1),
(NOW(), NOW(), 'AUTH_METHOD_PIN',      'Allow quick PIN authentication',             '1', 'bi bi-shield-lock',          '2', '10', '1', 1);