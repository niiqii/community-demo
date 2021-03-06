package com.song.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

/**
 * @author Kycni
 * @date 2022/2/27 8:37
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityDemoApplication.class)
public class RedisTests {
    @Autowired
    private RedisTemplate redisTemplate;
    
    @Test
    public void testString () {
        String redisKey = "test:count";
        redisTemplate.opsForValue().set(redisKey, 1);
        System.out.println(redisTemplate.opsForValue().get(redisKey));
        System.out.println(redisTemplate.opsForValue().increment(redisKey));
        System.out.println(redisTemplate.opsForValue().decrement(redisKey));
    }
    
    @Test
    public void testHash () {
        String redisKey = "test:user";
        redisTemplate.opsForHash().put(redisKey, "id", 10);
        redisTemplate.opsForHash().put(redisKey, "username", "Tom");
        
        System.out.println(redisTemplate.opsForHash().get(redisKey, "username"));
        System.out.println(redisTemplate.opsForHash().get(redisKey, "id"));
    }
    
    @Test
    public void testLists () {
        String redisKey = "test:ids";
        redisTemplate.opsForList().leftPush(redisKey, 101);
        redisTemplate.opsForList().leftPush(redisKey, 102);
        redisTemplate.opsForList().leftPush(redisKey, 103);
        System.out.println(redisTemplate.opsForList().size(redisKey));
        System.out.println(redisTemplate.opsForList().index(redisKey, 0));
        System.out.println(redisTemplate.opsForList().range(redisKey, 0,2));
        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
    }
    
    @Test
    public void testSet () {
        String redisKey = "test:teachers";
        redisTemplate.opsForSet().add(redisKey, "Tom","Lily","Adam");

        System.out.println(redisTemplate.opsForSet().size(redisKey));
        System.out.println(redisTemplate.opsForSet().pop(redisKey));
        System.out.println(redisTemplate.opsForSet().members(redisKey));
    }
    
    @Test
    public void testSortedSets () {
        String redisKey = "test:students";
        
        redisTemplate.opsForZSet().add(redisKey, "?????????", 60);
        redisTemplate.opsForZSet().add(redisKey, "?????????", 50);
        redisTemplate.opsForZSet().add(redisKey, "??????", 35);
        redisTemplate.opsForZSet().add(redisKey, "??????", 90);

        System.out.println(redisTemplate.opsForZSet().size(redisKey));
        System.out.println(redisTemplate.opsForZSet().zCard(redisKey));
        System.out.println(redisTemplate.opsForZSet().score(redisKey,"?????????"));
        System.out.println(redisTemplate.opsForZSet().reverseRank(redisKey,"?????????"));
        System.out.println(redisTemplate.opsForZSet().reverseRange(redisKey, 0, 2));
        
    }
    
    @Test
    public void testKeys() {
        redisTemplate.delete("test:user");
        System.out.println(redisTemplate.hasKey("test:user"));
        redisTemplate.expire("test:students", 10, TimeUnit.SECONDS);
    }
    
    // ?????????????????????Key
    @Test
    public void testBoundOperations () {
        String redisKey = "test:count";
        BoundValueOperations operations = redisTemplate.boundValueOps(redisKey);
        operations.increment();
        operations.increment();
        operations.increment();
        operations.increment();
        operations.increment();
        operations.increment();
        System.out.println(operations.get());
    }
    
    // ???????????????
    @Test
    public void testTransactional() {
        Object object = redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String redisKey = "test:tx";
                // ????????????
                operations.multi();
                
                operations.opsForSet().add(redisKey,"??????","?????????");
                operations.opsForSet().add(redisKey,"??????","asdas");
                operations.opsForSet().add(redisKey,"??????");
                operations.delete(redisKey);
                operations.opsForSet().add(redisKey,"??????","?????????");
                System.out.println(operations.opsForSet().members(redisKey));
                
                return operations.exec();
            }
        });
        System.out.println(object);
    }
}
