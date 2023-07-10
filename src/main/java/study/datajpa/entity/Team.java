package study.datajpa.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // proxy 기술을 쓸 때, 사용한다.
@ToString(of = {"id", "name"})
public class Team extends JpaBaseEntity{

    @Id
    @GeneratedValue // 식별자 값을 디비에 알아서 넣어준다.
    @Column(name = "team_id")
    private Long id;
    private String name;

    @OneToMany(mappedBy = "team")
    private List<Member> members = new ArrayList<>();

    public Team(String name) {
        this.name = name;
    }
}
