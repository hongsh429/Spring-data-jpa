package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import study.datajpa.entity.Member;

import java.util.List;

@RequiredArgsConstructor
    /* 클래스 이름은 ~~Impl 이라고 하자! 그러면 알아서 SpringBoot가 해준다. */
public class MemberRepositoryImpl implements MemberRepositoryCustom {

//    @PersistenceContext  --> 이걸 사용해서 해도 된다~
    private final EntityManager em;
    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m")
                .getResultList();
    }
}
