package study.springdatajpa.repository;

import lombok.RequiredArgsConstructor;
import study.springdatajpa.entity.Member;

import javax.persistence.EntityManager;
import java.util.List;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {
    private final EntityManager entityManager;

    @Override
    public List<Member> findMemberCustom() {
        return entityManager.createQuery("select m from Member m", Member.class)
                .getResultList();
    }
}
