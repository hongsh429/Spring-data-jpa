package study.datajpa.repository;

import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;


/* 결론 : 실무에서 사용하기에는... 다른 좋은게 있다!!!! */
public class MemberSpec {

    public static Specification<Member> teamName(final String teamName) {
        return new Specification<Member>(){
            @Override
            public Predicate toPredicate(Root<Member> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

                if (StringUtils.isEmpty(teamName)) {
                    return null;
                }

                Join<Member, Team> t = root.join("team", JoinType.INNER);
                return criteriaBuilder.equal(t.get("name"), teamName);
            }
        };
    }

    public static Specification<Member> username(final String username) {
        return (Specification<Member>) (root, query, builder)->
            builder.equal(root.get("username"), username);
    }
}
