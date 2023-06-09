package ssf_assessment.respositories;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RedisRepository {
    @Autowired @Qualifier("login")
    private RedisTemplate<String, Object> template;

    public void timeout(String username){
        
        // this.template.opsForValue().set(username, " ");

        template.opsForValue().set(username, " ");
        template.opsForValue().getAndExpire(username, 30, TimeUnit.SECONDS);
    }

    public boolean isTimeout(String username) {
        return (null != template.opsForValue().get(username));
    }
}
