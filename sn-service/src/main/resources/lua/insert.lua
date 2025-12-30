for i=1, #ARGV, 2 do
    redis.call('HSET', KEYS[1], ARGV[i], ARGV[i+1])
end

--в "беседу" добавляем сообщение чтобы потом удобно было найти
redis.call('SADD', KEYS[2], KEYS[1])

return "OK"