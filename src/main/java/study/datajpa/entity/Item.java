package study.datajpa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item implements Persistable<String> {
    @Id
    private String id;

    @CreatedDate
    private LocalDateTime createdBy;    // 새로운 객체인지 아닌지 판단하는 것@!!

    public Item(String id) {
        this.id = id;
    }

    @Override
    public boolean isNew() {
        // save 메서드에 있는 isNew메서드를 내가 따로 재정의해주어야한다.
        // 이 때 새로운 entity인지 아닌지를뭘로 하느냐? aduiting으로 체크 가능!

        return createdBy == null; /* 매우 좋다 */
    }
}
