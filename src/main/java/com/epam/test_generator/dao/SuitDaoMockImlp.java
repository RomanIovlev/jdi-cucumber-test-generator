package com.epam.test_generator.dao;

import com.epam.test_generator.dao.interfaces.EntitiesDAO;
import com.epam.test_generator.entities.Suit;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class SuitDaoMockImlp implements EntitiesDAO<Suit>{

    List<Suit> suitList = new ArrayList<>();

  {
        suitList.add( new Suit(new Long(1), "Mai suit", "First suit", new ArrayList<>()));
        suitList.add( new Suit(new Long(2), "Mai suit", "First suit", new ArrayList<>()));
    }

    Long counter = new Long(2);

    @Override
    public Suit addEntity(Suit ts) {
        ts.setId(++counter);
        suitList.add(ts);
        return ts;
    }

    @Override
    public List<Suit> getAllEntities() {
        return suitList;
    }

    @Override
    public Suit getEntity(Long id) {
        return suitList.get(0);
    }

    @Override
    public void removeEntity(Long id) {
        suitList.remove(id.intValue() - 1);
    }

    @Override
    public void editEntity(Suit ts) {
        suitList.set(ts.getId().intValue() - 1, ts);
    }
}