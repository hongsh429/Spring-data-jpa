package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    TeamRepository teamRepository;
    @PersistenceContext
    EntityManager em;


    @Test

    public void testMember() throws Exception {
        // given
        Member member = new Member("memberA");

        // when
        Member savedMember = memberRepository.save(member);
        Member findMember = memberRepository.findById(savedMember.getId())
                .orElseThrow(() -> new IllegalArgumentException("찾는 회원이 없습니다"));

        // then
        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member); // 같은 트랜잭션 안에서는 동일한 객체를 보장한다.
    }

    @Test
    @Rollback(value = false)
    public void basicCRUD() throws Exception {
        // given
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        // 단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        // 다건 조회 검증
        List<Member> members = memberRepository.findAll();
        assertThat(members.size()).isEqualTo(2);

        // 카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        // 삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);
        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThen() throws Exception {
        // given
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("AAA", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);
        assertThat(result.get(0)).isEqualTo(member2);

        List<Member> result2 = memberRepository.findByUserGreaterThanAge(15);
        assertThat(result2.get(0)).isEqualTo(member2);
    }

    @Test
    public void testNamedQueryInSpringDataJpa() throws Exception {
        // given
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("bbb", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findByUsername("AAA");
        assertThat(result.get(0)).isEqualTo(member1);
    }

    @Test
    public void testFindUserQuery() throws Exception {
        // given
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("bbb", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findUser("AAA", 10);
        assertThat(result.get(0)).isEqualTo(member1);
    }

    @Test
    public void testFindUsernameList() throws Exception {
        // given
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("bbb", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<String> result = memberRepository.findUsernameList();
        for (String name : result) {
            System.out.println("name = " + name);
        }
    }

    @Test
    public void testFindMemberDto() throws Exception {
        // given
        Team teamA = new Team("teamA");
        teamRepository.save(teamA);

        Member member1 = new Member("AAA", 10, teamA);
        Member member2 = new Member("bbb", 20, teamA);

        memberRepository.save(member1);
        memberRepository.save(member2);


        List<MemberDto> result = memberRepository.findMemberDto();
        for (MemberDto dto : result) {
            System.out.println("dto = " + dto);
        }
    }

    @Test
    public void testFindByNames() throws Exception {
        // given
        Team teamA = new Team("teamA");
        teamRepository.save(teamA);

        Member member1 = new Member("AAA", 10, teamA);
        Member member2 = new Member("BBB", 20, teamA);

        memberRepository.save(member1);
        memberRepository.save(member2);


        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
        for (Member member : result) {
            System.out.println("member = " + member);
        }
    }

    @Test
    public void returnTypeTest() throws Exception {
        // given
        Team teamA = new Team("teamA");
        teamRepository.save(teamA);

        Member member1 = new Member("AAA", 10, teamA);
        Member member2 = new Member("BBB", 20, teamA);

        memberRepository.save(member1);
        memberRepository.save(member2);


        // 아래의 3개에 대해서 그냥 간단하게 Optional로 해라! 그게 최고임
        List<Member> aaa = memberRepository.findListByUsername("AAA");
        System.out.println("aaa.get(0) = " + aaa.get(0));

        Member member = memberRepository.findMemberByUsername("AAA");
        System.out.println("member = " + member);

        Member member3 = memberRepository.findOptionalByUsername("AAA").get();
        System.out.println("member3 = " + member3);
    }

    @Test
    public void paging() throws Exception {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(
                0, 3, Sort.by(Sort.Direction.DESC, "username")
        );

        // when
        Page<Member> page = memberRepository.findByAge(age, pageRequest);
        /* 반환 타입이 page이니까 알아서 count 쿼리를 알아서 내보낸다. */

        // then
        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements();
        for (Member member : content) {
            System.out.println("member = " + member);
        }
        System.out.println("totalElements = " + totalElements);

        assertThat(content.size()).isEqualTo(3);
        assertThat(totalElements).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0); // 페이지 번호
        assertThat(page.getTotalPages()).isEqualTo(2); // 총페이지 갯수
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }


    @Test
    public void paging2() throws Exception {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(
                0, 3, Sort.by(Sort.Direction.DESC, "username")
        );

        // when
        Slice<Member> page = memberRepository.findAllByAge(age, pageRequest);
        /* 반환 타입이 page이니까 알아서 count 쿼리를 알아서 내보낸다. */

        // then
        List<Member> content = page.getContent();
//        long totalElements = page.getTotalElements();
        for (Member member : content) {
            System.out.println("member = " + member);
        }
//        System.out.println("totalElements = " + totalElements);

        assertThat(content.size()).isEqualTo(3);
//        assertThat(totalElements).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0); // 페이지 번호
//        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }

    @Test
    public void paging3() throws Exception {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(
                0, 3, Sort.by(Sort.Direction.DESC, "username")
        );

        // when
        List<Member> page = memberRepository.findMemberByAge(age, pageRequest);

        /*
        여기까지 보면 pageable로 내보내면 limit offset 옵션을 주어 sql문을 날리지만,
        반환 타입은 내가 그냥 알아서 정할 수 있다.

        대개는 totalCount와 관련된 쿼리에서 성능의 차이가 난다. 만약 Member가 많은 것들과 엮여 있다면,
        totalCount 쿼리가 매우 성능이 낮아질 수있는데, 실제로 count query는 다른 것들과의 조인이 필요없다.
        이를 해결하는 방법도 존재.(MemberRepository 확인)
        */
    }

    @Test
    public void top3() throws Exception {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member1", 12));
        memberRepository.save(new Member("member2", 20));
        memberRepository.save(new Member("member3", 15));
        memberRepository.save(new Member("member4", 30));
        memberRepository.save(new Member("member5", 29));


        List<Member> page = memberRepository.findTop3ByAgeGreaterThanOrderByAgeDesc(12);
        for (Member member : page) {
            System.out.println("member = " + member);
        }

    }

    @Test
    public void bulkUpdate() throws Exception {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 40));
        memberRepository.save(new Member("member5", 30));

        // when
        int resultCount = memberRepository.bulkAgePlus(20);
        List<Member> result = memberRepository.findByUsername("member5");
        Member member = result.get(0);
//        em.clear(); // Spring data JPA는 이걸 해주는 기능이 있다. @Modifying(clearAutomatically = true)

        System.out.println("member.getAge() = " + member.getAge()); // 이전 나이 그대로.. 그래서 바로 flush 해줘야 한다.
        List<Member> result2 = memberRepository.findByUsername("member5");
        Member member2 = result2.get(0);
        System.out.println("member.getAge() = " + member2.getAge()); // 이전 나이 그대로.. 그래서 바로 flush 해줘야 한다.

        // then
        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    public void findMemberLazy() throws Exception {
        // given
        //member1 -> team1
        //member2 -> team2
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        memberRepository.save(new Member("member1", 10, teamA));
        memberRepository.save(new Member("member2", 10, teamB));

        em.flush();
        em.clear();

        // when
        List<Member> members = memberRepository.findAll();

        for (Member member : members) {
            System.out.println("member.getUsername() = " + member.getUsername());
            System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass()); // class study.datajpa.entity.Team$HibernateProxy$eGwQ8bQC
            /* 지연로딩을 위해 가짜 객체(proxy)를 집어넣는다. 그리고 필요한 때에 다시 select 를 날린다.(프록시 초기화라고 말함.) */
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
            /*
             그래서 team 을 이제야 찾으니, team query 가 2번 날라간 것이다.
                이런 문제를 (N + 1)문제(select member 1, select team N 개...)

             다시 돌렸을 때는 왜 join을 날리는가? > findAll()을 오버라이딩해서 @EntityGraph를 넣었기 때문
             <MemberRepository 참고>
            */
        }

        // then
    }

    @Test
    public void findMemberFetchJoin() throws Exception {
        // given
        //member1 -> team1
        //member2 -> team2
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        memberRepository.save(new Member("member1", 10, teamA));
        memberRepository.save(new Member("member2", 10, teamB));

        em.flush();
        em.clear();

        // when
        List<Member> members = memberRepository.findMemberFetchJoin();
        /*
        * N + 1 문제 해결 방법!
        *       fetch 조인 사용!
        *       여기선 가짜 Team 객체(proxy)를 사용하지 않고 처음 한번 join문을 날린다.
        *       fetch join이란? > 연관관계에 있는 것을 fetch join한 객체를 한방에 select 한다.
        * */
        for (Member member : members) {
            System.out.println("member.getUsername() = " + member.getUsername());
            System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass());
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());

        }

        // then
    }

    @Test
    public void findEntityGraphFetchJoin() throws Exception {
        // given
        //member1 -> team1
        //member2 -> team2
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        memberRepository.save(new Member("member1", 10, teamA));
        memberRepository.save(new Member("member1", 10, teamB));

        em.flush();
        em.clear();

        // when
        List<Member> members = memberRepository.findEntityGraphByUsername("member1");

        for (Member member : members) {
            System.out.println("member.getUsername() = " + member.getUsername());
            System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass());
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());

        }

        // then
    }

    @Test
    public void findNamedEntityGraphFetchJoin() throws Exception {
        // given
        //member1 -> team1
        //member2 -> team2
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        memberRepository.save(new Member("member1", 10, teamA));
        memberRepository.save(new Member("member1", 10, teamB));

        em.flush();
        em.clear();

        // when
        List<Member> members = memberRepository.findNamedEntityGraphByUsername("member1");

        for (Member member : members) {
            System.out.println("member.getUsername() = " + member.getUsername());
            System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass());
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());

        }

        // then
    }

    @Test
    public void queryHint() throws Exception {
        // given
        Member member1 = memberRepository.save(new Member("member1", 10));
        em.flush(); // 여기서 sql 지연 저장소에 쿼리 날라감.
        em.clear(); // 영속성 컨텍스트를 날림

        // when
        Member findMember = memberRepository.findReadOnlyByUsername(member1.getUsername()); // get() 원래는 하면 안돼!
        findMember.setUsername("member2");  // 이래 놓고 끝나면 commit 될 때, update 쿼리 나간다.
         /*
         ↑ 이럴 때, 변경감지를 위해 스넵샷(초기상태)을 저장하여 변경된 내용과 비교한다.
            하지만, @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value="true"))를 주면,
            스넵샷을 비워두어 메모리 최적화를 하면서 조회를 한다.

            그러나,,, 진짜 실시간 조회 트레픽이 진짜 많으면, 어쩔 수 없이 redis를 깔아야한다...
            기승전 Redis...
         */



        // then
    }

    @Test
    public void findLock() throws Exception {
        // given
        //member1 -> team1
        //member2 -> team2
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        memberRepository.save(new Member("member1", 10, teamA));
        memberRepository.save(new Member("member1", 10, teamB));

        em.flush();
        em.clear();

        // when
        List<Member> result = memberRepository.findLockByUsername("member1");
//        select
//        m1_0.member_id,
//                m1_0.age,
//                m1_0.team_id,
//                m1_0.username
//        from
//        member m1_0
//        where
       /* m1_0.username=? for update */ // transaction과 Lock. 이건 좀 어렵당...ㅋ
/*
실시간 트레픽이 많은 서비스에서는 락은 좀 피해라.. 현재는 이해가 안되지만 ㅋㅋ
*/

    }

}