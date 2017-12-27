package com.epam.test_generator.services;

import static org.mockito.Mockito.*;

import com.epam.test_generator.entities.Status;
import com.epam.test_generator.state.machine.StateMachineAdapter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class StatesServiceTest {

    @Mock
    StateMachineAdapter stateMachineAdapter;

    @InjectMocks
    StatesService statesService;

    Status status = Status.NOT_RUN;

    @Test
    public void availableTransitions() {
        statesService.availableTransitions(status);

        verify(stateMachineAdapter).availableTransitions(status);
    }
}