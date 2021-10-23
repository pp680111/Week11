local v = redis.call('get', KEYS[1])
if v and tonumber(v) >= ARGV[1] then
    return redis.call("decrby", KEYS[1], ARGV[1])
else
	return 0
end