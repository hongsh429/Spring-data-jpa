package study.datajpa.repository;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    @Query("select m from Member m where m.age > :age")
    List<Member> findByUserGreaterThanAge(@Param("age") int age);


    // NamedQuery
//    @Query(name = "Member.findByUsername") // 없애도 동작한다.
    /*
     1. Member.((NamedQuery)) 를 먼저 찾는다.
     2. Method 이름으로 쿼리를 생성하는 Query Method가 동작한다.

     Named Query는 application 로딩 시점에 parsing 을 하는 과정에서 문법 오류를 찾아준다.
    */
    List<Member> findByUsername(@Param("username") String username);


    // Repository에 JPQL을 바로 쓰는 방법
    /*
    *  이름이 없는 Named Query와 비슷
    * 1. 문법오류를 로딩시점에 잡아준다.
    * */
    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();


    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    /* @Query : 파라미터 바인딩 & 컬렉션 바인딩 */
    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") List<String> names);


    List<Member> findListByUsername(String username); // 컬렉션
    Member findMemberByUsername(String username); // 단건
    Optional<Member> findOptionalByUsername(String username); // 단건 Optional



    /*
    * countQuery는 조인을 할 필요가 없기 때문에! countQuery만 따로 날려줄 수도 있다.
    * 성능 테스트를 거쳐서 이를 해결할 수 있다.
    * */
                                                                    /*이렇게 안하면 기본은 앞의 쿼리 처럼 join 문이 날라감.*/
    @Query(value = "select m from Member m left join m.team t", countQuery = "select count(m.username) from Member m")
    Page<Member> findByAge(int age, Pageable pageable);

    Slice<Member> findAllByAge(int age, Pageable pageable);
    List<Member> findMemberByAge(int age, Pageable pageable);

    /* Top 3 !*/
    List<Member> findTop3ByAgeGreaterThanOrderByAgeDesc(int age);


    /*bulk*/
    @Modifying(clearAutomatically = true)      // 이게 있어야, excuteUpdate();를 실행한다.
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);


    /* fetch join */
    @Query(value = "select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();


    /* @EntityGraph */
    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    // JPQL과 같이 사용 가능
    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    // Query Method와도 사용가능
    @EntityGraph(attributePaths = {"team"})
                    /*find ~~~ By  : ~~ 사이에는 좀 아무값이나 써도 괜춘*/
    List<Member> findEntityGraphByUsername(@Param("username") String username);


    @EntityGraph(value = "Member.all")/*NamedEntityGraph 사용. <Member Entity 참조>*/
    List<Member> findNamedEntityGraphByUsername(@Param("username") String username);


    /* @QueryHints : readOnly 옵션 */
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value="true"))
    Member findReadOnlyByUsername(String username);


    /* @Lock */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);
}
