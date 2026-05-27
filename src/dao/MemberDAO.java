package dao;

import interfaces.IDao;
import models.Member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object for Member.
 * In-memory store using a HashMap.
 */
public class MemberDAO implements IDao<Member> {

    private Map<String, Member> store = new HashMap<>();

    @Override
    public void add(Member member) {
        if (store.containsKey(member.getMemberId())) {
            throw new IllegalArgumentException(
                "Member '" + member.getMemberId() + "' already exists.");
        }
        store.put(member.getMemberId(), member);
    }

    @Override
    public Member getById(String memberId) {
        return store.get(memberId);
    }

    @Override
    public List<Member> getAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void update(Member member) {
        if (!store.containsKey(member.getMemberId())) {
            throw new IllegalArgumentException(
                "Member '" + member.getMemberId() + "' not found.");
        }
        store.put(member.getMemberId(), member);
    }

    @Override
    public boolean delete(String memberId) {
        return store.remove(memberId) != null;
    }
}
