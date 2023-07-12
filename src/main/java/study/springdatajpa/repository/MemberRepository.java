package study.springdatajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.springdatajpa.dto.MemberDto;
import study.springdatajpa.entity.Member;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    // Named Query가 메서드 이름으로 JPQL을 생성하는 것보다 우선순위가 높기 때문에 @Query 애노테이션을 생략해도 된다.
    @Query(name = "Member.findByUsername")
    List<Member> findByUsername(@Param("username") String username);

    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findMembers(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernames();

    @Query("select new study.springdatajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDtos();

    @Query("select m from Member m where m.username in :usernames")
    List<Member> findByUsernames(@Param("usernames") List<String> usernames);

    List<Member> findMemberListByUsername(String username);

    Member findMemberByUsername(String username);

    Optional<Member> findOptionalMemberByUsername(String username);

    //@Query(value = "select m from Member m left join m.team t", countQuery = "select count(m.username) from Member m")
    Page<Member> findByAge(int age, Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

//    @Query("select m from Member m join fetch m.team")
//    List<Member> findMembersWithTeam();

    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMembersWithTeam();

    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Member findLockByUsername(String username);
}
