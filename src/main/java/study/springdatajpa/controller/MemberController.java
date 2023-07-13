package study.springdatajpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.springdatajpa.dto.MemberDto;
import study.springdatajpa.entity.Member;
import study.springdatajpa.entity.Team;
import study.springdatajpa.repository.MemberRepository;

import javax.annotation.PostConstruct;

@RequiredArgsConstructor
@RestController
public class MemberController {
    private final MemberRepository memberRepository;

//    @PostConstruct
//    public void init() {
//        for (int i = 0; i < 100; i++) {
//            Member member = createMember(String.valueOf(i), 25, null);
//            memberRepository.save(member);
//        }
//    }

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Member member) {
        return member.getUsername();
    }

    @GetMapping("/members")
    public Page<MemberDto> list(@PageableDefault(size = 5) Pageable pageable) {
        return memberRepository.findAll(pageable)
                .map(MemberDto::of);
    }

    private Member createMember(String username, int age, Team team) {
        return Member.builder()
                .username(username)
                .age(age)
                .team(team)
                .build();
    }
}
