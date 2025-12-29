local dialog_keys = redis.call('SMEMBERS', KEYS[1])
local dialogs = {}

for i, key in ipairs(dialog_keys) do
    local dialog = redis.call('HGETALL', key)
    table.insert(dialogs, dialog)
end
return dialogs