package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import study.datajpa.entity.Item;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Test
    public void save() throws Exception {

        // given
        Item item = new Item("A");
        itemRepository.save(item);  // debug로 SimpleJpa의 save() 메서드 확인
        /*
        그래서 em.persist(entity) 이후에 @GeneragtedValue에 의해서 id 값을 넣는데!
        만약 뭔가 우리가 id 값을 세팅해서 준다면?
        1. id가 존재하므로, select 문이 발생(디비에 있는지 부터 찾는다.)
        2. em.merge가 발생한다!...  --> 완전 비효율
        */


        // when

        // then
    }
}