package study.datajpa.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // proxy 기술을 쓸 때, 사용한다.
public class Member {

    @Id
    @GeneratedValue // 식별자 값을 디비에 알아서 넣어준다.
    private Long id;
    private String username;

    public Member(String username) {
        this.username = username;
    }
}
