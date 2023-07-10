package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

import java.util.List;


/*
사용자 정의 repository를 무분별하게 사용할 바엔,
이렇게 그냥 하나 따로 만들어서 사용하는 것이 훨씬 유지보수 측면에서 바람직 하다.
*/


@Repository
@RequiredArgsConstructor
public class MemberQueryRepository {

    private final EntityManager em;

    List<Member> findQueryAll() {
        return em.createQuery("select m from Member m")
                .getResultList();
    }
}
