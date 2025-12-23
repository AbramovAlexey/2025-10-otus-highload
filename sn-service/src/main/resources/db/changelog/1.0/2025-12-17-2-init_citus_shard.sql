SELECT create_reference_table('users');
SELECT create_distributed_table('messages', 'conversation_id');