package study.datajpa.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // proxy 기술을 쓸 때, 사용한다.
@ToString(of = {"id", "username", "age"}) // toString의 내용. 여기에 team 있다면, toString 무한 루프가 돈다.
@NamedQuery(
        name = "Member.findByUsername",
        query="select m from Member m where m.username= :username"
)
public class Member {

    @Id
    @GeneratedValue // 식별자 값을 디비에 알아서 넣어준다.
    @Column(name = "member_id")
    private Long id;
    private String username;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY)  // Memmber + Team(proxy) -> Team을 조회하면, 그때 team select 쿼리가 날라감.
    @JoinColumn(name = "team_id")
    private Team team;

    public Member(String username) {
        this.username = username;
    }

    public Member(String username, int age, Team team) {
        this.username = username;
        this.age = age;
        if (team != null) {
            changeTeam(team); // 연관관계 세팅 메서드
        }
    }

    public Member(String username, int age) {
        this.username = username;
        this.age = age;
    }

    // == 연관관계 세팅 메서드 == // -> 이건 연관관계의 주인에게 있는 것이 맞다. 왜? 수정이 가능한 곳!
    public void changeTeam(Team team) {
        this.team = team;
        team.getMembers().add(this);
    }
}
