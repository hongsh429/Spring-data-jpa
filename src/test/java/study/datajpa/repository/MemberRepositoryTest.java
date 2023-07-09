package study.datajpa.repository;

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

}