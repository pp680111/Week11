if redis.call('get', KEYS[1]) then
    return redis.call('incrby', KEYS[1], ARGV[1])
else
    redis.call('set',KEYS[1], ARGV[1]);
    return tonumber(ARGV[1]);
end