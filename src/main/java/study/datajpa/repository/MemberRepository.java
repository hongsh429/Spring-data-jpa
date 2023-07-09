package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);


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
}
