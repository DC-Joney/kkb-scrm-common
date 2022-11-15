local result = tonumber(redis.call('hincrby', KEYS[1], ARGV[1], 1));
redis.call('hset', KEYS[1], result, ARGV[2]);
return result