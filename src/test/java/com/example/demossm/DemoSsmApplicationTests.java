package com.example.demossm;

import javax.transaction.Transactional;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.data.jpa.JpaStateMachineRepository;
import org.springframework.statemachine.service.StateMachineService;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class DemoSsmApplicationTests {

    @Autowired
    private StateMachineService<String, String> stateMachineService;
    
    @Autowired
    private JpaStateMachineRepository jpaStateMachineRepository;
    
    @Test
    public void acquireStateMachine_withStateMachineService_stateMachineDataIsPersisted(){
        StateMachine<String, String> stateMachine = stateMachineService.acquireStateMachine("machine1");
        assertTrue(stateMachine.sendEvent("Evento2"));
        assertTrue(jpaStateMachineRepository.existsById("machine1"));
    }
}
