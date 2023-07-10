package study.datajpa.dto;


import lombok.Data;
import study.datajpa.entity.Member;

@Data
public class MemberDto {
    private Long id;
    private String username;
    private String teamName;

    public MemberDto(Long id, String username, String teamName) {
        this.id = id;
        this.username = username;
        this.teamName = teamName;
    }

    /* Entity는 dto를 모르게, dto는 entity를 바라보게 하면 좋다. */
    public MemberDto(Member member) {
        this.id = member.getId();
        this.username = member.getUsername();
    }
}
