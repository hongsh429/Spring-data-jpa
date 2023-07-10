package study.datajpa.repository;

import org.springframework.beans.factory.annotation.Value;

public interface UsernameOnly {
    @Value("#{target.username + ' ' + target.age}")  /* 이게 있으면 오픈 프로잭션 / 없으면 클로즈 프로잭션*/
    String getUsername();
}
